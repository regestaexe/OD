package com.openDams.index.factory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.search.Query;

import com.openDams.index.analyzers.IT_Analyzer;
public class LuceneFactory {
     public static Store getStore(String storeType){
    	 if(storeType.toLowerCase().equals("yes")){
    		 return Field.Store.YES;
    	 }else{
    		 return Field.Store.NO; 
    	 }
    	 
     }
     public static Index getIndexType(String analyzingMode){
    	 if(analyzingMode.toLowerCase().equals("one") || analyzingMode.toUpperCase().equals("NOT_ANALYZED")){
    		 return Field.Index.NOT_ANALYZED;
    	 }else if(analyzingMode.toLowerCase().equals("double") || analyzingMode.toUpperCase().equals("ANALYZED")){
    		 return Field.Index.ANALYZED; 
    	 }else if(analyzingMode.toUpperCase().equals("ANALYZED_NO_NORMS")){
    		 return Field.Index.ANALYZED_NO_NORMS; 
    	 }else if(analyzingMode.toUpperCase().equals("NOT_ANALYZED_NO_NORMS")){
    		 return Field.Index.NOT_ANALYZED_NO_NORMS; 
    	 }else{
    		 return Field.Index.NOT_ANALYZED;
    	 }  	 
     }
     public static Analyzer getAnalyzer(String analyzer) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
    	 Class<?> c = Class.forName(analyzer);
    	 return (Analyzer)c.newInstance();
     }
     public static Analyzer getSearchAnalyzer(String analyzingMode) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
    	 if(analyzingMode.toLowerCase().equals("one") || analyzingMode.toUpperCase().equals("NOT_ANALYZED")){
    		 return new KeywordAnalyzer();
    	 }else if(analyzingMode.toLowerCase().equals("double") || analyzingMode.toUpperCase().equals("ANALYZED")){
    		 return new IT_Analyzer(); 
    	 }else{
    		 return new IT_Analyzer(); 
    	 }   		 
     }
     public static Query getQuery(String query) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
    	 Class<?> c = Class.forName(query);
    	 return (Query)c.newInstance();
     }
}
