package com.openDams.admin.controller;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Departments;
import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.index.searchers.Searcher;
import com.openDams.index.searchers.VocTerm;
import com.openDams.index.searchers.Vocabulary;
import com.openDams.services.OpenDamsService;
import com.openDams.utility.StringsUtils;
import com.regesta.framework.xml.XMLBuilder;
import com.regesta.framework.xml.XMLReader;


public class MagazinesController implements Controller,ServletContextAware{
	private ServletContext servletContext;
	private Searcher searcher = null;
	private  OpenDamsService service ;
	private IndexConfiguration indexConfiguration = null;
	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if(arg0.getParameter("action")!=null){
			if(arg0.getParameter("action").equals("vocListDottrina")){
				String xmlPath = "";
				if(!indexConfiguration.isUse_external_conf_location()){
					xmlPath+=servletContext.getRealPath("");
				}
				xmlPath+=indexConfiguration.getConfiguration_location()+"/"+arg0.getParameter("id_archive")+"/department_filter_magazines.xml";
				
				StringBuilder result = new StringBuilder();
				result.append("{\"riviste\":[");		
				if(arg0.getParameter("magazines").trim().equals("all")){
					HashMap<String, String> departmentMagazines = new HashMap<String, String>();
					XMLReader xmlReader = new XMLReader(new File(xmlPath));
					ArrayList<String> magazines = xmlReader.getNodesValues("/department_list/department[@id='"+arg0.getParameter("department")+"']/magazine/text()");
					for (int i = 0; i < magazines.size(); i++) {
						departmentMagazines.put(magazines.get(i),magazines.get(i));
					}		
					Vocabulary voc = searcher.getVocabulary(arg0.getParameter("id_archive"), "rivista", "", 1000, false, false, true);
					ArrayList<VocTerm> at = voc.getTerms();
					
					for (int i = 0; i < at.size(); i++) {
						VocTerm vocTerm = at.get(i);
						if(departmentMagazines.get(vocTerm.term)==null){
							result.append("{\"rivista\":"+StringsUtils.escapeJson(vocTerm.term)+"}");
							try {
								at.get(i+1);
								result.append(",");
							} catch (IndexOutOfBoundsException e) {

							}
						}						
					}
				}else{
					if(!arg0.getParameter("department").equals("")){
						XMLReader xmlReader = new XMLReader(new File(xmlPath));
						ArrayList<String> magazines = xmlReader.getNodesValues("/department_list/department[@id='"+arg0.getParameter("department")+"']/magazine/text()");
						for (int i = 0; i < magazines.size(); i++) {
							result.append("{\"rivista\":"+StringsUtils.escapeJson(magazines.get(i))+" }");
							if(i<magazines.size()-1)
								result.append(",");
						}
					}else{
						Vocabulary voc = searcher.getVocabulary(arg0.getParameter("id_archive"), "rivista", "", 1000, false, false, true);
						ArrayList<VocTerm> at = voc.getTerms();
						
						for (int i = 0; i < at.size(); i++) {
							VocTerm vocTerm = at.get(i);
							result.append("{\"rivista\":"+StringsUtils.escapeJson(vocTerm.term)+"}");
							try {
								at.get(i+1);
								result.append(",");
							} catch (IndexOutOfBoundsException e) {

							}					
						}
						
					}
					
				}
				result.append("]}");
				mav = new ModelAndView("admin/magazines/json/jsonResponse");
				mav.addObject("result", result.toString());
			}else if(arg0.getParameter("action").equals("departmentList")){
				List<Departments> departmentsList = (List<Departments>)service.getList(Departments.class);
				StringBuilder result = new StringBuilder();
				result.append("{\"departments\":[");
				for (int i = 0; i < departmentsList.size(); i++) {
					Departments departments = departmentsList.get(i);
					result.append("{\"id\":\""+departments.getAcronym()+"\",\"text\":\""+departments.getDescription()+"\"}");
					if(i<departmentsList.size()-1)
						result.append(",");
				}
				result.append("]}");
				mav = new ModelAndView("admin/magazines/json/jsonResponse");
				mav.addObject("result", result.toString());
			}else if(arg0.getParameter("action").equals("saveMagazines")){
				String xmlPath = "";
				if(!indexConfiguration.isUse_external_conf_location()){
					xmlPath+=servletContext.getRealPath("");
				}
				xmlPath+=indexConfiguration.getConfiguration_location()+"/"+arg0.getParameter("id_archive")+"/department_filter_magazines.xml";
				File file = new File(xmlPath);
				XMLBuilder xmlBuilder = new XMLBuilder(file);
				xmlBuilder.deleteNode("/department_list/department[@id='"+arg0.getParameter("department")+"']");
				String[] magazines = arg0.getParameter("magazines").split("~");
				for (int i = 0; i < magazines.length; i++) {
					System.out.println("magazine = "+magazines[i]);
					xmlBuilder.insertNode("/department_list/department[@id='"+arg0.getParameter("department")+"']/magazine["+(i+1)+"]/text()", magazines[i]);
				}
				String xml = xmlBuilder.getXML("UTF-8");
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(xml);
				fileWriter.close();
				mav = new ModelAndView("admin/magazines/result");
			}
		}else{
			mav = new ModelAndView("admin/magazines/magazinesManager");
		}		
		return mav;
	}
	
	@Override
	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;		
	}

	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}

	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}

	public void setService(OpenDamsService service) {
		this.service = service;
	}
}
