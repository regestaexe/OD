package com.openDams.search_builder.controller;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.index.searchers.Searcher;
import com.openDams.index.searchers.VocTerm;
import com.openDams.index.searchers.Vocabulary;
import com.openDams.utility.StringsUtils;
import com.regesta.framework.xml.XMLReader;


public class VocController implements Controller,ServletContextAware{
		private ServletContext servletContext;
		private Searcher searcher = null;
		private IndexConfiguration indexConfiguration = null;
		public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
			ModelAndView mav = null;
			if(arg0.getParameter("action")!=null){
				if(arg0.getParameter("action").equals("vocListDottrina")){									
					StringBuilder result = new StringBuilder();
					result.append("{\"riviste\":[");
					if(!arg0.getParameter("department").equals("")){
						String xmlPath = "";
						if(!indexConfiguration.isUse_external_conf_location()){
							xmlPath+=servletContext.getRealPath("");
						}
						xmlPath+=indexConfiguration.getConfiguration_location()+"/"+arg0.getParameter("id_archive")+"/department_filter_magazines.xml";
						XMLReader xmlReader = new XMLReader(new File(xmlPath));
						ArrayList<String> magazines = xmlReader.getNodesValues("/department_list/department[@id='"+arg0.getParameter("department")+"']/magazine/text()");
						for (int i = 0; i < magazines.size(); i++) {
							result.append("{\"rivista\":"+StringsUtils.escapeJson(magazines.get(i))+" }");
							if(i<magazines.size()-1)
								result.append(",");
						}
					}else{
						Vocabulary voc = searcher.getVocabulary(arg0.getParameter("id_archive"),arg0.getParameter("field"), "", 1000, false, false, true);
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
					result.append("]}");
					mav = new ModelAndView("search_builder/json/jsonResponse");
					mav.addObject("result", result.toString());
				}else if(arg0.getParameter("action").equals("vocListStoric")){
					StringBuilder result = new StringBuilder();
					result.append("{\"types\":[");
					Vocabulary voc = searcher.getVocabulary(arg0.getParameter("id_archive"), arg0.getParameter("field"), "", 1000, false, false, true);
					ArrayList<VocTerm> at = voc.getTerms();					
					for (int i = 0; i < at.size(); i++) {
						VocTerm vocTerm = at.get(i);
						result.append("{\"type\":"+StringsUtils.escapeJson(vocTerm.term)+"}");
						try {
							at.get(i+1);
							result.append(",");
						} catch (IndexOutOfBoundsException e) {

						}					
					}
					result.append("]}");
					mav = new ModelAndView("search_builder/json/jsonResponse");
					mav.addObject("result", result.toString());
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
}
