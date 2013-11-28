/*
 * Creato il 28-apr-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package com.openDams.title.configuration;

import java.util.ArrayList;

public class Title extends com.openDams.configuration.Element{
	
	private String name = null;
	private String text = null;
	private String separator = null;
	private String multiple_path_separator = null;
	private String multiple_node_separator = null;
	private String style = null;
	private String html_container = null;
	private String classUtil;
	private String method;
	private String find;
	private String replace;
	private String id_relation;
	private String id_archive;
	private String import_values;
	private String type;
	
	public Title() {
	}
	public void addElement(Object object) {
		super.addElement(object);
	}
	public ArrayList<com.openDams.configuration.Element> getElemets() {
		return super.getElemets();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	public String getMultiple_path_separator() {
		return multiple_path_separator;
	}
	public void setMultiple_path_separator(String multiplePathSeparator) {
		multiple_path_separator = multiplePathSeparator;
	}
	public String getMultiple_node_separator() {
		return multiple_node_separator;
	}
	public void setMultiple_node_separator(String multipleNodeSeparator) {
		multiple_node_separator = multipleNodeSeparator;
	}
	public void setHtml_container(String htmlContainer) {
		html_container = htmlContainer;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getHtml_container() {
		return html_container;
	}
	public void setClassUtil(String classUtil) {
		this.classUtil = classUtil;
	}
	public String getClassUtil() {
		return classUtil;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getMethod() {
		return method;
	}
	public String getFind() {
		return find;
	}
	public String getReplace() {
		return replace;
	}
	public void setFind(String find) {
		this.find = find;
	}
	public void setReplace(String replace) {
		this.replace = replace;
	}
	public String getId_relation() {
		return id_relation;
	}
	public void setId_relation(String id_relation) {
		this.id_relation = id_relation;
	}
	public String getImport_values() {
		return import_values;
	}
	public void setImport_values(String import_values) {
		this.import_values = import_values;
	}
	public String getId_archive() {
		return id_archive;
	}
	public void setId_archive(String id_archive) {
		this.id_archive = id_archive;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
