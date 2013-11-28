package com.openDams.skos.controller;


import java.util.ArrayList;

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

import com.openDams.bean.Archives;
import com.openDams.index.searchers.Searcher;
import com.openDams.index.searchers.VocTerm;
import com.openDams.index.searchers.Vocabulary;
import com.openDams.services.OpenDamsService;

public class WorkspaceController implements Controller{
	private  OpenDamsService service ;
	private Searcher searcher = null;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		//System.out.println("INIZIO CHIAMATA WorkspaceController");
		ModelAndView mav = new ModelAndView("skos/workspace");
		Archives archives = (Archives)service.getObject(Archives.class, new Integer(arg0.getParameter("idArchive")));
		mav.addObject("archives", archives);
		System.out.println("INIZIO CHIAMATA Archives");
		//mav.addObject("unlinked", service.getCountFromSQL("SELECT count(id_record) FROM records  where id_record not in (select ref_id_record_1 from relations) and id_record not in (select ref_id_record_2 from relations)"));
		mav.addObject("unlinked", service.getCountFromSQL("SELECT count(id_record) FROM records WHERE ref_id_archive = "+arg0.getParameter("idArchive")+" and NOT EXISTS (SELECT ref_id_record_1 FROM relations WHERE ref_id_record_1 = id_record and (ref_id_relation_type=1 or ref_id_relation_type=2 or ref_id_relation_type=10 or ref_id_relation_type=11)) and NOT EXISTS (SELECT ref_id_record_2 FROM relations WHERE ref_id_record_2 = id_record and (ref_id_relation_type=1 or ref_id_relation_type=2 or ref_id_relation_type=10 or ref_id_relation_type=11))"));
		//System.out.println("INIZIO CHIAMATA unlinked");
		Vocabulary voc = searcher.getVocabulary(arg0.getParameter("idArchive"), "notation", "",999999, false, false, true);
		ArrayList<VocTerm> at = voc.getTerms();					
		for (int i = 0; i < at.size(); i++) {
			VocTerm vocTerm = at.get(i);
			if(vocTerm.getFrequence()>1){
				mav.addObject("duplicate", true);
				break;
			}
		}
		//System.out.println("INIZIO CHIAMATA duplicate");
		String toSearch = "+(inScheme:\"\")";
		Analyzer analyzer = new KeywordAnalyzer();
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "contents", analyzer);
		parser.setDefaultOperator(Operator.AND);
		BooleanQuery query = new BooleanQuery();
		query.add(parser.parse(toSearch), BooleanClause.Occur.MUST);
		Document[] hits = searcher.singleSearch(query, null, 2, arg0.getParameter("idArchive"), false);
        if(hits!=null && hits.length>1){
        	mav.addObject("orphan", hits);
        }
        //System.out.println("INIZIO CHIAMATA orphan");
        //System.out.println("FINE CHIAMATA WorkspaceController");
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
}
