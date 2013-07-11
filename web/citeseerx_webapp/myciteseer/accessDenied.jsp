<%@ include file="/WEB-INF/jsp/myciteseer/IncludeTop.jsp" %>
<%@ page import="org.springframework.security.ui.AccessDeniedHandlerImpl" %>

<!-- <link rel="stylesheet" href="<c:url value="/css/login.css"/>" type="text/css" /> -->

<div class="mypagecontent"> <!-- contains left and center content -->
 <div class="columns-float-sec">
  <div class="column-one-sec"> <!-- center column -->
   <div class="column-one-content clearfix">
     <h1>Sorry, access is denied</h1>
     <p>
      <%= request.getAttribute(AccessDeniedHandlerImpl.SPRING_SECURITY_ACCESS_DENIED_EXCEPTION_KEY)%>
     </p>
   </div> <!-- End column-one-content -->
  </div> <!-- End column-one-sec (center column) -->
  <div class="column-two-sec"> <!-- Left column -->
   
  </div> <!-- End column-two (Left column) -->
  <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
 </div><!-- End columns-float-sec -->
 <div class="column-three-sec"> <!-- right column -->
  <div class="column-three-content"></div>
 </div> <!-- End column-three -->
 <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
 <div class="nn4clear">&nbsp;</div><!-- # needed for NN4 to clear all columns || not needed by any other browser -->
</div> <!-- End mypagecontent -->
<%@ include file="/WEB-INF/jsp/myciteseer/IncludeBottom.jsp" %>
