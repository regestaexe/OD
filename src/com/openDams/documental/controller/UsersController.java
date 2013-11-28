package com.openDams.documental.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.UsersProfile;
import com.openDams.services.OpenDamsService;
import com.openDams.utility.StringsUtils;

public class UsersController implements Controller, ApplicationContextAware {
	private OpenDamsService service;
	private ApplicationContext applicationContext;
	private String departments;

	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if (arg0.getParameter("action") != null) {
			if (arg0.getParameter("action").startsWith("listaAutori")) {
				mav = new ModelAndView("documental/json/simpleJsonResponse");
				String query ="";
				if (arg0.getParameter("action").equals("listaAutoriDipartimento")) {
					query ="select users.id_user, users_profile.name, users_profile.lastname from users_profile inner join users on users.id_user=users_profile.ref_id_user inner join departments on  departments.id_department=users.ref_id_department  where departments.acronym='"+arg0.getParameter("dipartimento")+"'"; 
				}else{
					departments = departments.replaceAll(",", " OR ");
					query =  "select  users_profile.name, users_profile.lastname  from users_profile inner join users on users.id_user=users_profile.ref_id_user where users.ref_id_department = "+departments;	
				}
					String result ="[";
					List<UsersProfile> profili = (List<UsersProfile>)service.getListFromSQL(UsersProfile.class,query);
					for (UsersProfile UsersProfile : profili) {
						result += StringsUtils.escapeJson(UsersProfile.getName()+"");
					}
					result+="]";
				mav.addObject("result",result);
			}
		}
		return mav;
	}

	public void setService(OpenDamsService service) {
		this.service = service;
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		applicationContext = arg0;

	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	 
}
