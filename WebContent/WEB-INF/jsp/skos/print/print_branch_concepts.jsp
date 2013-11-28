<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Relations"%>
<%@page import="java.util.Set"%>
<%@include file="../../locale.jsp"%>
<style>
.archive_print{
   text-decoration: none;
   width: 17px;
   height: 15px;
   display: block;
   float: left;
   color: black;
   background: url(img/tree_sky_blue/archive.png);
   background-repeat: no-repeat;
}
.open_close_print{
    text-decoration: none;
    width: 17px;
    height: 15px;
    display: block;
    float: left;
    color: black;
	position:absolute;
}
.opened_print{
	 background: url(img/tree_sky_blue/folder_list_node.png);
	 background-repeat: no-repeat;
}
.leaf_print{
	 background: url(img/tree_sky_blue/file.png);
	 background-repeat: no-repeat;
}
.record_children_print{
   width: 100%;
   margin-left:7px;
   border-left: 1px solid #99bbe8;
   border-bottom: 1px solid #99bbe8;
   padding-bottom: 5px;
   padding-top: 5px;
   float: left;
}
.tree_title_print{
	text-decoration: none;
	float: left;
	display: block;
	color: #15428b;
	font-size: 12px;
	border: 0;
	font:normal 10px verdana;
	margin-left:17px;
}
</style>
<script>
<%
String idRecordFather="";
if(request.getParameter("action")!=null && request.getParameter("action").equalsIgnoreCase("getConcepts")){
	idRecordFather = request.getParameter("idRecord");
}
%>
$(document).ready(function(){
	
    $(".<%=idRecordFather%>_concept_record_print").each(function(){
        if( $(".<%=idRecordFather%>_record_row_print_children_"+$(this).attr("idRecord"))!=null &&  $(".record_row_print_children_"+$(this).attr("idRecord"))!=undefined){
        	$(".<%=idRecordFather%>_record_row_print_children_"+$(this).attr("idRecord")).html("<img src=\"img/ajax-loader.gif\" border=\"0\"/>");
            $(".<%=idRecordFather%>_record_row_print_children_"+$(this).attr("idRecord")).load("ajax/print_branch.html?action=getConcepts&print_mode=<%=request.getParameter("print_mode")%>&idRecord="+$(this).attr("idRecord"));
        }
    });
});
</script>
<%
if(request.getAttribute("recordsList")!=null){
	TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
	List<Records> recordsList = (List<Records>)request.getAttribute("recordsList");
	for(int i=0;i<recordsList.size();i++){
		Records records = recordsList.get(i);
		HashMap<String, String[]> parsedTitle = titleManager.parseTitle(records.getTitle(),records.getArchives().getIdArchive());	
		int tot_children = 0;
		tot_children = records.getTotChildren();	
		%>
		<div class="record_row_print" style="background-color:#FFFFFF;<%if(idRecordFather.equals("")){%>width:900px;border:1px solid #99bbe8;overflow:hidden;padding:5px;<%}%>">
			<div class="record_row_print">
					 <%
					    boolean deprecated = false;
					 	try{
					 		if(!parsedTitle.get("deprecated")[0].trim().equals("")){
					 			deprecated=true;
					 		}
					 	}catch(NullPointerException ex){}
					 	String[] labels = parsedTitle.get("label");
					 	String label ="";
					 	for(int s=0;s<labels.length;s++){
					 		label+=labels[s];
					 	}
					 %>
				<div class="<%=idRecordFather%>_concept_record_print"  style="<%if(deprecated){%>color:#808080;<%}%>" idRecord="<%=records.getIdRecord()%>">						
						<a href="#no" class="<%if(idRecordFather.equals("")){%>archive_print<%}else if(tot_children>0){%>open_close_print opened_print<%}else{%>open_close_print leaf_print<%}%>"></a>
						<span class="tree_title_print"><%=parsedTitle.get("notation")[0]%> <%=label%></span>				
				</div>
			</div>
			<%if(tot_children>0){%>
			<div class="<%=idRecordFather%>_record_row_print_children_<%=records.getIdRecord()%> record_children_print"></div>
			<%}%>
		</div>
		<%
	}
}%>
