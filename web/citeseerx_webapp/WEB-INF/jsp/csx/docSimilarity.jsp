<%@ include file="viewdoc_header.jsp" %>
<div id="main_content">  
 <div id="right-sidebar"> <!-- Contains left content -->
  <div class="inside"> <!-- to give some room between columns -->
  </div> <!--End inside -->
 </div> <!-- End of right-sidebar --> 
 <br/>
 <h2 class="topic_heading">Document Similarity Options:</h2>
 <p class="para4 char_increased"><a href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=ab"/>">Active Bibliography</a> (Documents that cite a similar set of documents)</p>
 <p class="para4 char_increased"><a href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=cc"/>">Co-Citation</a> (Documents cited by a similar set of documents)</p>
 <p class="para4 char_increased"><a href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=sc"/>">Clustered</a> (Different versions of the same document)</p>
</div> <!-- End main_content -->
<%@ include file="viewdoc_footer.jsp" %>
