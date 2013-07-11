<%@page contentType="text/html" import="java.util.*" %>

<html>
<body>
<p>&nbsp;</p>
<div align="center">
<center>
<table border="0" cellpadding="0" cellspacing="0" width="460" bgcolor="#EEFFCA">
<tr>
<td width="100%"><font size="6" color="#008000">&nbsp;Date Example</font></td>
</tr>
<tr>
<td width="100%"><b>&nbsp;Current Date and time is:&nbsp; <font color="#FF0000">
<% out.println( "Evaluating date now" );
    java.util.Date date = new java.util.Date();%>
</font></b></td>
</tr>
</table>
</center>
</div>
</body>
</html>
