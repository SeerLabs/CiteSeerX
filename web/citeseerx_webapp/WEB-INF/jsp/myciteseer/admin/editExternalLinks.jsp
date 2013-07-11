<%@ include file="../shared/IncludeMyCSXHeader.jsp" %>
<div id="content">
  <h2>Administration</h2>
  <%@ include file="IncludeLeftAdmin.jsp" %>

  <div id="body"> <!-- center column -->
  <c:if test="${error}">
    <div class="error">
      <h3><c:out value="${ errorMsg }"/></h3>
    </div>
  </c:if>
  <c:if test="${!error}">
   <c:if test="${editLinkType.newLinkType}">
    <h2 class="char_headers">Create Link Type</h2>
   </c:if>
   <c:if test="${!editLinkType.newLinkType}">
    <h2 class="char_headers">Edit Link Type</h2>
   </c:if>
   <div class="content">
    <c:if test="${editLinkType.newLinkType}">
     <form method="post" 
           action="<c:url value="/myciteseer/action/admin/editExternalLinks"/>" 
           id="link_type_form" class="wform labelsLeftAligned hintsTooltip">
    </c:if>
    <c:if test="${!editLinkType.newLinkType}">
     <form method="post" 
           action="<c:url value="/myciteseer/action/admin/editExternalLinks"/>" 
           id="link_type_form" class="wform labelsLeftAligned hintsTooltip">
    </c:if>
    <fieldset class="">
     <legend>External Link Type</legend>
     <spring:bind path="editLinkType.link.label">
      <div class="oneField">
       <label for="<c:out value="${status.expression}"/>" class="preField">Label&nbsp;
        <span class="reqMark">*</span>
       </label>
       <input type="text" size="20" maxlength="50" 
              id="<c:out value="${status.expression}"/>" 
              name="<c:out value="${status.expression}"/>"
              value="<c:out value="${status.value}"/>" 
              <c:if test="${empty status.errorMessage}">class="required"</c:if>
              <c:if test="${!empty status.errorMessage}">class="required errFld"</c:if>
       />
       <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
        <span>Example: DBLP</span>
       </div>
       <br />
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
        <c:out value="${status.errorMessage}"/>
       </span>
      </div>
     </spring:bind>
     <spring:bind path="editLinkType.link.baseURL">
      <div class="oneField">
       <label for="<c:out value="${status.expression}"/>" class="preField">Base URL&nbsp;
        <span class="reqMark">*</span>
       </label>
       <input type="text" size="40" maxlength="50" 
              id="<c:out value="${status.expression}"/>" 
              name="<c:out value="${status.expression}"/>"
              value="<c:out value="${status.value}"/>" 
              <c:if test="${empty status.errorMessage}">class="required"</c:if>
              <c:if test="${!empty status.errorMessage}">class="required errFld"</c:if>
       />
       <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
        <span>http://http://www.informatik.uni-trier.de/~ley/</span>
       </div>
       <br />
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
        <c:out value="${status.errorMessage}"/>
       </span>
      </div>
     </spring:bind>
     <spring:bind path="editLinkType.oldLabel">
       <input type="hidden" id="<c:out value="${status.expression}"/>" 
              name="<c:out value="${status.expression}"/>"
              value="<c:out value="${status.value}"/>" />
     </spring:bind>
     <div class="actions">
      <input type="submit" class="primaryAction" id="submit-" name="submit" value="submit" />
      <c:if test="${!editLinkType.newLinkType}">
       &nbsp;
       <a href="<c:url value="/myciteseer/action/admin/deleteExternalLinks?id=${editLinkType.link.label}"/>" title="Delete <c:out value="${editLinkType.link.label}"/>">Delete</a>
      </c:if>
     </div>
    </fieldset>
    </form>
    <c:if test="${!empty editLinkType.links}">
     <table  class="datatable">
      <thead>
       <tr>
        <th>Label</th><th>Base URL</th>
       </tr>
      </thead>
      <tbody>
      <c:forEach var="link" items="${editLinkType.links}" varStatus="status">
       <c:if test="${(status.count%2)==0}">
        <tr class="even">
       </c:if>
       <c:if test="${(status.count%2)!=0}">
        <tr class="odd">
       </c:if>
        <td>
         <a href="<c:url value="/myciteseer/action/admin/editExternalLinks?id=${link.label}"/>">
          <c:out value="${link.label}"/>
         </a>
        </td>
        <td><c:out value="${link.baseURL}"/></td>
       </tr>
       </c:forEach>
      </tbody>
     </table>
    </c:if>
   </div> <!-- End content -->
  </c:if>

    </div> <!-- End column-one-content -->
  </div>

  </div>

  <%@ include file="../../shared/IncludeFooter.jsp" %>