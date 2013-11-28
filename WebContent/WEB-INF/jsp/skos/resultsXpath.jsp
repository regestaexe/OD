<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<html>
<head></head>
<body>
<%
if(request.getAttribute("results")!=null){
	ArrayList<Records> hits = (ArrayList<Records>)request.getAttribute("results");%>
	numero risultati : <%=hits.size()%><br/>
	<%
	String id_records="";
	for (int i = 0; i < hits.size(); i ++) {
		Records docsearch = hits.get(i);
		id_records+="#"+docsearch.getIdRecord();
%>
<%=(i+1)%>) <%=docsearch.getIdRecord()+" | "+docsearch.getTitle()%><br/>
<%}%>
		<%if(hits.size()>1){%>
			<br/>
			<form action="xpathFilter.html" name="xpathFilter_form">
						<input type="hidden" name="id_records" value="<%=id_records%>">
						filtra per XPATH : 
						<br/><input type="text" name="xPath" style="width:500px;" value="/rdf:RDF/skos:Concept/skos:altLabel[child::text()='Mitzi']">
						<input type="submit">
			</form>
		<%}%>
<%}%>

</body>
</html>