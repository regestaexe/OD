package com.openDams.relations.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentException;
import org.springframework.web.context.ServletContextAware;
import org.xml.sax.SAXException;

import com.openDams.configuration.ConfigurationException;
import com.openDams.configuration.ConfigurationXMLReader;


public class ConfigurationReader implements ServletContextAware{
	private ServletContext servletContext;
	private ArrayList<Object> elements_list = null;
	private Map<String,Element> elements_map = null;
	private RelationsConfiguration relationsConfiguration ;
	public ConfigurationReader() throws FileNotFoundException, DocumentException{
     }
    public void loadElementsList() throws ConfigurationException {
    	try {
    		if(elements_list==null){
	    		ConfigurationXMLReader configurationXMLReader = null;
	    		String path="";
				if(!relationsConfiguration.isUse_external_conf_location()){
					path+=servletContext.getRealPath("");
				}
				path+=relationsConfiguration.getConfiguration_location()+"/"+relationsConfiguration.getFile_name();
				File fileConf = new File(path);	
		    	configurationXMLReader  = new ConfigurationXMLReader(new FileInputStream(fileConf),Element.class,true);
		    	elements_list = configurationXMLReader.getObjects();
    		}	    	
  		} catch (ParserConfigurationException e) {
  			e.printStackTrace();
  		} catch (SAXException e) {
  			e.printStackTrace();
  		} catch (IOException e) {
  			e.printStackTrace();
  		} catch (SecurityException e) {
  			e.printStackTrace();
  		} catch (IllegalArgumentException e) {
  			e.printStackTrace();
  		} catch (ClassNotFoundException e) {
  			e.printStackTrace();
  		} catch (NoSuchMethodException e) {
  			e.printStackTrace();
  		} catch (InstantiationException e) {
  			e.printStackTrace();
  		} catch (IllegalAccessException e) {
  			e.printStackTrace();
  		} catch (InvocationTargetException e) {
  			e.printStackTrace();
  		} catch (NullPointerException e) {
  			e.printStackTrace();
  		}
 	}
    public void loadElements_map() throws ConfigurationException {
    	if(elements_map==null){
    		loadElementsList();
    		elements_map = new HashMap<String, Element>();
			for (int i = 0; i < elements_list.size(); i++) {
				Element element = (Element)elements_list.get(i);				
				elements_map.put(element.getId_relation()+"_"+element.getGet_from()+"_"+element.getInsert_to(), element);
			}
    	}
	}
    public Element getElement(int ref_id_relation_type,int ref_id_archive_from,int ref_id_archive_to) throws ConfigurationException{
    	if(elements_map==null){
    		loadElements_map();
    	}
    	System.out.println("ConfigurationReader.getElement() "+ref_id_relation_type+"_"+ref_id_archive_from+"_"+ref_id_archive_to);
    	return elements_map.get(ref_id_relation_type+"_"+ref_id_archive_from+"_"+ref_id_archive_to);
    }
    public void setServletContext(ServletContext arg0) {
 		servletContext = arg0;
 	}
	public ServletContext getServletContext() {
		return servletContext;
	}
	public RelationsConfiguration getRelationsConfiguration() {
		return relationsConfiguration;
	}
	public void setRelationsConfiguration(
			RelationsConfiguration relationsConfiguration) {
		this.relationsConfiguration = relationsConfiguration;
	}
	
	
}
