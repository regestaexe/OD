<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.openDams.bean.Archives"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.ArchiveType"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="com.openDams.security.UserDetails"%>
<%@page import="java.util.Collection"%>
<%@page import="org.springframework.security.core.GrantedAuthority"%>
<%@page import="org.springframework.security.core.context.SecurityContext"%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="com.openDams.security.RoleTester"%>
<%@page import="com.openDams.security.ArchiveRoleTester"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<%@include file="../locale.jsp"%>
<%
	UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
	Collection<GrantedAuthority> authorities = user.getAuthorities();
	Object[] authoritiesList = authorities.toArray();
	boolean admin = false;
	boolean isGod = false;
	for (int s = 0; s < authoritiesList.length; s++) {
		GrantedAuthority grantedAuthority = (GrantedAuthority) authoritiesList[s];
		if (RoleTester.testAdminTools(grantedAuthority.getAuthority())) {
			admin = true;
		}
		if (RoleTester.testGod(grantedAuthority.getAuthority())) {
			isGod = true;
		}
	}
%>
<%
	ArrayList<Archives> documentalList = new ArrayList<Archives>();
	ArrayList<Archives> skosList = new ArrayList<Archives>();
	ArrayList<Archives> externalArchiveList = new ArrayList<Archives>();
	Archives messageManager = null;
	Archives help = null;
	Archives basket = null;
	Archives twitter = null;
	Archives qr = null;
	Archives report = null;
	if (request.getAttribute("archiveList") != null) {
		List<Object> archives = (List) request.getAttribute("archiveList");
		Object[] archiveList = archives.toArray();
		for (int i = 0; i < archiveList.length; i++) {
			Archives archive = (Archives) archiveList[i];
			if (archive.getArchiveTypes().getIdArchiveType() == ArchiveType.THESAURUS) {
				skosList.add(archive);
			} else if (archive.getArchiveTypes().getIdArchiveType() == ArchiveType.DOCUMENTAL || archive.getArchiveTypes().getIdArchiveType() == ArchiveType.FILE || archive.getArchiveTypes().getIdArchiveType() == ArchiveType.PHOTOGRAFIC) {
				documentalList.add(archive);
			} else if (archive.getArchiveTypes().getIdArchiveType() == ArchiveType.MESSAGE) {
				messageManager = archive;
			} else if (archive.getArchiveTypes().getIdArchiveType() == ArchiveType.BASKET) {
				basket = archive;
			} else if (archive.getArchiveTypes().getIdArchiveType() == ArchiveType.TWITTER) {
				twitter = archive;
			}else if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.WEB_EXTERNAL){
			    externalArchiveList.add(archive);
		    }else if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.HELP){
			    help = archive;
		    }else if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.QR){
				qr = archive;
			}else if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.REPORT){
				report = archive;
			}
		}
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="com.openDams.bean.Roles"%>
<%@page import="com.openDams.bean.ArchiveIdentity"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>openDams</title>

<link rel="stylesheet" type="text/css"href="css/ext-js-4.0.7/css/ext-all.css" />
<link rel="stylesheet" type="text/css"href="css/ext-js-4.0.7/desktop/desktop.css" />
<link rel="stylesheet" type="text/css"href="css/ext-js-4.0.7/desktop/message-manager/message-manager.css">
<link rel="stylesheet" type="text/css"href="css/ext-js-4.0.7/desktop/basket/basket.css">
<link type="text/css" href="css/redmond/jquery-ui-1.8.6.custom.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css"href="css/style.css">
<script type="text/javascript" src="js/ext-js-4.0.7/ext-all.js"></script>
<script type="text/javascript"src="js/ext-js-4.0.7/locale/ext-lang-it.js"></script>
<script src="js/jquery-1.4.2.min.js" type="text/javascript"></script>
<script type="text/javascript" src="js/application.js"></script>
<script type="text/javascript" src="js/jpath.js"></script>
<script type="text/javascript">
    var globalOpt={};
    globalOpt.document = {};
        
    	loadScripts({base:"application",scripts:['loading','utils','search','basket_4.1','twitter_4.1','report']});

	
	    function openArchiveTab(id_archive,archive_label){
	    	parent.addTab('<img src="img/archive_img/archive_'+id_archive+'.gif" border="0"/>&nbsp;'+archive_label,id_archive,'documentalWorkspace.html?idArchive='+id_archive);
	    }
	    function openCustomTab(id_archive,archive_label){
	    	parent.addTab('<img src="img/archive_img/archive_'+id_archive+'.gif" border="0"/>&nbsp;'+archive_label,id_archive,'customWorkspace.html?idArchive='+id_archive);
		}
        Ext.Loader.setConfig({enabled:true});
        Ext.Loader.setPath({
            'Ext.ux.desktop': 'js/ext-js-4.0.7/desktop/core',
            'MyDesktop': 'js/ext-js-4.0.7/desktop',
            'Ext.ux':'js/ext-js-4.0.7/ux'
        });
        <%if(request.getAttribute("archiveList")!=null){
        	for(int i=0;i<skosList.size();i++){
				   Archives archive =  skosList.get(i);
			   %>
			        Ext.define('OpenDams.ArchiveWindow_<%=archive.getIdArchive()%>', {
			            extend: 'Ext.ux.desktop.Module',
			            id:'archive-win_<%=archive.getIdArchive()%>',
			            init : function() {
				            	top.Message['Bus<%=archive.getIdArchive()%>'].on('offline', function() {
				        			$('.archive_<%=archive.getIdArchive()%>-shortcut').parent('div').fadeTo(1000,0.2);
			        			}, this);
			        			top.Message['Bus<%=archive.getIdArchive()%>'].on('online', function() {
				        			$('.archive_<%=archive.getIdArchive()%>-shortcut').parent('div').fadeTo(1000,1);
			        			}, this);
				        },
			            createWindow : function(src) {
			            	parent.addTab('<%=archive.getLabel()%>','<%=archive.getIdArchive()%>','skosWorkspace.html?idArchive=<%=archive.getIdArchive()%>');			            	
			                return false;
			            }
			        });		        
        		    top.Message['Bus<%=archive.getIdArchive()%>'] = new Ext.util.Observable();
        		    top.Message['Bus<%=archive.getIdArchive()%>'].addEvents('savedoc');
        		    top.Message['Bus<%=archive.getIdArchive()%>'].addEvents('offline');
	      		 	top.Message['Bus<%=archive.getIdArchive()%>'].addEvents('online');
     	<%}
			   for(int i=0;i<documentalList.size();i++){
				   Archives archive =  documentalList.get(i);
			   %>
	      		    top.Message['Bus<%=archive.getIdArchive()%>'] = new Ext.util.Observable();
	      		    top.Message['Bus<%=archive.getIdArchive()%>'].addEvents('savedoc');
	      		 	top.Message['Bus<%=archive.getIdArchive()%>'].addEvents('offline');
	      		 	top.Message['Bus<%=archive.getIdArchive()%>'].addEvents('online');
			        Ext.define('OpenDams.ArchiveWindow_<%=archive.getIdArchive()%>', {
			            extend: 'Ext.ux.desktop.Module',
			            id:'archive-win_<%=archive.getIdArchive()%>',
			            init : function() {
				        			top.Message['Bus<%=archive.getIdArchive()%>'].on('offline', function() {
					        			$('.archive_<%=archive.getIdArchive()%>-shortcut').parent('div').fadeTo(1000,0.2);
				        			}, this);
				        			top.Message['Bus<%=archive.getIdArchive()%>'].on('online', function() {
					        			$('.archive_<%=archive.getIdArchive()%>-shortcut').parent('div').fadeTo(1000,1);
				        			}, this);
				        		
			            },    
			            createWindow : function(src) {
				            <%if(archive.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.PLANE){%>
				            	openArchiveTab('<%=archive.getIdArchive()%>','<%=JsSolver.escapeSingleApex(archive.getLabel())%>');
				            <%}else if(archive.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.CUSTOM){%>
				            	openCustomTab('<%=archive.getIdArchive()%>','<%=JsSolver.escapeSingleApex(archive.getLabel())%>');
				            <%}else if(archive.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.COLLECTION){%>
				            	openArchiveTab('<%=archive.getIdArchive()%>','<%=JsSolver.escapeSingleApex(archive.getLabel())%>');
				            <%}%>			            	
			                return false;
			            }			        	
			        });
        	<%}
		}%>
		<%if(request.getAttribute("archiveList")!=null){
			if(skosList.size()>0 || documentalList.size()>0){%>
			Ext.define('OpenDams.ArchiveListMenuModule', {
			    extend: 'MyDesktop.BogusModule',
			    init : function() {
			        this.launcher = {
			            text: 'Lista Archivi',
			            iconCls: 'bogus',
			            handler: function() {
			                return false;
			            },
			            menu: {
			                items: []
			            }
			        };
			        <%for(int i=0;i<skosList.size();i++){
							   Archives archive =  skosList.get(i);
						   %>
						   this.launcher.menu.items.push({
				                text: '<%=JsSolver.escapeSingleApex(archive.getLabel())%>',
				                iconCls:'archive_<%=archive.getIdArchive()%>_icon',
				                handler : function() {			                	
				                	parent.addTab('<%=archive.getLabel()%>','<%=archive.getIdArchive()%>','skosWorkspace.html?idArchive=<%=archive.getIdArchive()%>');
					                return false;
					            },
				                scope: this
				            });					   
			        	<%}
						   for(int i=0;i<documentalList.size();i++){
							   Archives archive =  documentalList.get(i);
						   %>
						   this.launcher.menu.items.push({
				                text: '<%=JsSolver.escapeSingleApex(archive.getLabel())%>',
				                iconCls:'archive_<%=archive.getIdArchive()%>_icon',
				                handler : function() {
									   	<%if(archive.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.PLANE){%>
						            			openArchiveTab('<%=archive.getIdArchive()%>','<%=JsSolver.escapeSingleApex(archive.getLabel())%>');
						            	<%}else if(archive.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.CUSTOM){%>
						            			openCustomTab('<%=archive.getIdArchive()%>','<%=JsSolver.escapeSingleApex(archive.getLabel())%>');
						            	<%}else if(archive.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.COLLECTION){%>
								            	openArchiveTab('<%=archive.getIdArchive()%>','<%=JsSolver.escapeSingleApex(archive.getLabel())%>');
								        <%}%>		                	
					                return false;
					            },
				                scope: this
				            });					   
			        	<%}%>
			    }
			});
			<%}
		}%>
		<%if (admin) {%>
		Ext.define('OpenDams.AdminTools', {
		    extend: 'Ext.ux.desktop.Module',
		    init : function(){
		        this.launcher = {
		            text: '${tabAdmin}',
		            iconCls:'settings',
		            handler : this.createWindow,
		            scope: this
		        }
		    },
		    createWindow : function(src){		    	
		    	parent.addTab('${tabAdmin}','adminTools','admin/adminTools.html');
		        return false;
		    }
		});	
		
		Ext.define('OpenDams.WorkspaceTools', {
		    extend: 'Ext.ux.desktop.Module',
		    init : function(){
		        this.launcher = {
		            text: 'Gestione Archivi',
		            iconCls:'settings',
		            handler : this.createWindow,
		            scope: this
		        }
		    },
		    createWindow : function(src){		    	
		    	parent.addTab('Gestione','workspaceTools','admin/workspace.html');
		        return false;
		    }
		});	
		<%}%>
	    
