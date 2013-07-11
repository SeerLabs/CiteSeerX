<%@ include file="../shared/IncludeHeader.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/about/site"/>"><span>About <fmt:message key="app.name"/></span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/myciteseer"/>"><span>About <fmt:message key="app.portal"/></span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/team"/>"><span>The Team</span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/about/metadata"/>"><span><fmt:message key="app.name"/> Metadata</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/previous"/>"><span><fmt:message key="app.name"/> Previous Sponsors</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/bot"/>"><span>About <fmt:message key="app.name"/> Crawler</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->

  <div id="primary_content">
   <div id="main_content" class="clearfix">  
   
    <div id="right-sidebar"> <!-- Contains left content -->
        <div class="inside"> <!-- to give some room between columns -->
        </div> <!--End inside -->
    </div><!-- End right-sidebar -->             
        
    <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">CiteSeer<sup>x</sup></em></span> 
    is compliant with the <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm" title="OAI-PMH">Open Archives Initiative Protocol for Metadata Harvesting</a>, which is an standard proposed by <a href="http://www.openarchives.org/" title="The Open Archive Initiative"> The Open Archive Initiative</a> in order to facilitate content dissemination. 
    </p>

    <p class="para4 para_book">
    To browse or download records  programmatically from CiteSeer<sup>x</sup> OAI collection please use the harvest url:    
    </p>
    <p class="char_bold" style="text-align:center">
      http://citeseerx.ist.psu.edu/oai2
    </p>
    <p class="para4 para_book">
    The archive may also be browsed from an interface via an <a href="http://re.cs.uct.ac.za/" title="OAI Repository Explorer">OAI Repository Explorer</a>, either by using the CiteSeer<sup>x</sup> archive identifier or by directly entering the harvest url.
    </p>
    <p class="para4 para_book">
    Here is a list of toolkits that can be used for OAI metadata harvesting.
    <ul class="formating">
	<li><a href="http://search.cpan.org/~thb/OAI-Harvester-1.13/" title="OAI Harvester">OAI-Harvester</a> - perl</li>
	<li><a href="http://www.oclc.org/research/software/oai/harvester2.htm" title="OAI Harvester2">OAIHarvester2</a> - Java</li>
	<li><a href="http://sourceforge.net/projects/netoaihvster" title=".NET OAI Harvester">.NET OAI Harvester</a> - .NET (dll)</li>
	<li><a href="http://sourceforge.net/projects/uilib-oai/" title="UIUC OAI">UIUC OAI</a> - UIUC OAI Metadata Harvesting Project.</li>
    </ul>
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
