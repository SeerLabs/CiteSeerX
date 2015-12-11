<div id="search">
  <h1><img src="<c:url value='/images/csx_logo_front.png'/>" alt="<fmt:message key="app.name"/>" height="150" width="142"/></h1>
  <div id="search_box">
    <div id="search_docs">
      <form method="get" action="<c:url value='/search'/>" enctype="application/x-www-form-urlencoded">
        <label>Documents:</label>
        <div class="searchFields">
          <input class="s_field" type="text" name="q" value="<c:out value='${ query }'/>" />
          <input class="s_button" type="image" name="submit" value="Search" alt="Search" src="<c:url value='/images/search_icon.png' />" />
          <input class="s_button" type="image" name="s2" value="Semantic Scholar" alt="Semantic Scholar" src="<c:url value='/images/s2_icon.png' />" />
        </div>
        <div class="opts">
          <a href="<c:url value='/advanced_search'/>" title="Search full text, title, abstract, date, author name, author affiliation, etc.">Advanced Search</a>
          <input class="c_box" type="checkbox" name="ic" value="1" /> Include Citations
        </div>
        <input type="hidden" name="sort" value="<c:if test='${ empty param.sort }'>rlv</c:if><c:if test='${ ! empty param.sort }'><c:out value='${ param.sort }'/></c:if>" />
        <input type="hidden" name="t" value="doc" />
      </form>
    </div>
		  
    <div id="search_auth">
      <form method="get" action="<c:url value='/search'/>" enctype="application/x-www-form-urlencoded">
	    <label>Authors:</label>
        <div class="searchFields">
          <input class="s_field" type="text" name="q" value="<c:out value='${ query }'/>" />
          <input class="s_button" type="submit" name="submit" value="Search" alt="Search" />
        </div>
        <div class="opts">
          <a href="<c:url value='/advanced_search'/>" title="Search full text, title, abstract, date, author name, author affiliation, etc.">Advanced Search</a>
          <input class="c_box" type="checkbox" name="ic" value="1" /> Include Citations |
          <input class="c_box" type="checkbox" name="uauth" value="1" checked="checked" /> Disambiguate
        </div>
        <input type="hidden" name="sort" value="<c:if test='${ empty param.sort }'>ndocs</c:if><c:if test='${ ! empty param.sort }'><c:out value='${ param.sort }'/></c:if>" />
        <input type="hidden" name="t" value="auth" />
      </form>
    </div>
		  
    <div id="search_tables">
      <form method="get" action="<c:url value='/search'/>" enctype="application/x-www-form-urlencoded">
        <label>Tables:</label>
        <input class="s_field" type="text" name="q" value="<c:out value='${ query }'/>" />
        <input class="s_button" type="submit" name="submit" value="Search" alt="Search" />
        <input type="hidden" name="sort" value="<c:if test='${ empty param.sort }'>rlv</c:if><c:if test='${ ! empty param.sort }'><c:out value='${ param.sort }'/></c:if>" />
        <input type="hidden" name="t" value="table" />
      </form>
    </div>
    <%-->
    <div id="search_algorithms">
      <form method="get" action="<c:url value='/search'/>" enctype="application/x-www-form-urlencoded">
        <label>Tables:</label>
        <input class="s_field" type="text" name="q" value="<c:out value='${ query }'/>" />
        <input class="s_button" type="submit" name="submit" value="Search" alt="Search" />
        <input type="hidden" name="sort" value="<c:if test='${ empty param.sort }'>rlv</c:if><c:if test='${ ! empty param.sort }'><c:out value='${ param.sort }'/></c:if>" />
        <input type="hidden" name="t" value="algorithm" />
      </form>
    </div>
    --%>
  </div>
</div>
