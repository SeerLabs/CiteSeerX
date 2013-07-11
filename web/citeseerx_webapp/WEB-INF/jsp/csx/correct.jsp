<%@ include file="shared/IncludeDocHeader.jsp" %>
  <div id="viewContent">
 <div id="right-sidebar"> <!-- Contains left content -->
  <div class="inside"> <!-- to give some room between columns -->
  </div> <!--End inside -->
 </div> <!-- End of right-sidebar --> 
 <br/>
 <c:if test="${ ! correctionsEnabled }">
   <h3><font color="red">Corrections are currently disabled!</font></h3>
 </c:if>
 <c:if test="${ correctionsEnabled }">
   <c:if test="${ error }">
     <h3><font color="red"><c:out value="${ errMsg }"/></font></h3>
   </c:if>
   <h3>Correct metadata errors for this document (login required)</h3>
   <p class="char_increased">It may be helpful to open the document via the "View/Download" button
   above.</p>
   <br/>
   <p class="char_increased">Fields marked with<span class="reqMark">*</span> are required.</p>
  
   <spring:bind path="correction.*">
    <c:forEach var="error" items="${status.errorMessages}">
     <br/><span class="errMsg"><c:out value="${error}"/></span>
    </c:forEach>
   </spring:bind>
   
   <form id="correctionForm" method="post" action="" class="wform labelsRightAligned">
    <fieldset>
     <legend>Correction Form</legend>
     <table width="100%"><tr>
     <span class="oneField">
	
      <spring:bind path="correction.title">
	<td class="tdfixedwidth">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Title:&nbsp;<span class="reqMark">*</span></label>
	</td>
        <td>
       <input type="text" size="80" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" />
	</td>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
      </spring:bind>
     </span>
      </tr>
     </table>
     <fieldset id="authorList">
      <legend><b>Authors: Please preserve correct order</b></legend>
      <div id="repeat_block">
      <fieldset id="author_0" class="repeat">
	<table width="100%"><tr>
        <td colspan="2" class="tdrightalign">
	<span class="removeLink">
                <span class="actionspan" onclick="moveAuthor(0, 'down');">Move Down 
			<img src="<c:url value='/images/arrow-asc.png'/>" alt="<fmt:message key="Move Down"/>" />
                </span>
        </span>
	</td>
	</tr>
	<tr>
        <span class="oneField">
         <spring:bind path="correction.authors[0].name">
	  <td class="tdfixedwidth">
          <label for="<c:out value="${ status.expression }"/>" class="preField">&nbsp;Name:&nbsp;<span class="reqMark">*</span></label>
	  </td>
	  <td>
          <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
          <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	 </td>
         </spring:bind>
        </span>
	</tr>
	<tr>
        <span class="oneField">
         <spring:bind path="correction.authors[0].affil">
	  <td class="tdfixedwidth">
          <label for="<c:out value="${ status.expression }"/>" class="preField">&nbsp;Affiliation:</label>
	  </td>
	  <td>
          <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
          <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	  </td>
         </spring:bind>
        </span>
	</tr>
	<tr>
        <span class="oneField">
         <spring:bind path="correction.authors[0].address">
	  <td class="tdfixedwidth">
          <label for="<c:out value="${ status.expression }"/>" class="preField">&nbsp;Address:</label>
	  </td>
	  <td>
          <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
          <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	  </td>
         </spring:bind>
        </span>
	</tr>
	<tr>
        <span class="oneField">
         <spring:bind path="correction.authors[0].email">
	  <td class="tdfixedwidth">
          <label for="<c:out value="${ status.expression }"/>" class="preField">&nbsp;Email:</label>
	  </td>
	  <td>
          <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
          <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	  </td>
         </spring:bind>
        </span>
	</tr>
	     
        <spring:bind path="correction.authors[0].order">
         <c:if test="${ ! empty status.value }">
          <input type="hidden" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
         </c:if>
         <c:if test="${ empty status.value }">
          <input type="hidden" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="1"/>
         </c:if>
        </spring:bind>      
        <spring:bind path="correction.authors[0].deleted">
         <input type="hidden" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
        </spring:bind>
      </fieldset>
      </table>
      <c:forEach var="counter" begin="1" end="${ correction.numberOfAuthors - 1 }">
       <fieldset id="author_<c:out value="${ counter }"/>" class="removeable" <c:if test="${ correction.authors[counter].deleted }">style="display:none"</c:if>>
	<table width="100%"><tr> 
	<td colspan="2" class="tdrightalign">
	<span class="removeLink"><span class="actionspan" onclick="moveAuthor(<c:out value="${ counter }"/>, 'up');">
        Move Up
        <img src="<c:url value='/images/arrow-desc.png'/>" alt="<fmt:message key="Move Up"/>" />
        </span> |
        <span class="actionspan" onclick="moveAuthor(<c:out value="${ counter }"/>, 'down');">Move Down
        <img src="<c:url value='/images/arrow-asc.png'/>" alt="<fmt:message key="Move Down"/>" />
        </span></span>
	</td>
	</tr>
	<tr>
       <span class="oneField">
          <spring:bind path="correction.authors[${ counter }].name">
         <td class="tdfixedwidth">  
	 <label for="<c:out value="${ status.expression }"/>" class="preField">&nbsp;Name:&nbsp;<span class="reqMark">*</span></label>
	  </td>
	  <td>
           <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
           <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	  </td>
	
          </spring:bind>
         </span>
	</tr>
	<tr>
         <span class="oneField">
          <spring:bind path="correction.authors[${ counter }].affil">
	   <td class="tdfixedwidth">
           <label for="<c:out value="${ status.expression }"/>" class="preField">&nbsp;Affiliation:</label>
	   </td>
	   <td>
           <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
           <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	   </td>
          </spring:bind>
         </span>
	</tr>
	<tr>
         <span class="oneField">
          <spring:bind path="correction.authors[${ counter }].address">
	   <td class="tdfixedwidth">
           <label for="<c:out value="${ status.expression }"/>" class="preField">&nbsp;Address:</label>
	   </td>
	   <td>
           <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
	   
           <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	   </td>
          </spring:bind>
         </span>
	</tr>
	<tr>
         <span class="oneField">
          <spring:bind path="correction.authors[${ counter }].email">
	   <td class="tdfixedwidth">
           <label for="<c:out value="${ status.expression }"/>" class="preField">&nbsp;Email:</label>
	   </td>
	   <td>
           <input type="text" size="40" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
           <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	   </td>
          </spring:bind>
         </span>
	 </tr>
	<tr>
	 <td colspan="2" class="tdrightalign">
         <spring:bind path="correction.authors[${ counter }].order">
          <input type="hidden" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
         </spring:bind>
         <spring:bind path="correction.authors[${ counter }].deleted">
          <input type="hidden" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/>
         </spring:bind>
         <span class="removeLink actionspan" onclick="deleteSection('author_<c:out value="${ counter }"/>', 'authors[<c:out value="${ counter }"/>].deleted');">Remove This Author</span>
	 </td>
        </tr>
	</table>
       </fieldset>
      </c:forEach>
      </div>
	<p align="right">
      <span class="duplicateLink actionspan" onclick="repeat('repeat_block');">Add Another Author</span>
	</p>
     </fieldset>
    <table width="100%"><tr> 
     <span class="oneField">
      <spring:bind path="correction.abs">
	<td class="tdfixedwidth">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Abstract:</label>
	</td><td>
       <textarea rows="10" cols="60" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>"><c:out value="${ status.value }"/></textarea>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	</td>
      </spring:bind>
     </span>
      </tr>
      </table>
     <table width="100%"><tr>
     <span class="oneField">
      <spring:bind path="correction.venue">
	<td class="tdfixedwidth">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Publication Venue:</label>
	 </td><td>
       <input type="text" size="50" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	</td>
      </spring:bind>
     </span>
     </tr>
      </table>
      <table width="100%"><tr>
     <span class="oneField">
      <spring:bind path="correction.venType">
	<td class="tdfixedwidth">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Venue Type:</label>
        </td><td>
       <input type="text" size="50" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	</td>
      </spring:bind>
     </span>
     </tr>
     </table>
     <table width="100%"><tr>	
     <span class="oneField">
      <spring:bind path="correction.year">
         <td class="tdfixedwidth">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Publication Year:</label>
         </td><td>
       <input type="text" size="4" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	</td>
      </spring:bind>
     </span>
     </tr>
     </table>
     <table width="100%"><tr>
     <span class="oneField">
      <spring:bind path="correction.vol">
	 <td class="tdfixedwidth">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Volume:</label>
	 </td><td>
       <input type="text" size="4" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
	
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	</td>
      </spring:bind>
     </span>
	</tr>
     </table>
     <table width="100%"><tr>
     <span class="oneField">
      <spring:bind path="correction.num">
	<td class="tdfixedwidth">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Number:</label>
	</td><td>
       <input type="text" size="4" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	</td>
      </spring:bind>
     </span>
	</tr>
     </table>
     <table width="100%"><tr>
     <span class="oneField">
      <spring:bind path="correction.pages">
	<td class="tdfixedwidth">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Pages:</label>
	</td><td>
       <input type="text" size="20" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	</td>
      </spring:bind>
     </span>
	 </tr>
     </table>
     <table width="100%"><tr>
     <span class="oneField">
      <spring:bind path="correction.publisher">
	<td class="tdfixedwidth">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Publisher:</label>
	</td><td>
       <input type="text" size="50" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	</td>
      </spring:bind>
     </span>
      </tr>
     </table>
     <table width="100%"><tr>
     <span class="oneField">
      <spring:bind path="correction.pubAddr">
	<td class="tdfixedwidth">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Publisher Address:</label>
        </td><td>
       <input type="text" size="50" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	</td>
      </spring:bind>
     </span>
	 </tr>
     </table>
     <table width="100%"><tr>
     <span class="oneField">
      <spring:bind path="correction.tech">
	<td class="tdfixedwidth">
       <label for="<c:out value="${ status.expression }"/>" class="preField">Tech Report Number:</label>
        </td><td>
       <input type="text" size="50" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
       <span class="errMsg" id="<c:out value="${status.expression}"/>-E"><c:out value="${status.errorMessage}"/></span>
	</td>
      </spring:bind>
     </span>
	</tr>
	</table>
     <spring:bind path="correction.paperID">
       <input type="hidden" id="<c:out value="${ status.expression}"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>"/>
     </spring:bind>
     <input type="hidden" id="doi" name="doi" value="<c:out value="${ correction.paperID }"/>"/>
    </fieldset>
    <div class="actions">
     <input type="submit" class="primaryAction" id="submit-" name="submitAction" value="Submit Correction" />
    </div>
   </form>
   <div id="repeatCounter" style="display:none;"><c:out value="${ correction.numberOfAuthors }"/></div>
  </c:if>
<br/><br/>
  </div><%-- viewContent close div --%>
  <div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>
