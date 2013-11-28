
<%@page import="com.openDams.title.configuration.OpenDamsTitleManagerProvider"%><%@page import="com.openDams.bean.Archives"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.utility.StringsUtils"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.regesta.framework.util.StringUtility"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.lucene.document.Document"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
	response.setContentType("application/json");
%>

<%
	if (request.getAttribute("deletedRecordsList") != null) {
		List<Records> deletedRecordsList = (List<Records>)request.getAttribute("deletedRecordsList");
%>
{ "success": true, "totalCount":<%=request.getAttribute("totDeletedRecords")%>, "records": [<%
		for (int i = 0; i < deletedRecordsList.size(); i++) {
			Records records = deletedRecordsList.get(i);
			String date = formatter.format(records.getModifyDate());
			TitleManager titleManager = OpenDamsTitleManagerProvider.getTitleManager();
			HashMap<String, String[]> parsedTitle = titleManager.parseTitle(records.getTitle(),records.getArchives().getIdArchive());
			String title = titleManager.getFieldValues(parsedTitle.get("notation"))+" "+ titleManager.getFieldValues(parsedTitle.get("label"));
			String description = titleManager.getFieldValues(parsedTitle.get("descrizione"));
		%>
			
				{"id":"<%=records.getIdRecord()%>","title":<%=StringsUtils.escapeJson(title)%>,"date":"<%=date%>","description":<%=StringsUtils.escapeJson(description)%>}
				<%
					if (i < (deletedRecordsList.size() - 1)) {
				%>,<%
					}
		}
%>
] }
<%
	} else {
%>
{ "success": false }
<%
	}
%>
