<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="com.openDams.bean.Relations"%>
<%@page import="com.openDams.bean.RelationTypes"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.openDams.security.UserDetails"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="org.springframework.security.core.context.SecurityContext"%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="com.regesta.framework.util.DateUtility"%>
<%@page import="com.openDams.bean.RecordsVersion"%>
<%@page import="java.util.Set"%>
<%@include file="../locale.jsp"%>
<%
if(request.getAttribute("records")!=null){
//UserDetails user =  (UserDetails)((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
Records records = (Records)request.getAttribute("records");
TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
HashMap<String, String[]> parsedTitle = titleManager.parseTitle(records.getTitle(),records.getArchives().getIdArchive());
%>

<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%><script>
$(document).ready(function() {
	$("#relations_BT").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=1",function(){
		build_relations_link($(this));
	});
	$("#relations_NT").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=2",function(){
		build_relations_link($(this));
	});
	$("#relations_RT").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=3",function(){
		build_relations_link($(this));
	});
	$("#relations_IS").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=9",function(){
		build_relations_link($(this));
	});
	$("#relations_BM").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=5",function(){
		build_relations_link($(this));
	});
	$("#relations_NM").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=6",function(){
		build_relations_link($(this));
	});
	$("#relations_RM").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=7",function(){
		build_relations_link($(this));
	});
	$("#relations_CM").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=8",function(){
		build_relations_link($(this));
	});	
});


