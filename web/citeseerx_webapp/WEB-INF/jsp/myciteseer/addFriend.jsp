<%@ include file="shared/IncludeMyCSXHeader.jsp" %>
<h2>Add a Friend</h2>
<form action="<c:url value="/myciteseer/action/addFriend"/>" method="post">
  <tableborder="0" cellspacing="0" cellpadding="5">
    <tr>
      <td alignment="right" width="23%">Friend ID:</td>
      <spring:bind path="friend.id">
        <td width="77%">
          <input type="text" name="friend.id">
        </td>
       </spring:bind>
    </tr>
  </table>
  <br>
  <input type="submit" alignment="center" value="Add to My Network">
</form>
<h4>
<a href="javascript:window.close();">I've changed my mind - cancel and close the window</a>.
</h4>
</div> <%-- end main div --%>
<div class="clear"></div>
</div>
<%@ include file="../shared/IncludeFooter.jsp" %>