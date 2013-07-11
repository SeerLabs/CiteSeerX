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
            <h3>The document with DOI "<c:out value="${doi}"/>"
            	has been removed. Please contact us through the <a href="http://csxstatic.ist.psu.edu/contact" title="Feedback Page">feedback page</a>, if you believe the removal is in error
	    </h3>
          </div>
        </div>
      </div> <!-- End main_content -->
    </div> <!-- End primary_content -->
  </div> <!-- End center-content -->
<%@ include file="../shared/IncludeFooter.jsp" %>
