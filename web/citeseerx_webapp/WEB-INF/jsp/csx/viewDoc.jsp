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
        <c:if test="${ ! empty chartdata }">
          <div id="citechart" class="block">
            <h3>Years of Citing Articles</h3>
			<div id="fig">
                <script type="text/javascript+protovis">

					/* Sizing and scales. */
					var w = 240,
						h = 200,
						x = pv.Scale.linear(chartdata, function(d) d.year).range(0, w),
						y = pv.Scale.linear(chartdata, function(d) d.ccount).range(0, h);
					
					/* The root panel. */
					var vis = new pv.Panel()
						.def("i", -1)
						.width(w + 10)
						.height(h +20)
						.bottom(20)
						.left(20)
						.right(10)
						.top(5)
						.overflow('visible');
					
					var line = vis.add(pv.Line)
						.data(chartdata)
						.left(function(d) x(d.year))
						.bottom(function(d) y(d.ccount))
						.lineWidth(2);
					
					/* Y-axis and ticks. */
					vis.add(pv.Rule)
						.data(y.ticks(5))
						.bottom(y)
						.strokeStyle(function(d) d ? "#eee" : "#000")
					  .anchor("left").add(pv.Label)
						.text(y.tickFormat);
					
					/* X-axis and ticks. */
					vis.add(pv.Rule)
						.data(x.ticks(6))
						.visible(function(d) d)
						.left(x)
						.bottom(-5)
						.height(5)
					  .anchor("bottom").add(pv.Label);
					
					vis.add(pv.Line)
						.data(chartdata)
						.left(function(d) x(d.year))
						.bottom(function(d) y(d.ccount))
						.lineWidth(2);
					
					var dot = line.add(pv.Dot)
						.visible(function() i >= 0)
						.data(function() [chartdata[i]])
						.fillStyle(function() line.strokeStyle())
						.strokeStyle("#000")
						.size(20)
						.lineWidth(3).anchor("top").add(pv.Label).font("bold 14px sans-serif").text(function(d) d.ccount.toFixed(0));
					
					vis.add(pv.Bar)
						.fillStyle("rgba(0,0,0,.001)")
						.event("mouseout", function() {
							i = -1;
							return vis;
						  })
						.event("mousemove", function() {
							var mx = x.invert(vis.mouse().x);
							i = pv.search(chartdata.map(function(d) d.year), mx);
							i = i < 0 ? (-i - 2) : i;
							return vis;
						  });
					
					
					vis.render();

			    </script>
            </div>
          </div> <!-- End content box -->
        </c:if>
        <div id="bookmark" class="block">
          <h3>Bookmark</h3>
          <table border="0" cellspacing="0" cellpadding="5">
            <tr>
              <td><a href="<c:url value="/viewdoc/bookmark?doi=${doi}&amp;title=${title}&amp;site=citeulike"/>" title="CiteULike"><img src="<c:url value="/images/citeulike.png"/>" alt="citeulike"/></a></td>
              <td><a href="<c:url value="/viewdoc/bookmark?doi=${doi}&amp;site=connotea"/>" title="Connotea"><img src="<c:url value="/images/connotea.png"/>" alt="Connotea"/></a></td>
              <td><a href="<c:url value="/viewdoc/bookmark?doi=${doi}&amp;site=bibsonomy&amp;title=${title}"/>" title="BibSonomy"><img src="<c:url value="/images/bibsonomy.png"/>" alt="Bibsonomy"/></a></td>
              <td><a href="<c:url value="/viewdoc/bookmark?doi=${doi}&amp;title=${title}&amp;site=delicious"/>" title="del.icio.us"><img src="<c:url value="/images/delicious.gif"/>" alt="Del.icio.us"/></a></td>
              <td><a href="<c:url value="/viewdoc/bookmark?doi=${doi}&amp;title=${title}&amp;site=digg"/>" title="Digg it"><img src="<c:url value="/images/digg.png"/>" alt="Digg"/></a></td>
              <td><a href="<c:url value="/viewdoc/bookmark?doi=${doi}&amp;title=${title}&amp;site=reddit"/>" title="Reddit"><img src="<c:url value="/images/reddit.gif"/>" alt="Reddit"/></a></td>
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
      <p><c:out value="${ abstractText }"/></p>
    </div>
    <div id="citations">
      <h3>Citations</h3>
      <c:if test="${empty citations}"><p>No citations identified.</p></c:if>
      <c:if test="${!empty citations}">
        <table class="refs" border="0" cellspacing="5" cellpadding="5">
          <c:forEach var="citation" items="${ citations }" varStatus="theCount">
            <tr><td class="title"><c:if test="${ citation.ncites > 0 }"><c:out value="${ citation.ncites }"/></c:if></td>
              <td><c:if test="${ citation.inCollection }"><a href="<c:url value="/viewdoc/summary?cid=${ citation.cluster }"/>"><c:out value="${ citation.title }" escapeXml="true" /></a></c:if>
                <c:if test="${ ! citation.inCollection }"><a class="citation_only" href="<c:url value="/showciting?cid=${ citation.cluster }"/>"><c:out value="${ citation.title }" escapeXml="true" /></a></c:if>
                - <c:out value="${ citation.authors }"/>
                <c:if test="${ citation.year > 0 }"> - <c:out value="${ citation.year }"/></c:if>
		<c:choose>
		  <c:when test="${citationContexts[theCount.index] != ''}">
		  <a href="" onclick="toggleCitation('citation<c:out value="${theCount.index}" />'); return false;">(Show Context)</a>
		  <div id="citation<c:out value="${theCount.index}" />" style="display:none">
		    <p class='citationContextHeader'>Citation Context</p>
		    <p class='citationContext'>...<c:out value="${ citationContexts[theCount.index] }" />...</p>
		  </div>
		  </c:when>
		</c:choose>
              </td></tr>
            </c:forEach>
        </table>
      </c:if>
    </div><%-- citations close div --%>
    </div>
  </div><%-- viewContent close div --%>
  <div class="clear"></div>
</div>



<%@ include file="../shared/IncludeFooter.jsp" %>
