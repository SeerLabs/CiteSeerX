<%@ include file="../shared/IncludeHeader.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/about/site"/>"><span>About <fmt:message key="app.name"/></span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/about/myciteseer"/>"><span>About <fmt:message key="app.portal"/></span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/team"/>"><span>The Team</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/metadata"/>"><span><fmt:message key="app.name"/> Metadata</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/previous"/>"><span><fmt:message key="app.name"/> Previous Sponsors</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/bot"/>"><span>About <fmt:message key="app.name"/> Crawler</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->
  
  <div id="primary_content">
   <div id="main_content" class="clearfix">
   
   <div id="right-sidebar"> <!-- Contains left content -->
   	<div class="inside"> <!-- to give some room between columns -->
     <div class="content_box">
      <h2>The Vision for MyCiteSeerX</h2>
		<p class="para4 para_book">
For an ACI system like CiteSeer, it becomes increasingly diffcult for users to
find information that accurately matches their needs with the growth of the
number of stored documents. In such scenarios, a user&#39;s query context,
as well as his personal interests, can be taken into consideration in answering
a user&#39;s query and effectively filter the results. To support personalized
services, CiteSeer<sup>x</sup> provides registration mechanism to profile users.
The new logging framework and log schema are user-aware and session-aware,
by which data mining algorithms and recommendation techniques can be applied.
        </p>                                                    	
	 </div><!-- End content_box -->

     </div> <!--End inside -->
   </div><!-- End right-sidebar -->             
  		
   <p class="para4 parafirstletters para_book">
<a class="remove" id="mycsxlogo" href="<c:url value="/myciteseer/action/accountHome"/>" title="MyCiteSeerX Home page"><img src="<c:url value="/images/logos/portallogo.gif"/>" alt="<fmt:message key="app.portal"/> logo" /></a>
is a personal content portal for the CiteSeer<sup>x</sup>, a scientific
literature digital library and search engine that focuses primarily on the
literature in computer and information science. TODO add more content.
   </p>               
   <h2 class="topic_heading">Features</h2> 
   <p class="para4 para_book"> Place your mouse over the orange arrows <img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /> to view the details for each MyCiteSeer<sup>x</sup> feature.
   </p>                 
	<ul class="formating">

	<li class="padded">Personal collections <a class="tooltip" tabindex="1"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span>TODO description</span></a></li>
    <li class="padded">User Awareness RSS-like notifications <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details" /><span>TODO description</span></a></li>  
    <li class="padded">Social bookmarking <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details"  /><span>TODO description</span></a></li>
    <li class="padded">Social network facilities <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details"  /><span>TODO description</span></a></li>
    <li class="padded">Personalized search settings <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details"  /><span>TODO description</span></a></li>

    <li class="padded">Institutional data tracking <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details"  /><span>TODO description</span></a></li>
    <li class="padded">Transparent document submission system <a class="tooltip" tabindex="2"><img class="icon remove" src="<c:url value="/icons/arrwdwnsm.gif"/>" alt="Details"  /><span>TODO description</span></a></li>                              
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
  