<%@ include file="topContent.jsp"%>
<span>
	<p>Thank you for taking this opportunity to register so you can take full advantage of all that
		the RecipeBox website has to offer!</p>
	<FORM METHOD=POST ACTION="updateUser.jsp">
		<input type="hidden" name="newUser" value="true">
		Requested Username <INPUT TYPE=TEXT NAME=username SIZE=20>
		Password <input type="text" name="password" size="20">
				<P><INPUT TYPE=SUBMIT>
	</FORM>
</span>
<%@ include file="bottomContent.jsp"%>