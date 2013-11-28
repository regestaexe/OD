<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
short_tab
<%
if(request.getAttribute("records")!=null){
Records records = (Records)request.getAttribute("records");%>

<div style="border:1px solid;">
<%=new String(records.getXml(),"UTF-8")%>
</div>
<%
}
%>