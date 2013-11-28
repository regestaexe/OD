<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="com.openDams.bean.Relations"%>
<%@page import="com.openDams.bean.RelationTypes"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.List"%>
<%@include file="../locale.jsp"%>
<link href="css/visual_browser_sky_blue.css" rel="stylesheet" type="text/css"/>
<script language="JavaScript" src="js/jsviz-browser/physics/ParticleModel.js"></script>
<script language="JavaScript" src="js/jsviz-browser/physics/Magnet.js"></script>
<script language="JavaScript" src="js/jsviz-browser/physics/Spring.js"></script>
<script language="JavaScript" src="js/jsviz-browser/physics/Particle.js"></script>
<script language="JavaScript" src="js/jsviz-browser/physics/RungeKuttaIntegrator.js"></script>
<script language="JavaScript" src="js/jsviz-browser/layout/graph/ForceDirectedLayout.js"></script>
<script language="JavaScript" src="js/jsviz-browser/layout/view/HTMLGraphView.js"></script>
<script language="JavaScript" src="js/jsviz-browser/util/Timer.js"></script>
<script language="JavaScript" src="js/jsviz-browser/util/EventHandler.js"></script>
<script language="JavaScript" src="js/jsviz-browser/io/DataGraph.js"></script>
<script language="JavaScript" src="js/jsviz-browser/io/HTTP.js"></script>
  

