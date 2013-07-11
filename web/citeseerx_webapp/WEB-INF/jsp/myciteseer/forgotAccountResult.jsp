

<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
  <div id="content">
    <div id="loginContent">
      <h2>Forgot your username or password?</h2>
      <br />
      <c:if test="${ error }">
       <h3><c:out value="${ errMsg }"/></h3>
       <h3>You entered "<c:out value="${ email }"/>"</h3>
       <h3><a href="javascript:history.back()">Click here to try again</a></h3>
      </c:if>
      <c:if test="${ ! error }">
       <h3>Your password has been reset and a reminder has been sent to <c:out value="${ email }"/></h3>
      </c:if>
    </div>
  </div> <%-- end content div --%>
  <div class="clear"></div>
</div> <%-- end main div --%>
<%@ include file="../shared/IncludeFooter.jsp" %>