<%if(messageManager!=null){%>
var columnsReceived = [
    					{
    					    header: '',
    					    sortable: true,
    					    width: 30,
    					    dataIndex: 'readed'
    					},
    					{
    					    header: 'Mittente',
    					    sortable: true,
    					    width: 200,
    					    dataIndex: 'sender'
    					},
    					{
    					    header: 'Dipartimento',
    					    sortable: true,
    					    width: 200,
    					    dataIndex: 'department'
    					},
    					{   
    						id:'object',
    					    header: 'Oggetto',
    					    sortable: true,
    					    flex: 1,
    					    dataIndex: 'object'
    					},{
    					    header: 'Data',
    					    sortable: true,
    					    width: 140,
    					    dataIndex: 'date'
    					} ];
var columnsSended = [
					{
					    header: '',
					    sortable: true,
					    width: 30,
					    dataIndex: 'readed'
					},
					{
					    header: 'A',
					    sortable: true,
					    width: 200,
					    dataIndex: 'sender'
					},
					{   
						id:'object',
					    header: 'Oggetto',
					    sortable: true,
					    flex: 1,
					    dataIndex: 'object'
					},{
					    header: 'Data',
					    sortable: true,
					    width: 140,
					    dataIndex: 'date'
					} ];
		Ext.define('OpenDams.Messages', {
		    extend: 'Ext.ux.desktop.Module',
		    requires: [
		        'Ext.Panel',
		        'Ext.util.Format'
		    ],
		    id:'messages-win',
		    init : function(){
		        this.launcher = {
		            text: 'Gestione Messaggi',
		            iconCls:'messages_manager',
		            handler : this.createWindow,
		            scope: this
		        };		        
		    },
		    createWindow : function(){
		        var desktop = this.app.getDesktop();
		        var win = desktop.getWindow('messages-win');
		        if (!win) {
			        win = desktop.createWindow(
		            		{
		            			id: 'messages-win',
				                title: 'Gestione Messaggi',
				                width: 700,
				                height: 550,
				                iconCls: 'messages_manager',				                
				                animCollapse: false,
				                constrainHeader: false,
				                bodyBorder: true,				                
				                border: false,
				                layout:'fit',
		        	            items:[this.getMessageApp()]		        	            
		        			}
			        );
		        }
		        win.show();
		        return win;
		    },
		    getMessageApp:function(){
		    	 var messageApp =  Ext.create('Ext.panel.Panel', {
			    	 id:'messageApp',
			    	 layout: 'border',
			    	 border: false,
			    	 items:[this.getMessageToolBar(),this.getLeftPanel(),this.getCenterPanel()]
				 });
		         return messageApp;
		    },
		    getMessageToolBar:function(){
		    	var messageToolBar = Ext.create('Ext.Toolbar', {
		            items: [
				        new Ext.Button({
					        text    : 'Nuovo Messaggio',
					        iconCls: 'send_message',
					        handler : function(btn) {
				        		createNewMessage('normal');
					        }
						}),
						'-',
						new Ext.Button({
					        text    : 'Nuovo Messaggio con lettura obbligata',
					        iconCls: 'send_mandatory_message',
					        handler : function(btn) {
								createNewMessage('modal');
					        }
						})
						<%if(isGod){%>
						,
						'-',
						new Ext.Button({
					        text    : 'Avviso di Sistema',
					        iconCls: 'send_system_message',
					        handler : function(btn) {
								createNewMessage('system');
					        }
						})
						<%}%>
						]
		        });
		    	var messagePanleToolBar =  Ext.create('Ext.panel.Panel', {
					region:'north',
					border:false,
					tbar:messageToolBar
				});
		    	
		        return messagePanleToolBar
			},
		    getLeftPanel:function(){			    
		    	var leftMessagePanel =  Ext.create('Ext.panel.Panel', {
			    	 id:'leftMessagePanel',
			    	 region: 'west',
			    	 layout:'fit',
			    	 title:'Categorie',
			    	 collapsible: true,
			         width: 225,
			         floatable: false,
			         split: true,
			         minWidth: 175,
			    	 html:'',
			    	 listeners:{
			         	'afterrender':function(){
		    					generateLeftMessagePanel();		
					    		top.Message['Bus_messages_manager'].on('messageReceived', function() {
					    			generateLeftMessagePanel();
					    		}, this);					    				    		
			         	}
			        }
			    	 
				 });
		         return leftMessagePanel;
		    },
		    getCenterPanel:function(){
		    	var centerMessagePanel =  Ext.create('Ext.panel.Panel', {
			    	 id:'centerMessagePanel',
			    	 region: 'center',
			    	 layout: 'border',
			    	 border:false,			    	 
			    	 items:[this.getCenterMessageListPanel(),this.getCenterMessageDetailPanel()]
				 });
		         return centerMessagePanel;
			},
		    getCenterMessageListPanel:function(){
			    var messageDs = Ext.create('Ext.data.Store', {
			        proxy: {
			            type: 'ajax',
			            url: 'messagesManager.html',			    			            
			    		extraParams : {						
							department : top.Application['user'].dipartimento,
							action:'getMessageList',
							messageType:''
						},
			            reader: {
			                type: 'json',
			                root: 'messages',
			                idProperty: 'id',
			                totalProperty : 'totalCount'
			            }
			        },
			        storeId: 'messageDs',	
			        pageSize: 10,			
					id: 'messageDs',										
					fields : [ 'id', 'object', 'date','sender','readed','msg_type','department']
				});
			    var messageListToolBar = Ext.create('Ext.Toolbar', {
		            items: ['Segna selezionati come :',    
				        new Ext.Button({
					        text    : 'Letti',
					        itemId: 'bt_readed',
					        disabled: true,
					        iconCls: 'bt_readed',
					        handler : function(btn) {
				        		updateMessages('readed',selected_messages);
					        }
						}),
						'-',
						new Ext.Button({
					        text    : 'Da Leggere',
					        itemId: 'bt_not_readed',
					        disabled: true,
					        iconCls: 'bt_not_readed',
					        handler : function(btn) {
								updateMessages('unreaded',selected_messages);
					        }
						}),
						'-',
						new Ext.Button({
					        text    : 'Di mio interesse',
					        itemId: 'bt_interest',
					        disabled: true,
					        iconCls: 'bt_interest',
					        handler : function(btn) {
								updateMessages('interest',selected_messages);
					        }
						}),
		                '->',
		                new Ext.Button({
					        text    : 'Cancella selezionati',
					        itemId: 'bt_delete_message',
					        disabled: true,
					        iconCls: 'bt_delete_message',
					        handler : function(btn) {
		                		updateMessages('delete',selected_messages);
					        }
						})
						]
		        });
		        var selected_messages = '';
			    var sm = Ext.create('Ext.selection.CheckboxModel', {
			        listeners: {
			            selectionchange: function(sm, selections) {			    	   
			    			messagesGrid.down('#bt_readed').setDisabled(selections.length == 0);
			    			messagesGrid.down('#bt_not_readed').setDisabled(selections.length == 0);
			    			messagesGrid.down('#bt_interest').setDisabled(selections.length == 0);
			    			messagesGrid.down('#bt_delete_message').setDisabled(selections.length == 0);
							if(selections.length == 0){
								selected_messages = '';
							}else{		
								selected_messages='';						
								for(i=0;i<selections.length;i++){
									selected_messages+=selections[i].data.id+";";
						    	}
							}							
			            }
			        }
			    });
				var messagesGrid = Ext.create('Ext.grid.Panel', {
					layout:'fit',
					store : messageDs,					
					id : 'messagesGrid',
					tbar:messageListToolBar,
					loadMask : {
						msg : 'caricamento in corso...',
						enable : true
					},
					selModel: sm,
					border : false,
					collapsible : false,
					enableColumnResize : true,
					enableColumnMove : true,
					enableColumnHide : true,
					animCollapse : false,
					iconCls : 'icon-grid',
					autoHeight : true,
					stateful : true,
					autoScroll : true,
					enableHdMenu : false,
					autoSizeColumns : true,
					autoSizeGrid : true,
					autoExpandColumn : 'object',
					columns : columnsReceived,
					viewConfig : {
						forceFit : true
					},
					listeners : {
						'afterrender' : function() {
							//this.getStore().load({params: {messageType: 1}});
							this.getStore().proxy.extraParams.messageType=1;
							this.getStore().loadPage(1);
							top.Message['Bus_messages_manager'].on('messageReceived', function() {
				    			generateLeftMessagePanel();
				    			//this.getStore().load({params: {messageType: 1}});
				    		}, this);
						},
						'selectionchange':function(sm,selections,eOpts){
							if(selections.length == 1){
								var record = selections[0]; 
								Ext.Ajax.request({
					    		 	 url : 'messagesManager.html',
							         method: 'POST',
							         nocache: true,
							         params :{action:'messageDetail',idMessage:record.data.id},
					                      success: function ( result, request ) {
							        	 	Ext.getCmp('centerMessageDetailPanel').update(result.responseText);
							        	 	Ext.getCmp('bt_reply_message').setDisabled(false);
							        	 	$('#message_gif_'+record.data.id).removeClass("message_close"+record.data.msg_type);
							        	 	$('#message_gif_'+record.data.id).addClass("message_open"+record.data.msg_type);
							        	 	$('#message_object_'+record.data.id).removeClass("unreaded_message");
							        	 	$('#message_object_'+record.data.id).addClass("readed_message");
							        	 	$('#message_date_'+record.data.id).removeClass("unreaded_message");
							        	 	$('#message_date_'+record.data.id).addClass("readed_message");
							        	 	$('#message_sender_'+record.data.id).removeClass("unreaded_message");
							        	 	$('#message_sender_'+record.data.id).addClass("readed_message");
							        	 	$('#message_department_'+record.data.id).removeClass("unreaded_message");
							        	 	$('#message_department_'+record.data.id).addClass("readed_message");
							        	 	generateLeftMessagePanel();
	 				        	 	
					                   },
					                      failure: function ( result, request ) {
					                	   Ext.Msg.alert('Attenzione','Si è verificato un errore!');
					                   }
					            });
							}else{
								Ext.getCmp('centerMessageDetailPanel').update('');
								Ext.getCmp('bt_reply_message').setDisabled(true);
							}
						}
					},
					 dockedItems: [{
			                xtype: 'pagingtoolbar',
			                store: messageDs,   // same store GridPanel is using
			                dock: 'bottom',
			                displayInfo: true
			            }]

				});
		    	var centerMessageListPanel =  Ext.create('Ext.panel.Panel', {
			    	 id:'centerMessageListPanel',
			    	 region: 'north',
			    	 layout: 'fit',
			    	 title:'Elenco messaggi ricevuti',
			    	 collapsible: true,
			    	 height:150,
			         floatable: false,
			         split: true,
			         items:[messagesGrid]
				 });
		         return centerMessageListPanel;
			},
			getCenterMessageDetailPanel:function(){
				var replyMessageToolBar = Ext.create('Ext.Toolbar', {
		            items: [
		                '->',
		                new Ext.Button({
					        text    : 'Rispondi al messaggio',
					        itemId: 'bt_reply_message',
					        id:'bt_reply_message',
					        disabled: true,
					        iconCls: 'send_message',
					        handler : function(btn) {
		                		//alert($('#hidden_msg_text').attr('value'));
		                		//alert($('#hidden_msg_object').attr('value'));
		                		//alert($('#hidden_msg_sendTo').attr('value'));
		                		//alert($('#hidden_msg_sendTo_view').attr('value'));
		                		createReplyMessage($('#hidden_msg_sendTo').attr('value'),$('#hidden_msg_sendTo_view').attr('value'),$('#hidden_msg_object').attr('value'),$('#hidden_msg_text').attr('value'));
					        }
						})
						]
		        });
				var centerMessageDetailPanel =  Ext.create('Ext.panel.Panel', {
			    	 id:'centerMessageDetailPanel',
			    	 region: 'center',
			    	 layout: 'fit',
			    	 autoScroll : true,
			    	 tbar:replyMessageToolBar
				 });
		         return centerMessageDetailPanel;
			}
		});
function openMessageList(messageType){
	$('.message-list-item').removeClass('message-list-item_selected');
	$('.message-list-item_'+messageType).addClass('message-list-item_selected');
	Ext.getCmp('messagesGrid').getStore().removeAll();
	if(messageType==500){
		Ext.getCmp('messagesGrid').reconfigure(Ext.getCmp('messagesGrid').getStore(),columnsSended);
	}else{
		Ext.getCmp('messagesGrid').reconfigure(Ext.getCmp('messagesGrid').getStore(),columnsReceived);
	}	
	Ext.getCmp('messagesGrid').getStore().proxy.extraParams.messageType=messageType;
	Ext.getCmp('messagesGrid').getStore().loadPage(1);;
	//Ext.getCmp('messagesGrid').getStore().load({params: {messageType: messageType}});
}
function updateMessages(action,selected_messages){
	Ext.Ajax.request({
		url : 'messagesManager.html',
        method: 'POST',
        nocache: true,
        params :{action:'updateMessages',actionType:action,selectedMessages:selected_messages},
        success: function ( result, request ) {
        	Ext.getCmp('messagesGrid').getStore().removeAll();
        	Ext.getCmp('messagesGrid').getStore().load();
        	generateLeftMessagePanel();
     	 },
        failure: function ( result, request ) {
     		Ext.Msg.alert('Stato invio Messaggio:','Si è verificato un errore imprevisto durante l\'invio del messaggio!');
     	}
	});
}
<%}%>
<%if(help!=null){%>
Ext.define('OpenDams.Help',{
    extend: 'Ext.ux.desktop.Module',
    requires: [],
    id:'help',
    init : function(){
        this.launcher = {
            text: 'Manuale d\'uso',
            iconCls:'help',
            handler : this.createWindow,
            scope: this
        }
    },
    createWindow : function(){
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('help');
        if(!win){
            win = desktop.createWindow({
                id: 'help',
                title:'Manuale d\'uso',
                width:600,
                height:400,
                iconCls: 'help',
                animCollapse:false,
                border: false,
                layout: 'fit',
                items : [{
                    xtype : 'component',
                    autoEl : {
                        tag : 'iframe',
                        src : 'help/<%=user.getIdCompany()%>/help.pdf?'+ top.Application.today
                    }
                }]
            });
        }
        win.show();
        return win;
    }
});
<%}%>
Ext.define('OpenDams.ExternalApp', {
    extend: 'Ext.ux.desktop.Module',
    requires: [],
    init : function(){
		var me = this;
        this.launcher = { 
            text: me.windowTitle,            
            iconCls: me.iconCls,
            handler : this.createWindow,
            scope: this
        }
    },
    createWindow : function(src){
        var me = this;         
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow(me.windowId);
        if(!win){
            win = desktop.createWindow({
                id: me.windowId,
                title:me.windowTitle,
                width:800,
                height:600,
                iconCls: me.iconCls,
                animCollapse:false,
                border: false,
                layout: 'fit',
                items : [
                         {
		                    xtype : 'component',
		                    autoEl : {
		                        tag : 'iframe',
		                        frameborder:0,
		                        src : me.url
		                    }
		                 }
                		]
            });
        }
        win.show();
        return win;
    }
});
<%if(basket!=null){%>
Ext.define('OpenDams.Basket', {
    extend: 'Ext.ux.desktop.Module',
    requires: ['Ext.ux.RowExpander'],
    id:'basket-win',
    init : function(){
        this.launcher = {
            text: 'Cestino',
            iconCls:'basket',
            handler : this.createWindow,
            scope: this
        }
    },
    createWindow : function(){
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('basket-win');
        if(!win){
            win = desktop.createWindow({
                id: 'basket-win',
                title:'Cestino',
                width:600,
                height:400,
                iconCls: 'basket',
                animCollapse:false,
                constrainHeader: false,
                bodyBorder: true,
                border: false,
                layout: 'fit',
                items : [this.getBasketApp()]         
            });
        }
        win.show();
        return win;
    },
    getBasketApp:function(){
   	 	var messageApp =  Ext.create('Ext.panel.Panel', {
	    	 id:'messageApp',
	    	 layout: 'border',
	    	 border: false,
	    	 items:[this.getLeftPanel(),this.getCenterPanel()]
		});
        return messageApp;
   },
   getLeftPanel:function(){			    
   		var leftBasketPanel =  Ext.create('Ext.panel.Panel', {
	    	 id:'leftBasketPanel',
	    	 region: 'west',
	    	 layout:'fit',
	    	 title:'Archivi',
	    	 collapsible: true,
	         width: 225,
	         floatable: false,
	         split: true,
	         minWidth: 175,
	         items:[getArchiveGrid()],
	    	 listeners:{
	         	'afterrender':function(){
         							    				    		
	         	}
	        }
	    	 
		 });
        return leftBasketPanel;
   },
   getCenterPanel:function(){
   	var centerBasketPanel =  Ext.create('Ext.panel.Panel', {
	    	 id:'centerBasketPanel',
	    	 region: 'center',
	    	 title:'Documenti nel cestino',
	    	 layout: 'fit',
	    	 border:false,			    	 
	    	 items:[getDeletedRecodsGrid()]
		 });
        return centerBasketPanel;
	}
});
<%}%>

 
Ext.define('OpenDams.App', {
    extend: 'Ext.ux.desktop.App',
    requires: [
        'Ext.ux.desktop.ShortcutModel'       
        /*'MyDesktop.Settings'*/
    ],
    init: function() {
        this.callParent();
        top.Message['Bus_messages_manager'].on('messageReceived', function() {
        	if(top.Ext.getCmp('tabPanelDesktop').getActiveTab().id==top.Ext.getCmp('tabPanelDesktop').getComponent(0).id){
        		openMessages();
        	}else{            	
            	top.Ext.ods.msg('Attenzione! Hai ricevuto un nuovo messaggio');
            	openMessages();        			
            }
		}, this);
    },
    getModules : function(){
        return [
             <%boolean isFirstApplication = true;%>
             <%if(admin){%>
             <%if(!isFirstApplication){%>,<%}%>new OpenDams.AdminTools(),new OpenDams.WorkspaceTools()
             <%isFirstApplication=false;}%>   
             <%if(messageManager!=null){%>
             <%if(!isFirstApplication){%>,<%}%>  new OpenDams.Messages()
	   	     <%isFirstApplication=false;}%>
	   	     <%if(basket!=null){%>
	   	  	 <%if(!isFirstApplication){%>,<%}%>  new OpenDams.Basket()
	   	     <%isFirstApplication=false;}%>
	   	     <%if(help!=null){%>
	   	  	 <%if(!isFirstApplication){%>,<%}%>  new OpenDams.Help()
	   	     <%isFirstApplication=false;}%>
	   	     <%if(twitter!=null){%>
	   	  	 <%if(!isFirstApplication){%>,<%}%>  new OpenDams.Twitter()
	   	     <%isFirstApplication=false;}%> 
	   	     <%if(qr!=null){%>
	   	  	 <%if(!isFirstApplication){%>,<%}%>  new OpenDams.QR()
	   	     <%isFirstApplication=false;}%>  
	   	     <%if(report!=null && user.getIdCompany()==1 || user.getIdCompany()==1000000000){%>
	   	  	 <%if(!isFirstApplication){%>,<%}%>  new OpenDams.Report()
	   	     <%isFirstApplication=false;}%>  
			<%if(request.getAttribute("archiveList")!=null){
				if(skosList.size()>0 || documentalList.size()>0){%>
				<%if(!isFirstApplication){%>,<%}%>new OpenDams.ArchiveListMenuModule()
				<%isFirstApplication=false;}
			 }%>           
            <%if(request.getAttribute("archiveList")!=null){           	
            	   for(int i=0;i<skosList.size();i++){
	 				   Archives archive =  skosList.get(i);%>
	 				  <%if(!isFirstApplication){%>,<%}%>new OpenDams.ArchiveWindow_<%=archive.getIdArchive()%>()
	 				   <%isFirstApplication=false;
 			       }
    			   for(int i=0;i<documentalList.size();i++){
    				   Archives archive =  documentalList.get(i);%>
    				   <%if(!isFirstApplication){%>,<%}%>new OpenDams.ArchiveWindow_<%=archive.getIdArchive()%>()
    				   <%isFirstApplication=false;
    			   }   			   
    			   for(int i=0;i<externalArchiveList.size();i++){
    				   Archives archive =  externalArchiveList.get(i);%>
    				   <%if(!isFirstApplication){%>,<%}%>new OpenDams.ExternalApp({id:'externalApp<%=archive.getIdArchive()%>',windowId:'externalWindow<%=archive.getIdArchive()%>',url:'<%=archive.getDescription()%>',windowTitle:'<%=JsSolver.escapeSingleApex(archive.getLabel())%>',iconCls:'archive_<%=archive.getIdArchive()%>_icon'})
    				   <%isFirstApplication=false;
    			   }
    	     }%>
        ];
    },
    getDesktopConfig: function () {
        var me = this, ret = me.callParent();        
        return Ext.apply(ret, {
            shortcuts: Ext.create('Ext.data.Store', {
                model: 'Ext.ux.desktop.ShortcutModel',
                data: [
                    <%
                    String currentGroup = "";
    				String prevGroup="";
                    isFirstApplication = true;%>   
                    <%if(request.getAttribute("archiveList")!=null){
                    	List<Archives> archiveList = (List<Archives>) request.getAttribute("archiveList");
                		for (int i = 0; i < archiveList.size(); i++) {
                			Archives archive = (Archives) archiveList.get(i);
                			currentGroup=archive.getArchiveGroup().getArchiveGroupLabel();
                			if(!currentGroup.equals(prevGroup)){%>
                			 <%if(!isFirstApplication){%>,<%}%>
                			{  iconCls: 'none',name:"<%=currentGroup%>", css:'separatore'}
                			<%isFirstApplication=false;}
                			prevGroup = currentGroup;
                			isFirstApplication=false;
                			if (archive.getArchiveTypes().getIdArchiveType() == ArchiveType.THESAURUS) {%>
                			 	 <%if(!isFirstApplication){%>,<%}%>
                			 	 { name: '<%=JsSolver.escapeSingleApex(archive.getLabel())%>', iconCls: 'archive_<%=archive.getIdArchive()%>-shortcut', module: 'archive-win_<%=archive.getIdArchive()%>'}
            					 <%isFirstApplication=false;
                			} else if (archive.getArchiveTypes().getIdArchiveType() == ArchiveType.DOCUMENTAL || archive.getArchiveTypes().getIdArchiveType() == ArchiveType.FILE || archive.getArchiveTypes().getIdArchiveType() == ArchiveType.PHOTOGRAFIC) {%>
            					 <%if(!isFirstApplication){%>,<%}%>
            			    	 { name: '<%=JsSolver.escapeSingleApex(archive.getLabel())%>', iconCls: 'archive_<%=archive.getIdArchive()%>-shortcut', module: 'archive-win_<%=archive.getIdArchive()%>'}
            					 <%isFirstApplication=false;
                			} else if (archive.getArchiveTypes().getIdArchiveType() == ArchiveType.MESSAGE) {%>
                				 <%if(!isFirstApplication){%>,<%}%>
                				 { name: '<%=messageManager.getLabel()%>', iconCls: 'messageManager-shortcut', module: 'messages-win' }
                				 <%isFirstApplication=false;%>	
                			<%} else if (archive.getArchiveTypes().getIdArchiveType() == ArchiveType.BASKET) {%>
                				 <%if(!isFirstApplication){%>,<%}%>
                				 { name: '<%=basket.getLabel()%>', iconCls: 'basket-shortcut', module: 'basket-win' }	
            					 <%isFirstApplication=false;%>
                			<%} else if (archive.getArchiveTypes().getIdArchiveType() == ArchiveType.TWITTER) {%>
                				 <%if(!isFirstApplication){%>,<%}%>
                				 { name: '<%=twitter.getLabel()%>', iconCls: 'twitter-shortcut', module: 'twitter-win' }	
        						 <%isFirstApplication=false;%>
                			<%}else if(archive.getArchiveTypes().getIdArchiveType()==ArchiveType.WEB_EXTERNAL){%>
                				 <%if(!isFirstApplication){%>,<%}%>
                				 { name: '<%=JsSolver.escapeSingleApex(archive.getLabel())%>', iconCls: 'archive_<%=archive.getIdArchive()%>-shortcut', module: 'externalApp<%=archive.getIdArchive()%>'}
          					    <%isFirstApplication=false;
                		      }
                		}
     				}
     				%>
     				<%if(!isFirstApplication){%>,<%}%>{iconCls: '',name:"", css:'hidden'}   
     				
                ]
            }),
            createWindowMenu: function () {
                var me = this;
                return {
                    defaultAlign: 'br-tr',
                    items: [
                        { text: 'Ripristina', handler: me.onWindowMenuRestore, scope: me },
                        { text: 'Minimizza', handler: me.onWindowMenuMinimize, scope: me },
                        { text: 'Massimizza', handler: me.onWindowMenuMaximize, scope: me },
                        '-',
                        { text: 'Chiudi', handler: me.onWindowMenuClose, scope: me }
                    ],
                    listeners: {
                        beforeshow: me.onWindowMenuBeforeShow,
                        hide: me.onWindowMenuHide,
                        scope: me
                    }
                };
            },
            createDesktopMenu: function () {
                var me = this, ret = {
                    items: me.contextMenuItems || []
                };

                if (ret.items.length) {
                    ret.items.push('-');
                }

                ret.items.push(
                		/*{ text: 'Change Settings', handler: me.onSettings, scope: me },
                		'-',*/
                        { text: 'Affianca', handler: me.tileWindows, scope: me, minWindows: 1 },
                        { text: 'In cascata', handler: me.cascadeWindows, scope: me, minWindows: 1 })

                return ret;
            },
            wallpaper: 'img/desktop/wallpapers/desktop2.jpg',
            wallpaperStretch: false
        });
    },
    // config for the start menu
    getStartConfig : function() {
        var me = this, ret = me.callParent();

        return Ext.apply(ret, {
            title: top.Application.user.nome+" "+top.Application.user.cognome,
            iconCls: 'user',
            height: 350,
            toolConfig: {
                width: 100,
                items: [
                    /*{
                        text:'Settings',
                        iconCls:'settings',
                        handler: me.onSettings,
                        scope: me
                    },
                    '-',*/
                    {
                        text:'Esci',
                        iconCls:'logout',
                        handler: function() {                   	
				        	top.location.href='<c:url value="/j_spring_security_logout"/>';
				  		}
                    }
                ]
            }
        });
    },
    getTaskbarConfig: function () {
    	var width = 0;
    	if(top.Application.user.dipartimentoExt!=''){
        	var test = $('<span style="display:none;">'+top.Application.user.dipartimentoExt+'</span>');
        	$('body').append(test);
        	width=test.width()+15;
        	$('body').remove(test);
        	
        }
        var ret = this.callParent();
        return Ext.apply(ret, {
            quickStart: [
				<%if(help!=null){%>         
				{ name: 'Manuale d\'uso', iconCls: 'help', module: 'help' }
				<%}%>
            ],
            trayItems: [
                { xtype: 'panel', html:top.Application.user.dipartimentoExt}
            ]
        });
    }
});
var openDamsApp;
Ext.onReady(function () {
	top.Message['Bus_messages_manager'] = new Ext.util.Observable();
	top.Message['Bus_messages_manager'].addEvents('messageReceived');
	openDamsApp = new OpenDams.App();
	//openMessages();	
    var taskArchiveStatus = {
   		   run: top.checkArchiveStatus,
   		   interval: 180000
   		};
    var runner = new Ext.util.TaskRunner();
    runner.start(taskArchiveStatus);
    var taskcheckMessageReceived = {
    		   run: top.checkMessageReceived,
    		   interval: 180000
    		};
     var runner2 = new Ext.util.TaskRunner();
     runner2.start(taskcheckMessageReceived);
});
function openMessages(){
	 <%if(messageManager!=null){%>
	openDamsApp.getModule('messages-win').createWindow();
	<%}%>
}
function generateLeftMessagePanel(){
	Ext.Ajax.request({
	 	url : 'messagesManager.html',
        method: 'POST',
        nocache: true,
        params :{action:'leftMessagePanel'},
        success: function ( result, request ) {
       	 	   Ext.getCmp('leftMessagePanel').update(result.responseText);				        	 	
        },
        failure: function ( result, request ) {
     	   Ext.Msg.alert('Attenzione','Si è verificato un errore!');
        }
  	});
}
function createNewMessage(messageMode){
	var iconCls = '';
	var titleWin = '';
	var hideField = true;
	var y = 95;
	var bodyStylePanel = '';
	Ext.define('MessageObjects', {
        extend: 'Ext.data.Model',
        fields: [{type: 'string', name: 'name'},{type: 'string', name: 'text'}]
    });
	var simpleObjects =  [];
	var modalObjects =  [
  	                {"name":"Avviso di Sistema","text":"Il sistema sarà teporaneamente indisponibile per interventi di manutenzione."},
  	                {"name":"Avviso importante","text":""}
  	               ];
	var systemObjects =  [
  	                {"name":"Avviso di blocco del Sistema per manutenzione","text":"Il sistema sarà teporaneamente indisponibile per interventi di manutenzione."},
  	                {"name":"Avviso di blocco Archivi per manutenzione","text":"L'archivio [.....] sarà teporaneamente indisponibile per interventi di manutenzione."}
  	               ];  
	var objStore = null;
	if(messageMode=='normal'){
		iconCls = 'send_message';
		titleWin='Invia un nuovo messaggio';
		bodyStylePanel='background:#ffffff;';
		objStore = Ext.create('Ext.data.Store', {
		        model: 'MessageObjects',
		        data: simpleObjects
		});
	}else if(messageMode=='modal'){
		iconCls = 'send_mandatory_message';
		titleWin='Invia un nuovo messaggio con lettura obbligata';
		bodyStylePanel='background:#d1d1d1;';
		objStore = Ext.create('Ext.data.Store', {
	        model: 'MessageObjects',
	        data: modalObjects
		});
	}else if(messageMode=='system'){
		iconCls = 'send_system_message';
		titleWin='Invia Avviso di Sistema';
		hideField = false;
		y = 215;
		bodyStylePanel='background:#f65351;';
		objStore = Ext.create('Ext.data.Store', {
	        model: 'MessageObjects',
	        data: systemObjects
		});
	}
	var usersDsMy = Ext.create('Ext.data.Store', {	
        proxy: {
            type: 'ajax',
            url: 'messagesManager.html',			    			            
    		extraParams : {
				action:'getUsersList',
				type:'my_dep',
				messageMode:messageMode
			},
            reader: {
                type: 'json',
                root: 'users',
                idProperty: 'idUser',
                totalProperty : 'totalCount'
            }
        },
        storeId: 'usersDsMy',		
		id: 'usersDsMy',										
		fields : [ 'idUser', 'name','class']
	});
	var usersDsAll = Ext.create('Ext.data.Store', {	
        proxy: {
            type: 'ajax',
            url: 'messagesManager.html',			    			            
    		extraParams : {
				action:'getUsersList',
				type:'other_dep',
				messageMode:messageMode
			},
            reader: {
                type: 'json',
                root: 'users',
                idProperty: 'idUser',
                totalProperty : 'totalCount'
            }
        },
        storeId: 'usersDsAll',		
		id: 'usersDsAll',										
		fields : [ 'idUser', 'name','class']
	});
	var form = Ext.create('Ext.form.Panel', {
        layout: 'absolute',
        defaultType: 'textfield',
        border: false,
        bodyStyle: bodyStylePanel,
        items: [
		         Ext.create('Ext.form.field.ComboBox', {
			        fieldLabel: 'Al mio dipartimento',
			        fieldWidth: 60,
			        labelWidth:120,
			        labelAlign:'left',
			        msgTarget: 'side',
			        allowBlank: true,
			        x: 5,
			        y: 5,
			        name: 'my_dep',		        
			        anchor: '-5',
			        multiSelect: true,
			        editable: false,
			        displayField: 'name',
			        valueField: 'idUser',
			        store: usersDsMy,
			        queryMode: 'remote',
			        blankText:'seleziona un destinatario...', 
			        id:'my_depComboBox',
			        listeners : {
						'focus' : function() {
						}
					},
					listConfig: {
						itemTpl : Ext.create('Ext.XTemplate','<span class="{class}">{name}</span>')
				    }
									
			    })
			    ,Ext.create('Ext.form.field.ComboBox', {
			        fieldLabel: 'Agli altri utenti',
			        fieldWidth: 60,
			        labelWidth:120,
			        labelAlign:'left',
			        msgTarget: 'side',
			        allowBlank: true,
			        x: 5,
			        y: 35,
			        name: 'other_dep',			        
			        anchor: '-5',
			        multiSelect: true,
			        editable: false,
			        displayField: 'name',
			        valueField: 'idUser',
			        store: usersDsAll,
			        queryMode: 'remote',
			        blankText:'seleziona un destinatario...', 
			        id:'other_depComboBox',
			        listeners : {
						'focus' : function() {
						}
					},
					listConfig: {
						itemTpl : Ext.create('Ext.XTemplate','<span class="{class}">{name}</span>')						
				    }
			    }), 
			    Ext.create('Ext.form.field.ComboBox', {
			    	id:'message_object',
			    	fieldLabel: 'Oggetto',
		            fieldWidth: 60,
		            labelWidth:120,
			        displayField: 'name',
			        valueField: 'name',
			        store: objStore,
			        queryMode: 'local',
			        typeAhead: true,
			        editable: true,
			        allowBlank: true,
			        multiSelect: false,
		            x: 5,
		            y: 65,
		            name: 'object',
		            anchor: '-5',
		            listeners : {
						'select' : function(combo,records,eOpts) {
			    				Ext.getCmp('message_text').setValue(records[0].data.text);
						}						
					}
			    }),
		        {
	                fieldLabel: 'Data Inizio',
	                labelWidth:120,
	                xtype: 'datefield',
	                name: 'startdt',
	                id: 'startdt',
	                hidden:hideField,
	                hideLabel:hideField,
	                format:'d/m/Y',
	                x: 5,
		            y: 95
	            },
	            {
	                fieldLabel: 'Ora Inizio',
	                labelWidth:120,
	                xtype: 'timefield',
	                name: 'starttime',
	                id: 'starttime',
	                hidden:hideField,
	                hideLabel:hideField,
	                format:'H:i:s',
	                x: 5,
		            y: 125
	            },
	            {
	                fieldLabel: 'Data Fine',
	                labelWidth:120,
	                xtype: 'datefield',
	                name: 'enddt',
	                id: 'enddt',
	                hidden:hideField,
	                hideLabel:hideField,
	                format:'d/m/Y',
	                startDateField: 'startdt',	               
	                x: 5,
		            y: 155
	            },
	            {
	                fieldLabel: 'Ora Fine',
	                labelWidth:120,
	                xtype: 'timefield',
	                name: 'endtime',
	                id: 'endtime',
	                hidden:hideField,
	                hideLabel:hideField,
	                format:'H:i:s',
	                x: 5,
		            y: 185
	            },
		        {
		            x:5,
		            y:y,
		            id:'message_text',
		            xtype: 'textarea',
		            style: 'margin:0',
		            hideLabel: true,
		            msgTarget: 'side',
		            allowBlank: true,
		            name: 'text',
		            anchor: '-5 -5'
		        }
		      ]
    });
    var createNewMessageWin = Ext.create('Ext.window.Window', {
        title: titleWin,
        width: 800,
        height: 500,
        minWidth: 300,
        minHeight: 200,
        modal:true,
        iconCls: iconCls,
        layout: 'fit',
        plain:true,
        items: form,
        buttons: [
                  {
			            text: 'Invia',
			            handler:function(){
			                    var my_dep_send_to = Ext.String.trim(''+Ext.getCmp('my_depComboBox').getValue()+'');
			                    var other_dep_send_to = Ext.String.trim(''+Ext.getCmp('other_depComboBox').getValue()+'');
								var message_object = Ext.String.trim(''+Ext.getCmp('message_object').getValue()+'');
								var message_text = Ext.String.trim(''+Ext.getCmp('message_text').getValue()+'');				
								if((my_dep_send_to!='' || other_dep_send_to!='') && message_object!='' && message_text!=''){
									var sendTo = '';
									if(my_dep_send_to!='' && other_dep_send_to){
										sendTo=my_dep_send_to+','+other_dep_send_to;
									}else if(my_dep_send_to!=''){
										sendTo=my_dep_send_to;
									}else{
										sendTo=other_dep_send_to;
									}
									var startdt = '';
									var starttime = '';
									var enddt = '';
									var endtime = '';
									if(messageMode=='system'){
										startdt = Ext.String.trim(''+Ext.getCmp('startdt').rawValue+'');
										starttime = Ext.String.trim(''+Ext.getCmp('starttime').rawValue+'');
										enddt = Ext.String.trim(''+Ext.getCmp('enddt').rawValue+'');
										endtime = Ext.String.trim(''+Ext.getCmp('endtime').rawValue+'');										
										if(startdt!='' && starttime!='' && enddt!='' && endtime!=''){
											var startDate=startdt+' '+starttime;
											var endDate=enddt+' '+endtime;
											message_text+='<br>Dal giorno:'+startdt+' alle ore:'+starttime;
											message_text+='<br>Al giorno:'+enddt+' alle ore:'+endtime;
											Ext.Ajax.request({
					                			url : 'messagesManager.html',
					                	        method: 'POST',
					                	        nocache: true,
					                	        params :{action:'sendMessage',messageMode:messageMode,sendTo:sendTo,message_object:message_object,message_text:message_text,endDate:endDate,startDate:startDate},
					                	        success: function ( result, request ) {
					                	        	Ext.MessageBox.alert('Stato invio Messaggio:', 'Messaggio inviato correttamente',function(){
					                	        		createNewMessageWin.destroy();       	
					                	            });
					                	     	 },
					                	        failure: function ( result, request ) {
					                	     		Ext.Msg.alert('Stato invio Messaggio:','Si è verificato un errore imprevisto durante l\'invio del messaggio!');
					                	     	}
					                		});
										}else{
												Ext.Msg.alert('Attenzione','Compilare tutti i campi prima di inviare il messaggio!');
										}
									}else{
										Ext.Ajax.request({
				                			url : 'messagesManager.html',
				                	        method: 'POST',
				                	        nocache: true,
				                	        params :{action:'sendMessage',messageMode:messageMode,sendTo:sendTo,message_object:message_object,message_text:message_text},
				                	        success: function ( result, request ) {
				                	        	Ext.MessageBox.alert('Stato invio Messaggio:', 'Messaggio inviato correttamente',function(){
				                	        		createNewMessageWin.destroy();       	
				                	            });
				                	     	 },
				                	        failure: function ( result, request ) {
				                	     		Ext.Msg.alert('Stato invio Messaggio:','Si è verificato un errore imprevisto durante l\'invio del messaggio!');
				                	     	}
				                		});
									}									
								}else{
									Ext.Msg.alert('Attenzione','Compilare tutti i campi prima di inviare il messaggio!');
								}	            		 
			        	}
			        },
			        {
			            text: 'Svuota',
			            handler:function(){
				        	Ext.getCmp('other_depComboBox').setValue(''); 
		            		Ext.getCmp('my_depComboBox').setValue('');
		            		Ext.getCmp('message_object').setValue('');
		            		Ext.getCmp('message_text').setValue('');
		            		if(messageMode=='system'){
		            			Ext.getCmp('startdt').setValue(''); 
								Ext.getCmp('starttime').setValue(''); 
								Ext.getCmp('enddt').setValue(''); 
								Ext.getCmp('endtime').setValue(''); 	
				            }
			            }
			        },
			        {
			            text: 'Chiudi',
			            handler:function(){
			        		createNewMessageWin.destroy();
			            }
			        }
			      ]
    });
	createNewMessageWin.show();
}

