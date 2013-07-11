<%@ page
    language="java"
 	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*, java.io.*, java.sql.*"
	%>
<%
	//if (session.isNew()==true) {
		//session.invalidate();
	//	response.sendRedirect("login.html");
	//} 
	String hello="Hello";
	//Date today=new Date();
	//int hours=today.getHours();
	//if (hours>=0&&hours<12){hello="Good Evening";}
	//else{hello="Good Morning";}
	//String name=request.getParameter("name");
	String name = (String)session.getAttribute("username");
	String IPAddr = (String)(request.getRemoteAddr());
	if(name.equals("")){
		response.sendRedirect("login.html");
	}
	session.setAttribute("username",name);
	session.setAttribute("ipaddress", IPAddr);
	//session.setMaxInactiveInterval(300);  //set the session expire time to be 5 mins
	
	//if(request.isRequestedSessionIdValid()){
	//	response.sendRedirect("error/loginfalse.html");
	//}

	String p_first = "";
	String p_last = "";
	String p_email = "";
	String p_affil = "";
	int    p_id = 0;
	String[] p_paperurl = new String[100];
	String[] p_description = new String[100];
	String[] p_status = new String[100];
	int p_sublength = 0;

       	Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
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
                        //userStruct user1=new userStruct();
			// select user profile
                        String  sql="select * from MyCiteSeerUser where username=?";
                	ps=con.prepareStatement(sql);
                	ps.setString(1,name);
                	rs = ps.executeQuery();
                        //while(rs.next())
                	if(rs.next())
			{
				p_id = rs.getInt("ID");
				p_first = rs.getString("firstname");
				p_last = rs.getString("lastname");
				p_email = rs.getString("email");
				p_affil = rs.getString("affiliation");
			}

			sql = "select * from submission where username=?";
			ps=con.prepareStatement(sql);
			ps.setString(1,name);
			rs = ps.executeQuery();
			while(rs.next()){
				p_paperurl[p_sublength] = rs.getString("url");
				p_description[p_sublength] = rs.getString("description");
				p_status[p_sublength] = rs.getString("status");
				p_sublength ++;	
			}
		}
		//session.setAttribute("userid",p_id+"");
	}catch(Exception e){
                e.printStackTrace();
                        //response.sendRedirect("error/loginFalse.jsp");
        }

	session.setAttribute("userid", p_id+"");

%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML><HEAD><TITLE>MyCiteSeer --- <%=p_first%> <%=p_last%></TITLE>


</HEAD>


