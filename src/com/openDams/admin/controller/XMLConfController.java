package com.openDams.admin.controller;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.relations.configuration.RelationsConfiguration;
import com.openDams.search.configuration.SearchConfiguration;

public class XMLConfController implements Controller,ServletContextAware{

	private RelationsConfiguration relationsConfiguration ;
	private SearchConfiguration searchConfiguration;

	private ServletContext servletContext;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if(arg0.getParameter("action")!=null){
			 String path="";
			 if(arg0.getParameter("xml_type").equals("search")){
				 if(!searchConfiguration.isUse_external_conf_location()){
						path+=servletContext.getRealPath("");
				 }
				 path+=searchConfiguration.getConfiguration_location()+"/"+searchConfiguration.getFile_name();
			 }else{
				 if(!relationsConfiguration.isUse_external_conf_location()){
						path+=servletContext.getRealPath("");
				 }
				 path+=relationsConfiguration.getConfiguration_location()+"/"+relationsConfiguration.getFile_name();
			 }
			 if(arg0.getParameter("action").equals("save_xml")){
				 writeXmlConfiguration(path,URLDecoder.decode(arg0.getParameter("xml"), "UTF-8"));	
				 mav = new ModelAndView("admin/xml_managment/save");			 		 
			 }else if(arg0.getParameter("action").equals("show_xml")){
				 mav = new ModelAndView("admin/xml_managment/xml_editor");
				 mav.addObject("xml", readXmlConfiguration(path));			 
			 }
		}
		return mav;	
	}
	private String readXmlConfiguration(String fileName) throws IOException{
		String xml = "";
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			File file = new File(fileName);
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			while (bufferedReader.ready()) {
				xml+=bufferedReader.readLine()+"\r\n";
			}
			bufferedReader.close();
			fileReader.close();
		} catch (IOException e) {
			throw e;
		}
		return xml;
	}
	private void writeXmlConfiguration(String fileName,String xml) throws IOException{
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			File file = new File(fileName);
			fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(xml);
			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			throw e;
		}
	}
	public void setServletContext(ServletContext arg0) {
 		servletContext = arg0;
 	}
	public void setRelationsConfiguration(
			RelationsConfiguration relationsConfiguration) {
		this.relationsConfiguration = relationsConfiguration;
	}
	public void setSearchConfiguration(SearchConfiguration searchConfiguration) {
		this.searchConfiguration = searchConfiguration;
	}
}
