<%@page contentType="application/json; charset=UTF-8"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="org.apache.lucene.document.Document"%>
<%@page import="java.util.HashMap"%>
<%if(request.getAttribute("doc_to")!=null){
	TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
	Document doc = (Document)request.getAttribute("doc_to");
	int idArchive = new Integer(request.getParameter("idArchive"));
	System.out.println(doc.get("id_record"));
	System.out.println(doc.get("title_record"));
	HashMap<String, String[]> parsedTitle = titleManager.parseTitle(doc.get("title_record"),idArchive);
	String[] labels = parsedTitle.get("label");
	String label ="";
 	for(int s=0;s<labels.length;s++){
 		label+=labels[s];
 	}
	%>
{
"success": true,
"id_record": "<%=doc.get("id_record")%>",
"title": "<%=JsSolver.escapeDoubleApex(parsedTitle.get("notation")[0]+" "+label).replaceAll("\r","").replaceAll("\n","")%>",
"id_archive": "<%=idArchive%>"
}
<%}else{%>
{"success":false};
<%}%>