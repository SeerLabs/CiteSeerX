<%@ include file="shared/IncludeAuthHeader.jsp" %>
<div id="viewContent">
<div id="viewContent-inner">
  <div id="authSort">Sorted by: 
    <select name="sortvalue" id="sortvalue" onchange="location = this.options[this.selectedIndex].value;" class="pulldown">
      <option value="<c:url value='/viewauth/summary?${ citeq }'/>" <c:if test='${ sorttype eq "cite" }'>selected</c:if>>Citation Count</option>
      <option value="<c:url value='/viewauth/summary?${ dateq }'/>" <c:if test='${ sorttype eq "date" }'>selected</c:if>>Year (Descending)</option>
    </select>
  </div>
  
  <h3>Publications</h3>
  
  <c:if test="${empty docs}">
    <p class="para4">No docs identified.</p>
  </c:if>
  <c:set var="limit" value="20" />
  <c:if test="${ param.list == 'full' }">
    <c:set var="limit" value="${uauth.ndocs}" />
  </c:if>
  <c:if test="${!empty docs}">
  <table class="refs">
  <tr><td class="title"><c:if test='${ sorttype eq "cite" }'>#Cited</c:if>
    <c:if test='${ sorttype eq "date" }'>Year</c:if></td><td></td></tr>
  <c:forEach var="doc" items="${ docs }" begin="0" end="${limit - 1}">
  <tr><td class="title"><c:if test='${ sorttype eq "cite" && doc.ncites > 0}'><c:out value="${ doc.ncites }"/></c:if>
      <c:if test='${ sorttype eq "date" }'>
        <c:if test="${ doc.year > 0 }"><c:out value="${ doc.year }"/></c:if>
        <c:if test="${ doc.year == 0 }">-</c:if>
      </c:if>
    </td>
    <td><a href="<c:url value="/viewdoc/summary?doi=${ doc.doi }"/>"><c:out value="${ doc.title }"/></a>
      - <c:out value="${ doc.venue }"/> <c:out value="${ doc.authors }"/>
      <c:if test='${ sorttype eq "date" }'> - <c:out value="${ doc.ncites }"/> citations
      </c:if>
      <c:if test='${ sorttype eq "cite" && doc.year > 0 }'> - <c:out value="${ doc.year }"/></c:if>
    </td>
  </tr>
  </c:forEach>
  </table>
  </c:if>
  
  <c:if test="${ uauth.ndocs > 20 }">
  <c:choose>
  <c:when test="${param.list=='full'}">
    <c:url value="summary" var="summaryUrl">
      <c:param name="aid" value="${ param.aid }"/>
      <c:if test="${!empty param.sort}"><c:param name="sort" value="${ param.sort }"/></c:if>
    </c:url>
       <p><a class="link" href="<c:out value="${summaryUrl}"/>">View shorten publications << </a></p>
  </c:when>
  <c:otherwise>
    <c:url value="summary" var="summaryUrl">
      <c:param name="aid" value="${ param.aid }"/>
      <c:if test="${!empty param.sort}"><c:param name="sort" value="${ param.sort }"/></c:if>
      <c:param name="list" value="full"/>
    </c:url>
       <p><a class="link" href="<c:out value="${summaryUrl}"/>">View completed publications >> </a></p>
  </c:otherwise>
  </c:choose>
  </c:if> 
  </div>
</div><%-- viewContent close div --%>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>