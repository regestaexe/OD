package com.openDams.index.searchers;

import java.util.Comparator;
import java.util.HashMap;

import org.apache.lucene.document.Document;

import com.openDams.title.configuration.TitleManager;

public class LuceneDocComparator implements Comparator<Document> {
	private String fieldToCompare = "";
	private TitleManager titleManager = null;
	private int idArchive;
	public LuceneDocComparator(String fieldToCompare,TitleManager titleManager,int idArchive) {
		this.fieldToCompare = fieldToCompare;
		this.titleManager = titleManager;
		this.idArchive = idArchive;
	}
	@Override
	public int compare(Document arg0, Document arg1) {		
		try {
			HashMap<String, String[]> parsedTitle  = titleManager.parseTitle(arg0.get("title_record"),idArchive);
			HashMap<String, String[]> parsedTitle2 = titleManager.parseTitle(arg1.get("title_record"),idArchive);
			return parsedTitle.get(fieldToCompare)[0].compareToIgnoreCase(parsedTitle2.get(fieldToCompare)[0]);
		} catch (Exception e){
			System.out.println(e.getMessage());
			return 1;
		}
	}

}
