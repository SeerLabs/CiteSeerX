<%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
<%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.AuthenticationException" %>
<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<div id="content">
<div id="loginContent">
  <h2>Login</h2>
  <p>Not Registered? <a href="<c:url value='/mcsutils/newAccount'/>">Create an account</a>.</p>
  <c:if test="${not empty param.login_error}">
    <div class="error">
      <p>Your login attempt was not successful.<br>
      <%= ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>. Please try again.</p>
    </div>
  </c:if>
  <div id="loginForm">
    <form method="post" action="<c:url value="/j_spring_security_check"/>" id="loginform" class="labelsRightAligned">
      <div id="userId">
        <label class="title">Username</label>
        <input type="text" id="j_username" name="j_username" class="loginField" <c:if test="${not empty param.login_error}">value="<%= session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY) %>"</c:if> />
      </div>
      <div id="password">
        <label class="title">Password</label>
        <input type="password" id="j_password" name="j_password" class="loginField" />
      </div>
      <div id="login-action">
        <a href="<c:url value='/forgotaccount'/>">Forgot your username or password?</a> <input type="submit" class="button" id="login-button" name="tfa_submitAction" value="Log In" alt="Log In" />
			</div>
    </form>
  </div> <%-- end loginContent div --%>
  </div> <%-- end content div --%>
</div> <%-- end main div --%>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>