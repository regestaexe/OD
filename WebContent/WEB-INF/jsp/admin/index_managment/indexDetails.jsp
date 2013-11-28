<%@page import="com.openDams.index.configuration.IndexInfo"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Set"%>
<%@page import="com.openDams.bean.Archives"%>



<%@page import="java.util.List"%><script>
var pbar<%=request.getParameter("idArchive")%>;
var pbarT<%=request.getParameter("idArchive")%>;
var pbarP<%=request.getParameter("idArchive")%>;
Ext.onReady(function(){
	Ext.getCmp('indexList').setDisabled(false);
	<%if(request.getAttribute("endPointList")!=null){%>
	Ext.getCmp('end_point_publisher').menu.removeAll(true);
    <%
	List<String> endPointList = (List<String>)  request.getAttribute("endPointList");
   	for (int i = 0; i < endPointList.size(); i++) {%>
   		Ext.getCmp('end_point_publisher').menu.add(
   	       		new Ext.Action({
	             text: 'pubblica tutto l\'archivio su <strong><%=endPointList.get(i)%></strong>',
		             id:'all_archive_<%=endPointList.get(i)%>',
		             endPointId:'<%=endPointList.get(i)%>',
		             disabled:true,
		             iconCls: 'not_publisched',
		             handler: function(btn){
		            	    Ext.getCmp('rebuild_index').setDisabled(true);
			        		Ext.getCmp('rebuild_title').setDisabled(true);
			        		Ext.getCmp('offline_archive').setDisabled(true);
			        		Ext.getCmp('end_point_publisher').setDisabled(true);
			        		Ext.getCmp('indexList').setDisabled(true);											      
			        		$('#img_online').attr('src','../img/offline.gif');
			        		$("#view_index_conf").hide();
			    			$("#view_titles_conf").hide();
			    			$("#xml_index").hide();
			    			$("#xml_titles").hide();
			    		//	alert(btn.text.indexOf('pubblica')!=-1 && btn.text.indexOf(btn.endPointId)!=-1 && btn.text.indexOf('tutto')!=-1)
			    			
			    		if (btn.text.indexOf('pubblica')!=-1 && btn.text.indexOf(btn.endPointId)!=-1 && btn.text.indexOf('tutto')!=-1) {
     						startEndPointProgressBar();
				        		Ext.Ajax.request({
				        	           url: 'endPointPublisher.html?action=publishEndPoint&idArchive='+$("input[name='index_idArchive']").attr("value")+'&endPointManager='+btn.endPointId,
				        	           success: function(r) {
				        					Ext.TaskMgr.start(eval('taskP'+$("input[name='index_idArchive']").attr("value")));
				        					btn.setText('rimuovi tutto l\'archivio da <strong>'+btn.endPointId+'</strong>');
				    						btn.setIconClass('publisched');
				        	           }
				        		});
         				}else{
         					startEndPointProgressBar();
				        		Ext.Ajax.request({
				        	           url: 'endPointPublisher.html?action=archiveRemove&idArchive='+$("input[name='index_idArchive']").attr("value")+'&endPointManager='+btn.endPointId,
				        	           success: function(r) {
				        					Ext.TaskMgr.start(eval('taskP'+$("input[name='index_idArchive']").attr("value")));
				        					btn.setText('pubblica tutto l\'archivio su <strong>'+btn.endPointId+'</strong>');
				    						btn.setIconClass('not_publisched');
				        	           }
				        		});
	                		}											        												        												        		
      				}
		         })
		);
   		Ext.getCmp('end_point_publisher').menu.add(
   	       		new Ext.Action({
	             text: 'ripubblica i documenti su <strong><%=endPointList.get(i)%></strong>',
		             id:'republish_archive_<%=endPointList.get(i)%>',
		             endPointId:'<%=endPointList.get(i)%>',
		             disabled:true,
		             iconCls: 'republish',
		             handler: function(btn){
		            	    Ext.getCmp('rebuild_index').setDisabled(true);
			        		Ext.getCmp('rebuild_title').setDisabled(true);
			        		Ext.getCmp('offline_archive').setDisabled(true);
			        		Ext.getCmp('end_point_publisher').setDisabled(true);
			        		Ext.getCmp('indexList').setDisabled(true);											      
			        		$('#img_online').attr('src','../img/offline.gif');
			        		$("#view_index_conf").hide();
			    			$("#view_titles_conf").hide();
			    			$("#xml_index").hide();
			    			$("#xml_titles").hide();
     						startEndPointProgressBar();
			        		Ext.Ajax.request({
			        	           url: 'endPointPublisher.html?action=rePublishEndPoint&idArchive='+$("input[name='index_idArchive']").attr("value")+'&endPointManager='+btn.endPointId,
			        	           success: function(r) {
			        					Ext.TaskMgr.start(eval('taskP'+$("input[name='index_idArchive']").attr("value")));
			        					//btn.setText('rimuovi tutto l\'archivio da '+btn.endPointId);
			    						//btn.setIconClass('publisched');
			        	           }
			        		});
										        												        												        		
      				}
		         })
		);
   	<%}
	 }else{%>
	 Ext.getCmp('end_point_publisher').menu.removeAll(true);
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
			Ext.getCmp('rebuild_index').setDisabled(false);
			Ext.getCmp('rebuild_title').setDisabled(false);
			Ext.getCmp('offline_archive').setDisabled(false);			

			Ext.getCmp('end_point_publisher').menu.items.each(function(item){
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
								item.setIconClass('publisched');
								item.setText('rimuovi tutto l\'archivio da <strong>'+item.endPointId+'</strong>');
								item.setDisabled(false);
							} else {
								item.setIconClass('not_publisched');
								item.setText('pubblica tutto l\'archivio su <strong>'+item.endPointId+'</strong>');
								item.setDisabled(false);
							}
							Ext.getCmp('end_point_publisher').setDisabled(false);
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
							Ext.getCmp('end_point_publisher').setDisabled(false);
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
					Ext.getCmp('offline_archive').setText('Metti online');
				<%}else{%>
					Ext.getCmp('offline_archive').setText('Metti offline');
				<%}
			}%>
			$("#view_index_conf").show();
			$("#view_titles_conf").show();
		<%}else{%>
			Ext.Ajax.request({
	           url: 'indexList.html?action=check_status&idArchive=<%=request.getParameter("idArchive")%>',
	           success: function(r) {
					if(r.responseText=='STARTED'){
						startProgressBar();
					}
	           }
			});
			Ext.Ajax.request({
	           url: 'rebuildTitle.html?action=check_status&idArchive=<%=request.getParameter("idArchive")%>',
	           success: function(r) {
	        	    //alert("titoli = "+r.responseText);
					if(r.responseText=='STARTED'){
						startTitleProgressBar();
					}
	           }
			});
			Ext.Ajax.request({
		           url: 'endPointPublisher.html?action=check_status&idArchive=<%=request.getParameter("idArchive")%>',
		           success: function(r) {
		        	    //alert("titoli = "+r.responseText);
						if(r.responseText=='STARTED'){
							startTitleProgressBar();
						}
		           }
				});					
			Ext.getCmp('rebuild_index').setDisabled(true);
			Ext.getCmp('rebuild_title').setDisabled(true);
			Ext.getCmp('offline_archive').setDisabled(true);
			Ext.getCmp('end_point_publisher').setDisabled(true);   		
			$("#view_index_conf").hide();
			$("#view_titles_conf").hide();
			$("#xml_index").hide();
			$("#xml_titles").hide();
		<%}
	}%>
});

