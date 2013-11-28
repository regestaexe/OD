package com.openDams.documental.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.index.factory.IndexManager;
import com.openDams.index.factory.LuceneFactory;
import com.openDams.index.searchers.LuceneFieldDocComparator;
import com.openDams.index.searchers.Searcher;
import com.openDams.index.searchers.VocTerm;
import com.openDams.index.searchers.Vocabulary;
import com.openDams.utility.StringsUtils;
import com.regesta.framework.xml.XMLReader;

public class SearchUtilsController implements Controller, ServletContextAware {
	private ServletContext servletContext;
	private Searcher searcher = null;
	private IndexManager indexManager = null;
	// private TitleManager titleManager;
	private IndexConfiguration indexConfiguration = null;
	private int page_size = 1;

	public static void main(String[] args) {
		String[] termini = "Gazzetta ufficiale dell'Unione europea C 086 del 23 marzo 2012;Gazzetta ufficiale dell'Unione europea C 006A del 23 marzo 2012;Gazzetta ufficiale dell'Unione europea L 071 del 9 marzo 2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 1 del 7-1-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 10 del 10-3-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 11 del 17-3-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 12 del 24-3-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 13 del 31-3-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 14 del 7-4-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 15 del 14-4-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 16 del 21-4-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 17 del 28-4-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 18 del 5-5-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 19 del 12-5-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 2 del 14-1-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 20 del 19-5-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 21 del 26-5-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 22 del 9-6-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 23 del 16-6-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 24 del 23-6-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 25 del 30-6-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 26 del 7-7-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 27 del 14-7-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 28 del 21-7-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 29 del 28-7-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 3 del 21-1-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 30 del 4-8-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 31 del 11-8-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 32 del 18-8-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 33 del 25-8-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 4 del 28-1-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 5 del 4-2-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 6 del 11-2-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 7 del 18-2-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 8 del 25-2-2012;Gazzetta Ufficiale - 3a Serie Speciale Regioni n. 9 del 3-3-2012"
				.split(";");

		Arrays.sort(termini, new Comparator<String>() {
			public int compare(String arg0, String arg1) {
				try {
					arg0 = arg0.replaceAll(".*(n\\.|L|C)\\s*([0-9]*[A-Z]*)\\s.*del.*", "$2");
					arg1 = arg1.replaceAll(".*(n\\.|L|C)\\s*([0-9]*[A-Z]*)\\s.*del.*", "$2");

					System.out.println(arg0 + "------------------------>" + arg1 + "===== " + (Integer.parseInt(arg0.replaceAll("[A-Z]*", "")) > Integer.parseInt(arg1.replaceAll("[A-Z]*", ""))));

					return (Integer.parseInt(arg0.replaceAll("[A-Z]*", "")) > Integer.parseInt(arg1.replaceAll("[A-Z]*", ""))) ? 0 : 1;
				} catch (Exception e) {
					e.printStackTrace();
					return 0;
				}
			}
		});
		String result = "";
		for (String vocTerm : termini) {
			System.out.println(vocTerm);
		}
	}

	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		System.out.println("SearchUtilsController.handleRequest()");
		mav = new ModelAndView("documental/json/simpleJsonResponse");

