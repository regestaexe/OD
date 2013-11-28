package com.openDams.admin.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.Companies;
import com.openDams.bean.Departments;
import com.openDams.security.RoleTester;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;

public class AdminToolsController implements Controller{
	private OpenDamsService service ;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;		
		mav = new ModelAndView("admin/adminTools");
		UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
		if(user.getIdCompany()==RoleTester.SYSTEM_ADMIN_GOD){
			mav.addObject("departmentsList", service.getList(Departments.class));
			mav.addObject("companiesList", service.getList(Companies.class));
			mav.addObject("archiveList", service.getListFromSQL(Archives.class,"SELECT * FROM archives order by archives.archive_order;"));
		}else{		
			mav.addObject("departmentsList", service.getListFromSQL(Departments.class,"SELECT * FROM departments where ref_id_company="+user.getIdCompany()+";"));
			mav.addObject("companiesList", service.getListFromSQL(Companies.class,"SELECT * FROM companies where id_company="+user.getIdCompany()+";"));
			mav.addObject("archiveList", service.getListFromSQL(Archives.class,"SELECT * FROM archives  inner join archive_user_role on archives.id_archive=archive_user_role.ref_id_archive where archive_user_role.ref_id_user="+user.getId()+" order by archives.archive_order;"));
		}	
		
		
		
		
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
}
