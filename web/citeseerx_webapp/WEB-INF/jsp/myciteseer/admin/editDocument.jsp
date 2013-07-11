<%@ include file="../shared/IncludeMyCSXHeader.jsp" %>
<div id="content">
  <h2>Administration</h2>
  <%@ include file="IncludeLeftAdmin.jsp" %>

  <div id="body"> <!-- center column -->
  <c:if test="${ error }"><div><c:out value="${ errMsg }"/></div></c:if>
  <form method="get" 
        action="<c:url value="/myciteseer/action/admin/editDocument"/>" 
        class="wform labelsLeftAligned hintsTooltip">
   <fieldset class="">
    <table class=""><tr><td bgcolor="#DEDEDE">
    Edit Document
    </td></tr></table>
    <table class="datatable">
     <thead>
	<tr>
	<th>
	Title
	</th>
	<th>
	DOI
	</th>
	<th class="centertext">
	Not Accessible<br/>
	<input type="checkbox" id="unpublishAll" name="checkAll"/>
	</th>
	<th class="centertext">
	DMCA<br/>
	<input type="checkbox" id="dmcaAll" name="checkAll"/>
	</th>
       </tr>
	</thead>
	<tbody>
<!-- Header ends here -->
	<c:forEach var="document" items="${docList}">
	<tr>
	<td>
	<c:out value="${ document.documentTitle }"/>
	</td>
	<td>
	<c:out value="${ document.DOI }"/>
	</td>
	<td class="centertext">

	<c:if test="${ document.isPublic }"><input type="checkbox" name="unpublish" value="${ document.DOI }"/></c:if>
	<c:if test="${ !document.isPublic }"><input type="checkbox" name="unpublish" checked="checked" value="${ document.DOI }"/></c:if>

	</td>
	<td class="centertext">
     	<c:if test="${ document.isDMCA }"><input type="checkbox" name="dmcaed" checked="checked" value="${ document.DOI }"/></c:if>
     	<c:if test="${ !document.isDMCA }"><input type="checkbox" name="dmcaed" value="${ document.DOI }"/></c:if>
	</td>
	</tr>
	</c:forEach>
<!-- -->
	</tbody>
	</table>
<!-- table ends -->
    </div>
    <input type="hidden" name="type" value="update"/>
    <div class="actions">
     <input type="submit" class="primaryAction" id="submit-" name="submit" value="submit" />
    </div>
   </fieldset>
  </form>

  </div> <!-- End column-one-content -->
</div>

</div>

<%@ include file="../../shared/IncludeFooter.jsp" %>
