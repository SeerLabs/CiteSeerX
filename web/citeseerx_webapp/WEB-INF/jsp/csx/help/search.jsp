<%@ include file="../shared/IncludeTop.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li id="current"><a class="page_tabs remove" href="<c:url value="/help/search"/>"><span>Search Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/site"/>"><span><fmt:message key="app.name"/> Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/myciteseer"/>"><span><fmt:message key="app.portal"/> Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/submit"/>"><span>Submissions</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/glossary"/>"><span>Glossary</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/faq"/>"><span>FAQ</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/api"/>"><span>API</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->

  <div id="primary_content">
   <div id="main_content" class="clearfix">  
	<p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">Searching <fmt:message key="app.nameHTML"/> Effectively</em></span> <img src="<c:url value="/images/searchbox.jpg"/>" alt="search box screen shot" /></p>
    <ul class="formating pushdown">                
     <li>The Documents tab is selected by default. To search for a specific author, select the Author tab.</li> 
     <li>type the topic or name you are searching for into the textbox</li> 
     <li>check the &quot;Include Citations&quot; box (optional)</li>
     <li>and select the &quot;Search&quot; button or hit the &quot;enter&quot; key.</li>
	</ul>
	<h2 class="topic_heading pushdown">Document Searches</h2>
    <p class="para_book">
      <fmt:message key="app.nameHTML"/> uses <a href="http://lucene.apache.org/solr/">Solr</a> as it's core search engine.
      As Solr is based upon <a href="http://lucene.apache.org/">Lucene</a>, a good overview of the supported 
      query syntax may be obtained by reading up on the
      <a href="http://lucene.apache.org/java/docs/queryparsersyntax.html">Lucene query parser syntax</a>.
    </p>
    <p class="para_book">
      <fmt:message key="app.nameHTML"/> supports complex queries based on the following fields:
    </p>

    <div class="char_indented"><span class="color1">title:</span> <span class="color3">paper title</span></div> 
    <div class="char_indented"><span class="color1">author:</span> <span class="color3">paper authors</span></div> 
    <div class="char_indented"><span class="color1">affil:</span> <span class="color3">affiliations of paper authors</span></div> 
    <div class="char_indented"><span class="color1">venue:</span> <span class="color3">place of publication, such as journal or conference name</span></div> 
    <div class="char_indented"><span class="color1">year:</span> <span class="color3">paper year</span></div> 
    <div class="char_indented"><span class="color1">abstract:</span> <span class="color3">paper abstract</span></div> 
    <div class="char_indented"><span class="color1">keyword:</span> <span class="color3">keywords identified from paper headers</span></div> 
    <div class="char_indented"><span class="color1">tag:</span> <span class="color3">user-supplied tags added to a paper</span></div> 
    <div class="char_indented"><span class="color1">text:</span> <span class="color3">article body text</span></div> 

    <p class="para_book">For example,</p>

    <div class="char_indented"><span class="color1">venue:"journal of the acm" AND author:"j kleinberg"</span></div>

    <p class="para_book">
      By default (if no fields are specified) <fmt:message key="app.nameHTML"/> will search the
      <span class="color1">title</span>, <span class="color1">author</span>, <span class="color1">abstract</span>,
      and <span class="color1">text</span> fields in parallel.
    </p>
 
	<h2 class="topic_heading pushdown">Author Searches</h2>
    <p class="para_book"><fmt:message key="app.nameHTML"/> supports Proximity and Boolean queries. Please note that adjacent words will default to one word proximity.</p>
    <div class="char_indented pushdown">For example, an author search using a full name may not return complete results.</div>     
    <div class="char_indented"><span class="color2">NOT GOOD:</span> <span class="color3">jon kleinberg</span></div> 
    <div class="char_indented">This will not return all records authored by Jon Kleinberg. Rather, it will only return records for which the first name "Jon" is included unabbreviated.</div>
    <div class="char_indented pushdown">In author searches, it may be better to use the last name only or the first initial and the last name. For example,</div>
    <div class="char_indented"><span class="color1">GOOD:</span> <span class="color3">kleinberg</span></div>
    <div class="char_indented"><span class="color1">BETTER:</span> <span class="color3">j kleinberg</span></div> 
	<div class="char_indented">Do to author normalizations included within the <fmt:message key="app.nameHTML"/> index, the latter query
	will match authors listed as "J Kleinberg", "Jon Kleinberg", and even "Jon M Kleinberg"</div>
	
	<h2 class="topic_heading pushdown">Citation Searches</h2>
	<p class="para_book">Unlike the old CiteSeer system, citations and full documents are
	  included within the same index in <fmt:message key="app.nameHTML"/>.  By default, citations for which we have no accompanying document file
	  will not show up in search results.  If it is desirable to include these records in search results,
	  simply check the <span class="color1">Include Citations</span> checkbox on the search form.
	</p>

    <h2 class="topic_heading pushdown">Search Plugins</h2>
    <p class="para_book"><fmt:message key="app.nameHTML"/> offers three Search plugins for browsers which supports the <a href="http://www.opensearch.org/" title="Open Search">Open Search</a> 1.1 specification.
      To add a plugin just click over it. 
    </p>
     <div class="char_indented"><a href="<c:url value="/search_plugins/citeseerx_general.xml"/>" class="splugin" id="generalp" title="Add General Search Plugin"><fmt:message key="app.nameHTML"/> general search plugin</a></div>
     <div class="char_indented"><a href="<c:url value="/search_plugins/citeseerx_author.xml"/>" class="splugin" id="authorp" title="Add Author Search Plugin"><fmt:message key="app.nameHTML"/> author search plugin</a></div>
     <div class="char_indented"><a href="<c:url value="/search_plugins/citeseerx_title.xml"/>" class="splugin" id="titlep" title="Add Title Search Plugin"><fmt:message key="app.nameHTML"/> title search plugin</a></div>
    <h2 class="topic_heading pushdown">Capitalization Does Not Matter</h2>
    <p class="para_book"><fmt:message key="app.nameHTML"/> is NOT case sensitive. All letters, regardless of how you type them, will be understood as lower case. For example, searches for  &quot;<span class="color3">bollacker</span>&quot;, &quot;<span class="color3">Bollacker</span>&quot;, and &quot;<span class="color3">bOlLaCkEr</span>&quot; will all return the same results.</p>
   </div> <!-- End main content -->            
  </div> <!-- End primary_content -->
 </div> <!-- End primary_tabs-n-content -->
<script type="text/javascript">
<!--
if (window != top) 
top.location.href = location.href;
function sf(){}

window.addEvent('domready', function(){
 $$('a.splugin').each(function(elem) {
  elem.addEvent('click', function(event) {
  var event = new Event(event);
  addSearchPlugin(elem.id);
  event.stop();
 });});
});
// -->
</script>
<%@ include file="../../shared/IncludeFooter.jsp" %>
