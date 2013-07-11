<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page contentType="text/html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head profile="http://www.w3.org/2005/11/profile">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" href="<c:url value="/css/csxprimary.css"/>" type="text/css" />
    <link rel="stylesheet" href="<c:url value="/css/wforms.css"/>" type="text/css" />              
    <link rel="stylesheet" href="<c:url value="/css/wforms-jsonly.css"/>" type="text/css" />
<!-- for mobile devices    <link type="text/css" rel="stylesheet" href="handheldstyle.css" media="handheld" /> -->
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value="/favicon.ico"/>" />
    <link rel="search" title="CiteSeerX" type="application/opensearchdescription+xml" href="<c:url value="/search_plugins/citeseerx_general.xml"/>" />
    <link rel="search" title="CiteSeerX Author" type="application/opensearchdescription+xml" href="<c:url value="/search_plugins/citeseerx_author.xml"/>" />
    <link rel="search" title="CiteSeerX Title" type="application/opensearchdescription+xml" href="<c:url value="/search_plugins/citeseerx_title.xml"/>" />
    <script type="text/javascript" src="<c:url value="/js/wforms.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/mootools.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/mootabs.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/searchbox.js"/>"></script>
	<title>CiteSeerX Document Submissions</title>
	<meta name="keywords" content="" />
	<meta name="description" content="Submit documents to the CiteSeerX team." />

</head>

<body>
<!-- Begin header for the primary, secondary, tertiary, etc. pages, can be placed in one external file to be called by all pages. -->
<%@ include file="../../shared/topnav.jsp" %>

<div id="logo"> <!-- Contains logo div -->
  <a class="remove" href="<c:url value="/"/>" title="CiteSeerX Home page and Statistics"><img src="<c:url value="/images/CSxbetaw.gif"/>" alt="CiteSeerXbeta logo" /></a>
</div> <!-- End logo -->
 
<%@ include file="../../shared/searchbox.jsp" %>

<div id="page_wrapper" class="clearfix"> <!-- Contains all the divisions (div's) within the page -->
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li id="current"><a class="page_tabs remove" href="<c:url value="/submit"/>"><span>Submit</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->
   <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">Submit Documents to CiteSeer<sup>x</sup></em></span>Submit document URLs for CiteSeer<sup>X</sup> to index. We accept links to pages containing documents. Before submitting make sure your <a class="remove" href="http://www.robotstxt.org">robots.txt</a> allows the documents to be crawled by our bots.
   </div>
     <c:if test="${ error }">
       <p class="para4" id="submission_error">Error: <c:out value="${ errMsg }"/></p>
     </c:if>
	 <form id="submissionsform" action="<c:url value="/submit"/>" method="post">
      <fieldset id="submissions">
       <!--  <legend>Submissions</legend> -->
	<center>
	<table align="center">
	<tr>
	<td>
	<label for="e-mail" class="preField">E-mail:&nbsp;</label>
	</td>
	<td align="left">
        <input type="text" size="30" maxlength="90" 
               id="email" 
               name="email"
               value="<c:out value="${email}"/>" 
        />
        <span class="errMsg" id="email-E"></span>
	</td>
	</tr>
	<tr>
	<td>
        <label for="url" class="preField">Page URL:&nbsp;</label>
	</td>
	<td align="left">
        <input type="text" size="40" maxlength="90" 
               id="url" 
               name="url"
               value="<c:out value="${url}"/>" 
               class=""
        />
        <span class="errMsg" id="url-E"></span>
        </td>
        </tr>
        </table>
	</center>
      </fieldset>
      <div class="actions pushdown">
      <input class="button" type="submit" value="Submit Document URL" name="submit" title="Submit Document URL" />     
      </div>
     <div id="sub_content" class="clearfix">
     <p class="para4 parafirstletters para_book"><sup>*</sup>Publishers <a class="remove" href="http://romeo.eprints.org/publishers.html">policy</a> on self-archiving of your publications.</p>
     </div>
       <div><input type="hidden" name="rt" value="send" /></div>
     
     </form> 

<%@ include file="../footer.jsp" %>

</div> <!--  End page_wrapper -->

<script type="text/javascript">
<!--
if (window != top) 
top.location.href = location.href;
function sf(){}
// -->
</script>
</body>
</html>
