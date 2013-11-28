package com.openDams.index.analyzers;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public final class IT_Analyzer extends Analyzer {

	public static final String IT_STOP_WORDS[] = { "a", "ad", "agli", "ai", "al", "all", "alla", "alle", "allo", "ce", "che", "chi", "ci", "col", "con", "cui", "d", "da", "dagli", "dai", "dal", "dall", "dalla", "dalle", "de", "degli", "dei", "del", "dell", "della", "delle", "dello", "di", "e",
			"ed", "gli", "i", "il", "in", "IN", "l", "la", "le", "li", "lo", "negli", "nei", "nel", "nell", "nella", "nelle", "nello", "nì", "o", "od", "per", "quel", "quell", "quella", "quelle", "quelli", "quello", "questa", "queste", "questi", "questo", "se", "si", "su", "sua", "sue", "sugli",
			"sui", "sul", "sull", "sulla", "sulle", "sullo", "suo", "ti", "un", "una", "uno", "ve", "vi" };

	@SuppressWarnings("unchecked")
	private Set stoptable;

	@SuppressWarnings({ "unused", "unchecked" })
	private Set excltable;

	@SuppressWarnings("unchecked")
	public IT_Analyzer() {
		stoptable = new HashSet();
		excltable = new HashSet();
		stoptable = StopFilter.makeStopSet(IT_STOP_WORDS);
	}
	@SuppressWarnings("unchecked")
	public IT_Analyzer(String stopwords[]) {
		stoptable = new HashSet();
		excltable = new HashSet();
		stoptable = StopFilter.makeStopSet(stopwords);
	}
	@SuppressWarnings("unchecked")
	public IT_Analyzer(Hashtable stopwords) {
		stoptable = new HashSet();
		excltable = new HashSet();
		stoptable = new HashSet(stopwords.keySet());
	}
	@SuppressWarnings("unchecked")
	public IT_Analyzer(File stopwords) throws IOException {
		stoptable = new HashSet();
		excltable = new HashSet();
		stoptable = WordlistLoader.getWordSet(stopwords);
	}

	public void setStemExclusionTable(String exclusionlist[]) {
		excltable = StopFilter.makeStopSet(exclusionlist);
	}
	@SuppressWarnings("unchecked")
	public void setStemExclusionTable(Hashtable exclusionlist) {
		excltable = new HashSet(exclusionlist.keySet());
	}

	public void setStemExclusionTable(File exclusionlist) throws IOException {
		excltable = WordlistLoader.getWordSet(exclusionlist);
	}

	public final TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream result = new StandardTokenizer(Version.LUCENE_CURRENT,reader);
		result = new SingleQuoteFilter(result);
		result = new StandardFilter(result);
		result = new StopFilter(true,result, stoptable);
		result = new LowerCaseFilter(result);
        result = new ASCIIFoldingFilter(result);
		return result;
	}

}
