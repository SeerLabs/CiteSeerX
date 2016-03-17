<%--
  -- This page includes Header specific information view document pages.
  --
  -- Author: Isaac Councill
  --%>
<%@ include file="shared/IncludeTop.jsp" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div id="center_content" class="clearfix"> <!-- Contains header div -->
  <div id="secondary_tabs_container">
    <div id="secondary_tabs">
    <!-- to make a tab highlighted, place id="currentpage" within li tag -->
      <ul><li<c:if test='${ pagetype == "summary" }'> id="currentpage"</c:if>><a class="page_tabs remove" href="<c:url value="/viewdoc/summary?doi=${ doi }"/>"><span>Summary</span></a></li><li<c:if test='${ pagetype == "similar" }'> id="currentpage"</c:if> class="tab4sublist"><a class="page_tabs remove" href="<c:url value="/viewdoc/similar?doi=${ doi }"/>" title="Find documents related to this document"><span class="clearfix">Related Documents</span></a><ul class="tab_sublist clearfix"><li><a class="sublist_links remove" href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=ab"/>" title="Documents that cite the same works"><span>Active Bibliography</span></a></li><li><a class="sublist_links remove" href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=cc"/>" title="Documents cited by the same works"><span>Co-citation</span></a></li><li><a class="sublist_links remove" href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=sc"/>" title="Documents in the Same Cluster"><span>Clustered Documents</span></a></li></ul></li><li<c:if test='${ pagetype == "versions" }'> id="currentpage"</c:if>><a class="page_tabs remove" href="<c:url value="/viewdoc/versions?doi=${ doi }"/>"><span>Version History</span></a></li></ul>
    </div> <!-- End secondary_tabs -->
  </div> <!-- End secondary_tabs_container -->
  <div id="primary_content">
    <h1 class="primaryheader">
     <c:out value="${ title }"/>
     <c:if test="${ ! empty year }"> (<c:out value="${ year }"/>) </c:if>
     <c:if test="${ ncites > 0 }">&#91;<a class="citation remove" href="<c:url value="/showciting?doi=${ doi }"/>" title="number of citations"><c:out value="${ ncites }"/> citations &mdash; <c:out value="${ selfCites }"/> self</a>&#93;</c:if>
    </h1>
    <div id="introduction" class="clearfix">
      <div id="downloads">
        <span class="dlspan">Download:</span><br/>
        <c:forEach var="docURL" items="${ urls }" varStatus="status">
          <c:if test="${ status.count > 1 }"> <br/> </c:if>
          <a href="<c:url value="/viewdoc/download?doi=${doi}&rep=${rep}&type=url&i=${status.index}"/>" title="<c:out value="${ docURL }"/>">
            <c:out value="${fn:substring(docURL,0,50) }"/></a>
        </c:forEach>
        <c:if test="${ !empty elinks }">
          <br/> 
          <c:forEach var="link" items="${elinks}" varStatus="status">
            <c:if test="${ status.count > 1 }"> | </c:if>
            <a href="<c:url value="/viewdoc/redirect?doi=${doi}&label=${link.label}"/>" title="<c:out value="${ link.label }"/>"><c:out value="${ link.label }"/></a>
          </c:forEach>
        </c:if>
        <br/>
        <span class="dlspan">CACHED:</span><br/>
        <c:forEach var="type" items="${ fileTypes }" varStatus="status"><c:if test="${ status.count > 1 }"> | </c:if>
          <c:url value="/viewdoc/download" var="downloadUrl"><c:param name="doi" value="${ doi }"/><c:param name="rep" value="${ rep }"/><c:param name="type" value="${ type }"/></c:url>
          <c:if test="${type == 'pdf'}">
            <a href="<c:out value="${downloadUrl}" escapeXml="true"/>" title="View or Download this document as PDF"><img src="<c:url value="/icons/pdf.gif"/>" alt="Download as a PDF"/> </a>
          </c:if>
          <c:if test="${type == 'ps'}">
            <a href="<c:out value="${downloadUrl}" escapeXml="true"/>" title="View or Download this document as PS"><img src="<c:url value="/icons/ps.gif"/>" alt="Download as a PS"/> </a>
          </c:if>
        </c:forEach>
<%--         <c:if test="${ !empty hubUrls }">
        <br/>
        <span class="dlspan" title="Other Documents From">From:</span><br/>
        <c:forEach var="hub" items="${ hubUrls }" varStatus="status">
          <c:if test="${ status.count > 1 }"> <br/> </c:if>
          <c:url value="/viewdoc/slocation" var="fromHub"><c:param name="hurl" value="${ hub.url }"/><c:param name="doi" value="${ doi }"/></c:url>
          <a href="<c:out value="${ fromHub }" escapeXml="true"/>" title="More Documents from: <c:out value="${ hub.url }"/>">
            <c:out value="${ fn:substring(hub.url,0,50) }"/></a>
        </c:forEach>
        </c:if> --%>
      </div> <!-- End downloads -->
      <div class="char_increased char_indented char_mediumvalue padded">
        by
        <c:if test="${empty uauthors }">
          <c:out value="${ authors }" />
        </c:if>
        <c:if test="${! empty uauthors }">
          <c:forEach var="uauthor" items="${ uauthors }" varStatus="status">
            <c:if test="${ !empty uauthor.aid }">      
              <a href="<c:url value="/viewauth/summary?aid=${ uauthor.aid }"/>"><c:out value="${ uauthor.canname }" /></a> 
            </c:if>
            <c:if test="${ empty uauthor.aid }">      
              <c:out value="${ uauthor.canname }" />       
            </c:if>
            <c:if test="${ !status.last }">
              ,&nbsp;            
            </c:if>
          </c:forEach>
        </c:if>
      </div>
      <c:if test="${ ! empty venue }">
        <div class="char_increased  char_indented char6 padded">
          <c:out value="${ venue }"/>
        </div>
      </c:if>
      <div class="char_increased char_indented"><span class="actionspan" onclick="addToCartProxy(<c:out value="${ clusterid }"/>)">Add To MetaCart</span> <span id="cmsg_<c:out value="${ clusterid }"/>" class="cartmsg"></span></div>
    </div> <!-- End introduction -->
    <div class="information_bar clearfix"><ul class="button_nav"><li><a class="remove"  href="<c:url value="/myciteseer/action/addPaperCollection?doi=${doi}"/>" title="Add this document to your collection (Account required)"><span>Add to Collection</span></a></li><li><a class="remove" href="<c:url value="/correct?doi=${ doi }"/>" title="Submit corrections for this document (Account required)"><span>Correct Errors</span></a></li><li><a class="remove" href="<c:url value="/myciteseer/action/editMonitors?doi=${ doi }"/>" title="Monitor changes to this document (Account required)"><span>Monitor Changes</span></a></li></ul></div>
