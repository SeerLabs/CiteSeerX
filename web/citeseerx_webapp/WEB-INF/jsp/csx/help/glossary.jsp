<%@ include file="../shared/IncludeHeader.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/help/search"/>"><span>Search Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/site"/>"><span><fmt:message key="app.name"/> Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/myciteseer"/>"><span><fmt:message key="app.portal"/> Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/submit"/>"><span>Submissions</span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/help/glossary"/>"><span>Glossary</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/faq"/>"><span>FAQ</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/api"/>"><span>API</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->

  <div id="primary_content">
   <div id="main_content" class="clearfix">  
    <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter"><fmt:message key="app.nameHTML"/> Technical Terms</em></span></p>
    <br /><br />             
	 <ul class="formating nofrills_list">                
	  <li><a>Abstract</a></li>
	  <li><a>ACM</a></li>
      <li><a>Active Bibliography</a></li>
	  <li><a>All</a></li>
	  <li><a>Article</a></li>
	  <li><a>Author</a></li>
	  <li><a>Authority</a></li>
	  <li><a>BibTeX Entry</a></li>
	  <li><a>Bibliography</a></li>
	  <li><a>Body</a></li>
	  <li><a>Boolean Query</a></li>  
	  <li><a>CCIDF</a></li>
	  <li><a>Citation</a></li>
	  <li><a>Citation Search</a></li>
	  <li><a>CiteSeer</a></li>
	  <li><a>Co-Citation</a></li>
	  <li><a>Context</a></li>
	  <li><a>Context Page</a></li>
	  <li><a>CSB</a></li>
	  <li><a>DBLP</a></li>

		<li><a>Document</a></li>
		<li><a>Document Link</a></li>
		<li><a>Document Page</a></li>
		<li><a>Document Search</a></li>
		<li><a>Download</a></li>
		<li><a>Duplicate</a></li>

		<li><a>Header</a></li>
		<li><a>Hits</a></li>
		<li><a>Hub</a></li>  
		<li><a>Image</a></li>
		<li><a>Index</a></li>   
		<li><a>More/All</a></li>    
		<li><a>Paper</a></li>

		<li><a>PDF</a></li>
		<li><a>Proximity Query</a></li>
		<li><a>PS</a></li>
		<li><a>PS.gz</a></li>
		<li><a>Publication</a></li>   
		<li><a>Related Articles</a></li>

		<li><a>Relevance Query</a></li>
		<li><a>Self Citation</a></li>
		<li><a>Similarity</a></li>
		<li><a>Similarity @ Sentence Level</a></li>
		<li><a>Similarity @ Text Level</a></li>
		<li><a>Summary</a></li>

		<li><a>TFIDF</a></li>
		<li><a>Title</a></li>
		<li><a>TOC</a></li>  
		<li><a>Unknown</a></li>
		<li><a>Usage</a></li>  
		<li><a>View</a></li>  
		<li><a>Within w/n</a></li>  
	</ul> 

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
