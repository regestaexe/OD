<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.lucene.document.Document"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%@include file="../locale.jsp"%>
<%
if(request.getAttribute("results")!=null){
	Document[] hits = (Document[])request.getAttribute("results");
	TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
	int current_page = 1;
	if(request.getParameter("current_page")!=null){
		current_page = new Integer(request.getParameter("current_page"));
	}
	int page_size = (Integer)request.getAttribute("page_size");
	int page_tot = 1;
	int tot_records = hits.length;
	if(tot_records > page_size){
		if(tot_records % page_size==0 )
			page_tot=tot_records/page_size;
		else
			page_tot=(tot_records/page_size)+1;
	}
%>

<div style="width:100%;">
	<div class="search_results_pager">
	<%if(tot_records>page_size){%>
		<div class="search_results_pager_container">
			 <div class="first" style="float:left;margin-left:5px;">
		     	 <%if(current_page==1){%>
			       <img src="img/skos_tab/first_off.png" class="first_button" title="${skostabtabbuttondisabled}">
			     <%}else{%>
			       <a class="" title="${arrowPrevios}" onclick="next_prew_results_page('first','<%=page_tot%>',<%=current_page%>);">
			      	<img src="img/skos_tab/first.png" class="first_button" title="${arrowFirst}">
			      </a>
			     <%}%>		      
		      </div>
		      <div class="letf" style="float:left;margin-left:5px;">
		     	 <%if(current_page==1){%>
			       <img src="img/skos_tab/left_off.png" class="left_button" title="${skostabtabbuttondisabled}">
			     <%}else{%>
			       <a class="" title="${arrowPrevios}" onclick="next_prew_results_page('previous','<%=page_tot%>',<%=current_page%>);">
			      	<img src="img/skos_tab/left.png" class="left_button" title="${arrowPrevios}">
			      </a>
			     <%}%>		      
		      </div>
		      <div style="float:left;padding-left:5px;padding-right:5px;">${arrowPage} <%=current_page%> ${arrowOf} <%=page_tot%></div>
		      <div class="right" style="float:left;">
			     <%if(current_page==page_tot){%>
			       <img src="img/skos_tab/right_off.png" class="right_button" title="${skostabtabbuttondisabled}">
			     <%}else{%>
			       <a class="" title="${arrowNext}" onclick="next_prew_results_page('next','<%=page_tot%>',<%=current_page%>);">			  	
				  	<img src="img/skos_tab/right.png" class="right_button" title="${arrowNext}">
				  </a>
			     <%}%>			  
			  </div>
		       <div class="last" style="float:left;margin-left:5px;">
			     <%if(current_page==page_tot){%>
			       <img src="img/skos_tab/last_off.png" class="last_button" title="${skostabtabbuttondisabled}">
			     <%}else{%>
			       <a class="" title="${arrowNext}" onclick="next_prew_results_page('last','<%=page_tot%>',<%=current_page%>);">			  	
				  	<img src="img/skos_tab/last.png" class="last_button" title="${arrowLast}">
				  </a>
			     <%}%>			  
			  </div>	  
		</div>
		<%}%>
		<%
		String sort1="";
		String sort2="";
		if(request.getParameter("order_by")!=null && !request.getParameter("order_by").equals("")){
			sort2="checked=\"checked\"";
		}else{
			sort1="checked=\"checked\"";
		}
		%>
		<div class="order" style="float:left;margin-left:10px;">
			<div style="float:left;">${skostabtablabelsearchorderby} :</div>  
			<div style="float:left;margin-left:5px;"><input type="radio" name="order_by" value="" <%=sort1%> onclick="sort();"></div>  
			<div style="float:left;margin-left:3px;">${skostabtablabelsearchorderbyrelevance}</div> 
			<div style="float:left;margin-left:5px;"><input type="radio" name="order_by" value="label" <%=sort2%> onclick="sort();"></div> 
			<div style="float:left;margin-left:3px;">${skostabtablabelsearchorderbylabel}</div> 
		</div>
		<div class="order" style="float:right;margin-right:10px;">${skostabtablabelsearchtotresults} : <%=tot_records%></div>
	</div>
	<div style="margin:0;padding:0;width:100%;">
		<%
		for (int i = 0; i < hits.length; i ++) {
			
			if(hits[i]!=null){
				Document docsearch = hits[i];				
				HashMap<String, String[]> parsedTitle = titleManager.parseTitle(docsearch.get("title_record"),new Integer(request.getParameter("idArchive")));
	    %>
	    	<div class="search_result_row"><span style="margin-left:3px;"><a class="relation_link" href="#no" onclick="fathersFinder('<%=docsearch.get("id_record")%>');"><%=parsedTitle.get("label")[0].replaceAll("ç","")%></a></span></div>
	    <%  
			}
		}%>
	</div>
</div>	
<%}%>
