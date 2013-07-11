<%@ include file="../shared/IncludeHeader.jsp" %>
 <div id="primary_tabs-n-content" class="clearfix"> <!-- Contains header div -->
  <div id="primary_tabs_container">
   <div id="primary_tabs">
    <ul class="clearfix"><li><a class="page_tabs remove" href="<c:url value="/about/site"/>"><span>About <fmt:message key="app.name"/></span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/myciteseer"/>"><span>About <fmt:message key="app.portal"/></span></a></li><li id="current"><a class="page_tabs remove" href="<c:url value="/about/team"/>"><span>The Team</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/metadata"/>"><span><fmt:message key="app.name"/> Metadata</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/previous"/>"><span><fmt:message key="app.name"/> Previous Sponsors</span></a></li><li><a class="page_tabs remove" href="<c:url value="/about/bot"/>"><span>About <fmt:message key="app.name"/> Crawler</span></a></li></ul>   <!-- remove extra whitespace by coding in-line lists on one line -->           
   </div> <!-- End primary_tabs -->
  </div> <!-- End primary_tabs_container -->
  
  <div id="primary_content" class="nopad clearfix">

<div class="colmask blogstyle">
    <div class="colmid">
		<div class="colleft">
			<div class="col1"> <!-- Column 1 start -->
				
<h2 class="smallcapped">The Core Team</h2>
  <div class="portrait_container clearfix">
    <img class="portrait" src="<c:url value="/images/team/clg.jpg"/>" alt="C. Lee Giles" /> <h3 class="topic_heading"><a href="http://clgiles.ist.psu.edu">Prof. C. Lee Giles</a></h3><p class="char_bold">PI and Project Director</p>
    <p class="portrait_text para_book">Prof. Giles created CiteSeer in 1997 with <a href="http://labs.google.com/people/lawrence/">Steve Lawrence</a> and <a href="http://en.wikipedia.org/wiki/Kurt_Bollacker">Kurt Bollacker</a> while they were at the NEC Research Institute (now NEC Labs) in Princeton, New Jersey, USA.  Now at Penn State, Dr. Giles continues to lead CiteSeer into the Next Generation.</p>
  </div>
  <div class="portrait_container clearfix">
    <img class="portrait" src="<c:url value="/images/team/igc.jpg"/>" alt="Isaac G. Councill" /> <h3 class="topic_heading"><a href="http://www.personal.psu.edu/~igc2/">Isaac G. Councill</a></h3><p class="char_bold">Consultant, Former Technical Director, Software/Deployment Architect, Principle Engineer</p>
    <p class="portrait_text para_book">Isaac led all technical aspects of the CiteSeer<sup>x</sup> development process, designed the system framework, and wrote most of the code.  Isaac maintained the legacy CiteSeer system since it moved to Penn State in 2003 and
      incorporated many hard-won lessons from that endeavor into the new codebase.</p>
  </div>
  <div class="portrait_container clearfix">
    <img class="portrait" src="<c:url value="/images/team/pbt.jpg"/>" alt="Pradeep B. Teregowda" /> <h3 class="topic_heading">Pradeep B. Teregowda</h3><p class="char_bold">Technical Director</p><p class="portrait_text para_book">Pradeep currently leads the technical aspects of the CiteSeer<sup>x</sup> development process.</p>
  </div>
  <div class="portrait_container clearfix">
<img class="portrait" src="<c:url value="/images/team/jpfr.jpg"/>" alt="Juan Pablo Fernandez Ramirez" />
<h3 class="topic_heading">Juan Pablo Fern&aacute;ndez Ram&iacute;rez</h3><p class="char_bold">Software Engineer</p>
<p class="portrait_text para_book">Juan is responsible for the collections feature of MyCiteSeer and general CiteSeer<sup>x</sup> bug squashing.  He has participated in developing document conversion routines and contributed in part to the navigation menu design and page layout for CiteSeer<sup>x</sup> and MyCiteSeer<sup>x</sup>. He has been resposible for OAI metadata interface, external links and legacy corrections.</p>
 </div>                                
 <div class="portrait_container clearfix">
<img class="portrait" src="<c:url value="/images/team/sz.jpg"/>" alt="Shuyi Zheng" /> <h3 class="topic_heading"><a href="http://www.cse.psu.edu/~shzheng/">Shuyi Zheng</a></h3><p class="char_bold">The Crawler</p>
<p class="portrait_text para_book">Shuyi scoured the web for millions of new document files and is largely responsible for bringing the CiteSeer<sup>x</sup> collection up to date with the latest literature on the net. He has also lead efforts, for integrating table search with CiteSeer<sup>x</sup>.</p>
  </div>
 <div class="portrait_container clearfix">
