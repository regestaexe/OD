<%@page import="com.regesta.framework.util.JsSolver"%><%@page import="org.apache.commons.lang.StringUtils"%><%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><%@page import="com.openDams.bean.Records"%><%@page import="java.util.List"%><%@page import="com.openDams.title.configuration.TitleManager"%><%@page import="java.util.HashMap"%><%@include file="../../locale.jsp"%><%@page import="com.openDams.bean.Relations"%><%@page import="java.util.Set"%>
<% 
TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
Records oldRecords = (Records)request.getAttribute("oldRecords");
%>
<script>
$(document).ready(function() {
	 getChildren_deprecate($(".archiveDeprecate").attr("idRecord"));
     higlight_row_tree($(".archiveDeprecate").parent("div").find(".tree_title:first"),true);
     build_tree_deprecate();   	    
});
function build_tree_deprecate(){	
	//$(".tree_title").contextMenu({ menu: 'right_menu' }, function(action, el, pos) { contextMenuWork(action, el, pos); });
	$(".open_close_deprecate").unbind();
	$(".open_close_deprecate").click(function() {
        if(!$(this).hasClass( "leaf" )){
        	if($(this).hasClass( "closed" )){
    			$(this).removeClass("closed");
    			$(this).addClass("opened");
    		}else{
    			$(this).removeClass("opened");
    			$(this).addClass("closed");
    		}
        	getChildren_deprecate($(this).attr("idRecord"));
        }
	});	
}
function getChildren_deprecate(id_record){	
    if($(".record_children_deprecate_"+id_record).is(":visible")){
    	$(".record_children_deprecate_"+id_record).hide("slide", { direction: "up" }, 1000);
    }else{
    	openLoading();
    	$(".record_children_deprecate_"+id_record).find(".record_list_deprecate:first").load("ajax/deprecate.html?idArchive=<%=request.getParameter("idArchive")%>&action=get_children&idRecord="+id_record,function(){
    		$(".record_children_deprecate_"+id_record).show("slide", { direction: "up" }, 1000);  		 
    		build_tree_deprecate();
    		closeLoading();
    	}); 	
    }
}
function next_prew_children_page_deprecate(id_record,action,tot_page,fromRelations){
	if(!$(".record_children_deprecate_"+id_record).is(":visible")){
		$(".record_children_deprecate_"+id_record+":first").parent("div").find(".open_close:first").each(function(){
			if($(this).hasClass( "closed" )){
				$(this).removeClass("closed");
				$(this).addClass("opened");
			}else{
				$(this).removeClass("opened");
				$(this).addClass("closed");
			}
		});
		$(".record_children_deprecate_"+id_record+":first").show("slide", { direction: "up" }, 1000);				
		build_tree();
    }	
	current_page = parseInt($(".record_children_deprecate_"+id_record+":first").find(".current_page:first").html(),10);
	tot  = parseInt(tot_page,10);
	if(action=='next'){
         if(current_page+1>tot){
			
         }else{
        	 current_page = current_page+1;
        	 $(".record_children_deprecate_"+id_record).find(".current_page:first").html(current_page);
         }
	}else if(action=='last'){
		current_page = tot_page;
		$(".record_children_deprecate_"+id_record).find(".current_page:first").html(tot_page);
	}else if(action=='first'){
		current_page = 1;
		$(".record_children_deprecate_"+id_record).find(".current_page:first").html("1");
	}else{
		 if(current_page-1<=0){
			
         }else{
        	 current_page = current_page-1;
        	 $(".record_children_deprecate_"+id_record).find(".current_page:first").html(current_page);
         }
	}
	openLoading();
	$(".record_children_deprecate_"+id_record).find(".record_list_deprecate").load("ajax/deprecate.html?idArchive=<%=request.getParameter("idArchive")%>&action=get_children&idRecord="+id_record+"&current_page="+current_page,function(){
		closeLoading();
		build_tree_deprecate();
	});
}
function deprecate(id_record,title){	
	Ext.MessageBox.buttonText.yes = "Sí"; 
	Ext.MessageBox.buttonText.cancel = "No"; 
	<%
	HashMap<String, String[]> oldParsedTitle = titleManager.parseTitle(oldRecords.getTitle(),oldRecords.getArchives().getIdArchive());	
	%>
	Ext.MessageBox.confirm('Attenzione', 'deprecare termine <b>"<%=JsSolver.escapeSingleApex(oldParsedTitle.get("notation")[0]+" "+oldParsedTitle.get("label")[0]).trim()%>"</b> in favore di  <b>"'+title+'"</b>?',function(btn){
		if(btn=="yes"){
			Ext.Ajax.request({
			        url: 'ajax/deprecate.html?action=deprecate&idRecordDeprecate='+id_record+'&encoding='+$("form[name='editForm']").find("input[name='encoding']").attr("value")+'&idArchive=<%=request.getParameter("idArchive")%>&idRecord='+$("form[name='editForm']").find("input[name='idRecord']").attr("value"),
			        nocache: true,
			        success: function(r) {
			        	    reloadTree();
			        		openRecord($("form[name='editForm']").find("input[name='idRecord']").attr("value"));
			        		Ext.getCmp('deprecate_button').setText('Riabilita voce');
			        		deprecate_window.hide();
						    deprecate_window.destroy();
			        },
			        failure: function(){}
			});
		}else{
			deprecate_window.hide();
		    deprecate_window.destroy();
		}
	});
}
</script>
<div style="background-color:#FFFFFF;overflow-x:hidden;overflow-y:auto;height:355px;;float:left;position:absolute;width:97%;padding-bottom:5px">
<%int page_size = (Integer)request.getAttribute("page_size");
		if(request.getAttribute("recordsList")!=null){		
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
				<div class="record_row" style="padding-left:5px;padding-top:5px;background-color:#FFFFFF;">
					<div class="record_row">
					 <a href="#no" class="archive archiveDeprecate archive_<%=records.getArchives().getIdArchive()%>"  idRecord="<%=records.getIdRecord()%>"></a>
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
					 
					 <a href="#no" idRecord="<%=records.getIdRecord()%>" <%if(deprecated){%>style="color:#808080;" title="voce deprecata"<%}%> class="tree_title clicked_<%=records.getIdRecord()%>" onclick="return deprecate('<%=parsedTitle.get("id")[0]%>','<%=JsSolver.escapeSingleApex(parsedTitle.get("notation")[0]+" "+label)%>');"><%=parsedTitle.get("notation")[0]%> <%=parsedTitle.get("label")[0]%></a>
					 <%--if(tot_related>0){--%><!--<div class="n_relations n_RT" title="related concepts">(<%//=tot_related%>)</div>--><%--}--%>
					</div>
					<%if(records.getRelationsesForRefIdRecord2()!=null && records.getRelationsesForRefIdRecord2().size()>0){
					    
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
</div>