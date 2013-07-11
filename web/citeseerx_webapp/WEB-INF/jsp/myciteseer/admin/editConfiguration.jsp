<%@ include file="../shared/IncludeMyCSXHeader.jsp" %>
<div id="content">
  <h2>Administration</h2>
  <%@ include file="IncludeLeftAdmin.jsp" %>

  <div id="body"> <!-- center column -->
  <form method="get" 
        action="<c:url value="/myciteseer/action/admin/editConfiguration"/>" 
        class="wform labelsLeftAligned hintsTooltip">
   <fieldset class="">
    <legend>Edit Configuration</legend>
    <table class="datatable">
     <thead>
      <tr><th>Parameter</th><th>Enabled</th></tr>
     </thead>
     <tbody>
       <tr class="even">
        <td><label class="preField" for="setaccounts">New Accounts</label></td>
        <td>
         <c:if test="${ setaccounts }"><input type="checkbox" name="setaccounts" id="setaccounts" checked="checked"/></c:if>
         <c:if test="${ !setaccounts }"><input type="checkbox" name="setaccounts" id="setaccounts"/></c:if>
        </td> 
       </tr>
       <tr class="odd">
        <td><label class="preField" for="seturlsubmission">URL Submissions</label></td>
        <td>
         <c:if test="${ seturlsubmission }"><input type="checkbox" name="seturlsubmission" id="seturlsubmission" checked="checked"/></c:if>
         <c:if test="${ !seturlsubmission }"><input type="checkbox" name="seturlsubmission" id="seturlsubmission"/></c:if>
        </td> 
       </tr>
       <tr class="even">
        <td><label class="preField" for="setcorrections">Corrections</label></td>
        <td>
         <c:if test="${ setcorrections }"><input type="checkbox" name="setcorrections" id="setcorrections" checked="checked"/></c:if>
         <c:if test="${ !setcorrections }"><input type="checkbox" name="setcorrections" id="setcorrections"/></c:if>
        </td>
       </tr>
       <tr class="odd">
        <td><label class="preField" for="setgroups">Groups</label></td>
        <td>
         <c:if test="${ setgroups }"><input type="checkbox" name="setgroups" id="setgroups" checked="checked"/></c:if>
         <c:if test="${ !setgroups }"><input type="checkbox" name="setgroups" id="setgroups"/></c:if>
        </td> 
       </tr>
       <tr class="even">
        <td><label class="preField" for="setpeoplesearch">People Search</label></td>
        <td>
         <c:if test="${ setpeoplesearch }"><input type="checkbox" name="setpeoplesearch" id="setpeoplesearch" checked="checked"/></c:if>
         <c:if test="${ !setpeoplesearch }"><input type="checkbox" name="setpeoplesearch" id="setpeoplesearch"/></c:if>
        </td>
      </tr>
      <tr class="odd">
        <td><label class="preField" for="setpersonalportal">Personal Portal</label></td>
        <td>
         <c:if test="${ setpersonalportal }"><input type="checkbox" name="setpersonalportal" id="setpersonalportal" checked="checked"/></c:if>
         <c:if test="${ !setpersonalportal }"><input type="checkbox" name="setpersonalportal" id="setpersonalportal"/></c:if>
        </td> 
       </tr>
     </tbody>
    </table>
    <input type="hidden" name="type" value="update"/>
    <div class="actions">
     <input type="submit" class="primaryAction" id="submit-" name="submit" value="submit" />
    </div>
   </fieldset>
  </form>
  </div> <!-- End column-one-content -->
</div>

</div>

<%@ include file="../../shared/IncludeFooter.jsp" %>