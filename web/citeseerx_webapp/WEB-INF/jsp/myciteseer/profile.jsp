<%@ include file="/WEB-INF/jsp/include.jsp" %>
<html>
<head><title><fmt:message key="title_profile"/></title>
<!-- <LINK href="style.css" rel="stylesheet" type="text/css"> -->
<style type="text/css">
<!--
body,td,th {
        font-family: Georgia;
        font-size: 12pt;
}
a:link {
        color: #FF0000;
        text-decoration: none;
}
a:visited {
        text-decoration: none;
        color: #C21B1F;
}
a:hover {
        text-decoration: none;
        color: #FFFFFF;
        background-color: #AC6260;
        border-top-color: #CC6633;
}
a:active {
        text-decoration: none;
        color: #FF9900;
}
-->

</style>
</head>

<body>
<h1><fmt:message key="heading_profile"/><c:out value="${model.friendId}"/></h1>

<h4><c:out value="${model.selfId}"/> is allowed to view <c:out value="${model.friendId}"/>'s collections as follows: </h4>
<table width="50%" cellspacing="0" cellpadding="5" style="border: 1px dashed #000000;">
 <c:forEach items="${model.collection}" var="col"> 
    <tr><td  bordercolor="#000000">
      <b>Friends: </b>
      <c:forEach items="${model.friends}" var="friends">
          <c:out value="${friends.id}"/>&nbsp;&nbsp;&nbsp;
      </c:forEach>
      <br><br>
      <b>Papers: </b><c:out value="${col.papers}"/><br><br>
      <b>Tags: </b><c:out value="${col.tags}"/><br>
    </td></tr>
</c:forEach>
</table>
<br>
<h4>
You can <a href="<c:url value="sendMsg.htm"/>?msgto=<c:out value="${model.friendId}"/>" target="_blank">send a message to <c:out value="${model.friendId}"/></a> or just <a href="<c:url value="/hello.htm"/>">return to your homepage.</a>
</h4>
</body>
</html>

