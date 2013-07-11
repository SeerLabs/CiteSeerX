<%@ include file="../shared/IncludeMyCSXHeader.jsp" %>
<div id="content">
  <h2>Administration</h2>
  <%@ include file="IncludeLeftAdmin.jsp" %>

  <div id="body"> <!-- center column -->
  <c:if test="${ error }"><c:out value="${ errMsg }"/></c:if>
  <c:if test="${ ! error && ! empty msg }"><c:out value="${ msg }"/></c:if>
  <form method="post" 
        action="<c:url value="/myciteseer/action/admin/invite"/>" 
        class="wform labelsLeftAligned hintsTooltip">
   <fieldset class="">
    <legend>Invite Users</legend>
    <div>Enter one or more email addresses below to invite users to join the system.</div>
    <div class="oneField">
     <textarea class="invite_field" cols="1" rows="3" name="invite"></textarea>
    </div>
    <div>Enter addresses you would like to CC.</div>
    <div class="oneField">
     <input type="text" class="invite_field" name="alsocc" value="giles@ist.psu.edu"/>
    </div>
    <div>Enter a supplementary message below.</div>
    <div class="oneField">
     <textarea class="invite_field" name="message" cols="1" rows="7">
You have been invited to join the CiteSeerX alpha preview!  Until we get the feedback system installed, please email Isaac Councill (icouncill@ist.psu.edu) and Lee Giles (giles@ist.psu.edu) directly with feedback.

Expect lots of additions and upgrades in the next few weeks, including lots of new documents.

Enjoy!
   Isaac
     </textarea>
    </div>
    <div class="oneField">
     <label>CC me </label><input type="checkbox" name="cc" checked="checked" />
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