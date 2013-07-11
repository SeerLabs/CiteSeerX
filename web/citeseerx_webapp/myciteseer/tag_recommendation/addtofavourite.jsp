<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*,java.lang.*" %>
<%
	//String name=request.getParameter("name");
	String name = (String) session.getAttribute("username");
	int userid = Integer.parseInt((String) request
			.getParameter("userid"));
	int did = Integer.parseInt((String) request.getParameter("did"));
	//String folder  = "machine learning";
	String folder = request.getParameter("foldername");
	//String newtag  = request.getParameter("newtag");
	session.setAttribute("username", name);
	int flag = 0;

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

		if (userid > 0) { // valid user id
			System.out.println("Connection Successful!");

			String updatefavourite = ("INSERT into favouritepapers "
					+ "(did, userid, folder) " + "values ('" + did
					+ "', '" + userid + "', '" + folder + "')");
			sm = con.createStatement();
			int r = sm.executeUpdate(updatefavourite);
			System.out.println("Insert Successful");
			flag = 1;
			response.sendRedirect("taggingthepaper.jsp?did=" + did
					+ "&userid=" + userid);
		} else {
			response.sendRedirect("error/loginfalse.html");
		}

	} catch (Exception e) {
		e.printStackTrace();
	}
%>
