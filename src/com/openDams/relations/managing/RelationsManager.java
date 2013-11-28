package com.openDams.relations.managing;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.jdbc.JdbcDirectory;
import org.dom4j.DocumentException;

import com.openDams.bean.Records;
import com.openDams.configuration.ConfigurationException;
import com.openDams.dao.OpenDamsServiceProvider;
import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.index.factory.DialectFactory;
import com.openDams.index.factory.DocumentFactory;
import com.openDams.relations.configuration.ConfigurationReader;
import com.openDams.relations.configuration.Element;
import com.regesta.framework.xml.XMLBuilder;
import com.regesta.framework.xml.XMLReader;

public class RelationsManager {
	private DataSource dataSource = null;
	private ConfigurationReader configurationRelationsReader = null;
	private HashMap<Integer, ArrayList<Object>> elements_map = null;
	private IndexConfiguration indexConfiguration = null;

	public RelationsManager() {
	}

	public void insertXmlRelation(int relationType, int idRecordTo, int idRecordFrom) throws Exception {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Statement st = connection.createStatement();
			// String query =
			// "SELECT id_record,ref_id_archive,xml FROM records where id_record="+idRecordFrom+" or id_record="+idRecordTo+";";
			String query = "SELECT records.id_record,records.ref_id_archive,records.xml,records.title,archives.label,archives.use_default_index FROM records inner join archives on records.ref_id_archive=archives.id_archive where records.id_record=" + idRecordFrom + " or records.id_record=" + idRecordTo + ";";

			ResultSet resultSet = st.executeQuery(query);
			TempRecord sourceRecord = new TempRecord();
			TempRecord destRecord = new TempRecord();
			while (resultSet.next()) {
				int id_record = resultSet.getInt("id_record");
				int ref_id_archive = resultSet.getInt("ref_id_archive");
				String xml = new String(resultSet.getBytes(("xml")), "UTF-8");
				if (id_record == idRecordFrom) {
					sourceRecord.id_record = id_record;
					sourceRecord.ref_id_archive = ref_id_archive;
					sourceRecord.xml = xml;
				} else {
					destRecord.id_record = id_record;
					destRecord.ref_id_archive = ref_id_archive;
					destRecord.xml = xml;
					String title = resultSet.getString("title");
					String archive_label = resultSet.getString("label");
					boolean use_default_index = resultSet.getBoolean("use_default_index");
					destRecord.title = title;
					destRecord.archive_label = archive_label;
					destRecord.use_default_index = use_default_index;
				}
			}
			resultSet.close();
			st.close();
			Element element = configurationRelationsReader.getElement(relationType, sourceRecord.ref_id_archive, destRecord.ref_id_archive);
			if (element != null && element.getText() != null) {
				String[] xpaths = element.getText().split(element.getXpath_separator());
				XMLReader xmlReader = new XMLReader(sourceRecord.xml);
				String relationtext = xmlReader.getNodeValue(xpaths[0]);
				XMLBuilder xmlToModify = new XMLBuilder(destRecord.xml, "UTF-8");
				xmlToModify.insertValueAt(xpaths[1], relationtext);
				String xmlString = xmlToModify.getXML("UTF-8");
				xmlString = xmlString.replaceAll("xmlns=\"\"", "");
				destRecord.xml = xmlString;
				try {
					
					  /*PreparedStatement ps = connection.prepareStatement(
					  "update records set xml=? where id_record="
					  +destRecord.id_record+";"); ps.setBytes(1,
					  xmlString.getBytes("UTF-8")); ps.executeUpdate();
					  ps.close(); rebuildIndex(destRecord);*/
					 // System.out.println(xmlString);
					 
					Records record = (Records) OpenDamsServiceProvider.getService().getListFromSQL(Records.class, "SELECT * FROM records where id_record="+destRecord.id_record+";").get(0);
					record.setXml(destRecord.xml.getBytes());
					record.setAlredyProcessed(true);
					System.out.println("record:  " + record.getIdRecord());
					OpenDamsServiceProvider.getService().update(record);
				} catch (Exception e) {
					System.out.println("################################ERRORE IN insertXmlRelation##############################");
					//e.printStackTrace();
				}
			} else {
				System.out.println("################################ERRORE RELAZIONE POST INSERT NON TROVATO ELEMENTO IN FILE CONFIGURAZIONE##############################");
			}
			connection.close();
		} finally {
			if (connection != null && !connection.isClosed())
				connection.close();
		}
	}
    	
	public void removeXmlRelation(int relationType, int idRecordTo, int idRecordFrom) throws Exception {
		Connection connection = null;

		try {
			connection = dataSource.getConnection();
			Statement st = connection.createStatement();
			// String query =
			// "SELECT id_record,ref_id_archive,xml FROM records where id_record="+idRecordFrom+" or id_record="+idRecordTo+";";
			String query = "SELECT records.id_record,records.ref_id_archive,records.xml,records.title,archives.label,archives.use_default_index FROM records inner join archives on records.ref_id_archive=archives.id_archive where records.id_record=" + idRecordFrom + " or records.id_record=" + idRecordTo + ";";
			ResultSet resultSet = st.executeQuery(query);
			TempRecord sourceRecord = new TempRecord();
			TempRecord destRecord = new TempRecord();
			while (resultSet.next()) {
				int id_record = resultSet.getInt("id_record");
				int ref_id_archive = resultSet.getInt("ref_id_archive");
				String xml = new String(resultSet.getBytes(("xml")), "UTF-8");
				if (id_record == idRecordFrom) {
					sourceRecord.id_record = id_record;
					sourceRecord.ref_id_archive = ref_id_archive;
					sourceRecord.xml = xml;
				} else {
					destRecord.id_record = id_record;
					destRecord.ref_id_archive = ref_id_archive;
					destRecord.xml = xml;
					String title = resultSet.getString("title");
					String archive_label = resultSet.getString("label");
					boolean use_default_index = resultSet.getBoolean("use_default_index");
					destRecord.title = title;
					destRecord.archive_label = archive_label;
					destRecord.use_default_index = use_default_index;
				}
			}
			resultSet.close();
			st.close();
			Element element = configurationRelationsReader.getElement(relationType, sourceRecord.ref_id_archive, destRecord.ref_id_archive);
			if (element != null && element.getText() != null) {
				String[] xpaths = element.getText().split(element.getXpath_separator());
				XMLReader xmlReader = new XMLReader(sourceRecord.xml);
				String relationtext = xmlReader.getNodeValue(xpaths[0]);
				XMLBuilder xmlToModify = new XMLBuilder(destRecord.xml, "UTF-8");
				String xpath = xpaths[1].replaceAll("\\[99\\]", "");
				xpath = xpath.replaceAll("\\[400\\]", "");
				String attr = StringUtils.substringAfterLast(xpath, "/");
				String xpathToRemove = StringUtils.substringBeforeLast(xpath, "/") + "[" + attr + "='" + relationtext + "']";
				System.out.println("xpathToRemove: " + xpathToRemove);
				xmlToModify.deleteNode(xpathToRemove);
				String xmlString = xmlToModify.getXML("UTF-8");
				xmlString = xmlString.replace("xmlns=\"\"", "");
				destRecord.xml = xmlString;
				try {
					/* PreparedStatement ps =
					 connection.prepareStatement("update records set xml=? where id_record="
					 + destRecord.id_record + ";");
					 ps.setBytes(1, xmlString.getBytes("UTF-8"));
					 ps.executeUpdate();
					 ps.close();
					 rebuildIndex(destRecord);*/
					

					Records record = (Records) OpenDamsServiceProvider.getService().getListFromSQL(Records.class, "SELECT * FROM records where id_record="+destRecord.id_record+";").get(0);
					record.setXml(destRecord.xml.getBytes());
					record.setAlredyProcessed(true);
					OpenDamsServiceProvider.getService().update(record);

				} catch (Exception e) {
					System.out.println("################################ERRORE IN removeXmlRelation##############################");
					//e.printStackTrace();
				}
			}
			connection.close();
		} finally {
			if (connection != null && !connection.isClosed())
				connection.close();
		}
	}
	
	
	public byte[] insertRelationInXml(int relationType, int idRecordTo, int idRecordFrom,byte[] xmlTo) throws Exception {
		Connection connection = null;
		byte[] result = null;
		try {
			connection = dataSource.getConnection();
			Statement st = connection.createStatement();
			// String query =
			// "SELECT id_record,ref_id_archive,xml FROM records where id_record="+idRecordFrom+" or id_record="+idRecordTo+";";
			String query = "SELECT records.id_record,records.ref_id_archive,records.xml,records.title,archives.label,archives.use_default_index FROM records inner join archives on records.ref_id_archive=archives.id_archive where records.id_record=" + idRecordFrom + " or records.id_record=" + idRecordTo + ";";

			ResultSet resultSet = st.executeQuery(query);
			TempRecord sourceRecord = new TempRecord();
			TempRecord destRecord = new TempRecord();
			while (resultSet.next()) {
				int id_record = resultSet.getInt("id_record");
				int ref_id_archive = resultSet.getInt("ref_id_archive");
				String xml = new String(resultSet.getBytes(("xml")), "UTF-8");
				if (id_record == idRecordFrom) {
					sourceRecord.id_record = id_record;
					sourceRecord.ref_id_archive = ref_id_archive;
					sourceRecord.xml = xml;
				} else {
					destRecord.id_record = id_record;
					destRecord.ref_id_archive = ref_id_archive;
					destRecord.xml = new String(xmlTo, "UTF-8");
					String title = resultSet.getString("title");
					String archive_label = resultSet.getString("label");
					boolean use_default_index = resultSet.getBoolean("use_default_index");
					destRecord.title = title;
					destRecord.archive_label = archive_label;
					destRecord.use_default_index = use_default_index;
				}
			}
			resultSet.close();
			st.close();
			Element element = configurationRelationsReader.getElement(relationType, sourceRecord.ref_id_archive, destRecord.ref_id_archive);
			if (element != null && element.getText() != null) {
				String[] xpaths = element.getText().split(element.getXpath_separator());
				XMLReader xmlReader = new XMLReader(sourceRecord.xml);
				String relationtext = xmlReader.getNodeValue(xpaths[0]);
				XMLBuilder xmlToModify = new XMLBuilder(destRecord.xml, "UTF-8");
				xmlToModify.insertValueAt(xpaths[1], relationtext);
				String xmlString = xmlToModify.getXML("UTF-8");
				xmlString = xmlString.replaceAll("xmlns=\"\"", "");
				destRecord.xml = xmlString;
				result = destRecord.xml.getBytes();
			} else {
				System.out.println("################################ERRORE RELAZIONE POST INSERT NON TROVATO ELEMENTO IN FILE CONFIGURAZIONE##############################");
			}
			connection.close();
		} finally {
			if (connection != null && !connection.isClosed())
				connection.close();
		}
		return result;
	}
	
	public byte[] removeRelationFromXml(int relationType, int idRecordTo, int idRecordFrom,byte[] xmlTo) throws Exception {
		Connection connection = null;
		byte[] result = null;
		try {
			connection = dataSource.getConnection();
			Statement st = connection.createStatement();
			// String query =
			// "SELECT id_record,ref_id_archive,xml FROM records where id_record="+idRecordFrom+" or id_record="+idRecordTo+";";
			String query = "SELECT records.id_record,records.ref_id_archive,records.xml,records.title,archives.label,archives.use_default_index FROM records inner join archives on records.ref_id_archive=archives.id_archive where records.id_record=" + idRecordFrom + " or records.id_record=" + idRecordTo + ";";
			ResultSet resultSet = st.executeQuery(query);
			TempRecord sourceRecord = new TempRecord();
			TempRecord destRecord = new TempRecord();
			while (resultSet.next()) {
				int id_record = resultSet.getInt("id_record");
				int ref_id_archive = resultSet.getInt("ref_id_archive");
				String xml = new String(resultSet.getBytes(("xml")), "UTF-8");
				if (id_record == idRecordFrom) {
					sourceRecord.id_record = id_record;
					sourceRecord.ref_id_archive = ref_id_archive;
					sourceRecord.xml = xml;
				} else {
					destRecord.id_record = id_record;
					destRecord.ref_id_archive = ref_id_archive;
					destRecord.xml = new String(xmlTo, "UTF-8");
					String title = resultSet.getString("title");
					String archive_label = resultSet.getString("label");
					boolean use_default_index = resultSet.getBoolean("use_default_index");
					destRecord.title = title;
					destRecord.archive_label = archive_label;
					destRecord.use_default_index = use_default_index;
				}
			}
			resultSet.close();
			st.close();
			Element element = configurationRelationsReader.getElement(relationType, sourceRecord.ref_id_archive, destRecord.ref_id_archive);
			if (element != null && element.getText() != null) {
				String[] xpaths = element.getText().split(element.getXpath_separator());
				XMLReader xmlReader = new XMLReader(sourceRecord.xml);
				String relationtext = xmlReader.getNodeValue(xpaths[0]);
				XMLBuilder xmlToModify = new XMLBuilder(destRecord.xml, "UTF-8");
				String xpath = xpaths[1].replaceAll("\\[99\\]", "");
				xpath = xpath.replaceAll("\\[400\\]", "");
				String attr = StringUtils.substringAfterLast(xpath, "/");
				String xpathToRemove = StringUtils.substringBeforeLast(xpath, "/") + "[" + attr + "='" + relationtext + "']";
				System.out.println("xpathToRemove: " + xpathToRemove);
				xmlToModify.deleteNode(xpathToRemove);
				String xmlString = xmlToModify.getXML("UTF-8");
				xmlString = xmlString.replace("xmlns=\"\"", "");
				destRecord.xml = xmlString;
				result = destRecord.xml.getBytes();
			}
			connection.close();
		} finally {
			if (connection != null && !connection.isClosed())
				connection.close();
		}
		return result;
	}
	/*private void rebuildIndex(TempRecord tempRecord) {
		Directory directory = null;
		try {
			if (elements_map == null)
				elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();
			if (elements_map.get(tempRecord.ref_id_archive) == null)
				elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();

			if (indexConfiguration.isFsDirectory())
				directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + tempRecord.ref_id_archive + "_" + indexConfiguration.getIndex_name()));
			else
				directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), tempRecord.ref_id_archive + "_" + indexConfiguration.getIndex_name());
			Document doc = DocumentFactory.buildDocument(Integer.toString(tempRecord.id_record), Integer.toString(tempRecord.ref_id_archive), tempRecord.archive_label, tempRecord.title, tempRecord.xml, elements_map.get(tempRecord.ref_id_archive));
			indexConfiguration.getIndexManager(tempRecord.ref_id_archive).updateIndex(directory, doc, Integer.toString(tempRecord.id_record), elements_map.get(tempRecord.ref_id_archive));
			if (!indexConfiguration.isFsDirectory())
				((JdbcDirectory) directory).deleteMarkDeleted();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("################################ERRORE RELAZIONE POST INSERT rebuildIndex##############################");
			e.printStackTrace();
		} finally {
			try {
				if (directory != null)
					directory.close();
			} catch (IOException e) {
			}
		}

		if (tempRecord.use_default_index) {
			try {
				if (indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + tempRecord.ref_id_archive + "_" + indexConfiguration.getGeneric_index_name()));
				else
					directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), tempRecord.ref_id_archive + "_" + indexConfiguration.getGeneric_index_name());
				Document doc = DocumentFactory.buildGenericdDocument(Integer.toString(tempRecord.id_record), Integer.toString(tempRecord.ref_id_archive), tempRecord.archive_label, tempRecord.title, tempRecord.xml);
				indexConfiguration.getIndexManager(tempRecord.ref_id_archive).updateIndex(directory, doc, Integer.toString(tempRecord.id_record));
				if (!indexConfiguration.isFsDirectory())
					((JdbcDirectory) directory).deleteMarkDeleted();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (LockObtainFailedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			} finally {
				try {
					if (directory != null)
						directory.close();
				} catch (IOException e) {
				}
			}
		}
	}*/

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setConfigurationRelationsReader(ConfigurationReader configurationRelationsReader) {
		this.configurationRelationsReader = configurationRelationsReader;
	}

	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}

	private class TempRecord {
		int id_record;
		int ref_id_archive;
		String xml;
		String title;
		String archive_label;
		boolean use_default_index;

		public TempRecord() {
		}
	}
}
