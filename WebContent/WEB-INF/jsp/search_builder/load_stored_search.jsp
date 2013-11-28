<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="com.openDams.bean.Records"%>
<%@page import="com.regesta.framework.xml.XMLReader"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.openDams.search_builder.utility.StoredSearchContainer"%>

<%@page import="com.regesta.framework.util.JsSolver"%>
<script>
<%if(request.getAttribute("storedSearchContainers")!=null){
	ArrayList<StoredSearchContainer> storedSearchContainers = (ArrayList<StoredSearchContainer>)request.getAttribute("storedSearchContainers");
	for(int i=0;i<storedSearchContainers.size();i++){
		StoredSearchContainer storedSearchContainer = storedSearchContainers.get(i);
		if(storedSearchContainer.getType() == StoredSearchContainer.TYPE_PANEL){
			System.out.println("'<img width=\"12\" height=\"12\" src=\"img/archive_img/archive_"+storedSearchContainer.getIdArchive()+".gif\" border=\"0\"/>&nbsp;"+JsSolver.escapeSingleApex(storedSearchContainer.getPanelTitle()).replaceAll("\r","").replaceAll("\n","")+"'");
		%>
		var panel_search_record_<%=storedSearchContainer.getIdRecord()%> =  new Ext.Panel({
			id : 'panel_search_record_<%=storedSearchContainer.getIdRecord()%>',																				
			title : '<img width="12" height="12" src="img/archive_img/archive_<%=storedSearchContainer.getIdArchive()%>.gif" border="0"/>&nbsp;<%=JsSolver.escapeSingleApex(storedSearchContainer.getPanelTitle()).replaceAll("\r","").replaceAll("\n","")%>',
			frame: true,
			collapsible: true,
			closable:false,
			html:'<div id="scheda_stored_search_<%=storedSearchContainer.getIdRecord()%>"></div>',																																												
			tools:[],
			padding: 5,
			frame:true,
			panel_type:'archive_panel',
			searchIdRecord:'<%=request.getParameter("idRecord")%>',
			idRecord:'<%=storedSearchContainer.getIdRecord()%>',
			idArchive:'<%=storedSearchContainer.getIdArchive()%>',
		    listeners: { 
		      			  'afterrender': function(elemento){
		      				loadDocumentForSearchResults('<%=storedSearchContainer.getIdRecord()%>','<%=storedSearchContainer.getIdArchive()%>','scheda_stored_search_<%=storedSearchContainer.getIdRecord()%>');																															
		      			   } 
		      			}
		});
		Ext.getCmp('stored_search_container').add(panel_search_record_<%=storedSearchContainer.getIdRecord()%>);	
		<%}else{%>
		var panel_search_record_<%=i%> =  new Ext.Panel({
			id : 'panel_search_record_<%=i%>',																				
			title : '<%=JsSolver.escapeSingleApex(storedSearchContainer.getPanelTitle())%>',
			frame: true,
			collapsible: true,
			closable:false,
			<%if(storedSearchContainer.getType() == StoredSearchContainer.TYPE_TITLE){%>
			panel_type:'title_panel',
			<%}else if(storedSearchContainer.getType() == StoredSearchContainer.TYPE_TEXT){%>
			panel_type:'text_panel',
			<%}else{%>
			panel_type:'index_panel',	
			<%}%>
			searchIdRecord:'<%=request.getParameter("idRecord")%>',
			padding: 5,
			frame:true,
			panel_text:'<%=JsSolver.escapeSingleApex(storedSearchContainer.getText()).replaceAll("\r","").replaceAll("\n","")%>',
			html:'<%=JsSolver.escapeSingleApex(storedSearchContainer.getText()).replaceAll("\r","").replaceAll("\n","")%>'
		});
		Ext.getCmp('stored_search_container').add(panel_search_record_<%=i%>);		
		<%}
	%>
						
	<%}%>
	Ext.getCmp('stored_search_container').doLayout();
<%}%>
</script>