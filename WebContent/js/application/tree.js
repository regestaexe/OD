function higlight_row_tree(toHighlight, higlight) {
	if (higlight) {
		toHighlight.css("background-color", "#ffa313");
		toHighlight.css("font-weight", "bold");
	} else {
		toHighlight.css("background-color", "");
		toHighlight.css("font-weight", "");
	}
}
function build_tree() {
	$(".open_close").unbind();
	$(".open_close").click(function() {
		if (!$(this).hasClass("leaf")) {
			if ($(this).hasClass("closed")) {
				$(this).removeClass("closed");
				$(this).addClass("opened");
			} else {
				$(this).removeClass("opened");
				$(this).addClass("closed");
			}
			getChildren($(this).attr("idRecord"));

		} else {

		}
	});

}
function next_prew_children_page(id_record, action, tot_page, fromRelations) {
	if (!$(".record_children_" + id_record).is(":visible")) {
		$(".record_children_" + id_record + ":first").parent("div").find(".open_close:first").each(function() {
			if ($(this).hasClass("closed")) {
				$(this).removeClass("closed");
				$(this).addClass("opened");
			} else {
				$(this).removeClass("opened");
				$(this).addClass("closed");
			}
		});
		$(".record_children_" + id_record + ":first").slideDown();
		build_tree();
	}
	current_page = parseInt($(".record_children_" + id_record + ":first").find(".current_page:first").html(), 10);
	/*if (fromRelations == undefined)
		next_prew_children_page_tab(id_record, action, tot_page, current_page, '2', true);*/
	tot = parseInt(tot_page, 10);
	if (action == 'next') {
		if (current_page + 1 > tot) {

		} else {
			current_page = current_page + 1;
			$(".record_children_" + id_record).find(".current_page:first").html(current_page);
		}
	} else if (action == 'last') {
		current_page = tot_page;
		$(".record_children_" + id_record).find(".current_page:first").html(tot_page);
	} else if (action == 'first') {
		current_page = 1;
		$(".record_children_" + id_record).find(".current_page:first").html("1");
	} else {
		if (current_page - 1 <= 0) {

		} else {
			current_page = current_page - 1;
			$(".record_children_" + id_record).find(".current_page:first").html(current_page);
		}
	}

	/*
	 * TODO: diego impostare un loading locale impostata solo sul componente
	 * dell'albero
	 */
	// openLoading();
	$(".record_children_" + id_record).find(".record_list").load("ajax/skosDocumental.html?idArchive=" + globalOpt.idArchivio + "&action=get_children&idRecord=" + id_record + "&current_page=" + current_page, function() {
		// closeLoading();
		build_tree();
	});
}
function getChildren(id_record) {
	if ($(".record_children_" + id_record).is(":visible")) {
		$(".record_children_" + id_record).slideUp();
	} else {
		// openLoading();
		$(".record_children_" + id_record).find(".record_list:first").load("ajax/skosDocumental.html?idArchive=" + globalOpt.idArchivio + "&action=get_children&idRecord=" + id_record, function() {
			$(".record_children_" + id_record).slideDown();

			build_tree();
			// closeLoading();
		});
	}
}
function fathersFinder(id_record,idArchive){
	$("#fathers_finder").load("ajax/documental_fathers_finder.html?idRecord="+id_record+"&idArchive="+idArchive,function(){
		Ext.getCmp('relations_tree_panel_archive_'+idArchive).expand();	
	});
}
function openFathers(fathers,positions,id_record,idArchive){
	higlight_row_tree($(".tree_title"),false);
    for(var i=1;i<fathers.length;i++){
	    	page_size = 40;
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
		        url: "ajax/skosDocumental.html?idArchive="+idArchive+"&action=get_children&idRecord="+fathers[i]+"&current_page="+current_page,
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
	higlight_row_tree($(".clicked_"+id_record),true);    
    build_tree();
}
function cancelRelation(id_record, id_relations, idToRemove) {
	$.ajax({
		url : "ajax/documental_relation_manager.html?action=delete&idRecord=" + globalOpt.document.id + "&id_record_relation=" + id_record + "&relation_type="+id_relations,
		cache : false,
		success : function() {
			resetRecord();
			$("#" + idToRemove).remove();
		}
	});
	return true;
}
function addRelationFromTree(id_record, id_relations, textRelation, idArchive) {
	var id_relation = 12;
	if(idArchive==16){
		id_relation = 14;
	}else if(idArchive==101){
		id_relation = 19;
	}else if(idArchive==121){
		id_relation = 25;
	}
	$.ajax({
		url : "ajax/documental_relation_manager.html?idRecord=" + globalOpt.document.id + "&id_record_relation=" + id_record + "&relation_type="+id_relation+"&action=add",
		cache : false,
		success : function(data) {
			resetRecord();
			if (data != 'ko') {			
				var html = "<div style=\"padding:2px;float:left;width:100%;\" id=\"div_relations_" + id_record + "\">";
				  	html += "<div style=\"width:100%;float:left;\">";
					html += "<div style=\"float:left;width:15px;\"><a href=\"#n\" onclick=\"return cancelRelation('" + id_record + "','" + id_relation + "','div_relations_" + id_record + "')\"><img src=\"img/skos_tab/delete.png\" class=\"delete_button input_label_buttons_img\" title=\"elimina relazione\"></a></div>";
					html += "<div style=\"float:left;margin-left:10px;\">" + textRelation + "</div>";
					html += "<div style=\"float:left;width:15px;margin-left:10px;\"><a href=\"#n\" onclick=\"openInfoRelation('"+id_record+"','info_relation_"+id_record+"')\"><img src=\"img/info.png\" border=\"0\"/ title=\"visualizza informazioni\"></a></div>";
					html += "<div id=\"info_relation_"+id_record+"\" style=\"display:none;float:left;width:95%;border:1px solid #ededed;padding:2px;margin-left:25px;margin-right:10px;background-color:#ffffff;font-size:10px;\"></div>";
					html += "</div>";
			 		html += "</div>";				
					$("#record_relations_panel_"+idArchive).append(html);
				return gestDepartment('add',true);
			}
		}
	});
	return true;
}

function treeCallback(archiveClass) {
	getChildren($("."+archiveClass).attr("idRecord"));
	// higlight_row_tree($(".archive").parent("div").find(".tree_title:first"),
	// true);
	build_tree();
}
function openInfoRelation(idRecord,div){	
	if($("#"+div).is(":visible")){
		$("#"+div).hide();
	}else{		
			$("#"+div).html("<img src=\"img/ajax-loader.gif\" border=\"0\"/>");
			$("#"+div).load("ajax/print_branch.html?action=getRelations&print_mode=relations&idRecord="+idRecord+"&idRelationType=3&from=tree",function(){
			});
		
		$("#"+div).show();		
		
	}
}