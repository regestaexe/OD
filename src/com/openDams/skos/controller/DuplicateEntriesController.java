package com.openDams.skos.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.index.searchers.Searcher;
import com.openDams.index.searchers.VocTerm;
import com.openDams.index.searchers.Vocabulary;
import com.openDams.title.configuration.TitleManager;


public class DuplicateEntriesController implements Controller{
	private Searcher searcher = null;
	private  TitleManager titleManager ;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;			
		Vocabulary voc = searcher.getVocabulary(arg0.getParameter("idArchive"), "notation", "",999999, false, true, true);
		ArrayList<VocTerm> results = voc.getTerms();	
		mav = new ModelAndView("skos/duplicate/duplicate");
		mav.addObject("results", results);
		mav.addObject("titleManager", titleManager);
		return mav;
	}
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}

}