<%
if(request.getAttribute("records")!=null){
	Records records = (Records)request.getAttribute("records");
	TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
	HashMap<String, String[]> parsedTitle = titleManager.parseTitle(records.getTitle(),records.getArchives().getIdArchive());
	int tot_children = 0;
	int current_page = 1;
	if(request.getParameter("current_page")!=null){
		current_page = new Integer(request.getParameter("current_page"));
	}
	tot_children = records.getTotChildren();	
	int page_size = (Integer)request.getAttribute("page_size");
	int page_tot = 1;
	if(tot_children > page_size){
		if(tot_children % page_size==0 )
			page_tot=tot_children/page_size;
		else
			page_tot=(tot_children/page_size)+1;
	}
	List<Records> recordsNT = null;
	List<Records> recordsBT = null;
	List<Records> recordsRT = null;
	List<Records> recordsNM = null;
	List<Records> recordsBM = null;
	List<Records> recordsRM = null;
	List<Records> recordsCM = null;
	List<Records> recordsIS = null;
	try{
		recordsNT = (List<Records>)request.getAttribute("recordsNT");
	}catch(Exception e){
		
	}
	try{
		recordsBT = (List<Records>)request.getAttribute("recordsBT");
	}catch(Exception e){}
	try{
		recordsRT = (List<Records>)request.getAttribute("recordsRT");
	}catch(Exception e){}
	try{
		recordsNM = (List<Records>)request.getAttribute("recordsNM");
	}catch(Exception e){}
	try{
		recordsBM = (List<Records>)request.getAttribute("recordsBM");
	}catch(Exception e){}
	try{
		recordsRM = (List<Records>)request.getAttribute("recordsRM");
	}catch(Exception e){}
	try{
		recordsCM = (List<Records>)request.getAttribute("recordsCM");
	}catch(Exception e){}
	try{
		recordsIS = (List<Records>)request.getAttribute("recordsIS");
	}catch(Exception e){}
	String concept = "Concept" ;
	if(records.getXMLReader().getNodeCount("/rdf:RDF/skos:ConceptScheme")>0){
		concept = "ConceptScheme";
	}
%>
  <script language="JavaScript">
            function reDrowGraph(){
              $("#visual_browser").html("");
              drowGraph();
            }
            function highLightConcept(link,highligth){
                if(highligth){
					$("#"+link).css("border","1px solid #99bbe8");
					$("#"+link).css("background-image","url(css/resources/images/default/tabs/tab-strip-btm-bg.gif)");
                }else{
                	$("#"+link).css("border","0");
					$("#"+link).css("background-image","");
                }
            }
			function drowGraph() {
			  try{
				var layout = new ForceDirectedLayout( document.getElementById("visual_browser"), true );							
				layout.config._default = {					
					model: function( dataNode ) {
						return {
							mass: .9
						}
					},
					view: function( dataNode, modelNode ) {						   
							var nodeElement = document.createElement( 'div' );
							nodeElement.style.position = "absolute";
							nodeElement.style.height = "20px";							
							if(dataNode.page != undefined ){
								nodeElement.innerHTML = '<div class="vb_concept_div" id="link_'+dataNode.id+'"><a class="vb_concept_link" style="color:'+dataNode.color+';" href="#no" onclick="openRecord('+dataNode.id+','+dataNode.page+');">'+dataNode.text+'</a>';
							}else{
								nodeElement.innerHTML = '<div class="vb_concept_div" id="link_'+dataNode.id+'"><a class="vb_concept_link" style="color:'+dataNode.color+';" href="#no" onclick="openRecord('+dataNode.id+');">'+dataNode.text+'</a>';
							}
							nodeElement.onmousedown =  new EventHandler( layout, layout.handleMouseDownEvent, modelNode.id )
							return nodeElement;
						
					}
				}
				layout.forces.spring._default = function( nodeA, nodeB, isParentChild ) {
					return {
						springConstant: 0.1,
						dampingConstant: 0.1,
						restLength: nodeA.restLength
					}
				}			
				
        		layout.forces.magnet = function() {
					return {
						magnetConstant: -4000,
						minimumDistance: 40
					}
				}
				layout.viewEdgeBuilder = function( dataNodeSrc, dataNodeDest ) {					
					return {
							'pixelColor': '#99bbe8',
							'pixelWidth': '2px',
							'pixelHeight': '2px',
							'pixels': 30
						}
					
				}
				layout.model.ENTROPY_THROTTLE=false;

				var nodes = [];
				var node = new DataGraphNode();
				node.color = "#666a72";
				node.mass=.5;
				node.text="<%=JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/rdfs:label/text()"))%>";
				node.id="<%=records.getIdRecord()%>";
				node.restLength = 50;
				layout.newDataGraphNode( node );
				nodes.push( node );
				<%
				//PAGINAZIONE
				if(page_tot>1){
					if(current_page!=1){
							%>
								var neighbor = new DataGraphNode();
								neighbor.color = "#666a72";
								neighbor.mass=.5;
								neighbor.text="${visualtabTabFirstPreviousMoreConcepts}";
								neighbor.id="<%=records.getIdRecord()%>";
								neighbor.page="1";
								neighbor.restLength = 80;
								layout.newDataGraphNode( neighbor );
								layout.newDataGraphEdge(neighbor, node);
								nodes.push( neighbor );
								var neighbor = new DataGraphNode();
								neighbor.color = "#666a72";
								neighbor.mass=.5;
								neighbor.text="${visualtabTabPreviousMoreConcepts}";
								neighbor.id="<%=records.getIdRecord()%>";
								neighbor.page="<%=current_page-1%>";
								neighbor.restLength = 80;
								layout.newDataGraphNode( neighbor );
								layout.newDataGraphEdge(neighbor, node);
								nodes.push( neighbor );
							<%
					
					}
					if(current_page!=page_tot){
								%>
								var neighbor = new DataGraphNode();
								neighbor.color = "#666a72";
								neighbor.mass=.5;
								neighbor.text="${visualtabTabLastNextMoreConcepts}";
								neighbor.id="<%=records.getIdRecord()%>";
								neighbor.page="1";
								neighbor.restLength = 80;
								layout.newDataGraphNode( neighbor );
								layout.newDataGraphEdge(neighbor, node);
								nodes.push( neighbor );
								var neighbor = new DataGraphNode();
								neighbor.color = "#666a72";
								neighbor.mass=.5;
								neighbor.text="${visualtabTabNextMreConcepts}";
								neighbor.id="<%=records.getIdRecord()%>";
								neighbor.page="<%=current_page+1%>";
								neighbor.restLength = 80;
								layout.newDataGraphNode( neighbor );
								layout.newDataGraphEdge(neighbor, node);
								nodes.push( neighbor );
							<%
						
					}
				}
				//PAGINAZIONE
				%>
					<%
					 for(int i=0;i<recordsNT.size();i++){
					   Records records2 = recordsNT.get(i);
					   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
					%>
						var neighbor = new DataGraphNode();
						neighbor.color = "#ff7313";
						neighbor.mass=.5;
						neighbor.text="<%=JsSolver.escapeSingleDoubleApex( parsedTitle2.get("label")[0])%>";
						neighbor.id="<%=records2.getIdRecord()%>";
						neighbor.restLength = 120;
						layout.newDataGraphNode( neighbor );
						layout.newDataGraphEdge(neighbor, node);
						nodes.push( neighbor );
						
					<%
					 }%>
					<%for(int i=0;i<recordsBT.size();i++){
						   Records records2 = recordsBT.get(i);
						   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
						%>
							var neighbor = new DataGraphNode();
							neighbor.color = "#0891ca";
							neighbor.mass=.5;
							neighbor.text="<%=JsSolver.escapeSingleDoubleApex( parsedTitle2.get("label")[0])%>";
							neighbor.id="<%=records2.getIdRecord()%>";
							neighbor.restLength = 160;
							layout.newDataGraphNode( neighbor );
							layout.newDataGraphEdge(neighbor, node);
							nodes.push( neighbor );
				    <%}%>
					<%for(int i=0;i<recordsRT.size();i++){
					   Records records2 = recordsRT.get(i);
					   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
					%>
						var neighbor = new DataGraphNode();
						neighbor.color = "#438a0f";
						neighbor.mass=.5;
						neighbor.text="<%=JsSolver.escapeSingleDoubleApex( parsedTitle2.get("label")[0])%>";
						neighbor.id="<%=records2.getIdRecord()%>";
						neighbor.restLength = 160;
						layout.newDataGraphNode( neighbor );
						layout.newDataGraphEdge(neighbor, node);
						nodes.push( neighbor );
					<%}%>
					<%for(int i=0;i<recordsIS.size();i++){
						   Records records2 = recordsIS.get(i);
						   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
						%>
							var neighbor = new DataGraphNode();
							neighbor.color = "#000000";
							neighbor.mass=.5;
							neighbor.text="<%=JsSolver.escapeSingleDoubleApex( parsedTitle2.get("label")[0])%>";
							neighbor.id="<%=records2.getIdRecord()%>";
							neighbor.restLength = 160;
							layout.newDataGraphNode( neighbor );
							layout.newDataGraphEdge(neighbor, node);
							nodes.push( neighbor );
						<%}%>
					<%for(int i=0;i<recordsBM.size();i++){
						   Records records2 = recordsBM.get(i);
						   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
						%>
							var neighbor = new DataGraphNode();
							neighbor.color = "lime";
							neighbor.mass=.5;
							neighbor.text="<%=JsSolver.escapeSingleDoubleApex( parsedTitle2.get("label")[0])%>";
							neighbor.id="<%=records2.getIdRecord()%>";
							neighbor.restLength = 180;
							layout.newDataGraphNode( neighbor );
							layout.newDataGraphEdge(neighbor, node);
							nodes.push( neighbor );
						<%}%>
						<%for(int i=0;i<recordsNM.size();i++){
						   Records records2 = recordsNM.get(i);
						   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
						%>
							var neighbor = new DataGraphNode();
							neighbor.color = "maroon";
							neighbor.mass=.5;
							neighbor.text="<%=JsSolver.escapeSingleDoubleApex( parsedTitle2.get("label")[0])%>";
							neighbor.id="<%=records2.getIdRecord()%>";
							neighbor.restLength = 180;
							layout.newDataGraphNode( neighbor );
							layout.newDataGraphEdge(neighbor, node);
							nodes.push( neighbor );
						<%}%>
						<%for(int i=0;i<recordsRM.size();i++){
						   Records records2 = recordsRM.get(i);
						   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
						%>
							var neighbor = new DataGraphNode();
							neighbor.color = "olive";
							neighbor.mass=.5;
							neighbor.text="<%=JsSolver.escapeSingleDoubleApex( parsedTitle2.get("label")[0])%>";
							neighbor.id="<%=records2.getIdRecord()%>";
							neighbor.restLength = 180;
							layout.newDataGraphNode( neighbor );
							layout.newDataGraphEdge(neighbor, node);
							nodes.push( neighbor );
						<%}%>
						<%for(int i=0;i<recordsCM.size();i++){
						   Records records2 = recordsCM.get(i);
						   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
						%>
							var neighbor = new DataGraphNode();
							neighbor.color = "purple";
							neighbor.mass=.5;
							neighbor.text="<%=JsSolver.escapeSingleDoubleApex( parsedTitle2.get("label")[0])%>";
							neighbor.id="<%=records2.getIdRecord()%>";
							neighbor.restLength = 180;
							layout.newDataGraphNode( neighbor );
							layout.newDataGraphEdge(neighbor, node);
							nodes.push( neighbor );
						<%}%> 
				var buildTimer = new Timer( 1 );
				buildTimer.subscribe( layout );
				buildTimer.start(); 
			  }catch(e){
				  $("#visual_browser").html("");
	             // drowGraph();
			  }
			}
		</script>
		<script type="text/javascript">
			function borderLayout(){
				var myData = [
								["${skostabtablabellabel}", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/rdfs:label/text()") )%>"],
								["${skostabtablabelnotation}", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:notation/text()") )%>"],
								["${skostabtablabelpreflabel}", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:prefLabel/text()") )%>"],
				                ["${skostabtablabeltype}", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dc:type/text()") )%>"],
				                ["${skostabtablabelstatus}","<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:historyNote/text()") )%>"],
				                <% int countAltLabel = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:altLabel");
				               	for(int i=0;i<countAltLabel;i++){%>
				                ["${skostabtablabelalternativelabel}", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:altLabel["+(i+1)+"]/text()") )%>"],
				                <%}%>
				                ["${skostabtablabelscopenote}", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:scopeNote/text()") )%>"],
				                <% int countImages = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI']");
				               	for(int i=0;i<countImages;i++){%>
				                ["${skostabtablabelimages}", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI']["+(i+1)+"]/text()") )%>"],
				                <%}%>
				                ["${skostabtablabelannotations}", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:editorialNote/text()") )%>"]
				               
				           ];
				var myData2 = [
								["http://www.w3.org/2000/01/rdf-schema#Label", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/rdfs:label/text()") )%>"],
								["http://www.w3.org/2000/01/rdf-schema#Notation", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/rdfs:notation/text()") )%>"],
				                ["http://purl.org/dc/elements/1.1#type", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dc:type/text()") )%>"],
				                ["http://www.w3.org/2008/05/skos#historyNote","<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:historyNote/text()") )%>"],
				                <%for(int i=0;i<countAltLabel;i++){%>
				                ["http://www.w3.org/2008/05/skos#altLabel", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:altLabel["+(i+1)+"]/text()") )%>"],
				                <%}%>					   			
				                ["http://www.w3.org/2008/05/skos#scopeNote", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:scopeNote/text()") )%>"],
				                <%
				               	for(int i=0;i<countImages;i++){%>
				                ["http://purl.org/dc/elements/1.1#relation[@xsi:type='dcterms:URI']", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI']["+(i+1)+"]/text()") )%>"],
				                <%}%>	
				                ["http://www.w3.org/2008/05/skos#editorialNote", "<%= JsSolver.escapeSingleDoubleApex( records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:editorialNote/text()") )%>"],
				                <%for(int i=0;i<recordsBT.size();i++){
				          			   Records records2 = recordsBT.get(i);
				          			   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
				          			%>
				          				["<span onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\" style=\"color:#0891ca;\">http://www.w3.org/2008/05/skos#<%if(concept.equals("Concept")){%>broader<%}else{%>topConceptOf<%}%></span>", "<a class=\"relation_link\" href=\"#no\" onclick=\"openRecord('<%=records2.getIdRecord()%>');\" onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\">http://lod.xdams.org/ontologies/ods<%=parsedTitle2.get("id")[0]%></a>"],
				          		<%}%>
				                <%for(int i=0;i<recordsNT.size();i++){
				          		   Records records2 = recordsNT.get(i);
				          		   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
				          		%>
				          			["<span onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\" style=\"color:#ff7313;\">http://www.w3.org/2008/05/skos#<%if(concept.equals("Concept")){%>narrower<%}else{%>hasTopConcept<%}%></span>", "<a class=\"relation_link\" href=\"#no\" onclick=\"openRecord('<%=records2.getIdRecord()%>');\" onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\">http://lod.xdams.org/ontologies/ods<%=parsedTitle2.get("id")[0]%></a>"],
				          		<%}%>
				          		<%for(int i=0;i<recordsRT.size();i++){
				          		   Records records2 = recordsRT.get(i);
				          		   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
				          		%>
				          			["<span onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\" style=\"color:#438a0f;\">http://www.w3.org/2008/05/skos#related</span>", "<a class=\"relation_link\" href=\"#no\" onclick=\"openRecord('<%=records2.getIdRecord()%>');\" onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\">http://lod.xdams.org/ontologies/ods<%=parsedTitle2.get("id")[0]%></a>"],
				          		<%}%>
				          		<%for(int i=0;i<recordsIS.size();i++){
					          		   Records records2 = recordsIS.get(i);
					          		   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
					          		%>
					          			["<span onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\" style=\"color:#000000;\">http://www.w3.org/2008/05/skos#inScheme</span>", "<a class=\"relation_link\" href=\"#no\" onclick=\"openRecord('<%=records2.getIdRecord()%>');\" onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\">http://lod.xdams.org/ontologies/ods<%=parsedTitle2.get("id")[0]%></a>"],
					          		<%}%>
				              <%for(int i=0;i<recordsBM.size();i++){
				          		   Records records2 = recordsBM.get(i);
				          		   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
				          		%>
				          			["<span onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\" style=\"color:lime;\">http://www.w3.org/2008/05/skos#broadMatch</span>", "<a class=\"relation_link\" href=\"#no\" onclick=\"openRecord('<%=records2.getIdRecord()%>');\" onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\">http://lod.xdams.org/ontologies/ods<%=parsedTitle2.get("id")[0]%></a>"],
				          		<%}%>
				          		<%for(int i=0;i<recordsNM.size();i++){
				          		   Records records2 = recordsNM.get(i);
				          		   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
				          		%>
				          			["<span onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\" style=\"color:maroon;\">http://www.w3.org/2008/05/skos#narrowMatch</span>", "<a class=\"relation_link\" href=\"#no\" onclick=\"openRecord('<%=records2.getIdRecord()%>');\" onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\">http://lod.xdams.org/ontologies/ods<%=parsedTitle2.get("id")[0]%></a>"],
				          		<%}%>
				          		<%for(int i=0;i<recordsRM.size();i++){
				          		   Records records2 = recordsRM.get(i);
				          		   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
				          		%>
				          			["<span onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\" style=\"color:olive;\">http://www.w3.org/2008/05/skos#relatedMatch</span>", "<a class=\"relation_link\" href=\"#no\" onclick=\"openRecord('<%=records2.getIdRecord()%>');\" onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\">http://lod.xdams.org/ontologies/ods<%=parsedTitle2.get("id")[0]%></a>"],
				          		<%}%>
				          		<%for(int i=0;i<recordsCM.size();i++){
				          		   Records records2 = recordsCM.get(i);
				          		   HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(records2.getTitle(),records2.getArchives().getIdArchive());
				          		%>
				          			["<span onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\" style=\"color:purple;\">http://www.w3.org/2008/05/skos#closeMatch</span>", "<a class=\"relation_link\" href=\"#no\" onclick=\"openRecord('<%=records2.getIdRecord()%>');\" onmouseover=\"highLightConcept('link_<%=records2.getIdRecord()%>',true);\" onmouseout=\"highLightConcept('link_<%=records2.getIdRecord()%>',false);\">http://lod.xdams.org/ontologies/ods<%=parsedTitle2.get("id")[0]%></a>"],
				          		<%}%>
				          		["",""]
				           ];     
				 var store = new Ext.data.ArrayStore({
				        fields: [
				           {name: 'name'},
				           {name: 'value'}
				        ]
				    });
				store.loadData(myData);
				var store2 = new Ext.data.ArrayStore({
			        fields: [
			           {name: 'name'},
			           {name: 'value'}
			        ]
			    });
				store2.loadData(myData2);
				var rightPanel = new Ext.TabPanel({			    
				    deferredRender: false,
				    activeTab: 0,
				    width:500,
				    height:385,
				    autoScroll: false,
				    border: false,				    
				    items: [
							new Ext.grid.GridPanel({
						        store: store,
						        columns: [
						            {id:'name',header: '${visualtabtablename}', width: 100, sortable: true, dataIndex: 'name'},
						            {id:'value',header: '${visualtabtablevalue}', width: 400, sortable: true, dataIndex: 'value'}
						        ],
						        stripeRows: true,
						        autoExpandColumn: 'value',
						        autoExpandMax : 530 ,
						        width: 530,
						        title: '${visualtabtabconcept}',
						        stateful: true
						             
						    })   , 
						    new Ext.grid.GridPanel({
						        store: store2,
						        columns: [
						            {id:'name',header: '${visualtabtablename}', width: 255, sortable: true, dataIndex: 'name'},
						            {id:'value',header: '${visualtabtablevalue}', width: 245, sortable: true, dataIndex: 'value'}
						        ],
						        stripeRows: true,
						        autoExpandMax : 300 ,
						        width: 530,
						        title: '${visualtabtabtriple}',
						        stateful: true
						             
						    })
						    ]
				});
				var border = new Ext.Panel({											
											height: 427,
											width: 1000,
											layout: 'border',
											items: [
													{									
														region: 'south',									
														height: 40,									
														margins: '5 5 5 5',
														border: true,
														items: [{
																contentEl: 'vb_footer',
																autoScroll: true,
																border: false
																}]									
													},{									
														region: 'east',									
														margins: '5 5 0 0',									
														width: 500,
														height: 400,									
														minSize: 100,									
														maxSize: 500,
														border: true,
														items: [rightPanel]										
													},{																		
														region: 'center',
														width: 500,									
														minSize: 300,									
														maxSize: 500,									
														margins: '5 5 0 5',
														split: true,									
														items: [{
																	contentEl: 'visual_browser',
																	autoScroll: true,
																	border: false
																}]									
													}]
										});			
				border.render('pageContainer');
			}
			$(document).ready(function(){ 
				borderLayout();
				});
    	</script>
    	
	<div id="pageContainer" class="pageContainer"></div>
	<div id="visual_browser" class="visual_browser"></div>
	<div id="vb_footer" class="vb_footer">
		<div class="vb_footer_div_container">
					<div class="vb_footer_div"><div class="vb_div_color vb_div_refresh"><a href="#no" onclick="reDrowGraph();"><img src="img/skos_tab/refresh.gif" title="refresh canvas"></img></a></div></div>
		            <div class="vb_footer_div"><div class="vb_div_color vb_div_color_CC"></div><div class="vb_div_color_label">Concept</div></div>
				    <div class="vb_footer_div"><div class="vb_div_color vb_div_color_BT"></div><div class="vb_div_color_label">Broader / Top concept of</div></div>
					<div class="vb_footer_div"><div class="vb_div_color vb_div_color_NT"></div><div class="vb_div_color_label">Narrower / Has top concept</div></div>
					<div class="vb_footer_div"><div class="vb_div_color vb_div_color_RT"></div><div class="vb_div_color_label">Related</div></div>
					<div class="vb_footer_div"><div class="vb_div_color vb_div_color_IS"></div><div class="vb_div_color_label">In scheme</div></div>
					<div class="vb_footer_div"><div class="vb_div_color vb_div_color_BM"></div><div class="vb_div_color_label">broad match</div></div>
					<div class="vb_footer_div"><div class="vb_div_color vb_div_color_NM"></div><div class="vb_div_color_label">narrow match</div></div>
					<div class="vb_footer_div"><div class="vb_div_color vb_div_color_RM"></div><div class="vb_div_color_label">related  match</div></div>
					<div class="vb_footer_div"><div class="vb_div_color vb_div_color_CM"></div><div class="vb_div_color_label">close  match</div></div>
			</div>
	</div>
<%}%>
