
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@page import="com.openDams.bean.Archives"%>
<%@page import="com.openDams.bean.ArchiveIdentity"%>
<%@page import="com.openDams.security.UserDetails"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="org.springframework.security.core.context.SecurityContext"%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="java.util.Collection"%>
<%@page import="org.springframework.security.core.GrantedAuthority"%>
<%@page import="com.openDams.security.RoleTester"%>
<%@page import="java.math.BigInteger"%>
<%@page import="com.openDams.bean.ArchiveType"%>
<%@page import="com.openDams.security.ArchiveRoleTester"%>
<%
	if (request.getAttribute("archives") != null) {
		Archives archives = (Archives) request.getAttribute("archives");
		UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
		//Collection<GrantedAuthority> authorities = user.getAuthorities();
		//Object[] authoritiesList = authorities.toArray();
		boolean editOn = ArchiveRoleTester.testEditing(user.getId(), archives.getIdArchive().intValue());
		boolean editXML = ArchiveRoleTester.testEditXML(user.getId(), archives.getIdArchive().intValue());
		boolean newWindow = false;
		if (request.getParameter("win") != null) {
			newWindow = true;
		}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="java.util.List"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>openDams</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" href="img/sqlxdicon.png" type="image/png" />
<link rel="shortcut icon" href="img/sqlxdicon.ico" type="image/png" />
<!-- link href="css/style.css" id="theme_color_tree" rel="stylesheet" type="text/css"  /-->
<link href="css/StyleSheet.css" rel="stylesheet" type="text/css" />
<link href="css/confirm.css" rel="stylesheet" type="text/css" />
<link href="css/jquery.contextMenu.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="css/resources/css/ext-all-notheme.css" />
<link rel="stylesheet" type="text/css" id="theme_color" href="css/resources/css/xtheme-blue.css" />
<link rel="stylesheet" href="js/sh/SyntaxHighlighter.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="css/style.css" />
<link rel="stylesheet" type="text/css" href="css/ext-js-4.0.7/desktop/message-manager/message-manager.css" />
<link rel="stylesheet" type="text/css" id="theme_color" href="js/ext-js/ux/fileuploadfield/css/fileuploadfield.css" />
<link type="text/css" href="css/redmond/jquery-ui-1.8.6.custom.css" rel="stylesheet" />
<link type="text/css" href="css/jquery.ui.daterangepicker.css" rel="stylesheet" />
<link href="css/style_sky_blue.css" id="theme_color_tree" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.6.custom.min.js"></script>

<script src="js/tiny_mce/jquery.tinymce.js" type="text/javascript"></script>
<script src="js/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script src="js/tiny_mce/langs/it.js" type="text/javascript"></script>
<script src="js/tiny_mce/themes/advanced/langs/it.js" type="text/javascript"></script>
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
<script type="text/javascript" src="js/ext-js/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="js/ext-js/ext-all.js"></script>
<script type="text/javascript" src="js/ext-js/ux/SearchField.js"></script>
<script type="text/javascript" src="js/ext-js/ux/RowExpander.js"></script>
<script type="text/javascript" src="js/ext-js/ux/Printer.js"></script>
<script type="text/javascript" src="js/ext-js/ux/fileuploadfield/FileUploadField.js"></script>
<!--script src="js/codemirror.js" type="text/javascript"></script-->
<script type="text/javascript" src="js/jpath.js"></script>
<script type="text/javascript" src="js/application.js"></script>
<link rel="stylesheet" href="js/codemirror/lib/codemirror.css" />
<link rel="stylesheet" href="js/codemirror/mode/xml/xml.css" />
<script src="js/codemirror/lib/codemirror.js"></script>
<script src="js/codemirror/mode/xml/xml.js"></script>
<script src="js/sockjs/sockjs-0.3.4.min.js"></script>
<script type="text/javascript"> 

Ext.ux.IFrameComponent = Ext.extend(Ext.BoxComponent, {
    onRender : function(ct, position){
          this.el = ct.createChild({tag: 'iframe', id: 'iframe-'+this.id, frameBorder: 0, src: this.url});
    } 
});


var globalOpt={}; 
globalOpt.editOnStart = <%=request.getParameter("openEdit")%>;
globalOpt.document = {};
globalOpt.document.id = '<%=request.getParameter("idRecord")%>'; 
globalOpt.document.archive = '<%=request.getParameter("idArchive")%>'; 
layout = {};
globalOpt.idArchivio = '<%=request.getParameter("idArchive")%>';
globalOpt.descrArchivio = '<%=JsSolver.escapeSingleApex(archives.getLabel())%>';
globalOpt.archiveHistory = [];  
globalOpt.archiveHistory.index = 0;
<%if (editOn) {%>globalOpt.editOn=true;<%}%>
<%if (editXML) {%>globalOpt.editUnlock = 'yes';<%}%>
//globalOpt.idRecord = '<%=request.getParameter("idRecord")%>';
	loadScripts({base:"jquery",scripts:['jquery.autogrow','jquery.form']}); 
	loadScripts({base:"application",scripts:['utils','ds','loading','search','insert','jsonInjector','tree','print','layouts','custom']});
</script>
<script src="js/pure_packed.js" type="text/javascript"></script>
<link href="css/kube.min.css"  rel="stylesheet" type="text/css" />
<link href="css/master.css"   rel="stylesheet" type="text/css" />

<%@include file="../locale.jsp"%>
<%
	String id_record = "";
		if (request.getParameter("idRecord") != null) {
			id_record = "&idRecord=" + request.getParameter("idRecord");
		}
%>
<script type="text/javascript">
var viewport = null;
    Ext.onReady(function(){
 	   /*Ext.state.Manager.setProvider(
 			  new Ext.state.CookieProvider({
 			   expires: new Date(new Date().getTime()+(1000*60*60*24*365)) //1 year from now
 		}));  	
		*/      
	 	var regionWestAzioni =  {
    	       region:'north',
    	       height: 100, 
               id:'azioni',
               border: false, 
               autoScroll: true,
               split:true,
               layout : 'fit',
     		   tbar:new Ext.Toolbar({ 
     		  		items : ['strumenti: ',
     		  		      <%if (editOn) {%>		
	     		    	   new Ext.Button({
	     				 		text:'inserisci un documento', 
	     				        handler: function() {
	     				        	addRecord(globalOpt.idArchivio,'inserisci: <%=archives.getDescription()%>','<%=JsSolver.escapeSingleApex(archives.getLabel())%>','gridDocumentiId');
	     			
	     				        }
	     				  }),
     				  	  <%}%>
     				  '|',
     				   new Ext.Button({ 
     				 		text:'scrivi nota',
     				        handler: function() {
    				        	writeArchiveNote();
     						}
     				})]
     		    }),
     		    items:[archiveNotesGrid]
         }; 
 	   
 	 	var regionWestDocumenti={ 
                id:'documents_accordion',
                title: 'ultimi documenti inseriti',
                border: false,
                autoScroll: true, 
                iconCls: 'navigation_img',
                layout:'fit', 
                items:[gridDocumenti],
                tbar: new Ext.Toolbar({
              	   items : ['->',generateFilters('gridDocumentiId','documents_accordion')]
                    }),  
 	 	 		  bbar: toolbarDocumenti
 	 		};
	 	var regionWestRicerca= { 
            	id:'results_accordion',
            	layout:'fit',
                title: 'cerca in: <%=archives.getLabel()%>', 	 
                border: false, 
                fullscreen:true,
                autoScroll: true,
                iconCls: 'results_img',
                items:[gridEsitoRicerca],
                 tbar: [
                    'termine: ', 
                    new Ext.ux.form.SearchField({
					    store: dsRegionWestRicerca, 
					    id:'query_field', 
					    width:180, 
					    emptyText:'inserisci termine',
			    	    listeners: { 
		      			  'afterrender': function(elemento){  
 			      			 $("#"+$(Ext.getDom(elemento)).attr("id")).parent('div').attr("style","");
		      			  } 
		         		}
				      })
                  , '->',generateFilters('esitoRicercaId')],
                  bbar: new Ext.PagingToolbar({
                  id:'paging_bar',
                  store: dsRegionWestRicerca, 
                  pageSize: 20,
                  displayInfo: true,
                  displayMsg: '${skosWestpanelSearchFooter}',
                  emptyMsg: "${skosWestpanelSearchNoresults}"
                  })
            };
	 		 		
	 	var schedaBreveItem = new Ext.Panel({
              // contentEl: 'short_tab', 
               id:'short_tab_tab',
               closable: false,
               autoScroll: true,
               margins:'0 0 0 0',
               region:'center',
               ddGroup : 'gridDDGroup',
               listeners: { 
      			  'afterrender': function(){
          			  elemento = this;		
      				if(!globalOpt.document['scheda'] || !globalOpt.document['scheda'].output){
      					//globalOpt.document = {};
      					globalOpt.document['scheda'] = {};
      					if(globalOpt.editOnStart){
							openRecord(globalOpt.document.id,'edit');
							globalOpt.editOnStart = null;
           				}else{
           					openRecord(globalOpt.document.id,'scheda');
          				}
      					
      					var postPanelDropTarget = generateMyDropTarget(elemento);
      					/*if(typeof(generateMyDropTargetMulti)!='undefined'){
      						 generateMyDropTargetMulti(elemento);
						}*/
      					
      				} 
      			  }
         		}, 
         		html:'<div id="inj_scheda"><div class="loading-indicator">caricamento in corso...</div></div>',
                tbar: new Ext.Toolbar({
             	   items : [
							new Ext.Button({
								text : '',
								iconCls: 'back',
								tooltip: 'Documento precedente',
							    tooltipType: 'title',
							    disabled:true,
							    id:'history_back_button_'+globalOpt.idArchivio,
								handler : function(btn) {backForward('back','scheda');}
						    }),'-','cronologia','-',
						    new Ext.Button({
								text : '',
								iconCls: 'forward',
								tooltip: 'Documento successivo',
							    tooltipType: 'title',
							    disabled:true,
							    id:'history_forward_button_'+globalOpt.idArchivio,
								handler : function(btn) {backForward('forward','scheda');}
						    }),
						    new Ext.Button({
								text : '',
								iconCls: 'delete_history',
								tooltip: 'Cancella cronologia',
							    tooltipType: 'title',
							    disabled:true,
							    id:'history_delete_button_'+globalOpt.idArchivio,
								handler : function(btn) {deleteHistory();}
						    }),
                      	   /*
             	            new Ext.Button({
						        text    : 'inserisci in una ricerca',
						        handler : function(btn) {
						        	//alert($('#short_tab').find("h1").html());
						        	Ext.getCmp ('strumenti_accordion').expand(); 
						        	if($("div[rel='"+$('#idArchive').val()+':'+$('#idRecord').val()+"']").length==0){
						        		description = $('.scheda').find("p.descr").html();
						        		if(description!=null && description!='' && description.length>75){
											description = description.substring(0,72)+"...";
										}else{
											description = "";
										}
						        		$('#nuovaRicerca').append('<div class="link_document" rel="'+$('#idArchive').val()+':'+$('#idRecord').val()+'"><input type="hidden" name="recordToSave" value="'+$('#idArchive').val()+':'+$('#idRecord').val()+'"><a onclick="openRecordInWindow('+$('#idRecord').val()+',null,'+$('#idArchive').val()+');" href="#no">'+$('.scheda').find("h1").html()+'<span>'+description+'</span></a></div>');
						        	}
						        }
							    }) , '->',*/
							    '->',new Ext.Button({
        					        text    : 'ricarica <img border="0" title="ricarica" alt="ricarica" style="cursor:hand;" src="img/refresh2.gif">',
        					        handler : function(btn) {
        					         reloadRecord('scheda');
        					        }
        					    })
							    
// 							    ,'-',new Ext.Button({
//         					        text    : 'segnala documento',
//         					        handler : function(btn) {
<%--         					         sendRecord('<%=archives.getIdArchive()%>','<%=archives.getLabel()%>'); --%>
//         					        }
//         					    })
							    
								
   					    
        					    ,'-',new Ext.Button({
        					        text    : 'stampa',
        					        handler : function(btn) {
        					    		Ext.ux.Printer.PanelRenderer = Ext.extend(Ext.ux.Printer.BaseRenderer, {
	                    	    		 generateBody: function(panel) {
	                    	    		   var text = panel.body.dom.innerHTML.replace(/\\/g,'');
	                    	    		   return String.format("<div class='x-panel-print'>{0}</div>",text);
	                    	    		 }
	                    	    		});
	    								Ext.ux.Printer.registerRenderer("panel", Ext.ux.Printer.PanelRenderer);
	    								Ext.ux.Printer.print(Ext.getCmp('short_tab_tab'));
        					        }
        					    }) 							    
								<%/*if (false && editXML) {%>
        					    ,'-',new Ext.Button({
            					    disabled:true,
            					    id:'singleRecordPublishButton',
        					        text    : 'pubblica',
        					        menu: [
               					          <%if(request.getAttribute("endPointList")!=null){
               					        	List<String> endPointList = (List<String>)  request.getAttribute("endPointList");
               					        	for (int i = 0; i < endPointList.size(); i++) {%>
               					        	new Ext.Action({
		               					             text: 'pubblica su <strong><%=endPointList.get(i)%></strong>',
		               					             id:'<%=endPointList.get(i)%>',
		               					             disabled:true,
		               					             handler: function(btn){
		               					            	publishRecord(btn);
		               					             },
		               					             iconCls: 'not_publisched'
		               					         })
               					 			<%
               					 			  if(i<endPointList.size()-1){%>,<%}
               					        	}
               					          }%> 
										  ]        					      
        					    }) 
        					    <%}*/%>
                         ]
                     })
           	});
           	
           						<%if (editOn) {%>
        					  var endpoints = [];
               					          <%if(request.getAttribute("endPointList")!=null){
               					        	List<String> endPointList = (List<String>)  request.getAttribute("endPointList");
               					        	for (int i = 0; i < endPointList.size(); i++) {%>
               					      endpoints.push({'name':'<%=endPointList.get(i)%>','id':'<%=endPointList.get(i)%>',})
               					        <%}
               					        }%>
               	 
        					    globalOpt.endpoints =endpoints; 
        					    <%}%>
           	
           	 
	 	   var schedaBreveNotes =new Ext.Panel({
		 		id:'schedaBreveNotes',
	            closable: false,
	            /*title:'Appunti per il documento selezionato',*/
	            collapsible:false,
	            region:'south',
	            layout:'fit',
	            height: 75,
	            split:false,
                minSize: 75,
                maxSize: 75,
                margins: '0 0 0 0',
	            contentEl:'postit-panel',
	            tbar:new Ext.Toolbar({ 
     		  		items : ['<strong>Appunti per il documento selezionato:</strong>','->',     		  		  
     		    	   	new Ext.Button({
     				 		text:'aggiungi un postit personale', 
     				        handler: function() {
     		    		  		addRecordPostIt('my');
     				  		}
     				  	}),'|',
     				   	new Ext.Button({ 
     				 		text:'aggiungi un postit dipartimentale',
     				        handler: function() {
     					  		addRecordPostIt('dep');
  						}
     				})]
     		    })
			});	 
		 	var schedaBreveContainer = new Ext.Panel({
		 		id:'schedaBreveContainer',
	            closable: false,	            
	            layout:'border',
	            title: 'Scheda',
	            listeners: { 
	       			  'activate': function(){	
	       				Ext.getCmp('short_tab_tab').fireEvent("afterrender");
	       			  }
 			    },
	            items:[schedaBreveItem,schedaBreveNotes]
			});	 
	 	var visualizzaXmlItem={
                //contentEl: 'xml_tab',
                id:'xml_tab_tab',
                title: '${centerXmlTab}',
                closable: false, 
                tbar: stampaBtt('xml_tab_tab'),
                <%if (editXML) {%>
                bbar: new Ext.Toolbar({
            		items : [ '->', new Ext.Button({
            			text : 'modifica XML',
            			handler : function(btn) {
		            			 Ext.getCmp('xml_tab_tab').load({
		            				  url:'ajax/documentalRecord.html?idArchive='+globalOpt.idArchivio+'&action=edit_xml_tab&idRecord='+globalOpt.document.id,
		       		          		  discardUrl: false,
		       		          		  nocache: true,
		       		          		  text: 'caricamento in corso...',
		       		          		  timeout: 30,
		       		          		  scripts: true
							   	 });
            			}
            		}) ]
            	}),
            	<%}%>
                listeners: {
        			  'activate': function(a){
        				  	a.getUpdater().update({
        				  	  url:'ajax/documentalRecord.html?idArchive='+globalOpt.idArchivio+'&action=xml_tab&idRecord='+globalOpt.document.id,
       		          		  discardUrl: false,
       		          		  nocache: true,
       		          		  text: 'caricamento in corso...',
       		          		  timeout: 30,
       		          		  callback:function(){loadScripts({base:"sh",scripts:['shCore','shBrushXml']});highLightXML();},
       		          		  scripts: false
        				  	}); 
	          			  }
	        		  }, 
                autoScroll: true,
                html:'<div id="destXML"><div class="loading-indicator">caricamento in corso...</div></div>'
            	};
	 	
	 	var modificaItem={
                //contentEl: 'edit_tab',
                id:'edit_tab_tab',
                title: 'Modifica',
                closable: false, 
                listeners: {
                   'activate': function(tabPanel, tab){ 
                	loadDocument({what:'edit',callback:mainEditCallback});
                   }},
       		 	html:'<div id="inj_edit"><div class="loading-indicator">caricamento in corso...</div></div>',
                autoScroll: true
                ,
                bbar:new Ext.Toolbar({
                    id:'edit_tab_tab_bbar', 
      		    	items : [
      	                   {
      	                       text:'Salva e continua',
      	                       id:'buttonSaveDocumentalRapid',
      	                       handler: function(){
      	                       		saveDocument('editForm',function(){top.Ext.ods.msg('documento salvato con successo');},function(){} );	
      	                       		$('#editForm').trigger('submit');
      	                   	}
      	                   },'|',{
      	                       text:'Salva e chiudi',
      	                       id:'buttonSaveDocumental',
      	                       handler: function(){
      	                       		saveDocument('editForm',function(){top.Ext.ods.msg('documento salvato con successo');openRecord(globalOpt.document.id);closeLoading();});	
      	                       		$('#editForm').trigger('submit');
      	                   	}
      	                   },'->',{
      	                       text: '${buttonReset}',
      	                       handler: function(){
      	                   	 	document.editForm.reset();
      	                       }
      	                   },'|',{
      	                	 <%if (!editXML) {%>id:'deleteButton',<%}%>
      	                       text: '${buttonDelete}',
      	                       <%if (!editXML) {%>
      	                       disabled:true,
      	                       <%}%>
      	                       handler: function(){     
      		                        		deleteRecord();
      	                       }
      	                   }	 
      	               ]
      			}) 
             };
	 	/*var ricercaItem={
                //contentEl: 'advSearch_tab',
                id: 'advSearch_tab_tab',
                title: '${centerAdvSearchTab}',
                closable: false,
                autoScroll: true,       			
                autoLoad:{
         		  url:'ajax/documental_adv_search.html?idArchive='+globalOpt.idArchivio,
         		  discardUrl: false,
         		  nocache: true,
         		  text: 'caricamento in corso...',
         		  timeout: 30,
         		  scripts: true
     		  	}
            };*/
	 	
<%if (request.getAttribute("archiveList") != null) {
					List<Object> archivesTH = (List) request.getAttribute("archiveList");
					Object[] archiveList = archivesTH.toArray();
					for (int i = 0; i < archiveList.length; i++) {
						Archives archive = (Archives) archiveList[i];%>
		   var treePanel_archive_<%=archive.getIdArchive()%> ={
	                id:'relations_tree_panel_archive_<%=archive.getIdArchive()%>',
	                title:'albero dei concetti',
	                closable: false, 
	                border:false,
	     		  	tbar:new Ext.Toolbar({ 
	      		    	items : [ '->','visualizza concetti: ', new Ext.Button({ 
	  				 		text:'tutti', 
	  				        handler: function() {
	  				        	Ext.getCmp('relations_tree_panel_archive_<%=archive.getIdArchive()%>').getUpdater().update({ 
	  				        		/*TODO: diego generalizzare startFrom dai dati dell'utente */
	   	     				  	    url:'ajax/skosDocumental.html?idArchive=<%=archive.getIdArchive()%>',
	  				        		callback:function(){treeCallback('archive_<%=archive.getIdArchive()%>');}
	  				        	});
	  					}
	  					}),'|',
	      				  new Ext.Button({ 
	      				 		text:'mio dipartimento', 
	      				        handler: function() {
	      				        	Ext.getCmp('relations_tree_panel_archive_<%=archive.getIdArchive()%>').getUpdater().update({ 
	      				        		url:'ajax/skosDocumental.html?idArchive=<%=archive.getIdArchive()%>&isolate='+getInSchemeId(<%=archive.getIdArchive()%>),
	      				        		callback:function(){treeCallback('archive_<%=archive.getIdArchive()%>');}
	      				        	});
	      					}
	      				})]
	      			}),
	                listeners: {
	                	'render': function(a){ 
	     					Ext.getCmp('relations_tab_tab').addListener("activate", 
	         					function(){
	     						if(!globalOpt.relazioni_<%=archive.getIdArchive()%> || !globalOpt.relazioni_<%=archive.getIdArchive()%>.rendered){
	     	      					globalOpt.relazioni_<%=archive.getIdArchive()%> = {};
	     	      					globalOpt.relazioni_<%=archive.getIdArchive()%>.rendered = true;
	     							a.getUpdater().update({
	     								/*TODO: diego generalizzare startFrom dai dati dell'utente */
	     	     				  	  url:'ajax/skosDocumental.html?idArchive=<%=archive.getIdArchive()%>&isolate='+getInSchemeId(<%=archive.getIdArchive()%>),
	   	    		          		  discardUrl: false,
	   	    		          		  nocache: true,
	   	    		          		  text: 'caricamento in corso...',
	   	    		          		  timeout: 30,
	   	    		          		  callback:function(){treeCallback('archive_<%=archive.getIdArchive()%>');},
	   	    		          		  scripts: false
	     	     				  	}); 
	     						}
	         					}
	    					
	     					);
	          			  } ,
	          			 'activate': function(a){ 
	          				if(!globalOpt.relazioni_<%=archive.getIdArchive()%> || !globalOpt.relazioni_<%=archive.getIdArchive()%>.rendered){
     	      					globalOpt.relazioni_<%=archive.getIdArchive()%> = {};
     	      					globalOpt.relazioni_<%=archive.getIdArchive()%>.rendered = true;
		          				a.getUpdater().update({
	 	     				  	  url:'ajax/skosDocumental.html?idArchive=<%=archive.getIdArchive()%>&isolate='+getInSchemeId(<%=archive.getIdArchive()%>),
		    		          		  discardUrl: false,
		    		          		  nocache: true,
		    		          		  text: 'caricamento in corso...',
		    		          		  timeout: 30,
		    		          		  callback:function(){treeCallback('archive_<%=archive.getIdArchive()%>');},
		    		          		  scripts: false
	 	     				  	});
	          				}
		 	          	  }
	                   },
	                autoScroll: true 
		 	};
		   generateGridConcetti('<%=archive.getIdArchive()%>');
		   var searchSkosPanel_archive_<%=archive.getIdArchive()%> = {
	                id:'relations_skos_search_panel_<%=archive.getIdArchive()%>',
	                closable: false, 
	                border:false,
	                title:'cerca un concetto',
	       		 	items:globalOpt.skosArchives['gridConcetti_<%=archive.getIdArchive()%>'],
	                autoScroll: true, 
	                layout:'fit',
	                tbar: [
	                       'concetto: ',  
	                        new Ext.ux.form.SearchField({
	  					    store: globalOpt.skosArchives['storeConcetti_<%=archive.getIdArchive()%>'],
	  					    id:'query_concetto_<%=archive.getIdArchive()%>',  					    
	  					    mode: 'remote',		
	  					    width:200,
	  					    selectOnFocus:false,
	  					    emptyText:'inserisci concetto',
				    	    listeners: { 
				      			  'afterrender': function(elemento){  
					      			 $("#"+$(Ext.getDom(elemento)).attr("id")).parent('div').attr("style","");
				      			  } 
			      			  }
	  			       		}),'->',{
			                           xtype: 'checkbox',
			                           id:'checkboxMyDip_<%=archive.getIdArchive()%>',
			                           name: 'myDip',
			                           boxLabel: 'mio dipartimento',
			                           checked: true
	                   				}
	                     ],
	                     bbar: new Ext.PagingToolbar({
	                     	id:'paging_bar_concetto_<%=archive.getIdArchive()%>',
	                     	store: globalOpt.skosArchives['storeConcetti_<%=archive.getIdArchive()%>'],
	                      pageSize: 20,
	                      displayInfo: true,
	                      displayMsg: '${skosWestpanelSearchFooter}',
	                      emptyMsg: "${skosWestpanelSearchNoresults}"
	                     })
		 	};
		   var relationsPanelAccordion_archive_<%=archive.getIdArchive()%> ={
		 			title:'<%=archive.getLabel()%>',
	                id:'relations_tab_accordion_archive_<%=archive.getIdArchive()%>',
	                closable: false,
	                split: true,
	                width: 400,
	                minSize: 175,
	                maxSize: 600,
	                collapsible: false,
	                autoScroll: true,
	                margins: '0 0 0 0',
	                layout: 'accordion',
	                layoutConfig: {
	                    collapseFirst : false,
	                    animate: false
	                },
	                autoScroll: true,
	                items:[searchSkosPanel_archive_<%=archive.getIdArchive()%>,treePanel_archive_<%=archive.getIdArchive()%>]
		 	}; 	
		   <%}
				}%>	 	
	 	var relationsPanelTab =new Ext.TabPanel({
	 		id:'relations_tab_accordion',
            closable: false, 
            region:'west',
            split: true,
            width: 400,
            minSize: 175,
            maxSize: 600,
            collapsible: true,
            autoScroll: true,
            margins: '0 0 0 0',
		    activeTab: 0,					
		    items:[ 
					<%if (request.getAttribute("archiveList") != null) {
					List<Object> archivesTH = (List) request.getAttribute("archiveList");
					Object[] archiveList = archivesTH.toArray();
					for (int i = 0; i < archiveList.length; i++) {
						Archives archive = (Archives) archiveList[i];%>
							   relationsPanelAccordion_archive_<%=archive.getIdArchive()%><%if (i < archiveList.length - 1) {%>,<%}%>
								<%}
				}%>
				    ]
		});
	 	
	 	var relationsList ={
                id:'relations_associate', 
                closable: false, 
                region:'center',
                border:false,
                listeners: {
     			'render': function(a){ 
     					Ext.getCmp('relations_tab_tab').addListener("activate", 
	     					function(){     	      			
     							a.getUpdater().update({
     	     				  	  url:'ajax/documentalRecord.html?idArchive='+globalOpt.idArchivio+'&action=relations_tab&idRecord='+globalOpt.document.id,
     	    		          		  discardUrl: false,
     	    		          		  nocache: true,
     	    		          		  text: 'caricamento in corso...',
     	    		          		  timeout: 30,
     	    		          		  scripts: false
     	     				  	});
     	      				 
	     					}
    					
     					);
          			  }
        		  }, 
              autoScroll: true
	 	} 	
	 	
	 	var relazioniItem={
                //contentEl: 'relations_tab',
                id:'relations_tab_tab',
                title: 'Relazioni con Griglia',
                closable: false,
                autoScroll: true,
                layout:'border',
                items:[relationsPanelTab,relationsList]                  
            	};
	 	 	 	
	 	var headerItem =  {
                region: 'north',
                //contentEl: 'header',
                split: false,
                height: 50,
                minSize: 100,
                maxSize: 200,
                collapsible: false,
                margins: '0 0 0 0'
            };
	 		 
	 	var footerItem={
                region: 'south',
                //contentEl: 'footer',
                split: false,
                minSize: 100,
                maxSize: 200,
                collapsible: false, 
                margins: '0 0 0 0'
            };


 
	viewport = new Ext.Viewport({
         layout: 'border',
         items: initLayout(),
         listeners:{
	         	'afterrender':function(){
     	        top.checkArchiveStatus();
				top.Message['Bus'+globalOpt.idArchivio].on('offline', function() {
					//this.disable();
					killSystemMSG();
					systemMSG('Attenzione Archivio in manutenzione');
				}, this);
				top.Message['Bus'+globalOpt.idArchivio].on('online', function() {
					killSystemMSG();
					//this.enable();
				}, this);
	         }
        }
     });
 



		var currentId = 0;
        function initLayout(){
	    	return [<%if (newWindow) {%>headerItem , footerItem,<%}%> 
	    	{
		 		 region: 'west',
	               id: 'west-panel',
	               /*title: '<span class="tree_top_title">${skosWestpanelTitle}</span>',*/
	               split: true,
	               width: 400,
	               minSize: 175,
	               maxSize: 600,
	               collapsible: false,
	               autoScroll: true, 
	               layout:'border',
	               margins:'0 0 0 0',
	               
	               items:[regionWestAzioni,
					new Ext.Panel({   
						region: 'center',
				        layout: 'accordion', 
				        id:'leftColumnAccordion',
		                layoutConfig: {
		                    collapseFirst : false,
		                    animate: false
		                },
		                items:initLeftColumn(),
		                listeners: { 
			      			  'afterrender': function(elemento){  
			      				initSpecificLayout();
			      			  } 
		                }
		                
	               })
	               ]
		 	}
            ,new Ext.TabPanel({
                region: 'center',
                margins:'0 0 0 0', 
                id:'documental_tab_panel',
                //contentEl: 'center',
       			//  deferredRender: false,
                activeTab: 0,
                items: initCenterColumn(),
                listeners: { 
	      			  'tabchange': function( panel, newCard){  
	      				if(newCard.id=='edit_tab_tab' && globalOpt.document.id!=''){
				 			publish('channel_archive_<%=request.getParameter("idArchive")%>',globalOpt.document.id,'askBlock','<%=session.getId()%>','<%=user.getName()+" "+user.getLastname()%>');
				 			currentId = globalOpt.document.id;
				 		} else if(newCard.id=='schedaBreveContainer'){
				 			if(Ext.getCmp('edit_tab_tab')){
			 					publish('channel_archive_<%=request.getParameter("idArchive")%>',globalOpt.document.id,'done','<%=session.getId()%>','<%=user.getName()+" "+user.getLastname()%>');
			 					currentId = globalOpt.document.id;
				 			}
				 		}
	      			  },
	      			  'tabLoaded':function(){
	      				  //console.log('tabLoaded!!!!!!!!!!!!!!!!!!!!!!!!!');
						if(currentId==0)
							currentId = globalOpt.document.id;
						if(currentId!=globalOpt.document.id){
							publish('channel_archive_<%=request.getParameter("idArchive")%>',currentId,'done','<%=session.getId()%>','<%=user.getName()+" "+user.getLastname()%>');
							currentId = globalOpt.document.id;
						}
						publish('channel_archive_<%=request.getParameter("idArchive")%>',globalOpt.document.id,'isBlocked','<%=session.getId()%>','<%=user.getName()+" "+user.getLastname()%>');
					  }
              }
            })
            ];
	    }
	    function initCenterColumn(){
	    	return [ schedaBreveContainer ,  <%if (editOn) {%>modificaItem ,<%}%> visualizzaXmlItem <%if (editOn) {%>, relazioniItem<%}%>];//,ricercaItem
	    }
	    function initLeftColumn(){
	    		return [regionWestDocumenti,regionWestRicerca,layout.regionWestMulti];
	    }

	   // connect("applicationbus/Bus"+globalOpt.idArchivio);  
		
 
    });
    
	  var eventBus_archive_<%=request.getParameter("idArchive")%> = null;
	    
	  function publish(address,idRecord,type,jSessionId,user) {
		    if (eventBus_archive_<%=request.getParameter("idArchive")%>) {
		      var json = {idRecord: idRecord,type:type,jSessionId:jSessionId,user:user};
		      eventBus_archive_<%=request.getParameter("idArchive")%>.publish(address, json);
		    }
		}
	  
	  function subscribe(address) {
	    if (eventBus_archive_<%=request.getParameter("idArchive")%>) {
	    	eventBus_archive_<%=request.getParameter("idArchive")%>.registerHandler(address, function(msg, replyTo) {
	    		//console.log("MESSAGGIO Address:" + address + " Message:" + msg.idRecord +" Type:"+msg.type+' JSessionId:'+msg.jSessionId);
	    		if(msg.type=='askBlock' || msg.type=='isBlocked'){
	    			if(msg.jSessionId != '<%=session.getId()%>'){
	    				//console.log("Ricevuto Address:" + address + " Message:" + msg.idRecord +" Type:"+msg.type);
	    				if( $('input[name=idRecord]',$("#inj_edit")).val() == msg.idRecord){
		    				if(!Ext.getCmp('edit_tab_tab').isDisabled()){
		    					//console.log("RISPONDO Address:" + address + " Message:" + msg.idRecord +" Type:"+msg.type);
		    					publish('channel_archive_<%=request.getParameter("idArchive")%>',msg.idRecord,'answer','<%=session.getId()%>','<%=user.getName()+" "+user.getLastname()%>');
		    				}
	    				}else if(msg.type=='askBlock' && msg.idRecord==globalOpt.document.id){
	    					//console.log('lo devo bloccareeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee');
	    					if($('#blocking-message').length==0)
	    						$('#inj_scheda').prepend('<div id="blocking-message" style="border:1px solid red;color:red;text-align:center;padding:20px;">Attenzione!Documento bloccato dall\'utente '+msg.user+'</div>');
		    				//Ext.getCmp('edit_tab_tab').setDisabled(true);
		    				Ext.getCmp('documental_tab_panel').items.each(function(item){
		    					if(item.id!='schedaBreveContainer')
		    				    	item.setDisabled(true);
		    				}); 
		    				Ext.getCmp('documental_tab_panel').setActiveTab(Ext.getCmp('schedaBreveContainer'));
	    				}
	    			}else{
	    				//console.log("Mandato Address:" + address + " Message:" + msg.idRecord +" Type:"+msg.type);
	    			}
	    		}else if(msg.type=='answer'){
	    			 if(msg.jSessionId != '<%=session.getId()%>'){
		    			//console.log("Ricevuta risposta di blocco Address:" + address + " Message:" + msg.idRecord +" Type:"+msg.type+' inj_edit:'+ $('input[name=idRecord]',$("#inj_edit")).val()+' globalOpt.document.id:'+globalOpt.document.id);
		    			if( $('input[name=idRecord]',$("#inj_edit")).val() == msg.idRecord || msg.idRecord==globalOpt.document.id){
		    				if($('#blocking-message').length==0)
		    					$('#inj_scheda').prepend('<div id="blocking-message" style="border:1px solid red;color:red;text-align:center;padding:20px;">Attenzione!Documento bloccato dall\'utente '+msg.user+'</div>');
		    				//Ext.getCmp('edit_tab_tab').setDisabled(true);
		    				Ext.getCmp('documental_tab_panel').items.each(function(item){
		    					if(item.id!='schedaBreveContainer')
		    				    	item.setDisabled(true);
		    				});
		    				Ext.getCmp('documental_tab_panel').setActiveTab(Ext.getCmp('schedaBreveContainer'));
		    			}
		    		}
	    		}else if(msg.type=='done' ){ 
	    				//console.log("Ricevuta risposta di sblocco Address:" + address + " Message:" + msg.idRecord +" Type:"+msg.type);
		    			if( $('input[name=idRecord]',$("#inj_edit")).val() == msg.idRecord || msg.idRecord==globalOpt.document.id){
		    				//console.log("Sbloccato Address:" + address + " Message:" + msg.idRecord +" Type:"+msg.type);
		    				//Ext.getCmp('edit_tab_tab').setDisabled(false);
		    				Ext.getCmp('documental_tab_panel').items.each(function(item){
		    					if(item.id!='schedaBreveContainer')
		    				    	item.setDisabled(false);
		    				});
		    				$('#blocking-message').remove();
		    				Ext.getCmp('edit_tab_tab').update('<div id="inj_edit"><div class="loading-indicator">caricamento in corso...</div></div>');
		    				//loadDocument({what:'update',callback:mainEditCallback,idArchivio:globalOpt.document.archive});
		    				if(msg.jSessionId != '<%=session.getId()%>'){
		    					reloadRecord('scheda');
		    				}
		    			}
	    		}
	      });
	      //console.log("Address:" + address);
	    }
	  }

	  function closeConn() {
	    if (eventBus_archive_<%=request.getParameter("idArchive")%>) {
	    	eventBus_archive_<%=request.getParameter("idArchive")%>.close();
	    }
	  }

 

	</script>
</head>
<body>
<div id="search-panel"></div>
<div id="postit-panel">
<div id="postit_container" style="float: left;"></div>
<div id="myDipPanel"></div>
<!--div style="float: right; margin-right: 5px;">
			<a href="#x" onclick="addRecordPostIt('dep');"><img src="img/notes/dep_postit.gif" border="0" title="aggiungi un postit dipartimentale" alt="aggiungi un postit dipartimentale" /> </a>
		</div>
		<div style="float: right; margin-right: 5px;">
			<a href="#x" onclick="addRecordPostIt('my');"><img src="img/notes/my_postit.gif" border="0" title="aggiungi un postit personale" alt="aggiungi un postit personale" /> </a>
		</div--></div>
	<div id="fathers_finder"></div>
</body>
</html>
<%
	}
%>