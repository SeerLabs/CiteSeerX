<script type="text/javascript" src="<c:url value="/js/country.js"/>"></script>
<div class="content">
 <form method="post" 
       action="<c:url value="/myciteseer/search/MCSAdvancedSearch"/>" 
       id="peopleadvanced_search" class="wform labelsLeftAligned hintsTooltip">
  <fieldset>
   <legend>Text Fields</legend>
   <div class="information_bar2">Specify search terms for each people field of interest. Values in separate fields will be joined with an "AND".</div>
   <spring:bind path="peopleAdvancedSearch.useridQuery">
    <div class="oneField">
     <label for="<c:out value="${status.expression}"/>" class="preField">User ID:&nbsp;
      <span class="reqMark"></span>
     </label>
     <input type="text" size="40" 
            id="<c:out value="${status.expression}"/>" 
            name="<c:out value="${status.expression}"/>"
            value="<c:out value="${status.value}"/>" 
            <c:if test="${empty status.errorMessage}">class=""</c:if>
            <c:if test="${!empty status.errorMessage}">class="errFld"</c:if>
     />
     <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
      <span></span>
     </div>
     <br />
     <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
      <c:out value="${status.errorMessage}"/>
     </span>
    </div>
   </spring:bind>
   <spring:bind path="peopleAdvancedSearch.firstNameQuery">
    <div class="oneField">
     <label for="<c:out value="${status.expression}"/>" class="preField">First name:&nbsp;
      <span class="reqMark"></span>
     </label>
     <input type="text" size="40" 
            id="<c:out value="${status.expression}"/>" 
            name="<c:out value="${status.expression}"/>"
            value="<c:out value="${status.value}"/>" 
            <c:if test="${empty status.errorMessage}">class=""</c:if>
            <c:if test="${!empty status.errorMessage}">class="errFld"</c:if>
     />
     <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
      <span></span>
     </div>
     <br />
     <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
      <c:out value="${status.errorMessage}"/>
     </span>
    </div>
   </spring:bind>
   <spring:bind path="peopleAdvancedSearch.middleNameQuery">
    <div class="oneField">
     <label for="<c:out value="${status.expression}"/>" class="preField">Middle name:&nbsp;
      <span class="reqMark"></span>
     </label>
     <input type="text" size="40" 
            id="<c:out value="${status.expression}"/>" 
            name="<c:out value="${status.expression}"/>"
            value="<c:out value="${status.value}"/>" 
            <c:if test="${empty status.errorMessage}">class=""</c:if>
            <c:if test="${!empty status.errorMessage}">class="errFld"</c:if>
     />
     <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
      <span></span>
     </div>
     <br />
     <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
      <c:out value="${status.errorMessage}"/>
     </span>
    </div>
   </spring:bind>
   <spring:bind path="peopleAdvancedSearch.lastNameQuery">
    <div class="oneField">
     <label for="<c:out value="${status.expression}"/>" class="preField">Last name:&nbsp;
      <span class="reqMark"></span>
     </label>
     <input type="text" size="40" 
            id="<c:out value="${status.expression}"/>" 
            name="<c:out value="${status.expression}"/>"
            value="<c:out value="${status.value}"/>" 
            <c:if test="${empty status.errorMessage}">class=""</c:if>
            <c:if test="${!empty status.errorMessage}">class="errFld"</c:if>
     />
     <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
      <span></span>
     </div>
     <br />
     <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
      <c:out value="${status.errorMessage}"/>
     </span>
    </div>
   </spring:bind>
   <spring:bind path="peopleAdvancedSearch.affil1Query">
    <div class="oneField">
     <label for="<c:out value="${status.expression}"/>" class="preField">Organization:&nbsp;
      <span class="reqMark"></span>
     </label>
     <input type="text" size="40" 
            id="<c:out value="${status.expression}"/>" 
            name="<c:out value="${status.expression}"/>"
            value="<c:out value="${status.value}"/>" 
            <c:if test="${empty status.errorMessage}">class=""</c:if>
            <c:if test="${!empty status.errorMessage}">class="errFld"</c:if>
     />
     <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
      <span></span>
     </div>
     <br />
     <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
      <c:out value="${status.errorMessage}"/>
     </span>
    </div>
   </spring:bind>
   <spring:bind path="peopleAdvancedSearch.affil2Query">
    <div class="oneField">
     <label for="<c:out value="${status.expression}"/>" class="preField">Department:&nbsp;
      <span class="reqMark"></span>
     </label>
     <input type="text" size="40" 
            id="<c:out value="${status.expression}"/>" 
            name="<c:out value="${status.expression}"/>"
            value="<c:out value="${status.value}"/>" 
            <c:if test="${empty status.errorMessage}">class=""</c:if>
            <c:if test="${!empty status.errorMessage}">class="errFld"</c:if>
     />
     <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
      <span></span>
     </div>
     <br />
     <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
      <c:out value="${status.errorMessage}"/>
     </span>
    </div>
   </spring:bind>
   <spring:bind path="peopleAdvancedSearch.countryQuery">
    <div class="oneField">
     <label for="<c:out value="${status.expression}"/>" class="preField">Country:&nbsp;
      <span class="reqMark"></span>
     </label>
     <select id="<c:out value="${status.expression}"/>" 
             name="<c:out value="${status.expression}"/>"
             onchange="Fill_States('countryQuery','provinceQuery');" 
             <c:if test="${empty status.errorMessage}">
               class=""
             </c:if>  
             <c:if test="${!empty status.errorMessage}">
               class="errFld"
             </c:if>
     >
      <c:if test="${empty peopleAdvancedSearch.countryQuery}">
       <option selected="selected">12345678901234567890</option>
      </c:if>
      <c:if test="${!empty peopleAdvancedSearch.countryQuery}">
       <option selected="selected" value="<c:out value="${status.value}"/>"><c:out value="${status.value}"/></option>
      </c:if>
     </select>
     <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
      <span></span>
     </div>
     <br />
     <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
      <c:out value="${status.errorMessage}"/>
     </span>
    </div>
   </spring:bind>
   <spring:bind path="peopleAdvancedSearch.provinceQuery">
    <div class="oneField">
     <label for="<c:out value="${status.expression}"/>" class="preField">Province:&nbsp;
      <span class="reqMark"></span>
     </label>
     <select id="<c:out value="${status.expression}"/>" 
             name="<c:out value="${status.expression}"/>"
             <c:if test="${empty status.errorMessage}">
               class=""
             </c:if>  
             <c:if test="${!empty status.errorMessage}">
               class="errFld"
             </c:if>
     >
      <c:if test="${empty peopleAdvancedSearch.countryQuery}">
       <option selected="selected">12345678901234567890</option>
      </c:if>
      <c:if test="${!empty peopleAdvancedSearch.provinceQuery}">
       <option selected="selected" value="<c:out value="${status.value}"/>"><c:out value="${status.value}"/></option>
      </c:if>
     </select>
     <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
      <span></span>
     </div>
     <br />
     <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
      <c:out value="${status.errorMessage}"/>
     </span>
    </div>
   </spring:bind>
  </fieldset>
  <div class="actions">
   <input type="submit" class="primaryAction" id="submit-" name="submit" value="Search" />
  </div>
</form>
</div>
<script type="text/javascript">
<!--
  window.addEvent('domready', function(){
    Fill_Country('countryQuery');
    Fill_States('countryQuery', 'provinceQuery');
  });
// -->
</script>