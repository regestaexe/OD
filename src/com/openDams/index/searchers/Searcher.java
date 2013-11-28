package com.openDams.index.searchers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.jdbc.JdbcDirectory;

import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.index.factory.DialectFactory;
import com.openDams.title.configuration.TitleManager;
import com.regesta.framework.util.ArrayBlockingQueue;

@SuppressWarnings("deprecation")
public class Searcher {
	private DataSource dataSource = null;
	private IndexConfiguration indexConfiguration = null;
	TitleManager titleManager = null;

	public Searcher() {
	}

	public Document[] singleSearch(Query query, String sort, String sort_type, int limit, String archive, boolean generic) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Directory directory = null;
		IndexSearcher searcher = null;
		try {
			if (generic) {
				try {
					if (indexConfiguration.isFsDirectory())
						directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getGeneric_index_name()));
					else
						directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getGeneric_index_name());
				} catch (Exception e) {
					throw new IllegalAccessException("Indice generico non configurato");
				}
			} else {
				if (indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getIndex_name()));
				else
					directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getIndex_name());
			}
			searcher = new IndexSearcher(directory, true);
			if (limit == 0)
				limit = searcher.maxDoc();
			Document[] result = null;
			ScoreDoc[] hits = null;
			TopDocs topDocs = searcher.search(query, null, limit);
			hits = topDocs.scoreDocs;
			result = new Document[hits.length];
			for (int i = 0; i < hits.length; i++) {
				int docId = hits[i].doc;
				Document docsearch = searcher.doc(docId);
				result[i] = docsearch;
			}
			/*
			 * System.out.println("-------------------PRIMA------------------");
			 * for (int i = 0; i < result.length; i++) {
			 * System.out.println(result
			 * [i].get("date")+" "+result[i].get("title_record")); }
			 */
			if (sort != null) {
				int mode = LuceneFieldDocComparator.MODE_ASC;
				if (!sort.toLowerCase().equals(sort)) { // SORT MAIUSCOLO
					mode = LuceneFieldDocComparator.MODE_DESC;
				}
				// System.out.println("ORDINO IN MODO "+mode);
				int sort_field_type = LuceneFieldDocComparator.TYPE_STRING;
				if (sort_type != null) {
					if (sort_type.equalsIgnoreCase("integer"))
						sort_field_type = LuceneFieldDocComparator.TYPE_INTEGER;
				}
				Arrays.sort(result, new LuceneFieldDocComparator(sort.toLowerCase(), mode, sort_field_type));
			}
			/*
			 * System.out.println("-------------------DOPO------------------");
			 * for (int i = 0; i < result.length; i++) {
			 * System.out.println(result
			 * [i].get("date")+" "+result[i].get("title_record")); }
			 */
			searcher.close();
			directory.close();
			return result;
		} catch (CorruptIndexException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IOException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (ClassNotFoundException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (InstantiationException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IllegalAccessException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		}
	}

	public List<VocTerm> getFacets(Query query, String field, int limit, String archive, boolean generic) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		List<VocTerm> resultTerms = new ArrayList<VocTerm>();
		Directory directory = null;
		IndexSearcher searcher = null;
		try {
			if (generic) {
				try {
					if (indexConfiguration.isFsDirectory())
						directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getGeneric_index_name()));
					else
						directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getGeneric_index_name());
				} catch (Exception e) {
					throw new IllegalAccessException("Indice generico non configurato");
				}
			} else {
				if (indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getIndex_name()));
				else
					directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getIndex_name());
			}
			searcher = new IndexSearcher(directory, true);
			IndexReader indexReader = searcher.getIndexReader();

			QueryWrapperFilter filter = new QueryWrapperFilter(query);
			CachingWrapperFilter cachingWrapperFilter = new CachingWrapperFilter(filter);
			TermEnum terms = indexReader.terms(new Term(field));
			System.out.println("CachingWrapperFilter " + filter);
			System.out.println("terms " + terms);
			while (field.equals(terms.term().field())) {
				query = new TermQuery(new Term(field, terms.term().text()));
				TopDocs topFieldDocs2 = searcher.search(query, cachingWrapperFilter, searcher.maxDoc());
				//System.out.println("********** filtered: " + topFieldDocs2.totalHits + "  --->   " + terms.term().text());
				VocTerm a = new VocTerm();
				a.setFrequence(topFieldDocs2.totalHits);
				a.setTerm(terms.term().text());
				resultTerms.add(a);
				if (!terms.next())
					break;
			}

			indexReader.close();
			searcher.close();
			directory.close();

			return resultTerms;
		} catch (CorruptIndexException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IOException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		}
	}

	public Document[] singleSearch(Query query, String sort, int limit, String archive, boolean generic) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		return singleSearch(query, sort, null, limit, archive, generic);
	}

	public Document[] singleSearchTest(Query query, String sort, int limit, String archive, boolean generic) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Directory directory = null;
		IndexSearcher searcher = null;
		try {
			if (generic) {
				try {
					if (indexConfiguration.isFsDirectory())
						directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getGeneric_index_name()));
					else
						directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getGeneric_index_name());
				} catch (Exception e) {
					throw new IllegalAccessException("Indice generico non configurato");
				}
			} else {
				if (indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getIndex_name()));
				else
					directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getIndex_name());
			}
			searcher = new IndexSearcher(directory, true);
			if (limit == 0)
				limit = searcher.maxDoc();
			Document[] result = null;
			ScoreDoc[] hits = null;
			TopScoreDocCollector collector = TopScoreDocCollector.create(limit, false);
			searcher.search(query, collector);
			int totalResults = collector.getTotalHits();
			int end = Math.min(totalResults, limit);
			hits = collector.topDocs().scoreDocs;
			result = new Document[collector.getTotalHits()];
			for (int i = 0; i < end; i++) {
				try {
					Document docsearch = searcher.doc(hits[i].doc);
					result[i] = docsearch;
				} catch (Exception e) {
					System.out.println("vado in eccezione qui");
				}
			}
			if (sort != null) {
				Arrays.sort(result, new LuceneDocComparator("label", titleManager, new Integer(archive)));
			}
			searcher.close();
			directory.close();
			return result;
		} catch (CorruptIndexException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IOException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (ClassNotFoundException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (InstantiationException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IllegalAccessException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		}
	}

	public Document[] singleSearchPaged(Query query, String sort, String sort_type, int page_size, int start, String archive, boolean generic) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Directory directory = null;
		IndexSearcher searcher = null;
		try {
			if (generic) {
				try {
					if (indexConfiguration.isFsDirectory())
						directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getGeneric_index_name()));
					else
						directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getGeneric_index_name());
				} catch (Exception e) {
					throw new IllegalAccessException("Indice generico non configurato");
				}
			} else {
				if (indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getIndex_name()));
				else
					directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getIndex_name());
			}
			searcher = new IndexSearcher(directory, true);
			ScoreDoc[] hits = null;
			Document[] result = null;
			if (sort != null) {
				int limit = searcher.maxDoc();
				TopScoreDocCollector collector = TopScoreDocCollector.create(limit, false);
				searcher.search(query, collector);
				int totalResults = collector.getTotalHits();
				int end = Math.min(totalResults, page_size + start);
				hits = collector.topDocs().scoreDocs;
				Document[] resultToOrder = new Document[totalResults];
				for (int i = 0; i < totalResults; i++) {
					try {
						Document docsearch = searcher.doc(hits[i].doc);
						resultToOrder[i] = docsearch;
					} catch (Exception e) {
						System.out.println("vado in eccezione qui");
					}
				}
				int mode = LuceneFieldDocComparator.MODE_ASC;
				if (!sort.toLowerCase().equals(sort)) { // SORT MAIUSCOLO
					mode = LuceneFieldDocComparator.MODE_DESC;
				}
				// System.out.println("ORDINO IN MODO "+mode);
				int sort_field_type = LuceneFieldDocComparator.TYPE_STRING;
				if (sort_type != null) {
					if (sort_type.equalsIgnoreCase("integer"))
						sort_field_type = LuceneFieldDocComparator.TYPE_INTEGER;
				}
				Arrays.sort(resultToOrder, new LuceneFieldDocComparator(sort.toLowerCase(), mode, sort_field_type));
				// Arrays.sort(resultToOrder, new LuceneDocComparator(sort,
				// titleManager, new Integer(archive)));
				System.out.println("risultati totali >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><" + totalResults);
				result = new Document[totalResults];
				for (int i = start; i < end; i++) {
					try {
						Document docsearch = resultToOrder[i];
						result[i] = docsearch;
					} catch (Exception e) {
						System.out.println("vado in eccezione qui");
					}
				}
			} else {
				TopScoreDocCollector collector = TopScoreDocCollector.create(page_size + start, false);
				searcher.search(query, collector);
				int totalResults = collector.getTotalHits();
				int end = Math.min(totalResults, page_size + start);
				hits = collector.topDocs().scoreDocs;
				result = new Document[totalResults];
				for (int i = start; i < end; i++) {
					try {
						Document docsearch = searcher.doc(hits[i].doc);
						result[i] = docsearch;
					} catch (Exception e) {
						System.out.println("vado in eccezione qui");
					}
				}
			}
			searcher.close();
			directory.close();
			return result;
		} catch (CorruptIndexException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IOException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (ClassNotFoundException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (InstantiationException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IllegalAccessException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		}
	}

	public Document[] singleSearchPaged(Query query, String sort, int page_size, int start, String archive, boolean generic) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		return singleSearchPaged(query, sort, null, page_size, start, archive, generic);
	}

	public HashMap<String, Object> fullLuceneDataPaged(Query query, String sort, int page_size, int start, String archive, boolean generic) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Directory directory = null;
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		IndexSearcher searcher = null;
		try {
			if (generic) {
				try {
					if (indexConfiguration.isFsDirectory())
						directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getGeneric_index_name()));
					else
						directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getGeneric_index_name());
				} catch (Exception e) {
					throw new IllegalAccessException("Indice generico non configurato");
				}
			} else {
				if (indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getIndex_name()));
				else
					directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getIndex_name());
			}
			searcher = new IndexSearcher(directory, true);
			Document[] result = null;

			Hits topdoc = null;

			if (sort != null) {
				boolean isReverse = false;
				if (!sort.toLowerCase().equals(sort)) {
					isReverse = true;
				}
				SortField[] sorts = new SortField[2];
				sorts[0] = new SortField(sort.toLowerCase(), Locale.ITALIAN, isReverse);
				sorts[1] = new SortField("id_record", org.apache.lucene.search.SortField.LONG, false);
				topdoc = searcher.search(query, new Sort(sorts));
			} else {
				topdoc = searcher.search(query);
			}

			int totResults = topdoc.length();
			resultMap.put("totResults", totResults);
			result = new Document[page_size];
			for (int i = 0; i < page_size && i + start < totResults; i++) {
				try {
					Document docsearch = topdoc.doc(i + start);
					/*
					 * System.out.println("vado qui " + docsearch);
					 * System.out.println("vado qui " +
					 * docsearch.get("numero")); System.out.println("vado qui "
					 * + docsearch.get("id_record"));
					 */
					result[i] = docsearch;
				} catch (Exception e) {
					System.out.println("Searcher.fullLuceneDataPaged() vado in eccezione qui");
				}
			}
			resultMap.put("hits", result);
			searcher.close();
			directory.close();
			return resultMap;
		} catch (CorruptIndexException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IOException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (ClassNotFoundException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (InstantiationException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IllegalAccessException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		}
	}

	public HashMap<String, Object> fullLuceneDataPaged(Query query, String sort, int page_size, int start, String archive, boolean generic, int fieldType) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Directory directory = null;
		IndexSearcher searcher = null;
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (generic) {
				try {
					if (indexConfiguration.isFsDirectory())
						directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getGeneric_index_name()));
					else
						directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getGeneric_index_name());
				} catch (Exception e) {
					throw new IllegalAccessException("Indice generico non configurato");
				}
			} else {
				if (indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getIndex_name()));
				else
					directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getIndex_name());
			}
			searcher = new IndexSearcher(directory, true);
			ScoreDoc[] hits = null;
			Document[] result = null;
			if (sort != null) {
				int mode = LuceneFieldDocComparator.MODE_ASC;
				if (!sort.toLowerCase().equals(sort)) { // SORT MAIUSCOLO
					mode = LuceneFieldDocComparator.MODE_DESC;
				}
				int limit = searcher.maxDoc();
				TopScoreDocCollector collector = TopScoreDocCollector.create(limit, false);
				searcher.search(query, collector);
				int totalResults = collector.getTotalHits();
				resultMap.put("totResults", totalResults);
				int end = Math.min(totalResults, page_size + start);
				hits = collector.topDocs().scoreDocs;
				Document[] resultToOrder = new Document[collector.getTotalHits()];
				for (int i = 0; i < collector.getTotalHits(); i++) {
					try {
						Document docsearch = searcher.doc(hits[i].doc);
						resultToOrder[i] = docsearch;
					} catch (Exception e) {
						System.out.println("vado in eccezione qui");
					}
				}
				Arrays.sort(resultToOrder, new LuceneFieldDocComparator(sort.toLowerCase(), mode, fieldType));
				result = new Document[collector.getTotalHits()];
				for (int i = start; i < end; i++) {
					try {
						Document docsearch = resultToOrder[i];
						result[i] = docsearch;
					} catch (Exception e) {
						System.out.println("vado in eccezione qui");
					}
				}
				resultMap.put("hits", result);
			} else {
				TopScoreDocCollector collector = TopScoreDocCollector.create(page_size + start, false);
				searcher.search(query, collector);
				int totalResults = collector.getTotalHits();
				int end = Math.min(totalResults, page_size + start);
				hits = collector.topDocs().scoreDocs;
				result = new Document[collector.getTotalHits()];
				for (int i = start; i < end; i++) {
					try {
						Document docsearch = searcher.doc(hits[i].doc);
						result[i] = docsearch;
					} catch (Exception e) {
						System.out.println("vado in eccezione qui");
					}
				}
				resultMap.put("hits", result);
			}
			searcher.close();
			directory.close();
			return resultMap;
		} catch (CorruptIndexException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IOException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (ClassNotFoundException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (InstantiationException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IllegalAccessException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		}
	}

	public Document[] multiSearch(Query query, Sort sort, int limit, String... indexes) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		IndexSearcher[] indexSearchers = null;
		Directory[] directorys = null;
		try {
			indexSearchers = new IndexSearcher[indexes.length];
			directorys = new Directory[indexes.length];
			for (int i = 0; i < indexes.length; i++) {
				Directory directory = null;
				if (indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + indexes[0] + "_" + indexConfiguration.getIndex_name()));
				else
					directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), indexes[0] + "_" + indexConfiguration.getIndex_name());
				IndexSearcher searcher = new IndexSearcher(directory, true);
				indexSearchers[i] = searcher;
				directorys[i] = directory;
			}
			MultiSearcher multiSearcher = new MultiSearcher(indexSearchers);
			if (limit == 0)
				limit = multiSearcher.maxDoc();
			TopFieldDocs topFieldDocs = multiSearcher.search(query, null, limit, sort);
			ScoreDoc[] hits = topFieldDocs.scoreDocs;
			Document[] result = new Document[hits.length];
			System.out.println(hits.length);
			for (int i = 0; i < hits.length; i++) {
				int docId = hits[i].doc;
				Document docsearch = multiSearcher.doc(docId);
				result[i] = docsearch;
				// System.out.println("[" + docsearch.get("frequence") + "] " +
				// docsearch.get("id_record") + " ref_id_kos= " +
				// docsearch.get("ref_id_kos"));
			}
			multiSearcher.close();
			for (int i = 0; i < indexSearchers.length; i++) {
				indexSearchers[i].close();
				directorys[i].close();
			}
			return result;
		} catch (CorruptIndexException e) {
			if (indexes != null) {
				for (int i = 0; i < indexSearchers.length; i++) {
					indexSearchers[i].close();
					directorys[i].close();
				}
			}
			throw e;
		} catch (IOException e) {
			if (indexes != null) {
				for (int i = 0; i < indexSearchers.length; i++) {
					indexSearchers[i].close();
					directorys[i].close();
				}
			}
			throw e;
		}
	}

	public Document[] xPathSearch(Query query, Sort sort, int limit, String archive) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Directory directory = null;
		IndexSearcher searcher = null;
		try {
			if (indexConfiguration.isFsDirectory())
				directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getGeneric_index_name()));
			else
				directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getGeneric_index_name());
			searcher = new IndexSearcher(directory, true);
			if (limit == 0)
				limit = searcher.maxDoc();
			TopFieldDocs topFieldDocs = searcher.search(query, null, limit, sort);

			ScoreDoc[] hits = topFieldDocs.scoreDocs;
			Document[] result = new Document[hits.length];
			for (int i = 0; i < hits.length; i++) {
				int docId = hits[i].doc;
				Document docsearch = searcher.doc(docId);
				result[i] = docsearch;
			}
			searcher.close();
			directory.close();
			return result;
		} catch (CorruptIndexException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IOException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (ClassNotFoundException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (InstantiationException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IllegalAccessException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		}
	}

	public int getTotalHitsNumber(Query query, String archive, boolean generic) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Directory directory = null;
		IndexSearcher searcher = null;
		int totalHits = 0;
		try {
			if (generic) {
				try {
					if (indexConfiguration.isFsDirectory())
						directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getGeneric_index_name()));
					else
						directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getGeneric_index_name());
				} catch (Exception e) {
					throw new IllegalAccessException("Indice generico non configurato");
				}
			} else {
				if (indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getIndex_name()));
				else
					directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getIndex_name());
			}
			searcher = new IndexSearcher(directory, true);
			TopDocs topDocs = searcher.search(query, null, searcher.maxDoc());
			totalHits = topDocs.totalHits;
			searcher.close();
			directory.close();
			return totalHits;
		} catch (CorruptIndexException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IOException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (ClassNotFoundException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (InstantiationException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IllegalAccessException e) {
			if (searcher != null) {
				searcher.close();
				searcher = null;
			}
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		}
	}

	public Document[] testSearch(Query query, Sort sort, int limit, String archive) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		try {
			Directory directory = null;
			if (indexConfiguration.isFsDirectory())
				directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getIndex_name()));
			else
				directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getIndex_name());
			IndexSearcher searcher = new IndexSearcher(directory, true);
			if (limit == 0)
				limit = searcher.maxDoc();
			TopFieldDocs topFieldDocs = searcher.search(query, null, limit, sort);
			ScoreDoc[] hits = topFieldDocs.scoreDocs;
			System.out.println("numero risultati =" + hits.length);
			Document[] result = new Document[hits.length];
			for (int i = 0; i < hits.length; i++) {
				int docId = hits[i].doc;
				Document docsearch = searcher.doc(docId);
				System.out.println(docsearch.get("id_record"));
				System.out.println(docsearch.get("title_record"));
				result[i] = docsearch;
			}
			searcher.close();
			directory.close();
			return result;
		} catch (CorruptIndexException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public Vocabulary getVocabulary(String archives, String field, String skipto, int limit, boolean generic, boolean addDocs, boolean firstTime) throws IllegalAccessException, IOException {
		MultiReader multiReader = null;
		Directory[] directorys = null;
		IndexSearcher[] indexSearchers = null;
		IndexReader[] indexReaders = null;
		Vocabulary result = null;
		try {
			String[] requestArchives = archives.split(";");
			directorys = new Directory[requestArchives.length];
			indexSearchers = new IndexSearcher[requestArchives.length];
			indexReaders = new IndexReader[requestArchives.length];
			for (int j = 0; j < requestArchives.length; j++) {
				IndexSearcher searcher = null;
				Directory directory = null;
				try {
					if (generic) {
						try {
							if (indexConfiguration.isFsDirectory())
								directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + requestArchives[j] + "_" + indexConfiguration.getGeneric_index_name()));
							else
								directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), requestArchives[j] + "_" + indexConfiguration.getGeneric_index_name());
						} catch (Exception e) {
							throw new IllegalAccessException("Indice generico non configurato");
						}
					} else {
						if (indexConfiguration.isFsDirectory())
							directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + requestArchives[j] + "_" + indexConfiguration.getIndex_name()));
						else
							directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), requestArchives[j] + "_" + indexConfiguration.getIndex_name());
					}
					directorys[j] = directory;
					searcher = new IndexSearcher(directory, true);
					indexSearchers[j] = searcher;
					indexReaders[j] = searcher.getIndexReader();
				} catch (Exception e) {
					if (searcher != null) {
						searcher.close();
					}
					try {
						directory.close();
					} catch (Exception e1) {
					}
					throw new IllegalAccessException(e.getMessage());
				}
			}
			multiReader = new MultiReader(indexReaders);
			TermEnum termEnum = null;
			/*
			 * if(skipto!=null && !skipto.equals("")) termEnum =
			 * multiReader.terms(new Term(field,skipto)); else
			 */
			termEnum = multiReader.terms();
			int count = 0;
			ArrayList<VocTerm> terms = new ArrayList<VocTerm>();
			String firstTerm = "";
			String lastTerm = "";
			boolean countdown = false;
			boolean moveTo = false;
			String localSkipto = skipto;
			if (localSkipto != null && localSkipto.indexOf("*") != -1) {
				localSkipto = localSkipto.replaceAll("\\*", "");
				moveTo = true;
			}
			ArrayBlockingQueue<Term> arrayBlockingQueue = new ArrayBlockingQueue<Term>((limit * 2) + 1);
			int stop = limit + 1;
			if (firstTime || localSkipto.equals("") && !moveTo)
				stop = limit;
			// System.out.println("----------------------------------------------------------");
			while (termEnum.next()) {
				Term term = termEnum.term();
				if (field.equalsIgnoreCase(term.field())) {
					if (firstTerm.equals(""))
						firstTerm = term.text();
					arrayBlockingQueue.add(term);
					if (localSkipto == null || localSkipto.equalsIgnoreCase(term.text()) || localSkipto.equals("") && !countdown) {
						countdown = true;
					}
					if (count < stop && countdown && (count > 0 || stop == limit || moveTo)) {
						terms.add(getVocTerm(multiReader, term, addDocs));
						if (moveTo && count == 0)
							count++;
						// System.out.println(term.text());
					}
					if (countdown) {
						count++;
					}
					if (count == stop) {
						break;
					}
					lastTerm = term.text();
				}
			}
			termEnum.close();
			if (terms.size() == 0 && skipto != null && skipto.indexOf("*") != -1) {
				termEnum = multiReader.terms();
				while (termEnum.next()) {
					Term term = termEnum.term();
					if (field.equalsIgnoreCase(term.field())) {
						if (firstTerm.equals(""))
							firstTerm = term.text();
						arrayBlockingQueue.add(term);
						// if(term.text().toLowerCase().startsWith(
						// skipto.substring(0,
						// skipto.length()-1).toLowerCase())){
						// System.out.println(term.text()+"---------------->"+skipto.toLowerCase().replaceAll("\\*",
						// ".*")+"---------------->"+term.text().toLowerCase().matches(skipto.toLowerCase().replaceAll("\\*",
						// ".*")));
						if (term.text().toLowerCase().matches(skipto.toLowerCase().replaceAll("\\*", ".*"))) {
							countdown = true;
							count++;
						}
						if (count < stop && countdown && (count > 0 || stop == limit)) {
							// if(count<stop && countdown && (stop==limit)){
							terms.add(getVocTerm(multiReader, term, addDocs));
							// System.out.println(term.text());
						}
						if (countdown) {
							count++;
						}
						if (count == stop) {
							break;
						}
						lastTerm = term.text();
					}
				}
				termEnum.close();
			}
			if (count < stop) {
				for (int i = 0; i < stop - count; i++) {
					arrayBlockingQueue.add(new Term("aa"));
				}
			}
			multiReader.close();
			for (int i = 0; i < indexSearchers.length; i++) {
				indexSearchers[i].close();
				directorys[i].close();
				try {
					indexReaders[i].close();
				} catch (Exception e) {
					System.err.println("i readers sono chiusi!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
				}
			}
			result = new Vocabulary();
			result.setTerms(terms);
			// System.out.println("terms = "+terms.toString());
			if (terms.size() > 0 && lastTerm.equals(terms.get(terms.size() - 1).getTerm()))
				result.setLastPage(true);
			else
				result.setLastPage(false);
			if (terms.size() > 0 && terms.get(0).getTerm().equals(firstTerm))
				result.setFirstPage(true);
			else
				result.setFirstPage(false);
			if (arrayBlockingQueue.size() > limit) {
				String term = arrayBlockingQueue.remove().text();
				if (term.equals(firstTerm)) {
					result.setTermForPrevious("");
				} else {
					result.setTermForPrevious(term);
				}
			} else {
				result.setTermForPrevious("");
			}
			result.setFirst_term(firstTerm);
			result.setLast_term(lastTerm);
			if (moveTo && terms.size() == 0) {
				result.setLastPage(true);
				result.setFirstPage(true);
			}
			/*
			 * System.out.println("firstTerm = "+firstTerm);
			 * System.out.println("lastTerm = "+lastTerm);
			 * System.out.println("firstPage = "+result.isFirstPage());
			 * System.out.println("lasPage = "+result.isLastPage());
			 * System.out.println("lasPage = "+result.isLastPage());
			 * System.out.println
			 * ("termForPrevious = "+result.getTermForPrevious());
			 * System.out.println
			 * ("----------------------------------------------------------");
			 */

			return result;
		} catch (CorruptIndexException e) {
			try {
				multiReader.close();
				for (int i = 0; i < indexSearchers.length; i++) {
					indexSearchers[i].close();
					directorys[i].close();
					try {
						indexReaders[i].close();
					} catch (Exception ex) {
						System.err.println("i readers sono chiusi!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
					}
				}
			} catch (Exception e1) {
			}
			throw e;
		} catch (IllegalAccessException e) {
			try {
				multiReader.close();
				for (int i = 0; i < indexSearchers.length; i++) {
					indexSearchers[i].close();
					directorys[i].close();
					try {
						indexReaders[i].close();
					} catch (Exception ex) {
						System.err.println("i readers sono chiusi!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
					}
				}
			} catch (Exception e1) {
			}
			throw e;
		} catch (IOException e) {
			try {
				multiReader.close();
				for (int i = 0; i < indexSearchers.length; i++) {
					indexSearchers[i].close();
					directorys[i].close();
					try {
						indexReaders[i].close();
					} catch (Exception ex) {
						System.err.println("i readers sono chiusi!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
					}
				}
			} catch (Exception e1) {
			}
			throw e;
		}

	}

	public Vocabulary getVocabularyLastTerms(String archives, String field, int limit, boolean generic, boolean addDocs) throws IllegalAccessException, IOException {
		MultiReader multiReader = null;
		Directory[] directorys = null;
		IndexSearcher[] indexSearchers = null;
		IndexReader[] indexReaders = null;
		Vocabulary result = null;
		try {
			String[] requestArchives = archives.split(";");
			directorys = new Directory[requestArchives.length];
			indexSearchers = new IndexSearcher[requestArchives.length];
			indexReaders = new IndexReader[requestArchives.length];
			for (int j = 0; j < requestArchives.length; j++) {
				IndexSearcher searcher = null;
				Directory directory = null;
				try {
					if (generic) {
						try {
							if (indexConfiguration.isFsDirectory())
								directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + requestArchives[j] + "_" + indexConfiguration.getGeneric_index_name()));
							else
								directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), requestArchives[j] + "_" + indexConfiguration.getGeneric_index_name());
						} catch (Exception e) {
							throw new IllegalAccessException("Indice generico non configurato");
						}
					} else {
						if (indexConfiguration.isFsDirectory())
							directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + requestArchives[j] + "_" + indexConfiguration.getIndex_name()));
						else
							directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), requestArchives[j] + "_" + indexConfiguration.getIndex_name());
					}
					directorys[j] = directory;
					searcher = new IndexSearcher(directory, true);
					indexSearchers[j] = searcher;
					indexReaders[j] = searcher.getIndexReader();
				} catch (Exception e) {
					if (searcher != null) {
						searcher.close();
					}
					try {
						directory.close();
					} catch (Exception e1) {
					}
					throw new IllegalAccessException(e.getMessage());
				}
			}
			multiReader = new MultiReader(indexReaders);
			TermEnum termEnum = multiReader.terms();
			ArrayList<VocTerm> terms = new ArrayList<VocTerm>();
			ArrayBlockingQueue<Term> arrayBlockingQueue = new ArrayBlockingQueue<Term>(limit);
			while (termEnum.next()) {
				Term term = termEnum.term();
				if (field.equalsIgnoreCase(term.field())) {
					arrayBlockingQueue.add(term);
				}
			}
			termEnum.close();
			for (int i = 0; i < arrayBlockingQueue.size(); i++) {
				terms.add(getVocTerm(multiReader, arrayBlockingQueue.poll(), addDocs));
			}
			multiReader.close();
			for (int i = 0; i < indexSearchers.length; i++) {
				indexSearchers[i].close();
				directorys[i].close();
				try {
					indexReaders[i].close();
				} catch (Exception e) {
					System.err.println("i readers sono chiusi!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
				}
			}

			result = new Vocabulary();
			result.setTerms(terms);

			return result;
		} catch (CorruptIndexException e) {
			try {
				multiReader.close();
				for (int i = 0; i < indexSearchers.length; i++) {
					indexSearchers[i].close();
					directorys[i].close();
					try {
						indexReaders[i].close();
					} catch (Exception ex) {
						System.err.println("i readers sono chiusi!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
					}
				}
			} catch (Exception e1) {
			}
			throw e;
		} catch (IllegalAccessException e) {
			try {
				multiReader.close();
				for (int i = 0; i < indexSearchers.length; i++) {
					indexSearchers[i].close();
					directorys[i].close();
					try {
						indexReaders[i].close();
					} catch (Exception ex) {
						System.err.println("i readers sono chiusi!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
					}
				}
			} catch (Exception e1) {
			}
			throw e;
		} catch (IOException e) {
			try {
				multiReader.close();
				for (int i = 0; i < indexSearchers.length; i++) {
					indexSearchers[i].close();
					directorys[i].close();
					try {
						indexReaders[i].close();
					} catch (Exception ex) {
						System.err.println("i readers sono chiusi!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
					}
				}
			} catch (Exception e1) {
			}
			throw e;
		}

	}

	private int getTermFrequence(IndexReader indexReader, Term term) throws IOException {
		TermDocs termDocs = indexReader.termDocs(term);
		int frequence = 0;
		while (termDocs.next()) {
			frequence += termDocs.freq();
		}
		return frequence;
	}

	private VocTerm getVocTerm(IndexReader indexReader, Term term, boolean addDocs) throws IOException {
		TermDocs termDocs = indexReader.termDocs(term);
		int frequence = 0;
		VocTerm vocTerm = new VocTerm();
		vocTerm.setTerm(term.text());
		while (termDocs.next()) {
			frequence += termDocs.freq();
			if (addDocs) {
				vocTerm.addDoc(indexReader.document(termDocs.doc()));
			}
		}
		vocTerm.setFrequence(frequence);
		return vocTerm;
	}

	public IndexConfiguration getIndexConfiguration() {
		return indexConfiguration;
	}

	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}

	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public static void main(String[] args) {
		String skipto = "vocabolario*";
		String termine = "vocabolario";
		String localSkipto = skipto;
		localSkipto = localSkipto.replaceAll("\\*", "");
		System.out.println(localSkipto);
		skipto = skipto.replaceAll("\\*", ".*");
		System.out.println(skipto);
		System.out.println(termine.matches(skipto));

	}
}
