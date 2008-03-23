<%@ include file="topContent.jsp"%>
<%@ page import="java.io.*"%>
<script type="text/javascript">
//adapted from Dustin Diaz's script http://www.dustindiaz.com/add-and-remove-html-elements-dynamically-with-javascript/
function addElement() {
  var ni = document.getElementById('myDiv');
  var numi = document.getElementById('numIng');
  var num = (document.getElementById('numIng').value -0);
  if(num>0)
  {
	  var ingText = document.getElementById('ingredient'+num).value;
	  var unitText = document.getElementById('unit'+num).value;
	  var countText = document.getElementById('count'+num).value;
	  //alert("'"+ingText+"'");
  }
  if(num>0 &&(ingText == '' || unitText == '' || countText == ''))
  {
	document.getElementById('error').innerHTML = "Don't be greedy, fill in the form you have first";
  }
  else
  {
	  num++;
	  numi.value = num;	//Keeps track of the number of ingredients present
	  var newdiv = document.createElement('li');
	  var divIdName = ''+num;
	  newdiv.setAttribute('id',divIdName);
	  newdiv.innerHTML = '<a href=\'#\' onclick=\'removeElement("'+divIdName+'")\'>delete </a>';
	  newdiv.innerHTML += '<input type="text" name="count'+num+'" id="count'+num+'" size="10"/>';
	  newdiv.innerHTML += '<input type="text" name="unit'+num+'" id="unit'+num+'" size="10"/>';
	  newdiv.innerHTML += '<input type="text" name="ingredient'+num+'" id="ingredient'+num+'" size="30"/>';
	  ni.appendChild(newdiv);
	  document.getElementById('error').innerHTML = '';
  }
}

function removeElement(divNum) {
  var d = document.getElementById('myDiv');
  var numi = document.getElementById('numIng');
  var num = (document.getElementById('numIng').value -1);
  numi.value = num;
  var olddiv = document.getElementById(divNum);
  d.removeChild(olddiv);
  //alert("var i= "+((divNum-0)+1)+"; i<="+(num+1)+"&& "+(num+1)+">1" );
  for(var i=(divNum-0)+1;i<=(num+1)&& (num+1)>1;i++)
  {
	var li = document.getElementById(''+i);
	li.id = (li.id-0)-1;
	li.firstChild.setAttribute("onclick", "removeElement('"+(li.id-0)+"')");
	//alert(li.firstChild.getAttribute("onclick"));
	var unit = document.getElementById("unit"+i);
	unit.id = "unit"+((unit.id.charAt(4)-0)-1);
	unit.name = unit.id;
	var count = document.getElementById("count"+i);
	count.id = "count"+((count.id.charAt(5)-0)-1);
	count.name = count.id;
	var ingredient = document.getElementById("ingredient"+i);
	ingredient.id = "ingredient"+((ingredient.id.charAt(10)-0)-1);
	ingredient.name = ingredient.id;
  }
  
}
</script>
<%
if(session.getAttribute("username") == null)
{%>
	<div class="error" id="error">you must be logged in to view this page</div>
<%}
else
{
	if(request.getParameter("newUser") != null && request.getParameter("newUser").equals("true"))
	{
		String name = request.getParameter("username");
		session.removeAttribute("newUser");
		out.println("create a new file here");
		FileOutputStream fout = new FileOutputStream(application.getRealPath("/users/"+name+".txt"));
		PrintStream write = new PrintStream(fout);
		write.println("password="+request.getParameter("password"));
		write.println("ingredients=0");
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
	else out.println("we're updating the pantry of an existing user");
	//Load the pantry, (either populated or not) then allow editing to occur
	
	String strCount = (String) session.getAttribute("ingredients");
	int intCount = Integer.parseInt(strCount);
	%>
	<form method="post" action="writePantry.jsp"/>
		<input type="submit" value="save"/>
		<input type="hidden" name="prevNumItems" value="<%=session.getAttribute("ingredients")%>"/>
		<input id="numIng" type="hidden" name="ingredients" value="<%=session.getAttribute("ingredients")%>"/>
	<ul id ="myDiv" class="ingList">
	<%
	for(int i=1;i<=intCount;i++)
	{%>
		<li id="<%=i%>"><a href="#" onclick="removeElement('<%=i%>')">delete</a>
		<input type="hidden" name="ingredient<%=i%>" id="ingredient<%=i%>" value="<%=session.getAttribute("ingredient"+i)%>"/>
		<input type="hidden" name="unit<%=i%>" id="unit<%=i%>" value="<%=session.getAttribute("unit"+i)%>"/>
		<input type="hidden" name="count<%=i%>" id="count<%=i%>" value="<%=session.getAttribute("count"+i)%>"/>
		<%
		out.print(session.getAttribute("count"+i)+" ");
		out.print(session.getAttribute("unit"+i)+" ");
		out.println(session.getAttribute("ingredient"+i)+"<br/>");
		%>
		</li>	
	<%}	
%>
	</ul>
	</form>
		<p><a href="javascript:;" onclick="addElement();">Add Some Elements</a></p>
<%
}
%>
<%@ include file="bottomContent.jsp"%>