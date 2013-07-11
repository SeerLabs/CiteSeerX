<%@ include file="IncludeTop.jsp" %>
   <div class="mypagecontent clearfix">
    <div class="columns-float-sec">
     <div class="column-one-sec"> <!-- center column -->
      <div class="column-one-content clearfix">
       <div id="tabs_container">
        <ul class="mootabs_title">
         <li title="People_AdvSearch"><div>People Advanced Search</div></li>
        </ul>
        <div id="People_AdvSearch" class="mootabs_panel">
         <div class="panel_content">
          <%@ include file="search/peopleSearchForm.jsp" %>
         </div> <!-- End panel_content -->
        </div> <!-- End People_AdvSearch - mootabs_panel -->
       </div> <!-- End tabs_container -->
      </div> <!-- end column-one-content -->
     </div> <!-- end column-one-sec (center column) -->
     <div class="column-two"> <!-- Left column -->

     </div> <!-- End column-two (Left column) -->
     <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
    </div> <!-- end columns-float-sec -->
    <div class="column-three-sec"> <!-- right column -->
     <div class="column-three-content"></div>
    </div> <!-- End column-three-sec -->
    <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
    <div class="nn4clear">&nbsp;</div><!-- # needed for NN4 to clear all columns || not needed by any other browser -->
   </div> <!-- end mypagecontent -->
<script type="text/javascript">
  <!--
    if (window != top) 
      top.location.href = location.href;
      function sf(){document.forms['<c:if test='${ empty param.t || param.t != "auth" }'>doc_search_form</c:if><c:if test='${ (! empty param.t) && param.t == "auth" }'>auth_search_form</c:if>'].q.focus();}

    function sf(){}
    function sa(){
     var elt = document.getElementById("search_tab");
     elt.setAttribute("class", "active");
     elt.setAttribute("className", "active");
    }
    
    window.addEvent('domready', function(){
     /* search tabs */
     var searchTabs = new mootabs('tabs_container', {
      width:'100%',
      changeTransition: 'none',
      mouseOverClass:'over'
     });
    });
  // -->
</script>
<%@ include file="../shared/IncludeFooter.jsp" %>