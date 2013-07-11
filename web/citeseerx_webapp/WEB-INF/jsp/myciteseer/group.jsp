<%@ include file="IncludeTop.jsp" %>
<script type="text/javascript" src="<c:url value="/js/mooPrompter.js"/>"></script>
<div class="mypagecontent clearfix">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <h2 class="char_headers">Create/Edit a Group</h2>
  <div class="content">
  <c:if test="${groupForm.newGroup}">
    <form method="post" 
          action="<c:url value="/myciteseer/action/addGroup"/>" 
          id="collection_form" class="wform labelsLeftAligned hintsTooltip">
   </c:if>
   <c:if test="${!groupForm.newGroup}">
    <form method="post" 
          action="<c:url value="/myciteseer/action/editGroup"/>" 
          id="collection_form" class="wform labelsLeftAligned hintsTooltip">
   </c:if>
   <fieldset id="group" class="">
    <legend>Group</legend>
    <spring:bind path="groupForm.group.name">
     <div class="oneField">
      <label for="<c:out value="${status.expression}"/>" class="preField">Name&nbsp;
       <span class="reqMark">*</span>
      </label>
      <input type="text" size="40" 
             id="<c:out value="${status.expression}"/>" 
             name="<c:out value="${status.expression}"/>"
             value="<c:out value="${status.value}"/>" 
             <c:if test="${empty status.errorMessage}">class="required"</c:if>
             <c:if test="${!empty status.errorMessage}">class="required errFld"</c:if>
      />
      <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
       <span>Example: My Co-Workers</span>
      </div>
      <br />
      <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
       <c:out value="${status.errorMessage}"/>
      </span>
     </div>
    </spring:bind>
    <div class="oneField">
     <spring:bind path="groupForm.group.description">
      <label for="<c:out value="${status.expression}"/>" class="preField">Description</label>
      <textarea cols="40" rows="4" id="<c:out value="${status.expression}"/>" 
                name="<c:out value="${status.expression}"/>" 
                <c:if test="${empty status.errorMessage}">class=""</c:if>
                <c:if test="${!empty status.errorMessage}">class="errFld"</c:if>
      ><c:out value="${status.value}"/></textarea>
      <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
       <span>Example: People working in my Lab</span>
      </div>
      <br />
      <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
       <c:out value="${status.errorMessage}"/>
      </span>
     </spring:bind>
    </div>
   </fieldset>
   <c:if test="${!groupForm.newGroup}">
    <spring:bind path="groupForm.group.id">
     <input type="hidden" 
            id="<c:out value="${status.expression}"/>" 
            name="<c:out value="${status.expression}"/>" 
            value="<c:out value="${status.value}"/>">
    </spring:bind>
   </c:if>
   <div class="actions">
    <input type="submit" class="primaryAction" id="submit-" name="submit" value="submit" />
    <c:if test="${!groupForm.newGroup}">
      &nbsp;
      <a href="<c:url value="/myciteseer/action/deleteGroup?gid=${groupForm.group.id}"/>" title="Delete <c:out value="${group.name}"/>"
         class="delete-group">Delete</a>
    </c:if>
   </div>
  </form>
 </div>

</div> <!-- End column-one-content -->
 </div> <!-- End column-one (center column) -->
 <div class="column-two"> <!-- Left column -->
   <%@ include file="IncludeLeftGroups.jsp" %>
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
 var elt = document.getElementById("groups_tab");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");

 elt = document.getElementById("add_group");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}

window.addEvent('domready', function(){
 /* Delete collection note confirmation dialog  */
 DelGroupConfirm = new mooPrompter({
    name: 'delete-conf',
    BoxStyles: {
            'width': 300
    }                  
 });
 
 $$('a.delete-group').each(function(elem) {
    elem.addEvent('click', function(event) {
        var event = new Event(event);
        event.stop();
        
        // Present the confirmation dialog.
        DelGroupConfirm.confirm("Are you sure you want to delete this group?", {
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