		if (arg0.getParameter("action") != null && arg0.getParameter("action").equals("vocList")) {
			Integer limit = 0;
			try {
				limit = new Integer(arg0.getParameter("limit"));
			} catch (Exception e) {
				// TODO: handle exception
			}
			/*
			 * Integer start = 0; try { start = new
			 * Integer(arg0.getParameter("start")); } catch (Exception e) { }
			 */
			String sort = arg0.getParameter("sort");
			String field = arg0.getParameter("field");
			String skipTo = arg0.getParameter("skipTo");
			String filter = arg0.getParameter("filter");

			if (skipTo == null) {
				skipTo = "";
			}
			Vocabulary voc = searcher.getVocabulary(arg0.getParameter("id_archive"), field, skipTo, limit, false, false, true);
			ArrayList<VocTerm> at = voc.getTerms();
			ArrayList<VocTerm> a = new ArrayList<VocTerm>();

			for (VocTerm vocTerm : at) {
				if (vocTerm.frequence < 1) {
					continue;
				}
				if (filter != null && !filter.equals("")) {
					if (!vocTerm.term.trim().matches(filter)) {
						continue;
					}
				}
				a.add(vocTerm);
			}

			StringBuilder result = new StringBuilder();
			result.append("[");
			String[] termini = new String[a.size()];
			int aInt = 0;
			for (VocTerm vocTerm : a) {
				termini[aInt++] = (vocTerm.term);
			}
			if (sort != null && sort.equals("gazzette")) {
				Arrays.sort(termini, new Comparator<String>() {
					public int compare(String arg0, String arg1) {
						try {
							arg0 = arg0.replaceAll(".*(n\\.|L|C)\\s*([0-9]*[A-Z]*)\\s.*del.*", "$2");
							arg1 = arg1.replaceAll(".*(n\\.|L|C)\\s*([0-9]*[A-Z]*)\\s.*del.*", "$2");
							return (Integer.parseInt(arg0.replaceAll("[A-Z]*", "")) > Integer.parseInt(arg1.replaceAll("[A-Z]*", ""))) ? 0 : 1;
						} catch (Exception e) {
							e.printStackTrace();
							return 0;
						}
					}
				});
			}
			for (String vocTerm : termini) {
				result.append(StringsUtils.escapeJson(vocTerm));
				result.append(",");
			}

			String ilResult = result.toString();
			if (ilResult.endsWith(",")) {
				ilResult = ilResult.substring(0, ilResult.length() - 1);
			}

			mav.addObject("result", ilResult + "]");
		} else if (arg0.getParameter("action") != null && arg0.getParameter("action").equals("vocListDottrina")) {
			String xmlPath = "";
			if (!indexConfiguration.isUse_external_conf_location()) {
				xmlPath += servletContext.getRealPath("");
			}
			xmlPath += indexConfiguration.getConfiguration_location() + "/" + arg0.getParameter("id_archive") + "/department_filter_magazines.xml";

			StringBuilder result = new StringBuilder();
			result.append("{\"riviste\":[");
			System.out.println("DIPARTIMENTO " + arg0.getParameter("department"));
			if (arg0.getParameter("department") == null || arg0.getParameter("department").trim().equals("")) {
				Vocabulary voc = searcher.getVocabulary(arg0.getParameter("id_archive"), "rivista", "", 1000, false, false, true);
				ArrayList<VocTerm> at = voc.getTerms();

				for (int i = 0; i < at.size(); i++) {
					VocTerm vocTerm = at.get(i);
					result.append("{\"rivista\":" + StringsUtils.escapeJsonNoXML(vocTerm.term) + "}");
					if (i < at.size() - 1)
						result.append(",");
				}
			} else {
				XMLReader xmlReader = new XMLReader(new File(xmlPath));
				ArrayList<String> magazines = xmlReader.getNodesValues("/department_list/department[@id='" + arg0.getParameter("department") + "']/magazine/text()");
				for (int i = 0; i < magazines.size(); i++) {
					result.append("{\"rivista\":" + StringsUtils.escapeJsonNoXML(magazines.get(i)) + "}");
					System.out.println(magazines.get(i));
					System.out.println(StringsUtils.escapeJson(magazines.get(i)));
					if (i < magazines.size() - 1)
						result.append(",");
				}
			}
			result.append("]}");
			mav.addObject("result", result.toString());
		} else {

			Integer start = 0;
			try {
				start = new Integer(arg0.getParameter("start"));
			} catch (Exception e) {
				// TODO: handle exception
			}
			Document[] hits = null;
			int limit = 0;
			try {
				limit = new Integer(arg0.getParameter("limit"));
			} catch (Exception e) {
				limit = page_size;
			}

			String sort = arg0.getParameter("sort");

			BooleanQuery query = new BooleanQuery();
			String[] toSearchArray = arg0.getParameterValues("query");

			if (toSearchArray.length == 1) {
				toSearchArray = toSearchArray[0].split("\\[array\\]");
			}

			for (int i = 0; i < toSearchArray.length; i++) {
				String toSearch = toSearchArray[i];
				if (toSearch != null && !toSearch.trim().equals("")) {

					if (toSearch.startsWith("k.")) {
						toSearch = StringUtils.substringAfter(toSearch, "k.");
						// Analyzer analyzer = new KeywordAnalyzer();
						// QueryParser parser = new
						// QueryParser(Version.LUCENE_CURRENT, "contents",
						// analyzer);
						query.add(new TermQuery(new Term(StringUtils.substringBefore(toSearch, ":"), StringUtils.substringAfter(toSearch, ":"))), BooleanClause.Occur.MUST);
					} else {
						Analyzer analyzer = LuceneFactory.getAnalyzer(indexManager.getAnalyzerClass());
						QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "contents", analyzer);
						query.add(parser.parse(toSearch), BooleanClause.Occur.MUST);
					}

					System.out.println(">>>>>>>>>>>>>>>>>>>>>toSearch<<<<<<<<<<<<<<<<<" + toSearch);
				}
			}

