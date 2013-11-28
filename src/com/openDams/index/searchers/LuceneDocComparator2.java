package com.openDams.index.searchers;

import java.util.Comparator;
import java.util.HashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

import com.openDams.title.configuration.TitleManager;

public class LuceneDocComparator2 implements Comparator<ScoreDoc> {
	private String fieldToCompare = "";
	private TitleManager titleManager = null;
	private int idArchive;
	private IndexSearcher searcher = null;
	public LuceneDocComparator2(String fieldToCompare,TitleManager titleManager,int idArchive,IndexSearcher searcher){
		this.fieldToCompare = fieldToCompare;
		this.titleManager = titleManager;
		this.idArchive = idArchive;
		this.searcher = searcher;
	}
	@Override
	public int compare(ScoreDoc arg0, ScoreDoc arg1) {
		try {
			Document doc1 =  searcher.doc(arg0.doc);
			Document doc2 =  searcher.doc(arg1.doc);
			HashMap<String, String[]> parsedTitle  = titleManager.parseTitle(doc1.get("title_record"),idArchive);
			HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(doc2.get("title_record"),idArchive);
			return parsedTitle.get(fieldToCompare)[0].compareToIgnoreCase(parsedTitle2.get(fieldToCompare)[0]);
		} catch (Exception e){
			System.out.println(e.getMessage());
			return 1;
		}
	}

}
