package com.openDams.admin.controller;


import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Companies;
import com.openDams.bean.Departments;
import com.openDams.bean.Roles;
import com.openDams.bean.UserRoles;
import com.openDams.bean.Users;
import com.openDams.bean.UsersProfile;
import com.openDams.security.RoleTester;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;
import com.regesta.framework.io.MD5;

public class UsersController implements Controller{

	private OpenDamsService service ;
	private String default_password;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		String action = arg0.getParameter("action");
		System.out.println("action = "+action);
		UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();	
		if(action==null){
			System.out.println("limit = "+arg0.getParameter("limit"));
			System.out.println("query = "+arg0.getParameter("query"));
			mav = new ModelAndView("admin/user_managment/users");
			
			int limit = 0;
			limit = new Integer(arg0.getParameter("limit"));
			Integer start = 0;
			try {
				start = Integer.parseInt(arg0.getParameter("start"));
			} catch (Exception e) {
			}
			int tot = 0;
			if(arg0.getParameter("query")!=null && !arg0.getParameter("query").trim().equals("")){
				String query = arg0.getParameter("query");
				if(user.getIdCompany()==RoleTester.SYSTEM_ADMIN_GOD){
					System.out.println("SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user where users.username like '"+query+"%' or users_profile.name like '"+query+"%' or users_profile.lastname like '"+query+"%' order by users_profile.lastname");
					tot = service.getCountFromSQL("SELECT count(users.id_user) FROM users inner join users_profile on users.id_user=users_profile.ref_id_user where users.username like '"+query+"%' or users_profile.name like '"+query+"%' or users_profile.lastname like '"+query+"%'").intValue();
					mav.addObject("usersList", service.getPagedListFromSQL(Users.class,"SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user where users.username like '"+query+"%' or users_profile.name like '"+query+"%' or users_profile.lastname like '"+query+"%' order by users_profile.lastname", start, limit));
				}else{	
					System.out.println("SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user where ref_id_company="+user.getIdCompany()+"and (users.username like '"+query+"%' or users_profile.name like '"+query+"%' or users_profile.lastname like '"+query+"%') order by users_profile.lastname");
					tot = service.getCountFromSQL("SELECT count(users.id_user) FROM users inner join users_profile on users.id_user=users_profile.ref_id_user where ref_id_company="+user.getIdCompany()+"and (users.username like '"+query+"%' or users_profile.name like '"+query+"%' or users_profile.lastname like '"+query+"%')").intValue();
					mav.addObject("usersList",service.getPagedListFromSQL(Users.class, "SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user where ref_id_company="+user.getIdCompany()+"and (users.username like '"+query+"%' or users_profile.name like '"+query+"%' or users_profile.lastname like '"+query+"%') order by users_profile.lastname",start, limit));
				}	
			}else{
				if(user.getIdCompany()==RoleTester.SYSTEM_ADMIN_GOD){
					System.out.println("SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user order by users_profile.lastname");
					tot = service.getCountFromSQL("SELECT count(users.id_user) FROM users inner join users_profile on users.id_user=users_profile.ref_id_user").intValue();
					mav.addObject("usersList", service.getPagedListFromSQL(Users.class,"SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user order by users_profile.lastname", start, limit));
				}else{		
					System.out.println("SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user where users.ref_id_company="+user.getIdCompany()+" order by users_profile.lastname");
					tot = service.getCountFromSQL("SELECT count(users.id_user) FROM users inner join users_profile on users.id_user=users_profile.ref_id_user where users.ref_id_company="+user.getIdCompany()).intValue();
					mav.addObject("usersList",service.getPagedListFromSQL(Users.class, "SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user where users.ref_id_company="+user.getIdCompany()+" order by users_profile.lastname",start, limit));
				}
			}
			mav.addObject("total",tot);
			return mav;
		}else if(action.equals("delete")){
			System.out.println("sto per cancellare utente = "+arg0.getParameter("id_user"));
			mav = new ModelAndView("admin/user_managment/success");
			Users users = (Users)service.getObject((Users.class), new Integer(arg0.getParameter("id_user")));
			service.remove(users);
			return mav;
		}else if(action.equals("add")){
			System.out.println("sto per aggiungere nuovo utente");
			Users users = new Users();
			users.setPassword(MD5.encode(default_password));
			UsersProfile usersProfile = new UsersProfile();
			usersProfile.setUsers(users);
			if(arg0.getParameter("username")!=null){
				users.setUsername(arg0.getParameter("username"));
			}
			if(arg0.getParameter("name")!=null){
				usersProfile.setName(arg0.getParameter("name"));
			}
			if(arg0.getParameter("lastname")!=null){
				usersProfile.setLastname(arg0.getParameter("lastname"));
			}
			if(arg0.getParameter("ref_id_company")!=null){
				Companies companies = (Companies)service.getObject(Companies.class, new Integer(arg0.getParameter("ref_id_company")));
				users.setCompanies(companies);
			}
			if(arg0.getParameter("ref_id_department")!=null){
				Departments departments = (Departments)service.getObject(Departments.class, new Integer(arg0.getParameter("ref_id_department")));
				users.setDepartments(departments);
			}
			if(arg0.getParameter("email")!=null){
				usersProfile.setEmail(arg0.getParameter("email"));
			}else{
				usersProfile.setEmail("");
			}
			if(arg0.getParameter("birth_date")!=null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		        Date date = sdf.parse(arg0.getParameter("birth_date"));
		        usersProfile.setBirthDate(date);
			}
			if(arg0.getParameter("telephone_number")!=null){
				usersProfile.setTelephoneNumber(arg0.getParameter("telephone_number"));
			}else{
				usersProfile.setTelephoneNumber("");
			}
			if(arg0.getParameter("active")!=null){
				users.setActive(Boolean.parseBoolean(arg0.getParameter("active")));
			}else{
				users.setActive(true);
			}
			if(arg0.getParameter("language")!=null){
				usersProfile.setLanguage(arg0.getParameter("language"));
			}else{
				usersProfile.setLanguage("IT");
			}
			UserRoles userRoles = new UserRoles();
			userRoles.setUsers(users);
			userRoles.setRoles((Roles)service.getObject(Roles.class, new Integer(4)));
			users.setUserRoles(userRoles);
			users.setUsersProfile(usersProfile);
			service.add(users);
			mav = new ModelAndView("admin/user_managment/success");
			return mav;
		}else {
			//update
			System.out.println("sto per modificare utente = "+arg0.getParameter("id_user"));
			Users users = (Users)service.getObject((Users.class), new Integer(arg0.getParameter("id_user")));
			if(arg0.getParameter("username")!=null){
				users.setUsername(arg0.getParameter("username"));
			}
			if(arg0.getParameter("name")!=null){
				users.getUsersProfile().setName(arg0.getParameter("name"));
			}
			if(arg0.getParameter("lastname")!=null){
				users.getUsersProfile().setLastname(arg0.getParameter("lastname"));
			}
			if(arg0.getParameter("ref_id_company")!=null){
				Companies companies = (Companies)service.getObject(Companies.class, new Integer(arg0.getParameter("ref_id_company")));
				users.setCompanies(companies);
			}
			if(arg0.getParameter("ref_id_department")!=null){
				Departments departments = (Departments)service.getObject(Departments.class, new Integer(arg0.getParameter("ref_id_department")));
				users.setDepartments(departments);
			}
			if(arg0.getParameter("email")!=null){
				users.getUsersProfile().setEmail(arg0.getParameter("email"));
			}
			if(arg0.getParameter("birth_date")!=null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		        Date date = sdf.parse(arg0.getParameter("birth_date"));
				users.getUsersProfile().setBirthDate(date);
			}
			if(arg0.getParameter("telephone_number")!=null){
				users.getUsersProfile().setTelephoneNumber(arg0.getParameter("telephone_number"));
			}
			if(arg0.getParameter("active")!=null){
				users.setActive(Boolean.parseBoolean(arg0.getParameter("active")));
			}
			if(arg0.getParameter("language")!=null){
				users.getUsersProfile().setLanguage(arg0.getParameter("language"));
			}
			service.update(users);
			mav = new ModelAndView("admin/user_managment/success");
			return mav;
		}
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setDefault_password(String defaultPassword) {
		default_password = defaultPassword;
	}
	
}
