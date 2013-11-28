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
UserDetails user =  (UserDetails)((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
Records records = (Records)request.getAttribute("records");
TitleManager titleManager = (TitleManager)request.getAttribute("titleManager");
HashMap<String, String[]> parsedTitle = titleManager.parseTitle(records.getTitle(),records.getArchives().getIdArchive());
int tot_children = 0;
tot_children = records.getTotChildren();
/*Set countRelationses = records.getCountRelationses();
if(countRelationses!=null){
	for(int x=0;x<countRelationses.size();x++){
		CountRelations countRelations = (CountRelations)countRelationses.toArray()[x];
		if(countRelations.getId().getRefIdRelationType()==1)
			tot_children = countRelations.getTot();
	}
}*/	
%>
<script>
$(document).ready(function() {

	$('.labelInput').change(function(){
		$('.destInputLabel').val($(this).val())
	})

	
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
	$(".macro_collapse").unbind();
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
	$('.edit_button').click(function(){
		if( $(this).parent("div").parent("div").parent("div").find(".edit_field")!=null){
        	$(this).parent("div").parent("div").parent("div").find(".edit_field").css('border', '1px dotted #7DAAEA');
        	$(this).parent("div").parent("div").parent("div").find(".edit_field").css('background-color', '#dfe8f6');        	
        	$(this).parent("div").parent("div").parent("div").find(".edit_field").focus();
		}     
	});
	$('.erase_button').click(function(){
		if( $(this).parent("div").parent("div").parent("div").find(".edit_field")!=null){
        	$(this).parent("div").parent("div").parent("div").find(".edit_field").css('border', '1px dotted #7DAAEA');
        	$(this).parent("div").parent("div").parent("div").find(".edit_field").css('background-color', '#dfe8f6');   
			try{
        		$(this).parent("div").parent("div").parent("div").find(".edit_field").text(""); 
			}catch (e) {
				$(this).parent("div").parent("div").parent("div").find(".edit_field").attr("value",""); 
			}   	
        	$(this).parent("div").parent("div").parent("div").find(".edit_field").focus();
		}
	});
	$('.up_button').click(function(){
		if($(this).attr("title")!="${skostabtabbuttondisabled}"){
			try{
				var splitted = $(this).attr("id").split("~");
				var container = splitted[0];
				var prog=parseInt(splitted[1],10);
				$("."+container+"_"+prog).insertBefore($("."+container+"_"+(prog-1)));
				renumerate(container);
			}catch(e){}
		}     
	});
	$('.down_button').click(function(){
		if($(this).attr("title")!="${skostabtabbuttondisabled}"){
			try{
				var splitted = $(this).attr("id").split("~");
				var container = splitted[0];
				var prog=parseInt(splitted[1],10);
				$("."+container+"_"+prog).insertAfter($("."+container+"_"+(prog+1)));
				renumerate(container);
			}catch(e){}
		}    
	}); 
	$('.edit_field').click(function(){		
        $(this).css('border', '1px dotted #7DAAEA');
        $(this).css('background-color', '#dfe8f6');   
        //alert($(this).attr("name"));
	});
	$('.edit_field').blur(function(){
	       //$(this).css("border","0px");
	       $(this).css('background-color', '#ffffff');	       
	});
	$('.delete_button').click(function(){
		 if($(this).attr("title")!="${skostabtabbuttondisabled}"){
			 var splitted = $(this).attr("id").split("~");
			 var container = splitted[0];
			 var prog=splitted[1];
			 if($("."+container+" > div[class*='"+container+"']").length>1){
			 	$("."+container).find("."+container+"_"+prog).remove();
			 }else{
				 try{
					   $("."+container+"_"+prog).find(".edit_field").text("");				
					}catch (e) {
						$("."+container+"_"+prog).find(".edit_field").attr("value",""); 
					} 				
			 }
			 renumerate(container);
		 } 
	});
	$('.add_button').click(function(){
		if($(this).attr("title")!="${skostabtabbuttondisabled}"){
			var splitted = $(this).attr("id").split("~");
			var container = splitted[0];
			var prog=splitted[1];
			var cloned = $("."+container+"_"+prog).clone();
			try{
				cloned.find(".edit_field").get(0).value="";												
				//cloned.find("input:hidden").attr("value","");	
				cloned.find("input:hidden").each(function(i){
						if($(this).attr("name").indexOf("/dc:date")!=-1 || $(this).attr("name").indexOf("/dc:creator")!=-1){
							if($(this).attr("name").indexOf("/dc:date")!=-1)
								$(this).attr("value","<%=DateUtility.getSystemDate()%>");
							else
								$(this).attr("value","<%=JsSolver.escapeSingleApex(user.getName())%> <%=JsSolver.escapeSingleApex(user.getLastname())%>");
						}else{
							$(this).attr("value","");
						}
						
				});	
			}catch(e) {
				cloned.find(".edit_field").attr("value","");				
				//cloned.find("input:hidden").attr("value","");	 
				cloned.find("input:hidden").each(function(i){
					if($(this).attr("name").indexOf("/dc:date")!=-1 || $(this).attr("name").indexOf("/dc:creator")!=-1){
						if($(this).attr("name").indexOf("/dc:date")!=-1)
							$(this).attr("value","<%=DateUtility.getSystemDate()%>");
						else
							$(this).attr("value","<%=JsSolver.escapeSingleApex(user.getName())%> <%=JsSolver.escapeSingleApex(user.getLastname())%>");
					}else{
						$(this).attr("value","");
					}
				});	
			}
	        cloned.insertAfter($("."+container+"_"+prog));	
	        renumerate(container);	
			addEvents(cloned);	
		}  
	});
	
	$(".onchangeField").change(function(){
		$(this).parent("div").parent("div").find("input").each(function(){
			if($(this).attr("name").indexOf("/dc:date")!=-1 || $(this).attr("name").indexOf("/dc:creator")!=-1){
				if($(this).attr("name").indexOf("/dc:date")!=-1)
				   $(this).attr("value","<%=DateUtility.getSystemDate()%>");
				else
				  $(this).attr("value","<%=JsSolver.escapeSingleApex(user.getName())%> <%=JsSolver.escapeSingleApex(user.getLastname())%>");
			}
		});	
    });
    if($('.autogrow').is(":visible")){
    	$('.autogrow').autogrow({minHeight:13,lineHeight:13,expandTolerance:1});
	}
});
function addEvents(div){
	div.find('.edit_button').click(function(){
		if( $(this).parent("div").parent("div").parent("div").find(".edit_field")!=null){
        	$(this).parent("div").parent("div").parent("div").find(".edit_field").css('border', '1px dotted #7DAAEA');
        	$(this).parent("div").parent("div").parent("div").find(".edit_field").css('background-color', '#dfe8f6');        	
        	$(this).parent("div").parent("div").parent("div").find(".edit_field").focus();
		}     
	});
	div.find('.erase_button').click(function(){
		if( $(this).parent("div").parent("div").parent("div").find(".edit_field")!=null){
        	$(this).parent("div").parent("div").parent("div").find(".edit_field").css('border', '1px dotted #7DAAEA');
        	$(this).parent("div").parent("div").parent("div").find(".edit_field").css('background-color', '#dfe8f6');   
			try{
        		$(this).parent("div").parent("div").parent("div").find(".edit_field").text(""); 
			}catch (e) {
				$(this).parent("div").parent("div").parent("div").find(".edit_field").attr("value",""); 
			}   	
        	$(this).parent("div").parent("div").parent("div").find(".edit_field").focus();        	
		}     
	});
	div.find('.up_button').click(function(){
		if($(this).attr("title")!="${skostabtabbuttondisabled}"){
			try{
				var splitted = $(this).attr("id").split("~");
				var container = splitted[0];
				var prog=parseInt(splitted[1],10);
				$("."+container+"_"+prog).insertBefore($("."+container+"_"+(prog-1)));
				renumerate(container);
			}catch(e){}
		}     
	});
	div.find('.down_button').click(function(){
		if($(this).attr("title")!="${skostabtabbuttondisabled}"){
			try{
				var splitted = $(this).attr("id").split("~");
				var container = splitted[0];
				var prog=parseInt(splitted[1],10);
				$("."+container+"_"+prog).insertAfter($("."+container+"_"+(prog+1)));
				renumerate(container);
			}catch(e){}
		}    
	}); 
	div.find('.edit_field').click(function(){		
        $(this).css('border', '1px dotted #7DAAEA');
        $(this).css('background-color', '#dfe8f6');
       // alert($(this).attr("name"));
	});
	div.find('.edit_field').blur(function(){
	      // $(this).css("border","0px");
	       $(this).css('background-color', '#ffffff');	       
	});
	div.find('.delete_button').click(function(){
		if($(this).attr("title")!="${skostabtabbuttondisabled}"){
			 var splitted = $(this).attr("id").split("~");
			 var container = splitted[0];
			 var prog=splitted[1];
			 if($("."+container+" > div[class*='"+container+"']").length>1){
			 	$("."+container).find("."+container+"_"+prog).remove();
			 }else{
				 try{
					   $("."+container+"_"+prog).find(".edit_field").text("");				
					}catch (e) {
						$("."+container+"_"+prog).find(".edit_field").attr("value",""); 
					} 				
			 }
			 renumerate(container);
			 
		 } 
	});
	div.find('.add_button').click(function(){
		if($(this).attr("title")!="${skostabtabbuttondisabled}"){
			var splitted = $(this).attr("id").split("~");
			var container = splitted[0];
			var prog=splitted[1];	
			var cloned = $("."+container+"_"+prog).clone();
			try{
				cloned.find(".edit_field").text("");
				cloned.find("input").attr("value","");					
			}catch (e) {
				cloned.find(".edit_field").attr("value",""); 
				cloned.find("input").attr("value","");	
			}
	        cloned.insertAfter($("."+container+"_"+prog));	
	        renumerate(container);	
			addEvents(cloned);	
		}
	});
	div.find(".onchangeField").change(function(){
		$(this).parent("div").parent("div").find("input").each(function(){
			if($(this).attr("name").indexOf("/dc:date")!=-1 || $(this).attr("name").indexOf("/dc:creator")!=-1){
				if($(this).attr("name").indexOf("/dc:date")!=-1)
				   $(this).attr("value","<%=DateUtility.getSystemDate()%>");
				else
				  $(this).attr("value","<%=JsSolver.escapeSingleApex(user.getName())%> <%=JsSolver.escapeSingleApex(user.getLastname())%>");
			}
		});	
   });
	
}
function renumerate(container){
	$("."+container+" > div[class*='"+container+"']").each(function(i){
        var classes = $(this).attr("class").split(" ");
        for(x=0;x<classes.length;x++){
            if(classes[x].indexOf(container)!=-1){
		        $(this).removeClass(classes[x]);
		        $(this).addClass(container+"_"+(i));
		        $(this).find(".delete_button").attr("id",container+"~"+(i));
		        $(this).find(".add_button").attr("id",container+"~"+(i));
		        $(this).find(".up_button").attr("id",container+"~"+(i));
		        $(this).find(".down_button").attr("id",container+"~"+(i));
		        $(this).find("input,textarea").each(function(z){
		        	 var name = $(this).attr("name");
				     name = name.substring(0,name.lastIndexOf("[")+1)+(i+1)+name.substring(name.lastIndexOf("]"),name.length);
					 $(this).attr("name",name);
				});
		        /*var name = $(this).find(".edit_field").attr("name");
		        name = name.substring(0,name.lastIndexOf("[")+1)+(i+1)+name.substring(name.lastIndexOf("]"),name.length);
				$(this).find(".edit_field").attr("name",name);*/
				return;
            }
        }
	});
}
</script>
<%String concept = "Concept" ;
if(records.getXMLReader().getNodeCount("/rdf:RDF/skos:ConceptScheme")>0){
	concept = "ConceptScheme";
}
%>

<form name="editForm" action="" method="post">
	<input type="hidden" name="idRecord" value="<%=records.getIdRecord()%>">
	<input type="hidden" name="idRecordType" value="<%=records.getRecordTypes().getIdRecordType()%>">
	<input type="hidden" name="totNT" value="<%=tot_children%>">
	<input type="hidden" name="idArchive" value="<%=records.getArchives().getIdArchive()%>">
	<input type="hidden" name="xml_root" value="rdf:RDF">
	<input type="hidden" name="encoding" value="UTF-8">
	<input type="hidden" name="xmlns:rdf"     value="http://www.w3.org/1999/02/22-rdf-syntax-ns#"> 
	<input type="hidden" name="xmlns:dc"      value="http://purl.org/dc/elements/1.1/">
	<input type="hidden" name="xmlns:rdfs"    value="http://www.w3.org/2000/01/rdf-schema#">
 	<input type="hidden" name="xmlns:ods"   value="http://lod.xdams.org/ontologies/ods/">
	<input type="hidden" name="xmlns:xsd"     value="http://www.w3.org/2001/XMLSchema#">
	<input type="hidden" name="xmlns:owl"     value="http://www.w3.org/2002/07/owl#">
	<input type="hidden" name="xmlns:skos"    value="http://www.w3.org/2008/05/skos#">
	<input type="hidden" name="xmlns:xsi"     value="http://www.w3.org/2001/XMLSchema-instance">
	<input type="hidden" name="xmlns:dcterms" value="http://purl.org/dc/terms/"> 
	
	<input type="hidden" name="/rdf:RDF/@xml:base" value="http://lod.xdams.org/ontologies/ods/">
	
	<input type="hidden" name="/rdf:RDF/skos:<%=concept%>/@xmlns:skos" value="http://www.w3.org/2008/05/skos#">
	<input type="hidden" name="/rdf:RDF/skos:<%=concept%>/@xmlns:rdf" value="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
	<input type="hidden" name="/rdf:RDF/skos:<%=concept%>/@rdf:about" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/@rdf:about")%>">
	<div class="area_top">
	<div class="area_tab"><div class="area_tab_label">${skosTabtabconcept} <div class="x-tool x-tool-toggle x-tool-collapse-north macro_collapse" id="concept_data">&nbsp;</div></div></div>
	<div class="area_tab_bottom_line"></div>
	<div  class="concept_data">
			<div class="input_label_row">
				<div class="input_label">${skostabtablabelnotation}</div>
				<div class="input_label_buttons">
					<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
					<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
					<img src="img/skos_tab/delete_off.png" class="delete_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
					<img src="img/skos_tab/plus_off.png" class="add_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
					<div class="up_down">
						<div class="up_down_up"><img src="img/skos_tab/up_off.png" class="up_button" title="${skostabtabbuttondisabled}"></div>
						<div class="up_down_down"><img src="img/skos_tab/down_off.png" class="down_button" title="${skostabtabbuttondisabled}"></div>
					</div>
				</div>
			</div>
			<div class="edit_div">
			<input type="text" class="edit_field"  name="<%="/rdf:RDF/skos:"+concept+"/skos:notation/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:notation/text()")%>">
			</div>
	</div>
	<div  class="concept_data">
			<div class="input_label_row">
				<div class="input_label">${skostabtablabellabel}</div>
				<div class="input_label_buttons">
					<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
					<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
					<img src="img/skos_tab/delete_off.png" class="delete_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
					<img src="img/skos_tab/plus_off.png" class="add_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
					<div class="up_down">
						<div class="up_down_up"><img src="img/skos_tab/up_off.png" class="up_button" title="${skostabtabbuttondisabled}"></div>
						<div class="up_down_down"><img src="img/skos_tab/down_off.png" class="down_button" title="${skostabtabbuttondisabled}"></div>
					</div>
				</div>
			</div>
			<div class="edit_div">
			<input type="text" class="edit_field labelInput"  name="<%="/rdf:RDF/skos:"+concept+"/rdfs:label/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/rdfs:label/text()")%>">
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
							<div class="input_label_buttons">
								<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
								<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
								<img src="img/skos_tab/delete.png" class="delete_button input_label_buttons_img" id="AltLabel~<%=i%>" title="${skostabtabbuttonremove}">
								<img src="img/skos_tab/plus.png" class="add_button input_label_buttons_img" id="AltLabel~<%=i%>" title="${skostabtabbuttonadd}">
								<div class="up_down">
									<div class="up_down_up"><img src="img/skos_tab/up.png" class="up_button" title="${skostabtabbuttonmoveup}"></div>
									<div class="up_down_down"><img src="img/skos_tab/down.png" class="down_button" title="${skostabtabbuttonmovedown}"></div>
								</div>
							</div>
						</div>
						<div class="edit_div">
						<input type="text" class="edit_field"  name="<%="/rdf:RDF/skos:"+concept+"/skos:altLabel["+(i+1)+"]/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:altLabel["+(i+1)+"]/text()")%>">
						</div>
				</div>
				 <%}%>
				 <%if(countAltLabel==0){%>
				 <div class="semantic_data AltLabel_0">
						<div class="input_label_row">
							<div class="input_label">${skostabtablabelalternativelabel}</div>
							<div class="input_label_buttons">
								<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
								<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
								<img src="img/skos_tab/delete.png" class="delete_button input_label_buttons_img" id="AltLabel~0" title="${skostabtabbuttonremove}">
								<img src="img/skos_tab/plus.png" class="add_button input_label_buttons_img" id="AltLabel~0" title="${skostabtabbuttonadd}">
								<div class="up_down">
									<div class="up_down_up"><img src="img/skos_tab/up.png" class="up_button" id="AltLabel~0" title="${skostabtabbuttonmoveup}"></div>
									<div class="up_down_down"><img src="img/skos_tab/down.png" class="down_button" id="AltLabel~0" title="${skostabtabbuttonmovedown}"></div>
								</div>
							</div>
						</div>
						<div class="edit_div">
						<input type="text" class="edit_field"  name="<%="/rdf:RDF/skos:"+concept+"/skos:altLabel[1]/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:altLabel[1]/text()")%>">
						</div>
				</div>
				 <%}%>
			</div>
			<div  class="semantic_data">
					<div class="input_label_row">
						<div class="input_label">${skostabtablabelscopenote}</div>
						<div class="input_label_buttons">
							<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
							<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
							<img src="img/skos_tab/delete_off.png" class="delete_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
							<img src="img/skos_tab/plus_off.png" class="add_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
							<div class="up_down">
								<div class="up_down_up"><img src="img/skos_tab/up_off.png" class="up_button" title="${skostabtabbuttondisabled}"></div>
								<div class="up_down_down"><img src="img/skos_tab/down_off.png" class="down_button" title="${skostabtabbuttondisabled}"></div>
							</div>
						</div>
					</div>
					<div class="edit_div">
			 		<textarea class="autogrow edit_field"  name="<%="/rdf:RDF/skos:"+concept+"/skos:scopeNote/text()"%>"><%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:scopeNote/text()").trim()%></textarea>
					</div>
			</div>
			<div  class="semantic_data">
					<div class="input_label_row">
						<div class="input_label">definition</div>
						<div class="input_label_buttons">
							<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
							<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
							<img src="img/skos_tab/delete_off.png" class="delete_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
							<img src="img/skos_tab/plus_off.png" class="add_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
							<div class="up_down">
								<div class="up_down_up"><img src="img/skos_tab/up_off.png" class="up_button" title="${skostabtabbuttondisabled}"></div>
								<div class="up_down_down"><img src="img/skos_tab/down_off.png" class="down_button" title="${skostabtabbuttondisabled}"></div>
							</div>
						</div>
					</div>
					<div class="edit_div">
			 		<textarea class="autogrow edit_field"  name="<%="/rdf:RDF/skos:"+concept+"/skos:definition/text()"%>"><%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:definition/text()").trim()%></textarea>
					</div>
			</div>
			<!--div class="multi_container Images">
			  <% int countImages = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI']");
	           	for(int i=0;i<countImages;i++){%>
				<div class="semantic_data Images_<%=i%>">
						<div class="input_label_row">
							<div class="input_label">${skostabtablabelimages}</div>
							<div class="input_label_buttons">
								<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
								<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
								<img src="img/skos_tab/delete.png" class="delete_button input_label_buttons_img" id="Images~<%=i%>" title="${skostabtabbuttonremove}">
								<img src="img/skos_tab/plus.png" class="add_button input_label_buttons_img" id="Images~<%=i%>" title="${skostabtabbuttonadd}">
								<div class="up_down">
									<div class="up_down_up"><img src="img/skos_tab/up.png" class="up_button" title="${skostabtabbuttonmoveup}"></div>
									<div class="up_down_down"><img src="img/skos_tab/down.png" class="down_button" title="${skostabtabbuttonmovedown}"></div>
								</div>
							</div>
						</div>
						<div class="edit_div">
						<input type="text" class="edit_field"  name="<%="/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI']["+(i+1)+"]/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI']["+(i+1)+"]/text()")%>">
						</div>
				</div>
				 <%}%>
				 <%if(countImages==0){%>
				 <div class="semantic_data Images_0">
						<div class="input_label_row">
							<div class="input_label">${skostabtablabelimages}</div>
							<div class="input_label_buttons">
								<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
								<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
								<img src="img/skos_tab/delete.png" class="delete_button input_label_buttons_img" id="Images~0" title="${skostabtabbuttonremove}">
								<img src="img/skos_tab/plus.png" class="add_button input_label_buttons_img" id="Images~0" title="${skostabtabbuttonadd}">
								<div class="up_down">
									<div class="up_down_up"><img src="img/skos_tab/up.png" class="up_button" title="${skostabtabbuttonmoveup}"></div>
									<div class="up_down_down"><img src="img/skos_tab/down.png" class="down_button" title="${skostabtabbuttonmovedown}"></div>
								</div>
							</div>
						</div>
						<div class="edit_div">
						<input type="text" class="edit_field"  name="<%="/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI'][1]/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dc:relation[@xsi:type='dcterms:URI'][1]/text()")%>">
						</div>
				</div>
				 <%}%>
			</div-->
			<div class="multi_container Annotations">
					<% int counteditorialNote = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:editorialNote");
				           for(int i=0;i<counteditorialNote;i++){%>
					           <div class="semantic_data Annotations_<%=i%>">
				               <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:date/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:editorialNote["+(i+1)+"]/dc:date/text()")%>"></input>
				               <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:creator/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:editorialNote["+(i+1)+"]/dc:creator/text()")%>"></input>
										<div class="input_label_row">
											<div class="input_label">${skostabtablabelannotations}</div>
											<div class="input_label_buttons">
												<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
												<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
												<img src="img/skos_tab/delete.png" class="delete_button input_label_buttons_img" id="Annotations~<%=i%>" title="${skostabtabbuttonremove}">
												<img src="img/skos_tab/plus.png" class="add_button input_label_buttons_img" id="Annotations~<%=i%>" title="${skostabtabbuttonadd}">
												<div class="up_down">
													<div class="up_down_up"><img src="img/skos_tab/up.png" class="up_button" title="${skostabtabbuttonmoveup}"></div>
													<div class="up_down_down"><img src="img/skos_tab/down.png" class="down_button" title="${skostabtabbuttonmovedown}"></div>
												</div>
											</div>
										</div>
										<div class="edit_div">
										<textarea class="autogrow edit_field onchangeField"  name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:description/text()"%>"><%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:description/text()").trim()%></textarea>
										</div>
								</div>
		       		 <%}%>
		       		 <%if(counteditorialNote==0){%>
		       		 		   <div class="semantic_data Annotations_0">
				               <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource'][1]/dc:date/text()"%>" value=""></input>
				               <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource'][1]/dc:creator/text()"%>" value=""></input>
										<div class="input_label_row">
											<div class="input_label">${skostabtablabelannotations}</div>
											<div class="input_label_buttons">
												<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
												<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
												<img src="img/skos_tab/delete.png" class="delete_button input_label_buttons_img" id="Annotations~0" title="${skostabtabbuttonremove}">
												<img src="img/skos_tab/plus.png" class="add_button input_label_buttons_img" id="Annotations~0" title="${skostabtabbuttonadd}">
												<div class="up_down">
													<div class="up_down_up"><img src="img/skos_tab/up.png" class="up_button" title="${skostabtabbuttonmoveup}"></div>
													<div class="up_down_down"><img src="img/skos_tab/down.png" class="down_button" title="${skostabtabbuttonmovedown}"></div>
												</div>
											</div>
										</div>
										<div class="edit_div">
										<textarea class="autogrow edit_field onchangeField"  name="<%="/rdf:RDF/skos:"+concept+"/skos:editorialNote[@rdf:parseType='Resource'][1]/dc:description/text()"%>"></textarea>
										</div>
							  </div>
		       		 <%}%> 
			</div>
			
	</div>
	<div class="area">
			<div class="area_tab">
			<div class="area_tab_label">Dati gestionali <div class="x-tool x-tool-toggle x-tool-collapse-south macro_collapse" id="managing_data">&nbsp;</div></div></div>
			<div class="area_tab_bottom_line"></div>
			<div class="managing_data" style="display: none;">
			<div class="input_label_row">
				<div class="input_label">${skostabtablabelpreflabel}</div>
				<div class="input_label_buttons">
					<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
					<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
					<img src="img/skos_tab/delete_off.png" class="delete_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
					<img src="img/skos_tab/plus_off.png" class="add_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
					<div class="up_down">
						<div class="up_down_up"><img src="img/skos_tab/up_off.png" class="up_button" title="${skostabtabbuttondisabled}"></div>
						<div class="up_down_down"><img src="img/skos_tab/down_off.png" class="down_button" title="${skostabtabbuttondisabled}"></div>
					</div>
				</div>
			</div>
			<div class="edit_div">
			<input type="text" class="edit_field destInputLabel"  name="<%="/rdf:RDF/skos:"+concept+"/skos:prefLabel/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:prefLabel/text()")%>">
			</div>
	</div>
	<div class="managing_data" style="display: none;">
			<div class="input_label_row">
				<div class="input_label">${skostabtablabeltype}</div>
				<div class="input_label_buttons">
					<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
					<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
					<img src="img/skos_tab/delete_off.png" class="delete_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
					<img src="img/skos_tab/plus_off.png" class="add_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
					<div class="up_down">
						<div class="up_down_up"><img src="img/skos_tab/up_off.png" class="up_button" title="${skostabtabbuttondisabled}"></div>
						<div class="up_down_down"><img src="img/skos_tab/down_off.png" class="down_button" title="${skostabtabbuttondisabled}"></div>
					</div>
				</div>
			</div>
			<div class="edit_div">
			<%
			String selected1="";
			String selected2="";
			String selected3="";
			String type = records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dc:type/text()");
			if(type.equalsIgnoreCase("person")){
				selected1="selected=\"selected\"";
			}else if(type.equalsIgnoreCase("place")){
				selected2="selected=\"selected\"";
			}else if(type.equalsIgnoreCase("topic")){
				selected3="selected=\"selected\"";
			}
			%>
			<select class="edit_field" name="<%="/rdf:RDF/skos:"+concept+"/dc:type/text()"%>">
			    <option value=""></option>
				<option value="person" <%=selected1%>>person</option>
				<option value="place" <%=selected2%>>place</option>
				<option value="topic" <%=selected3%>>topic</option>
			</select>
			<!--input type="text" class="edit_field" readonly="readonly" name="<%="/rdf:RDF/skos:"+concept+"/dc:type/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dc:type/text()")%>"-->
			</div>
	</div>
	<div class="managing_data" style="display: none;">
			<div class="input_label_row">
				<div class="input_label">${skostabtablabelstatus}</div>
				<div class="input_label_buttons">
					<img src="img/skos_tab/edit.png" class="edit_button input_label_buttons_img" title="${skostabtabbuttonedit}">
					<img src="img/skos_tab/erase.png" class="erase_button input_label_buttons_img" title="${skostabtabbuttonempty}">
					<img src="img/skos_tab/delete_off.png" class="delete_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
					<img src="img/skos_tab/plus_off.png" class="add_button input_label_buttons_img" title="${skostabtabbuttondisabled}">
					<div class="up_down">
						<div class="up_down_up"><img src="img/skos_tab/up_off.png" class="up_button" title="${skostabtabbuttondisabled}"></div>
						<div class="up_down_down"><img src="img/skos_tab/down_off.png" class="down_button" title="${skostabtabbuttondisabled}"></div>
					</div>
				</div>
			</div>
			<div class="edit_div">
			<%
			String selected4="";
			String selected5="";
			String selected6="";
			String status = records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:historyNote/text()");
			if(status.equalsIgnoreCase("draft")){
				selected4="selected=\"selected\"";
			}else if(status.equalsIgnoreCase("edited")){
				selected5="selected=\"selected\"";
			}else if(status.equalsIgnoreCase("minimal")){
				selected6="selected=\"selected\"";
			}
			%>
			<select class="edit_field" name="<%="/rdf:RDF/skos:"+concept+"/skos:historyNote/text()"%>">
			    <option value=""></option>
				<option value="draft" <%=selected4%>>draft</option>
				<option value="edited" <%=selected5%>>edited</option>
				<option value="minimal" <%=selected6%>>minimal</option>
			</select>
			<!--input type="text" class="edit_field"  name="<%="/rdf:RDF/skos:"+concept+"/skos:historyNote/text()"%>" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:historyNote/text()")%>"-->
			</div>
	</div>
	<div class="managing_data" style="display: none;">
			<div class="input_label_row">
				<div class="input_label">${skostabtablabelchangenote}
				<div class="x-tool x-tool-toggle x-tool-collapse-south changeNote_button">&nbsp;</div>
				</div>
			</div>
			<div id="changeNote" class="note_container">
			<% int countChangeNote = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:changeNote");
	           for(int i=0;i<countChangeNote;i++){%>
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource']["+(i+1)+"]/rdf:value/text()"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:changeNote["+(i+1)+"]/rdf:value/text()")%>">
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote["+(i+1)+"]/dc:creator/text()"%>"   value="<%=records.getXMLReader().getValueOf("/rdf:RDF/skos:"+concept+"/skos:changeNote["+(i+1)+"]/dc:creator/text()")%>">
			   <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:creator/@rdf:parseType"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:creator/@rdf:parseType")%>">
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource']["+(i+1)+"]/dc:date/text()"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:changeNote["+(i+1)+"]/dc:date/text()")%>">
	           <ul class="changeNoteUl">
	           	 <li class="changeNoteLi"><span class="changeNoteUl_span"><%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:changeNote["+(i+1)+"]/rdf:value/text()")%></span> : <%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:changeNote["+(i+1)+"]/dc:creator/text()")%></li>
	   		     <li class="changeNoteLi"><span class="changeNoteUl_span">${skostabtablabeldate}</span> : <%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:changeNote["+(i+1)+"]/dc:date/text()")%></li>
	   		   </ul>     
	        <%}%>
	        <%if(countChangeNote<=1){%>
	          <%if(records.getRecordsVersions()!=null && records.getRecordsVersions().size()>0){
	        	  Object[] recordsVersions = records.getRecordsVersions().toArray();
	              RecordsVersion recordsVersion = (RecordsVersion)recordsVersions[0];
	           %>
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource'][2]/rdf:value/text()"%>"   value="version <%=recordsVersion.getVersion()%>">
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource'][2]/dc:date/text()"%>"     value="<%=recordsVersion.getVersionDate().toString()%>">
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource'][3]/rdf:value/text()"%>"   value="modify">
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[3]/dc:creator/text()"%>"   value="<%=user.getName()%> <%=user.getLastname()%>">
			   <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource'][3]/dc:creator/@rdf:parseType"%>"   value="Literal">
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource'][3]/dc:date/text()"%>"   value="<%=DateUtility.getSystemDate()%>">	
	           <%}else{%>
	        	<input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource'][2]/rdf:value/text()"%>"   value="modify">
	            <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[2]/dc:creator/text()"%>"   value="<%=user.getName()%> <%=user.getLastname()%>">
			    <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource'][2]/dc:creator/@rdf:parseType"%>"   value="Literal">
	            <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource'][2]/dc:date/text()"%>"   value="<%=DateUtility.getSystemDate()%>">	   
	           <%}%>
	          
	          
	        <%}%>
	        </div>
	</div>
	</div>
	<input type="hidden" name="/rdf:RDF/skos:<%=concept%>/owl:deprecated/text()" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/owl:deprecated/text()")%>">
	<input type="hidden" name="/rdf:RDF/skos:<%=concept%>/dcterms:isReplacedBy/@rdf:resource" value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dcterms:isReplacedBy/@rdf:resource")%>">
	 <%int countInscheme = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:inScheme");
	   for(int i=0;i<countInscheme;i++){%>
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:inScheme["+(i+1)+"]/@rdf:resource"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:inScheme["+(i+1)+"]/@rdf:resource")%>">   
	 <%}%>
	 <%int countbroader = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:broader");
	   for(int i=0;i<countbroader;i++){%>
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:broader["+(i+1)+"]/@rdf:resource"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:broader["+(i+1)+"]/@rdf:resource")%>">   
	 <%}%>
	 <%int countnarrower = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:narrower");
	   for(int i=0;i<countnarrower;i++){%>
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:narrower["+(i+1)+"]/@rdf:resource"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:narrower["+(i+1)+"]/@rdf:resource")%>">   
	 <%}%>
	 <%int countrelated = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:related");
	   for(int i=0;i<countrelated;i++){%>
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:related["+(i+1)+"]/@rdf:resource"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:related["+(i+1)+"]/@rdf:resource")%>">   
	 <%}%>
	 <%int counthasTopConcept = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:hasTopConcept");
	   for(int i=0;i<counthasTopConcept;i++){%>
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:hasTopConcept["+(i+1)+"]/@rdf:resource"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:hasTopConcept["+(i+1)+"]/@rdf:resource")%>">   
	 <%}%>
	 <%int counttopConceptOf = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:topConceptOf");
	   for(int i=0;i<counttopConceptOf;i++){%>
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:topConceptOf["+(i+1)+"]/@rdf:resource"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:topConceptOf["+(i+1)+"]/@rdf:resource")%>">   
	 <%}%>
	 <%int countbroadMatch = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:broadMatch");
	   for(int i=0;i<countbroadMatch;i++){%>
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:broadMatch["+(i+1)+"]/@rdf:resource"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:broadMatch["+(i+1)+"]/@rdf:resource")%>">   
	 <%}%>
	 <%int countnarrowMatch = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:narrowMatch");
	   for(int i=0;i<countnarrowMatch;i++){%>
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:narrowMatch["+(i+1)+"]/@rdf:resource"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:narrowMatch["+(i+1)+"]/@rdf:resource")%>">   
	 <%}%>
	 <%int countrelatedMatch = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:relatedMatch");
	   for(int i=0;i<countrelatedMatch;i++){%>
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:relatedMatch["+(i+1)+"]/@rdf:resource"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:relatedMatch["+(i+1)+"]/@rdf:resource")%>">   
	 <%}%>
	 <%int countcloseMatch = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:closeMatch");
	   for(int i=0;i<countcloseMatch;i++){%>
	           <input type="hidden" name="<%="/rdf:RDF/skos:"+concept+"/skos:closeMatch["+(i+1)+"]/@rdf:resource"%>"   value="<%=records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:closeMatch["+(i+1)+"]/@rdf:resource")%>">   
	 <%}%>
	<div class="last_empty_line">&nbsp;</div>
</form>
<%if(records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dcterms:isReplacedBy/@rdf:resource")!=null && !records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/dcterms:isReplacedBy/@rdf:resource").equals("")){%>
	<script>
		Ext.getCmp('deprecate_button').setText('Riabilita voce');
	</script>
<%}else{%>
	<script>
		Ext.getCmp('deprecate_button').setText('Depreca voce');
	</script>
<%}%>


<%
records.closeXMLReader();
}
%>