var relation, id_father,idArchive; 
function build_relations_link(div){
	div.find('.delete_button_relation').click(function(){
		 if($(this).attr("title")!="${skostabtabbuttondisabled}"){
			 var id_record_relation = $(this).attr("id_record_relation");
			 var relation_type = $(this).attr("relation_type");
			 function doDelete(btn){
				 if(btn=='yes'){
					 var id_record = $("form[name='editForm']").find("input[name='idRecord']").attr("value");
					 $("#relation_results").load("ajax/relation_manager.html?idRecord="+id_record+"&id_record_relation="+id_record_relation+"&relation_type="+relation_type+"&action=delete",function(){openRecord(id_record);});				 
				 }
			 }
			 Ext.MessageBox.confirm('${skosTabconfirmtitle}', '${skosTabconfirmmessage}', doDelete);	 
		} 
	});
	
	div.find('.add_button_relation').click(function(){
		if($(this).attr("title")!="${skostabtabbuttondisabled}"){
           var relation = $(this).attr("id").substring($(this).attr("id").lastIndexOf("_")+1,$(this).attr("id").length);
			var store = new Ext.data.JsonStore({
			    url: 'ajax/autocomplete.html',
			    root: 'data',
			    idProperty: 'id',
			    baseParams:{id_archive:'<%=records.getArchives().getIdArchive()%>'},
			    fields: ['id','value']
			});
			store.setDefaultSort('value', 'asc');
			var wind = new Ext.Window({
	                title: '${skosTabsearchconcepttitle}',
	                modal: true,
	                width: 292,
	                height: 87,
	                border:false,
	                margins: '5 5 5 5',
	                items: [
							new Ext.form.ComboBox({
								id: 'searchinput',
							    store: store,
							    displayField:'value',
					        	valueField: 'id',
							    typeAhead: true,
							    mode: 'remote',
							    queryParam: 'searchinput',  //contents of the field sent to server.
							    hideTrigger: true,  //hide trigger so it doesn't look like a combobox.
							    selectOnFocus:true,
							    width: 280,							   
							    emptyText:'${skosTabsearchconceptinputvalue}'    						                              
							})
	    	    	],
	    	    	buttons: [
	  	                    {
	  	                        text:'${skosTabsearchconceptbuttonadd}',
	  	                        handler: function(){
	  	                        $("#relation_results").load("ajax/relation_manager.html?idRecord="+$("form[name='editForm']").find("input[name='idRecord']").attr("value")+"&id_record_relation="+Ext.getCmp ('searchinput').getValue()+"&relation_type="+relation+"&action=add",function(){openRecord($("form[name='editForm']").find("input[name='idRecord']").attr("value"));});
	  	                        wind.hide();
	  	                        wind.destroy(true);
	  	                        
	  	                    	}
	  	                    },{
	  	                        text: '${skosTabsearchconceptbuttonsearch}',
	  	                        handler: function(){
	  	                    		Ext.getCmp ('relation_results_accordion').expand();	  
	  	                    		Ext.getCmp ('relation_results_accordion').setDisabled(false);	  
	  	                    		
	  	                    		Ext.getCmp ('query_relation').setRawValue($("#searchinput").attr("value"));
	  	                    		Ext.getCmp ('query_relation').setValue($("#searchinput").attr("value"));
	  	                    		ds_relation.baseParams.query=$("#searchinput").attr("value");
	  	                    		ds_relation.baseParams.relation=relation;
	  	                    		ds_relation.baseParams.id_archive ='<%=records.getArchives().getIdArchive()%>';
	  	                    		if(!$("#tree").is(":hidden")){
	  	                    	    	ds_relation.baseParams.reopen_accordion='tree_accordion';	  	
			  	                    }else{
			  	                    	ds_relation.baseParams.reopen_accordion='results_accordion';	  
				  	                }   	
	  	                    		ds_relation.load({params:{start:0}});
	  	                    		wind.hide();
	  	                    		wind.destroy(true);
	  	                        }
	  	                    }
	                      ]
	              }).show();
		}  
	});
	div.find('.add_button_external_relation').click(function(){
		if($(this).attr("title")!="${skostabtabbuttondisabled}"){
           var relation = $(this).attr("id").substring($(this).attr("id").lastIndexOf("_")+1,$(this).attr("id").length);
			var store = new Ext.data.JsonStore({
			    url: 'ajax/autocomplete.html',
			    root: 'data',
			    idProperty: 'id',
			    baseParams:{id_archive:'16'},
			    fields: ['id','value']
			});
			store.setDefaultSort('value', 'asc');
			var wind = new Ext.Window({
	                modal: true,
	                width: 302,
	                autoHeight: true,
	                border:false,
	                margins: '5 5 5 5',
	                items: [
							{
					            xtype: 'fieldset',
					            title: 'seleziona il tesauro',
					            autoHeight: true,
					            defaultType: 'radio', // each item will be a radio button
					            id:"archive_id_radio_container",
					            items: [
							    <%if(request.getAttribute("archives")!=null){
							    	List<Archives> list = (List<Archives>)request.getAttribute("archives");
							    	int count = 0;
							    	for(int i=0;i<list.size();i++){
							    		Archives archives = list.get(i);
							    		if(archives.getIdArchive()!=records.getArchives().getIdArchive()){%>
							    		{
							    			<%if(i==0){%>checked: true,<%count++;}%>
							                fieldLabel: <%if(i==0){%>'tesauri'<%}else{%>''<%}%>,
							                boxLabel: '<%=archives.getLabel()%>',
							                name: 'archive_id_radio',
							                inputValue: '<%=archives.getIdArchive()%>',
							                onClick: function(e, el) {
							                	store.baseParams.id_archive = '<%=archives.getIdArchive()%>';
											}
							            }
							    		<%}else{%>
							    		{
							                disabled: true,
							                fieldLabel: <%if(i==0){%>'tesauri'<%}else{%>''<%}%>,
							                boxLabel: '<%=archives.getLabel()%>',
							                name: 'archive_id_radio',
							                inputValue: '<%=archives.getIdArchive()%>'
							            }
							    		<%}%>
							    		<%if(i!=list.size()-1){%>,<%}%>
							    	<%}	
							    }%>        
							    ]
					        },
					        new Ext.form.ComboBox({
								id: 'searchinput',
							    store: store,
							    displayField:'value',
					        	valueField: 'id',
							    typeAhead: true,
							    mode: 'remote',
							    queryParam: 'searchinput',  //contents of the field sent to server.
							    hideTrigger: true,  //hide trigger so it doesn't look like a combobox.
							    selectOnFocus:true,
							    width: 290,							   
							    emptyText:'inserisci il termine da ricercare'    						                              
							})
	    	    	],
	    	    	buttons: [
	  	                    {
	  	                        text:'${skosTabsearchconceptbuttonadd}',
	  	                        handler: function(){
	  	                        $("#relation_results").load("ajax/relation_manager.html?idRecord="+$("form[name='editForm']").find("input[name='idRecord']").attr("value")+"&id_record_relation="+Ext.getCmp ('searchinput').getValue()+"&relation_type="+relation+"&action=add",function(){});
	  	                        wind.hide();
	  	                        wind.destroy(true);
	  	                        openRecord($("form[name='editForm']").find("input[name='idRecord']").attr("value"));
	  	                    	}
	  	                    },{
	  	                        text: '${skosTabsearchconceptbuttonsearch}',
	  	                        handler: function(){
	  	                    		Ext.getCmp ('relation_results_accordion').expand();	  
	  	                    		Ext.getCmp ('relation_results_accordion').setDisabled(false);	  
	  	                    		
	  	                    		Ext.getCmp ('query_relation').setRawValue($("#searchinput").attr("value"));
	  	                    		Ext.getCmp ('query_relation').setValue($("#searchinput").attr("value"));
	  	                    		ds_relation.baseParams.query=$("#searchinput").attr("value");
	  	                    		ds_relation.baseParams.relation=relation;
	  	                    		ds_relation.baseParams.id_archive = Ext.getCmp('archive_id_radio_container').items.get(0).getGroupValue();
	  	                    		if(!$("#tree").is(":hidden")){
	  	                    	    	ds_relation.baseParams.reopen_accordion='tree_accordion';	  	
			  	                    }else{
			  	                    	ds_relation.baseParams.reopen_accordion='results_accordion';	  
				  	                }   	
	  	                    		ds_relation.load({params:{start:0}});
	  	                    		wind.hide();
	  	                    		wind.destroy(true);
	  	                    		
	  	                        }
	  	                    }
	                      ]
	              }).show();
		}  
	});
	div.find('.add_button_relation_create').click(function(){
		  relation = $(this).attr("id").substring($(this).attr("id").lastIndexOf("_")+1,$(this).attr("id").length);
		  id_father = $("form[name='editForm']").find("input[name='idRecord']").attr("value");
		  idArchive = $("form[name='editForm']").find("input[name='idArchive']").attr("value"); 
		if($(this).attr("title")!="${skostabtabbuttondisabled}"){
			Ext.MessageBox.buttonText.ok = "${buttonCreate} ConceptScheme";
			Ext.MessageBox.buttonText.cancel = "${buttonCreate} Concept";
			Ext.MessageBox.show({
		           title: '${skosTabcreateAddtitle}',
		           msg: '${skosTabcreateAddmessage}',
		           width:300,
		           buttons: Ext.MessageBox.OKCANCEL,
		           multiline: true,
		           fn: createAdd
		    });
			Ext.MessageBox.buttonText.ok = "OK";
			Ext.MessageBox.buttonText.cancel = "Cancell";	
			
		}  
	});	
}
function createAdd(btn, text){     
    if(btn=='ok'){
    	 if($.trim(text)!=''){
    		 openLoading();
    		 $("#relation_results").load("ajax/create_add.html?id_father="+id_father+"&label="+escape(text)+"&relation_type="+relation+"&idArchive="+idArchive+"&xmltype=ConceptScheme&idRecordType=1",function(){
    			 closeLoading();
    			 reloadTree();
    			 openRecord(id_father);
        	 });	                        
         }else{
        	 Ext.MessageBox.show({
                 msg: '${skosTabcreateAddmessagetrim}',
                 buttons: Ext.MessageBox.OK,                         
                 icon: 'ext-mb-warning'
             });
         }
     }else{
    	 if($.trim(text)!=''){
    		 openLoading();
    		 $("#relation_results").load("ajax/create_add.html?id_father="+id_father+"&label="+escape(text)+"&relation_type="+relation+"&idArchive="+idArchive+"&xmltype=Concept&idRecordType=2",function(){
    			 closeLoading();
    			 reloadTree();
    			 openRecord(id_father);
        	 });	                        
         }else{
        	 Ext.MessageBox.show({
                 msg: '${skosTabcreateAddmessagetrim}',
                 buttons: Ext.MessageBox.OK,                         
                 icon: 'ext-mb-warning'
             });
         }
     }
		
	}
