<%@page import="java.util.ArrayList"%>
<%@page	import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page	import="org.springframework.security.core.context.SecurityContext"%>
<%@page import="com.openDams.security.UserDetails"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@include file="../locale.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%

ArrayList<Archives> documentalList = new ArrayList<Archives>();
ArrayList<Archives> skosList = new ArrayList<Archives>();
 
if(request.getAttribute("archiveList")!=null){ 
	   List<Object> archives = (List)request.getAttribute("archiveList");
	   Object[] archiveList = archives.toArray();
	   //for(int i=archiveList.length-1;i>-1;i--){ PRODUCE LISTA INVERSA
		   for(int i=0;i<archiveList.length;i++){
		   Archives archive = (Archives)archiveList[i];
		   if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.THESAURUS){
			   skosList.add(archive);
		   }else{
			   documentalList.add(archive);
		   }
	   }
}

%>
<%@page import="com.openDams.bean.ArchiveType"%>
<%@page import="com.openDams.bean.ArchiveIdentity"%><html
	xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>SQLXDams</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<link rel="icon" href="img/sqlxdicon.png" type="image/png" />
<link rel="shortcut icon" href="img/sqlxdicon.ico" type="image/png" />


<link href="css/StyleSheet.css" rel="stylesheet" type="text/css" />
<link href="css/confirm.css" rel="stylesheet" type="text/css" />
<link href="css/jquery.contextMenu.css" rel="stylesheet" type="text/css" />
<link href="css/resources/css/ext-all-notheme.css" rel="stylesheet" type="text/css"/>
<link href="css/resources/css/xtheme-blue.css" rel="stylesheet" type="text/css" id="theme_color"/>
<link href="css/style.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="js/ext-js/ux/css/Portal.css" />
<link type="text/css" href="css/redmond/jquery-ui-1.8.6.custom.css" rel="stylesheet" />
<link href="css/style_sky_blue.css" id="theme_color_tree" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="js/ext-js/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="js/ext-js/ext-all.js"></script>
<script type="text/javascript" src="js/ext-js/locale/ext-lang-it.js"></script>
<script type="text/javascript" src="js/ext-js/ux/Portal.js"></script>
<script type="text/javascript" src="js/ext-js/ux/PortalColumn.js"></script>
<script type="text/javascript" src="js/ext-js/ux/Portlet.js"></script>
<script type="text/javascript" src="js/ext-js/ux/RowExpander.js"></script>
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
<style type="text/css">body,html{overflow: hidden}</style>
<script type="text/javascript" src="js/application.js"></script>
<script type="text/javascript" src="js/jpath.js"></script>
<script type="text/javascript"> 
var globalOpt={};
globalOpt.document = {};
var myaWin=null;
top['grids'] = {}; 
loadScripts({base:"jquery",scripts:['jquery.autogrow','jquery.form']});
loadScripts({base:"application",scripts:['loading','utils','search','insert','jsonInjector','ds']});
	Ext.onReady(function(){ 
		  var ricercaForm = new Ext.FormPanel({
		        labelWidth: 75, // label settings here cascade unless overridden
		    	frame:true,
		        bodyStyle:'padding:5px 5px 0',
		        defaultType: 'textfield', 
		        items: [{
		                fieldLabel: 'ricerca libera',
		                name: 'contents',
		                allowBlank:false
		            } 
		        ],
		        buttons: [{	        	
		            text: 'cerca',
		            handler: function(){
		            	if(ricercaForm.getForm().isValid()){
		            		doMultiSearch('ajax/loadLastOccurences.html?contents='+ricercaForm.getForm().getValues().contents);
		                }
		            }
		        } ]
		    });

		  
	 
		 var stateProvider = 	  new Ext.state.CookieProvider({
			   expires: new Date(new Date().getTime()+(1000*60*60*24*365)) //1 year from now
			});
		Ext.state.Manager.setProvider(stateProvider);
	  
	    var viewport2 = new Ext.Viewport({
	        layout:'border',
	        items:[{
	            region:'west',
	            id:'west-panel-applications',
	            title:'Accessi per',
	            split:true,
	            width: 400,
                minSize: 175,
                maxSize: 400,
	            collapsible: true,
	            margins:'5 0 5 5',
	            cmargins:'5 5 5 5',
	            layout:'accordion',
	            layoutConfig:{
	                animate:true
	            },
	            items: [<%
			for(int i=0;i<skosList.size();i++){
	  		 Archives archive =  skosList.get(i); %>
	            {
	            	contentEl: 'tree<%=i%>' ,
                    id:'tree_accordion<%=i%>',
                    title: '<%=archive.getDescription()%>',
                    border: false,
                    autoScroll: true,
                    <%if(i==0){%>
                    autoLoad:{
		         		  url:'ajax/skosDesktop.html?idArchive=<%=archive.getIdArchive()%>',
		         		  discardUrl: false,
		         		  nocache: true,
		         		  text: 'caricamento in corso...',
		         		  timeout: 30,
		         		  scripts: true
		     		  	},
		     		<%}%>  	
                    iconCls: 'navigation_img',
                    tbar: new Ext.Toolbar({
					                  	   items : [  '->',
					                                 new Ext.Button({
					            					        text    : 'accedi a: <%=archive.getDescription()%>',
					            					        handler : function(btn) {
					            					        	parent.addTab('<%=archive.getLabel()%>','<%=archive.getIdArchive()%>','skosWorkspace.html?idArchive=<%=archive.getIdArchive()%>');
					            					        }
					            					  }) 
					                             ]
                        				 })
		     		<%if(i>0){%>
                 	,listeners:{
							   'expand':function(){ 
						   							if(globalOpt.archive_<%=archive.getIdArchive()%>_loaded==undefined){
						   								   globalOpt.archive_<%=archive.getIdArchive()%>_loaded=true;
														   Ext.getCmp('tree_accordion<%=i%>').load({
															  url:'ajax/skosDesktop.html?idArchive=<%=archive.getIdArchive()%>',
											         		  discardUrl: false,
											         		  nocache: true,
											         		  text: 'caricamento in corso...',
											         		  timeout: 30,
											         		  scripts: true								  
														   });
						   							}
												  }
							  }
                 	<%}%>	 
                    
	            }<%if(i<(skosList.size()-1)){%>,<%}%><%
	            }%>]
	        },
			new Ext.TabPanel({
				region:'center',
			    deferredRender: true,			    
			    activeTab: 0,
			    items: [
						{   
							title: 'Documentazione',
							closable: false,
							autoScroll: true,
						    xtype:'portal',
						    region:'center',
						    id:'center-panel-applications',					
						    margins:'5 5 5 0',
						    items:[
							<%if(request.getAttribute("archiveList")!=null){ 
								   for(int i=0;i<documentalList.size();i++){
									   Archives archive =  documentalList.get(i); 
								   		%>{
							                //columnWidth:.32,
							                width:350,
							                style:'padding:10px 0 10px 10px',							                
							                items:[{
							                	id:'portlet<%=i%>',
							                    title: '<img width="12" height="12" src="img/archive_img/archive_<%=archive.getIdArchive()%>.gif" border="0"/>&nbsp;<a href="#n" class="linkArchiveTitle" onclick="openArchiveTab(\'<%=archive.getIdArchive()%>\',\'<%=JsSolver.escapeSingleApex(archive.getLabel())%>\')"><%=archive.getLabel()%></a>',
							                    closable:false, 
							                    collapsed:true,
							                    listeners:{
													   'expand':function(){ 
														   Ext.getCmp('grid<%=i%>').getStore().load({
															   callback:function(){
																   changeNumeroElementi('portlet<%=i%>',this.reader.jsonData.totalCount);
																   },
															   params:{"start":0, "limit":15}
														   });
																//loadGrids(top['grids']['grid<%=i%>']);
														}
													},
							           			items:[         
									                    new Ext.grid.GridPanel({									                    	
									                    	id:'grid<%=i%>',										                
																	store : new Ext.data.GroupingStore({
																		 
																		url : 'ajax/loadLastOccurences.html',
																		reader : new Ext.data.JsonReader({
																			root : 'data',
																			totalProperty : 'totalCount',
																			id : 'id',
																			fields : [ 'id', 'title', 'date', 'description', 'datenormal' ,'preview','myDip']
																		}), 
																		groupDir : 'DESC',
																		sortInfo : {
																			field : 'datenormal',
																			direction : 'DESC'
																		},
																		groupField : 'datenormal',
																		baseParams : {
																			idArchive : <%=archive.getIdArchive()%>,
																			action : 'json_data',
																			department : top.Application['user'].dipartimento,
																			limit : 15
																		},listeners : {
																				'load' : function() {								
																				//$('#searchresultsGrid<%=archive.getIdArchive()%>').find(".x-grid3-row-table").addClass("archive_<%=archive.getIdArchive()%>_color");
																				//$('#grid<%=i%>').find("tr").addClass("archive_<%=archive.getIdArchive()%>_color");								
																		}			
																	}
																	}),
																   listeners:{
																		'afterrender':function(){
																			top['grids']['grid<%=i%>']=this;
												                    		    top.Message['Bus<%=archive.getIdArchive()%>'] = new Ext.util.Observable();
												                    		    top.Message['Bus<%=archive.getIdArchive()%>'].addEvents('savedoc');
												                    		    top.Message['Bus<%=archive.getIdArchive()%>'].on('savedoc', function() {
												                    				this.getStore().reload({
																						   callback:function(){
																							   changeNumeroElementi('portlet<%=i%>',this.reader.jsonData.totalCount);
																							}
																					   });
												                    			}, this);
													                    	}
												                    },
																	trackMouseOver : false,
																	disableSelection : true,
																	loadMask : {
																		msg : 'caricamento in corso...',
																		enable : true
																	},
																	// grid columns
																	border : false,
																	header : false,
																 	hideHeaders :true,
																	collapsible : false,
																	animCollapse : false,
																	iconCls : 'icon-grid',
																	autoHeight : true,
																	stateful : true,
																	autoScroll : false,
																	autoSizeColumns: true,
																	autoSizeGrid: true,
																	autoExpandColumn : 'title',																	
																	columns : [  {
																		id : 'preview',
																		header : '',
																		width : 20,
																		flex : 0,
																		fixed : true,
																		dataIndex : 'preview'
																	}, {
																		id : 'title',
																		header : "Titolo",
																		dataIndex : 'title'
																	}, {
																		id : 'laData',
																		dataIndex : 'date',
																		hidden : true,
																		hideable : false 
																	} , {
																		id : 'datenormal',
																		dataIndex : 'datenormal',
																		hidden : true,
																		hideable : false 
																	} ,{
																		id : 'myDip',
																		header : '',
																		width : 23,
																		flex : 0,
																		fixed : true,
																		dataIndex : 'myDip'
																	}],
																	view : new Ext.grid.GroupingView({
																		forceFit : true,
																		groupTextTpl : '{[ values.rs[0].data["date"] ]} ({[values.rs.length]} {[values.rs.length > 1 ? "elementi" : "elemento"]})'
																	}),
																	viewConfig : {
																		forceFit : true
																	}
						
																})
									                          ],
							                    tbar:[new Ext.Button({
						     				 		text:'inserisci un documento', 
						     				        handler: function() {
						     				        	addRecord(<%=archive.getIdArchive()%>,'inserisci: <%=archive.getDescription()%>','<%=JsSolver.escapeSingleApex(archive.getLabel())%>','grid<%=i%>');
						     				  	}}),'|',new Ext.Button({
						     				 		text:'accedi all\'archivio', 
						     				        handler: function() {
								                    	 <%if(archive.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.THESAURUS){%>
								                    	 	parent.addTab('<%=JsSolver.escapeSingleApex(archive.getLabel())%>','<%=archive.getIdArchive()%>','skosWorkspace.html?idArchive=<%=archive.getIdArchive()%>');
								                    	 <%}else if(archive.getArchiveIdentities().getIdArchiveIdentity()== ArchiveIdentity.HIERARCHIC){%>
								                    	 	parent.addTab('<%=JsSolver.escapeSingleApex(archive.getLabel())%>','<%=archive.getIdArchive()%>','hierarchicWorkspace.html?idArchive=<%=archive.getIdArchive()%>');
								                    	 <%}else {%>
								                    		parent.addTab('<img width="12" height="12" width="12" height="12" src="img/archive_img/archive_<%=archive.getIdArchive()%>.gif" border="0"/>&nbsp;<%=JsSolver.escapeSingleApex(archive.getLabel())%>','<%=archive.getIdArchive()%>','documentalWorkspace.html?idArchive=<%=archive.getIdArchive()%>');
									                     <%}%>
						     				  	
						     				 	 }
						     				 	 }),'->', generateFilters('grid<%=i%>','portlet<%=i%>') ],
							                    tools: [{
							                    	id:'gear',
							            	        alt:'Accedi all\'archivio',
							            	        handler: function(){
							                    	 <%if(archive.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.THESAURUS){%>
							                    	 	parent.addTab('<%=JsSolver.escapeSingleApex(archive.getLabel())%>','<%=archive.getIdArchive()%>','skosWorkspace.html?idArchive=<%=archive.getIdArchive()%>');
							                    	 <%}else if(archive.getArchiveIdentities().getIdArchiveIdentity()== ArchiveIdentity.HIERARCHIC){%>
							                    	 	parent.addTab('<%=JsSolver.escapeSingleApex(archive.getLabel())%>','<%=archive.getIdArchive()%>','hierarchicWorkspace.html?idArchive=<%=archive.getIdArchive()%>');
							                    	 <%}else {%>
							                    		parent.addTab('<img src="img/archive_img/archive_<%=archive.getIdArchive()%>.gif" border="0"/>&nbsp;<%=JsSolver.escapeSingleApex(archive.getLabel())%>','<%=archive.getIdArchive()%>','documentalWorkspace.html?idArchive=<%=archive.getIdArchive()%>');
								                     <%}%>
							                    		
							            	        }
							            	    }]
							                  } ]
							            	}<%
							         	
								   		/* TODO: questo non va bene perchè se inserisco un nuovo archivio dopo il 16 devo modificare il -2*/
						
							         	if(i<documentalList.size()-1){%>,<%}%>
							      
									<%}%>
								<%}%>
						  ]
						}
					    ,
						{
						    
						    title: 'Ricerca semplice',
						    closable: false,
						    autoScroll: true,
						    id: 'multiSearchTab',
						    autoLoad:{
				         		  url:'multiSearch.html',
				         		  discardUrl: false,
				         		  nocache: true,
				         		  text: 'caricamento in corso...',
				         		  timeout: 30,
				         		  scripts: true
				     		}
						 }
							
			            ]
			})
	        ]
	    });
	     
		<%--if(request.getAttribute("archiveList")!=null){ 
			   List<Object> archives = (List)request.getAttribute("archiveList");
			   Object[] archiveList = archives.toArray();
			   for(int i=0;i<archiveList.length;i++){
				   Archives archive = (Archives)archiveList[i];
				   if(archive.getArchiveTypes().getIdArchiveType()!=ArchiveType.THESAURUS){
				   %>	  
				//   Ext.getCmp('grid<%=i%>').getStore().load({params:{start:0, limit:15}});
			   <%  }
			   }
	}--%>
	//parent.closeLoadingApplications();
	});
 	   function loadGrids(obj){
			for ( var idGrid in obj) { 
				obj[idGrid].getStore().load({params:{"start":0, "limit":15}}); 
			} 
 	 	}
	 	function openArchiveTab(id_archive,archive_label){
	 		parent.addTab('<img width="12" height="12" src="img/archive_img/archive_'+id_archive+'.gif" border="0"/>&nbsp;'+archive_label,id_archive,'documentalWorkspace.html?idArchive='+id_archive);
		}
	 </script>

</head>
<body>
	<div id="ricerca" class="x-hide-display ricerca"></div>
	<div id="tree1" class="x-hide-display tree"></div>
	<div id="tree0" class="x-hide-display tree"></div>
	<div id="multiSearchTab" class="x-hide-display tree"></div>

</body>
</html>