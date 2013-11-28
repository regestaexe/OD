package com.openDams.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserDetails extends User {

	private static final long serialVersionUID = 6035604765653413831L;
	private int id;
	private String name = "";
	private String lastname = "";
	private String language = "";
	private int idCompany;
	private String company = "";
	private String departmentAcronym = "";
	private String imageLogo = "";

	public UserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getImageLogo() {
		return imageLogo;
	}

	public void setImageLogo(String imageLogo) {
		this.imageLogo = imageLogo;
	}

	public void setDepartmentAcronym(String departmentAcronym) {
		this.departmentAcronym = departmentAcronym;
	}

	public String getDepartmentAcronym() {
		return departmentAcronym;
	}

	public int getIdCompany() {
		return idCompany;
	}

	public void setIdCompany(int idCompany) {
		this.idCompany = idCompany;
	}

}
