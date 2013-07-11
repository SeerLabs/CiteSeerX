<%@ include file="shared/IncludeHeader.jsp" %>
  <div id="page_wrapper" class="clearfix"> <!-- Contains all the divisions (div's) within the page (not including top navigation bar, search box and meta information) -->
    <div id="center_content" class="clearfix"> <!-- Contains header div -->
      <div id="primary_content">
        <h1 class="primaryheader">Latest acquired documents by <fmt:message key="app.nameHTML"/></h1>
        <div class="information_bar">
          <c:if test="${ ! empty nextPageParams }">
            <a href="<c:url value="/new?${ nextPageParams }"/>">Next <c:out value="${ nrows }"/> <img class="mini_icon remove" src="<c:url value="/icons/arrwnxt.gif"/>" alt="View Next Page" /></a>  
          </c:if>
        </div> <!-- End information_bar top -->
        <div id="main_content">
          <div class="searchresult">
            <c:if test="${ nfound == 0 }">
              <br/><br/>
              <span class="char_increased">There aren't more documents.</span>
              <br/><br/>
            </c:if>
            <!--LRIS-->
            <c:forEach var="hit" items="${ hits }" varStatus="status">
              <!--RIS-->
              <c:if test="${ hit.inCollection }">
                <div class="blockhighlight_box">
                  <ul class="blockhighlight">
                    <li class="padded">
                      <c:if test="${ ! empty hit.abstract }">
                        <a class="paper-tips" href="#" title="&lt;h3&gt;Abstract&lt;/h3&gt;::<c:out value="${hit.abstract}" escapeXml="true"/>"><img class="icon remove" src="<c:url value="/icons/iconarrwor.gif"/>" alt=""/></a>
                      </c:if>
                      <c:if test="${ empty hit.abstract }">
                        <a class="paper-tips" href="#" title="&lt;h3&gt;Abstract&lt;/h3&gt;::No abstract found"><img class="icon remove" src="<c:url value="/icons/iconarrwor.gif"/>" alt=""/></a>
                      </c:if>
                      <a class="remove doc_details" href="<c:url value="/viewdoc/summary?doi=${ hit.doi }"/>"><em class="title"><c:if test="${ ! empty hit.title }"><c:out value="${ hit.title }" escapeXml="false"/></c:if><c:if test="${ empty hit.title }">unknown title</c:if></em></a>
                      <c:if test="${ ! empty coins[status.index]}">
                        <span class="Z3988" title="<c:out value="${coins[status.index]}" />">&nbsp;</span>
                      </c:if>
                    </li>
                    <li class="author char6 padded">by <c:if test="${ ! empty hit.authors }"><c:out value="${ hit.authors }"/></c:if>
                      <c:if test="${ empty hit.authors }">unknown authors</c:if>
                      <c:if test="${ ! empty hit.year && hit.year > 0 }"> &#8212; <c:out value="${ hit.year }"/></c:if>
                      <c:if test="${ ! empty hit.venue }"> &#8212; <c:out value="${ hit.venue }"/></c:if>
                    </li>
                    <li class="doc_clipping char_increased padded">&#0133;<c:out value="${ hit.snippet }" escapeXml="false"/>&#0133;</li>
                    <li class="char_increased"><c:if test="${ hit.ncites > 0 }"><a class="citation remove" href="<c:url value="/showciting?cid=${ hit.cluster }"/>" title="number of citations">Cited by <c:out value="${ hit.ncites }"/> (<c:out value="${ hit.selfCites }"/> self)</a> &ndash;</c:if>
                      <span class="actionspan" onclick="addToCartProxy(<c:out value="${hit.cluster}"/>)">Add To MetaCart</span> <span id="cmsg_<c:out value="${ hit.cluster }"/>" class="cartmsg"></span>
                    </li>
                  </ul>
                </div> <!-- End blockhighlight_box -->
              </c:if>
              <c:if test="${ ! hit.inCollection }">
                <div class="blockhighlight_box">
                  <ul class="blockhighlight">
                    <li class="padded"><span class="char_decreased">&#91;CITATION&#93;</span>
                      <em class="title"><c:if test="${ ! empty hit.title }"><c:out value="${ hit.title }" escapeXml="false"/></c:if>
                      <c:if test="${ empty hit.title }">unknown title</c:if></em>
                      <c:if test="${ ! empty coins[status.index]}">
                        <span class="Z3988" title="<c:out value="${coins[status.index]}" />">&nbsp;</span>
                      </c:if>
                     </li>
                    <li class="author char6 padded">by <c:if test="${ ! empty hit.authors }"><c:out value="${ hit.authors }"/></c:if>
                      <c:if test="${ empty hit.authors }">unknown authors</c:if>
                      <c:if test="${ ! empty hit.year && hit.year > 0 }"> &#8212; <c:out value="${ hit.year }"/></c:if>
                      <c:if test="${ ! empty hit.venue }"> &#8212; <c:out value="${ hit.venue }"/></c:if>
                    </li>
                    <li class="char_increased"><c:if test="${ hit.ncites > 0 }"><a class="citation remove" href="<c:url value="/showciting?cid=${ hit.cluster }"/>" title="number of citations">Cited by <c:out value="${ hit.ncites }"/> (<c:out value="${ hit.selfCites }"/> self)</a> &ndash;</c:if>
                      <span class="actionspan" onclick="addToCartProxy(<c:out value="${hit.cluster}"/>)">Add To MetaCart</span> <span id="cmsg_<c:out value="${ hit.cluster }"/>" class="cartmsg"></span>
                    </li>
                  </ul>
                </div> <!-- blockhighlight_box -->
              </c:if>
              <!--end RIS-->
            </c:forEach>
            <!--End LRIS -->
          </div> <!-- End searchresult -->
        </div> <!-- End main_content -->
        <div class="information_bar">
          <c:if test="${ ! empty nextPageParams }">
            <a href="<c:url value="/new?${ nextPageParams }"/>">Next <c:out value="${ nrows }"/> <img class="mini_icon remove" src="<c:url value="/icons/arrwnxt.gif"/>" alt="View Next Page" /></a>  
          </c:if>
        </div> <!-- End information_bar bottom -->
      </div> <!-- End primary_content -->
    </div> <!-- End center-content -->
<script type="text/javascript">
<!--
if (window != top) 
top.location.href = location.href;
function sf(){document.forms['<c:if test='${ empty param.t || param.t != "auth" }'>doc_search_form</c:if><c:if test='${ (! empty param.t) && param.t == "auth" }'>auth_search_form</c:if>'].q.focus();}

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
 
window.addEvent('domready', function(){
 /* paperTips */
 var paperTips = new Tips($$('.paper-tips'), {
  maxTitleChars:150,
  className:'paper-tool'
 });
});
// -->
</script>
<%@ include file="../shared/IncludeFooter.jsp" %>