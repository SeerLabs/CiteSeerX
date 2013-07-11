<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
  <div id="content">
    <div id="loginContent">
      <h2>Forgot your username or password?</h2>
      <br />
      <div style="text-align: left;">
        <p>If you have forgotten your username or password, you may have your password reset here.  By entering your email address in the text area below and clicking "Reset", a new password will be generated for you and your login details will be sent to the email address you enter.</p>
        <p><strong>NOTE:</strong> You must enter the email address
          that you specified for the account upon registering.</p>
      </div>
      <form method="post" action="<c:url value="/forgotaccount"/>" >
        <strong class="title">Email:</strong>
        <input type="text" id="email" name="email" class="textField" size="48"/><input type="submit" class="button" class="button" id="submit" name="tfa_submitAction" value="Reset" alt="Reset Password" />
      </form>
    </div>
  </div> <%-- end content div --%>
  <div class="clear"></div>
</div> <%-- end main div --%>
<%@ include file="../shared/IncludeFooter.jsp" %>