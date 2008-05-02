<%@ include file="topContent.jsp"%>
<html>
	<head>
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
			var id;

			function handleHttpResponse() {
			    if (http.readyState == 4) {
			        if (http.status == 200) {
			            //alert("handleHTTPResponse");            
			            document.getElementById('parent').innerHTML = "";
						document.getElementById('parent').innerHTML = http.responseText;
			        } else {
			            alert ( "Not able to retrieve name" +http.status);
			        }
			    }    
			}

			function getResults(query) {
				var url = "getResults.jsp?"; // The server-side script	
			    var queryString = "f=pantry&q="+query;
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

			function submitQuery() {
				var parent = document.getElementById("ingQuery");
				var queryString = "";
				var mainIng = "";
				var mainFound = false;
				for(var i=0; i<parent.childNodes.length; i++) {
					if(parent.childNodes[i].tagName == "INPUT") {
						if(eval(parent.childNodes[i].checked)) {
							mainFound = true;
							mainIng = parent.childNodes[i].value+";";
						}
						else queryString += parent.childNodes[i].value +";";
					}
				}
				if(mainFound) getResults(mainIng+queryString);
				else alert("you must select a main ingredient!");
			}
			function getValue(row) {
				var include = "";
				if(row.firstChild.childNodes[1].title == "false") include = "!";
				return include + row.firstChild.childNodes[2].value;
			}
			function load() {
				http = getHTTPObject(); // We create the XMLHTTPRequest Object
				id=0;
			}
			function getParameter(key) {
				var url = document.location.search;
				var start = url.indexOf(key+"=");
				if (start == -1) return null;
				var end = (url.indexOf("&",start) == -1)?url.length:url.indexOf("&",start);
				return unescape(url.substring(start+key.length+1,end));
			}
		</script>
	</head>
	<body onLoad="load()">
		<h3>I want a recipe using only these ingredients with:</h3>
		<form><div id="ingQuery">
<%	if(session.getAttribute("username")!= null) { 
		String strCount3 = (String) session.getAttribute("ingredients");
		int intCount3 = Integer.parseInt(strCount3);
		for(int i=0;i<intCount3;i++)
		{	out.print("<input type='radio' name='ing' value='"+session.getAttribute("ingredient"+i)+"'/>");
			out.print(session.getAttribute("count"+i)+" ");
			out.print(session.getAttribute("unit"+i)+" ");
			out.println(session.getAttribute("ingredient"+i)+"<br/>");
		}
	}
	else out.print ("<h3> You must be logged in to use this feature </h3>");
%>
		</div>
		<h3>as the main ingredient</h3>
			<input type="button" value="Make Query" onClick="submitQuery()"/>
		</form>
		<table>
			<thead>
				<tr id="selector"/>
			</thead>
			<tbody id='parent'>
			</tbody>
		</table>
	</body>
</html>
<%@ include file="bottomContent.jsp"%>