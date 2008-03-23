<%@ page import="java.io.*"%>
<%@ page import="java.util.Properties"%>
<%
	Properties defaultProps = new Properties();
	defaultProps.setProperty("password","1lmiagmc!");
	defaultProps.setProperty("pantry", "true");
	defaultProps.setProperty("indgredients", "5");
		defaultProps.setProperty("ingredient"+1, "artichoke");
			defaultProps.setProperty("count"+1, ""+2);
			defaultProps.setProperty("unit"+1, "medium");
		defaultProps.setProperty("ingredient"+2, "baking soda");
			defaultProps.setProperty("count"+2, ""+1);
			defaultProps.setProperty("unit"+2, "box");
		defaultProps.setProperty("ingredient"+3, "cilantro");
			defaultProps.setProperty("count"+3, ""+2);
			defaultProps.setProperty("unit"+3, "bunch");
		defaultProps.setProperty("ingredient"+4, "dill");
			defaultProps.setProperty("count"+4, ""+1);
			defaultProps.setProperty("unit"+4, "tin");
		defaultProps.setProperty("ingredient"+5, "eggs");
			defaultProps.setProperty("count"+5, ""+12);
			defaultProps.setProperty("unit"+5, "extra large");
	FileOutputStream outFile = new FileOutputStream(application.getRealPath("/users/defaultProperties.txt"));
	defaultProps.store(outFile, "Matt was here");
	outFile.close();
	out.println("success!");
	
	
%>