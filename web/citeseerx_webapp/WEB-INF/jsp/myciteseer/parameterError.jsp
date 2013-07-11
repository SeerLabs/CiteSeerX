<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<script type="text/javascript">
  $("#monitoring").addClass('active');
</script>
<div id="content">
        <div class="error">
          <h1>There was an error interpreting your request</h1>
          <h3><c:out value="${ errMsg }"/></h3>
        </div>
      </div>
      <div class="clear"></div>
      </div>
      <%@ include file="../shared/IncludeFooter.jsp" %>