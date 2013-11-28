package com.openDams.search.configuration;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;


public class SearchConfiguration  implements ServletContextAware{	
	private static final long serialVersionUID = 8628132333422887069L;
	private ServletContext servletContext;
	private ConfigurationReader configurationSearchReader = null;
	private boolean use_external_conf_location = false;	
	private String configuration_location = "/WEB-INF/configuration/search";
	private String  real_path = "";
	private String  file_name = "search_configuration.xml";
	private boolean use_test_settings = false;
	public SearchConfiguration(){
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
	public ConfigurationReader getConfigurationSearchReader() {
		return configurationSearchReader;
	}
	public void setConfigurationSearchReader(
			ConfigurationReader configurationSearchReader) {
		this.configurationSearchReader = configurationSearchReader;
	}
	public boolean isUse_test_settings() {
		return use_test_settings;
	}
	public void setUse_test_settings(boolean use_test_settings) {
		this.use_test_settings = use_test_settings;
	}
}
