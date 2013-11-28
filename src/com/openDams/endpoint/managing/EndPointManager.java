package com.openDams.endpoint.managing;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import com.openDams.endpoint.utility.ReflectClass;
import com.openDams.index.configuration.IndexConfiguration;
import com.regesta.framework.xml.XMLBuilder;

public abstract class EndPointManager implements EndPointManagerInterface {

	public final static int ACTION_ADD = 1;
	public final static int ACTION_UPDATE = 2;
	public final static int ACTION_DELETE = 3;

	public final static String ACTION_ADD_NAME = "ACTION_ADD";
	public final static String ACTION_UPDATE_NAME = "ACTION_UPDATE";
	public final static String ACTION_DELETE_NAME = "ACTION_DELETE";

	protected DataSource openDamsdataSource = null;
	protected Map<Object, List<String>> excludeXpathListArchiveMap;
	protected Map<Integer, Boolean> autoArchivePublishing;
	protected Map<Integer, Boolean> allowedArchivePublishing;
	protected Map<String, List<String>> archiveReplacer;

	protected String defaultDomain;
	protected String sparqlEndpointUri;
	protected IndexConfiguration indexConfiguration = null;
	protected int publishingPageSet = 100;
	protected DataSource dataSource = null;

	protected Map<Integer, Map<String, ReflectClass>> endPointClassUtils;

	public EndPointManager() {
	}

	protected abstract void manageRecord(EndpointRecordContainerList endpointRecordContainerList, boolean singleRecord, String action, boolean clear, int ref_id_endpoint_actio);

	protected abstract void clearEndpoint(int idArchive) throws RepositoryException;

	public void publishRecord(int idRecord, String user, String endPointManagerkey) throws Exception {
		String query = "select id_record,ref_id_archive,xml,xml_id from records where id_record=" + idRecord + " and (deleted<>1 OR deleted is NULL) LIMIT ?," + publishingPageSet + ";";
		publishRecords(query, null, 0, user, endPointManagerkey);
	}

	public void rePublishRecords(int idArchive, String user, String endPointManagerkey) throws Exception {
		int idEndpoint = findEndPoint(endPointManagerkey);
		String queryCount = "SELECT count(ref_id_record) AS tot FROM records r INNER JOIN endpoints_manager em  ON r.id_record=em.ref_id_record WHERE em.ref_id_endpoint_action <> 3 AND ref_id_endpoint=" + idEndpoint + " AND (r.deleted<>1 OR r.deleted is NULL) AND r.ref_id_archive=" + idArchive + ";";
		String query = "SELECT ref_id_record FROM records r INNER JOIN endpoints_manager em  ON r.id_record=em.ref_id_record WHERE em.ref_id_endpoint_action <> 3 AND em.ref_id_endpoint=" + idEndpoint + " AND (r.deleted<>1 OR r.deleted is NULL) AND r.ref_id_archive=" + idArchive + " LIMIT ?," + publishingPageSet + ";";
		String queryRecord = "SELECT id_record,ref_id_archive,xml,xml_id FROM records where id_record = ?";
		rePublishRecords(queryCount, query, queryRecord, idArchive, user, endPointManagerkey);
	}

	public void publishRecordsNotPublished(int idArchive, String user, String endPointManagerkey) throws Exception {
		String queryCount = "SELECT count(r.id_record) as tot FROM records r LEFT JOIN endpoints_manager em ON r.id_record=em.ref_id_record WHERE r.ref_id_archive=" + idArchive + " AND (em.ref_id_record IS NULL OR em.ref_id_endpoint_action=3) AND (deleted<>1 OR deleted is NULL);";
		String query = "SELECT r.id_record,r.ref_id_archive,r.xml,r.xml_id FROM records r LEFT JOIN endpoints_manager em ON r.id_record=em.ref_id_record where r.ref_id_archive=" + idArchive + " AND (em.ref_id_record IS NULL OR em.ref_id_endpoint_action=3) AND (deleted<>1 OR deleted is NULL) LIMIT ?," + publishingPageSet + ";";
		publishRecords(query, queryCount, idArchive, user, endPointManagerkey);
	}

	public void publishRecordsModified(int idArchive, String user, String endPointManagerkey) throws Exception {
		String queryCount = "SELECT count(ref_id_record) as tot FROM records r INNER JOIN endpoints_manager em ON r.id_record=em.ref_id_record WHERE em.ref_id_endpoint_action <> 3 AND r.modify_date > em.date AND (r.deleted<>1 OR r.deleted is NULL) AND r.ref_id_archive=" + idArchive + ";";
		String query = "SELECT ref_id_record,r.ref_id_archive,r.xml,r.xml_id FROM records r INNER JOIN endpoints_manager em ON r.id_record=em.ref_id_record WHERE em.ref_id_endpoint_action <> 3 AND r.modify_date > em.date AND (r.deleted<>1 OR r.deleted is NULL) AND r.ref_id_archive=" + idArchive + " LIMIT ?," + publishingPageSet + ";";
		String queryRecord = "SELECT id_record,ref_id_archive,xml,xml_id FROM records where id_record = ?";
		rePublishRecordsModified(queryCount, query, queryRecord, idArchive, user, endPointManagerkey);
	}

