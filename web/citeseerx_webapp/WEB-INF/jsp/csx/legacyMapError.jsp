<%@ include file="shared/IncludeHeader.jsp" %>
  <div id="center_content" class="clearfix"> <!-- Contains header div -->
    <div id="primary_content">
      <div id="main_content">
        <div class="inside pushdown">
          <div class="error">
            <h1>Error mapping legacy id <c:out value="${ did }"/></h1>
            <h3><c:out value="${ errMsg }"/></h3>
          </div>
        </div>
      </div> <!-- End main_content -->
    </div> <!-- End primary_content -->
  </div> <!-- End center-content -->
<script type="text/javascript">
<!--
if (window != top) 
top.location.href = location.href;
function sf(){}
// -->
</script>
<%@ include file="../shared/IncludeFooter.jsp" %>