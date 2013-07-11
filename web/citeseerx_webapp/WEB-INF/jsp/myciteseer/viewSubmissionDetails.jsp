<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent clearfix">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <h2 class="char_headers">Seed URL:&nbsp;<c:out value="${submission.url}"/></h2>
  <h3>Submitted: <c:out value="${submission.time}"/></h3>
  <h3>Status: <c:out value="${ submission.statusDesc }"/> - <c:out value="${ submission.statusTime }"/></h3>
  <div id="tabs_container">
    <ul class="mootabs_title">
      <li title="Documents"><div>Documents Found</div></li>
      <li title="Errors"><div>Errors</div></li>
    </ul>
    <div id="Documents" class="mootabs_panel">
      <div class="panel_content">
       <div class="information_bar char_increased">
        <c:if test="${ ! empty previouspageparamssuc }"><a href="<c:url value="/myciteseer/action/viewSubmissionDetails${ previouspageparams }"/>">&#8592; Previous Page&nbsp;</a></c:if>Documents found: <c:out value="${ssize}"/> Documents.
         Page <c:out value="${spn}" /> of <c:out value="${tps}" />
        <c:if test="${ ! empty nextpageparamssuc }"><a href="<c:url value="/myciteseer/action/viewSubmissionDetails${ nextpageparamssuc }"/>">Next Page &#8594;</a></c:if>
       </div> <!-- End Information Bar -->
       <div class="searchresult">
        <div class="padded">&nbsp;</div>
        <c:if test="${!empty successes}">
          <c:forEach var="component" items="${successes}">
           <div class="blockhighlight_box"> <!-- List Item -->
            <ul class="blockhighlight">
             <li class="author char6 padded"><c:out value="${component.URL}"/></li>
             <li class="padded">DOI: <a class="remove doc_details" href="<c:url value="/viewdoc/summary?doi=${component.DID}"/>"><c:out value="${component.DID}"/></a></li>
             <li class="author char6 padded">Date:<c:out value="${component.date}"/></li>
            </ul>
           </div> <!-- End List Item -->
          </c:forEach>
        </c:if>
        <c:if test="${empty successes}">
          <span class="char_increased">No documents have been added from this submission.</span>
        </c:if>
       </div> <!-- End Search Results -->
       <div class="information_bar char_increased">
        <c:if test="${ ! empty previouspageparamssuc }"><a href="<c:url value="/myciteseer/action/viewSubmissionDetails${ previouspageparamssuc }"/>">&#8592; Previous Page&nbsp;</a></c:if>Documents found: <c:out value="${ssize}"/> Documents.
         Page <c:out value="${spn}" /> of <c:out value="${tps}" />
        <c:if test="${ ! empty nextpageparamssuc }"><a href="<c:url value="/myciteseer/action/viewSubmissionDetails${ nextpageparamssuc }"/>">Next Page &#8594;</a></c:if>
       </div> <!-- End Information Bar -->
      </div> <!-- End content -->
    </div> <!-- End Documents - mootabs_panel -->
    <div id="Errors" class="mootabs_panel">
      <div class="panel_content">
       <div class="information_bar char_increased">
        <c:if test="${ ! empty previouspageparamsfail }"><a href="<c:url value="/myciteseer/action/viewSubmissionDetails${ previouspageparamsfail }"/>">&#8592; Previous Page&nbsp;</a></c:if>Errors found: <c:out value="${fsize}"/> Errors.
         Page <c:out value="${fpn}" /> of <c:out value="${tpf}" />
        <c:if test="${ ! empty nextpageparamsfail }"><a href="<c:url value="/myciteseer/action/viewSubmissionDetails${ nextpageparamsfail }"/>">Next Page &#8594;</a></c:if>
       </div> <!-- End Information Bar -->
       <div class="searchresult">
        <div class="padded">&nbsp;</div>
          <c:if test="${!empty failures}">
            <c:forEach var="component" items="${failures}">
             <div class="blockhighlight_box"> <!-- List Item -->
              <ul class="blockhighlight">
               <li class="char_increased padded"><c:out value="${component.URL}"/></li>
               <li class="author char6 padded">Status:&nbsp;<c:out value="${component.statusDesc}"/></li>
               <li class="author char6 padded">Date:&nbsp;<c:out value="${component.date}"/></li>
              </ul>
             </div> <!-- End List Item -->
            </c:forEach>
          </c:if>
          <c:if test="${empty failures}">
            <p>There are no errors.</p>
          </c:if>
       </div> <!-- End result list -->
       <div class="information_bar char_increased">
        <c:if test="${ ! empty previouspageparamsfail }"><a href="<c:url value="/myciteseer/action/viewSubmissionDetails${ previouspageparamsfail }"/>">&#8592; Previous Page&nbsp;</a></c:if>Errors found: <c:out value="${fsize}"/> Errors.
         Page <c:out value="${fpn}" /> of <c:out value="${tpf}" />
        <c:if test="${ ! empty nextpageparamsfail }"><a href="<c:url value="/myciteseer/action/viewSubmissionDetails${ nextpageparamsfail }"/>">Next Page &#8594;</a></c:if>
       </div> <!-- End Information Bar -->
      </div> <!-- End content -->
    </div> <!-- End Errors - mootabs_panel -->
  </div> <!-- End tabs_container -->
</div> <!-- End column-one-content -->
 </div> <!-- End column-one (center column) -->
 <div class="column-two-sec"> <!-- Left column -->
   <%@ include file="IncludeLeftSubmissions.jsp" %>
 </div> <!-- End column-two (Left column) -->
 <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
</div><!-- End columns-float -->
 <div class="column-three-sec"> <!-- right column -->
  <div class="column-three-content"></div>
 </div> <!-- End column-three -->
 <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
 <div class="nn4clear">&nbsp;</div><!-- # needed for NN4 to clear all columns || not needed by any other browser -->
</div> <!-- End mypagecontent -->

<script type="text/javascript">
<!--
if (window != top) 
 top.location.href = location.href;
function sf(){}
function sa(){
 var elt = document.getElementById("submissions_tab");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");

 elt = document.getElementById("view_submissions");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}

window.addEvent('domready', function(){
    /* collection_tabs */ 
    var collectionTabs = new mootabs('tabs_container', {
        height:             'auto',
        width:              '98%',
        changeTransition:   'none',
        mouseOverClass:     'over'
    });
    
    collectionTabs.activate('<c:out value="${tab}"/>');
    
 $$('div.blockhighlight_box').each(function(elem) {
  // add offblock class to each collection item
  elem.addClass('offblock');
 
  // Add mouse over and out events to each collection item.
  elem.addEvent('mouseover', function(event) {
   var event = new Event(event);
   event.stop();
   elem.removeClass('offblock');
   elem.addClass('overblock');
  });
 
 elem.addEvent('mouseout', function(event) {
   var event = new Event(event);
   event.stop();
   elem.removeClass('overblock');
   elem.addClass('offblock');
  });
 });
    
});

// -->
</script>
<%@ include file="../shared/IncludeFooter.jsp" %>