<img class="portrait" src="<c:url value="/images/team/bjb.jpg"/>" alt="Bonnie J. Batres" /> <h3 class="topic_heading"><a href="http://clubgray.com/bonnie/">Bonnie J. Batres</a></h3><p class="char_bold">Web Designer</p>
<p class="portrait_text para_book">Mrs. Batres created new logos, graphics, navigation menus and page layouts for CiteSeer<sup>x</sup> and MyCiteSeer<sup>x</sup>.</p>
  </div>
 <br />

 <h2 class="smallcapped">With noted contributions from:</h2>
 <div class="portrait_container clearfix">
<img class="portrait" src="<c:url value="/images/team/pum.jpg"/>" alt="Prasenjit Mitra" /> <h3 class="topic_heading"><a href="http://www.personal.psu.edu/users/p/u/pum10/">Prasenjit Mitra</a></h3>
<p class="portrait_text para_book">Prof. Mitra lead the team which developed table search features for CiteSeer<sup>x</sup>. He has also been, a collaborator in the CiteSeer<sup>x</sup> project.</p>
  </div>
<div class="portrait_container clearfix">
<img class="portrait" src="<c:url value="/images/team/yzl.jpg"/>" alt="Ying Liu" /> <h3 class="topic_heading"><a href="http://ist.psu.edu/ist/directory/staff/?EmployeeID=461">Ying Liu</a></h3>
<p class="portrait_text para_book">Liu developed the table search system that underlies the table search features of CiteSeer<sup>x</sup>.</p>
  </div>

 <div class="portrait_container clearfix">
  <img class="portrait" src="<c:url value="/images/team/myk.jpg"/>" alt="Min-Yen Kan"/> <h3 class="topic_heading"><a href="http://www.comp.nus.edu.sg/~kanmy/">Min-Yen Kan</a></h3>
  <p class="portrait_text para_book">Min-Yen Kan contributed the excellent conditional random field model that underlies the CiteSeer<sup>x</sup> citation parser, ParsCit, and collaborated with Isaac Councill to build the ParsCit application.</p>
 </div>
 <ul class="formating para_book">
  <li>The initial header parsing algorithm used by CiteSeer<sup>x</sup> was developed by Hui Han, C. Lee Giles, Eren Manavoglu, Hongyuan Zha, Zhenyue Zhang, and Edward A. Fox.  The algorithm was further refined by Levent Bolelli and Isaac Councill.</li>
  <li><a href="http://www.cse.psu.edu/~yasong/">Yang Song</a> developed an initial MyCiteSeer prototype that guided later efforts.</li>
  <li><a href="http://www.personal.psu.edu/yus115/">Yang Sun</a> contributed the venue analysis code for calculating impact factor statistics.</li>
 </ul>
 
 <h2 class="smallcapped">Open Source Acknowledgements</h2>
 <p class="para_book">CiteSeer<sup>x</sup> is supported by numerous excellent open source applications and libraries.
 Specifically, we would like to thank all who participated in the development of the following projects:</p>
 <ul class="formating para_book">
 <li><a href="http://www.mysql.com/">The MySQL Database</a> and <a href="http://www.innodb.com/">InnoDB Storage Engine</a></li>
 <li><a href="http://lucene.apache.org/">Lucene</a> and <a href="http://lucene.apache.org/solr/">Solr</a></li>
 <li><a href="http://xapian.org/">Xapian</a></li>
 <li><a href="http://tomcat.apache.org/">Apache Tomcat</a></li>
 <li><a href="http://www.springframework.org/">The Spring Framework</a></li>
 <li><a href="http://www.acegisecurity.org/">Acegi Security</a></li>
 <li><a href="http://activemq.apache.org/">ActiveMQ</a></li>
 <li><a href="http://www.active-endpoints.com/active-bpel-engine-overview.htm">ActiveBPEL Open Source Engine</a></li>
 <li><a href="http://commons.apache.org/">The Apache Commons Libraries</a></li>
 <li><a href="http://svmlight.joachims.org/">SVM<sup><i>light</i></sup> support vector machine package</a></li>
 <li><a href="http://crfpp.sourceforge.net/">CRF++ conditional random field package</a></li>
 </ul>
 
 <h2 class="smallcapped">We Also Recognize</h2>
 <ul class="formating para_book">

 <li>Andrew Ng was the first to extract title and author information from the header of PostScript files.</li>
 <li><a href="http://nzdl.sadl.uleth.ca/cgi-bin/library">The New Zealand Digital Library</a> was the first to index the full text of PostScript research articles.</li> 
 <li>Dr. Eugene Garfield created the idea of citation indexing of the scientific literature.</li>

 </ul>
				
</div> <!-- Column 1 end -->
<div class="col2"> <!-- Column 2 start -->
				
