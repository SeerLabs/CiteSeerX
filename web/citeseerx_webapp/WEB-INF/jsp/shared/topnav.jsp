<%--
  -- This page shows the top Menu. It's include mostly by all pages.
  -- This page assumes that its parent is including IncludeTagLibs.jsp
  --
  -- Author: Isaac Councill
  -- Author: Juan Pablo Fernandez Ramirez
  --%>
<div id="top_nav_bar" class="top_nav clearfix">
 <div id="top_nav_bar_main">
  <ul><li><a class="lightlink" href="<c:url value="/"/>" title="<fmt:message key="app.name"/> Home page and Statistics">Home&#124;Statistics</a></li><li><a class="lightlink" href="<c:url value="/about/site"/>" title="About <fmt:message key="app.name"/>">About</a></li><li><a class="lightlink" href="<fmt:message key="app.bulletin"/>" title="Updates and New Features">Bulletin</a></li><li><a class="lightlink" href="<c:url value="/submit"/>" title="Add Your Document">Submit Documents</a></li><li><a class="lightlink" href="<c:url value="/feedback"/>" title="Submit Your Feedback">Feedback</a></li></ul>
 </div>
 <div id="top_nav_bar_user">
   <span id="cartspan"><a class="lightlink" href="<c:url value="/metacart"/>">MetaCart</a></span>
   <% if (mscConfig.getPersonalPortalEnabled()) {%>
     <% if (account != null) { %>
       <span id="userspan">Logged in as <a class="lightlink" href="<c:url value="/myciteseer/action/accountHome"/>" title="Go to MyCiteSeerX"><%= account.getUsername() %></a> &#124; <a class="lightlink" href="<c:url value="/j_spring_security_logout"/>" title="Log Out of <fmt:message key="app.portal"/>">Sign Out</a></span>
     <% } else { %>
      <span id="userspan">Sign in to <a class="lightlink" href="<c:url value="/myciteseer/action/accountHome"/>"><fmt:message key="app.portal"/></a></span>
     <% } %>
   <% } %>
 </div>
</div> <!-- End top_nav_bar -->
