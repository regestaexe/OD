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
<title>openDams</title>
<link rel="icon" href="img/sqlxdicon.ico" type="image/png" />
<link rel="shortcut icon" href="img/sqlxdicon.ico" type="image/png" />
<link href="css/style_sky_blue.css" id="theme_color_tree" rel="stylesheet" type="text/css" />
<link href="css/StyleSheet.css" rel="stylesheet" type="text/css" />
<link href="css/confirm.css" rel="stylesheet" type="text/css" />
<link href="css/jquery.contextMenu.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="css/resources/css/ext-all-notheme.css" />
<link rel="stylesheet" type="text/css" id="theme_color" href="css/resources/css/xtheme-blue.css" />
<link href="css/style.css" rel="stylesheet" type="text/css" />
<link type="text/css" href="css/redmond/jquery-ui-1.8.6.custom.css" rel="stylesheet" />
<script src="js/jquery-1.4.2.min.js" type="text/javascript"></script>
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
    
    Ext.onReady(function(){
 
        Application.user = {}; 
        Application.user = {company:'<%=user.getIdCompany()%>',nome:'<%=JsSolver.escapeSingleApex(user.getName())%>', cognome:'<%=JsSolver.escapeSingleApex(user.getLastname())%>',dipartimento:'<%=user.getDepartmentAcronym()%>',id:'<%=user.getUsername()%>',idUser:'<%=user.getId()%>'};
        Application.user.dipartimentoExt=choseDipartimento('<%=user.getDepartmentAcronym()%>');
        
        var ie=false;
        listDipartimenti();
    	$.each($.browser, function(key, val) {
    	if(key=='msie')
    		ie=true;
    	});
         
        
    	//loadGestione();
		 var stateProvider =   new Ext.state.CookieProvider({
			   expires: new Date(new Date().getTime()+(1000*60*60*24*365)) //1 year from now
			});
		Ext.state.Manager.setProvider(stateProvider);
		
 	   		ds = new Ext.data.JsonStore({
 		    url: 'ajax/documental_adv_search.html',
 		    root: 'data',
 		    idProperty: 'id',
 		    totalProperty: 'totalCount',
 		    baseParams:{id_archive:'<%=request.getParameter("idArchive")%>',action:'search',limit:15},
 		    fields: ['id','descrizione','value'] 
 		}); 
    	
        Ext.ux.IFrameComponent = Ext.extend(Ext.BoxComponent, {
		    onRender : function(ct, position){
		          this.el = ct.createChild({tag: 'iframe', id: 'iframe-'+this.id, frameBorder: 0, src: this.url});
		    },
        	onDestroy: function(){
				for(var bus in top.Message){
					var i = 0;
					var dummyArray = new Array();
				 	for ( var a =0;a < top.Message[bus].events.savedoc.listeners.length;a++) {
	        			try{
	        				top.Message[bus].events.savedoc.listeners[a].scope.getEl().isVisible();
	        				  dummyArray[i] =  top.Message[bus].events.savedoc.listeners[a];
	        				  i++;
	        			}
	        			catch (e) {
	        			}
	        		}
	            	top.Message[bus].events.savedoc.listeners = dummyArray;
				}
        	}
		});
	    <%if (admin) {%>
        var tabAdminTools = new Ext.Panel({
       	 				contentEl: 'center2',
            			title: '${tabAdmin}',
            			closable: false,
            			autoScroll: true,
		     			layout:'fit',
		     			items: [ new Ext.ux.IFrameComponent({ id: 'adminTools', url: 'admin/adminTools.html' }) ]
		});
		<%}%>
		var searchBuilder = new Ext.Panel({
			contentEl: 'center3',
			title: '${tabSearchBuilder}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="img/refresh2.gif" border="0" onclick="resetSearchBuilder();" style="cursor:hand;" alt="ricarica la ricerca" title="ricarica la ricerca">',
			id:'tabSearchBuilder',
			closable: false,
			autoScroll: true,
 			layout:'fit',
 			items: [ new Ext.ux.IFrameComponent({ id: 'searchBuilder', url: 'searchBuilder.html?idArchive=1&ie='+ie }) ]
		});
		/*var showcase = new Ext.Panel({
			contentEl: 'center4',
			title: '${tabShowcase}',
			id:'tabShowcase',
			closable: false,
			autoScroll: true,
 			layout:'fit',
 			items: [ ]
		});*/
        var viewport = new Ext.Viewport({
            layout: 'border',
            items: [
            /*{
                region: 'north',
                contentEl: 'header',
                split: false,
                height: 30, 
                collapsible: false,
                margins: '0 0 0 0'
            },*/ {
                region: 'south',
                contentEl: 'footer',
                split: false,
                minSize: 100,
                maxSize: 200,
                collapsible: false, 
                title: '<strong>'+top.Application.user.nome+ " "+top.Application.user.cognome+'</strong> <em>'+top.Application.user.dipartimentoExt+'</em> &#160;-&#160; <a href="<c:url value="/j_spring_security_logout"/>">${footerLogout}</a>',
                margins: '0 0 0 0'
            },new Ext.TabPanel({
                region: 'center',
                id:'tabPanelDesktop',
                contentEl: 'center',
                deferredRender: false,
                collapsible: false,
                enableTabScroll:true,
                activeTab: 0,  
                items: [
                     	{
		                    contentEl: 'center1',
		                    title: '${tabArchive}',
		                    closable: false, 
		                    autoScroll: true,
		                    layout:'fit',
		                    items: [new Ext.ux.IFrameComponent({ id: "applications", url: "applications.html" }) ]
		                },
		                searchBuilder
		                /*showcase*/
                       <%if (admin) {%>
                     	,
                     	tabAdminTools
                     	<%}%>
                ], listeners: {
                    'tabchange': function(tabPanel, tab){ 
                        	setCurrentTab(tab.title);
    	                    if(tab.contentEl=="center1"){
    	                    	resetGestione();
    	                    }/*else if(tab.contentEl=="center4"){
    	                    	Ext.getCmp('tabShowcase').removeAll(true);
    	                    	Ext.getCmp('tabShowcase').add( new Ext.ux.IFrameComponent({ id: 'showcase', url: 'showcase.html'}) );
    	                    	Ext.getCmp('tabShowcase').doLayout();
        	                }*/
                        }
                	}
            })
            ]
        });   
    });
    function addTab(title,id,url){
        var tabs = Ext.getCmp('tabPanelDesktop');
        if(Ext.getCmp(id)!=null){
        	Ext.getCmp(id).destroy();
        	Ext.getCmp("tab-"+id).destroy();
        } 
        tabs.add({
            title: title,
            iconCls: 'tabs',
            id:"tab-"+id,
            closable:true,
            autoDestroy:true,
            layout:'fit',
            items: [new Ext.ux.IFrameComponent({ id: id, url: url,listeners: {
                'activate': function(tabPanel, tab){ 
                	setCurrentTab(title);
                }
        	}}) ]
        }).show();
        
	}   
    $(function(){
        $.ajaxSetup({cache:false,error:function(e, xhr, settings, exception){
 		   if(e.status == 601){
 			   top.location.reload();
 		   }else{
 			 closeLoading();
 			 Ext.MessageBox.alert('Status', 'Si è verificato un errore.');
 		   }
	 	}});
	 	
   //   connect("applicationbus/accessStatus");  
 	 	//publish("applicationbus/accessStatus","aaaaaaaaaaaaaaaaaaaaaaaa ");  

       });
    function resetSearchBuilder(){
        console.debug(Ext.getCmp("tabSearchBuilder"));
    	Ext.getCmp("tabSearchBuilder").removeAll(true);
    	Ext.getCmp("tabSearchBuilder").add( new Ext.ux.IFrameComponent({ id: 'searchBuilder', url: 'searchBuilder.html?idArchive=1&ie='+ie }) );
    	Ext.getCmp("tabSearchBuilder").doLayout();
     }

    </script>
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