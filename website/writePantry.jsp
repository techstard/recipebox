<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Properties"%>
<%@ page import="java.io.*"%>
<%
	response.setContentType("text/xml");
	response.setHeader("Cache-Control", "no-cache");
	String path = application.getRealPath("/users/"+session.getAttribute("username")+".txt");
	Properties userProps = new Properties();
	FileInputStream userFile = new FileInputStream(path);
	userProps.load(userFile);
	userFile.close();
	
	String[] amtArray = request.getParameter("a").split(";");
	String[] unitArray = request.getParameter("u").split(";");
	String[] ingArray = request.getParameter("i").split(";");
	String shortcuts = request.getParameter("s");
	userProps.setProperty("shortcuts",shortcuts);
	session.setAttribute("shortcuts",shortcuts);
	for(int i=0;i<ingArray.length;i++) {
		//try
		//{
			userProps.setProperty("count"+i, amtArray[i]);
			userProps.setProperty("unit"+i, unitArray[i]);
			userProps.setProperty("ingredient"+i, ingArray[i]);
			session.setAttribute("count"+i, amtArray[i]);
			session.setAttribute("unit"+i, unitArray[i]);
			session.setAttribute("ingredient"+i, ingArray[i]);
		//}
		//catch(Exception e)
		//{
		//	response.getWriter().write("Save unsuccessful :(");
		//}
	}
	userProps.setProperty("ingredients",ingArray.length+"");
	session.setAttribute("ingredients",ingArray.length+"");
	int prevNum = Integer.parseInt(request.getParameter("prevNum"));
	if(prevNum>ingArray.length)
	{
		for(int i=(ingArray.length+1);i<=prevNum;i++)
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
	response.getWriter().write("Save Successful");
%>