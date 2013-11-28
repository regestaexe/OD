<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%>
<%@page import="com.openDams.bean.ArchiveTypes"%>
<%@page import="com.openDams.bean.ArchiveIdentities"%><html>
	<head></head>
	<body>
			<form action="createArchive.html" method="post">
			<input type="hidden" name="action" value="insert">
			        <table>
			                <tr>
						        <td>Tipo Archivio:</td>
						        <td>
						        	<select name="archiveTypes.idArchiveType">
						        	<%if(request.getAttribute("archiveTypesList")!=null){ 
						        	   List<Object> archives = (List)request.getAttribute("archiveTypesList");
						        	   Object[] archiveList = archives.toArray();
						        	   for(int i=0;i<archiveList.length;i++){
						        		   ArchiveTypes archive = (ArchiveTypes)archiveList[i];
						        	%>
						        			<option value="<%=archive.getIdArchiveType()%>"><%=archive.getDescription()%></option>
						        	<%}%>
						        	<%}%>
						        	</select>
						        </td>
					        </tr>
					        <tr>
						        <td>Identità Archivio:</td>
						        <td>
						        	<select name="archiveIdentities.idArchiveIdentity">
						        	<%if(request.getAttribute("archiveIdentitiesList")!=null){ 
						        	   List<Object> archives = (List)request.getAttribute("archiveIdentitiesList");
						        	   Object[] archiveList = archives.toArray();
						        	   for(int i=0;i<archiveList.length;i++){
						        		   ArchiveIdentities archive = (ArchiveIdentities)archiveList[i];
						        	%>
						        			<option value="<%=archive.getIdArchiveIdentity()%>"><%=archive.getDescription()%></option>
						        	<%}%>
						        	<%}%>
						        	</select>
						        </td>
					        </tr>
					        <tr>
						        <td>label:</td>
						        <td>
						        	<input type="text" name="label">
						        </td>
					        </tr>
					        <tr>
						        <td>description:</td>
						        <td>
						        	<input type="text" name="description">
						        </td>
					        </tr>
					        <tr>
						        <td>XML Prefix:</td>
						        <td>
						        	<input type="text" name="archiveXmlPrefix" value="ODSN">
						        </td>
					        </tr>
					        <tr>
						        <td>create default index:</td>
						        <td>
						        	<select name="use_default_index">
						        	      <option value="true">true</option>
						        	      <option value="false" selected="selected">false</option>
						        	</select>
						        </td>
					        </tr>
					        <tr>
						        <td colspan="2"><input type="submit"></td>
					        </tr>
			        </table>	
			</form>
	</body>
</html>