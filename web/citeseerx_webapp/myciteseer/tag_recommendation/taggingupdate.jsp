<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*,java.lang.*" %>
<%
	//String name=request.getParameter("name");
	String name = (String) session.getAttribute("username");
	int userid = Integer.parseInt((String) request
			.getParameter("userid"));
	int newtagflag = Integer.parseInt((String) request
			.getParameter("newtag"));
	int did = Integer.parseInt((String) request.getParameter("did"));
	String oldtag = request.getParameter("oldtag");
	oldtag = oldtag.toLowerCase();
	oldtag = oldtag.substring(0, Math.min(40, oldtag.length())); //we limit the size of the string to be 20
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
	//		      2. update the count in tags table
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

		if (newtagflag == 0) { // This is an old tag
			// con = DriverManager.getConnection(url,mysqluser.toLowerCase(), mysqlpass.toLowerCase());
			// System.out.println("Connection Successful!");
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

			// then update into the tagmap
			String updatetagmap = ("INSERT into tagmap "
					+ "(did, tagid, userid) " + "values ('" + did
					+ "', '" + tagid + "', '" + userid + "')");
			sm = con.createStatement();
			int r = sm.executeUpdate(updatetagmap);
			System.out.println("Insert Successful");

			// then update the tags table count = count+1
			String updatetags = ("UPDATE tags set count=count+1 where id=" + tagid);
			sm = con.createStatement();
			r = sm.executeUpdate(updatetags);
			System.out.println("Update Successful");
			//response.sendRedirect("main.jsp?name="+request.getParameter("loginname"));
			//userStruct user1=new userStruct();
			//while(rs.next())
		} else if (newtagflag == 1) { // This is a new tag
			// first we need to insert into the tags table
			// then get the new tag id
			// then insert into the tagmap

			// first check whether it is an existing tag for other papers
			indicator = 0; // 0 means a TRUE new tag

			String findnewtag = "select id from tags where tagname =?";
			ps = con.prepareStatement(findnewtag);
			ps.setString(1, oldtag);
			rs = ps.executeQuery();
			//while(rs.next())
			if (rs.next()) {
				indicator = 1;
				tagid = rs.getInt("id");
			}

			if (indicator == 1) { // it is not a brand new tag, we only need to update the count
				String updateoldtag = ("UPDATE tags set count=count+1 where tagname='"
						+ oldtag + "'");
				sm = con.createStatement();
				int r = sm.executeUpdate(updateoldtag);
				System.out.println("Insert Successful");

				String updatetagmap = ("INSERT into tagmap "
						+ "(did, tagid, userid) " + "values ('" + did
						+ "', '" + tagid + "', '" + userid + "')");
				sm = con.createStatement();
				r = sm.executeUpdate(updatetagmap);
				System.out.println("Insert Successful");

			}

			else { // This is a TRUE new tag
				/*	 	String insertnewtag = ("INSERT into tags " +
				                                    "(tagname, count) "+
				                                    "values ('"+oldtag+"', '"+"1"+"')") ;
				                    sm = con.createStatement();
				                    int r = sm.executeUpdate(insertnewtag);
				                    System.out.println("Insert Successful");



				             	//first get the tagid based on tag name
				    	        //int tagid = 0;
				            	String findtagid = "select id from tags where tagname =?";
				                ps=con.prepareStatement(findtagid);
				    	        ps.setString(1,oldtag);
				            	rs = ps.executeQuery();
				                    //while(rs.next())
				            	if(rs.next()){
				                    	tagid = rs.getInt("id");
				                }

						String updatetagmap = ("INSERT into tagmap " +
				                                    "(did, tagid, userid) "+
				                                    "values ('"+did+"', '"+tagid+"', '"+userid+"')") ;
				                    sm = con.createStatement();
				                    r = sm.executeUpdate(updatetagmap);
				                    System.out.println("Insert Successful"); */
				response.sendRedirect("taggingsuggestion.jsp?tagname="
						+ oldtag + "&did=" + did + "&userid=" + userid);
			}

		} else {
			response.sendRedirect("error/loginfalse.html");
		}

	} catch (Exception e) {
		e.printStackTrace();
		//response.sendRedirect("error/loginFalse.jsp");
	}
	//finally{
	//      rs.close();
	//ps.close();
	//con.close();
	//}
	//      else{
	//response.sendRedirect("error/loginFalse.jsp");
	//}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>MyCiteSeer- Tagging Update (Temporary Process)</title>
</head>

<body>
<br>
<br>
<br>
<center>
<h1>
<%
	if (newtagflag == 0) {
%> <!-- Username: [<%=name%>] 
			newtag:[<%=oldtag%>]
			newtagflag:[<%=newtagflag%>]
			userid:[<%=userid%>]
			did:[<%=did%>]
			updatesuccessful:[<%=updatesuccessful%>]
			tagid:[<%=tagid%>]
			indicator:[<%=indicator%>] --> Tagging Successful! <%
 	}
 %> <%
 	if (newtagflag == 1) {
 %>
something here <%
 	}
 %>
</h1>
</center>
<br>
</body>
</html>
