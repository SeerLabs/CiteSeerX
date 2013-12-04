<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<script type="text/javascript">
  $("#home").addClass('active');
</script>
<div id="content">
  <h2>Welcome back <%= account.getFirstName() %></h2>
  <h3>Latest News (<a href="<fmt:message key="app.bulletin"/>">See All</a>)</h3>

  <div id="newsFeed">
    <c:forEach var="item" items="${ newsItems }">
      <div class="newsItem">
       <h4><a href="<c:url value="${ item.link }"/>"><em class="title"><c:out value="${ item.title }" escapeXml="false"/></em></a> [<c:out value="${ item.date }"/>]</h4>
       <c:if test="${ !empty item.description }"><p><c:out value="${ item.description }" escapeXml="false"/></p></c:if>
      </div>
    </c:forEach>
  </div>

  <!-- <c:if test="${ peoplesearchenabled }">
    <div class="content_box myhome_content_box clearfix">
      <h2>Searches</h2>
      <%@include file="search/MCSSearchBox.jsp"%>
    </div>
  </c:if> -->

</div>

<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>
