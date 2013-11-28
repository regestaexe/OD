<%@page import="java.util.ArrayList"%>
<%@page import="com.openDams.admin.tasks.JobDetails"%>

<div style="font-weight:bold;margin-top:10px;">rigenerazioni indici</div>
<table border="0" cellspacing="8">
	<tr align="center">
		<td>ID</td>
		<td>VERSIONE</td>
		<td>CREAZIONE</td>
		<td>INIZIO</td>
		<td>FINE</td>
		<td>STATO</td>
	</tr>
<%if(request.getAttribute("rebuildIndexList")!=null){
	ArrayList<JobDetails> rebuildIndexList = (ArrayList<JobDetails>)request.getAttribute("rebuildIndexList");
	for(int i=0;i<rebuildIndexList.size();i++){
		JobDetails jobDetails = rebuildIndexList.get(i);%>
	    <tr>
			<td><%=jobDetails.getJOB_EXECUTION_ID()%></td>
			<td><%=jobDetails.getVERSION()%></td>
			<td><%=jobDetails.getCREATE_TIME()%></td>
			<td><%=jobDetails.getSTART_TIME()%></td>
			<td><%=jobDetails.getEND_TIME()%></td>
			<td><%=jobDetails.getSTATUS()%></td>
		</tr>
	<%} 	
}%>
</table>

<div style="font-weight:bold;margin-top:10px;">rigenerazioni titoli</div>
<table border="0" cellspacing="8">
	<tr align="center">
		<td>ID</td>
		<td>VERSIONE</td>
		<td>CREAZIONE</td>
		<td>INIZIO</td>
		<td>FINE</td>
		<td>STATO</td>
	</tr>
	<%if(request.getAttribute("rebuildTitleList")!=null){
	ArrayList<JobDetails> rebuildTitleList = (ArrayList<JobDetails>)request.getAttribute("rebuildTitleList");
	for(int i=0;i<rebuildTitleList.size();i++){
		JobDetails jobDetails = rebuildTitleList.get(i);%>
	    <tr>
			<td><%=jobDetails.getJOB_EXECUTION_ID()%></td>
			<td><%=jobDetails.getVERSION()%></td>
			<td><%=jobDetails.getCREATE_TIME()%></td>
			<td><%=jobDetails.getSTART_TIME()%></td>
			<td><%=jobDetails.getEND_TIME()%></td>
			<td><%=jobDetails.getSTATUS()%></td>
		</tr>
	<%} 	
}%>
</table>