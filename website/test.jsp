<html>
	<head>
		<script type="text/javascript">
			function addInput() {
					var parent = document.getElementById("ingQuery");
					var input = document.createElement("tr");
					input.innerHTML = "<div><a href='#'>x</a><input type='text'/></div>"
					parent.appendChild(input);
			}
			function submitQuery() {
				var parent = document.getElementById("ingQuery");
				var queryString = "";
				alert(parent.childNodes.length);
				for(var i=0; i<parent.childNodes.length; i++) {
					queryString += getValue(parent.childNodes[i]) + ";";
				}
				alert(queryString);
			}
			function getValue(row) {
				return row.firstChild.childNodes[1].value;
			}
		</script>
	</head>
	<body onLoad="addInput()">
		<table id="ingQuery"></table>
		<form>
			<input type="button" value="Add an Ingredient" onClick="addInput()"/>
			<input type="button" value="Make Query" onClick="submitQuery()"/>
		</form>
	</body>
</html>