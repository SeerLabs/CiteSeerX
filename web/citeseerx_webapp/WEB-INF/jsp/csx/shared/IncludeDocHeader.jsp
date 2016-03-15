<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="../../shared/IncludeHeader.jsp" %>
<div id="main">
<%@ include file="../../shared/IncludeSearchBox.jsp" %>
  <div id="viewHeader" class="viewDoc">
    <h2><c:out value="${ title }"/> <c:if test="${ ! empty year }"> (<c:out value="${ year }"/>) </c:if></h2>
    <div id="downloads">
      <c:choose>
        <c:when test="${ empty pdfRedirectUrl }">
          <h3>Cached</h3>
          <ul id="clinks">
          <c:forEach var="type" items="${ fileTypes }" varStatus="status">
            <c:if test="${ status.count > 1 }">&nbsp;</c:if>
            <c:url value="/viewdoc/download" var="downloadUrl">
            <c:param name="doi" value="${ doi }"/>
            <c:param name="rep" value="${ rep }"/>
            <c:param name="type" value="${ type }"/></c:url>
            <c:if test="${type == 'pdf'}">
              <li><a href="<c:out value="${downloadUrl}" escapeXml="true"/>" title="View or Download this document as PDF">
              <img src="<c:url value="/images/pdf_icon.png"/>" alt="Download as a PDF"/>
              </a></li>
            </c:if>
            <c:if test="${type == 'ps'}">
              <li><a href="<c:out value="${downloadUrl}" escapeXml="true"/>" title="View or Download this document as PS">
              <img src="<c:url value="/images/ps_icon.png"/>" alt="Download as a PS"/>
              </a></li>
            </c:if>
          </c:forEach>
          </ul>
        </c:when>
        <c:otherwise>
          <h3>Download From</h3>
          <a href="<c:url value="${ pdfRedirectUrl }"/>" title="Download from <c:out value="${ pdfRedirectLabel }" escapeXml="true"/>">
          <c:out value="${ pdfRedirectLabel }"/>
          <c:if test="${ pdfRedirectLabel == 'IEEE' }">
            <br/><img src="<c:url value="/images/ieee_download.gif"/>" alt="Download from IEEE" height="20px" width="60px"/>
          </c:if>
          </a>
        </c:otherwise>
      </c:choose>
      <c:if test="${ !empty urls}">
        <h3>Download Links</h3>
        <ul id="dlinks">
        <c:forEach var="docURL" items="${ urls }">
          <li>
            <a href="<c:url value="${ docURL }"/>" title="<c:out value="${ docURL }"/>" escapeXml="true">
              [<c:out value="${fn:substringBefore(fn:substringAfter(docURL,'//'),'/') }"/>]
            </a>
          </li>
        </c:forEach>
      </c:if>
      <br/>
      <c:if test="${ !empty elinks }">
        <h3>Other Repositories/Bibliography</h3>
        <c:forEach var="link" items="${elinks}" varStatus="status">
          <li><a href="<c:url value="/viewdoc/redirect?doi=${doi}&label=${link.label}"/>" title="<c:out value="${ link.label }"/>">
          <c:out value="${ link.label }"/></a></li>
        </c:forEach>
      </c:if>
      </ul>
    </div><%--downloads close div --%>
    <div id="docTools">
      <ul class="button_nav">
        <li><a style="cursor: pointer;" onclick="addToCartProxy(<c:out value="${ clusterid }"/>)">Save to List</a>
        <span id="cmsg_<c:out value="${ clusterid }"/>" class="cartmsg"></span></li>
        <li><a class="remove"  href="<c:url value="/myciteseer/action/addPaperCollection?doi=${doi}"/>" title="Add this document to your collection (Account required)">Add to Collection</a></li>
        <li><a class="remove" href="<c:url value="/correct?doi=${ doi }"/>" title="Submit corrections for this document (Account required)">Correct Errors</a></li>
        <li><a class="remove" href="<c:url value="/myciteseer/action/editMonitors?doi=${ doi }"/>" title="Monitor changes to this document (Account required)">Monitor Changes</a></li>
      </ul>
    </div>
    <div id="docInfo">
      <div id="docAuthors">
        by <c:if test="${empty uauthors }"><c:out value="${ authors }" /></c:if>
        <c:if test="${! empty uauthors }">
          <c:forEach var="uauthor" items="${ uauthors }" varStatus="status">
            <c:if test="${ !empty uauthor.aid }">
              <a href="<c:url value="/viewauth/summary?aid=${ uauthor.aid }"/>"><c:out value="${ uauthor.canname }" /></a>
            </c:if>
            <c:if test="${ empty uauthor.aid }">
              <c:out value="${ uauthor.canname }" />
            </c:if>
            <c:if test="${ !status.last }">,</c:if>
          </c:forEach>
        </c:if>
      </div>
      <div id="docOther">
        <table>
          <c:if test="${ ! empty venue }">
            <tr id="docVenue"><td class="title">Venue:</td><td><c:out value="${ venue }"/></td>
          </c:if>
          <c:if test="${ ncites > 0 }">
            <tr id="docCites"><td class="title">Citations:</td><td><a href="<c:url value="/showciting?doi=${ doi }"/>" title="number of citations"><c:out value="${ ncites }"/> - <c:out value="${ selfCites }"/> self</a>
            </td></tr>
          </c:if>
        </table>
      </div>
    </div><%--docInfo close div --%>
    <div class="clear"></div>
  </div><%--viewHeader close div --%>

  <div id="docMenu" class="submenu">
    <ul>
      <li <c:if test='${ pagetype == "summary" }'> class="active"</c:if> ><a href="<c:url value="/viewdoc/summary?doi=${ doi }"/>">Summary</a></li>
      <li <c:if test='${ pagetype == "citations" }'> class="active"</c:if> ><a href="<c:url value="/viewdoc/citations?doi=${ doi }"/>">Citations</a></li>
      <li <c:if test='${ pagetype == "similar" && param.type == "ab" }'> class="active"</c:if> ><a href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=ab"/>" title="Documents that cite the same works">Active Bibliography</a></li>
      <li <c:if test='${ pagetype == "similar" && param.type == "cc"}'> class="active"</c:if> ><a href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=cc"/>" title="Documents cited by the same works">Co-citation</a></li>
      <li <c:if test='${ pagetype == "similar" && param.type == "sc"}'> class="active"</c:if>><a href="<c:url value="/viewdoc/similar?doi=${ doi }&amp;type=sc"/>" title="Documents in the Same Cluster">Clustered Documents</a></li>
      <li <c:if test='${ pagetype == "versions" }'> class="active"</c:if>><a href="<c:url value="/viewdoc/versions?doi=${ doi }"/>">Version History</a></li>
      <!--<li><a <c:if test='${ pagetype == "cluster" }'> id="active"</c:if> href="<c:url value="/viewdoc/clustered?doi=${ doi }"/>">Clustered Documents</a></li>-->
    </ul>
  </div>
