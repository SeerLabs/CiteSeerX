<%@ include file="shared/IncludeDocHeader.jsp" %>
<div id="viewContent" class="sidebar">
  <div id="viewContent-inner">
    <div id="viewSidebar">
      <div id="versions" class="block">
        <h3>Versions</h3>
        <ul>
          <c:forEach var="vers" begin="0" end="${ maxversion }">
          <li<c:if test="${ vers==thisversion }"> class="active"</c:if>>
            <a href="<c:url value="/viewdoc/versions?doi=${ doi }&amp;version=${ vers }"/>">Version <c:out value="${ vers }"/></a>
          </li>
          </c:forEach>
        </ul>
      </div>
    </div>
    <h3>Version History</h3>
    <c:if test="${ error }"><p><c:out value="${ errMsg }"/></p></c:if>
    <c:if test="${ ! error }">
      <h4>Metadata Version <c:out value="${ thisversion }"/></h4>
      <c:if test="${ user != null }"> 
        <p>User correction supplied by <em><c:out value="${ user }"/></em></p>
      </c:if>
      <table class="version">
        <tr><th>Datum</th><th>Value</th><th>Source</th></tr>
        <c:if test="${ ! empty titlev }">
          <tr><td class="title">TITLE</td>
            <td><c:out value="${ titlev }"/></td>
            <td><c:out value="${ title_src }"/></td>
          </tr></c:if>
        <c:forEach var="auth" items="${ authv }">
          <tr><td>AUTHOR NAME</td>
            <td><c:out value="${ auth.name }"/></td>
            <td><c:out value="${ auth.nameSrc }"/></td>
          </tr>
          <c:if test="${ ! empty auth.affil }">
          <tr><td>AUTHOR AFFIL</td>
            <td><c:out value="${ auth.affil }"/></td>
            <td><c:out value="${ auth.affilSrc }"/></td>
          </tr></c:if>
          <c:if test="${ ! empty auth.addr }">
          <tr><td>AUTHOR ADDR</td>
            <td><c:out value="${ auth.addr }"/></td>
            <td><c:out value="${ auth.addrSrc }"/></td>
          </tr></c:if>
        </c:forEach>
        <c:if test="${ ! empty absv }">
        <tr><td>ABSTRACT</td>
          <td><c:out value="${ absv }"/></td>
          <td><c:out value="${ abs_src }"/></td>
          </tr></c:if>
        <c:if test="${ ! empty yearv }">
          <tr><td>YEAR</td>
            <td><c:out value="${ yearv }"/></td>
            <td><c:out value="${ year_src }"/></td>
          </tr></c:if>
        <c:if test="${ ! empty venuev }">
          <tr><td>VENUE</td>
            <td><c:out value="${ venuev }"/></td>
            <td><c:out value="${ venue_src }"/></td>
          </tr></c:if>
        <c:if test="${ ! empty ventypev }">
          <tr><td>VENUE TYPE</td>
            <td><c:out value="${ ventypev }"/></td>
            <td><c:out value="${ ventype_src }"/></td>
          </tr></c:if>
        <c:if test="${ ! empty pagesv }">
          <tr><td>PAGES</td>
            <td><c:out value="${ pagesv }"/></td>
            <td><c:out value="${ pages_src }"/></td>
          </tr></c:if>
        <c:if test="${ ! empty volv }">
          <tr><td>VOLUME</td>
            <td><c:out value="${ volv }"/></td>
            <td><c:out value="${ vol_src }"/></td>
          </tr></c:if>
        <c:if test="${ ! empty numv }">
          <tr><td>NUMBER</td>
            <td><c:out value="${ numv }"/></td>
            <td><c:out value="${ num_src }"/></td>
          </tr></c:if>
        <c:if test="${ ! empty techv }">
          <tr><td>TECH</td>
            <td><c:out value="${ techv }"/></td>
            <td><c:out value="${ tech_src }"/></td>
          </tr></c:if>
        <c:if test="${ ! empty citesv }">
          <tr><td>CITATIONS</td>
            <td><c:out value="${ citesv }"/></td>
            <td><c:out value="${ cites_src }"/></td>
          </tr></c:if>
      </table>
    </c:if>
  </div> <%-- viewContent div close --%>
</div> <%-- viewContent div close --%>
<%@ include file="../shared/IncludeFooter.jsp" %>