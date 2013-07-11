<%--
  -- This page includes Header information for public pages which uses a tabs as a main container
  --   Top Navigation Bar
  --   Logo
  --   SearchBox
  -- Author: Juan Pablo Fernandez Ramirez
  --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="../../shared/IncludeTagLibs.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head profile="http://www.w3.org/2005/11/profile http://a9.com/-/spec/opensearch/1.1/">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" href="<c:url value="/css/csxprimary.css"/>" type="text/css" />
  <link rel="stylesheet" href="<c:url value="/css/wforms.css"/>" type="text/css"/>
  <link rel="stylesheet" href="<c:url value="/css/wforms-jsonly.css"/>" type="text/css"/>
  <!-- for mobile devices    <link type="text/css" rel="stylesheet" href="handheldstyle.css" media="handheld" /> -->
  <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/favicon.ico"/>" />
  <%@ include file="../../shared/IncludeSearchLinks.jsp"%>
  <script type="text/javascript" src="<c:url value="/js/metacart.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/js/mootools.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/js/mootabs.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/js/searchbox.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/js/wforms.js"/>"></script>

  <c:if test="${ ! empty pagetitle }">
    <title><c:out value="${ pagetitle }"/></title>
  </c:if>
  <c:if test="${  empty pagetitle }">
   <title><fmt:message key="app.name"/></title>
  </c:if>

  <c:if test="${ ! empty pagedescription }">
    <meta name="description" content="<c:out value="${ pagedescription }"/>" />
  </c:if>
  
  <c:if test="${ ! empty pagekeywords }">
    <meta name="keywords" content="<c:out value="${ pagekeywords }"/>" />
  </c:if>
  
  <!-- Next is an Internet Explorer Conditional Comment (IECC) which will give the topic_heading the layout it needs to prevent the three pixel gap. This gap is caused by an inline declared item adjacent to a float making a 3px margin between the item and the float. The 3px margin disappears as soon as the content clears the float, causing a 3 pixel text jog. In other words a yucky gap. -->
  <!--[if IE]><style type="text/css"> h2.topic_heading { zoom: 1;}</style><![endif]-->
  
  <!-- Next is a call for IE. It applies the 'sfhover' class to li elements in the 'secondary_nav' id'd ul element when they are 'moused over' and removes it, using a regular expression, when 'moused out'. -->
  <script type="text/javascript" src="<c:url value="/js/mousedout.js"/>"></script>
  
  <!-- Next are the calls for nifty rounded corners -->
  <link rel="stylesheet" type="text/css" href="<c:url value="/css/niftyCorners.css"/>" />
  <script type="text/javascript" src="<c:url value="/js/niftycube.js"/>"></script>
</head>
<body onload='Nifty("ul#secondary_nav a","transparent");Nifty("ul.button_nav a","transparent");'>
  <!-- Begin header for the primary, secondary, tertiary, etc. pages, can be placed in one external file to be called by all pages. -->
  <%@ include file="../../shared/topnav.jsp" %>
  <c:if test="${ ! empty banner }"><div class="banner char_increased"><span class="banner_header">Announcement:</span> <span class="banner_text"><c:out value="${ banner }" escapeXml="false"/></span></div></c:if>
  <div id="logo"> <!-- Contains logo div -->
    <a class="remove" href="<c:url value="/"/>" title="<fmt:message key="app.name"/> Home page and Statistics"><img src="<c:url value="/images/blank.gif"/>" alt="<fmt:message key="app.name"/> logo" /></a>
  </div> <!-- End logo -->
  <!-- First are the floating containers -->
  <%@ include file="../../shared/searchbox.jsp" %>
  <!-- End of the floating containers -->
  <!-- End header for the primary, secondary, tertiary, etc. pages -->
  <div id="page_wrapper" class="clearfix"> <!-- Contains all the divisions (div's) within the page (not including top navigation bar, search box and meta information) -->