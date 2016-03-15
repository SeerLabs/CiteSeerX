<%@ include file="../../shared/IncludeHeader.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li id="current"><a class="page_tabs remove" href="<c:url value="/feedback"/>"><span>Feedback</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->
  
  <div id="primary_content">
   <div id="main_content" class="clearfix">
   
   <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">Help <fmt:message key="app.nameHTML"/> Move Forward</em></span>Share your observations, concerns and suggestions for <fmt:message key="app.nameHTML"/> with the intention of improving our performance as a scientific literature digital library. Encouraging words regarding anything that we are doing right will also help us to continue to provide those features you have enjoyed. When the &quot;Proceed to Verification&quot; button is selected, you will be given the opportunity to approve your message before submitting it.</p>
    <div class="pushdown">
     <c:if test="${ error }">
       <p class="para4" id="feedback_error">Error: <c:out value="${ errMsg }"/></p>
     </c:if>
	 <form id="feedbackform" action="<c:url value="/feedback"/>" method="post" class="labelsRightAligned">
      <fieldset id="feedback">
       <!--  <legend>Feedback</legend> -->
       <div class="oneField">
        <label for="name" class="preField">Name:&nbsp;</label>
        <input type="text" size="30" maxlength="90" 
               id="name" 
               name="name"
               value="<c:out value="${name}"/>" 
        />
        <span class="errMsg" id="name-E"></span>
       </div>
       <div class="oneField">
        <label for="addr" class="preField">Email:&nbsp;</label>
        <input type="text" size="30" maxlength="90" 
               id="addr" 
               name="addr"
               value="<c:out value="${addr}"/>" 
               class=""
        />
        <br />
        <span class="errMsg" id="addr-E"></span>
       </div>
       <div class="oneField pushdown">
        <label for="subj" class="preField">Subject:&nbsp;</label>
        <input type="text" size="30" maxlength="90" 
               id="subj" 
               name="subj"
               value="<c:out value="${subj}"/>" 
               class=""
        />
        <br />
        <span class="errMsg" id="subj-E"></span>
       </div>
       <div class="oneField">
        <label for="msg" class="preField">Message:&nbsp;</label>
        <textarea cols="65" rows="8" id="msg" 
                  name="msg" 
                  class=""
        ><c:out value="${msg}"/></textarea>
        <br />
        <span class="errMsg" id="msg-E"></span>
       </div>
      </fieldset>
      <div class="actions pushdown">
      <input class="button" type="submit" value="Proceed to Verification" name="submit" title="Verification will allow you to approve your message before submitting it." />
     </div>
     <div><input type="hidden" name="rt" value="verify" /></div>
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
