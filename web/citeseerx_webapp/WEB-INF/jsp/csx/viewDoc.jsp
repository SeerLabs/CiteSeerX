<%@ include file="shared/IncludeDocHeader.jsp" %>
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
            <% } %>
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
        <p><c:out value="${ abstract }"/></p>
      </div>
      <div id="keywords">
        <c:if test="${!empty keyphrases}">
          <h3>Keyphrases</h3>
          <p>
            <c:forEach items="${ keyphrases }" var="keyphrase">
            <a href="<c:url value="/search?q=${keyphrase}&submit=Search&sort=rlv&t=doc"/>"><c:out value="${keyphrase}"/></a>&nbsp;&nbsp;&nbsp;
            </c:forEach>
          </p>
        </c:if>
      </div>
    </div>
  </div><%-- viewContent close div --%>
  <div class="clear"></div>
</div>

<%@ include file="../shared/IncludeFooter.jsp" %>
