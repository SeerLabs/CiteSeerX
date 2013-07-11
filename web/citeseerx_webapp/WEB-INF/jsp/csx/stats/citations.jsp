<%@ include file="shared/IncludeStatsHeader.jsp" %>
<div id="statsMenu" class="submenu">
<ul>
  <li><a href="<c:url value="/stats/articles"/>" >Most Cited Articles</a></li>
  <li class="active"><a href="<c:url value="/stats/citations"/>">Most Cited Citations</a></li>
  <li><a href="<c:url value="/stats/authors"/>">Most Cited Authors</a></li>
  <li><a href="<c:url value="/stats/venues"/>">Venue Impact Ratings</a></li>
</ul>
</div>

<div id="content">
  <div id="statsHeader">
    <h2>Most Cited Computer Science Citations</h2>
    <p>This list is generated from documents in the <fmt:message key="app.nameHTML"/> database as of <c:out value="${ gendate }"/>.  This list is automatically generated and may contain errors. The list is generated in batch mode and citation counts may differ from those currently in the <fmt:message key="app.nameHTML"/> database, since the database is continuously updated.</p>    
  </div>

  <div id="statsSubMenu">
     <c:if test="${ empty currentlink }"><a class="active" href="<c:url value="/stats/citations"/>">All Years</a> </c:if>
     <c:if test="${ ! empty currentlink }"><a href="<c:url value="/stats/citations"/>">All Years</a> </c:if>
     <c:forEach var="year" items="${ years }">
      <c:if test="${ year == currentlink }">&#124; <a id="currentlink" href="<c:url value="/stats/citations?y=${year}"/>"><c:out value="${ year }"/></a> </c:if>
      <c:if test="${ year != currentlink }">&#124; <a href="<c:url value="/stats/citations?y=${year}"/>"><c:out value="${ year }"/></a> </c:if>
     </c:forEach>
    </div>
    
    
  <div id="statsList">          
   <ol start="<c:out value="${ start+1 }"/>">
    <c:forEach var="doc" items="${ docs }">
     <c:if test="${ doc.inCollection }">
      <li>
        <div class="bibcite">
        <div class="authors"><c:out value="${ doc.authors }"/></div>
        <a href="<c:url value="/viewdoc/summary?cid=${ doc.cluster }"/>"><c:out value="${ doc.title }"/></a>. 
        <c:out value="${ doc.venue }"/>
        <c:out value="${ doc.year }"/>
	<div class="cites"><c:out value="${ doc.ncites }"/></div>
        </div>
      </li>
     </c:if>
     <c:if test="${ ! doc.inCollection }">
      <li>
        <div class="bibcite">
        <div class="authors"><c:out value="${ doc.authors }"/></div>
        <a class="citation_only" href="<c:url value="/showciting?cid=${ doc.cluster }"/>"><c:out value="${ doc.title }"/></a>
        <c:out value="${ doc.venue }"/>
        <c:out value="${ doc.year }"/>
	<div class="cites"><c:out value="${ doc.ncites }"/></div>
        </div>
      </li>
     </c:if>
    </c:forEach>
   </ol>
  </div>
   <div id="statsNav">
    <c:if test="${ ! empty nextPageParams }">
     <a href="<c:url value="/stats/citations?${ nextPageParams }"/>">Next <c:out value="${ pageSize }"/> &#8594;</a>  
    </c:if>
   </div>
       
            
 </div> <!-- End primary_content -->
 </div>
 <%@ include file="../../shared/IncludeFooter.jsp" %>
