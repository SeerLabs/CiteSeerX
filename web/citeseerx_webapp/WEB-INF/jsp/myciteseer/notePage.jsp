<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<script type="text/javascript">
  $("#collections").addClass('active');
  $("#col_tabs").idTabs();
</script>
<div id="content">
  <h2>Collections</h2>
  <div id="left-sidebar">
    <%@ include file="shared/IncludeCollectionsSidebar.jsp" %>
  </div>
  <div id="body">
    
  <c:if test="${collectionNoteForm.newCollectionNote}">
   <form method="post" id="CollectionNote" 
         action="<c:url value="/myciteseer/action/addCollectionNote"/>" 
         class="wform labelsLeftAligned hintsTooltip">
  </c:if>
  <c:if test="${!collectionNoteForm.newCollectionNote}">
   <form method="post" id="CollectionNote" 
         action="<c:url value="/myciteseer/action/editCollectionNote"/>" 
         class="wform labelsLeftAligned hintsTooltip">
  </c:if>
  <fieldset id="Note" class="">
   <legend>Note</legend>
   <div class="oneField">
    <label class="preField">Collection:&nbsp;</label>
    <label><c:out value="${collectionNoteForm.collectionName}"/></label><br />
    <spring:bind path="collectionNoteForm.collectionNote.collectionID">
     <input type="hidden" 
            id="<c:out value="${status.expression}"/>" 
            name="<c:out value="${status.expression}"/>" 
            value="<c:out value="${status.value}"/>"/>
     <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
      <c:out value="${status.errorMessage}"/>
     </span>
    </spring:bind>
   </div>
   <div class="oneField">
    <spring:bind path="collectionNoteForm.collectionNote.note">
     <label for="<c:out value="${status.expression}"/>" class="preField">Note:&nbsp;
      <span class="reqMark">*</span>
     </label>
     <c:if test="${empty status.errorMessage}">
      <textarea cols="40" rows="4" id="<c:out value="${status.expression}"/>" 
                name="<c:out value="${status.expression}"/>" 
                <c:if test="${empty status.errorMessage}">class="required"</c:if>
      ><c:out value="${status.value}"/></textarea>
     </c:if>
     <c:if test="${!empty status.errorMessage}">
      <textarea cols="40" rows="4" id="<c:out value="${status.expression}"/>" 
                name="<c:out value="${status.expression}"/>" 
                <c:if test="${!empty status.errorMessage}">class="required errFld"</c:if>
      ><c:out value="${status.value}"/></textarea>
     </c:if>
     <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
      <span>Type your note about the collection</span>
     </div><br />
     <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
      <c:out value="${status.errorMessage}"/>
     </span>
    </spring:bind>
   </div>
  </fieldset>
  <c:if test="${!collectionNoteForm.newCollectionNote}">
   <spring:bind path="collectionNoteForm.collectionNote.noteID">
    <input type="hidden" 
           id="<c:out value="${status.expression}"/>" 
           name="<c:out value="${status.expression}"/>" 
           value="<c:out value="${status.value}"/>"/>
   </spring:bind>
  </c:if>
  <div class="actions">
   <input type="submit" class="primaryAction" id="save" name="saveAction" value="Save"/>
   <c:if test="${!collectionNoteForm.newCollectionNote}">
    &nbsp;
    <a href="<c:url value="/myciteseer/action/deleteCollectionNote?cid=${collectionNoteForm.collectionNote.collectionID}&amp;nid=${collectionNoteForm.collectionNote.noteID}"/>" title="Delete note"
       class="delete-note">Delete</a>
   </c:if>
  </div>
 </form>

</div>
</div>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>