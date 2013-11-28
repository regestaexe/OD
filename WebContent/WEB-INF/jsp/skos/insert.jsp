<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>

<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%>
<html>
	<head></head>
	<body>
			<form action="insert.html" method="post">
			<input type="hidden" name="action" value="insert">
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
						        <td>XML:</td>
						        <td>
						        	<textarea rows="15" cols="150" name="xml_to_insert"></textarea> 
						        </td>
					        </tr>
					        <tr>
						        <td colspan="2"><input type="submit"></td>
					        </tr>
			        </table>	
			</form>
	</body>
</html>