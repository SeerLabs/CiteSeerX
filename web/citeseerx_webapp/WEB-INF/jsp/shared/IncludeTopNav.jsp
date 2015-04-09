<%-- If on a search result page, menu click performs search on term in search field --%>
<c:if test="${ param != null }">

</c:if>
<div id="topnav">
  <%-- Add additional items for each product you're adding to this installation --%>
  <ul id="search_nav">
    <c:if test="${ param.q == null }">
      <li class="active"><a href="#search_docs">Documents</a></li>
      <li><a href="/#search_auth">Authors</a></li>
      <li><a href="/#search_tables">Tables</a></li>
      <%-- <li><a href="/#search_algorithms">Algorithms</a></li> --%>
    </c:if>
    <c:if test="${ param.q != null }">
      <li<c:if test="${ param.t == 'doc'    }"> class="active" </c:if>><a class="slink" href="<c:url value='/search'/>?q=<c:out value='${param.q}' />&t=doc&sort=rlv">Documents</a></li>
      <li<c:if test="${ param.t == 'auth'  }"> class="active" </c:if>><a class="slink" href="<c:url value='/search'/>?q=<c:out value='${param.q}' />&t=auth&uauth=1&sort=ndocs">Authors</a></li>
      <li<c:if test="${ param.t == 'table' }"> class="active" </c:if>><a class="slink" href="<c:url value='/search'/>?q=<c:out value='${param.q}' />&t=table&sort=rlv">Tables</a></li>
      <%-- <li<c:if test="${ param.t == 'algorithm' }"> class="active" </c:if>><a class="slink" href="<c:url value='/search'/>?q=<c:out value='${param.q}' />&t=algorithm&sort=rlv">Algorithms</a></li> --%>
    </c:if>
  </ul>
  
  <%-- Check for myCiteSeerX --%>
  <% if (mscConfig.getPersonalPortalEnabled()) {%>
    <%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
    <%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
    <%@ page import="org.springframework.security.AuthenticationException" %>
    <ul id="toptools">
  		<% if (account != null) { %>
        <li><a href="<c:url value='/myciteseer/action/accountHome'/>" style="margin-top: -3px;" >MyCiteSeer<sup>X</sup></a></li>
        <li><a href="<c:url value='/j_spring_security_logout'/>">Log out</a></li>
      <% } else { %>
        <li><a href="<c:url value='/myciteseer/login'/>">Log in</a></li>
        <li><a href="<c:url value='/mcsutils/newAccount'/>">Sign up</a></li>
      <% } %>
        <li><a href="<c:url value='/metacart'/>">MetaCart</a></li>
        <li><a href="<c:url value='http://www.givenow.psu.edu/CiteseerxFund'/>"><font color="#045FB4">Donate</font></a></li>
    </ul>
  <% } %>
</div>
