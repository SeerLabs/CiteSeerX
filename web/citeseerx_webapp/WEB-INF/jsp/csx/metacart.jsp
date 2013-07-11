<%@ include file="../shared/IncludeHeader.jsp" %>
<%-- Put your searchbox here. Best pratice is to customize and keep classes and ids. --%>
<div id="main">
<%@ include file="../shared/IncludeSearchBox.jsp" %>
<div id="content" class="sidebar">
  <h2>MetaCart</h2>
  <c:if test="${ ! empty docs }">  
    <div id="sidebar">
      <h3>Tools</h3>  
      <div id="saveCart">Save citations as:
        <ul>
          <li><a href="<c:url value="/metacart?dl=bibtex"/>">BibTeX</a></li>
          <li><a href="<c:url value="/metacart?dl=refbib"/>">Refer/BibIX</a></li>
        </ul>
      </div>
      <div id="clearCart"><form action="<c:url value="/metacart"/>" method="post"><button class="button csx" name="del" value="all">Clear All</button></form></div>
    </div>
  </c:if>
  
  
  <div id="result_list">
    <c:if test="${ error }">
      <p class="error"><c:out value="${ errMsg }"/></p>
    </c:if>

    <c:if test="${ empty docs }">
      <h3 class="error">Your cart currently contains no items</h3>
    </c:if>
  
    <c:if test="${ ! empty docs }">
      <c:forEach var="doc" items="${ docs }">
        <div class="result">
          <h3>
            <c:if test="${ doc.inCollection }"><a href="<c:url value="/viewdoc/summary?cid=${ doc.cluster }"/>"></c:if>
            <c:if test="${ ! doc.inCollection }"><a href="<c:url value="/showciting?cid=${ doc.cluster }"/>"></c:if>
            <c:if test="${ ! empty doc.title }"><c:out value="${ doc.title }"/></c:if>
            <c:if test="${ empty doc.title }">unknown title</c:if>
            </a>
          </h3>
          <div class="pubinfo">
            <span class="authors">by <c:if test="${ ! empty doc.authors }"><c:out value="${ doc.authors }"/></c:if>
              <c:if test="${ empty doc.authors }">unknown authors</c:if></span>
            <c:if test="${ ! empty doc.venue }"><span class="pubvenue">- <c:out value="${ doc.venue }"/></span></c:if>
            <c:if test="${ doc.year > 0 }"><span class="pubyear">, <c:out value="${ doc.year }"/></span></c:if>
          </div>
          <div class="pubextras">
            <form action="<c:url value="/metacart"/>" method="post">
              <button class="link csx" name="del" value="<c:out value="${ doc.cluster }"/>">Delete</button>
            </form>
          </div>
        </div>
      </c:forEach>
    </c:if>
  </div>

</div
  </div>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>