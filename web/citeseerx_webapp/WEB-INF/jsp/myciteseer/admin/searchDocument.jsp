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
    <legend>Find Document</legend>
    <div class="oneField">
     <label class="preField">Enter Document IDs (DOI):</label>
     <textarea rows="2" cols="18" name="listdois">
	<c:out value="${status.value}"/>
     </textarea>
    </div>
    <div class="actions">
     <input type="submit" class="primaryAction" id="submit-" name="submit" value="submit" />
    </div>
   </fieldset>
  </form>

  </div> <!-- End column-one-content -->
</div>

</div>

<%@ include file="../../shared/IncludeFooter.jsp" %>