			String[] resultFormat = arg0.getParameterValues("resultFormat[]");
			// hits = searcher.fullLuceneDataPaged(query, sort, limit, start,
			// arg0.getParameter("id_archive"), false,
			// LuceneFieldDocComparator.TYPE_INTEGER);
			/*
			 * System.out.println("SearchUtilsController.handleRequest() query "
			 * + query);
			 * System.out.println("SearchUtilsController.handleRequest() sort "
			 * + sort);
			 * System.out.println("SearchUtilsController.handleRequest() limit "
			 * + limit);
			 * System.out.println("SearchUtilsController.handleRequest() start "
			 * + start); System.out.println(
			 * "SearchUtilsController.handleRequest() id_archive " +
			 * arg0.getParameter("id_archive"));
			 */
			// HashMap<String, Object> resultMap =
			// searcher.fullLuceneDataPaged(query, sort, limit, start,
			// arg0.getParameter("id_archive"), false);
			HashMap<String, Object> resultMap = searcher.fullLuceneDataPaged(query, sort, limit, start, arg0.getParameter("id_archive"), false, LuceneFieldDocComparator.TYPE_INTEGER);
			hits = (Document[]) resultMap.get("hits");
			// System.out.println(">>>>>>>>>>>>>>>>>>>>>totalCount<<<<<<<<<<<<<<<<<"
			// + hits.length);

			StringBuffer result = new StringBuffer();
			result.append("[");
			for (int i = 0; i < limit && i < hits.length; i++) {
				Document docsearch = hits[i];
				try {
					result.append("{");
					for (int j = 0; j < resultFormat.length; j++) {
						if (resultFormat[j] != null) {
							try {
								result.append("\"" + resultFormat[j] + "\":\"" + com.regesta.framework.util.JsSolver.escapeDoubleApex(docsearch.get(resultFormat[j]).replaceAll("\n", "<br />")) + "\",");
							} catch (Exception se) {
								// TODO: handle exception
							}
						}
					}
					result.append("\"void\":\"void\"");
					result.append("},");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			String ilResult = result.toString();
			if (ilResult.endsWith(",")) {
				ilResult = ilResult.substring(0, ilResult.length() - 1);
			}
			mav.addObject("result", ilResult + "]");
			mav.addObject("totResults", resultMap.get("totResults"));
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

	/*
	 * public void setTitleManager(TitleManager titleManager) {
	 * this.titleManager = titleManager; }
	 */

	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}

	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}

	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;
	}
}
