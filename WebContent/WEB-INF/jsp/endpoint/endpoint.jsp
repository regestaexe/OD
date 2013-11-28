<%if(request.getAttribute("published")!=null){%>
<%=request.getAttribute("published")%>
<%}else{%>
success
<%}%>