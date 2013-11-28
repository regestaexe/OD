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

<%
if(request.getAttribute("archives")!=null){
Archives archives = (Archives)request.getAttribute("archives");
UserDetails user =  (UserDetails)((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
Collection<GrantedAuthority> authorities = user.getAuthorities();
Object[] authoritiesList = authorities.toArray();
boolean editOn = false;
for(int s=0;s<authoritiesList.length;s++){
	GrantedAuthority grantedAuthority = (GrantedAuthority)authoritiesList[s];
	if(RoleTester.testEditing(grantedAuthority.getAuthority())){
		editOn = true;
		break;
	}	
} 
boolean newWindow = false;
if(request.getParameter("win")!=null){
	newWindow = true;
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="java.math.BigInteger"%>
<html xmlns="http://www.w3.org/1999/xhtml" >
<head>
<title>openDams</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="img/sqlxdicon.png" type="image/png" />
<LINK REL="shortcut icon" HREF="img/sqlxdicon.ico" type="image/png">	
<!-- link href="css/style.css" id="theme_color_tree" rel="stylesheet" type="text/css"  /-->
<link href="css/style_sky_blue.css" id="theme_color_tree" rel="stylesheet" type="text/css"  />
<link href="css/StyleSheet.css" rel="stylesheet" type="text/css" />
<link href="css/confirm.css" rel="stylesheet" type="text/css" />  
<link href="css/jquery.ui.all.css" rel="stylesheet" type="text/css" />  
<link href="css/jquery.contextMenu.css" rel="stylesheet" type="text/css" />

<link rel="stylesheet" type="text/css" href="css/resources/css/ext-all-notheme.css" />
<link rel="stylesheet" type="text/css" id="theme_color" href="css/resources/css/xtheme-blue.css" /> 
<link rel="stylesheet" type="text/css" href="css/resources/css/debug.css" />
<script src="js/jquery-1.4.2.min.js" type="text/javascript"></script>
<script src="js/jquery-ui-1.8rc3.custom.min.js" type="text/javascript"></script>
<script src="js/jquery.simplemodal-1.1.1.js" type="text/javascript"></script>
<script src="js/jquery.contextMenu.js" type="text/javascript"></script>
<script type="text/javascript" src="js/ext-js/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="js/ext-js/ext-all.js"></script>
<script type="text/javascript" src="js/ext-js/debug.js"></script>
<script type="text/javascript" src="js/ext-js/ux/SearchField.js"></script>
<script src="js/codemirror.js" type="text/javascript"></script>
<script src="js/jquery.autogrow.js" type="text/javascript"></script>
<%@include file="../locale.jsp"%>
    <script type="text/javascript">
    var ds;
    var ds_relation;
    Ext.onReady(function(){
    	Ext.state.Manager.setProvider(new Ext.state.CookieProvider());  
    	ds = new Ext.data.JsonStore({
 		    url: 'ajax/autocomplete.html',
 		    root: 'data',
 		    idProperty: 'id',
 		    totalProperty: 'totalCount',
 		    baseParams:{id_archive:'<%=request.getParameter("idArchive")%>',limit:20},
 		    fields: ['id','value']
 		});
    	ds_relation = new Ext.data.JsonStore({
 		    url: 'ajax/autocomplete.html',
 		    root: 'data',
 		    idProperty: 'id',
 		    totalProperty: 'totalCount',
 		    baseParams:{id_archive:'<%=request.getParameter("idArchive")%>',limit:20,relation:''},
 		    fields: ['id','value','relation','reopen_accordion']
 		});
         var resultTpl = new Ext.XTemplate(
             '<tpl for=".">',
             '<div class="search-item" style="height:12px;">',
                 '<h3>',
                 '<a href="#no" onclick="openRecord(\'{id}\');">{value}</a></h3>',
             '</div></tpl>'
         );  
         var resultTpl_relation = new Ext.XTemplate(
              '<tpl for=".">',
              '<div class="search-item" style="float:left;height:12px;">',
                  '<div style="float:left;">',
                  '<a href="#no" onclick="addRelation(\'{id}\',\'{relation}\',\'{reopen_accordion}\');" title="${skosAddrelationButtonLabel}">{value}</a></div><div style="float:right;"><a href="#no" onclick="addRelation(\'{id}\',\'{relation}\');" title="${skosAddrelationButtonLabel}"><img src="img/skos_tab/add_relation_concept.gif"  title="${skosAddrelationButtonLabel}"></a></div>',
              '</div></tpl>'
         );       
        var viewport = new Ext.Viewport({
            layout: 'border',
            items: [
            <%if(newWindow){%>
            {
                region: 'north',
                contentEl: 'header',
                split: false,
                height: 50,
                minSize: 100,
                maxSize: 200,
                collapsible: false,
                margins: '0 0 0 0'
            }, {
                region: 'south',
                contentEl: 'footer',
                split: false,
                minSize: 100,
                maxSize: 200,
                collapsible: false, 
                title: '${footerMessage} <strong><%=JsSolver.escapeSingleApex(user.getName())%> <%=JsSolver.escapeSingleApex(user.getLastname())%></strong>',
                margins: '0 0 0 0'
            },
			<%}%>
            {
                region: 'west',
                id: 'west-panel',
                title: '<span class="tree_top_title">${skosWestpanelTitle}</span>',
                split: true,
                width: 400,
                minSize: 175,
                maxSize: 600,
                collapsible: true,
                margins: '0 0 0 0',
                layout: {
                    type: 'accordion',
                    animate: true
                },
                items: [{
                    contentEl: 'tree',
                    id:'tree_accordion',
                    title: '${skosWestpanelTreeTitle}',
                    border: false,
                    autoScroll: true,
                    iconCls: 'navigation_img'
                }, {
                	contentEl: 'results',
                	id:'results_accordion',
                	layout:'fit',
                    title: '${skosWestpanelSearchTitle}',
                    border: false,
                    autoScroll: true,
                    iconCls: 'results_img',
                    items:[
                           new Ext.Panel({
                        	    applyTo: 'search-panel',
		                        autoScroll:true,
		                        border: false,
		                        items: new Ext.DataView({
		                            tpl: resultTpl,
		                            store: ds,
		                            itemSelector: 'div.search-item'
		                        }),
		                        tbar: [
		                            'concept: ', ' ',
		                            new Ext.ux.form.SearchField({
									    store: ds,
									    id:'query',
									    mode: 'remote',								   
									    selectOnFocus:false,
									    width: 318,
									    emptyText:'${skosWestpanelSearchConceptInputValue}'
		                            })
		                        ],
		                        bbar: new Ext.PagingToolbar({
		                        	id:'paging_bar',
		                        	store: ds,
		                            pageSize: 20,
		                            displayInfo: true,
		                            displayMsg: '${skosWestpanelSearchFooter}',
		                            emptyMsg: "${skosWestpanelSearchNoresults}"
		                        })
                    		})
                          ]
                }
                , {
                	contentEl: 'relation_results',
                	id:'relation_results_accordion',
                	layout:'fit',
                    title: '${skosWestpanelRelationTitle}',
                    border: false,
                    collapsible: false,
                    disabled:true,
                    iconCls: 'results_img_relation',
                    items:[
                           new Ext.Panel({
                        	    applyTo: 'relation_search-panel',
		                        autoScroll:true,
		                        border: false,
		                        items: new Ext.DataView({
		                            tpl: resultTpl_relation,
		                            store: ds_relation,
		                            itemSelector: 'div.search-item'
		                        }),
		                        tbar: [
		                            'concept: ', ' ',
		                            new Ext.ux.form.SearchField({
									    store: ds_relation,
									    id:'query_relation',
									    mode: 'remote',								   
									    selectOnFocus:false,
									    width: 318,
									    emptyText:'${skosWestpanelSearchConceptInputValue}'
		                            })
		                        ],
		                        bbar: new Ext.PagingToolbar({
		                        	id:'paging_bar_relation',
		                        	store: ds_relation,
		                            pageSize: 20,
		                            displayInfo: true,
		                            displayMsg: '${skosWestpanelSearchFooter}',
		                            emptyMsg: "${skosWestpanelSearchNoresults}"
		                        })
                    		})
                          ]
                }
                ]
            },new Ext.TabPanel({
                region: 'center',
                contentEl: 'center',
                deferredRender: false,
                activeTab: 0,
                items: [
                <%if(archives.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.THESAURUS){%>
                	{
                    id:'skos_tab_tab',	
                	contentEl: 'skos_tab',
                    title: '${skosCenterSkosTab}',
                    closable: false,
                    autoScroll: true
                    <%if(editOn){%>
                     ,
                     buttons: [
	                    {
	                        text:'${buttonSave}',
	                        handler: function(){
	                        	openLoading();
		                    	$.post("ajax/save.html", $("form[name='editForm']").serialize(),function(data){
		                    		closeLoading();
		                    		Ext.MessageBox.alert('${messageStatus}', '${messageStatusTextOperation}');
		                    		reloadTree();
		                    		openRecord($("form[name='editForm']").find("input[name='idRecord']").attr("value"));
		                    	});
	                    	}
	                    },{
	                        text: '${buttonReset}',
	                        handler: function(){
	                    	 document.editForm.reset();
	                        }
	                    },{
	                        text: '${buttonDelete}',
	                        handler: function(){                        
		                        if(parseInt($("form[name='editForm']").find("input[name='totNT']").attr("value"),10)==0){
		                        	openLoading();
		                        	$("#relation_results").load("ajax/save.html?idRecord="+$("form[name='editForm']").find("input[name='idRecord']").attr("value")+"&action=delete",function(){
		                        		closeLoading();
			                    		Ext.MessageBox.alert('${messageStatus}', '${messageStatusTextOperation}');
			                    		moveToFather();
		                       		 });
				                }else{
				                	Ext.MessageBox.show({
			                             msg: '${skosMessageDeletingConcept}',
			                             buttons: Ext.MessageBox.OK,                         
			                             icon: 'ext-mb-warning'
			                         });
						        }
	                        }
	                    }, {
	                        text:'${buttonVersion}',
	                        handler: function(){
		                        window.open("version_manager.html?idRecord="+$("form[name='editForm']").find("input[name='idRecord']").attr("value"), 'version_manager', ',type=fullWindow,fullscreen,resizable=yes,toolbar=false');

	                    	}
	                    }
                    ]
                    <%}%>
                	}
                	,{
                		id: 'relations_tab_tab',
                        contentEl: 'relations_tab',
                        title: '${skosCenterRelationsTab}',
                        closable: false,
                        autoScroll: true
                    	}
                	,{
                        contentEl: 'visual_tab',
                        title: '${skosCenterVisualTab}',
                        closable: false,
                        autoScroll: true
                    	}
                <%}else{%>
                	{
                    contentEl: 'short_tab',
                    title: '${centeropenDamsShortTab}',
                    closable: false,
                    autoScroll: true
                	}
                <%}%>
                	,{
                    contentEl: 'xml_tab',
                    title: '${centerXmlTab}',
                    closable: false,
                    autoScroll: true
                	}
                	,{
                       contentEl: 'advSearch_tab',
                       title: '${centerAdvSearchTab}',
                       closable: false,
                       autoScroll: true,
                       buttons: [
         	                    {
         	                        text:'${buttonAdvSearchButton}',
         	                        handler: function(){
                                        search = false;
         	                    		var fields = $("form[name='adv_search_form']").serializeArray();
         	                        	$.each(fields, function(i, field){
         	                         		if($.trim(field.value)!='' && field.name!='action' && field.name!='idArchive'){
         	                         			search=true;
         	                         			return;
                 	                        } 
         	                            });
        	                            if(search){
        	                            	openLoading();
	         	                        	$("#adv_search_results").load("ajax/skos_adv_search.html?"+$("form[name='adv_search_form']").serialize(),function(){
	         	                        		closeLoading();
	         	                        		if($(".search_fields").is(":visible")){
		         	                        		$(".search_fields").hide(500);
		         	                   				$(".search_fields").removeClass("x-tool-collapse-north");
		         	                   				$(".search_fields").addClass("x-tool-collapse-south");
	         	                        		}
	             	                        });
        	                            }else{
        	                            	Ext.MessageBox.alert('${messageStatus}', '${messageStatusEmptyField}');
                	                    }
         	                    	}
         	                    },{
        	                        text: '${buttonReset}',
        	                        handler: function(){
        	                    	 document.adv_search_form.reset();
        	                        }
        	                    }]
                   	}
                	<%if(request.getAttribute("unlinked")!=null && ((BigInteger)request.getAttribute("unlinked")).intValue()>0){%>
                	,{
                       contentEl: 'unlinked_tab',
                       title: '${centerUnlinkedTab}',
                       closable: false,
                       autoScroll: true
                          
                   	}
                	<%}%>
                   	
                ],
                 listeners: {
                'tabchange': function(tabPanel, tab){
		                if(tab.contentEl=="visual_tab"){
		                	openLoading();
		                	$(".visual_tab").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=visual_tab&idRecord="+$(".skos_tab").find("input[name='idRecord']").attr("value"),function(){		                		
		                		closeLoading();
		                		drowGraph();
			                });
		                }else if(tab.contentEl=="skos_tab"){
		                	$('.autogrow').autogrow({minHeight:13,lineHeight:13,expandTolerance:1});
				        }
		                <%if(request.getAttribute("unlinked")!=null && ((BigInteger)request.getAttribute("unlinked")).intValue()>0){%>
			                else if(tab.contentEl=="unlinked_tab"){
			                	  openLoading();
			                	  if($("#unlinked_tab").data("url")==undefined){
			                		  $("#unlinked_tab").data("url","ajax/unlinked.html?idArchive=<%=request.getParameter("idArchive")%>");
						          }
		            			  $(".unlinked_tab").load($("#unlinked_tab").data("url"),function(){closeLoading();});
					        } 	
				        <%}%>   
                }
            }
            })
            ]
        });
    });
    var loadBar = true;
    function openLoading(){
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
    function closeLoading(){	
    	loadBar=false;
    	Ext.MessageBox.hide();
    }
    </script>
    <script>
		$(document).ready(function() {
			<%if(archives.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.HIERARCHIC){%>
				$(".tree").load("ajax/tree.html?idArchive=<%=archives.getIdArchive()%>",function(){});
			<%}else if(archives.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.THESAURUS){%>
				$(".tree").load("ajax/skos.html?idArchive=<%=archives.getIdArchive()%>",function(){});
			<%}%>
			$(".advSearch_tab").load("ajax/skos_adv_search.html?idArchive=<%=request.getParameter("idArchive")%>",function(){});
			<%if(request.getAttribute("unlinked")!=null && ((BigInteger)request.getAttribute("unlinked")).intValue()>0){%>
			//$(".unlinked_tab").load("ajax/unlinked.html?idArchive=<%=request.getParameter("idArchive")%>",function(){});
			<%}%>
			$(".change_theme").change(function(){
				$("#theme_color").attr("href",$(".change_theme option:selected").attr("id"));
				$("#theme_color_tree").attr("href","css/style_sky_blue.css");
				
			});
			$(document).ajaxError(function(e, xhr, settings, exception){
				   if(xhr.status == 601){
					   document.location.reload();
				   }else{
					 closeLoading();
					 Ext.MessageBox.alert('${messageStatus}', '${messageStatusError}');
				   }
			});
		});
		function reloadAllTree(){
			<%if(archives.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.HIERARCHIC){%>
				$(".tree").load("ajax/tree.html?idArchive=<%=archives.getIdArchive()%>",function(){})
			<%}else if(archives.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.THESAURUS){%>
				$(".tree").load("ajax/skos.html?idArchive=<%=archives.getIdArchive()%>",function(){})
			<%}%>
		}
	</script>
</head>
<body>
	<%if(newWindow){%>
    <div id="header" class="x-hide-display header">
    	<div class="header_logo"><%if(user.getImageLogo()!=null){%><img src="img/<%=user.getImageLogo()%>" alt="<%=user.getCompany()%>"/> <%}%></div>
        <div class="header_box">       
    	<!-- div style="float:right;display:none;">
	    	<select class="change_theme">
	    			<option id="css/resources/css/ext-all-notheme.css">standard</option>
	    			<option id="css/resources/css/xtheme-blue.css">sky blue</option>
	    			<option id="css/resources/css/xtheme-gray.css">gray</option>
	    			<option id="css/resources/css/xtheme-access.css">dark blue</option>
	    	</select>
    	</div-->    	
    	</div>
    	<div class="header_title"><%=archives.getLabel()%></div>
    </div>
    <%}%>
    <div id="tree" class="x-hide-display tree"></div>
    <div id="results" class="x-hide-display results"></div>
    <div style="width:100%;" id="search-panel"></div>
    <div id="relation_results" class="x-hide-display results"></div>
    <div style="width:100%;" id="relation_search-panel"></div>
    <div id="center" class="x-hide-display">
		    <%if(archives.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.THESAURUS){%>
		        <div id="skos_tab" class="x-hide-display skos_tab" style="padding:10px;"></div>
		        <div id="relations_tab" class="x-hide-display relations_tab" style="padding:10px;"></div>
		        <div id="visual_tab" class="x-hide-display visual_tab" style="padding:10px;"></div>
		    <%}else{%>
		    	<div id="short_tab" class="x-hide-display short_tab"></div>
		    <%}%>
		    <div id="xml_tab" class="x-hide-display xml_tab"></div>
		    <div id="advSearch_tab" class="x-hide-display advSearch_tab"></div>
		    <%if(request.getAttribute("unlinked")!=null && ((BigInteger)request.getAttribute("unlinked")).intValue()>0){%>
		    	<div id="unlinked_tab" class="x-hide-display unlinked_tab"></div>
		    <%}%>
    </div>
    <%if(newWindow){%>   
    <div id="footer" class="x-hide-display"></div>
     <%}%>
</body>
</html>
<%}%>