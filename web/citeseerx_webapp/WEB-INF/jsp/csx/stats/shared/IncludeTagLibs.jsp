<%--
  -- Page to be included by all jsp pages. Here is the place to add references
  -- to tag libraries.
  -- This page also loads the following objects:
  --
  -- account: User related information
  -- mscConfig: Personal Portal configuration options
  --
  -- Author: Juan Pablo Fernandez Ramirez
  --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> <%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %> <%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %> <%@ page import="edu.psu.citeseerx.myciteseer.domain.Account" %> <%@ page import="edu.psu.citeseerx.myciteseer.domain.MCSConfiguration" %> <%@ page import="edu.psu.citeseerx.myciteseer.web.utils.MCSUtils" %>
<%  MCSConfiguration mscConfig = MCSUtils.getMyCiteSeer(session.getServletContext()).getConfiguration();
    Account account = MCSUtils.getLoginAccount(); %>