<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*" %>
<%
	//String name=request.getParameter("name");
	String name = (String) session.getAttribute("username");
	session.setAttribute("username", name);

	int userid = Integer.parseInt((String) session
			.getAttribute("userid"));
	int did = Integer.parseInt(request.getParameter("did"));

	String[] doc_tags = new String[10000];
	int[] doc_tagid = new int[10000];
	String[] doc_tagname = new String[10000];
	int[] doc_tagcount = new int[10000];
	int doc_length = 0;

	String[] foldername = new String[10000];
	int folder_length = 0;

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

			String folders = "select folder from favouritepapers where userid = ? group by folder";
			ps = con.prepareStatement(folders);
			ps.setInt(1, userid);
			rs = ps.executeQuery();
			while (rs.next()) {
				foldername[folder_length++] = rs.getString("folder");
			}

			String papertags = "select tagid from tagmap where did = ? group by tagid";
			ps = con.prepareStatement(papertags);
			ps.setInt(1, did);
			rs = ps.executeQuery();
			while (rs.next()) {
				doc_tagid[doc_length] = rs.getInt("tagid");
				doc_length++;
			}
			// then get the tagnames for each tagid
			for (int i = 0; i < doc_length; i++) {
				String tagnames = "select tagname, count from tags where id = ?";
				ps = con.prepareStatement(tagnames);
				ps.setInt(1, doc_tagid[i]);
				rs = ps.executeQuery();
				if (rs.next()) {
					doc_tagname[i] = rs.getString("tagname");
					doc_tagcount[i] = rs.getInt("count");
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
<title>MyCiteSeer-Tagging the Paper</title>
</head>

<body>
<center>
<h1>Tagging the paper <%=did%> by user <%=name%></h1>
</center>
<%
	String addtofavourite = "addtofavourite.jsp?userid=" + userid
			+ "&did=" + did;
%>
<form method="post" action="<%=addtofavourite%>">Add this paper
to your collections! <select name="foldername">
	<%
		for (int i = 0; i < folder_length; i++) {
	%>
	<option><%=foldername[i]%></option>
	<%
		}
	%>
</select> <input type="submit" name="Submit3" value="Add"></form>
<p>&nbsp;</p>
<p>Existing tags:</p>
<table border="1">
	<tr>

		<%
			for (int i = 0; i < doc_length; i++) {
				String color = "black";
				if (doc_tagcount[i] >= 3) {
					color = "red";
				}
		%>
		<td><font size="<%=doc_tagcount[i]%>" color="<%=color%>"><%=doc_tagname[i]%>
		</font></td>
		<%
			}
		%>

	</tr>
</table>

<%
	String update = "taggingupdate.jsp?newtag=0&userid=" + userid
			+ "&did=" + did;
%>
<form name="tagging" method="post" action="<%=update%>">
<table border="0">
	<tr>
		<td>Use An Existing Tag:</td>
		<td><select name="oldtag">
			<%
				for (int i = 0; i < doc_length; i++) {
			%>
			<option><%=doc_tagname[i]%></option>
			<%
				}
			%>
		</select></td>
		<td><input type="submit" name="Submit" value="Submit"></td>
	</tr>
</table>
</form>
<p>Or</p>

<%
	String update1 = "taggingupdate.jsp?newtag=1&userid=" + userid
			+ "&did=" + did;
%>

<form method="post" name="tagging1" action="<%=update1%>">
<table border="0">
	<tr>
		<td>Create Your New Tag:</td>
		<td><input type="text" name="oldtag"></td>
		<td><input type="submit" name="Submit2" value="Submit"></td>
	</tr>
</table>
</form>

</body>
</html>