<BODY class="bg" topMargin=20 leftmargin="0" marginwidth="0" marginheight="0">
<DIV align=center >
  <TABLE class=outter cellSpacing=0 cellPadding=0 width=600 border=0>
    <TBODY>
    <TR>
      <TD><IMG height=4 src="background/lefttop.gif" width=6></TD>
      <TD background=background/top.gif colSpan=3><IMG height=4
      src="background/top.gif" width=1></TD>
      <TD><IMG height=4 src="background/righttop.gif"></TD>
    </TR>
    <TR>
      <TD vAlign=top background=background/lwing.gif rowSpan=3><IMG height=18
      src="background/left.gif" width=6></TD>
      <TD vAlign=top background=background/titlebg.gif width="16">
          <DIV><img src="background/logo.gif" width="16" height="16"  border="0"></DIV>
      </TD>
        <TD vAlign=top noWrap align=center width="100%"
    background=background/titlebg.gif><SPAN
      style="BACKGROUND-COLOR: #cecece"><FONT class=strong>MyCiteSeer</font></SPAN></TD>
        <TD><IMG height=18 src="background/right_b2.gif" width=18 border="0" ></TD>
      <TD vAlign=top background=background/rwing.gif rowSpan=3><IMG height=18
      src="background/right.gif" width=7></TD>
    </TR>
    <TR>
      <TD background=background/hr.gif colSpan=3><IMG height=2
      src="background/hr.gif" width=1></TD>
    </TR>
    <TR>
        <TD colSpan=3 align="center"><img src="images/citeseer3.gif" width="400" height="71">

          <br>
        <span class=iframestyle
            style="OVERFLOW: visible; HEIGHT: 100%"> 
        <table width="564" border="0" cellpadding="5" cellspacing="1">
          <tr bgcolor="#999999"> 
            <td><font class="strongw"><%=hello%>, <%=name%>. Welcome to use myciteseer. [<%=IPAddr%>]
              <%
      Integer count = (Integer)session.getAttribute("COUNT");
      // If COUNT is not found, create it and add it to the session
      if ( count == null ) {
      
        count = new Integer(1);
        session.setAttribute("COUNT", count);
      }
      else {
        count = new Integer(count.intValue() + 1);
        session.setAttribute("COUNT", count);
      }  
      out.println("<b>You have visited this site: "
        + count + " times.</b>");
    %> </font></td>
            <td><a href="logout.jsp">logout</a></td>
          </tr>
          <tr> 
            <td align="center" colspan="2" bgcolor="#e0e0e0"> 
              <table width="539" border="0">
                <tr> 
                  <td width="115" valign="top"><font class="strong">Your Profile:</font></td>
                  <td width="414"> <b>name:</b> <%=p_first%> <%=p_last%><br>
                    <b>email:</b> <%=p_email%><br>
                    <b>username:</b> <%=name%><br>
                    <b>affiliation:</b> <%=p_affil%><br>
                    <% String url = "modify.jsp?name="+name;%> <a href="modify.jsp">Update</a> 
                </tr>
                <tr> 
                  <td valign="top" colspan="2"> 
                    <hr size="1">
                </tr>
                <tr> 
                  <td valign="top" colspan="2" height="27"> <!-- the submission system --> 
                    <p>Your Submissions:</p>
                </tr>
                <tr> 
                  <td valign="top" colspan="2" height="59"> 
                    <div align="center"> </div>
                    <table width="550" border="1">
                      <tr> 
                        <td width = "100">URL</td>
                        <td width = "350">Description</td>
                        <td width = "10">Status</td>
                      </tr>
                      <% for (int i = 0; i < p_sublength; i++) { %> 
                      <tr> 
                        <td width = "100"> <%=p_paperurl[i]%> </td>
                        <td width = "350"> <%=p_description[i]%> </td>
                        <td width = "10"> <%=p_status[i]%> </td>
                      </tr>
                      <% } %> 
                    </table>
                    <div align="center"></div>
                    <div align="center"></div>
                    <p>&nbsp;</p>
                </tr>
              </table>
              <% String subnd = "submission.jsp?name="+name;%> <a href="submission.jsp">Submit 
              New Documents</a> <!-- end of the submission system --> <!-- the tagging system --> 
              <hr size="1">
              <img src="images/tag.jpg" height="100" width="200"> 
              <table width="573" border="1">
                <tr> 
                  <td colspan="2"><a href="alltaglist.jsp">View All Tags</a></td>
                  <td width="277"><a href="">View All Tagged Pappers</a></td>
                </tr>
                <tr> 
                  <td colspan="3"> 
                    <div align="center"><a href="usertags.jsp">Your Tags</a></div>
                  </td>
                </tr>
              </table>
              <p>Tagging a new paper! Input paper ID: <% String linknew =  "taggingframe.jsp?userid="+p_id; %> 
              <form name="usertagnew" action="<%=linknew%>" method="post">
                <input type="text" name="did">
                <input type="submit" name="Submit" value="Go">
              </form>
  	<!-- end of the tagging system -->
	<hr size="1">
 	<!-- start of the favourite system -->
	<table width="443" border="1" height="44">
          <tr>
            <td>
		<%  String linkfav = "favouritepapers.jsp"; %>
		<a href="<%=linkfav%>">My Paper Collections</a></td>
            <td><a href = "favouriteauthors.jsp">My Author Collections</a></td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
        </table>
 <!-- end of the favourite system -->
            </td>
          </tr>
        </table>
        </span> 
        <span class=iframestyle
            style="OVERFLOW: visible; HEIGHT: 100%"> 
        </span> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td background="background/hr.gif"><img src="background/hr.gif" width="1" height="2"></td>
            </tr>
            
          </table>
</TD>
    </TR>
    <TR>
      <TD><IMG height=7 src="background/leftbottom.gif" width=6></TD>
      <TD background=background/bottom.gif colSpan=3><IMG height=7
      src="background/bottom.gif" width=1></TD>
      <TD><IMG height=7 src="background/rightbottom.gif"
width=7></TD>
    </TR>
    </TBODY>
  </TABLE>
  <BR>
    <TABLE cellSpacing=1 cellPadding=2 width="300" border=0>
     </TABLE>
  </DIV>

</BODY></HTML>