	public void publishErrorResults(int idArchive, String user, String endPointManagerkey) throws Exception {
		int idEndpoint = findEndPoint(endPointManagerkey);
		String queryCount = "SELECT count(ref_id_record) as tot FROM endpoints_manager WHERE result=0 AND ref_id_endpoint=" + idEndpoint + " AND ref_id_archive=" + idArchive + ";";
		String query = "SELECT ref_id_record FROM endpoints_manager WHERE result=0 AND ref_id_endpoint=" + idEndpoint + " AND ref_id_archive=" + idArchive + " LIMIT ?," + publishingPageSet + ";";
		String queryRecord = "SELECT id_record,ref_id_archive,xml,xml_id FROM records where id_record = ?";
		rePublishRecords(queryCount, query, queryRecord, idArchive, user, endPointManagerkey);
	}

	public void publishArchive(int idArchive, String user, String endPointManagerkey) throws Exception {
		String queryCount = "SELECT count(id_record) as tot FROM records where ref_id_archive=" + idArchive + " and (deleted<>1 OR deleted is NULL);";
		String query = "SELECT id_record,ref_id_archive,xml,xml_id FROM records where ref_id_archive=" + idArchive + " and (deleted<>1 OR deleted is NULL) LIMIT ?," + publishingPageSet + ";";
		publishRecords(query, queryCount, idArchive, user, endPointManagerkey);
	}

	public void removeEndPointObject(int idRecord, String user, String endPointManagerkey) {
		String query = "SELECT id_record,ref_id_archive,xml,xml_id FROM records where id_record=" + idRecord + " ;";
		removeRecord(query, 0, user, endPointManagerkey);
	}

	public void removeEndPointObjects(int idArchive, String user, String endPointManagerkey) throws Exception {
		int idEndpoint = findEndPoint(endPointManagerkey);
		String queryCount = "SELECT count(ref_id_record) as tot FROM endpoints_manager where ref_id_archive=" + idArchive + " and ref_id_endpoint=" + idEndpoint + " and not ref_id_endpoint_action=" + ACTION_DELETE + ";";
		String query = "SELECT ref_id_record FROM endpoints_manager  where ref_id_archive=" + idArchive + " and ref_id_endpoint=" + idEndpoint + " and not ref_id_endpoint_action=" + ACTION_DELETE + " LIMIT ?," + publishingPageSet + ";";
		String queryRecord = "SELECT id_record,ref_id_archive,xml,xml_id FROM records where id_record = ?";
		System.out.println("Sto cancellando " + queryCount + " records");
		removeRecords(queryCount, query, queryRecord, idArchive, user, endPointManagerkey);
	}

