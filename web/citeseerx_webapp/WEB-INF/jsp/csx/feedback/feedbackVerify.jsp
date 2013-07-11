<%@ include file="../shared/IncludeHeader.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li id="current"><a class="page_tabs remove" href="<c:url value="/feedback"/>"><span>Feedback</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->
  
  <div id="primary_content">
   <div id="main_content" class="clearfix">
   
   <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">Verify Your Message</em></span></p>
    <div class="pushdown clearfix">
     <form id="feedbackform" action="<c:url value="/feedback"/>" method="post" class="labelsRightAligned">
      <fieldset id="feedback">
       <!--  <legend>Feedback</legend> -->
       <div class="oneField">
        <label class="preField char_bold">Name:&nbsp;</label>
        <span class="verification"><c:out value="${ name }"/></span>
        <input type="hidden" id="name" name="name" value="<c:out value="${name}"/>" />
       </div>
       <div class="oneField">
        <label class="preField char_bold">Email:&nbsp;</label>
        <span class="verification"><c:out value="${addr}"/></span>
        <input type="hidden" id="addr" name="addr" value="<c:out value="${addr}"/>" />
       </div>
       <div class="oneField pushdown">
        <label class="preField char_bold">Subject:&nbsp;</label>
        <span class="verification"><c:out value="${subj}"/></span>
        <input type="hidden" id="subj" name="subj" value="<c:out value="${subj}"/>" />
       </div>
       <div class="oneField">
        <label class="preField char_bold">Message&nbsp;</label><br />
        <div class="verification"><c:out value="${msg}"/></div>
        <input type="hidden" id="msg" name="msg" value="<c:out value="${msg}"/>" />
       </div>
      </fieldset>
      <div class="pushdown actions">
       <input class="button" type="submit" value="Send Feedback" name="submit" title="Send your message to the CiteSeerX team." />
      </div>
      <div><input type="hidden" name="rt" value="send" /></div>
     </form>
    </div>
   </div> <!-- End main content -->            
  </div> <!-- End primary_content -->
 </div> <!-- End primary_tabs-n-content -->
<script type="text/javascript">
<!--
if (window != top) 
top.location.href = location.href;
function sf(){}
// -->
</script>
<%@ include file="../../shared/IncludeFooter.jsp" %>