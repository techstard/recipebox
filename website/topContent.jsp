<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%//Defined for use in other pages
String errorString = null;
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 
		<title>Matt's 3-column Test!</title>
		<LINK REL=StyleSheet HREF="default.css" TYPE="text/css">
	</head>
	<body onload="load()">
		<div id="header">
			<!-- Background photo from catsper's flickr stream, http://www.flickr.com/photos/catsper/71443147/-->
			<a href="test.jsp"><img src="name.png" alt="Recipe Box" border="0"></a>
				<%
					if(session.getAttribute("username") == null)
					{
				%>
						<div class="register"><a href="newuser.jsp">register a new user</a></div>
						<form class="login" method="post" action="signin.jsp?link=<%=request.getServletPath()%>">
							username? <input type="text" name="username">
							password? <input type="password" name="password">
							<input type="submit" value="=">
						</form>
				<%
					}
					else
					{
				%>
					<span class="login">
					Hello, <%=session.getAttribute("username")%>, <a href="logout.jsp?link=<%=request.getServletPath()%>">logout</a>?
					</span>
				<%
					}
				%>
		</div>
		<div class="colmask holygrail">
			<div class="colmid">
				<div class="colleft">
					<div class="col1wrap">
						<div class="col1">
							<div class="error" id="error">
								<%=request.getParameter("error")==null?"":request.getParameter("error")%>
								<%=(errorString==null)?"":null%>
							</div>