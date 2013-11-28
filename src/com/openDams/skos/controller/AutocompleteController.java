package com.openDams.skos.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.util.Version;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.index.factory.IndexManager;
import com.openDams.index.factory.LuceneFactory;
import com.openDams.index.searchers.Searcher;
import com.openDams.title.configuration.TitleManager;

public class AutocompleteController implements Controller{
	private Searcher searcher = null;
	private IndexManager indexManager = null;
	private  TitleManager titleManager ;
	
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		Analyzer analyzer = LuceneFactory.getAnalyzer(indexManager.getAnalyzerClass());
        BooleanQuery query = new BooleanQuery();
        //System.out.println("arg0.getParameter(\"id_archive\")="+arg0.getParameter("id_archive"));
        //System.out.println("arg0.getParameter(\"searchinput\")="+arg0.getParameter("searchinput"));
        //System.out.println("arg0.getParameter(\"query\")="+arg0.getParameter("query"));
        String toSearch="";
        QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,"contents", analyzer);		 	
		Document[] hits = null;
		int totalCount = 0;
		int limit = 0;
        if(arg0.getParameter("searchinput")!=null){
        	toSearch=arg0.getParameter("searchinput");
        	System.out.println(">>>>>>>>>>>>>>>>>>>>>toSearch<<<<<<<<<<<<<<<<<"+toSearch);
        	query.add(parser.parse(toSearch), BooleanClause.Occur.MUST);  
        	limit=50;
        	hits = searcher.singleSearchPaged(query,null,limit,0, arg0.getParameter("id_archive"), false);
        	totalCount = hits.length;
        }else{
        	if(arg0.getParameter("query")!=null){
	        	toSearch=arg0.getParameter("query");
	        	System.out.println(">>>>>>>>>>>>>>>>>>>>>toSearch<<<<<<<<<<<<<<<<<"+toSearch);
	        	query.add(parser.parse(toSearch), BooleanClause.Occur.MUST);  
	        	System.out.println(">>>>>>>>>>>>>>>>>>>>>start<<<<<<<<<<<<<<<<<"+arg0.getParameter("start"));
	        	System.out.println(">>>>>>>>>>>>>>>>>>>>>limit<<<<<<<<<<<<<<<<<"+arg0.getParameter("limit"));
	        	System.out.println(">>>>>>>>>>>>>>>>>>>>>relation<<<<<<<<<<<<<<<<<"+arg0.getParameter("relation"));
	        	limit = new Integer(arg0.getParameter("limit"));
	        	hits = searcher.singleSearchPaged(query,null,limit, new Integer(arg0.getParameter("start")), arg0.getParameter("id_archive"), false);
	        	System.out.println(">>>>>>>>>>>>>>>>>>>>>totalCount<<<<<<<<<<<<<<<<<"+totalCount);
        	}
        	
        }		
		System.out.println(">>>>>>>>>>>>>>>>>>>>>hits<<<<<<<<<<<<<<<<<"+hits.length);
		mav = new ModelAndView("skos/autocomplete");
		mav.addObject("results", hits);
		mav.addObject("titleManager", titleManager);
		mav.addObject("limit", limit);
		return mav;
	}
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
}
