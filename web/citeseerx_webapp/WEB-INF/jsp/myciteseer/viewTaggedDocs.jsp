<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<script type="text/javascript">
  $("#tags").addClass('active');
</script>
  <div id="content">
    <h2>Documents tagged as &quot;<c:out value="${ tag }"/>&quot;</h2>

    <div id="result_info">
      Found <strong><c:out value="${ nresults }"/></strong> papers
      <div id="pager">
        <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/myciteseer/action/viewTaggedDocs${ nextpageparams }"/>">Next <c:out value="${ nrows }"/> &#8594;</a></c:if>
      </div>
    </div>
    
    <div id="result_list">
      <c:if test="${empty papers}"><span class="char_increased">No papers have been found.</span></c:if>
      <c:if test="${!empty papers}">
        <c:forEach var="paper" items="${papers}">
          <div class="result">
            <h3>
              <a class="paper-tips remove doc_details" href="<c:url value='/viewdoc/summary?doi=${paper.doc.doi}'/>" title="<c:out value='${paper.doc.title}'/>"><c:out value="${paper.doc.title}"/></a>
            </h3>
            <div class="pubinfo">
              <span class="authors">by 
                <c:if test="${ ! paper.doc.authors }"><c:out value="${ paper.doc.authors }"/></c:if>
                <c:if test="${ empty paper.doc.authors }">unknown authors</c:if>
              </span>
              <c:if test="${ ! empty paper.doc.venue }">
                <span class="pubvenue">- <c:out value="${ paper.doc.venue }"/></span>
              </c:if>
              <c:if test="${! empty paper.doc.year && paper.doc.year > 0}">
                <span class="pubyear">, <c:out value="${paper.doc.year}"/></span>
              </c:if>
              <c:if test="${ ! empty paper.coins}">
                <span class="Z3988" title="<c:out value='${paper.coins}' />"></span>
              </c:if>
            </div>
            <div class="pubextras">
              <c:if test="${ paper.doc.ncites > 0 }">
                <a class="citation remove" href="<c:url value="/showciting?cid=${ paper.doc.cluster }"/>" title="number of citations">Cited by <c:out value="${ paper.doc.ncites }"/> (<c:out value="${ paper.doc.selfCites }"/> self)</a> &ndash;</c:if>
                <a href="<c:url value="/myciteseer/action/editTags?tag=${ tag }&amp;doi=${ paper.doc.doi }&amp;type=del"/>" title="Delete">Delete Tag</a> &ndash;
                <span class="link" onclick="addToCartProxy(<c:out value="${paper.doc.cluster}"/>)">Add To MetaCart</span> <span id="cmsg_<c:out value="${ paper.doc.cluster }"/>" class="cartmsg"></span>
            </div>
          </div>
        </c:forEach>
      </c:if>
    </div>
  </div> <%-- end content div --%>
  <div class="clear"></div>
</div> <%-- end main div --%>
<%@ include file="../shared/IncludeFooter.jsp" %>