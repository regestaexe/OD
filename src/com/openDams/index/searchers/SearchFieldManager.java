package com.openDams.index.searchers;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;

import com.openDams.configuration.ConfigurationException;
import com.openDams.index.configuration.Element;
import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.index.factory.LuceneFactory;

public class SearchFieldManager {
	protected HashMap<Integer, ArrayList<Object>> elements_map = null;
	protected IndexConfiguration indexConfiguration = null;
	public SearchFieldManager() {
	}
	public Analyzer getSearchAnalizer(int idArchive,String field) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigurationException{
		if(elements_map==null){
			elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();
		}
		ArrayList<Object> elements = elements_map.get(idArchive);
		for(int i=0;i<elements.size();i++){
			Element element=(Element)elements.get(i);
			if(element.getName().equalsIgnoreCase(field)){
				return LuceneFactory.getSearchAnalyzer(element.getKey_style());
			}

		}
		return LuceneFactory.getSearchAnalyzer("double");
	}
	public String getKeyStyle(int idArchive,String field) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigurationException{
		if(elements_map==null){
			elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();
		}
		ArrayList<Object> elements = elements_map.get(idArchive);
		for(int i=0;i<elements.size();i++){
			Element element=(Element)elements.get(i);
			if(element.getName().equalsIgnoreCase(field)){
				return element.getKey_style();
			}

		}
		return "double";
	}
	public IndexConfiguration getIndexConfiguration() {
		return indexConfiguration;
	}
	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}
	
}
