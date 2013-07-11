<%@ page language="java" contentType="text/html"
	import="java.util.*,java.io.*,java.sql.*" %>

<%
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
		String name = request.getParameter("loginname");
		String password = request.getParameter("loginpass");
		session.setAttribute("username", name);
		if (!name.equals("")) {
			con = DriverManager.getConnection(url, mysqluser
					.toLowerCase(), mysqlpass.toLowerCase());
			System.out.println("Connection Successful!");
			//userStruct user1=new userStruct();
			String sql = "select * from MyCiteSeerUser where username=? and password=?";
			ps = con.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, password);
			rs = ps.executeQuery();
			//while(rs.next())
			if (rs.next()) {
				String n = rs.getString("firstname");
				String p = rs.getString("lastname");
				System.out.println(n + "\t" + p);
				/*
				user1.userId = rs.getString("user_id");
				user1.userPass = rs.getString("user_pass");
				user1.userName = rs.getString("user_name");
				user1.userAccess = new Integer(rs.getInt("user_access"));
				user1.unitid = new Integer(rs.getInt("unitid"));
				session = request.getSession();
					userStruct user = (userStruct)session.getAttribute("user");
				session.setAttribute("user", user1);
				session.setMaxInactiveInterval(36000);*/
				//				response.sendRedirect("main.jsp?name="+request.getParameter("loginname"));
				response.sendRedirect("main.jsp");

			} else {
				response.sendRedirect("error/loginfalse.html");
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
		//response.sendRedirect("error/loginFalse.jsp");
	}
	//finally{
	//	rs.close();
	//ps.close();
	//con.close();
	//}
	//	else{
	//response.sendRedirect("error/loginFalse.jsp");
	//}
%>
