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
          <%-- Sorting Dropdown--%>
          <c:if test="${ error }"><div class="error"><c:out value="${ errorMsg }" escapeXml="false"/></div></c:if>
          <c:forEach var="hit" items="${ hits }" varStatus="status">
            <div class="result">
              <h3><c:out value="${ hit.caption }" escapeXml="false"/></h3>
              <div class="doc">
                in <a href="viewdoc/summary?doi=${ hit.paperIDForAlgorithm  }" ><c:out value="${ hit.title }" escapeXml="false"/></a>
              </div>
              <div class="pubinfo">
                <span class="authors">by
                  <c:if test="${ ! empty hit.authors }"><c:out value="${ hit.authors }"/></c:if>
                  <c:if test="${ empty hit.authors }">unknown authors</c:if>
                </span>
                <span class="pubyear">
                  <c:if test="${ ! empty hit.paperYear && hit.paperYear > 0 }"><c:out value="${ hit.paperYear }"/></c:if>
                </span>
              </div>
              <div class="snippet">
                <c:if test="${! empty hit.algorithmReference}">"... <c:out value="${ hit.algorithmReference }" escapeXml="false"/> ..."</c:if>
              </div>
              <div class="pubextras">
                <c:if test="${ !empty hit.ncites && hit.ncites > 0 }">
                  Cited by <c:out value="${ hit.ncites }"/>
                </c:if>
              </div>
              <div class="snippet">
                <c:if test='${ param.tc eq "true" }'>
                  <c:out value="${ hit.content }" escapeXml="false"/>
                </c:if>
              </div>
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