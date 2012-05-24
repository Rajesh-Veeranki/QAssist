package com.aneedo.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StopwordRecognizer {
	private static final String DEFAULT_STOP_WORDS = "a,a's,able,about,above,according,accordingly,across,actually,after,"
			+ "afterwards,again,against,ain't,all,allow,allows,almost,alone,along,already,also,"
			+ "although,always,am,among,amongst,an,and,another,any,anybody,anyhow,anyone,anything,include,including,"
			+ "anyway,anyways,anywhere,apart,appear,appreciate,appropriate,are,aren't,around,as,aside,ask,"
			+ "asking,associated,at,available,away,awfully,b,be,became,because,become,becomes,becoming,been,"
			+ "before,beforehand,behind,being,believe,below,beside,besides,best,better,between,beyond,both,brief,"
			+ "but,by,c,c'mon,c's,came,can,can't,cannot,cant,cause,causes,certain,certainly,changes,clearly,co,"
			+ "com,come,comes,concerning,consequently,consider,considering,contain,containing,contains,"
			+ "corresponding,could,couldn't,course,currently,d,definitely,described,despite,did,didn't,different,"
			+ "do,does,doesn't,doing,don't,done,down,downwards,during,e,each,edu,eg,eight,either,else,elsewhere,"
			+ "enough,entirely,especially,et,etc,even,ever,every,everybody,everyone,everything,everywhere,ex,"
			+ "exactly,example,except,f,far,few,fifth,first,five,followed,following,follows,for,former,formerly,"
			+ "forth,four,from,further,furthermore,g,get,gets,getting,given,gives,go,goes,going,gone,got,gotten,"
			+ "greetings,h,had,hadn't,happens,hardly,has,hasn't,have,haven't,having,he,he's,hello,hence,"
			+ "her,here,here's,hereafter,hereby,herein,hereupon,hers,herself,hi,him,himself,his,hither,"
			+ "hopefully,how,howbeit,however,i,i'd,i'll,i'm,i've,ie,if,ignored,immediate,in,inasmuch,inc,"
			+ "indeed,indicate,indicated,indicates,inner,insofar,instead,into,inward,is,isn't,it,it'd,it'll,"
			+ "it's,its,itself,j,just,k,keep,keeps,kept,know,knows,known,l,last,lately,later,latter,latterly,least,less,"
			+ "lest,let,let's,like,liked,likely,little,look,looking,looks,ltd,m,mainly,many,may,maybe,me,mean,meanwhile,"
			+ "merely,might,more,moreover,mostly,much,must,most,my,myself,n,name,namely,nd,near,nearly,necessary,need,needs,"
			+ "neither,never,nevertheless,new,next,nine,no,nobody,non,none,noone,nor,normally,not,nothing,novel,now,nowhere,o,"
			+ "obviously,of,off,often,oh,ok,okay,old,on,once,one,ones,only,onto,or,other,others,otherwise,ought,our,ours,ourselves,"
			+ "out,outside,over,overall,own,p,particular,particularly,per,perhaps,placed,please,plus,possible,presumably,probably,"
			+ "provides,q,que,quite,qv,r,rather,rd,re,really,reasonably,regarding,regardless,regards,relatively,respectively,right,s,"
			+ "said,same,saw,say,saying,says,second,secondly,see,seeing,seem,seemed,seeming,seems,seen,self,selves,sensible,sent,"
			+ "serious,seriously,seven,several,shall,she,should,shouldn't,since,six,so,some,somebody,somehow,someone,something,sometime,"
			+ "sometimes,somewhat,somewhere,soon,sorry,specified,specify,specifying,still,sub,such,sup,sure,t,t's,take,taken,tell,"
			+ "tends,th,than,thank,thanks,thanx,that,that's,thats,the,their,theirs,them,themselves,then,thence,there,there's,thereafter,"
			+ "thereby,therefore,therein,theres,thereupon,these,they,they'd,they'll,they're,they've,think,third,this,thorough,"
			+ "thoroughly,those,though,three,through,throughout,thru,thus,to,together,too,took,toward,towards,tried,tries,truly,"
			+ "try,trying,twice,two,u,un,under,unfortunately,unless,unlikely,until,unto,up,upon,us,use,used,useful,uses,using,"
			+ "usually,uucp,v,value,various,very,via,viz,vs,w,want,wants,was,wasn't,way,we,we'd,we'll,we're,we've,welcome,well,"
			+ "went,were,weren't,what,what's,whatever,when,whence,whenever,where,where's,whereafter,whereas,whereby,wherein,"
			+ "whereupon,wherever,whether,which,while,whither,who,who's,whoever,whole,whom,whose,why,will,willing,wish,"
			+ "with,within,without,won't,wonder,would,would,wouldn't,x,y,yes,yet,you,you'd,you'll,you're,you've,your,"
			+ "yours,yourself,yourselves,z,zero,a,about,above,across,after,again,against,all,almost,alone,along,already,"
			+ "also,although,always,among,an,and,another,any,anybody,anyone,anything,anywhere,are,area,areas,around,as,"
			+ "ask,asked,asking,asks,at,away,b,back,backed,backing,backs,be,became,because,become,becomes,been,before,"
			+ "began,behind,being,beings,best,better,between,both,but,by,c,came,can,cannot,case,cases,certain,certainly,"
			+ "clear,clearly,come,could,d,did,differ,different,differently,do,does,done,down,down,downed,downing,downs,during,"
			+ "e,each,early,either,end,ended,ending,ends,enough,even,evenly,ever,every,everybody,everyone,everything,"
			+ "everywhere,f,face,faces,fact,facts,far,felt,few,find,finds,first,for,four,from,full,fully,further,furthered,"
			+ "furthering,furthers,g,gave,general,generally,get,gets,give,given,gives,go,going,got,greater,"
			+ "greatest,grouped,grouping,groups,h,had,has,have,having,he,her,here,herself,higher,"
			+ "highest,him,himself,his,how,however,i,if,in,interest,interested,interests,"
			+ "into,is,it,its,itself,j,just,k,keep,keeps,kind,knew,know,known,knows,largely,last,later,"
			+ "latest,least,less,let,lets,like,likely,longer,longest,m,made,make,making,man,many,may,me,"
			+ "men,might,more,most,mostly,mr,mrs,much,must,my,myself,n,necessary,need,needed,"
			+ "needing,needs,never,new,new,newer,newest,next,no,nobody,non,noone,not,nothing,now,nowhere,number,"
			+ "numbers,o,of,off,often,old,older,oldest,on,once,one,only,open,opened,opening,opens,or,order,ordered,"
			+ "ordering,orders,other,others,our,out,over,p,part,parted,parting,parts,per,perhaps,place,places,point,"
			+ "pointed,pointing,points,possible,present,presented,presenting,presents,problem,problems,put,puts,q,"
			+ "quite,r,rather,really,right,right,room,rooms,s,said,same,saw,say,says,second,seconds,see,seem,seemed,"
			+ "seeming,seems,sees,several,shall,she,should,show,showed,showing,shows,side,sides,since,smaller,"
			+ "smallest,so,some,somebody,someone,something,somewhere,state,states,still,still,such,sure,t,take,taken,"
			+ "than,that,the,their,them,then,there,therefore,these,they,thing,things,think,thinks,this,those,though,"
			+ "thought,thoughts,three,through,thus,to,today,together,too,took,toward,turn,turned,turning,turns,two,u,"
			+ "under,until,up,upon,us,use,used,uses,v,very,w,want,wanted,wanting,wants,was,way,ways,we,well,wells,went,"
			+ "were,what,when,where,whether,which,while,who,whole,whose,why,will,with,within,without,work,worked,"
			+ "works,would,x,y,year,years,yet,you,younger,youngest,your,yours,z,"
			+ "a,about,add,ago,after,all,also,an,and,another,any,are,as,at,be,"
			+ "because,been,before,being,between,both,but,by,came,can,come,"
			+ "could,did,do,does,due,each,else,end,far,few,for,from,get,got,had,"
			+ "has,have,he,her,here,him,himself,his,how,if,in,into,is,it,its,"
			+ "just,let,lie,like,low,make,many,me,might,more,much,must,"
			+ "my,never,no,nor,not,now,of,off,old,on,only,or,other,our,out,over,"
			+ "per,pre,put,re,said,same,see,she,should,since,so,some,still,such,"
			+ "take,than,that,the,their,them,then,there,these,they,this,those,"
			+ "through,to,too,under,up,use,very,via,want,was,way,we,well,were,"
			+ "what,when,where,which,while,who,will,with,would,yes,yet,you,your,based,base,widely";

	private Set<String> stopwords = new HashSet<String>();
    private static final StopwordRecognizer stopWordRecog = new StopwordRecognizer();
    
    private StopwordRecognizer() {
		init();
	}
    public static StopwordRecognizer getInstance() {
    	return stopWordRecog;
    }
	public void init()  {
		if (stopwords.size() == 0) {
			String[] stopwordArray = DEFAULT_STOP_WORDS.split(",");
			stopwords.addAll(Arrays.asList(stopwordArray));
		}
	}

	public List<String> removeStopWords(List<String> tokens) {
		List<String> recognizedTokens = new ArrayList<String>();
		
		for (String token : tokens) {
			if (!stopwords.contains(token.toLowerCase())) {
				//System.out.print(token +" ");
				recognizedTokens.add(token);
			}
		}
		//System.out.println("After removing stop words ....");
		return recognizedTokens;
	}
	
	public boolean isStopWordForType(String word) {
		if("a".equals(word) ||"".equals(word.trim())) {
			return false;
		}
		return isStopWord(word);
	}
	
	public boolean isStopWord(String word) {
		return stopwords.contains(word);
	}

}