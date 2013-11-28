
<%@page import="com.regesta.framework.util.DateUtility"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="com.regesta.framework.xml.XMLReader"%>
<%@page import="com.openDams.bean.Records"%>
<%if(request.getAttribute("records")!=null){
	Records records = (Records)request.getAttribute("records");
	String save_title = records.getXMLReader().getNodeValue("/rdf:RDF/ods:ricerca/dc:title/text()");
	String save_date = records.getXMLReader().getNodeValue("/rdf:RDF/ods:ricerca/dc:date/text()");
	if(!save_date.equals("")){
		save_date = DateUtility.NormalDateToUserSate(save_date);
	}	
	String save_description = records.getXMLReader().getNodeValue("/rdf:RDF/ods:ricerca/dc:description/text()");
	String save_legislatura = records.getXMLReader().getNodeValue("/rdf:RDF/ods:ricerca/ods:rif_leg/@rdf:resource");
	if(!save_legislatura.equals("")){
		save_legislatura = StringUtils.substringAfterLast(save_legislatura,"/");
	}
	String save_dipartimento = records.getXMLReader().getNodeValue("/rdf:RDF/ods:ricerca/ods:rif_unitaOrganizzativa/@rdf:resource");
	if(!save_dipartimento.equals("")){
		save_dipartimento = StringUtils.substringAfterLast(save_dipartimento,"/");
	}
	String search_code = records.getXMLReader().getNodeValue("/rdf:RDF/ods:ricerca/dc:identifier/text()");
	String search_audience = records.getXMLReader().getNodeValue("/rdf:RDF/ods:ricerca/dcterms:audience/text()");
	%>
	{
    "success": true,
    "data": {"save_title":"<%=JsSolver.escapeSingleDoubleApex(save_title)%>","save_date":"<%=JsSolver.escapeSingleDoubleApex(save_date)%>","save_description":"<%=JsSolver.escapeSingleDoubleApex(save_description)%>","save_legislatura":"<%=JsSolver.escapeSingleDoubleApex(save_legislatura)%>","save_dipartimento":"<%=JsSolver.escapeSingleDoubleApex(save_dipartimento)%>","search_code":"<%=JsSolver.escapeSingleDoubleApex(search_code)%>","search_audience":"<%=JsSolver.escapeSingleDoubleApex(search_audience)%>"}
    }
<%
records.closeXMLReader();
}else{%>
    {
    "success": false
    }
<%}%>