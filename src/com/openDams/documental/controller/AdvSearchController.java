package com.openDams.documental.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Departments;
import com.openDams.bean.NoteType;
import com.openDams.bean.Notes;
import com.openDams.index.factory.IndexManager;
import com.openDams.index.factory.LuceneFactory;
import com.openDams.index.searchers.Searcher;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;

public class AdvSearchController implements Controller {
	private OpenDamsService service;
	private Searcher searcher = null;
	private IndexManager indexManager = null;
	private TitleManager titleManager;
	private int page_size = 1;

	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;

		if (arg0.getParameter("action") == null) {
			mav = new ModelAndView("documental/adv_search");
		} else if (arg0.getParameter("action").equalsIgnoreCase("search")) {
			Document[] hits = null;
			int limit = 0;
			limit = new Integer(arg0.getParameter("limit"));
			BooleanQuery query = new BooleanQuery();
			String toSearch = arg0.getParameter("query");
			String[] keytoSearch = arg0.getParameterValues("keywords");
			if (toSearch != null && !toSearch.trim().equals("")) {

				if (toSearch.equals("{a TO Z}")) {
					query.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
				} else {
					Analyzer analyzer = LuceneFactory.getAnalyzer(indexManager.getAnalyzerClass());
					QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "contents", analyzer);
					parser.setDefaultOperator(Operator.AND);
					query.add(parser.parse(toSearch), BooleanClause.Occur.MUST);
				}

				System.out.println(">>>>>>>>>>>>>>>>>>>>>toSearch<<<<<<<<<<<<<<<<< " + toSearch);
			}
			if (keytoSearch != null && keytoSearch.length > 0) {
				for (int i = 0; i < keytoSearch.length; i++) {
					if (!keytoSearch[i].trim().equals("")) {
						TermQuery queryTerm = new TermQuery(new Term(StringUtils.substringBefore(keytoSearch[i], ":"), java.net.URLDecoder.decode(StringUtils.substringAfter(keytoSearch[i], ":"), "UTF-8")));
						query.add(queryTerm, BooleanClause.Occur.MUST);
						// System.out.println(">>>>>>>>>>>>>>>>>>>>>keytoSearch<<<<<<<<<<<<<<<<< "+keytoSearch[i]);
					}
				}
			}
			if (arg0.getParameter("filtersUtente") != null) {
				TermQuery queryTerm = new TermQuery(new Term("utente", arg0.getParameter("filtersUtente")));
				query.add(queryTerm, BooleanClause.Occur.MUST);
			}
			if (arg0.getParameter("filtersDipartimento") != null) {
				TermQuery queryTerm = new TermQuery(new Term("dipartimento", arg0.getParameter("filtersDipartimento")));
				query.add(queryTerm, BooleanClause.Occur.MUST);
			}
			if (arg0.getParameter("inScheme") != null && !arg0.getParameter("inScheme").trim().equals("")) {
				BooleanQuery booleanQuery = new BooleanQuery();
				TermQuery queryTerm = new TermQuery(new Term("inScheme", arg0.getParameter("inScheme")));
				booleanQuery.add(queryTerm, BooleanClause.Occur.SHOULD);
				TermQuery queryTerm2 = new TermQuery(new Term("id", arg0.getParameter("inScheme")));
				booleanQuery.add(queryTerm2, BooleanClause.Occur.SHOULD);
				query.add(booleanQuery, BooleanClause.Occur.MUST);
				// toSearch+=" +(inScheme:"+arg0.getParameter("inScheme")+" id:"+arg0.getParameter("inScheme")+")";
			}
			// System.out.println(">>>>>>>>>>>>>>>>>>>>>start<<<<<<<<<<<<<<<<< "+arg0.getParameter("start"));

