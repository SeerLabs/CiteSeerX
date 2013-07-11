<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
  <div id="content">
    <c:if test="${success == 'true'}">
      <h2>Account Activation Successful</p>
      <br />
      <p>Welcome, <c:out value="${username}"/><br /><br />
          You can now <a href="<c:url value="/myciteseer/action/accountHome"/>">log into your account</a>.
      </p>
    </c:if>
    <c:if test="${success != 'true'}">
      <div class="error">
        <h2>Account Activation Failed</h2>
        <p>Please check the url that you used to access this page.</p>
      </div>
    </c:if>
  </div> <%-- end content div --%>
</div> <%-- end main div --%>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>