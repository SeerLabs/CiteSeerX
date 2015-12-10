<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="IncludeTagLibs.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head profile="http://www.w3.org/2005/11/profile http://a9.com/-/spec/opensearch/1.1/">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta property="og:image" content="/images/csx_logo_front.png" />
   
    <title><fmt:message key="app.name"/><c:if test="${ ! empty pagetitle }"> &mdash; <c:out value="${ pagetitle }" escapeXml="false"/></c:if></title>
  
  <c:if test="${ ! empty pagedescription }">
    <meta name="description" content="<fmt:message key="app.name"/> - <c:out value='${ pagedescription }'/>" />
  </c:if>
  <c:if test='${ ! empty pagekeywords }'>
    <meta name="keywords" content="<fmt:message key="app.name"/>, <c:out value='${ pagekeywords }'/>" />
  </c:if>
    <c:if test="${ ! empty title }">
    <meta name="citation_title" content="<c:out value="${ title }"/>" />
  </c:if>
  <c:if test="${ ! empty uauthors }">
    <meta name="citation_authors" content="<c:out value="${ authors }"/>" />
  </c:if>
  <c:if test="${ ! empty year }">
    <meta name="citation_year" content="<c:out value="${ year }"/>" />
  </c:if>
  <c:if test="${ ! empty venue }">
    <meta name="citation_conference" content="<c:out value="${ venue }"/>" />
  </c:if>

  <link rel="shortcut icon" type="image/x-icon" href="<c:url value='/favicon.ico'/>" />
  <%@ include file="IncludeSearchLinks.jsp"%>
    <c:url value='/search?${ atom }' var="atomFeed"/>
    <c:url value='/search?${ rss }' var="rssFeed"/>
	<link rel="alternate" type="application/atom+xml" title="CiteSeerX Search Results - Atom" href="<c:out value='${ atomFeed }' escapeXml="true"/>" />
	<link rel="alternate" type="application/rss+xml" title="CiteSeerX Search Results - RSS" href="<c:out value='${ rssFeed }' escapeXml="true"/>" />

	<%-- Put all CSS first then JS imports here --%>
	<link type="text/css" rel="stylesheet" href="<c:url value='/css/main.css' />" />
	<script type="text/javascript" src="<c:url value='/js/jquery-1.4.2.min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/js/jquery-ui-1.8.custom.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.idTabs.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/metacart.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/topnav.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/citeseerx.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/correctionutils.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/checkboxes.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/ga.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/s2button.js'/>"></script>
	<%-- Data for charts if available --%>
	<c:if test="${! empty chartdata }">
    <script type="text/javascript" src="<c:url value='/js/protovis-r3.2.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/fancybox/jquery.fancybox-1.3.1.pack.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/fancybox/jquery.easing-1.3.pack.js'/>"></script>
    <link rel="stylesheet" href="<c:url value='/css/fancybox/jquery.fancybox-1.3.1.css'/>" type="text/css" media="screen" />
	 <script type="text/javascript">
  	 var chartdata = <c:out value='${chartdata}' escapeXml='false' />;
	 </script>
	</c:if>
</head>
<body>
	<div id="wrapper">
  	<%@ include file="IncludeTopNav.jsp" %>
	  <div id="header">
			<h1 id="title"><a href="<c:url value='/'/>" title="<fmt:message key="app.name"/>"><img src="<c:url value='/images/csx_logo.png'/>" alt="<fmt:message key="app.name"/> logo" height="50%" width="50%"/></a></h1>
		</div>