			// System.out.println(">>>>>>>>>>>>>>>>>>>>>limit<<<<<<<<<<<<<<<<< "+arg0.getParameter("limit"));
			System.out.println("query>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + query.toString());

			mav = new ModelAndView("documental/json/adv_results");
			// hits = searcher.singleSearchPaged(query,null,limit, new
			// Integer(arg0.getParameter("start")),
			// arg0.getParameter("id_archive"), false);
			if (arg0.getParameter("sort") != null) {
				/*
				 * hits = searcher.fullLuceneDataPaged(query,
				 * arg0.getParameter("sort"), limit, new
				 * Integer(arg0.getParameter("start")),
				 * arg0.getParameter("id_archive"), false,
				 * LuceneFieldDocComparator.TYPE_INTEGER);
				 */// LuceneFieldDocComparator.TYPE_INTEGER
				Integer start = 0;
				try {
					start = Integer.parseInt(arg0.getParameter("start"));
				} catch (Exception e) {
					// TODO: handle exception
				}
				HashMap<String, Object> resultMap = searcher.fullLuceneDataPaged(query, arg0.getParameter("sort"), limit, start, arg0.getParameter("id_archive"), false);
				hits = (Document[]) resultMap.get("hits");
				mav.addObject("totResults", resultMap.get("totResults"));
			} else {
				hits = searcher.singleSearchPaged(query, null, limit, new Integer(arg0.getParameter("start")), arg0.getParameter("id_archive"), false);
			}
			if (arg0.getParameter("outputMode") != null && (arg0.getParameter("outputMode").equals("dottrina") || arg0.getParameter("outputMode").equals("normativa") || arg0.getParameter("outputMode").equals("giurisprudenza"))) {
				try {
					HashMap<String, Boolean> testMagazines = new HashMap<String, Boolean>();
					HashMap<String, String> idToCheck = new HashMap<String, String>();
					int idArchive = Integer.parseInt(arg0.getParameter("id_archive"));
					for (int i = 0; i < hits.length; i++) {
						if (hits[i] != null) {
							Document docsearch = hits[i];
							HashMap<String, String[]> parsedTitle = titleManager.parseTitle(docsearch.get("title_record"), idArchive);
							String note_text = parsedTitle.get("id")[0];
							String[] magazines = parsedTitle.get("data");
							String magazine = "";
							for (int j = 0; j < magazines.length; j++) {
								magazine += "-" + magazines[j];
							}
							if (testMagazines.get(magazine) == null) {
								String department = arg0.getParameter("idDepartment");
								List<Departments> departmentsList = (List<Departments>) service.getListFromSQL(Departments.class, "SELECT * FROM departments where acronym = '" + department + "';");
								Departments departments = null;
								if (departmentsList != null && departmentsList.size() > 0) {
									departments = departmentsList.get(0);
								}
								if (departments != null && service.getListFromSQL(Notes.class, "SELECT * FROM notes where ref_id_department=" + departments.getIdDepartment() + " AND ref_id_note_type=" + NoteType.APPLICATION_NOTE + " AND note_text LIKE '%" + note_text + "%';").size() > 0) {
									System.out.println(magazine + " già esistente!!!!!");
									testMagazines.put(magazine, true);
									idToCheck.put(note_text, note_text);
								} else {
									System.out.println(magazine + " non esistente!!!!!");
									testMagazines.put(magazine, false);
								}
							} else {
								if (testMagazines.get(magazine)) {
									idToCheck.put(note_text, note_text);
								}
							}

						}
					}
					mav.addObject("idToCheck", idToCheck);
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				}
			}
			// System.out.println(">>>>>>>>>>>>>>>>>>>>>totalCount<<<<<<<<<<<<<<<<< "+hits.length);

			mav.addObject("results", hits);
			mav.addObject("titleManager", titleManager);
			mav.addObject("limit", limit);

		} else if (arg0.getParameter("action").equalsIgnoreCase("searchRegionWest")) {
			Document[] hits = null;
			int limit = 0;
			limit = new Integer(arg0.getParameter("limit"));
			BooleanQuery query = new BooleanQuery();
			String toSearch = arg0.getParameter("query");
			String[] keytoSearch = arg0.getParameterValues("keywords");

			if (toSearch.startsWith("k:")) {
				keytoSearch = StringUtils.substringAfter(toSearch, "k:").split("~");
				toSearch = null;
			}

			if (toSearch != null && !toSearch.trim().equals("")) {

				if (toSearch.equals("{a TO Z}")) {
					query.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
				} else {
					Analyzer analyzer = LuceneFactory.getAnalyzer(indexManager.getAnalyzerClass());
					QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "title", analyzer);
					parser.setDefaultOperator(Operator.AND);
					query.add(parser.parse(toSearch), BooleanClause.Occur.SHOULD);
					QueryParser parser2 = new QueryParser(Version.LUCENE_CURRENT, "contents", analyzer);
					parser2.setDefaultOperator(Operator.AND);
					query.add(parser2.parse(toSearch), BooleanClause.Occur.SHOULD);
				}
			}
			if (keytoSearch != null && keytoSearch.length > 0) {
				for (int i = 0; i < keytoSearch.length; i++) {
					if (!keytoSearch[i].trim().equals("")) {
						TermQuery queryTerm = new TermQuery(new Term(StringUtils.substringBefore(keytoSearch[i], ":"), java.net.URLDecoder.decode(StringUtils.substringAfter(keytoSearch[i], ":"), "UTF-8")));
						query.add(queryTerm, BooleanClause.Occur.MUST);
					}
				}
			}
			if (arg0.getParameter("filtersUtente") != null) {
				TermQuery queryTerm = new TermQuery(new Term("utente", arg0.getParameter("filtersUtente")));
				query.add(queryTerm, BooleanClause.Occur.MUST);
			}
			if (arg0.getParameter("filtersDipartimento") != null) {
				TermQuery queryTerm = new TermQuery(new Term("dipartimento", arg0.getParameter("filtersDipartimento")));
				query.add(queryTerm, BooleanClause.Occur.MUST);
			}
			System.out.println("query>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + query.toString());
			mav = new ModelAndView("documental/json/adv_results");
			hits = searcher.singleSearchPaged(query, null, limit, new Integer(arg0.getParameter("start")), arg0.getParameter("id_archive"), false);
			mav.addObject("results", hits);
			mav.addObject("titleManager", titleManager);
			mav.addObject("limit", limit);

		}
		return mav;
	}

	public void setService(OpenDamsService service) {
		this.service = service;
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
