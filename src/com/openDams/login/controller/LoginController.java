package com.openDams.login.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Companies;
import com.openDams.services.OpenDamsService;

public class LoginController implements Controller{
	private OpenDamsService service ;
	
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = new ModelAndView("login");			
		mav.addObject("companyList", service.getList(Companies.class));
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
}
