<%@page import="java.util.ArrayList"%>
<%if(request.getAttribute("querys")!=null){%>
	<%ArrayList<String> querys = (ArrayList<String> )request.getAttribute("querys");
	String query="";
	for(int i=0;i<querys.size();i++){
		query+=request.getAttribute("query_field")+":"+querys.get(i)+"$";
	}
	query+="";
	%>
	<%=query%>
<%}%>