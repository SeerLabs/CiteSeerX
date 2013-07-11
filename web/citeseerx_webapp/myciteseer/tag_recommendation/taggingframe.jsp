<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*" %>
<%
	//String name=request.getParameter("name");
	String name = (String) session.getAttribute("username");
	session.setAttribute("username", name);

	int userid = Integer.parseInt((String) session
			.getAttribute("userid"));
	int did = Integer.parseInt(request.getParameter("did"));
	//String did = request.getParameter("did");

	String[] doc_tags = new String[10000];
	int[] doc_tagid = new int[10000];
	String[] doc_tagname = new String[10000];
	int doc_length = 0;

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
				String tagnames = "select tagname from tags where id = ?";
				ps = con.prepareStatement(tagnames);
				ps.setInt(1, doc_tagid[i]);
				rs = ps.executeQuery();
				if (rs.next()) {
					doc_tagname[i] = rs.getString("tagname");
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

<frameset rows="350,*" frameborder="NO" border="0" framespacing="0">
	<%
		String taglink = "taggingthepaper.jsp?did=" + did + "&userid="
				+ userid;
	%>
	<frame name="topFrame" scrolling="YES" noresize src="<%=taglink%>">
	<%
		String citeseerlink = "http://citeseer.ist.psu.edu/" + did
				+ ".html";
	%>
	<frame name="mainFrame" src="<%=citeseerlink%>">
</frameset>
<noframes>
<body bgcolor="#FFFFFF">

</body>
</noframes>
</html>
