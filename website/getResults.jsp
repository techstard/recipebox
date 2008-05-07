<%@ page import="org.apache.lucene.document.Document"%>
<%@ page import="org.apache.lucene.search.IndexSearcher"%>
<%@ page import="org.apache.lucene.search.BooleanQuery"%>
<%@ page import="org.apache.lucene.search.BooleanClause.Occur"%>
<%@ page import="org.apache.lucene.search.Hits"%>
<%@ page import="org.apache.lucene.store.FSDirectory"%>
<%@ page import="org.apache.lucene.store.Directory"%>
<%@ page import="org.apache.lucene.queryParser.QueryParser"%>
<%@ page import="org.apache.lucene.analysis.standard.StandardAnalyzer"%>
<%@ page import="java.io.File"%>

<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.Random"%>

<%@ page import="lucenesearcher.Result"%>
<%!
boolean haveIngredients(String[] myArray, String[] theirArray) {
	if(theirArray.length < 3) return false;
	boolean has = false;
	for(int i=0;i<theirArray.length;i++) {
		for(int j=0;j<myArray.length;j++)
		{
			if(theirArray[i].contains(myArray[j])) {
				has = true;
				break;
			}
		}
		if(!has) return false;
		has = false;
	}
	
	return true;
}
%>
<%
	/*
	 * Parameters of NetBeans search function
	 */
	String query = request.getParameter("q");
	String field = request.getParameter("f");
	String shortcuts = request.getParameter("s");
	if(query == null || field == null) return;
	/*
	 *	Contents of NetBeans search function
	 */
	 
	 File indexDir = new File("E:\\finalcrawl\\index");
	Directory fsDir = FSDirectory.getDirectory(indexDir, false);
	IndexSearcher is = new IndexSearcher(fsDir);
	boolean search = false;
	boolean pantry = false;
	boolean menu = false;
	if(field.equals("search")) {
		field = "ingredient";
		search = true;
	}
	if(field.equals("pantry")) {
		field = "ingredient";
		pantry = true;
		query += "salt;black pepper;"; //common ingredients
		if(shortcuts.contains("v")) //vegetarian
			query += "!beef;!pork;!veal;!chicken;!fish;!turkey;";
		if(shortcuts.contains("s")) {//spice rack
			query += "allspice;anise;caraway;cayenne;celery seed;chile powder;cinnamon;clove;coriander;cumin;";
			query += "curry;ginger;mace;mustard seed;nutmeg;paprika;pepper corns;poppy seed;saffron;sesame seed;";
		}
		if(shortcuts.contains("b")) {//bakery supplies
			query += "flour;sugar;brown sugar;baking soda;baking powder;";
		}
	}
	if(field.equals("menu")) {
		field = "ingredient";
		menu = true;
	}

	String[] q = query.split(";");
	QueryParser qp = new QueryParser(field, new StandardAnalyzer());
	BooleanQuery bq = new BooleanQuery();
	if(pantry) {
		for(int i=0;i<q.length;i++) {
			if(q[i].startsWith("!")) {
				q[i] = q[i].substring(1);
				bq.add(qp.parse(q[i]), Occur.MUST_NOT);
			}
			else if(i==0) //Main ingredient
				bq.add(qp.parse(q[i]), Occur.MUST);
			else
				bq.add(qp.parse(q[i]), Occur.SHOULD);
		}
	}
	else {
		for(int i=0;i<q.length;i++) {
			if(q[i].startsWith("!"))
				bq.add(qp.parse(q[i].substring(1)), Occur.MUST_NOT);
			else
				bq.add(qp.parse(q[i]), Occur.MUST);
		}
	}
	Hits hits = is.search(bq);
	
    HashMap<String,Result> hs = new HashMap<String,Result>();
	System.out.println("Found " + hits.length() + " document(s) that matched query '" + query + "':");
	for (int i = 0; i < hits.length(); i++) {
		Double sum = 0.0;
		String summary = "";
		Document doc = hits.doc(i);
			
		if(pantry){
			if(haveIngredients(q,doc.getValues("ingredient")))
				hs.put(doc.getValues("recipeTitle")[0],new Result(doc.getValues("recipeTitle")[0], doc.getField("url").stringValue()));
		}
		else if(menu) {
			Result temp = new Result(doc.getValues("recipeTitle")[0], doc.getField("url").stringValue());
			temp.setDocId(hits.id(i));
            hs.put(doc.getValues("recipeTitle")[0],temp);
		}
		else {
			for(int j=0; j<doc.getValues(field).length;j++) {
				if(search) {
					for(int k=0;k<q.length;k++)
					{
						if(doc.getValues(field)[j].contains(q[k]))
							sum += Double.parseDouble(doc.getField(doc.getValues(field)[j]).stringValue());
					}

				}
				else if(field.equals("recipeTitle")){//Browse
					if(doc.getValues(field)[j].toLowerCase().trim().startsWith(Character.toString(q[0].charAt(0)))) {
						hs.put(doc.getValues(field)[j],new Result(doc.getValues(field)[j], doc.getField("url").stringValue()));
					}
				}
				else if(field.equals("ingredient")) {
					if(doc.getValues(field)[j].toLowerCase().trim().startsWith(Character.toString(q[0].charAt(0)))) {
						hs.put(doc.getValues(field)[j],new Result(doc.getValues(field)[j], "search.jsp?q="+doc.getValues(field)[j]));
					}
				}
			}
			if(search)
			{
				//summarizer.getSummary(detailer.getDetails(), bq);
				hs.put(Double.toString(sum/Double.parseDouble(doc.getField("totVolume").stringValue())),new Result(sum/Double.parseDouble(doc.getField("totVolume").stringValue()), doc.getField("recipeTitle").stringValue(),"",doc.getField("url").stringValue()));
			}
		}
	}
    Object[] outArray = hs.keySet().toArray();
	Arrays.sort(outArray);
	response.setContentType("text/xml");
	response.setHeader("Cache-Control", "no-cache");
	
	if(menu) {
		response.getWriter().write(hs.get(outArray[new Random().nextInt(outArray.length)]).toMenuString());
	}
	else {
		for(int j=outArray.length-1;j>=0;j--) {
			response.getWriter().write(hs.get(outArray[j]).toWebString());
		}
		if(outArray.length == 0) {
			response.getWriter().write("No Results");
		}
	}
%>