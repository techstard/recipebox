						</div>
					</div>
					<div class="col2">
						<span>
							<h4>Nav Menu</h4>
							<ul class="navMenu">
								<li><a>Browse Recipes </a></li>
								<li><a>Search </a></li>
								<li><a>Plan a Menu </a></li>
								<li><a>About Us </a></li>
								<li><a>Additional Things </a></li>
							</ul>
						</span>
					</div>
					<div class="col3">
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
		</div>
	</body>
</html>