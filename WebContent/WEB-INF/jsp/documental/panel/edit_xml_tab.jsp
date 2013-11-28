<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
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
<script>
$(document).ready(function(){
	$("#saveXMLConfiguration").click(function(){ 
		 var val = (editor.getValue());
		 saveAnXML(globalOpt.idArchivio,globalOpt.document.id,val);
	});
});
</script>
<div style="border:0;">
<textarea id="code" name="code">
<%=StringEscapeUtils.escapeXml(xml)%>
</textarea>
<div style="cursor:pointer;float:right;font-size:13px;margin-top:10px;" id="saveXMLConfiguration">salva xml</div>
</div>	
<script>
var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
  mode: "application/xml",
  lineNumbers: true,
  onCursorActivity: function() {
    editor.setLineClass(hlLine, null);
    hlLine = editor.setLineClass(editor.getCursor().line, "activeline");
  }
});
var hlLine = editor.setLineClass(0, "activeline");
</script>
<%}%>