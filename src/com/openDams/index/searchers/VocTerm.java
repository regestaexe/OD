package com.openDams.index.searchers;

import java.util.ArrayList;

import org.apache.lucene.document.Document;

public class VocTerm {
	public String term;
	public int frequence;
	private ArrayList<Document> docs;
	public VocTerm(){}
	public VocTerm(String term,int frequence){
		this.term = term;
		this.frequence = frequence;
	}
	public String getTerm() {
		return term;
	}
	
	public int getFrequence() {
		return frequence;
	}
	public ArrayList<Document> getDocs() {
		return docs;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public void setFrequence(int frequence) {
		this.frequence = frequence;
	}
	public void setDocs(ArrayList<Document> docs) {
		this.docs = docs;
	}
	public void addDoc(Document document){
		if(docs==null){
			docs = new ArrayList<Document>();
		}
		docs.add(document);
	}
	@Override
	public String toString() {
		return "VocTerm [docs=" + docs + ", frequence=" + frequence + ", term="
				+ term + "]";
	}
}
