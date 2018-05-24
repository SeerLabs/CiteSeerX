<%@ include file="shared/IncludeDocHeader.jsp" %>
<%@ page language="java"
	import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*,java.io.*,java.sql.*,java.lang.*" %>
<%

	String doi = request.getParameter("doi"); 
	String keyphrase = request.getParameter("keyphrase");
	Connection con = null;
	ResultSet rs = null;
	PreparedStatement ps = null;
	Statement sm = null;


%>
testworked
<script>
	$(document).ready(function()
	{
		console.log("vote.jsp");
		var doi = "${ doi }";
		console.log(doi);
	});

</script> 
<%@ include file="../shared/IncludeFooter.jsp" %>

