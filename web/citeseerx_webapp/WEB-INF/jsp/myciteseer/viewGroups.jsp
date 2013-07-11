<%@ include file="IncludeTop.jsp" %>
<script type="text/javascript" src="<c:url value="/js/mooPrompter.js"/>"></script>
<div class="mypagecontent clearfix">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <div id="tabs_container">
   <ul class="mootabs_title">
    <li title="MyGroups"><div>My Groups</div></li>
    <li title="Member"><div>Member Of</div></li>
   </ul>
   <div id="MyGroups" class="mootabs_panel">
    <div class="panel_content">
     <div id="introduction">
      <div class="padded">Order By:
        <c:if test='${ mgsort eq "name" }'>
         <a href="<c:url value="/myciteseer/action/viewGroups${ nameqmg }"/>">Name <c:if test='${smgtype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif" />" alt="Asc" /></c:if><c:if test='${smgtype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc"/></c:if> </a>
        </c:if>
       </div>
     </div> <!-- End Introduction -->
     <div class="information_bar char_increased">
      <c:if test="${ ! empty previouspageparamsmg }"><a href="<c:url value="/myciteseer/action/viewGroups${ previouspageparamsmg }"/>">&#8592; Previous Page&nbsp;</a></c:if>Groups found: <c:out value="${mgsize}"/> groups.
      Page <c:out value="${mgpn}" /> of <c:out value="${tpmg}" />
      <c:if test="${ ! empty nextpageparamsmg }"><a href="<c:url value="/myciteseer/action/viewGroups${ nextpageparamsmg }"/>">Next Page &#8594;</a></c:if>
     </div> <!-- End Information Bar -->
     <div class="content">
      <c:if test="${!empty mygroups}">
       <table class="datatable">
        <thead>
         <tr>
          <th>Name</th><th>Description</th><th>Views</th>
         </tr>
        </thead>
        <tbody>
         <c:forEach var="mygroup" items="${mygroups}" varStatus="status">
          <c:if test="${(status.count%2)==0}">
          <tr class="even">
          </c:if>
          <c:if test="${(status.count%2)!=0}">
          <tr class="odd">
          </c:if>
          <td><c:out value="${mygroup.name}"/></td>
          <td><c:out value="${mygroup.description}"/></td>
          <td>
           <a href="<c:url value="/myciteseer/action/viewGroupMembers?gid=${mygroup.id}"/>" 
              title="View <c:out value="${mygroup.name}"/> Members">Members</a>
            <a href="<c:url value="/myciteseer/action/editGroup?gid=${mygroup.id}"/>"
               title="Edit <c:out value="${mygroup.name}"/>">Edit</a>
            &nbsp;
            <a href="<c:url value="/myciteseer/action/deleteGroup?gid=${mygroup.id}"/>"
               class="delete-group" title="Delete <c:out value="${mygroup.name}"/>">Delete</a>
          </td>
          </tr>
         </c:forEach>
        </tbody>
       </table>
      </c:if>
      <c:if test="${empty mygroups}">You don't own any group.</c:if>
     </div> <!-- End content -->
     <div class="conclusion">
      <div class="padded">Order By:
        <c:if test='${ mgsort eq "name" }'>
         <a href="<c:url value="/myciteseer/action/viewGroups${ nameqmg }"/>">Name <c:if test='${smgtype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif" />" alt="Asc" /></c:if><c:if test='${smgtype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc"/></c:if> </a>
        </c:if>
       </div>
     </div> <!-- End Introduction -->
     <div class="information_bar char_increased">
      <c:if test="${ ! empty previouspageparamsmg }"><a href="<c:url value="/myciteseer/action/viewGroups${ previouspageparamsmg }"/>">&#8592; Previous Page&nbsp;</a></c:if>Groups found: <c:out value="${mgsize}"/> groups.
      Page <c:out value="${mgpn}" /> of <c:out value="${tpmg}" />
      <c:if test="${ ! empty nextpageparamsmg }"><a href="<c:url value="/myciteseer/action/viewGroups${ nextpageparamsmg }"/>">Next Page &#8594;</a></c:if>
     </div> <!-- End Information Bar -->
    </div> <!-- End panel_content -->
   </div> <!-- End MyGroups - mootabs_panel -->
   <div id="Member" class="mootabs_panel">
    <div class="panel_content">
     <div class="introduction">
      <div class="padded">Order By:
        <c:if test='${ mogsort eq "name" }'>
         <a href="<c:url value="/myciteseer/action/viewGroups${ nameqmog }"/>">Name <c:if test='${smogtype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif" />" alt="Asc" /></c:if><c:if test='${smogtype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc"/></c:if> </a>
        </c:if>
       </div>
     </div> <!-- End Introduction -->
     <div class="information_bar char_increased">
      <c:if test="${ ! empty previouspageparamsmog }"><a href="<c:url value="/myciteseer/action/viewGroups${ previouspageparamsmog }"/>">&#8592; Previous Page&nbsp;</a></c:if>Groups found: <c:out value="${mosize}"/> groups.
      Page <c:out value="${mogpn}" /> of <c:out value="${tpmog}" />
      <c:if test="${ ! empty nextpageparamsmog }"><a href="<c:url value="/myciteseer/action/viewGroups${ nextpageparamsmog }"/>">Next Page &#8594;</a></c:if>
     </div> <!-- End Information Bar -->
     <div class="content">
      <c:if test="${!empty memberof}">
       <table class="datatable">
        <thead>
         <tr>
          <th>Name</th><th>Description</th><th>Views</th>
         </tr>
        </thead>
        <tbody>
         <c:forEach var="group" items="${memberof}">
          <tr>
           <td><c:out value="${group.name}"/></td>
           <td><c:out value="${group.description}"/></td>
           <td>
            <a href="<c:url value="/myciteseer/action/viewGroupMembers?gid=${group.id}"/>" 
               title="View <c:out value="${group.name}"/> members">Members</a>
             &nbsp;<a href="<c:url value="/myciteseer/action/leaveGroup?gid=${group.id}&tab=Member"/>" 
                title="Leave Group">Leave Group</a>
           </td>
          </tr>
         </c:forEach>
        </tbody>
       </table>
      </c:if>
      <c:if test="${empty memberof}">You're not part of any group.</c:if>
     </div> <!-- End content -->
     <div id="conclusion">
      <div class="padded">Order By:
        <c:if test='${ mogsort eq "name" }'>
         <a href="<c:url value="/myciteseer/action/viewGroups${ nameqmog }"/>">Name <c:if test='${smogtype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif" />" alt="Asc" /></c:if><c:if test='${smogtype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc"/></c:if> </a>
        </c:if>
       </div>
     </div> <!-- End Introduction -->
     <div class="information_bar char_increased">
      <c:if test="${ ! empty previouspageparamsmog }"><a href="<c:url value="/myciteseer/action/viewGroups${ previouspageparamsmog }"/>">&#8592; Previous Page&nbsp;</a></c:if>Groups found: <c:out value="${mosize}"/> groups.
      Page <c:out value="${mogpn}" /> of <c:out value="${tpmog}" />
      <c:if test="${ ! empty nextpageparamsmog }"><a href="<c:url value="/myciteseer/action/viewGroups${ nextpageparamsmog }"/>">Next Page &#8594;</a></c:if>
     </div> <!-- End Information Bar -->
    </div> <!-- End panel_content -->
   </div> <!-- End MyGroups - mootabs_panel -->
  </div> <!-- End tabs_container -->
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

 elt = document.getElementById("view_groups");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}

window.addEvent('domready', function(){
 /* group_tabs */
 var groupTabs = new mootabs('tabs_container', {
  width:'100%',
  changeTransition: 'none',
  mouseOverClass:'over'
 });
 
 groupTabs.activate('<c:out value="${tab}"/>');
 
 /* Delete group confirmation dialog  */
 DelGroupConfirm = new mooPrompter({
    name: 'delete-conf',
    BoxStyles: {
            'width': 300
    }                  
 });
 
 // Appends a confirmation to each delete link.
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