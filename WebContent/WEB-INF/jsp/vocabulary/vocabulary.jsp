<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="com.openDams.index.searchers.VocTerm"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.lucene.document.Document"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>	
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="com.openDams.index.searchers.Vocabulary"%>
<%@page import="org.apache.commons.lang.ArrayUtils"%>
<%if(request.getAttribute("vocabulary")!=null){
	Vocabulary vocabulary = (Vocabulary)request.getAttribute("vocabulary");
%>
<script>
	var term_previous = '';
	var isFirstPage;
	var isLastPage;
	var last_term = '';
	<%
	if(vocabulary.isLastPage()){%>
		isLastPage=true;
	<%}else{%>
		isLastPage=false;
	<%}
	if(vocabulary.isFirstPage()){%>
		isFirstPage=true;
	<%}else{%>
		isFirstPage=false;
	<%}%>
	$(document).ready(function(){
	  		$(".ck_voc_class").click(function(){
	  			if($(this).attr("checked")==true){
	  				$("input[name='selected_terms']").attr("value",$("input[name='selected_terms']").attr("value")+"|"+$(this).attr("value"));
	  			}else{
	  				var value = $("input[name='selected_terms']").attr("value");
	  				value = value.substring(0,value.indexOf("|"+$(this).attr("value")))+value.substring(value.indexOf("|"+$(this).attr("value"))+("|"+$(this).attr("value")).length,value.length);
	  				$("input[name='selected_terms']").attr("value",value);
	  	   		}
	  		});
	});
function vocMoveTo(){
	var cks = Ext.select("input[name^=ck-multi-search-archive_]").elements;
	var requestArchives='';
	for (var x = 0 ; x < cks.length; x ++){
		if(Ext.getCmp(Ext.get(cks[x]).id).getValue()){
			requestArchives+=Ext.getCmp(Ext.get(cks[x]).id).inputValue+";";
		}						
	}
	$(".voc_contents").html('<img src="img/ajax-loader.gif" />');
	var skipTo = $.trim($("input[name='voc_move_to']").attr("value"));
	if(skipTo!='' && skipTo.indexOf("*")==-1 || skipTo.substring(skipTo.length-1,skipTo.length)!='*'){
		skipTo+="*";
	}
	skipTo = escape(skipTo);
	var one_field = false;
	if($("input[name='one_field']")!=undefined){
		one_field = $("input[name='one_field']").attr("checked");
	}
	$(".voc_contents").load("vocabulary.html?voc_field="+$("input[name='voc_field']").attr("value")+"&one_field="+one_field+"&last_term="+skipTo+"&requestArchives="+requestArchives+"&action=voc_page&selected_terms="+$("input[name='selected_terms']").attr("value"),function(){
       	if(isFirstPage){
       		Ext.getCmp('button_prev').setDisabled(true);			
	    }else{
	    	Ext.getCmp('button_prev').setDisabled(false);		
		}
       	if(isLastPage){
       		Ext.getCmp('button_next').setDisabled(true);
	    }else{
	    	Ext.getCmp('button_next').setDisabled(false);		
		}
    });
}	
</script>
<div style="width:100%;float:left;padding-top:10px;">
	<input type="hidden" name="voc_field" value="<%=request.getParameter("voc_field")%>"><br/>
	<input type="hidden" name="selected_terms" value=""><br/>
	<div class="voc_go_to" style="width:100%;float:left;margin-left:10px;">
		<div style="float:left;">
			<input type="text" name="voc_move_to" class="x-form-text x-form-field x-form-focus" style="width: 200px;" value="">
		</div>
		<div style="float:left;margin-left:10px;">
			<table cellspacing="0" class="x-btn   x-btn-noicon " id="ext-comp-1719" style="width:75px;"><tbody class="x-btn-small x-btn-icon-small-left"><tr><td class="x-btn-tl"><i>&nbsp;</i></td><td class="x-btn-tc"></td><td class="x-btn-tr"><i>&nbsp;</i></td></tr><tr><td class="x-btn-ml"><i>&nbsp;</i></td><td class="x-btn-mc"><em unselectable="on" class=""><button type="button"  class=" x-btn-text" onclick="vocMoveTo();">Vai a</button></em></td><td class="x-btn-mr"><i>&nbsp;</i></td></tr><tr><td class="x-btn-bl"><i>&nbsp;</i></td><td class="x-btn-bc"></td><td class="x-btn-br"><i>&nbsp;</i></td></tr></tbody></table>
		</div>
		<%if(request.getAttribute("one_field")!=null){%>
			<div style="float:left;margin-left:10px;">
				<input type="checkbox" name="one_field" class="x-form-text x-form-field x-form-focus" value="true"> campo intero
			</div>
		<%}%>		
	</div>
	<div class="voc_contents" style="width:100%;height:254px;;float:left;margin-top:10px;padding-bottom:10px;border-top:1px solid #99bbe8;background-color:#FFFFFF;overflow:auto;">
			<script>
				term_previous=escape('<%=JsSolver.escapeSingleApex(vocabulary.getTermForPrevious())%>');
			</script>
			<%
			String[] selected_terms = null;
			try{
				selected_terms = request.getParameter("selected_terms").split("\\|");
			}catch(NullPointerException e){}
			ArrayList<VocTerm> vocTerms = vocabulary.getTerms();
			if(vocTerms!=null && vocTerms.size()>0){
					TitleManager titleManager = (TitleManager) request.getAttribute("titleManager");
					int half = 0;
					if(vocTerms.size()%2==0)
						half = vocTerms.size()/2;
					else
						half = (vocTerms.size()/2)+1;
					%>
					<div class="voc_left" style="width:225px;float:left;margin-left:10px;">
					<%
					for(int i=0;i<half;i++){
						VocTerm vocTerm = vocTerms.get(i);
						%>
						<div class="voc_term" style="float:left;width:225px;margin-top:5px;border-bottom: 1px dotted #99bbe8;"><div style="float:left;width:15px;"><input type="checkbox" class="ck_voc_class" <%if(i==0){%>id="first_term_page"<%}%> name="ck_<%=i%>" value="<%=vocTerm.getTerm()%>"  <%if(ArrayUtils.contains(selected_terms,vocTerm.getTerm())){%> checked="checked"<%}%>></div><div style="float:left;margin-left:3px;width:25px;text-align:justify;color:#808080;">[<%=vocTerm.getFrequence()%>]</div><%=vocTerm.getTerm()%></div>
						<%
					}
					%>
					</div>
					<div class="voc_right" style="width:225px;float:left;margin-left:10px;">
					<%
					for(int i=half;i<vocTerms.size();i++){
						VocTerm vocTerm = vocTerms.get(i);
						%>						
						<div class="voc_term" style="float:left;width:225px;margin-top:5px;border-bottom: 1px dotted #99bbe8;"><div style="float:left;width:15px;"><input type="checkbox" class="ck_voc_class" <%if(i==vocTerms.size()-1){%>id="last_term_page"<%}%> name="ck_<%=i%>" value="<%=vocTerm.getTerm()%>"  <%if(ArrayUtils.contains(selected_terms,vocTerm.getTerm())){%> checked="checked"<%}%>></div><div style="float:left;margin-left:3px;width:25px;text-align:justify;color:#808080;">[<%=vocTerm.getFrequence()%>]</div><%=vocTerm.getTerm()%></div>
						<%if(i==vocTerms.size()-1){
							%>
							<script>
							  last_term = escape('<%=JsSolver.escapeSingleApex(vocTerm.getTerm())%>');
							</script>
							<%
						}
					}
					%>
					</div>				
			<%}else{%>
			<div class="voc_left" style="width:90%;float:left;margin-left:10px;">nessun termine trovato</div>
			<%}%>
	</div>

</div>	
<%}else{%>
	<div id="form-ct">nessun termine trovato</div>
<%}%>