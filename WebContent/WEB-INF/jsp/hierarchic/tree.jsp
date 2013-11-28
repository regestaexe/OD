<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%@include file="../locale.jsp"%>
<script>
$(document).ready(function() {
	$(".tree").bind("contextmenu",function(e){
        return false;
     });
	getChildren($(".archive").attr("idRecord"));
	$(".short_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=short_tab&idRecord="+$(".archive").attr("idRecord"));
	$(".xml_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=xml&idRecord="+$(".archive").attr("idRecord"));
	build_tree();
});
function build_tree(){	
	$(".tree_title").contextMenu({ menu: 'right_menu' }, function(action, el, pos) { contextMenuWork(action, el, pos); });
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
        	//$(".short_tab").load("record.html?idArchive=<%=request.getParameter("idArchive")%>&action=xml&idRecord="+$(this).attr("idRecord"));
        	$(".short_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=short_tab&idRecord="+$(this).attr("idRecord"));
        	$(".xml_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=xml&idRecord="+$(this).attr("idRecord"));
        }		
		getChildren($(this).attr("idRecord"));
	});
	
}
function next_prew_children_page(id_record,action,tot_page){
	current_page = parseInt($(".record_children_"+id_record).find(".current_page:first").html(),10);
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
	$(".record_children_"+id_record).find(".record_list").load("ajax/tree.html?idArchive=<%=request.getParameter("idArchive")%>&action=get_children&idRecord="+id_record+"&current_page="+current_page,function(){
		build_tree();
	});
}
function getChildren(id_record){
    if($(".record_children_"+id_record).is(":visible")){
    	$(".record_children_"+id_record).hide("slide", { direction: "up" }, 1000);
    }else{
    	$(".record_children_"+id_record).find(".record_list:first").load("ajax/tree.html?idArchive=<%=request.getParameter("idArchive")%>&action=get_children&idRecord="+id_record,function(){
    		$(".record_children_"+id_record).show("slide", { direction: "up" }, 1000);
    		build_tree();
    	}); 	
    }
}
function openRecord(id_record){
	$(".short_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=short_tab&idRecord="+id_record);
	$(".xml_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=xml&idRecord="+id_record);
}
function contextMenuWork(action, el, pos) {

    switch (action) {
        case "delete":
            {
            	alert("cancella"+el.attr("idRecord"));
                break;
            }
        case "insert":
            {
               alert("crea nuovo"+el.attr("idRecord"));
                break;
            }

        case "edit":
            {
            	alert("modifica"+el.attr("idRecord"));
            	break;
            }
        case "copy":
	        {
	        	alert("copy"+el.attr("idRecord"));
	        	break;
	        }   
        case "cut":
	        {
	        	alert("taglia"+el.attr("idRecord"));
	        	break;
	        }
        case "paste":
	        {
	        	alert("incolla"+el.attr("idRecord"));
	        	break;
	        }
        case "xml":
	        {
	        	openRecord(el.attr("idRecord"));
	        	break;
	        }     
        case "sel_on":
        {
        	$(".sel_check").show();
        	$(".sel_radio").show();
        	$(".sel_radio").click(function(){
        		if($(this).is(":checked")){
                  	 $("#destination_id").attr("value",$(this).attr("value"));
                   }
            });
        	$(".sel_check").click(function(){
        		$("#multi_sel_id").attr("value","");
        		$(".sel_check").each(function(){
        			if($(this).is(":checked")){
                   	 $("#multi_sel_id").attr("value", $("#multi_sel_id").attr("value")+$(this).attr("value")+"~");
                    }
                });
        		if($("#multi_sel_id").attr("value")!=""){
                	$(".sel_radio").attr("disabled",false);
                }else{
                	$(".sel_radio").attr("disabled",true);
                	$("#destination_id").attr("value","");	
                	$(".sel_radio").attr("checked",false);
                }
            });
            
        	break;
        }     
        case "sel_off":
        {
        	$(".sel_check").hide();
        	$(".sel_radio").hide();
        	$(".sel_check").attr("checked",false);
        	$(".sel_radio").attr("checked",false);
        	$("#multi_sel_id").attr("value","");
        	$("#destination_id").attr("value","");	
        	break;
        }
        case "move_on":
        {
        	$(".sel_check").show();
        	break;
        }     
        case "move_off":
        {
        	$(".sel_check").hide();
        	break;
        }             
    }
}
</script>

				<ul id="right_menu" class="contextMenu">
		    <li class="insert"><a href="#insert">${contextMenuNew}</a></li>		
		    <li class="edit"><a href="#edit">${contextMenuModify}</a></li>
		    <li class="copy"><a href="#copy">${contextMenuCopy}</a></li>
		    <li class="cut"><a href="#cut">${contextMenuCut}</a></li>
		    <li class="paste"><a href="#paste">${contextMenuPaste}</a></li>	        
		    <li class="delete"><a href="#delete">${contextMenuDelete}</a></li>
		    <li class="back"><a href="#back" class="back">${contextMenuReset}</a></li>
		    <li class="xml"><a href="#xml">${contextMenuViewxml}</a></li>
		    <li class="sel_on"><a href="#sel_on">${contextMenuSelecton}</a></li>
		    <li class="sel_off"><a href="#sel_off">${contextMenuSelectoff}</a></li>
		    <li class="import"><a href="#sel_off">${contextMenuImportxml}</a></li>			
		</ul>
		<%
		if(request.getAttribute("recordsList")!=null){
			TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");			
			List<Records> recordsList = (List<Records>)request.getAttribute("recordsList");
			for(int i=0;i<recordsList.size();i++){
				Records records = recordsList.get(i);
				HashMap<String, String[]> parsedTitle = titleManager.parseTitle(records.getTitle(),records.getArchives().getIdArchive());			
				int tot_children = 0;
				if(records.getRelationsesForRefIdRecord2()!=null){
					tot_children = records.getRelationsesForRefIdRecord1().size();
 				}
				%>
				<div class="record_row" style="padding-left:5px;padding-top:5px;">
					<div class="record_row">
					 <a href="#no" class="archive"  idRecord="<%=records.getIdRecord()%>"></a>
					 <input type="radio" class="sel_radio" name="sel_radio" value="<%=records.getIdRecord()%>" disabled="disabled" title="record destinazione"/>
					 <input type="checkbox" class="sel_check" value="<%=records.getIdRecord()%>" title="record da selezionare"/>
					 <a href="#no" idRecord="<%=records.getIdRecord()%>" class="tree_title" onclick="openRecord('<%=records.getIdRecord()%>');"><%=parsedTitle.get("composed_title")[0]%></a>
					 <span class="n_figli">(<%=tot_children%>)</span>
					</div>
					<%if(records.getRelationsesForRefIdRecord1()!=null && records.getRelationsesForRefIdRecord1().size()>0){
					    int page_size = (Integer)request.getAttribute("page_size");
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
							<div class="record_buttons"><div class="arrow_buttons"><a class="first_page_record" title="click to first" onclick="next_prew_children_page('<%=records.getIdRecord()%>','first','<%=page_tot%>')"></a><a class="prev_page_record" title="click to previous" onclick="next_prew_children_page('<%=records.getIdRecord()%>','previous','<%=page_tot%>')"></a><span class="label_buttons">PAGINA<span class="current_page">1</span>DI <%=page_tot%></span><a class="next_page_record" title="click to next" onclick="next_prew_children_page('<%=records.getIdRecord()%>','next','<%=page_tot%>')"><a/><a class="last_page_record" title="click to last" onclick="next_prew_children_page('<%=records.getIdRecord()%>','last','<%=page_tot%>')"><a/></div></div>
							<%}%>
							<div class="record_list"></div>
						</div>
					<%}%>
					
				</div>
				<%
			}
		}%>

<input type="hidden" id="destination_id" name="destination_id" value=""/>
<input type="hidden" id="multi_sel_id" name="multi_sel_id" value="~" size="100"/> 
