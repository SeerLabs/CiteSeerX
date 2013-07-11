<div id="mcs_search_meta_box">
 <div id="mcs_search_tabs">
  <ul id="mcs_tabs_titles" class="mootabs_title">
   <li title="people_tab" class="active"><div>People</div></li></ul><!-- remove extra whitespace by coding in-line lists on one line -->
  <div id="people_tab" class="mootabs_panel">
   <form method="get" action="<c:url value="/myciteseer/search/peoplesearch"/>" enctype="application/x-www-form-urlencoded" id="people_search_form">
    <!-- name not valid in XHTML, To get around this issue, use <form id="header_search_form" ... > and in my link use javascript:document.forms['header_search_form'].submit(); which validates correctly. -->
    <fieldset class="rightAligned">
     <p><input class="query csx" type="text" name="query" value="<c:out value="${ mcsquery }"/>" size="35" />
        <input class="button csx" type="submit" name="submit" value="Search" alt="Search" /></p>
     <p><a href="<c:url value="/myciteseer/search/MCSAdvancedSearch"/>" title="People Advanced Search.">Advanced Search</a> &#124; <a href="<c:url value="/help/search"/>" title="Get Help">Help</a></p>             
    </fieldset>
   </form>
  </div> <!-- End docs_tab -->
 </div> <!-- End search_tabs -->
</div> <!-- End search_meta_box -->