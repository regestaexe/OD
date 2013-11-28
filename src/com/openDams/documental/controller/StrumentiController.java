package com.openDams.schedoni.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.services.OpenDamsService;

public class StrumentiController implements Controller{
	private  OpenDamsService service ;
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		

		mav = new ModelAndView("documental/strumenti/"+request.getParameter("page"));
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	
}
