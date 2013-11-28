package com.openDams.relations.configuration;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;


public class RelationsConfiguration  implements ServletContextAware{	
	private static final long serialVersionUID = 8628132333422887069L;
	private ServletContext servletContext;
	private boolean use_external_conf_location = false;	
	private String configuration_location = "/WEB-INF/configuration/relations";
	private String  real_path = "";
	private String  file_name = "relations_configuration.xml";
	public RelationsConfiguration(){
	}
	public void setConfiguration_location(String configurationLocation) {
		configuration_location = configurationLocation;
	}
	public String getConfiguration_location() {
		return configuration_location;
	}
	public ServletContext getServletContext() {
		return servletContext;
	}
	public String getReal_path() {
		return real_path;
	}
	public void setServletContext(ServletContext arg0) {
	 		servletContext = arg0;
	 		if(!use_external_conf_location)
				real_path = servletContext.getRealPath("");
	}
	public boolean isUse_external_conf_location() {
		return use_external_conf_location;
	}
	public void setUse_external_conf_location(boolean useExternalConfLocation) {
		use_external_conf_location = useExternalConfLocation;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String fileName) {
		file_name = fileName;
	}
}
