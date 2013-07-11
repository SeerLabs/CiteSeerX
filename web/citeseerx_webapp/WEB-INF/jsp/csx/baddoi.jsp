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
            <h1>No document with DOI "<c:out value="${doi}"/>"</h1>
            <h3>The supplied document identifier does not match<br/>
                any document in our repository.</h3>
          </div>
        </div>
      </div> <!-- End main_content -->
    </div> <!-- End primary_content -->
  </div> <!-- End center-content -->
<%@ include file="../shared/IncludeFooter.jsp" %>