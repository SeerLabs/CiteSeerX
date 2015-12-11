<div id="search">
  <c:if test="${ empty param.q }">
    <div id="search_docs">
      <form method="get" action="<c:url value='/search'/>" enctype="application/x-www-form-urlencoded">
        <label>Documents:</label>
        <input class="s_field" type="text" name="q" value="<c:out value='${ query }'/>" />
		<input class="s_button" type="image" name="submit" value="Search" alt="Search" src="<c:url value='/images/search_icon.png' />" />
		<input class="s_button" type="image" name="s2" value="Semantic Scholar" alt="Semantic Scholar" src="<c:url value='/images/s2_icon.png' />" />
		<div class="opts">
          <a href="<c:url value='/advanced_search'/>" title="Search full text, title, abstract, date, author name, author affiliation, etc.">Advanced Search</a>
          <input class="c_box" type="checkbox" name="ic" value="1" <c:if test='${ ! empty param.ic }'>checked="checked"</c:if> /> Include Citations
        </div>
        <input type="hidden" name="sort" value="<c:if test='${ empty param.sort }'>rlv</c:if><c:if test='${ ! empty param.sort }'><c:out value='${ param.sort }'/></c:if>" />
        <input type="hidden" name="t" value="doc" />
      </form>
    </div>
		  
	<div id="search_auth">
	  <form method="get" action="<c:url value='/search'/>" enctype="application/x-www-form-urlencoded">
	    <label>Authors:</label>
        <input class="s_field" type="text" name="q" value="<c:out value='${ query }'/>" />
        <input class="s_button" type="submit" name="submit" value="Search" alt="Search" />
        <div class="opts">
          <a href="<c:url value='/advanced_search'/>" title="Search full text, title, abstract, date, author name, author affiliation, etc.">Advanced Search</a>
          <input class="c_box" type="checkbox" name="ic" value="1" <c:if test='${ ! empty param.ic }'>checked="checked"</c:if> /> Include Citations |
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
        <input type="hidden" name="t" value="table" />
        <input type="hidden" name="sort" value="<c:if test='${ empty param.sort }'>rlv</c:if><c:if test='${ ! empty param.sort }'><c:out value='${ param.sort }'/></c:if>" />
     </form>
    </div>
</c:if>
<c:if test="${ !empty param.q && param.t == 'doc'}">
	  <div id="search_docs">
	   <form method="get" action="<c:url value='/search'/>" enctype="application/x-www-form-urlencoded">
	   	<label>Documents:</label>
	     <input class="s_field" type="text" name="q" value="<c:out value='${ query }'/>" />
	     <input class="s_button" type="image" name="submit" value="Search" alt="Search" src="<c:url value='/images/search_icon.png' />" onclick='this.form.action="<c:url value='/search' />"; return true;' />
	     <input class="s_button" type="image" name="s2" value="Semantic Scholar" alt="Semantic Scholar" src="<c:url value='/images/s2_icon.png' />" onclick="this.form.action='http://s2.allenai.org/search'; return true;" />
			 <div class="opts">
		     <a href="<c:url value='/advanced_search'/>" title="Search full text, title, abstract, date, author name, author affiliation, etc.">Advanced Search</a>
		     <input class="c_box" type="checkbox" name="ic" value="1" <c:if test='${ ! empty param.ic }'>checked="checked"</c:if> /> Include Citations
		   </div>
		   <input type="hidden" name="sort" value="<c:if test='${ empty param.sort }'>rlv</c:if><c:if test='${ ! empty param.sort }'><c:out value='${ param.sort }'/></c:if>" />
		   <input type="hidden" name="t" value="doc" />
	   </form>
	  </div>
  </c:if>
  <c:if test="${ !empty param.q && param.t == 'auth' }">
    <div id="search_auth">
      <form method="get" action="<c:url value='/search'/>" enctype="application/x-www-form-urlencoded">
        <label>Authors:</label>
	    <input class="s_field" type="text" name="q" value="<c:out value='${ query }'/>" />
        <input class="s_button" type="submit" name="submit" value="Search" alt="Search" />
        <div class="opts">
          <a href="<c:url value='/advanced_search'/>" title="Search full text, title, abstract, date, author name, author affiliation, etc.">Advanced Search</a>
          <input class="c_box" type="checkbox" name="ic" value="1" <c:if test='${ ! empty param.ic }'>checked="checked"</c:if> /> Include Citations |
          <input class="c_box" type="checkbox" name="uauth" value="1" <c:if test='${ ! empty param.uauth }'>checked="checked"</c:if> />Disambiguate
	    </div>
        <input type="hidden" name="sort" value="<c:if test='${ empty param.sort }'>cite</c:if><c:if test='${ ! empty param.sort }'><c:out value='${ param.sort }'/></c:if>" />
        <input type="hidden" name="t" value="auth" />
      </form>
    </div>
  </c:if>
  <c:if test="${ !empty param.q && param.t == 'table' }">
    <div id="search_tables">
      <form method="get" action="<c:url value='/search'/>" enctype="application/x-www-form-urlencoded">
        <label>Tables:</label>
        <input class="s_field" type="text" name="q" value="<c:out value='${ query }'/>" />
        <input class="s_button" type="submit" name="submit" value="Search" alt="Search" />
        <input type="hidden" name="t" value="table" />
      </form>
    </div>
  </c:if>
</div>
