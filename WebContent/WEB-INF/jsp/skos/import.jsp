<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%>
<html>
	<head></head>
	<body <%if(request.getParameter("impoprtOK")!=null){%>onload="alert('importazione eseguita con successo')"<%}%> >
			<form action="import.html" method="post">
			<input type="hidden" name="action" value="import">
			        <table>
			                <tr>
						        <td>Archivio:</td>
						        <td>
						        	<select name="id_archive">
						        	<%if(request.getAttribute("archiveList")!=null){ 
						        	   List<Object> archives = (List)request.getAttribute("archiveList");
						        	   Object[] archiveList = archives.toArray();
						        	   for(int i=0;i<archiveList.length;i++){
						        		   Archives archive = (Archives)archiveList[i];
						        	%>
						        			<option value="<%=archive.getIdArchive()%>"><%=archive.getDescription()%></option>
						        	<%}%>
						        	<%}%>
						        	</select>
						        </td>
					        </tr>
					        <tr>
						        <td>ID RADICE:</td>
						        <td>
						        	<input type="text" name="idRadice" value="L0000021">
						        </td>
					        </tr>
					        <tr>
						        <td colspan="2"><input type="submit"></td>
					        </tr>
			        </table>	
			</form>
	</body>
</html>