<%@ include file="shared/IncludeStatsHeader.jsp" %>
<div id="statsMenu" class="submenu">
<ul>
  <li><a href="<c:url value="/stats/articles"/>" >Most Cited Articles</a></li>
  <li><a href="<c:url value="/stats/citations"/>">Most Cited Citations</a></li>
  <li class="active"><a href="<c:url value="/stats/authors"/>">Most Cited Authors</a></li>
  <li><a href="<c:url value="/stats/venues"/>">Venue Impact Ratings</a></li>
</ul>
</div>


<div id="content">
  <div id="statsHeader">
    <h2>Most Cited Computer Science Authors</h2>
    <p>This is generated from documents in the <fmt:message key="app.nameHTML"/> database as of <c:out value="${ gendate }"/>.  An entry may correspond to multiple authors (e.g. J. Smith). This list is automatically generated and may contain errors. Citation counts may differ from search results because this list is generated in batch mode whereas the database is continually updated.</p>    
  </div>
            
  <div id="statsList">
   <ol start="<c:out value="${ start+1 }"/>">
    <c:forEach var="auth" items="${ authors }">
     <li>
       <span class="authors"><a href="<c:url value="/search?q=%22${ auth.name }%22&amp;sort=cite&amp;t=auth"/>"><c:out value="${ auth.name }"/></a>
	<div class="cites"><c:out value="${ auth.ncites }"/></div>
      </li>
    </c:forEach>
   </ol>
  </div>
  
   <div id="statsNav">
    <c:if test="${ ! empty nextPageParams && ! showingAll }">
     <a href="<c:url value="/stats/authors?${ nextPageParams }"/>">Next <c:out value="${ pageSize }"/> &#8594;</a>  
    </c:if>
   </div>

   </div> <!-- End primary_content -->
   </div>
   <%@ include file="../../shared/IncludeFooter.jsp" %>
