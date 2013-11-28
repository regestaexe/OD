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

<link rel="stylesheet" href="js/sh/SyntaxHighlighter.css" type="text/css" />
<script src="js/sh/shCore.js"></script>
<script src="js/sh/shBrushXml.js"></script>
<style>
.dp-highlighter {
  white-space: nowrap;
  overflow: visible;
  width: 100%;
  font-size: 11px;
  font-family:Courier New,monospace;
}
</style>
<script>
dp.SyntaxHighlighter.HighlightAll('code');
</script>
<div style="border:0;">
<textarea name="code" class="xml:nogutter:nocontrols">
<%=xml%>
</textarea>
</div>
<%}%>