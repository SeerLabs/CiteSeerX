<%@ include file="shared/IncludeDocHeader.jsp" %>
<div id="viewContent">
  <h3>Active Bibliography</h3>
  <c:if test="${empty citations}">
    <p class="para4">No active bibliography identified.</p>
  </c:if>
  <c:if test="${!empty citations}">
    <table class="refs">
      <c:forEach var="citation" items="${ citations }">
        <tr><td class="title">
          <c:if test="${ citation.ncites > 0 }"><c:out value="${ citation.ncites }"/></c:if>
        </td><td>
          <c:if test="${ citation.inCollection }"><a href="<c:url value="/viewdoc/summary?cid=${ citation.cluster }"/>"><c:out value="${ citation.title }"/></a></c:if>
          <c:if test="${ ! citation.inCollection }"><a href="<c:url value="/showciting?cid=${ citation.cluster }"/>"><c:out value="${ citation.title }"/></a></c:if>
          &ndash; <c:out value="${ citation.authors }"/>
          <c:if test="${ citation.year > 0 }"> - <c:out value="${ citation.year }"/></c:if>
        </td></tr>
      </c:forEach>
    </table>
  </c:if>
</div>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>
