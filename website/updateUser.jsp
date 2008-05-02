<%@ include file="topContent.jsp"%>
<%@ page import="java.io.*"%>
<script type="text/javascript">
var registeredEventListeners = new Array();
	//
	// Equivalent to target.addEventListener(event, listener, capture)
	// Returns nothing.
	//
	function addEventListener(target, event, listener, capture)
	{
		registeredEventListeners.push( [target, event, listener, capture] );
		target.addEventListener(event, listener, capture);
	}

	//
	// Removes all event listeners from the page when it unloads.
	//
	function unregisterEventListeners(event)
	{
		while (registeredEventListeners.length > 0) {
			var rel = registeredEventListeners.pop();
			rel[0].removeEventListener(rel[1], rel[2], rel[3]);
		}
		window.removeEventListener('unload', unregisterEventListeners, false);
	}
	var http;

	function handleHttpResponse() {
		if (http.readyState == 4) {
			if (http.status == 200) {
				//alert("handleHTTPResponse");            
				alert(http.responseText);
				window.location.reload();
			} else {
				alert ( "Not able to retrieve name" +http.status);
			}
		}    
	}

	function getResults(amt, unit, ing, s) {
		http = getHTTPObject();
		var url = "writePantry.jsp?"; // The server-side script	
		var queryString = "a="+amt+"&u="+unit+"&i="+ing+"&s="+s+"&prevNum="+<%=session.getAttribute("ingredients")%>;
		http.open("GET", url + queryString, true);
		http.onreadystatechange = handleHttpResponse;
		http.send(null);
	}
	function getHTTPObject() {
		var xmlhttp;
		if (window.XMLHttpRequest) {
			xmlhttp = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		return xmlhttp;
	}
	function addElement(amt, unit, name) {
		var div = document.createElement("div");
		var del = document.createElement("a");
		del.innerHTML = "delete ";
		addEventListener(del,'click', function(e) {
			e.target.parentNode.parentNode.removeChild(del.parentNode);
		}, false);
		div.appendChild(del);
		var amtInput = document.createElement("input");
			amtInput.type = "text";
			amtInput.size = "5";
			amtInput.value = (amt== null)?"":amt;
		var unitInput = document.createElement("input");
			unitInput.type = "text";
			unitInput.value = (unit== null)?"":unit;
		var ingInput = document.createElement("input");
			ingInput.type = "text";
			ingInput.value = (name== null)?"":name;
		div.appendChild(amtInput);
		div.appendChild(unitInput);
		div.appendChild(ingInput);
		document.getElementById("myDiv").appendChild(div);
	}
	function save() {
		var shortcuts = "";
		if(eval(document.getElementById("spices").checked)) shortcuts += "s";
		if(eval(document.getElementById("baking").checked)) shortcuts += "b";
		if(eval(document.getElementById("veg").checked)) shortcuts += "v";
		getResults(getArray(1),getArray(2),getArray(3), shortcuts);
		
	}
	function getArray(j) {
		var parent = document.getElementById("myDiv");
		var temp = "";
		for(var i=2;i<parent.childNodes.length;i++) {
			if(parent.childNodes[i].tagName == "DIV")
			temp += ((parent.childNodes[i].childNodes[j].value != "")?parent.childNodes[i].childNodes[j].value:" ") +";";
		}
		return temp;
	}

</script>
<%
if(session.getAttribute("username") == null && request.getParameter("newUser") == null)
{%>
	<div class="error" id="error">you must be logged in to view this page</div>
<%}
else
{
	if( request.getParameter("newUser") != null && request.getParameter("newUser").equals("true"))
	{
		String name = request.getParameter("username");
		session.removeAttribute("newUser");
		out.println("create a new file here");
		FileOutputStream fout = new FileOutputStream(application.getRealPath("/users/"+name+".txt"));
		PrintStream write = new PrintStream(fout);
		write.println("password="+request.getParameter("password"));
		write.println("ingredients=0");
		write.println("shortcuts=\\");
		write.close();
		fout.close();
		
		session.setAttribute("username", name);
		%>
		<jsp:forward page="signin.jsp">
			<jsp:param name="link" value="updateUser.jsp"/>
			<jsp:param name="password" value="<%=request.getParameter("password")%>"/>
		</jsp:forward>
		<%
	}
	else out.println("<h4>Updating your existing pantry</h4>");
	//Load the pantry, (either populated or not) then allow editing to occur
	
	String strCount = (String) session.getAttribute("ingredients");
	int intCount = Integer.parseInt(strCount);
	%>
	<form>
		<input type="checkbox" id="spices" <%if(((String)session.getAttribute("shortcuts")).contains("s"))out.print("CHECKED");%>>I have a spice rack</input></br>
		<input type="checkbox" id="baking" <%if(((String)session.getAttribute("shortcuts")).contains("b"))out.print("CHECKED");%>>I have common baking supplies</input></br>
		<input type="checkbox" id="veg" <%if(((String)session.getAttribute("shortcuts")).contains("v"))out.print("CHECKED");%>>I am a vegetarian or I only want vegetarian recipes</input></br>
		<input type="button" onClick="save()" value="save"/>
	</form>
	<ul id ="myDiv" class="ingList">
	<script type="text/javascript">
	<%
	for(int i=0;i<intCount;i++)
	{
		out.print("addElement('"+session.getAttribute("count"+i)+"',");
		out.print("'"+session.getAttribute("unit"+i)+"',");
		out.println("'"+session.getAttribute("ingredient"+i)+"')");
	}	
%></script>
	</ul>
	</form>
		<p><a href="javascript:;" onclick="addElement();">Add a New Ingredient</a></p>
<%
}
%>
<%@ include file="bottomContent.jsp"%>