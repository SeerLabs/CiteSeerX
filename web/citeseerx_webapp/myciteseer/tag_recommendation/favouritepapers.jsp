<%@ page
    language="java"
    import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*, java.io.*, java.sql.*, java.lang.*"        %>
<%
	//String name=request.getParameter("name");
	String name = (String)session.getAttribute("username");
	int userid = Integer.parseInt((String)session.getAttribute("userid"));
	session.setAttribute("username", name);
	
	String[] foldername = new String[10000];
	int folder_length = 0;

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
                //String  name = request.getParameter("loginname");
                //String password = request.getParameter("loginpass");
                if(!name.equals("")){
                        con = DriverManager.getConnection(url,mysqluser.toLowerCase(), mysqlpass.toLowerCase());
                        System.out.println("Connection Successful!");
			// select folders for each user
			String folders = "select folder from favouritepapers where userid = ? group by folder";
			ps = con.prepareStatement(folders);
			ps.setInt(1, userid);
			rs = ps.executeQuery();
			while(rs.next()){
				foldername[folder_length++] = rs.getString("folder");
			}
                }
        }catch(Exception e){
                e.printStackTrace();
                        //response.sendRedirect("error/loginFalse.jsp");
        }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>MyCiteSeer- Paper Collections</title>
</head>

<body>
	<br><a href ="main.jsp">back to main</a>
	<br>
	<br>
	<center><h1>Paper Collection by User <%=userid%>:<%=name%></h1></center>
	<br>

	<% for(int f = 0; f < folder_length; f++) { 

		String link = "favouritepapersbytopic.jsp?foldername="+foldername[f];
	%>
		<IMG height=34 src="images/folder.jpg" width=36>
		<a href = "<%=link%>"><h2><%=foldername[f]%></h2></a>
	<%} %>

<% String create = "newfolder.jsp"; %>
<form method="post" name = "createfolder" action="<%=create%>">
  <table border="0">
    <tr>
      <td>Create a new folder:</td>
      <td>
        <input type="text" name="newfolder">
      </td>
      <td>
        <input type="submit" name="Submit" value="Submit">
      </td>
    </tr>
  </table>
</form>


</body>
</html>
