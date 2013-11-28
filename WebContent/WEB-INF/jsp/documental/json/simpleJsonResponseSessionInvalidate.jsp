<%@page contentType="application/json; charset=UTF-8"%>
<%=((String)request.getAttribute("result"))%>
<%
org.apache.catalina.session.StandardSessionFacade standard = new org.apache.catalina.session.StandardSessionFacade(session);
standard.invalidate();
%>