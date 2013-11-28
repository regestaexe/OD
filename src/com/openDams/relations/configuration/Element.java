package com.openDams.relations.configuration;

/**
 * @author sandro de leo
 *
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class Element extends com.openDams.configuration.Element{
	
	private String name;
	private String id_relation;
	private String get_from;
	private String insert_to;
	private String xpath_separator=",";
	private String text;
	
	public Element() {
	}

	public String getName() {
		return name;
	}

	public String getId_relation() {
		return id_relation;
	}

	public String getGet_from() {
		return get_from;
	}

	public String getInsert_to() {
		return insert_to;
	}

	public String getText() {
		return text;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId_relation(String idRelation) {
		id_relation = idRelation;
	}

	public void setGet_from(String getFrom) {
		get_from = getFrom;
	}

	public void setInsert_to(String insertTo) {
		insert_to = insertTo;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getXpath_separator() {
		return xpath_separator;
	}

	public void setXpath_separator(String xpathSeparator) {
		xpath_separator = xpathSeparator;
	}

	@Override
	public String toString() {
		return "Element [get_from=" + get_from + ", id_relation=" + id_relation
				+ ", insert_to=" + insert_to + ", name=" + name + ", text="
				+ text + "]";
	}

}
