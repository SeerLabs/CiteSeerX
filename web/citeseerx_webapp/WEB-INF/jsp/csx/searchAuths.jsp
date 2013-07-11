<%@ include file="../shared/IncludeHeader.jsp" %>
<%-- Put your searchbox here. Best pratice is to customize and keep classes and ids. --%>
<div id="main">
<%@ include file="../shared/IncludeSearchBox.jsp" %>

  <div id="content" class="sidebar">
    <%@ include file="shared/IncludeResultsSidebar.jsp" %>
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
          <c:forEach var="uauthor" items="${ uauthors }" varStatus="status">
            <div class="result">
              <h3><a href="<c:url value="/viewauth/summary?aid=${ uauthor.aid }"/>"><c:out value="${ uauthor.canname }" /></a></h3>
              <table class="authInfo" border="0" cellspacing="5" cellpadding="5">
                <c:if test='${ !empty uauthor.varnames }'>
                  <tr><td class="title">Variations</td>
                    <td><c:out value="${ uauthor.varnames }" /></td>
                  </tr></c:if>
                <c:if test='${ !empty uauthor.affil }'>
                  <tr><td class="title">Affiliations</td>
                    <td><c:out value="${ uauthor.affil }" /></td>
                  </tr></c:if>
                <c:if test='${ !empty uauthor.address }'>
                  <tr><td class="title">Address</td>
                    <td><c:out value="${ uauthor.address }" /></td>
                  </tr></c:if>
                <c:if test='${ !empty uauthor.ndocs }'>
                  <tr><td class="title">Papers</td>
                    <td><c:out value="${ uauthor.ndocs }" /></td>
                  </tr></c:if>                    
                <c:if test='${ ! empty uauthor.url }'>
                  <tr><td class="title">Homepage</td>
                    <td><a href="${ uauthor.url }"><c:out value="${ uauthor.url }" /></a></td>
                  </tr></c:if>                        
              </table>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>
    <c:if test="${ nfound != 0}">
      <div id="result_info">
        <%@ include file="shared/IncludeResultsPager.jsp" %>
        <%@ include file="shared/IncludeResultsInfo.jsp" %>
      </div>
    </c:if>
  </div>

  
  <div class="clear"></div>
</div>

<%@ include file="../shared/IncludeFooter.jsp" %>
