package com.openDams.admin.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Companies;
import com.openDams.bean.Departments;
import com.openDams.security.RoleTester;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;

public class WorkspaceController implements Controller{
	private OpenDamsService service ;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;		
		mav = new ModelAndView("admin/workspace");
		
		
		UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
		if(user.getIdCompany()==RoleTester.SYSTEM_ADMIN_GOD){
			mav.addObject("departmentsList", service.getList(Departments.class));
			mav.addObject("companiesList", service.getList(Companies.class));
		}else{		
			mav.addObject("departmentsList", service.getListFromSQL(Departments.class,"SELECT * FROM departments where ref_id_company="+user.getIdCompany()+";"));
			mav.addObject("companiesList", service.getListFromSQL(Companies.class,"SELECT * FROM companies where id_company="+user.getIdCompany()+";"));
		}	
		return mav;
		
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
}

