<tr><td class="left">
<font color="red">* </font>First Name:</td><td>
  <spring:bind path="accountForm.account.firstName">
	  <input class="text" type="text" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" onChange="validateAccountInputField(this);"/></td>
	  <td><span id="<c:out value="${status.expression}"/>Error" class="error"><c:out value="${status.errorMessage}"/></span></td>
  </spring:bind>
</td></tr>

<tr><td class="left">
  Middle Name:</td><td>
  <spring:bind path="accountForm.account.middleName">
	  <input class="text" type="text" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" onChange="validateAccountInputField(this);"/></td>
	  <td><span id="<c:out value="${status.expression}"/>Error" class="error"><c:out value="${status.errorMessage}"/></span></td>
  </spring:bind>
</td></tr>

<tr><td class="left">
<font color="red">* </font>Last Name:</td><td>
  <spring:bind path="accountForm.account.lastName">
	  <input class="text" type="text" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" onChange="validateAccountInputField(this);"/></td>
	  <td><span id="<c:out value="${status.expression}"/>Error" class="error"><c:out value="${status.errorMessage}"/></span></td>
  </spring:bind>
</td></tr>

<tr><td class="left">
<font color="red">* </font>Email Address:</td><td>
  <spring:bind path="accountForm.account.email">
	  <input class="text" type="text" size="40" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" onChange="validateAccountInputField(this);"/></td>
	  <td><span id="account.emailError" class="error"><c:out value="${status.errorMessage}"/></span></td>
  </spring:bind>
</td></tr>

<tr><td class="left">
<font color="red">* </font>Primary Affiliation:<br>(Organization)</td><td>
  <spring:bind path="accountForm.account.affiliation1">
	  <input class="text" type="text" size="40" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" onChange="validateAccountInputField(this);"/></td>
	  <td><span id="<c:out value="${status.expression}"/>Error" class="error"><c:out value="${status.errorMessage}"/></span></td>
  </spring:bind>
</td></tr>

<tr><td class="left">
  Primary Affiliation:<br>(Department)</td><td>
  <spring:bind path="accountForm.account.affiliation2">
	  <input class="text" type="text" size="40" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>"/></td>
	  <td><span id="<c:out value="${status.expression}"/>Error" class="error"><c:out value="${status.errorMessage}"/></span></td>
  </spring:bind>
</td></tr>

<tr><spring:bind path="accountForm.account.webPage">
  <td class="left">
    Personal Web Page:</td>
  <td>  
	<input class="text" type="text" size="40" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>"/>
  </td>
  <td><span id="<c:out value="${status.expression}"/>Error" class="error"><c:out value="${status.errorMessage}"/></span></td>
</spring:bind></tr>

<tr><spring:bind path="accountForm.account.country">
  <td class="left">
    <font color="red">* </font>Country:</td><td>
      <select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" onchange="validateAccountInputField(this); Fill_States();">
        <c:if test="${empty accountForm.account.province}">
          <option selected="selected">12345678901234567890</option>
        </c:if>
        <c:if test="${!empty accountForm.account.province}">
          <option selected="selected"><c:out value="${status.value}"/></option>
        </c:if>
      </select>
  </td>
  <td><span id="<c:out value="${status.expression}"/>Error" class="error"><c:out value="${status.errorMessage}"/></span></td>
</spring:bind></tr>

<tr><spring:bind path="accountForm.account.province">
  <td class="left">
    State/Province:</td><td>
      <select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>">
        <c:if test="${empty accountForm.account.province}">
          <option selected="selected">12345678901234567890</option>
        </c:if>
        <c:if test="${!empty accountForm.account.province}">
          <option selected="selected"><c:out value="${status.value}"/></option>
        </c:if>
      </select>
  </td>
  <td><span id="<c:out value="${status.expression}"/>Error" class="error"><c:out value="${status.errorMessage}"/></span></td>
</spring:bind></tr>    
