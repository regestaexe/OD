
<%@page import="com.openDams.bean.Messages"%>
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
	if (request.getAttribute("messagesList") != null) {
		List<Messages> messagesList = (List<Messages>)request.getAttribute("messagesList");
%>
{ "success": true, "totalCount":<%=messagesList.size()%>, "messages": [<%
		for (int i = 0; i < messagesList.size(); i++) {
				Messages messages = messagesList.get(i);
				String date = formatter.format(messages.getDate());
				String sender = messages.getUsersByRefIdUserSender().getUsersProfile().getName()+" "+messages.getUsersByRefIdUserSender().getUsersProfile().getLastname();
		        String message_gif = "message_close";
		        String readed_class = "unreaded_message";
		        if(messages.isReaded()){
		        	message_gif = "message_open";
		        	readed_class = "readed_message";
		        }
		%>
			
				{"id":"<%=messages.getIdMessage()%>","object":<%=StringsUtils.escapeJson(messages.getObject())%>,"date":"<%=date%>","sender":"<%=sender%>","msg_type":"<%=messages.getMessageTypes().getIdMessageType()%>","modal":"<%=messages.isModal()%>","text":<%=StringsUtils.escapeJson(messages.getMessageText())%>}<%
					if (i < (messagesList.size() - 1)) {
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
