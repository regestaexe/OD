
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="com.openDams.bean.NoteType"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.openDams.utility.StringsUtils"%>
<%@page import="com.openDams.bean.Notes"%>
<%@page import="java.util.List"%>

<%if(request.getAttribute("noteList")!=null && ((List<Notes>)request.getAttribute("noteList")).size()>0){%>
{"notes":[
<%
	List<Notes> notesList = (List<Notes>)request.getAttribute("noteList");
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat formatterDests = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
	for(int i=0;i<notesList.size();i++){
		Notes notes = notesList.get(i);
		String date = "";
		//String time = "";
		try {
			date = StringUtils.substringBefore(notes.getDate().toString()," ").replaceAll("-","");
			//time = StringUtils.substringAfter(notes.getDate().toString()," ");
			Date data = formatter.parse(date);
			date = formatterDests.format(data);
			//date += " "+time; 
		} catch (Exception e) {
			date = notes.getDate().toString();
		}
		String noteImg = "<img title=\"concluso\" alt=\"concluso\" src=\"img/archive_img/ended.gif\">";
		if(notes.getNoteTypes().getIdNoteType()== NoteType.USER_APPLICATION_NOTE){
			noteImg = "<a href=\"#no\" onclick=\"writeArchiveNote('"+notes.getIdNote()+"','"+JsSolver.escapeSingleApex(notes.getNoteTitle())+"');\"><img title=\"modifica nota\" alt=\"modifica nota\" src=\"img/archive_img/page_white_edit.gif\"></a>";
		}
%>
    {"id":<%=notes.getIdNote()%>,"title":<%=StringsUtils.escapeJson(notes.getNoteTitle())%>,"date":<%=StringsUtils.escapeJson(date)%>,"img":<%=StringsUtils.escapeJson(noteImg)%>}
    <%if(i<notesList.size()-1){%>,<%}%>
<%	}%>
],"totalCount":<%=notesList.size()%>}
<%}else{%>
	{"notes":[],"totalCount":0}	
<%}%>