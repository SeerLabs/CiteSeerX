<%@ include file="../shared/IncludeMyCSXHeader.jsp" %>
<div id="content">
  <h2>Administration</h2>
  <%@ include file="IncludeLeftAdmin.jsp" %>

  <div id="body"> <!-- center column -->
    <fieldset class="">
    <!-- know this is not good, but clobbering it anyway in -->
	<c:choose>
  	<c:when test="${ error }">
		<div><c:out value="${ errMsg }"/></div>
  	</c:when>
  	<c:otherwise>
		<div>
		<table>
		<tbody>
		<tr class="odd"><td colspan="2" bgcolor="#DEDEDE">
		Collection Statistics
		</td>
		</tr>
      		<tr class="even"><td width="30%">
		<font size="3">
		Total Number of Documents
		</font>
		<br/><font size="1">
		&nbsp;Represents the total number of documents in the collection
		</font>
		</td>
		<td class="tdnumber">
		<font size="3">
		<c:out value="${ totaldocuments }"/>
		</font>
		</td>
		</tr>
		<tr class="odd"><td width="30%">
		<font size="3">
		Total Number of Citations
		</font>
		<br/><font size="1">
                &nbsp;Represents the total number of citations (bibliographic records) 
		in the collection
                </font>
		</td>
		<td class="tdnumber">
		 <font size="3">
		<c:out value="${ totalcitations }"/>
		 </font>
		</td>
		</tr>
		<tr class="even">
		<td width="30%">
		<font size="3">
		Accessible Documents
		</font>
		<br/><font size="1"> 
                &nbsp;Represents the number of documents with status set to
                public (always less then or equal to the the total)
                </font>
		</td>
		<td class="tdnumber">
		<font size="3">
		<c:out value = "${ publicdocuments }"/>
		</font>
		</td>
		</tr>
		<tr class="odd">
		<td width="30%">
		<font size="3">
		Authors in Collection
		</font>
		<br/><font size="1">
                &nbsp;Represents the number of authors identified in the
		collection.
                </font>
		</td>
		<td class="tdnumber">
		<font size="3">
		<c:out value="${ totalauthors }"/>
		</font>
		</td>
		</tr>
		<tr class="even">
		<td width="30%">
		<font size="3">
		Unique Authors
		</font>
		<br/><font size="1">
		&nbsp;Represents the number of unique authors identified by
		name (variations are counted separately).
		</font>
		</td>
		<td class="tdnumber">
		<font size="3">
		<c:out value = "${ uniqueauthors }"/>
		</font>
		</td>
		</tr>
		<tr class="odd">
		<td width="30%">
		<font size="3">
		Disambiguated Authors
		</font>
		<br/><font size="1">
		&nbsp;Represents the number of disambiguated authors identified
		by the disambiguation system
		</font>
		</td>
		<td class="tdnumber">
		<font size="3">
		<c:out value="${ disambiguatedauthors }"/>
		</font>
                </td>
		</tr>
		<tr class="even">
		<td width="30%">
		<font size="3">
		Unique Records (Bibliographic Items)
		</font>
                <br/><font size="1">
                &nbsp;Represents the number of records (citations/documents) identified
		as unique.
                </font>
		</td>
		<td class="tdnumber">
		<font size="3">
		<c:out value = "${ uniquerecords }"/>
		</font>
		</td>
		</tr>
		<tr class="odd">
		<td width="30%">
		<font size="3">
		Unique Public Documents
		</font>
                <br/><font size="1">
                &nbsp;Represents the number of unique documents accessible publicly.
                </font>
		</td>
		<td class="tdnumber">
		<font size="3">
		<c:out value=" ${ uniquepublicrecords }"/>
		</font>
		</td>
		</tr>
		</tbody>
		</table>
		</div>
  	</c:otherwise>
	</c:choose>
   </fieldset>

  </div> <!-- End column-one-content -->
</div>


<%@ include file="../../shared/IncludeFooter.jsp" %>
