package com.aneedo.jwplext.dao.tablecreation;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;



public class CategoryDataDumpDao {

	private final String SELECT_CATEGORY_PAGES = "select pages from category_pages where id = ?";

	private final String ASS_PARENT_OVERLAP_QUERY = "insert into outlink_parent_overlap(pageId1, pageId2, category_ids, category_names) "
			+ "values(?,?,?,?)";

	private final String SELECT_IS_EXISTS_PAGE_OVERLAP = "select * from outlink_overlap where (pageId1 = ? and pageId2 =?) or (pageId1 = ? and pageId2 =?)";

	private final String SELECT_ASS_PARENT_OVERLAP = "select distinct pc2.id as pageId, c.pageId as catId, c.name as name from page_categories pc1, "
			+ "page_categories pc2, Category c where c.pageId = pc1.pages and c.pageId = pc2.pages and pc1.id = ? and pc2.id = ?";

	private final String ASS_OVERLAP_QUERY = "insert into outlink_overlap(pageId1, pageId2, name1, name2,overlap_count) select distinct o1.id, o2.id, "
			+ "p1.name, p2.name, count(*) from page_outlinks o1, page_outlinks o2, Page p1, Page p2 where o1.outLinks = o2.outLinks "
			+ "and p1.id = o1.id and p2.id = o2.id and o1.id = ? and o2.id = ?";

	private String LOG_FILE = "/data2/qassist/indexing/datadump/log/CategoryDataDump";

	// From which rowId should start
	private int offset = 0;

	// How many rows to pick
	private final int LIMIT = 25;

	private int MAX_RECORD = 100000;// 712865; 

	private PreparedStatement pstmt = null;

	private BufferedWriter errorWriter = null;
	private String password = "aneedo";
	
	public static void main(String[] args) {
		CategoryDataDumpDao dao = new CategoryDataDumpDao();
		try {
			
			System.out.println("Enter offset max-record db-password");
			if(args.length < 2) {
				System.out.println("Not entered all parameters....");
				return;
			}
			dao.offset = Integer.parseInt(args[0]);
			dao.MAX_RECORD = Integer.parseInt(args[1]);
			dao.LOG_FILE = dao.LOG_FILE +dao.offset+".log";
			
			dao.errorWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(dao.LOG_FILE), "UTF8"));
			if(args.length > 2) {
				dao.password = args[2];
			}
			Connection conn = DBConnectionFactory.getInstance(dao.password).getConnection();

			dao.pstmt = conn.prepareStatement("select pageId from Category LIMIT "
					+ dao.LIMIT + " offset ?");