function createReplyMessage(to,to_view,obj,old_text){
	var iconCls = 'send_message';
	var messageMode = 'normal';
	var form = Ext.create('Ext.form.Panel', {
        layout: 'absolute',
        defaultType: 'textfield',
        border: false,
        bodyStyle: 'background:#ffffff;',
        items: [
		         Ext.create('Ext.form.TextField', {
			        fieldLabel: 'A',
			        fieldWidth: 60,
			        labelWidth:120,
			        labelAlign:'left',
			        msgTarget: 'side',
			        allowBlank: true,
			        x: 5,
			        y: 5,
			        name: 'message_to_view',
			        id:'message_to_view',
			        value:to_view,		        
			        anchor: '-5',
			        editable: false									
			    }),
			    Ext.create('Ext.form.TextField', {
			        fieldLabel: 'A',
			        fieldWidth: 60,
			        labelWidth:120,
			        labelAlign:'left',
			        msgTarget: 'side',
			        allowBlank: true,
			        hidden:true,
	                hideLabel:true,
			        x: 5,
			        y: 5,
			        value:to,
			        name: 'message_to',
			        id:'message_to',		        
			        anchor: '-5',
			        disabled: true							
			    }),  
			    Ext.create('Ext.form.TextField', {
			    	id:'message_object',
			    	fieldLabel: 'Oggetto',
		            fieldWidth: 60,
		            labelWidth:120,			       
		            disabled: true,
			        allowBlank: true,			   
		            x: 5,
		            y: 35,
		            value:obj,
		            name: 'object',
		            anchor: '-5'
			    }),	           
		        {
		            x:5,
		            y:65,
		            id:'message_text_old',
		            xtype: 'textarea',
		            style: 'margin:0',
		            disabled: true,
		            hideLabel: true,
		            msgTarget: 'side',
		            allowBlank: true,
		            value:old_text,
		            rows:2,
		            name: 'text',
		            anchor: '-5'
		        },{
		            x:5,
		            y:125,
		            id:'message_text',
		            xtype: 'textarea',
		            style: 'margin:0',
		            hideLabel: true,
		            msgTarget: 'side',
		            allowBlank: true,
		            value:'',
		            name: 'text',
		            anchor: '-5 -5'
		        }
		      ]
    });

    var createReplyMessageWin = Ext.create('Ext.window.Window', {
        title: 'Rispondi al messaggio',
        width: 800,
        height: 500,
        minWidth: 300,
        minHeight: 200,
        modal:true,
        iconCls: iconCls,
        layout: 'fit',
        plain:true,
        items: form,
        buttons: [
                  {
			            text: 'Invia',
			            handler:function(){
                	  			var sendTo = Ext.String.trim(''+Ext.getCmp('message_to').getValue()+'');
								var message_object = Ext.String.trim(''+Ext.getCmp('message_object').getValue()+'');
								var message_text = Ext.String.trim(''+Ext.getCmp('message_text').getValue()+'');		
								var message_text_old = 	Ext.String.trim(''+Ext.getCmp('message_text_old').getValue()+'');									
								if(sendTo!='' && message_object!='' && message_text!=''){
										message_text+='<br><br><hr style="width:100%;"><br>'+message_text_old;
										Ext.Ajax.request({
				                			url : 'messagesManager.html',
				                	        method: 'POST',
				                	        nocache: true,
				                	        params :{action:'sendMessage',messageMode:messageMode,sendTo:sendTo,message_object:message_object,message_text:message_text},
				                	        success: function ( result, request ) {
				                	        	Ext.MessageBox.alert('Stato invio Messaggio:', 'Messaggio inviato correttamente',function(){
				                	        		createReplyMessageWin.destroy();       	
				                	            });
				                	     	 },
				                	        failure: function ( result, request ) {
				                	     		Ext.Msg.alert('Stato invio Messaggio:','Si è verificato un errore imprevisto durante l\'invio del messaggio!');
				                	     	}
				                		});
																		
								}else{
									Ext.Msg.alert('Attenzione','Compilare tutti i campi prima di inviare il messaggio!');
								}	            		 
			        	}
			        },
			        {
			            text: 'Svuota',
			            handler:function(){
		            		Ext.getCmp('message_text').setValue('');		            		
			            }
			        },
			        {
			            text: 'Chiudi',
			            handler:function(){
			        	createReplyMessageWin.destroy();
			            }
			        }
			      ]
    });
    createReplyMessageWin.show();
}
</script>
<style type="text/css">
#poweredby div {
	position: relative;
	width: 150px;
	height: 30px;
	background-image: url(img/ desktop/ openDamsLogo <%=user.getIdCompany()%>.png
		);
	background-repeat: no-repeat;
}
body,html{overflow:hidden;}
.x-fit-item{overflow-y:auto;overflow-x:hidden }
.separatore{
margin-top:66px;
height:25px;
margin-bottom: -28px;
width:99%;
float:left;
background: none;
border-top:2px solid #fff;
clear:both;
text-align: left;
opacity:0.8;

}
.separatore * {
background: none;
}
.separatore *:hover{text-decoration: none}
.separatore .ux-desktop-shortcut-text{
font-size: 20px;
margin-left:-9px;display:block;
line-height:20px;
position:relative;
top:-40px;
}
.separatore .ux-desktop-shortcut-icon{display:none}
.hidden{display:none}
</style>
</head>

<body>
    <a href="#n" alt="<%=user.getCompany()%>" id="poweredby"><div></div></a>
</body>
</html>