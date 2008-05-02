<%@ include file="topContent.jsp"%>
<%@	page import="java.io.*"%>
<%@ page import="java.util.Properties"%>
<%
   String name = request.getParameter( "username" );
%>
<%
	String path = application.getRealPath("/users/"+name+".txt");
	Properties userProps = new Properties();
	try
	{
		out.print(request.getParameter("link"));
		FileInputStream userFile = new FileInputStream(path);
		userProps.load(userFile);
		userFile.close();

		//Check if password matches given password else boot to test with error message
		if(!userProps.getProperty("password").equals(request.getParameter("password")))
		{%>
			<jsp:forward page="<%=request.getParameter("link")%>">
				<jsp:param name="error" value="incorrect password"/>
			</jsp:forward>
		<%}	
		//implicit else	
		session.setAttribute( "username", name );
		//Set rest of properties to attributes
		String strCount = (String) userProps.getProperty("ingredients");
		int intCount = Integer.parseInt(strCount);
		session.setAttribute("ingredients",intCount+"");
		for(int i=0;i<intCount;i++)
		{
			session.setAttribute("ingredient"+i,userProps.getProperty("ingredient"+i));
			session.setAttribute("unit"+i,userProps.getProperty("unit"+i));
			session.setAttribute("count"+i,userProps.getProperty("count"+i));
		}
		session.setAttribute("shortcuts",userProps.getProperty("shortcuts"));
%>	
		<jsp:forward page="<%=request.getParameter("link")%>"/>
<%
	}
	catch(Exception e)
	{
		out.print("failure :(");
		session.setAttribute( "newUsername", name );
		//The forward to newuser.jsp is only reached if we failed to forward to test.
	}
%>
		<jsp:forward page="<%=request.getParameter("link")%>">
			<jsp:param name="error" value="unknown username"/>
		</jsp:forward>
<%@ include file="bottomContent.jsp"%>