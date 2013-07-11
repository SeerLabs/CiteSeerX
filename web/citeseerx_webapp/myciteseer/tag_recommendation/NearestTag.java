import java.util.*;
import java.io.*;
import java.sql.*;

public class NearestTag{
	String[] t_tags = new String[10000];
        int[] t_tagid = new int[10000];
        int[] t_counts = new int[10000];
        int t_length = 0;

	public NearestTag(){
    		Connection con = null;
        	ResultSet rs = null;
        	PreparedStatement ps = null;
        	Statement sm = null;
        	try{    
                	String mysqluser = "yasong";
                	String mysqlpass = "";
                	String url = "jdbc:mysql://localhost/citeseer";
                	  Class.forName("com.mysql.jdbc.Driver").newInstance();
                        con = DriverManager.getConnection(url,
                          mysqluser.toLowerCase(), mysqlpass.toLowerCase());
                        System.out.println("Connection Successful!");
                        //userStruct user1=new userStruct();
                        // select user profile
                        String  tags="select * from tags group by tagname";
                        sm = con.createStatement();
                        rs = sm.executeQuery(tags);
                        while(rs.next())
                        {
                                t_tagid[t_length] = rs.getInt("id");
                                t_tags[t_length] = rs.getString("tagname");
                                t_counts[t_length] = rs.getInt("count");
                                t_length ++;
                        }
               
        	}catch(Exception e){
                	e.printStackTrace();
                        //response.sendRedirect("error/loginFalse.jsp");
        	}
		for(int i = 0; i < t_length; i++){
			System.out.println(i+":"+t_tags[i]);
		}
	}

	public static void main(String[] args){
		NearestTag nt = new NearestTag();
	}

}
