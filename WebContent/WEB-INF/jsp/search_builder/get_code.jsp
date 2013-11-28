
<%@page import="com.regesta.framework.util.StringUtility"%><%@page import="com.openDams.bean.SearchSequentials"%>
<%if(request.getAttribute("searchSequentials")!=null){
	SearchSequentials searchSequentials = (SearchSequentials)request.getAttribute("searchSequentials");
%>
<%=searchSequentials.getPrefix()+StringUtility.fillZero(Integer.toString(searchSequentials.getSequentialNumber()),4)%>
<%}else{%>
codice non disponibile
<%}%>