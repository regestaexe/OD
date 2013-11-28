<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="com.openDams.bean.Archives"%>
<%@page import="com.openDams.bean.ArchiveIdentity"%>
<%@page import="com.openDams.security.UserDetails"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="org.springframework.security.core.context.SecurityContext"%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="java.util.Collection"%>
<%@page import="org.springframework.security.core.GrantedAuthority"%>
<%@page import="com.openDams.security.RoleTester"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Departments"%>
<%@page import="com.openDams.bean.Companies"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.openDams.bean.ArchiveType"%>
<%@page import="com.openDams.search.configuration.Element"%>
<%@page import="com.openDams.security.ArchiveRoleTester"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" >
<head>
<title>openDams</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="img/sqlxdicon.ico" type="image/png" />
<link rel="shortcut icon" href="img/sqlxdicon.ico" type="image/png" />
<link href="css/StyleSheet.css" rel="stylesheet" type="text/css" />
<link href="css/confirm.css" rel="stylesheet" type="text/css" />
<link href="css/jquery.contextMenu.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="css/resources/css/ext-all-notheme.css" />
<link rel="stylesheet" type="text/css" id="theme_color" href="css/resources/css/xtheme-blue.css" />
<link href="css/style.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="js/ext-js-3.4.0/ux/css/RowEditor.css" /> 
<link rel="stylesheet" href="js/codemirror/lib/codemirror.css" />
<link rel="stylesheet" href="js/codemirror/mode/xml/xml.css"/>
<link href="css/style_sky_blue.css" id="theme_color_tree" rel="stylesheet" type="text/css" />
<style type="text/css">
.x-form-field-wrap .x-form-voc-trigger {
    background:transparent url(img/voc-blue.png) no-repeat 0 0 ;
    width:20px;
    cursor:pointer; 
}
.x-form-field-wrap .x-form-lookup-trigger  {
    background:transparent url(img/lookUp.png) no-repeat 0 0 ;
    width:20px;
    cursor:pointer; 
}

