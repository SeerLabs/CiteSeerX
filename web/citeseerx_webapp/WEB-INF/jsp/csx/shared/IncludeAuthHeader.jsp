<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="../../shared/IncludeHeader.jsp" %>
<div id="main">
<%@ include file="../../shared/IncludeSearchBox.jsp" %>

<div id="viewHeader" class="viewAuth">
  <h2><c:out value="${ uauth.canname }"/> <font size="-2"><a href="../authmerge?aid=<c:out value="${ uauth.aid }" />">edit</a></font></h2> 
  <table id="authInfo" class="info">
    <%-- <tr><td class="title">Homepage</td>
      <td><c:if test="${ ! empty uauth.url }"><a href='<c:out value="${ uauth.url }"/>'><c:out value="${ uauth.url }"/></a></c:if>
          <c:if test="${ empty uauth.url }"><a href='<c:url value="${hpslink}"/>'>Not found. Submit a homepage</a></c:if></td></tr>
    <c:if test="${ ! empty uauth.affil }"> --%>
      <tr><td class="title">Affiliation</td>
        <td><c:out value="${ uauth.affil }"/></td>
      </tr></c:if>
    <c:if test="${ ! empty uauth.address }">
      <tr><td class="title">Address</td>
        <td><c:out value="${ uauth.address }"/></td>
      </tr></c:if>
    <c:if test="${ ! empty uauth.ndocs }">
      <tr><td class="title">Publications</td>
        <td><c:out value="${ uauth.ndocs }"/></td>
      </tr></c:if>
    <c:if test="${ ! empty uauth.hindex }">
      <tr><td class="title">H-index</td>
        <td><c:out value="${ uauth.hindex }"/></td>
      </tr></c:if>
    </table>

</div><%--viewHeader close div --%>
