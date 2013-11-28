<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.lucene.document.Document"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%@include file="../locale.jsp"%>
<%@page import="java.math.BigInteger"%>
<%
if(request.getAttribute("unlinkedList")!=null){
	List<Records> list = (List<Records>)request.getAttribute("unlinkedList");
	TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
	int current_page = 1;
	if(request.getParameter("current_page")!=null){
		current_page = new Integer(request.getParameter("current_page"));
	}
	int page_size = (Integer)request.getAttribute("page_size");
	int page_tot = 1;
	int tot_records = ((BigInteger)request.getAttribute("unlinked")).intValue();
	if(tot_records > page_size){
		if(tot_records % page_size==0 )
			page_tot=tot_records/page_size;
		else
			page_tot=(tot_records/page_size)+1;
	}
%>
<script>
function next_prew_results_unlinked(action,tot_page,current_page){
	tot  = parseInt(tot_page,10);
	if(action=='next'){
         if(current_page+1>tot){			
         }else{        	
        	 current_page = current_page+1;
         }
	}else if(action=='last'){
		current_page = tot_page;
	}else if(action=='first'){
		current_page = 1;
	}else{
		 if(current_page-1<=0){			
         }else{
        	 current_page = current_page-1;
         }
	}
	openLoading();
	$("#unlinked_tab").data("url","ajax/unlinked.html?idArchive=<%=request.getParameter("idArchive")%>&current_page="+current_page);
	$(".unlinked_tab").load("ajax/unlinked.html?idArchive=<%=request.getParameter("idArchive")%>&current_page="+current_page,function(){		
		closeLoading();
	});
	
}
function openRecordRelations(id_record){
	Ext.getCmp('relations_tab_tab').show();	
	openRecord(id_record);
}
</script>
<div style="width: 99%;padding:2px;">
	<div class="area">
			<div class="area_tab">
			<div class="area_tab_label">${skostabtablabelunlinkedelements}<div class="x-tool x-tool-toggle x-tool-collapse-north macro_collapse" id="search_results">&nbsp;</div></div></div>
			<div class="area_tab_bottom_line"></div>
			<div  class="search_results" id="adv_search_results">
			<div style="width:100%;">
						<div class="search_results_pager">
						<%if(tot_records>page_size){%>
							<div class="search_results_pager_container">
								 <div class="first" style="float:left;margin-left:5px;">
							     	 <%if(current_page==1){%>
								       <img src="img/skos_tab/first_off.png" class="first_button" title="${skostabtabbuttondisabled}">
								     <%}else{%>
								       <a class="" title="${arrowPrevios}" onclick="next_prew_results_unlinked('first','<%=page_tot%>',<%=current_page%>);">
								      	<img src="img/skos_tab/first.png" class="first_button" title="${arrowFirst}">
								      </a>
								     <%}%>		      
							      </div>
							      <div class="letf" style="float:left;margin-left:5px;">
							     	 <%if(current_page==1){%>
								       <img src="img/skos_tab/left_off.png" class="left_button" title="${skostabtabbuttondisabled}">
								     <%}else{%>
								       <a class="" title="${arrowPrevios}" onclick="next_prew_results_unlinked('previous','<%=page_tot%>',<%=current_page%>);">
								      	<img src="img/skos_tab/left.png" class="left_button" title="${arrowPrevios}">
								      </a>
								     <%}%>		      
							      </div>
							      <div style="float:left;padding-left:5px;padding-right:5px;">${arrowPage} <%=current_page%> ${arrowOf} <%=page_tot%></div>
							      <div class="right" style="float:left;">
								     <%if(current_page==page_tot){%>
								       <img src="img/skos_tab/right_off.png" class="right_button" title="${skostabtabbuttondisabled}">
								     <%}else{%>
								       <a class="" title="${arrowNext}" onclick="next_prew_results_unlinked('next','<%=page_tot%>',<%=current_page%>);">			  	
									  	<img src="img/skos_tab/right.png" class="right_button" title="${arrowNext}">
									  </a>
								     <%}%>			  
								  </div>
							       <div class="last" style="float:left;margin-left:5px;">
								     <%if(current_page==page_tot){%>
								       <img src="img/skos_tab/last_off.png" class="last_button" title="${skostabtabbuttondisabled}">
								     <%}else{%>
								       <a class="" title="${arrowNext}" onclick="next_prew_results_unlinked('last','<%=page_tot%>',<%=current_page%>);">			  	
									  	<img src="img/skos_tab/last.png" class="last_button" title="${arrowLast}">
									  </a>
								     <%}%>			  
								  </div>	  
							</div>
							<%}%>
							<div class="order" style="float:right;margin-right:10px;">${skostabtablabelsearchtotresults} : <%=tot_records%></div>
						</div>
						<div style="margin:0;padding:0;width:100%;">
							<%for(int i=0;i<list.size();i++){
					   				Records records2 = list.get(i);				
					   			    HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
						    %>
						    	<div class="search_result_row"><span style="margin-left:3px;"><a class="relation_link" href="#no" onclick="openRecordRelations('<%=records2.getIdRecord()%>');"><%=parsedTitle2.get("notation")[0]%> <%=parsedTitle2.get("label")[0]%></a></span></div>
						    <%
							}%>
						</div>
					</div>	
			</div>
	</div>
</div>
<%}%>
