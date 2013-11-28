<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%>
<%@page import="com.openDams.bean.Departments"%>
<html>
	<head></head>
	<body>
				<%if(request.getAttribute("departmentsList")!=null){ 
						        	   List<Object> departments = (List)request.getAttribute("departmentsList");
						        	   Object[] departmentsList = departments.toArray();
						        	   for(int i=0;i<departmentsList.length;i++){
						        		   Departments department = (Departments)departmentsList[i];
						        	%>
						        			<%=department.getDescription()%><br />
						        	<%}%>
				<%}%>
	</body>
</html>