var task<%=request.getParameter("idArchive")%> = {
    run: function(){
        Ext.Ajax.request({
           url: 'indexList.html?action=check_status&idArchive=<%=request.getParameter("idArchive")%>',
           success: function(r) {
				if(r.responseText=='FAILED'){
					Ext.TaskMgr.stop(task<%=request.getParameter("idArchive")%>);
					pbar<%=request.getParameter("idArchive")%>.reset();
					pbar<%=request.getParameter("idArchive")%>.updateText("Operazione fallita");
					pbar<%=request.getParameter("idArchive")%>.reset();
					$("#indexDetails").load("indexList.html?action=index_details&idArchive=<%=request.getParameter("idArchive")%>&operation=FAILED");
				}else if(r.responseText=='COMPLETED'){
					Ext.TaskMgr.stop(task<%=request.getParameter("idArchive")%>);				
					pbar<%=request.getParameter("idArchive")%>.updateText("Operazione completata");
					pbar<%=request.getParameter("idArchive")%>.reset();
					Ext.getCmp('rebuild_index').setDisabled(false);
					Ext.getCmp('rebuild_title').setDisabled(false);
					Ext.getCmp('offline_archive').setDisabled(false);
		    		Ext.getCmp('end_point_publisher').setDisabled(false);
					$("#view_index_conf").show();
					$("#view_titles_conf").show();
					$("#indexDetails").load("indexList.html?action=index_details&idArchive=<%=request.getParameter("idArchive")%>&operation=COMPLETED");
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
						Ext.TaskMgr.stop(taskT<%=request.getParameter("idArchive")%>);
						pbarT<%=request.getParameter("idArchive")%>.reset();
						pbarT<%=request.getParameter("idArchive")%>.updateText("Operazione fallita");
						pbarT<%=request.getParameter("idArchive")%>.reset();
						$("#indexDetails").load("indexList.html?action=index_details&idArchive=<%=request.getParameter("idArchive")%>&tOperation=FAILED");
					}else if(r.responseText=='COMPLETED'){
						Ext.TaskMgr.stop(taskT<%=request.getParameter("idArchive")%>);				
						pbarT<%=request.getParameter("idArchive")%>.updateText("Operazione completata");
						pbarT<%=request.getParameter("idArchive")%>.reset();
						Ext.getCmp('rebuild_index').setDisabled(false);
						Ext.getCmp('rebuild_title').setDisabled(false);
						Ext.getCmp('offline_archive').setDisabled(false);
			    		Ext.getCmp('end_point_publisher').setDisabled(false);
						$("#view_index_conf").show();
						$("#view_titles_conf").show();
						$("#indexDetails").load("indexList.html?action=index_details&idArchive=<%=request.getParameter("idArchive")%>&tOperation=COMPLETED");
					}
	           }
			});
	    },
	    interval: 2000
	}
