<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%><html>
	<head></head>
	<body>
			<form action="search.html" name="simple_form">
			<input type="hidden" name="action" value="search">
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
						        <td>ID:</td>
						        <td><input type="text" name="sf_id" value=""></td>
					        </tr>
					        <tr>
						        <td>LABEL:</td>
						        <td><input type="text" name="sf_label" value=""></td>
					        </tr>
					        <tr>
						        <td>CONTENTS:</td>
						        <td><input type="text" name="sf_contents" value=""></td>
					        </tr>
					        <tr>
						        <td>ALTLABEL:</td>
						        <td><input type="text" name="sf_altLabel" value=""></td>
					        </tr>
					        <tr>
						        <td>SCOPENOTE:</td>
						        <td><input type="text" name="sf_scopeNote" value=""></td>
					        </tr>
					        <tr>
						        <td colspan="2"><input type="submit"></td>
					        </tr>
			        </table>
			</form>
			<form action="search.html" name="generic_form">
			<input type="hidden" name="action" value="search">
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
						        <td>/rdf:RDF/skos:Concept/@rdf:about :</td>
						        <td><input type="text" name="gsf_/rdf:RDF/skos:Concept/@rdf:about" value=""></td>
					        </tr>
					        <tr>
						        <td>/rdf:RDF/skos:Concept/skos:scopeNote/text() :</td>
						        <td><input type="text" name="gsf_/rdf:RDF/skos:Concept/skos:scopeNote" value=""></td>
					        </tr>
					        <tr>
						        <td>rdf:RDF/skos:Concept/skos:inScheme/@rdf:resource :</td>
						        <td><input type="text" name="gsf_/rdf:RDF/skos:Concept/skos:inScheme/@rdf:resource" value=""></td>
					        </tr>
					        <tr>
						        <td colspan="2"><input type="submit"></td>
					        </tr>
			        </table>		
			</form>
	</body>
</html>