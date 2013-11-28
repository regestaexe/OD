package com.openDams.vocabulary.controller;


import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.index.factory.DocumentFactory;
import com.openDams.index.searchers.Searcher;
import com.openDams.title.configuration.TitleManager;

public class VocabularyController implements Controller{

	private Searcher searcher = null;
	private TitleManager titleManager;
	private int limit = 20;
	protected IndexConfiguration indexConfiguration = null;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		String last_term = URLDecoder.decode(arg0.getParameter("last_term"),"UTF-8");
		String requestArchives = arg0.getParameter("requestArchives");
		if(arg0.getParameter("action")!=null && arg0.getParameter("action").equals("voc_page")){
			mav = new ModelAndView("vocabulary/voc_page");
			String voc_field = arg0.getParameter("voc_field");
			if(voc_field.startsWith("sf_")){
				voc_field = StringUtils.substringAfter(voc_field,"sf_");
			}
			if(arg0.getParameter("one_field")!=null && arg0.getParameter("one_field").equals("true")){
				voc_field = voc_field+"_one";
			}
			mav.addObject("vocabulary",searcher.getVocabulary(requestArchives,voc_field,last_term,limit, false,false,false));
			mav.addObject("titleManager", titleManager);
			return mav;
		}else{
			mav = new ModelAndView("vocabulary/vocabulary");
			String voc_field = arg0.getParameter("voc_field");
			if(voc_field.startsWith("sf_")){
				voc_field = StringUtils.substringAfter(voc_field,"sf_");
			}
			HashMap<Integer, ArrayList<Object>> elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();
			if(DocumentFactory.checkMultiFieldValue(elements_map, voc_field, requestArchives.split(";"))){
				if(arg0.getParameter("one_field")!=null && arg0.getParameter("one_field").equals("true")){
					voc_field = voc_field+"_one";
				}
				mav.addObject("one_field","true");
			}
			mav.addObject("vocabulary",searcher.getVocabulary(requestArchives,voc_field,last_term,limit, false,false,true));
			mav.addObject("titleManager", titleManager);
			return mav;
		}
		
	}
	public Searcher getSearcher() {
		return searcher;
	}
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}

}
