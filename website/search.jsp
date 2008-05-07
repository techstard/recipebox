<%@ include file="topContent.jsp"%>
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
		alert(query);
		var url = "getResults.jsp?"; // The server-side script	
		var queryString = "f=search&q="+query;
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
	function addInput(value) {
		var parent = document.getElementById("ingQuery");
		var input = document.createElement("tr");
		input.innerHTML = "<div>with<a id='ing"+(id++)+"' style='color:#BBBBBB;' title='true'>out <\/a><input type='text' value='"+((value!=null)?value:'')+"'>(<a href='#'>remove<\/a>)<\/div>"
		addEventListener(input, 'click', function(e) {
			if(e.target.tagName == "A") {
				if(e.target.title == "true") {
					e.target.title = "false";
					document.getElementById(e.target.id).style.color = "#000000";
				}
				else if(e.target.title == "false") {
					e.target.title= "true";
					document.getElementById(e.target.id).style.color= "#BBBBBB";
				}
				else {//remove elements
					e.target.parentNode.parentNode.parentNode.removeChild(e.target.parentNode.parentNode);
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
</script>
		<h3>I want a recipe:</h3>
		<table id="ingQuery"><tr><td></td></tr></table>
		<form action="#">
			<input type="button" value="Add an Ingredient" onClick="addInput()">
			<input type="button" value="Make Query" onClick="submitQuery()">
		</form>
		<table>
			<thead>
				<tr id="selector"><td></td>
			</thead>
			<tbody id='parent'><tr><td></td></tr>
			</tbody>
		</table>
<%@ include file="bottomContent.jsp"%>