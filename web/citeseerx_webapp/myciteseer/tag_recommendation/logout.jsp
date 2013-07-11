<%@ page
    language="java"
 	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*, java.io.*, java.sql.*"
	%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML><HEAD><TITLE>MyCiteSeer --- Louout</TITLE>


</HEAD>

<body>

<%if (session.isNew()==true)
response.sendRedirect(response.encodeRedirectURL("login.html"));%>
</body>
<HTML>

<%session.invalidate();%>
<h4> You were being Logged out, click OK to return to login.html.</h4> <br>
	<form name="logout" action="login.html" method = "post">
		<input name = "submit" type = "submit" class = "file" value = "OK">
	</form>
</body>
<HTML>
