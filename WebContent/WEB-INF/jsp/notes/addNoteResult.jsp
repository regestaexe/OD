<%@page import="com.openDams.bean.Notes"%>
<%
if(request.getAttribute("notes")!=null){
	Notes notes = (Notes) request.getAttribute("notes");%>
	<%=notes.getIdNote() %>
	<%
}%>
