<div id="leftmenu" class="column-two-content">
 <h2 class="smallcapped">Groups</h2>
 <ul class="nofrills_list">
  <li id="view_groups"><a href="<c:url value="/myciteseer/action/viewGroups"/>" class="left_item remove">View Groups</a></li>
  <li id="add_group"><a href="<c:url value="/myciteseer/action/addGroup"/>" class="left_item remove">Add Group</a></li>
  <%-- User loaded viewGroup or viewGroupMembersPage --%>
  <c:if test="${ ! empty group and group.id > 0}">
   <li id="view_group_members"><a href="<c:url value="/myciteseer/action/viewGroupMembers?gid=${group.id}"/>" class="left_item remove">View Group Members</a></li>
   <security:accesscontrollist domainObject="${group}" hasPermission="2,16">
    <%-- 2 = write 8 = admin permissions --%>
    <li id="add_member"><a href="<c:url value="/myciteseer/action/addGroupMembers?gid=${group.id}"/>" class="left_item remove">Add Members</a></li>
   </security:accesscontrollist>
  </c:if>
  <%-- User loaded create/edit group page --%>
  <c:if test="${ ! empty groupForm and groupForm.group.id > 0}">
   <li id="view_group_members"><a href="<c:url value="/myciteseer/action/viewGroupMembers?gid=${groupForm.group.id}"/>" class="left_item remove">View Group Members</a></li>
   <security:accesscontrollist domainObject="${group}" hasPermission="2,16">
    <%-- 2 = write 8 = admin permissions--%>
    <li id="add_member"><a href="<c:url value="/myciteseer/action/addGroupMembers?gid=${groupForm.group.id}"/>" class="left_item remove">Add Members</a></li>
   </security:accesscontrollist>
  </c:if>
  <%-- User loaded addGroupMembers page --%>
  <c:if test="${ ! empty groupMemberForm }">
   <li id="view_group_members"><a href="<c:url value="/myciteseer/action/viewGroupMembers?gid=${groupMemberForm.group.id}"/>" class="left_item remove">View Group Members</a></li>
   <security:accesscontrollist domainObject="${group}" hasPermission="2,16">
    <%-- 2 = write 8 = admin permissions--%>
    <li id="add_member"><a href="<c:url value="/myciteseer/action/addGroupMembers?gid=${groupMemberForm.group.id}"/>" class="left_item remove">Add Members</a></li>
   </security:accesscontrollist>
  </c:if>
  <li id="search_groups" class="end"><a href="<c:url value="/myciteseer/action/searchGroups"/>" class="left_item remove">Search Groups</a></li>
 </ul>
</div> <!-- end leftmenu -->