<h2 class="smallcapped">Special Thanks</h2>
<p class="para_book">Many have contributed to CiteSeer and 
its continuing development. In a list in which some are surely missing, we would like to
thank</p>
<ul class="formating">
<li>Joshua Alspector</li>
<li>Esam Alwagait</li>

<li>Jose Nelson Amaral</li>
<li>Anders Ardo</li>
<li>Bill Arms</li>
<li>Shumeet Baluja</li>
<li>Arunava Banerjee</li>
<li>Eric Baum</li>
<li>Donna Bergmark</li>
<li>Levent Bolelli</li>
<li>Kurt Bollacker</li>
<li>Shannon Bradshaw</li>

<li>Vivek Bhatnagar</li>
<li>Jay Budzik</li>
<li>Robert Cameron</li>
<li>Jack Carroll</li>
<li>Rich Caruana</li>
<li>Ingemar Cox</li>
<li>Sandip Debnath</li>
<li>Seyda Ertekin</li>
<li>Scott Fahlman</li>
<li>Umer Farooq</li>

<li>Gary Flake</li>
<li>Ed Fox</li>
<li>Eugene Garfield</li>
<li>Susan Gauch</li>
<li>Bill Gear</li>
<li>Paul Ginsparg</li>
<li>Eric Glover</li>
<li>Abby Goodrum</li>
<li>Marco Gori</li>
<li>Allan Gottlieb</li>

<li>Jim Gray</li>
<li>Hui Han</li>
<li>Mike Halm</li>
<li>Steve Hanson</li>
<li>Stevan Harnad</li>
<li>Eric Hellman</li>
<li>Hui Han</li>
<li>Geoff Hinton</li>
<li>Haym Hirsh</li>
<li>Steve Hitchcock</li>
<li>Jian Huang</li>
<li>Kirby Huntsinger</li>

<li>Gerd Hoff</li>
<li>Ernesto Di Iorio</li>
<li>Jim Jansen</li>
<li>Shannon Johnson</li>
<li>Paul Kantor</li>
<li>Jon Kleinberg</li>
<li>Thomas Krichel</li>
<li>Bob Krovetz</li>
<li>Carl Lagoze</li>
<li>Andrea LaPaugh</li>
<li>Steve Lawrence</li>

<li>Wang-Chien Lee</li>
<li>Jay Lepreau</li>
<li>Michael Lesk</li>
<li>Huajing Li</li>
<li>Marco Maggini</li>
<li>Eren Manavoglu</li>
<li>Andrew McCallum</li>
<li>Chris Milito</li>
<li>Steve Minton</li>
<li>Tom Mitchell</li>

<li>Finn Nielsen</li>
<li>Michael Nelson</li>
<li>Craig Nevill-Manning</li>
<li>Andrew Ng</li>
<li>Andrew Odlyzko</li>
<li>David Pennock</li>
<li>Yves Petinot</li>
<li>Brian Pinkerton</li>

<li>Alexandrin Popescul</li>
<li>Augusto Pucci</li>
<li>Betsy Richmond</li>
<li>Ben Schafer</li>
<li>Bruce Schatz</li>
<li>Terrence Sejnowski</li>
<li>Anand Sivasubramaniam</li>
<li>Warren Smith</li>

<li>Yang Song</li>
<li>Amanda Spink</li>
<li>Yang Sun</li>
<li>Harold Stone</li>
<li>Pucktada Treeratpituk</li>
<li>Kostas Tsioutsiouliklis</li>
<li>Valerie Tucci</li>
<li>Lyle Ungar</li>
<li>Frits Vaandrager</li>
<li>Moshe Vardi</li>
<li>David Waltz</li>

<li>James Ze Wang</li>
<li>Ian Witten</li>
<li>Hongyuag Zha</li>
<li>Ding Zhou</li>
<li>Ziming Zhuang</li>
</ul>
				
</div> <!-- Column 2 end -->
<div class="col3"> <!-- Column 3 start -->
				
<div id="team" class="content_box"> <!-- Sponsors content_box -->
<h2>Sponsors</h2>
<div class="imageinbox">
 <a class="remove" href="http://www.nsf.gov/" title="Sponsored by National Science Foundation">
 <img id="nsf" src="<c:url value="/images/sponsors/nsf_gif.gif"/>" alt="National Science Foundation logo" /></a>
</div> <!-- End imageinbox -->

<p class="char1 para2">Interested in</p><p class="char1 para2">sponsoring CiteSeer<sup>x</sup>?</p>
<p class="char1 para2"><a href="http://clgiles.ist.psu.edu/">Contact the Director</a></p> <!-- End of char1 para2 -->
</div> <!-- End sponsors content_box -->

</div> <!-- Column 3 end -->

		</div> <!-- End colleft -->
    </div> <!-- End colmid -->
</div> <!-- End colmask -->               

   

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
     
