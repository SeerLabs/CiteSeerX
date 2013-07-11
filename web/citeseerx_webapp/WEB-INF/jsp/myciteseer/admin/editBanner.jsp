<%@ include file="../shared/IncludeMyCSXHeader.jsp" %>
<div id="content">
  <h2>Administration</h2>
  <%@ include file="IncludeLeftAdmin.jsp" %>

  <div id="body"> <!-- center column -->

    <form method="post" 
          action="<c:url value="/myciteseer/action/admin/editBanner"/>" 
          class="wform labelsLeftAligned hintsTooltip">
     <fieldset class="">
      <legend>Edit Site Banner</legend>
      <spring:bind path="editBannerForm.banner">
       <div class="oneField">
        <textarea class="banner_edit" cols="1" rows="3"
                  id="<c:out value="${status.expression}"/>" 
                  name="<c:out value="${status.expression}"/>"><c:out value="${status.value}"/></textarea>
       </div>
      </spring:bind>
      <div class="actions">
       <input type="submit" class="primaryAction" id="submit-" name="submit" value="submit" />
      </div>
     </fieldset>
    </form>

  </div> <!-- End column-one-content -->
</div>

</div>

<%@ include file="../../shared/IncludeFooter.jsp" %>