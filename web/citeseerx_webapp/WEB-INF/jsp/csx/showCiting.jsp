<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="../shared/IncludeHeader.jsp" %>
<div id="main">
<%@ include file="../shared/IncludeSearchBox.jsp" %>
<div id="viewHeader" class="viewDoc">
  <h2><c:if test="${ inCollection }"><a href="<c:url value="/viewdoc/summary?cid=${cid}"/>"></c:if>
  <c:if test="${! empty title }"><c:out value="${ title }"/></c:if>
  <c:if test="${ empty title }">Unknown Title</c:if>
  <c:if test="${ inCollection }"></a></c:if>
  <c:if test="${ ! empty year }"> (<c:out value="${ year }"/>) </c:if></h2>
  <div id="docInfo">
    <div id="docAuthors">
      <c:if test="${ ! empty authors }">by <c:out value="${ authors }"/></c:if>
      <c:if test="${ empty authors }">unknown authors</c:if>
    </div>
    <div id="docOther">
      <table>
        <c:if test="${ ! empty venue }">
          <tr id="docVenue"><td class="title">Venue:</td><td><c:out value="${ venue }"/></td>
        </c:if>
      </table>
      <div class="save_doc"><a class="actionspan" onclick="addToCartProxy(<c:out value="${ cid }"/>)">Add To MetaCart</a> <span id="cmsg_<c:out value="${ cid }"/>" class="cartmsg"></span></div>
    </div>
  </div><%--docInfo close div --%>
</div>

<div id="content" class="sidebar">
  
  <div id="sidebar">
    <h3>Tools</h3>
    <!-- <div id="feeds">
      <a href="<c:url value='/search?${ rss }'/>"><img src="<c:url value='/images/rss.png'/>" alt="RSS"/></a>
    </div> -->
    <c:if test="${ empty param.t}" >
    <div id="sorting">Sorted by: 
      <select name="sortvalue" id="sortvalue" onchange="location = this.options[this.selectedIndex].value;" class="pulldown">
        <option value="<c:url value='/showciting?${ citeq }'/>" <c:if test='${ sorttype eq "cite" }'>selected</c:if>>Citation Count</option>
        <option value="<c:url value='/showciting?${ dateq }'/>" <c:if test='${ sorttype eq "date" }'>selected</c:if>>Year (Descending)</option>
        <option value="<c:url value='/showciting?${ ascdateq }'/>" <c:if test='${ sorttype eq "ascdate" }'>selected</c:if>>Year (Ascending)</option>
        <option value="<c:url value='/showciting?${ timeq }'/>" <c:if test='${ sorttype eq "recent" }'>selected</c:if>>Recency</option>
      </select>
    </div>
    </c:if>
  </div>
  
  <div id="result_info">
    Results <strong><fmt:formatNumber value="${ start+1 }" type="number"/> - <c:if test="${ (start+nrows) <= ncites }"><fmt:formatNumber value="${ start+nrows }" type="number"/></c:if>
    <c:if test="${ (start+nrows) > ncites }"><fmt:formatNumber value="${ ncites }" type="number"/></c:if></strong> of
    <strong><fmt:formatNumber value="${ ncites }" type="number"/></strong>    
    <div id="pager">
      <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/showciting?${ nextpageparams }"/>">Next <c:out value="${ nrows }"/> &#8594;</a></c:if>
    </div>
  </div>

  <div id="result_list">
    <c:if test="${ ncites == 0 && !error }">
      <div class="error">No citations were found for this document.</div>
    </c:if>
    <c:if test="${ error }">
      <div class="error"><c:out value="${ errorMsg }" escapeXml="false"/></div>
    </c:if>

  <c:forEach var="hit" items="${ hits }" varStatus="status">
    <div class="result">
      <h3>
        <a class="remove doc_details" href="<c:url value='/viewdoc/summary?doi=${ hit.doi }'/>">
        <c:if test="${ ! empty hit.title }"><c:out value="${ hit.title }" escapeXml="false"/></c:if>
        <c:if test="${ empty hit.title }">Unknown Title</c:if></a>
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
        <c:if test="${ ! empty hit.abstract }"><a class="abstract_toggle">Abstract</a></c:if>
        <c:if test="${ hit.ncites > 0 }"> - 
          <a class="citation remove" href="<c:url value='/showciting?cid=${ hit.cluster }'/>" title="number of citations">Cited by <c:out value='${ hit.ncites }'/> (<c:out value='${ hit.selfCites }'/> self)</a>
        </c:if>
        - <a class="save_doc" onclick="addToCartProxy(<c:out value='${hit.cluster}'/>)">Add to MetaCart</a>
        <span id="cmsg_<c:out value='${ hit.cluster }'/>" class="cartmsg"></span>
        <c:if test="${ ! empty hit.abstract }">
        
          <div class="pubabstract">
            <c:out value="${ hit.abstract}"/>
          </div>
        </c:if>
      </div>
      <c:choose>
        <c:when test="${citationContexts[status.index] != ''}">
        <a href="" onclick="toggleCitation('citation<c:out value="${status.index}" />'); return false;">(Show Context)</a>
        <div id="citation<c:out value="${status.index}" />" style="display:none">
          <p class='citationContextHeader'>Citation Context</p>
          <p class='citationContext'>...<c:out value="${ citationContexts[status.index] }" />...</p>
        </div>
        </c:when>
      </c:choose>
      <div class="pubtools">
        <c:if test="${ ! empty coins[status.index]}"><span class="Z3988" title="<c:out value='${coins[status.index]}' />"></span></c:if>
      </div>
    </div>
  </c:forEach>
  </div> <!-- End result_list -->
  </div>
  <div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>
