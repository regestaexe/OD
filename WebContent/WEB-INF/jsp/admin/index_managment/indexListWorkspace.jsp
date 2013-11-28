<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Archives"%>
<%@page import="com.openDams.index.configuration.IndexInfo"%>
var statusMap = {};
 
function getStatus(arg){
var idArchive = arg.replace(/^.*indexDetailsArchive/,'');
	if(!statusMap['a'+idArchive]){
		return "";
	}else{
		return '<img src="../img/'+statusMap['a'+idArchive]+'">';
	}
}

var panelIndexes = [{	title : 'Gestione Archivi'},
				<%if(request.getAttribute("archiveIndexList")!=null){ 
					List<Object> archiveIndexList = (List)request.getAttribute("archiveIndexList");
					Object[] archiveList = archiveIndexList.toArray();
					for(int i=0;i<archiveList.length;i++){
			 			Archives archive = (Archives)archiveList[i];%>  
						{
							xtype: 'panel',
						    margin:'5 0 0 0', 
						    title:"<span id='indexDetailsArchiveTit<%=archive.getIdArchive()%>'><span id='statusArchive<%=archive.getIdArchive()%>'></span><%=archive.getIdArchive()%> - <%=archive.getLabel()%></span>",
						    id: 'indexDetailsArchive<%=archive.getIdArchive()%>',
						    autoScroll: true,
				            autoLoad:{
				         		  url:'indexListWorkspace.html?action=index_details&idArchive=<%=archive.getIdArchive()%>',
				         		  scripts: true,
		     		           	  discardUrl: false,
		  		         		  nocache: true,
		  		         		  scope: this
				         	},
							listeners: {
						            activate: function(){
						            
						            
						          
<!-- 						            Ext.getCmp('adminWorkspaceContent').getLoader().load(); -->
						           
<!-- 						             Ext.Msg.alert('test','test'); -->
 					        
						             
						            }
						    },
				        	buttons: [{
							             	id:'refresh_<%=archive.getIdArchive()%>',
							 	        	disabled:false,
							                text: '<img src="../img/refresh2.gif" border="0"  alt="aggiorna" title="aggiorna">',
							                handler: function(){
							                		Ext.getCmp('indexDetailsArchive<%=archive.getIdArchive()%>').getLoader().load();
							                }
							        },{
						 	        	 id:'rebuild_index<%=archive.getIdArchive()%>',
						 	        	 disabled:false,
						 	        	 cls:'rebuild_index',
						                 text: 'Rigenera indici',
						                 handler: function(){
						                 					Ext.ComponentQuery.query('button[cls=rebuild_index]').forEach(function(entry){entry.setDisabled(true);});
						                 					Ext.ComponentQuery.query('button[cls=rebuild_title]').forEach(function(entry){entry.setDisabled(true);});
											        		Ext.getCmp('rebuild_index<%=archive.getIdArchive()%>').setDisabled(true);
											        		Ext.getCmp('rebuild_title<%=archive.getIdArchive()%>').setDisabled(true);
											        		Ext.getCmp('offline_archive<%=archive.getIdArchive()%>').setDisabled(true);
											        		Ext.getCmp('end_point_publisher<%=archive.getIdArchive()%>').setDisabled(true);
											        		Ext.getCmp('indexDetailsArchive<%=archive.getIdArchive()%>').setDisabled(true);										      
											        		$('#img_online<%=archive.getIdArchive()%>').attr('src','../img/offline.gif');
											        		$('#view_index_conf<%=archive.getIdArchive()%>').hide();
											    			$('#view_titles_conf<%=archive.getIdArchive()%>').hide();
											    			$('#xml_index<%=archive.getIdArchive()%>').hide();
											    			$('#xml_titles<%=archive.getIdArchive()%>').hide();										        		
											        		startProgressBar<%=archive.getIdArchive()%>();
											        		Ext.Ajax.request({
											        	           url: 'indexListWorkspace.html?action=rebuild_index&idArchive=<%=archive.getIdArchive()%>',
											        	           success: function(r) {
											        					Ext.TaskManager.start(eval('task'+<%=archive.getIdArchive()%>));
											        	           }
											        		});
						                 		   }
						             },{
						             	id:'rebuild_title<%=archive.getIdArchive()%>',
						 	        	disabled:false,
						                text: 'Rigenera titoli',
						                cls:'rebuild_title',
						                handler: function(){
						                					Ext.ComponentQuery.query('button[cls=rebuild_index]').forEach(function(entry){entry.setDisabled(true);});
						                 					Ext.ComponentQuery.query('button[cls=rebuild_title]').forEach(function(entry){entry.setDisabled(true);});
											        		Ext.getCmp('rebuild_index<%=archive.getIdArchive()%>').setDisabled(true);
											        		Ext.getCmp('rebuild_title<%=archive.getIdArchive()%>').setDisabled(true);
											        		Ext.getCmp('offline_archive<%=archive.getIdArchive()%>').setDisabled(true);
											        		Ext.getCmp('end_point_publisher<%=archive.getIdArchive()%>').setDisabled(true);
											        		Ext.getCmp('indexDetailsArchive<%=archive.getIdArchive()%>').setDisabled(true);										      
											        		$('#img_online<%=archive.getIdArchive()%>').attr('src','../img/offline.gif');
											        		$('#view_index_conf<%=archive.getIdArchive()%>').hide();
											    			$('#view_titles_conf<%=archive.getIdArchive()%>').hide();
											    			$('#xml_index<%=archive.getIdArchive()%>').hide();
											    			$('#xml_titles<%=archive.getIdArchive()%>').hide();
											        		startTitleProgressBar<%=archive.getIdArchive()%>();
											        		Ext.Ajax.request({
											        	           url: 'rebuildTitle.html?action=rebuild_title&idArchive=<%=archive.getIdArchive()%>',
											        	           success: function(r) {
											        					Ext.TaskManager.start(eval('taskT'+<%=archive.getIdArchive()%>));
											        	           }
											        		});
						                 }
						             },{
							             	id:'end_point_publisher<%=archive.getIdArchive()%>',
							 	        	disabled:false,
							 	        	cls:'end_point_publisher',
							                text: 'Pubblica su EndPoint',
							                text    : 'pubblica',
							                menu: []
							        },{
							             	id:'offline_archive<%=archive.getIdArchive()%>',
							 	        	disabled:false,
							                text: 'Metti offline',
							                handler: function(){
							                                 if(this.text=='Metti offline'){
							                                	Ext.Ajax.request({
											        	           url: 'archiveStatus.html?action=offline&idArchive=<%=archive.getIdArchive()%>',
											        	           success: function(r) {
							                                			Ext.getCmp('offline_archive<%=archive.getIdArchive()%>').setText('Metti online');
							                                			$('#img_online<%=archive.getIdArchive()%>').attr('src','../img/offline.gif');
											        	          		Ext.getCmp('indexDetailsArchive<%=archive.getIdArchive()%>').getLoader().load();
											        	           }
											        			});
							                                	 
									                         }else{
									                        	 Ext.Ajax.request({
											        	           url: 'archiveStatus.html?action=online&idArchive=<%=archive.getIdArchive()%>',
											        	           success: function(r) {
								                        		 		Ext.getCmp('offline_archive<%=archive.getIdArchive()%>').setText('Metti offline');
								                        		 		$('#img_online<%=archive.getIdArchive()%>').attr('src','../img/online.gif');
											        	           		Ext.getCmp('indexDetailsArchive<%=archive.getIdArchive()%>').getLoader().load();
											        	           }
											        			});
									                         }                                                         											        
							                 }
							             }]
						 }<%if(archiveList.length-1> i){%>,<%}%>
					<%}%>
				<%}%>	
		      	]




