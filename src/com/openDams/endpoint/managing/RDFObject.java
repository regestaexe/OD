package com.openDams.endpoint.managing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class RDFObject {
	public static final int TYPE_LITERAL = 1;
	public static final int TYPE_URI = 2;
	public static final int TYPE_BLANK_NODE = 3;
	public static final int OBJECT_TYPE_BLANK_NODE = 4;
	public static final int OBJECT_TYPE_NODE = 5;
	private int objectType;
	private Map<String, String> namespaceTable;
	private Map<String, RDFObject> blankNodes;
	private String subject;
	private String predicate;
	private String object;
	private ArrayList<Tuple> values;
	public RDFObject(){
		
	}
	public int getObjectType() {
		return objectType;
	}
	public String getSubject() {
		return subject;
	}
	public String getPredicate() {
		return predicate;
	}
	public String getObject() {
		return object;
	}
	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public void addToBlankNodes(String key,RDFObject turtleObject) {
		if(blankNodes==null)
			blankNodes = new  LinkedHashMap<String, RDFObject>();
		blankNodes.put(key,turtleObject);
	}
	public RDFObject findBlankNode(String key) {
		if(blankNodes==null){
			return null;
		}else{
			return blankNodes.get(key);
		}		
	}
	public ArrayList<Tuple> getValues() {
		return values;
	}
	public void setValues(ArrayList<Tuple> values) {
		this.values = values;
	}
	public void addToValues(Tuple tuple) {
		if(values==null){
		  values = new ArrayList<Tuple>();
		} 
		values.add(tuple);
	}
	public Map<String, RDFObject> getBlankNodes() {
		return blankNodes;
	}
	public Map<String, String> getNamespaceTable() {
		return namespaceTable;
	}
	public void setNamespaceTable(Map<String, String> namespaceTable) {
		this.namespaceTable = namespaceTable;
	}
}
