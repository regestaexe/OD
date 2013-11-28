<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@page import="com.openDams.bean.Archives"%>
<%@page import="com.openDams.bean.ArchiveIdentity"%>
<%@page import="com.openDams.security.UserDetails"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="org.springframework.security.core.context.SecurityContext"%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="java.util.Collection"%>
<%@page import="com.openDams.bean.MessageTypes"%>
<%@page import="org.springframework.security.core.GrantedAuthority"%>
<%@page import="com.openDams.security.RoleTester"%>
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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<spring:message code="tab.archive" text="Lista Archivi" var="tabArchive" />
<spring:message code="tab.admin" text="Funzioni di Gestione" var="tabAdmin" />
<spring:message code="footer.message" text="" var="footerMessage" />
<spring:message code="footer.logout" text="Esci" var="footerLogout" />
<title>openDams</title><style type="text/css">body,html{overflow: hidden}</style>
<link rel="icon" href="img/sqlxdicon.ico" type="image/png" />
<link rel="shortcut icon" href="img/sqlxdicon.ico" type="image/png" />
<link href="css/style_sky_blue.css" id="theme_color_tree" rel="stylesheet" type="text/css" />
<link href="css/StyleSheet.css" rel="stylesheet" type="text/css" />
<link href="css/confirm.css" rel="stylesheet" type="text/css" />
<link href="css/jquery.contextMenu.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="css/resources/css/ext-all-notheme.css" />
<link type="text/css" href="css/redmond/jquery-ui-1.8.6.custom.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" id="theme_color" href="css/resources/css/xtheme-blue.css" />
<link href="css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/jquery-1.4.2.min.js" ></script>
<script type="text/javascript" src="js/ext-js/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="js/ext-js/ext-all.js"></script>
<script type="text/javascript" src="js/ext-js/locale/ext-lang-it.js"></script>
<script type="text/javascript" src="js/application.js"></script>
<script type="text/javascript">
<%SimpleDateFormat formatToday = new SimpleDateFormat("yyyyMMdd");%>

	Ext.ns('Message'); 
	Ext.ns('Application'); 
	Application.today = '<%=formatToday.format(new Date())%>'; 
	loadScripts({base:"application",scripts:['loading','utils','search','insert','jsonInjector']});

    var ds;
    var ie=false;
    Ext.onReady(function(){
        Application.user = {}; 
        Application.user = {company:'<%=user.getIdCompany()%>',nome:'<%=JsSolver.escapeSingleApex(user.getName())%>', cognome:'<%=JsSolver.escapeSingleApex(user.getLastname())%>',dipartimento:'<%=user.getDepartmentAcronym()%>',id:'<%=user.getUsername()%>',idUser:'<%=user.getId()%>'};
        Application.user.dipartimentoExt=choseDipartimento('<%=user.getDepartmentAcronym()%>');

		listDipartimenti();
		$.each($.browser, function(key, val) {
			if (key == 'msie')
				ie = true;
		});

		//loadGestione();
		var stateProvider = new Ext.state.CookieProvider({
			expires : new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 365))
		//1 year from now
		});
		Ext.state.Manager.setProvider(stateProvider);

		Ext.ux.IFrameComponent = Ext.extend(Ext.BoxComponent, {
			onRender : function(ct, position) {
				this.el = ct.createChild({
					tag : 'iframe',
					id : 'iframe-' + this.id,
					frameBorder : 0,
					src : this.url
				});
			},
			onDestroy : function() {				
				var i = 0;
				var dummyArray = new Array();
				var busId = this.id;
				if(top.Message['Bus'+busId]){				
					top.Message['Bus'+busId].events.savedoc.listeners =  new Array();
					top.Message['Bus'+busId].events.online.listeners =  [top.Message['Bus'+busId].events.online.listeners[0]];
					top.Message['Bus'+busId].events.offline.listeners = [top.Message['Bus'+busId].events.offline.listeners[0]];
				}else if(busId=='searchBuilder' && top.Message['Bus7']){
					top.Message['Bus7'].events.modifystoredsearch.listeners = new Array();
					top.Message['Bus7'].events.savedoc.listeners =  new Array();
					top.Message['Bus7'].events.online.listeners =  [top.Message['Bus7'].events.online.listeners[0]];
					top.Message['Bus7'].events.offline.listeners = [top.Message['Bus7'].events.offline.listeners[0]];
				}
			}
		});
		var searchBuilder = new Ext.Panel({
			contentEl : 'center3',
			title : '${tabSearchBuilder}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/refresh2.gif" border="0" onclick="resetSearchBuilder();" style="cursor:hand;" alt="ricarica la ricerca" title="ricarica la ricerca">',
			id : 'tabSearchBuilder',
			closable : false,
			autoScroll : true,
			layout : 'fit',			
			items : []
		});
		var viewport = new Ext.Viewport({
			layout : 'border',
			items : [ new Ext.TabPanel({
				region : 'center',
				id : 'tabPanelDesktop',
				contentEl : 'center',
				deferredRender : false,
				collapsible : false,
				enableTabScroll : true,
				activeTab : 0,
				items : [ {
					contentEl : 'center1',
					title : '${tabArchive}',
					closable : false,
					autoScroll : true,
					layout : 'fit',
					items : [ new Ext.ux.IFrameComponent({
						id : "applications",
						url : "workspace.html"
					}) ]
				}, searchBuilder ],
				listeners : {
					'tabchange' : function(tabPanel, tab) {				        
						setCurrentTab(tab.title);
						if (tab.contentEl == "center1") {
							resetGestione();
						}else if(tab.id=='tabSearchBuilder'){
							if(tab.items.length==0){
								openLoading();
								Ext.getCmp("tabSearchBuilder").add(new Ext.ux.IFrameComponent({
									id : 'searchBuilder',
									url : 'searchBuilder.html?idArchive=1&ie=' + ie
								}));
								Ext.getCmp("tabSearchBuilder").doLayout();
							}
						}
					}
				}
			}) ]
		});
	});
	
	function testActiveTab(id) {
		if (Ext.getCmp(id) != null) {
			return Ext.getCmp("tab-" + id).isVisible();
		}else{
			return false;
		}
	}
	function addTab(title, id, url) {
		var tabs = Ext.getCmp('tabPanelDesktop');
		if (Ext.getCmp(id) != null) {
			Ext.getCmp(id).destroy();
			Ext.getCmp("tab-" + id).destroy();
		}
		tabs.add({
			title : title,
			iconCls : 'tabs',
			id : "tab-" + id,
			closable : true,
			autoDestroy : true,
			layout : 'fit',
			items : [ new Ext.ux.IFrameComponent({
				id : id,
				url : url,
				listeners : {
					'activate' : function(tabPanel, tab) {
						setCurrentTab(title);
					}
				}
			}) ]
		}).show();

	}
	function openCustomTabFromPost(id_archive,archive_label,idRecord){
    	addTab('<img width="12" height="12" src="img/archive_img/archive_'+id_archive+'.gif" border="0"/>&nbsp;'+archive_label,id_archive,'customWorkspace.html?idArchive='+id_archive+'&idRecord='+idRecord);
	}
	$(function() {
		$.ajaxSetup({
			cache : false,
			error : function(e, xhr, settings, exception) {
				if (e.status == 601) {
					top.location.reload();
				} else {
					closeLoading();
					Ext.MessageBox.alert('Status', 'Si è verificato un errore.');
				}
			}
		});

	});
	function resetSearchBuilder() {
		openLoading();			
		Ext.getCmp("tabSearchBuilder").removeAll(true);
		Ext.getCmp("tabSearchBuilder").add(new Ext.ux.IFrameComponent({
			id : 'searchBuilder',
			url : 'searchBuilder.html?idArchive=1&ie=' + ie
		}));
		Ext.getCmp("tabSearchBuilder").doLayout();
	}
	function checkArchiveStatus(){
		 Ext.Ajax.request({
		 	 url : 'system/checkArchives.html',
	         method: 'POST',
	         nocache: true,
	         params :{},
	              success: function ( result, request ) {
	                  var jsonData = Ext.util.JSON.decode(result.responseText);						                           
	                  for (var i = 0;i<jsonData.archives.length;i++){
	                	  var idArchive = jsonData.archives[i].id;
	                	  var offline = jsonData.archives[i].offline;
	                	  if(offline){
	                		  if(top['Message'] && top['Message']['Bus'+idArchive] && top.Message['Bus'+idArchive].events.offline){
	                				top['Message']['Bus'+idArchive].fireEvent('offline');
	                		  }
	                      }else{
	                    	  if(top['Message'] && top['Message']['Bus'+idArchive] && top.Message['Bus'+idArchive].events.offline){
	              				top['Message']['Bus'+idArchive].fireEvent('online');
	              		  	  }
	                      }
	                  }
	           },
	              failure: function ( result, request ) {
	        	     //Ext.Msg.alert('Attenzione','Si è verificato un errore!');
	           }
	   });
		
	}
	function checkMessageReceived(){
		 var systemMessage
		 Ext.Ajax.request({
		 	 url : 'messagesManager.html',
	         method: 'POST',
	         nocache: true,
	         params :{action:'checkMessageReceived'},
	         success: function ( result, request ) {
	                  var jsonData = Ext.util.JSON.decode(result.responseText);
	                  var simpleMessage = false;
	                  if(jsonData.messages.length>0){	                	  
		                  for (var i = 0;i<jsonData.messages.length;i++){
			                  messageType = jsonData.messages[i].msg_type;
			                  if(jsonData.messages[i].modal=='true' || jsonData.messages[i].modal==true){
									if(jsonData.messages[i].msg_type=='<%=MessageTypes.SYSTEM_MESSAGE%>'){										
										messagesManagerModalSystemMSG(jsonData.messages[i].id,'<b>'+jsonData.messages[i].object+'</b><br>'+jsonData.messages[i].text);
									}else{
										messagesManagerModalMSG(jsonData.messages[i].id,'L\'utente:<b>'+jsonData.messages[i].sender+'</b> ti ha inviato il seguente messaggio<br>con oggetto:<b>'+jsonData.messages[i].object+'</b><br>e testo:<br>'+jsonData.messages[i].text);
									}
				              }else{
				            	  simpleMessage = true;
						      }
		                  }
		                  if(simpleMessage && top['Message'] && top['Message']['Bus_messages_manager'] && top.Message['Bus_messages_manager'].events.messageReceived){
	              				top['Message']['Bus_messages_manager'].fireEvent('messageReceived');
	              		  }
			          }
             },
             failure: function ( result, request ) {
             }
	   });
		
	}        
</script>
<link type="text/css" href="css/redmond/jquery-ui-1.8.6.custom.css" rel="stylesheet" />

</head>
<body>
<!--div id="header" class="x-hide-display header">
		<div class="header_box">
			<div class="header_title"><%=user.getCompany()%>
				${messageVariable}
				<div class="header_logo">
					<%if (user.getImageLogo() != null && !user.getImageLogo().equals("")) {%><img src="img/<%=user.getImageLogo()%>" alt="<%=user.getCompany()%>" />
					<%}%>
				</div>
			</div>
		</div>
	</div-->
<div id="center" class="x-hide-display">
<div id="center1" class="x-hide-display center1"></div>
<div id="center3" class="x-hide-display"></div>
<div id="center4" class="x-hide-display"></div>
<%
	if (admin) {
%>
<div id="center2" class="x-hide-display"></div>
<%
	}
%>
</div>
<div id="footer" class="x-hide-display"></div>
</body>
</html>