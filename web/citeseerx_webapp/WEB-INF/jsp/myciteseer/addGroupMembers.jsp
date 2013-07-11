<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent clearfix">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <h2 class="char_headers"><c:out value="${groupMemberForm.group.name}" /></h2>
  <div class="content">
   <form method="get" 
          action="<c:url value="/myciteseer/action/addGroupMembers"/>" 
          id="user_group_search_form" class="wform labelsLeftAligned hintsTooltip">
    <fieldset id="user_group_search" class="">
     <legend>Find Users:</legend>
     <div id="full_name" class="inlineSection">
      <label class="preField">Enter a Name (part of):&nbsp;</label>
      <div class="oneField">
       <label for="search_firstname/>" class="inlineLabel"></label>
        <c:if test="${ ! empty groupMemberForm }">
         <input type="text" size="15" id="search_firstname" name="search_firstname" value="<c:out value="${groupMemberForm.firstName}"/>" />
        </c:if>
        <c:if test="${ empty groupMemberForm }">
         <input type="text" size="15" id="search_firstname" name="search_firstname" value="" />
        </c:if>
      </div>
      <div class="oneField">
       <label for="search_middlename" class="inlineLabel"></label>
       <c:if test="${ ! empty groupMemberForm }">
        <input type="text" size="2" id="search_middlename" name="search_middlename" value="<c:out value="${groupMemberForm.middleName}"/>" />
       </c:if>
       <c:if test="${ empty groupMemberForm }">
        <input type="text" size="2" id="search_middlename" name="search_middlename" value="" />
       </c:if>
      </div>
      <div class="oneField">
       <label for="search_lastname" class="inlineLabel"></label>
       <c:if test="${ ! empty groupMemberForm }">
        <input type="text" size="18" id="search_lastname" name="search_lastname" value="<c:out value="${groupMemberForm.lastName}"/>" />
       </c:if>
       <c:if test="${ empty groupMemberForm }">
        <input type="text" size="18" id="search_lastname" name="search_lastname" value="" />
       </c:if>
      </div>
     </div>
     <div class="actions">
      <input type="submit" class="primaryAction" id="submit-" name="submitAction" value="Search" />
     </div>
    </fieldset>
    <c:if test="${ ! empty groupMemberForm }">
      <input type="hidden" name="gid" value="<c:out value="${groupMemberForm.group.id }"/>"/>
    </c:if>
   </form>
  </div> <!-- End content -->
  <c:if test="${ error }">
   <br/><br/>
   <span class="char_increased"><c:out value="${ errorMsg }" escapeXml="false"/></span>
   <br/><br/>
  </c:if>
  <c:if test="${ ! empty users }">
  <div class="content">
   <form method="post" 
         action="<c:url value="/myciteseer/action/addGroupMembers"/>" 
         id="add_users" class="wform labelsLeftAligned hintsTooltip">
    <table class="datatable">
     <thead>
      <tr><th>User Id</th><th>Name</th><th>Add User</th></tr>
     </thead>
     <tbody>
      <c:forEach var="user" items="${users}" varStatus="status">
       <c:if test="${(status.count%2)==0}">
        <tr class="even">
       </c:if>
       <c:if test="${(status.count%2)!=0}">
        <tr class="odd">
       </c:if>
       <td><c:out value="${user.username}"/></td><td><c:out value="${user.firstName}" escapeXml="false"/>&nbsp;<c:out value="${user.middleName}" escapeXml="false"/>&nbsp;<c:out value="${user.lastName}" escapeXml="false"/></td>
        <spring:bind path="groupMemberForm.userIDs">
         <td>
          <input type="checkbox" value="<c:out value="${user.username}"/>"
                 id="<c:out value="${status.expression}"/>" 
                 name="<c:out value="${status.expression}"/>" 
                 <c:if test="${!empty status.errorMessage}">class="errFld"</c:if>
          />
         </td>
        </spring:bind>
       </tr>
      </c:forEach>
     </tbody>
    </table>
    <div>
     <spring:bind path="groupMemberForm.userIDs">
      <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
       <c:out value="${status.errorMessage}"/>
      </span>
     </spring:bind>
    </div>
    <spring:bind path="groupMemberForm.group.id">
     <input type="hidden" id="<c:out value="${status.expression}"/>" name="<c:out value="${status.expression}"/>"
             value="<c:out value="${status.value}"/>" />
    </spring:bind>
    <spring:bind path="groupMemberForm.group.name">
     <input type="hidden" id="<c:out value="${status.expression}"/>" name="<c:out value="${status.expression}"/>"
             value="<c:out value="${status.value}"/>" />
    </spring:bind>
    <spring:bind path="groupMemberForm.group.description">
     <input type="hidden" id="<c:out value="${status.expression}"/>" name="<c:out value="${status.expression}"/>"
             value="<c:out value="${status.value}"/>" />
    </spring:bind>
    <spring:bind path="groupMemberForm.group.owner">
     <input type="hidden" id="<c:out value="${status.expression}"/>" name="<c:out value="${status.expression}"/>"
             value="<c:out value="${status.value}"/>" />
    </spring:bind>
    <div class="actions">
     <input type="submit" class="primaryAction" id="submit-" name="submitAction" value="Add Users" />
    </div>
   </form>
  </div>
  </c:if>
 </div> <!-- End column-one-content -->
</div> <!-- End column-one (center column) -->
 <div class="column-two-sec"> <!-- Left column -->
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

 elt = document.getElementById("add_member");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}
// -->
</script>
<%@ include file="../shared/IncludeFooter.jsp" %>