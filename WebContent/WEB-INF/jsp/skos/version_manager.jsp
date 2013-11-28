<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="com.openDams.bean.Records"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="com.openDams.bean.RecordsVersion"%>
<%@page import="com.regesta.framework.util.DateUtility"%>
<%@page import="com.regesta.framework.xml.XMLReader"%><html xmlns="http://www.w3.org/1999/xhtml" >
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
<%
if(request.getAttribute("records")!=null){
Records records = (Records)request.getAttribute("records");
String concept = "Concept" ;
if(records.getXMLReader().getNodeCount("/rdf:RDF/skos:ConceptScheme")>0){
	concept = "ConceptScheme";
}
Object[] recordsVersions = null;
if(records.getRecordsVersions()!=null){
	recordsVersions= records.getRecordsVersions().toArray();
}	
%>
    <style type="text/css">
		   
    </style>
    <script type="text/javascript">
    Ext.onReady(function(){
        Ext.state.Manager.setProvider(new Ext.state.CookieProvider());       
        var viewport = new Ext.Viewport({
            layout: 'border',
            items: [
		        new Ext.Panel({
                region: 'west',
                title:'<%=JsSolver.escapeSingleApex(records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/rdfs:label/text()"))%>',
                id: 'west-panel-version',
                contentEl: 'west',
                width: '50%',
                deferredRender: false,
                split: true,
                margins: '0 0 0 5',
                autoScroll: true,
                buttons: [
     	                    {
     	                        text:'${buttonVersionNew}',
     	                        handler: function(){
     	                    	    $("#ajaxLoad").load("ajax/version_manager.html?idRecord=<%=records.getIdRecord()%>&action=new_version",function(){
     	                    	    	    Ext.MessageBox.alert('${messageStatus}', '${messageStatusText}');
     	                    	    	    document.location.reload();
         	                        });
     	                    	}
     	                    }
     	              ]
		        }),
            new Ext.TabPanel({
                region: 'center',
                id: 'center-panel-version',
                deferredRender: false,
                activeTab: 0,
                enableTabScroll:true,
                items: [
                    <%
                    if(recordsVersions!=null){
                    	for(int i=0;i< recordsVersions.length;i++){
                    		RecordsVersion recordsVersion = (RecordsVersion)recordsVersions[i];
                    	%>
                    	     {
                             contentEl: 'version_<%=i%>',
                             title: 'V.<%=recordsVersion.getVersion()%> del <%=recordsVersion.getVersionDate().toString()%>',
                             closable: false,
                             autoScroll: true,
                             buttons: [
               	                    {
               	                        text:'${buttonVersionRestore}',
               	                        handler: function(){
	               	                        	$("#ajaxLoad").load("ajax/version_manager.html?idRecord=<%=records.getIdRecord()%>&idVersion=<%=recordsVersion.getIdVersion()%>&action=restore_version",function(){
	         	                    	    	    Ext.MessageBox.alert('${messageStatus}', '${messageStatusText}');
	         	                    	    	    window.opener.reloadTree();
	         	                    	    	    window.opener.openRecord('<%=records.getIdRecord()%>');;
	         	                    	    	    document.location.reload();
	             	                        	});              	
               	                    	}
               	                    }
               	                    <%if(i>0){%>
               	                    ,{
               	                        text:'${buttonVersionDelete}',
               	                        handler: function(){
	               	                        	$("#ajaxLoad").load("ajax/version_manager.html?idRecord=<%=records.getIdRecord()%>&idVersion=<%=recordsVersion.getIdVersion()%>&action=delete_version",function(){
	         	                    	    	    Ext.MessageBox.alert('${messageStatus}', '${messageStatusText}');
	         	                    	    	    document.location.reload();
	             	                        	});              	
               	                    	}
               	                    }
               	                   <%}%>
               	              ]
                         	 }
							<%if(i<recordsVersions.length-1){%>
                         	 ,
                         	 <%}%>
                    
                    <%  }
                    }%>
                ]
            })]
        });
       
    });
    $(document).ready(function() {
    	$(".changeNote_button").click(function(e){
    		if($(".note_container").is(":visible")){
    			$(".note_container").hide(500);
    			$(this).removeClass("x-tool-collapse-north");
    			$(this).addClass("x-tool-collapse-south");
    		}else{
    			$(".note_container").show(500);
    			$(this).removeClass("x-tool-collapse-south");
    			$(this).addClass("x-tool-collapse-north");
    		}
               
    	});
    	$(".macro_collapse").click(function(e){
    		if($("."+$(this).attr("id")).is(":visible")){
    			$("."+$(this).attr("id")).hide(500);
    			$(this).removeClass("x-tool-collapse-north");
    			$(this).addClass("x-tool-collapse-south");
    		}else{
    			$("."+$(this).attr("id")).show(500);
    			$(this).removeClass("x-tool-collapse-south");
    			$(this).addClass("x-tool-collapse-north");
    		}
               
    	});
    	$('.autogrow').autogrow({minHeight:1,lineHeight:13,expandTolerance:1});
    	$(".to_compare").find("input").each(function(i){
            try{    
		            if($(".compare_original").find("input[name='"+$(this).attr("name")+"']").get(0)!='undefined'){
		                 if($(this).attr("value")!= $(".compare_original").find("input[name='"+$(this).attr("name")+"']").attr("value")){
							 if($(this).attr("value")==""){
								 $(this).css("background-color","red");
							 }else{
								 $(this).css("color","red");
							 }                	 
		                 }
		            }else{
		            	 if($(this).attr("value")==""){
							 $(this).css("background-color","red");
						 }else{
							 $(this).css("color","red");
						 }
		            }
		            if($(".compare_original").find("textarea[name='"+$(this).attr("name")+"']").get(0)!='undefined'){
		                 if($(this).text()!=$(".compare_original").find("textarea[name='"+$(this).attr("name")+"']").text()){
		                	 if($(this).attr("value")==""){
								 $(this).css("background-color","red");
							 }else{
								 $(this).css("color","red");
							 }
		                 }
		            }else{
		            	 if($(this).attr("value")==""){
							 $(this).css("background-color","red");
						 }else{
							 $(this).css("color","red");
						 }
		            }
            }catch(e){
            	$(this).attr("name");
            }
        });
    	
    });
    </script>
</head>
<body>
    <div style="margin:0;padding:0;display: none;" id="ajaxLoad"></div>
    <div id="west" class="x-hide-display compare_original" style="width: 99%;">
        <div class="area_top">
			<div class="area_tab"><div class="area_tab_label">${skosTabtabconcept} <div class="x-tool x-tool-toggle x-tool-collapse-north macro_collapse" id="concept_data">&nbsp;</div></div></div>
			<div class="area_tab_bottom_line"></div>
			<div  class="concept_data">
					<div class="input_label_row">
						<div class="input_label">${skostabtablabellabel}</div>
						
					</div>
					<div class="edit_div">
					<input type="text" class="edit_field" readonly="readonly"  name="<%="/rdf:RDF/skos:"+concept+"/rdfs:label/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/rdfs:label/text()")%>">
					</div>
			</div>
			<div  class="concept_data">
					<div class="input_label_row">
						<div class="input_label">${skostabtablabelnotation}</div>
						
					</div>
					<div class="edit_div">
					<input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/rdfs:notation/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:notation/text()")%>">
					</div>
			</div>
			<div  class="concept_data">
					<div class="input_label_row">
						<div class="input_label">${skostabtablabeltype}</div>
						
					</div>
					<div class="edit_div">
					<input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/dc:type/text()"%>" value="<%=concept%><%//=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dc:type/text()")%>">
					</div>
			</div>
			<div  class="concept_data">
					<div class="input_label_row">
						<div class="input_label">${skostabtablabelstatus}</div>
						
					</div>
					<div class="edit_div">
					<input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/skos:historyNote/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:historyNote/text()")%>">
					</div>
			</div>
			</div>
			<div class="area">
					<div class="area_tab">
					<div class="area_tab_label">${skosTabtabsemantic} <div class="x-tool x-tool-toggle x-tool-collapse-north macro_collapse" id="semantic_data">&nbsp;</div></div></div>
					<div class="area_tab_bottom_line"></div>
					<div class="multi_container AltLabel">
					   <% int countAltLabel = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:altLabel");
			           	for(int i=0;i<countAltLabel;i++){%>
						<div class="semantic_data AltLabel_<%=i%>">
								<div class="input_label_row">  
									<div class="input_label">${skostabtablabelalternativelabel}</div>
									
								</div>
								<div class="edit_div">
								<input type="text" class="edit_field" readonly="readonly"  name="<%="/rdf:RDF/skos:"+concept+"/skos:altLabel["+(i+1)+"]/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:altLabel["+(i+1)+"]/text()")%>">
								</div>
						</div>
						 <%}%>
						 <%if(countAltLabel==0){%>
						 <div class="semantic_data AltLabel_0">
								<div class="input_label_row">
									<div class="input_label">${skostabtablabelalternativelabel}</div>
									
								</div>
								<div class="edit_div">
								<input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/skos:altLabel[1]/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:altLabel[1]/text()")%>">
								</div>
						</div>
						 <%}%>
					</div>
					<div  class="semantic_data">
							<div class="input_label_row">
								<div class="input_label">${skostabtablabelscopenote}</div>
								
							</div>
							<div class="edit_div">
					 		<textarea class="autogrow edit_field" readonly="readonly"  name="<%="/rdf:RDF/skos:"+concept+"/skos:scopeNote/text()"%>"><%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:scopeNote/text()").trim()%></textarea>
							</div>
					</div>
					<div class="multi_container Images">
					  <% int countImages = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI']");
			           	for(int i=0;i<countImages;i++){%>
						<div class="semantic_data Images_<%=i%>">
								<div class="input_label_row">
									<div class="input_label">${skostabtablabelimages}</div>
									
								</div>
								<div class="edit_div">
								<input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI']["+(i+1)+"]/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI']["+(i+1)+"]/text()")%>">
								</div>
						</div>
						 <%}%>
						 <%if(countImages==0){%>
						 <div class="semantic_data Images_0">
								<div class="input_label_row">
									<div class="input_label">${skostabtablabelimages}</div>
									
								</div>
								<div class="edit_div">
								<input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI'][1]/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI'][1]/text()")%>">
								</div>
						</div>
						 <%}%>
					</div>
					<div class="multi_container Annotations">
							<% int counteditorialNote = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:editorialNote");
						           for(int i=0;i<counteditorialNote;i++){%>
							           <div class="semantic_data Annotations_<%=i%>">
							            <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:date/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:editorialNote["+(i+1)+"]/dc:date/text()")%>"></input>
			               				<input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:creator/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:editorialNote["+(i+1)+"]/dc:creator/text()")%>"></input>
												<div class="input_label_row">
													<div class="input_label">${skostabtablabelannotations}</div>
													
												</div>
												<div class="edit_div">
												<textarea class="autogrow edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:description/text()"%>"><%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:description/text()").trim()%></textarea>
												</div>
										</div>
				       		 <%}%>
				       		 <%if(counteditorialNote==0){%>
				       		 		   <div class="semantic_data Annotations_0">
				       		 		   <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource'][1]/dc:date/text()"%>" value=""></input>
			              			   <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource'][1]/dc:creator/text()"%>" value=""></input>
												<div class="input_label_row">
													<div class="input_label">${skostabtablabelannotations}</div>
													
												</div>
												<div class="edit_div">
												<textarea class="autogrow edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource'][1]/dc:description/text()"%>"></textarea>
												</div>
									  </div>
				       		 <%}%> 
					</div>
					
			</div>
    </div>
     <%
                    if(recordsVersions!=null){
                    	for(int x=0;x< recordsVersions.length;x++){
                    		RecordsVersion recordsVersion = (RecordsVersion)recordsVersions[x];
                    		String xml = new String(recordsVersion.getXml(),"UTF-8");
                    		XMLReader xmlReader = new XMLReader(xml);
                    	%>
                    	<div id="version_<%=x%>" class="x-hide-display to_compare" style="width: 99%;">
                    	        <div class="area_top">
									<div class="area_tab"><div class="area_tab_label">${skosTabtabconcept} <div class="x-tool x-tool-toggle x-tool-collapse-north macro_collapse" id="concept_data">&nbsp;</div></div></div>
									<div class="area_tab_bottom_line"></div>
									<div  class="concept_data">
											<div class="input_label_row">
												<div class="input_label">${skostabtablabellabel}</div>
												
											</div>
											<div class="edit_div">
											<input type="text" class="edit_field" readonly="readonly"  name="<%="/rdf:RDF/skos:"+concept+"/rdfs:label/text()"%>" value="<%=xmlReader.getNodeValue("/rdf:RDF/skos:"+concept+"/rdfs:label/text()")%>">
											</div>
									</div>
									<div  class="concept_data">
											<div class="input_label_row">
												<div class="input_label">${skostabtablabelnotation}</div>
												
											</div>
											<div class="edit_div">
											<input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/rdfs:notation/text()"%>" value="<%=xmlReader.getNodeValue("/rdf:RDF/skos:"+concept+"/skos:notation/text()")%>">
											</div>
									</div>
									<div  class="concept_data">
											<div class="input_label_row">
												<div class="input_label">${skostabtablabeltype}</div>
												
											</div>
											<div class="edit_div">
											<input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/dc:type/text()"%>" value="<%=concept%><%//=xmlReader.getNodeValue("/rdf:RDF/skos:"+concept+"/dc:type/text()")%>">
											</div>
									</div>
									<div  class="concept_data">
											<div class="input_label_row">
												<div class="input_label">${skostabtablabelstatus}</div>
												
											</div>
											<div class="edit_div">
											<input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/skos:historyNote/text()"%>" value="<%=xmlReader.getNodeValue("/rdf:RDF/skos:"+concept+"/skos:historyNote/text()")%>">
											</div>
									</div>
									</div>
									<div class="area">
											<div class="area_tab">
											<div class="area_tab_label">${skosTabtabsemantic} <div class="x-tool x-tool-toggle x-tool-collapse-north macro_collapse" id="semantic_data">&nbsp;</div></div></div>
											<div class="area_tab_bottom_line"></div>
											<div class="multi_container AltLabel">
											   <% int countAltLabelV = xmlReader.getNodeCount("/rdf:RDF/skos:"+concept+"/skos:altLabel");
									           	for(int i=0;i<countAltLabelV;i++){%>
												<div class="semantic_data AltLabel_<%=i%>">
														<div class="input_label_row">  
															<div class="input_label">${skostabtablabelalternativelabel}</div>
															
														</div>
														<div class="edit_div">
														<input type="text" class="edit_field" readonly="readonly"  name="<%="/rdf:RDF/skos:"+concept+"/skos:altLabel["+(i+1)+"]/text()"%>" value="<%=xmlReader.getNodeValue("/rdf:RDF/skos:"+concept+"/skos:altLabel["+(i+1)+"]/text()")%>">
														</div>
												</div>
												 <%}%>
												 <%if(countAltLabelV==0){%>
												 <div class="semantic_data AltLabel_0">
														<div class="input_label_row">
															<div class="input_label">${skostabtablabelalternativelabel}</div>
															
														</div>
														<div class="edit_div">
														<input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/skos:altLabel[1]/text()"%>" value="<%=xmlReader.getNodeValue("/rdf:RDF/skos:"+concept+"/skos:altLabel[1]/text()")%>">
														</div>
												</div>
												 <%}%>
											</div>
											<div  class="semantic_data">
													<div class="input_label_row">
														<div class="input_label">${skostabtablabelscopenote}</div>
														
													</div>
													<div class="edit_div">
											 		<textarea class="autogrow edit_field" readonly="readonly"  name="<%="/rdf:RDF/skos:"+concept+"/skos:scopeNote/text()"%>"><%=xmlReader.getNodeValue("/rdf:RDF/skos:"+concept+"/skos:scopeNote/text()").trim()%></textarea>
													</div>
											</div>
											<div class="multi_container Images">
											  <% int countImagesV = xmlReader.getNodeCount("/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI']");
									           	for(int i=0;i<countImagesV;i++){%>
												<div class="semantic_data Images_<%=i%>">
														<div class="input_label_row">
															<div class="input_label">${skostabtablabelimages}</div>
															
														</div>
														<div class="edit_div">
														<input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI']["+(i+1)+"]/text()"%>" value="<%=xmlReader.getNodeValue("/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI']["+(i+1)+"]/text()")%>">
														</div>
												</div>
												 <%}%>
												 <%if(countImagesV==0){%>
												 <div class="semantic_data Images_0">
														<div class="input_label_row">
															<div class="input_label">${skostabtablabelimages}</div>
															
														</div>
														<div class="edit_div">
														<input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI'][1]/text()"%>" value="<%=xmlReader.getNodeValue("/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI'][1]/text()")%>">
														</div>
												</div>
												 <%}%>
											</div>
											<div class="multi_container Annotations">
													<% int counteditorialNoteV = xmlReader.getNodeCount("/rdf:RDF/skos:"+concept+"/skos:editorialNote");
												           for(int i=0;i<counteditorialNoteV;i++){%>
													           <div class="semantic_data Annotations_<%=i%>">
																		<div class="input_label_row">
																			<div class="input_label">${skostabtablabelannotations}</div>
																			
																		</div>
																		<div class="edit_div">
																		<textarea class="autogrow edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:description/text()"%>"><%=xmlReader.getNodeValue("/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:description/text()").trim()%></textarea>
																		</div>
																</div>
										       		 <%}%>
										       		 <%if(counteditorialNoteV==0){%>
										       		 		   <div class="semantic_data Annotations_0">
																		<div class="input_label_row">
																			<div class="input_label">${skostabtablabelannotations}</div>
																		</div>
																		<div class="edit_div">
																		<textarea class="autogrow edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource'][1]/dc:description/text()"%>"></textarea>
																		</div>
															  </div>
										       		 <%}%> 
											</div>
											
									</div>
                    	
                    	</div>
                    	     
                    
                    <%  }
                    }%>
</body>
<%}%>
</html>