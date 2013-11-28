<%@page import="java.util.ArrayList"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="org.apache.lucene.document.Document"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Arrays"%>
<%@page import="com.openDams.utility.StringsUtils"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Records"%>

<%if(request.getAttribute("results")!=null){	
	Document[] hits = (Document[])request.getAttribute("results");
	TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
	int limit = (Integer)request.getAttribute("limit");
	
	ArrayList<String> labelList = new ArrayList<String>();
	HashMap<String,String> idRecordsMap = new HashMap<String,String>();
	for (int i = 0; i < hits.length; i++) {
		if(hits[i]!=null){
			Document docsearch = hits[i];				
			HashMap<String, String[]> parsedTitle = titleManager.parseTitle(docsearch.get("title_record"),new Integer(request.getParameter("idArchive")));
			String[] multiLabel = parsedTitle.get("label");
		 	String label ="";
		 	for(int s=0;s<multiLabel.length;s++){
		 		label+=multiLabel[s];
		 	}
		 	idRecordsMap.put(label, docsearch.get("id_record"));
		 	labelList.add(label);
		}
	}
	String[] labels = new String[labelList.size()];
	for(int i=0;i<labelList.size();i++){
		labels[i]=labelList.get(i);
	}
	Arrays.sort(labels);
	%>
	{"success": true,"totalCount":<%=hits.length%>,"dataList":[
	<% 
	for(int i=0;i<labels.length;i++){%>
		{"id":<%=idRecordsMap.get(labels[i])%>,"descrizione":<%=StringsUtils.escapeJson(labels[i])%>}
		<%if(i<labels.length-1){%>
			,
		<%}
	}
	%>
	]}
<%}else{%>
	{"success": false,"totalCount":0,"dataList":[]}
<%}%>