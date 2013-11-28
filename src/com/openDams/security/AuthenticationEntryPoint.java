package com.openDams.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class AuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint{

	@Override
	public void commence(HttpServletRequest arg0, HttpServletResponse arg1,AuthenticationException arg2) throws IOException, ServletException {
		 
		System.out.println("AuthenticationEntryPoint.commence() arg0.getServletPath "+arg0.getServletPath() );
		if (arg0.getServletPath().startsWith("/ajax/")) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>chiamata ajax");
			 arg1.sendError(601, "");			 
	     } else {
	    	 System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>chiamata classica");
	    	 super.commence(arg0, arg1, arg2);
	     }
		
	}
}
