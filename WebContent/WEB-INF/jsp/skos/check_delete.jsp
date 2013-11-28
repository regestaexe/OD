<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="com.openDams.bean.Records"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%
Records records = (Records)request.getAttribute("records");
TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
HashMap<String, String[]> parsedTitle = titleManager.parseTitle(records.getTitle(),records.getArchives().getIdArchive());
String[] labels = parsedTitle.get("label");
String label ="";
	for(int s=0;s<labels.length;s++){
		label+=labels[s];
	}
List<Records> relationRecords = (List<Records>)request.getAttribute("relationRecords");
%>
<script>
<%
if(records.getTotChildren()==0){%>
	Ext.getCmp('buttonDelete_postCheck').enable();
<%}%>
</script>
<div style="padding: 5px;background-color: #ffffff;width: 100%;height: 100%;">
<%if(records.getTotChildren()>0){%>
<div style="color:red;font-weight:bold;">Attenzione:il record "<%=parsedTitle.get("notation")[0]%> <%=label%>" non può essere cancellato in quanto padre di altre voci</div>
<%}else if(relationRecords!=null && relationRecords.size()>0){%>
<div style="color:red;font-weight:bold;">Attenzione:il record "<%=parsedTitle.get("notation")[0]%> <%=label%>" è collegato ai seguenti documenti:</div>
<%
	for(int i=0;i<relationRecords.size();i++){
	   Records records2 = relationRecords.get(i);
	   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
	   String[] labels2 = parsedTitle2.get("label");
	   String label2 ="";
	   if(labels2!=null){
			for(int s=0;s<labels2.length;s++){
				label2+=labels2[s];
			}
			if(parsedTitle2.get("notation")!=null){
				label2=parsedTitle2.get("notation")[0]+" "+label2;
		 	}
	   }else{
		    String[] titleArray = parsedTitle2.get("notation");
			StringBuffer titleBuffer = new StringBuffer();
			if (titleArray != null) {
				for (String singleTitle : titleArray) {
					try {
						titleBuffer.append(singleTitle.replaceAll("ç", " "));
					} catch (Exception e) {
					}
				}
			}
			titleArray = parsedTitle2.get("title");
			if (titleArray != null) {
				for (String singleTitle : titleArray) {
					try {
						titleBuffer.append(singleTitle.replaceAll("ç", " "));
					} catch (Exception e) {
					}
				}
			}
			titleArray = parsedTitle2.get("sottotitolo");
			if (titleArray != null) {
				for (String singleTitle : titleArray) {
					try {
						titleBuffer.append(singleTitle.replaceAll("ç", " "));
					} catch (Exception e) {
					}
				}
			}
		   
		   label2 = titleBuffer.toString();
	   }
		%>
		<div>[<%=records2.getArchives().getLabel()%>] <%=label2%></div>
		<%
	}
}else{%>
<div style="font-weight:bold;">E' possibile cancellare il record "<%=parsedTitle.get("notation")[0]%> <%=label%>" in quanto non collegato con altre voci o documenti.</div>	
<%}
%>
<div>