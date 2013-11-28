<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%@include file="../locale.jsp"%>
<%int page_size = (Integer)request.getAttribute("page_size");%>

<%@page import="com.openDams.bean.Relations"%>
<%@page import="java.util.Set"%>



<%@page import="com.regesta.framework.util.JsSolver"%><script>
$(document).ready(function() {
	 getChildren($(".archive_<%=request.getParameter("idArchive")%>").attr("idRecord"));
     higlight_row_tree($(".archive_<%=request.getParameter("idArchive")%>").parent("div").find(".tree_title:first"),true);
	 build_tree();
});
function higlight_row_tree(toHighlight,higlight){
	if(higlight){
		toHighlight.css("background-color","#ffa313");
		toHighlight.css("font-weight","bold");
	}else{
		toHighlight.css("background-color","");
		toHighlight.css("font-weight","");
	}	
}
function build_tree(){
	$(".open_close").unbind();
	$(".open_close").click(function() {
        if(!$(this).hasClass( "leaf" )){
        	if($(this).hasClass( "closed" )){
    			$(this).removeClass("closed");
    			$(this).addClass("opened");
    		}else{
    			$(this).removeClass("opened");
    			$(this).addClass("closed");
    		}
        }else{
        }		
		getChildren($(this).attr("idRecord"));
	});
	
}
function next_prew_children_page(id_record,action,tot_page,fromRelations){
	if(!$(".record_children_"+id_record).is(":visible")){
		$(".record_children_"+id_record+":first").parent("div").find(".open_close:first").each(function(){
			if($(this).hasClass( "closed" )){
				$(this).removeClass("closed");
				$(this).addClass("opened");
			}else{
				$(this).removeClass("opened");
				$(this).addClass("closed");
			}
		});
		$(".record_children_"+id_record+":first").slideDown();				
		build_tree();
    }	
	current_page = parseInt($(".record_children_"+id_record+":first").find(".current_page:first").html(),10);
    if(fromRelations==undefined)
			next_prew_children_page_tab(id_record,action,tot_page,current_page,'2',true);
	tot  = parseInt(tot_page,10);
	if(action=='next'){
         if(current_page+1>tot){
			
         }else{
        	 current_page = current_page+1;
        	 $(".record_children_"+id_record).find(".current_page:first").html(current_page);
         }
	}else if(action=='last'){
		current_page = tot_page;
		$(".record_children_"+id_record).find(".current_page:first").html(tot_page);
	}else if(action=='first'){
		current_page = 1;
		$(".record_children_"+id_record).find(".current_page:first").html("1");
	}else{
		 if(current_page-1<=0){
			
         }else{
        	 current_page = current_page-1;
        	 $(".record_children_"+id_record).find(".current_page:first").html(current_page);
         }
	}
	openLoading();
	$(".record_children_"+id_record).find(".record_list").load("searchBuilderTree.html?idArchive=<%=request.getParameter("idArchive")%>&action=get_children&idRecord="+id_record+"&current_page="+current_page,function(){
		closeLoading();
		build_tree();
	});
}
function getChildren(id_record){	
    if($(".record_children_"+id_record).is(":visible")){
    	$(".record_children_"+id_record).slideUp();
    }else{
    	openLoading();
    	$(".record_children_"+id_record).find(".record_list:first").load("searchBuilderTree.html?idArchive=<%=request.getParameter("idArchive")%>&action=get_children&idRecord="+id_record,function(){
    		$(".record_children_"+id_record).slideDown();  		 

    		build_tree();
    		closeLoading();
    	}); 	
    }
}
 
</script>
		<%
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
				<div class="record_row" style="padding-left:5px;padding-top:5px;">
					<div class="record_row">
					 <a href="#no" class="archive archive_<%=request.getParameter("idArchive")%>"  idRecord="<%=records.getIdRecord()%>"></a>
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
					 <a href="#no" idRecord="<%=records.getIdRecord()%>" <%if(deprecated){%>style="color:#808080;" title="voce deprecata"<%}%> class="tree_title clicked_<%=records.getIdRecord()%>" ><%=parsedTitle.get("notation")[0]%> <%=label%></a>
					 <%--if(tot_related>0){--%><!--<div class="n_relations n_RT" title="related concepts">(<%//=tot_related%>)</div>--><%--}--%>
					</div>
					<%if(tot_children>0){					    
					    int page_tot = 1;
					    if(tot_children > page_size){
					    	if(tot_children % page_size==0 )
					    		page_tot=tot_children/page_size;
							else
								page_tot=(tot_children/page_size)+1;
					    }
					%>
						<div class="record_children record_children_<%=records.getIdRecord()%>">
							<%if(page_tot>1){ %>
							<div class="record_buttons"><div class="arrow_buttons"><a class="first_page_record" title="${arrowFirst}" onclick="next_prew_children_page('<%=records.getIdRecord()%>','first','<%=page_tot%>')"></a><a class="prev_page_record" title="${arrowPrevios}" onclick="next_prew_children_page('<%=records.getIdRecord()%>','previous','<%=page_tot%>')"></a><span class="label_buttons">${arrowPage}<span class="current_page">1</span>${arrowOf} <%=page_tot%></span><a class="next_page_record" title="${arrowNext}" onclick="next_prew_children_page('<%=records.getIdRecord()%>','next','<%=page_tot%>')"><a/><a class="last_page_record" title="${arrowLast}" onclick="next_prew_children_page('<%=records.getIdRecord()%>','last','<%=page_tot%>')"><a/></div></div>
							<%}%>
							<div class="record_list"></div>
						</div>
					<%}%>
				</div>
				<%
			}
		}%>
