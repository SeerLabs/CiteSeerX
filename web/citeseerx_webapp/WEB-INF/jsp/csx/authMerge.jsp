<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="../shared/IncludeHeader.jsp" %>
<div id="main">
<%@ include file="../shared/IncludeSearchBox.jsp" %>

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

		<h3>Correct metadata errors for this author (login required)</h3>
		<!--p class="char_increased">......</p-->

		<h4><font color="red"><c:out value="${ message }"/></font></h4>

		<br/>		
		<p><font color="red">Fields marked with<span class="reqMark">*</span> are required to be non-empty.</font></p>

        <div id="textfields" class="fieldSet">
		<form id="correctionForm" method="post" action="" class="wform labelsRightAligned">
		<fieldset>
		<h3>Primary Author</h3>
		<table>
		<spring:bind path="command.uauth.canname">
	        <tr><td><label for="<c:out value="${ status.expression }"/>" class="preField">Canonical Name*:</label></td>
			<td><input type="text" size="100" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/></td></tr>
		</spring:bind>

		<spring:bind path="command.uauth.affil">
	        <tr><td><label for="<c:out value="${ status.expression }"/>" class="preField">Affiliation*:</label></td>
			<td><input type="text" size="100" id="<c:out value="${ status.expression }"/>" name="<c:out value="${ status.expression }"/>" value="<c:out value="${ status.value }"/>" class=""/></td></tr>
		</spring:bind>

		<spring:bind path="command.uauth.ndocs">
	        <tr><td><label for="<c:out value="${ status.expression }"/>" class="preField">#Publications:</label></td>
			<td><c:out value="${ status.value }"/></td></tr>
		</spring:bind>

		<spring:bind path="command.uauth.hindex">
	        <tr><td><label for="<c:out value="${ status.expression }"/>" class="preField">H-index:</label></td>
			<td><c:out value="${ status.value }"/></td></tr>
		</spring:bind>
		</table>
		</fieldset>

		<input type="submit" class="primaryAction" id="submit" name="correctInfo" value="Correct Metadata" />
		<br><br>

		<c:set var="limit" value="15" />
        <c:if test="${ param.list == 'full' }">
        <c:set var="limit" value="${ command.ndocs }" />
        </c:if>

		<h3>Top publications</h3><br>
		<table class="refs">
		<tr><td></td><td>#Cites</td><td>Title</td></tr>
		<c:forEach var="doc" items="${ command.docs }" begin="0" end="${limit - 1}">
			<tr>
			<td><input type="checkbox" name="<c:out value="${ doc.cluster }"/>" 
				       value="<c:out value="${ doc.cluster }"/>" /></td>
			<td><font color="purple"><c:out value="${ doc.ncites }"/></font></td>
			<td><a href="<c:url value="/viewdoc/summary?doi=${ doc.doi }"/>"><c:out value="${ doc.title }"/></a> - <c:out value="${ doc.year }"/></td>
			</tr>
		</c:forEach>
		</table>

        <c:if test="${ command.ndocs > 15 }">
        <c:choose>
 		  <c:when test="${param.list=='full'}">
			<c:url value="authmerge" var="editUrl">
			  <c:param name="aid" value="${ param.aid }"/>
			  <c:if test="${!empty param.query}"><c:param name="query" value="${ param.query }"/></c:if>
			  <c:if test="${!empty param.cstart}"><c:param name="cstart" value="${ param.cstart }"/></c:if>
			</c:url>
		  	<p><a class="link" href="<c:out value="${editUrl}"/>">View shorten publications << </a></p>
		  </c:when>
		<c:otherwise>
			<c:url value="authmerge" var="editUrl">
			  <c:param name="aid" value="${ param.aid }"/>
			  <c:if test="${!empty param.query}"><c:param name="query" value="${ param.query }"/></c:if>
			  <c:if test="${!empty param.cstart}"><c:param name="cstart" value="${ param.cstart }"/></c:if>
			  <c:param name="list" value="full"/>
			</c:url>
		   <p><a class="link" href="<c:out value="${editUrl}"/>">View completed publications >> </a></p>
		</c:otherwise>
		</c:choose>
		</c:if> 

		<br>
		<input type="submit" class="primaryAction" id="submit" name="removePapers" value="Remove Selected Publications" />
		</form>
		</div>

		<div id="textfields" class="fieldSet">
		<fieldset>
		<h3>Possible Confused Authors</h3>
		<span class="char_decreased"><font color="red">Secondary authors to merge with the primary author</font></span>
		<br>
		<table>
		<span class="oneField">
			<form id="loadingForm" method="get" action="" class="wform labelsRightAligned">
		    <tr><td><label class="preField">Author to Merge:</label></td>
			<input type="hidden" size="20" id="aid" name="aid" value="${ command.uauth.aid }" class=""/>
			<td><input type="text" size="20" id="query" name="query" value="${ command.query }" class=""/>
			<input type="submit" class="primaryAction" id="load" value="Load"/>
			<font color="red">Search for desired authors to merge</font></td></tr>
			</form>
		</span>
		</table>
		</fieldset>

		<form id="mergingForm" method="post" action="" class="wform labelsRightAligned">
		<table class="refs">
        <c:forEach var="auth" items="${ command.candidates }">
			<tr>
			<td>
                <!-- input type="radio" name="gender" value="F" /></td -->
				<input type="radio" name="merge_author" value="<c:out value="${ auth.aid }"/>"  /></td>
			<td width="120"><font color="purple"><a href="viewauth/summary?aid=<c:out value="${ auth.aid }" />"><c:out value="${ auth.canname }"/></a></font></td>
			<td width="600"><font><c:out value="${ auth.affil }"/></font></td>
			<td><font><c:out value="${ auth.ndocs }"/> pubs</font></td>
			</tr>
		</c:forEach>
		<tr><td></td>
			<td></td>
			<td></td></tr>
		</table>
		<br>
		<c:if test="${ command.nextCandidatePage != null }">
        <div style="margin:0 0 0 30pt;">
 			<a href="authmerge?<c:out value="${ command.nextCandidatePage }" /> "> Next Page >> </a>
        </div>
		</c:if>

		<br>
		<input type="submit" class="primaryAction" id="submit" name="mergeAuthors" value="Merge selected authors" />
		</form>
		</div>
	</c:if>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>