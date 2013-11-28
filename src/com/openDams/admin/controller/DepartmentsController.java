package com.openDams.admin.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Departments;
import com.openDams.services.OpenDamsService;

public class DepartmentsController implements Controller{

	private OpenDamsService service ;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		mav = new ModelAndView("admin/user_managment/departments");
		mav.addObject("departmentsList", service.getList(Departments.class));
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	
}
