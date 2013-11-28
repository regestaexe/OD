package com.openDams.index.searchers;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;

import com.openDams.configuration.ConfigurationException;

public class QueryBuilder {
	private SearchFieldManager searchFieldManager = null;
	private String highlightFields = null;

	public QueryBuilder() {

	}

	public BooleanQuery buildQuery(int idArchive, String[] multipleQuerys) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigurationException, ParseException {
		BooleanQuery booleanQuery = new BooleanQuery();
		for (int x = 0; x < multipleQuerys.length; x++) {
			String singleFieldQuery = multipleQuerys[x].trim();
			Query query = null;
			Occur operator = getBooleanOperator(singleFieldQuery);
			if (operator.equals(BooleanClause.Occur.MUST)) {
				singleFieldQuery = StringUtils.substringAfter(singleFieldQuery, "+");
				query = getQuery(idArchive, singleFieldQuery);
			} else if (operator.equals(BooleanClause.Occur.MUST_NOT)) {
				singleFieldQuery = StringUtils.substringAfter(singleFieldQuery, "-");
				query = getQuery(idArchive, singleFieldQuery);
			} else {
				query = getQuery(idArchive, singleFieldQuery);
			}
			booleanQuery.add(query, operator);

		}
		return booleanQuery;
	}

	public Query getQuery(int idArchive, String singleFieldQuery) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigurationException, ParseException {
		if (singleFieldQuery.indexOf("[") != -1 && singleFieldQuery.indexOf("]") != -1 && singleFieldQuery.indexOf(" TO ") != -1) {
			String field = StringUtils.substringBefore(singleFieldQuery, ":");
			String start = escapeLuceneSpecialCharacters(StringUtils.substringBetween(singleFieldQuery, "[", " TO"), false);
			String end = escapeLuceneSpecialCharacters(StringUtils.substringBetween(singleFieldQuery, "TO ", "]"), false);
			TermRangeQuery termRangeQuery = new TermRangeQuery(field, start, end, true, true);
			return termRangeQuery;
		} else if (singleFieldQuery.indexOf("{") != -1 && singleFieldQuery.indexOf("}") != -1 && singleFieldQuery.indexOf(" TO ") != -1) {
			if (singleFieldQuery.equals("{a TO Z}")) {
				return new MatchAllDocsQuery();
			} else {
				String field = StringUtils.substringBefore(singleFieldQuery, ":");
				String start = escapeLuceneSpecialCharacters(StringUtils.substringBetween(singleFieldQuery, "{", " TO"), false);
				String end = escapeLuceneSpecialCharacters(StringUtils.substringBetween(singleFieldQuery, "TO ", "}"), false);
				TermRangeQuery termRangeQuery = new TermRangeQuery(field, start, end, false, false);
				return termRangeQuery;
			}
		} else if (singleFieldQuery.startsWith("(") && singleFieldQuery.endsWith(")")) {
			BooleanQuery booleanQuery = new BooleanQuery();
			String[] multipleQuerys = StringUtils.substringBetween(singleFieldQuery, "(", ")").split("\\$");
			for (int x = 0; x < multipleQuerys.length; x++) {
				String newSingleFieldQuery = multipleQuerys[x].trim();
				Query query = null;
				Occur operator = getBooleanOperator(newSingleFieldQuery);
				if (operator.equals(BooleanClause.Occur.MUST)) {
					newSingleFieldQuery = StringUtils.substringAfter(newSingleFieldQuery, "+");
					query = getQuery(idArchive, newSingleFieldQuery);
				} else if (operator.equals(BooleanClause.Occur.MUST_NOT)) {
					newSingleFieldQuery = StringUtils.substringAfter(newSingleFieldQuery, "-");
					query = getQuery(idArchive, newSingleFieldQuery);
				} else {
					query = getQuery(idArchive, newSingleFieldQuery);
				}
				booleanQuery.add(query, operator);
			}
			return booleanQuery;
		} else {
			Analyzer analyzer = searchFieldManager.getSearchAnalizer(idArchive, StringUtils.substringBefore(singleFieldQuery, ":"));

			QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, StringUtils.substringBefore(singleFieldQuery, ":"), analyzer);

			String value = StringUtils.substringAfter(singleFieldQuery, ":");

			// if (!searchFieldManager.getKeyStyle(idArchive,
			// StringUtils.substringBefore(singleFieldQuery,
			// ":")).equalsIgnoreCase("one")) {
			// value = escapeLuceneSpecialCharacters(value, true).trim();
			// value = "\"" + value + "\"";
			// } else {
			// value = escapeLuceneSpecialCharacters(value, false).trim();
			// parser.setDefaultOperator(Operator.AND);
			// }

			if (!searchFieldManager.getKeyStyle(idArchive, StringUtils.substringBefore(singleFieldQuery, ":")).equalsIgnoreCase("one")) {
				value = escapeLuceneSpecialCharacters(value, false).trim();
				parser.setDefaultOperator(Operator.AND);
			} else if (value.indexOf(" ") != -1) {
				value = escapeLuceneSpecialCharacters(value, true).trim();
				value = "\"" + value + "\"";
			} else {
				value = escapeLuceneSpecialCharacters(value, true).trim();
			}

			//
			// if(!searchFieldManager.getKeyStyle(idArchive,
			// StringUtils.substringBefore(singleFieldQuery,":")).equalsIgnoreCase("one")){
			// }else if(value.indexOf(" ")!=-1){
			// }
			Query query = parser.parse(value);
			return query;
		}
	}

	public Occur getBooleanOperator(String queryString) {
		//System.out.println("queryStringqueryStringqueryString " + queryString);
		if (queryString.startsWith("+")) {
			return BooleanClause.Occur.MUST;
		} else if (queryString.startsWith("-")) {
			return BooleanClause.Occur.MUST_NOT;
		} else {
			return BooleanClause.Occur.SHOULD;
		}
	}

	public String getWordsToHilight(String[] multipleQuerys) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigurationException, ParseException {
		String result = "";
		for (int x = 0; x < multipleQuerys.length; x++) {
			String singleFieldQuery = multipleQuerys[x].trim();
			if (singleFieldQuery.indexOf("$") != -1 && singleFieldQuery.endsWith(")")) {
				singleFieldQuery = StringUtils.substringAfter(singleFieldQuery, "(");
				singleFieldQuery = StringUtils.substringBeforeLast(singleFieldQuery, ")");
				String[] multipleQuerysRanged = singleFieldQuery.split("\\$");
				for (int y = 0; y < multipleQuerysRanged.length; y++) {
					String singleFieldQueryRanged = multipleQuerysRanged[y].trim();
					String field = StringUtils.substringBefore(singleFieldQueryRanged, ":");
					if (field.startsWith("+")) {
						field = StringUtils.substringAfter(field, "+");
					} else if (field.startsWith("-")) {
						field = StringUtils.substringAfter(field, "-");
					}
					String value = StringUtils.substringAfter(singleFieldQueryRanged, ":");
					String[] fielsdToHighLight = highlightFields.split(",");
					for (int i = 0; i < fielsdToHighLight.length; i++) {
						if (field.equalsIgnoreCase(fielsdToHighLight[i])) {
							value = StringUtils.normalizeSpace(value);
							value = value.replaceAll(" ", "~");
							result += value + "~";
						}
					}
				}
			} else {
				String field = StringUtils.substringBefore(singleFieldQuery, ":");
				if (field.startsWith("+")) {
					field = StringUtils.substringAfter(field, "+");
				} else if (field.startsWith("-")) {
					field = StringUtils.substringAfter(field, "-");
				}
				String value = StringUtils.substringAfter(singleFieldQuery, ":");
				String[] fielsdToHighLight = highlightFields.split(",");
				for (int i = 0; i < fielsdToHighLight.length; i++) {
					if (field.equalsIgnoreCase(fielsdToHighLight[i])) {
						value = StringUtils.normalizeSpace(value);
						value = value.replaceAll(" ", "~");
						result += value;
					}
				}
			}

		}
		return result;
	}

	private String escapeLuceneSpecialCharacters(String toEscape, boolean preserveApex) {
		String result = toEscape;
		result = result.replaceAll("\\\\", "\\\\");
		result = result.replaceAll("&", "\\\\&");
		result = result.replaceAll("\\|", "\\\\|");
		result = result.replaceAll("\\+", "\\\\+");
		result = result.replaceAll("\\-", "\\\\-");
		result = result.replaceAll("\\!", "\\\\!");
		result = result.replaceAll("\\(", "\\\\(");
		result = result.replaceAll("\\)", "\\\\)");
		result = result.replaceAll("\\{", "\\\\{");
		result = result.replaceAll("\\}", "\\\\}");
		result = result.replaceAll("\\[", "\\\\[");
		result = result.replaceAll("\\]", "\\\\]");
		result = result.replaceAll("\\^", "\\\\^");
		result = result.replaceAll("\\?", "\\\\?");
		result = result.replaceAll(":", "\\\\:");
		result = result.replaceAll("~", "\\\\~");
		if (!preserveApex) {
			result = result.replaceAll("'", " ");
		}

		if (toEscape.trim().startsWith("*"))
			result = StringUtils.stripStart(result, "*");
		if (StringUtils.countMatches(result, "\"") % 2 > 0)
			result = result.replaceAll("\"", "\\\\\"");
		return result;
	}

	public void setSearchFieldManager(SearchFieldManager searchFieldManager) {
		this.searchFieldManager = searchFieldManager;
	}

	public void setHighlightFields(String highlightFields) {
		this.highlightFields = highlightFields;
	}

	public static void main(String[] args) {
		QueryBuilder queryBuilder = new QueryBuilder();
		System.out.println(queryBuilder.escapeLuceneSpecialCharacters("Diritto ed economia dell'assicurazione [ex Diritto e pratica nell'assicurazione]", false));
	}
}
