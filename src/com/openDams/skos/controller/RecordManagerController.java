package com.openDams.skos.controller;


import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.RecordTypes;
import com.openDams.bean.Records;
import com.openDams.bean.XmlId;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;
import com.regesta.framework.xml.XMLBuilder;

public class RecordManagerController implements Controller{
	private  OpenDamsService service ;
	private  TitleManager titleManager ;
	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		
		try {
			Enumeration<String> names = arg0.getParameterNames();
			Hashtable<String, String> mapSKOS = new Hashtable<String, String>();
			while (names.hasMoreElements()) {
				String string = (String) names.nextElement();			
				if(string.startsWith("xmlns")){				
					mapSKOS.put(string.replaceAll("xmlns:",""),arg0.getParameter(string));
				}
			}
			XMLBuilder xmlBuilder = new XMLBuilder(arg0.getParameter("xml_root"),mapSKOS,arg0.getParameter("encoding"));
			names = arg0.getParameterNames();
			while (names.hasMoreElements()) {
				String string = (String) names.nextElement();			
				if(string.startsWith("/"+arg0.getParameter("xml_root")+"/")){	
					xmlBuilder.insertNode(string, arg0.getParameter(string));
				}
			}
			
			 Records record =  null;
			if(arg0.getParameter("idRecord")!=null && !arg0.getParameter("idRecord").trim().equals("") && arg0.getParameter("action")==null){
				//UPDATE
				record = (Records)service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));
				record.setXml(xmlBuilder.getXML(arg0.getParameter("encoding"),true).getBytes("UTF-8"));
			//	record.setTitle(titleManager.getTitle(record.getXMLReader(),new Integer(arg0.getParameter("idArchive"))));
				record.setModifyDate(new Date());
				service.update(record);
			}else{
				if(arg0.getParameter("action")!=null && arg0.getParameter("action").equalsIgnoreCase("delete")){
					record = (Records)service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));
					record.setDeleted(true);
					record.setModifyDate(new Date());
					//service.remove(record);
					service.update(record);
				}else if(arg0.getParameter("action")!=null && arg0.getParameter("action").equalsIgnoreCase("check_delete")){
					mav = new ModelAndView("skos/check_delete");
					record = (Records)service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));
					List<Records> relationRecords = (List<Records>) service.getListFromSQL(Records.class, "SELECT records.* FROM (SELECT records.* FROM records inner join relations on records.id_record=relations.ref_id_record_1 where (ref_id_record_1="+record.getIdRecord()+" or ref_id_record_2="+record.getIdRecord()+") and not records.id_record="+record.getIdRecord()+" and not (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=2 or relations.ref_id_relation_type=10 or relations.ref_id_relation_type=11 or relations.ref_id_relation_type=9) union SELECT records.* FROM records inner join relations on records.id_record=relations.ref_id_record_2 where (ref_id_record_1="+record.getIdRecord()+" or ref_id_record_2="+record.getIdRecord()+") and not records.id_record="+record.getIdRecord()+" and not (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=2 or relations.ref_id_relation_type=10 or relations.ref_id_relation_type=11 or relations.ref_id_relation_type=9)) records order by records.ref_id_archive;");
					mav.addObject("records", record);
					mav.addObject("relationRecords", relationRecords);
					mav.addObject("titleManager", titleManager);
					return mav;
				}else{
					Archives archives = (Archives)service.getObject(Archives.class, new Integer(arg0.getParameter("idArchive")));
					record = new Records();
					record.setXml(xmlBuilder.getXML(arg0.getParameter("encoding")).getBytes("UTF-8"));
					record.setRecordTypes((RecordTypes)service.getObject(RecordTypes.class,new Integer(arg0.getParameter("idRecordType"))));
					record.setArchives(archives);
			//		record.setTitle(titleManager.getTitle(record.getXMLReader(),new Integer(arg0.getParameter("idArchive"))));
					record.setCreationDate(new Date());
					record.setModifyDate(new Date());
					record.setPosition(0);
					record.setDepth(0);
					XmlId xmlId = archives.getXmlId();
					xmlId.setIdXml(xmlId.getIdXml()+1);
					service.add(record);
					service.update(xmlId);
				}
				
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		mav = new ModelAndView("skos/record_manager_result");
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	
}
