<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.Set"%>
<%@page import="com.openDams.bean.Relations"%>
<%@include file="../../locale.jsp"%>
<%
if(request.getAttribute("recordsList")!=null){
TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
List<Records> list = (List<Records>)request.getAttribute("recordsList");
Records records = (Records)request.getAttribute("records");
%>
<%if(request.getParameter("from")!=null){
			String descrizione=records.getXMLReader().getNodeValue("/rdf:RDF/*/skos:scopeNote/text()").trim();
			if(descrizione!=null && !descrizione.equals("")){%>
	        <div><span style="font-weight:bold;">descrizione:</span> <%=descrizione%></div>
			<%}%>	        
	        <div>
	        <%int counteditorialNote = records.getXMLReader().getNodeCount("/rdf:RDF/*/skos:editorialNote");
			for(int z=0;z<counteditorialNote;z++){
				if(!records.getXMLReader().getNodeValue("/rdf:RDF/*/skos:editorialNote[@rdf:parseType='Resource']["+(z+1)+"]/dc:description/text()").trim().equals("")){
			%>
					<span style="font-weight:bold;">nota <%=(z+1)%>:</span> <%=records.getXMLReader().getNodeValue("/rdf:RDF/*/skos:editorialNote[@rdf:parseType='Resource']["+(z+1)+"]/dc:description/text()").trim()%><br/>
			<%	}
			}%>
			</div>
<%}%>
<%for(int i=0;i<list.size();i++){
   Records records2 = list.get(i);
   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
    String[] labels = parsedTitle2.get("label");
	String label ="";
	for(int s=0;s<labels.length;s++){
		label+=labels[s];
	}
    String note="";
    Object[] relationsList = records2.getRelationsesForRefIdRecord1().toArray();
    for(int z=0;z<relationsList.length;z++){
	   Relations relations = (Relations)relationsList[z];
	   if(relations.getRelationTypes().getIdRelationType()==3 && relations.getNote()!=null && relations.getId().getRefIdRecord2()==records.getIdRecord()){
		   note = relations.getNote();
	   }
    }
    relationsList = records2.getRelationsesForRefIdRecord2().toArray();
    for(int z=0;z<relationsList.length;z++){
	   Relations relations = (Relations)relationsList[z];
	   if(relations.getRelationTypes().getIdRelationType()==3 && relations.getNote()!=null && relations.getId().getRefIdRecord1()==records.getIdRecord()){
		   note = relations.getNote();
	   }
    }
   %>       
		<%if(!note.equals("")){%>
		<div><%=note%></div>
		<%}%>
		<div>
			<span class="relation_span"><%=parsedTitle2.get("notation")[0]%> <%=label%></span>
			<%int counteditorialNote2 = records2.getXMLReader().getNodeCount("/rdf:RDF/*/skos:editorialNote");
			for(int z=0;z<counteditorialNote2;z++){
				if(!records2.getXMLReader().getNodeValue("/rdf:RDF/*/skos:editorialNote[@rdf:parseType='Resource']["+(z+1)+"]/dc:description/text()").trim().equals("")){
			%>
					<div style="margin-left:2px;font-style: italic;"><%if(request.getParameter("from")!=null){%>nota <%=(z+1)%>:<%}%><%=records2.getXMLReader().getNodeValue("/rdf:RDF/*/skos:editorialNote[@rdf:parseType='Resource']["+(z+1)+"]/dc:description/text()").trim()%></div>
			<%	}
			}%>
		</div>
<%}%>
<%}%>	