function next_prew_children_page_tab(id_record,action,tot_page,current_page,id_relation,fromTree){
	tot  = parseInt(tot_page,10);
	 if(fromTree==undefined)
	     next_prew_children_page('<%=records.getIdRecord()%>',action,tot_page,current_page,true);
	if(action=='next'){
         if(current_page+1>tot){			
         }else{        	
        	 current_page = current_page+1;
         }
	}else if(action=='last'){
		current_page = tot_page;
	}else if(action=='first'){
		current_page = 1;
	}else{
		 if(current_page-1<=0){			
         }else{
        	 current_page = current_page-1;
         }
	}
	openLoading();
	if(id_relation=='1'){
		$("#relations_BT").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=1&current_page="+current_page,function(){
			build_relations_link($(this));
			closeLoading();
		});
	}else if(id_relation=='2'){	
		$("#relations_NT").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=2&current_page="+current_page,function(){
			build_relations_link($(this));
			closeLoading();
		});
	}else if(id_relation=='3'){	
		$("#relations_RT").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=3&current_page="+current_page,function(){
			build_relations_link($(this));
			closeLoading();
		});
	}else if(id_relation=='9'){	
		$("#relations_IS").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=9&current_page="+current_page,function(){
			build_relations_link($(this));
			closeLoading();
		});
	}else if(id_relation=='5'){	
		$("#relations_BM").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=5&current_page="+current_page,function(){
			build_relations_link($(this));
			closeLoading();
		});
	}else if(id_relation=='6'){	
		$("#relations_NM").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=6&current_page="+current_page,function(){
			build_relations_link($(this));
			closeLoading();
		});
	}else if(id_relation=='7'){	
		$("#relations_RM").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=7&current_page="+current_page,function(){
			build_relations_link($(this));
			closeLoading();
		});
	}else if(id_relation=='8'){	
		$("#relations_CM").load("ajax/record.html?idArchive=<%=request.getParameter("idArchive")%>&action=record_relations&idRecord=<%=records.getIdRecord()%>&idRelationType=8&current_page="+current_page,function(){
			build_relations_link($(this));
			closeLoading();
		});
	}
} 
function editRelationNote(ref_id_record_1,ref_id_record_2,relation_type,note_text){
	var win_edit_relation_note = new Ext.Window({
        title:'Inserisci nota',
        width       : 450,
        height      : 200,
        plain       : true,
        closeAction:'destroy',
        html       : '<div><textarea id="ref_'+ref_id_record_1+'_'+ref_id_record_2+'_'+relation_type+'_htmleditor">'+note_text+'</textarea></div>',
        buttons: [{
                    text: 'Salva Nota',
                    handler: function(){
		                	Ext.Ajax.request({
						          url : 'ajax/relation_manager.html?idRecord='+ref_id_record_1+'&id_record_relation='+ref_id_record_2+'&relation_type='+relation_type+'&action=add_note&note='+escape(getMCEText($('#ref_'+ref_id_record_1+'_'+ref_id_record_2+'_'+relation_type+'_htmleditor'))),
				                  method: 'POST',
				                  success: function ( result, request ) {
		                			  openRecord(ref_id_record_1);
		                		 	  win_edit_relation_note.close();
					              },
					                  failure: function ( result, request ) {
					                  fn_AKExt(result.responseText, 'Error');
					              }
							 });
                    }
                   }
      			  ],
      	listeners: { 
	      			   'afterrender': function(elemento){
						initSimpleMceEditor($('#ref_'+ref_id_record_1+'_'+ref_id_record_2+'_'+relation_type+'_htmleditor'));
    			   }
			}
    });
	win_edit_relation_note.show();
}
function deleteRelationNote(ref_id_record_1,ref_id_record_2,relation_type){
	Ext.Ajax.request({
        url : 'ajax/relation_manager.html?idRecord='+ref_id_record_1+'&id_record_relation='+ref_id_record_2+'&relation_type='+relation_type+'&action=add_note&note=null',
        method: 'POST',
        success: function ( result, request ) {
			  openRecord(ref_id_record_1);
        },
            failure: function ( result, request ) {
            fn_AKExt('Attenzione si è verificato un errore', 'Error');
        }
	 });
}
function fn_AKExt( message, title ){
	   Ext.Msg.show({
	      title: title,
	      msg: message ,
	      buttons: Ext.MessageBox.OK,
	      icon: Ext.MessageBox.INFO
	     });
}
function reorderRelations(id_record,id_relation){
	var dsSearchRelations = new Ext.data.JsonStore({
		url : 'ajax/record.html',
		root : 'data',
		idProperty : 'id',
		baseParams : {
			idArchive:'<%=request.getParameter("idArchive")%>',
			idRecord :id_record,
			idRelationType:id_relation,
			action : 'record_relations_sort'
		},
		fields : [ 'id', 'title']
	});

	var reorderRelationsGrid = new Ext.grid.GridPanel({
		id: 'relation_grid_'+id_record+'_'+id_relation,
		store: dsSearchRelations,
		loadMask: true,
		ddGroup:'dd_relations_'+id_record+'_'+id_relation,
		enableDragDrop: true,
		viewConfig: {
		sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
		forceFit: true
		}, 
		columns: [{
			id : 'title',
			header : "",
			dataIndex : 'title'
		}],
		listeners: {
		"afterrender": {
		  scope: this,
		  fn: function(grid) {
		              var ddrow = new Ext.dd.DropTarget(grid.container, {
		                  ddGroup : 'dd_relations_'+id_record+'_'+id_relation,
		                  copy:false,
		                  notifyDrop : function(dd, e, data){
		                        var ds = grid.store;
		                        var sm = grid.getSelectionModel();
		                        var rows = sm.getSelections();
		                        if(dd.getDragData(e)) {
		                            var cindex=dd.getDragData(e).rowIndex;
		                            if(typeof(cindex) != "undefined") {
		                                for(i = 0; i <  rows.length; i++) {
		                                ds.remove(ds.getById(rows[i].id));
		                                }
		                                ds.insert(cindex,data.selections);
		                                sm.clearSelections();
		                             }
		                         }
		                      }
		                   }) 
		              	  dsSearchRelations.load();
		       }
		   }
		}
	});
	    
	
	var win_reorder_relations = new Ext.Window({
        title:'Riordina Relazioni',
        width       : 450,
        height      : 500,
        plain       : true,
        modal:true,
        closeAction:'destroy',
        layout : 'fit',
        items:[reorderRelationsGrid],
        buttons: [{
                    text: 'Salva ordine',
                    handler: function(){
        						var rows = reorderRelationsGrid.getStore().getRange();
        						var relationsToSort = '';
        						for (var x = 0 ; x < rows.length; x ++){
        							relationsToSort+='<%=records.getIdRecord()%>_'+rows[x].data.id+'_'+x+';';
            					}
            				
		                	    Ext.Ajax.request({
						          url : 'ajax/relation_manager.html?relationsToSort='+relationsToSort+'&action=reorder_relations&relation_type='+id_relation,
				                  method: 'POST',
				                  success: function ( result, request ) {
		                	    	  reloadTree();
		                			  openRecord('<%=records.getIdRecord()%>');
		                			  win_reorder_relations.destroy();
					              },
					                  failure: function ( result, request ) {
					                  fn_AKExt('Attenzione si è verificato un errore', 'Error');
					              }
							 });
                    }
                   }
      			  ]
    });
	win_reorder_relations.show();
}

