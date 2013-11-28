package com.openDams.db.event;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.jdbc.JdbcDirectory;
import org.dom4j.DocumentException;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.Initializable;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;

import com.openDams.bean.Records;
import com.openDams.bean.Relations;
import com.openDams.configuration.ConfigurationException;
import com.openDams.endpoint.managing.EndPointManager;
import com.openDams.endpoint.managing.OpenDamsEndPointManagerFactoryProvider;
import com.openDams.index.factory.DialectFactory;
import com.openDams.index.factory.DocumentFactory;

public class DBEventInsertListener extends DBEventListener implements Serializable, Initializable, PostInsertEventListener {

	private static final long serialVersionUID = 8628132333422887069L;

	public void initialize(Configuration configuration) {
		try {
			elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void onPostInsert(PostInsertEvent arg0) {
		if (arg0.getEntity() instanceof Records) {

			Records record = (Records) arg0.getEntity();

			if (!record.getAlredyProcessed()) {
				record.setAlredyProcessed(true);
				System.out.println("DBEventUpdateListener.onPostInsert() record: " + record.getIdRecord());

				Directory directory = null;
				Document doc = null;
				try {
					String oldTitle = record.getTitle();
					String newTitle = titleManager.buildTitle(record.getXMLReader(), record.getArchives().getIdArchive(), record.getIdRecord());
					if (!oldTitle.equals(newTitle)) {
						record.setTitle(newTitle);
						// OpenDamsServiceProvider.getService().update(record);
					}
					if (elements_map.get(record.getArchives().getIdArchive()) == null)
						elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();
					doc = DocumentFactory.buildDocument(record, record.getArchives(), elements_map.get(record.getArchives().getIdArchive()));
					if (indexConfiguration.isFsDirectory())
						directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + record.getArchives().getIdArchive() + "_" + indexConfiguration.getIndex_name()));
					else
						directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), record.getArchives().getIdArchive() + "_" + indexConfiguration.getIndex_name());
					indexConfiguration.getIndexManager(record.getArchives().getIdArchive()).writeIndex(directory, doc);
					if (!indexConfiguration.isFsDirectory())
						((JdbcDirectory) directory).deleteMarkDeleted();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (LockObtainFailedException e) {
					try {
						if (doc == null)
							doc = DocumentFactory.buildDocument(record, record.getArchives(), elements_map.get(record.getArchives().getIdArchive()));
						indexConfiguration.getIndexManager(record.getArchives().getIdArchive()).createIndex(directory);
						indexConfiguration.getIndexManager(record.getArchives().getIdArchive()).writeIndex(directory, doc);
						if (!indexConfiguration.isFsDirectory())
							((JdbcDirectory) directory).deleteMarkDeleted();
					} catch (IOException ex) {
						ex.printStackTrace();
					} catch (DocumentException ex) {
						e.printStackTrace();
					} catch (ClassNotFoundException ex) {
						e.printStackTrace();
					} catch (InstantiationException ex) {
						e.printStackTrace();
					} catch (IllegalAccessException ex) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
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
						doc = DocumentFactory.buildGenericdDocument(record, record.getArchives());
						if (indexConfiguration.isFsDirectory())
							directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + record.getArchives().getIdArchive() + "_" + indexConfiguration.getGeneric_index_name()));
						else
							directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), record.getArchives().getIdArchive() + "_" + indexConfiguration.getGeneric_index_name());
						indexConfiguration.getIndexManager(record.getArchives().getIdArchive()).writeIndex(directory, doc);
						if (!indexConfiguration.isFsDirectory())
							((JdbcDirectory) directory).deleteMarkDeleted();
					} catch (CorruptIndexException e) {
						e.printStackTrace();
					} catch (LockObtainFailedException e) {
						try {
							if (doc == null)
								doc = DocumentFactory.buildGenericdDocument(record, record.getArchives());
							indexConfiguration.getIndexManager(record.getArchives().getIdArchive()).createIndex(directory);
							indexConfiguration.getIndexManager(record.getArchives().getIdArchive()).writeIndex(directory, doc);
							if (!indexConfiguration.isFsDirectory())
								((JdbcDirectory) directory).deleteMarkDeleted();
						} catch (IOException ex) {
							ex.printStackTrace();
						} catch (DocumentException ex) {
							e.printStackTrace();
						} catch (ClassNotFoundException ex) {
							e.printStackTrace();
						} catch (InstantiationException ex) {
							e.printStackTrace();
						} catch (IllegalAccessException ex) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
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
						EndPointManager endPointManager = OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(endPointList.get(i));
						if (endPointManager.isAutoPublishingArchive(record.getArchives().getIdArchive())) {
							try {
								endPointManager.publishRecord(record.getIdRecord(), "pubblicazione",endPointList.get(i));
							} catch (Exception e) {
								System.out.println("IMPOSSIBILE PUBBLICARE SU ENDPOINT IL RECORD " + record.getIdRecord());
							}
						}
					}
				}
			} else {
				System.out.println("DBEventUpdateListener.onPostInsert() ALREDY PROCESSED!!! " + record.getIdRecord());
			}
		}else if (arg0.getEntity() instanceof Relations) {
			Relations relations = (Relations) arg0.getEntity();
			if(relations.isAlredyProcessed()==false){
				if(relations.isAsynctask()){
					relations.setAlredyProcessed(true);
					try {
						System.out.println("DBEventInsertListener.onPostInsert() relations: " + relations.getId().getRefIdRecord1() + " -> " + relations.getId().getRefIdRecord2() + " (tipo: " + relations.getId().getRefIdRelationType() + ")");
						relationsManager.insertXmlRelation(relations.getId().getRefIdRelationType(), relations.getId().getRefIdRecord1(), relations.getId().getRefIdRecord2());
					} catch (Exception e) {
						System.out.println("################################ERRORE RELAZIONE POST INSERT##############################");
					}
					
				}else{
					relations.setAlredyProcessed(true);
					System.out.println("DBEventInsertListener.onPostInsert() RELAZIONE GESTITA IN MANIERA SINCRONA!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}
			}
		}
	}
}
