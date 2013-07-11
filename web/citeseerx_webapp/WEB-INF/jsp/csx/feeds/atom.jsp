<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/xml; charset=UTF-8" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<feed xmlns="http://www.w3.org/2005/Atom">
 <id><c:out value="${ feedLink }" escapeXml="true"/></id>
 <title type="html"><fmt:message key="app.name"/> <c:out value="${ feedTitle }"/></title>
 <updated><c:out value="${ feedDate }"/></updated>
 <author>
  <name><fmt:message key="app.name"/></name>
  <uri><c:out value="${ baseUrl }"/></uri>
 </author>
 <link rel="self" href="<c:out value="${ feedLink }" escapeXml="true"/>"/>
 <generator><fmt:message key="app.name"/> Atom Generator</generator>
 <c:forEach var="hit" items="${ hits }">
 <entry>
  <title type="html"><c:out value="${ hit.title }"/></title>
  <updated><c:out value="${ hit.rfc3339Time }"/></updated>
  
  <author>
   <name><c:out value="${ hit.authors }"/></name>
  </author>
  
  <c:if test="${ hit.inCollection }">
  <link rel="alternate" href="<c:url value="${ baseUrl }/viewdoc/summary"><c:param name="doi" value="${ hit.doi }"/></c:url>"/>
  <summary type="html"><c:out value="${ hit.abs }"/></summary>
  <id><c:out value="${ baseUrl }"/>/document/<c:out value="${ hit.doi }"/></id>
  </c:if>
  <c:if test="${ ! hit.inCollection }">
  <link rel="alternate" href="<c:url value="${ baseUrl }/showciting"><c:param name="cid" value="${ hit.cluster }"/></c:url>"/>
  <id><c:out value="${ baseUrl }"/>/cluster/<c:out value="${ hit.cluster }"/></id>
  </c:if>
 </entry>
 </c:forEach>
</feed>
