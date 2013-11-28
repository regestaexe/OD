<%@page import="java.util.Set"%><%@page import="com.openDams.bean.Relations"%><%@page import="com.openDams.bean.Records"%><%@page import="com.openDams.title.configuration.TitleManager"%><%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%><%@page import="java.util.Map"%><%@page import="com.openDams.bean.Archives"%>
<%
Map<Integer, Integer> thArchiveRelations = null;
if(request.getAttribute("archiveList")!=null && request.getAttribute("records") != null){
	   thArchiveRelations = (Map<Integer, Integer>)request.getAttribute("thArchiveRelations");
	   List<Object> archives = (List)request.getAttribute("archiveList");
	   Object[] archiveList = archives.toArray();
	   for(int i=0;i<archiveList.length;i++){
		   Archives archive = (Archives)archiveList[i];
		 //  System.out.println("Archivio "+archive.getIdArchive()+" relazione "+thArchiveRelations.get(archive.getIdArchive()));
	   %>
		   
			<div class="scheda">
				<div id="record_relations_panel_<%=archive.getIdArchive()%>" class="scheda sezione" style="float:left;width:96%;">
				<div class="head">relazioni con <%=archive.getLabel()%> <%if(archive.getIdArchive()==121){%>  <a href="#no" onclick="createAddNewCocept('<%=archive.getIdArchive()%>');"><img src="img/skos_tab/create_add.png" alt="crea un nuovo concetto" title="crea un nuovo concetto"/></a><%}%></div><%
						Records records = (Records) request.getAttribute("records"); 
						Set<Relations> relations = records.getRelationsesForRefIdRecord1();
						if (relations.size() > 0) {
							TitleManager manager = (TitleManager) request.getAttribute("titleManager");
							for (Relations relazione : relations) {
								if(relazione.getRelationTypes().getIdRelationType().intValue() == thArchiveRelations.get(archive.getIdArchive()).intValue()){
									HashMap<String, String[]> map = manager.parseTitle(relazione.getRecordsByRefIdRecord2().getTitle(), archive.getIdArchive());
									Records records2 = relazione.getRecordsByRefIdRecord2();
									String[] labels = map.get("label");
								 	String label ="";
								 	for(int s=0;s<labels.length;s++){
								 		label+=labels[s];
								 	}%>
									<div style="padding:2px;float:left;width:100%;border-bottom: 1px solid #ededed;" id="div_relations_<%=relazione.getRecordsByRefIdRecord2().getIdRecord()%>">
										<div style="width:100%;float:left;">
											<div style="float:left;width:15px;"><a href="#n" onclick="return cancelRelation('<%=relazione.getRecordsByRefIdRecord2().getIdRecord()%>','<%=thArchiveRelations.get(archive.getIdArchive())%>','div_relations_<%=relazione.getRecordsByRefIdRecord2().getIdRecord()%>')"><img src="img/skos_tab/delete.png" class="delete_button input_label_buttons_img" title="elimina relazione"></a></div>
											<div style="float:left;margin-left:10px;"><%=map.get("notation")[0]%> <%=label%></div>
											<div style="float:left;width:15px;margin-left:10px;"><a href="#n" onclick="openInfoRelation('<%=records2.getIdRecord()%>','info_relation_<%=records2.getIdRecord()%>')"><img src="img/info.png" border="0"/ title="visualizza informazioni"></a></div>
											<div id="info_relation_<%=records2.getIdRecord()%>" style="display:none;float:left;width:95%;border:1px solid #ededed;padding:2px;margin-left:25px;margin-right:10px;background-color:#ffffff;font-size:10px;"></div>
									    </div>
									</div>
								<%}
							}
						}%>
				</div>
			</div>
	   <%}
}%>