package com.openDams.documental.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.ArchiveIdentity;
import com.openDams.bean.Archives;
import com.openDams.endpoint.managing.OpenDamsEndPointManagerFactoryProvider;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;

public class WorkspaceController implements Controller{
	private  OpenDamsService service ;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = new ModelAndView("documental/workspace");
		Archives archives = (Archives)service.getObject(Archives.class, new Integer(arg0.getParameter("idArchive")));
		mav.addObject("archives", archives);
		UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();		
		List<Archives> archiveList = (List<Archives>)service.getListFromSQL(Archives.class,"SELECT * FROM archives  inner join archive_user_role on archives.id_archive=archive_user_role.ref_id_archive where archive_user_role.ref_id_user="+user.getId()+" and archives.ref_id_archive_identity="+ArchiveIdentity.THESAURUS+" order by archives.archive_order;");
		mav.addObject("archiveList",archiveList );
		List endPointList =  OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getAllowedEndPointList(new Integer(arg0.getParameter("idArchive")));
		mav.addObject("endPointList",endPointList );
		//mav.addObject("unlinked", service.getCountFromSQL("SELECT count(id_record) FROM records  where id_record not in (select ref_id_record_1 from relations) and id_record not in (select ref_id_record_2 from relations)"));
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
}
