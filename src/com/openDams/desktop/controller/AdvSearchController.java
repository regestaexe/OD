package com.openDams.desktop.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Records;
import com.openDams.index.searchers.QueryBuilder;
import com.openDams.index.searchers.Searcher;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;
import com.regesta.framework.util.DateUtility;

public class AdvSearchController implements Controller {
	private Searcher searcher = null;
	private TitleManager titleManager;
	private OpenDamsService service ;
	private QueryBuilder queryBuilder;
	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if (arg0.getParameter("action").equalsIgnoreCase("search")) {
			Document[] hits = null;
			int limit = 0;
			limit = new Integer(arg0.getParameter("limit"));
			BooleanQuery booleanQuery = null;
			TermRangeQuery termRangeQuery = null;
			String toSearch = "";		
			String date_range_query = "";
			toSearch = URLDecoder.decode(arg0.getParameter("query"),"UTF-8");
			if(arg0.getParameter("date_range_query")!=null && !arg0.getParameter("date_range_query").trim().equals("")){
				date_range_query = arg0.getParameter("date_range_query").trim();
				System.out.println("date_range_query"+date_range_query); 
				String startD = "";
				String endD = "";
				String field = "";
				if(date_range_query.indexOf(" ")!=-1){
					String[] dates = date_range_query.split(" ");
					for (int i = 0; i < dates.length; i++) {
						if(dates[i].indexOf("_start")!=-1){
							field = StringUtils.substringBefore(dates[i], "_");
							startD=DateUtility.UserToXwDateTranslate(StringUtils.substringAfterLast(dates[i],":"));
						}else{
							endD=DateUtility.UserToXwDateTranslate(StringUtils.substringAfterLast(dates[i],":"));
						}	
					}
					termRangeQuery = new TermRangeQuery(field,startD,endD,true,true);
				}else{
					field = StringUtils.substringBefore(date_range_query,"_");
					startD=DateUtility.UserToXwDateTranslate(StringUtils.substringAfterLast(date_range_query,":"));
					toSearch+=" +"+field+":"+startD;
				}	
			}
			if(termRangeQuery!=null){
				toSearch+="+"+termRangeQuery.toString()+"~";
			}
			String[] multipleQuerys = toSearch.split("~");
			if (toSearch != null) {
				int idArchive = Integer.parseInt(arg0.getParameter("id_archive"));
				booleanQuery = queryBuilder.buildQuery(idArchive, multipleQuerys);
				System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<query>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+booleanQuery.toString());
			}
			if (arg0.getParameter("filtersUtente") != null) {
				TermQuery queryTerm = new TermQuery(new Term("utente", arg0.getParameter("filtersUtente")));
				booleanQuery.add(queryTerm, BooleanClause.Occur.MUST);
			}
			if (arg0.getParameter("filtersDipartimento") != null) {
				TermQuery queryTerm = new TermQuery(new Term("dipartimento", arg0.getParameter("filtersDipartimento")));
				booleanQuery.add(queryTerm, BooleanClause.Occur.MUST);
			}
			mav = new ModelAndView("desktop/json/adv_results");
			if (arg0.getParameter("sort") != null) {
				HashMap<String, Object> resultMap = searcher.fullLuceneDataPaged(booleanQuery, arg0.getParameter("sort"), limit, new Integer(arg0.getParameter("start")), arg0.getParameter("id_archive"), false);
				hits = (Document[]) resultMap.get("hits");
				mav.addObject("totResults", resultMap.get("totResults"));
			} else {
				hits = searcher.singleSearchPaged(booleanQuery, null, limit, new Integer(arg0.getParameter("start")), arg0.getParameter("id_archive"), false);
			}
			mav.addObject("results", hits);
			mav.addObject("titleManager", titleManager);
			mav.addObject("limit", limit);

		}else if(arg0.getParameter("action").equalsIgnoreCase("find_sons")){
			int idRecord = new Integer(arg0.getParameter("idRecord"));
			ArrayList<String> querys = new ArrayList<String>();
			Records records = (Records)service.getObject(Records.class, idRecord);
			String xmlId = StringUtils.substringBefore(records.getTitle(),"[").replaceAll("ç","");
			if(xmlId.indexOf("/")!=-1)
				xmlId=StringUtils.substringAfterLast(xmlId,"/");
			querys.add(xmlId);
			if(arg0.getParameter("deep")!=null && arg0.getParameter("deep").equals("true")){
				findSons(idRecord,querys);
			}
			mav = new ModelAndView("desktop/json/findSons");
			mav.addObject("querys", querys);
			if(records.getArchives().getIdArchive()==1){
				mav.addObject("query_field", "griglia");
			}else{
				mav.addObject("query_field", "eurovoc");
			}
		}else if(arg0.getParameter("action").equalsIgnoreCase("th_relations")){
			try {
				int idRecord = new Integer(arg0.getParameter("idRecord"));
				List<Records> recordsList = (List<Records>)service.getListFromSQL(Records.class,"SELECT * FROM records inner join relations on relations.ref_id_record_2=records.id_record where (relations.ref_id_relation_type=5 or relations.ref_id_relation_type=6 or relations.ref_id_relation_type=7 or relations.ref_id_relation_type=8) and relations.ref_id_record_1="+idRecord+" union SELECT * FROM records inner join relations on relations.ref_id_record_1=records.id_record where (relations.ref_id_relation_type=5 or relations.ref_id_relation_type=6 or relations.ref_id_relation_type=7 or relations.ref_id_relation_type=8) and relations.ref_id_record_2="+idRecord+";");
				mav = new ModelAndView("desktop/th_relations");
				mav.addObject("recordsList", recordsList);
				mav.addObject("titleManager", titleManager);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		return mav;
	}
    @SuppressWarnings("unchecked")
	private void findSons(int idRecord,ArrayList<String> querys){
    	List<Records> list = (List<Records>) service.getListFromSQL(Records.class,"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+idRecord+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+idRecord+" and relations.ref_id_relation_type=2 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+idRecord+" and relations.ref_id_relation_type=10");
        if(list!=null && list.size()>0){
        	for (int i = 0; i < list.size(); i++) {
				Records records = list.get(i);
				String xmlId = StringUtils.substringBefore(records.getTitle(),"[").replaceAll("ç","");
				if(xmlId.indexOf("/")!=-1)
					xmlId=StringUtils.substringAfterLast(xmlId,"/");
				querys.add(xmlId);
				findSons(records.getIdRecord(),querys);
			}
        }
    }
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setQueryBuilder(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}
}
