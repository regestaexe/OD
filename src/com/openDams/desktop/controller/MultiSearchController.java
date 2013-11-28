package com.openDams.desktop.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.ArchiveType;
import com.openDams.bean.Archives;
import com.openDams.search.configuration.ConfigurationReader;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;

public class MultiSearchController implements Controller{
	private OpenDamsService service ;
	private ConfigurationReader configurationSearchReader;
	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
		if(arg0.getParameter("action")==null){
			mav = new ModelAndView("desktop/multiSearch");		
			List<Archives> archiveList = (List<Archives>)service.getListFromSQL(Archives.class,"SELECT * FROM archives  inner join archive_user_role on archives.id_archive=archive_user_role.ref_id_archive where archive_user_role.ref_id_user="+user.getId()+"  and archives.ref_id_archive_type="+ArchiveType.DOCUMENTAL+" order by archives.archive_order;");
			String requestArchives ="";
			for(int i=0;i<archiveList.size();i++){
				    Archives archives = archiveList.get(i);
					requestArchives+=archives.getIdArchive()+";";
			}
			mav.addObject("archiveList",archiveList);
			mav.addObject("searchElements", configurationSearchReader.getFilteredElementsList(user.getIdCompany(),requestArchives));
		}else if(arg0.getParameter("action").equals("filter")){
			System.out.println("requestArchives "+arg0.getParameter("requestArchives"));
			mav = new ModelAndView("desktop/filteredFields");	
			mav.addObject("searchElements", configurationSearchReader.getFilteredElementsList(user.getIdCompany(),arg0.getParameter("requestArchives")));
		}
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setConfigurationSearchReader(
			ConfigurationReader configurationSearchReader) {
		this.configurationSearchReader = configurationSearchReader;
	}
}
