package com.openDams.skos.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.util.Version;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.index.searchers.Searcher;
import com.openDams.title.configuration.TitleManager;


public class OrphanEntriesController implements Controller{
	private Searcher searcher = null;
	private  TitleManager titleManager ;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;	
		mav = new ModelAndView("skos/orphan/orphan");
		String toSearch = "+(inScheme:\"\")";
		Analyzer analyzer = new KeywordAnalyzer();
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "contents", analyzer);
		parser.setDefaultOperator(Operator.AND);
		BooleanQuery query = new BooleanQuery();
		query.add(parser.parse(toSearch), BooleanClause.Occur.MUST);
		Document[] hits = searcher.singleSearch(query, null, 0, arg0.getParameter("idArchive"), false);
        mav.addObject("orphan", hits);		
		mav.addObject("results", hits);
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
