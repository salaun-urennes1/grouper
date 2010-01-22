<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!--
  $Id: inspectMessages.jsp,v 1.2 2006-05-16 17:37:35 ddonn Exp $
  $Date: 2006-05-16 17:37:35 $
  
  Copyright 2004 Internet2 and Stanford University.  All Rights Reserved.
  Licensed under the Signet License, Version 1,
  see doc/license.txt in this distribution.
-->

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta name="robots" content="noindex, nofollow" />
    <title>
      <%=ResLoaderUI.getString("inspectMessages.title") %>
    </title>
    <link href="styles/signet.css" rel="stylesheet" type="text/css" />
    <script language="JavaScript" type="text/javascript" src="scripts/signet.js">
    </script>
  </head>
  <body>

<%@ page import="java.util.*" %>
<%@ page import="org.apache.struts.*" %>
<%@ page import="org.apache.struts.util.*" %>
<%@ page import="org.apache.struts.action.*" %>
<%@ page import="edu.internet2.middleware.signet.resource.ResLoaderUI" %>

<%
  // Print all attributes in the request object
  out.println("<p><b>" + ResLoaderUI.getString("inspectMessages.allattrs_req.txt") + "</b>");
  Enumeration paramNames = request.getAttributeNames();
  while (paramNames.hasMoreElements()) {
    String name = (String) paramNames.nextElement();
    Object values = request.getAttribute(name);
    out.println("<br> " + name + ":" + values);
  }
  
  // Print all attributes in the session object
  out.println("<p><b>" + ResLoaderUI.getString("inspectMessages.allattrs_sess.txt") + "</b>");
  paramNames = session.getAttributeNames();
  while (paramNames.hasMoreElements()) {
    String name = (String) paramNames.nextElement();
    Object values = session.getAttribute(name);
    out.println("<br> " + name + ":" + values);
  }

  out.println("<p><b>" + ResLoaderUI.getString("inspectMessages.actmsgdata.txt") + "</b>");

  // Get the ActionMessages 
  Object o = request.getAttribute(Globals.MESSAGE_KEY);
  if (o != null)
  {
    ActionMessages ae = (ActionMessages)o;

    // Get the locale and message resources bundle
    Locale locale = 
      (Locale)session.getAttribute(Globals.LOCALE_KEY);
    MessageResources messages = 
      (MessageResources)request.getAttribute
      (Globals.MESSAGES_KEY);

    // Loop thru all the labels in the ActionMessage's  
    for (Iterator i = ae.properties(); i.hasNext();) {
      String property = (String)i.next();
      out.println("<br>" + ResLoaderUI.getString("inspectMessages.property.txt") + " " + property + ": ");

      // Get all messages for this label
      for (Iterator it = ae.get(property); it.hasNext();) {
        ActionMessage a = (ActionMessage)it.next();
        String key = a.getKey();
        Object[] values = a.getValues();
        out.println(" [key=" + key + 
          ", message=" + 
          messages.getMessage(locale,key,values) + 
          "]");
      }
    }
  }
%>
  </body>
</html>