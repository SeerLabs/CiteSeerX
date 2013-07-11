<%@ include file="/WEB-INF/jsp/include.jsp" %>
<html>
<head><title><fmt:message key="title_readmsg"/></title>
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
<h1><fmt:message key="heading_readmsg"/> for <c:out value="${model.selfId}"/></h1>

<table width="50%" cellspacing="0" cellpadding="5" style="border: 1px dashed #000000;">
 <c:forEach items="${model.messages}" var="msg"> 
    <tr bordercolor="#000000"><td  bordercolor="#000000">
      <b>From:</b>&nbsp;<c:out value="${msg.messageFrom}"/>
      <br>
      <b>Sent at:</b>&nbsp;<c:out value="${msg.messageTime}" />
      <br>
      <b>Message:</b>&nbsp;<c:out value="${msg.messageBody}"/><br>
      <br>
    </td></tr>
</c:forEach>
</table>
<h4>
<a href="javascript:window.close();">Close window</a>
</h4>
</body>
</html>

