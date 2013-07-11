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
  
  <c:if test="${collectionForm.newCollection}">
    <h3 class="char_headers">Create a Collection</h3>
  </c:if>
  <c:if test="${!collectionForm.newCollection}">
    <h3 class="char_headers">Edit a Collection</h3>
  </c:if>
  <c:if test="${error}">
    <div class="error">
      <c:out value="${ errorMsg }"/>
    </div>
  </c:if>

  <c:if test="${!error}">
    <div class="content">
      <c:if test="${collectionForm.newCollection}">
        <form method="post" 
             action="<c:url value="/myciteseer/action/addCollection"/>" 
             id="collection_form" >
      </c:if>
      <c:if test="${!collectionForm.newCollection}">
        <form method="post" 
              action="<c:url value="/myciteseer/action/editCollection"/>" 
             id="collection_form">
      </c:if>
        <div class="formField">
        <spring:bind path="collectionForm.collection.name">

          <strong class="title">Name <span class="required">*</span></strong>
            <input type="text" size="40" 
                   id="<c:out value="${status.expression}"/>" 
                   name="<c:out value="${status.expression}"/>"
                   value="<c:out value="${status.value}"/>" 
                   class="required textField
                   <c:if test="${!empty status.errorMessage}">errFld</c:if>"
            />

            <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
              <c:out value="${status.errorMessage}"/>
            </span>
        </spring:bind>
        </div>
		<div class="formField">
          <spring:bind path="collectionForm.collection.description">
            <strong class="title">Description</strong>
              <textarea cols="40" rows="4" id="<c:out value="${status.expression}"/>" 
                        name="<c:out value="${status.expression}"/>" 
                        class="textField <c:if test="${!empty status.errorMessage}">errFld</c:if>"><c:out value="${status.value}"/></textarea>
              <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
                <c:out value="${status.errorMessage}"/>
              </span>
            </spring:bind>
		</div>

        <c:if test="${collectionForm.addPaper}">
          <spring:bind path="collectionForm.paperID">
            <input type="hidden" 
                   id="<c:out value="${status.expression}"/>" 
                   name="<c:out value="${status.expression}"/>" 
                   value="<c:out value="${collectionForm.paperID}"/>">
          </spring:bind>
        </c:if>
        <c:if test="${!collectionForm.newCollection}">
          <spring:bind path="collectionForm.collection.collectionID">
            <input type="hidden" 
                   id="<c:out value="${status.expression}"/>" 
                   name="<c:out value="${status.expression}"/>" 
                  value="<c:out value="${status.value}"/>">
          </spring:bind>
        </c:if>
        <div class="actions">
          <input type="submit" class="button" id="submit-" name="submit" value="submit" />
          <c:if test="${!collectionForm.newCollection}">
            <c:if test="${collectionForm.collection.deleteAllowed}">
              &nbsp;
              <a href="<c:url value="/myciteseer/action/deleteCollection?cid=${collectionForm.collection.collectionID}"/>" title="Delete <c:out value="${collection.name}"/>"
                 class="delete-collection">Delete</a>
            </c:if>
          </c:if>
        </div>
      </form>
    </div>
  </c:if>
 </div> <!-- End column-one-content -->
</div> <!-- End column-one (center column) -->

</div>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>

