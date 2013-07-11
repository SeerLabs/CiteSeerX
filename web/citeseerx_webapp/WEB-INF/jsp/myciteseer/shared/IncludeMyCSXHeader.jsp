<%@ include file="../../shared/IncludeHeader.jsp" %>
<div id="main" style="border-top: none;">
<%@ include file="../../shared/IncludeSearchBox.jsp" %>
  <div id="mycsxMenu" class="submenu">
    <ul>
      <li><a id="home" href="<c:url value="/myciteseer/action/accountHome"/>">Account Home</a></li>
      <li><a id="profile" href="<c:url value="/myciteseer/action/editAccount"/>">Profile</a></li>
      <li><a id="collections"href="<c:url value="/myciteseer/action/viewCollections"/>" title="">Collections</a></li>
      <li><a id="tags" href="<c:url value="/myciteseer/action/viewTags"/>" title="">Tags</a></li>
      <li><a id="monitoring" href="<c:url value="/myciteseer/action/viewMonitors"/>" title="">Monitoring</a></li>
      <% if (mscConfig.getUrlSubmissionsEnabled()) {%>
        <li><a id="submissions" href="<c:url value="/myciteseer/action/viewSubmissions"/>">Submissions</a></li>
      <% } %>
      <% if (mscConfig.getGroupsEnabled()) {%>
        <li><a id="groups" href="<c:url value="/myciteseer/action/viewGroups"/>">Groups</a></li>
      <% } %>  
      <% if (account != null && account.isAdmin()) { %>
        <li><a id="admin" href="<c:url value="/myciteseer/action/admin/editBanner"/>">Admin Console</a></li>
      <% } %>
     </ul>
   </div><%-- end submenu --%>