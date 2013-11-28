package com.openDams.endpoint.managing;

public class Tuple{
	private String predicate;
	private String object;
	private int valueType;
	public Tuple(String predicate,String object,int valueType){
		this.predicate = predicate;
		this.object = object;
		this.valueType = valueType;
	}
	public String getPredicate() {
		return predicate;
	}
	public String getObject() {
		return object;
	}
	public int getValueType() {
		return valueType;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public void setObject(String subject) {
		this.object = subject;
	}
	public void setValueType(int valueType) {
		this.valueType = valueType;
	}
}