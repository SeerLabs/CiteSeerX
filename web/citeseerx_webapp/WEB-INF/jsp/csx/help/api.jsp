<%@ include file="../shared/IncludeHeader.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/help/search"/>"><span>Search Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/site"/>"><span><fmt:message key="app.name"/> Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/myciteseer"/>"><span><fmt:message key="app.portal"/> Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/submit"/>"><span>Submissions</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/glossary"/>"><span>Glossary</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/faq"/>"><span>FAQ</span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/help/api"/>"><span>API</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->

  <div id="primary_content">
   <div id="main_content" class="clearfix">
     <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">Getting started with <fmt:message key="app.nameHTML"/> API</em></span></p>
     <p class="para_book">
       <fmt:message key="app.nameHTML"/> API is RESTful web service that provides an interface to access the internal data about the documents in <fmt:message key="app.nameHTML"/> repository. The API will provide access to the authors, papers, and citations information about each of the documents in <fmt:message key="app.nameHTML"/>. The API won't, however, provide access to the extraction and automatic citation algorithms and their implementations. 
     </p>
     <h2 class="topic_heading pushdown">What do you need?</h2>
     <p class="para_book">
       To start using the API you need an application ID. To get an application ID:
     </p>
     <ul class="formating pushdown">
       <li>First, you must have a <fmt:message key="app.portalHTML"/> account, if you don't have one yet, <a href="<c:url value='/mcsutils/newAccount'/>">register</a> one!</li>
       <li>Log into your <fmt:message key="app.portalHTML"/> account and go to <strong>Profile</strong></li>
       <li>Within you profile info you can request your appid by clicking the <strong>Request API Key</strong> link</li>
     </ul>
     <h2 class="topic_heading pushdown">Overview</h2>
     <p class="para_book">URI: <fmt:message key="app.api"/>/api/{object}/[{object_id}]/?appid=ABC[&amp;param1=val1&amp;param2=val2]</p>
     <div class="char_indented"><span class="color1">Start:</span> <span class="color3">The start index, default = 0</span></div>
     <div class="char_indented"><span class="color1">Max:</span> <span class="color3">Max number of returned rows, max 100</span></div>
     <div class="char_indented"><span class="color1">format:</span> <span class="color3">optional, default is xml, other option is json</span></div>
     <div class="char_indented"><span class="color1">appid:</span> <span class="color3">Application ID</span></div>
     <div class="char_indented"><span class="color1">query:</span> <span class="color3">The SQL query to perform on the data</span></div>
     <h2 class="topic_heading pushdown">Papers Queries</h2>
     <br />
     <div class="char_indented"><span class="color1">All Papers:</span> <span class="color3"><a href="<fmt:message key='app.api'/>/api/papers/?appid=YOUR_KEY"><fmt:message key='app.api'/>/api/papers/?appid=YOUR_KEY</a></span></div>
     <div class="char_indented"><span class="color1">100 Papers:</span> <span class="color3"><a href="<fmt:message key='app.api'/>/api/papers/?appid=YOUR_KEY&max=100"><fmt:message key='app.api'/>/api/papers/?appid=YOUR_KEY&amp;max=100</a></span></div>
     <div class="char_indented"><span class="color1">100 Papers in JSON format:</span> <span class="color3"><a href="<fmt:message key='app.api'/>/api/papers/?appid=YOUR_KEY&max=100&format=json"><fmt:message key='app.api'/>/api/papers/?appid=YOUR_KEY&amp;max=100&amp;format=json</a></span></div>
     <div class="char_indented"><span class="color1">Papers about citesser:</span> <span class="color3"><a href="<fmt:message key='app.api'/>/api/papers/?appid=YOUR_KEY&query=SELECT e FROM Paper e where e.title like '%citeseer%'"><fmt:message key='app.api'/>/api/papers/?appid=YOUR_KEY&amp;query=SELECT e FROM Paper e where e.title like '%citeseer%'</a></span></div>
     <h2 class="topic_heading pushdown">Authors Queries</h2>
     <br />
     <div class="char_indented"><span class="color1">All Authors:</span> <span class="color3"><a href="<fmt:message key='app.api'/>/api/authors/?appid=YOUR_KEY"><fmt:message key='app.api'/>/api/authors/?appid=YOUR_KEY</a></span></div>
     <div class="char_indented"><span class="color1">Lee Giles:</span> <span class="color3"><a href="<fmt:message key='app.api'/>/api/authors/?appid=YOUR_KEY&query=SELECT e FROM Author e where e.name like '%lee giles%'"><fmt:message key='app.api'/>/api/authors/?appid=YOUR_KEY&amp;query=SELECT e FROM Author e where e.name like '%lee giles%'</a></span></div>
     <h2 class="topic_heading pushdown">Citation Queries</h2>
     <br />
     <div class="char_indented"><a href="<fmt:message key='app.api'/>/api/citations/?appid=YOUR_KEY"><fmt:message key='app.api'/>/api/citations/?appid=YOUR_KEY</a></div>     
     <h2 class="topic_heading pushdown">Keywords Queries</h2>
     <br />
     <div class="char_indented"><a href="<fmt:message key='app.api'/>/api/keywords/?appid=YOUR_KEY"><fmt:message key='app.api'/>/api/keywords/?appid=YOUR_KEY</a></div>
     <h2 class="topic_heading pushdown">Citation Context Queries</h2>
     <br />
     <div class="char_indented"><a href="<fmt:message key='app.api'/>/api/citationContext/?appid=YOUR_KEY"><fmt:message key='app.api'/>/api/citationContext/?appid=YOUR_KEY</a></div>
     <h2 class="topic_heading pushdown">Cannonical Names</h2>
     <br />
     <div class="char_indented"><a href="<fmt:message key='app.api'/>/api/cannames/?appid=YOUR_KEY"><fmt:message key='app.api'/>/api/cannames/?appid=YOUR_KEY</a></div>
     <h2 class="topic_heading pushdown">Query Limit</h2>
     <p class="para_book">All queries along with the IP address of the sender are logged for both monitoring and research purposes. The current limit on the requests per day is 1000 per user. This limit is enough to retrieve 100,000 entries from our database, please email us if you need this limit to be left over of your account explaining why you need to use the API for more than 1000 requests per day.</p>
     <h2 class="topic_heading pushdown">Warning</h2>
     <p class="para_book">If you are taking a databases or security class and happened to learn about SQL-injections, please don't apply your knowledge on our system. We are running a research project and trying to make our data available to other researchers through this API, so don't take part in hindering research efforts</p>
     <p class="para_book">To be assured, the queries you are submitting to this API are <strong>NOT EXECUTED AGAINST ANY DATABASE</strong>. It's not even SQL queries. It's just a descriptive language that has a similar syntax with SQL and used to query a dataset, not a database. </p>
     <p class="para_book">However, any attempt to hack the system with SQL-injections will lead to banning the IP address of the user and/or the entire institution from where the attack originated. It might also lead to banning access to the system from the entire country where the attack was carried from.</p>
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