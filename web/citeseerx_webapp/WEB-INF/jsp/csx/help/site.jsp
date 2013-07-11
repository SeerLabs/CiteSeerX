<%@ include file="../shared/IncludeHeader.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/help/search"/>"><span>Search Help</span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/help/site"/>"><span><fmt:message key="app.name"/> Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/myciteseer"/>"><span><fmt:message key="app.portal"/> Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/submit"/>"><span>Submissions</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/glossary"/>"><span>Glossary</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/faq"/>"><span>FAQ</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/api"/>"><span>API</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->

  <div id="primary_content">
   <div id="main_content" class="clearfix">  
    <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">Getting the Most from <fmt:message key="app.nameHTML"/></em></span></p>
    <h2 class="topic_heading pushdown">To Do...</h2>
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