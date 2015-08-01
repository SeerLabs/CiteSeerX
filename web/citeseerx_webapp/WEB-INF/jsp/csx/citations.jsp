<%@ include file="shared/IncludeDocHeader.jsp" %>
  <div id="viewContent" class="sidebar">
    <div id="viewContent-inner">
      <div id="viewSidebar">
        <% if (mscConfig.getPersonalPortalEnabled()) {%>
          <%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
          <%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
          <%@ page import="org.springframework.security.AuthenticationException" %>
        <% } %>
      </div> <%-- sidebar div close --%>
      <div id="citations">
        <h3>Citations</h3>
        <c:if test="${empty citations}"><p>No citations identified.</p></c:if>
        <c:if test="${!empty citations}">
          <table class="refs" border="0" cellspacing="5" cellpadding="5">
            <c:forEach var="citation" items="${ citations }" varStatus="theCount">
              <tr><td class="title"><c:if test="${ citation.ncites > 0 }"><c:out value="${ citation.ncites }"/></c:if></td>
                <td><c:if test="${ citation.inCollection }"><a href="<c:url value="/viewdoc/summary?cid=${ citation.cluster }"/>"><c:out value="${ citation.title }" escapeXml="true" /></a></c:if>
                  <c:if test="${ ! citation.inCollection }"><a class="citation_only" href="<c:url value="/showciting?cid=${ citation.cluster }"/>"><c:out value="${ citation.title }" escapeXml="true" /></a></c:if>
                - <c:out value="${ citation.authors }"/>
                  <c:if test="${ citation.year > 0 }"> - <c:out value="${ citation.year }"/></c:if>
                  <c:choose>
                    <c:when test="${citationContexts[theCount.index] != ''}">
                    <a href="" onclick="toggleCitation('citation<c:out value="${theCount.index}" />'); return false;">(Show Context)</a>
                    <div id="citation<c:out value="${theCount.index}" />" style="display:none">
                      <p class='citationContextHeader'>Citation Context</p>
                      <p class='citationContext'>...<c:out value="${ citationContexts[theCount.index] }" />...</p>
                    </div>
                    </c:when>
                  </c:choose>
                </td></tr>
              </c:forEach>
          </table>
        </c:if>
      </div><%-- citations close div --%>
    </div>
  </div><%-- viewContent close div --%>
  <div class="clear"></div>
</div>

<%@ include file="../shared/IncludeFooter.jsp" %>
