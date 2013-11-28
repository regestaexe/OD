<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.ArrayList"%>
<%@page import="com.openDams.bean.Archives"%>
<%@page import="com.openDams.bean.ArchiveIdentity"%>
<%@page import="com.openDams.bean.ArchiveType"%>
<%@page import="com.openDams.security.UserDetails"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="org.springframework.security.core.context.SecurityContext"%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="java.util.Collection"%>
<%@page import="org.springframework.security.core.GrantedAuthority"%>
<%@page import="com.openDams.security.RoleTester"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="java.math.BigInteger"%>

<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Departments"%>
<%@page import="com.openDams.bean.Companies"%><html xmlns="http://www.w3.org/1999/xhtml" >
<head>
<title>openDams</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="../img/sqlxdicon.ico" type="image/png" />
<link rel="shortcut icon" href="../img/sqlxdicon.ico" type="image/png" />
<link href="../css/style_sky_blue.css" id="theme_color_tree" rel="stylesheet" type="text/css" />
<link href="../css/StyleSheet.css" rel="stylesheet" type="text/css" />
<link href="../css/confirm.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery.contextMenu.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="../css/resources/css/ext-all-notheme.css" />
<link rel="stylesheet" type="text/css" id="theme_color" href="../css/resources/css/xtheme-blue.css" />
<link href="../css/style.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="../js/ext-js/ux/css/RowEditor.css" /> 
<link rel="stylesheet" href="../js/codemirror/lib/codemirror.css" />
<link rel="stylesheet" href="../js/codemirror/mode/xml/xml.css" />
<script src="../js/codemirror/lib/codemirror.js"></script>
<script src="../js/codemirror/mode/xml/xml.js"></script>  
<style type="text/css">
        html, body {
            font: normal 11px verdana;
        }
        #main-panel td {
            padding:5px;
        }
    </style>

