package com.openDams.db.event;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.jdbc.JdbcDirectory;
import org.dom4j.DocumentException;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.Initializable;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;

import com.openDams.bean.Records;
import com.openDams.configuration.ConfigurationException;
import com.openDams.dao.OpenDamsServiceProvider;
import com.openDams.endpoint.managing.EndPointManager;
import com.openDams.endpoint.managing.OpenDamsEndPointManagerFactoryProvider;
import com.openDams.index.factory.DialectFactory;
import com.openDams.index.factory.DocumentFactory;

public class DBEventUpdateListener extends DBEventListener implements Serializable, Initializable, PostUpdateEventListener {

	private static final long serialVersionUID = 8628132333422887069L;

	public void initialize(Configuration configuration) {
		try {
			elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void onPostUpdate(PostUpdateEvent arg0) {
		if (arg0.getEntity() instanceof Records) {
			Records record = (Records) arg0.getEntity();
			if (!record.getAlredyProcessed()) {
				record.setAlredyProcessed(true);
				System.out.println("DBEventUpdateListener.onPostUpdate() record: " + record.getIdRecord());
				Directory directory = null;
				try {
					String oldTitle = record.getTitle();
					String newTitle = titleManager.buildTitle(record.getXMLReader(), record.getArchives().getIdArchive(), record.getIdRecord());
					if (!oldTitle.equals(newTitle)) {
						record.setTitle(newTitle);
						OpenDamsServiceProvider.getService().update(record);
					}
					if (elements_map.get(record.getArchives().getIdArchive()) == null)
						elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();

					if (indexConfiguration.isFsDirectory())
						directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + record.getArchives().getIdArchive() + "_" + indexConfiguration.getIndex_name()));
					else
						directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), record.getArchives().getIdArchive() + "_" + indexConfiguration.getIndex_name());
					Document doc = DocumentFactory.buildDocument(record, record.getArchives(), elements_map.get(record.getArchives().getIdArchive()));

					if (record.getDeleted() != null && record.getDeleted())
						indexConfiguration.getIndexManager(record.getArchives().getIdArchive()).deleteIndex(directory, record.getIdRecord().toString());
					else
						indexConfiguration.getIndexManager(record.getArchives().getIdArchive()).updateIndex(directory, doc, record.getIdRecord().toString(), elements_map.get(record.getArchives().getIdArchive()));

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
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						if (directory != null)
							directory.close();
					} catch (IOException e) {
					}
				}

				if (record.getArchives().getUse_default_index()) {
					try {
						if (indexConfiguration.isFsDirectory())
							directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + record.getArchives().getIdArchive() + "_" + indexConfiguration.getGeneric_index_name()));
						else
							directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), record.getArchives().getIdArchive() + "_" + indexConfiguration.getGeneric_index_name());
						Document doc = DocumentFactory.buildGenericdDocument(record, record.getArchives());
						indexConfiguration.getIndexManager(record.getArchives().getIdArchive()).updateIndex(directory, doc, record.getIdRecord().toString());
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
				List<String> endPointList = OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getAllowedEndPointList(record.getArchives().getIdArchive());
				if (endPointList != null) {
					for (int i = 0; i < endPointList.size(); i++) {
						System.out.println("DBEventUpdateListener.onPostUpdate() ENDPOINT " + endPointList.get(i));
						EndPointManager endPointManager = OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(endPointList.get(i));
						if (endPointManager.isAutoPublishingArchive(record.getArchives().getIdArchive())) {
							if (record.getDeleted() != null && record.getDeleted()) {
								try {
									endPointManager.removeEndPointObject(record.getIdRecord(), "rimozione",endPointList.get(i));
								} catch (Exception e) {
									System.out.println("IMPOSSIBILE PUBBLICARE SU ENDPOINT IL RECORD " + record.getIdRecord());
								}
							} else if (!record.isAlreadyPublished().contains("@" + endPointManager.getSparqlEndpointUri() + "->" + endPointManager.getDefaultDomain() + "@")) {
								try {
									endPointManager.publishRecord(record.getIdRecord(), "pubblicazione",endPointList.get(i));
								} catch (Exception e) {
									System.out.println("IMPOSSIBILE PUBBLICARE SU ENDPOINT IL RECORD " + record.getIdRecord());
								}
							}
						}
					}
				}
			} else {
				System.out.println("DBEventUpdateListener.onPostUpdate() ALREDY PROCESSED!!! " + record.getIdRecord());
			}
		}
	}
}
