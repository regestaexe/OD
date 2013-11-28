package com.openDams.admin.controller;


import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.ArchiveIdentity;
import com.openDams.bean.ArchiveUserRole;
import com.openDams.bean.ArchiveUserRoleId;
import com.openDams.bean.Archives;
import com.openDams.bean.Roles;
import com.openDams.bean.Users;
import com.openDams.security.RoleTester;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;

public class ArchivesUsersController implements Controller{

	private OpenDamsService service ;
	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if(arg0.getParameter("action")==null){
			mav = new ModelAndView("admin/user_managment/user_archives");
			if(service.getCountFromSQL("SELECT count(id_user) FROM users where id_user="+arg0.getParameter("id_user")+";").intValue()>0) {
				mav.addObject("user", service.getObject((Users.class), new Integer(arg0.getParameter("id_user"))));
			} 
			UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();	
			if(user.getIdCompany()==RoleTester.SYSTEM_ADMIN_GOD){
				mav.addObject("archivesList", service.getListFromSQL((Archives.class),"SELECT * FROM archives where not ref_id_archive_identity="+ArchiveIdentity.THESAURUS+" order by archive_order;"));			
				mav.addObject("thesaurusList", service.getListFromSQL((Archives.class),"SELECT * FROM archives where ref_id_archive_identity="+ArchiveIdentity.THESAURUS+" order by archive_order;"));
			}else{
				mav.addObject("archivesList", service.getListFromSQL((Archives.class),"SELECT archives.* FROM archives inner join company_archives on archives.id_archive=company_archives.ref_id_archive where not archives.ref_id_archive_identity="+ArchiveIdentity.THESAURUS+" and company_archives.ref_id_company="+user.getIdCompany()+" order by archives.archive_order;"));			
				mav.addObject("thesaurusList", service.getListFromSQL((Archives.class),"SELECT archives.* FROM archives inner join company_archives on archives.id_archive=company_archives.ref_id_archive where archives.ref_id_archive_identity="+ArchiveIdentity.THESAURUS+" and company_archives.ref_id_company="+user.getIdCompany()+" order by archives.archive_order;"));
				
			}		
			return mav;
		}else{
			service.executeUpdate("update user_roles set ref_id_role="+arg0.getParameter("role_nsid")+" where ref_id_user="+arg0.getParameter("id_user")+";");
			Users users = (Users)service.getObject((Users.class), new Integer(arg0.getParameter("id_user")));
			users.getArchiveUserRoles().clear();			
			Enumeration<String> enumeration = arg0.getParameterNames();
			while (enumeration.hasMoreElements()) {
				String string = (String) enumeration.nextElement();
				if(string.startsWith("ref_id_archive")){
					String id_archive = StringUtils.substringAfterLast(string,"_");
					if(!arg0.getParameter(string).equals("0")){
						Archives archives = (Archives)service.getObject(Archives.class, new Integer(id_archive));
						Roles roles = (Roles)service.getObject(Roles.class, new Integer(arg0.getParameter(string)));
						ArchiveUserRole archiveUserRole = new ArchiveUserRole(new ArchiveUserRoleId(archives.getIdArchive(), users.getIdUser(), roles.getIdRole()),archives,roles,users);
						users.getArchiveUserRoles().add(archiveUserRole);
					}
				}
				
			}
			service.update(users);
			mav = new ModelAndView("admin/user_managment/success");
			return mav;
		}
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	
}