<script src="../js/jquery-1.4.2.min.js" type="text/javascript"></script>
<script type="text/javascript" src="../js/ext-js/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="../js/ext-js/ext-all.js"></script>
<script type="text/javascript" src="../js/ext-js/ux/RowEditor.js"></script>
<script type="text/javascript" src="../js/ext-js/ux/SearchField.js"></script>
<script type="text/javascript" src="../js/ext-js/ux/CheckColumn.js"></script>
<script type="text/javascript" src="../js/application.js"></script>
<%@include file="../locale.jsp"%>   
<script type="text/javascript">
    Ext.onReady(function(){
    	var stateProvider =   new Ext.state.CookieProvider({
			   expires: new Date(new Date().getTime()+(1000*60*60*24*365)) //1 year from now
		});
		Ext.state.Manager.setProvider(stateProvider);   
		var fm = Ext.form;
		
		 var Employee = Ext.data.Record.create([
                        {
                            name: 'username',
                            type: 'string'
                        },
                        {
                            name: 'name',
                            type: 'string'
                        },{
                            name: 'lastname',
                            type: 'string'
                        },
                        {
                            name: 'ref_id_company',
                            type: 'string'
                        },
                        {
                            name: 'ref_id_department',
                            type: 'string'
                        },{
                            name: 'email',
                            type: 'string'
                        }, {
                            name: 'birth_date',
                            type: 'date',
                            dateFormat: 'n/j/Y'
                        },{
                            name: 'telephone_number',
                            type: 'string'
                        },{
                            name: 'active',
                            type: 'bool'
                        },
                        {
                            name: 'language',
                            type: 'string'
                        }
                        ]);
                        
                        var storeUsers = new Ext.data.JsonStore({
                    		        // store configs
                    		        autoDestroy: true,
                    		        url: 'users.html',
                    		        remoteSort: false,
                    		        sortInfo: {
                    		            field: 'lastname',
                    		            direction: 'ASC'
                    		        },
                    		        storeId: 'usersStore',
                    		        idProperty: 'id',
                    		        root: 'data',
                    		        totalProperty: 'total',
                    		        baseParams : {
                    		    		limit : 20
                    		    	},
                    		        fields: [
										{name: 'id'},
                    		       		{name: 'username',type:'string'},
               							{name: 'name',type:'string'},
               							{name: 'lastname',type:'string'},
               							{name: 'ref_id_company'},
               							{name: 'ref_id_department'},
               							{name: 'email',type:'string'},
               							{name: 'birth_date'},
               							{name: 'telephone_number'},
               							{name: 'active'},
               							{name: 'language'}
                    		        ]
                    		    });

                        var editor = new Ext.ux.grid.RowEditor({
                            saveText: 'Salva',
                            cancelText:'Annulla'
                        });

                        editor.on({
                        	  scope: this,
                        	  afteredit: function(roweditor, changes, record, rowIndex) {
                      	          var userUrl = '';
                      	          if(record.get('id')!=undefined){
                      	        		userUrl='users.html?action=update&id_user='+record.get('id');
                              	  }else{
                              			userUrl='users.html?action=add';
                                  }
      							Ext.Ajax.request({
							          url : userUrl,
					                  method: 'POST',
					                  params :changes,
					                  success: function ( result, request ) {
	                                      storeUsers.reload();
	                                      grid.getView().refresh();
					                      fn_AKExt(result.responseText, 'Success');
						              },
						                  failure: function ( result, request ) {
						            	  storeUsers.reload();
						            	  grid.getView().refresh();
						                  fn_AKExt(result.responseText, 'Error');
						              }
								 });
                        	  }
                        	});
                        	                       
                        var grid = new Ext.grid.GridPanel({
                            store: storeUsers,
                            width: 600,
                            margins: '0 5 5 5',
                            plugins: [editor],
                           /* view: new Ext.grid.GroupingView({
                                markDirty: false
                            }),*/
                            tbar: [{
                                iconCls: 'icon-user-add',
                                text: 'Aggiungi utente',
                                handler: function(){
                                    var e = new Employee({
                                        username: '',
   										name: '',  
   										lastname: '',  
   										ref_id_company: '',  
   										ref_id_department: '',  
   										email: '',  
   										birth_date: '',  
   										telephone_number: '',  
   										active: true,  
   										language: 'IT'
                                    });
                                    editor.stopEditing();
                                    storeUsers.insert(0, e);
                                    grid.getView().refresh();
                                    grid.getSelectionModel().selectRow(0);
                                    editor.startEditing(0);
                                }
                            },{
                                ref: '../removeBtn',
                                iconCls: 'icon-user-delete',
                                text: 'Rimuovi utente',
                                disabled: false,
                                handler: function(){
                                    editor.stopEditing();
                                    var s = grid.getSelectionModel().getSelections();
                                    for(var i = 0, r; r = s[i]; i++){
                                    	Ext.get("userDetails").html='';
                                        Ext.Ajax.request({
										          url : 'users.html',
								                  method: 'POST',
								                  params :{action:'delete',id_user:r.get('id')},
								                  success: function ( result, request ) {
								                	  storeUsers.remove(r);
								                	  storeUsers.load();
				                                      grid.getView().refresh();
								                      fn_AKExt(result.responseText, 'Success');
									              },
									                  failure: function ( result, request ) {
									            	   grid.getView().refresh();
									                   fn_AKExt("Si è verificato un errore durante l'esecuzione dell'operazione richiesta!", 'Error');
									              }
										});
                                    }
                                }
                            },
							'->'
                            ,
                            'cerca: ', 
                            new Ext.ux.form.SearchField({
        					    store: storeUsers, 
        					    id:'query_field', 
        					    width:180, 
        					    emptyText:''
        				      })
                            ],
                            columns: [
                            new Ext.grid.RowNumberer(),
                             {
                                id: 'username',
                                header: 'User',
                                dataIndex: 'username',
                                width: 100,
                                sortable: true,
                                editor: {
                                    xtype: 'textfield',
                                    allowBlank: false
                                }
                            },{
                                id: 'name',
                                header: 'Nome',
                                dataIndex: 'name',
                                width: 100,
                                sortable: true,
                                editor: {
                                    xtype: 'textfield',
                                    allowBlank: false
                                }
                            },{
                                id: 'lastname',
                                header: 'Cognome',
                                dataIndex: 'lastname',
                                width: 100,
                                sortable: true,
                                editor: {
                                    xtype: 'textfield',
                                    allowBlank: false
                                }
                            },{
                                id: 'ref_id_company',
                                header: 'Società',
                                dataIndex: 'ref_id_company',
                                width: 200,
                                sortable: true,
                                editor: new fm.ComboBox({
                                	allowBlank: false,
                                    typeAhead: true,
                                    triggerAction: 'all',
                                    transform: 'ref_id_company',
                                    lazyRender: true,
                                    listClass: 'x-combo-list-small',
                                    editable:false
                                })
                            },{
                                id: 'ref_id_department',
                                header: 'Dipartimento',
                                dataIndex: 'ref_id_department',
                                width: 200,
                                sortable: true,
                                editor: new fm.ComboBox({
                                    typeAhead: true,
                                    triggerAction: 'all',
                                    transform: 'ref_id_department',
                                    lazyRender: true,
                                    listClass: 'x-combo-list-small',
                                    editable:false
                                })
                            },{
                                header: 'Email',
                                dataIndex: 'email',
                                width: 200,
                                sortable: true,
                                editor: {
                                    xtype: 'textfield',
                                    allowBlank: true,
                                    vtype: 'email'
                                }
                            },{
                                xtype: 'datecolumn',
                                id: 'birth_date',
                                header: 'Data nascita',
                                dataIndex: 'birth_date',
                                format: 'm/d/Y',
                                width: 100,
                                sortable: true,
                                groupRenderer: Ext.util.Format.dateRenderer('M y'),
                                editor: {
                                    xtype: 'datefield',
                                    allowBlank: true,                                   
                                    maxValue: (new Date()).format('m/d/Y')
                                }
                            },{
                                id: 'telephone_number',
                                header: 'Telefono',
                                dataIndex: 'telephone_number',
                                width: 100,
                                sortable: true,
                                editor: {
                                    xtype: 'textfield',
                                    allowBlank: true
                                }
                            },{
                                xtype: 'booleancolumn',
                                header: 'Active',
                                dataIndex: 'active',
                                align: 'center',
                                width: 50,
                                trueText: 'Yes',
                                falseText: 'No',
                                editor: {
                                    xtype: 'checkbox'
                                }
                            },{
                                id: 'language',
                                header: 'Lingua',
                                dataIndex: 'language',
                                width: 60,
                                sortable: true,
                                editor: new fm.ComboBox({
                                    typeAhead: true,
                                    triggerAction: 'all',
                                    transform: 'language',
                                    lazyRender: true,
                                    listClass: 'x-combo-list-small',
                                    editable:false
                                })
                            }
                            ],
                            bbar: new Ext.PagingToolbar({
                                id:'paging_bar',
                                store: storeUsers, 
                                pageSize: 20,
                                displayInfo: true,
                                displayMsg: '${skosWestpanelSearchFooter}',
                                emptyMsg: "${skosWestpanelSearchNoresults}"
                                })
                            ,
                            listeners: {
                    		            render: {
                    		                fn: function(){
                    		    					storeUsers.load();
                    		                }
                    		            }
                    		        }
                        });
                        grid.getSelectionModel().on('rowselect', function(sm, rowIdx, r) {
                            if(r.get('id')!=undefined){
	                            Ext.get("userDetails").load(
				                    {
				                        url:"user_archives.html?id_user="+r.get('id'),
				                        text:"loading",
				                        scripts:true
				                    }
				                );
                            }else{
                            	Ext.getCmp('but_save').setDisabled(true);
                                Ext.getCmp('but_reset').setDisabled(true);
                            }
                        });
                        var usersPanel = new Ext.Panel({
                            layout: 'fit',
                            layoutConfig: {
                                columns: 1
                            },
                            width:'100%',
                            height: 200,
                            items: [grid]
                        });

		    
		var panelUsers = new Ext.Panel({
	         layout: 'border',
	         border : false,
	         defaults: {autoScroll: true},
	         split: true,
	         items: [
		        new Ext.Panel({
		               region: 'north',
		               id: 'usersList',		               
		               width: '100%',
		               height: '100%',
		               margins: '0 0 0 5',
		               layout: 'fit',
		               items:[usersPanel] 
		        }),
	          	new Ext.Panel({
		               region: 'center',
		               id: 'userDetails',
		               autoScroll: true,
		               width: '100%',
		               height:300,
		               layout: 'fit',
		               margins: '0 0 0 0'
	        	}),
	        	new Ext.Panel({
	        		region: 'south',
		        	margins: '0 0 0 0',
		        	border: false,
		        	buttons: [{
			        	id:'but_save',
			        	disabled:true,
		                text: 'Salva configurazione archivi',
		                handler: function(){
		                   var table = Ext.getCmp('archive-table-panel');
		                   if(table.getForm().isValid()){
		                	   var s = grid.getSelectionModel().getSelections();
                               for(var i = 0, r; r = s[i]; i++){
                                Ext.Ajax.request({
							          url : 'user_archives.html?id_user='+r.get('id')+'&'+table.getForm().getValues(true),
					                  method: 'POST',
					                  params :{action:'save'},
					                  success: function ( result, request ) {
					                      fn_AKExt(result.responseText, 'Success');
						              },
						                  failure: function ( result, request ) {
						                  fn_AKExt("Si è verificato un errore durante l'esecuzione dell'operazione richiesta!", 'Error');
						              }
								});
                               }
		                   }
		                }
		            },{
		            	id:'but_reset',
			        	disabled:true,
		                text: 'Reimposta',
		                handler: function(){
		            		var table = Ext.getCmp('archive-table-panel');
		            		table.getForm().reset();
		                }
		            }]
	        	})

	       	]		       
	    });

		var panelIndexDetails = new Ext.Panel({
    		layout: 'border',
	        border : false,
	        defaults: {autoScroll: true},
	        split: true,
            id:'panelIndexDetails',
            items:[
					new Ext.Panel({
					    region: 'center',
					    id: 'indexDetailsCenter',
					    autoScroll: true,
					    width: '100%',
					    layout: 'fit',
					    margins: '0 0 0 0',
			            contentEl: 'indexDetails'
					}),
					new Ext.Panel({
						region: 'south',
			        	margins: '0 0 0 0',
			        	border: false,
			        	buttons: [{
					 	        	 id:'rebuild_index',
					 	        	 disabled:true,
					                 text: 'Rigenera indici',
					                 handler: function(){
										        		//Ext.get('rebuild_index').dom.disabled = true;
										        		//Ext.get('rebuild_title').dom.disabled = true;
										        		Ext.getCmp('rebuild_index').setDisabled(true);
										        		Ext.getCmp('rebuild_title').setDisabled(true);
										        		Ext.getCmp('offline_archive').setDisabled(true);
										        		Ext.getCmp('end_point_publisher').setDisabled(true);
										        		Ext.getCmp('indexList').setDisabled(true);
										        		$('#img_online').attr('src','../img/offline.gif');
										        		$("#view_index_conf").hide();
										    			$("#view_titles_conf").hide();
										    			$("#xml_index").hide();
										    			$("#xml_titles").hide();										        		
										        		startProgressBar();
										        		Ext.Ajax.request({
										        	           url: 'indexList.html?action=rebuild_index&idArchive='+$("input[name='index_idArchive']").attr("value"),
										        	           success: function(r) {
										        					Ext.TaskMgr.start(eval('task'+$("input[name='index_idArchive']").attr("value")));
										        	           }
										        		});
					                 		   }
					             },{
					             	id:'rebuild_title',
					 	        	disabled:true,
					                text: 'Rigenera titoli',
					                handler: function(){
									            		//Ext.get('rebuild_index').dom.disabled = true;
										        		//Ext.get('rebuild_title').dom.disabled = true;
										        		Ext.getCmp('rebuild_index').setDisabled(true);
										        		Ext.getCmp('rebuild_title').setDisabled(true);
										        		Ext.getCmp('offline_archive').setDisabled(true);
										        		Ext.getCmp('end_point_publisher').setDisabled(true);
										        		Ext.getCmp('indexList').setDisabled(true);
										        		$('#img_online').attr('src','../img/offline.gif');
										        		$("#view_index_conf").hide();
										    			$("#view_titles_conf").hide();
										    			$("#xml_index").hide();
										    			$("#xml_titles").hide();
										        		startTitleProgressBar();
										        		Ext.Ajax.request({
										        	           url: 'rebuildTitle.html?action=rebuild_title&idArchive='+$("input[name='index_idArchive']").attr("value"),
										        	           success: function(r) {
										        					Ext.TaskMgr.start(eval('taskT'+$("input[name='index_idArchive']").attr("value")));
										        	           }
										        		});
					                 }
					             },{
						             	id:'end_point_publisher',
						 	        	disabled:true,
						                text: 'Pubblica su EndPoint',
						                text    : 'pubblica',
	        					        menu: []
						             },{
						             	id:'offline_archive',
						 	        	disabled:true,
						                text: 'Metti offline',
						                handler: function(){
						                                 if(this.text=='Metti offline'){
						                                	Ext.Ajax.request({
										        	           url: 'archiveStatus.html?action=offline&idArchive='+$("input[name='index_idArchive']").attr("value"),
										        	           success: function(r) {
						                                			Ext.getCmp('offline_archive').setText('Metti online');
						                                			$('#img_online').attr('src','../img/offline.gif');
										        	           }
										        			});
						                                	 
								                         }else{
								                        	 Ext.Ajax.request({
										        	           url: 'archiveStatus.html?action=online&idArchive='+$("input[name='index_idArchive']").attr("value"),
										        	           success: function(r) {
							                        		 		Ext.getCmp('offline_archive').setText('Metti offline');
							                        		 		$('#img_online').attr('src','../img/online.gif');
										        	           }
										        			});
								                         }                                                         											        
						                 }
						             }]
					})
                   ]
        });
		var panelIndexes = new Ext.Panel({
	         layout: 'border',
	         border : false,
	         defaults: {autoScroll: true},
	         split: true,
	         items: [
				new Ext.Panel({
				    region: 'west',
				    id: 'indexList',
				    autoScroll: true,
				    layout: 'fit',
				    split:true,
		            width: 400,
	                minSize: 175,
	                maxSize: 400,
		            collapsible: true,
		            margins:'5 0 5 5',
		            cmargins:'5 5 5 5',
		            autoLoad:{
		         		  url:'indexList.html',
		         		  discardUrl: false,
		         		  nocache: true,
		         		  text: 'caricamento in corso...',
		         		  timeout: 30,
		         		  scripts: true
		     		  	}
				})
	    	    ,
	          	new Ext.Panel({
		               region: 'center',
		               autoScroll: true,
		               layout: 'fit',
		               margins: '0 0 0 0',
		               items:[panelIndexDetails]
	        	})

	       	]		       
	    });
		<%
		ArrayList<Archives> documentalList = new ArrayList<Archives>();
		ArrayList<Archives> skosList = new ArrayList<Archives>();
		ArrayList<Archives> externalArchiveList = new ArrayList<Archives>();
		Archives messageManager = null;
		Archives help = null;
		Archives basket = null;
		if(request.getAttribute("archiveList")!=null){ 
			   List<Object> archives = (List)request.getAttribute("archiveList");
			   Object[] archiveList = archives.toArray();
				   for(int i=0;i<archiveList.length;i++){
				   Archives archive = (Archives)archiveList[i];
				   if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.THESAURUS){
					   skosList.add(archive);
				   }else if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.DOCUMENTAL){
					   documentalList.add(archive);
				   }else if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.MESSAGE){
					   messageManager = archive;
				   }else if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.BASKET){
					   basket = archive;
				   }else if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.WEB_EXTERNAL){
					   externalArchiveList.add(archive);
				   }else if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.HELP){
					   help = archive;
				   }
			   }
		}

		%>	    
        var viewportAdminTools = new Ext.Viewport({
            layout: 'border',
            border : false,
            id:'admin-tools',
            items: [
		            new Ext.TabPanel({
		                region: 'center',
		                deferredRender: false,
		                activeTab: 0,
		                border : false,
		                items: [
						        {
				                    contentEl: 'centerUser',				                    
				                    title: 'Gestione utenti',
				                    autoScroll: true,
				                    layout:'fit',            
				                    items:[panelUsers],
				                    border : false,
				                    text:"loading"
				                }
								<%if(skosList.size()>0 || documentalList.size()>0){%>
				                /*, 
				                {
				                    contentEl: 'centerDepartments',
				                    title: 'Gestione dipartimenti',
				                    autoScroll: true,
				                    border : false,
				                    autoLoad:{"url":"departments.html","scripts":true}
				                }*/
				                /*, 
				                {
				                    contentEl: 'centerArchives',
				                    title: 'Gestione archivi',
				                    autoScroll: true,
				                    border : false,
				                    autoLoad:{"url":"createArchive.html","scripts":true}
				                }*/, 
				                {
				                    contentEl: 'centerIndexes',
				                    title: 'Gestione indici e titoli di ricerca',
				                    autoScroll: true,
				                    layout:'fit',            
				                    items:[panelIndexes],
				                    border : false,
				                    text:"loading"
				                }, 
				                {
				                    contentEl: 'centerSearchConf',
				                    id: 'centerSearchConfContainer',
				                    title: 'Configurazione ricerca',
				                    autoScroll: true,
				                    layout:'fit',            
				                    border : false,
					                listeners:{
										   'activate':function(){ 
															   Ext.getCmp('centerSearchConfContainer').load({
																   url:'editXmlConf.html?action=show_xml&xml_type=search',
													         		  discardUrl: false,
													         		  nocache: true,
													         		  text: 'caricamento in corso...',
													         		  timeout: 30,
													         		  scripts: true 
															   });
					   									}
									}
				                }, 
				                {
				                    contentEl: 'centerRelationConf',
				                    id: 'centerRelationConfContainer',
				                    title: 'Configurazione relazioni',
				                    autoScroll: true,
				                    layout:'fit',            
				                    border : false,
				                    listeners:{
											   'activate':function(){ 
																   Ext.getCmp('centerRelationConfContainer').load({
																	   	  url:'editXmlConf.html?action=show_xml&xml_type=relation',
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
		                ]
		            })
            ]
        });

    });
    function fn_AKExt( message, title ){
    	   Ext.Msg.show({
    	      title: title,
    	      msg: message ,
    	      buttons: Ext.MessageBox.OK,
    	      icon: Ext.MessageBox.INFO
    	     });
    }
    	    
    </script>
</head>
<body>
    <div id="centerUser" class="x-hide-display usersManager"></div>
    <div id="centerDepartments" class="x-hide-display departmentsManager"></div>
    <div id="centerArchives" class="x-hide-display archivesManager"></div>
    <div id="centerIndexes" class="x-hide-display archivesManager"></div>
    <div id="centerSearchConf" class="x-hide-display"></div>
    <div id="centerRelationConf" class="x-hide-display"></div>
    <div id="indexDetails" class="x-hide-display archivesManager"></div>
    <div id="centerMagazines" class="x-hide-display"></div>
    <select name="ref_id_department" id="ref_id_department" style="display: none;">
    	<option value=""></option>
    	<%if(request.getAttribute("departmentsList")!=null){
			List<Object> departments = (List)request.getAttribute("departmentsList");
			Object[] departmentsList = departments.toArray();%>
			<%      	   
	      	   for(int i=0;i<departmentsList.length;i++){
	      		 Departments department = (Departments)departmentsList[i];
	      	%>
	      		<option value="<%=department.getIdDepartment()%>"><%=department.getDescription()%></option>
	      	<%}%>
    	<%}%>
    </select>
    <select name="ref_id_company" id="ref_id_company" style="display: none;">
    	<option value=""></option>
    	<%if(request.getAttribute("companiesList")!=null){
				List<Object> companies = (List)request.getAttribute("companiesList");
				Object[] companiesList = companies.toArray();%>
				<%      	   
		      	   for(int i=0;i<companiesList.length;i++){
		      		 Companies company = (Companies)companiesList[i];
		      	%>
		      		<option value="<%=company.getIdCompany()%>"><%=company.getCompanyName()%></option>
		      	<%}%> 		
    	<%}%>
    </select>    
    <select name="language" id="language" style="display: none;">
    	<option value=""></option>
    	<option value="IT">IT</option>
    	<option value="EN">EN</option>
    </select>
</body>
</html>