.x-panel-header-text p {display:inline;}
.x-tree-node-anchor p {display:inline;}
.voc-icon { background-image: url(img/voc-red.png) 0 6px no-repeat !important; }
.htmlToClean{border:1px solid #8db2e3;padding:10px;font-size:13px;}
.htmlToCleanPar{padding-bottom:20px;}
.highlight_class{background-color: #FFFF66;-moz-border-radius:15px;padding-left:2px;padding-right:2px;}
</style>
<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.6.custom.min.js"></script>
<script src="js/tiny_mce/jquery.tinymce.js" type="text/javascript"></script>

<script src="js/tiny_mce/jquery.tinymce.js" type="text/javascript"></script>
<script src="js/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script src="js/tiny_mce/langs/it.js" type="text/javascript"></script>
<script src="js/tiny_mce/themes/advanced/editor_template.js" type="text/javascript"></script>
<script src="js/tiny_mce/plugins/table/editor_plugin.js" type="text/javascript"></script>
<script src="js/tiny_mce/plugins/searchreplace/editor_plugin.js" type="text/javascript"></script>
<script src="js/tiny_mce/plugins/fullscreen/editor_plugin.js" type="text/javascript"></script>
<script src="js/tiny_mce/plugins/style/editor_plugin.js" type="text/javascript"></script>
<script src="js/tiny_mce/plugins/paste/editor_plugin.js" type="text/javascript"></script>
<script src="js/tiny_mce/plugins/inlinepopups/editor_plugin.js" type="text/javascript"></script>
<script src="js/tiny_mce/plugins/nonbreaking/editor_plugin.js" type="text/javascript"></script>
<script src="js/tiny_mce/plugins/xhtmlxtras/editor_plugin.js" type="text/javascript"></script>
<script src="js/tiny_mce/plugins/advlist/editor_plugin.js" type="text/javascript"></script>
<script src="js/tiny_mce/plugins/save/editor_plugin.js" type="text/javascript"></script>
<script src="js/tiny_mce/plugins/template/editor_plugin.js" type="text/javascript"></script>

<script src="js/jquery.contextMenu.js" type="text/javascript"></script>
<%if(request.getParameter("ie")!=null && request.getParameter("ie").equals("true")){%>
<script type="text/javascript" src="js/ext-js-3.4.0/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="js/ext-js-3.4.0/ext-all.js"></script>
<script type="text/javascript" src="js/ext-js-3.4.0/ux/SearchField.js"></script>
<script type="text/javascript" src="js/ext-js-3.4.0/ux/RowExpander.js"></script>
<script type="text/javascript" src="js/ext-js-3.4.0/ux/Printer.js"></script>
<script type="text/javascript" src="js/ext-js-3.4.0/ux/fileuploadfield/FileUploadField.js"></script>	
<script type="text/javascript" src="js/ext-js-3.4.0/locale/ext-lang-it.js"></script>
<%}else{%>
<script type="text/javascript" src="js/ext-js/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="js/ext-js/ext-all.js"></script>
<script type="text/javascript" src="js/ext-js/ux/SearchField.js"></script>
<script type="text/javascript" src="js/ext-js/ux/RowExpander.js"></script>
<script type="text/javascript" src="js/ext-js/ux/Printer.js"></script>
<script type="text/javascript" src="js/ext-js/ux/fileuploadfield/FileUploadField.js"></script>
<script type="text/javascript" src="js/ext-js/locale/ext-lang-it.js"></script>
<%}%>
<script type="text/javascript" src="js/codemirror.js"></script>
<script type="text/javascript" src="js/jpath.js"></script>
<script type="text/javascript" src="js/application.js"></script>
<%@include file="../locale.jsp"%>
<%
ArrayList<Archives> documentalList = new ArrayList<Archives>();
ArrayList<Archives> skosList = new ArrayList<Archives>();
String requestArchives = "";
Archives searchArchive = null;
if(request.getAttribute("archiveList")!=null){ 
	   List<Object> archives = (List)request.getAttribute("archiveList");
	   Object[] archiveList = archives.toArray();
	   for(int i=0;i<archiveList.length;i++){
		   Archives archive = (Archives)archiveList[i];
		   if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.THESAURUS){
			   skosList.add(archive);
		   }else{
			   documentalList.add(archive);
			   requestArchives+=archive.getIdArchive()+";";
		   }
   	   }
}
if(request.getAttribute("searchArchive")!=null){
	searchArchive = (Archives)request.getAttribute("searchArchive");
}
Archives archives = (Archives) request.getAttribute("archives");
UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
boolean editOn = ArchiveRoleTester.testEditing(user.getId(),searchArchive.getIdArchive().intValue());
%>
<script type="text/javascript">
var globalOpt={};
globalOpt.document = {};
loadScripts({base:"application",scripts:['ds','voc','lookUp','loading','utils','search','insert','jsonInjector','tree','print','mce']});
var winDocumentManager = null;
var save_search_win = null;
Ext.ux.Printer.PanelRenderer = Ext.extend(Ext.ux.Printer.BaseRenderer, {
 generateBody: function(panel) {
   return String.format("<div class='x-panel-print'>{0}</div>", panel.body.dom.innerHTML);
 }
});
Ext.ux.Printer.registerRenderer("panel", Ext.ux.Printer.PanelRenderer);




<%if(request.getAttribute("archiveList")!=null){ 
	   for(int x=0;x<documentalList.size();x++){
		   Archives archiveDocumental =  documentalList.get(x); 
	   		%>      
			   	  var panelTH_archive_<%=archiveDocumental.getIdArchive()%>_ToolBar = new Ext.Toolbar({
						id:'panelTH_archive_<%=archiveDocumental.getIdArchive()%>_ToolBar',
						height:'23',
						items:[
						       'Cerca un concetto in : ',    		   
						       <%for(int i=0;i<skosList.size();i++){
									 Archives archive =  skosList.get(i);%>
								{
						            xtype: 'tbbutton',
						            text: '<%=archive.getDescription()%>',
						            handler: function(){
					   						openSearchConcept('<%=archive.getIdArchive()%>');
			                 		}
						        }<%if(i<skosList.size()-1){%>,{
						            xtype: 'tbseparator'
						        },<%}%>
						        <%}%>			       
				        ]
					});
			   		var panelTH_archive_<%=archiveDocumental.getIdArchive()%> = new Ext.Panel({			   				
					        id:'thPanel_archive_<%=archiveDocumental.getIdArchive()%>',
					        border:true,
					        anchor: '95%',
					        autoScroll: true,
					        tbar: panelTH_archive_<%=archiveDocumental.getIdArchive()%>_ToolBar,
						    padding:5
					});
					var commonSearchFields_archive_<%=archiveDocumental.getIdArchive()%> = {	        
						        xtype: 'fieldset',
						        id:'id_commonSearchFields_archive_<%=archiveDocumental.getIdArchive()%>',
						        bodyStyle:'padding:5px;',
						        border:false,
						        margins : '5 5 5 5',
						        anchor: '95%',
						        items: [
											<%
											ArrayList<Object>  searchElements= (ArrayList<Object>)request.getAttribute("searchElements_"+archiveDocumental.getIdArchive());
											for (int i = 0; i < searchElements.size(); i++) {
												Element element = (Element) searchElements.get(i);
												if(element.getPages().equalsIgnoreCase("all") || element.getPages().indexOf("advanced_search")!=-1){
												%>												
												<%//=element.getCdata_section().replaceAll("boxLabel: 'Rilevanza', name: 'order_by', inputValue: '',sort_type:'',checked: true","boxLabel: 'Rilevanza', name: 'order_by', inputValue: '',sort_type:''").replaceAll("boxLabel: 'Data\\(de\\.\\)', name: 'order_by', inputValue: 'DATE',sort_type:'integer'","boxLabel: 'Data\\(de\\.\\)', name: 'order_by', inputValue: 'DATE',sort_type:'integer',checked: true"),%>
												<%=element.getCdata_section().replaceAll("xtype: 'vocfield',","xtype: 'singleVocfield',archives:'"+archiveDocumental.getIdArchive()+";',")%>,
												<%}
											}
											%>
											panelTH_archive_<%=archiveDocumental.getIdArchive()%>
											,{
												xtype: 'checkbox',
						                        hidden: true,
						                        name: 'ck-multi-search-archive_<%=archiveDocumental.getIdArchive()%>',
												id: 'ck-multi-search-archive_<%=archiveDocumental.getIdArchive()%>', 
												inputValue: <%=archiveDocumental.getIdArchive()%>,
												checked: true			                        
						                    }
						        		]
					};
<%		}%>
        var panelTH_allToolBar = new Ext.Toolbar({
			id:'panelTH_allToolBar',
			height:'23',
			items:[
			       'Cerca un concetto in : ',    		   
			       <%for(int i=0;i<skosList.size();i++){
						 Archives archive =  skosList.get(i);%>
					{
			            xtype: 'tbbutton',
			            text: '<%=archive.getDescription()%>',
			            handler: function(){
		   						openSearchConcept('<%=archive.getIdArchive()%>');
                 		}
			        }<%if(i<skosList.size()-1){%>,{
			            xtype: 'tbseparator'
			        },<%}%>
			        <%}%>			       
	        ]
		});
        
		var panelTH_all =  new Ext.Panel({
		        id:'thPanel_archive_all',
			    border:true,
			    anchor: '95%',
			    padding:5,
			    tbar: panelTH_allToolBar,
			    autoScroll: true,
			    items:[]
		});
		var commonSearchFields_archive_all = {	        
		        xtype: 'fieldset',
		        id:'id_commonSearchFields_all',
		        bodyStyle:'padding:5px;',
		        border:false,
		        margins : '5 5 5 5',
		        anchor: '95%',
		        items: [
							<%
							ArrayList<Object>  searchElements= (ArrayList<Object>)request.getAttribute("searchElements");
							for (int i = 0; i < searchElements.size(); i++) {
								Element element = (Element) searchElements.get(i);
								if(element.getPages().equalsIgnoreCase("all") || element.getPages().indexOf("advanced_search")!=-1){
								%>
										<%//=element.getCdata_section().replaceAll("xtype: 'vocfield',","xtype: 'singleVocfield',archives:'"+requestArchives+"',").replaceAll("boxLabel: 'Rilevanza', name: 'order_by', inputValue: '',sort_type:'',checked: true","boxLabel: 'Rilevanza', name: 'order_by', inputValue: '',sort_type:''").replaceAll("boxLabel: 'Data\\(de\\.\\)', name: 'order_by', inputValue: 'DATE',sort_type:'integer'","boxLabel: 'Data\\(de\\.\\)', name: 'order_by', inputValue: 'DATE',sort_type:'integer',checked: true"),%>
										<%=element.getCdata_section().replaceAll("xtype: 'vocfield',","xtype: 'singleVocfield',archives:'"+requestArchives+"',")%>,
								<%}
							}
							%>
							panelTH_all
							<%for(int x=0;x<documentalList.size();x++){
								   Archives archiveDocumental =  documentalList.get(x);%>							  
									,{                                                                                              
				                        xtype: 'checkbox',
				                        hidden: true,
				                        name: 'ck-multi-search-archive_all_<%=archiveDocumental.getIdArchive()%>',
										id: 'ck-multi-search-archive_all_<%=archiveDocumental.getIdArchive()%>', 
										inputValue: <%=archiveDocumental.getIdArchive()%>,
										checked: true			                        
					                  }					
							<%}%>	    
							
		        		]
		};
		var tabArchiveAll = new Ext.Panel({
			title:'IMPOSTA LA RICERCA',
			id:'search_builder_archive_all',
			region: 'center',
			layout: 'border',
			border : false,		
			items:[
								new Ext.Panel({
								    region: 'west',
								    id: 'west-panel_searcBuilder_archive_all',
								    title: '<span class="tree_top_title">Ricerche da tesaurus</span>',
								    split: true,
								    width: 400,
								    minSize: 175,
								    maxSize: 600,
								    collapsible: true,
								    layout:'table',
								    layoutConfig: {columns:1},
								    height:'100%',
								    items:[ 							
										    new Ext.Panel({
										    	id : 'accordionpanelSearch_archive_all',						    	
										    	layout:'accordion',
												layoutConfig:{animate:true},
												width: 400,
												height:515,
												minSize: 175,
											    maxSize: 600,    
										    	margins: '0 0 0 0',
										    	layoutConfig: {
										            animate: true,
										            activeOnTop: false
										        },													    	
										    	items:[
													 <%for(int i=0;i<skosList.size();i++){
															 Archives archive =  skosList.get(i);%>
															 {
																	id : 'accordionpanelsearch_archive_all_<%=archive.getIdArchive()%>',
																	title:'<%=archive.getDescription()%>',
																	border: false,
																	autoScroll: true,
												                    iconCls: 'navigation_img',												                  
												                    collapsed:true,
												                    tbar: ['->',{
												                           xtype: 'checkbox',
												                           id:'checkboxMyDip_archive_all_<%=archive.getIdArchive()%>',
												                           name: 'myDip',
												                           boxLabel: 'mio dipartimento',
												                           checked: true,
												                           listeners:{
																					'check':function(ck,checked){
												                        	   			var idRecord = getInSchemeId('<%=archive.getIdArchive()%>');
												                        	   			if(idRecord!=null && idRecord!=undefined && idRecord!=''){
																							   idRecord='&idRecord='+idRecord;
																						}
																						if(!checked){
																							idRecord='';
																						}
																						Ext.getCmp('accordionpanelsearch_archive_all_<%=archive.getIdArchive()%>').load({
							    															  url:'searchBuilderTree.html?idArchive=<%=archive.getIdArchive()%>'+idRecord,
							    											         		  discardUrl: false,
							    											         		  nocache: true,
							    											         		  text: 'caricamento in corso...',
							    											         		  timeout: 30,
							    											         		  scripts: true								  
							    														  });
																					}
														                   }												                          
										                   				}],
											                    	listeners:{
											    							   'expand':function(){
																						if(globalOpt.archive_<%=archive.getIdArchive()%>_loaded==undefined){
																							   var idRecord = getInSchemeId('<%=archive.getIdArchive()%>');
																							   if(idRecord!=null && idRecord!=undefined && idRecord!=''){
																								   idRecord='&idRecord='+idRecord;
																							   }
								    						   								   globalOpt.archive_<%=archive.getIdArchive()%>_loaded=true;
									    														   this.load({
									    															  url:'searchBuilderTree.html?idArchive=<%=archive.getIdArchive()%>'+idRecord,
									    											         		  discardUrl: false,
									    											         		  nocache: true,
									    											         		  text: 'caricamento in corso...',
									    											         		  timeout: 30,
									    											         		  scripts: true								  
									    														   });
																						}
									    										}												                    		
									    							 }      											
																}, 
														<%}%>
														{
															id : 'accordionpanelsearch_archive_all_activate_tab',
															title:'Attiva ricerche su singolo archivio',
															border: false,
															autoScroll: true,
										                    iconCls: 'results_img',												                  
										                    collapsed:true,
									                    	items:[
																	{	        
																	    xtype: 'fieldset',																	    
																	    autoHeight: true,
																	    collapsed: false,
																	    collapsible: false,
																	    items: [
																					{
																					    xtype: 'checkboxgroup',
																					    name: 'ck_archives',
																					    columns: 2,
																					    id:'ck_archives',
																						fieldLabel: 'Archivi',
																						items:[
																							<%if(request.getAttribute("archiveList")!=null){ 
																								   for(int x=0;x<documentalList.size();x++){
																									   Archives archiveDocumental =  documentalList.get(x); %>
																								 			{
																											<%-- boxLabel: '<img width="12" height="12" src="img/archive_img/archive_<%=archiveDocumental.getIdArchive()%>.gif" border="0"/>&nbsp;<%=archiveDocumental.getLabel()%>', --%> 

																								 				boxLabel: '<%=archiveDocumental.getLabel()%>', 
																									 			name: 'ck_archiveactivate_tab_<%=archiveDocumental.getIdArchive()%>',
																									 			id: 'ck_archiveactivate_tab_id_<%=archiveDocumental.getIdArchive()%>',
																									 			idArchive:'<%=archiveDocumental.getIdArchive()%>',
																								 				listeners: {
																								     			'check': function(ctl, val){ 																		
																						     								if(val){
																						     									showArchiveTabFromCK('<%=archiveDocumental.getIdArchive()%>');
																						     								}else{
																						     									 hideArchiveTab('<%=archiveDocumental.getIdArchive()%>');
																							     							}																			     								
																								          			  }
																						        				}	
																										 	}
																								 			<%if(x<documentalList.size()-1){%>,<%}%>
																					         <%	   }
																							   }%>              
																					         ]
																					 }
																	            ]
																	}											                    																		
												                  ]    											
														}																				
														] 
											})											
									]
								}),
								 new Ext.Panel({
					              id: 'center-panel_searcBuilder_container_archive_all',
					              region:'center',
					              layout:'border',
					              border : true,
					              padding: 5,
					              margins : '5 5 5 5',
					              autoScroll: true,					              														             
								  items : [
											   	new Ext.FormPanel({
													id : 'search_panel_archive_all',
													title:'<span class="tree_top_title">Campi di Ricerca</span>',
								                    border:false,
								                    region:'north',
								                    split: true,
								                    height:280,
								                    border : true,
										            padding: 5,
										            margins : '5 5 5 5',
								                    autoScroll:true,
								                    iconCls: 'results_img',
											        items: [commonSearchFields_archive_all],
											        collapsible:true,
											        collapsed:false,
											        keys: [
											               { key: [Ext.EventObject.ENTER], handler: function() {
											            	   		executeAllSearch();
											                   }
											               }
											           ], 											    
												    buttons: [
															   {	        	
													            text: 'Cerca',
													            disabled:false,
													            handler: function(){
																   				executeAllSearch();
														                 }
											        		   },
											        		   {	        	
													            text: 'Ripristina',
													            disabled:false,
													            handler: function(){
											        			   			Ext.getCmp('search_panel_archive_all').getForm().reset();
											        			   			panelTH_all.removeAll(true);
											        			   			hideAllArchiveTab();
											        			   			Ext.getCmp('center-panel_searcBuilder_archive_all').update("");
														           		 }
											        		   }	
											        		]
												}),
												new Ext.Panel({
										              id: 'center-panel_searcBuilder_archive_all',
										              title:'<span class="tree_top_title">Risultati ricerca</span>',
										              region:'center',
										              border : true,										              
										              padding: 5,
										              margins : '5 5 5 5',
										              autoScroll: true,														             
													  items : []
										            })
										]
					            })
						]
				});    
<%}%>

Ext.onReady(function(){
	var viewportSearchBuilder = new Ext.Viewport({
        layout: 'border',
        border : false,
        forceLayout : true,
        id:'search-builder', 
        items: [
				new Ext.TabPanel({
				    region: 'center',
				    id:'tabPanel_searcBuilder',
				    deferredRender: false,
				    collapsible: false,
				    enableTabScroll:true,
				    activeTab: 0,			
				    items: [
						tabArchiveAll,    
						<%if(request.getAttribute("archiveList")!=null){ 
							   for(int x=0;x<documentalList.size();x++){
								   Archives archiveDocumental =  documentalList.get(x); 
							   		%>
								//inizio pannello ricerca
								new Ext.Panel({
												title:'<img width="12" height="12" src="img/archive_img/archive_<%=archiveDocumental.getIdArchive()%>.gif" border="0"/>&nbsp;<%=archiveDocumental.getLabel()%><a href="#" class="simulateCloseTabButton" onclick="hideArchiveTab(\'<%=archiveDocumental.getIdArchive()%>\');"></a>',
												id:'search_builder_archive_<%=archiveDocumental.getIdArchive()%>',
												region: 'center',
												layout: 'border',
												border : false,
												closable:true,
												items:[
															new Ext.Panel({
																	    region: 'west',
																	    id: 'west-panel_searcBuilder_archive_<%=archiveDocumental.getIdArchive()%>',
																	    title: '<span class="tree_top_title">Ricerche da tesaurus</span>',
																	    split: true,
																	    width: 400,
																	    minSize: 175,
																	    maxSize: 600,
																	    collapsible: true,
																	    collapsed:true,
																	    layout:'table',
																	    layoutConfig: {columns:1},
																	    height:'100%',
																	    items:[ 							
																			    {
																			    	id : 'accordionpanelSearch_archive_<%=archiveDocumental.getIdArchive()%>',						    	
																			    	layout:'accordion',
																					layoutConfig:{animate:true},
																					width: 400,
																					height:515,
																					minSize: 175,
																				    maxSize: 600,    
																			    	margins: '0 0 0 0',
																			    	layoutConfig: {
																			            animate: true,
																			            activeOnTop: false
																			        },														    	
																			    	items:[
																						 <%for(int i=0;i<skosList.size();i++){
																								 Archives archive =  skosList.get(i);%>
																								 {
																										id : 'accordionpanelsearch_archive_<%=archiveDocumental.getIdArchive()%>_<%=archive.getIdArchive()%>',
																										title:'<%=archive.getDescription()%>',
																										border: false,
																										autoScroll: true,
																					                    iconCls: 'navigation_img',
																					                    collapsed:true,
																				                    	listeners:{
																				    							   'expand':function(){
																															if(globalOpt.archive_<%=archiveDocumental.getIdArchive()%>_<%=archive.getIdArchive()%>_loaded==undefined){
																	    						   								   globalOpt.archive_<%=archiveDocumental.getIdArchive()%>_<%=archive.getIdArchive()%>_loaded=true;
																		    														   this.load({
																		    															  url:'searchBuilderTree.html?idArchive=<%=archive.getIdArchive()%>',
																		    											         		  discardUrl: false,
																		    											         		  nocache: true,
																		    											         		  text: 'caricamento in corso...',
																		    											         		  timeout: 30,
																		    											         		  scripts: true								  
																		    														   });
																															}
																		    										}
																		    							 }      											
																									}<%if(i<(skosList.size()-1)){%>,<%}%> 
																							<%}%>																				
																							]
																				}																				
																		]
																		
																	}),
																	new Ext.Panel({
															              id: 'center-panel_searcBuilder_container_archive_<%=archiveDocumental.getIdArchive()%>',
															              region:'center',
															              layout:'border',
															              border : true,
															              padding: 5,
															              margins : '5 5 5 5',
															              autoScroll: true,														             
																		  items : [
																				new Ext.FormPanel({
																					id : 'search_panel_archive_<%=archiveDocumental.getIdArchive()%>',
																					title:'<span class="tree_top_title">Campi di Ricerca</span>',
																                    border:false,
																                    region:'north',
																                    split: true,
																                    height:280,
																                    border : true,
																		           padding: 5,
																		            margins : '5 5 5 5',
																                    autoScroll:true,
																                    iconCls: 'results_img',
																                    items: [commonSearchFields_archive_<%=archiveDocumental.getIdArchive()%>],
																			        collapsible:true,
																			        collapsed:true,
																			        keys: [
																			               { key: [Ext.EventObject.ENTER], handler: function() {
																			            	   		executeSingleSearch('<%=archiveDocumental.getIdArchive()%>');
																			                   }
																			               }
																			           ], 	
																			        buttons: [
																											   {	        	
																										            text: 'Cerca',
																										            disabled:false,
																										            handler: function(){
																												   				executeSingleSearch('<%=archiveDocumental.getIdArchive()%>');  
																											            }
																								        		   },
																								        		   {	        	
																										            text: 'Ripristina',
																										            disabled:false,
																										            handler: function(){
																								        			   				Ext.getCmp('search_panel_archive_<%=archiveDocumental.getIdArchive()%>').getForm().reset();
																								        			   				panelTH_archive_<%=archiveDocumental.getIdArchive()%>.removeAll(true);
																													          }
																								        		   } 		
																								        		]																				    
																				}), 
																				 new Ext.Panel({
																	              id: 'center-panel_searcBuilder_archive_<%=archiveDocumental.getIdArchive()%>',
																	              title:'<span class="tree_top_title">Risultati ricerca</span> (<span id="nResults_archive_<%=archiveDocumental.getIdArchive()%>">0</span> documenti) <span id="nResults_archive_page_<%=archiveDocumental.getIdArchive()%>"></span>',
																	              region: 'center',
																	              border : true,
																	              ddGroup : 'searcBuilderDDGroup',
																	              padding: 5,
																	              autoScroll: true,
																	              defaults : {
																							style : 'margin-top:15px;margin-left:5px;margin-right:5px;',
																							draggable : {
																							insertProxy : false,
																							onDrag : function(e) {
																									var pel = this.proxy.getEl();
																									this.x = pel.getLeft(true);
																									this.y = pel.getTop(true);
																									var s = this.panel.getEl().shadow;
																									if (s) {
																										s.realign(this.x, this.y, pel.getWidth(), pel.getHeight());
																									}
																							},
																							endDrag : function(e) {
																							        /*var parentPosition = this.panel.ownerCt.getPosition();
																							        if(this.panel.ownerCt.id=='tabDocumentManager'){
																							        	this.panel.el.setX(parentPosition[0]+5);
																								    }else{
																								    	this.panel.el.setX(parentPosition[0]+12);
																									}																			        
																				                    this.panel.el.setY(this.y);*/																	                 																																																																										
																							}
																							
																						}
																					},
																					items : [],
																					tbar: new Ext.Toolbar({
																		             	   items : [
																									    '->',
																									    new Ext.Button({
																				        					        text    : 'Aggiungi tutti i risultati visualizzati',
																				        					        handler : function(btn) {
																									    	              var results = Ext.getCmp('center-panel_searcBuilder_archive_<%=archiveDocumental.getIdArchive()%>').items;
																														  var count_results = results.length;
																														  var count_results_for_loading = results.length;																																													    	              
																									    	              while (count_results!=0) {
																										    	          	    var comp = results.get(0);																										    	          	   
																										    	          	    if(comp.id.endsWith("_dis")){
																										    	          	    	Ext.getCmp('center-panel_searcBuilder_archive_<%=archiveDocumental.getIdArchive()%>').remove(comp.id,true);
																												    	        }else{
																												    	        	 comp.collapse();
																												    	        	 Ext.getCmp('east-panel_searcBuilder_dest').add(comp);
																													    	    }																										    	          	   																					    	          	   
																										    	          	    count_results--;		    	          	   
																									    	          	  }
																									    	              Ext.getCmp('east-panel_searcBuilder_dest').doLayout();	
																									    	              if(count_results_for_loading>50){
																									    	            	  closeLoadingS();
																														  }	
																									    	          	  if(Ext.getCmp('east-panel_searcBuilder').collapsed){
																									    	          			Ext.getCmp('east-panel_searcBuilder').expand();
																											    	      }																						    	              
																				        					        }
																        					    		}),'',new Ext.Button({
																				        					        text    : 'Svuota visualizzati',
																				        					        handler : function(btn) {
																		        					    				Ext.getCmp('center-panel_searcBuilder_archive_<%=archiveDocumental.getIdArchive()%>').removeAll(true);
																				        					        }
														        					    				}) 
																		                         ]
																		                     }),
																                     bbar: new Ext.Toolbar({
																											id:'center-panel_searcBuilder_archive_bbar_<%=archiveDocumental.getIdArchive()%>',
																											height:'20',
																											autoScroll:true
													                     									}),       
																                     listeners: {
																		      				'add': function(elemento){
																			      				if(Ext.getCmp('center-panel_searcBuilder_archive_<%=archiveDocumental.getIdArchive()%>').items!=undefined){
																				      				//$("#nResults_archive_<%=archiveDocumental.getIdArchive()%>").html(Ext.getCmp('center-panel_searcBuilder_archive_<%=archiveDocumental.getIdArchive()%>').items.getCount());								      				      
																					      		}							      								
																		      										
																		      				},
																		      				'remove': function(elemento){					
																		      					if(Ext.getCmp('center-panel_searcBuilder_archive_<%=archiveDocumental.getIdArchive()%>').items!=undefined){
																		      						//$("#nResults_archive_<%=archiveDocumental.getIdArchive()%>").html(Ext.getCmp('center-panel_searcBuilder_archive_<%=archiveDocumental.getIdArchive()%>').items.getCount()); 
																					      		}   					
																		      				}   
																		      		}
																	            })
																	]})
															]
													})
								//fine pannello ricerca
								<%if(x<documentalList.size()-1){%>,<%}%>
								<%}%>
							   
							   <%}%>
						    ],
						    listeners: {
										'afterrender': function(elemento){											
											var tab_archive_panels = Ext.getCmp('tabPanel_searcBuilder').items;
											for (i=0; i<tab_archive_panels.getCount(); ++i) {
												var comp = tab_archive_panels.get(i);
												if(i>0)
													Ext.getCmp('tabPanel_searcBuilder').hideTabStripItem(comp.id);
											}   						      				
					  					}
				    		}
	              }),

	              new Ext.Panel({
	            	    id: 'east-panel_searcBuilder',
		            	title:'<span class="tree_top_title">Costruzione documento (<span id="tot_search_builder_documents">0</span> documenti)</span>',
		                region: 'east',
		                border : true,
		                split: true,
					    width: 400,
					    minSize: 200,
					    collapsible: true,
					    collapsed:false,
					    layoutConfig: {columns:1},
					    defaults: {frame:true},
					    layout:'border',
					    items:[
					           new Ext.Panel({
					            	id: 'east-panel_searcBuilder_dest',
								    /*margins: '0 0 0 0',*/
								    html:'',
								    ddGroup : 'searcBuilderDDGroup',
								    frame: true,
								    autoScroll: true,
								    region : 'center',
								    defaults : {
										style : 'margin-top:5px;margin-left:5px;margin-right:5px;'
									},
								    listeners: { 
							      			  	'afterrender': function(elemento){
							      					var postPanelDropTarget = generateMyDropTarget(elemento);
							      					if(this.items==undefined || this.items.length==0){
							      						Ext.getCmp('east-panel_searcBuilder').collapse();
								      				}							      						      				
							      				},
							      				'add': function(elemento){
								      				if(Ext.getCmp('east-panel_searcBuilder_dest').items!=undefined){
									      				$("#tot_search_builder_documents").html(Ext.getCmp('east-panel_searcBuilder_dest').items.getCount());								      				      
										      		}							      								
							      										
							      				},
							      				'remove': function(elemento){					
							      					if(Ext.getCmp('east-panel_searcBuilder_dest').items!=undefined){
							      						$("#tot_search_builder_documents").html(Ext.getCmp('east-panel_searcBuilder_dest').items.getCount()); 
										      		}   					
							      				}   
							      			  },
								    buttons: [{
			        					        text    : 'Svuota',
			        					        handler : function(btn) {
		    					    				Ext.getCmp('east-panel_searcBuilder_dest').removeAll(true);
			        					        }
			        					       },
											   {	        	
									            text: 'Elabora Documento',
									            disabled:false,
									            handler: function(){
			        					    	   				openDocumetManager();
										            	 }
							        		   }	
							        		]		         		
					            }),
					            new Ext.Panel({
					            	id: 'south-panel_searcBuilder_basket',
					            	title:'<span class="tree_top_title">Cestino</span>',
								    collapsible: true,
								    collapsed:false,
								    html:'<br/><br/>',
								    ddGroup : 'searcBuilderDDGroup',
								    frame: true,
								    autoScroll: true,
								    region : 'south',
								    height:60,		
								    resizable:true,	    
								    listeners: { 
							      			  'afterrender': function(elemento){
							      					var postPanelDropTarget = generateMyDropTarget(elemento);	      					
							      				} 
							      			  },
					      			tools:[
											{
										        id:'close',
										        handler: function(e, target, panel){
										            panel.removeAll(true);
										        }
										    }
										   ]		         		
					            })
							  ],listeners : {
					 				'afterrender' : function() {
			 						if(top.Message['Bus7']){
					 					top.Message['Bus7'].on('modifyStoredSearch', function() {					 						
					 						addFromStoredSearch();				       					    	      	
					 					}, this);	
			 						}	
			 						if(top['Application'].searchpanels && top['Application'].searchpanels.length>0){
			 							addFromStoredSearch();		
				 					}			 					
				 				}
				 			}
							

				  })
        ]
    });
	 top.closeLoading();
});
function addFromStoredSearch(){
	   Ext.getCmp('east-panel_searcBuilder_dest').removeAll(true);
		Ext.getCmp('east-panel_searcBuilder').expand();
		count_text_node = 0;								    	          		
		var results = top['Application'].searchpanels;
		var count_results = results.length;	
		for (var x = 0 ; x < results.length; x ++){
			 var comp = results.get(x);
			 var panel;
			 if(comp.panel_type=='archive_panel'){
				panel =  getArchivePanel(comp.idRecord,comp.idArchive,comp.title,comp.searchIdRecord);				 								
			 }else if(comp.panel_type=='title_panel'){
				panel =  getTitlePanel(comp.panel_text,comp.searchIdRecord); 					 							
			 }else if(comp.panel_type=='text_panel'){
				panel =  getTextPanel(comp.panel_text,comp.searchIdRecord); 						 						
			 }else if(comp.panel_type=='index_panel'){
				panel =  getIndexPanel(comp.panel_text,comp.searchIdRecord);							 					
			 }
			 if(panel!=null && panel!=undefined){
				Ext.getCmp('east-panel_searcBuilder_dest').add(panel);
				Ext.getCmp('east-panel_searcBuilder_dest').doLayout();
	 	 }
		}		
		top['Application'].searchpanels = new Array();	
}
function generateMyDropTarget(cmp) {
	return new Ext.dd.DropTarget(cmp.body.dom,{
		notifyEnter : function(ddSource, e, data) {
			cmp.body.stopFx();
			cmp.body.highlight();
		},
		notifyDrop : function(ddSource, e, data) {
			//console.debug(ddSource);
			//console.debug(e);
			//console.debug(data);
			if(cmp.get(ddSource.panel.id)){
 
			}else{
				cmp.add(ddSource.panel.id);
				ddSource.panel.collapse();
				cmp.doLayout();
			}		
			return (true);
		}
	});
}
function validateSearchForm(fieldValue){
	var ck_ok = false;
    var cks = Ext.select("input[name^=sf_]").elements;
	for (var x = 0 ; x < cks.length; x ++){
		if(Ext.getCmp(Ext.get(cks[x]).id)!=undefined && Ext.util.Format.trim(Ext.getCmp(Ext.get(cks[x]).id).getValue())!=''){
			ck_ok = true;
			break;
		}							
	}
	if(ck_ok){
		for (var x = 0 ; x < cks.length; x ++){
			if(Ext.getCmp(Ext.get(cks[x]).id)!=undefined)
				Ext.getCmp(Ext.get(cks[x]).id).clearInvalid();				
		}		
	}
	return ck_ok;
}
function addFilterToSearch(id,title){
	var panel_id ='thPanel_archive_'+(Ext.getCmp('tabPanel_searcBuilder').getActiveTab().id).substring(Ext.getCmp('tabPanel_searcBuilder').getActiveTab().id.lastIndexOf('_')+1,Ext.getCmp('tabPanel_searcBuilder').getActiveTab().id.length);
	if(Ext.getCmp(panel_id)!=undefined){
		if(Ext.getCmp('th_filter_ck_'+panel_id+'_'+id)==undefined){
			    if(Ext.getCmp(panel_id).items.length==0){
			    	Ext.getCmp(panel_id).add({
						 xtype: 'checkbox',
			             name: 'find_sons',
			             id:'find_sons_th',
			             boxLabel:'Estendi ai figli',
						 inputValue: 'true', 
						 checked: false
				    });
				}
				Ext.getCmp(panel_id).add({
										 xtype: 'checkbox',
							             name: 'th_filter',
							             id:'th_filter_ck_'+panel_id+'_'+id,
							             boxLabel: title+' <a href="#n" onclick="removeSearchBuilderFilter(\'th_filter_ck_'+panel_id+'_'+id+'\');"><img src="img/skos_tab/delete.png" title="rimuovi filtro" border="0"/></a>',
										 inputValue: id, 
										 checked: true
								        });
				Ext.getCmp(panel_id).doLayout();
				
		}
		if(Ext.getCmp(panel_id).items.length>2){
			Ext.getCmp(panel_id).remove(Ext.getCmp('find_sons_th'),true);
		}
	}
	if(Ext.getCmp('search_concept_win')!=undefined){
		Ext.getCmp('search_concept_win').destroy();
	}
}
function removeSearchBuilderFilter(id_ck){
	var panel_id ='thPanel_archive_'+(Ext.getCmp('tabPanel_searcBuilder').getActiveTab().id).substring(Ext.getCmp('tabPanel_searcBuilder').getActiveTab().id.lastIndexOf('_')+1,Ext.getCmp('tabPanel_searcBuilder').getActiveTab().id.length);
	if(Ext.getCmp(panel_id)!=undefined){
		if(Ext.getCmp(id_ck)!=undefined){
				Ext.getCmp(panel_id).remove(id_ck,true);
				Ext.getCmp(panel_id).doLayout();
		}
		if(Ext.getCmp(panel_id).items.length==1 && Ext.getCmp('find_sons_th')!=undefined){
			Ext.getCmp(panel_id).removeAll(true)
		}else if(Ext.getCmp(panel_id).items.length==1 && Ext.getCmp('find_sons_th')==undefined){
			Ext.getCmp(panel_id).insert(0,{
				 xtype: 'checkbox',
	             name: 'find_sons',
	             id:'find_sons_th',
	             boxLabel:'Estendi ai figli',
				 inputValue: 'true', 
				 checked: false
		    });
			Ext.getCmp(panel_id).doLayout();
		}
	}	
}
function openSearchTab(idArchive,query,totalHits,order_by,sort_type,find_sons,file_search){
	var sf_fields = Ext.getCmp('id_commonSearchFields_all').items;
	for (i=0; i<sf_fields.getCount(); ++i) {
	    var comp = sf_fields.get(i);
	    try{
	        if(comp.xtype=='vocfield' || comp.xtype=='textfield' || comp.xtype=='combo'){	        	
	        	Ext.getCmp('id_commonSearchFields_archive_'+idArchive).find("name",comp.name)[0].setValue(comp.getValue());
	        }
	    }catch(e){}
	}
	Ext.getCmp('thPanel_archive_'+idArchive).removeAll(true);
	var thfilters = Ext.getCmp('thPanel_archive_all').items;
	for (i=0; i<thfilters.getCount(); ++i) {
	    var comp = thfilters.get(i);
	    var title = comp.boxLabel.substring(0,comp.boxLabel.indexOf(" <a "));
	    if(comp.name!=undefined && comp.name=='th_filter'){
	    	var newFilter = {
					 xtype: 'checkbox',
		             name: 'th_filter',
		             id:'th_filter_ck_'+'thPanel_archive_'+idArchive+'_'+comp.id.substring(comp.id.lastIndexOf('_')+1,comp.id.length),
		             boxLabel: title+' <a href="#n" onclick="removeSearchBuilderFilter(\'th_filter_ck_'+'thPanel_archive_'+idArchive+'_'+comp.id.substring(comp.id.lastIndexOf('_')+1,comp.id.length)+'\');"><img src="img/skos_tab/delete.png" title="rimuovi filtro" border="0"/></a>',
					 inputValue: comp.inputValue, 
					 checked: true
			        };
   			Ext.getCmp('thPanel_archive_'+idArchive).add(newFilter);
		}else{
			var ck_find_sons = false;
			if(find_sons!='null' && find_sons!='' && find_sons=='true'){
				ck_find_sons = true;
			}
			Ext.getCmp('thPanel_archive_'+idArchive).add({
				 xtype: 'checkbox',
	             name: 'find_sons',
	             id:'find_sons_th',
	             boxLabel:'Estendi ai figli',
				 inputValue: 'true', 
				 checked: ck_find_sons
		    });
		}
	}
	showArchiveTab(idArchive);
	//Ext.getCmp('search_builder_archive_'+idArchive).show();
	Ext.getCmp('center-panel_searcBuilder_archive_'+idArchive).removeAll(true);	
	Ext.getCmp('center-panel_searcBuilder_archive_bbar_'+idArchive).removeAll(true);	
	$('#nResults_archive_page_'+idArchive).html("");	
	Ext.getCmp('center-panel_searcBuilder_archive_'+idArchive).load({ 
		url: 'searchBuilder_adv_search.html?action=search&idArchive='+idArchive+'&order_by='+order_by+'&sort_type='+sort_type+'&file_search='+file_search+'&query='+encodeURI(query),
		method: 'POST',
		nocache: true,
		text: 'Ricerca in corso...', 
		timeout: 30, 
		scripts: true
	});
	//$('#nResults_archive_'+idArchive).html(totalHits);
}
var count_text_node = 0;
function openDocumetManager(){
	var tabsDocumentManager = new Ext.TabPanel({
		id:'tabsDocumentManager',
        region: 'center',
        margins:'3 3 3 3', 
        activeTab: 0,
		defaults : {
		    autoScroll:true
		},
        items:[
               new Ext.Panel({
            	    layout: 'border',
            	    title: 'Gestione documento ricerca',
            	    border : false,
            	    id:'tabDocumentSearchManager',
					items:[{
		           		title: 'Costruzione documento',
		           		id:'tabDocumentManager',
		           		items:[],
		           		region:'east',
		           		split: true,
		           		border : true,
		           		ddGroup : 'searcBuilderDDGroup',
		           		padding: 5,
			            autoScroll: true,
			            defaults : {
									style : 'margin-top:15px;margin-left:5px;margin-right:5px;'
						},
						listeners: {
		       			  	'afterrender': function(elemento){
		       							var postPanelDropTarget = generateMyDropTarget(elemento);
		       							var results = Ext.getCmp('east-panel_searcBuilder_dest').items;
		       							var count_results = results.length;
		       							var count_results_for_loading = results.length;
		       							var modify = false;
		       							var searchIdRecord = '';
		       							if(count_results_for_loading>50){
		       								openLoadingS();
										 }																						    	              
		       					        while (count_results!=0) {
		       					        	    var comp = results.get(0);
		       					        	    comp.collapse();																						    	          	   
		       					        	    this.add(comp);																							    	          	    
		       					        	    this.doLayout();	
		       					        	    count_results--;
		       					        	    if(comp.panel_type=='title_panel'){
		       					        	    	Ext.getCmp('generateTitleButton').disable();
			       					        	}
		       					        	 	if(comp.panel_type=='index_panel'){
		       					        	    	Ext.getCmp('generateIndexButton').disable();
			       					        	}
			       					        	if(comp.searchIdRecord && comp.searchIdRecord!=''){
			       					        		modify = true;
			       					        		searchIdRecord = comp.searchIdRecord;
					       					    }					       						    	          	   
		       					    	}
		       					    	<%if(editOn){%>
		       					    	if(modify){
		       					    		if(Ext.getCmp('save_search_button')!=null && Ext.getCmp('save_search_button')!=undefined)
				       					    	Ext.getCmp('tabsDocumentManagerBbar').remove('save_search_button',true);
		       					    		Ext.getCmp('tabsDocumentManagerBbar').insert(0,
		       					    				{
		       									        text    : 'Modifica documento',
		       									        id:'modify_search_button',
		       									        disabled:true,
		       									        handler : function(btn) {
		       					    							submitSaveForm(searchIdRecord);
		       										        }
		       									       }

				       					    		);
			       					    }else{
				       					    if(Ext.getCmp('modify_search_button')!=null && Ext.getCmp('modify_search_button')!=undefined)
				       					    	Ext.getCmp('tabsDocumentManagerBbar').remove('modify_search_button',true);
			       					    	Ext.getCmp('tabsDocumentManagerBbar').insert(0,
		       					    				{
		       									        text    : 'Salva documento',
		       									        id:'save_search_button',
		       									        disabled:true,
		       									        handler : function(btn) {
		       					    						submitSaveForm();
		       										        }
		       									       }

				       					    		);
					       			    }
					       			    <%}%>
		       					     if(count_results_for_loading>50){
		       					    	closeLoadingS();
									  }
		       					  	updateIndexPanel();
		       					 	$(".highlight_class").removeClass('highlight_class');						
		       				}
		       			} 
	        		},new Ext.tree.TreePanel({
	        			title: 'Riordina documento',
	        			id:'treePanelSearchBuilder',
	        			region:'center',
	        			useArrows: true,
	        		    autoScroll: true,
	        		    animate: true,
	        		    enableDD: true,
	        		    split: true,
	        		    containerScroll: true,
	        		    border: true,
	        		    loader: new Ext.tree.TreeLoader(),
						root: new Ext.tree.TreeNode({
						            expanded: true,
						            text: 'Documento di ricerca',
			        	            draggable: false,
			        	            id: 'documentRoot',
						            children: []
						}),
						listeners: { 
	        			  	'afterrender': function(){
					        			var items = Ext.getCmp('tabDocumentManager').items;
					        			for (var y = 0 ; y < items.getCount();y++) {
					        			    var comp = items.get(y);
					        			    var title = comp.titleForTree;
					        			    title = title.replace(/<br\s*[\/]?>/gi,' ');	        			   
					        			    this.getRootNode().appendChild(getTreeNode(title,comp.id));
					        			}			
	        				},
	        				'dragdrop':function(){
	        					//Ext.getCmp('tabDocumentManager').removeAll(true);
	        					var results = Ext.getCmp('tabDocumentManager').items;			
								var count_results = results.length;																					    	              
						        while (count_results!=0) {
						        	    var comp = results.get(0);
						        	    if(comp.collapsible)
						        	    	comp.collapse();																						    	          	   
						        	    Ext.getCmp('east-panel_searcBuilder_dest').add(comp);																							    	          	    
						        	    Ext.getCmp('east-panel_searcBuilder_dest').doLayout();	
						        	    count_results--;		    	          	   
						    	}	
	        					
	        					var children = this.getRootNode().childNodes;        					
	        					for (var i = 0;i<children.length;i++){
									var currentNode = children[i];
									//if(currentNode.attributes.idRef!='noref')
										Ext.getCmp('tabDocumentManager').add(Ext.getCmp(currentNode.attributes.idRef));
								}
	        					updateIndexPanel();
	        					Ext.getCmp('tabDocumentManager').doLayout();		        				
							},
							'click':function(node, event){
								if(Ext.getCmp(node.attributes.idRef).collapsible){									
									if(Ext.getCmp(node.attributes.idRef).collapsed){									
										Ext.getCmp(node.attributes.idRef).expand(true);
									}else{
										//Ext.getCmp(node.attributes.idRef).collapse();
									}
								}
                    			var y_pos = Ext.getCmp(node.attributes.idRef).el.getY()+Ext.getCmp(node.attributes.idRef).el.getHeight();
                    			Ext.getCmp('tabDocumentManager').body.scrollTo('top', y_pos);
                    			Ext.getCmp(node.attributes.idRef).body.highlight( 'ffdb75', {
                    				   attr: 'background-color',
                    			       easing: 'easeOut',
                    			       duration: 10
                    			});
					        }
												
	        			},tbar: new Ext.Toolbar({
			             	   items : [
										'->',
										new Ext.Button({
										    text    : 'Aggiungi titolo',
										    id:'generateTitleButton',
										    handler : function(btn) {	

											    if(Ext.getCmp('treePanelSearchBuilder').getRootNode().hasChildNodes()){	
												    var titleNode = new Ext.tree.TreeNode({id:'title_searchBuilder_treeNode',text:'Titolo documento',leaf:true,idRef:'title_searchBuilder',editable:true});										    	
												    Ext.getCmp('treePanelSearchBuilder').getRootNode().appendChild(titleNode);
											    	Ext.getCmp('treePanelSearchBuilder').getRootNode().insertBefore(titleNode , Ext.getCmp('treePanelSearchBuilder').getRootNode().item(0));
											    	Ext.getCmp('tabDocumentManager').insert(0,getTitlePanel());
												}else{
													Ext.getCmp('treePanelSearchBuilder').getRootNode().appendChild({id:'title_searchBuilder_treeNode',text:'Titolo documento',leaf:true,idRef:'title_searchBuilder',editable:true/*,href:'#title_searchBuilder'*/});
													Ext.getCmp('tabDocumentManager').add(getTitlePanel());
												}																				    	              												
												Ext.getCmp('tabDocumentManager').doLayout();
												Ext.getCmp('generateTitleButton').disable();
										    }
										}),
									    '',
									    new Ext.Button({
				        					        text    : 'Aggiungi testo',
				        					        handler : function(btn) {									    	
									    				Ext.getCmp('treePanelSearchBuilder').getRootNode().appendChild({id:'text_node_'+count_text_node+'_treeNode',text:'Sezione testuale '+(count_text_node+1),leaf:true,idRef:'text_node_'+count_text_node,editable:true/*,href:'#text_node_'+count_text_node+'_htmleditor'*/});																				    	              
									    				Ext.getCmp('tabDocumentManager').add(getTextPanel());
									    				Ext.getCmp('tabDocumentManager').doLayout();
					        					    }
        					    		}),'',
        					    		new Ext.Button({
		        					        text: 'Genera indice',
		        					        id:'generateIndexButton',
		        					        handler : function(btn) {									    	
							    					Ext.getCmp('treePanelSearchBuilder').getRootNode().appendChild({id:'index_searchBuilder_treeNode',text:'Indice',leaf:true,idRef:'index_searchBuilder'/*,href:'#index_searchBuilder'*/});																				    	              
							    					Ext.getCmp('tabDocumentManager').add(getIndexPanel());
								    				Ext.getCmp('tabDocumentManager').doLayout();
								    				Ext.getCmp('generateIndexButton').disable();
								    				
				        					}
					    		}) 
		                         ]
		                     })				
	        	    })]
               })
               ,{
            		title: 'Anteprima',
            		html: '',
            		padding:10,
            		renderTo:'tabDocumentManagerAnteprima',
            		listeners: { 
        			  	'activate': function(el){
        						var items = Ext.getCmp('tabDocumentManager').items;
        						var has_index = false;
        						var to_save='';
        						var search_contents = '';
        						var search_title='';
        						var htmlToShow='<div id="htmlToClean" class="htmlToClean">\r\n';        						
			        			for (var y = 0 ; y < items.getCount();y++) {
			        			    var comp = items.get(y);
			        			    if(comp.panel_type=='archive_panel'){
			        			    	 htmlToShow+='<div class="htmlToCleanPar html_archive">'+comp.body.dom.innerHTML+'</div>\r\n';
			        			    	 to_save+=comp.id+'~|~|~';
			        			    	 search_contents+=comp.body.dom.innerHTML+' ';
				        			}else if(comp.panel_type=='text_panel'){
				        				htmlToShow+='<div class="htmlToCleanPar html_text">'+getMCEText($("#"+comp.id+"_htmleditor"))+'</div>\r\n';
				        				to_save+='text_'+getMCEText($("#"+comp.id+"_htmleditor"))+'~|~|~';
				        				search_contents+=getMCEText($("#"+comp.id+"_htmleditor"))+' ';
					        		}else if(comp.panel_type=='index_panel'){
					        			htmlToShow+='<div class="htmlToCleanPar html_index">'+comp.body.dom.innerHTML+'</div>\r\n';
					        			has_index = true;
					        		}else if(comp.panel_type=='title_panel'){
					        			htmlToShow+='<div class="htmlToCleanPar html_title"><h1><b>'+Ext.getCmp('title_searchBuilder_textarea').getValue()+'</b></h1></div>\r\n';
					        			to_save+='title_'+Ext.getCmp('title_searchBuilder_textarea').getValue()+'~|~|~';
					        			search_title=Ext.getCmp('title_searchBuilder_textarea').getValue();
						        	}		        			   			        			   
			        			}
			        			htmlToShow+='</div>';
			        			var htmlToHide='<div id="htmlToHide" style="display:none;">';
			        			    htmlToHide+='<form method="post" id="rtf_form" name="rtf_form" target="_new" action="searchBuilder_rtf_producer.html">';			        			   
			        			    htmlToHide+='<input type="hidden" name="output_mode" id="output_mode" value=""/>';
				        			htmlToHide+='</form>';
				        			htmlToHide+='</div>';
				        		var htmlToHideForSave='<div id="htmlToHideForSave" style="display:none;">';
				        			htmlToHideForSave+='<form method="post" id="save_search_form" name="save_search_form" target="_new" action="searchBuilder_rtf_producer.html">';			        			   
			        			    htmlToHideForSave+='<input type="hidden" name="has_index" id="has_index" value="'+has_index+'"/>';
			        			    htmlToHideForSave+='<textarea name="to_save" id="to_save">'+to_save+'</textarea>';
			        			    htmlToHideForSave+='<textarea name="search_title" id="search_title">'+search_title+'</textarea>';
			        			    //htmlToHideForSave+='<textarea name="search_contents" id="search_contents">'+search_contents+'</textarea>';
			        			    htmlToHideForSave+='</form>';
				        			htmlToHideForSave+='</div>';	
			        			this.update(htmlToShow+htmlToHide+htmlToHideForSave);
			        			clearHTMLToPrint();			        							
        				}
        			}
        		}
        	   ],
        	   bbar: new Ext.Toolbar({
					id:'tabsDocumentManagerBbar',
					/*height:'20',*/
					autoHeight:true,
					autoScroll:true,
					buttonAlign:'right',
					items:[
						   {	        	
						    text: 'Stampa RTF',
						    id:'rtf_button',
						    disabled:true,
						    handler: function(){
							   		submitRtfForm('rtf');
						        }
						  	   },
						   {	        	
						    text: 'Stampa PDF',
						    id:'pdf_button',
						    disabled:true,
						    handler: function(){
						  				submitRtfForm('pdf');
						        }
					  	   }
						]
					}),        	  
       			listeners: { 
						'tabchange': function(el,tab){
							if(tab.id=='tabDocumentSearchManager'){
								Ext.getCmp('rtf_button').disable();
								Ext.getCmp('pdf_button').disable();
								if(Ext.getCmp('save_search_button')!=null && Ext.getCmp('save_search_button')!=undefined)
									Ext.getCmp('save_search_button').disable();
								if(Ext.getCmp('modify_search_button')!=null && Ext.getCmp('modify_search_button')!=undefined)
									Ext.getCmp('modify_search_button').disable();		
								
							}else{
								Ext.getCmp('rtf_button').enable();
			        			Ext.getCmp('pdf_button').enable();
			        			if(Ext.getCmp('save_search_button')!=null && Ext.getCmp('save_search_button')!=undefined)
									Ext.getCmp('save_search_button').enable();
								if(Ext.getCmp('modify_search_button')!=null && Ext.getCmp('modify_search_button')!=undefined)
									Ext.getCmp('modify_search_button').enable();		
							}
						}
   				}
    });
	winDocumentManager = new Ext.Window({
        title: 'Elabora Documento',
        closable:true,
        plain:true,
        minWidth:800,
        minHeight:500,
        layout: 'border',
        autoScroll: true,
        maximized : true,
        items: [tabsDocumentManager],
        listeners: {
			'beforeclose': function(){
				var results = Ext.getCmp('tabDocumentManager').items;			
				var count_results = results.length;																					    	              
		        while (count_results!=0) {
		        	    var comp = results.get(0);
						if(comp.collapsible)
		        	    	comp.collapse();																						    	          	   
		        	    Ext.getCmp('east-panel_searcBuilder_dest').add(comp);																							    	          	    
		        	    Ext.getCmp('east-panel_searcBuilder_dest').doLayout();	
		        	    count_results--;		    	          	   
		    	}						
			} 
		} 
    });
	winDocumentManager.show();
}
function getTreeNode(title,idRef){
	var cls = '';
	if(idRef.indexOf('panel_archive_')!=-1){
		cls = 'x-tree-noicon';
	}
	var node = new Ext.tree.TreeNode({    
			        id:idRef+'_treeNode',
					text:title,
					leaf:true,
					/*href:'#'+idRef,*/
					cls:cls,
					idRef:idRef		
				});
	return node;
}
function getTextPanel(compHtml,compSearchIdRecord){
 var html='';
 if(compHtml!=null && compHtml!=undefined){
	 html=compHtml;
 }
 var searchIdRecord='';
 if(compSearchIdRecord!=null && compSearchIdRecord!=undefined){
	 searchIdRecord=compSearchIdRecord;
 }
 var idTextPanel = 'text_node_'+count_text_node;
 var panel = new Ext.Panel({
		id : idTextPanel,																				
		title : 'Sezione testuale '+(count_text_node+1),
		titleForTree: 'Sezione testuale '+(count_text_node+1),
		frame: true,
		collapsible: true,
		draggable:true,
		panel_type:'text_panel',
		searchIdRecord:searchIdRecord,
		html:'<div><textarea id="'+idTextPanel+'_htmleditor">'+html+'</textarea></div>',
		margins:'5 5 5 5',
		items:[],																									
		tools:[
				{
			        id:'close',
			        handler: function(e, target, panel){
						try{
					    	var children =  Ext.getCmp('treePanelSearchBuilder').getRootNode().childNodes;        					
	 					    for (var i = 0;i<children.length;i++){
								var currentNode = children[i];
								if(currentNode.attributes.idRef==idTextPanel){
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
	 						this.setPosition(0,0);
	 						if(!this.collapsed)
	 							initSimpleMceEditor($("#"+idTextPanel+"_htmleditor"));
	      			   },
	      			   'beforecollapse': function(elemento){
	      				    getMCEremove($("#"+idTextPanel+"_htmleditor"));
	      			   },
	      			   'expand': function(elemento){
	 						initSimpleMceEditor($("#"+idTextPanel+"_htmleditor"));
	      			   }
	      			}
	});
 	count_text_node++;
    return panel;
}
function getIndexPanel(compHtml,compSearchIdRecord){
	 var html='';
	 if(compHtml!=null && compHtml!=undefined){
		 html=compHtml;
	 }
	 var searchIdRecord='';
	 if(compSearchIdRecord!=null && compSearchIdRecord!=undefined){
		 searchIdRecord=compSearchIdRecord;
	 }
	 var panel = new Ext.Panel({
			id : 'index_searchBuilder',																				
			title : 'Indice',
			titleForTree: 'Indice',
			frame: true,
			collapsible: true,
			draggable:true,																			
			html:'indice',
			panel_type:'index_panel',
			searchIdRecord:searchIdRecord,																									
			tools:[
					{
				        id:'close',
				        handler: function(e, target, panel){
					        try{
							    Ext.getCmp('generateIndexButton').enable();
							    var children =  Ext.getCmp('treePanelSearchBuilder').getRootNode().childNodes;        					
	        					for (var i = 0;i<children.length;i++){
									var currentNode = children[i];
									if(currentNode.attributes.idRef=='index_searchBuilder'){
										Ext.getCmp('treePanelSearchBuilder').getRootNode().removeChild(currentNode,true);										
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
		 						this.setPosition(0,0);
		 						if(html==''){
			 						var items = Ext.getCmp('tabDocumentManager').items;
	        						var htmlToShow='<div><ul>';		
				        			for (var y = 0 ; y < items.getCount();y++) {
				        			    var comp = items.get(y);
				        			    if(comp.panel_type=='archive_panel'){
					        			     var title = comp.title;
					        			     if(comp.titleForTree!=null && comp.titleForTree!=undefined){
					        			    	 title = comp.titleForTree;
						        			 }
					        			     title = title.replace(/<br\s*[\/]?>/gi,' ');
						        			 if(title.indexOf('&nbsp;')!=-1){
							        			 title = title.substring(title.indexOf('&nbsp;')+'&nbsp;'.length,title.length);
							        		 }    
				        			    	 htmlToShow+='<li> - <!--start index voice-->'+title+'<!--end index voice--></li>';
					        			}			        			   			        			   
				        			}
				        			htmlToShow+='</ul></div>';
			        				this.update(htmlToShow);
		 						}else{
		 							this.update(html);	
				 				}	
		      			   }
		      			}
		});
	    return panel;
	}
function getTitlePanel(compHtml,compSearchIdRecord){
	 var html='';
	 if(compHtml!=null && compHtml!=undefined){
		 html=compHtml.replace(/(<([^>]+)>)/ig,''); 
	 }
	 var searchIdRecord='';
	 if(compSearchIdRecord!=null && compSearchIdRecord!=undefined){
		 searchIdRecord=compSearchIdRecord;
	 }
	 var panel = new Ext.Panel({
			id : 'title_searchBuilder',																				
			title : 'Titolo Documento',
			titleForTree: 'Titolo Documento',
			frame: true,
			collapsible: true,
			draggable:true,
			layout:'fit',
			panel_type:'title_panel',
			searchIdRecord:searchIdRecord,
			items:[
					{   
						id:'title_searchBuilder_textarea',
					    xtype:'textarea',
					    height:30,
					    grow:true,
					    value:html,
					    preventScrollbars:true,
					    style: {
				            width:'95%'
				        } 
					}
				  ],																									
			tools:[
					{
				        id:'close',
				        handler: function(e, target, panel){
					        try{
							    Ext.getCmp('generateTitleButton').enable();
							    var children =  Ext.getCmp('treePanelSearchBuilder').getRootNode().childNodes;        					
	        					for (var i = 0;i<children.length;i++){
									var currentNode = children[i];
									if(currentNode.attributes.idRef=='title_searchBuilder'){
										Ext.getCmp('treePanelSearchBuilder').getRootNode().removeChild(currentNode,true);
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
		 						this.setPosition(0,0);
		      			   }
		      			}
		});
	    return panel;
}
function getArchivePanel(compIdRecord,compIdArchive,compTitle,compSearchIdRecord){
	var searchIdRecord='';
	if(compSearchIdRecord!=null && compSearchIdRecord!=undefined){
		 searchIdRecord=compSearchIdRecord;
	 }
	var panel_archive =  new Ext.Panel({
		id : 'panel_archive_'+compIdArchive+'_record_'+compIdRecord,																				
		title : compTitle,
		titleForTree: compTitle,
		frame: true,
		collapsible: true,
		draggable:true,
		html:'<div id="scheda_'+compIdRecord+'"></div>',
		panel_type:'archive_panel',
		searchIdRecord:searchIdRecord,																																													
		tools:[
				{
			        id:'close',
			        handler: function(e, target, panel){
						try{
						    var children =  Ext.getCmp('treePanelSearchBuilder').getRootNode().childNodes;        					
        					for (var i = 0;i<children.length;i++){
								var currentNode = children[i];
								if(currentNode.attributes.idRef=='panel_archive_'+compIdArchive+'_record_'+compIdRecord){
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
	      				loadDocumentForSearchResults(compIdRecord,compIdArchive,'scheda_'+compIdRecord);																															
	      			   } 
	      			}
	});
	return panel_archive;
}

function clearHTMLToPrint(){
	$("#htmlToClean").find(".sezione:has(div:contains('informazioni gestionali'))").hide();
	$("#htmlToClean").find(".head").hide();
	$("#htmlToClean").find(".scheda").removeClass("scheda");
	$("#htmlToClean").find(".sezione").removeClass("sezione");
	$("#htmlToClean").find(".archive_container_color").removeClass("archive_container_color");
	$("#htmlToClean").find(".if-empty-remove").removeClass("if-empty-remove");
	$("#htmlToClean").find(".if-empty-remove-sezione").removeClass("if-empty-remove-sezione");
	$("#htmlToClean").find(":hidden").remove();
	var count_title_panel = 0;
	var count_index_panel = 0;
	var count_text_panel = 0;
	var count_archive_panel = 0;
	var panel_order='';	
	var search_contents='';//
	$(".htmlToCleanPar").each(function(){
			if($(this).hasClass('html_title')){
				$("#rtf_form").append('<textarea name="title_panel_contents_'+count_title_panel+'">'+encodeURI($(this).html())+'</textarea>');
				search_contents+=encodeURI($(this).html())+' ';
				panel_order+='title_panel_contents_'+count_title_panel+';';
				count_title_panel++;	
			}else if($(this).hasClass('html_text')){
				$("#rtf_form").append('<textarea name="text_panel_contents_'+count_text_panel+'">'+encodeURI($(this).html())+'</textarea>');
				search_contents+=encodeURI($(this).html())+' ';
				panel_order+='text_panel_contents_'+count_text_panel+';';
				count_text_panel++;
			}else if($(this).hasClass('html_index')){
				$("#rtf_form").append('<textarea name="index_panel_contents_'+count_index_panel+'">'+encodeURI($(this).html())+'</textarea>');
				search_contents+=encodeURI($(this).html())+' ';
				panel_order+='index_panel_contents_'+count_index_panel+';';
				count_index_panel++;
			}else if($(this).hasClass('html_archive')){
				$("#rtf_form").append('<textarea name="archive_panel_contents_'+count_archive_panel+'">'+encodeURI($(this).html())+'</textarea>');
				search_contents+=encodeURI($(this).html())+' ';
				panel_order+='archive_panel_contents_'+count_archive_panel+';';
				count_archive_panel++;
			}
	});
	$("#rtf_form").append('<input type="text" name="panel_order" value="'+panel_order+'"/>');	
	$("#save_search_form").append('<textarea name="search_contents" id="search_contents">'+search_contents+'</textarea>');
}
function openCloseInfo(clicked){
	if($(clicked).parent("div").parent("div").find(".open_close").is(":visible")){
		$(clicked).parent("div").parent("div").find(".open_close").hide();
		$(clicked).html("+")
	}else{
		$(clicked).parent("div").parent("div").find(".open_close").show();
		$(clicked).html("-")
	}
}
function openLoadingS(){
	loadBar = true;
	Ext.MessageBox.show({
        title: '${messageStatusTitle}',
        msg: '${messageStatusText}',
        progressText: '${messageStatusLoading}',
        width:300,
        progress:true,
        closable:false,
     	wait:true
	});
}
function closeLoadingS(){	
loadBar=false;
Ext.MessageBox.hide();
}
function submitRtfForm(output_mode){
	$("#output_mode").attr("value",output_mode)
	document.rtf_form.submit();
}
var old_search_code = '';
function submitSaveForm(searchIdRecord){
	var save_title = '';
    var save_date = '';
    var save_description = '';
    var save_legislatura = '';
    var save_dipartimento = '';
    var search_code = '';
    var search_audience = '';
    if(searchIdRecord){
    	 Ext.Ajax.request({
    		 	 url : 'searchBuilder_adv_search.html',
		         method: 'POST',
		         nocache: true,
		         params :{action:'get_modify_values',idRecord:searchIdRecord},
                      success: function ( result, request ) {
                          var jsonData = Ext.util.JSON.decode(result.responseText);
                          save_title = jsonData.data.save_title;
                          save_date = jsonData.data.save_date;
                          save_description = jsonData.data.save_description;
                          save_legislatura = jsonData.data.save_legislatura;
                          save_dipartimento = jsonData.data.save_dipartimento;
                          search_code = jsonData.data.search_code;
                          old_search_code = search_code;
                          search_audience = jsonData.data.search_audience;
                          openSaveSearchWin(searchIdRecord,save_title,save_date,save_description,save_legislatura,save_dipartimento,search_code,search_audience);
                   },
                      failure: function ( result, request ) {
                	   Ext.Msg.alert('Attenzione','Si è verificato un errore!');
                   }
           });
    }else{
    	openSaveSearchWin(save_title,save_date,save_description,save_legislatura,save_dipartimento,search_code,search_audience);
    }
}
function openSaveSearchWin(idRecord,save_title,save_date,save_description,save_legislatura,save_dipartimento,search_code,search_audience){
	var titleToSave = '';
	var dateToSave;    
    if(save_title==''){
    	 titleToSave = $("#search_title").val();
    }else{
    	titleToSave = save_title;
    } 
    if(save_date==''){
    	dateToSave =  new Date;
  	}else{
  		dateToSave = save_date;
   	}
	var save_search_fields = {	        
	        xtype: 'fieldset',
	        id:'save_search_fields',
	        bodyStyle:'padding:5px;',
	        border:false,
	        margins : '5 5 5 5',
	        anchor: '95%',
	        items: [
		    	    {
		             xtype: 'textfield',
		             name: 'save_title',
		             fieldLabel: 'Titolo',
		             value:titleToSave,
		             anchor: '95%'
			        },
			        {
		             xtype: 'datefield',
		             id: 'save_date',
		             fieldLabel: 'Data',
		             format:'d/m/Y',
		             width:90,
		             value: save_date,
		             style: 'margin:5 0 0 0;'
			        },
			        {   
					 id:'save_description',
					 fieldLabel:'Descrizione',
				     xtype:'textarea',
				     height:30,
				     grow:true,
				     value:save_description,
				     preventScrollbars:true,
				     anchor: '95%'
					},
			        {
					 xtype: 'combo',
					 name: 'legislatura',
				     fieldLabel: 'Legislatura',
				     id:'save_legislatura',
				     anchor: '95%',
					 editable: false,
					 triggerAction: 'all',
					 typeAhead: false,
					 mode: 'local',
					 hiddenName: 'sf_legislatura',
					 store: getDataLegislatureForSearch(true,4),
					 listeners:{
				         'added': function(el){
				         		if(save_legislatura!=''){
				         			el.setValue(save_legislatura);
					         	}				         		
				         }
				     }			
			        },
			        {
					 xtype: 'combo',
					 name: 'dipartimento',
				     fieldLabel: 'Dipartimento',
				     id:'save_dipartimento_id',
				     anchor: '95%',
					 editable: false,
					 triggerAction: 'all',
					 typeAhead: false,
					 mode: 'local',
					 hiddenName: 'save_dipartimento',
					 store: getDataDipartimentiForSearch(),
					 listeners:{
				         'added': function(el){
				         		if(save_dipartimento!=''){
				         			el.setValue(save_dipartimento);
					         	}else{
					         		el.setValue(top.Application['user'].dipartimento);
						        }					         		
				         		
				         }
				     }				
					},
			        {
		             xtype: 'radiogroup',
		             id:'search_code_question',
		             fieldLabel: 'Assegna codice numerico',
		             items: [
			                {boxLabel: 'No', name: 'search_code_question', inputValue: 'no',checked: true},
			                {boxLabel: 'Si', name: 'search_code_question', inputValue: 'yes'},
			                {boxLabel: 'ultimo assegnato', name: 'search_code_question', inputValue: 'last'}
			            	],
			         listeners:{
				         'change': function(el,radio){
				         		if(radio.inputValue=='yes'){
					         		if(Ext.getCmp('save_dipartimento_id').getValue()!=''){
					         			openMessageGetCode();				         			
					         			Ext.Ajax.request({
					         				 url : 'searchBuilder_adv_search.html',
					         		         method: 'POST',
					         		         nocache: true,
					         		         params :{action:'get_code',department_acronym:Ext.getCmp('save_dipartimento_id').getValue()},
					         		         success: function ( result, request ) {
					         		        	Ext.getCmp('search_code').setValue(result.responseText);
					         		        	closeMessageGetCode();
					         		      	 },
					         		         failure: function ( result, request ) {
					         		      		Ext.Msg.alert('Attenzione','Codice non disponibile!');
					         		      		closeMessageGetCode();
					         		      	 }
					         		    });
						         	}else{
						         		Ext.getCmp('search_code_question').setValue('no');
						         		Ext.Msg.alert('Attenzione','Selezionare un dipartimento!');
								    }				         			
						        }else if(radio.inputValue=='last'){
						        	if(Ext.getCmp('save_dipartimento_id').getValue()!=''){
							        	openMessageGetCode();
							        	Ext.Ajax.request({
					         				 url : 'searchBuilder_adv_search.html',
					         		         method: 'POST',
					         		         nocache: true,
					         		         params :{action:'get_code',department_acronym:Ext.getCmp('save_dipartimento_id').getValue(),last_code:'yes'},
					         		         success: function ( result, request ) {
					         		        	Ext.getCmp('search_code').setValue(result.responseText);
					         		        	closeMessageGetCode();
					         		      	 },
					         		         failure: function ( result, request ) {
					         		      		Ext.Msg.alert('Attenzione','Codice non disponibile!');
					         		      		closeMessageGetCode();
					         		      	 }
					         		    });
						        	}else{
						        		Ext.getCmp('search_code_question').setValue('no');
						         		Ext.Msg.alert('Attenzione','Selezionare un dipartimento!');
								    }	
								}else{
						        	Ext.getCmp('search_code').setValue('');
								}
				         }
				     }	
			        },
			        {
		             xtype: 'textfield',
		             name: 'search_code',
		             id:'search_code',
		             fieldLabel: 'Codice numerico',
		             disabled:false,
		             value:search_code,
		             width:130
				    } 
		        ],
		        buttons:[
							{
							    text:'Salva',
							    id:'win_save_search_button',
							    disabled:false,
							    handler : function(btn) {
										if(save_title){											     
											     if(Ext.util.Format.trim(Ext.getCmp('search_code').getValue())!=old_search_code && Ext.util.Format.trim(Ext.getCmp('search_code').getValue())!=''){												    
											    	 Ext.Ajax.request({
														 url : 'searchBuilder_adv_search.html',
												         method: 'POST',
												         nocache: true,
												         params :{action:'check_code',search_code:Ext.getCmp('search_code').getValue()},
												         success: function ( result, request ) {
												            if(parseInt(result.responseText,10)>0){
												            	Ext.Msg.alert('Attenzione','Codice di ricerca già presente!');
														    }else{
																	saveSearch('save_search',idRecord);
															}											           
												      	 },
												         failure: function ( result, request ) {
												      		Ext.MessageBox.alert('${messageStatus}', '${messageStatusError}');
												      	 }
												     });
											      }else{
											    	  saveSearch('modify_search',idRecord);
												  }											
												 								
										}else{
											if(Ext.util.Format.trim(Ext.getCmp('search_code').getValue())!=''){
												Ext.Ajax.request({
													 url : 'searchBuilder_adv_search.html',
											         method: 'POST',
											         nocache: true,
											         params :{action:'check_code',search_code:Ext.getCmp('search_code').getValue()},
											         success: function ( result, request ) {
											            if(parseInt(result.responseText,10)>0){
											            	Ext.Msg.alert('Attenzione','Codice di ricerca già presente!');
													    }else{
																saveSearch('save_search','');
														}											           
											      	 },
											         failure: function ( result, request ) {
											      		Ext.MessageBox.alert('${messageStatus}', '${messageStatusError}');
											      	 }
											     });
											}else{
												saveSearch('save_search','');
											}
										}
							        }
							   },
							   {
						        text: 'Chiudi',
						        id:'win_close_search_button',
						        disabled:false,
						        handler : function(btn) {
								   		save_search_win.destroy();
							        }
						       }
			 		     ]
	}
	
     var save_search_panel = new Ext.FormPanel({
			id : 'save_search_panel',
            border:false,
            width: 590,
			height:340,
            autoScroll:true,
	        items: []
	 });
	 save_search_win = new Ext.Window({
         title: 'Salva ricerca/appunti',
         id:'save_search_win',
         closable:true,
         width:600,
         height:350,
         modal:true,
         //border:false,
         plain:true,
         layout: 'fit',
         items: [save_search_fields]
     });

	 save_search_win.show(this);
}
function openMessageGetCode(){
	Ext.MessageBox.show({
		title : 'attendere',
		msg :'richiesta codice in corso...',
		progressText :'generazione codice...',
		width : 300,
		progress : true,
		closable : false,
		wait : true
	});
}
function closeMessageGetCode(){	
Ext.MessageBox.hide();
}
function saveSearch(action,idRecord){
	Ext.Ajax.request({
		 url : 'searchBuilder_adv_search.html',
        method: 'POST',
        nocache: true,
        params :{idRecord:idRecord,action:action,has_index:$("#has_index").attr("value"),to_save:$("#to_save").val(),search_title:$("#search_title").val(),search_audience:Ext.getCmp('search_audience').getValue().inputValue,search_contents:$("#search_contents").val(),search_dep:Ext.getCmp('save_dipartimento_id').getValue(),search_leg:Ext.getCmp('save_legislatura').getValue(),search_code:Ext.getCmp('search_code').getValue(),save_date:Ext.getCmp('save_date').value,save_description:Ext.getCmp('save_description').getValue()},
        success: function ( result, request ) {
        	save_search_win.destroy();
        	Ext.MessageBox.alert('${messageStatus}', '${messageStatusTextOperation}',function(){
        		winDocumentManager.close();
        		top.openRecord(Ext.util.Format.trim(result.responseText),true,'<%=searchArchive.getIdArchive()%>','<%=JsSolver.escapeSingleApex(searchArchive.getLabel())%>');
        		//top.resetSearchBuilder(); 
        		resetSearchBuilder();          	
            });
     	 },
        failure: function ( result, request ) {
     		Ext.MessageBox.alert('${messageStatus}', '${messageStatusError}');
     	 }
	});
}
function updateIndexPanel(){
	if(Ext.getCmp('index_searchBuilder')!=null && Ext.getCmp('index_searchBuilder')!=undefined){
		var items = Ext.getCmp('tabDocumentManager').items;
		var htmlToShow='<div><ul>';		
		for (var y = 0 ; y < items.getCount();y++) {
		    var comp = items.get(y);
		    if(comp.panel_type=='archive_panel'){
			     var title = comp.title;
			     var title = comp.title;
			     if(comp.titleForTree!=null && comp.titleForTree!=undefined){
			    	 title = comp.titleForTree;
    			 }
			     title = title.replace(/<br\s*[\/]?>/gi,' ');    			
				 if(title.indexOf('&nbsp;')!=-1){
	    			 title = title.substring(title.indexOf('&nbsp;')+'&nbsp;'.length,title.length);
	    		 }    
		    	 htmlToShow+='<li> - <!--start index voice-->'+title+'<!--end index voice--></li>';
			}			        			   			        			   
		}
		htmlToShow+='</ul></div>';
		Ext.getCmp('index_searchBuilder').update(htmlToShow);
	}
}
function resetSearchBuilder(){
	Ext.getCmp('east-panel_searcBuilder_dest').removeAll(true);
	Ext.getCmp('south-panel_searcBuilder_basket').removeAll(true);
	count_text_node = 0;
}

function executeAllSearch(){
    var fields = Ext.getCmp('search_panel_archive_all').getForm().items;
	var query = '';
	var order_by = '';
	var sort_type = '';
	var file_search = '';
	var date_range_query = '';
	for (var x = 0 ; x < fields.length; x ++){							
		if(fields.get(x).getName().indexOf('sf_')!=-1){																																																											
			if(fields.get(x).xtype=='checkboxgroup' || fields.get(x).xtype=='radiogroup'){
				var ckFields = fields.get(x).items;
				var ck_query='%2B(';
				for (var y = 0 ; y < ckFields.length; y ++){
					if(ckFields.get(y).getValue()){																												
						if(fields.get(x).getName().indexOf('sf_')!=-1){
							if(ckFields.get(y).getName().indexOf(fields.get(x).getName())!=-1){
								ck_query+=fields.get(x).getName().substring(3,fields.get(x).getName().length)+":"+ckFields.get(y).inputValue+'$';
							}else{								
								ck_query+=ckFields.get(y).getName()+":"+ckFields.get(y).inputValue+'$';
							}
						}else{
							ck_query+=+fields.get(x).getName()+":"+ckFields.get(y).inputValue+'$';
						}	
					}
				}
				ck_query+=")";
				if(ck_query!='%2B()'){
					query+=ck_query+'~';
				}																											
			}else if(fields.get(x).xtype=='datefield'){
				if(fields.get(x).value!=undefined && Ext.util.Format.trim(fields.get(x).value)!=''){
					date_range_query+=fields.get(x).getName().substring(3,fields.get(x).getName().length)+':'+fields.get(x).value+' ';
				}																						
			}else if(fields.get(x).xtype=='lookUpField'){
				//do nothing
			}else if(Ext.util.Format.trim(fields.get(x).getValue())!=''){
				//query+='%2B'+fields.get(x).getName().substring(3,fields.get(x).getName().length)+':'+fields.get(x).getValue()+' ';				
				var field_names = fields.get(x).getName();
				if(field_names.indexOf("+")!=-1){
					var names = field_names.split('+');
					query+="%2B("
					for (var y = 0 ; y < names.length; y ++){
						query+=names[y].substring(3,names[y].length)+':'+fields.get(x).getValue()+'$';
					}
					query+=")~";				
				}else{
					query+='%2B'+fields.get(x).getName().substring(3,fields.get(x).getName().length)+':'+fields.get(x).getValue()+'~';
				}
			}																																																							
		}else{
			if(fields.get(x).xtype=='checkboxgroup'){
					var ckFields = fields.get(x).items;
					var ck_query='%2B(';
					for (var y = 0 ; y < ckFields.length; y ++){
						if(ckFields.get(y).getValue()){
							if(fields.get(x).getName().indexOf('sf_')!=-1){
								if(ckFields.get(y).getName().indexOf(fields.get(x).getName())!=-1){
									ck_query+=fields.get(x).getName().substring(3,fields.get(x).getName().length)+":"+ckFields.get(y).inputValue+'$';
								}else{								
									ck_query+=ckFields.get(y).getName()+":"+ckFields.get(y).inputValue+'$';
								}
							}else{
								//query+='%2B'+fields.get(x).getName()+":"+ckFields.get(y).inputValue+' ';
								ck_query+=fields.get(x).getName()+":"+ckFields.get(y).inputValue+'$';
							}																																	
						}
					}
					ck_query+=")";
					if(ck_query!='%2B()'){
						query+=ck_query+'~';
					}																																	
			}else if(fields.get(x).xtype=='radiogroup'){
				var radioFields = fields.get(x).items;
				for (var y = 0 ; y < radioFields.length; y ++){
					if(radioFields.get(y).getValue() && radioFields.get(y).getName()=='order_by'){
						order_by = radioFields.get(y).inputValue;
						sort_type = radioFields.get(y).sort_type; 																															
					}else if(radioFields.get(y).getValue() && radioFields.get(y).getName()=='file_search'){
						file_search = radioFields.get(y).inputValue;
					}
				}	
				
			}
			
		}	
	}
	var id_filters = '';
	var find_sons = false;																																																																																															
	var panel_id ='thPanel_archive_'+(Ext.getCmp('tabPanel_searcBuilder').getActiveTab().id).substring(Ext.getCmp('tabPanel_searcBuilder').getActiveTab().id.lastIndexOf('_')+1,Ext.getCmp('tabPanel_searcBuilder').getActiveTab().id.length);
	if(Ext.getCmp(panel_id)!=undefined){
		var thFilters = Ext.getCmp(panel_id).items;	
		for (var x = 0 ; x < thFilters.length; x ++){
			if(Ext.getCmp(thFilters.get(x).id)!=null && Ext.getCmp(thFilters.get(x).id)!=undefined && Ext.getCmp(thFilters.get(x).id).getValue()){
				if(Ext.getCmp(thFilters.get(x).id).inputValue!='true'){
					id_filters+=Ext.getCmp(thFilters.get(x).id).inputValue+";";
				}else{
					find_sons = true;
				}
			}
		}
	}
	if(query!='' || id_filters!=''){							
		Ext.getCmp('center-panel_searcBuilder_archive_all').load({
			url: 'searchBuilder_adv_search.html?action=query_all&order_by='+order_by+'&sort_type='+sort_type+'&date_range_query='+date_range_query+'&find_sons='+find_sons+'&id_filters='+id_filters+'&file_search='+file_search+'&query='+encodeURI(query),
			nocache: true,
			text: 'Ricerca su tutti gli archivi in corso...', 
			timeout: 30, 
			scripts: true
		});
	}else{
		Ext.Msg.alert('Attenzione','Inserire almeno un termine di ricerca!');
	}
}

function executeSingleSearch(id_archive){
	    var fields = Ext.getCmp('search_panel_archive_'+id_archive).getForm().items;
		var query = '';
		var order_by = '';
		var sort_type = '';
		var file_search = '';
		var date_range_query = '';									
		for (var x = 0 ; x < fields.length; x ++){							
			if(fields.get(x).getName().indexOf('sf_')!=-1){																																																											
				if(fields.get(x).xtype=='checkboxgroup' || fields.get(x).xtype=='radiogroup'){
					var ckFields = fields.get(x).items;
					var ck_query='%2B(';
					for (var y = 0 ; y < ckFields.length; y ++){
						if(ckFields.get(y).getValue()){																												
							if(fields.get(x).getName().indexOf('sf_')!=-1){
								if(ckFields.get(y).getName().indexOf(fields.get(x).getName())!=-1){
									ck_query+=fields.get(x).getName().substring(3,fields.get(x).getName().length)+":"+ckFields.get(y).inputValue+'$';
								}else{								
									ck_query+=ckFields.get(y).getName()+":"+ckFields.get(y).inputValue+'$';
								}
							}else{
								//query+='%2B'+fields.get(x).getName()+":"+ckFields.get(y).inputValue+' ';
								ck_query+=+fields.get(x).getName()+":"+ckFields.get(y).inputValue+'$';
							}	
						}
					}
					ck_query+=")";
					if(ck_query!='%2B()'){
						query+=ck_query+'~';
					}																											
				}else if(fields.get(x).xtype=='datefield'){
					if(fields.get(x).value!=undefined && Ext.util.Format.trim(fields.get(x).value)!=''){
						date_range_query+=fields.get(x).getName().substring(3,fields.get(x).getName().length)+':'+fields.get(x).value+' ';
					}																						
				}else if(fields.get(x).xtype=='lookUpField'){
					//do nothing
				}else if(Ext.util.Format.trim(fields.get(x).getValue())!=''){
					var field_names = fields.get(x).getName();
					if(field_names.indexOf("+")!=-1){
						var names = field_names.split('+');
						query+="%2B("
						for (var y = 0 ; y < names.length; y ++){
							query+=names[y].substring(3,names[y].length)+':'+fields.get(x).getValue()+'$';
						}
						query+=")~";				
					}else{
						query+='%2B'+fields.get(x).getName().substring(3,fields.get(x).getName().length)+':'+fields.get(x).getValue()+'~';
					}
				}																																																							
			}else{
				if(fields.get(x).xtype=='checkboxgroup'){
						var ckFields = fields.get(x).items;
						var ck_query='%2B(';
						for (var y = 0 ; y < ckFields.length; y ++){
							if(ckFields.get(y).getValue()){
								if(fields.get(x).getName().indexOf('sf_')!=-1){
									if(ckFields.get(y).getName().indexOf(fields.get(x).getName())!=-1){
										ck_query+=fields.get(x).getName().substring(3,fields.get(x).getName().length)+":"+ckFields.get(y).inputValue+'$';
									}else{								
										ck_query+=ckFields.get(y).getName()+":"+ckFields.get(y).inputValue+'$';
									}
								}else{
									//query+='%2B'+fields.get(x).getName()+":"+ckFields.get(y).inputValue+' ';
									ck_query+=fields.get(x).getName()+":"+ckFields.get(y).inputValue+'$';
								}																																	
							}
						}
						ck_query+=")";
						if(ck_query!='%2B()'){
							query+=ck_query+'~';
						}																																	
				}else if(fields.get(x).xtype=='radiogroup'){
					var radioFields = fields.get(x).items;
					for (var y = 0 ; y < radioFields.length; y ++){
						if(radioFields.get(y).getValue() && radioFields.get(y).getName()=='order_by'){
							order_by = radioFields.get(y).inputValue;
							sort_type = radioFields.get(y).sort_type; 																															
						}else if(radioFields.get(y).getValue() && radioFields.get(y).getName()=='file_search'){
							file_search = radioFields.get(y).inputValue;
						}
					}	
					
				}
				
			}	
		}
		var id_filters = '';
		var find_sons = false;																																																																																															
		var panel_id ='thPanel_archive_'+(Ext.getCmp('tabPanel_searcBuilder').getActiveTab().id).substring(Ext.getCmp('tabPanel_searcBuilder').getActiveTab().id.lastIndexOf('_')+1,Ext.getCmp('tabPanel_searcBuilder').getActiveTab().id.length);
		if(Ext.getCmp(panel_id)!=undefined){
			var thFilters = Ext.getCmp(panel_id).items;	
			for (var x = 0 ; x < thFilters.length; x ++){
				if(Ext.getCmp(thFilters.get(x).id)!=null && Ext.getCmp(thFilters.get(x).id)!=undefined && Ext.getCmp(thFilters.get(x).id).getValue()){
					if(Ext.getCmp(thFilters.get(x).id).inputValue!='true'){
						id_filters+=Ext.getCmp(thFilters.get(x).id).inputValue+";";
					}else{
						find_sons = true;
					}
				}
			}
		}																																																																					
		if(query!='' || id_filters!=''){
			Ext.getCmp('center-panel_searcBuilder_archive_'+id_archive).removeAll(true);
			Ext.getCmp('center-panel_searcBuilder_archive_bbar_'+id_archive).removeAll(true);
			$("#nResults_archive_page_"+id_archive).html("");																					
			Ext.getCmp('center-panel_searcBuilder_archive_'+id_archive).load({ 
				url: 'searchBuilder_adv_search.html?action=search&idArchive='+id_archive+'&order_by='+order_by+'&sort_type='+sort_type+'&file_search='+file_search+'&query='+encodeURI(query)+'&date_range_query='+date_range_query+'&find_sons='+find_sons+'&id_filters='+id_filters,																								
				nocache: true,
				text: 'Ricerca in corso...', 
				timeout: 30, 
				scripts: true
			});
		}else{
			Ext.Msg.alert('Attenzione','Inserire almeno un termine di ricerca!');
		}
}
function hideArchiveTab(id_archive){	
	Ext.getCmp('tabPanel_searcBuilder').hideTabStripItem('search_builder_archive_'+id_archive);
	Ext.getCmp('search_builder_archive_'+id_archive).hide();
	Ext.getCmp('search_builder_archive_all').show();
	Ext.getCmp('ck_archiveactivate_tab_id_'+id_archive).setValue(false);
	Ext.getCmp('center-panel_searcBuilder_archive_'+id_archive).removeAll(true);
	Ext.getCmp('center-panel_searcBuilder_archive_bbar_'+id_archive).removeAll(true);
	$("#nResults_archive_page_"+id_archive).html("");
	$("#nResults_archive_"+id_archive).html('0');
}
function hideAllArchiveTab(id_archive){		
	var tab_archive_panels = Ext.getCmp('ck_archives').items;
	for (i=0; i<tab_archive_panels.getCount(); ++i) {
		var comp = tab_archive_panels.get(i);
		hideArchiveTab(comp.idArchive);	
	}
}
function showArchiveTab(id_archive){	
	Ext.getCmp('tabPanel_searcBuilder').unhideTabStripItem('search_builder_archive_'+id_archive);
	Ext.getCmp('search_builder_archive_'+id_archive).show();
	Ext.getCmp('ck_archiveactivate_tab_id_'+id_archive).setValue(true);
}
function showArchiveTabFromCK(id_archive){	
	Ext.getCmp('tabPanel_searcBuilder').unhideTabStripItem('search_builder_archive_'+id_archive);
}
function addGoogleTab(){
   var query = '';
   var fields = Ext.getCmp('search_panel_archive_all').getForm().items;
   for (var x = 0 ; x < fields.length; x ++){
	   if(fields.get(x).getName()=='sf_title+sf_contents'){
		   query+=fields.get(x).getValue();
	   }
   }	
   window.open('http://www.google.it/search?q='+query);
   //window.open('http://www.google.it/#hl=it&output=search&sclient=psy-ab&q='+query+'&oq='+query);
}
function openSearchConcept(id_archive){
	var storeConcettiSearch = new Ext.data.JsonStore({
		url : 'ajax/documental_adv_search.html',
		root : 'data',
		idProperty : 'id',
		totalProperty : 'totalCount',
		baseParams : {
			id_archive : id_archive,
			action : 'search',
			outputMode : 'skosAddFilterToSearch',
			limit : 40
		},
		fields : [ 'id', 'title', 'date', 'description', 'afunction', 'preview', 'myDip' ]
	});
	var gridConceptSearch = new Ext.grid.GridPanel({
		store : storeConcettiSearch,

		trackMouseOver : true,
		disableSelection : true,
		loadMask : {
			msg : 'caricamento in corso...',
			enable : true
		},
		// grid columns
		border : false,
		enableColumnResize : false,
		enableColumnMove : false,
		headerAsText : false,
		hideHeaders : true,
		enableColumnHide : false,
		header : false,
		enableHdMenu : false,
		plugins : expanderConcetti,
		collapsible : false,
		animCollapse : false,
		iconCls : 'icon-grid',
		autoHeight : true,
		stateful : true,
		autoScroll : false,
		autoSizeColumns : true,
		autoSizeGrid : true,
		autoExpandColumn : 'title',

		columns : [ expanderConcetti, {
			id : 'title',
			header : "Titolo",
			dataIndex : 'title'
		},
		{
			id : 'myDip',
			header : '',
			width : 23,
			flex : 0,
			fixed : true,
			dataIndex : 'myDip'
		} ],
		viewConfig : {
			forceFit : true
		}
	});
	var searchSkosPanel ={
            id:'searchbuilder_skos_search_panel',
            closable: false, 
            border:false,
   		 	items:[gridConceptSearch],
            autoScroll: true, 
            layout:'fit',
            tbar: [
                   'concetto: ',  
                    new Ext.ux.form.SearchField({
					    store: storeConcettiSearch,
					    id:'searchbuilder_query_concetto',
					    
					    mode: 'remote',		
					    width:200,
					    selectOnFocus:false,
					    emptyText:'inserisci concetto',
		    	    listeners: { 
		      			  'afterrender': function(elemento){  
			      			 $("#"+$(Ext.getDom(elemento)).attr("id")).parent('div').attr("style","");
		      			  } 
	      			  }
			       })
                 ],
                 bbar: new Ext.PagingToolbar({
                 	id:'searchbuilder_paging_bar_concetto',
                 	store: storeConcettiSearch,
                  pageSize: 20,
                  displayInfo: true,
                  displayMsg: '${skosWestpanelSearchFooter}',
                  emptyMsg: "${skosWestpanelSearchNoresults}"
                 })
 	};
	var search_concept_win = new Ext.Window({
        title: 'cerca un concetto',
        id:'search_concept_win',
        closable:true,
        width:600,
        height:350,
        modal:true,
        plain:true,
        layout: 'fit',
        items: [searchSkosPanel]
    });
	search_concept_win.show(this);
}
</script>
</head>
<body>
<div id="treeSearchBuilder" class="treeSearchBuilder"></div>
<div id="searchSearchBuilder" class="x-hide-display searchSearchBuilder"></div>
<div id="multiSearchVoc"></div>
<div id="tabDocumentManagerAnteprima" stile="border:1px solid;width:100%;padding:10px;backgroung-colo:#FFFFFF;"></div>
</body>
</html>
