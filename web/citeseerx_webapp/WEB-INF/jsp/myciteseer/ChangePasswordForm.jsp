<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<script type="text/javascript">
  $("#profile").addClass('active');
</script>

<div id="content">
  <h2>Change Password</h2>
  <div id="id-block" class="accountEdit">
    <form method="post" action="<c:url value="/myciteseer/action/changePassword"/>" id="accountForm" >
    <div id="current_pass" class="formField">
      <spring:bind path="changePasswordForm.suppliedPassword">
        <strong class="title">Current password:</strong>
        <input type="password" size="40"
                 id="<c:out value="${status.expression}"/>" 
                 name="<c:out value="${status.expression}"/>" 
                 value="" 
                 class="textField
                 <c:if test="${!empty status.errorMessage}">errFld</c:if>
                 "
        />
        <span class="error" id="<c:out value="${status.expression}"/>-E">
          <c:out value="${status.errorMessage}"/>
        </span>
      </spring:bind>
    </div>
    <div id="new_pass"class="formField">
      <spring:bind path="changePasswordForm.newPassword">
        <strong class="title">New Password:</strong>
        <input type="password" size="40"
           id="<c:out value="${status.expression}"/>"
           name="<c:out value="${status.expression}"/>" 
           value=""
           class="textField <c:if test="${!empty status.errorMessage}">errFld</c:if>"
        />
        <span class="error" id="<c:out value="${status.expression}"/>-E">
          <c:out value="${status.errorMessage}"/>
        </span>
      </spring:bind>
    </div>
    <div id="new_pass_repeat" class="formField">
      <spring:bind path="changePasswordForm.repeatedPassword">
        <strong class="title">Repeat new password:</strong>
        <input type="password" size="40"
               id="<c:out value="${status.expression}"/>"
               name="<c:out value="${status.expression}"/>" 
               value=""
               class="textField <c:if test="${!empty status.errorMessage}">errFld</c:if>"
        />
        <span class="error" id="<c:out value="${status.expression}"/>-E">
          <c:out value="${status.errorMessage}"/>
        </span>
      </spring:bind>
    </div>
    <input type="submit" class="button" id="submit-" name="submitAction" value="Change Password" style="margin-left: 156px"/>
  </div>
</div>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>
  