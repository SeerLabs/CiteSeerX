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
        <c:when test="${ !error && resultsize == 0 }">
          <%-- Display error for no results --%>
          <div class="error"><%@ include file="shared/IncludeResultsError.jsp" %></div>
        </c:when>
        <c:otherwise>
          <%-- Sorting Dropdown--%>
          <c:if test="${ error }"><div class="error"><c:out value="${ errorMsg }" escapeXml="false"/></div></c:if>
          <c:forEach var="hit" items="${ hits }" varStatus="status">
            <div class="result">
              <h3>
                <c:if test="${ hit.inCollection }">
                  <a class="remove doc_details" href="<c:url value='/viewdoc/summary?doi=${ hit.doi }'/>">
                  <c:if test="${ ! empty hit.title }"><c:out value="${ hit.title }" escapeXml="false"/></c:if>
                  <c:if test="${ empty hit.title }">Unknown Title</c:if></a>
                </c:if>
                <c:if test="${ ! hit.inCollection }">
                  <span class="remove doc_details">&#91;CITATION&#93;
                  <c:if test="${ ! empty hit.title }"><c:out value="${ hit.title }" escapeXml="false"/></c:if>
                  <c:if test="${ empty hit.title }">Unknown Title</c:if></span>"
                </c:if>
              </h3>
              <div class="pubinfo">
                <span class="authors">by 
                  <c:if test="${ ! empty hit.authors }"><c:out value="${ hit.authors }"/></c:if>
                  <c:if test="${ empty hit.authors }">unknown authors</c:if>
                </span>
                <c:if test="${ ! empty hit.venue }">
                  <span class="pubvenue">- <c:out value="${ hit.venue }"/></span>
                </c:if>
                <c:if test="${ ! empty hit.year && hit.year > 0 }">
                  <span class="pubyear">, <c:out value="${ hit.year }"/></span>
                </c:if>
              </div>
              <div class="snippet">"... <c:out value="${ hit.snippet }" escapeXml="false"/> ..."</div>
              <div class="pubextras">
                <c:if test="${ hit.inCollection }">
                  <c:if test="${ ! empty hit['abstract'] }"><a class="abstract_toggle">Abstract</a></c:if>
                </c:if>
                <c:if test="${ hit.ncites > 0 }"> - 
                  <a class="citation remove" href="<c:url value='/showciting?cid=${ hit.cluster }'/>" title="number of citations">Cited by <c:out value='${ hit.ncites }'/> (<c:out value='${ hit.selfCites }'/> self)</a>
                </c:if>
                - <a class="save_doc" onclick="addToCartProxy(<c:out value='${hit.cluster}'/>)">Add to MetaCart</a>
                <span id="cmsg_<c:out value='${ hit.cluster }'/>" class="cartmsg"></span>
                <c:if test="${ hit.inCollection }">
                  <c:if test="${ ! empty hit['abstract'] }">
                  <div class="pubabstract">
                    <c:out value="${ hit['abstract']}"/>
                  </div>
                  </c:if>
                </c:if>
              </div>
              <div class="pubtools">
                <c:if test="${ ! empty coins[status.index]}"><span class="Z3988" title="<c:out value='${coins[status.index]}' />"></span></c:if>
              </div>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>
    <c:if test="${ resultsize != 0}">
      <div id="result_info">
        <%@ include file="shared/IncludeResultsPager.jsp" %>
        <%@ include file="shared/IncludeResultsInfo.jsp" %>
      </div>
    </c:if>
  </div>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>
