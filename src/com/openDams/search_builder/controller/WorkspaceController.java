package com.openDams.search_builder.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.ArchiveIdentity;
import com.openDams.bean.ArchiveType;
import com.openDams.bean.Archives;
import com.openDams.search.configuration.ConfigurationReader;
import com.openDams.search_builder.utility.ArchiveUtils;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;

public class WorkspaceController implements Controller{
	private  OpenDamsService service ;
	private ConfigurationReader configurationSearchReader;
	private String excludeList;
	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = new ModelAndView("search_builder/workspace");
		UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();	
		//System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAACOMPANYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+user.getIdCompany());
		List<Archives> archiveList = (List<Archives>)service.getListFromSQL(Archives.class,"SELECT * FROM archives  inner join archive_user_role on archives.id_archive=archive_user_role.ref_id_archive where archive_user_role.ref_id_user="+user.getId()+" and not archives.ref_id_archive_identity="+ArchiveIdentity.COLLECTION+" "+ArchiveUtils.getExcludeSLQFields(excludeList)+" AND archives.ref_id_archive_type != "+ArchiveType.BASKET+" order by archives.archive_order;");
		mav.addObject("archiveList",archiveList );
		Archives searchArchive = (Archives)service.getListFromSQL(Archives.class,"SELECT * FROM archives where ref_id_archive_identity="+ArchiveIdentity.COLLECTION+";").get(0);
		mav.addObject("searchArchive",searchArchive );
		String requestArchives ="";
		for(int i=0;i<archiveList.size();i++){
			    Archives archives = archiveList.get(i);
			    requestArchives+=archives.getIdArchive()+";";
				mav.addObject("searchElements_"+archives.getIdArchive(), configurationSearchReader.getFilteredElementsList(user.getIdCompany(),archives.getIdArchive()+";"));
		}
		mav.addObject("archiveList",archiveList);
		mav.addObject("searchElements", configurationSearchReader.getFilteredElementsList(user.getIdCompany(),requestArchives));
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setConfigurationSearchReader(
			ConfigurationReader configurationSearchReader) {
		this.configurationSearchReader = configurationSearchReader;
	}
	public void setExcludeList(String excludeList) {
		this.excludeList = excludeList;
	}
}
