
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
<%@page import="com.openDams.bean.Users"%>
<%
	response.setContentType("application/json");
	if (request.getAttribute("usersList") != null) {
		Users my = (Users)request.getAttribute("users");
		List<Users> usersList = (List<Users>)request.getAttribute("usersList");
%>
{ "success": true, "totalCount":<%=usersList.size()%>, "users": [		
<%
	   String depAll = "";
       for (int i = 0; i < usersList.size(); i++) {
				Users users = usersList.get(i);
				String dep = "";
				String itemClass="normal_list_item";				
				if(users.getDepartments()!=null){
					dep = users.getDepartments().getDescription()+" - ";
					itemClass = "normal_list_item_indent";
					if(!depAll.equals(dep)){
						depAll=dep;
					    %>
						{"idUser":"dep_<%=users.getDepartments().getIdDepartment()%>","name":<%=StringsUtils.escapeJson(dep+"Tutti gli utenti")%>,"class":"bold_list_item"},						
					<%}
				}
		%>
			   <%if(request.getParameter("messageMode")==null || !request.getParameter("messageMode").equalsIgnoreCase("system")){%> 
					{"idUser":"user_<%=users.getIdUser()%>","name":<%=StringsUtils.escapeJson(users.getUsersProfile().getLastname()+" "+users.getUsersProfile().getName())%>,"class":"<%=itemClass%>"}
					<%if (i < (usersList.size() - 1)) {%>,<%}%>
			   <%}%>
		<%}%>
		<%if(!request.getParameter("type").equalsIgnoreCase("my_dep") || request.getParameter("messageMode").equalsIgnoreCase("system")){%>
			 <%if(request.getParameter("messageMode")==null || !request.getParameter("messageMode").equalsIgnoreCase("system")){%> ,<%}%> 
			 {"idUser":"all_users","name":<%=StringsUtils.escapeJson("TUTTI GLI UTENTI DI P.A.D.")%>,"class":"bold_list_item"}
		<%}%>
] }
<%
	} else {
%>
{ "success": false }
<%
	}
%>
