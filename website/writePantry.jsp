<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Properties"%>
<%@ page import="java.io.*"%>
<%
	String strCount = (String) request.getParameter("ingredients");
	int newCount = Integer.parseInt(strCount);
	strCount = (String) request.getParameter("prevNumItems");
	int oldCount = Integer.parseInt(strCount);
	
	String path = application.getRealPath("/users/"+session.getAttribute("username")+".txt");
	Properties userProps = new Properties();
	FileInputStream userFile = new FileInputStream(path);
	userProps.load(userFile);
	userFile.close();
	Enumeration params = request.getParameterNames();
	while(params.hasMoreElements())
	{
		String paramName = (String) params.nextElement();
		if(!paramName.equals("prevNumItems"))
		{
			try
			{
				userProps.setProperty(paramName, request.getParameter(paramName));
				session.setAttribute(paramName, request.getParameter(paramName));
			}
			catch(Exception e)
			{
				out.println("failed on "+paramName);
			}
		}
	}
	if(oldCount>newCount)
	{
		for(int i=(newCount+1);i<=oldCount;i++)
		{
			userProps.remove("ingredient"+i);
			userProps.remove("unit"+i);
			userProps.remove("count"+i);
			
			session.removeAttribute("ingredient"+i);
			session.removeAttribute("unit"+i);
			session.removeAttribute("count"+i);
		}
	}
	FileOutputStream outFile = new FileOutputStream(path);
	userProps.store(outFile, "<!--This is a test-->");
	out.println("it worked!");
%>
<jsp:forward page="updateUser.jsp"/>