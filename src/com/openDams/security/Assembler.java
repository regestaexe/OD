package com.openDams.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;

import com.openDams.bean.Users;

public class Assembler {

	public User buildUserFromUserEntity(Users user) {
		String username = user.getUsername();
		String password = user.getPassword();
		boolean enabled = user.isActive();
		boolean accountNonExpired = user.isActive();
		boolean credentialsNonExpired = user.isActive();
		boolean accountNonLocked = user.isActive();
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		/*for (UserRoles role : user.getUserRoleses()) {
			authorities.add(new GrantedAuthorityImpl(role.getRoles().getRoleName()));
		}*/
		authorities.add(new GrantedAuthorityImpl(user.getUserRoles().getRoles().getRoleName()));
		UserDetails userS = new UserDetails(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		userS.setId(user.getIdUser());
		userS.setName(user.getUsersProfile().getName());
		userS.setLastname(user.getUsersProfile().getLastname());
		userS.setLanguage(user.getUsersProfile().getLanguage());
		userS.setCompany(user.getCompanies().getCompanyName());
		userS.setIdCompany(user.getCompanies().getIdCompany());
		userS.setImageLogo(user.getCompanies().getImageName());
		userS.setDepartmentAcronym((user.getDepartments() != null ? user.getDepartments().getAcronym() : ""));
		System.out.println("Assembler.buildUserFromUserEntity() " + userS);
		return userS;
	}
}
