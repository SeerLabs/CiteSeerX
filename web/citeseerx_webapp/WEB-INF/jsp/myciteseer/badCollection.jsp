<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<script type="text/javascript">
  $("#collections").addClass('active');
</script>
<div id="content">
  <h2>Collections</h2>
  <div id="left-sidebar">
    <%@ include file="shared/IncludeCollectionsSidebar.jsp" %>
  </div>
  <div id="body">
      <p class="error">No Collection with id: "<c:out value="${cid}"/>"<br/>
        The supplied collection identifier does not match any collection in our repository.</p>
  </div>
</div>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>
