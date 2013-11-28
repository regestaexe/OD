<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%> 
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="com.regesta.framework.xml.XMLBuilder"%>
<%
if(request.getAttribute("records")!=null){
Records records = (Records)request.getAttribute("records");
String xml = new String(records.getXml(),"UTF-8");
XMLBuilder xmlBuilderNew = new XMLBuilder(xml,"UTF-8");
xml = xmlBuilderNew.getXML("UTF-8",true);
%>
<div style="border:0;">
<textarea name="codeXML" id="codeXML" class="xml:nogutter:nocontrols" style="display:none">
<%=xml%>
</textarea>
</div>
<%}else{%>
<div class="scheda">
<h1>selezionare un record</h1>	
</div>	
<%}%>