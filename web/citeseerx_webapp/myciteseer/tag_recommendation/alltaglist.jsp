<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*" %>
<%
	//String name=request.getParameter("name");
	String name = (String) session.getAttribute("username");
	session.setAttribute("username", name);

	String[] t_tags = new String[10000];
	int[] t_tagid = new int[10000];
	int[] t_counts = new int[10000];
	int t_length = 0;

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
			String tags = "select * from tags group by tagname ORDER BY RAND()";
			sm = con.createStatement();
			rs = sm.executeQuery(tags);
			while (rs.next()) {
				t_tagid[t_length] = rs.getInt("id");
				t_tags[t_length] = rs.getString("tagname");
				t_counts[t_length] = rs.getInt("count");
				t_length++;
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
<title>MyCiteSeer-All Tag List</title>
</head>

<body>
<br>
<a href="main.jsp">back to main</a>
<br>
<br>
<center>
<h1>Top 100 Tag List in MyCiteSeer</h1>
</center>
<br>
<table border="0" align="center">
	<%
		for (int i = 0; i < t_length; i++) {
			String color = "black";
			String link = "papertags.jsp?tagid=" + t_tagid[i] + "&tagname="
					+ t_tags[i];
			if (t_counts[i] >= 20) {
				color = "#000000";
			} else if (t_counts[i] >= 10) {
				color = "#333333";
			} else if (t_counts[i] >= 5) {
				color = "#666666";
			} else if (t_counts[i] >= 3) {
				color = "#999999";
			} else {
				color = "#bbbbbb";
			}
	%>
	<%
		if (i % 3 == 0) {
	%>
	<tr>
		<%
			}
				int tagsize = Math.max(3, Math.min(8, t_counts[i] / 2));
		%>
		<td><a href="<%=link%>"><font size="<%=tagsize%>"
			color="<%=color%>"> <%=t_tags[i]%> </font></a></td>
		<%
			if (i % 3 == 2) {
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
