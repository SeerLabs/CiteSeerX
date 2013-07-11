<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="../../shared/IncludeTagLibs.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head profile="http://www.w3.org/2005/11/profile http://a9.com/-/spec/opensearch/1.1/">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
   <c:if test="${ ! empty pagetitle }">
    <title><fmt:message key="app.name"/> &mdash; <c:out value="${ pagetitle }" escapeXml="false"/></title>
  </c:if>
  <c:if test="${ ! empty pagedescription }">
    <meta name="description" content="<fmt:message key="app.name"/> - <c:out value='${ pagedescription }'/>" />
  </c:if>
  <c:if test='${ ! empty pagekeywords }'>
    <meta name="keywords" content="<fmt:message key="app.name"/>, <c:out value='${ pagekeywords }'/>" />
  </c:if>
  <link rel="shortcut icon" type="image/x-icon" href="<c:url value='/favicon.ico'/>" />
  <%@ include file="../../shared/IncludeSearchLinks.jsp"%>
	<link rel="alternate" type="application/atom+xml" title="CiteSeerX Search Results - Atom" href="<c:url value='/search?${ atom }'/>" />
	<link rel="alternate" type="application/rss+xml" title="CiteSeerX Search Results - RSS" href="<c:url value='/search?${ rss }'/>" />

	<%-- Put all CSS first then JS imports here --%>
	<link type="text/css" rel="stylesheet" href="<c:url value='/js/jquery.ui.base.css'/>" />
	<link rel="stylesheet" href="<c:url value='/css/main.css' />" />
	<script type="text/javascript" src="<c:url value='/js/jquery-1.4.2.min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/js/jquery-ui-18.custom.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.idTabs.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/metacart.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/topnav.js'/>"></script>
	
</head>
<body>
	<div id="wrapper">
  	<%@ include file="IncludeTopNav.jsp" %>
	  <div id="header">
			<h1 id="title"><a href="<c:url value='/'/>" title="<fmt:message key="app.name"/>"><img src="<c:url value='/images/csx_logo.png'/>" alt="<fmt:message key="app.name"/> logo" /></a></h1>
		</div>