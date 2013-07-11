<%@ include file="../shared/IncludeHeader.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/about/site"/>"><span>About <fmt:message key="app.name"/></span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/myciteseer"/>"><span>About <fmt:message key="app.portal"/></span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/team"/>"><span>The Team</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/metadata"/>"><span><fmt:message key="app.name"/> Metadata</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/previous"/>"><span><fmt:message key="app.name"/> Previous Sponsors</span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/about/bot"/>"><span>About <fmt:message key="app.name"/> Crawler</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->
  
  <div id="primary_content">
 <div id="main_content" class="clearfix">
   <span class="firstletters"><em class="firstletter">citeseerxbot.</em></span>
is the web agent or spider used by the the <fmt:message key="app.nameHTML"/> Digital Library to crawl the web.
It is important to note that citeseerxbot is a <a class="remove" href="http://en.wikipedia.org/wiki/Focused_crawler">Focused Crawler</a>.
<p class="para_book">
If citeseerxbot is crawling a site, it is either from a crawl list or from a submitted URL crawl request.
</p>
<p class="para_book">
Since citeseerxbot obeys the <a class="remove" href="http://www.robotstxt.org" title="robots.txt">robots.txt</a> exclusion protocol, 
citeseerxbot can be prohibited from crawling a site by adding the appropriate entry to the robots.txt file.
</p>
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
