package com.openDams.admin.controller;


import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.ArchiveIdentities;
import com.openDams.bean.ArchiveTypes;
import com.openDams.bean.Archives;
import com.openDams.bean.XmlId;
import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.services.OpenDamsService;
import com.openDams.utility.InstanceBuilder;
import com.regesta.framework.io.FileManager;

public class ArchiveController implements Controller,ServletContextAware{
	private ServletContext servletContext;
	private OpenDamsService service ;
	private IndexConfiguration indexConfiguration ;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if(arg0.getParameter("action")==null || !arg0.getParameter("action").equalsIgnoreCase("insert")){
			mav = new ModelAndView("admin/createArchive");
			mav.addObject("archiveTypesList", service.getList(ArchiveTypes.class));
			mav.addObject("archiveIdentitiesList", service.getList(ArchiveIdentities.class));
		}else{
			Archives archives = new Archives();
			ServletRequestDataBinder servletRequestDataBinder = new ServletRequestDataBinder(archives);
			InstanceBuilder.buildObject(archives);
			archives.setOffline(false);
			servletRequestDataBinder.bind(arg0);
			if(archives.getXmlId()!=null){
				XmlId xmlId = archives.getXmlId();
				xmlId.setIdXml(0);
				xmlId.setArchives(archives);
				archives.setXmlId(xmlId);
			}else{
				archives.setXmlId(new XmlId(archives,0));
			}
			service.add(archives);
			String path="";
			String generic_path="";
			if(!indexConfiguration.isUse_external_conf_location()){
				path+=servletContext.getRealPath("");
				generic_path+=servletContext.getRealPath("");
			}
			path+=indexConfiguration.getConfiguration_location();
			generic_path+=indexConfiguration.getGeneric_configuration_location();
			File file = new File(path+"/"+archives.getIdArchive());
			file.mkdir();
			if(indexConfiguration.isUse_db_for_generic_configuration()){
				ArchiveTypes archiveTypes = ((ArchiveTypes)service.getObject(ArchiveTypes.class,  archives.getArchiveTypes().getIdArchiveType()));
				File indexFile = new File(path+"/"+archives.getIdArchive()+"/index_configuration.xml");
				FileCopyUtils.copy(archiveTypes.getGeneric_index_configuration_xml(),indexFile);
				File titleFile = new File(path+"/"+archives.getIdArchive()+"/title_configuration.xml");
				FileCopyUtils.copy(archiveTypes.getGeneric_title_configuration_xml(), titleFile);
			}else{
				FileManager.copyFile(new File(generic_path+"/["+((ArchiveTypes)service.getObject(ArchiveTypes.class,  archives.getArchiveTypes().getIdArchiveType())).getDescription()+"]_index_configuration.xml"), new File(path+"/"+archives.getIdArchive()+"/index_configuration.xml"));
				FileManager.copyFile(new File(generic_path+"/["+((ArchiveTypes)service.getObject(ArchiveTypes.class,  archives.getArchiveTypes().getIdArchiveType())).getDescription()+"]_title_configuration.xml"), new File(path+"/"+archives.getIdArchive()+"/title_configuration.xml"));
			} 
			mav = new ModelAndView("admin/createArchive");
			mav.addObject("archiveTypesList", service.getList(ArchiveTypes.class));
			mav.addObject("archiveIdentitiesList", service.getList(ArchiveIdentities.class));
			
		}
		return mav;
	}
	
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;
	}
	public IndexConfiguration getIndexConfiguration() {
		return indexConfiguration;
	}
	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}

}
