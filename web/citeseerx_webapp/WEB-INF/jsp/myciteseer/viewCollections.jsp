<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<script type="text/javascript">
  $("#collections").addClass('active');
</script>
<div id="content">
  <h2>Collections</h2>
  <div id="left-sidebar">
    <%@ include file="shared/IncludeCollectionsSidebar.jsp" %>
  </div>
  <div id="body">
   <c:if test="${!empty collections}">
    <table border="0" cellspacing="5" cellpadding="5" width="100%" id="collections-list">
     <thead>
      <tr>
       <th>Name</th><th>Description</th><th>Operations</th>
      </tr>
     </thead>
     <tbody>
     <c:forEach var="collection" items="${collections}" varStatus="status">
      <tr>
       <td><strong><a href="<c:url value="/myciteseer/action/viewCollectionDetails?cid=${collection.collectionID}"/>"
         title="View <c:out value="${collection.name}"/> Details"><c:out value="${collection.name}"/></a></strong></td>
       <td><c:out value="${collection.description}"/></td>
       <td class="ops">
        <ul>
        <c:if test="${collection.deleteAllowed}">
         <li>
           <a href="<c:url value="/myciteseer/action/editCollection?cid=${collection.collectionID}"/>"
              title="Edit <c:out value="${collection.name}"/>">Edit</a>
         </li>
         <li>
         <a href="<c:url value="/myciteseer/action/deleteCollection?cid=${collection.collectionID}"/>"
            class="delete-collection" title="Delete <c:out value="${collection.name}"/>">Delete</a>
        </li>
        </c:if>
        <li>
        <a href="<c:url value="/myciteseer/action/monitorCollection?cid=${collection.collectionID}"/>"
           title="Add <c:out value="${collection.name}"/> papers to monitor list">Monitor</a>
        </li>
        <li>
        <a href="<c:url value="/myciteseer/action/addCollectionMetaCart?cid=${collection.collectionID}"/>"
           title="Add <c:out value="${collection.name}"/> papers to Metadata Cart">Add to Cart</a>
        </li>
       </td>
      </tr>
     </c:forEach>
     </tbody>
    </table>
   </c:if>

  <c:if test="${empty collections}">No collections were found.</c:if>
  </div>
</div>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>
