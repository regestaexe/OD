package com.openDams.skos.controller;


import java.util.Date;

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

public class InsertController implements Controller{
	private  OpenDamsService service ;
	private  TitleManager titleManager ;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if(arg0.getParameter("action")==null || !arg0.getParameter("action").equalsIgnoreCase("insert")){
			mav = new ModelAndView("skos/insert");
			mav.addObject("archiveList", service.getList(Archives.class));
		}else{
			Records record = new Records();
			String  xml=arg0.getParameter("xml_to_insert");
			record.setXml(xml.getBytes());
			//PARAMETRIZZARE LA TIPOLOGIA DEL RECORD!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			record.setRecordTypes((RecordTypes)service.getObject(RecordTypes.class, new Integer(2)));
			Archives archives = (Archives)service.getObject(Archives.class, new Integer(arg0.getParameter("id_archive")));
			record.setArchives(archives);		
		//	record.setTitle(titleManager.getTitle(record.getXMLReader(),new Integer(arg0.getParameter("id_archive"))));
			record.setCreationDate(new Date());
			record.setModifyDate(new Date());
			record.setPosition(0);
			record.setDepth(0);
			XmlId xmlId = archives.getXmlId();
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+xmlId+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			xmlId.setIdXml(xmlId.getIdXml()+1);
			service.add(record);
			service.update(xmlId);
			mav = new ModelAndView("skos/insert");
			mav.addObject("archiveList", service.getList(Archives.class));
		}
		return mav;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
}
