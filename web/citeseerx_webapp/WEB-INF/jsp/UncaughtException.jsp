<%--
  -- Header for UncaughtException.jsp
  --
  -- Author: Isaac Councill
  --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="shared/IncludeTagLibs.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
  <head>
    <title>Unknown URL</title>
  </head>
  <body>
  <div id="center_content" class="clearfix"> <!-- Contains header div -->
    <div id="primary_content">
      <div id="main_content">
      <div class="inside pushdown">
        <div class="error">
          <center><h1>We are sorry !</h1>
          <h3>The URL does not match any resource in our repository.<br>
	  </center>
        </div>
        </div>
      </div> <!-- End main_content -->
    </div> <!-- End primary_content -->
  </div> <!-- End center-content -->
<%@ include file="IncludeBottom.jsp" %>
  </body>
</html>
