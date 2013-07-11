<%@ page import="org.acegisecurity.ui.AbstractProcessingFilter" %>
<%@ page import="org.acegisecurity.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.acegisecurity.AuthenticationException" %>
<%@ include file="/WEB-INF/jsp/myciteseer/IncludeTop.jsp" %>
<!--<link rel="stylesheet" href="<c:url value="/css/login.css"/>" type="text/css" /> -->

<div id="main-body" class="clearfix"> <!-- contains left and center content -->
  <div id="center-content"> <!-- Main content -->
    <div class="inside"> <!-- to give some room between columns -->
    <div class="content_box">
      <h1>MyCiteSeer Login</h1>
      <%-- this form-login-page form is also used as the 
         form-error-page to ask for a login again.
         --%>
      <c:if test="${not empty param.login_error}">
        <div id="login_error">
          <p>Your login attempt was not successful, try again.</p>
          <p>
            Reason: <%= ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.ACEGI_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>
          </p>
        </div>
      </c:if>
      <form method="post" action="<c:url value="/j_acegi_security_check"/>" 
            id="loginform" class="labelsRightAligned">
        <fieldset id="login" class="">
          <div style="float:left;">
            <div class="oneField">
              <label for="j_username" class="preField">User ID:</label>
              <input type="text" id="j_username" name="j_username"
                     size="" class="" 
                <c:if test="${not empty param.login_error}">
                  value="<%= session.getAttribute(AuthenticationProcessingFilter.ACEGI_SECURITY_LAST_USERNAME_KEY) %>"
                </c:if> 
              /><br />
              <span class="errMsg" id="j_username-E"></span>
            </div>
            <div class="oneField">
              <label for="j_password" class="preField">Password:</label>
              <input type="password" id="j_password" name="j_password" 
                     value="" size="" class="" /><br />
              <span class="errMsg" id="j_password-E"></span>
            </div>
            <div class="oneField">
              <label for="j_captcha_response" class="preField">
                &nbsp;
              </label>
              <input type="text" id="j_captcha_response" 
                     name="j_captcha_response" value="" size="" class="" /><br />
              <div class="field-hint-inactive" id="j_captcha_response-H"><span>Please enter the text that appears in the image</span></div>
              <span class="errMsg" id="j_captcha_response-E"></span>
            </div>
          </div>
          <div style="float:left;">
            <div class="inside">
              <img src="<c:url value="/mcsutils/captcha-image.jpg"/>" style="border:1px solid black" alt="" />
            </div>
          </div>
        </fieldset>
        <div class="actions">
          <input type="submit" class="primaryAction" id="submit-" name="tfa_submitAction" value="Log In" />
          <p>
            Not Registered?  <a href="<c:url value='/mcsutils/newAccount'/>">Create an account</a>
          </p>
        </div>
      </form>
    </div>
    </div> <!-- end of inside -->
  </div> <!-- end of center-content -->
  <div id="left-sidebar"> <!-- contains left content -->
    <div class="inside"> <!-- to give some room between columns -->
    </div> <!-- end of inside -->
  </div> <!-- end of left-sidebar -->
</div> <!-- end of main-body -->
<div id="right-sidebar"> <!-- contains right content -->
  <div class="inside"> <!-- to give some room between columns -->
  </div> <!-- end of inside -->
</div> <!-- end of right-sidebar -->
<script type="text/javascript">
<![CDATA[
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
]]>
</script>

<%@ include file="/WEB-INF/jsp/myciteseer/IncludeBottom.jsp" %>