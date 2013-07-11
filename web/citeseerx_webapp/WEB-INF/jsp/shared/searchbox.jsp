<%--
  -- Search box used all around the place
  --
  -- Author: Isaac Councill
  -- Author: Juan Pablo Fernandez Ramirez
  --%>
<div id="search_meta_box">
 <div id="search_tabs">
  <ul id="tabs_titles" class="mootabs_title">
   <li title="docs_tab" class="active"><div>Documents</div></li>
   <li title="auth_tab"><div>Authors</div></li>
   <li title="table_tab" class="yellow">
   <div>Tables <span class="style1" style="color:#FF0000; font-style:italic; font-weight:bold; font:'Times New Roman', Times, serif">!</span></div>
   </li>
   <li title="algorithm_tab" class="yellow">
     <div>Algorithms <span class="style1" style="color:#FF0000; font-style:italic; font-weight:bold; font:'Times New Roman', Times, serif">!</span></div>
   </li>
  </ul><!-- remove extra whitespace by coding in-line lists on one line -->
  
  <div id="docs_tab" class="mootabs_panel">
   <form method="get" action="<c:url value="/search"/>" enctype="application/x-www-form-urlencoded" id="doc_search_form">
    <!-- name not valid in XHTML, To get around this issue, use <form id="header_search_form" ... > and in my link use javascript:document.forms['header_search_form'].submit(); which validates correctly. -->
    <fieldset class="rightAligned">
     <p><input class="query csx" type="text" name="q" value="<c:out value="${ query }"/>" size="50" />
        <input class="button csx" type="submit" name="submit" value="Search" alt="Search" /></p>
        <input type="hidden" name="sort" value="<c:if test='${ empty param.sort }'>rel</c:if><c:if test='${ ! empty param.sort }'><c:out value='${ param.sort }'/></c:if>" />
     <p><input type="checkbox" name="ic" value="1" <c:if test='${ ! empty param.ic }'>checked</c:if> /> Include Citations &#124; <a href="<c:url value="/advanced_search"/>" title="Search full text, title, abstract, date, author name, author affiliation, etc.">Advanced Search</a> &#124; <a href="<c:url value="/help/search"/>" title="Get Help">Help</a></p>             
    </fieldset>
   </form>
  </div> <!-- End docs_tab -->
  
  <div id="auth_tab" class="mootabs_panel">
   <form method="get" action="<c:url value="/search"/>" enctype="application/x-www-form-urlencoded" id="auth_search_form">
    <!-- name not valid in XHTML, To get around this issue, use <form id="header_search_form" ... > and in my link use javascript:document.forms['header_search_form'].submit(); which validates correctly. -->
    <fieldset class="rightAligned">
     <p><input class="query csx" type="text" name="q" value="<c:out value="${ query }"/>" size="50" />
        <input class="button csx" type="submit" name="submit" value="Search" alt="Search" />
        <input type="hidden" name="sort" value="<c:if test='${ empty param.sort }'>cite</c:if><c:if test='${ ! empty param.sort }'><c:out value='${ param.sort }'/></c:if>" />
        <input type="hidden" name="t" value="auth" /></p>
     <p>
	<input type="checkbox" name="uauth" value="1" <c:if test='${ ! empty param.uauth }'>checked</c:if> /> Disambiguated Search &#124;
	<input type="checkbox" name="ic" value="1" <c:if test='${ ! empty param.ic }'>checked</c:if> /> Include Citations &#124; 
	<a href="<c:url value="/advanced_search"/>" title="Search full text, title, abstract, date, author name, author affiliation, etc.">Advanced Search</a> &#124; <a href="<c:url value="/help/search"/>" title="Get Help">Help</a></p>             
    </fieldset>
   </form>
  </div> <!-- End auth_tab -->
  
  <div id="table_tab" class="mootabs_panel">
   <form method="get" action="<c:url value="/search"/>" enctype="application/x-www-form-urlencoded" id="table_search_form">
    <!-- name not valid in XHTML, To get around this issue, use <form id="header_search_form" ... > and in my link use javascript:document.forms['header_search_form'].submit(); which validates correctly. -->
    <fieldset class="rightAligned">
     <p><input class="query csx" type="text" name="q" value="<c:out value="${ query }"/>" size="50" />
        <input class="button csx" type="submit" name="submit" value="Search" alt="Search" />        
        <input type="hidden" name="t" value="table" /></p>
     <p><a href="#" title="Get Help">Help</a></p>             
    </fieldset>
   </form>
  </div> <!-- End table_tab -->
  
  <div id="algorithm_tab" class="mootabs_panel">
   <form method="get" action="<c:url value="/search"/>" enctype="application/x-www-form-urlencoded" id="algorithm_search_form">
    <!-- name not valid in XHTML, To get around this issue, use <form id="header_search_form" ... > and in my link use javascript:document.forms['header_search_form'].submit(); which validates correctly. -->
    <fieldset class="rightAligned">
     <p><input class="query csx" type="text" name="q" value="<c:out value="${ query }"/>" size="50" />
        <input class="button csx" type="submit" name="submit" value="Search" alt="Search" />        
        <input type="hidden" name="t" value="algorithm" /></p>
     <p><a href="#" title="Get Help">Help</a></p>             
    </fieldset>
   </form>
  </div> <!-- End algorithm_tab -->

 </div> <!-- End search_tabs -->
</div> <!-- End search_meta_box -->
<script type="text/javascript">
 <!--
  window.addEvent('domready', function(){
   <c:if test='${ (! empty param.t) && param.t == "auth" }'>
     startSearchTabs('auth_tab');
   </c:if>
   <c:if test='${ (! empty param.t) && param.t == "table" }'>
     startSearchTabs('table_tab');
   </c:if>
   <c:if test='${ (! empty param.t) && param.t == "algorithm" }'>
     startSearchTabs('algorithm_tab');
   </c:if>
   <c:if test='${ empty param.t }'>
     startSearchTabs('docs_tab');
   </c:if>
  });
 // -->
</script>