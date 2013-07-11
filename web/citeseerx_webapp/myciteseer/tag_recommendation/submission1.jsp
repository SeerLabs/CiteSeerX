<%@ page    language="java"    
	contentType="text/html"    
	import=" java.util.*,java.io.*,java.sql.*,java.text.*" %>

<%		
	Connection con = null;
	ResultSet rs = null;
	PreparedStatement ps = null;
	Statement sm = null;
	try{
		String mysqluser = "yasong";
	    	String mysqlpass = "";
		String url = "jdbc:mysql://localhost/citeseer";
		Class.forName("com.mysql.jdbc.Driver").newInstance();

		//String name = "yasong";
		//String password = "citeseerx";
                //String username = (String)request.getParameter("username");
		String username = (String)session.getAttribute("username");
		//out.println(username);
                String sub_url = request.getParameter("docurl");
		//out.println(sub_url);
                String description = request.getParameter("description");
		description = description.replace('\'', '\"');
		//out.println(description);
		String status = "SENT";

		java.util.Date date = new java.util.Date (System.currentTimeMillis());
		SimpleDateFormat dtgFormat = new SimpleDateFormat ("HHmmssMMddyyyy");
		String ts = dtgFormat.format (date);
		//out.println(ts);
		String ip = (String)session.getAttribute("ipaddress");
		//out.println(ip);
		String userid = (String)session.getAttribute("userid");
		//out.println("id:"+userid);
		String jobid =ip+"_"+userid+"_"+ts;
		//out.println(jobid);
		String depth = "3";
		if((!username.equals(""))){
		//	out.println("before");
			con = DriverManager.getConnection(url,mysqluser.toLowerCase(), mysqlpass.toLowerCase());
			System.out.println("Connection Successful!");
                        String submission = ("INSERT into submission " +
                       	"(userid,jobid,url,depth,timestamp,description,status) "+
                       	"values ('"+	userid				+"', '"+
                       	           	jobid				+"', '"+
					sub_url				+"', '"+
					depth				+"', '"+
					ts				+"', '"+
				    	description.toLowerCase()	+"', '"+
                       			status				+"')") ;


			sm = con.createStatement();
		//	out.println("after1");
			out.println("<br>");
			out.println(submission);
                        int r = sm.executeUpdate(submission);
			out.println("after2");
                        System.out.println("Insert Successful");
			//response.sendRedirect("main.jsp?name="+session.getAttribute("username"));
			response.sendRedirect("main.jsp");
			//userStruct user1=new userStruct();
			//while(rs.next())
		}
 		else{
                	response.sendRedirect("error/loginfalse.html");
               	}

	}catch(Exception e){
  		//	e.printStackTrace();
		out.println(e.getMessage());
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

