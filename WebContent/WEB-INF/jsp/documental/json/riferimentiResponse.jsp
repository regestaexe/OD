<%@page contentType="application/json; charset=UTF-8"%>{"valori":[<%
 String[] urls = (String[])request.getAttribute("urls");
if(urls!=null){
for(int i=0;i<urls.length;i++){
	%>"<%=urls[i] %>",<%
}
}
%>"<%=request.getAttribute("errorMsg") %>"]}