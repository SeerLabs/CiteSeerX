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
    <c:url value='http://search.yahoo.com/search' var='yahoo'><c:param name='p' value='${ param.q }'/></c:url>
    <c:url value='http://dblp.uni-trier.de/search' var='dblp'><c:param name='q' value='${ param.q }'/></c:url>
    <c:url value='http://www.bing.com/search' var="Bing"><c:param name="q" value='${ param.q }'/></c:url>
    <c:url value='http://liinwww.ira.uka.de/csbib/index' var="CBS"><c:param name="query" value='${ param.q }'/><c:param name="submit" value='Search'/></c:url>
    <c:url value='http://academic.research.microsoft.com/Search.aspx' var="Academic"><c:param name="query" value='${ param.q }'/><c:param name="submit" value='Search'/></c:url>
    <table border="0" cellspacing="5" cellpadding="5" >
      <tr><td><a href="<c:out value='${ googleScholar }' escapeXml="true"/>" title="Google Scholar search engine">Scholar</a></td>
        <td><a href="<c:out value='${ yahoo }' escapeXml="true"/>" title="Yahoo Web Search">Yahoo!</a></td>
        <td><a href="<c:out value='${ dblp }' escapeXml="true"/>" title="DBLP Computer Science Bibliography">DBLP</a></td></tr>
      <tr><td><a href="<c:out value='${ Bing }' escapeXml="true"/>" title="Live Search is evolving">Bing</a></td>
        <td><a href="<c:out value='${ CBS }' escapeXml="true"/>" title="Collection of Computer Science Bibliographies">CSB</a></td>
        <td><a href="<c:out value='${ Academic }' escapeXml="true"/>" title="Microsoft Academic Search">Academic</a></td></tr>
    </table>
  </div>
</div>
