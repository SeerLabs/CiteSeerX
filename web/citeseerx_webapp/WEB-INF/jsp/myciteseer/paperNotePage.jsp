<%@ include file="IncludeTop.jsp" %>
<script type="text/javascript" src="<c:url value="/js/mootools.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/mooPrompter.js"/>"></script>
<div class="mypagecontent clearfix">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <c:if test="${paperNoteForm.newPaperNote}">
   <form method="post" id="NoteCollection" 
         action="<c:url value="/myciteseer/action/addPaperNote"/>" 
         class="wform labelsLeftAligned hintsTooltip">
  </c:if>
  <c:if test="${!paperNoteForm.newPaperNote}">
   <form method="post" id="NoteCollection" 
         action="<c:url value="/myciteseer/action/editPaperNote"/>" 
         class="wform labelsLeftAligned hintsTooltip">
  </c:if>
  <fieldset id="Note" class="">
   <legend>Note</legend>
   <div class="oneField">
    <label class="preField">Paper:&nbsp;</label>
    <label><c:out value="${paperNoteForm.paperTitle}"/></label>
    <spring:bind path="paperNoteForm.paperNote.PID">
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
    <label class="preField">Collection:&nbsp;</label>
    <label><c:out value="${paperNoteForm.collectionName}"/></label><br />
    <spring:bind path="paperNoteForm.paperNote.CID">
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
    <spring:bind path="paperNoteForm.paperNote.note">
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
      <span>Type your note about the paper</span>
     </div><br />
     <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
      <c:out value="${status.errorMessage}"/>
     </span>
    </spring:bind>
   </div>
  </fieldset>
  <c:if test="${!paperNoteForm.newPaperNote}">
   <spring:bind path="paperNoteForm.paperNote.noteID">
    <input type="hidden" 
           id="<c:out value="${status.expression}"/>" 
           name="<c:out value="${status.expression}"/>" 
           value="<c:out value="${status.value}"/>"/>
   </spring:bind>
  </c:if>
  <div class="actions">
   <input type="submit" class="primaryAction" id="submit-" name="submitAction" value="Save" />
   <c:if test="${!paperNoteForm.newPaperNote}">
    &nbsp;
    <a href="<c:url value="/myciteseer/action/deletePaperNote?doi=${paperNoteForm.paperNote.PID}&amp;cid=${paperNoteForm.paperNote.CID}&amp;nid=${paperNoteForm.paperNote.noteID}"/>" title="Delete note"
       class="delete-note">Delete</a>
   </c:if>
  </div>
 </form>
      
</div> <!-- End column-one-content -->
 </div> <!-- End column-one (center column) -->
 <div class="column-two-sec"> <!-- Left column -->
  <div class="column-two-content">
   <%@ include file="IncludeLeftCollections.jsp" %>
  </div> <!-- End column-two-content -->
 </div> <!-- End column-two (Left column) -->
 <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
</div><!-- End columns-float -->
 <div class="column-three-sec"> <!-- right column -->
  <div class="column-three-content"></div>
 </div> <!-- End column-three -->
 <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
 <div class="nn4clear">&nbsp;</div><!-- # needed for NN4 to clear all columns || not needed by any other browser -->
</div> <!-- End mypagecontent -->

<script type="text/javascript">
<!--
if (window != top) 
 top.location.href = location.href;
function sf(){}
function sa(){
 var elt = document.getElementById("collections_tab");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}

window.addEvent('domready', function(){
 /* Delete paper note confirmation dialog  */
 DelNoteConfirm = new mooPrompter({
    name: 'delete-conf',
    BoxStyles: {
            'width': 300
    }                  
 });
 
 $$('a.delete-note').each(function(elem) {
    elem.addEvent('click', function(event) {
        var event = new Event(event);
        event.stop();
        
        // Present the confirmation dialog.
        DelNoteConfirm.confirm("Are you sure you want to delete this note?", {
            textBoxBtnOk: 'Yes',
            textBoxBtnCancel: 'No',
            onComplete: function(returnValue) {
            if (returnValue) {
                top.location.href = elem.href;
                
            }
        }});
    });
 });
});
// -->
</script>
<%@ include file="../shared/IncludeFooter.jsp" %>