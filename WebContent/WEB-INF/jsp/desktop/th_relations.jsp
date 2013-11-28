<%@page import="java.util.Set"%><%@page import="com.openDams.bean.Relations"%><%@page import="com.openDams.bean.Records"%><%@page import="com.openDams.title.configuration.TitleManager"%><%@page import="java.util.HashMap"%><%@page import="java.util.List"%><%@page import="com.regesta.framework.util.JsSolver"%>

	<%if(request.getAttribute("recordsList")!=null){
		TitleManager manager = (TitleManager) request.getAttribute("titleManager");
		List<Object> recordsList = (List)request.getAttribute("recordsList");
		for(int i=0;i<recordsList.size();i++){
			Records record = (Records)recordsList.get(i);
			HashMap<String, String[]> map = manager.parseTitle(record.getTitle(), record.getArchives().getIdArchive());
					 
	%>
			<div style="padding: 2px;"><a href="#n" onclick="return doMultiSearch('<%=record.getIdRecord()%>','<%=JsSolver.escapeSingleApex(map.get("notation")[0]+" "+map.get("label")[0])%>')">[<%=record.getArchives().getLabel()%>] <%=map.get("notation")[0]%> - <%=map.get("label")[0]%></a></div>
	<%
			}
		}
	%>
