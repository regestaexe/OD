package com.openDams.documental.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.services.OpenDamsService;

public class DocumentsController implements Controller{
	private  OpenDamsService service ;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = new ModelAndView("documental/documents");
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
}
