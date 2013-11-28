package com.openDams.skos.controller;


import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.util.Version;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.index.factory.IndexManager;
import com.openDams.index.factory.LuceneFactory;
import com.openDams.index.searchers.Searcher;
import com.openDams.services.OpenDamsService;

public class SearchController implements Controller{
	private Searcher searcher = null;
	private OpenDamsService service  = null;
	private IndexManager indexManager = null;
	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null; 
		if(arg0.getParameter("action")==null || !arg0.getParameter("action").equalsIgnoreCase("search")){
			mav = new ModelAndView("skos/search");
			mav.addObject("archiveList", service.getList(Archives.class));
			 
		}else{ 
			Analyzer analyzer = LuceneFactory.getAnalyzer(indexManager.getAnalyzerClass());
            Enumeration<String> names = arg0.getParameterNames();
            BooleanQuery query = null;
            BooleanQuery generic_query = null;
            while (names.hasMoreElements()) {
				String string = (String) names.nextElement();
				if(string.startsWith("sf_") && !arg0.getParameter(string).equals("")){
					if(query==null)
						query = new BooleanQuery();
					QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,StringUtils.substringAfter(string, "sf_"), analyzer);
					query.add(parser.parse(arg0.getParameter(string)), BooleanClause.Occur.MUST);   
				}else if(string.startsWith("gsf_") && !arg0.getParameter(string).equals("")){
					if(generic_query==null)
						generic_query = new BooleanQuery();
					QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,StringUtils.substringAfter(string, "gsf_"), analyzer);
					generic_query.add(parser.parse(arg0.getParameter(string)), BooleanClause.Occur.MUST);   
				}			
			}
			//Sort sort = new Sort(new SortField("id_record", SortField.STRING));
			Document[] hits = null;
			if(generic_query!=null){
				 hits = searcher.singleSearch(generic_query,"id_record",0,arg0.getParameter("id_archive"),true);
			}else{
				 hits = searcher.singleSearch(query,"id_record",0,arg0.getParameter("id_archive"),false);
			}	
			mav = new ModelAndView("skos/results");
			mav.addObject("results", hits);
		}
		return mav;
	}
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
	public IndexManager getIndexManager() {
		return indexManager;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
}
