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
    term_previous = escape('<%=JsSolver.escapeSingleApex(vocabulary.getTermForPrevious())%>'); 
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
						<%
						if(i==vocTerms.size()-1){
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
<%}else{%>
	<div>nessun termine</div>
<%}%>