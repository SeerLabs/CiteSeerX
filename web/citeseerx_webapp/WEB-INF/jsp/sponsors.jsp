<%--
  -- Page to be included in the index.jsp page. It will include links to all
  -- CiteSeerX sponsors.
  -- For Mirrors this page should be empty, or link to their sponsors
  --
  -- Author: Juan Pablo Fernandez Ramirez
  --%>
      <div class="infobox">
        <a class="remove" href="http://www.nsf.gov/" title="Sponsored by National Science Foundation"><img id="nsf" src="<c:url value="/images/sponsors/nsf_logosm.jpg"/>" alt="National Science Foundation logo" /></a> 
        <!-- <a class="remove" href="http://research.microsoft.com/" title="Sponsored by Microsoft Research"><img id="msr" src="<c:url value="/images/sponsors/MSR_logo.gif"/>" alt="Microsoft Research logo" /></a> -->
      </div> <!-- End infobox -->
      <div class="infobox">
        <a class ="remove" href="<c:url value="/about/previous"/>" title="Previous Sponsors"><img id="previous_sponsor" src="<c:url value="/images/sponsors/previous_sponsors_small.png"/>" alt="Previous Sponsors" /></a>
      </div><!-- End infobox -->