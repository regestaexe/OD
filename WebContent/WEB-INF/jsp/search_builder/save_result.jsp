<%@page import="com.openDams.bean.Records"%>
<%if(request.getAttribute("records")!=null){
Records records = (Records)request.getAttribute("records");
%>
	<%=records.getIdRecord()%>	
<%}%>
