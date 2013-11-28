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


<script type="text/javascript">

<%
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat formatterDests = new SimpleDateFormat("d MMM. yyyy", Locale.ITALY);
%>
<%
	if (request.getAttribute("result") != null) {
		TitleManager titleManager = (TitleManager) request.getAttribute("titleManager");
				Records docsearch = (Records) request.getAttribute("result");
				try {
					HashMap<String, String[]> parsedTitle = titleManager.parseTitle(docsearch.getTitle(), new Integer(request.getParameter("idArchive")));
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
							date = parsedTitle.get("data")[0].trim();
							dateNormal = date;
							Date data = formatter.parse(date);
							date = formatterDests.format(data);
						} catch (Exception e) {
						}
						date = date.replaceAll(" ", "&nbsp;");
						title = title.replaceAll("&amp;apos;", "'");
						title = title.replaceAll("&apos;", "'");
						%>
						if(Ext.getCmp('east-panel_searcBuilder_dest').get('panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>')==null || Ext.getCmp('east-panel_searcBuilder_dest').get('panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>')==undefined){
						var panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%> =  new Ext.Panel({
																									id : 'panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>',																				
																									title : '<img width="12" height="12" src="img/archive_img/archive_<%=request.getParameter("idArchive")%>.gif" border="0"/>&nbsp;<a href="#" class="linkArchiveTitle" onclick="return openRecord(\'<%=docsearch.getIdRecord()%>\',true,\'<%=request.getParameter("idArchive")%>\',\'<%=JsSolver.escapeSingleApex(docsearch.getArchives().getLabel())%>\');"><%=JsSolver.escapeSingleApex(simpleTitle).replaceAll("\n","\\\\\n")%></a>',
																									frame: true,
																									collapsible: true,
																									draggable:true,
																									html:'<div id="scheda_<%=docsearch.getIdRecord()%>"></div>',
																									panel_type:'archive_panel',																																													
																									tools:[
																											{
																										        id:'close',
																										        handler: function(e, target, panel){
																													try{
																													    var children =  Ext.getCmp('treePanelSearchBuilder').getRootNode().childNodes;        					
																							        					for (var i = 0;i<children.length;i++){
																															var currentNode = children[i];
																															if(currentNode.attributes.idRef=='panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>'){
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
																						      			  					//this.setPosition(0,0);
																								      				loadDocumentForSearchResults('<%=docsearch.getIdRecord()%>','<%=request.getParameter("idArchive")%>','scheda_<%=docsearch.getIdRecord()%>','panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>','<%=request.getParameter("wordsToHighlight")%>');																															
																								      			   } 
																								      			}
																								});
							//Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').add(panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>);													
							Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').insert(<%=request.getParameter("panel_position")%>, panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>);
							Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').remove('panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>_before',true);
							Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').doLayout();
							count_panel++;							
						}else{
							var panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>_dis =  new Ext.Panel({
								id : 'panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>_dis',																				
								title : '<img width="12" height="12" src="img/archive_img/archive_<%=request.getParameter("idArchive")%>.gif" border="0"/>&nbsp;<%=JsSolver.escapeSingleApex(simpleTitle).replaceAll("\n","\\\\\n")%>',
								frame: true,
								collapsible: true,
								draggable:false,
								disabled:true,
								html:'<div id="scheda_<%=docsearch.getIdRecord()%>"></div>',
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
					      			  					//this.setPosition(0,0);
							      				loadDocumentForSearchResults('<%=docsearch.getIdRecord()%>','<%=request.getParameter("idArchive")%>','scheda_<%=docsearch.getIdRecord()%>','panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>_dis','<%=request.getParameter("wordsToHighlight")%>');																															
							      			   } 
							      			}
							});
							//Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').add(panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>_dis);						
							Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').insert(<%=request.getParameter("panel_position")%>, panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>_dis);
							Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').remove('panel_archive_<%=request.getParameter("idArchive")%>_record_<%=docsearch.getIdRecord()%>_before',true);							
							Ext.getCmp('center-panel_searcBuilder_archive_<%=request.getParameter("idArchive")%>').doLayout();
							count_panel++;
						}
						if(count_panel==<%=request.getParameter("totDoc")%>){
							closeLoading();
							//loadDocumentData();
						}
						<%
				} catch (Exception a) {
					a.printStackTrace();
				}			
		%>
		
		<%
	}
%>
</script>
