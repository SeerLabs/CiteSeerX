<%@ include file="../shared/IncludeHeader.jsp" %>
<div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/about/site"/>"><span>About <fmt:message key="app.name"/></span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/myciteseer"/>"><span>About <fmt:message key="app.portal"/></span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/team"/>"><span>The Team</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/metadata"/>"><span><fmt:message key="app.name"/> Metadata</span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/about/previous"/>"><span><fmt:message key="app.name"/> Previous Sponsors</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/bot"/>"><span>About <fmt:message key="app.name"/> Crawler</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div>
  <div id="primary_content">
    <div id="main_content" class="clearfix">
      <div class="imageinbox">
        <a class="remove" href="http://research.microsoft.com/" title="Sponsored by Microsoft Research">
        <img id="msr" src="<c:url value="/images/sponsors/MSR_logo.gif"/>" alt="Microsoft Research logo" /></a>
      </div> <!-- End imageinbox -->
      <div class="imageinbox">
        <a class="remove" href="http://www.nasa.gov/home/" title="Sponsored by NASA">
        <img id="nasa" src="<c:url value="/images/sponsors/nasa.gif"/>" alt="NASA logo" /></a>
      </div> <!-- End imageinbox -->
    </div> <!-- End main content -->            
  </div> <!-- End primary_content -->
</div> <!-- End primary_tabs-n-content -->
<script type="text/javascript">
<!--
if (window != top) 
top.location.href = location.href;
function sf(){}
// -->
</script>
<%@ include file="../../shared/IncludeFooter.jsp" %>