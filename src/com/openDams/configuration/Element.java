package com.openDams.configuration;

import java.util.ArrayList;

public class Element {
	private ArrayList<Element> elements = null;
	public Element() {
	}
	public ArrayList<Element> getElemets() {
		return elements;
	}

	/**
	 * @param list
	 */
	public void addElement(Object object) {
		if(elements==null)
			elements=new ArrayList<Element>();
		elements.add((Element)object);
	}
}
