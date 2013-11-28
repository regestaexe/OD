<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>

<%@page import="java.util.List"%>

<%@page import="org.apache.lucene.document.Document"%><html>
<head></head>
<body>
<%
if(request.getAttribute("results")!=null){
	Document[] hits = (Document[])request.getAttribute("results");%>
	numero risultati : <%=hits.length%><br/>
	<%
	String id_records="";
	for (int i = 0; i < hits.length; i ++) {
		Document docsearch = hits[i];
		id_records+="#"+docsearch.get("id_record");
%>
<%=(i+1)%>) <%=docsearch.get("id_record")+" | "+docsearch.get("title_record")%><br/>
<%}%>
		<%if(hits.length>1){%>
			<br/>
			<form action="xpathFilter.html" name="xpathFilter_form">
						<input type="text" name="id_records" value="<%=id_records%>">
						filtra per XPATH : 
						<br/><input type="text" name="xPath" style="width:500px;" value="/rdf:RDF/skos:Concept/skos:altLabel[child::text()='Mitzi']">
						<input type="submit">
			</form>
		<%}%>
<%}%>

</body>
</html>