<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*" %>
<%
	//String name=request.getParameter("name");
	String name = (String) session.getAttribute("username");
	session.setAttribute("username", name);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>MyCiteSeer-Submit New Document Link</title>
</head>

<body>
<br>
<br>
<br>
<center>
<h1>Submission.html</h1>
</center>
<br>
<table width="539" border="1" align="center" cellpadding="0"
	cellspacing="0" bordercolor="#FFFFFF" bgcolor="#FFFFFF">
	<tr>
		<td width="535" bordercolor="#000000" bgcolor="#FFFFFF">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td colspan="5">
				<form name="submit1" action="submission1.jsp" method="post">

				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td colspan="2">
						<div align="center">Username: <br>
						<input name="username" type="text" class="text" value=<%=name%>
							size="10" DISABLED></div>
						</td>
					</tr>
					<tr>
						<td colspan="2">
						<div align="center">Document URL: <br>
						<input name="docurl" type="text" class="text" size="40"focus()>
						</div>
						</td>
					</tr>
					<tr>
						<td colspan="2">
						<div align="center">Description:<br>
						<textarea name="description" cols=40 rows=6></textarea></div>
						</td>
					</tr>
					<tr>
						<br>
						<br>
						<td colspan="2">
						<div align="center"><input name="Submit" type="submit"
							class="file" value="submit"></div>
						</td>
					</tr>
				</table>
				</form>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</body>
</html>
