package com.openDams.endpoint.managing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeMap;

public class EndPointManagerFactory {
	private TreeMap<String, EndPointManager> endPointMap;
	private ArrayList<String> endPointList;
	public List<String> getEndPointList(){
		if(endPointList==null){
			endPointList = new ArrayList<String>();
			NavigableSet<String> keys = endPointMap.descendingKeySet();
			Iterator<String> iterator = keys.descendingIterator();
			while (iterator.hasNext()) {
				endPointList.add(iterator.next());
			}
		}
		return endPointList;
	}	
	public List<String> getAllowedEndPointList(int idArchive){		
	    ArrayList<String> allowedEndPointList = new ArrayList<String>();
		NavigableSet<String> keys = endPointMap.descendingKeySet();
		Iterator<String> iterator = keys.descendingIterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if(endPointMap.get(key).isAllowedArchive(idArchive)){
				allowedEndPointList.add(key);
			}
		}
		return allowedEndPointList;
	}
	public TreeMap<String, EndPointManager> getEndPointMap() {
		return endPointMap;
	}

	public void setEndPointMap(TreeMap<String, EndPointManager> endPointMap) {
		this.endPointMap = endPointMap;
	}
}
