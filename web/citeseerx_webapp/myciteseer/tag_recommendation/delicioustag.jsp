<%@ page
    language="java"
    import="javax.naming.*,javax.rmi.PortableRemoteObject,java.util.*, java.io.*, java.sql.*, java.lang.*"        %>
<%
        //String name=request.getParameter("name");
        String url    = (String)request.getParameter("url");
        //int userid     = Integer.parseInt((String)request.getParameter("userid"));
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>无标题文档</title>
<script type="text/javascript" src="md5.js"></script>

</head>

<body>
URL: [<%=url%>]
<div id="container">
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

<script type="text/javascript">
function getURLParam(strParamName){
  var strReturn = "";
  var strHref = window.location.href;
  if ( strHref.indexOf("?") > -1 ){
    var strQueryString = strHref.substr(strHref.indexOf("?")).toLowerCase();
    var aQueryString = strQueryString.split("&");
    for ( var iParam = 0; iParam < aQueryString.length; iParam++ ){
      if (
aQueryString[iParam].indexOf(strParamName.toLowerCase() + "=") > -1 ){
        var aParam = aQueryString[iParam].split("=");
        strReturn = aParam[1];
        break;
      }
    }
  }
  return unescape(strReturn);
}
</script>
<script type="text/javascript" >
  	var d = getURLParam("url");
	hash = hex_md5("http://www.oracle.com/technology/software/tech/oci/instantclient/index.html");
	hash1 = hex_md5(d);
	document.write(d);
	document.write(hash);
	document.write("<br>");
	document.write(hash1);
	var temp = "script type=";
	document.write("<br>");
	document.write(temp);
  
</script> 

<script type="text/javascript"
        src="http://badges.del.icio.us/feeds/json/url/data?hash=61359c3a1ab360f5e3f2c9396368960f&callback=displayURL"></script>



</body>
</html>
