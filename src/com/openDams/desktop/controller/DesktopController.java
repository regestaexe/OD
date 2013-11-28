package com.openDams.desktop.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.dao.OpenDamsServiceProvider;
import com.openDams.security.UserDetails;

public class DesktopController implements Controller{
	private String page;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;		
		mav = new ModelAndView(page);
		UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
		mav.addObject("archiveList", OpenDamsServiceProvider.getService().getListFromSQL(Archives.class,"SELECT * FROM archives  inner join archive_user_role on archives.id_archive=archive_user_role.ref_id_archive where archive_user_role.ref_id_user="+user.getId()+" order by archives.archive_order;"));
		return mav;
	}
	public void setPage(String page) {
		this.page = page;
	}
}
