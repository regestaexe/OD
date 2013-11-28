<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%>


<%@page import="com.regesta.framework.util.JsSolver"%>
<html> 
	<head>
	<link rel="icon" href="img/sqlxdicon.png" type="image/png" />
	<LINK REL="shortcut icon" HREF="img/sqlxdicon.ico">	
	</head>
	<body>			
			        <table>		                
						        	<%if(request.getAttribute("archiveList")!=null){ 
						        	   List<Object> archives = (List)request.getAttribute("archiveList");
						        	   Object[] archiveList = archives.toArray();
						        	   for(int i=0;i<archiveList.length;i++){
						        		   Archives archive = (Archives)archiveList[i];
						        	%>
						        		<tr>						       
						        			<td>
						        			<%=archive.getDescription()%>
						        			<a onclick="addTab('<%=JsSolver.escapeSingleApex(archive.getDescription())%>','<%=archive.getIdArchive()%>','workspace.html?idArchive=<%=archive.getIdArchive()%>');" href="#no"> tab</a>
						        			<a href="workspace.html?idArchive=<%=archive.getIdArchive()%>&win=new" target="_new">win</a>
						        			 </td>
					        			</tr>
						        		<%}%>
						        	<%}%>
			        </table>					
	</body>
</html>