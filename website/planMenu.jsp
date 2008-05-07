<%
// Menu Planning:
// Basic search in main window, ask for main ingredient, return random result (or provide full search)
// 	Present recipe in iframe within my framework
// Right side (pantry window) taken over with Saturday-Sunday information
//
// produces a link to a dynamically created ical format xml file for inclusion in calendaring programs 
%>
<%@ include file="topContent.jsp"%>
		<script type="text/javascript" src="calendarPopup.js"></script>
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
						document.getElementById("parent").style.display = "block";
						document.getElementById('assign').style.display = "block";
						document.getElementById("recipePreview").style.display = "block";
			        } else {
			            alert ( "Not able to retrieve name" +http.status);
			        }
			    }    
			}

			function getResults(query) {
				var url = "getResults.jsp?"; // The server-side script	
			    var queryString = "f=menu&q="+query;
				http.open("GET", url + queryString, true);
			    http.onreadystatechange = handleHttpResponse;
			    http.send(null);
			}
			function loadPreview(url) {
				document.getElementById("recipePreview").src = url;
				document.getElementById("recipePreview").style.display = "block";
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
			function addInput(value) {
				var parent = document.getElementById("ingQuery");
				var input = document.createElement("tr");
				input.innerHTML = "<div>with<a id='ing"+(id++)+"' style='color:#BBBBBB;' title='true'>out <\/a><input type='text' value='"+((value!=null)?value:'')+"'/><\/div>"
				addEventListener(input, 'click', function(e) {
					if(e.target.tagName == "A") {
						if(e.target.title == "true") {
							e.target.title = "false";
							document.getElementById(e.target.id).style.color = "#000000";
						}
						else {
							e.target.title= "true";
							document.getElementById(e.target.id).style.color= "#BBBBBB";
						}
					}
				},false);
				parent.appendChild(input);
			}
			function submitQuery() {
				var parent = document.getElementById("ingQuery");
				var queryString = "";
				//alert(parent.childNodes.length);
				for(var i=1; i<parent.childNodes.length; i++) {
					queryString += getValue(parent.childNodes[i]) + ";";
				}
				getResults(queryString);
			}
			function getValue(row) {
				var include = "";
				if(row.firstChild.childNodes[1].title == "false") include = "!";
				return include + row.firstChild.childNodes[2].value;
			}
			function load() {
				http = getHTTPObject(); // We create the XMLHTTPRequest Object
				id=0;
				if(getParameter("q") != null) {
					getResults(getParameter("q"));
					addInput(getParameter("q"));
				}
				else addInput();
			}
			function getParameter(key) {
				var url = document.location.search;
				var start = url.indexOf(key+"=");
				if (start == -1) return null;
				var end = (url.indexOf("&",start) == -1)?url.length:url.indexOf("&",start);
				return unescape(url.substring(start+key.length+1,end));
			}
			function setSidebar(date) {
				//alert(formatDate(date,"EE, M/d"));

				var sidebarStr = "";
				currentDate.setMilliseconds(date.getMilliseconds());
				startDate.setMilliseconds(date.getMilliseconds());
				for(var i=0;i<7;i++) {
					sidebarStr += formatDate(date,"EE, M/d")+"<br/>";
					sidebarStr += "&nbsp;&nbsp;&nbsp;<a href='#' id='"+formatDate(date,"MM/dd/yy")+"' onClick='setDay("+'"'+formatDate(date,"MM/dd/yy")+'"'+")'>select a recipe<\/a><br/>";
					date.setMilliseconds(date.getMilliseconds()+ (day));
				}
				document.getElementById("rightMenu").innerHTML = sidebarStr;
				document.getElementById("currDate").innerHTML = formatDate(currentDate, "EE");
				document.getElementById("ingForm").style.display="block";
				document.forms['example'].style.display="none";
			}
			function assignRecipe() {
				var docId = document.getElementById("parent").firstChild.id;
				var docName = document.getElementById("parent").firstChild.getAttribute("name");
				var tag = formatDate(currentDate,"MM/dd/yy");

				document.getElementById(tag).href="";
				document.getElementById(tag).innerHTML=docName;
				var index = (currentDate-startDate)/day;
				docIdArray[index] = docId;
				var tomorrow = new Date(currentDate.getTime()+(day));
				if(document.getElementById(formatDate(tomorrow,"MM/dd/yy")) != null) {
					currentDate.setTime(tomorrow.getTime());
					setDay(tomorrow);
				}
				else alert("error: "+tomorrow);
			}
			function setDay(date) {
				document.getElementById("currDate").innerHTML = formatDate(date, "EE");
				document.getElementById("parent").innerHTML = "";
				document.getElementById('assign').style.display = "none";
				document.getElementById("recipePreview").style.display = "none";
			}
			var day = 1000*60*60*24;
			var docIdArray = new Array();
			var currentDate = new Date();
			var startDate = new Date();
			var cal = new CalendarPopup();
		</script>
		<form action="#" name='example'>
			<INPUT TYPE="text" NAME="date1" id="startDate" VALUE="" SIZE='25' style='display:none;'>
			<A HREF="#" onClick="cal.select(document.forms['example'].date1,'anchor1x','MM/dd/yyyy'); return false;" TITLE="cal1x.select(document.forms[0].date1x,'anchor1x','MM/dd/yyyy'); return false;" NAME="anchor1x" ID="anchor1x">Select a Start Date for your week's menu</A>
		</form>

		<form action="#" id="ingForm" style="display:none;">
			<h3>For <b id="currDate"></b> I want a recipe:</h3>
			<table id="ingQuery"><tr><td></td></tr></table>
			<input type="button" value="Make Query" onClick="submitQuery()">
			<input type="button" value="I'd eat this" id="assign" onClick="assignRecipe()" style="display:none;">
		</form>
		<table>
			<thead>
				<tr id="selector"><td></td></tr>
			</thead>
			<tbody id='parent'><tr><td></td></tr>
			</tbody>
		</table>
		<iframe id="recipePreview" style="display:none;height:100%;width:99%"></iframe>
<%@ include file="bottomContent.jsp"%>