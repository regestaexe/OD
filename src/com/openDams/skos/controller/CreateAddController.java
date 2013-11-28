package com.openDams.skos.controller;


import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.RecordTypes;
import com.openDams.bean.Records;
import com.openDams.bean.Relations;
import com.openDams.bean.RelationsId;
import com.openDams.bean.XmlId;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;
import com.regesta.framework.util.DateUtility;
import com.regesta.framework.util.StringUtility;

public class CreateAddController implements Controller{
	private  OpenDamsService service ;
	private  TitleManager titleManager ;

	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;		
		/*
		System.out.println("arg0.getParameter(\"idArchive\")="+arg0.getParameter("idArchive"));
		System.out.println("arg0.getParameter(\"id_father\")="+arg0.getParameter("id_father"));
		System.out.println("arg0.getParameter(\"label\")="+arg0.getParameter("label"));
		System.out.println("arg0.getParameter(\"relation_type\")="+arg0.getParameter("relation_type"));
		System.out.println("arg0.getParameter(\"xmltype\")="+arg0.getParameter("xmltype"));
		System.out.println("arg0.getParameter(\"idRecordType\")="+arg0.getParameter("idRecordType"));
		*/
		Archives archives = (Archives)service.getObject(Archives.class, new Integer(arg0.getParameter("idArchive")));
		XmlId xmlId = archives.getXmlId();
		xmlId.setIdXml(xmlId.getIdXml()+1);
		String xmlIdRecord = archives.getArchiveClass()+"/"+archives.getArchiveXmlPrefix()+StringUtility.fillZero(Integer.toString(xmlId.getIdXml()),7);
		UserDetails user =  (UserDetails)((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
		String  xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";  
				xml+="<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:skos=\"http://www.w3.org/2008/05/skos#\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:bio=\"http://purl.org/vocab/bio/0.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ods=\"http://lod.xdams.org/ontologies/ods/\">";
				xml+="		<skos:"+arg0.getParameter("xmltype")+" xmlns=\"\" rdf:about=\""+xmlIdRecord+"\">";
				xml+="				<rdfs:label>"+arg0.getParameter("label")+"</rdfs:label>";
				xml+="				<rdfs:notation></rdfs:notation>";
				xml+="				<dc:type></dc:type>";
				xml+="				<skos:historyNote></skos:historyNote>";
				xml+="				<skos:changeNote rdf:parseType=\"Resource\">";
				xml+="					<rdf:value>create</rdf:value>";
				xml+="					<dc:creator rdf:parseType=\"Literal\">"+user.getName()+" "+user.getLastname()+"</dc:creator>";
				xml+="					<dc:date>"+DateUtility.getMySQLSystemDate().replaceAll("-", "")+"</dc:date>";
				xml+="				</skos:changeNote>";
				xml+="		</skos:"+arg0.getParameter("xmltype")+">";
				xml+="</rdf:RDF>";
		Records record = new Records();
		record.setXml(xml.getBytes());
		record.setXmlId(xmlIdRecord);
		record.setRecordTypes((RecordTypes)service.getObject(RecordTypes.class,new Integer(arg0.getParameter("idRecordType"))));				
		record.setArchives(archives);		
		//record.setTitle(titleManager.getTitle(record.getXMLReader(),new Integer(arg0.getParameter("idArchive"))));
		record.setCreationDate(new Date());
		record.setModifyDate(new Date());
		record.setPosition(0);
		record.setDepth(0);				
		service.add(record);
		service.update(xmlId);
		if(arg0.getParameter("action")==null){
			Relations relations = new Relations();
			RelationsId relationsId= new RelationsId(new Integer(arg0.getParameter("id_father")),record.getIdRecord(),new Integer(arg0.getParameter("relation_type")));
			relations.setId(relationsId);
			service.add(relations);
		}
		mav = new ModelAndView("skos/relations_manager_result");
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	
}
