<%@ include file="../shared/IncludeHeader.jsp" %>
<div id="main">
<div id="content">
  <h2>Advanced Search</h2>
    <spring:bind path="advancedSearch.*">
      <c:forEach var="msg" items="${ status.errorMessages }">
        <div class="message"><c:out value="${ msg }"/></div>
      </c:forEach>
    </spring:bind>
    <div id="advSearch" >
      <form id="advancedSearch" class="wform labelsRightAligned hintsTooltip" method="post" action="<c:url value="/advanced_search"/>">
         <div id="textfields" class="fieldSet">
            <h3>Text Fields</h3>
            <div class="help"><p>Specify search terms for each metadata field of interest. Values in separate fields will be joined with an "AND".</p></div>
            <table>
             <spring:bind path="advancedSearch.textQuery">
              <tr><td><strong class="title">Text:</strong></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" class="textField" size="43"/></td></tr>
             </spring:bind>
              
             <spring:bind path="advancedSearch.titleQuery">
              <tr><td><strong class="title">Title:</strong></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" class="textField" size="43"/></td></tr>
             </spring:bind>
 
             <spring:bind path="advancedSearch.authorQuery">
              <tr><td><strong class="title">Author Name:</strong></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" class="textField" size="43"/></td></tr>
             </spring:bind>
 
             <spring:bind path="advancedSearch.affilQuery">
              <tr><td><strong class="title">Author Affiliation:</strong></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" class="textField" size="43"/></td></tr>
             </spring:bind>

             <spring:bind path="advancedSearch.venueQuery">
              <tr><td><strong class="title">Publication Venue:</strong></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" class="textField" size="43"/></td></tr>
             </spring:bind>

             <spring:bind path="advancedSearch.keywordQuery">
              <tr><td><strong class="title">Keywords:</strong></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" class="textField" size="43"/></td></tr>
             </spring:bind>

             <spring:bind path="advancedSearch.abstractQuery">
              <tr><td><strong class="title">Abstract:</strong></td>
              <td><input name="<c:out value="${ status.expression }"/>" id="<c:out value="${ status.expression }"/>" value="<c:out value="${status.value}"/>" type="text" class="textField" size="43"/></td></tr>
             </spring:bind>
            </table>
          </div>
          <div id="rangecriteria" class="fieldSet">
            <h3>Range Criteria</h3>
            <div class="help"><p>Specify any range criteria, including publication date ranges, minimum number of citations, and whether you wish to include records for which we have no corresponding document file (include citations).</p>
            <p>For date ranges, you may leave either the "From" or "To" field blank in order to find all matching records whose publication year is greater or less than the value you specify, respectively.</p></div>
            <table class="advsearch_table">
             <!-- <spring:bind path="advancedSearch.year">
              <tr><td><strong class="title">Publication Year:</strong></td>
              <td><input size="4" maxlength="4" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" type="text" class="textField"/></td>
             </spring:bind> -->
               <tr><td><strong class="title">Publication Year Range</td>
                 <td><spring:bind path="advancedSearch.yearFrom">
              <input size="4" maxlength="4" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" type="text" class="textField"/>
             </spring:bind> -
             <spring:bind path="advancedSearch.yearTo">
              <input size="4" maxlength="4" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" type="text" class="textField"/>
              </spring:bind>
             </td></tr>
   
             <spring:bind path="advancedSearch.minCitations">
              <tr><td><strong class="title">Minimum Number of Citations:</strong></td>
              <td><input size="4" maxlength="5" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" type="text" class="textField"/><span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span></td></tr>
             </spring:bind>
   
             <spring:bind path="advancedSearch.includeCites">
              <tr><td><strong class="title">Include Citations?</strong></td>
              <td><input type="checkbox" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="1" <c:if test="${ advancedSearch.includeCites == '1' }">checked </c:if>/></td></tr>
             </spring:bind>
            </table>
            </div>
	        <div id="sortingcriteria" class="fieldSet">
            <h3>Sorting Criteria</h3>
            <div class="help"><p>Select a method by which your results should be sorted.</p></div>
             <spring:bind path="advancedSearch.sortCriteria">
              <strong class="title">Sort by:</strong>
              <select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" >
               <option value="cite">Citations</option>
               <option value="rlv">Relevance</option>
               <option value="date">Date (Descending)</option>
               <option value="ascdate">Date (Ascending)</option>
               <option value="recent">Recency</option>
              </select>
             </spring:bind>
           </div>
           <p><input type="submit" class="button" id="submit-advsearch" name="submitAction" value="Advanced Search"/></p>
           </form>
		</div>
	</div>
 <div class="clear"></div>

</div>

<%@ include file="../shared/IncludeFooter.jsp" %>