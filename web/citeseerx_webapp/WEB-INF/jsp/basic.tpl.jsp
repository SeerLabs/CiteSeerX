<%@ include file="shared/IncludeHeader.jsp" %>
<%-- Put your searchbox here. Best pratice is to customize and keep classes and ids. --%>
<div id="main">
<%@ include file="shared/IncludeSearchBox.jsp" %>
  <div id="menu">
    <ul>
      <li><a href="<c:url value='/about/site'/>">About <fmt:message key="app.name"/></a></li>
      <li><a href="<c:url value='/about/myciteseer'/>">About <fmt:message key="app.portal"/></a></li>
      <li><a href="<c:url value=/about/team/>">Team</a></li>
      <li><a href="<c:url value='/about/metadata'/>">Metadata</a></li>
      <li><a href="<c:url value='/about/previous'/>">Our Sponsors</a></li>
      <li><a href="<c:url value='/about/bot'/>"><span>Crawler</span></a></li>
    </ul>
  </div>
	<div id="content">
		<div id="result_info">
			<%@ include file="shared/IncludeResultsInfo.jsp" %>
			<%@ include file="shared/IncludeResultsPager.jsp" %>
		</div>
		<div id="result_list">
			<%-- Check to make sure we actually got results --%>
			<c:choose>
				<c:when test="${ nfound == 0 }">
					<%-- Display error for no results --%>
					<div class="error"><%@ include file="shared/IncludeResultsError.jsp" %></div>
				</c:when>
				<c:otherwise>
					<c:if test="${ error }"><div class="error"><c:out value="${ errorMsg }" escapeXml="false"/></div></c:if>
		      <c:forEach var="hit" items="${ hits }" varStatus="status">
		       	<div class="result">
		        	<h3>
		         		<a class="remove doc_details" href="<c:url value='/viewdoc/summary?doi=${ hit.doi }'/>">
								<c:if test="${ ! empty hit.title }"><c:out value="${ hit.title }" escapeXml="false"/></c:if>
								<c:if test="${ empty hit.title }">Unknown Title</c:if></a>
		          </h3>
		          <c:if test="${ ! empty coins[status.index]}"><span class="Z3988" title="<c:out value='${coins[status.index]}' />"></span></c:if>
		          <span class="authors">
		 						<c:if test="${ ! empty hit.authors }"><c:out value="${ hit.authors }"/></c:if>
		            <c:if test="${ empty hit.authors }">unknown authors</c:if>
		          </span>
							<span class="pubinfo">
								<c:if test="${ ! empty hit.venue }"> &#8212; <c:out value="${ hit.venue }"/></c:if><c:if test="${ ! empty hit.year && hit.year > 0 }">, <c:out value="${ hit.year }"/></c:if>
							</span>
							<p><c:out value="${ hit.snippet }" escapeXml="false"/>...</p>
							<div class="pubtools">
								<c:if test="${ hit.ncites > 0 }">
									<a class="citation remove" href="<c:url value='/showciting?cid=${ hit.cluster }'/>" title="number of citations">Cited by <c:out value='${ hit.ncites }'/> (<c:out value='${ hit.selfCites }'/> self)</a>
								</c:if>
								<a class="save_doc" onclick="addToCartProxy(<c:out value='${hit.cluster}'/>)"><img src="<c:url value='/images/page_add.gif'/>" alt="Save to MetaCart"/> Save</a>
						 		<span id="cmsg_<c:out value='${ hit.cluster }'/>" class="cartmsg"></span>
							</div>
						</div>
		     	</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
		<c:if test="${ nfound != 0}">
			<div id="result_info">
				<%@ include file="shared/IncludeResultsInfo.jsp" %>
				<%@ include file="shared/IncludeResultsPager.jsp" %>
			</div>
		</c:if>
	</div>
	
	<div id="sidebar">
		<%@ include file="shared/IncludeSidebar.jsp" %>
	</div>
</div>
          
<%@ include file="shared/IncludeFooter.jsp" %>
