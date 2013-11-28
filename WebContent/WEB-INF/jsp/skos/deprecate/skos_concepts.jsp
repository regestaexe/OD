<%@page import="com.regesta.framework.util.JsSolver"%><%@page import="org.apache.commons.lang.StringUtils"%><%@page contentType="text/html; charset=UTF-8"%><%@page pageEncoding="UTF-8"%><%@page import="com.openDams.bean.Records"%><%@page import="com.openDams.title.configuration.TitleManager"%><%@page import="java.util.HashMap"%><%@page import="java.util.List"%><%@page import="com.openDams.bean.Relations"%><%@page import="java.util.Set"%><%@include file="../../locale.jsp"%><%
if(request.getAttribute("recordsList")!=null){
	TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
	List<Records> recordsList = (List<Records>)request.getAttribute("recordsList");
	for(int i=0;i<recordsList.size();i++){
		Records records = recordsList.get(i);
		HashMap<String, String[]> parsedTitle = titleManager.parseTitle(records.getTitle(),records.getArchives().getIdArchive());	
		int tot_children = records.getTotChildren();
		String[] labels = parsedTitle.get("label");
	 	String label ="";
	 	for(int s=0;s<labels.length;s++){
	 		label+=labels[s];
	 	}
		%>
		<div class="record_row" style="background-color:#FFFFFF;">
			<div class="record_row">
			    <a href="#no" class="open_close_deprecate <%if(tot_children>0){%>closed<%}else{%>leaf<%}%>" idRecord="<%=records.getIdRecord()%>"></a>
			    <input type="radio" class="sel_radio" name="sel_radio" value="<%=records.getIdRecord()%>" disabled="disabled" title="record destinazione"/>
				<input type="checkbox" class="sel_check" value="<%=records.getIdRecord()%>" title="record da selezionare"/>
					 <%
					    boolean deprecated = false;
					 	try{
					 		if(!parsedTitle.get("deprecated")[0].trim().equals("")){
					 			deprecated=true;
					 		}
					 	}catch(NullPointerException ex){}
					 %>
				<a href="#no" idRecord="<%=records.getIdRecord()%>"  <%if(deprecated){%>style="color:#808080;" title="voce deprecata"<%}%> class="tree_title clicked_<%=records.getIdRecord()%>" onclick="return deprecate('<%=parsedTitle.get("id")[0]%>','<%=JsSolver.escapeSingleApex(parsedTitle.get("notation")[0]+" "+label)%>');"><%=parsedTitle.get("notation")[0]%> <%=label%><img src="img/skos_tab/add_relation_concept.gif"  title="${skosAddrelationButtonLabel}"></a>
			</div>
			<%if(tot_children>0){
				int page_size = (Integer)request.getAttribute("page_size");
			    int page_tot = 1;
			    if(tot_children > page_size){
			    	if(tot_children % page_size==0 )
			    		page_tot=tot_children/page_size;
					else
						page_tot=(tot_children/page_size)+1;
			    }
			%>
				<div class="record_children record_children_deprecate_<%=records.getIdRecord()%>">
				<%if(page_tot>1){ %>
				<div class="record_buttons"><div class="arrow_buttons"><a class="first_page_record" title="${arrowFirst}" onclick="next_prew_children_page_deprecate('<%=records.getIdRecord()%>','first','<%=page_tot%>')"></a><a class="prev_page_record" title="${arrowPrevios}" onclick="next_prew_children_page_deprecate('<%=records.getIdRecord()%>','previous','<%=page_tot%>')"></a><span class="label_buttons">${arrowPage}<span class="current_page">1</span>${arrowOf} <%=page_tot%></span><a class="next_page_record" title="${arrowNext}" onclick="next_prew_children_page_deprecate('<%=records.getIdRecord()%>','next','<%=page_tot%>')"><a/><a class="last_page_record" title="${arrowLast}" onclick="next_prew_children_page_deprecate('<%=records.getIdRecord()%>','last','<%=page_tot%>')"><a/></div></div>
				<%}%>
				<div class="record_list_deprecate"></div>
				</div>
			<%}%>
			
		</div>
		<%
	}
}%>
