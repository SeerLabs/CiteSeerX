<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<script type="text/javascript">
  $("#collections").addClass('active');
  $("#col_tabs").idTabs();
</script>
<div id="content">
  <h2>Collections</h2>
  <div id="left-sidebar">
    <%@ include file="shared/IncludeCollectionsSidebar.jsp" %>
  </div>
  <div id="body">
  <h3><c:out value="${collection.name}"/></h3>
   <!-- <ul id="col_tabs" class="idTabs">
    <li><a href="#papers" class="selected">Papers</a></li>
    <li><a href="#notes">Notes</a></li>
   </ul> -->
   <div id="papers" style="display:block;">
     <table border="0" cellspacing="5" cellpadding="5">
      <tr>
        <th class="title">
          <a href="<c:url value="/myciteseer/action/viewCollectionDetails${ titleq }"/>">Title 
          <c:if test='${ psort == "title" }'>
            <c:if test='${sptype == "asc"}'><img src="<c:url value="/images/arrow-asc.png" />" alt="Asc" /></c:if>
            <c:if test='${sptype == "desc"}'><img src="<c:url value="/images/arrow-desc.png"/>" alt="Desc"/></c:if>
          </c:if>
          </a>
        </th>
        
        <th class="year">
          <a href="<c:url value="/myciteseer/action/viewCollectionDetails${ dateq }"/>">Year
          <c:if test='${ psort == "date" }'>
            <c:if test='${sptype eq "asc"}'><img src="<c:url value="/images/arrow-asc.png"/>" alt="Asc" /></c:if>
            <c:if test='${sptype eq "desc"}'><img src="<c:url value="/images/arrow-desc.png"/>" alt="Desc" /></c:if>
          </c:if>
          </a>
        </th>
        
        <th class="author">Author</th>
        <th class="cites">
          <a href="<c:url value="/myciteseer/action/viewCollectionDetails${ citeq }"/>">Cites
          <c:if test='${ psort == "cite" }'>
            <c:if test='${sptype eq "asc"}'><img src="<c:url value="/images/arrow-asc.png"/>" alt="Asc" /></c:if>
            <c:if test='${sptype eq "desc"}'><img src="<c:url value="/images/arrow-desc.png"/>" alt="Desc" /></c:if>
          </c:if>
          </a>
        </th>
        <th class="ops">Operations</th>
      </tr>
      
      <c:if test="${!empty papers}">
        <c:forEach var="paper" items="${papers}">
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
                <li><a href="<c:url value="deletePaperCollection?pid=${paper.doc.doi}&amp;cid=${collection.collectionID}"/>" title="Delete paper from collection" class="delete-paper">Delete Paper</a></li>
                <%-- <li><a href="<c:url value="addPaperNote?pid=${paper.doc.doi}&amp;cid=${collection.collectionID}"/>" title="Add note to this paper">Add Note</a></li> --%>
              <c:if test="${!empty paper.notes}">
                <!--<a href="#">Show Notes</a>
                <c:if test="${!empty paper.notes}">
                
                  <a href="<c:url value="/myciteseer/action/editPaperNote?pid=${paper.doc.doi}&amp;cid=${collection.collectionID}&amp;nid=${pNote.noteID}"/>" title="Edit note">Edit</a>
                  <a href="<c:url value="/myciteseer/action/deletePaperNote?doi=${paper.doc.doi}&amp;cid=${collection.collectionID}&amp;nid=${pNote.noteID}"/>" title="Delete note" class="delete-note">Delete</a>
                        </c:if>
                -->
              </c:if>
              </ul>
            </td>
          </tr>
        </c:forEach>
        </c:if>
      </table>
      <c:if test="${empty papers}"><p>No papers have been added to this collection.</p></c:if>
      <c:if test="${!empty papers}">
        <c:if test="${ ! empty previouspageparams }"><a href="<c:url value="/myciteseer/action/viewCollectionDetails${ previouspageparams }"/>">&larr; Previous Page</a></c:if>
        <c:if test="${ ! empty nextpageparams }"><a href="<c:url value="/myciteseer/action/viewCollectionDetails${ nextpageparams }"/>">&rarr; Next Page</a></c:if>
      <p><c:out value="${npresults}"/> papers in collection.
      Page <c:out value="${ppn}" /> of <c:out value="${tppn}" /></p>
      </c:if>
    </div>
    
    <div id="notes" style="display:none;">
      <c:if test="${!empty collectionNotes}">
        <table border="0" cellspacing="5" cellpadding="5">
          <tr><th>Note</th><th>Operations</th></tr>
          <c:forEach var="note" items="${collectionNotes}">
            <tr><td><c:out value="${note.note}"/></td>
            <td class="ops">
              <ul>
              <li><a href="<c:url value="/myciteseer/action/editCollectionNote?cid=${note.collectionID}&amp;nid=${note.noteID}"/>" title="Edit note">Edit</a></li>
              <li><a href="<c:url value="/myciteseer/action/deleteCollectionNote?cid=${note.collectionID}&amp;nid=${note.noteID}"/>" title="Delete note" class="delete-note">Delete</a></li>
              </ul>
            </td></tr>
          </c:forEach>
        </table>
      </c:if>
      <c:if test="${empty collectionNotes}">
        <p>No notes have been added to this collection.</p>
      </c:if>
      <a href="<c:url value="addCollectionNote?cid=${collection.collectionID}"/>" title="Add note to this collection">Add Note</a>
    
    </div>

</div>
</div>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>