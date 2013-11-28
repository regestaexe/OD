package com.openDams.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.AssertionFailure;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.openDams.bean.Records;
import com.openDams.bean.Relations;
@Repository("dbManager")
@Transactional
public class DBManager {

	private HibernateTemplate hibernateTemplate;
	private Map<Integer, HierarchyCountQuery> hierarchyQuerys;

	@SuppressWarnings("unchecked")
	public List<?> getList(final Class<?> genericClass) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Criteria crit = session.createCriteria(genericClass);
					List<?> list = crit.list();
					return list;
				}
			};
			return (List<?>) addTotChildren(hibernateTemplate.executeFind(callback));
		} catch (HibernateException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<?> getPagedList(final Class<?> genericClass, final int start, final int lenght) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Criteria crit = session.createCriteria(genericClass);
					crit.setFirstResult(start);
					crit.setMaxResults(lenght);
					List<?> list = crit.list();
					return list;
				}
			};
			return (List<?>) addTotChildren(hibernateTemplate.executeFind(callback));
		} catch (HibernateException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<?> getList(final DetachedCriteria criteria) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					List<?> list = hibernateTemplate.findByCriteria(criteria);
					return list;
				}
			};
			return (List<?>) addTotChildren(hibernateTemplate.executeFind(callback));
		} catch (HibernateException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public Session getSession() throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					return session;
				}
			};
			return (Session) hibernateTemplate.execute(callback);
		} catch (HibernateException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<?> getPagedList(final DetachedCriteria criteria, final int start, final int lenght) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Criteria pagingCriteria = criteria.getExecutableCriteria(session);
					pagingCriteria.setFirstResult(start);
					pagingCriteria.setMaxResults(lenght);
					List<?> list = pagingCriteria.list();
					return list;
				}
			};
			return (List<?>) addTotChildren(hibernateTemplate.executeFind(callback));
		} catch (HibernateException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public Object getObject(final Class<?> genericClass, final Object id) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					try {
						Object object = session.load(genericClass, (Serializable) id);
						return object;
					} catch (Exception e) {
						throw new HibernateException(e);
					} catch (Error e) {
						throw e;
					}
				}
			};
			return addTotChildren(hibernateTemplate.execute(callback));
		} catch (Exception e) {
			throw new HibernateException(e);
		} catch (Error e) {
			throw e;
		}
	}

	@Transactional(readOnly = false)
	public void update(final Object genericObj) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					//Transaction tx = null;
					try {
						//tx = session.beginTransaction();
						session.merge(genericObj);
						//tx.commit();
						session.flush();
						session.close();
					} catch(AssertionFailure e) {
						System.out.println("AssertionFailure INTERCETTATA E NON RILANCIATA in update");
					} catch(ConstraintViolationException e){
						if(genericObj instanceof Relations){
							System.out.println("ConstraintViolationException INTERCETTATA E NON RILANCIATA in update");
						}else{
							throw e;
						}
					}catch (HibernateException e) {
						//e.printStackTrace();
						//if (tx != null && tx.isActive()) {
							//tx.rollback();
						//}
						throw e;
					}
					try {
						session.flush();
					} catch (Exception e) {
						System.err.println("ERRRRRRROR FLUSHING " + e.getMessage());
					}

					return null;
				}

			};
			hibernateTemplate.execute(callback);
		} catch(AssertionFailure e) {
			System.out.println("AssertionFailure2 INTERCETTATA E NON RILANCIATA");
		}catch (HibernateException e) {
			//e.printStackTrace();
			throw e;
		}
	}

	@Transactional(readOnly = false)
	public void add(final Object genericObj) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					//Transaction tx = null;
					try {
						//tx = session.beginTransaction();
						session.saveOrUpdate(genericObj);
						//tx.commit();
						session.flush();
						session.close();
					} catch(AssertionFailure e) {
						System.out.println("AssertionFailure INTERCETTATA E NON RILANCIATA");
					} catch (HibernateException e) {
						/*if (tx != null && tx.isActive()) {
							tx.rollback();
						}*/
						throw e;
					}
					return null;
				}
			};
			hibernateTemplate.execute(callback);
			System.out.println("OGGETTO AGGIUNTO CORRETTAMENTE!!!!!!!");
		} catch(AssertionFailure e) {
			System.out.println("AssertionFailure2 INTERCETTATA E NON RILANCIATA in add");
		}catch(ConstraintViolationException e){
			if(genericObj instanceof Relations){
				System.out.println("ConstraintViolationException INTERCETTATA E NON RILANCIATA in add");
			}else{
				throw e;
			}
		}catch (HibernateException e) {
			throw e;
		}
	}

	@Transactional(readOnly = false)
	public void remove(final Object genericObj) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					//Transaction tx = null;
					try {
						//tx = session.beginTransaction();
						session.delete(genericObj);
						//tx.commit();
						session.flush();
						session.close();
					} catch(AssertionFailure e) {
						System.out.println("AssertionFailure INTERCETTATA E NON RILANCIATA");
					} catch (HibernateException e) {
						//e.printStackTrace();
						/*if (tx != null && tx.isActive()) {
							tx.rollback();
						}*/
						throw e;
					}
					return null;
				}
			};
			hibernateTemplate.execute(callback);
		}catch(AssertionFailure e) {
			System.out.println("AssertionFailure2 INTERCETTATA E NON RILANCIATA");
		}
		catch (HibernateException e) {
			//e.printStackTrace();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public void addAll(final Set<?> genericObjects) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Transaction tx = null;
					try {
						tx = session.beginTransaction();
						Iterator<?> iterator = genericObjects.iterator();
						while (iterator.hasNext()) {
							// session.saveOrUpdate(iterator.next());
							session.merge(iterator.next());
						}
						tx.commit();
						session.flush();
					} catch (HibernateException e) {
						if (tx != null && tx.isActive()) {
							tx.rollback();
						}
						throw e;
					}
					return null;
				}
			};
			hibernateTemplate.execute(callback);
		} catch (HibernateException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public void removeAll(final Set<?> genericObjects) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Transaction tx = null;
					try {
						tx = session.beginTransaction();
						Iterator<?> iterator = genericObjects.iterator();
						while (iterator.hasNext()) {
							session.delete(iterator.next());
						}
						tx.commit();
						session.flush();
					} catch (HibernateException e) {
						if (tx != null && tx.isActive()) {
							tx.rollback();
						}
						throw e;
					}
					return null;
				}
			};
			hibernateTemplate.execute(callback);
		} catch (HibernateException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public void updateAll(final Set<?> genericObjects) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Transaction tx = null;
					try {
						tx = session.beginTransaction();
						Iterator<?> iterator = genericObjects.iterator();
						while (iterator.hasNext()) {
							session.merge(iterator.next());
						}
						tx.commit();
						session.flush();
					} catch (HibernateException e) {
						if (tx != null && tx.isActive()) {
							tx.rollback();
						}
						throw e;
					}
					return null;
				}
			};
			hibernateTemplate.execute(callback);
		} catch (HibernateException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public void executeAll(final DBQuery... dBQuerys) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Transaction tx = null;
					try {
						tx = session.beginTransaction();
						for (int i = 0; i < dBQuerys.length; i++) {
							DBQuery dbQuery = dBQuerys[i];
							switch (dbQuery.operation) {
							case 1:
								if (dbQuery.genericObject instanceof Set) {
									Iterator<?> iterator = ((Set<?>) dbQuery.genericObject).iterator();
									while (iterator.hasNext()) {
										session.saveOrUpdate(iterator.next());
									}
								} else
									session.saveOrUpdate(dbQuery.genericObject);
								break;
							case 2:
								if (dbQuery.genericObject instanceof Set) {
									Iterator<?> iterator = ((Set<?>) dbQuery.genericObject).iterator();
									while (iterator.hasNext()) {
										session.merge(iterator.next());
									}
								} else
									session.merge(dbQuery.genericObject);
								break;
							case 3:
								if (dbQuery.genericObject instanceof Set) {
									Iterator<?> iterator = ((Set<?>) dbQuery.genericObject).iterator();
									while (iterator.hasNext()) {
										session.delete(iterator.next());
									}
								} else
									session.delete(dbQuery.genericObject);
								break;
							default:
								break;
							}

						}
						tx.commit();
						session.flush();
					} catch (HibernateException e) {
						if (tx != null && tx.isActive()) {
							tx.rollback();
						}
						throw e;
					}
					return null;
				}
			};
			hibernateTemplate.execute(callback);
		} catch (HibernateException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public void executeAll(final ArrayList<DBQuery> dBQuerys) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Transaction tx = null;
					try {
						tx = session.beginTransaction();
						for (int i = 0; i < dBQuerys.size(); i++) {
							DBQuery dbQuery = dBQuerys.get(i);
							switch (dbQuery.operation) {
							case 1:
								if (dbQuery.genericObject instanceof Set) {
									Iterator<?> iterator = ((Set<?>) dbQuery.genericObject).iterator();
									while (iterator.hasNext()) {
										session.saveOrUpdate(iterator.next());
									}
								} else
									session.saveOrUpdate(dbQuery.genericObject);
								break;
							case 2:
								if (dbQuery.genericObject instanceof Set) {
									Iterator<?> iterator = ((Set<?>) dbQuery.genericObject).iterator();
									while (iterator.hasNext()) {
										session.merge(iterator.next());
									}
								} else
									session.merge(dbQuery.genericObject);
								break;
							case 3:
								if (dbQuery.genericObject instanceof Set) {
									Iterator<?> iterator = ((Set<?>) dbQuery.genericObject).iterator();
									while (iterator.hasNext()) {
										session.delete(iterator.next());
									}
								} else
									session.delete(dbQuery.genericObject);
								break;
							default:
								break;
							}

						}
						tx.commit();
						session.flush();
					} catch (HibernateException e) {
						if (tx != null && tx.isActive()) {
							tx.rollback();
						}
						throw e;
					}
					return null;
				}
			};
			hibernateTemplate.execute(callback);
		} catch (HibernateException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<?> getListFromSQL(final Class<?> genericClass, final String query) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					SQLQuery sQLQuery = session.createSQLQuery(query);
					sQLQuery.addEntity(genericClass);
					return sQLQuery.list();
				}
			};
			return (List<?>) addTotChildren(hibernateTemplate.executeFind(callback));
		} catch (HibernateException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public int executeUpdate(final String query) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					SQLQuery sQLQuery = session.createSQLQuery(query);
					sQLQuery.executeUpdate();
					return sQLQuery.executeUpdate();
				}
			};
			return hibernateTemplate.execute(callback);
		} catch (HibernateException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public BigInteger getCountFromSQL(final String query) throws HibernateException {
		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				try {
					SQLQuery sQLQuery = session.createSQLQuery(query);
					return sQLQuery.uniqueResult();
				} catch (HibernateException e) {
					throw e;
				}
			}
		};
		try {
			return (BigInteger) hibernateTemplate.execute(callback);
		} catch (Error e) {
			throw e;
		} catch (Exception e) {
			throw new HibernateException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<?> getPagedListFromSQL(final Class<?> genericClass, final String query, final int start, final int lenght) throws HibernateException {
		try {
			HibernateCallback callback = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					SQLQuery sQLQuery = session.createSQLQuery(query);
					sQLQuery.addEntity(genericClass);
					sQLQuery.setFirstResult(start);
					sQLQuery.setMaxResults(lenght);
					return sQLQuery.list();
				}
			};
			return (List<?>) addTotChildren(hibernateTemplate.executeFind(callback));
		} catch (HibernateException e) {
			throw e;
		}
	}

	private Object addTotChildren(Object obj) {
		try {
			if (obj == null)
				return obj;
			if (obj instanceof List<?>) {
				List<?> list = (List<?>) obj;
				if (list != null) {
					if (list.get(0) instanceof Records) {
						if (list.size() > 0) {
							ArrayList<Records> a = new ArrayList<Records>();
							for (int i = 0; i < list.size(); i++) {
								Records records = (Records) list.get(i);
								if (hierarchyQuerys.get(records.getArchives().getIdArchive()) != null) {
									String query;
									try {
										query = hierarchyQuerys.get(records.getArchives().getIdArchive()).buildQuery(records);
									} catch (Exception e) {
										throw new HibernateException(e.getMessage());
									}
									try {
										records.setTotChildren(getCountFromSQL(query).intValue());
									} catch (Exception e) {
									}
								}
								a.add(records);
							}
							return Arrays.asList(a.toArray());
						} else {
							return obj;
						}

					} else {
						return obj;
					}
				} else {
					return obj;
				}
			} else if (obj instanceof Records) {
				Records records = (Records) obj;
				if (hierarchyQuerys.get(records.getArchives().getIdArchive()) != null) {
					String query;
					try {
						query = hierarchyQuerys.get(records.getArchives().getIdArchive()).buildQuery(records);
					} catch (Exception e) {
						throw new HibernateException(e.getMessage());
					}
					try {
						records.setTotChildren(getCountFromSQL(query).intValue());
					} catch (Exception e) {
					}
				}
				return records;
			} else {
				return obj;
			}
		} catch (Exception e) {
			return obj;
		}
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public Map<Integer, HierarchyCountQuery> getHierarchyQuerys() {
		return hierarchyQuerys;
	}

	public void setHierarchyQuerys(Map<Integer, HierarchyCountQuery> hierarchyQuerys) {
		this.hierarchyQuerys = hierarchyQuerys;
	}

}
