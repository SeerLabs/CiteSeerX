<%--
  -- This page includes Header information for pages within the personal portal
  --
  -- Author: Isaac Councill
  -- Author: Juan Pablo Fernandez Ramirez
  --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="../shared/IncludeTagLibs.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head profile="http://www.w3.org/2005/11/profile http://a9.com/-/spec/opensearch/1.1/">
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
 <meta name="keywords" content="myciteseer, myciteseerx, citeseerx, personal content portal" />
 <meta name="description" content="A personal content portal into CiteSeerX that holds profile (account) information required for interactive tasks such as tagging and data corrections." />
 <link rel="stylesheet" href="<c:url value="/css/csxsecondary.css"/>" type="text/css" />              
 <link rel="stylesheet" href="<c:url value="/css/wforms.css"/>" type="text/css" />              
 <link rel="stylesheet" href="<c:url value="/css/wforms-jsonly.css"/>" type="text/css" />              
 <link rel="stylesheet" type="text/css" href="<c:url value="/css/niftyCorners.css"/>" />
 <link rel="stylesheet" type="text/css" href="<c:url value="/css/niftyPrint.css"/>" media="print" />
 <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/favicon.ico"/>" />
 <%@ include file="../shared/IncludeSearchLinks.jsp"%>
 <script type="text/javascript" src="<c:url value="/js/niftycube.js"/>"></script>
 <script type="text/javascript" src="<c:url value="/js/wforms.js"/>"></script>
 <script type="text/javascript" src="<c:url value="/js/mootools.js"/>"></script>
 <script type="text/javascript" src="<c:url value="/js/mootabs.js"/>"></script>
 <script type="text/javascript" src="<c:url value="/js/searchbox.js"/>"></script>
 <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
 <script type="text/javascript" src="<c:url value="/dwr/interface/MetadataCartJS.js"/>"></script>
 <script type="text/javascript" src="<c:url value="/js/metacart.js"/>"></script>
 <title><fmt:message key="app.portal"/></title>

 <!-- Next is an Internet Explorer Conditional Comment (IECC) which will give the topic_heading the layout it needs to prevent the three pixel gap. This gap is caused by an inline declared item adjacent to a float making a 3px margin between the item and the float. The 3px margin disappears as soon as the content clears the float, causing a 3 pixel text jog. In other words a yucky gap. -->
 <!--[if IE]><style type="text/css"> h2.topic_heading { zoom: 1;}</style><![endif]-->

 <!-- Next is a call for IE. It applies the 'sfhover' class to li elements in the 'secondary_nav' id'd ul element when they are 'moused over' and removes it, using a regular expression, when 'moused out'. -->
 <script type="text/javascript" src="<c:url value="/js/mousedout.js"/>"></script>
</head>
<body onload='sa();sf();'>
  <!-- Begin header for the primary, secondary, tertiary, etc. pages, can be placed in one external file to be called by all pages. -->
  <%@ include file="../shared/topnav.jsp" %>

  <div id="mycsxlogo"> <!-- Contains logo div -->
   <a class="remove" href="<c:url value="/myciteseer/action/accountHome"/>" title="<fmt:message key="app.portal"/>"><img src="<c:url value="/images/blank.gif"/>" alt="<fmt:message key="app.portal"/> logo" /></a>
  </div> <!-- End logo -->
    
  <!-- First are the floating containers -->
  <%@ include file="../shared/searchbox.jsp" %>
  <!-- End of the floating containers -->    
 
  <div id="page_wrapper" class="clearfix"> 
   <div id="myheader" class="clearfix"> <!-- Contains header info -->
    <div id="mymeta_nav" class="mynavsub_menu"> <!-- Meta Nav, change if the user is in or out -->
     <ul><li><a href="<c:url value="/about/myciteseer"/>">About <fmt:message key="app.portal"/></a></li><li class="last"><a href="<c:url value="/help/myciteseer"/>" title="">Account Help</a></li></ul>
    </div><!-- end div meta_nav-->
    <div id="mymain_menu" class="mynav"> <!-- Main Menu -->
     <ul>
      <li><a id="home_tab" href="<c:url value="/myciteseer/"/>">Account Home</a></li>
      <li><a id="profile_tab" href="<c:url value="/myciteseer/action/editAccount"/>">Profile</a></li>
      <li><a id="collections_tab" href="<c:url value="/myciteseer/action/viewCollections"/>" title="">Collections</a></li>
      <li><a id="tags_tab" href="<c:url value="/myciteseer/action/viewTags"/>" title="">Tags</a></li>
      <li><a id="subscriptions_tab" href="<c:url value="/myciteseer/action/viewMonitors"/>" title="">Monitoring</a></li>
      <% if (mscConfig.getUrlSubmissionsEnabled()) {%>
        <li><a id="submissions_tab" href="<c:url value="/myciteseer/action/viewSubmissions"/>">Submissions</a></li>
      <% } %>
      <% if (mscConfig.getGroupsEnabled()) {%>
        <li><a id="groups_tab" href="<c:url value="/myciteseer/action/viewGroups"/>">Groups</a></li>
      <% } %>  
      <% if (account != null && account.isAdmin()) { %>
      <li><a id="admin_tab" href="<c:url value="/myciteseer/action/admin/editBanner"/>">Admin Console</a></li>
      <% } %>
     </ul>
    </div><!-- end div main_nav-->
   </div> <!-- end div myheader -->
