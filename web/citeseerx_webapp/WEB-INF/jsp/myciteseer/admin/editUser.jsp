<%@ include file="../shared/IncludeMyCSXHeader.jsp" %>
<div id="content">
  <h2>Administration</h2>
  <%@ include file="IncludeLeftAdmin.jsp" %>

  <div id="body"> <!-- center column -->
  <c:if test="${ error }"><div><c:out value="${ errMsg }"/></div></c:if>
  <form method="get" 
        action="<c:url value="/myciteseer/action/admin/editUser"/>" 
        class="wform labelsLeftAligned hintsTooltip">
   <fieldset class="">
    <legend>Edit User</legend>
    <table class="metatable">
     <tbody>
      <tr><td>User ID:</td><td><c:out value="${ editaccount.username }"/></td></tr>
      <tr><td>First Name:</td><td><c:out value="${ editaccount.firstName }"/></td></tr>
      <tr><td>Middle Name:</td><td><c:out value="${ editaccount.middleName }"/></td></tr>
      <tr><td>Last Name:</td><td><c:out value="${ editaccount.lastName }"/></td></tr>
     </tbody>
    </table>
    <div class="oneField">
     <label class="preField">Enabled:</label>
     <c:if test="${ enabled }"><input type="checkbox" name="setenabled" checked="checked" /></c:if>
     <c:if test="${ !enabled }"><input type="checkbox" name="setenabled" /></c:if>
    </div>
    <div class="oneField">
     <label class="preField">Administrator:</label>
     <c:if test="${ admin }"><input type="checkbox" name="setadmin" checked="checked" /></c:if>
     <c:if test="${ !admin }"><input type="checkbox" name="setadmin" /></c:if>
     <input type="hidden" name="uid" value="<c:out value="${ editaccount.username }"/>"/>
     <input type="hidden" name="type" value="update"/>
    </div>
    <div class="actions">
     <input type="submit" class="primaryAction" id="submit-" name="submit" value="submit" />
    </div>
   </fieldset>
  </form>


    </div> <!-- End column-one-content -->
  </div>

  </div>

  <%@ include file="../../shared/IncludeFooter.jsp" %>