<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Messages"%>
<%if(request.getAttribute("messages")!=null){
	Messages messages = (Messages)request.getAttribute("messages");
	%>
	<div class="messageDetails">
	<%=messages.getMessageText()%>
	</div>
	<div style="display: none;">
		<textarea id="hidden_msg_text"><%=messages.getMessageText()%></textarea>
		<textarea id="hidden_msg_object">Re:<%=messages.getObject()%></textarea>
		<textarea id="hidden_msg_sendTo">user_<%=messages.getUsersByRefIdUserSender().getIdUser()%></textarea>
		<textarea id="hidden_msg_sendTo_view"><%=messages.getUsersByRefIdUserSender().getUsersProfile().getName()+" "+messages.getUsersByRefIdUserSender().getUsersProfile().getLastname()%></textarea>
	</div>
	<%
}%>
