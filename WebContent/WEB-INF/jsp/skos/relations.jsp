<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.Set"%>
<%@page import="com.openDams.security.ArchiveRoleTester"%>
<%@page import="com.openDams.security.UserDetails"%>
<%@page import="org.springframework.security.core.context.SecurityContext"%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@include file="../locale.jsp"%>
<%@page import="com.openDams.bean.Relations"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%
Records records = (Records)request.getAttribute("records");
String concept = "Concept" ;
if(records.getXMLReader().getNodeCount("/rdf:RDF/skos:ConceptScheme")>0){
	concept = "ConceptScheme";
}
int idArchive=records.getArchives().getIdArchive();
records.closeXMLReader();
UserDetails user =  (UserDetails)((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
boolean editOn = ArchiveRoleTester.testEditing(user.getId(),idArchive);
TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
List<Records> list = (List<Records>)request.getAttribute("recordsList");
int tot_children = 0;
int tot_related = 0;
int current_page = 1;
if(request.getParameter("current_page")!=null){
	current_page = new Integer(request.getParameter("current_page"));
}
tot_children = records.getTotChildren();
/*
Set countRelationses = records.getCountRelationses();
if(countRelationses!=null){
	for(int x=0;x<countRelationses.size();x++){
		CountRelations countRelations = (CountRelations)countRelationses.toArray()[x];
		if(countRelations.getId().getRefIdRelationType()==1)
			tot_children = countRelations.getTot();
		
		else if(countRelations.getId().getRefIdRelationType()==3)
			tot_related = countRelations.getTot();
	}
}*/	
int page_size = (Integer)request.getAttribute("page_size");
int page_tot = 1;
if(tot_children > page_size){
	if(tot_children % page_size==0 )
		page_tot=tot_children/page_size;
	else
		page_tot=(tot_children/page_size)+1;
}
%>
<%if(request.getParameter("idRelationType").equals("1")){%>

<div class="relation_column_left" >
	<%if(concept.equals("Concept")){%>				
	<span class="relation_column_label">${skostabtablabelbroader}</span> <span  class="n_relations n_BT">(<%=list.size()%>)</span><%if(editOn){%><img src="img/skos_tab/plus.png" class="add_button_relation input_label_buttons_img" id="relation_1" title="${skostabtabbuttonadd} ${skostabtablabelrelation} BT"><%}%>
	<%}else{%>
	<span class="relation_column_label">${skostabtablabeltopconceptof}</span> <span  class="n_relations n_BT">(<%=list.size()%>)</span><%if(editOn){%><img src="img/skos_tab/plus.png" class="add_button_relation input_label_buttons_img" id="relation_1" title="${skostabtabbuttonadd} ${skostabtablabelrelation} TC"><%}%>
	<%}%>
	<span class="relation_column_label" style="float:right;margin-right:3px;"><%if(editOn){%><a href="#no" onclick="reorderRelations('<%=records.getIdRecord()%>','1');"><img src="img/reorder.gif" class="reorder_relations" id="relation_1" title="ordina relazioni"></a><%}%></span>
</div>
<%for(int i=0;i<list.size();i++){
   Records records2 = list.get(i);
   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
   String[] labels = parsedTitle2.get("label");
   String label ="";
	for(int s=0;s<labels.length;s++){
		label+=labels[s];
	}
	boolean deleted = false;
	String titleLink = "";
	String classLink = "relation_link";
	String onClick = " onclick=\"openRecord('"+records2.getIdRecord()+"');\" ";
 	try{
 		deleted=records2.getDeleted();
 		if(deleted){
 			classLink = "relation_link_pending_delete";
 			onClick = "";
 			titleLink = " title=\"voce nel cestino\" ";
 		}
 	}catch(NullPointerException ex){}
    if(list.size()>1){%>
		<%if(concept.equals("Concept")){%>
			<div class="related_concept record_father" id="<%=records2.getIdRecord()%>"><%if(editOn){%><img src="img/skos_tab/delete.png" class="delete_button_relation input_label_buttons_img" title="${skostabtablabeldelete} ${skostabtablabelrelation}" relation_type="1" id_record_relation="<%=records2.getIdRecord()%>"><%}%><span class="relation_span"><a class="<%=classLink%>" href="#no" <%=onClick%><%=titleLink%>><%=parsedTitle2.get("notation")[0]%> <%=label%></a></span></div>
		<%}else{%>
			<div class="related_concept record_father" id="<%=records2.getIdRecord()%>"><%if(editOn){%><img src="img/skos_tab/delete.png" class="delete_button_relation input_label_buttons_img" title="${skostabtablabeldelete} ${skostabtablabelrelation}" relation_type="1" id_record_relation="<%=records2.getIdRecord()%>"><%}%><span class="relation_span"><a class="<%=classLink%>" href="#no" <%=onClick%><%=titleLink%>><%=parsedTitle2.get("notation")[0]%> <%=label%></a></span></div>
		<%}%>				
	<%}else{%>
		<div class="related_concept record_father" id="<%=records2.getIdRecord()%>"><%if(editOn){%><img src="img/skos_tab/delete_off.png" class="delete_button input_label_buttons_img" title="${skostabtabbuttondisabled}"><%}%><span class="relation_span"><a class="<%=classLink%>" href="#no" <%=onClick%><%=titleLink%>><%=parsedTitle2.get("notation")[0]%> <%=label%></a></span></div>
	<%}
 }%>	
<%}
else if(request.getParameter("idRelationType").equals("2")){%>
<div class="relation_column_center">
	<% if(concept.equals("Concept")){%>
		<span class="relation_column_label">${skostabtablabelnarrower}</span> <span  class="n_relations n_NT">(<%=list.size()%>)</span><%if(editOn){%><img src="img/skos_tab/plus.png" class="add_button_relation input_label_buttons_img" id="relation_2" title="${skostabtabbuttonadd} ${skostabtablabelrelation} NT"><img src="img/skos_tab/create_add.png" id="relation_create_2" class="add_button_relation_create input_label_buttons_img" title="${skostabtablabelcreate} & ${skostabtabbuttonadd} ${skostabtablabelrelation} NT"><%}%>
	<%}else{%>
		<span class="relation_column_label">${skostabtablabelhastopconcept}</span> <span  class="n_relations n_NT">(<%=list.size()%>)</span><%if(editOn){%><img src="img/skos_tab/plus.png" class="add_button_relation input_label_buttons_img" id="relation_2" title="${skostabtabbuttonadd} ${skostabtablabelrelation} HC"><img src="img/skos_tab/create_add.png" id="relation_create_2" class="add_button_relation_create input_label_buttons_img" title="${skostabtablabelcreate} & ${skostabtabbuttonadd} ${skostabtablabelrelation} HC"><%}%>
	<%}%>
	<span class="relation_column_label" style="float:right;margin-right:3px;"><%if(editOn){%><a href="#no" onclick="reorderRelations('<%=records.getIdRecord()%>','2');""><img src="img/reorder.gif" class="reorder_relations" id="relation_2" title="ordina relazioni"></a><%}%></span>
</div>
<%if(tot_children>page_size){%>
<div class="related_concept_pager">
	<div class="related_concept_pager_container">
		 <div class="first" style="float:left;">
	     	 <%if(current_page==1){%>
		       <img src="img/skos_tab/first_off.png" class="first_button" title="${skostabtabbuttondisabled}">
		     <%}else{%>
		       <a class="" title="${arrowPrevios}" onclick="next_prew_children_page_tab('<%=records.getIdRecord()%>','first','<%=page_tot%>',<%=current_page%>,'2')">
		      	<img src="img/skos_tab/first.png" class="first_button" title="${arrowFirst}">
		      </a>
		     <%}%>		      
	      </div>
	      <div class="letf" style="float:left;margin-left:5px;">
	     	 <%if(current_page==1){%>
		       <img src="img/skos_tab/left_off.png" class="left_button" title="${skostabtabbuttondisabled}">
		     <%}else{%>
		       <a class="" title="${arrowPrevios}" onclick="next_prew_children_page_tab('<%=records.getIdRecord()%>','previous','<%=page_tot%>',<%=current_page%>,'2')">
		      	<img src="img/skos_tab/left.png" class="left_button" title="${arrowPrevios}">
		      </a>
		     <%}%>		      
	      </div>
	      <div style="float:left;padding-left:5px;padding-right:5px;">${arrowPage} <%=current_page%> ${arrowOf} <%=page_tot%></div>
	      <div class="right" style="float:left;">
		     <%if(current_page==page_tot){%>
		       <img src="img/skos_tab/right_off.png" class="right_button" title="${skostabtabbuttondisabled}">
		     <%}else{%>
		       <a class="" title="${arrowNext}" onclick="next_prew_children_page_tab('<%=records.getIdRecord()%>','next','<%=page_tot%>',<%=current_page%>,'2')">			  	
			  	<img src="img/skos_tab/right.png" class="right_button" title="${arrowNext}">
			  </a>
		     <%}%>			  
		  </div>
	       <div class="last" style="float:left;margin-left:5px;">
		     <%if(current_page==page_tot){%>
		       <img src="img/skos_tab/last_off.png" class="last_button" title="${skostabtabbuttondisabled}">
		     <%}else{%>
		       <a class="" title="${arrowNext}" onclick="next_prew_children_page_tab('<%=records.getIdRecord()%>','last','<%=page_tot%>',<%=current_page%>,'2')">			  	
			  	<img src="img/skos_tab/last.png" class="last_button" title="${arrowLast}">
			  </a>
		     <%}%>			  
		  </div>		  
	</div>
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
   	int tot_children2 = 0;
   	tot_children2 = records2.getTotChildren();
   	boolean deleted = false;
	String titleLink = "";
	String classLink = "relation_link";
	String onClick = " onclick=\"getChildrenFromTab('"+records2.getIdRecord()+"',"+i+",'"+records.getIdRecord()+"',"+list.size()+");\" ";
	try{
		deleted=records2.getDeleted();
		if(deleted){
			classLink = "relation_link_pending_delete";
			onClick = "";
 			titleLink = " title=\"voce nel cestino\" ";
		}
	}catch(NullPointerException ex){}	 
    if(tot_children2==0){
	%>
		<%if(concept.equals("Concept")){%>
				<div class="related_concept"><%if(editOn){%><img src="img/skos_tab/delete.png" class="delete_button_relation input_label_buttons_img" title="${skostabtablabeldelete} ${skostabtablabelrelation}"  relation_type="2" id_record_relation="<%=records2.getIdRecord()%>"><%}%><span class="relation_span"><a class="<%=classLink%>" href="#no" <%=onClick%><%=titleLink%>><%=parsedTitle2.get("notation")[0]%> <%=label%></a></span></div>
		<%}else{%>
				<div class="related_concept"><%if(editOn){%><img src="img/skos_tab/delete.png" class="delete_button_relation input_label_buttons_img" title="${skostabtablabeldelete} ${skostabtablabelrelation}"  relation_type="2" id_record_relation="<%=records2.getIdRecord()%>"><%}%><span class="relation_span"><a class="<%=classLink%>" href="#no" <%=onClick%><%=titleLink%>><%=parsedTitle2.get("notation")[0]%> <%=label%></a></span></div>
		<%}%>	
    <%}else{%>
    	<div class="related_concept"><%if(editOn){%><img src="img/skos_tab/delete_off.png" class="delete_button_relation input_label_buttons_img" title="${skostabtabbuttondisabled}"><%}%><span class="relation_span"><a class="<%=classLink%>" href="#no" <%=onClick%><%=titleLink%>><%=parsedTitle2.get("notation")[0]%> <%=label%></a></span></div>
    <%}%>
<%}%>	
<%}
else if(request.getParameter("idRelationType").equals("3")){%>
<div class="relation_column_center">
	<span class="relation_column_label">${skostabtablabelrelated}</span> <span  class="n_relations n_RT">(<%=list.size()%>)</span><%if(editOn){%><img src="img/skos_tab/plus.png" class="add_button_relation input_label_buttons_img" id="relation_3" title="${skostabtabbuttonadd} ${skostabtablabelrelation} RT"><%}%>
	<span class="relation_column_label" style="float:right;margin-right:3px;"><%if(editOn){%><a href="#no" onclick="reorderRelations('<%=records.getIdRecord()%>','3');""><img src="img/reorder.gif" class="reorder_relations" id="relation_3" title="ordina relazioni"></a><%}%></span>
</div>
<%for(int i=0;i<list.size();i++){
   Records records2 = list.get(i);
   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
   String note="";
   String[] labels = parsedTitle2.get("label");
   String label ="";
	for(int s=0;s<labels.length;s++){
		label+=labels[s];
	}
	boolean deleted = false;
	String titleLink = "";
	String classLink = "relation_link";
	String onClick = " onclick=\"openRecord('"+records2.getIdRecord()+"');\" ";
 	try{
 		deleted=records2.getDeleted();
 		if(deleted){
 			classLink = "relation_link_pending_delete";
 			onClick = "";
 			titleLink = " title=\"voce nel cestino\" ";
 		}
 	}catch(NullPointerException ex){}
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
	<div class="related_concept">
	<%if(editOn){%>
	<img src="img/skos_tab/delete.png" class="delete_button_relation input_label_buttons_img" title="${skostabtablabeldelete} ${skostabtablabelrelation}"  relation_type="3"  id_record_relation="<%=records2.getIdRecord()%>"><%}%><span class="relation_span"><a class="<%=classLink%>" href="#no" <%=onClick%><%=titleLink%>><%=parsedTitle2.get("notation")[0]%> <%=label%></a></span>
	<div style="border:1px solid #99bbe8;margin:5px;padding:2px;"><%=note%>
		<div style="margin-top:3px;border-top:1px dashed #99bbe8;padding-top:2px;">
		<%if(editOn){%>
		<a href="#no" onclick="editRelationNote('<%=records.getIdRecord()%>','<%=records2.getIdRecord()%>','3','<%=JsSolver.escapeSingleApex(note)%>');"><img src="img/page_white_edit.png" title="scrivi nota"/></a>
		<%if(!note.equals("")){%>
			<a href="#no" onclick="deleteRelationNote('<%=records.getIdRecord()%>','<%=records2.getIdRecord()%>','3');"><img src="img/skos_tab/delete.png" title="cancella nota"></a>
		<%}%>
		<%}%>
		</div>
	</div>
	</div>
<%}%>
<%}
else if(request.getParameter("idRelationType").equals("9")){%>
<div class="relation_column_right">
	<span class="relation_column_label">${skostabtablabelinscheme}</span> <span  class="n_relations n_IS">(<%=list.size()%>)</span><%if(editOn){%><img src="img/skos_tab/plus.png" class="add_button_relation input_label_buttons_img" id="relation_9" title="${skostabtabbuttonadd} ${skostabtablabelrelation} IS"><%}%>
	<span class="relation_column_label" style="float:right;margin-right:3px;"><%if(editOn){%><a href="#no" onclick="reorderRelations('<%=records.getIdRecord()%>','9');""><img src="img/reorder.gif" class="reorder_relations" id="relation_9" title="ordina relazioni"></a><%}%></span>
</div>
<%for(int i=0;i<list.size();i++){
   Records records2 = list.get(i);
   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
   String[] labels = parsedTitle2.get("label");
   String label ="";
	for(int s=0;s<labels.length;s++){
		label+=labels[s];
	}
	boolean deleted = false;
	String titleLink = "";
	String classLink = "relation_link";
	String onClick = " onclick=\"openRecord('"+records2.getIdRecord()+"');\" ";
 	try{
 		deleted=records2.getDeleted();
 		if(deleted){
 			classLink = "relation_link_pending_delete";
 			onClick = "";
 			titleLink = " title=\"voce nel cestino\" ";
 		}
 	}catch(NullPointerException ex){}
%>
	<div class="related_concept"><%if(editOn){%><img src="img/skos_tab/delete.png" class="delete_button_relation input_label_buttons_img" title="${skostabtablabeldelete} ${skostabtablabelrelation}"  relation_type="9"  id_record_relation="<%=records2.getIdRecord()%>"><%}%><span class="relation_span"><a class="<%=classLink%>" href="#no" <%=onClick%><%=titleLink%>><%=parsedTitle2.get("notation")[0]%> <%=label%></a></span></div>
<%}%>
<%}
else if(request.getParameter("idRelationType").equals("5")){%>
<div class="relation_column_left">
	<span class="relation_column_label">${skostabtablabelbroadmatch}</span> <span  class="n_relations n_BM">(<%=list.size()%>)</span><%if(editOn){%><img src="img/skos_tab/plus.png" class="add_button_external_relation input_label_buttons_img" id="relation_5" title="${skostabtabbuttonadd} ${skostabtablabelrelation} BM"><%}%>
	<span class="relation_column_label" style="float:right;margin-right:3px;"><%if(editOn){%><a href="#no" onclick="reorderRelations('<%=records.getIdRecord()%>','5');""><img src="img/reorder.gif" class="reorder_relations" id="relation_5" title="ordina relazioni"></a><%}%></span>
</div>
<%for(int i=0;i<list.size();i++){
   Records records2 = list.get(i);
   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
   String[] labels = parsedTitle2.get("label");
   String label ="";
	for(int s=0;s<labels.length;s++){
		label+=labels[s];
	}
	boolean deleted = false;
	String titleLink = "";
	String classLink = "relation_link";
	String onClick = " onclick=\"parent.addTab('"+records2.getArchives().getLabel()+"','"+records2.getArchives().getIdArchive()+"','skosWorkspace.html?idArchive="+records2.getArchives().getIdArchive()+"&callIdFromExternal="+records2.getIdRecord()+"');\" ";
 	try{
 		deleted=records2.getDeleted();
 		if(deleted){
 			classLink = "relation_link_pending_delete";
 			onClick = "";
 			titleLink = " title=\"voce nel cestino\" ";
 		}
 	}catch(NullPointerException ex){}
%>
<div class="related_concept"><%if(editOn){%><img src="img/skos_tab/delete.png" class="delete_button_relation input_label_buttons_img" title="${skostabtablabeldelete} ${skostabtablabelrelation}"  relation_type="5"  id_record_relation="<%=records2.getIdRecord()%>"><%}%><span class="relation_span"><a class="<%=classLink%>" href="#no" <%=onClick%><%=titleLink%>><%=parsedTitle2.get("notation")[0]%> <%=label%></a></span></div>
<%}%>
<%}
else if(request.getParameter("idRelationType").equals("6")){%>
<div class="relation_column_center">
	<span class="relation_column_label">${skostabtablabelnarrowmatch}</span> <span  class="n_relations n_NM">(<%=list.size()%>)</span><%if(editOn){%><img src="img/skos_tab/plus.png" class="add_button_external_relation input_label_buttons_img" id="relation_6" title="${skostabtabbuttonadd} ${skostabtablabelrelation} NM"><%}%>
	<span class="relation_column_label" style="float:right;margin-right:3px;"><%if(editOn){%><a href="#no" onclick="reorderRelations('<%=records.getIdRecord()%>','6');""><img src="img/reorder.gif" class="reorder_relations" id="relation_6" title="ordina relazioni"></a><%}%></span>
</div>
<%for(int i=0;i<list.size();i++){
   Records records2 = list.get(i);
   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
   String[] labels = parsedTitle2.get("label");
   String label ="";
	for(int s=0;s<labels.length;s++){
		label+=labels[s];
	}
	boolean deleted = false;
	String titleLink = "";
	String classLink = "relation_link";
	String onClick = " onclick=\"parent.addTab('"+records2.getArchives().getLabel()+"','"+records2.getArchives().getIdArchive()+"','skosWorkspace.html?idArchive="+records2.getArchives().getIdArchive()+"&callIdFromExternal="+records2.getIdRecord()+"');\" ";
 	try{
 		deleted=records2.getDeleted();
 		if(deleted){
 			classLink = "relation_link_pending_delete";
 			onClick = "";
 			titleLink = " title=\"voce nel cestino\" ";
 		}
 	}catch(NullPointerException ex){}
%>
	<div class="related_concept"><%if(editOn){%><img src="img/skos_tab/delete.png" class="delete_button_relation input_label_buttons_img" title="${skostabtablabeldelete} ${skostabtablabelrelation}"  relation_type="6" id_record_relation="<%=records2.getIdRecord()%>"><%}%><span class="relation_span"><a class="<%=classLink%>" href="#no" <%=onClick%><%=titleLink%>><%=parsedTitle2.get("notation")[0]%> <%=label%></a></span></div>
<%}%>
<%}
else if(request.getParameter("idRelationType").equals("7")){%>
<div class="relation_column_center">
	<span class="relation_column_label">${skostabtablabelrelatedmatch}</span> <span  class="n_relations n_RM">(<%=list.size()%>)</span><%if(editOn){%><img src="img/skos_tab/plus.png" class="add_button_external_relation input_label_buttons_img" id="relation_7" title="${skostabtabbuttonadd} ${skostabtablabelrelation} RM"><%}%>
	<span class="relation_column_label" style="float:right;margin-right:3px;"><%if(editOn){%><a href="#no" onclick="reorderRelations('<%=records.getIdRecord()%>','7');""><img src="img/reorder.gif" class="reorder_relations" id="relation_7" title="ordina relazioni"></a><%}%></span>
</div>
<%for(int i=0;i<list.size();i++){
   Records records2 = list.get(i);
   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
   String[] labels = parsedTitle2.get("label");
   String label ="";
	for(int s=0;s<labels.length;s++){
		label+=labels[s];
	}
	boolean deleted = false;
	String titleLink = "";
	String classLink = "relation_link";
	String onClick = " onclick=\"parent.addTab('"+records2.getArchives().getLabel()+"','"+records2.getArchives().getIdArchive()+"','skosWorkspace.html?idArchive="+records2.getArchives().getIdArchive()+"&callIdFromExternal="+records2.getIdRecord()+"');\" ";
 	try{
 		deleted=records2.getDeleted();
 		if(deleted){
 			classLink = "relation_link_pending_delete";
 			onClick = "";
 			titleLink = " title=\"voce nel cestino\" ";
 		}
 	}catch(NullPointerException ex){}
%>
	<div class="related_concept"><%if(editOn){%><img src="img/skos_tab/delete.png" class="delete_button_relation input_label_buttons_img" title="${skostabtablabeldelete} ${skostabtablabelrelation}"  relation_type="7" id_record_relation="<%=records2.getIdRecord()%>"><%}%><span class="relation_span"><a class="<%=classLink%>" href="#no" <%=onClick%><%=titleLink%>><%=parsedTitle2.get("notation")[0]%> <%=label%></a></span></div>
<%}%>
<%}
else if(request.getParameter("idRelationType").equals("8")){%>
<div class="relation_column_right">
	<span class="relation_column_label">${skostabtablabelclosematch}</span> <span  class="n_relations n_CM">(<%=list.size()%>)</span><%if(editOn){%><img src="img/skos_tab/plus.png" class="add_button_external_relation input_label_buttons_img" id="relation_8" title="${skostabtabbuttonadd} ${skostabtablabelrelation} CM"><%}%>
	<span class="relation_column_label" style="float:right;margin-right:3px;"><%if(editOn){%><a href="#no" onclick="reorderRelations('<%=records.getIdRecord()%>','8');""><img src="img/reorder.gif" class="reorder_relations" id="relation_8" title="ordina relazioni"></a><%}%></span>
</div>
<%for(int i=0;i<list.size();i++){
   Records records2 = list.get(i);
   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
   String[] labels = parsedTitle2.get("label");
   String label ="";
	for(int s=0;s<labels.length;s++){
		label+=labels[s];
	}
	boolean deleted = false;
	String titleLink = "";
	String classLink = "relation_link";
	String onClick = " onclick=\"parent.addTab('"+records2.getArchives().getLabel()+"','"+records2.getArchives().getIdArchive()+"','skosWorkspace.html?idArchive="+records2.getArchives().getIdArchive()+"&callIdFromExternal="+records2.getIdRecord()+"');\" ";
 	try{
 		deleted=records2.getDeleted();
 		if(deleted){
 			classLink = "relation_link_pending_delete";
 			onClick = "";
 			titleLink = " title=\"voce nel cestino\" ";
 		}
 	}catch(NullPointerException ex){}
%>
	<div class="related_concept"><%if(editOn){%><img src="img/skos_tab/delete.png" class="delete_button_relation input_label_buttons_img" title="${skostabtablabeldelete} ${skostabtablabelrelation}" relation_type="8" id_record_relation="<%=records2.getIdRecord()%>"><%}%><span class="relation_span"><a class="<%=classLink%>" href="#no" <%=onClick%><%=titleLink%>><%=parsedTitle2.get("notation")[0]%> <%=label%></a></span></div>
<%}%>
<%}%>

