<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%@include file="../locale.jsp"%>
<%int page_size = (Integer)request.getAttribute("page_size");%>

<%@page import="com.openDams.bean.Relations"%>
<%@page import="java.util.Set"%>

<script>
$(document).ready(function() {
	if($(".archive").attr("idRecord")!=null && $(".archive").attr("idRecord")!=undefined){
		 getChildren($(".archive").attr("idRecord"));
	     higlight_row_tree($(".archive").parent("div").find(".tree_title:first"),true);
		$(".xml_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=xml_tab&idRecord="+$(".archive").attr("idRecord"));
		$(".skos_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=skos_tab&idRecord="+$(".archive").attr("idRecord"));
		$(".relations_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=relations_tab&idRecord="+$(".archive").attr("idRecord"));
	    if(!$(".visual_tab").is(":hidden"))
			$(".visual_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=visual_tab&idRecord="+$(".archive").attr("idRecord"),function(){drowGraph();});
		 build_tree();   	
	}    
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
	//$(".tree_title").contextMenu({ menu: 'right_menu' }, function(action, el, pos) { contextMenuWork(action, el, pos); });
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
    		getChildren($(this).attr("idRecord"));
        }else{
        	openLoading();
        	$(".xml_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=xml_tab&idRecord="+$(this).attr("idRecord"));
        	if(!$(".visual_tab").is(":hidden"))
        		$(".visual_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=visual_tab&idRecord="+$(this).attr("idRecord"),function(){drowGraph();});
        	$(".skos_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=skos_tab&idRecord="+$(this).attr("idRecord"),function(){
            $(".relations_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=relations_tab&idRecord="+$(this).attr("idRecord"));
            		closeLoading();
            });
        }		
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
		$(".record_children_"+id_record+":first").show("slide", { direction: "up" }, 1000);				
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
	$(".record_children_"+id_record).find(".record_list").load("ajax/skos.html?idArchive=<%=request.getParameter("idArchive")%>&action=get_children&idRecord="+id_record+"&current_page="+current_page,function(){
		closeLoading();
		build_tree();
	});
}
function getChildren(id_record){	
    if($(".record_children_"+id_record).is(":visible")){
    	$(".record_children_"+id_record).hide("slide", { direction: "up" }, 1000);
    }else{
    	openLoading();
    	//console.debug($(".record_children_"+id_record));
    	//console.debug($(".record_children_"+id_record).find(".record_list:first"));
    	$(".record_children_"+id_record).find(".record_list:first").load("ajax/skos.html?idArchive=<%=request.getParameter("idArchive")%>&action=get_children&idRecord="+id_record,function(){
    		$(".record_children_"+id_record).show("slide", { direction: "up" }, 1000);  		 
    		build_tree();    		
    		closeLoading();
    	}); 	
    }
}
function openFathers(fathers,positions,id_record){	
	openLoading();
    for(i=1;i<fathers.length;i++){
	    	page_size = <%=page_size%>;
			current_page = 1;
			current_position = (parseInt(positions[i],10)+1);
		    if(current_position > page_size){
			    if((current_position % page_size) <= 5){
			    	current_page = Math.round(current_position/page_size)+1;
			    }else{
			    	current_page = Math.round(current_position/page_size);
				}
		    }
    		$.ajax({
	        url: "ajax/skos.html?idArchive=<%=request.getParameter("idArchive")%>&action=get_children&idRecord="+fathers[i]+"&current_page="+current_page,
	        type: 'GET',
	        async: false,
	        cache: false,
	        timeout: 30000,
	        error: function(){
	            return true;
	        },
	        complete: function(html,textStatus){
	        	$(".record_children_"+fathers[i]).parent("div").find(".open_close:first").each(function(){
    				$(this).removeClass("closed");
    				$(this).removeClass("opened");
    				$(this).addClass("opened");
    			});
	        	$(".record_children_"+fathers[i]).find(".current_page:first").html(current_page); 
	        	$(".record_children_"+fathers[i]).find(".record_list:first").html(html.responseText);
	            $(".record_children_"+fathers[i]).show("slide", { direction: "up" }, 1000);
	            
	        }
	    }); 
    }	
    build_tree();
    openRecord(id_record);
    closeLoading();
}
function getChildrenFromTab(id_record,i,id_record_father,tot_children){
    if($(".record_children_"+id_record).is(":visible")){
    	//$(".record_children_"+id_record).hide("slide", { direction: "up" }, 1000);
    	
    }else{  
    	if($(".record_children_"+id_record+":first").html() == null){
			page_size = <%=page_size%>;
			current_page = 1;
			current_position = (i+1);
		    if(current_position > page_size){
			    if((current_position % page_size) <= 5){
			    	current_page = Math.round(current_position/page_size)+1;
			    }else{
			    	current_page = Math.round(current_position/page_size);
				}
		    }
    		$(".record_children_"+id_record_father+":first").find(".record_list:first").load("ajax/skos.html?idArchive=<%=request.getParameter("idArchive")%>&action=get_children&idRecord="+id_record_father+"&current_page="+current_page,function(){
        		$(".record_children_"+id_record_father+":first").parent("div").find(".open_close:first").each(function(){
						if($(this).hasClass( "closed" )){
			    			$(this).removeClass("closed");
			    			$(this).addClass("opened");
			    		}else{
			    			$(this).removeClass("opened");
			    			$(this).addClass("closed");
			    		}
				});
        		$(".record_children_"+id_record_father+":first").find(".current_page:first").html(current_page);
				$(".record_children_"+id_record_father+":first").show("slide", { direction: "up" }, 1000);				
        		build_tree();
        		openRecord(id_record);
        	}); 
        }		
    }
    openRecord(id_record);
}
function openRecord(id_record,page){
	openLoading();
	$(".xml_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=xml_tab&idRecord="+id_record);
	if(!$(".visual_tab").is(":hidden")){
		if(page!=undefined){
			$(".visual_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=visual_tab&idRecord="+id_record+"&current_page="+page,function(){drowGraph();});
		}else{
			$(".visual_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=visual_tab&idRecord="+id_record,function(){drowGraph();});
		}
		
	}
	$(".relations_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=relations_tab&idRecord="+id_record);
	$(".skos_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=skos_tab&idRecord="+id_record,function(){closeLoading();});	
	higlight_row_tree($(".tree_title"),false);
	higlight_row_tree($(".clicked_"+id_record),true);
}
function reloadTree(){
	var isArchive = true;
	$("form[name='relationsForm']").find(".record_father").each(function(){
		var id_record = $(this).attr("id");	
		isArchive = false;
		$(".record_children_"+id_record).find(".record_list:first").load("ajax/skos.html?idArchive=<%=request.getParameter("idArchive")%>&action=get_children&idRecord="+id_record,function(){
			$(".record_children_"+id_record).show("slide", { direction: "up" }, 1000);		 
			build_tree();
		});
		
	});
	if(isArchive)
		reloadAllTree();	
}
function moveToFather(){   
	$("form[name='relationsForm']").find(".record_father").each(function(){
		var id_recordChild = $(this).attr("id");
		var classes = $(".record_children_"+id_recordChild).parent("div").parent("div").parent("div").attr("class");
		var id_record = classes.substring(classes.lastIndexOf("_")+1,classes.length);
			var isArchive = true;
			$(".record_children_"+id_record).find(".record_list:first").load("ajax/skos.html?idArchive=<%=request.getParameter("idArchive")%>&action=get_children&idRecord="+id_record,function(){
				$(".record_children_"+id_record).show("slide", { direction: "up" }, 1000);		 
				build_tree();
				openRecord(id_recordChild);
				isArchive = false;
			});
			if(isArchive)
				reloadAllTree();		
			
	});
}
function addRelation(id_record_relation,relation_type,reopen_accordion){
	var id_record = $("form[name='editForm']").find("input[name='idRecord']").attr("value");
    $("#relation_results").load("ajax/relation_manager.html?idRecord="+id_record+"&id_record_relation="+id_record_relation+"&relation_type="+relation_type+"&action=add",function(){});    
    Ext.getCmp('relation_results_accordion').collapse();	
    Ext.getCmp ('relation_results_accordion').setDisabled(true);	  
    Ext.getCmp(reopen_accordion).expand();
    openRecord(id_record);
}
function addFirstRecord(){
	Ext.MessageBox.buttonText.ok = "Crea Record";
	Ext.MessageBox.buttonText.cancel = "Chiudi";
	Ext.MessageBox.show({
           title: '${skosTabcreateAddtitle}',
           msg: '${skosTabcreateAddmessage}',
           width:300,
           buttons: Ext.MessageBox.OKCANCEL,
           multiline: true,
           fn: addRecord
    });
	Ext.MessageBox.buttonText.ok = "OK";
	Ext.MessageBox.buttonText.cancel = "Cancell";
	
}
function addRecord(btn, text){     
    if(btn=='ok'){
    	 if($.trim(text)!=''){
    		 $("#relation_results").load("ajax/create_add.html?action=create&label="+escape(text)+"&idArchive=<%=request.getParameter("idArchive")%>&xmltype=ConceptScheme&idRecordType=3",function(){
    			 reloadTree();
        	 });	                        
         }else{
        	 Ext.MessageBox.show({
                 msg: '${skosTabcreateAddmessagetrim}',
                 buttons: Ext.MessageBox.OK,                         
                 icon: 'ext-mb-warning'
             });
         }
     }		
}
/*function contextMenuWork(action, el, pos) {

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
}*/
</script>

		<!--ul id="right_menu" class="contextMenu">
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
		</ul-->
		<%
		if(request.getAttribute("recordsList")!=null && ((List<Records>)request.getAttribute("recordsList")).size()>0){
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
					 <a href="#no" class="archive"  idRecord="<%=records.getIdRecord()%>"></a>
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
					 <%
					 	boolean deleted = false;
					 	try{
					 		deleted=records.getDeleted();
					 	}catch(NullPointerException ex){}
					 	if(deleted){%>
					 		<a href="#no" idRecord="<%=records.getIdRecord()%>" title="voce nel cestino" class="pending_delete clicked_<%=records.getIdRecord()%>"><%=parsedTitle.get("notation")[0]%> <%=label%> <%if(tot_children>0){%><span class="n_relations n_NT" title="narrower concepts">(<%=tot_children%>)</span><%}%></a>
					 	<%}else{%>
					 		<a href="#no" idRecord="<%=records.getIdRecord()%>" <%if(deprecated){%>style="color:#808080;" title="voce deprecata"<%}%> class="tree_title clicked_<%=records.getIdRecord()%>" onclick="openRecord('<%=records.getIdRecord()%>');"><%=parsedTitle.get("notation")[0]%> <%=label%> <%if(tot_children>0){%><span class="n_relations n_NT" title="narrower concepts">(<%=tot_children%>)</span><%}%></a>
					 	<%}
					 %>
					 <%--if(tot_related>0){--%><!--<div class="n_relations n_RT" title="related concepts">(<%//=tot_related%>)</div>--><%--}--%>
					</div>
					<%//if(tot_children>0){
					    
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
					<%//}%>
				</div>
				<%
			}
		}else{%>
		<div class="record_row" style="padding-left:5px;padding-top:5px;">
			<div class="record_row">
			    <a href="#no" class="archive"></a>
			 	<a href="#no" class="tree_title" onclick="addFirstRecord();">Inserisci la radice</a>
			</div>
		</div>
		<%}%>
