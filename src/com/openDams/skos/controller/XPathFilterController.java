package com.openDams.skos.controller;


import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Records;
import com.openDams.services.OpenDamsService;

public class XPathFilterController implements Controller{
	private  OpenDamsService service ;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		String[] id_records =  arg0.getParameter("id_records").split("#");
		ArrayList<Records> results = null;
		for (int i = 0; i < id_records.length; i++) {
			String id_record = id_records[i].trim();
			if(!id_record.equals("")){
				Records records = (Records)service.getObject(Records.class, new Integer(id_record));
				if(records.xpathFilter(arg0.getParameter("xPath"))){
					if(results == null)
						results = new ArrayList<Records>();
					results.add(records);
				}
			}
		}
		mav = new ModelAndView("skos/resultsXpath");
		mav.addObject("results", results);
		
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	
}
