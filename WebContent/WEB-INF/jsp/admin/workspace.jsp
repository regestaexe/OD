<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="java.math.BigInteger"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Departments"%>
<%@page import="com.openDams.bean.Companies"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>openDams</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="../img/sqlxdicon.ico" type="image/png" />
<link rel="shortcut icon" href="../img/sqlxdicon.ico" type="image/png" />
<script type="text/javascript" src="../ext-4.2/include-ext.js"></script>
<script type="text/javascript" src="../js/jquery-1.4.2.min.js" type="text/javascript"></script>
<link rel="stylesheet" href="../js/codemirror/lib/codemirror.css" />
<link rel="stylesheet" href="../js/codemirror/mode/xml/xml.css" />
<script type="text/javascript" src="../js/codemirror/lib/codemirror.js"></script>
<script type="text/javascript" src="../js/codemirror/mode/xml/xml.js"></script>
<script type="text/javascript" src="../js/application.js"></script>

<link rel="stylesheet" type="text/css" href="../ext-4.2/portal/portal.css" />
<link rel="stylesheet" type="text/css" href="../ext-4.2/ux/css/GroupTabPanel.css" />


<%@include file="../locale.jsp"%>

<script type="text/javascript">
	Ext.onReady(function() {

		Ext.Loader.setConfig({
			enabled : true
		});

		Ext.Loader.setPath('Ext.ux', '../ext-4.2/ux/');

		Ext.require([ 'Ext.ux.GroupTabPanel' ]);

		Ext.define('MyApp.tab.GroupTabPanel', {
			extend : 'Ext.ux.GroupTabPanel',
			alias : [ 'widget.largegrouptabpanel' ],
			tabBarWidth : 300,
			initComponent : function() {
				this.callParent(arguments);
				this.down('treepanel').setWidth(this.tabBarWidth);
			}
		});

		Ext.onReady(function() {
			$.ajax({
				url : 'indexListWorkspace.html',
				dataType : 'script',
				success : function(data) {
					Ext.create('Ext.Viewport', {
						layout : 'fit',
						items : [ {
						 
							xtype : 'largegrouptabpanel',
							activeGroup : 0,
							id : 'adminWorkspace',
							items : [ {
								mainItem : 0,
								id : 'adminWorkspaceContent',
								items : panelIndexes
							}  ]
						} ]
					});
				}
			});

		});
		
	});
</script>

</head>
<body>

</body>
</html>