<%@ include file="IncludeTop.jsp" %>
   <div class="mypagecontent clearfix">
    <div class="columns-float-sec">
     <div class="column-one-sec"> <!-- center column -->
      <div class="column-one-content clearfix">
       <h1 class="primaryheader">Searching for <span class="char3"><c:out value="${mcsquery}"/></span></h1>
        <div class="information_bar char_increased"><a class="remove" href="<c:url value="/help/search"/>"><img class="icon" src="<c:url value="/images/iconqust.gif"/>" alt="Help&#33;"/></a>
          <c:out value="${ nfound }"/> people found<c:if test="${ nfound > 0 }">, showing <c:out value="${ start+1 }"/> through <c:if test="${ (start+nrows) <= nfound }"><c:out value="${ start+nrows }"/></c:if><c:if test="${ (start+nrows) > nfound }"><c:out value="${ nfound }"/></c:if>.</c:if>
          <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/searchPeople?${ nextpageparams }"/>">Next <c:out value="${ nrows }"/> &#8594;</a></c:if>
        </div> <!-- End information_bar top -->
        <div id="main_content">
          <div class="searchresult">
            <c:if test="${ nfound == 0 && !error }">
              <br/><br/>
              <span class="char_increased">Your search &ndash; <span class="char_emphasized"><c:out value="${ mcsquery }"/></span> &ndash; did not match any people.</span>
              <br/><br/>
            </c:if>
            <c:if test="${ error }">
              <br/><br/>
              <span class="char_increased"><c:out value="${ errorMsg }" escapeXml="false"/></span>
              <br/><br/>
            </c:if>
            <!--LRIS-->
            <c:forEach var="hit" items="${ hits }">
              <!--RIS--> 
              <div class="blockhighlight_box">
                <ul class="blockhighlight">
                  <li class="padded">
                    <a class="tooltip" href="#"><img class="icon remove" src="<c:url value="/icons/iconarrwor.gif"/>" alt=""/><span><c:out value="First Name: ${hit.firstName}" escapeXml="false"/><br /><c:out value="Province: ${hit.province}" escapeXml="false"/><br /></span></a>
                    <a href="<c:url value="/myciteseer/action/viewUserProfile?userid=${hit.username}" />"><em class="title"><c:out value="${hit.firstName}" escapeXml="false"/><c:if test="${! empty hit.middleName}">&nbsp;<c:out value="${hit.middleName}" escapeXml="false"/></c:if>&nbsp;<c:out value="${hit.lastName}" escapeXml="false"/></em></a>
                  </li>
                  <li class="doc_clipping char_increased padded">Affiliation:&nbsp;<c:out value="${hit.affiliation1}" escapeXml="false"/>&nbsp;&ndash;&nbsp;<c:out value="${hit.affiliation2}" escapeXml="false"/></li>
                  <li class="doc_clipping char_increased padded"><c:out value="Country: ${hit.country}" escapeXml="false"/></li>
                </ul>
              </div> <!-- End blockhighlight_box -->
              <!--ERIS-->
            </c:forEach>
            <!--ELRIS -->
          </div> <!-- End searchresult -->
        </div> <!-- End main_content -->
        <div class="information_bar char_increased"><a class="remove" href="<c:url value="/help/search"/>"><img class="icon" src="<c:url value="/images/iconqust.gif"/>" alt="Help&#33;"/></a>
          <c:out value="${ nfound }"/> people found<c:if test="${ nfound > 0 }">, showing <c:out value="${ start+1 }"/> through <c:if test="${ (start+nrows) <= nfound }"><c:out value="${ start+nrows }"/></c:if><c:if test="${ (start+nrows) > nfound }"><c:out value="${ nfound }"/></c:if>.</c:if>
          <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/searchPeople?${ nextpageparams }"/>">Next <c:out value="${ nrows }"/> &#8594;</a></c:if>
        </div> <!-- End information_bar bottom -->
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
      
    $$('div.blockhighlight_box').each(function(elem) {
      // add offblock class to each search result
      elem.addClass('offblock');
 
      // Add mouse over and out events to search results.
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
  // -->
</script>
<%@ include file="../shared/IncludeFooter.jsp" %>