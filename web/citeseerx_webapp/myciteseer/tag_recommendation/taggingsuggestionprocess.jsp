<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*,java.lang.*" %>
<%
	//String name=request.getParameter("name");
	String name = (String) session.getAttribute("username");
	int userid = Integer.parseInt((String) request
			.getParameter("userid"));
	int did = Integer.parseInt((String) request.getParameter("did"));
	int flag = Integer.parseInt((String) request.getParameter("flag"));
	String oldtag = request.getParameter("tagname");
	oldtag = oldtag.toLowerCase();
	//String newtag  = request.getParameter("newtag");
	session.setAttribute("username", name);
	int[] tag_did = new int[10000];
	String[] tag_papername = new String[10000];
	int tag_length = 0;
	int updatesuccessful = 0;
	int tagid = 0;
	int indicator = 0;
	// We should deal with existing tags and new tags
	// For existing tags: 1. insert into tagmap
	//                    2. update the count in tags table
	// For new tags:      2. insert new tag into the tags table
	// Note: tags can have the same name, we we retrieve it
	// at alltaglist.jsp, simply use "group by".

	Connection con = null;
	ResultSet rs = null;
	PreparedStatement ps = null;
	Statement sm = null;
	try {
		String mysqluser = "yasong";
		String mysqlpass = "";
		String url = "jdbc:mysql://localhost/citeseer";
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		con = DriverManager.getConnection(url, mysqluser.toLowerCase(),
				mysqlpass.toLowerCase());
		if (flag == 1) { // this is a new tag

			// now let's insert whatever into the database
			String insertnewtag = ("INSERT into tags "
					+ "(tagname, count) " + "values ('" + oldtag
					+ "', '" + "1" + "')");
			sm = con.createStatement();
			int r = sm.executeUpdate(insertnewtag);
			System.out.println("Insert Successful");

			//first get the tagid based on tag name
			//int tagid = 0;
			String findtagid = "select id from tags where tagname =?";
			ps = con.prepareStatement(findtagid);
			ps.setString(1, oldtag);
			rs = ps.executeQuery();
			//while(rs.next())
			if (rs.next()) {
				tagid = rs.getInt("id");
			}

			String updatetagmap = ("INSERT into tagmap "
					+ "(did, tagid, userid) " + "values ('" + did
					+ "', '" + tagid + "', '" + userid + "')");
			sm = con.createStatement();
			r = sm.executeUpdate(updatetagmap);
			System.out.println("Insert Successful");
		} else { // the user follows our suggestion, this is not a new tag
			String updateoldtag = ("UPDATE tags set count=count+1 where tagname='"
					+ oldtag + "'");
			sm = con.createStatement();
			int r = sm.executeUpdate(updateoldtag);
			System.out.println("Insert Successful");
			String findtagid = "select id from tags where tagname =?";
			ps = con.prepareStatement(findtagid);
			ps.setString(1, oldtag);
			rs = ps.executeQuery();
			//while(rs.next())
			if (rs.next()) {
				tagid = rs.getInt("id");
			}

			String updatetagmap = ("INSERT into tagmap "
					+ "(did, tagid, userid) " + "values ('" + did
					+ "', '" + tagid + "', '" + userid + "')");
			sm = con.createStatement();
			r = sm.executeUpdate(updatetagmap);
			System.out.println("Insert Successful");

		}
	} catch (Exception e) {
		e.printStackTrace();
		//response.sendRedirect("error/loginFalse.jsp");
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>MyCiteSeer- Tagging Suggestions (Temporary Process)</title>
</head>

<body>
<br>
<br>
<br>
<center>
<h1>Username: [<%=name%>] userid:[<%=userid%>] did:[<%=did%>]
tagname:[<%=oldtag%>]</h1>
</center>
<br>
</body>
</html>
