<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*" %>
<%
	//String name=request.getParameter("name");

	String[] t_tags = new String[10000];
	int[] t_tagid = new int[10000];
	int[] t_counts = new int[10000];
	int t_length = 0;
	try {

		BufferedReader in = new BufferedReader(
				new FileReader(
						"/home/yasong/apache-tomcat-4.1.34/webapps/ROOT/CHIkeywords.txt"));
		String str;
		StringTokenizer st;
		while ((str = in.readLine()) != null) {

			st = new StringTokenizer(str, ",");
			t_tags[t_length] = st.nextToken();
			t_counts[t_length] = Integer.parseInt(st.nextToken());
			t_length++;
		}
		in.close();
	} catch (Exception e) {
		e.printStackTrace();
		//response.sendRedirect("error/loginFalse.jsp");
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>MyCiteSeer-CHI Tag Clouds</title>
</head>

<body>
<br>

<br>
<center>
<h1>Tag Clouds CHI</h1>
</center>
<br>
<table border="1" align="center">
	<tr>
		<td>
		<div align="center">Tags</div>
		</td>
	</tr>
	<%
		for (int i = 0; i < t_length; i++) {
			String color = "black";
			String link = "papertags.jsp?tagid=" + t_tagid[i] + "&tagname="
					+ t_tags[i];
			if (t_counts[i] >= 20) {
				color = "#000000";
			} else if (t_counts[i] >= 10) {
				color = "#333333";
			} else if (t_counts[i] >= 5) {
				color = "#666666";
			} else if (t_counts[i] >= 3) {
				color = "#999999";
			} else {
				color = "#bbbbbb";
			}
	%>
	<tr>
		<td><a href="<%=link%>"><font size="<%=t_counts[i]%>"
			color="<%=color%>"> <%=t_tags[i]%> </font></a></td>
	</tr>
	<%
		}
	%>
</table>
</body>
</html>
