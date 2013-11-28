
<%@page import="com.openDams.bean.Archives"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="com.openDams.bean.NoteType"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.openDams.utility.StringsUtils"%>
<%@page import="com.openDams.bean.Notes"%>
<%@page import="java.util.List"%>

<%if(request.getAttribute("archiveList")!=null && ((List<Notes>)request.getAttribute("archiveList")).size()>0){%>
{"archives":[
<%
	List<Archives> archiveList = (List<Archives>)request.getAttribute("archiveList");
	for(int i=0;i<archiveList.size();i++){
		Archives archives = archiveList.get(i);
%>
    {"id":<%=archives.getIdArchive()%>,"offline":<%=archives.isOffline()%>}
    <%if(i<archiveList.size()-1){%>,<%}%>
<%	}%>
],"totalCount":<%=archiveList.size()%>}
<%}%>