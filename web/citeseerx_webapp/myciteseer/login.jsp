<%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
<%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.AuthenticationException" %>
<%@ include file="/WEB-INF/jsp/myciteseer/IncludeTop.jsp" %>
<!-- <link rel="stylesheet" href="<c:url value="/css/login.css"/>" type="text/css" /> -->

<div class="mypagecontent"> <!-- contains left and center content -->
 <div id="login_content"> <!-- Main content -->
  <div> <!-- contains left and right columns and login information -->
   <c:if test="${not empty param.login_error}">
    <div id="login_error">
     <p>Your login attempt was not successful, try again.</p>
     <p>Reason: <%= ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %></p>
    </div>
   </c:if>
   <form method="post" action="<c:url value="/j_spring_security_check"/>" id="loginform" class="labelsRightAligned">
	<fieldset id="mylogin" class="noborder">
    <!-- <legend>Log In</legend> -->                            
     <div class="left-column_content "> <!-- Contains content in left col -->
      <div> <!-- contains left content -->
	   <div class="oneField">
        <label for="j_username" class="preField">User ID:</label> <input type="text" id="j_username" name="j_username" size="" class="" <c:if test="${not empty param.login_error}">value="<%= session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY) %>"</c:if> />
        <span class="errMsg" id="j_username-E"></span>
       </div> <!-- end .oneField -->                            
	   <div class="oneField">
        <label for="j_password" class="preField">Password:</label> <input type="password" id="j_password" name="j_password" value="" size="" class="" />
        <span class="errMsg" id="j_password-E"></span>
       </div> <!-- end .oneField -->
       <!-- Uncomment for captcha support
       <div class="oneField">
        <label for="j_captcha_response" class="preField">Enter the text of this distorted image:</label> <input type="text" id="j_captcha_response" name="j_captcha_response" value="" size="" class="" />
        <span class="errMsg" id="j_captcha_response-E"></span>
       </div>
       -->
      </div> <!-- End contains left content -->
     </div> <!-- End left-column_content -->     
     <div class="right-column_content">
      <!-- Uncomment for captcha support
      <img id="captcha" src="<c:url value="/captcha.jpg"/>" alt="captcha" />
      -->
	 </div> <!-- End right-column_content -->
    </fieldset> 
    <div class="actions para2">
     <input type="submit" class="primaryAction button" id="submit-" name="tfa_submitAction" value="Log In" alt="Log In" />
     <p class="char_increased">Not Registered?  <a href="<c:url value='/mcsutils/newAccount'/>">Create an account</a></p>
     <p><a href="<c:url value='/forgotaccount'/>">Forgot your username or password?</a></p>
  	</div> <!-- End actions -->                 
   </form>                                         
  </div> <!-- contains left and right columns and login information -->
 </div> <!-- End contains login content -->
</div> <!-- End mypagecontent -->
</div> <!-- End center_content -->
<script type="text/javascript">
<!--
if (window != top) 
 top.location.href = location.href;
function sf() {
 var elt = document.getElementById("j_username");
 elt.focus();
}
function sa(){
 var elt = document.getElementById("profile_tab");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}
// -->
</script>

<%@ include file="/WEB-INF/jsp/myciteseer/IncludeBottom.jsp" %>