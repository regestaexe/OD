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
	ArrayList<VocTerm> results = (ArrayList<VocTerm>)request.getAttribute("results");
	TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
	for (int i = 0; i < results.size(); i++) {
		VocTerm vocTerm = results.get(i);
		if(vocTerm.getFrequence()>1){
			if(vocTerm.getFrequence()>1){
				%>
				

<div class="duplicate_row">
					<span style="text-decoration:underline;"><span style="font-weight: bold;"><%=vocTerm.term%></span> <span style="font: 10px;">numero occorrenze:</span> <span style="font-weight: bold;"><%=vocTerm.frequence%></span></span> 
					<ul style="margin-left:30px;list-style: decimal;">
					<%
						ArrayList<Document> docs = vocTerm.getDocs();
						for (int j = 0; j < docs.size(); j++) {
							Document docsearch = docs.get(j);				
							HashMap<String, String[]> parsedTitle = titleManager.parseTitle(docsearch.get("title_record"),new Integer(request.getParameter("idArchive")));
							String[] labels = parsedTitle.get("label");
						 	String label ="";
						 	for(int s=0;s<labels.length;s++){
						 		label+=labels[s];
						 	}
							%>
							<li class="duplicate_li"><a class="relation_link" onclick="openRecordFromDuplicate('<%=docsearch.get("id_record")%>');" href="#no"><%=label%></a></li>
						<%}
					%>
					</ul>
				</div>
				<%	
			}
		}
	}
}%>