			PreparedStatement linkOverlapStmt = conn
					.prepareStatement(dao.ASS_OVERLAP_QUERY);
			PreparedStatement linkCatOverlapStmt = conn
					.prepareStatement(dao.ASS_PARENT_OVERLAP_QUERY);
			PreparedStatement selectLinkCatOverlapStmt = conn
					.prepareStatement(dao.SELECT_ASS_PARENT_OVERLAP);
			PreparedStatement selectCatPagesStmt = conn
					.prepareStatement(dao.SELECT_CATEGORY_PAGES);
			PreparedStatement selectOutlinkOverlapStmt = conn
					.prepareStatement(dao.SELECT_IS_EXISTS_PAGE_OVERLAP);
			
			
			int catId = 0;
			int pageId1 = 0;
			int pageId2 = 0;
			while (dao.offset + dao.LIMIT <= dao.MAX_RECORD) {
				System.out.println(dao.offset);
				final List<Integer> catIdList = dao.getCatIdList(dao.offset);
				for (int i = 0, size = catIdList.size(); i < size; i++) {
					catId = catIdList.get(i);

					selectCatPagesStmt.setInt(1, catId);
					ResultSet result = selectCatPagesStmt.executeQuery();
					List<Integer> catPages = new ArrayList<Integer>();
					while (result.next()) {
						catPages.add(result.getInt("pages"));
					}
					result.close();

					for (int j = 0, outSize = catPages.size(); j < outSize; j++) {
						for (int k = j+1; k < outSize; k++) {
						pageId1 = catPages.get(j);
						pageId2 = catPages.get(k);

						selectOutlinkOverlapStmt.setInt(1, pageId2);
						selectOutlinkOverlapStmt.setInt(2, pageId1);
						selectOutlinkOverlapStmt.setInt(3, pageId1);
						selectOutlinkOverlapStmt.setInt(4, pageId2);

						result = selectOutlinkOverlapStmt.executeQuery();
						
						if (!(result != null && result.next())) {
							linkOverlapStmt.setInt(1, pageId2);
							linkOverlapStmt.setInt(2, pageId1);
							linkOverlapStmt.addBatch();

							try {
								String[] catOverlap = dao.getParentOverlap(
										selectLinkCatOverlapStmt, dao.errorWriter,
										pageId2, pageId1);
								if (catOverlap != null) {
									linkCatOverlapStmt.setInt(1, pageId2);
									linkCatOverlapStmt.setInt(2, pageId1);
									linkCatOverlapStmt.setString(3,
											catOverlap[0]);
									linkCatOverlapStmt.setString(4,
											catOverlap[1]);
									linkCatOverlapStmt.addBatch();
								}
							} catch (Exception e) {
								try {
									dao.errorWriter.write("getting overlap falied"
											+ catId + " " + pageId1 + pageId2
											+ e.getMessage() + " "
											+ e.getCause() + "\n");
								} catch (Exception exp) {
									exp.printStackTrace();
								}
							}
						}
						if(result != null) result.close();
					}
				}
					System.out.println("done .." + (i + dao.offset));
				}
				try {
					linkCatOverlapStmt.executeBatch();
				} catch (Exception e) {
					try {
						dao.errorWriter.write("linkCatOverlapStmt falied" + catId
								+ " " + pageId1 + e.getMessage() + " "
								+ e.getCause() + "\n");
					} catch (Exception exp) {
						exp.printStackTrace();
					}
				}
				try {
					linkOverlapStmt.executeBatch();
				} catch (Exception e) {
					try {
						dao.errorWriter.write("linkOverlapStmt falied" + catId
								+ " " + pageId1 + e.getMessage() + " "
								+ e.getCause() + "\n");
					} catch (Exception exp) {
						exp.printStackTrace();
					}
				}

				dao.offset = dao.offset + dao.LIMIT;
				try {
					
					linkOverlapStmt.close();
					linkCatOverlapStmt.close();
					selectLinkCatOverlapStmt.close();
					selectCatPagesStmt.close();
					selectOutlinkOverlapStmt.close();
					
				conn.close();
				conn = DBConnectionFactory.getInstance(dao.password).getConnection();
				dao.pstmt = conn.prepareStatement("select pageId from Category LIMIT "
						+ dao.LIMIT + " offset ?");

				linkOverlapStmt = conn
						.prepareStatement(dao.ASS_OVERLAP_QUERY);
				linkCatOverlapStmt = conn
						.prepareStatement(dao.ASS_PARENT_OVERLAP_QUERY);
				selectLinkCatOverlapStmt = conn
						.prepareStatement(dao.SELECT_ASS_PARENT_OVERLAP);
				selectCatPagesStmt = conn
						.prepareStatement(dao.SELECT_CATEGORY_PAGES);
				selectOutlinkOverlapStmt = conn
						.prepareStatement(dao.SELECT_IS_EXISTS_PAGE_OVERLAP);
				} catch (Exception e) {
					try {
						dao.errorWriter.write("connection closed falied after" +dao.offset + e.getMessage() + " "
								+ e.getCause() + "\n");
					} catch (Exception exp) {
						exp.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				dao.errorWriter.write("initalization falied" + e.getMessage() + " "
						+ e.getCause() + "\n");
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		
		try {
			dao.errorWriter.flush();
			dao.errorWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private String[] getParentOverlap(
			PreparedStatement selectLinkCatOverlapStmt,
			BufferedWriter errorWriter, int pageId, int outlinkId) {
		String[] toReturn = null;

		// as catId, c.name as name, pc2.id as pageId
		// prune articles, wikipedia categories
		try {
			selectLinkCatOverlapStmt.setInt(1, pageId);
			selectLinkCatOverlapStmt.setInt(2, outlinkId);
			final ResultSet result = selectLinkCatOverlapStmt.executeQuery();
			// int prevId = -1;
			if (result != null) {
				StringBuilder catIdBuilder = new StringBuilder();
				StringBuilder catNameBuilder = new StringBuilder();
				while (result.next()) {
					final String catName = result.getString("name")
							.toLowerCase();
					// System.out.println("catName : " + catName);
					if (!(catName.indexOf("article_") >= 0
							|| catName.indexOf("wikipedia_") >= 0
							|| catName.indexOf("_article") >= 0
							|| catName
							.indexOf("articles_") >= 0)) {
						catIdBuilder.append(result.getInt("catId") + "|");
						catNameBuilder.append(catName + "|");

					}

				}
				if (catIdBuilder.length() > 3) {
					toReturn = new String[2];
					toReturn[0] = catIdBuilder.toString();
					toReturn[1] = catNameBuilder.toString();
				}
			}
		} catch (Exception e) {
			try {
				errorWriter.write("Fetching parent overlap falied " + pageId
						+ " " + outlinkId + e.getMessage() + " " + e.getCause()
						+ "\n");
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		return toReturn;
	}

	private List<Integer> getCatIdList(int offset) {
		List<Integer> pageIdList = new ArrayList<Integer>();
		try {
			pstmt.setInt(1, offset);
			ResultSet result = pstmt.executeQuery();
			if (result != null) {
				while (result.next()) {
					pageIdList.add(result.getInt("pageId"));
				}
			}
			result.close();
		} catch (Exception e) {
			try {
				errorWriter.write("fetching page id falied for offset "
						+ offset + e.getMessage() + " " + e.getCause() + "\n");
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		return pageIdList;
	}
}

