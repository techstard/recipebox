<%
	String strCount2 = (String) session.getAttribute("ingredients");
	int intCount2 = Integer.parseInt(strCount2);
%>
	update your <a href="updateUser.jsp">pantry</a><br/>
<%
	for(int i=1;i<=intCount2;i++)
	{
		out.print(session.getAttribute("count"+i)+" ");
		out.print(session.getAttribute("unit"+i)+" ");
		out.println(session.getAttribute("ingredient"+i)+"<br/>");
	}
%>