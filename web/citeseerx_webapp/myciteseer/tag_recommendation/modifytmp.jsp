<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*" %>
<%
	String hello = "Good Morning";
	String name = request.getParameter("name");

	String p_first = "";
	String p_last = "";
	String p_email = "";
	String p_affil = "";

	Connection con = null;
	ResultSet rs = null;
	PreparedStatement ps = null;
	try {
		String mysqluser = "yasong";
		String mysqlpass = "";
		String url = "jdbc:mysql://localhost/citeseer";
		Class.forName("com.mysql.jdbc.Driver").newInstance();

		//String name = "yasong";
		//String password = "citeseerx";
		//String  name = request.getParameter("loginname");
		//String password = request.getParameter("loginpass");
		if (!name.equals("")) {
			con = DriverManager.getConnection(url, mysqluser
					.toLowerCase(), mysqlpass.toLowerCase());
			System.out.println("Connection Successful!");
			//userStruct user1=new userStruct();
			String sql = "select * from MyCiteSeerUser where username=?";
			ps = con.prepareStatement(sql);
			ps.setString(1, name);
			rs = ps.executeQuery();
			//while(rs.next())
			if (rs.next()) {
				p_first = rs.getString("firstname");
				p_last = rs.getString("lastname");
				p_email = rs.getString("email");
				p_affil = rs.getString("affiliation");
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
		//response.sendRedirect("error/loginFalse.jsp");
	}
%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE>Modify Your Profile --- <%=p_first%> <%=p_last%></TITLE>


</HEAD>


<BODY class="bg" topMargin=20 leftmargin="0" marginwidth="0"
	marginheight="0">

<form name="modify1" action="modify1.jsp" method="post">
<table width="320" border="0">
	<td width="320" valign="top" colspan=2><font class="strong">Modify
	Your Profile:</font></td>
	<td width="250">
	<tr>
		<td width="18%">User Name:*</td>
		<td><input name="loginname" type="text" class="text" size="20"
			value=<%=name%> DISABLEDfocus()></td>
	</tr>
	<tr>
		<td width="17%">Password:*</td>
		<td><input name="loginpass" type="password" class="text"
			size="20"></td>
	</tr>
	<tr>
		<td width="17%">Repeat Password:*</td>
		<td><input name="passverify" type="password" class="text"
			size="20"></td>
	</tr>
	<tr>
		<td width="18%">First Name:*</td>
		<td><input name="firstname" type="text" class="text"
			value=<%=p_first%> size="20"></td>
	</tr>
	<tr>
		<td width="18%">Last Name:*</td>
		<td><input name="lastname" type="text" class="text"
			value=<%=p_last%> size="20"></td>
	</tr>
	<tr>
		<td width="18%">Email:</td>
		<td><input name="email" type="text" class="text"
			value=<%=p_email%> size="20"></td>
	</tr>
	<tr>
		<td width="18%">Affiliation:</td>
		<td><input name="affiliation" type="text" class="text"
			value=<%=p_affil%> size="20"></td>
	</tr>
	<tr>
		<center>
		<td width="28%"><input name="Submit" type="submit" class="file"
			value="modify"></td>
		</center>
	</tr>


</table>
</form>

</BODY>
</HTML>