var taskP<%=request.getParameter("idArchive")%> = {
	    run: function(){
	        Ext.Ajax.request({
	           url: 'endPointPublisher.html?action=check_status&idArchive=<%=request.getParameter("idArchive")%>',
	           success: function(r) {
					if(r.responseText=='FAILED'){
						Ext.TaskMgr.stop(taskP<%=request.getParameter("idArchive")%>);
						pbarP<%=request.getParameter("idArchive")%>.reset();
						pbarP<%=request.getParameter("idArchive")%>.updateText("Operazione fallita");
						pbarP<%=request.getParameter("idArchive")%>.reset();
						$("#indexDetails").load("indexList.html?action=index_details&idArchive=<%=request.getParameter("idArchive")%>&tOperation=FAILED");
					}else if(r.responseText=='COMPLETED'){
						Ext.TaskMgr.stop(taskP<%=request.getParameter("idArchive")%>);				
						pbarP<%=request.getParameter("idArchive")%>.updateText("Operazione completata");
						pbarP<%=request.getParameter("idArchive")%>.reset();
						Ext.getCmp('rebuild_index').setDisabled(false);
						Ext.getCmp('rebuild_title').setDisabled(false);
						Ext.getCmp('offline_archive').setDisabled(false);
			    		Ext.getCmp('end_point_publisher').setDisabled(false);
						$("#view_index_conf").show();
						$("#view_titles_conf").show();
						$("#indexDetails").load("indexList.html?action=index_details&idArchive=<%=request.getParameter("idArchive")%>&tOperation=COMPLETED");
					}
	           }
			});
	    },
	    interval: 2000
	}
