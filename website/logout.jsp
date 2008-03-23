<%
	session.invalidate();
	out.println(request.getParameter("link"));
%>
	<jsp:forward page="<%=request.getParameter("link")%>"/>