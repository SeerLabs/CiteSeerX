<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent clearfix">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <h2 class="char_headers">My Submissions</h2>
  <div class="content">
   <c:if test="${!empty submissions}">
    <table class="datatable">
     <thead><tr><th>URL</th><th>Time Submitted</th></tr></thead>
     <tbody>
      <c:forEach var="submission" items="${submissions}" varStatus="status">
       <c:if test="${(status.count%2)==0}">
        <tr class="even">
       </c:if>
       <c:if test="${(status.count%2)!=0}">
        <tr class="odd">
       </c:if>
        <td><a href="<c:url value="/myciteseer/action/viewSubmissionDetails?subid=${submission.jobID}"/>"><c:out value="${submission.url}"/></a></td>
        <td><c:out value="${submission.time}"/></td>
       </tr>
      </c:forEach>
     </tbody>
    </table>
   </c:if>
   <c:if test="${empty submissions}">No submissions were found.</c:if>
  </div> <!-- End content -->

</div> <!-- End column-one-content -->
 </div> <!-- End column-one (center column) -->
 <div class="column-two-sec"> <!-- Left column -->
   <%@ include file="IncludeLeftSubmissions.jsp" %>
 </div> <!-- End column-two (Left column) -->
 <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
</div><!-- End columns-float -->
 <div class="column-three-sec"> <!-- right column -->
  <div class="column-three-content"></div>
 </div> <!-- End column-three -->
 <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
 <div class="nn4clear">&nbsp;</div><!-- # needed for NN4 to clear all columns || not needed by any other browser -->
</div> <!-- End mypagecontent -->

<script type="text/javascript">
<!--
if (window != top) 
 top.location.href = location.href;
function sf(){}
function sa(){
 var elt = document.getElementById("submissions_tab");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");

 elt = document.getElementById("view_submissions");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}
// -->
</script>
<%@ include file="../shared/IncludeFooter.jsp" %>