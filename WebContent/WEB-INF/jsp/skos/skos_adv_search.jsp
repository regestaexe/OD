<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%>
<%@include file="../locale.jsp"%>
<script>
//$(document).ready(function(){});
function next_prew_results_page(action,tot_page,current_page){
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
	$("#adv_search_results").load("ajax/skos_adv_search.html?"+$("form[name='adv_search_form']").serialize()+"&current_page="+current_page,function(){
		closeLoading();
	});
	
}
function sort(){
	openLoading();
	$("#adv_search_results").load("ajax/skos_adv_search.html?"+$("form[name='adv_search_form']").serialize(),function(){
		closeLoading();
	});
}


</script>
<form action="" name="adv_search_form" style="margin:0;padding:5px;">
		<input type="hidden" name="action" value="search">
		<input type="hidden" name="idArchive" value="<%=request.getParameter("idArchive")%>">
<div class="area_top">		
	<div class="area_tab"><div class="area_tab_label">${skostabtablabelsearchfields} <div class="x-tool x-tool-toggle x-tool-collapse-north macro_collapse" id="search_fields">&nbsp;</div></div></div>
	<div class="area_tab_bottom_line"></div>
	<div class="search_fields">
	    <div class="input_label_row">
			<div class="input_label">${skostabtablabelid}</div>
		</div>
		<div class="edit_div">
		<input type="text" class="edit_field"  name="sf_id" value="">
		</div>
		<div class="input_label_row">
			<div class="input_label">${skostabtablabellabel}</div>
		</div>
		<div class="edit_div">
		<input type="text" class="edit_field"  name="sf_label">
		</div>
	    <div class="input_label_row">
			<div class="input_label">${skostabtablabelalternativelabel}</div>
		</div>
		<div class="edit_div">
		<input type="text" class="edit_field"  name="sf_altLabel">
		</div>
		<div class="input_label_row">
			<div class="input_label">${skostabtablabelscopenote}</div>
		</div>
		<div class="edit_div">
		<input type="text" class="edit_field"  name="sf_scopeNote">
		</div>
		<div class="input_label_row">
			<div class="input_label">${skostabtablabelfulltext}</div>
		</div>
		<div class="edit_div">
		<input type="text" class="edit_field"  name="sf_contents">
		</div>
	    <div class="edit_div">
		</div>
	</div>
</div>
<div class="area">
		<div class="area_tab">
		<div class="area_tab_label">${skostabtablabelsearchresults}<div class="x-tool x-tool-toggle x-tool-collapse-north macro_collapse" id="search_results">&nbsp;</div></div></div>
		<div class="area_tab_bottom_line"></div>
		<div  class="search_results" id="adv_search_results">
		
		</div>
</div>
<div class="last_empty_line">&nbsp;</div>

</form>

