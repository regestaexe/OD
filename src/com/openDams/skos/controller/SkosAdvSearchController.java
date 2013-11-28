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

import com.openDams.index.factory.IndexManager;
import com.openDams.index.factory.LuceneFactory;
import com.openDams.index.searchers.Searcher;
import com.openDams.title.configuration.TitleManager;

public class SkosAdvSearchController implements Controller {
	private Searcher searcher = null;
	private IndexManager indexManager = null;
	private TitleManager titleManager;
	private int page_size = 1;

	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if (arg0.getParameter("action") == null || !arg0.getParameter("action").equalsIgnoreCase("search")) {
			mav = new ModelAndView("skos/skos_adv_search");
		} else {
			Analyzer analyzer = LuceneFactory.getAnalyzer(indexManager.getAnalyzerClass());
			Enumeration<String> names = arg0.getParameterNames();
			BooleanQuery query = null;
			while (names.hasMoreElements()) {
				String string = (String) names.nextElement();
				if (string.startsWith("sf_") && !arg0.getParameter(string).equals("")) {
					if (query == null)
						query = new BooleanQuery();
					QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, StringUtils.substringAfter(string, "sf_"), analyzer);
					query.add(parser.parse(arg0.getParameter(string)), BooleanClause.Occur.MUST);
				}
			}
			String sort = null;
			if (arg0.getParameter("order_by") != null && !arg0.getParameter("order_by").equals("")) {
				sort = arg0.getParameter("order_by");
				System.out.println("Faccio ricerca con ordine : " + sort);
			}
			Document[] hits = null;
			int start = 0;
			if (arg0.getParameter("current_page") != null) {
				int current_page = new Integer(arg0.getParameter("current_page"));
				start = (current_page - 1) * page_size;
			}
			hits = searcher.singleSearchPaged(query, sort, page_size, start, arg0.getParameter("idArchive"), false);
			mav = new ModelAndView("skos/skos_adv_results");
			mav.addObject("results", hits);
			mav.addObject("page_size", page_size);
			mav.addObject("titleManager", titleManager);
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

	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}

	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}
}
