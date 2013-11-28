<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%>
<%@page import="com.openDams.bean.ArchiveIdentity"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.openDams.search.configuration.Element"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@include file="../locale.jsp"%>
<style type="text/css">
.x-form-field-wrap .x-form-voc-trigger {
    background:transparent url(img/voc-blue.png) no-repeat 0 0 ;
    width:20px;
    cursor:pointer; 
}  
.voc-icon { background-image: url(img/voc-red.png) 0 6px no-repeat !important; }
</style>
<script type="text/javascript" src="js/application.js"></script>
<script>
var filter = true;
var queryTH = null;
var fp = null;
var checkGroupVoc = null;
var vocPanel = null;
loadScripts({base:"application",scripts:['ds','jsonInjector','utils','voc']});
<%if(request.getAttribute("archiveList")!=null){%>
	<%List<Object> archives = (List)request.getAttribute("archiveList");
	for(int i=0;i<archives.size();i++){
		   Archives archive = (Archives)archives.get(i);
		   %>
		   var dsSearch<%=archive.getIdArchive()%>;
	<%}
}
%>

Ext.onReady(function(){
	<%if(request.getAttribute("archiveList")!=null){%>
		<%List<Object> archives = (List)request.getAttribute("archiveList");
		for(int i=0;i<archives.size();i++){
			   Archives archive = (Archives)archives.get(i);
			   %>
			   dsSearch<%=archive.getIdArchive()%> = new Ext.data.JsonStore({
					url : 'desktop_adv_search.html',
					root : 'data',
					idProperty : 'id',
					totalProperty : 'totalCount',
					baseParams : {
						id_archive : <%=archive.getIdArchive()%>,
						action : 'search',
						limit : 15,
						query:'',
						date_range_query:'',
						order_by:'',
						sort_type:'',
						sort : 'DATE',
						idRecord : '',
						start:0
					},
					fields : [ 'id', 'title', 'date', 'description', 'afunction' ],
					listeners : {
						'load' : function() {		
								if(this.getCount()>0){
									Ext.getCmp('searchPortlet<%=archive.getIdArchive()%>').show();
									Ext.getCmp('box_<%=archive.getIdArchive()%>').show();
									Ext.getCmp('paging_barMulti<%=archive.getIdArchive()%>').show();									
								}else{
									Ext.getCmp('searchPortlet<%=archive.getIdArchive()%>').hide();
									Ext.getCmp('box_<%=archive.getIdArchive()%>').hide();	
									Ext.getCmp('paging_barMulti<%=archive.getIdArchive()%>').hide();									
								}								
						}			
					}
				});
		 
				var searchresultsGrid<%=archive.getIdArchive()%> = new Ext.grid.GridPanel({
						store : dsSearch<%=archive.getIdArchive()%>,
						id : 'searchresultsGrid<%=archive.getIdArchive()%>',
						trackMouseOver : true,
						disableSelection : true,
						loadMask : {
							msg : 'caricamento in corso...',
							enable : true
						},
						border : false,
						plugins : new Ext.ux.grid.RowExpander({ 
							tpl : new Ext.Template('<div style="padding:2px;margin:1px;border-top:1px solid #d0d0d0;text-align:justify">{description}</div>')
						}),
						collapsible : false,
						enableColumnResize : false,
						enableColumnMove : false,
						headerAsText : false,
						enableColumnHide : false,
						animCollapse : false,
						header : false,
						iconCls : 'icon-grid',
						autoHeight : true,
						stateful : true,
						hideHeaders : true,
						autoScroll : false,
						enableHdMenu : false,
						autoSizeColumns : true,
						autoSizeGrid : true,
						autoExpandColumn : 'title',
						cm: new Ext.grid.ColumnModel({
				            columns : [ new Ext.ux.grid.RowExpander({
								width : 16, 
								tpl : new Ext.Template('<div style="padding:2px;margin:1px;border-top:1px solid #d0d0d0;text-align:justify">{description}</div>')
							}), {
								id : 'title',
								header : "Titolo",
								dataIndex : 'title'
							}, {
								id : 'data',
								header : "Data",
								dataIndex : 'date',
								width : 30
							} ]
				        }),
						viewConfig : {
							forceFit : true,
							getRowClass: function(record, index) {
					                return 'archive_<%=archive.getIdArchive()%>_color';
					        }
						},
						listeners : {
							'afterrender' : function() {
							}			
						}
				 });
				 var bbar<%=archive.getIdArchive()%> = new Ext.PagingToolbar({
                 	 id:'paging_barMulti<%=archive.getIdArchive()%>',
                 	 store: dsSearch<%=archive.getIdArchive()%>,
                     pageSize: 15,
                     displayInfo: true,
                     displayMsg: 'Risultati {0} - {1} di {2}',
                     emptyMsg: "Nessuna occorrenza trovata"
                 });
				 var searchresultsPanel<%=archive.getIdArchive()%> = new Ext.Panel({
					 layout:'fit',
					 autoHeight : true,
					 id:'searchresultsPanel<%=archive.getIdArchive()%>',
					 items:[searchresultsGrid<%=archive.getIdArchive()%>],
					 bbar:bbar<%=archive.getIdArchive()%>
				 });
				 var box_<%=archive.getIdArchive()%> =  {
			                columnWidth:.95,
			                id:'box_<%=archive.getIdArchive()%>',
			                style:'padding:10px 0 10px 10px',
			                collapsed: false,
			                items:[{
			                	id:'searchPortlet<%=archive.getIdArchive()%>',
			                    title: '<img width="12" height="12" src="img/archive_img/archive_<%=archive.getIdArchive()%>.gif" border="0"/>&nbsp;<%=archive.getLabel()%>',
			                    id_archive:<%=archive.getIdArchive()%>,
			                    closable:false, 
			                    collapsed:true,
			                    layout:'fit',
			                    tools: [{
			           	        id:'gear',
							       	        alt:'Accedi all\'archivio',
							       	        handler: function(e, target, box){
								                    	<%if(archive.getArchiveIdentities().getIdArchiveIdentity()==ArchiveIdentity.THESAURUS){%>
								                    	 	parent.addTab('<%=JsSolver.escapeSingleApex(archive.getLabel())%>','<%=archive.getIdArchive()%>','skosWorkspace.html?idArchive=<%=archive.getIdArchive()%>');
								                    	 <%}else if(archive.getArchiveIdentities().getIdArchiveIdentity()== ArchiveIdentity.HIERARCHIC){%>
								                    	 	parent.addTab('<%=JsSolver.escapeSingleApex(archive.getLabel())%>','<%=archive.getIdArchive()%>','hierarchicWorkspace.html?idArchive=<%=archive.getIdArchive()%>');
								                    	 <%}else {%>
								                    		parent.addTab('<%=JsSolver.escapeSingleApex(archive.getLabel())%>','<%=archive.getIdArchive()%>','documentalWorkspace.html?idArchive=<%=archive.getIdArchive()%>');
									                     <%}%>
							       	        }
							       	    },{
							       	        id:'close',
							       	        handler: function(e, target, box){
							       	    		Ext.getCmp('ck-multi-search-archive_'+box.id_archive).setValue(false);							       	    		
							       	    		Ext.getCmp('box_'+box.id_archive).hide();

							       	        }
							       	    }],
							       	items:[searchresultsPanel<%=archive.getIdArchive()%>]
			                }]
			            };
			   <%
		}
	}%>	
	var checkboxGroup = {	        
	        xtype: 'fieldset',
	        title: 'Seleziona archivi',
	        autoHeight: true,
	        collapsed: true,
	        collapsible: true,
	        items: [{
		             xtype: 'checkboxgroup',
		             columns: 3,
		             vertical: true,
		             items: [
							<%if(request.getAttribute("archiveList")!=null){%> 
								   {
									   boxLabel: 'Seleziona/deseleziona tutti', 
									   name: 'sel_all',
									   id:'sel_all', 
									   inputValue: 0, 
									   checked: true,
									   listeners: {
						     			'check': function(ctl, val){ 
									   				filter = false;
				     								if(val){
				     										loadSearchFields(false);
					     									var cks = Ext.select("input[name^=ck-multi-search-archive_]").elements;
					     									for (var x = 0 ; x < cks.length; x ++){
						     									Ext.getCmp(Ext.get(cks[x]).id).setValue(true);
						     									Ext.getCmp('box_'+Ext.getCmp(Ext.get(cks[x]).id).inputValue).show();
							     							}
				     								}else{
				     									var cks = Ext.select("input[name^=ck-multi-search-archive_]").elements;
				     									for (var x = 0 ; x < cks.length; x ++){
					     									Ext.getCmp(Ext.get(cks[x]).id).setValue(false);
					     									Ext.getCmp('box_'+Ext.getCmp(Ext.get(cks[x]).id).inputValue).hide();
						     							}														
				     									Ext.getCmp('commonSearchFields').removeAll(true);
					     							}
				     								filter = true;
						          			  }
				        				}
									},
								   <%List<Object> archives = (List)request.getAttribute("archiveList");
								   Object[] archiveList = archives.toArray();
								   for(int i=0;i<archiveList.length;i++){
									   Archives archive = (Archives)archiveList[i];%>								   
									   {
										boxLabel: '<%=archive.getLabel()%>', 
										name: 'ck-multi-search-archive_<%=archive.getIdArchive()%>',
										id: 'ck-multi-search-archive_<%=archive.getIdArchive()%>', 
										inputValue: <%=archive.getIdArchive()%>,
										checked: true,
										listeners: {
									     			'check': function(ctl, val){
						     									loadSearchFields(true);
							     								if(val){
							     									Ext.getCmp('box_'+ctl.inputValue).show();
							     								}else{
								     								Ext.getCmp('box_'+ctl.inputValue).hide();
							     								}	
									          			  }
							        				}
										}
										<%if(i<archiveList.length-1){%>,<%}%>
								   <%}
							}%>
		            		]
			        }
	        ]
	 };
	 
	var commonSearchFields = {	        
	        xtype: 'fieldset',
	        title: 'Campi di ricerca',
	        autoHeight: true,
	        id:'commonSearchFields',
	        bodyStyle:'padding:5px;',
	        items: [
	    	        <%
	    	        ArrayList<Object>  searchElements= (ArrayList<Object>)request.getAttribute("searchElements");
					for (int i = 0; i < searchElements.size(); i++) {
						Element element = (Element) searchElements.get(i);
						if(!element.getName().equals("order")){
							if(element.getPages().equalsIgnoreCase("all") || element.getPages().indexOf("simple_search")!=-1){
						%>
							<%=element.getCdata_section()%>
							<%if(i<searchElements.size()-1){%>,<%}%>
						<%  }
						}
					}
					%>
	        		]
	 };
	var panelTH = {	        
	        xtype: 'fieldset',
	        title: 'Relazioni con altri tesauri',
	        autoHeight: true,
	        collapsed: true,
	        collapsible: true,
	        id:'thPanel',
	        contentEl:'relationTH'
	 };
	 var archiveBoxes = {
	            xtype:'portal',
	            region:'center',
	            margins:'5 5 5 5',
	            autoScroll: false,
	            id:'archiveBoxes',
	            items:[
						<%if(request.getAttribute("archiveList")!=null){
							List<Object> archives = (List)request.getAttribute("archiveList");
							Object[] archiveList = archives.toArray();
							for(int i=0;i<archiveList.length;i++){
								   Archives archive = (Archives)archiveList[i];%>
								   box_<%=archive.getIdArchive()%><%if(i<archiveList.length-1){%>,<%}%>
							<%}
						}%>
	   	          	]
	        };
	 fp = new Ext.FormPanel({
	        frame: true,
	        id:'multiSearchPanel',
	        title: 'Gestione ricerca',
	        width: '99%',
	        renderTo:'multiSearch',
	        bodyStyle: 'padding:0 10px 0;',
	        style:'margin:5px 5px 5px 5px',
	        collapsible: true,
	        items: [
					checkboxGroup,
					commonSearchFields,
					panelTH
	        ],
	        keys: [
	               { key: [Ext.EventObject.ENTER], handler: function() {
	            	   		executeMultiSearch();
	                   }
	               }
	           ], 	
	        buttons: [{
	            text: 'Cerca',
	            handler: function(){
	        		executeMultiSearch();
	            }
	        },{
	            text: 'Ripristina',
	            handler: function(){
					try{
                		fp.getForm().reset();
					}catch(e){}
	            }
	        }]
	    });
	 var archivePanel = new Ext.FormPanel({
	        frame: true,
	        labelWidth: 110,
	        title: 'Risultati',
	        width: '99%',
	        renderTo:'archivePanel',
	        bodyStyle: 'padding:0 10px 0;',
	        style:'margin:10px 5px 5px 5px',
	        items: [archiveBoxes]
	    });
	 if(fromTreeMultiSearch){ 
			hierarchicalSearch(fromTreeMultiSearchIdRecord,fromTreeMultiSearchTitle); 
			fromTreeMultiSearch=false;
			fromTreeMultiSearchIdRecord='';
	 }	  
});
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
function loadSearchFields(testChecked){
	if(filter || !testChecked){
		var cks = Ext.select("input[name^=ck-multi-search-archive_]").elements;
		var requestArchives='';
		for (var x = 0 ; x < cks.length; x ++){
			if(Ext.getCmp(Ext.get(cks[x]).id).getValue() || !testChecked){
				requestArchives+=Ext.getCmp(Ext.get(cks[x]).id).inputValue+";";
			}
		}		
		Ext.getCmp('commonSearchFields').removeAll(true);
		Ext.getCmp('commonSearchFields').load({ 
			url: 'multiSearch.html?action=filter&requestArchives='+requestArchives,
			nocache: true,
			text: 'Aggiornamento campi di ricerca in corso...', 
			timeout: 30, 
			scripts: true,
			callback: addOldfilter
		});
	}
}
function addOldfilter(){
	if(queryTH!=null && queryTH!=undefined){
		 Ext.getCmp('commonSearchFields').add({
											 xtype: 'checkbox',
											 id:'th_filter',
								             name: 'th_filter',
								             fieldLabel:'Filtro',
								             boxLabel: fromTreeMultiSearchTitle,
								             name: 'sel_all', 
											 inputValue: queryTH, 
											 checked: true,
								             anchor: '95%'
									        });
		 Ext.getCmp('commonSearchFields').doLayout();   
	}
}
function hierarchicalSearch(idRecord,title){
	Ext.MessageBox.buttonText.yes = "Sí"; 
	Ext.MessageBox.buttonText.cancel = "No";
	var deep = false; 
	Ext.MessageBox.confirm('Attenzione', 'Estendere la ricerca a tutto il ramo selezionato?',function(btn){
		if(btn=="yes"){
			openLoading('attendere','Ricerca dei documenti collegati al ramo selezionato...','ricerca...');
			deep=true;
		}else{
			openLoading('attendere','Ricerca dei documenti collegati alla voce selezionata...','ricerca...');
			deep=false;
		}
		thSearch(idRecord);
		Ext.Ajax.request({
			         url: 'desktop_adv_search.html?action=find_sons&idRecord='+idRecord+"&deep="+deep,
			         nocache: true,
			         success: function(r) {
			        	 var query = '%2B('+encodeURI(Ext.util.Format.trim(r.responseText))+")~";
			        	 queryTH = query;      	 
			        	 Ext.getCmp('commonSearchFields').remove('th_filter');
			        	 Ext.getCmp('commonSearchFields').add({
			       												 xtype: 'checkbox',
			       												 id:'th_filter',
													             name: 'th_filter',
													             fieldLabel:'Filtro',
													             boxLabel: title,
													             name: 'sel_all', 
																 inputValue: query, 
																 checked: true,
													             anchor: '95%'
														        });
			        	 Ext.getCmp('commonSearchFields').doLayout();   
			        	 var cks = Ext.select("input[name^=ck-multi-search-archive_]").elements;
			        	 var oldQuery = '';		
			        	 var date_range_query = '';	
			        	 var fields = fp.getForm().items;							
						 for (var x = 0 ; x < fields.length; x ++){
							  if(fields.get(x).getName().indexOf('sf_')!=-1){																																																											
									if(fields.get(x).xtype=='checkboxgroup' || fields.get(x).xtype=='radiogroup'){
										var ckFields = fields.get(x).items;
										for (var y = 0 ; y < ckFields.length; y ++){
											if(ckFields.get(y).getValue()){																												
												if(fields.get(x).getName().indexOf('sf_')!=-1){
													oldQuery+='%2B'+fields.get(x).getName().substring(3,fields.get(x).getName().length)+":"+ckFields.get(y).inputValue+'~';
												}else{
													try{
													if(fields.get(x).getName().indexOf("ext-comp")==-1)
														oldQuery+='%2B'+fields.get(x).getName()+":"+ckFields.get(y).inputValue+'~';
													}catch(ecc){}
												}	
											}
										}																														
									}else if(fields.get(x).xtype=='datefield'){
										if(fields.get(x).value!=undefined && Ext.util.Format.trim(fields.get(x).value)!=''){
											date_range_query+=fields.get(x).getName().substring(3,fields.get(x).getName().length)+':'+fields.get(x).value+'~';
										}																						
									}else if(Ext.util.Format.trim(fields.get(x).getValue())!=''){
										oldQuery+='%2B'+fields.get(x).getName().substring(3,fields.get(x).getName().length)+':'+fields.get(x).getValue()+'~';
									}																																																							
							  }
						 }
						 if(oldQuery!=''){
							 query=oldQuery+" "+query;
						 }
			       		 for (var x = 0 ; x < cks.length; x ++){
				       			if(Ext.getCmp(Ext.get(cks[x]).id).getValue()){
				       				Ext.getCmp('searchPortlet'+Ext.getCmp(Ext.get(cks[x]).id).inputValue).expand();									
				       				eval('dsSearch'+Ext.getCmp(Ext.get(cks[x]).id).inputValue+'.baseParams.query=\''+query+'\';');
				       				eval('dsSearch'+Ext.getCmp(Ext.get(cks[x]).id).inputValue+'.baseParams.date_range_query=\''+date_range_query+'\';');
				       				if(oldQuery==''){
				       					eval('dsSearch'+Ext.getCmp(Ext.get(cks[x]).id).inputValue+'.baseParams.idRecord=\''+idRecord+'\';');
				       				}else{
				       					eval('dsSearch'+Ext.getCmp(Ext.get(cks[x]).id).inputValue+'.baseParams.idRecord=\'\';');	
						       		}
				       				eval('dsSearch'+Ext.getCmp(Ext.get(cks[x]).id).inputValue+'.load()');
				       			}							
			       		 }
			       		closeLoading();
			         },
			         failure: function(){closeLoading();}
		});
	});
}
function thSearch(idRecord){	
	$("#relationTH").load("desktop_adv_search.html?action=th_relations&idRecord="+idRecord,function(){
		Ext.getCmp('thPanel').expand();	
	});
}
function executeMultiSearch(){
	if(fp.getForm().isValid()){
        var ck_ok = false;
        var cks = Ext.select("input[name^=ck-multi-search-archive_]").elements;
		for (var x = 0 ; x < cks.length; x ++){
			if(Ext.getCmp(Ext.get(cks[x]).id).getValue()){
				ck_ok = true;
				break;
			}							
		}
		if(ck_ok){
			var fields = fp.getForm().items;
			var query = '';			
			var order_by = '';
			var sort_type = '';
			var date_range_query = '';							
			for (var x = 0 ; x < fields.length; x ++){							
				if(fields.get(x).getName().indexOf('sf_')!=-1){																																																											
					if(fields.get(x).xtype=='checkboxgroup' || fields.get(x).xtype=='radiogroup'){
						var ckFields = fields.get(x).items;
						for (var y = 0 ; y < ckFields.length; y ++){
							if(ckFields.get(y).getValue()){																												
								if(fields.get(x).getName().indexOf('sf_')!=-1){
									query+='%2B'+fields.get(x).getName().substring(3,fields.get(x).getName().length)+":"+ckFields.get(y).inputValue+'~';
								}else{
									try{
									if(fields.get(x).getName().indexOf("ext-comp")==-1)
										query+='%2B'+fields.get(x).getName()+":"+ckFields.get(y).inputValue+'~';
									}catch(ecc){}
								}	
							}
						}																														
					}else if(fields.get(x).xtype=='datefield'){
						if(fields.get(x).getValue()!=undefined && Ext.util.Format.trim(fields.get(x).getValue())!=''){
							date_range_query+=fields.get(x).getName().substring(3,fields.get(x).getName().length)+':'+fields.get(x).value+' ';	
						}																												
					}else if(Ext.util.Format.trim(fields.get(x).getValue())!=''){
						query+='%2B'+fields.get(x).getName().substring(3,fields.get(x).getName().length)+':'+fields.get(x).getValue()+'~';
					}																																																							
				}else{
					if(fields.get(x).xtype=='checkboxgroup'){
							var ckFields = fields.get(x).items;
							for (var y = 0 ; y < ckFields.length; y ++){
								if(ckFields.get(y).getValue()){
									if(fields.get(x).getName().indexOf('sf_')!=-1){
										query+='%2B'+fields.get(x).getName().substring(3,fields.get(x).getName().length)+":"+ckFields.get(y).inputValue+'~';
									}else{
										//console.debug(ckFields.get(y).id);
										try{
										  if(fields.get(x).getName().indexOf("ext-comp")==-1)
											query+='%2B'+fields.get(x).getName()+":"+ckFields.get(y).inputValue+'~';
										}catch(ecc){}
									}																																	
								}
							}																															
					}else if(fields.get(x).xtype=='radiogroup'){
						var radioFields = fields.get(x).items;
						for (var y = 0 ; y < radioFields.length; y ++){
							if(radioFields.get(y).getValue() && radioFields.get(y).getName()=='order_by'){
								order_by = radioFields.get(y).inputValue;
								sort_type = radioFields.get(y).sort_type; 																															
							}
						}	
						
					}
					
				}	
			}
			if(Ext.getCmp('th_filter')!=null && Ext.getCmp('th_filter')!=undefined && Ext.getCmp('th_filter').getValue()){
				query=query+" "+Ext.getCmp('th_filter').inputValue+"~";
			}
			for (var x = 0 ; x < cks.length; x ++){
				if(Ext.getCmp(Ext.get(cks[x]).id).getValue()){
					Ext.getCmp('searchPortlet'+Ext.getCmp(Ext.get(cks[x]).id).inputValue).expand();
					//Ext.getCmp('searchPortlet'+Ext.getCmp(Ext.get(cks[x]).id).inputValue).show();									
					eval('dsSearch'+Ext.getCmp(Ext.get(cks[x]).id).inputValue+'.baseParams.idRecord=\'\';');								
					eval('dsSearch'+Ext.getCmp(Ext.get(cks[x]).id).inputValue+'.baseParams.query=\''+query+'\';');
					eval('dsSearch'+Ext.getCmp(Ext.get(cks[x]).id).inputValue+'.baseParams.order_by=\''+order_by+'\';');
					eval('dsSearch'+Ext.getCmp(Ext.get(cks[x]).id).inputValue+'.baseParams.sort_type=\''+sort_type+'\';');
					eval('dsSearch'+Ext.getCmp(Ext.get(cks[x]).id).inputValue+'.baseParams.date_range_query=\''+date_range_query+'\';');
					eval('dsSearch'+Ext.getCmp(Ext.get(cks[x]).id).inputValue+'.load()');
				}							
			}
			if(Ext.getCmp('th_filter')==null || Ext.getCmp('th_filter')==undefined){
				Ext.getCmp('thPanel').collapse();	
				$("#relationTH").html('');
			}							
		}else{
			Ext.Msg.alert('Attenzione','Selezionare almeno un\'archivio prima di effettuare la ricerca!');
		}
        
    }else{
    	Ext.Msg.alert('Attenzione','Inserire almeno un termine di ricerca!');
    }
}
</script>
<div id="multiSearch"></div>
<div id="archivePanel"></div>
<div id="relationTH"></div>
<div id="multiSearchVoc"></div>