<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="/spring" %>

<html>
<head><title><fmt:message key="title_sendmsg"/></title>
<style>
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
<h1><fmt:message key="heading_sendmsg"/></h1>
<form method="post">
  <table width="95%" bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5" style="border: 1px dashed #000000;">
    <tr> 
      <td alignment="right" width="9%">To:</td>
      <spring:bind path="sendMsg.messageTo"> 
      <td width="91%"> <input type="text" name="messageTo" value="<c:out value="${status.value}"/>"> </td>
      </spring:bind> </tr>
    <tr> <br>
      <br>
      <td alignment="right" width="9%">Message:</td>
      <spring:bind path="sendMsg.messageBody"> 
      <td width="91%"> <textarea name="messageBody" cols="80"></textarea>
         </td>
      </spring:bind> </tr>
  </table>
  <br>
  <input type="submit" alignment="center" value="Send this message">
</form>
<h4>
<a href="javascript:window.close();">I've changed my mind - cancel this message and close the window</a>.
</h4>
</body>
</html>