function startProgressBar(){
	pbar<%=request.getParameter("idArchive")%>.wait({
        interval:200,
        increment:15,
        fn:function(){
    		Ext.get('rebuild_index').dom.disabled = false;
    		Ext.get('rebuild_title').dom.disabled = false;
        }
    });
}
function startTitleProgressBar(){
	pbarT<%=request.getParameter("idArchive")%>.wait({
        interval:200,
        increment:15,
        fn:function(){
    		Ext.get('rebuild_index').dom.disabled = false;
    		Ext.get('rebuild_title').dom.disabled = false;
        }
    });
}
function startEndPointProgressBar(){
	pbarP<%=request.getParameter("idArchive")%>.wait({
        interval:200,
        increment:15,
        fn:function(){
    		Ext.get('rebuild_index').dom.disabled = false;
    		Ext.get('rebuild_title').dom.disabled = false;
        }
    });
}
$(document).ready(function(){
     $("#index_link").click(function(){          
        if(!$("#operationsList").is(":visible")){
        	$("#operationsList").show(1000);
        	$("#operationsList").load("indexList.html?action=history&idArchive=<%=request.getParameter("idArchive")%>",function(){

            });
        	$(this).html("nascondi storico operazioni");
        	
        }else{
        	$("#operationsList").hide(1000);
        	$(this).html("visualizza storico operazioni");	
        }
     });
     $("#view_index_conf").click(function(){          
         if(!$("#xml_index").is(":visible")){
         	$("#xml_index").show(1000);
         	$(this).html("nascondi configurazione indice");
         	
         }else{
         	$("#xml_index").hide(1000);
         	$(this).html("visualizza configurazione indice");	
         }
      });
     $("#view_titles_conf").click(function(){          
         if(!$("#xml_titles").is(":visible")){
         	$("#xml_titles").show(1000);
         	$(this).html("nascondi configurazione titoli");
         	
         }else{
         	$("#xml_titles").hide(1000);
         	$(this).html("visualizza configurazione titoli");	
         }
      });
     $("#saveIndexConfiguration").click(function(){
            var val = escape(editor.getValue());
			$.post("indexList.html",
					  { xml: val,action: 'save_xml',xml_type:'index',idArchive:'<%=request.getParameter("idArchive")%>'},
					  function(data){
						  Ext.Msg.alert('Attenzione','Configurazione salvata correttamente!');
						  $("#xml_index").hide(1000);
					  }
					);
     });
     $("#saveTitlesConfiguration").click(function(){ 
	    	 var val = escape(editor2.getValue());  
	    	 $.post("indexList.html",
	    			 { xml: val,action: 'save_xml',xml_type:'title',idArchive:'<%=request.getParameter("idArchive")%>'},
					  function(data){
						  Ext.Msg.alert('Attenzione','Configurazione salvata correttamente!');
						  $("#xml_titles").hide(1000);
					  }
					);
  	 });
     
});
</script>
<div style="margin-left:10px;margin-top:10px;">
<%if(request.getAttribute("indexInfo")!=null){
	IndexInfo indexInfo = (IndexInfo)request.getAttribute("indexInfo");
	Archives archives = (Archives)request.getAttribute("archives"); 
	%>
	<div style="font-weight:bold;font-size:15px;">ARCHIVIO <%=archives.getLabel()%> <span><%if(archives.isOffline()){%><img src="../img/offline.gif" id="img_online"><%}else{%><img src="../img/online.gif" id="img_online"><%}%></span></div>
	<input type="hidden" name="index_idArchive" value="<%=request.getParameter("idArchive")%>"/>		
	<div style="margin-top:10px;"><span style="font-weight:bold;">Nome indice : </span><%=indexInfo.getIndex_name()%></div>
	<div><span style="font-weight:bold;">Versione indice : </span><%=indexInfo.getIndex_version()%></div>
	<div><span style="font-weight:bold;">Ottimizzato : </span><%=indexInfo.isOptimized()%></div>
	<div><span style="font-weight:bold;">Numero documenti indice: </span><%=indexInfo.getNumDocs()%></div>
	<div><span style="font-weight:bold;">Numero documenti archivio: </span><%=indexInfo.getNumDocsArchive()%></div>
	<div><span style="font-weight:bold;">Numero documenti nel cestino: </span><%=indexInfo.getNumDocsBasket()%></div>
	<div><span style="font-weight:bold;">Numero documenti totali: </span><%=indexInfo.getNumDocsBasket()+indexInfo.getNumDocsArchive()%>(<%=indexInfo.getNumDocsArchive()%>+<%=indexInfo.getNumDocsBasket()%>)</div>
	<div><span style="font-weight:bold;">Data ultima modifica : </span><%=indexInfo.getLastModifyDate()%></div>
	<div><span style="font-weight:bold;">File di indice : </span></div>
	<%
	HashMap<String,String> files = indexInfo.getFiles();
	Set<String> names = files.keySet();
	Object[] namesString = names.toArray();
	for(int i=0;i<namesString.length;i++){%>
		<div style="margin-left:90px;font-weight:bold;font-size:10px;"><%=namesString[i]%>&nbsp;&nbsp;&nbsp;&nbsp;<%=files.get(namesString[i])%></div>
	<%}%>

	<div style="margin-top:20px;"><span style="font-weight:bold;">Rigenerazione indici</span></div>
	<div id="p3<%=request.getParameter("idArchive")%>"></div>
	<div style="margin-top:20px;"><span style="font-weight:bold;">Rigenerazione titoli</span></div>
	<div id="p4<%=request.getParameter("idArchive")%>"></div>
	<div style="margin-top:20px;"><span style="font-weight:bold;">Pubblicazione endpoint</span></div>
	<div id="p5<%=request.getParameter("idArchive")%>"></div>
	<div style="margin-top:20px;cursor:pointer;" id="index_link">visualizza storico operazioni</div>
	<div id="operationsList" style="display:none;border:1px solid #6593cf;width:90%;"></div>
	
	<div style="margin-top:20px;cursor:pointer;" id="view_index_conf">visualizza configurazione indice</div>
	<div id="xml_index" style="margin-top:5px;border: 1px solid #6593cf;width:90%;line-height:1em;font-size:15px;">
	<textarea id="code" name="code">
<%=indexInfo.getXmlIndex().trim()%>
	</textarea>
	<div style="cursor:pointer;float:right;font-size:10px;" id="saveIndexConfiguration">salva configurazione</div>
	</div>
	<div style="margin-top:20px;cursor:pointer;" id="view_titles_conf">visualizza configurazione titoli</div>
	<div id="xml_titles" style="margin-top:5px;border: 1px solid #6593cf;width:90%;line-height:1em;font-size:15px;">
	<textarea id="code2" name="code2">
<%=indexInfo.getXmlTitle().trim()%>
	</textarea>
	<div style="cursor:pointer;float:right;font-size:10px;" id="saveTitlesConfiguration">salva configurazione</div>
	</div>
</div>
<script>
	var editor = CodeMirror.fromTextArea(document.getElementById("code"),{
		  mode: "application/xml",
		  lineNumbers: true
	});
	$("#xml_index").hide();
</script>
<script>
	var editor2 = CodeMirror.fromTextArea(document.getElementById("code2"), {
		  mode: "application/xml",
		  lineNumbers: true
	});
	$("#xml_titles").hide();
</script>
<%}%>