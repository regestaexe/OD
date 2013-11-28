<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.search.configuration.Element"%>
<%@page import="java.util.ArrayList"%> 
<%@page import="com.openDams.search_builder.utility.SearchResult"%>
<%@page import="com.regesta.framework.util.JsSolver"%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.openDams.utility.StringsUtils"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>

<%@page import="org.apache.lucene.document.Document"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.regesta.framework.util.StringUtility"%>

<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>



<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%><script type="text/javascript">
var count_panel = 0;
<%
	if (request.getAttribute("results") != null) {
		Document[] hits = (Document[]) request.getAttribute("results");
		%>
		Ext.getCmp('search_panel_archive_<%=request.getParameter("idArchive")%>').collapse();
		$("#nResults_archive_<%=request.getParameter("idArchive")%>").html('<%=hits.length%>');
<%
if(hits.length>(Integer)request.getAttribute("page_size")){%>
			Ext.getCmp('center-panel_searcBuilder_archive_bbar_<%=request.getParameter("idArchive")%>').removeAll(true);
<%
			int tot_results = hits.length;
			int page_size = (Integer)request.getAttribute("page_size");
			int start = 0;
			if(request.getParameter("start")!=null){
				start = Integer.parseInt(request.getParameter("start"));
			}
			%>
			Ext.getCmp('center-panel_searcBuilder_archive_bbar_<%=request.getParameter("idArchive")%>').addButton({
				<%if((start-page_size)<0){%>
    			disabled:true,
    			<%}%>	        	
			    text: 'Risultati precedenti',				    
			    handler: function(){
    				        Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').removeAll(true);
							Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').load({ 
								url: 'searchBuilder_adv_search.html?action=search&idArchive=<%=request.getParameter("idArchive")%>&order_by=<%=request.getParameter("order_by")%>&sort_type=<%=request.getParameter("sort_type")%>&start=<%=start-page_size%>&file_search=<%=request.getParameter("file_search")%>&query=<%=URLEncoder.encode(request.getParameter("query"),"UTF-8")%>',
								nocache: true,
								text: 'Ricerca in corso...', 
								timeout: 30, 
								scripts: true
							});
			    }
			});
			Ext.getCmp('center-panel_searcBuilder_archive_bbar_<%=request.getParameter("idArchive")%>').addButton({	        	
			    text: 'Risultati successivi',
			    <%if((start+page_size)>=tot_results){%>
    			disabled:true,
    			<%}%>					    
			    handler: function(){
    						Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').removeAll(true);
							Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').load({ 
								url: 'searchBuilder_adv_search.html?action=search&idArchive=<%=request.getParameter("idArchive")%>&order_by=<%=request.getParameter("order_by")%>&sort_type=<%=request.getParameter("sort_type")%>&start=<%=(start+page_size)%>&file_search=<%=request.getParameter("file_search")%>&query=<%=URLEncoder.encode(request.getParameter("query"),"UTF-8")%>',
								nocache: true,
								text: 'Ricerca in corso...', 
								timeout: 30, 
								scripts: true
							});
			    }
			});
			Ext.getCmp('center-panel_searcBuilder_archive_bbar_<%=request.getParameter("idArchive")%>').doLayout();
			<%
			int end = Math.min(tot_results, (start+page_size));
			%>
			$("#nResults_archive_page_<%=request.getParameter("idArchive")%>").html(' visualizzati: <%=(start+1)%>-<%=end%>');	
			<%
		
}
%>

					<%
					SimpleDateFormat formatter6 = new SimpleDateFormat("yyyyMM");
					SimpleDateFormat formatterDests6 = new SimpleDateFormat("MMM. yyyy", Locale.ITALY);
					SimpleDateFormat formatter8 = new SimpleDateFormat("yyyyMMdd");
					SimpleDateFormat formatterDests8 = new SimpleDateFormat("d MMM. yyyy", Locale.ITALY);
					TitleManager titleManager = (TitleManager) request.getAttribute("titleManager");
					for (int i = 0; i < hits.length; i++) {
						if (hits[i] != null) {
							Document docsearch = hits[i];
							HashMap<String, String[]> parsedTitle = titleManager.parseTitle(docsearch.get("title_record"), new Integer(request.getParameter("idArchive")));
							String title = "";
							String date = "";
							String dateNormal = "";
							String simpleTitle = "";
								String[] titleArray = parsedTitle.get("notation");
								
								StringBuffer titleBuffer = new StringBuffer();
								if (titleArray != null) {
									for (String singleTitle : titleArray) {
										try {
											simpleTitle += singleTitle.replaceAll("ç", " ");
											titleBuffer.append(singleTitle.replaceAll("ç", " "));
										} catch (Exception e) {
										}
									}
								}
								titleArray = parsedTitle.get("title");
								if (titleArray != null) {
									for (String singleTitle : titleArray) {
										try {									
											titleBuffer.append(singleTitle.replaceAll("ç", " "));
										} catch (Exception e) {
										}
									}
								}
								titleArray = parsedTitle.get("sottotitolo");
								if (titleArray != null) {
									for (String singleTitle : titleArray) {
										try {
											titleBuffer.append(singleTitle.replaceAll("ç", " "));
										} catch (Exception e) {
										}
									}
								}
								title = (titleBuffer.toString());
								if (title.trim().equals("")) {
									title = "[senza titolo]";
								}
								try {
									date = parsedTitle.get("adata")[0].trim();
									dateNormal = date; 
									if(date.length()==6){
										Date data = formatter6.parse(date);
										date = formatterDests6.format(data);
									}else if(date.length()==8){
										Date data = formatter8.parse(date);
										date = formatterDests8.format(data);
									}else if(date.length()==4){ 
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								date = date.replaceAll(" ", "&nbsp;");
								title = title.replaceAll("&amp;apos;", "'");
								title = title.replaceAll("&apos;", "'");
								
								simpleTitle = simpleTitle.replaceAll("<[^>bB]*>","");
								if(simpleTitle.length()>100){
									simpleTitle = simpleTitle.substring(0,100)+"..";
								}
							try {%> 
							if(Ext.getCmp('east-panel_searcBuilder_dest').get('panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.get("id_record")%>')==null || Ext.getCmp('east-panel_searcBuilder_dest').get('panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.get("id_record")%>')==undefined){
								var panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.get("id_record")%> =  new Ext.Panel({
																											id : 'panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.get("id_record")%>',																				
																											title : '<span class="search_result_float_right_date"><%=date%></span><img width="12" height="12" src="img/archive_img/archive_<%=request.getParameter("idArchive")%>.gif" border="0"/>&nbsp;<a href="#" class="linkArchiveTitle" onclick="return openRecord(\'<%=docsearch.get("id_record")%>\',true,\'<%=request.getParameter("idArchive")%>\',\'<%=JsSolver.escapeSingleApex(docsearch.get("archive_label"))%>\');"><span id="span_scheda_<%=docsearch.get("id_record")%>"><%=JsSolver.escapeSingleApex(simpleTitle).replaceAll("\n","\\\\\n")%></span></a>',
																											titleForTree : '<img width="12" height="12" src="img/archive_img/archive_<%=request.getParameter("idArchive")%>.gif" border="0"/>&nbsp;<%=JsSolver.escapeSingleApex(simpleTitle).replaceAll("\n","\\\\\n")%>',
																											frame: true,
																											collapsible: true,
																											draggable:true,
																											html:'<div id="scheda_<%=docsearch.get("id_record")%>"></div>',
																											panel_type:'archive_panel',																																													
																											tools:[
																													{
																												        id:'close',
																												        handler: function(e, target, panel){
																															try{
																															    var children =  Ext.getCmp('treePanelSearchBuilder').getRootNode().childNodes;        					
																									        					for (var i = 0;i<children.length;i++){
																																	var currentNode = children[i];
																																	if(currentNode.attributes.idRef=='panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.get("id_record")%>'){
																																		Ext.getCmp('treePanelSearchBuilder').getRootNode().removeChild(currentNode,true);
																																		updateIndexPanel();
																																	}
																																}
																													        }catch(ex){
																															}
																												            panel.ownerCt.remove(panel, true);
																												        }
																												    }
																												   ],
																										    listeners: { 
																										      			  'afterrender': function(elemento){	
																									      			  			try{																							      			  			
																											      					loadDocumentForSearchResults('<%=docsearch.get("id_record")%>','<%=request.getParameter("idArchive")%>','scheda_<%=docsearch.get("id_record")%>','panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.get("id_record")%>','<%=request.getAttribute("wordsToHighlight")%>');																															
																									      			  			}catch(e){
																										      			  		}
																											      			} 
																										      			}
																										});
									Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').add(panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.get("id_record")%>);									
									Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').doLayout();						
								}else{
									var panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.get("id_record")%>_dis =  new Ext.Panel({
										id : 'panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.get("id_record")%>_dis',																				
										title : '<span class="search_result_float_right_date"><%=date%></span><img width="12" height="12" src="img/archive_img/archive_<%=request.getParameter("idArchive")%>.gif" border="0"/>&nbsp;<%=JsSolver.escapeSingleApex(simpleTitle).replaceAll("\n","\\\\\n")%>',
										titleForTree : '<img width="12" height="12" src="img/archive_img/archive_<%=request.getParameter("idArchive")%>.gif" border="0"/>&nbsp;<%=JsSolver.escapeSingleApex(simpleTitle).replaceAll("\n","\\\\\n")%>',
										frame: true,
										collapsible: true,
										draggable:false,
										disabled:true,
										html:'<div id="scheda_<%=docsearch.get("id_record")%>"></div>',
										panel_type:'archive_panel',																																													
										tools:[
												{
											        id:'close',
											        handler: function(e, target, panel){
											            panel.ownerCt.remove(panel, true);
											        }
											    }
											   ],
									    listeners: { 
									      			  'afterrender': function(elemento){
								      			  			try{
										      					loadDocumentForSearchResults('<%=docsearch.get("id_record")%>','<%=request.getParameter("idArchive")%>','scheda_<%=docsearch.get("id_record")%>','panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.get("id_record")%>_dis','<%=request.getAttribute("wordsToHighlight")%>');																															
									      			  		}catch(e){
									      			  		}
											      		} 
									      			}
									});
									Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').add(panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.get("id_record")%>_dis);																
									Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').doLayout();
								}			
							<%} catch (Exception a) {
								a.printStackTrace();
							}
						}
					}
					%>

<%}%>

</script>