<%@ include file="shared/IncludeStatsHeader.jsp" %>
<div id="statsMenu" class="submenu">
  <ul>
    <li><a href="<c:url value="/stats/articles"/>" >Most Cited Articles</a></li>
    <li><a href="<c:url value="/stats/citations"/>">Most Cited Citations</a></li>
    <li><a href="<c:url value="/stats/authors"/>">Most Cited Authors</a></li>
    <li class="active"><a href="<c:url value="/stats/venues"/>">Venue Impact Ratings</a></li>
  </ul>
</div>

<div id="content">
  <div id="statsHeader">
    <h2>Estimated Venue Impact Factors</h2>
    <p> Generated from documents in the <fmt:message key="app.nameHTML"/> database as of <c:out value="${ gendate }"/>.  This list is automatically generated and may contain errors. Impact is estimated based on Garfield's traditional impact factor.</p>    
  </div>

  <div id="statsSubMenu">
    Choose Window:
    <c:forEach var="year" items="${ years }">
     <c:if test="${ year == currentlink }">&#124; <a id="currentlink" href="<c:url value="/stats/venues?y=${year}"/>"><c:out value="${ year }"/></a> </c:if>
     <c:if test="${ year != currentlink }">&#124; <a href="<c:url value="/stats/venues?y=${year}"/>"><c:out value="${ year }"/></a> </c:if>
    </c:forEach>
  </div>

   <div>
     Only venues with at least 25 articles are shown. Venue details obtained from <a href="http://dblp.uni-trier.de/">DBLP</a> by <a href="http://www.informatik.uni-trier.de/~ley/">Michael Ley</a>. Only venues contained in DBLP are included.
   </div>
   
   <div id="statsList">
     <ol start="<c:out value="1"/>">
      <c:forEach var="venue" items="${ venues }">
       <li>
         <a href="<c:url value="${ venue.url }"/>"><c:out value="${ venue.name }" escapeXml="false"/></a>
         <em style="font-weight:bold;"><c:out value="${ venue.impact }"/></em>
        </li>
      </c:forEach>
     </ol>
   </div>
   


            
  </div> <!-- End primary_content -->
 </div> <!-- End primary_tabs-n-content -->


  <%@ include file="../../shared/IncludeFooter.jsp" %>