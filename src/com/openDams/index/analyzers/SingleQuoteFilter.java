package com.openDams.index.analyzers;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

public class SingleQuoteFilter extends TokenFilter{
	
	public SingleQuoteFilter(TokenStream in) {
	    super(in);
	    termAtt = (TermAttribute) addAttribute(TermAttribute.class);
	  }

	  private TermAttribute termAtt;
	  
	  /** Returns the next token in the stream, or null at EOS.
	   * <p>Removes <tt>'</tt> in the words.
	   * <p>Removes single quote from token.
	   */
	  public final boolean incrementToken() throws java.io.IOException {
	    if (!input.incrementToken()) {
	      return false;
	    }
	    String term = termAtt.term();
	    if (term.indexOf("'")!=-1) {
		      termAtt.setTermBuffer(term.substring(0,term.indexOf("'")));
		      TermAttribute termAtt2 = (TermAttribute) addAttribute(TermAttribute.class);
		      termAtt2.setTermBuffer(term.substring(term.indexOf("'")+1,term.length()));
	    }
	    return true;
	  }

}
