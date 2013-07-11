<%@ include file="../shared/IncludeHeader.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/help/search"/>"><span>Search Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/site"/>"><span><fmt:message key="app.name"/> Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/myciteseer"/>"><span><fmt:message key="app.portal"/> Help</span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/help/submit"/>"><span>Submissions</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/glossary"/>"><span>Glossary</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/faq"/>"><span>FAQ</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/api"/>"><span>API</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->

  <div id="primary_content">
   <div id="main_content" class="clearfix">  
    <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">Submitting content to <fmt:message key="app.nameHTML"/></em></span></p>

    <h2><span class="char_emphasized">NOTE:</span> The submission system will be disabled throughout the alpha deployment phase.  An announcement
     will be made on the <a href="<fmt:message key="app.bulletin"/>">bulletin</a> when the system is ready for use.</h2>

    <p class="para_book">Users are encouraged to submit content that they deem appropriate to the <fmt:message key="app.nameHTML"/> collection.
      We require that all content be submitted through links to publicly accessible documents on the Web.  Once we receive
      a link submission, that link will be queued for crawling and processed dynamically.
    </p>
    <h2 class="topic_heading pushdown">Overview</h2>
    <p class="para_book">
      A user account will be required for all submissions, and the submission interface can be found as a navigation tab within
      the <a href="<c:url value="/myciteseer/action/accountHome"/>"><fmt:message key="app.portal"/></a> application.  Once a URL is submitted, it will be
      crawled to a depth of 1 for PDF and PostScript files.  These files may be compressed with zip, gzip, or compress formats.
      Any matching files will be downloaded and queued for processing within our
      ingestion pipeline, at which point our parsers will attempt to extract the text from the documents, filter the text for relevance,
      convert to PDF if necessary,
      and extract metadata from the document headers and reference sections.  Any errors that are encountered during the
      crawl or extraction process will be reported within the <fmt:message key="app.portal"/> submission sub-application.  Documents that are
      successfully added to the repository will also be reported, with a link to the document summary page.
    </p>
    <h2 class="topic_heading pushdown">Supported File Formats</h2>
    <ul class="formating pushdown">
      <li><span class="char_emphasized">PDF:</span> (Recommended) We are generally able to convert PDF documents in such a way as to preserve UTF-8 character codes.  Therefore, we recommend submitting content in this format particulary if your files contain characters that cannot be correctly represented within the ASCII character set.</li>
      <li><span class="char_emphasized">PS:</span> We do support PostScript files; however, text conversion will be limited to ASCII-only due to limitations in standard PostScript text extractors.</li>
      <li><span class="char_emphasized">(ZIP|GZ|Z):</span> Common compression formats such as zip, gzip, and UNIX compress are all supported.</li>
    </ul>
    <h2 class="topic_heading pushdown">Getting Started</h2>
    <p class="para_book">
      You may <a href="<c:url value="/myciteseer/action/submitUrl"/>">submit your URLs here</a> when ready.  If you are not currently signed in, you will be redirected to a login page where
      you will be asked to sign in or create an account if you do not already have one.
    </p>
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