package com.openDams.security;

import com.openDams.bean.Roles;
import com.openDams.services.OpenDamsService;

public class ArchiveRoleTester {
   public static final String ROLE_ADMIN  = "ROLE_ADMIN";
   public static final String ROLE_GOD    = "ROLE_GOD";
   public static final String ROLE_USER   = "ROLE_USER";
   public static final String ROLE_READER = "ROLE_READER";
   private static OpenDamsService service ;
   public static boolean testEditing(int id_user,int idArchive){
	 try {
		 Roles roles = (Roles)service.getListFromSQL(Roles.class,"SELECT roles.* FROM roles inner join archive_user_role on roles.id_role = archive_user_role.ref_id_role where archive_user_role.ref_id_archive="+idArchive+" and archive_user_role.ref_id_user="+id_user+";").get(0);
		 String role = roles.getRoleName();
		 if(role.equals(ROLE_ADMIN)){
			 return true;
		 }else if(role.equals(ROLE_GOD)){
			 return true;
		 }else if(role.equals(ROLE_USER)){
			 return true;
		 }
	 } catch (Exception e) {
		// e.printStackTrace();
	 }
	 return false;
   }
   public static String getUserArchiveRole(int id_user,int idArchive){
	try {
		Roles roles = (Roles)service.getListFromSQL(Roles.class,"SELECT roles.* FROM roles inner join archive_user_role on roles.id_role = archive_user_role.ref_id_role where archive_user_role.ref_id_archive="+idArchive+" and archive_user_role.ref_id_user="+id_user+";").get(0);
		String role = roles.getRoleName();
		return role;
	} catch (Exception e) {
		return "";
	}
   }
   public static boolean testEditXML(int id_user,int idArchive){
		 try {
			 Roles roles = (Roles)service.getListFromSQL(Roles.class,"SELECT roles.* FROM roles inner join archive_user_role on roles.id_role = archive_user_role.ref_id_role where archive_user_role.ref_id_archive="+idArchive+" and archive_user_role.ref_id_user="+id_user+";").get(0);
			 String role = roles.getRoleName();
			 if(role.equals(ROLE_ADMIN)){
				 return true;
			 }else if(role.equals(ROLE_GOD)){
				 return true;
			 }
		 } catch (Exception e) {
			// e.printStackTrace();
		 }
		 return false;
	   }
   public void setService(OpenDamsService service) {
	   ArchiveRoleTester.service = service;
   }
}
