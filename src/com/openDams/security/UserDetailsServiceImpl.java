package com.openDams.security;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openDams.bean.Users;
import com.openDams.services.OpenDamsService;

public class UserDetailsServiceImpl implements UserDetailsService {
	private OpenDamsService service ;
	private Assembler assembler;
  	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
	    try {
			Users users = (Users)service.getListFromSQL(Users.class, "SELECT * FROM users where username='"+username+"';").get(0);
			if (users == null)
			  throw new UsernameNotFoundException("user not found");    
			return  assembler.buildUserFromUserEntity(users);
		} catch (HibernateException e) {
			throw new UsernameNotFoundException("user not found");  
		}catch (IndexOutOfBoundsException e) {
			throw new UsernameNotFoundException("user not found");
		}
  	}
  	public UserDetails loadUserByUsernameCompany(String username,String company) throws UsernameNotFoundException, DataAccessException {
  		try {
  			System.out.println("SELECT * FROM users where username='"+username+"' and ref_id_company="+company+";");
			Users users = (Users)service.getListFromSQL(Users.class, "SELECT * FROM users where username='"+username+"' and ref_id_company="+company+";").get(0);
			if (users == null)
			  throw new UsernameNotFoundException("user not found");    
			return  assembler.buildUserFromUserEntity(users);
		} catch (HibernateException e) {
			throw new UsernameNotFoundException("user not found");  
		}catch (IndexOutOfBoundsException e) {
			throw new UsernameNotFoundException("user not found");
		}
  	} 	
  	public UserDetails loadUserFromSSO(String username) throws UsernameNotFoundException, DataAccessException {
  		try {
			Users users = (Users)service.getListFromSQL(Users.class, "SELECT * FROM users where username='"+username+"'").get(0);
			if (users == null)
			  throw new UsernameNotFoundException("user not found");    
			return  assembler.buildUserFromUserEntity(users);
		} catch (HibernateException e) {
			throw new UsernameNotFoundException("user not found");  
		}catch (IndexOutOfBoundsException e) {
			throw new UsernameNotFoundException("user not found");
		}
  	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setAssembler(Assembler assembler) {
		this.assembler = assembler;
	}
}