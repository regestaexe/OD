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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>ExtTop - Desktop Sample App</title>
    <link rel="icon" href="img/sqlxdicon.ico" type="image/png" />
	<link rel="shortcut icon" href="img/sqlxdicon.ico" type="image/png" />
	<link href="css/style_sky_blue.css" id="theme_color_tree" rel="stylesheet" type="text/css" />
	<link href="css/StyleSheet.css" rel="stylesheet" type="text/css" />
	<link href="css/confirm.css" rel="stylesheet" type="text/css" />
	<link href="css/jquery.contextMenu.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="css/resources/css/ext-all-notheme.css" />
	<link rel="stylesheet" type="text/css" id="theme_color" href="css/resources/css/xtheme-blue.css" />
	<link type="text/css" href="css/redmond/jquery-ui-1.8.6.custom.css" rel="stylesheet" />
	<link href="css/style.css" rel="stylesheet" type="text/css" />
	<script src="js/jquery-1.4.2.min.js" type="text/javascript"></script>
	
	
	<script type="text/javascript" src="js/ext-js-3.4.0/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" src="js/ext-js-3.4.0/ext-all.js"></script>
	<script type="text/javascript" src="js/ext-js-3.4.0/locale/ext-lang-it.js"></script>
	
	
	
	<script type="text/javascript" src="js/application.js"></script>
    <link rel="stylesheet" type="text/css" href="css/showcase/desktop.css" />


    <!-- DESKTOP -->
    <script type="text/javascript" src="js/ext-js-3.4.0/showcase/StartMenu.js"></script>
    <script type="text/javascript" src="js/ext-js-3.4.0/showcase/TaskBar.js"></script>
    <script type="text/javascript" src="js/ext-js-3.4.0/showcase/Desktop.js"></script>
    <script type="text/javascript" src="js/ext-js-3.4.0/showcase/App.js"></script>
    <script type="text/javascript" src="js/ext-js-3.4.0/showcase/Module.js"></script>
    <script type="text/javascript">
    var globalOpt={};
    globalOpt.document = {};
       
    MyDesktop = new Ext.app.App({
    	init :function(){
    		Ext.QuickTips.init();
    	},

    	getModules : function(){
    		return [
    			new MyDesktop.GridWindow(),
                new MyDesktop.TabWindow(),
                new MyDesktop.AccordionWindow(),
                new MyDesktop.BogusMenuModule(),
                new MyDesktop.BogusModule()
    		];
    	},

        // config for the start menu
        getStartConfig : function(){
            return {
                title: top.Application.user.nome+" "+top.Application.user.cognome,
                iconCls: 'user',
                toolItems: [
                   {
                    text:'Esci',
                    iconCls:'logout',
                    scope:this,
                    handler: function() {
				        	top.location.href='<c:url value="/j_spring_security_logout"/>';
				  	}
                }]
            };
        }
    });



    /*
     * Example windows
     */
    MyDesktop.GridWindow = Ext.extend(Ext.app.Module, {
        id:'grid-win',
        init : function(){
            this.launcher = {
                text: 'Grid Window',
                iconCls:'icon-grid',
                handler : this.createWindow,
                scope: this
            }
        },
        createWindow : function(){
            var desktop = this.app.getDesktop();
            var win = desktop.getWindow('grid-win');
            if(!win){
                win = desktop.createWindow({
                    id: 'grid-win',
                    title:'Grid Window',
                    width:740,
                    height:480,
                    iconCls: 'icon-grid',
                    shim:false,
                    animCollapse:false,
                    constrainHeader:true,

                    layout: 'fit',
                    items:
                        new Ext.grid.GridPanel({
                            border:false,
                            ds: new Ext.data.Store({
                                reader: new Ext.data.ArrayReader({}, [
                                   {name: 'company'},
                                   {name: 'price', type: 'float'},
                                   {name: 'change', type: 'float'},
                                   {name: 'pctChange', type: 'float'}
                                ]),
                                data: Ext.grid.dummyData
                            }),
                            cm: new Ext.grid.ColumnModel([
                                new Ext.grid.RowNumberer(),
                                {header: "Company", width: 120, sortable: true, dataIndex: 'company'},
                                {header: "Price", width: 70, sortable: true, renderer: Ext.util.Format.usMoney, dataIndex: 'price'},
                                {header: "Change", width: 70, sortable: true, dataIndex: 'change'},
                                {header: "% Change", width: 70, sortable: true, dataIndex: 'pctChange'}
                            ]),

                            viewConfig: {
                                forceFit:true
                            },
                            //autoExpandColumn:'company',

                            tbar:[{
                                text:'Add Something',
                                tooltip:'Add a new row',
                                iconCls:'add'
                            }, '-', {
                                text:'Options',
                                tooltip:'Blah blah blah blaht',
                                iconCls:'option'
                            },'-',{
                                text:'Remove Something',
                                tooltip:'Remove the selected item',
                                iconCls:'remove'
                            }]
                        })
                });
            }
            win.show();
        }
    });



    MyDesktop.TabWindow = Ext.extend(Ext.app.Module, {
        id:'tab-win',
        init : function(){
            this.launcher = {
                text: 'Tab Window',
                iconCls:'tabs',
                handler : this.createWindow,
                scope: this
            }
        },
        createWindow : function(){
            var desktop = this.app.getDesktop();
            var win = desktop.getWindow('tab-win');
            if(!win){
                win = desktop.createWindow({
                    id: 'tab-win',
                    title:'Tab Window',
                    width:740,
                    height:480,
                    iconCls: 'tabs',
                    shim:false,
                    animCollapse:false,
                    border:false,
                    constrainHeader:true,

                    layout: 'fit',
                    items:
                        new Ext.TabPanel({
                            activeTab:0,

                            items: [{
                                title: 'Tab Text 1',
                                header:false,
                                html : '<p>Something useful would be in here.</p>',
                                border:false
                            },{
                                title: 'Tab Text 2',
                                header:false,
                                html : '<p>Something useful would be in here.</p>',
                                border:false
                            },{
                                title: 'Tab Text 3',
                                header:false,
                                html : '<p>Something useful would be in here.</p>',
                                border:false
                            },{
                                title: 'Tab Text 4',
                                header:false,
                                html : '<p>Something useful would be in here.</p>',
                                border:false
                            }]
                        })
                });
            }
            win.show();
        }
    });



    MyDesktop.AccordionWindow = Ext.extend(Ext.app.Module, {
        id:'acc-win',
        init : function(){
            this.launcher = {
                text: 'Accordion Window',
                iconCls:'accordion',
                handler : this.createWindow,
                scope: this
            }
        },

        createWindow : function(){
            var desktop = this.app.getDesktop();
            var win = desktop.getWindow('acc-win');
            if(!win){
                win = desktop.createWindow({
                    id: 'acc-win',
                    title: 'Accordion Window',
                    width:250,
                    height:400,
                    iconCls: 'accordion',
                    shim:false,
                    animCollapse:false,
                    constrainHeader:true,

                    tbar:[{
                        tooltip:{title:'Rich Tooltips', text:'Let your users know what they can do!'},
                        iconCls:'connect'
                    },'-',{
                        tooltip:'Add a new user',
                        iconCls:'user-add'
                    },' ',{
                        tooltip:'Remove the selected user',
                        iconCls:'user-delete'
                    }],

                    layout:'accordion',
                    border:false,
                    layoutConfig: {
                        animate:false
                    },

                    items: [
                        new Ext.tree.TreePanel({
                            id:'im-tree',
                            title: 'Online Users',
                            loader: new Ext.tree.TreeLoader(),
                            rootVisible:false,
                            lines:false,
                            autoScroll:true,
                            tools:[{
                                id:'refresh',
                                on:{
                                    click: function(){
                                        var tree = Ext.getCmp('im-tree');
                                        tree.body.mask('Loading', 'x-mask-loading');
                                        tree.root.reload();
                                        tree.root.collapse(true, false);
                                        setTimeout(function(){ // mimic a server call
                                            tree.body.unmask();
                                            tree.root.expand(true, true);
                                        }, 1000);
                                    }
                                }
                            }],
                            root: new Ext.tree.AsyncTreeNode({
                                text:'Online',
                                children:[{
                                    text:'',
                                    expanded:true,
                                    children:[{
                                        text:'Jack',
                                        iconCls:'user',
                                        leaf:true
                                    },{
                                        text:'Brian',
                                        iconCls:'user',
                                        leaf:true
                                    },{
                                        text:'Jon',
                                        iconCls:'user',
                                        leaf:true
                                    },{
                                        text:'Tim',
                                        iconCls:'user',
                                        leaf:true
                                    },{
                                        text:'Nige',
                                        iconCls:'user',
                                        leaf:true
                                    },{
                                        text:'Fred',
                                        iconCls:'user',
                                        leaf:true
                                    },{
                                        text:'Bob',
                                        iconCls:'user',
                                        leaf:true
                                    }]
                                },{
                                    text:'Family',
                                    expanded:true,
                                    children:[{
                                        text:'Kelly',
                                        iconCls:'user-girl',
                                        leaf:true
                                    },{
                                        text:'Sara',
                                        iconCls:'user-girl',
                                        leaf:true
                                    },{
                                        text:'Zack',
                                        iconCls:'user-kid',
                                        leaf:true
                                    },{
                                        text:'John',
                                        iconCls:'user-kid',
                                        leaf:true
                                    }]
                                }]
                            })
                        }), {
                            title: 'Settings',
                            html:'<p>Something useful would be in here.</p>',
                            autoScroll:true
                        },{
                            title: 'Even More Stuff',
                            html : '<p>Something useful would be in here.</p>'
                        },{
                            title: 'My Stuff',
                            html : '<p>Something useful would be in here.</p>'
                        }
                    ]
                });
            }
            win.show();
        }
    });

    // for example purposes
    var windowIndex = 0;

    MyDesktop.BogusModule = Ext.extend(Ext.app.Module, {
        init : function(){
            this.launcher = {
                text: 'Window '+(++windowIndex),
                iconCls:'bogus',
                handler : this.createWindow,
                scope: this,
                windowId:windowIndex
            }
        },

        createWindow : function(src){
            var desktop = this.app.getDesktop();
            var win = desktop.getWindow('bogus'+src.windowId);
            if(!win){
                win = desktop.createWindow({
                    id: 'bogus'+src.windowId,
                    title:src.text,
                    width:640,
                    height:480,
                    html : '<p>Something useful would be in here.</p>',
                    iconCls: 'bogus',
                    shim:false,
                    animCollapse:false,
                    constrainHeader:true
                });
            }
            win.show();
        }
    });


    MyDesktop.BogusMenuModule = Ext.extend(MyDesktop.BogusModule, {
        init : function(){
            this.launcher = {
                text: 'Bogus Submenu',
                iconCls: 'bogus',
                handler: function() {
    				return false;
    			},
                menu: {
                    items:[{
                        text: 'Bogus Window '+(++windowIndex),
                        iconCls:'bogus',
                        handler : this.createWindow,
                        scope: this,
                        windowId: windowIndex
                        },{
                        text: 'Bogus Window '+(++windowIndex),
                        iconCls:'bogus',
                        handler : this.createWindow,
                        scope: this,
                        windowId: windowIndex
                        },{
                        text: 'Bogus Window '+(++windowIndex),
                        iconCls:'bogus',
                        handler : this.createWindow,
                        scope: this,
                        windowId: windowIndex
                        },{
                        text: 'Bogus Window '+(++windowIndex),
                        iconCls:'bogus',
                        handler : this.createWindow,
                        scope: this,
                        windowId: windowIndex
                        },{
                        text: 'Bogus Window '+(++windowIndex),
                        iconCls:'bogus',
                        handler : this.createWindow,
                        scope: this,
                        windowId: windowIndex
                    }]
                }
            }
        }
    });


    // Array data for the grid
    Ext.grid.dummyData = [
        ['3m Co',71.72,0.02,0.03,'9/1 12:00am'],
        ['Alcoa Inc',29.01,0.42,1.47,'9/1 12:00am'],
        ['American Express Company',52.55,0.01,0.02,'9/1 12:00am'],
        ['American International Group, Inc.',64.13,0.31,0.49,'9/1 12:00am'],
        ['AT&T Inc.',31.61,-0.48,-1.54,'9/1 12:00am'],
        ['Caterpillar Inc.',67.27,0.92,1.39,'9/1 12:00am'],
        ['Citigroup, Inc.',49.37,0.02,0.04,'9/1 12:00am'],
        ['Exxon Mobil Corp',68.1,-0.43,-0.64,'9/1 12:00am'],
        ['General Electric Company',34.14,-0.08,-0.23,'9/1 12:00am'],
        ['General Motors Corporation',30.27,1.09,3.74,'9/1 12:00am'],
        ['Hewlett-Packard Co.',36.53,-0.03,-0.08,'9/1 12:00am'],
        ['Honeywell Intl Inc',38.77,0.05,0.13,'9/1 12:00am'],
        ['Intel Corporation',19.88,0.31,1.58,'9/1 12:00am'],
        ['Johnson & Johnson',64.72,0.06,0.09,'9/1 12:00am'],
        ['Merck & Co., Inc.',40.96,0.41,1.01,'9/1 12:00am'],
        ['Microsoft Corporation',25.84,0.14,0.54,'9/1 12:00am'],
        ['The Coca-Cola Company',45.07,0.26,0.58,'9/1 12:00am'],
        ['The Procter & Gamble Company',61.91,0.01,0.02,'9/1 12:00am'],
        ['Wal-Mart Stores, Inc.',45.45,0.73,1.63,'9/1 12:00am'],
        ['Walt Disney Company (The) (Holding Company)',29.89,0.24,0.81,'9/1 12:00am']
    ];
    function openArchiveTab(id_archive,archive_label){
 		parent.addTab('<img width="12" height="12" src="img/archive_img/archive_'+id_archive+'.gif" border="0"/>&nbsp;'+archive_label,id_archive,'documentalWorkspace.html?idArchive='+id_archive);
	}
    </script>
