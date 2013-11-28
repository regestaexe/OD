<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Notes"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<script type="text/javascript">
<%
if(request.getAttribute("noteList")!=null){
	List<Notes>  noteList = (List<Notes>) request.getAttribute("noteList");
	for(int i=0;i<noteList.size();i++){
		Notes notes = noteList.get(i);
		if(notes.getDepartments()==null){%>
			$('#postit_container').append('<a id="href_note_<%=notes.getIdNote()%>" href="#no" onclick="openPostit(\'<%=notes.getIdNote()%>\',\'<%=JsSolver.escapeSingleApex(notes.getNoteText().replaceAll("\n"," ").replaceAll("\r"," "))%>\',\'my\')"><img id="<%=notes.getIdNote()%>" src="img/notes/postit_y.gif" border="0" title="<%=StringEscapeUtils.escapeHtml(notes.getNoteText().replaceAll("\n"," ").replaceAll("\r"," "))%>" alt="<%=StringEscapeUtils.escapeHtml(notes.getNoteText().replaceAll("\n"," ").replaceAll("\r"," "))%>"/></a>');
		<%}else{%>
			$('#postit_container').append('<a id="href_note_<%=notes.getIdNote()%>" href="#no" onclick="openPostit(\'<%=notes.getIdNote()%>\',\'<%=JsSolver.escapeSingleApex(notes.getNoteText().replaceAll("\n"," ").replaceAll("\r"," "))%>\',\'dep\')"><img id="<%=notes.getIdNote()%>" src="img/notes/postit_g.gif" border="0" title="<%=StringEscapeUtils.escapeHtml(notes.getNoteText().replaceAll("\n"," ").replaceAll("\r"," "))%>" alt="<%=StringEscapeUtils.escapeHtml(notes.getNoteText().replaceAll("\n"," ").replaceAll("\r"," "))%>"/></a>');
		<%}
	}
}
%>
</script>
