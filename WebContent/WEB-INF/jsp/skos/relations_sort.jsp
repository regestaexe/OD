<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.Set"%>
<%@include file="../locale.jsp"%>
<%@page import="com.openDams.bean.Relations"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%
Records records = (Records)request.getAttribute("records");
TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
List<Records> list = (List<Records>)request.getAttribute("recordsList");
%>
{ "success": true, "data": [
<%for(int i=0;i<list.size();i++){
   Records records2 = list.get(i);
   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
   String[] labels = parsedTitle2.get("label");
	String label ="";
	for(int s=0;s<labels.length;s++){
		label+=labels[s];
	}
    %>
		{"id":"<%=records2.getIdRecord()%>","title":"<%=parsedTitle2.get("notation")[0]%> <%=label%>"}<%if(i!=list.size()-1){%>,<%}%>	
	<%
 }%>	
]
}