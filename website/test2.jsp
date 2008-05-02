<%
	String strCount2 = (String) session.getAttribute("ingredients");
	int intCount2 = Integer.parseInt(strCount2);
%>
	update your <a href="updateUser.jsp">pantry</a><br/>
<%
	if(((String)session.getAttribute("shortcuts")).contains("v"))out.println("vegetarian</br>");
	if(((String)session.getAttribute("shortcuts")).contains("b"))out.println("baking supplies<br/>");
	if(((String)session.getAttribute("shortcuts")).contains("s"))out.println("spice rack<br/>");
	
	for(int i=0;i<intCount2;i++)
	{
		out.print(session.getAttribute("count"+i)+" ");
		out.print(session.getAttribute("unit"+i)+" ");
		out.println(session.getAttribute("ingredient"+i)+"<br/>");
	}
%>