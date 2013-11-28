<script>
$(document).ready(function(){
	$("#saveXMLConfiguration").click(function(){ 
		 var val = encodeURI(editor<%=request.getParameter("xml_type")%>.getValue());  
		 $.post("editXmlConf.html",
				 { xml:val,action:'save_xml',xml_type:'<%=request.getParameter("xml_type")%>'},
				  function(data){
					  Ext.Msg.alert('Attenzione','Configurazione salvata correttamente!');
				  }
				);
	});
});
</script>
<div style="margin:5px;border: 1px solid #6593cf;font-size:13px;padding:5px;">
<textarea id="code<%=request.getParameter("xml_type")%>" name="code<%=request.getParameter("xml_type")%>">
<%=request.getAttribute("xml") %>
</textarea>
<div style="cursor:pointer;float:right;font-size:13px;margin-top:10px;" id="saveXMLConfiguration">salva configurazione</div>
</div>	
<script>
var editor<%=request.getParameter("xml_type")%> = CodeMirror.fromTextArea(document.getElementById("code<%=request.getParameter("xml_type")%>"), {
  mode: "application/xml",
  lineNumbers: true,
  onCursorActivity: function() {
    editor<%=request.getParameter("xml_type")%>.setLineClass(hlLine<%=request.getParameter("xml_type")%>, null);
    hlLine<%=request.getParameter("xml_type")%> = editor<%=request.getParameter("xml_type")%>.setLineClass(editor<%=request.getParameter("xml_type")%>.getCursor().line, "activeline");
  }
});
var hlLine<%=request.getParameter("xml_type")%> = editor<%=request.getParameter("xml_type")%>.setLineClass(0, "activeline");
</script>
