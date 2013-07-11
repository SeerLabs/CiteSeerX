<%@ include file="shared/IncludeDocHeader.jsp" %>
<div id="viewContent">
  <h3 class="topic_heading">Document Versions</h3>
  <!-- List as title - firstauth, secondauth, et al (year) -->
  <!-- whitespace after number is visible on web page -->
  <c:if test="${empty citations}">
    <p class="para4">There are no papers in this cluster.</p>
  </c:if>
  <c:if test="${!empty citations}">
    <table class="citelist">
      <c:forEach var="hit" items="${ citations }" varStatus="status">
        <tr>
          <td>
            <a class="remove doc_details" href="<c:url value="/viewdoc/summary?doi=${ hit.doi }"/>"><em class="title"><c:if test="${ ! empty hit.title }"><c:out value="${ hit.title }" escapeXml="false"/></c:if><c:if test="${ empty hit.title }">unknown title</c:if></em></a>
            &ndash;<c:if test="${ ! empty hit.authors }"><c:out value="${ hit.authors }"/></c:if>
            <c:if test="${ empty hit.authors }">unknown authors</c:if>
            <c:if test="${ ! empty hit.year && hit.year > 0 }"> &#8212; <c:out value="${ hit.year }"/></c:if>
            <c:if test="${ ! empty hit.venue }"> &#8212; <c:out value="${ hit.venue }"/></c:if> 
           </td>
        </tr>
      </c:forEach>
    </table>
  </c:if>
  </div>
  <div class="clear"></div>
  </div>
  <%@ include file="../shared/IncludeFooter.jsp" %>