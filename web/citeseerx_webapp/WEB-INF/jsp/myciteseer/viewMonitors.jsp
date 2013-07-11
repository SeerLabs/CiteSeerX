<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<script type="text/javascript">
  $("#monitoring").addClass('active');
</script>
<div id="content">
  <h2>Monitoring</h2>
     <table border="0" cellspacing="5" cellpadding="5">
      <tr>
        <th class="title">
          <a href="<c:url value="/myciteseer/action/viewMonitors${ titleq }"/>">Title 
          <c:if test='${ psort == "title" }'>
            <c:if test='${sptype == "asc"}'><img src="<c:url value="/images/arrow-asc.png" />" alt="Asc" /></c:if>
            <c:if test='${sptype == "desc"}'><img src="<c:url value="/images/arrow-desc.png"/>" alt="Desc"/></c:if>
          </c:if>
          </a>
        </th>
        
        <th class="year">
          <a href="<c:url value="/myciteseer/action/viewMonitors${ dateq }"/>">Year
          <c:if test='${ psort == "date" }'>
            <c:if test='${sptype eq "asc"}'><img src="<c:url value="/images/arrow-asc.png"/>" alt="Asc" /></c:if>
            <c:if test='${sptype eq "desc"}'><img src="<c:url value="/images/arrow-desc.png"/>" alt="Desc" /></c:if>
          </c:if>
          </a>
        </th>
        
        <th class="author">Author</th>
        <th class="cites">
          <a href="<c:url value="/myciteseer/action/viewMonitors${ citeq }"/>">Cites
          <c:if test='${ psort == "cite" }'>
            <c:if test='${sptype eq "asc"}'><img src="<c:url value="/images/arrow-asc.png"/>" alt="Asc" /></c:if>
            <c:if test='${sptype eq "desc"}'><img src="<c:url value="/images/arrow-desc.png"/>" alt="Desc" /></c:if>
          </c:if>
          </a>
        </th>
        <th class="ops">Operations</th>
      </tr>
      
      <c:if test="${!empty monitors}">
        <c:forEach var="paper" items="${monitors}">
          <tr>
            <td class="title">
              <a href="<c:url value="/viewdoc/summary?doi=${paper.doc.doi}"/>">
                <c:out value="${paper.doc.title}"/>
              </a>
              <c:if test="${ ! empty paper.coins}">
                <span class="Z3988" title="<c:out value="${paper.coins}" />">&nbsp;</span>
              </c:if>
            </td>
            <td class="year">
              <c:out value="${paper.doc.year}"/>
            </td>
            <td class="author">
              <c:out value="${paper.doc.authors}"/>
            </td>
            <td class="cites">
                <a class="citation remove" href="<c:url value="/showciting?cid=${ paper.doc.cluster }"/>" title="number of citations"><c:out value="${ paper.doc.ncites }"/> 
                <c:if test='${ paper.doc.selfCites > 0}'>
                  (<c:out value="${ paper.doc.selfCites }"/> self)
                </c:if>
                </a>
            </td>
            <td class="ops">
              <ul>
                <li><a class="metacart" onclick="addToCartProxy(<c:out value="${paper.doc.cluster}"/>)">Add To MetaCart</a><span id="cmsg_<c:out value="${ paper.doc.cluster }"/>" class="cartmsg"></span></li>
                <li><a href="<c:url value="/myciteseer/action/editMonitors?&amp;doi=${ paper.doc.doi }&amp;type=del"/>" title="Delete" class="delete-paper">Delete</a></li>
              </ul>
            </td>
          </tr>
        </c:forEach>
        </c:if>
      </table>
      <c:if test="${empty monitors}"><p>No papers are currently being monitored.</p></c:if>
      <c:if test="${!empty monitors}">
        <c:if test="${ ! empty previouspageparams }"><a href="<c:url value="/myciteseer/action/viewMonitors${ previouspageparams }"/>">&larr; Previous Page</a></c:if>
        <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/myciteseer/action/viewMonitors${ nextpageparams }"/>">&rarr; Next Page</a></c:if>
      <p>Found <c:out value="${npresults}"/> papers.
      Page <c:out value="${ppn}" /> of <c:out value="${tppn}" /></p>
      </c:if>
    </div>
    </div>
    <div class="clear"></div>
    </div>
    <%@ include file="../shared/IncludeFooter.jsp" %>