</script>
<form name="relationsForm" action="" method="post">
		<div class="area">
			<div class="area_tab"><div class="area_tab_concept"><%=parsedTitle.get("notation")[0]%> <%=parsedTitle.get("label")[0]%></div><div class="area_tab_label">${skosTabtabsemanticrelations} <div class="x-tool x-tool-toggle x-tool-collapse-north macro_collapse" id="semantic_relations">&nbsp;</div></div></div>    
		    <div class="area_tab_bottom_line"></div>
		    <div class="semantic_relations" id="relations_BT"></div>
			<div class="semantic_relations" id="relations_NT"></div>
			<div class="semantic_relations" id="relations_RT"></div>	
			<div class="semantic_relations"  id="relations_IS"></div>		
		</div>
		<div class="area">
			<div class="area_tab"><div class="area_tab_label">${skosTabtabmappingrelations} <div class="x-tool x-tool-toggle x-tool-collapse-north macro_collapse" id="mapping_relations">&nbsp;</div></div></div>   
		    <div class="area_tab_bottom_line"></div>
		    <div  class="mapping_relations"  id="relations_BM"></div>
			<div  class="mapping_relations"  id="relations_NM"></div>
			<div  class="mapping_relations" id="relations_RM"></div>
			<div  class="mapping_relations"  id="relations_CM"></div>		
		</div>
		<div class="last_empty_line">&nbsp;</div>
</form>
<%
records.closeXMLReader();
}
%>