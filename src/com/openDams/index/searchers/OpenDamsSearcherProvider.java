package com.openDams.index.searchers;


public class OpenDamsSearcherProvider {
	private static Searcher searcher = null;

	public static Searcher getSearcher() {
		return searcher;
	}
	public void setSearcher(Searcher searcher) {
		OpenDamsSearcherProvider.searcher = searcher;
	}
}
