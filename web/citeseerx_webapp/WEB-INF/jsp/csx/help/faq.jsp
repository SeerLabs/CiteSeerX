<%@ include file="../shared/IncludeHeader.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/help/search"/>"><span>Search Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/site"/>"><span><fmt:message key="app.name"/> Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/myciteseer"/>"><span><fmt:message key="app.portal"/> Help</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/submit"/>"><span>Submissions</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/glossary"/>"><span>Glossary</span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/help/faq"/>"><span>FAQ</span></a></li><li><a class="page_tabs remove" href="<c:url value="/help/api"/>"><span>API</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->

  <div id="primary_content">
   <div id="main_content" class="clearfix">
    <p class="para4 parafirstletters para_book"><span class="firstletters"><em class="firstletter">Frequently Asked Questions (FAQ)</em></span></p>
    <br/>
    <p class="pushdown">
      <ol class="numbered_list pushdown">
        <li>
          <a href="#" class="toggle_faq" id="tfaq_1">Where is the year of publication within the document summary page?</a>
          <div id="faq_1" class="para4 para_book">
            <p>The year of publication shows up beside the paper title if we have it.
               The paper metadata in our database is automatically extracted and/or inferred
               and some times our extractors are not able to obtain all the metadata. However,
               if you know the year of publication or another missing or wrong metadata
               you can help us by correcting it using the Correct Errors button.
            </p>
          </div>
        </li>
        <li>
          <a href="#" class="toggle_faq" id="tfaq_2">How can I see from the summary page what papers cite the current paper?</a>
          <div id="faq_2" class="para4 para_book">
            <p>If the paper is cited by others, you will see a link to the right 
               the paper title ([# citations - # self]). By clicking on it you will get
               more information.
            </p>
            <p>You should also be aware that our system extracts citations automatically
               from Portable document Format (.pdf) and/or Postscript (.ps) files, and
               sometimes we are not able to extract all citations. However, we keep working
               on our extractors to improve them.
            </p>  
          </div>
        </li>
        <li>
          <a href="#" class="toggle_faq" id="tfaq_3">Why this paper of mine does not appear in CiteSeer<sup>X</sup>?</a>
          <div id="faq_3" class="para4 para_book">
            <p>Papers does not appear within CiteSeer<sup>X</sup> corpus if 
               they are not accessible from the web or our crawler has not crawl them yet.
               More information about how CiteSeer<sup>X</sup> obtain the documents can be
               found in the CiteSeer<sup>X</sup> 
               <a href="<c:url value="/about/bot"/>">Crawler</a> and 
               <a href="<c:url value="/help/submit"/>">submission</a> help pages.  
            </p>
            <p>You can submit your documents using CiteSeer<sup>X</sup> 
               <a href="<c:url value="/myciteseer/actions/submitUrl"/>">Submission page</a>
            </p>
          </div>
        </li>
        <li>
          <a href="#" class="toggle_faq" id="tfaq_4">Why my name is not the author list of a paper of mine?</a>
          <div id="faq_4" class="para4 para_book">
            <p>Author information is automatically extracted from Portable 
               Document Format (.pdf) and/or Postscript (.ps) files after they are converted to
               text. Some times the output produced from the extractor makes it difficult to ours
               parsers to extract authors information. We keep working on our parsers to improve
               them as well as testing new text extractors to use the one which gives us the best
               output. 
            </p>
            <p>Moreover, all CiteSeer<sup>X</sup> metadata can be corrected by our users. If you find something that is
               incorrect, help CiteSeer<sup>X</sup> by correcting it using the <strong>Correct Errors</strong> button located
               in the summary page.
            </p> 
          </div>
        </li>
        <li>
          <a href="#" class="toggle_faq" id="tfaq_5">Why CiteSeer<sup>X</sup> misspell names or part of article abstracts?</a>
          <div id="faq_5" class="para4 para_book">
            <p>All the metadata within the CiteSeer<sup>X</sup> corpus is 
               automatically obtained from Portable 
               Document Format (.pdf) and/or Postscript (.ps) files after they are converted to
               text.
             </p>
             <p>Files that were not created with UNICODE will have wrong characters when 
                they are converted to text. 
             </p>
          </div>
        </li> 
        <li>
          <a href="#" class="toggle_faq" id="tfaq_6">Why CiteSeer<sup>X</sup> statistics are different from other sources (eg. Google Scholar)?</a>
          <div id="faq_6" class="para4 para_book">
            <p>All the metadata within the CiteSeer<sup>X</sup> corpus is 
               automatically obtained from Portable 
               Document Format (.pdf) and/or Postscript (.ps) files after they are converted to
               text. Sometimes, our parsers are not able to obtain all the information
               from the document (authors, citations, etc). Statistics are base on the extracted
               metadata so they might differ from other sources where metadata is manually generated.
            </p>
            <p> CiteSeer<sup>X</sup> does provide a way to correct/add some metadata. 
                For example, authors names and affiliations. We also continue working
                in our parser to improve their accuracy.
            </p>
          </div>
        </li>
        <li>
          <a href="#" class="toggle_faq" id="tfaq_7">How can I removed a paper from CiteSeer<sup>X</sup> database?</a>
          <div id="faq_7" class="para4 para_book">
            <p>Papers within CiteSeer<sup>X</sup> corpus are crawled from the web. 
               The only reason a papers of yours is in the CiteSeer<sup>X</sup> 
               database is because it was/is available from the web.
            </p>
            <p>If you do not want your papers being crawled by CiteSeer<sup>X</sup> you should:</p>
            <ul>
              <li>Put a <a href="http://www.robotstxt.org/">robots.txt</a> file disallowing 
              citeseerxbot to crawl your site.
              </li>
              <li>Do not put the files you don't want to be crawled in the web, or</li>
              <li>Put the files within a folder that requires authentication</li>
            </ul>
          </div>
        </li>
        <li>
          <a href="#" class="toggle_faq" id="tfaq_8">Does CiteSeer<sup>X</sup> support the Open Archives Initiative?</a>
          <div id="faq_8" class="para4 para_book">
            <p>CiteSeer<sup>X</sup> supports and participates in the <a href="http://www.openarchives.org/" title="OAI">Open Archives Initiative</a> (OAI)
               For more information visit our <a href="<c:url value="/about/metadata"/>">metadata help</a> page or go to our 
               <a href="<c:url value="/oai2"/>">OAI harvest</a> URL.
            </p>
          </div>
        </li>
      </ol>
    </p>
   </div> <!-- End main content -->            
  </div> <!-- End primary_content -->
 </div> <!-- End primary_tabs-n-content -->
<script type="text/javascript">
<!--
if (window != top) 
top.location.href = location.href;
function sf(){}

 // Shows/hides the answer to a question
 $$('a.toggle_faq').each(function(elem) {
 
   // Obtain the answers div.
   var aDivId = 'faq' +
    elem.id.substring(elem.id.indexOf('_'));
   
   // Creates the slider
   var mySlider = new Fx.Slide(aDivId);
   mySlider.slideOut();
 
   elem.addEvent('click', function(event) {
    var event = new Event(event);
    event.stop();
    /*if (elem.text == 'Show Notes') {
     elem.setText('Hide Notes');
    }else{
     elem.setText('Show Notes');;
    }*/
    mySlider.toggle();
   });
 });
// -->
</script>
<%@ include file="../../shared/IncludeFooter.jsp" %>