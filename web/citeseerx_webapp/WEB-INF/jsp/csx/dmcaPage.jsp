<%--
  -- Shows a DOI error when trying to access a document
  --
  -- Author: Isaac Councill
  -- Author: Juan Pablo Fernandez Ramirez
  --%>
<%@ include file="../shared/IncludeHeader.jsp" %>
  <div id="center_content" class="clearfix"> <!-- Contains header div -->
    <div id="primary_content">
      <div id="main_content">
        <div class="inside pushdown">
          <div class="error">
            <h3>The document with the identifier "<c:out value="${doi}"/>"
                has been removed due to a DMCA takedown notice. If you believe
		the removal has been in error, please contact us through the
		feedback page, along with the identifier mentioned in this page.
	   </h3>
          </div>
        </div>
      </div> <!-- End main_content -->
    </div> <!-- End primary_content -->
  </div> <!-- End center-content -->
<%@ include file="../shared/IncludeFooter.jsp" %>