	private void publishRecords(String query, String queryCount, int idArchive, String user, String endPointManagerkey) throws Exception {
		Connection openDamsConnection = null;
		boolean singleRecord = true;
		if (idArchive != 0) {
			singleRecord = false;
		}
		try {
			openDamsConnection = openDamsdataSource.getConnection();
			int pages = 0;
			if (queryCount != null && !singleRecord) {
				Statement st = openDamsConnection.createStatement();
				System.out.println("EndPointManager.publishRecords() query: " + queryCount);
				ResultSet resultSet = st.executeQuery(queryCount);
				resultSet.next();
				int tot = resultSet.getInt("tot");
				resultSet.close();
				if (tot % publishingPageSet == 0) {
					pages = tot / publishingPageSet;
				} else {
					pages = (tot / publishingPageSet) + 1;
				}
			} else {
				pages = 1;
			}
			if (!singleRecord) {
				setArchiveOffline(openDamsConnection, idArchive);
			}
			clearEndpoint(idArchive);
			for (int x = 0; x < pages; x++) {
				PreparedStatement preparedStatement = openDamsConnection.prepareStatement(query);
				preparedStatement.setInt(1, (x * 100));
				System.out.println("EndPointManager.publishRecords() 	preparedStatement.setInt(1, (" + (x * 100) + ")");
				System.out.println("EndPointManager.publishRecords() query: " + query);
				ResultSet resultSet = preparedStatement.executeQuery();
				EndpointRecordContainerList endpointRecordContainerList = new EndpointRecordContainerList(user, endPointManagerkey);
				System.out.println("EndPointManager.publishRecords() collecting");
				int conta = 0;
				while (resultSet.next()) {
					if (conta % 10 == 0) {
						conta++;
						System.out.println("EndPointManager.publishRecords() " + conta);
					}
					int idRecord = 0;
					try {
						idRecord = resultSet.getInt("id_record");
						idArchive = resultSet.getInt("ref_id_archive");
						byte[] odsRdf = resultSet.getBytes("xml");

						String xml_id = resultSet.getString("xml_id");
						List<String> excludeXpathList = getExcludeXpathListArchiveMap().get(idArchive);
						if (excludeXpathList == null) {
							excludeXpathList = getExcludeXpathListArchiveMap().get("all");
						}
						XMLBuilder xmlBuilder = new XMLBuilder(new ByteArrayInputStream(odsRdf));
						if (excludeXpathList != null && excludeXpathList.size() > 0) {
							for (int i = 0; i < excludeXpathList.size(); i++) {
								xmlBuilder.deleteNode(excludeXpathList.get(i));
							}
						}
						byte[] rdf = xmlBuilder.getXML("UTF-8").getBytes();
						xmlBuilder.addNameSpace("ods", "http://lod.xdams.org/ontologies/ods/");
						Date today = new Date();
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ITALY);
						String date = format.format(today);
						xmlBuilder.insertNode("/rdf:RDF/*/ods:modified[@rdf:datatype='http://www.w3.org/2001/XMLSchema#dateTime']/text()", date);
						byte[] odsRdfEndPoint = xmlBuilder.getXML("UTF-8").getBytes();
						endpointRecordContainerList.add(new EndpointRecordContainer(idRecord, idArchive, modifyRdf(rdf, idArchive), modifyRdf(odsRdfEndPoint, idArchive), xml_id));
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("ERRORE GENERAZIONE RECORD IN END POINT");
						System.err.println("IDRECORD = " + idRecord);
						System.err.println(new String(resultSet.getBytes(("xml")), "UTF-8"));
					}
				}
				System.out.println("##################RECORD PRESI E MODIFICATI PER PUBBLICAZIONE");
				resultSet.close();
				preparedStatement.close();
				System.out.println("EndPointManager.publishRecords() collected " + endpointRecordContainerList.size());
				if (!singleRecord) {
					System.out.println("##################PUBBLICAZIONE MULTIPLA");
					manageRecord(endpointRecordContainerList, false, "pubblicazione", false, ACTION_ADD);
					executeReflectClass(idArchive, endpointRecordContainerList, ACTION_ADD);
				} else {
					System.out.println("##################PUBBLICAZIONE SINGOLA");
					manageRecord(endpointRecordContainerList, true, "pubblicazione", false, ACTION_ADD);
					executeReflectClass(idArchive, endpointRecordContainerList, ACTION_ADD);
				}
			}
			if (!singleRecord)
				setArchiveOnline(openDamsConnection, idArchive);
			openDamsConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			throw e;
		} finally {
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private String getStringAction(int action) {
		switch (action) {
		case ACTION_ADD:
			return ACTION_ADD_NAME;
		case ACTION_UPDATE:
			return ACTION_UPDATE_NAME;
		case ACTION_DELETE:
			return ACTION_DELETE_NAME;
		default:
			return "";
		}
	}

	private void executeReflectClass(int idArchive, EndpointRecordContainerList endpointRecordContainerList, int action) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {

		if (endPointClassUtils != null && endPointClassUtils.containsKey(idArchive)) {
			Map<String, ReflectClass> actionsMap = endPointClassUtils.get(idArchive);
			if (actionsMap != null && actionsMap.containsKey(getStringAction(action))) {
				ReflectClass reflectClass = actionsMap.get(getStringAction(action));
				System.out.println(reflectClass.toString());
				Class<?> c = Class.forName(reflectClass.invockingClass);
				Object obj = c.newInstance();
				Method setEndpointRecordContainerList = c.getMethod("setEndpointRecordContainerList", EndpointRecordContainerList.class);
				setEndpointRecordContainerList.invoke(obj, endpointRecordContainerList);

				Method[] methods = c.getDeclaredMethods();
				for (int i = 0; i < methods.length; i++) {
					System.out.println(methods[i].getName());
				}

				try {
					if (reflectClass.args != null && !reflectClass.args.isEmpty()) {
						Field[] fields = c.getDeclaredFields();
						for (int i = 0; i < fields.length; i++) {
							if (reflectClass.args.containsKey(fields[i].getName())) {
								System.out.println("Invocking " + "set" + StringUtils.capitalize(fields[i].getName()) + " " + fields[i].getType());
								Method method = c.getMethod("set" + StringUtils.capitalize(fields[i].getName()), fields[i].getType());
								method.invoke(obj, reflectClass.args.get(fields[i].getName()));
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Method method = c.getMethod(reflectClass.invockingMethod);
				method.invoke(obj);
			}
		}
	}

	private byte[] modifyRdf(byte[] odsRdf, int idArchive) {
		try {
			
			if (archiveReplacer != null && archiveReplacer.size() > 0) {
				String odsRdfString = new String(odsRdf, "UTF-8");
				if (archiveReplacer.get("all") != null) {
					List<String> replacerArray = archiveReplacer.get("all");
					for (String replaceString : replacerArray) {
						try {
							//System.out.println("replacing: " + StringUtils.substringBetween(replaceString, "find:", ";") + " ||| with: " + StringUtils.substringAfter(replaceString, ";replace:"));
							odsRdfString = odsRdfString.replaceAll(StringUtils.substringBetween(replaceString, "find:", ";"), StringUtils.substringAfter(replaceString, ";replace:"));
						} catch (Exception e) {
							System.out.println("EndPointManager.modifyRdf() replacer non valido " + e.getMessage());
						}
					}
					odsRdf = odsRdfString.getBytes("UTF-8");
				}
				if (archiveReplacer.get(String.valueOf(idArchive)) != null) {
					List<String> replacerArray = archiveReplacer.get(String.valueOf(idArchive));
					for (String replaceString : replacerArray) {
						try {
							odsRdfString = odsRdfString.replaceAll(StringUtils.substringBetween(replaceString, "find:", ";"), StringUtils.substringAfter(replaceString, ";replace:"));
						} catch (Exception e) {
							System.out.println("EndPointManager.modifyRdf() replacer non valido " + e.getMessage());
						}
					}
					odsRdf = odsRdfString.getBytes("UTF-8");
				}
			}
		} catch (Exception es) {
			System.out.println("EndPointManager.modifyRdf() error: " + es.getMessage());
		}
		return odsRdf;
	}

	private void rePublishRecords(String queryCount, String query, String queryRecord, int idArchive, String user, String endPointManagerkey) throws Exception {
		Connection openDamsConnection = null;
		boolean singleRecord = true;
		if (idArchive != 0) {
			singleRecord = false;
		}
		try {
			openDamsConnection = openDamsdataSource.getConnection();
			int pages = 0;
			if (queryCount != null && !singleRecord) {
				Statement st = openDamsConnection.createStatement();
				System.out.println("EndPointManager.publishRecords() query: " + queryCount);
				ResultSet resultSet = st.executeQuery(queryCount);
				resultSet.next();
				int tot = resultSet.getInt("tot");
				resultSet.close();
				if (tot % publishingPageSet == 0) {
					pages = tot / publishingPageSet;
				} else {
					pages = (tot / publishingPageSet) + 1;
				}
			} else {
				pages = 1;
			}
			if (!singleRecord) {
				setArchiveOffline(openDamsConnection, idArchive);
			}
			clearEndpoint(idArchive);
			for (int x = 0; x < pages; x++) {
				PreparedStatement preparedStatement = openDamsConnection.prepareStatement(query);
				preparedStatement.setInt(1, (x * 100));
				System.out.println("EndPointManager.publishRecords() query: " + query);
				ResultSet resultSet = preparedStatement.executeQuery();
				EndpointRecordContainerList endpointRecordContainerList = new EndpointRecordContainerList(user, endPointManagerkey);
				System.out.println("EndPointManager.publishRecords() collecting");
				int conta = 0;
				while (resultSet.next()) {
					int ref_id_Record = resultSet.getInt("ref_id_record");
					PreparedStatement preparedStatementRecord = openDamsConnection.prepareStatement(queryRecord);
					preparedStatementRecord.setInt(1, ref_id_Record);
					ResultSet resultSetRecord = preparedStatementRecord.executeQuery();
					while (resultSetRecord.next()) {
						if (conta % 10 == 0) {
							conta++;
							System.out.println("EndPointManager.publishRecords() " + conta);
						}
						int idRecord = 0;
						try {
							idRecord = resultSetRecord.getInt("id_record");
							idArchive = resultSetRecord.getInt("ref_id_archive");
							byte[] odsRdf = resultSetRecord.getBytes("xml");

							String xml_id = resultSetRecord.getString("xml_id");
							List<String> excludeXpathList = getExcludeXpathListArchiveMap().get(idArchive);
							if (excludeXpathList == null) {
								excludeXpathList = getExcludeXpathListArchiveMap().get("all");
							}
							XMLBuilder xmlBuilder = new XMLBuilder(new ByteArrayInputStream(odsRdf));
							if (excludeXpathList != null && excludeXpathList.size() > 0) {
								for (int i = 0; i < excludeXpathList.size(); i++) {
									xmlBuilder.deleteNode(excludeXpathList.get(i));
								}
							}
							byte[] rdf = xmlBuilder.getXML("UTF-8").getBytes();
							xmlBuilder.addNameSpace("ods", "http://lod.xdams.org/ontologies/ods/");
							Date today = new Date();
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ITALY);
							String date = format.format(today);
							xmlBuilder.insertNode("/rdf:RDF/*/ods:modified[@rdf:datatype='http://www.w3.org/2001/XMLSchema#dateTime']/text()", date);
							byte[] odsRdfEndPoint = xmlBuilder.getXML("UTF-8").getBytes();
							endpointRecordContainerList.add(new EndpointRecordContainer(idRecord, idArchive, modifyRdf(rdf, idArchive), modifyRdf(odsRdfEndPoint, idArchive), xml_id));
						} catch (Exception e) {
							e.printStackTrace();
							System.err.println("ERRORE GENERAZIONE RECORD IN END POINT");
							System.err.println("IDRECORD = " + idRecord);
							System.err.println(new String(resultSetRecord.getBytes(("xml")), "UTF-8"));
						}
					}
					resultSetRecord.close();
					preparedStatementRecord.close();
				}
				System.out.println("##################RECORD PRESI E MODIFICATI PER PUBBLICAZIONE");
				resultSet.close();
				preparedStatement.close();
				System.out.println("EndPointManager.publishRecords() collected " + endpointRecordContainerList.size());
				if (!singleRecord) {
					System.out.println("##################PUBBLICAZIONE MULTIPLA");
					manageRecord(endpointRecordContainerList, false, "ripubblicazione", false, ACTION_UPDATE);
					executeReflectClass(idArchive, endpointRecordContainerList, ACTION_UPDATE);
				} else {
					System.out.println("##################PUBBLICAZIONE SINGOLA");
					manageRecord(endpointRecordContainerList, true, "ripubblicazione", false, ACTION_UPDATE);
					executeReflectClass(idArchive, endpointRecordContainerList, ACTION_UPDATE);
				}
			}
			if (!singleRecord)
				setArchiveOnline(openDamsConnection, idArchive);
			openDamsConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			throw e;
		} finally {
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private void rePublishRecordsModified(String queryCount, String query, String queryRecord, int idArchive, String user, String endPointManagerkey) throws Exception {
		Connection openDamsConnection = null;
		boolean singleRecord = true;
		if (idArchive != 0) {
			singleRecord = false;
		}
		try {
			openDamsConnection = openDamsdataSource.getConnection();
			int pages = 0;
			if (queryCount != null && !singleRecord) {
				Statement st = openDamsConnection.createStatement();
				System.out.println("EndPointManager.publishRecords() query: " + queryCount);
				ResultSet resultSet = st.executeQuery(queryCount);
				resultSet.next();
				int tot = resultSet.getInt("tot");
				resultSet.close();
				if (tot % publishingPageSet == 0) {
					pages = tot / publishingPageSet;
				} else {
					pages = (tot / publishingPageSet) + 1;
				}
			} else {
				pages = 1;
			}
			if (!singleRecord) {
				setArchiveOffline(openDamsConnection, idArchive);
			}

			for (int x = 0; x < pages; x++) {
				PreparedStatement preparedStatement = openDamsConnection.prepareStatement(query);
				preparedStatement.setInt(1, (x * 100));
				System.out.println("EndPointManager.publishRecords() query: " + query);
				ResultSet resultSet = preparedStatement.executeQuery();
				EndpointRecordContainerList endpointRecordContainerList = new EndpointRecordContainerList(user, endPointManagerkey);
				System.out.println("EndPointManager.publishRecords() collecting");
				int conta = 0;
				while (resultSet.next()) {
					int ref_id_Record = resultSet.getInt("ref_id_record");
					PreparedStatement preparedStatementRecord = openDamsConnection.prepareStatement(queryRecord);
					preparedStatementRecord.setInt(1, ref_id_Record);
					ResultSet resultSetRecord = preparedStatementRecord.executeQuery();
					while (resultSetRecord.next()) {
						if (conta % 10 == 0) {
							conta++;
							System.out.println("EndPointManager.publishRecords() " + conta);
						}
						int idRecord = 0;
						try {
							idRecord = resultSetRecord.getInt("id_record");
							removeEndPointObject(idRecord, user, endPointManagerkey);
							idArchive = resultSetRecord.getInt("ref_id_archive");
							byte[] odsRdf = resultSetRecord.getBytes("xml");

							String xml_id = resultSetRecord.getString("xml_id");
							List<String> excludeXpathList = getExcludeXpathListArchiveMap().get(idArchive);
							if (excludeXpathList == null) {
								excludeXpathList = getExcludeXpathListArchiveMap().get("all");
							}
							XMLBuilder xmlBuilder = new XMLBuilder(new ByteArrayInputStream(odsRdf));
							if (excludeXpathList != null && excludeXpathList.size() > 0) {
								for (int i = 0; i < excludeXpathList.size(); i++) {
									xmlBuilder.deleteNode(excludeXpathList.get(i));
								}
							}
							byte[] rdf = xmlBuilder.getXML("UTF-8").getBytes();
							xmlBuilder.addNameSpace("ods", "http://lod.xdams.org/ontologies/ods/");
							Date today = new Date();
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ITALY);
							String date = format.format(today);
							xmlBuilder.insertNode("/rdf:RDF/*/ods:modified[@rdf:datatype='http://www.w3.org/2001/XMLSchema#dateTime']/text()", date);
							byte[] odsRdfEndPoint = xmlBuilder.getXML("UTF-8").getBytes();
							endpointRecordContainerList.add(new EndpointRecordContainer(idRecord, idArchive, modifyRdf(rdf, idArchive), modifyRdf(odsRdfEndPoint, idArchive), xml_id));
						} catch (Exception e) {
							e.printStackTrace();
							System.err.println("ERRORE GENERAZIONE RECORD IN END POINT");
							System.err.println("IDRECORD = " + idRecord);
							System.err.println(new String(resultSetRecord.getBytes(("xml")), "UTF-8"));
						}
					}
					resultSetRecord.close();
					preparedStatementRecord.close();
				}
				System.out.println("##################RECORD PRESI E MODIFICATI PER PUBBLICAZIONE");
				resultSet.close();
				preparedStatement.close();
				System.out.println("EndPointManager.publishRecords() collected " + endpointRecordContainerList.size());
				if (!singleRecord) {
					System.out.println("##################PUBBLICAZIONE MULTIPLA");
					manageRecord(endpointRecordContainerList, false, "ripubblicazione", false, ACTION_UPDATE);
					executeReflectClass(idArchive, endpointRecordContainerList, ACTION_UPDATE);
				} else {
					System.out.println("##################PUBBLICAZIONE SINGOLA");
					manageRecord(endpointRecordContainerList, true, "ripubblicazione", false, ACTION_UPDATE);
					executeReflectClass(idArchive, endpointRecordContainerList, ACTION_UPDATE);
				}
			}
			if (!singleRecord)
				setArchiveOnline(openDamsConnection, idArchive);
			openDamsConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			throw e;
		} finally {
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private void removeRecord(String query, int idArchive, String user, String endPointManagerkey) {
		Connection openDamsConnection = null;
		try {
			openDamsConnection = openDamsdataSource.getConnection();
			Statement st = openDamsConnection.createStatement();
			ResultSet resultSet = st.executeQuery(query);
			EndpointRecordContainerList endpointRecordContainerList = new EndpointRecordContainerList(user, endPointManagerkey);
			while (resultSet.next()) {
				int idRecord = 0;
				try {
					idRecord = resultSet.getInt("id_record");
					idArchive = resultSet.getInt("ref_id_archive");
					byte[] odsRdf = resultSet.getBytes("xml");
					String xml_id = resultSet.getString("xml_id");
					XMLBuilder xmlBuilder = new XMLBuilder(new ByteArrayInputStream(odsRdf));
					xmlBuilder.deleteNode("/rdf:RDF/*/*");
					xmlBuilder.addNameSpace("ods", "http://lod.xdams.org/ontologies/ods/");
					Date today = new Date();
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ITALY);
					String date = format.format(today);
					xmlBuilder.insertNode("/rdf:RDF/*/ods:deleted[@rdf:datatype='http://www.w3.org/2001/XMLSchema#dateTime']/text()", date);
					byte[] odsRdfEndPoint = xmlBuilder.getXML("UTF-8").getBytes();
					endpointRecordContainerList.add(new EndpointRecordContainer(idRecord, idArchive, null, odsRdfEndPoint, xml_id));
				} catch (Exception e) {
					System.err.println("ERRORE GENERAZIONE RECORD IN END POINT");
					System.err.println("IDRECORD = " + idRecord);
					System.err.println(new String(resultSet.getBytes(("xml")), "UTF-8"));
				}
			}
			resultSet.close();
			st.close();
			System.out.println("##################DEPUBBLICAZIONE SINGOLA");
			manageRecord(endpointRecordContainerList, true, "rimozione", false, ACTION_DELETE);
			executeReflectClass(idArchive, endpointRecordContainerList, ACTION_DELETE);
			openDamsConnection.close();
		} catch (Exception e) {
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} finally {
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void removeRecords(String queryCount, String query, String queryRecord, int idArchive, String user, String endPointManagerkey) throws Exception {
		Connection openDamsConnection = null;
		try {
			openDamsConnection = openDamsdataSource.getConnection();
			int pages = 0;
			if (queryCount != null) {
				Statement st = openDamsConnection.createStatement();
				ResultSet resultSet = st.executeQuery(queryCount);
				resultSet.next();
				int tot = resultSet.getInt("tot");
				resultSet.close();
				if (tot % publishingPageSet == 0) {
					pages = tot / publishingPageSet;
				} else {
					pages = (tot / publishingPageSet) + 1;
				}
			} else {
				pages = 1;
			}
			setArchiveOffline(openDamsConnection, idArchive);
			clearEndpoint(idArchive);
			for (int x = 0; x < pages; x++) {
				PreparedStatement preparedStatement = openDamsConnection.prepareStatement(query);
				preparedStatement.setInt(1, (x * 100));
				System.out.println("EndPointManager.publishRecords() query: " + query);
				ResultSet resultSet = preparedStatement.executeQuery();
				EndpointRecordContainerList endpointRecordContainerList = new EndpointRecordContainerList(user, endPointManagerkey);
				System.out.println("EndPointManager.publishRecords() collecting");
				int conta = 0;
				while (resultSet.next()) {
					int ref_id_Record = resultSet.getInt("ref_id_record");
					PreparedStatement preparedStatementRecord = openDamsConnection.prepareStatement(queryRecord);
					preparedStatementRecord.setInt(1, ref_id_Record);
					ResultSet resultSetRecord = preparedStatementRecord.executeQuery();
					while (resultSetRecord.next()) {
						if (conta % 10 == 0) {
							conta++;
							System.out.println("EndPointManager.publishRecords() " + conta);
						}
						int idRecord = 0;
						try {

							String xml_id = resultSetRecord.getString("xml_id");
							//
							idRecord = resultSetRecord.getInt("id_record");
							idArchive = resultSetRecord.getInt("ref_id_archive");
							// byte[] odsRdf = resultSetRecord.getBytes("xml");
							// XMLBuilder xmlBuilder = new XMLBuilder(new
							// ByteArrayInputStream(odsRdf));
							// xmlBuilder.deleteNode("/rdf:RDF/*/*");
							// xmlBuilder.addNameSpace("ods",
							// "http://lod.xdams.org/ontologies/ods/");
							// Date today = new Date();
							// SimpleDateFormat format = new
							// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
							// Locale.ITALY);
							// String date = format.format(today);
							// xmlBuilder.insertNode("/rdf:RDF/*/ods:deleted[@rdf:datatype='http://www.w3.org/2001/XMLSchema#dateTime']/text()",
							// date);
							// byte[] odsRdfEndPoint =
							// xmlBuilder.getXML("UTF-8").getBytes();

							endpointRecordContainerList.add(new EndpointRecordContainer(idRecord, idArchive, null, null, xml_id));
						} catch (Exception e) {
							e.printStackTrace();
							System.err.println("ERRORE RIMOZIONE RECORD IN END POINT");
							System.err.println("IDRECORD = " + idRecord);
							System.err.println(new String(resultSetRecord.getBytes(("xml")), "UTF-8"));
						}
					}
					resultSetRecord.close();
					preparedStatementRecord.close();
				}
				System.out.println("##################RECORD PRESI E MODIFICATI PER DEPUBBLICAZIONE");
				resultSet.close();
				preparedStatement.close();
				System.out.println("EndPointManager.publishRecords() collected " + endpointRecordContainerList.size());
				manageRecord(endpointRecordContainerList, false, "rimozione", true, ACTION_DELETE);
				executeReflectClass(idArchive, endpointRecordContainerList, ACTION_DELETE);
			}
			setArchiveOnline(openDamsConnection, idArchive);
			openDamsConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			throw e;
		} finally {
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public abstract boolean checkPublished(int id_record, int id_archive, String rdfAbout) throws SQLException, RepositoryException, MalformedQueryException, QueryEvaluationException;

	public abstract boolean checkPublishedArchive(int idArchive) throws SQLException, RepositoryException, MalformedQueryException, QueryEvaluationException;

	public abstract List<HashMap<String, Value>> executeQuery(String query);

	public abstract URI[] getArchiveGraphs(Repository repository, int idArchive);

	private void setArchiveOffline(Connection connection, int idArchive) {
		String update = "Update archives set offline=1 where id_archive=" + idArchive + ";";
		try {
			Statement updateStatement = connection.createStatement();
			updateStatement.executeUpdate(update);
			updateStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setArchiveOnline(Connection connection, int idArchive) {
		String update = "Update archives set offline=0 where id_archive=" + idArchive + ";";
		try {
			Statement updateStatement = connection.createStatement();
			updateStatement.executeUpdate(update);
			updateStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setExcludeXpathListArchiveMap(Map<Object, List<String>> excludeXpathListArchiveMap) {
		this.excludeXpathListArchiveMap = excludeXpathListArchiveMap;
	}

	public Map<Object, List<String>> getExcludeXpathListArchiveMap() {
		return excludeXpathListArchiveMap;
	}

	public void setOpenDamsdataSource(DataSource openDamsdataSource) {
		this.openDamsdataSource = openDamsdataSource;
	}

	public void setAutoArchivePublishing(Map<Integer, Boolean> autoArchivePublishing) {
		this.autoArchivePublishing = autoArchivePublishing;
	}

	public boolean isAutoPublishingArchive(int idArchive) {
		System.out.println("autoArchivePublishing.get(idArchive) >>>>>>>>>>>>>>>>>>>>>>>>>>>>><" + autoArchivePublishing.get(idArchive));
		if (autoArchivePublishing.get(idArchive) != null)
			return autoArchivePublishing.get(idArchive);
		else
			return false;
	}

	public boolean isAllowedArchive(int idArchive) {
		if (allowedArchivePublishing.get(idArchive) != null)
			return allowedArchivePublishing.get(idArchive);
		else
			return false;
	}

	public void performOperation(int idRecord, int refIdEndoPoint, String action, boolean result, int refIdArchive, String user, int ref_id_endpoint_action) {
		System.out.println("EndPointManager.performOperation() idRecord: " + idRecord + " | refIdEndoPoint: " + refIdEndoPoint + " | action: " + action + " | result: " + result + " | user: " + user + " | ref_id_endpoint_action: " + ref_id_endpoint_action);

		Connection openDamsConnection = null;
		try {
			openDamsConnection = openDamsdataSource.getConnection();
			int operationResult = 0;
			if (result)
				operationResult = 1;

			java.sql.Timestamp data = new java.sql.Timestamp(System.currentTimeMillis());
			String query = "insert into endpoints_manager values (" + idRecord + "," + refIdEndoPoint + ",'" + action + "','" + data.toString() + "'," + operationResult + "," + refIdArchive + ",'" + user + "'," + ref_id_endpoint_action + ");";
			if (findOperation(openDamsConnection, idRecord, refIdEndoPoint))
				query = "update endpoints_manager set ref_id_record=" + idRecord + ",ref_id_endpoint=" + refIdEndoPoint + ",action='" + action + "',date='" + data.toString() + "',result=" + operationResult + ",ref_id_archive=" + refIdArchive + ",user='" + user + "',ref_id_endpoint_action=" + ref_id_endpoint_action + " where ref_id_record=" + idRecord + " and ref_id_endpoint='" + refIdEndoPoint + "';";
			Statement updateStatement = openDamsConnection.createStatement();
			updateStatement.executeUpdate(query);
			updateStatement.close();
			openDamsConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} finally {
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean findOperation(Connection connection, int idRecord, int idEndPoint) throws SQLException {
		String query = "SELECT * FROM endpoints_manager where ref_id_record=" + idRecord + " and ref_id_endpoint='" + idEndPoint + "';";
		Statement st = connection.createStatement();
		ResultSet resultSet = st.executeQuery(query);
		return resultSet.next();
	}

	public int findEndPoint(String endPointManagerkey) throws SQLException {
		Connection openDamsConnection = null;
		int result = 0;
		try {
			openDamsConnection = openDamsdataSource.getConnection();
			String query = "SELECT id_endpoint FROM endpoints where manager_key='" + endPointManagerkey + "';";
			Statement st = openDamsConnection.createStatement();
			ResultSet resultSet = st.executeQuery(query);
			resultSet.next();
			result = resultSet.getInt("id_endpoint");
			resultSet.close();
			st.close();
			openDamsConnection.close();
		} catch (Exception e) {
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} finally {
			try {
				if (openDamsConnection != null && !openDamsConnection.isClosed()) {
					openDamsConnection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	protected class EndpointRecordContainer {
		public int idRecord;
		public int idArchive;
		public byte[] rdf;
		public byte[] odsRdf;
		public String rdfAbout;

		public EndpointRecordContainer(int idRecord, int idArchive, byte[] rdf, byte[] odsRdf, String rdfAbout) {
			this.idArchive = idArchive;
			this.idRecord = idRecord;
			this.rdf = rdf;
			this.odsRdf = odsRdf;
			this.rdfAbout = rdfAbout;
		}
	}

	public class EndpointRecordContainerList extends ArrayList<EndpointRecordContainer> {
		private static final long serialVersionUID = 6280443789662917697L;
		public String user;
		public String endPointManagerkey;

		public EndpointRecordContainerList(String user, String endPointManagerkey) {
			super();
			this.user = user;
			this.endPointManagerkey = endPointManagerkey;
		}

	}

	public void setAllowedArchivePublishing(Map<Integer, Boolean> allowedArchivePublishing) {
		this.allowedArchivePublishing = allowedArchivePublishing;
	}

	public void setDefaultDomain(String defaultDomain) {
		this.defaultDomain = defaultDomain;
	}

	public void setSparqlEndpointUri(String sparqlEndpointUri) {
		this.sparqlEndpointUri = sparqlEndpointUri;
	}

	public String getDefaultDomain() {
		return defaultDomain;
	}

	public String getSparqlEndpointUri() {
		return sparqlEndpointUri;
	}

	public Map<String, List<String>> getArchiveReplacer() {
		return archiveReplacer;
	}

	public void setArchiveReplacer(Map<String, List<String>> archiveReplacer) {
		this.archiveReplacer = archiveReplacer;
	}

	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}

	public int getPublishingPageSet() {
		return publishingPageSet;
	}

	public void setPublishingPageSet(int publishingPageSet) {
		this.publishingPageSet = publishingPageSet;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setEndPointClassUtils(Map<Integer, Map<String, ReflectClass>> endPointClassUtils) {
		this.endPointClassUtils = endPointClassUtils;
	}
}
