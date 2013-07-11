<%@ include file="shared/IncludeMyCSXHeader.jsp" %>

<script type="text/javascript" src="<c:url value="/js/country.js"/>"></script>

<script type="text/javascript">
<!--

$("#profile").addClass('active');

DWREngine.setErrorHandler(null);
DWREngine.setWarningHandler(null);

function validateAccountInputField(element) {
  var value;
  if (element.type == 'text')
    value = element.value;
  else if (element.type == 'select-one')
    value = element.options[element.selectedIndex].value;
}

function setInputFieldStatus(elementId, message) {
  document.getElementById("" + elementId + "-E").innerHTML = message;
}
// -->
</script>
<div id="content">
<c:if test="${accountForm.newAccount}">
	<form id="accountForm" method="post" class="wform strongsRightAligned hintsTooltip" action="<c:url value="/mcsutils/newAccount"/>">
</c:if>
<c:if test="${!accountForm.newAccount}">
  <form id="accountForm" method="post" class="wform strongsRightAligned hintsTooltip" action="<c:url value="/myciteseer/action/editAccount"/>">
</c:if>
<c:if test="${accountForm.newAccount}">
  <h2>Account Registration</h2>
</c:if>
<c:if test="${!accountForm.newAccount}">
  <h2>Account Information</h2>
</c:if>

<div id="id-block" class="accountEdit">
	<div id="username" class="formField">
	<c:if test="${accountForm.newAccount}">
    <spring:bind path="accountForm.account.username">
      <strong class="title">Username <span class="required">*</span></strong>
      <input type="text" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" class="textField required<c:if test="${!empty status.errorMessage}"> errFld</c:if>" />
      <span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
    </spring:bind>
  </c:if>
  <c:if test="${!accountForm.newAccount}">
    <strong class="title">Username</strong>
    <input type="text" class="textField" value="<c:out value="${accountForm.account.username}"/>" DISABLED/>
  </c:if>
	</div>
  
  <div id="password" class="formField">
  <c:if test="${accountForm.newAccount}">
    <spring:bind path="accountForm.account.password">
			<strong class="title">Password <span class="required">*</span></strong>
      <input type="password" class="textField" value="<c:out value="${status.value}"/>" id="<c:out value="${status.expression}"/>" name="<c:out value="${status.expression}"/>" size="" <c:if test="${empty status.errorMessage}">class="required"</c:if><c:if test="${!empty status.errorMessage}">class="required errFld"</c:if>/>
      <span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
    </spring:bind> <br />
    <spring:bind path="accountForm.repeatedPassword">
      <strong class="title">Confirm Password <span class="required">*</span></strong>
      <input type="password" value="<c:out value='${status.value}'/>" id="<c:out value='${status.expression}'/>" name="<c:out value="${status.expression}"/>" size="" class="<c:if test="${empty status.errorMessage}">required</c:if><c:if test="${!empty status.errorMessage}">required errFld</c:if> textField"/>
      <span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value='${status.errorMessage}'/></span>
    </spring:bind>
  </c:if>
  <c:if test="${!accountForm.newAccount}">
		<strong class="title">Password </strong>
    <a href="<c:url value='/myciteseer/action/changePassword'/>" class="change_pass">Change Password</a>
  </c:if>
  </div>
</div>

