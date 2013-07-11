<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent clearfix">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <h2 class="char_headers">User Details</h2>
  <div id="primary_content">
    <!--<ul class="blockhighlight">
      <li class="padded char_increased">User ID: <span class="padded"><c:out value="${profile.username}"/></span></li>
      <li class="padded char_increased">Name: <c:out value="${profile.firstName}"/>&nbsp;<c:if test="${! empty profile.middleName}"><c:out value="${profile.middleName}"/>&nbsp;</c:if><c:out value="${profile.lastName}"/></li>
      <c:if test="${! empty profile.webPage}"><li class="padded char_increased">Web Page: <a href="<c:out value="${profile.webPage}"/>"><c:out value="${profile.webPage}"/></a></li></c:if>
      <li class="padded char_increased">Organization: <c:out value="${profile.affiliation1}"/>
      <c:if test="${! empty profile.affiliation2}"><li class="padded char_increased">Department: <c:out value="${profile.affiliation2}"/></li></c:if>
      <li class="padded char_increased">Country: <c:out value="${profile.country}"/>
      <c:if test="${! empty profile.province}"><li class="padded char_increased">Province: <c:out value="${profile.province}"/></li></c:if>
    </ul>
    -->
    <table class="datatable char_increased">
      <tbody>
        <tr class="even"><td>User ID:</td><td><c:out value="${profile.username}"/></td></tr>
        <tr class="odd"><td>Name:</td><td><c:out value="${profile.firstName}"/>&nbsp;<c:if test="${! empty profile.middleName}"><c:out value="${profile.middleName}"/>&nbsp;</c:if><c:out value="${profile.lastName}"/></td></tr>
        <c:if test="${! empty profile.webPage}"><tr class="even"><td>Web Page:</td><td><a href="<c:out value="${profile.webPage}"/>"><c:out value="${profile.webPage}"/></a></td></tr></c:if>
        <tr class="odd"><td>Organization:</td><td><c:out value="${profile.affiliation1}"/></td></tr>
        <c:if test="${! empty profile.affiliation2}"><tr class="even"><td>Department:</td><td><c:out value="${profile.affiliation2}"/></td></tr></c:if>
        <tr class="odd"><td>Country:</td><td><c:out value="${profile.country}"/></td></tr>
        <c:if test="${! empty profile.province}"><tr class="even"><td>Province:</td><td><c:out value="${profile.province}"/></td></tr></c:if>
      </tbody>
    </table>
  </div>
 </div> <!-- End column-one-content -->
</div> <!-- End column-one (center column) -->
 <div class="column-two-sec"> <!-- Left column -->
   
 </div> <!-- End column-two (Left column) -->
 <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
</div><!-- End columns-float -->
 <div class="column-three-sec"> <!-- right column -->
  <div class="column-three-content"></div>
 </div> <!-- End column-three -->
 <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
 <div class="nn4clear">&nbsp;</div><!-- # needed for NN4 to clear all columns || not needed by any other browser -->
</div> <!-- End mypagecontent -->
<%@ include file="../shared/IncludeFooter.jsp" %>