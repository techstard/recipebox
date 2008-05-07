<%@ include file="topContent.jsp"%>
	<script language="javascript" type="text/javascript">
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
	// And add the unload event handler that will unload all the registered
	// handlers, including itself.
	addEventListener(window, 'unload', unregisterEventListeners, false);
	
	var http; 
	var field = "title";
	
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
		if(field == "title") var queryString = "f=recipeTitle&q="+query;
		else if(field == "ingredient") var queryString = "f=ingredient&q="+query;
	    //alert(queryString);
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
	function load() {
		field = "title";
		var selector = document.getElementById("selector");
		var categories = document.getElementById("cats");
			var title = document.createElement("td");
			title.style.fontWeight = "bold";
			title.setAttribute("id","title");
			title.innerHTML = "Title";
			var ingredient = document.createElement("td");
			ingredient.setAttribute("id","ingredient");
			ingredient.innerHTML = "Ingredient";
			categories.appendChild(title);
			categories.appendChild(ingredient);
			addEventListener(title,'click', function(){
				//alert("title clicked, field = "+field);
				if(field == "title") return;
				document.getElementById(field).style.fontWeight = "";
				field = "title";
				document.getElementById(field).style.fontWeight = "bold";
			}, false);
			addEventListener(ingredient,'click', function(){
				//alert("ingredient clicked, field = "+field);
				if(field == "ingredient") return;
				document.getElementById(field).style.fontWeight = "";
				field = "ingredient";
				document.getElementById(field).style.fontWeight = "bold";
			}, false);
		var temp = new Array();
		http = getHTTPObject(); // We create the XMLHTTPRequest Object
		for(var i=65;i<91;i++)
		{
			temp[i-65] = document.createElement('td');
			temp[i-65].innerHTML = unescape('%'+i.toString(16));
			addEventListener(temp[i-65], 'click', function(e) {
				//alert(e);
				getResults(e.target.innerHTML.toLowerCase()+"*");
			}, false);
			selector.appendChild(temp[i-65]);
		}
	}
	</script>
	<form action="#">
	 <table>
		<thead id='cats'><tr><td></td></tr></thead>
		<tbody>
			<tr><td>
			<table>
				<thead>
					<tr id="selector"><td></td></tr>
				</thead>
				<tbody id='parent'>
					<tr><td colspan="26">This is a Result!</td></tr>
				</tbody>
			</table>
			</td></tr>
		</tbody>
	 </table>
	</form>
<%@ include file="bottomContent.jsp"%>