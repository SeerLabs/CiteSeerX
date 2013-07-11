<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent clearfix">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <h2 class="char_headers"><c:out value="${group.name}" /></h2>
  <div id="tabs_container">
   <ul class="mootabs_title">
    <li title="Members"><div>Members</div></li>
    <li title="Validating"><div>Validating</div></li>
   </ul>
   <div id="Members" class="mootabs_panel">
    <div class="panel_content">
     <div id="introduction">
      <div class="padded">Order By:
        <c:if test='${ msort eq "name" }'>
         <a href="<c:url value="/myciteseer/action/viewGroupMembers${ nameqm }"/>">Name <c:if test='${smtype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif" />" alt="Asc" /></c:if><c:if test='${smtype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc"/></c:if> </a>
        </c:if>
       </div>
     </div> <!-- End Introduction -->
     <div class="information_bar char_increased">
      <c:if test="${ ! empty previouspageparamsmem }"><a href="<c:url value="/myciteseer/action/viewGroupMembers${ previouspageparamsmem }"/>">&#8592; Previous Page&nbsp;</a></c:if>Members found: <c:out value="${msize}"/> members.
      Page <c:out value="${mpn}" /> of <c:out value="${tpm}" />
      <c:if test="${ ! empty nextpageparamsmem }"><a href="<c:url value="/myciteseer/action/viewGroupMembers${ nextpageparamsmem }"/>">Next Page &#8594;</a></c:if>
     </div> <!-- End Information Bar -->
     <div class="content">
      <c:if test="${!empty members}">
       <table class="datatable">
        <thead>
         <tr>
          <th>User Id</th><th>Name</th><c:if test="${isowner eq true}"><th>Views</th></c:if>
         </tr>
        </thead>
        <tbody>
         <c:forEach var="groupMember" items="${members}" varStatus="status">
          <c:if test="${(status.count%2)==0}">
          <tr class="even">
          </c:if>
          <c:if test="${(status.count%2)!=0}">
          <tr class="odd">
          </c:if>
          <td><c:out value="${groupMember.member.username}"/></td>
          <td><c:out value="${groupMember.member.firstName}"/>&nbsp;<c:out value="${groupMember.member.middleName}"/>&nbsp;<c:out value="${groupMember.member.lastName}"/></td>
          <security:accesscontrollist domainObject="${group}" hasPermission="2,16">
           <%-- 2 = write, 8 = admin permissions--%>
           <c:if test="${(groupMember.member.username ne group.owner)}">
           <td>
            <a href="<c:url value="/myciteseer/action/removeMember?gid=${groupMember.groupId}&userid=${groupMember.member.username}&tab=Members"/>" 
               title="Remove Member">Remove Member</a>
           </td>
           </c:if>
          </security:accesscontrollist>
          </tr>
         </c:forEach>
        </tbody>
       </table>
      </c:if>
      <c:if test="${empty members}">This group does not have any members.</c:if>
     </div> <!-- End content -->
     <div id="conclusion">
      <div class="padded">Order By:
        <c:if test='${ msort eq "name" }'>
         <a href="<c:url value="/myciteseer/action/viewGroupMembers${ nameqv }"/>">Name <c:if test='${smtype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif" />" alt="Asc" /></c:if><c:if test='${smtype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc"/></c:if> </a>
        </c:if>
       </div>
     </div> <!-- End Introduction -->
     <div class="information_bar char_increased">
      <c:if test="${ ! empty previouspageparamsmem }"><a href="<c:url value="/myciteseer/action/viewGroupMembers${ previouspageparamsmem }"/>">&#8592; Previous Page&nbsp;</a></c:if>Members found: <c:out value="${msize}"/> members.
      Page <c:out value="${mpn}" /> of <c:out value="${tpm}" />
      <c:if test="${ ! empty nextpageparamsmem }"><a href="<c:url value="/myciteseer/action/viewGroupMembers${ nextpageparamsmem }"/>">Next Page &#8594;</a></c:if>
     </div> <!-- End Information Bar -->
    </div> <!-- End panel_content -->
   </div> <!-- End MyGroups - mootabs_panel -->
   <div id="Validating" class="mootabs_panel">
    <div class="panel_content">
     <div class="introduction">
      <div class="padded">Order By:
        <c:if test='${ vsort eq "name" }'>
         <a href="<c:url value="/myciteseer/action/viewGroupMembers${ nameqv }"/>">Name <c:if test='${svtype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif" />" alt="Asc" /></c:if><c:if test='${svtype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc"/></c:if> </a>
        </c:if>
       </div>
     </div> <!-- End Introduction -->
     <div class="information_bar char_increased">
      <c:if test="${ ! empty previouspageparamsval }"><a href="<c:url value="/myciteseer/action/viewGroupMembers${ previouspageparamsval }"/>">&#8592; Previous Page&nbsp;</a></c:if>Validating members found: <c:out value="${vsize}"/> members.
      Page <c:out value="${vpn}" /> of <c:out value="${tpv}" />
      <c:if test="${ ! empty nextpageparamsval }"><a href="<c:url value="/myciteseer/action/viewGroupMembers${ nextpageparamsval }"/>">Next Page &#8594;</a></c:if>
     </div> <!-- End Information Bar -->
     <div class="content">
      <c:if test="${!empty validating}">
       <table class="datatable">
        <thead>
         <tr>
          <th>User Id</th><th>Name</th><th>Views</th>
         </tr>
        </thead>
        <tbody>
         <c:forEach var="groupMember" items="${validating}" varStatus="status">
          <c:if test="${(status.count%2)==0}">
          <tr class="even">
          </c:if>
          <c:if test="${(status.count%2)!=0}">
          <tr class="odd">
          </c:if>
           <td><c:out value="${groupMember.member.username}"/></td>
           <td><c:out value="${groupMember.member.firstName}"/>&nbsp;<c:out value="${groupMember.member.middleName}"/>&nbsp;<c:out value="${groupMember.member.lastName}"/></td>
           <security:accesscontrollist domainObject="${group}" hasPermission="2,16">
           <%-- 2 = write, 8 = admin permissions--%>
           <td>
            <a href="<c:url value="/myciteseer/action/validateMember?gid=${groupMember.groupId}&userid=${groupMember.member.username}&tab=Validating"/>" 
              title="Accept Member">Accept</a>
            &nbsp;
            <a href="<c:url value="/myciteseer/action/removeMember?gid=${groupMember.groupId}&userid=${groupMember.member.username}&tab=Validating"/>" 
               title="Remove Member">Remove</a>
           </td>
          </security:accesscontrollist>
          </tr>
         </c:forEach>
        </tbody>
       </table>
      </c:if>
      <c:if test="${empty validating}">There are not users awaiting for validation.</c:if>
     </div> <!-- End content -->
     <div class="conclusion">
      <div class="padded">Order By:
        <c:if test='${ vsort eq "name" }'>
         <a href="<c:url value="/myciteseer/action/viewGroupMembers${ nameq }"/>">Name <c:if test='${svtype eq "asc"}'><img src="<c:url value="/icons/iconarrwor.gif" />" alt="Asc" /></c:if><c:if test='${svtype eq "desc"}'><img src="<c:url value="/icons/iconarrowup.gif"/>" alt="Desc"/></c:if> </a>
        </c:if>
       </div>
     </div> <!-- End Introduction -->
     <div class="information_bar char_increased">
      <c:if test="${ ! empty previouspageparamsval }"><a href="<c:url value="/myciteseer/action/viewGroupMembers${ previouspageparamsval }"/>">&#8592; Previous Page&nbsp;</a></c:if>Validating members found: <c:out value="${vsize}"/> members.
      Page <c:out value="${vpn}" /> of <c:out value="${tpv}" />
      <c:if test="${ ! empty nextpageparamsval }"><a href="<c:url value="/myciteseer/action/viewGroupMembers${ nextpageparamsval }"/>">Next Page &#8594;</a></c:if>
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

 elt = document.getElementById("view_group_members");
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
});
// -->
</script>
<%@ include file="../shared/IncludeFooter.jsp" %>