package com.openDams.index.searchers;

import java.util.ArrayList;

public class Vocabulary {
	private String first_term;
	private String last_term;
	private String termForPrevious;
	ArrayList<VocTerm> terms;
	private boolean firstPage=true;
	private boolean lastPage=false;
	public Vocabulary(){}
	public String getFirst_term() {
		return first_term;
	}
	public String getLast_term() {
		return last_term;
	}
	public ArrayList<VocTerm> getTerms() {
		return terms;
	}
	public void setFirst_term(String firstTerm) {
		first_term = firstTerm;
	}
	public void setLast_term(String lastTerm) {
		last_term = lastTerm;
	}
	public void setTerms(ArrayList<VocTerm> terms) {
		this.terms = terms;
	}
	public boolean isFirstPage() {
		return firstPage;
	}
	public boolean isLastPage() {
		return lastPage;
	}
	public void setFirstPage(boolean firstPage) {
		this.firstPage = firstPage;
	}
	public void setLastPage(boolean lastPage) {
		this.lastPage = lastPage;
	}
	public String getTermForPrevious() {
		return termForPrevious;
	}
	public void setTermForPrevious(String termForPrevious) {
		this.termForPrevious = termForPrevious;
	}
	
}
