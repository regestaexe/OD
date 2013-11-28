<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Relations"%>
<%@page import="java.util.Set"%>
<%@include file="../../locale.jsp"%>
<script>
<%
String idRecordFather="";
if(request.getParameter("action")!=null && request.getParameter("action").equalsIgnoreCase("getConcepts")){
	idRecordFather = request.getParameter("idRecord");
}
%>
$(document).ready(function(){
	
    $(".<%=idRecordFather%>_concept_record_print").each(function(){
        var idRecord = $(this).attr("idRecord");
        if( $(".<%=idRecordFather%>_record_row_print_children_"+idRecord)!=null &&  $(".record_row_print_children_"+idRecord)!=undefined){
        	$(".<%=idRecordFather%>_record_row_print_children_"+idRecord).html("<img src=\"img/ajax-loader.gif\" border=\"0\"/>");
        	$(".<%=idRecordFather%>_record_row_print_children_"+idRecord).load("ajax/print_branch.html?action=getConcepts&print_mode=<%=request.getParameter("print_mode")%>&idRecord="+idRecord<%if(idRecordFather.equals("")){%>+"&highlightChildren=true"<%}%>);
        }
        $("#print_related_"+idRecord).html("<img src=\"img/ajax-loader.gif\" border=\"0\"/>");
        $("#print_related_"+idRecord).load("ajax/print_branch.html?action=getRelations&print_mode=relations&idRecord="+idRecord+"&idRelationType=3",function(){
    	});
    });
});
</script>
<%
if(request.getAttribute("recordsList")!=null){
	TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
	List<Records> recordsList = (List<Records>)request.getAttribute("recordsList");
	for(int i=0;i<recordsList.size();i++){
		Records records = recordsList.get(i);
		HashMap<String, String[]> parsedTitle = titleManager.parseTitle(records.getTitle(),records.getArchives().getIdArchive());	
		int tot_children = 0;
		tot_children = records.getTotChildren();
		%>
		<%if(idRecordFather.equals("")){%>
		<div class="record_row_print" style="background-color:#ffffff;float:left;width:100%;margin:0;padding:0;">
			<table class="record_row_print" border="0" cellpadding="0" cellspacing="0" style="width:1054px;border:1px solid #41428b;background-color:#deecfd;margin:0;">
				<tr>
				    <td style="width:100px;padding:5px;font-weight:bold;border-right:1px solid #41428b;">Codice Griglia</td>
					<td style="width:300px;padding:5px;font-weight:bold;border-right:1px solid #41428b;">Denominazione</td>
					<td style="width:200px;padding:5px;font-weight:bold;border-right:1px solid #41428b;">Descrizione</td>
					<td style="width:200px;padding:5px;font-weight:bold;border-right:1px solid #41428b;">Note</td>
					<td style="width:200px;padding:5px;font-weight:bold;">Voci Associate</td>
				</tr>
			</table>
		</div>
		<%}%>
		<div class="record_row_print" style="background-color:#FFFFFF;float:left;width:100%;margin:0;padding:0;">
		    <%int count_dot = StringUtils.countMatches(parsedTitle.get("notation")[0].trim(),".");%>
			<table class="record_row_print" border="0" cellpadding="0" cellspacing="0" style="width:1054px;border:1px solid #41428b;border-top:0;<%if(request.getParameter("highlightChildren")!=null){%>font-weight:bold;<%}%>background-color:<%if(request.getParameter("highlightChildren")!=null){%>#b1aeae;<%}else if(count_dot==1){%>#cccccc;<%}else{%>#FFFFFF;<%}%>margin:0;">
					 <%
					    boolean deprecated = false;
					 	try{
					 		if(!parsedTitle.get("deprecated")[0].trim().equals("")){
					 			deprecated=true;
					 		}
					 	}catch(NullPointerException ex){}
					 	String[] labels = parsedTitle.get("label");
					 	String label ="";
					 	for(int s=0;s<labels.length;s++){
					 		label+=labels[s];
					 	}
					 	String altLabel = "";
					 	int countAltLabel = records.getXMLReader().getNodeCount("/rdf:RDF/*/skos:altLabel");
						for(int z=0;z<countAltLabel;z++){
							if(!records.getXMLReader().getNodeValue("/rdf:RDF/*/skos:altLabel["+(z+1)+"]/text()").trim().equals("")){					
								altLabel+="</br>"+records.getXMLReader().getNodeValue("/rdf:RDF/*/skos:altLabel["+(z+1)+"]/text()").trim();
							}
						}
					 %>
				<tr>	 
				    <td style="width:100px;padding:5px;border-right:1px solid #41428b;<%if(deprecated){%>color:#808080;<%}%>"><%=parsedTitle.get("notation")[0].trim()%></td>
					<td style="width:300px;padding:5px;border-right:1px solid #41428b;<%if(deprecated){%>color:#808080;<%}%>" class="<%=idRecordFather%>_concept_record_print" idRecord="<%=records.getIdRecord()%>"><FONT size="4"><%=label.trim()%></FONT><FONT style="font-style:italic;" size="4"><%=altLabel%></FONT></td>
					<td style="width:200px;padding:5px;border-right:1px solid #41428b;font-style:italic;">
					<%if(!records.getXMLReader().getNodeValue("/rdf:RDF/*/skos:scopeNote/text()").trim().equals("")){%>
					<%=records.getXMLReader().getNodeValue("/rdf:RDF/*/skos:scopeNote/text()").trim()%>
					<%}else{%>
					&nbsp;
					<%}%>
					</td>
					<td style="width:200px;padding:5px;font-style:italic;border-right:1px solid #41428b;">
					<%int counteditorialNote = records.getXMLReader().getNodeCount("/rdf:RDF/*/skos:editorialNote");
					for(int z=0;z<counteditorialNote;z++){
						if(!records.getXMLReader().getNodeValue("/rdf:RDF/*/skos:editorialNote[@rdf:parseType='Resource']["+(z+1)+"]/dc:description/text()").trim().equals("")){
					%>
							<div style="margin:0;padding:0;"><%=records.getXMLReader().getNodeValue("/rdf:RDF/*/skos:editorialNote[@rdf:parseType='Resource']["+(z+1)+"]/dc:description/text()").trim()%></div>
					<%	}
					}%>
					<%if(counteditorialNote==0){%>&nbsp;<%}%>
					</td>
					<td style="width:200px;padding:5px;font-style:italic;" id="print_related_<%=records.getIdRecord()%>"></td>
				</tr>
			</table>
		</div>
		<%if(tot_children>0){%>
		<div class="<%=idRecordFather%>_record_row_print_children_<%=records.getIdRecord()%>" style="float:left;width:100%;margin:0;padding:0;"></div>
		<%}%>
		<%
	}
}%>
