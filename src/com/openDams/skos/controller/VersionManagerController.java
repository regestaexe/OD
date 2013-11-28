package com.openDams.skos.controller;


import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Records;
import com.openDams.bean.RecordsVersion;
import com.openDams.services.OpenDamsService;
import com.regesta.framework.util.DateUtility;
import com.regesta.framework.xml.XMLBuilder;

public class VersionManagerController implements Controller{
	private OpenDamsService service ;	
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		Records records = (Records)service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));
		if(arg0.getParameter("action")==null){
			mav = new ModelAndView("skos/version_manager");					
			mav.addObject("records", records);
		}else if(arg0.getParameter("action").equalsIgnoreCase("new_version")){
			insertNewVersion(records,null);			
			mav = new ModelAndView("skos/version_manager_result");					
		}else if(arg0.getParameter("action").equalsIgnoreCase("restore_version")){
			RecordsVersion recordsVersion = (RecordsVersion)service.getObject(RecordsVersion.class, new Integer(arg0.getParameter("idVersion")));			
			insertNewVersion(records,recordsVersion);
			mav = new ModelAndView("skos/version_manager_result");		
		}else if(arg0.getParameter("action").equalsIgnoreCase("delete_version")){
			RecordsVersion recordsVersion = (RecordsVersion)service.getObject(RecordsVersion.class, new Integer(arg0.getParameter("idVersion")));
			service.remove(recordsVersion);
		}
		return mav;
	}
	private void insertNewVersion(Records records,RecordsVersion  oldRecordsVersion) throws Exception{
		RecordsVersion recordsVersion = new RecordsVersion();
		recordsVersion.setRecords(records);
		recordsVersion.setTitle(records.getTitle());
		recordsVersion.setXml(records.getXml());
		recordsVersion.setVersionDate(new Date());
		int version = 1;
		if(records.getRecordsVersions()!= null)
			version = records.getRecordsVersions().size()+1;
		recordsVersion.setVersion(version);			
		recordsVersion.setRecords(records);
		service.add(recordsVersion);
		if(oldRecordsVersion!=null){
			records.setTitle(oldRecordsVersion.getTitle());
			records.setXml(oldRecordsVersion.getXml());
		}
		String xml = new String(records.getXml(),"UTF-8");
		XMLBuilder xmlBuilder = new XMLBuilder(xml, "UTF-8");			
		String concept = "Concept" ;
		if(xmlBuilder.testNode("/rdf:RDF/skos:ConceptScheme")){
			concept = "ConceptScheme";
		}
		int countChangeNote = records.getXMLReader().getNodeCount("/rdf:RDF/skos:"+concept+"/skos:changeNote");
        boolean versionEdited = false;
		 for(int i=0;i<countChangeNote;i++){
			 if(records.getXMLReader().getNodeValue("/rdf:RDF/skos:"+concept+"/skos:changeNote["+(i+1)+"]/rdf:value/text()").indexOf("version")!=-1){
				 xmlBuilder.insertValueAt("/rdf:RDF/skos:"+concept+"/skos:changeNote["+(i+1)+"]/dc:date/text()", DateUtility.getSystemDate());
				 xmlBuilder.insertValueAt("/rdf:RDF/skos:"+concept+"/skos:changeNote["+(i+1)+"]/rdf:value/text()", "version "+version); 
				 versionEdited = true;
			 }				 
		 }
		 if(!versionEdited){
			 xmlBuilder.insertNode("/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource']["+(countChangeNote+1)+"]/dc:date/text()", DateUtility.getSystemDate());
			 xmlBuilder.insertNode("/rdf:RDF/skos:"+concept+"/skos:changeNote[@rdf:parseType='Resource']["+(countChangeNote+1)+"]/rdf:value/text()", "version "+version);
		 }
		records.setXml(xmlBuilder.getXML("UTF-8").getBytes());
		records.getRecordsVersions().add(recordsVersion);
		service.add(records);
		
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
}
