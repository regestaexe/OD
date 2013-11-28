<%
int personalMessageCount = ((BigInteger)request.getAttribute("personalMessageCount")).intValue();
int departmentMessageCount =  ((BigInteger)request.getAttribute("departmentMessageCount")).intValue();
int systemMessageCount =  ((BigInteger)request.getAttribute("systemMessageCount")).intValue();
personalMessageCount+=departmentMessageCount;
%>

<%@page import="java.math.BigInteger"%><div class="message-list-item message-list-item_1 message-list-item_selected" onclick="openMessageList(1);">
	I miei Messaggi <span><%if(personalMessageCount>0){%>(<%=personalMessageCount%>)<%}%></span>
</div>
<!--div class="message-list-item message-list-item_2" onclick="openMessageList(2);">
	Messaggi Dipartimentali <span><%if(departmentMessageCount>0){%>(<%=departmentMessageCount%>)<%}%></span>
</div-->
<div class="message-list-item message-list-item_3" onclick="openMessageList(3);">
	Avvisi di Sistema <span><%if(systemMessageCount>0){%>(<%=systemMessageCount%>)<%}%></span>
</div>
<div class="message-list-item message-list-item_500" onclick="openMessageList(500);">
	Messaggi inviati <span id="send_message_count"></span>
</div>
<div class="message-list-item message-list-item_501" onclick="openMessageList(501);">
	Messaggi di mio interesse <span id="interest_message_count"></span>
</div>