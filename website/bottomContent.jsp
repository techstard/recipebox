						</div>
					</div>
					<div class="col2">
						<div>
							<h4>Nav Menu</h4>
							<ul class="navMenu">
								<li><a href="browse.jsp">Browse by Field </a></li>
								<li><a href="search.jsp">Search </a></li>
								<li>-<a href="search.jsp">Basic Search </a></li>
								<li>-<a href="pantrySearch.jsp">Pantry Search </a></li>
								<li><a href="planMenu.jsp">Plan a Menu </a></li>
								<li><a href="aboutUs.jsp">About Us </a></li>
							</ul>
						</div>
					</div>
					<div id="rightMenu" class="col3">
						<p>populated from user database</p>
						<%
						if(session.getAttribute("username")!= null)
						{
							%><%@ include file="test2.jsp"%><%
							
						}
						%>
					</div>
				</div>
			</div>
		</div>
		<div id="footer">
			<img style="float:right" src="valid-html401.png" alt="W3C 4.01 Validated">
		</div>
	</body>
</html>