<%@page import="com.openDams.search.configuration.Element"%>
<%@page import="java.util.ArrayList"%> 

<%@page import="com.openDams.search_builder.utility.SearchResult"%>

<%@page import="com.regesta.framework.util.JsSolver"%><script type="text/javascript">
<%
if(request.getAttribute("searchResultsList")!=null){%>
	    	        <%
	    	        ArrayList<SearchResult>  searchResultsList = (ArrayList<SearchResult>)request.getAttribute("searchResultsList");
					for (int i = 0; i < searchResultsList.size(); i++) {
						SearchResult searchResult = searchResultsList.get(i);
						//System.out.println(searchResult.getArchiveLabel()+" "+searchResult.getTotalHits());
						//System.out.println(searchResult.getQuery());
						%>
						var searchResult_archive_<%=searchResult.getIdArchive()%> =  new Ext.Panel({
																							  id: 'searchResult_archive_<%=searchResult.getIdArchive()%>',
																				              html:'<a href="#" class="link_document" onclick="openSearchTab(\'<%=searchResult.getIdArchive()%>\',\'<%=JsSolver.escapeSingleApex(searchResult.getQuery().replaceAll("'"," ").replaceAll("\\+","%2B"))%>\',\'<%=searchResult.getTotalHits()%>\',\'<%=searchResult.getOrder_by()%>\',\'<%=searchResult.getSort_type()%>\',\'<%=searchResult.getFind_sons()%>\',\'<%=searchResult.getFile_search()%>\');"><img width="12" height="12" src="img/archive_img/archive_<%=searchResult.getIdArchive()%>.gif" border="0"/>&nbsp;<span class="tree_top_title" style="font-weight:bold;"><%=searchResult.getArchiveLabel()%></span> <span style="font-weight:bold;"><%=searchResult.getTotalHits()%></span></a>',
																				              border : false,
																				              padding : 3
																					  });
						Ext.getCmp('center-panel_searcBuilder_archive_all').add(searchResult_archive_<%=searchResult.getIdArchive()%>);						
						<%
					}
					%>
					var goolePanel =  new Ext.Panel({
						  id: 'goolePanel',
			              html:'<a href="#" class="link_document" onclick="addGoogleTab();"><img src="img/archive_img/google.gif" border="0"/>&nbsp;<span class="tree_top_title" style="font-weight:bold;">Risultati su Google</span></a>',
			              border : false,
			              padding : 3
				  });
					Ext.getCmp('center-panel_searcBuilder_archive_all').add(goolePanel);		
					Ext.getCmp('center-panel_searcBuilder_archive_all').doLayout();
					//Ext.getCmp('search_panel_archive_all').collapse();
	     
<%}%>
</script>

