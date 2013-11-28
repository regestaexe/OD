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
{ "success": true, "totalCount":<%=request.getAttribute("totMessages")%>, "messages": [<%
		for (int i = 0; i < messagesList.size(); i++) {
				Messages messages = messagesList.get(i);
				String date = formatter.format(messages.getDate());
				String sender = "";
				if(request.getParameter("messageType")!=null && request.getParameter("messageType").equals("500")){
					try{
					sender = messages.getUsersByRefIdUserTo().getUsersProfile().getName()+" "+messages.getUsersByRefIdUserTo().getUsersProfile().getLastname();
					}catch(Exception e){
						sender = messages.getUsersByRefIdUserSender().getUsersProfile().getName()+" "+messages.getUsersByRefIdUserSender().getUsersProfile().getLastname();
					}		        
				}else{
		        	sender = messages.getUsersByRefIdUserSender().getUsersProfile().getName()+" "+messages.getUsersByRefIdUserSender().getUsersProfile().getLastname();
		        }
				String department = "";
		        try{
		        	department=messages.getUsersByRefIdUserSender().getDepartments().getDescription();
		        }catch(Exception e){}	
		        	
				String message_gif = "message_close";
		        String readed_class = "unreaded_message";
		        if(messages.isReaded()){
		        	message_gif = "message_open";
		        	readed_class = "readed_message";
		        }
		%>
			
				{"id":"<%=messages.getIdMessage()%>","object":<%=StringsUtils.escapeJson("<span id=\"message_object_"+messages.getIdMessage()+"\" class=\""+readed_class+"\">"+messages.getObject()+"</span>")%>,"date":"<span id=\"message_date_<%=messages.getIdMessage()%>\" class=\"<%=readed_class%>\"><%=date%></span>","sender":"<span id=\"message_sender_<%=messages.getIdMessage()%>\" class=\"<%=readed_class%>\"><%=sender%></span>","readed":"<div id=\"message_gif_<%=messages.getIdMessage()%>\" class=\"<%=message_gif%><%=messages.getMessageTypes().getIdMessageType()%>\"></div>","msg_type":"<%=messages.getMessageTypes().getIdMessageType()%>","department":<%=StringsUtils.escapeJson("<span id=\"message_department_"+messages.getIdMessage()+"\" class=\""+readed_class+"\">"+department+"</span>")%>}<%
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
