<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*,java.lang.*" %>
<%!
	int minimum(int a, int b, int c) {
		if (a <= b && a <= c)
			return a;
		if (b <= a && b <= c)
			return b;
		return c;
	}

	int computeLevenshteinDistance(String str1, String str2) {
		return computeLevenshteinDistance(str1.toCharArray(), str2
				.toCharArray());
	}

	/* compute the levenshtein distance between two tags */
	int computeLevenshteinDistance(char[] str1, char[] str2) {
		int[][] distance = new int[str1.length + 1][];

		for (int i = 0; i <= str1.length; i++) {
			distance[i] = new int[str2.length + 1];
			distance[i][0] = i;
		}
		for (int j = 0; j < str2.length + 1; j++)
			distance[0][j] = j;

		for (int i = 1; i <= str1.length; i++)
			for (int j = 1; j <= str2.length; j++)
				distance[i][j] = minimum(distance[i - 1][j] + 1,
						distance[i][j - 1] + 1, distance[i - 1][j - 1]
								+ ((str1[i - 1] == str2[j - 1]) ? 0 : 1));

		return distance[str1.length][str2.length];
	}%>
<%
	String[] t_tags = new String[10000];
	int[] t_tagid = new int[10000];
	int[] t_counts = new int[10000];
	int t_length = 0;

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
		System.out.println("Connection Successful!");
		//userStruct user1=new userStruct();
		// select user profile
		String tags = "select * from tags group by tagname";
		sm = con.createStatement();
		rs = sm.executeQuery(tags);
		while (rs.next()) {
			t_tagid[t_length] = rs.getInt("id");
			t_tags[t_length] = rs.getString("tagname");
			t_counts[t_length] = rs.getInt("count");
			t_length++;
		}

	} catch (Exception e) {
		e.printStackTrace();
		//response.sendRedirect("error/loginFalse.jsp");
	}

	String testtag = "text";
	int[] distancet = new int[t_length];
	for (int i = 0; i < t_length; i++) {
		distancet[i] = computeLevenshteinDistance(testtag, t_tags[i]);
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Nearest.jsp</title>
</head>

<body>
<br>
<br>
<br>
<center>
<h1>
<%
	for (int i = 0; i < t_length; i++) {
%> <%=t_tags[i]%>:<%=distancet[i]%><br>
<%
	}
%>
</h1>
</center>
<br>
</body>
</html>
