package com.openDams.index.searchers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.openDams.index.factory.LuceneFactory;

public class LuceneFieldDocComparator implements Comparator<Document> {
	private String fieldToCompare = "";
	private int mode = 0;
	private int fieldType = 0;
	public final static int MODE_ASC = 0;
	public final static int MODE_DESC = 1;
	public final static int TYPE_STRING = 0;
	public final static int TYPE_INTEGER = 1;

	public LuceneFieldDocComparator(String fieldToCompare, int mode, int fieldType) {
		this.fieldToCompare = fieldToCompare;
		this.mode = mode;
		this.fieldType = fieldType;
	}

	public int compare(Document arg0, Document arg1) {
		try {
			if (mode == MODE_ASC) {
				if (fieldType == TYPE_STRING) {
					return arg0.get(fieldToCompare).compareToIgnoreCase(arg1.get(fieldToCompare));
				} else if (fieldType == TYPE_INTEGER) {
					int first = Integer.parseInt(arg0.get(fieldToCompare).replaceAll("[^0-9].*$", ""));
					int second = Integer.parseInt(arg1.get(fieldToCompare).replaceAll("[^0-9].*$", ""));
					return (first - second);
				} else {
					throw new Exception("INCOMPATIBLE FIELD TYPE MODE");
				}
			} else if (mode == MODE_DESC) {
				if (fieldType == TYPE_STRING) {
					return arg1.get(fieldToCompare).compareToIgnoreCase(arg0.get(fieldToCompare));
				} else if (fieldType == TYPE_INTEGER) {
					int first = Integer.parseInt(arg0.get(fieldToCompare).replaceAll("[^0-9].*$", ""));
					int second = Integer.parseInt(arg1.get(fieldToCompare).replaceAll("[^0-9].*$", ""));
					return (second - first);
				} else {
					throw new Exception("INCOMPATIBLE FIELD TYPE MODE");
				}
			} else {
				throw new Exception("INCOMPATIBLE SORT MODE");
			}
		} catch (Exception e) {
			if (mode == MODE_ASC) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.MIN_VALUE;
			}
		}
	}

	public static void main(String[] args) {
		Document[] result = new Document[12];
		for (int i = 0; i < 10; i++) {
			Document doc = new Document();
			Random generator = new Random();
			int tot = generator.nextInt(100) + 1;
			doc.add(new Field("tot", Integer.toString(tot), LuceneFactory.getStore("no"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			result[i] = doc;
		}
		Document doc = new Document();
		doc.add(new Field("tot", "1/2", LuceneFactory.getStore("no"), LuceneFactory.getIndexType("NOT_ANALYZED")));
		result[10] = doc;
		  doc = new Document();
		doc.add(new Field("tot", "1/3", LuceneFactory.getStore("no"), LuceneFactory.getIndexType("NOT_ANALYZED")));
		result[11] = doc;
		System.out.println("PRIMA");
		for (int i = 0; i < result.length; i++) {
			doc = result[i];
			if (doc != null)
				System.out.println(doc.get("tot"));
		}
		Arrays.sort(result, new LuceneFieldDocComparator("tot", LuceneFieldDocComparator.MODE_DESC, LuceneFieldDocComparator.TYPE_INTEGER));
		System.out.println("DOPO");
		for (int i = 0; i < result.length; i++) {
			doc = result[i];
			if (doc != null)
				System.out.println(doc.get("tot"));
		}
	}
}
