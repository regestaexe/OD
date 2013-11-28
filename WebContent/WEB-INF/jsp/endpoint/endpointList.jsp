<%@page import="java.util.List"%>
<%if(request.getAttribute("endPointList")!=null){
List<String> endPointList = (List<String>)  request.getAttribute("endPointList");%>
 {"endpoints":[
<%for (int i = 0; i < endPointList.size(); i++) {%>
		{"endpoint":"<%=endPointList.get(i)%>"}
<%
if(i<endPointList.size()-1){%>,<%}
}%>
],"totalCount":<%=endPointList.size()%>} 
<%}%>