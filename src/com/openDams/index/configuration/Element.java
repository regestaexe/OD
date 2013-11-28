/*
 * Creato il 28-apr-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package com.openDams.index.configuration;

import java.util.ArrayList;




/**
 * @author sandro
 *
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class Element extends com.openDams.configuration.Element{
	
	private String name = null;
	private String search_alias;
	private String lucene_store_type;
	private String key_style;
	private String text;
	private String prefix;
	private String db_sort_field;
	private String if_empty_default;
	private String classUtil;
	private String method;
	private String type;
	private String boost;
	private String id_relation;
	private String id_archive;
	
	
	
	public Element() {
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSearch_alias() {
		return search_alias;
	}
	public void setSearch_alias(String searchAlias) {
		search_alias = searchAlias;
	}
	public String getLucene_store_type() {
		return lucene_store_type;
	}
	public void setLucene_store_type(String luceneStoreType) {
		lucene_store_type = luceneStoreType;
	}
	public String getKey_style() {
		return key_style;
	}
	public void setKey_style(String keyStyle) {
		key_style = keyStyle;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public void addElement(Object object) {
		super.addElement(object);
	}
	public ArrayList<com.openDams.configuration.Element> getElemets() {
		return super.getElemets();
	}
	public String getDb_sort_field() {
		return db_sort_field;
	}
	public void setDb_sort_field(String dbSortField) {
		db_sort_field = dbSortField;
	}
	public String getClassUtil() {
		return classUtil;
	}
	public void setClassUtil(String classUtil) {
		this.classUtil = classUtil;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIf_empty_default() {
		return if_empty_default;
	}
	public void setIf_empty_default(String ifEmptyDefault) {
		if_empty_default = ifEmptyDefault;
	}
	public String getBoost() {
		return boost;
	}
	public void setBoost(String boost) {
		this.boost = boost;
	}
	public String getId_relation() {
		return id_relation;
	}
	public void setId_relation(String id_relation) {
		this.id_relation = id_relation;
	}
	public String getId_archive() {
		return id_archive;
	}
	public void setId_archive(String id_archive) {
		this.id_archive = id_archive;
	}
}
