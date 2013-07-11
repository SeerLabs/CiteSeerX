<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/xml; charset=UTF-8" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<rss version="2.0">
 <channel>
  <title><fmt:message key="app.name"/> <c:out value="${ feedTitle }"/></title>
  <link><c:out value="${ feedLink }" escapeXml="true"/></link>
  <description><fmt:message key="app.name"/> <c:out value="${ feedDesc }"/></description>
  <language>en-us</language>
  <pubDate><c:out value="${ feedDate }"/></pubDate>
  <generator>CiteSeerX RSS Generator</generator>
  <ttl>60</ttl>
  <c:forEach var="hit" items="${ hits }">
  <item>
   <title><c:out value="${ hit.title }"/></title>
   <pubDate><c:out value="${ hit.rfc822Time }"/></pubDate>
   <c:if test="${ hit.inCollection }">
   <link><c:url value="${ baseUrl }/viewdoc/summary"><c:param name="doi" value="${ hit.doi }"/></c:url></link>
   <description><c:out value="${ hit.abs }"/></description>
   <guid><c:out value="${ baseUrl }"/>/document/<c:out value="${ hit.doi }"/></guid>
   </c:if>
   <c:if test="${ ! hit.inCollection }">
   <link><c:url value="${ baseUrl }/showciting"><c:param name="cid" value="${ hit.cluster }"/></c:url></link>
   <guid><c:out value="${ baseUrl }"/>/cluster/<c:out value="${ hit.cluster }"/></guid>
   </c:if>
  </item>
  </c:forEach>
 </channel>
</rss>
