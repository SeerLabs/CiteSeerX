<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent clearfix">
<div class="columns-float-sec">
<div class="column-one-sec"> <!-- center column -->
 <div class="column-one-content clearfix">
  <form method="post" action="<c:url value="/myciteseer/action/submitUrl"/>" class="wform labelsLeftAligned hintsTooltip">
   <fieldset id="tfa_UrlSubmissions" class="">
    <legend>Submit a URL</legend>
    <div class="oneField">
     <spring:bind path="urlSubmissionForm.urlSubmission.url">
      <label for="<c:out value="${status.expression}"/>" class="preField">URL:&nbsp;
       <span class="reqMark">*</span>
      </label>
      <input type="text" size="40"  
             id="<c:out value="${status.expression}"/>" 
             name="<c:out value="${status.expression}"/>"
             <c:if test="${empty status.errorMessage}">value="" class="required"</c:if>
             <c:if test="${!empty status.errorMessage}">value="<c:out value="${status.value}"/>" class="required errFld"</c:if>
      />
      <div class="field-hint-inactive" id="<c:out value="${status.expression}"/>-H">
       <span>Submit a new URL</span>
      </div><br />
      <span class="errMsg" id="<c:out value="${status.expression}"/>-E">
       <c:out value="${status.errorMessage}"/>
      </span>
     </spring:bind>
    </div>
   </fieldset>
   <div class="actions">
    <input type="submit" class="primaryAction" id="submit-" name="submitAction" value="submit" />
   </div>
  </form>

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

 elt = document.getElementById("submit_url");
 elt.setAttribute("class", "active");
 elt.setAttribute("className", "active");
}
// -->
</script>
    
<%@ include file="../shared/IncludeFooter.jsp" %>
