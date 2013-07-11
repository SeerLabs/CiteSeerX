<%@ include file="viewdoc_header.jsp" %>
<div id="main_content">  
  <div id="right-sidebar"> <!-- Contains left content -->
    <div class="inside"> <!-- to give some room between columns -->
    </div> <!--End inside -->
  </div> <!-- End of right-sidebar --> 
  <h2 class="topic_heading">Documents from <c:out value="${ hurl }"/></h2>
  <!-- List as title - firstauth, secondauth, et al (year) -->
  <!-- whitespace after number is visible on web page -->
  <c:if test="${empty hits}">
    <p class="para4">There are no more documents from <c:out value="${ hurl }"/></p>
  </c:if>
  <c:if test="${!empty hits}">
    <table class="citelist">
      <c:forEach var="hit" items="${ hits }">
        <tr><td>
          <c:if test="${ hit.ncites > 0 }"><span class="char_emphasized"><c:out value="${ hit.ncites }"/></span></c:if>
        </td>
        <td>
          <a href="<c:url value="/viewdoc/summary?doi=${ hit.doi }"/>"><c:out value="${ hit.title }" escapeXml="true" /></a>
          &ndash; <c:out value="${ hit.authors }"/>
          <c:if test="${ hit.year > 0 }"> - <c:out value="${ hit.year }"/></c:if>
        </td></tr>
      </c:forEach>
    </table>
    <div>
      <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/viewdoc/slocation?${ nextpageparams }"/>">More </a></c:if>
      <a href=""
    </div>
  </c:if>
  <div class="parent_div_spacer"></div>
</div> <!-- End main_content -->
<%@ include file="viewdoc_footer.jsp" %>