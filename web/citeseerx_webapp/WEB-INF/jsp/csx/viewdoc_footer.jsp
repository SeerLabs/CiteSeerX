<%--
  -- This page includes footer specific information view document pages.
  --
  -- Author: Isaac Councill
  --%>
  <div class="information_bar">
    <div class="para4"><a class="remove" href="<c:url value="/viewdoc/download?doi=${ doi }&amp;rep=${ rep }&amp;type=pdf"/>" title="View or Download this Document">View or Download</a> &#124; <a class="remove"  href="<c:url value="/myciteseer/action/addPaperCollection?doi=${doi}"/>">Add to My Collection</a> &#124; <a class="remove" href="<c:url value="/correct?doi=${ doi }"/>" title="Submit corrections for this document">Correct Errors</a></div>
  </div> <!-- End bottom information_bar -->
  <div id="conclusion">
    <div><a href="<c:url value="/viewdoc/similar?doi=${ doi }"/>" title="Find documents related to this document">Related Documents</a>: <a href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=ab"/>" title="Documents that cite the same works">Active Bibliography</a> &#124; <a href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=cc"/>" title="Documents cited by the same works">Co-citation</a><!-- &#124; <a href="#">Version History</a> --></div>					
  </div> <!-- End conclusion -->
  </div> <!-- End primary_content - Opened in viewdoc_header -->
</div> <!-- End center_content - Opened in viewdoc_header -->
<%@ include file="../shared/IncludeBottom.jsp" %>
