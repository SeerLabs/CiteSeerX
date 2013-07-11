<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<script type="text/javascript">
  $("#collections").addClass('active');
</script>
<div id="content">
  <h2>Collections</h2>
  <div id="left-sidebar">
    <%@ include file="shared/IncludeCollectionsSidebar.jsp" %>
  </div>
  <div id="body">
  	<h3>Add to Collection</h3>
   	<form method="post" id="collection_paper_form" action="<c:url value="/myciteseer/action/addPaperCollection"/>" >
      <div>
          <strong class="title">Paper:</strong> <c:out value="${paperCollectionForm.paperTitle}"/>
	      	<spring:bind path="paperCollectionForm.paperCollection.paperID">
    	    	<input type="hidden" id="<c:out value="${status.expression}"/>" 
                                name="<c:out value="${status.expression}"/>" 
                                value="<c:out value="${status.value}"/>">
           		<span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      		</spring:bind>
     </div>
     <c:if test="${!empty paperCollectionForm.collections}">
       <spring:bind path="paperCollectionForm.paperCollection.collectionID">
        <strong for="<c:out value="${status.expression}"/>" class="title">Collection <span class="required">*</span> :</strong>
        <select id="<c:out value="${status.expression}"/>" 
                name="<c:out value="${status.expression}"/>"
                class="required
                <c:if test='${!empty status.errorMessage}'>errFld</c:if> value="<c:out value="${status.value}"/>" >">
         <option value="">Please select...</option>
          <c:forEach var="collection" items="${paperCollectionForm.collections}">
           <option value="<c:out value="${collection.collectionID}"/>" class=""><c:out value="${collection.name}"/></option>
          </c:forEach>
        </select>
        <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
         <c:out value="${status.errorMessage}"/>
        </span>
       </spring:bind>
     </c:if>
    <div class="actions">
     <c:if test="${!empty paperCollectionForm.collections}">
      <input type="submit" class="primaryAction" id="submit-" name="submitAction" value="submit">
     </c:if>
     <a href="<c:url value="/myciteseer/action/addCollection?doi=${paperCollectionForm.paperCollection.paperID}"/>" class="secondaryAction" title="Create a new collection and add the paper to it">Create new collection</a>
    </div>
   </form>
</div>
</div>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>
