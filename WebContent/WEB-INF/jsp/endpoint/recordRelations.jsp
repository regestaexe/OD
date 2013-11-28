
<%@page import="java.util.ArrayList"%>
<%@page import="com.regesta.framework.xml.XMLReader"%>
<%@page import="com.openDams.title.configuration.OpenDamsTitleManagerProvider"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%if(request.getAttribute("recordRelations")!=null){
	List<Records>  recordRelations = (List<Records>)request.getAttribute("recordRelations");
%>
{"totalCount":"<%=recordRelations.size()%>","records":[
<%	
	for (int i = 0; i < recordRelations.size(); i++) {
		Records records = recordRelations.get(i);
		TitleManager titleManager = OpenDamsTitleManagerProvider.getTitleManager();
		HashMap<String, String[]> parsedTitle = titleManager.parseTitle(records.getTitle(),records.getArchives().getIdArchive());
		String title = titleManager.getFieldValues(parsedTitle.get("notation"))+" "+ titleManager.getFieldValues(parsedTitle.get("label"));
		XMLReader xmlReader = records.getXMLReader();		
		ArrayList<String>  publishedOnList = xmlReader.getNodesValues("/rdf:RDF/*/ods:publishedOn/@rdf:resource");
		/*for (int x = 0; x < publishedOnList.size(); x++) {
			System.out.println(publishedOnList.get(i));
		}*/
		%>
		{"id":"<%=records.getIdRecord()%>","archive":"<%=records.getArchives().getLabel()%>","title":"<%=title%>","publish":false}
		<%
		
		if(i<recordRelations.size()-1){%>,<%}
	}
%>
]}
<%
}else{%>
{"totalCount":"0","records":[]}	
<%}%>