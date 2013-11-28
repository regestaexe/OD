package com.openDams.search.configuration;

/**
 * @author sandro de leo
 *
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class Element extends com.openDams.configuration.Element{
	
	private String name;
	private String archives;
	private String type;
	private String text;
	private String cdata_section;
	private String pages;
	
	public Element() {
	}


	public String getName() {
		return name;
	}


	public String getArchives() {
		return archives;
	}


	public String getType() {
		return type;
	}


	public String getText() {
		return text;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setArchives(String archives) {
		this.archives = archives;
	}


	public void setType(String type) {
		this.type = type;
	}


	public void setText(String text) {
		this.text = text;
	}


	public String getCdata_section() {
		return cdata_section;
	}


	public void setCdata_section(String cdataSection) {
		cdata_section = cdataSection;
	}


	public String getPages() {
		return pages;
	}


	public void setPages(String pages) {
		this.pages = pages;
	}


}
