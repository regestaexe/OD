package com.openDams.db.event;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.hibernate.cfg.Configuration;

import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.relations.managing.RelationsManager;
import com.openDams.title.configuration.TitleManager;


public class DBEventListener{

	private static final long serialVersionUID = 8628132333422887069L;
	protected DataSource	dataSource = null;
	protected HashMap<Integer, ArrayList<Object>> elements_map = null;
	protected IndexConfiguration indexConfiguration = null;
	protected RelationsManager relationsManager = null;
	protected TitleManager titleManager;
	public TitleManager getTitleManager() {
		return titleManager;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	public void initialize(Configuration configuration) {
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	public IndexConfiguration getIndexConfiguration() {
		return indexConfiguration;
	}
	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}
	public HashMap<Integer, ArrayList<Object>> getElements_map() {
		return elements_map;
	}
	public void setElements_map(HashMap<Integer, ArrayList<Object>> elementsMap) {
		elements_map = elementsMap;
	}
	public void setRelationsManager(RelationsManager relationsManager) {
		this.relationsManager = relationsManager;
	}
}
