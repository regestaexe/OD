package com.openDams.services;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.criterion.DetachedCriteria;

import com.openDams.bean.Records;
import com.openDams.dao.DBManager;
import com.openDams.dao.DBQuery;

public class OpenDamsService implements DBService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DBManager dbManager;

	public List<?> getList(Class<?> genericClass) throws HibernateException {
		return dbManager.getList(genericClass);
	}

	public List<?> getPagedList(Class<?> genericClass, int start, int lenght) throws HibernateException {
		return dbManager.getPagedList(genericClass, start, lenght);
	}

	public List<?> getList(DetachedCriteria criteria) throws HibernateException {
		return dbManager.getList(criteria);
	}

	public List<?> getListFromSQL(Class<?> genericClass, String sql) throws HibernateException {
		return dbManager.getListFromSQL(genericClass, sql);
	}

	public List<?> getPagedList(DetachedCriteria criteria, int start, int lenght) throws HibernateException {
		return dbManager.getPagedList(criteria, start, lenght);
	}

	public List<?> getPagedListFromSQL(Class<?> genericClass, String sql, int start, int lenght) throws HibernateException {
		return dbManager.getPagedListFromSQL(genericClass, sql, start, lenght);
	}

	public void add(Object genericObj) throws HibernateException {
		dbManager.add(genericObj);
	}

	public Object getObject(Class<?> genericClass, Object id) throws HibernateException {
		Object object = null;
		try {
			object = dbManager.getObject(genericClass, id);
		} catch (HibernateException e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>intercettataccccccccccc!!!!!!!!!!!!!!!!<<<<<<<<<<<<<<<");
			throw e;
		}
		return object;
	}

	public Records getRecordsByAbout(String about) throws HibernateException {
		Records record = null;
		try {
			record = ((List<Records>) getListFromSQL(Records.class, "SELECT * FROM records WHERE xml_id='" + about + "' LIMIT 1;")).get(0);
		} catch (Exception e) {

			throw new HibernateException("about " + about + " not found in records table");
		}
		return record;
	}

	public void update(Object genericObj) throws HibernateException {
		dbManager.update(genericObj);
	}

	public void addAll(Set<?> genericObjects) throws HibernateException {
		dbManager.addAll(genericObjects);
	}

	public void remove(Object genericObj) throws HibernateException {
		dbManager.remove(genericObj);

	}

	public void removeAll(Set<?> genericObjects) throws HibernateException {
		dbManager.removeAll(genericObjects);

	}

	public void updateAll(Set<?> genericObjects) throws HibernateException {
		dbManager.updateAll(genericObjects);

	}

	public void executeAll(DBQuery... dBQuerys) throws HibernateException {
		dbManager.executeAll(dBQuerys);

	}

	public void executeAll(ArrayList<DBQuery> dBQuerys) throws HibernateException {
		dbManager.executeAll(dBQuerys);

	}

	public void setDbManager(DBManager dbManager) {
		this.dbManager = dbManager;
	}

	public BigInteger getCountFromSQL(String sql) throws HibernateException {
		return dbManager.getCountFromSQL(sql);
	}

	@Override
	public int executeUpdate(String sql) throws HibernateException {
		return dbManager.executeUpdate(sql);
	}
}
