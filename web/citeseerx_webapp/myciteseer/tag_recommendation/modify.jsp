<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*" %>
<%
	String hello = "Good Morning";
	//String name=request.getParameter("name");
	String name = (String) session.getAttribute("username");
	session.setAttribute("username", name);

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
<DIV align=center>
<TABLE class=outter cellSpacing=0 cellPadding=0 width=600 border=0>
	<TBODY>
		<TR>
			<TD><IMG height=4 src="background/lefttop.gif" width=6></TD>
			<TD background=background/top.gif colSpan=3><IMG height=4
				src="background/top.gif" width=1></TD>
			<TD><IMG height=4 src="background/righttop.gif"></TD>
		</TR>
		<TR>
			<TD vAlign=top background=background/lwing.gif rowSpan=3><IMG
				height=18 src="background/left.gif" width=6></TD>
			<TD vAlign=top background=background/titlebg.gif width="16">
			<DIV><img src="background/logo.gif" width="16" height="16"
				border="0"></DIV>
			</TD>
			<TD vAlign=top noWrap align=center width="100%"
				background=background/titlebg.gif><SPAN
				style="BACKGROUND-COLOR: #cecece"><FONT class=strong>MyCiteSeer</font></SPAN></TD>
			<TD><IMG height=18 src="background/right_b2.gif" width=18
				border="0"></TD>
			<TD vAlign=top background=background/rwing.gif rowSpan=3><IMG
				height=18 src="background/right.gif" width=7></TD>
		</TR>
		<TR>
			<TD background=background/hr.gif colSpan=3><IMG height=2
				src="background/hr.gif" width=1></TD>
		</TR>
		<TR>
			<TD colSpan=3 align="center"><img src="images/citeseer3.gif"
				width="400" height="71"> <br>
			<span class=iframestyle style="OVERFLOW: visible; HEIGHT: 100%">
			<table width="400" border="0" cellpadding="5" cellspacing="1">
				<tr bgcolor="#999999">
					<td colspan="2"><font class="strongw"><%=hello%>,
					welcome to use myciteseer</font></td>
				</tr>
				<tr>
					<td align="center" colspan="2" bgcolor="#e0e0e0">
					<form name="modify1" action="modify1.jsp" method="post">
					<table width="320" border="0">
						<td width="320" valign="top" colspan=2><font class="strong">Modify
						Your Profile:</font></td>
						<td width="250">
						<tr>
							<td width="18%">User Name:*</td>
							<td><input name="loginname" type="text" class="text"
								size="20" value=<%=name%> DISABLEDfocus()></td>
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
								value="<%=p_affil%>" size="20"></td>
						</tr>
						<tr>
							<center>
							<td width="28%"><input name="Submit" type="submit"
								class="file" value="modify"></td>
							</center>
						</tr>

						<tr>
							<td valign="top" colspan="2">
							<hr size="1">
						</tr>

					</table>
					</form>
					</td>
				</tr>
			</table>
			</span>



			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td background="background/hr.gif"><img
						src="background/hr.gif" width="1" height="2"></td>
				</tr>

			</table>
			</TD>
		</TR>
		<TR>
			<TD><IMG height=7 src="background/leftbottom.gif" width=6></TD>
			<TD background=background/bottom.gif colSpan=3><IMG height=7
				src="background/bottom.gif" width=1></TD>
			<TD><IMG height=7 src="background/rightbottom.gif" width=7></TD>
		</TR>
	</TBODY>
</TABLE>
<BR>
<TABLE cellSpacing=1 cellPadding=2 width="300" border=0>
</TABLE>
</DIV>

</BODY>
</HTML>
