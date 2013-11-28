<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Users"%>  

<%if(request.getAttribute("usersList")!=null){
	List<Object> users = (List)request.getAttribute("usersList");
	Object[] usersList = users.toArray();
%>
  		{  
  		   "total":"<%=request.getAttribute("total")%>",
  		   "data":[
      	<%      	   
      	   for(int i=0;i<usersList.length;i++){
      		   Users user = (Users)usersList[i];
      		   
      	%>
      	  { 
      	    "id": "<%=user.getIdUser()%>",
      	  	"username": "<%=user.getUsername()%>",
			"name":"<%if(user.getUsersProfile()!=null){%><%=user.getUsersProfile().getName()%><%}%>",
         	"lastname":"<%if(user.getUsersProfile()!=null){%><%=user.getUsersProfile().getLastname()%><%}%>",
			"ref_id_company":"<%=user.getCompanies().getCompanyName()%>",  
			"ref_id_department": "<%if(user.getDepartments()!=null){%><%=user.getDepartments().getDescription()%><%}%>",  
			"email": "<%if(user.getUsersProfile()!=null && user.getUsersProfile().getEmail()!=null){%><%=user.getUsersProfile().getEmail()%><%}%>",  
			"birth_date": "<%if(user.getUsersProfile()!=null && user.getUsersProfile().getBirthDate()!=null){%><%=user.getUsersProfile().getBirthDate()%><%}%>",
			"telephone_number": "<%if(user.getUsersProfile()!=null  && user.getUsersProfile().getTelephoneNumber()!=null){%><%=user.getUsersProfile().getTelephoneNumber()%><%}%>",  
			"active": "true",  
			"language": "IT"
         	
      	  }<%if(i<usersList.length-1){%>,<%}%>
      	<%}%>
      	   ]
		}
<%}%>    



