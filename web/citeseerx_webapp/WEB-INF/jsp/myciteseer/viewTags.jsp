<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<script type="text/javascript">
  $("#tags").addClass('active');
</script>
  <div id="content">
    <h2>My Tags</h2>
    <div>
     <c:if test="${ empty tags }">No tags were found for this account.</c:if>
     <c:if test="${ ! empty tags }">
      <ul id="tagcloud">
       <c:forEach var="tag" items="${ tags }">
        <c:if test="${ tag.count < 3 }">
         <li class="tag1">
        </c:if>
        <c:if test="${ tag.count >= 3 && tag.count < 5 }">
         <li class="tag2">
        </c:if>
        <c:if test="${ tag.count >= 5 && tag.count < 8 }">
         <li class="tag3">
        </c:if>
        <c:if test="${ tag.count >= 8 && tag.count < 11 }">
         <li class="tag4">
        </c:if>
        <c:if test="${ tag.count >= 11 && tag.count < 15 }">
         <li class="tag5">
        </c:if>
        <c:if test="${ tag.count >= 15 }">
         <li class="tag6">
        </c:if>      
        <a href="<c:url value="/myciteseer/action/viewTaggedDocs?tag=${ tag.tag }"/>"><c:out value="${ tag.tag }"/></a></li>
       </c:forEach>
      </ul>      
     </c:if>
    </div>

  </div> <%-- end content div --%>
  <div class="clear"></div>
</div> <%-- end main div --%>
<%@ include file="../shared/IncludeFooter.jsp" %>