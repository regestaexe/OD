<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="org.apache.lucene.document.Document"%>
<%@page import="com.openDams.index.searchers.VocTerm"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<script>
function openRecordFromDuplicate(id_record){
	Ext.getCmp('relations_tab_tab').show();	
	openRecord(id_record);
}
</script>
<%if(request.getAttribute("results")!=null){
	Document[] results= (Document[])request.getAttribute("results");
	TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
	for (int i = 0; i < results.length; i++) {
		Document docsearch = results[i];	

				%>
<div class="duplicate_row">
	<ul style="margin-left:5px;list-style:none;">
	<%

			HashMap<String, String[]> parsedTitle = titleManager.parseTitle(docsearch.get("title_record"),new Integer(request.getParameter("idArchive")));
			String[] labels = parsedTitle.get("label");
			String[] notations = parsedTitle.get("notation");
		 	String label ="";
		 	String notation="";
		 	for(int s=0;s<labels.length;s++){
		 		label+=labels[s];
		 	}
		 	for(int s=0;s<notations.length;s++){
		 		notation+=notations[s];
		 	}
			%>
			<li class="duplicate_li"><a class="relation_link" onclick="openRecordFromDuplicate('<%=docsearch.get("id_record")%>');" href="#no"><%=notation%> <%=label%></a></li>
		<%
	%>
	</ul>
</div>
				<%	
			
		
	}
}%>