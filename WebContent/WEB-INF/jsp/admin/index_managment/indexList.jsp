<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%>
<script>
$(document).ready(function() {
    $(".indexLink").click(function(){
             $("#indexDetails").load("indexList.html?action=index_details&idArchive="+$(this).attr("id"));

    });
});
</script>
<%if(request.getAttribute("archiveIndexList")!=null){ 
List<Object> archiveIndexList = (List)request.getAttribute("archiveIndexList");
	Object[] archiveList = archiveIndexList.toArray();
	for(int i=0;i<archiveList.length;i++){
	 	Archives archive = (Archives)archiveList[i];%>
		<div style="margin-left:5px;font-weight:bold;font-size:12px;cursor: pointer;" class="indexLink" id="<%=archive.getIdArchive()%>"><%=archive.getLabel()%></div>
	<%}%>
<%}%>