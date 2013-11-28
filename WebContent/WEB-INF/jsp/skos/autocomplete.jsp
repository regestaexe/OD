<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.lucene.document.Document"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%if(request.getAttribute("results")!=null){

	Document[] hits = (Document[])request.getAttribute("results");
	TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
	int limit = (Integer)request.getAttribute("limit");
	%>
	{
    "success": true,
    "totalCount":<%=hits.length%>,
    "data": [
	<%
	int count = 0;
	for (int i = 0; i < hits.length; i++) {
			if(hits[i]!=null){
				Document docsearch = hits[i];				
				HashMap<String, String[]> parsedTitle = titleManager.parseTitle(docsearch.get("title_record"),new Integer(request.getParameter("id_archive")));
				String[] labels = parsedTitle.get("label");
			 	String label ="";
			 	for(int s=0;s<labels.length;s++){
			 		label+=labels[s];
			 	}
			%>
			    {"id": <%=docsearch.get("id_record")%>,"value": "<%=JsSolver.escapeSingleDoubleApex(parsedTitle.get("notation")[0])%> <%=JsSolver.escapeSingleDoubleApex(label.replaceAll("ç",""))%>"<%if(request.getParameter("relation")!=null && !request.getParameter("relation").trim().equals("")){%>,"relation": "<%=request.getParameter("relation")%>"<%}%><%if(request.getParameter("reopen_accordion")!=null && !request.getParameter("reopen_accordion").trim().equals("")){%>,"reopen_accordion": "<%=request.getParameter("reopen_accordion")%>"<%}%>}<%if(i!=hits.length-1 && count!=limit-1){%>,<%}%>        
	<%
				count++;
			}
	}%>
	    ]
	}
<%


}else{%>
    {
    "success": false
    }
<%}%>