<div id="profile-block" class="accountEdit">
  <div id="email" class="formField">
    <spring:bind path="accountForm.account.email">
      <strong class="title">Email <span class="required">*</span></strong>
      <input type="text" size="43"
           id="<c:out value="${status.expression}"/>" 
           name="<c:out value="${status.expression}"/>"
           value="<c:out value="${status.value}"/>"
           onchange="validateAccountInputField(this);"
           class="textField 
           <c:if test="${empty status.errorMessage}">
             validate-email required
           </c:if>  
           <c:if test="${!empty status.errorMessage}">
             validate-email required errFld
           </c:if>" />
      <span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
    </spring:bind>
  </div>

	<div id="name" class="formField">
    <strong class="title">Full Name <span class="required">*</span></strong>
    <spring:bind path="accountForm.account.firstName">
      <input type="text" size="15" class="textField"
      	id="<c:out value="${status.expression}"/>" 
        name="<c:out value="${status.expression}"/>" 
        value="<c:out value="${status.value}"/>"
        onchange="validateAccountInputField(this);" 
        class="textField
        <c:if test="${!empty status.errorMessage}">
          errFld
        </c:if>" />
    	<span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
  	</spring:bind>
  	<spring:bind path="accountForm.account.middleName">
      <input type="text" size="2"
             id="<c:out value="${status.expression}"/>" 
             name="<c:out value="${status.expression}"/>" 
             value="<c:out value="${status.value}"/>"
             onchange="validateAccountInputField(this);"
             class="textField
             <c:if test="${!empty status.errorMessage}">
               errFld
             </c:if>" />
      <span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
  	</spring:bind>
  	<spring:bind path="accountForm.account.lastName">
	    <input type="text" size="18"
           id="<c:out value="${status.expression}"/>" 
           name="<c:out value="${status.expression}"/>" 
           value="<c:out value="${status.value}"/>"
           onchange="validateAccountInputField(this);"
           class="textField
           <c:if test="${!empty status.errorMessage}">
              errFld
            </c:if>" />
	    <span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
  	</spring:bind>
	</div>

	<div id="organization" class="formField">
		<spring:bind path="accountForm.account.affiliation1">
      <strong class="title">Organization <span class="required">*</span></strong>
      <input type="text" size="43"
           id="<c:out value="${status.expression}"/>"
           name="<c:out value="${status.expression}"/>" 
           value="<c:out value="${status.value}"/>"
           onchange="validateAccountInputField(this);"
           class="textField
           <c:if test="${empty status.errorMessage}">
             validate-alpha required
           </c:if>
           <c:if test="${!empty status.errorMessage}">
             validate-alpha required errFld
           </c:if>" />
      <span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
		</spring:bind>
	</div>

	<div id="department" class="formField">
		<spring:bind path="accountForm.account.affiliation2">
  		<strong class="title">Department </strong>
  		<input type="text" size="43" 
         id="<c:out value="${status.expression}"/>"
         name="<c:out value="${status.expression}"/>"
         value="<c:out value="${status.value}"/>"
         class="textField
         <c:if test="${!empty status.errorMessage}">
           errFld
         </c:if>" />
      <span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
		</spring:bind>
	</div>

	<div id="webpage" class="formField">
		<spring:bind path="accountForm.account.webPage">
  		<strong class="title">Web Page</strong>
			<input type="text" size="43"
         id="<c:out value="${status.expression}"/>"
         name="<c:out value="${status.expression}"/>"
         value="<c:out value="${status.value}"/>" 
         class="textField
         <c:if test="${!empty status.errorMessage}">
           errFld
         </c:if>" />
  		<span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
		</spring:bind>
	</div>

	<div id="country" class="formField">
    <spring:bind path="accountForm.account.country">
      <strong class="title">Country <span class="required">*</span></strong>
      <select id="<c:out value="${status.expression}"/>" 
            name="<c:out value="${status.expression}"/>"
            onchange="validateAccountInputField(this); Fill_States('account.country', 'account.province');" 
            <c:if test="${empty status.errorMessage}">
              class="required"
            </c:if>  
            <c:if test="${!empty status.errorMessage}">
              class="required errFld"
            </c:if>>
      <c:if test="${empty accountForm.account.country}">
        <option selected="selected"> --- </option>
      </c:if>
      <c:if test="${!empty accountForm.account.country}">
        <option selected="selected" value="<c:out value="${status.value}"/>"><c:out value="${status.value}"/></option>
      </c:if>
      </select>
      <span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
    </spring:bind>
	</div>

	<div id="geo_area" class="formField">
		<spring:bind path="accountForm.account.province">
      <strong class="title">State/Province </strong>
      <select id="<c:out value="${status.expression}"/>"
          name="<c:out value="${status.expression}"/>"
          <c:if test="${!empty status.errorMessage}">
            class="errFld"
          </c:if> >
    	<c:if test="${empty accountForm.account.province}">
      	<option selected="selected"> --- </option>
    	</c:if>
    	<c:if test="${!empty accountForm.account.province}">
      	<option selected="selected" value="<c:out value="${status.value}"/>"><c:out value="${status.value}"/></option>
    	</c:if>
  		</select>
  		<span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
		</spring:bind>
	</div>
</div>

<div id="system-form" class="accountEdit">
  <c:if test="${!accountForm.newAccount}">
    <div id="apikey_request" class="formField">
      <c:if test="${empty accountForm.account.appid}">
          <a href="<c:url value="/myciteseer/action/requestAppid"/>" title="Request API Key">Request API Key</a>
      </c:if>
      <c:if test="${!empty accountForm.account.appid}">
          <strong class="title">API Key </strong>
          <input type="text" class="textField" value="<c:out value='${accountForm.account.appid}'/>" readonly>
      </c:if>
    </div>
  </c:if>

  <c:if test="${accountForm.newAccount}">
    <div id="captcha" class="formField">
      <img src="<c:url value="/captcha.jpg"/>" /><br />
      <spring:bind path="accountForm.captcha">
        <strong class="title">Type the letters in the picture above <span class="required">*</span></strong>
          <input type="text" size="10"  
             id="<c:out value="${status.expression}"/>"
             name="<c:out value="${status.expression}"/>" 
             value="" 
             class="textField required
             <c:if test="${!empty status.errorMessage}">
               errFld
             </c:if>" />
           <span class="errorMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
    </div>
  </c:if>
</div>

<input type="submit" class="button" id="accountSubmit" name="submitAction" value="<c:if test='${accountForm.newAccount}'>Create Account</c:if><c:if test='${!accountForm.newAccount}'>Save Account Information</c:if>" />


<c:if test="${ ! empty param.ticket }">
  <input type="hidden" name="ticket" value="<c:out value="${ param.ticket }"/>"/>
</c:if>
</form>

</div> <%-- end content div --%>
<div class="clear"></div>
</div> <%-- end main div --%>

<script type="text/javascript">
<!--
  if (window != top) 
    top.location.href = location.href;
    Fill_Country("account.country");Fill_States("account.country", "account.province");
// -->
</script>
<%@ include file="../shared/IncludeFooter.jsp" %>