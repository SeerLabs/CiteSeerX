<div id="pager">
<c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/search?${ nextpageparams }"/>">Next <c:out value="${ nrows }"/> &#8594;</a></c:if>
</div>