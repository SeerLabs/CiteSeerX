<c:choose>
<c:when test="${ resultsize > 0 }">
Results <strong><fmt:formatNumber value="${ start+1 }" type="number"/> - <c:if test="${ (start+nrows) <= resultsize }"><fmt:formatNumber value="${ start+nrows }" type="number"/></c:if>
<c:if test="${ (start+nrows) > resultsize }"><fmt:formatNumber value="${ resultsize }" type="number"/></c:if></strong> of
<strong><fmt:formatNumber value="${ resultsize }" type="number"/></strong>
</c:when>
<c:otherwise>
	<strong>No results found</strong>
</c:otherwise>
</c:choose>
