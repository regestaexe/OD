package com.openDams.index.configuration;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

import com.openDams.index.factory.IndexManager;
import com.openDams.index.factory.IndexManagerPool;


public class IndexConfiguration  implements ServletContextAware{	
	private static final long serialVersionUID = 8628132333422887069L;
	private ServletContext servletContext;
	private String jDBCDialect = null;
	private ConfigurationReader configurationIndexReader = null;
	private IndexManager indexManager = null;
	private IndexManagerPool indexManagerPool = null;
	private String index_name = "search_index";
	private String generic_index_name = "generic_search_index";
	private String configuration_location = "/WEB-INF/configuration";
	private boolean use_external_conf_location = false;	
	private String generic_configuration_location = "/WEB-INF/generic-configuration";
	private boolean use_db_for_generic_configuration = false;
	private boolean fsDirectory = false;
	private boolean use_external_index_location = false;	
	private String  index_location = "/WEB-INF/indexes";
	private boolean use_test_settings = false;
	private String  real_path = "";
	public IndexConfiguration(){
	}
	public void setjDBCDialect(String jDBCDialect) {
		this.jDBCDialect = jDBCDialect;
	}
	public void setConfigurationIndexReader(ConfigurationReader configurationIndexReader) {
			this.configurationIndexReader = configurationIndexReader;
	}
	public IndexManager getIndexManager(int idArchive) {
		try {
			return indexManagerPool.getIndexManager(idArchive);
		} catch (Exception e) {
			System.out.println("IL POOL NON FUNZIONA PRENDO L'INDEX MANAGER DI DEFAULT");
			return indexManager;
		}
	}
	public String getIndex_name() {
		return index_name;
	}
	public void setIndex_name(String indexName) {
		index_name = indexName;
	}
	public String getGeneric_index_name() {
		return generic_index_name;
	}
	public void setGeneric_index_name(String genericIndexName) {
		generic_index_name = genericIndexName;
	}
	public String getjDBCDialect() {
		return jDBCDialect;
	}
	public ConfigurationReader getConfigurationIndexReader() {
		return configurationIndexReader;
	}
	public void setConfiguration_location(String configurationLocation) {
		configuration_location = configurationLocation;
	}
	public void setUse_external_conf_location(boolean useExternalConfLocation) {
		use_external_conf_location = useExternalConfLocation;
	}
	public String getConfiguration_location() {
		return configuration_location;
	}
	public boolean isUse_external_conf_location() {
		return use_external_conf_location;
	}
	public String getGeneric_configuration_location() {
		return generic_configuration_location;
	}
	public void setGeneric_configuration_location(
			String genericConfigurationLocation) {
		generic_configuration_location = genericConfigurationLocation;
	}
	public boolean isUse_db_for_generic_configuration() {
		return use_db_for_generic_configuration;
	}
	public void setUse_db_for_generic_configuration(
			boolean useDbForGenericConfiguration) {
		use_db_for_generic_configuration = useDbForGenericConfiguration;
	}
	public boolean isFsDirectory() {
		return fsDirectory;
	}
	public void setFsDirectory(boolean fsDirectory) {
		this.fsDirectory = fsDirectory;
	}
	public String getIndex_location() {
		return index_location;
	}
	public void setIndex_location(String indexLocation) {
		index_location = indexLocation;
	}
	 public void setServletContext(ServletContext arg0) {
 		servletContext = arg0;
 		if(!use_external_index_location)
			real_path = servletContext.getRealPath("");
 	}
	public ServletContext getServletContext() {
		return servletContext;
	}
	public String getReal_path() {
		return real_path;
	}
	public boolean isUse_external_index_location() {
		return use_external_index_location;
	}
	public void setUse_external_index_location(boolean useExternalIndexLocation) {
		use_external_index_location = useExternalIndexLocation;
	}
	public IndexManagerPool getIndexManagerPool() {
		return indexManagerPool;
	}
	public void setIndexManagerPool(IndexManagerPool indexManagerPool) {
		this.indexManagerPool = indexManagerPool;
	}
	public IndexManager getIndexManager() {
		return indexManager;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	public boolean isUse_test_settings() {
		return use_test_settings;
	}
	public void setUse_test_settings(boolean use_test_settings) {
		this.use_test_settings = use_test_settings;
	}
}
