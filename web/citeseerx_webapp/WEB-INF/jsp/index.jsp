<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head profile="http://www.w3.org/2005/11/profile">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="keywords" content="CiteSeerX, ResearchIndex, ScienceIndex, CiteSeer, scientific citation index, autonomous citation indexing, scientific literature, computer science, digital library, metadata" />
  <meta name="description" content="Scientific Literature Digital Library incorporating autonomous citation indexing, awareness and tracking, citation context, related document retrieval, similar document identification, citation graph analysis, and query-sensitive document summaries. Advantages in terms of availability, coverage, timeliness, and efficiency. Isaac Councill and C. Lee Giles." />
	<%@ include file="shared/IncludeTagLibs.jsp" %>
  <%@ include file="shared/IncludeSearchLinks.jsp" %>
  <title><fmt:message key='app.name' /></title>
	<%-- Put all CSS first then JS imports here --%>
  <link type="text/css" rel="stylesheet" href="<c:url value='/css/front.css'/>" />
  
  <script type="text/javascript" src="<c:url value='/js/jquery-1.4.2.min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/js/jquery-ui-18.custom.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.idTabs.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/topnav.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/citeseerx.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/ga.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/s2button.js'/>"></script>
</head>

<body class="front">
	<div id="wrapper">
		<%@ include file="shared/IncludeTopNav.jsp" %>
		<%@ include file="shared/IncludeSearchBoxFront.jsp" %>
    <div id="statsLinks">
      Most Cited: <a href="<c:url value="/stats/articles"/>">Documents</a> , <a href="<c:url value="/stats/citations"/>">Citations</a> , <a href="<c:url value="/stats/authors"/>">Authors</a> , <a href="<c:url value="/stats/venues"/>">Venue Impact Rating</a> 
    </div>
	  <%@ include file="shared/IncludeFooter.jsp"%>
