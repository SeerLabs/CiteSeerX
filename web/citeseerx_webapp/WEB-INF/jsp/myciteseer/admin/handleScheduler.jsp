<%@ include file="../shared/IncludeMyCSXHeader.jsp" %>
<div id="content">
  <h2>Administration</h2>
  <%@ include file="IncludeLeftAdmin.jsp" %>

  <div id="body"> <!-- center column -->
  <c:if test="${ error }"><div><c:out value="${ errMsg }"/></div></c:if>
  <form method="get" 
        action="<c:url value="/myciteseer/action/admin/handleScheduler"/>" 
        class="wform labelsLeftAligned hintsTooltip">
   <fieldset class="">
    <legend>Scheduler</legend>
    <div class="oneField">
     <c:if test="${ running }">
       <label class="preField">Start:</label>
       <input type="radio" name="action" value="start" checked="checked"/><br />
       <label class="preField">Stop:</label>
       <input type="radio" name="action" value="stop"/>
     </c:if>
     <c:if test="${ !running }">
       <label class="preField">Start:</label>
       <input type="radio" name="action" value="start"/><br />
       <label class="preField">Stop:</label>
       <input type="radio" name="action" value="stop" checked="checked"/>
     </c:if>
    </div>
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