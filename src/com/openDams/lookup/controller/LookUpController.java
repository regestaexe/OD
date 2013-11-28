package com.openDams.lookup.controller;


import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanQuery;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.index.searchers.QueryBuilder;
import com.openDams.index.searchers.Searcher;
import com.openDams.title.configuration.TitleManager;

public class LookUpController implements Controller{

	private Searcher searcher = null;
	private TitleManager titleManager;
	private QueryBuilder queryBuilder;
	private int page_size = 20;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;

		Document[] hits = null;
		String order_by = null;
		String sort_type = null;
		String toSearch = "";
		toSearch = URLDecoder.decode(arg0.getParameter("query"),"UTF-8");
		if(!toSearch.equalsIgnoreCase("{a TO Z}")){
			toSearch = arg0.getParameter("field")+":"+toSearch;
		}
		if(arg0.getParameter("order_by")!=null && !arg0.getParameter("order_by").trim().equals("") && !arg0.getParameter("order_by").trim().equals("relevance")){
			order_by = arg0.getParameter("order_by");
		}
		if(arg0.getParameter("sort_type")!=null && !arg0.getParameter("sort_type").trim().equals("")){
			sort_type = arg0.getParameter("sort_type");
		}
		BooleanQuery booleanQuery = null;
		String[] multipleQuerys = toSearch.split("~");
		if (toSearch != null) {
			int idArchive = Integer.parseInt(arg0.getParameter("idArchive"));
			booleanQuery = queryBuilder.buildQuery(idArchive, multipleQuerys);				
		}
		
		int start = 0;
		if(arg0.getParameter("start")!=null){
			start = Integer.parseInt(arg0.getParameter("start"));
		}
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<query action=search>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+booleanQuery.toString());
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<order_by>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+order_by);
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<sort_type>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+sort_type);
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<page_size>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+page_size);
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+start);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>find_sons = "+arg0.getParameter("find_sons"));
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>id_filters = "+arg0.getParameter("id_filters"));
		hits = searcher.singleSearchPaged(booleanQuery, order_by,sort_type, page_size, start, arg0.getParameter("idArchive"), false);
		mav = new ModelAndView("lookup/lookup");
		mav.addObject("results",hits);
		mav.addObject("page_size",page_size);
		mav.addObject("titleManager",titleManager);
		return mav;
	}
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	public void setQueryBuilder(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}
	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}
	

}
