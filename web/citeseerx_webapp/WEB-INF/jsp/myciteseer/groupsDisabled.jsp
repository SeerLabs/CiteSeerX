<%@ include file="IncludeTop.jsp" %>
<div class="mypagecontent clearfix">
  <div class="columns-float-sec">
    <div class="column-one-sec"> <!-- center column -->
      <div class="column-one-content clearfix">
        <div id="center-content"> <!-- Main content -->
          <div class="inside pushdown"> <!-- to give some room between columns -->
            <h1>This feature is currently disabled.</h1>
          </div> <!-- end of inside -->
        </div> <!-- end of center-content -->
      </div> <!-- End column-one-content -->
    </div> <!-- End column-one (center column) -->
    <div class="column-two-sec"> <!-- Left column -->
    <div class="column-two-content"></div> <!-- End column-two-content -->
    </div> <!-- End column-two (Left column) -->
    <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
  </div><!-- End columns-float -->
  <div class="column-three-sec"> <!-- right column -->
    <div class="column-three-content"></div>
  </div> <!-- End column-three -->
  <div class="box-clear">&nbsp;</div><!-- # needed to make sure column 3 is cleared || but IE5(PC) and OmniWeb don't like it  -->
  <div class="nn4clear">&nbsp;</div><!-- # needed for NN4 to clear all columns || not needed by any other browser -->
  <!--<div id="right-sidebar">--> <!-- contains right content-->
  <!--   <div class="inside">--> <!-- to give some room between columns-->
  <!--  </div>--> <!-- end of inside-->
  <!--</div>--> <!-- end of right-sidebar-->
</div> <!-- End mypagecontent -->
<%@ include file="../shared/IncludeFooter.jsp" %>
