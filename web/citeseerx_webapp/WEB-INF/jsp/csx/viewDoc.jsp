<%@ include file="shared/IncludeDocHeader.jsp" %>
<%@ page import="java.io.*,java.util.*,java.sql.*"%>
<%@ page import="javax.servlet.http.*,javax.servlet.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
  <div id="viewContent" class="sidebar">
    <div id="viewContent-inner">
      <div id="viewSidebar">
        <% if (mscConfig.getPersonalPortalEnabled()) {%>
          <%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
          <%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
          <%@ page import="org.springframework.security.AuthenticationException" %>
        		<% if (account != null) { %>
              <div id="tags" class="block">
                <h3>Popular Tags</h3>
                <form method="post" action="<c:url value="/myciteseer/action/editTags"/>" enctype="application/x-www-form-urlencoded" id="tag_form">
                  <p>Add a tag: <input class="tagField textField" type="text" name="tag" value="" size="20" />
                  <input class="button" type="submit" name="submit" value="Submit" alt="submit" /></p>
                  <input type="hidden" name="doi" value="<c:out value="${ doi }"/>"/>
                </form>
                <c:if test="${ empty tags }"><p>No tags have been applied to this document.</p></c:if>
                <c:if test="${ ! empty tags }">
                  <ul id="tagcloud">
                    <c:forEach var="tag" items="${ tags }">
                      <c:url value="/search" var="searchUrl"><c:param name="q" value="tag:\"${ tag.tag }\""/></c:url>
                      <li><a href="<c:out value="${ searchUrl }" escapeXml="true" />"><c:out value="${ tag.tag }"/></a></li>
                    </c:forEach>
                  </ul>
                </c:if>
              </div>
            <% 
		
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Statement sm = null;
		try {
			String mysqluser = "csx-prod"; //temp web server login creds
			String mysqlpass = "csx-prod"; //temp web server login creds
			String url = "jdbc:mysql://localhost/citeseerx";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(url, mysqluser.toLowerCase(),mysqlpass.toLowerCase());

		if (request.getParameter("upvote") != null) {
			//add 1 upvote to keyphrase_voting table
			String upvoteKeyphrase = request.getParameter("upvote");
			String upvoteQuery = ("UPDATE keyphrase_voting SET upvote = upvote + 1, lastvotetime = CURRENT_TIMESTAMP WHERE keyphrase = '"+upvoteKeyphrase+"'");
			sm = con.createStatement();
			sm.executeUpdate(upvoteQuery);
			//add upvote event to keyphrase_action table
			String getUpvoteID = ("SELECT id FROM keyphrase_voting WHERE keyphrase = '"+upvoteKeyphrase+"'");
			rs = sm.executeQuery(getUpvoteID);
			while (rs.next()) {
				Statement sm2 = null;
				sm2 = con.createStatement();
				String upvoteID = rs.getString("id");
				String upvoteIDQuery = ("INSERT INTO `keyphrase_action` (`id`, `actiontime`, `action`, `keyphrase_voting_id`) VALUES (NULL, CURRENT_TIMESTAMP, 'up', '"+upvoteID+"')");
				sm2.executeUpdate(upvoteIDQuery);
			}
		}

		if (request.getParameter("downvote") != null) {
			//add 1 downvote to keyphrase_voting table
			String downvoteKeyphrase = request.getParameter("downvote");
			String downvoteQuery = ("UPDATE keyphrase_voting SET downvote = downvote + 1, lastvotetime = CURRENT_TIMESTAMP WHERE keyphrase = '"+downvoteKeyphrase+"'");
			sm = con.createStatement();
			sm.executeUpdate(downvoteQuery);
			//add downvote event to keyphrase_action table
			String getDownvoteID = ("SELECT id FROM keyphrase_voting WHERE keyphrase = '"+downvoteKeyphrase+"'");
			rs = sm.executeQuery(getDownvoteID);
			while (rs.next()) {
				Statement sm3 = null;
				sm3 = con.createStatement();
				String downvoteID = rs.getString("id");
				String downvoteIDQuery = ("INSERT INTO `keyphrase_action` (`id`, `actiontime`, `action`, `keyphrase_voting_id`) VALUES (NULL, CURRENT_TIMESTAMP, 'down', '"+downvoteID+"')");
				sm3.executeUpdate(downvoteIDQuery);
			}
		}

		if (request.getParameter("undoUpvote") != null) {
			//remove upvote
			String undoUpvoteKeyphrase = request.getParameter("undoUpvote");
			String undoUpvoteQuery = ("UPDATE keyphrase_voting SET upvote = upvote - 1, lastvotetime = CURRENT_TIMESTAMP WHERE keyphrase = '"+undoUpvoteKeyphrase+"'");
			sm = con.createStatement();
			sm.executeUpdate(undoUpvoteQuery);
			
			//remove upvote event to keyphrase_action table
			String getUpvoteID = ("SELECT id FROM keyphrase_voting WHERE keyphrase = '"+undoUpvoteKeyphrase+"'");
			rs = sm.executeQuery(getUpvoteID);
			while (rs.next()) {
				Statement sm4 = null;
				sm4 = con.createStatement();
				String upvoteID = rs.getString("id");
				String upvoteIDQuery = ("INSERT INTO `keyphrase_action` (`id`, `actiontime`, `action`, `keyphrase_voting_id`) VALUES (NULL, CURRENT_TIMESTAMP, 'undoUp', '"+upvoteID+"')");
				sm4.executeUpdate(upvoteIDQuery);
			}
		}
		
		if (request.getParameter("undoDownvote") != null) {
			//remove downvote
			String undoDownvoteKeyphrase = request.getParameter("undoDownvote");
			String undoDownvoteQuery = ("UPDATE keyphrase_voting SET downvote = downvote - 1, lastvotetime = CURRENT_TIMESTAMP WHERE keyphrase = '"+undoDownvoteKeyphrase+"'");
			sm = con.createStatement();
			sm.executeUpdate(undoDownvoteQuery);
			
			//remove downvote event to keyphrase_action table
			String getDownvoteID = ("SELECT id FROM keyphrase_voting WHERE keyphrase = '"+undoDownvoteKeyphrase+"'");
			rs = sm.executeQuery(getDownvoteID);
			while (rs.next()) {
				Statement sm5 = null;
				sm5 = con.createStatement();
				String downvoteID = rs.getString("id");
				String downvoteIDQuery = ("INSERT INTO `keyphrase_action` (`id`, `actiontime`, `action`, `keyphrase_voting_id`) VALUES (NULL, CURRENT_TIMESTAMP, 'undoDown', '"+downvoteID+"')");
				sm5.executeUpdate(downvoteIDQuery);
			}
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}	


		}%>
        <% } %>
        
        <c:if test="${ ! empty bibtex }">
          <div id="bibtex" class="block">
            <h3>BibTeX</h3>
            <p><c:out value="${ bibtex }" escapeXml="false"/></p>
          </div> <!-- End content box -->
        </c:if>
        <div id="bookmark" class="block">
          <h3>Share</h3>
          <table border="0" cellspacing="0" cellpadding="5">
            <tr>
              <td><a href="<c:url value="/viewdoc/bookmark?doi=${doi}&amp;title=${title}&amp;site=facebook"/>" title="Facebook"><img src="<c:url value="/images/facebook_icon.png"/>" alt="Facebook"/></a></td>
              <td><a href="<c:url value="/viewdoc/bookmark?doi=${doi}&amp;title=${title}&amp;site=twitter"/>" title="Twitter"><img src="<c:url value="/images/twitter_icon.png"/>" alt="Twitter"/></a></td>
              <td><a href="<c:url value="/viewdoc/bookmark?doi=${doi}&amp;title=${title}&amp;site=reddit"/>" title="Reddit"><img src="<c:url value="/images/reddit_icon.png"/>" alt="Reddit"/></a></td>
              <td><a href="<c:url value="/viewdoc/bookmark?doi=${doi}&amp;site=bibsonomy&amp;title=${title}"/>" title="BibSonomy"><img src="<c:url value="/images/bibsonomy_icon.png"/>" alt="Bibsonomy"/></a></td>
              </tr>
	      <tr>
		<td colspan="6">
		<script src="http://widgets.twimg.com/j/2/widget.js"></script>	
		<script>
			var doi;
			doi="<c:out value= "${ doi }"/>";
			new TWTR.Widget({
  			version: 2,
  			type: 'search',
  			search: 'citeseerx+'+doi,
  			interval: 30000,
  			title: 'CiteSeerX',
  			subject: doi,
  			width: 255,
  			height: 30,
  			theme: {
    			shell: {
      			background: '#E6E6E6',
      			color: '#000000'
    			},
    			tweets: {
      				background: '#ffffff',
      				color: '#444444',
      				links: '#1985b5'
    			}
  		},
  		features: {
    		scrollbar: false,
    		loop: true,
    		live: true,
    		behavior: 'default'
 		 }
		}).render().start();

		function upvote(x) {
			$.ajax({
				type: "POST",
				data: {"upvote": x}
			
			}); 
		}

		function downvote(x) {
			$.ajax({
				type: "POST",
				data: {"downvote": x}
			});
		}
		
		function undoUpvote(x) {
			$.ajax({
				type: "POST",
				data: {"undoUpvote": x}
			});
		}

		function undoDownvote(x) {
			$.ajax({
				type: "POST",
				data: {"undoDownvote": x}
			});
		}		

		$(document).ready(function(){
			$('button.upvote').click(function(){
				var upvoteClass = this.className;				
				var keyphrase = event.currentTarget.attributes.value.nodeValue;
				

				var value = $("button.downvoted[value='"+keyphrase+"']");
				
				if (upvoteClass == 'upvote'){
					upvote(keyphrase);
					$(this).toggleClass('upvote upvoted');
				} else if (upvoteClass == 'upvoted') {
					undoUpvote(keyphrase);
					$(this).toggleClass('upvoted upvote');
				}

				if (value[0].className == 'downvoted'){
					undoDownvote(keyphrase);
					value.toggleClass('downvoted downvote');	
				}
			});

			$('button.downvote').click(function(){
				var downvoteClass = this.className;
				var keyphrase = event.currentTarget.attributes.value.nodeValue;
				
				var value = $("button.upvoted[value='"+keyphrase+"']");

				if (downvoteClass == 'downvote'){
					downvote(keyphrase);
					$(this).toggleClass('downvote downvoted');
				} else if (downvoteClass == 'downvoted') {
					undoDownvote(keyphrase);
					$(this).toggleClass('downvoted downvote');
				}
				
				if (value[0].className == 'upvoted'){
					undoUpvote(keyphrase);
					value.toggleClass('upvoted upvote');
				}
			});		
		});

		</script>	
		</td>
	      </tr>
          </table>
        </div>
        <c:if test="${ ! empty coins}">
          <div id="OpenURL" class="block">
            <h3>OpenURL</h3>
            <span class="Z3988" title="<c:out value="${ coins }"/>">&nbsp;</span>
          </div>
        </c:if>
      </div> <%-- sidebar div close --%>
      <div id="abstract">
        <h3>Abstract</h3>
        <p><c:out value="${ abstractText }"/></p>
      </div>
      <div id="keywords">
        <c:if test="${!empty keyphrases}">
          <h3>Keyphrases</h3>
          <p>
            <c:forEach items="${ keyphrases }" var="keyphrase">
            <a href="<c:url value="/search?q=${keyphrase}&submit=Search&sort=rlv&t=doc"/>"><c:out value="${keyphrase}"/></a>&nbsp;<%if (account != null) {%><button name="upvote" value="${keyphrase}" class="upvote"><i class="fas fa-thumbs-up"></i></button>&nbsp;<button name="downvote" value="${keyphrase}" class="downvote"><i class="fas fa-thumbs-down"></i></button>&nbsp;&nbsp;&nbsp;<%}%>
            </c:forEach>
          </p>
        </c:if>
      </div>
    </div>
  </div><%-- viewContent close div --%>
  <div class="clear"></div>
</div>

<%@ include file="../shared/IncludeFooter.jsp" %>