<style>
#menu-wrapper{
	 position: relative;
	 width: 100%;
	 height: 100%;
	}
</style>    
</head>
<body scroll="no">
	<div id="x-desktop">
		<div id="menu-wrapper">
			<span style="margin:5px; float:right;display:block;height:90%;"><img src="img/showcase/powered2.gif" /></span>
		    <dl id="x-shortcuts">		       		       
		         <%if(request.getAttribute("archiveList")!=null){ 
				   for(int i=0;i<documentalList.size();i++){
					   Archives archive =  documentalList.get(i);
					   %>
			    <dt id="archive_<%=archive.getIdArchive()%>-win-shortcut_">
		            <a href="#" onclick="openArchiveTab('<%=archive.getIdArchive()%>','<%=JsSolver.escapeSingleApex(archive.getLabel())%>');"><img src="img/archive_img/archive_<%=archive.getIdArchive()%>_48.gif"/>
		            <div><%=archive.getLabel()%></div></a>
		        </dt>
					   <%
				   }
				}
				%>		
				<dt id="acc-win-shortcut">
		            <a href="#"><img src="img/showcase/s.gif" />
		            <div>Gestione Messaggi</div></a>
		        </dt>	
		    </dl>
		</div>     
	</div>
	
	<div id="ux-taskbar">
		<div id="ux-taskbar-start"></div>		
		<div id="ux-taskbuttons-panel"></div>
		<div class="x-clear"></div>
	</div>

</body>
</html>
