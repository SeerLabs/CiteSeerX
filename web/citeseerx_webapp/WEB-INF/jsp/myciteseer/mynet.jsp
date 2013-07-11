<%@ include file="/WEB-INF/jsp/myciteseer/IncludeTop.jsp" %>
<html>
<head><title><fmt:message key="title"/></title>
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
<h1>My Network</h1>
<p><h4>
You have <a href="readmessage.htm" target="_blank"><c:out value="${model.numberNewMsg}"/> new message(s)</a>.
</h4></p>
<h4>Here is a list of your friends: </h4>
<table width="50%" cellspacing="0" cellpadding="5" style="border: 1px dashed #000000;">
  <tr> 
      <td bordercolor="#000000"> <c:forEach items="${model.friends}" var="friend"> 
       <a href="profile.htm?friend=<c:out value="${friend.id}"/>"><c:out value="${friend.id}"/></a>&nbsp;&nbsp;&nbsp;</c:forEach> </td>
  </tr>
</table>
<br>
<h4>
To browse profiles or send messages, you'll need to first <a href="<c:url value="addFriend"/>">add someone to your network</a>.
</h4>
<br>
</body>
</html>

