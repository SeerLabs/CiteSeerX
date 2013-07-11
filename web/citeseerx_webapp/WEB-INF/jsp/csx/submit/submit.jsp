<%@ include file="../shared/IncludeHeader.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li id="current"><a class="page_tabs remove" href="<c:url value="/submit"/>"><span>Submit Documents</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->
  
  <div id="primary_content">
 <div id="main_content" class="clearfix">
   <span class="firstletters"><em class="firstletter">Submit Documents to CiteSeer<sup>X</sup>.</em></span>
Users are encouraged to submit content that they deem appropriate to the CiteSeer<sup>x</sup> collection. It is advisable
to check with your co-authors before submission. 
<p class="para_book">
If you do not want your documents crawled by CiteSeer<sup>X</sup>, please use a <a class="remove" href="http://www.robotstxt.org">robots.txt</a>
to disallow our crawler named "citeseerxbot".
We require that all content be submitted through links to publicly accessible documents on the Web.
Please make sure you have provided relevant permissions and 
your <a class="remove" href="http://www.robotstxt.org">robots.txt</a> permits documents to be crawled by our bot "citeseerxbot". 
Once we receive a link submission, that link will be queued for crawling and processed dynamically. 
Allow several weeks before the documents are indexed by CiteSeer<sup>X</sup>.</p>
<h2 class="topic_heading pushdown">Overview</h2>
<p class="para_book">
	Once a URL is submitted, it will be crawled to a depth of 1 for PDF and PostScript files.  
These files may be compressed with zip, gzip, or compress formats.
Any matching files will be downloaded and queued for processing within our
ingestion pipeline, at which point our parsers will attempt to extract the text from the documents, filter the text for relevance,
convert to PDF if necessary, and extract metadata from the document headers and reference sections.  
</p>
<h2 class="topic_heading pushdown">Supported File Formats</h2>
<ul class="formating pushdown">
<li><span class="char_emphasized">PDF:</span> (Recommended) We are generally able to convert PDF documents in such a way as to preserve UTF-8 character codes.  Therefore, we recommend submitting content in this format particulary if your files contain characters that cannot be correctly represented within the ASCII character set.</li>
<li><span class="char_emphasized">PS:</span> We do support PostScript files; however, text conversion will be limited to ASCII-only due to limitations in standard PostScript text extractors.</li>
<li><span class="char_emphasized">(ZIP|GZ|Z):</span> Common compression formats such as zip, gzip, and UNIX compress are all supported.</li>
</ul>

    <div class="pushdown">
     <c:if test="${ error }">
       <p class="para4" id="submission_error" style="color:red"><c:out value="${ errMsg }"/></p>
     </c:if>
     </div>
	<form id="submissionsform" action="<c:url value="/submit"/>" method="post">
       	<!--  <legend>Feedback</legend> -->
       	<div class="oneField" style="text-align: center; border-style: none">
        	<label for="name" class="preField" style="text-align: center">E-mail:&nbsp;</label>
        	<input type="text" size="30" maxlength="90" 
               	id="email" 
               	name="email"
               	value="<c:out value="${email}"/>" 
        	/>
	
        <span class="errMsg" id="email-E"></span>
       	</div>
       	<div class="oneField" style="text-align: center; border-style: none">
       	  <br />
        	<label for="addr" class="preField" style="text-align: center">URL:&nbsp;&nbsp;&nbsp;&nbsp;</label>
        	<input type="text" size="30" maxlength="255" 
               	id="url" 
               	name="url"
               	value="<c:out value="${url}"/>"
		/>
        <br />
        <span class="errMsg" id="url-E"></span>
       </div>
      <div class="actions pushdown" style="text-align: center">
      <input class="button" type="submit" value="Submit Document Links" name="submit" title="Submit these URLs to CiteSeerX." />
	<p class="para4 parafirstletters para_book"><sup>*</sup>Publishers <a class="remove" href="http://romeo.eprints.org/publishers.html">policy</a> on self-archiving of your publications.</p>
	</div>
     <div><input type="hidden" name="rt" value="send" /></div>
     </form> 
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