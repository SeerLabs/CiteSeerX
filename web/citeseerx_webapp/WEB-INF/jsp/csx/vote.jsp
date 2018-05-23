<%@ include file="shared/IncludeDocHeader.jsp" %>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"></script>


<% String doi = request.getParameter("doi"); %>

<script>
	$(document).ready(function()
	{
		console.log("vote.jsp");
		var doi = "${ doi }";
		console.log(doi);
	});

</script>

<%@ include file="../shared/IncludeFooter.jsp" %>