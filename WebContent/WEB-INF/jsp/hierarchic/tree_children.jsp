<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>

<%
if(request.getAttribute("recordsList")!=null){
	TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
	List<Records> recordsList = (List<Records>)request.getAttribute("recordsList");
	for(int i=0;i<recordsList.size();i++){
		Records records = recordsList.get(i);
		HashMap<String, String[]> parsedTitle = titleManager.parseTitle(records.getTitle(),records.getArchives().getIdArchive());	
		int tot_children = 0;
	/*	if(records.getRelationsesForRefIdRecord2()!=null){
			tot_children = records.getRelationsesForRefIdRecord1().size();
		}*/
		%>
		

		<div class="record_row">
			<div class="record_row">
		    <a href="#no" class="open_close <%if(tot_children>0){%>closed<%}else{%>leaf<%}%>" idRecord="<%=records.getIdRecord()%>"></a>
		    <input type="radio" class="sel_radio" name="sel_radio" value="<%=records.getIdRecord()%>" disabled="disabled" title="record destinazione"/>
			  <input type="checkbox" class="sel_check" value="<%=records.getIdRecord()%>" title="record da selezionare"/>
			<a href="#no" idRecord="<%=records.getIdRecord()%>" class="tree_title" onclick="openRecord('<%=records.getIdRecord()%>');"><%=parsedTitle.get("composed_title")[0]%></a>
			<span class="n_figli"><%if(tot_children>0){%>(<%=tot_children%>)<%}%></span>
			</div>
			<%if(records.getDepth()>0 && records.getRelationsesForRefIdRecord1()!=null && records.getRelationsesForRefIdRecord1().size()>0){
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
