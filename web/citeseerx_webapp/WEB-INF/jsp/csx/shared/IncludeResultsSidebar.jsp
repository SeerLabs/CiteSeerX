<div id="sidebar">
  <h3>Tools</h3>
  <!-- <div id="feeds">
    <a href="<c:url value='/search?${ rss }'/>"><img src="<c:url value='/images/rss.png'/>" alt="RSS"/></a>
  </div> -->

  <div id="sorting">Sorted by: 
    <select name="sortvalue" id="sortvalue" onchange="location = this.options[this.selectedIndex].value;" class="pulldown">
      <option value="<c:url value='/search?${ rlvq }'/>" <c:if test='${ sorttype eq "rlv" }'>selected</c:if>>Relevance</option>
      <option value="<c:url value='/search?${ citeq }'/>" <c:if test='${ sorttype eq "cite" }'>selected</c:if>>Citation Count</option>
      <option value="<c:url value='/search?${ dateq }'/>" <c:if test='${ sorttype eq "date" }'>selected</c:if>>Year (Descending)</option>
      <option value="<c:url value='/search?${ ascdateq }'/>" <c:if test='${ sorttype eq "ascdate" }'>selected</c:if>>Year (Ascending)</option>
      <c:if test='${ param.t ne "table" and param.t ne "algorithm"}'>
      <option value="<c:url value='/search?${ timeq }'/>" <c:if test='${ sorttype eq "recent" }'>selected</c:if>>Recency</option>
      </c:if>
    </select>
  </div>

  <div id="qother">Try your query at:
    <c:url value='http://scholar.google.com/scholar' var="googleScholar"><c:param name='q' value='${ param.q }'/><c:param name='hl' value='en' /><c:param name='btnG' value='Search'/></c:url>
    <c:url value='https://www.semanticscholar.org/search' var='s2'><c:param name='q' value='${ param.q }'/></c:url>
    <c:url value='http://dblp.uni-trier.de/search' var='dblp'><c:param name='q' value='${ param.q }'/></c:url>
    <c:url value='http://www.bing.com/search' var="Bing"><c:param name="q" value='${ param.q }'/></c:url>
    <c:url value='https://www.google.com/search' var="Google"><c:param name="q" value='${ param.q }'/></c:url>
    <c:url value='http://academic.research.microsoft.com/Search.aspx' var="Academic"><c:param name="query" value='${ param.q }'/><c:param name="submit" value='Search'/></c:url>
    <table border="0" cellspacing="5" cellpadding="5" >
      <tr>
          <td><a href="<c:out value='${ s2 }' escapeXml="true"/>" title="AllenAI Semantic Scholar"><img src="<c:url value="/images/ai2_icon.png"/>" alt="Semantic Scholar" height="30" width="30"/></a></td>
          <td><a href="<c:out value='${ googleScholar }' escapeXml="true"/>" title="Google Scholar"><img src="<c:url value="/images/googlescholar_icon.png"/>" alt="Scholar" height="24" width="24"/></a></td>
          <td><a href="<c:out value='${ Academic }' escapeXml="true"/>" title="Microsoft Academic Search"><img src="<c:url value="/images/microsoftacademicsearch_icon.jpg"/>" alt="Academic" height="24" width="24"/></a></td>
      </tr>
      <tr>
          <td><a href="<c:out value='${ Google }' escapeXml="true"/>" title="Google"><img src="<c:url value="/images/google_icon.png"/>" alt="Google" height="24" width="24"/></a></td>
          <td><a href="<c:out value='${ Bing }' escapeXml="true"/>" title="Bing"><img src="<c:url value="/images/bing_icon.ico"/>" alt="Bing" height="24" width="24"/></a></td>
          <td><a href="<c:out value='${ dblp }' escapeXml="true"/>" title="DBLP Computer Science Bibliography"><img src="<c:url value="/images/dblp_icon.png"/>" alt="DBLP" height="30" width="30"/></a></td>
      </tr>
    </table>
  </div>
</div>
