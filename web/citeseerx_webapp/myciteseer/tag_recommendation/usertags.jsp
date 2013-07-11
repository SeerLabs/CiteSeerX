<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*,java.lang.*" %>
<%
	//String name=request.getParameter("name");
	String name = (String) session.getAttribute("username");
	int userid = Integer.parseInt((String) session
			.getAttribute("userid"));
	session.setAttribute("username", name);

	int[] u_did = new int[10000];
	int[] u_tagid = new int[10000];
	String[] u_tagname = new String[10000];
	int[] u_tagcount = new int[10000];
	int u_length = 0;

	Connection con = null;
	ResultSet rs = null;
	PreparedStatement ps = null;
	Statement sm = null;
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
			// select user profile
			String usertags = "select *, count(*) from tagmap where userid =? group by tagid";
			ps = con.prepareStatement(usertags);
			ps.setInt(1, userid);
			rs = ps.executeQuery();
			while (rs.next()) {
				u_did[u_length] = rs.getInt("did");
				u_tagid[u_length] = rs.getInt("tagid");
				u_tagcount[u_length] = rs.getInt("count(*)");
				u_length++;
			}
			// then get the tagnames for each tagid
			for (int i = 0; i < u_length; i++) {
				String tagnames = "select tagname from tags where id = ?";
				ps = con.prepareStatement(tagnames);
				ps.setInt(1, u_tagid[i]);
				rs = ps.executeQuery();
				if (rs.next()) {
					u_tagname[i] = rs.getString("tagname");
				}
			}

		}
	} catch (Exception e) {
		e.printStackTrace();
		//response.sendRedirect("error/loginFalse.jsp");
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>MyCiteSeer-User Tag List</title>
</head>

<body>
<br>
<a href="main.jsp">back to main</a>
<br>
<br>
<center>
<h1>Tag List by User <%=name%></h1>
</center>
<br>
<table border="0" align="center">
	<%
		for (int i = 0; i < u_length; i++) {
			String color = "black";
			String link = "papertags.jsp?tagid=" + u_tagid[i] + "&tagname="
					+ u_tagname[i];
			if (u_tagcount[i] >= 20) {
				color = "#000000";
			} else if (u_tagcount[i] >= 10) {
				color = "#333333";
			} else if (u_tagcount[i] >= 5) {
				color = "#666666";
			} else if (u_tagcount[i] >= 3) {
				color = "#999999";
			} else {
				color = "#bbbbbb";
			}
	%>
	<%
		if (i % 4 == 0) {
	%>
	<tr>
		<%
			}
				int tagsize = Math.max(3, Math.min(8, u_tagcount[i] / 2));
		%>
		<td><a href="<%=link%>"><font size="<%=tagsize%>"
			color="<%=color%>"><%=u_tagname[i]%> </font></a></td>
		<%
			if (i % 4 == 3) {
		%>
	</tr>
	<%
		}
	%>
	<%
		}
	%>
</table>
</body>
</html>
