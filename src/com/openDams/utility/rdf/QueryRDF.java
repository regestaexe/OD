package com.openDams.utility.rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class QueryRDF {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static List<HashMap<String, Value>> doTupleQuery(RepositoryConnection con, String query) {

		List<HashMap<String, Value>> results = new ArrayList<HashMap<String, Value>>();
		try {
			System.out.println("************************\n" + query);
			TupleQuery resultsTable = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
			TupleQueryResult bindings = resultsTable.evaluate();
			for (int row = 0; bindings.hasNext(); row++) {
				HashMap<String, Value> riga = new HashMap<String, Value>();
				// System.out.println("RESULT " + (row + 1) + ": ");
				BindingSet pairs = bindings.next();
				List<String> names = bindings.getBindingNames();
				for (int i = 0; i < names.size(); i++) {
					String name = names.get(i);
					Value value = pairs.getValue(name);
					
					riga.put(name, value != null && value.stringValue().equals("") ? null : value);
				}
				results.add(riga);
			}
 		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(results.size() + "\n************************\n");
		return results;
	}

	public static boolean doBooleanQuery(RepositoryConnection con, String query) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		System.out.println("************************\n" + query);
		BooleanQuery resultsTable = con.prepareBooleanQuery(QueryLanguage.SPARQL, query);
		boolean result = resultsTable.evaluate();
		System.out.println(result + "\n************************\n");
		return result;
	}

	public static String getAbout(Value value) {
		return getAbout(value.stringValue());
	}

	public static String getValue(String chiave, HashMap<String, Value> colonne) {
		return (String) getValue(chiave, colonne, "");
	}

	public static Object getValue(String chiave, HashMap<String, Value> colonne, Object defaultValue) {
		return colonne.get(chiave) != null ? colonne.get(chiave).stringValue() : defaultValue;
	}

	public static String getAbout(String about) {
		if (!about.equals("")) {
			about = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(about, "/"), "/") + "/" + StringUtils.substringAfterLast(about, "/");
		}
		return about;
	}

}
