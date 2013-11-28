<%@page import="org.openrdf.model.Value"%>
<%@page import="com.openDams.index.configuration.IndexInfo"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Set"%>
<%@page import="com.openDams.bean.Archives"%>
<%@page import="java.util.List"%>



<script type="text/javascript">

var pbar<%=request.getParameter("idArchive")%>;
var pbarT<%=request.getParameter("idArchive")%>;
var pbarP<%=request.getParameter("idArchive")%>; 



Ext.onReady(function(){
	
	<%if(request.getAttribute("indexInfo")!=null){
		IndexInfo indexInfo = (IndexInfo)request.getAttribute("indexInfo");
		Archives archives = (Archives)request.getAttribute("archives"); 
	%>
		<%if(archives.isOffline()){%>
		setIconStatusArchive<%=request.getParameter("idArchive")%>('gray',<%=request.getParameter("idArchive")%>);
		Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').setTitle('<span >ARCHIVIO <%=archives.getLabel()%><span style="padding: 0px 5px 0px 5px;"><img src="../img/offline.gif" id="img_online<%=request.getParameter("idArchive")%>"></span></span>');
		<%}else{%>
		checkStatusArchive<%=request.getParameter("idArchive")%>(<%=indexInfo.getNumDocs()%>,<%=indexInfo.getNumDocsArchive()%>,<%=request.getParameter("idArchive")%>);
		checkStatusPublisher<%=request.getParameter("idArchive")%>(<%=request.getParameter("idArchive")%>);
		Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').setTitle('<span >ARCHIVIO <%=archives.getLabel()%><span style="padding: 0px 5px 0px 5px;"><img src="../img/online.gif" id="img_online<%=request.getParameter("idArchive")%>"></span></span>');
		<%}%>
	<%}%>
	

	
	<%if(request.getAttribute("endPointList")!=null){%>
	Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').menu.removeAll(true);
    <%
	List<String> endPointList = (List<String>)  request.getAttribute("endPointList");
   	for (int i = 0; i < endPointList.size(); i++) {%>
   		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').menu.add(
   	       		new Ext.Action({
	             	 text: 'pubblica tutto l\'archivio su <strong><%=endPointList.get(i)%></strong>',
		             id:'all_archive_<%=endPointList.get(i)%>',
		             endPointId:'<%=endPointList.get(i)%>',
		             disabled:true,
		             iconCls: 'not_publisched',
		             handler: function(btn<%=request.getParameter("idArchive")%>){
		            	    Ext.ComponentQuery.query('button[cls=end_point_publisher]').forEach(function(entry){entry.setDisabled(true);});
		            	    Ext.getCmp('rebuild_index<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('rebuild_title<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('offline_archive<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').setDisabled(true);										      
			        		$('#img_online<%=request.getParameter("idArchive")%>').attr('src','../img/offline.gif');
			        		$("#view_index_conf<%=request.getParameter("idArchive")%>").hide();
			    			$("#view_titles_conf<%=request.getParameter("idArchive")%>").hide();
			    			$("#xml_index<%=request.getParameter("idArchive")%>").hide();
			    			$("#xml_titles<%=request.getParameter("idArchive")%>").hide();
			    		//	alert(btn<%=request.getParameter("idArchive")%>.text.indexOf('pubblica')!=-1 && btn<%=request.getParameter("idArchive")%>.text.indexOf(btn<%=request.getParameter("idArchive")%>.endPointId)!=-1 && btn<%=request.getParameter("idArchive")%>.text.indexOf('tutto')!=-1)
			    			
			    		if (btn<%=request.getParameter("idArchive")%>.text.indexOf('pubblica')!=-1 && btn<%=request.getParameter("idArchive")%>.text.indexOf(btn<%=request.getParameter("idArchive")%>.endPointId)!=-1 && btn<%=request.getParameter("idArchive")%>.text.indexOf('tutto')!=-1) {
			    			startEndPointProgressBar<%=request.getParameter("idArchive")%>();
				        		Ext.Ajax.request({
				        	           url: 'endPointPublisherWorkspace.html?action=publishEndPoint&idArchive='+<%=request.getParameter("idArchive")%>+'&endPointManager='+btn<%=request.getParameter("idArchive")%>.endPointId,
				        	           success: function(r) {
				        					Ext.TaskManager.start(eval('taskP'+<%=request.getParameter("idArchive")%>));
				        					btn<%=request.getParameter("idArchive")%>.setText('rimuovi tutto l\'archivio da <strong>'+btn<%=request.getParameter("idArchive")%>.endPointId+'</strong>');
				        					//????btn<%=request.getParameter("idArchive")%>.setIconClass('publisched');
				        	           }
				        		});
         				}else{
         					startEndPointProgressBar<%=request.getParameter("idArchive")%>();
				        		Ext.Ajax.request({
				        	           url: 'endPointPublisherWorkspace.html?action=archiveRemove&idArchive='+<%=request.getParameter("idArchive")%>+'&endPointManager='+btn<%=request.getParameter("idArchive")%>.endPointId,
				        	           success: function(r) {
				        					Ext.TaskManager.start(eval('taskP'+<%=request.getParameter("idArchive")%>));
				        					btn<%=request.getParameter("idArchive")%>.setText('pubblica tutto l\'archivio su <strong>'+btn<%=request.getParameter("idArchive")%>.endPointId+'</strong>');
				        					//????btn<%=request.getParameter("idArchive")%>.setIconClass('not_publisched');
				        	           }
				        		});
	                		}											        												        												        		
      				}
		         })
		);
   		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').menu.add(
   	       		new Ext.Action({
	             text: 'ripubblica i documenti su <strong><%=endPointList.get(i)%></strong>',
		             id:'republish_archive_<%=endPointList.get(i)%>',
		             endPointId:'<%=endPointList.get(i)%>',
		             disabled:true,
		             iconCls: 'republish',
		             handler: function(btn<%=request.getParameter("idArchive")%><%=request.getParameter("idArchive")%>){
		            	    Ext.ComponentQuery.query('button[cls=end_point_publisher]').forEach(function(entry){entry.setDisabled(true);});
		            	    Ext.getCmp('rebuild_index<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('rebuild_title<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('offline_archive<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('refresh_<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').setDisabled(true);											      
			        		$('#img_online<%=request.getParameter("idArchive")%>').attr('src','../img/offline.gif');
			        		$("#view_index_conf<%=request.getParameter("idArchive")%>").hide();
			    			$("#view_titles_conf<%=request.getParameter("idArchive")%>").hide();
			    			$("#xml_index<%=request.getParameter("idArchive")%>").hide();
			    			$("#xml_titles<%=request.getParameter("idArchive")%>").hide();
			    			startEndPointProgressBar<%=request.getParameter("idArchive")%>();
			        		Ext.Ajax.request({
			        	           url: 'endPointPublisherWorkspace.html?action=rePublishEndPoint&idArchive='+<%=request.getParameter("idArchive")%>+'&endPointManager='+btn<%=request.getParameter("idArchive")%><%=request.getParameter("idArchive")%>.endPointId,
			        	           success: function(r) {
			        					Ext.TaskManager.start(eval('taskP'+<%=request.getParameter("idArchive")%>));
			        					//btn<%=request.getParameter("idArchive")%>.setText('rimuovi tutto l\'archivio da '+btn<%=request.getParameter("idArchive")%>.endPointId);
			    						//btn<%=request.getParameter("idArchive")%>.setIconClass('publisched');
			        	           }
			        		});
										        												        												        		
      				}
		         })
		);
   		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').menu.add(
   	       		new Ext.Action({
	             text: 'pubblica i non pubblicati su <strong><%=endPointList.get(i)%></strong>',
		             id:'notPublished_records_<%=endPointList.get(i)%>',
		             endPointId:'<%=endPointList.get(i)%>',
		             disabled:true,
		             iconCls: 'notPublished',
		             handler: function(btn<%=request.getParameter("idArchive")%>){
		            	    Ext.ComponentQuery.query('button[cls=end_point_publisher]').forEach(function(entry){entry.setDisabled(true);});
		            	    Ext.getCmp('rebuild_index<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('rebuild_title<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('offline_archive<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('refresh_<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').setDisabled(true);											      
			        		$('#img_online<%=request.getParameter("idArchive")%>').attr('src','../img/offline.gif');
			        		$("#view_index_conf<%=request.getParameter("idArchive")%>").hide();
			    			$("#view_titles_conf<%=request.getParameter("idArchive")%>").hide();
			    			$("#xml_index<%=request.getParameter("idArchive")%>").hide();
			    			$("#xml_titles<%=request.getParameter("idArchive")%>").hide();
			    			startEndPointProgressBar<%=request.getParameter("idArchive")%>();
			        		Ext.Ajax.request({
			        	           url: 'endPointPublisherWorkspace.html?action=notPublished&idArchive='+<%=request.getParameter("idArchive")%>+'&endPointManager='+btn<%=request.getParameter("idArchive")%>.endPointId,
			        	           success: function(r) {
			        					Ext.TaskManager.start(eval('taskP'+<%=request.getParameter("idArchive")%>));
			        	           }
			        		});
										        												        												        		
      				}
		         })
		);
   		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').menu.add(
   	       		new Ext.Action({
	             text: 'ripubblica i modificati su <strong><%=endPointList.get(i)%></strong>',
		             id:'records_modified_<%=endPointList.get(i)%>',
		             endPointId:'<%=endPointList.get(i)%>',
		             disabled:true,
		             iconCls: 'modified',
		             handler: function(btn<%=request.getParameter("idArchive")%>){
		            	    Ext.ComponentQuery.query('button[cls=end_point_publisher]').forEach(function(entry){entry.setDisabled(true);});
		            	    Ext.getCmp('rebuild_index<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('rebuild_title<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('offline_archive<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('refresh_<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').setDisabled(true);											      
			        		$('#img_online<%=request.getParameter("idArchive")%>').attr('src','../img/offline.gif');
			        		$("#view_index_conf<%=request.getParameter("idArchive")%>").hide();
			    			$("#view_titles_conf<%=request.getParameter("idArchive")%>").hide();
			    			$("#xml_index<%=request.getParameter("idArchive")%>").hide();
			    			$("#xml_titles<%=request.getParameter("idArchive")%>").hide();
			    			startEndPointProgressBar<%=request.getParameter("idArchive")%>();
			        		Ext.Ajax.request({
			        	           url: 'endPointPublisherWorkspace.html?action=modified&idArchive='+<%=request.getParameter("idArchive")%>+'&endPointManager='+btn<%=request.getParameter("idArchive")%>.endPointId,
			        	           success: function(r) {
			        					Ext.TaskManager.start(eval('taskP'+<%=request.getParameter("idArchive")%>));
			        	           }
			        		});
										        												        												        		
      				}
		         })
		);
   		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').menu.add(
   	       		new Ext.Action({
	             text: 'pubblica gli errori su <strong><%=endPointList.get(i)%></strong>',
		             id:'publish_errors_<%=endPointList.get(i)%>',
		             endPointId:'<%=endPointList.get(i)%>',
		             disabled:true,
		             iconCls: 'errors',
		             handler: function(btn<%=request.getParameter("idArchive")%>){
		            	    Ext.ComponentQuery.query('button[cls=end_point_publisher]').forEach(function(entry){entry.setDisabled(true);});
		            	    Ext.getCmp('rebuild_index<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('rebuild_title<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('offline_archive<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('refresh_<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').setDisabled(true);
			        		Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').setDisabled(true);											      
			        		$('#img_online<%=request.getParameter("idArchive")%>').attr('src','../img/offline.gif');
			        		$("#view_index_conf<%=request.getParameter("idArchive")%>").hide();
			    			$("#view_titles_conf<%=request.getParameter("idArchive")%>").hide();
			    			$("#xml_index<%=request.getParameter("idArchive")%>").hide();
			    			$("#xml_titles<%=request.getParameter("idArchive")%>").hide();
			    			startEndPointProgressBar<%=request.getParameter("idArchive")%>();
			        		Ext.Ajax.request({
			        	           url: 'endPointPublisherWorkspace.html?action=errors&idArchive='+<%=request.getParameter("idArchive")%>+'&endPointManager='+btn<%=request.getParameter("idArchive")%>.endPointId,
			        	           success: function(r) {
			        					Ext.TaskManager.start(eval('taskP'+<%=request.getParameter("idArchive")%>));
			        	           }
			        		});
										        												        												        		
      				}
		         })
		);
   	<%}
	 }else{%>
	 Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').menu.removeAll(true);
	<%}%>
	pbar<%=request.getParameter("idArchive")%> = new Ext.ProgressBar({
	    id:'pbar<%=request.getParameter("idArchive")%>',
	    <%if(request.getParameter("operation")!=null && request.getParameter("operation").equals("COMPLETED")){%>
	    text:'Operazione completata',
	    <%}else if(request.getParameter("operation")!=null && request.getParameter("operation").equals("FAILED")){%>
	    text:'Operazione fallita',
	    <%}else{%>
	    text:'',
	    <%}%> 
	    width:500,
	    renderTo:'p3<%=request.getParameter("idArchive")%>'
	});
	pbarT<%=request.getParameter("idArchive")%> = new Ext.ProgressBar({
	    id:'pbarT<%=request.getParameter("idArchive")%>',
	    <%if(request.getParameter("tOperation")!=null && request.getParameter("tOperation").equals("COMPLETED")){%>
	    text:'Operazione completata',
	    <%}else if(request.getParameter("tOperation")!=null && request.getParameter("tOperation").equals("FAILED")){%>
	    text:'Operazione fallita',
	    <%}else{%>
	    text:'',
	    <%}%>    
	    width:500,
	    renderTo:'p4<%=request.getParameter("idArchive")%>'
	});
	pbarP<%=request.getParameter("idArchive")%> = new Ext.ProgressBar({
	    id:'pbarT<%=request.getParameter("idArchive")%>',
	    <%if(request.getParameter("tOperation")!=null && request.getParameter("tOperation").equals("COMPLETED")){%>
	    text:'Operazione completata',
	    <%}else if(request.getParameter("tOperation")!=null && request.getParameter("tOperation").equals("FAILED")){%>
	    text:'Operazione fallita',
	    <%}else{%>
	    text:'',
	    <%}%>    
	    width:500,
	    renderTo:'p5<%=request.getParameter("idArchive")%>'
	});	
	<%if(request.getAttribute("STATUS")!=null){
		String STATUS = (String)request.getAttribute("STATUS");
		if(!STATUS.equals("STARTED")){%>
			Ext.getCmp('rebuild_index<%=request.getParameter("idArchive")%>').setDisabled(false);
			Ext.getCmp('rebuild_title<%=request.getParameter("idArchive")%>').setDisabled(false);
			Ext.getCmp('offline_archive<%=request.getParameter("idArchive")%>').setDisabled(false);	
			Ext.getCmp('refresh_<%=request.getParameter("idArchive")%>').setDisabled(false);
			Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').setDisabled(false);	
			Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').menu.items.each(function(item){
				if(item.id.indexOf('all_archive_')!=-1){
					Ext.Ajax.request({
						url : '../endPointManager.html',
						method : 'POST',
						nocache : true,
						params : {
							action : 'checkPublishedArchive',
							idArchive : '<%=request.getParameter("idArchive")%>',
							endPointManager : item.endPointId
						},
						success : function(result, request) {
							var published = Ext.util.Format.trim(result.responseText);
							if (published == 'true' || published == true) {
								item.setText('rimuovi tutto l\'archivio da <strong>'+item.endPointId+'</strong>');
								item.setDisabled(false);
							} else {
								item.setText('pubblica tutto l\'archivio su <strong>'+item.endPointId+'</strong>');
								item.setDisabled(false);
							}
							Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').setDisabled(false);
						},
						failure : function(result, request) {
							item.setDisabled(true);
						}
					});
				}else{
					Ext.Ajax.request({
						url : '../endPointManager.html',
						method : 'POST',
						nocache : true,
						params : {
							action : 'checkPublishedArchive',
							idArchive : '<%=request.getParameter("idArchive")%>',
							endPointManager : item.endPointId
						},
						success : function(result, request) {
							item.setDisabled(false);
							Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').setDisabled(false);
						},
						failure : function(result, request) {
							item.setDisabled(true);
						}
					});
				}
			});			
			<%if(request.getAttribute("archives")!=null){
				Archives archives = (Archives)request.getAttribute("archives");
				if(archives.isOffline()){%>
					Ext.getCmp('offline_archive<%=request.getParameter("idArchive")%>').setText('Metti online');
				<%}else{%>
					Ext.getCmp('offline_archive<%=request.getParameter("idArchive")%>').setText('Metti offline');
				<%}
			}%>
			$("#view_index_conf<%=request.getParameter("idArchive")%>").show();
			$("#view_titles_conf<%=request.getParameter("idArchive")%>").show();
		<%}else{%>
			Ext.Ajax.request({
	           url: 'indexListWorkspace.html?action=check_status&idArchive=<%=request.getParameter("idArchive")%>',
	           success: function(r) {
					if(r.responseText=='STARTED'){
						setIconStatusArchive<%=request.getParameter("idArchive")%>('loader',<%=request.getParameter("idArchive")%>);
						startProgressBar<%=request.getParameter("idArchive")%>();
					}
	           }
			});
			Ext.Ajax.request({
	           url: 'rebuildTitle.html?action=check_status&idArchive=<%=request.getParameter("idArchive")%>',
	           success: function(r) {
	        	    //alert("titoli = "+r.responseText);
					if(r.responseText=='STARTED'){
						setIconStatusArchive<%=request.getParameter("idArchive")%>('loader',<%=request.getParameter("idArchive")%>);
						startTitleProgressBar<%=request.getParameter("idArchive")%>();
					}
	           }
			});
			Ext.Ajax.request({
		           url: 'endPointPublisherWorkspace.html?action=check_status&idArchive=<%=request.getParameter("idArchive")%>',
		           success: function(r) {
		        	    //alert("titoli = "+r.responseText);
						if(r.responseText=='STARTED'){
							setIconStatusArchive<%=request.getParameter("idArchive")%>('loader',<%=request.getParameter("idArchive")%>);
							startTitleProgressBar<%=request.getParameter("idArchive")%>();
						}
		           }
				});					
			Ext.getCmp('rebuild_index<%=request.getParameter("idArchive")%>').setDisabled(true);
			Ext.getCmp('refresh_<%=request.getParameter("idArchive")%>').setDisabled(true);
			Ext.getCmp('rebuild_title<%=request.getParameter("idArchive")%>').setDisabled(true);
			Ext.getCmp('offline_archive<%=request.getParameter("idArchive")%>').setDisabled(true);
			Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').setDisabled(true); 
			Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').setDisabled(true);	
			$("#view_index_conf<%=request.getParameter("idArchive")%>").hide();
			$("#view_titles_conf<%=request.getParameter("idArchive")%>").hide();
			$("#xml_index<%=request.getParameter("idArchive")%>").hide();
			$("#xml_titles<%=request.getParameter("idArchive")%>").hide();
		<%}
	}%>
	
	
	 $("#index_link<%=request.getParameter("idArchive")%>").click(function(){          
	    if(!$("#operationsList<%=request.getParameter("idArchive")%>").is(":visible")){
	    	$("#operationsList<%=request.getParameter("idArchive")%>").show(1000);
	    	$("#operationsList<%=request.getParameter("idArchive")%>").load("indexListWorkspace.html?action=history&idArchive=<%=request.getParameter("idArchive")%>",function(){
	
	        });
	    	$(this).html("nascondi storico operazioni");
	    	
	    }else{
	    	$("#operationsList<%=request.getParameter("idArchive")%>").hide(1000);
	    	$(this).html("visualizza storico operazioni");	
	    }
	 });
	 $("#view_index_conf<%=request.getParameter("idArchive")%>").click(function(){          
	     if(!$("#xml_index<%=request.getParameter("idArchive")%>").is(":visible")){
	     	$("#xml_index<%=request.getParameter("idArchive")%>").show(1000);
	     	$(this).html("nascondi configurazione indice");
	     	
	     }else{
	     	$("#xml_index<%=request.getParameter("idArchive")%>").hide(1000);
	     	$(this).html("visualizza configurazione indice");	
	     }
	  });
	 $("#view_titles_conf<%=request.getParameter("idArchive")%>").click(function(){          
	     if(!$("#xml_titles<%=request.getParameter("idArchive")%>").is(":visible")){
	     	$("#xml_titles<%=request.getParameter("idArchive")%>").show(1000);
	     	$(this).html("nascondi configurazione titoli");
	     	
	     }else{
	     	$("#xml_titles<%=request.getParameter("idArchive")%>").hide(1000);
	     	$(this).html("visualizza configurazione titoli");	
	     }
	  });
	 $("#saveIndexConfiguration<%=request.getParameter("idArchive")%>").click(function(){
	        var val = escape(editor.getValue());
			$.post("indexListWorkspace.html",
					  { xml: val,action: 'save_xml',xml_type:'index',idArchive:'<%=request.getParameter("idArchive")%>'},
					  function(data){
						  Ext.Msg.alert('Attenzione','Configurazione salvata correttamente!');
						  $("#xml_index<%=request.getParameter("idArchive")%>").hide(1000);
					  }
					);
	 });
	 $("#saveTitlesConfiguration<%=request.getParameter("idArchive")%>").click(function(){ 
	    	 var val = escape(editor2.getValue());  
	    	 $.post("indexListWorkspace.html",
	    			 { xml: val,action: 'save_xml',xml_type:'title',idArchive:'<%=request.getParameter("idArchive")%>'},
					  function(data){
						  Ext.Msg.alert('Attenzione','Configurazione salvata correttamente!');
						  $("#xml_titles<%=request.getParameter("idArchive")%>").hide(1000);
					  }
					);
		 });
	 
	
	CodeMirror.fromTextArea(document.getElementById("code<%=request.getParameter("idArchive")%>"),{
	  mode: "application/xml",
	  lineNumbers: true
	});
	$("#xml_index<%=request.getParameter("idArchive")%>").hide();
	
	CodeMirror.fromTextArea(document.getElementById("code2<%=request.getParameter("idArchive")%>"), {
	  mode: "application/xml",
	  lineNumbers: true
	});
	$("#xml_titles<%=request.getParameter("idArchive")%>").hide();
	
});

var task<%=request.getParameter("idArchive")%> = {
		
    run: function(){
    	
        Ext.Ajax.request({
        	
           url: 'indexListWorkspace.html?action=check_status&idArchive=<%=request.getParameter("idArchive")%>',
           success: function(r) {
				if(r.responseText=='FAILED'){
					$('#statusArchive<%=request.getParameter("idArchive")%>').html('');
					$('#actionId<%=request.getParameter("idArchive")%>').remove();
					setIconStatusArchive<%=request.getParameter("idArchive")%>('yellow',<%=request.getParameter("idArchive")%>);
					Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').getLoader().load();
					Ext.TaskManager.stop(task<%=request.getParameter("idArchive")%>);
					pbar<%=request.getParameter("idArchive")%>.reset();
					pbar<%=request.getParameter("idArchive")%>.updateText("Operazione fallita");
					pbar<%=request.getParameter("idArchive")%>.reset();
					$("#indexDetailsArchives<%=request.getParameter("idArchive")%>").load("indexListWorkspace.html?action=index_details&idArchive=<%=request.getParameter("idArchive")%>&operation=FAILED");
				}else if(r.responseText=='COMPLETED'){
					
					$('#statusArchive<%=request.getParameter("idArchive")%>').html('');
					$('#actionId<%=request.getParameter("idArchive")%>').remove();
					setIconStatusArchive<%=request.getParameter("idArchive")%>('green',<%=request.getParameter("idArchive")%>);
	        		$('#img_online<%=request.getParameter("idArchive")%>').attr('src','../img/online.gif');
					Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').getLoader().load();
					<%if(request.getAttribute("indexInfo")!=null){
						IndexInfo indexInfo = (IndexInfo)request.getAttribute("indexInfo");%>
						checkStatusArchive<%=request.getParameter("idArchive")%>(<%=indexInfo.getNumDocs()%>,<%=indexInfo.getNumDocsArchive()%>,<%=request.getParameter("idArchive")%>);
						checkStatusPublisher<%=request.getParameter("idArchive")%>(<%=request.getParameter("idArchive")%>);
					<%}%>
					
					Ext.TaskManager.stop(task<%=request.getParameter("idArchive")%>);				
					pbar<%=request.getParameter("idArchive")%>.updateText("Operazione completata");
					pbar<%=request.getParameter("idArchive")%>.reset();
					
					Ext.ComponentQuery.query('button[cls=rebuild_index]').forEach(function(entry){entry.setDisabled(false);});
 					Ext.ComponentQuery.query('button[cls=rebuild_title]').forEach(function(entry){entry.setDisabled(false);});
					Ext.getCmp('rebuild_index<%=request.getParameter("idArchive")%>').setDisabled(false);
					Ext.getCmp('refresh_<%=request.getParameter("idArchive")%>').setDisabled(false);
					Ext.getCmp('rebuild_title<%=request.getParameter("idArchive")%>').setDisabled(false);
					Ext.getCmp('offline_archive<%=request.getParameter("idArchive")%>').setDisabled(false);
		    		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').setDisabled(false);
		    		Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').setDisabled(false);	
					$("#view_index_conf<%=request.getParameter("idArchive")%>").show();
					$("#view_titles_conf<%=request.getParameter("idArchive")%>").show();
					$("#indexDetailsArchives<%=request.getParameter("idArchive")%>").load("indexListWorkspace.html?action=index_details&idArchive=<%=request.getParameter("idArchive")%>&operation=COMPLETED");
				}
           }
		});
    },
    interval: 2000
}
var taskT<%=request.getParameter("idArchive")%> = {
	    run: function(){
	        Ext.Ajax.request({
	           url: 'rebuildTitle.html?action=check_status&idArchive=<%=request.getParameter("idArchive")%>',
	           success: function(r) {
					if(r.responseText=='FAILED'){
						
						$('#statusArchive<%=request.getParameter("idArchive")%>').html('');
						$('#actionId<%=request.getParameter("idArchive")%>').remove();
						Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').getLoader().load()
						setIconStatusArchive<%=request.getParameter("idArchive")%>('yellow',<%=request.getParameter("idArchive")%>);
						
						<%if(request.getAttribute("indexInfo")!=null){
							IndexInfo indexInfo = (IndexInfo)request.getAttribute("indexInfo");%>
							checkStatusArchive<%=request.getParameter("idArchive")%>(<%=indexInfo.getNumDocs()%>,<%=indexInfo.getNumDocsArchive()%>,<%=request.getParameter("idArchive")%>);
							checkStatusPublisher<%=request.getParameter("idArchive")%>(<%=request.getParameter("idArchive")%>);
						<%}%>

						Ext.TaskManager.stop(taskT<%=request.getParameter("idArchive")%>);
						pbarT<%=request.getParameter("idArchive")%>.reset();
						pbarT<%=request.getParameter("idArchive")%>.updateText("Operazione fallita");
						pbarT<%=request.getParameter("idArchive")%>.reset();
						$("#indexDetailsArchives<%=request.getParameter("idArchive")%>").load("indexListWorkspace.html?action=index_details&idArchive=<%=request.getParameter("idArchive")%>&tOperation=FAILED");
					
					}else if(r.responseText=='COMPLETED'){
						
						$('#statusArchive<%=request.getParameter("idArchive")%>').html('');
						$('#actionId<%=request.getParameter("idArchive")%>').remove();
						$('#img_online<%=request.getParameter("idArchive")%>').attr('src','../img/online.gif');
						setIconStatusArchive<%=request.getParameter("idArchive")%>('green',<%=request.getParameter("idArchive")%>);
						Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').getLoader().load()
						<%if(request.getAttribute("indexInfo")!=null){
							IndexInfo indexInfo = (IndexInfo)request.getAttribute("indexInfo");%>
							checkStatusArchive<%=request.getParameter("idArchive")%>(<%=indexInfo.getNumDocs()%>,<%=indexInfo.getNumDocsArchive()%>,<%=request.getParameter("idArchive")%>);
							checkStatusPublisher<%=request.getParameter("idArchive")%>(<%=request.getParameter("idArchive")%>);
						<%}%>

						Ext.TaskManager.stop(taskT<%=request.getParameter("idArchive")%>);				
						pbarT<%=request.getParameter("idArchive")%>.updateText("Operazione completata");
						pbarT<%=request.getParameter("idArchive")%>.reset();
						Ext.ComponentQuery.query('button[cls=rebuild_index]').forEach(function(entry){entry.setDisabled(false);});
     					Ext.ComponentQuery.query('button[cls=rebuild_title]').forEach(function(entry){entry.setDisabled(false);});
						Ext.getCmp('rebuild_index<%=request.getParameter("idArchive")%>').setDisabled(false);
						Ext.getCmp('refresh_<%=request.getParameter("idArchive")%>').setDisabled(false);
						Ext.getCmp('rebuild_title<%=request.getParameter("idArchive")%>').setDisabled(false);
						Ext.getCmp('offline_archive<%=request.getParameter("idArchive")%>').setDisabled(false);
			    		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').setDisabled(false);
			    		Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').setDisabled(false);	
						$("#view_index_conf<%=request.getParameter("idArchive")%>").show();
						$("#view_titles_conf<%=request.getParameter("idArchive")%>").show();
						$("#indexDetailsArchives<%=request.getParameter("idArchive")%>").load("indexListWorkspace.html?action=index_details&idArchive=<%=request.getParameter("idArchive")%>&tOperation=COMPLETED");
					}
	           }
			});
	    },
	    interval: 2000
	}
	
var taskP<%=request.getParameter("idArchive")%> = {
	    run: function(){
	        Ext.Ajax.request({
	           url: 'endPointPublisherWorkspace.html?action=check_status&idArchive=<%=request.getParameter("idArchive")%>',
	           success: function(r) {
					if(r.responseText=='FAILED'){
						
						$('#statusArchive<%=request.getParameter("idArchive")%>').html('');
						$('#actionId<%=request.getParameter("idArchive")%>').remove();
						setIconStatusArchive<%=request.getParameter("idArchive")%>('green',<%=request.getParameter("idArchive")%>);
						Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').getLoader().load();
						checkStatusPublisher<%=request.getParameter("idArchive")%>(<%=request.getParameter("idArchive")%>);

						Ext.TaskManager.stop(taskP<%=request.getParameter("idArchive")%>);
						pbarP<%=request.getParameter("idArchive")%>.reset();
						pbarP<%=request.getParameter("idArchive")%>.updateText("Operazione fallita");
						pbarP<%=request.getParameter("idArchive")%>.reset();
						$("#indexDetailsArchives<%=request.getParameter("idArchive")%>").load("indexListWorkspace.html?action=index_details&idArchive=<%=request.getParameter("idArchive")%>&tOperation=FAILED");
					
					}else if(r.responseText=='COMPLETED'){
						
						$('#statusArchive<%=request.getParameter("idArchive")%>').html('');
						$('#actionId<%=request.getParameter("idArchive")%>').remove();
						$('#img_online<%=request.getParameter("idArchive")%>').attr('src','../img/online.gif');
						Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').getLoader().load();
						checkStatusPublisher<%=request.getParameter("idArchive")%>(<%=request.getParameter("idArchive")%>);
						Ext.TaskManager.stop(taskP<%=request.getParameter("idArchive")%>);				
						pbarP<%=request.getParameter("idArchive")%>.updateText("Operazione completata");
						pbarP<%=request.getParameter("idArchive")%>.reset();

						Ext.ComponentQuery.query('button[cls=end_point_publisher]').forEach(function(entry){entry.setDisabled(false);});
						Ext.getCmp('rebuild_index<%=request.getParameter("idArchive")%>').setDisabled(false);
						Ext.getCmp('refresh_<%=request.getParameter("idArchive")%>').setDisabled(false);
						Ext.getCmp('rebuild_title<%=request.getParameter("idArchive")%>').setDisabled(false);
						Ext.getCmp('offline_archive<%=request.getParameter("idArchive")%>').setDisabled(false);
			    		Ext.getCmp('end_point_publisher<%=request.getParameter("idArchive")%>').setDisabled(false);
			    		Ext.getCmp('indexDetailsArchive<%=request.getParameter("idArchive")%>').setDisabled(false);	
						$("#view_index_conf<%=request.getParameter("idArchive")%>").show();
						$("#view_titles_conf<%=request.getParameter("idArchive")%>").show();
						$("#indexDetailsArchives<%=request.getParameter("idArchive")%>").load("indexListWorkspace.html?action=index_details&idArchive=<%=request.getParameter("idArchive")%>&tOperation=COMPLETED");
					}
	           }
			});
	    },
	    interval: 2000
	}
function startProgressBar<%=request.getParameter("idArchive")%>(){
	
	setIconStatusArchive<%=request.getParameter("idArchive")%>('loader',<%=request.getParameter("idArchive")%>);	

	$('#indexDetailsArchive<%=request.getParameter("idArchive")%>_header_hd-textEl').append('<span style="padding: 0px 5px 0px 5px;" id="actionId<%=request.getParameter("idArchive")%>"></span>');

	$('#actionId<%=request.getParameter("idArchive")%>').html('indicizzazione in corso...');

	pbar<%=request.getParameter("idArchive")%>.wait({
        interval:200,
        increment:15,
        fn:function(){
    		Ext.get('rebuild_index<%=request.getParameter("idArchive")%>').dom.disabled = false;
    		Ext.get('rebuild_title<%=request.getParameter("idArchive")%>').dom.disabled = false;
        }
    });
}
function startTitleProgressBar<%=request.getParameter("idArchive")%>(){
	
	setIconStatusArchive<%=request.getParameter("idArchive")%>('loader',<%=request.getParameter("idArchive")%>);
	$('#indexDetailsArchive<%=request.getParameter("idArchive")%>_header_hd-textEl').append('<span style="padding: 0px 5px 0px 5px;" id="actionId<%=request.getParameter("idArchive")%>"></span>');
	$('#actionId<%=request.getParameter("idArchive")%>').html('titolazione in corso...');

	pbarT<%=request.getParameter("idArchive")%>.wait({
        interval:200,
        increment:15,
        fn:function(){
    		Ext.get('rebuild_index<%=request.getParameter("idArchive")%>').dom.disabled = false;
    		Ext.get('rebuild_title<%=request.getParameter("idArchive")%>').dom.disabled = false;
        }
    });
}

function startEndPointProgressBar<%=request.getParameter("idArchive")%>(){
	
	setIconStatusArchive<%=request.getParameter("idArchive")%>('loader',<%=request.getParameter("idArchive")%>);
	$('#indexDetailsArchive<%=request.getParameter("idArchive")%>_header_hd-textEl').append('<span style="padding: 0px 5px 0px 5px;" id="actionId<%=request.getParameter("idArchive")%>"></span>');
	$('#actionId<%=request.getParameter("idArchive")%>').html('pubblicazione in corso...');

	pbarP<%=request.getParameter("idArchive")%>.wait({
        interval:200,
        increment:15,
        fn:function(){
    		Ext.get('rebuild_index<%=request.getParameter("idArchive")%>').dom.disabled = false;
    		Ext.get('rebuild_title<%=request.getParameter("idArchive")%>').dom.disabled = false;
        }
    });
	
}

function setIconStatusArchive<%=request.getParameter("idArchive")%>(arg,idArchive){
	
	if(arg=="loader"){
		$('#statusArchive'+idArchive+'').parent().parent().parent().children("img:first").remove();
		$('#statusArchive'+idArchive+'').parent().parent().parent().prepend('<img id="Ajaxloader" src="../img/ajax-loader.gif" />');
		statusMap['a'+idArchive]='ajax-loader.gif';
	}
	else{
		$('#statusArchive'+idArchive+'').parent().parent().parent().children("img:first").remove();
		$('#statusArchive'+idArchive+'').parent().parent().parent().prepend('<img src="../img/'+arg+'.png">');
		statusMap['a'+idArchive]=arg+'.png';
	}

}


function checkStatusArchive<%=request.getParameter("idArchive")%>(indici,record,idArchive){
	
	if(indici == 0 || record == 0){
		setIconStatusArchive<%=request.getParameter("idArchive")%>('red',idArchive);
		$('#indexStatus'+idArchive+'').html('<img src="../img/red.png">');
	}
	
	if(indici != 0 && record != 0){
		if(indici==record){
			setIconStatusArchive<%=request.getParameter("idArchive")%>('green',idArchive);
			$('#indexStatus'+idArchive+'').html('<img src="../img/green.png">');
		}
		else{
			setIconStatusArchive<%=request.getParameter("idArchive")%>('yellow',idArchive);
			$('#indexStatus'+idArchive+'').html('<img src="../img/yellow.png">');
		}
	}
}

function checkStatusPublisher<%=request.getParameter("idArchive")%>(idArchive){
	
	var nDoc=$('#numDocArchive'+idArchive+'').text();
	var nIndex=$('#numDocIndex'+idArchive+'').text();
	var rPubblicati=$('#recordPubblicati'+idArchive+'').text();
	var rNonPubblicati=$('#recordNotPublished'+idArchive+'').text();
	var rModificati=$('#recordModified'+idArchive+'').text();
	var errors=$('#publishError'+idArchive+'').text();
	
	if(rPubblicati != null && rPubblicati != ""){
		
		if(rPubblicati == 0){
			$('#endPointStatusPublished'+idArchive+'').html('<img src="../img/red.png">');
		}else{
			if(nDoc == rPubblicati){
				$('#endPointStatusPublished'+idArchive+'').html('<img src="../img/green.png">');
			}else{
				$('#endPointStatusPublished'+idArchive+'').html('<img src="../img/yellow.png">');
			}
			
			if(errors != 0 && errors!=null){
				$('#endPointStatusErrors'+idArchive+'').html('<img src="../img/red.png">');
			}else{
				$('#endPointStatusErrors'+idArchive+'').html('<img src="../img/green.png">');
			}
			
			if(rNonPubblicati !=0 && rNonPubblicati!=null){
				$('#endPointStatusNotPublished'+idArchive+'').html('<img src="../img/yellow.png">');
			}else{
				$('#endPointStatusNotPublished'+idArchive+'').html('<img src="../img/green.png">');
			} 
			
			if(rModificati !=0 && rModificati!=null){
				$('#endPointStatusModified'+idArchive+'').html('<img src="../img/yellow.png">');
			}else{
				$('#endPointStatusModified'+idArchive+'').html('<img src="../img/green.png">');
			}
		}
		
		if(rPubblicati==0 || errors != 0){
			setIconStatusArchive<%=request.getParameter("idArchive")%>('red',idArchive);
		}else if(nDoc == nIndex && nDoc == rPubblicati && rModificati==0  && rNonPubblicati ==0  && errors == 0){
			setIconStatusArchive<%=request.getParameter("idArchive")%>('green',idArchive);
		}else{
			setIconStatusArchive<%=request.getParameter("idArchive")%>('yellow',idArchive);
		}
	}
}

</script>




<%if(request.getAttribute("indexInfo")!=null){
	IndexInfo indexInfo = (IndexInfo)request.getAttribute("indexInfo");	
	if(request.getAttribute("endPointInfo")!=null){
%>
<div style="margin-left:10px; margin-top:10px;">
	<hr>
	<span style="font-weight:bold;  font-variant: small-caps;">Info Archivio</span>
	<hr>
	<br />
	<table id="tableArchive">
		<tr>
			<td><span style="font-weight:bold;">Nome indice : </span></td><td><span><%=indexInfo.getIndex_name()%></span></td>
		</tr>
		<tr>
			<td><span style="font-weight:bold;">Versione indice : </span></td><td><span><%=indexInfo.getIndex_version()%></span></td>
		</tr>
		<tr>
			<td><span style="font-weight:bold;">Ottimizzato : </span></td><td><span><%=indexInfo.isOptimized()%></span></td>
		</tr>
		<tr>
			<td><span style="font-weight:bold;">Numero documenti indice: </span></td><td><span id="numDocIndex<%=request.getParameter("idArchive")%>"><%=indexInfo.getNumDocs()%></span></td><td><span id="indexStatus<%=request.getParameter("idArchive")%>"></span></td>
		</tr>
		<tr>
		<td><span style="font-weight:bold;">Numero documenti archivio: </span></td><td><span id="numDocArchive<%=request.getParameter("idArchive")%>"><%=indexInfo.getNumDocsArchive()%></span></td>
		</tr>
		<tr>
			<td><span style="font-weight:bold;">Numero documenti nel cestino: </span></td><td><span><%=indexInfo.getNumDocsBasket()%></span></td>
		</tr>
		<tr>
		<td>
			<span style="font-weight:bold;">Numero documenti totali: </span></td><td><span><%=indexInfo.getNumDocsBasket()+indexInfo.getNumDocsArchive()%>(<%=indexInfo.getNumDocsArchive()%>+<%=indexInfo.getNumDocsBasket()%>)</span></td>
		</tr>
		<tr>
			<td><span style="font-weight:bold;">Data ultima modifica : </span></td><td><span><%=indexInfo.getLastModifyDate()%></span></td>
		</tr>
	</table>
	<br />
	<br />
	<hr>
	<span style="font-weight:bold; font-variant: small-caps;">Info Endpoints</span>
	<hr>
	<br />
	
	<%  HashMap<String, HashMap<String, String>> endPointInfo =  (HashMap<String, HashMap<String, String>> ) request.getAttribute("endPointInfo");
		Set<String> endPointList = endPointInfo.keySet();
		
   		for (String chiave : endPointList){
   			
   		 HashMap<String, String> eMap = endPointInfo.get(chiave);
   	%>
   		
		<table id="tableEndPoint<%=request.getParameter("idArchive")%>_<%=chiave%>">
			<tr><td><span style="font-weight:bold;">EndPoint : </span></td><td><span><%=chiave%> --&gt; <a target="_blank"  href="<%=eMap.get("endpointURL")%>"><%=eMap.get("endpointURL")%></a></span></td></tr>
			<%=eMap.get("grafi")%>
			<tr>
			<td><span style="font-weight:bold;">Pubblicati : </span></td><td><span id="recordPubblicati<%=request.getParameter("idArchive")%>"><%=eMap.get("pubblicati") %></span></td><td><span id="endPointStatusPublished<%=request.getParameter("idArchive")%>"></span></td>
			
			</tr><tr>
			<td><span style="font-weight:bold;">Non pubblicati : </span></td><td><span id="recordNotPublished<%=request.getParameter("idArchive")%>"><%=eMap.get("non pubblicati")%></span></td><td><span id="endPointStatusNotPublished<%=request.getParameter("idArchive")%>"></span></td>
			
			</tr><tr>
			<td><span style="font-weight:bold;">Modificati da pubblicare: </span></td><td><span id="recordModified<%=request.getParameter("idArchive")%>"><%=eMap.get("modificati")%></span></td><td><span id="endPointStatusModified<%=request.getParameter("idArchive")%>"></span></td>
			
			<tr>
			<td><span style="font-weight:bold;">Errori : </span></td><td><span id="publishError<%=request.getParameter("idArchive")%>"><%=eMap.get("errori")%></span></td><td><span id="endPointStatusErrors<%=request.getParameter("idArchive")%>"></span></td>
		</table>
		<br />
		<hr>
		<br />
	
	<% }%>
	<div style="margin-top:20px;display:none;"><span style="font-weight:bold;">Rigenerazione indici</span></div>
	<div style="display:none;" id="p3<%=request.getParameter("idArchive")%>"></div>
	<div style="margin-top:20px;display:none;"><span style="font-weight:bold;">Rigenerazione titoli</span></div>
	<div style="display:none;" id="p4<%=request.getParameter("idArchive")%>"></div>
	<div style="margin-top:20px;display:none;"><span style="font-weight:bold;">Pubblicazione endpoint</span></div>
	<div style="display:none;" id="p5<%=request.getParameter("idArchive")%>"></div>
	
	<div style="margin-top:20px;cursor:pointer;" id="index_link<%=request.getParameter("idArchive")%>">visualizza storico operazioni</div>
	<div id="operationsList<%=request.getParameter("idArchive")%>" style="display:none;border:1px solid #6593cf;width:90%;"></div>
	
	<div style="margin-top:20px;cursor:pointer;" id="view_index_conf<%=request.getParameter("idArchive")%>">visualizza configurazione indice</div>
	<div id="xml_index<%=request.getParameter("idArchive")%>" style="margin-top:5px;border: 1px solid #6593cf;width:90%;line-height:1em;font-size:15px;">
	<textarea id="code<%=request.getParameter("idArchive")%>" name="code">
<%=indexInfo.getXmlIndex().trim()%>
	</textarea>
	<div style="cursor:pointer;float:right;font-size:10px;" id="saveIndexConfiguration<%=request.getParameter("idArchive")%>">salva configurazione</div>
	</div>
	<div style="margin-top:20px;cursor:pointer;" id="view_titles_conf<%=request.getParameter("idArchive")%>">visualizza configurazione titoli</div>
	<div id="xml_titles<%=request.getParameter("idArchive")%>" style="margin-top:5px;border: 1px solid #6593cf;width:90%;line-height:1em;font-size:15px;">
	<textarea id="code2<%=request.getParameter("idArchive")%>" name="code2">
<%=indexInfo.getXmlTitle().trim()%>
	</textarea>
	<div style="cursor:pointer;float:right;font-size:10px;" id="saveTitlesConfiguration<%=request.getParameter("idArchive")%>">salva configurazione</div>
	</div>
</div> 
<% }%>
<% }%>

<style type="text/css">

	.x-tree-icon-leaf {
	display:none;
	}
	 
	#statusArchive<%=request.getParameter("idArchive")%> img{ 
	margin: 0px 0px -6px 0px;
	}
	
	 
</style>
