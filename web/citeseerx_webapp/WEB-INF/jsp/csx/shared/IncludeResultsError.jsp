Your search "<c:out value='${query}' />" did not match any 
<c:choose>
	<c:when test="${ param.t == 'auth' && param.uauth == 1}">
		authors.
	</c:when>
  <c:when test="${ param.t == 'table'}">
    tables.
  </c:when>
	<c:otherwise>
		documents.
	</c:otherwise>

</c:choose><br>
Please try a different search.