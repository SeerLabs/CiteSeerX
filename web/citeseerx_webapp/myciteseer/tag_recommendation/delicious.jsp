<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<%
  String url = request.getParameter("url");
  String md5 = request.getParameter("md5");
  %>
<head>
<meta http-equiv="content-type"
	content="text/html; charset=windows-1250">
<meta name="generator" content="PSPad editor, www.pspad.com">
<title></title>
</head>
<body>
<%
	if (md5 != null) {
		String hash_url = "http://badges.del.icio.us/feeds/json/url/data?hash="
				+ md5 + "&callback=displayURL";
%>
<div id="container">
<p><%=hash_url%></p>
<h2>url info for <a href="#" id="url-url">(loading)</a></h2>
<p>saved by <em id="url-count">(loading)</em> others</p>
<div>
<p>top tags:</p>
<ul id="url-tags"></ul>
</div>
<br />
</div>
<script type="text/javascript">
      function displayURL(data) {
  
          var urlinfo = data[0];
          if (!urlinfo.total_posts) return;
  
          document.getElementById('url-count').innerHTML = urlinfo.total_posts;
          document.getElementById('url-url').innerHTML   = urlinfo.url;
          document.getElementById('url-url').href        = urlinfo.url;
  
          var ct = document.getElementById('url-tags');
          for (tag in urlinfo.top_tags) {
              var li = document.createElement('li');
              var a = document.createElement('a');
  
              a.setAttribute('href', 'http://del.icio.us/tag/'+tag);
              a.appendChild(document.createTextNode(tag));
              li.appendChild(a);
              ct.appendChild(li);
              ct.appendChild(document.createTextNode(' '));
          }
  
      }
  </script>
<script type="text/javascript" src="<%=hash_url%>"></script>

<%
	} else {
%>
<script type="text/javascript" src="md5.js">
  </script>
<script type="text/javascript">
    var hash = hex_md5('<%=url%>');
    document.location.href='tags.jsp?md5='+hash;
  </script>
<%
	}
%>
</body>
</html>
