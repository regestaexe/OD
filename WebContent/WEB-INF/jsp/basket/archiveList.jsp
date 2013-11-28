<%@page import="com.openDams.bean.Archives"%>
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
	response.setContentType("application/json");
%>

<%
	if (request.getAttribute("archiveList") != null) {
		List<Archives> archiveList = (List<Archives>)request.getAttribute("archiveList");
%>
{ "success": true, "totalCount":<%=archiveList.size()%>, "archives": [<%
		for (int i = 0; i < archiveList.size(); i++) {
				Archives archives = archiveList.get(i);
		%>
			
				{"id":"<%=archives.getIdArchive()%>","label":<%=StringsUtils.escapeJson(archives.getLabel())%>,"countDeleted":"<%=archives.getCountDeleted()%>"}
				<%
					if (i < (archiveList.size() - 1)) {
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
