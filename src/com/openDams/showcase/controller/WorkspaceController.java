package com.openDams.showcase.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;

public class WorkspaceController implements Controller{
	private OpenDamsService service;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = new ModelAndView("showcase/workspace");
		UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
		mav.addObject("archiveList", service.getListFromSQL(Archives.class,"SELECT * FROM archives  inner join archive_user_role on archives.id_archive=archive_user_role.ref_id_archive where archive_user_role.ref_id_user="+user.getId()+" order by archives.archive_order;"));
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
}
