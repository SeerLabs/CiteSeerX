<%@ page
    language="java"
    import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*, java.io.*, java.sql.*, java.lang.*"        %>
<%
	//String name=request.getParameter("name");
	String name = (String)session.getAttribute("username");
	int userid = Integer.parseInt((String)session.getAttribute("userid"));
	session.setAttribute("username", name);
	
	String[] authorname = new String[10000];
	int author_length = 0;

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
			// select authors for each user
 			String authors = "select authorname from favouriteauthors where userid = ? group by authorname";
                        ps = con.prepareStatement(authors);
                        ps.setInt(1, userid);
                        rs = ps.executeQuery();
                        while(rs.next()){
                                authorname[author_length++] = rs.getString("authorname");
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
<title>MyCiteSeer- Favourite</title>
</head>

<body>
	<br><a href ="main.jsp">back to main</a>
	<br>
	<br>
	<center><h1>Favourite Authors of User <%=userid%>:<%=name%></h1></center>
	<br>

	<% for(int f = 0; f < author_length; f++) { 
		String templink = authorname[f].replace(' ', '+');
		String link = "http://citeseer.ist.psu.edu/cis?q="+templink+"&cs=1";
	%>
		<IMG height=34 src="images/folder.jpg" width=36>
		<a href = "<%=link%>"><h2><%=authorname[f]%></h2></a>
	<%} %>

<% String create = "newauthor.jsp"; %>
<form method="post" name = "newauthor" action="<%=create%>">
  <table border="0">
    <tr>
      <td>Add a new favourite author:</td>
      <td>
        <input type="text" name="newauthor">
      </td>
      <td>
        <input type="submit" name="Submit" value="Submit">
      </td>
    </tr>
  </table>
</form>


</body>
</html>
