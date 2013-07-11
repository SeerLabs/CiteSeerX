<%@ page language="java" contentType="text/html"
	import="java.util.*,java.io.*,java.sql.*" %>

<%
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
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		//String username = request.getParameter("loginname");
		String username = (String) session.getAttribute("username");
		String password = request.getParameter("loginpass");
		String repass = request.getParameter("passverify");
		String email = request.getParameter("email");
		String affiliation = request.getParameter("affiliation");

		session.setAttribute("username", username);
		if (!password.equals(repass)) { // passwords must match
			response.sendRedirect("error/loginfalse.html");
		}
		//if(username.exist()){	// user name must be unique
		//}

		if ((!username.equals(""))) {
			con = DriverManager.getConnection(url, mysqluser
					.toLowerCase(), mysqlpass.toLowerCase());
			System.out.println("Connection Successful!");
			String update = ("UPDATE MyCiteSeerUser SET "
					+ "firstname = '" + firstname.toLowerCase() + "', "
					+ "lastname ='" + lastname.toLowerCase() + "', "
					+ "email ='" + email.toLowerCase() + "', "
					+ "password ='" + password + "', "
					+ "affiliation ='" + affiliation.toLowerCase()
					+ "' WHERE username ='" + username + "'");

			sm = con.createStatement();
			int r = sm.executeUpdate(update);
			System.out.println("Update Successful");
			//response.sendRedirect("main.jsp?name="+session.getAttribute("username"));
			response.sendRedirect("main.jsp");
			//userStruct user1=new userStruct();
			//while(rs.next())
		} else {
			response.sendRedirect("error/loginfalse.html");
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
