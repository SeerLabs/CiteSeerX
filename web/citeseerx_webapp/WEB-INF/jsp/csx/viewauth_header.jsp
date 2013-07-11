<%--
  -- This page includes Header specific information view document pages.
  --
  -- Author: Isaac Councill
  --%>
<%@ include file="shared/IncludeTop.jsp" %>
<div id="center_content" class="clearfix"> <!-- Contains header div -->
  <div id="secondary_tabs_container">
    <div id="secondary_tabs">
    <!-- to make a tab highlighted, place id="currentpage" within li tag -->
      <ul><li id="currentpage"><a class="page_tabs remove" href="<c:url value="/viewauth/summary?aid=${ uauth.aid }"/>"><span>Summary</span></a></li></ul>
    </div> <!-- End secondary_tabs -->
  </div> <!-- End secondary_tabs_container -->
  <div id="primary_content">
    <h1 class="primaryheader">
	  <c:out value="${ uauth.canname }"/>
    </h1>
    <div id="introduction">
      <div class="char_increased char_indented char_mediumvalue padded">
        <c:if test="${ ! empty uauth.url }"><a href='<c:out value="${ uauth.url }"/>'>Homepage</a> </c:if>
        <c:if test="${ empty uauth.url }"><a href='<c:url value="${hpslink}"/>'>Submit Homepage</a> </c:if>
      </div>
      <!--div class="char_increased char_indented char_mediumvalue padded">
	  <c:if test="${ ! empty uauth.email }"> (<c:out value="${ uauth.email }"/>) </c:if>     
      </div-->
      <c:if test="${ ! empty uauth.affil }">
        <div class="char_increased char_indented char_mediumvalue padded"><c:out value="${ uauth.affil }"/></div>
	  </c:if>
	  <c:if test="${ ! empty uauth.address }">
        <div class="char_increased char_indented char_mediumvalue padded"><c:out value="${ uauth.address }"/></div>
      </c:if>
      <div class="char_increased char_indented char_mediumvalue padded">
	    <c:if test="${ ! empty uauth.ndocs }"> <c:out value="${ uauth.ndocs }"/> publications, </c:if>
	    <c:if test="${ ! empty uauth.hindex }"> H-index: <c:out value="${ uauth.hindex }"/> </c:if>
      </div>
    </div> <!-- End introduction -->
    <!--div class="information_bar clearfix"><ul class="button_nav"><li><a class="remove">A</a></li></ul></div-->
    <div class="information_bar clearfix"><ul class="button_nav"><li></li></ul></div>