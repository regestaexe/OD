package com.openDams.db.event;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.jdbc.JdbcDirectory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.Initializable;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;

import com.openDams.bean.Records;
import com.openDams.bean.Relations;
import com.openDams.endpoint.managing.EndPointManager;
import com.openDams.endpoint.managing.OpenDamsEndPointManagerFactoryProvider;
import com.openDams.index.factory.DialectFactory;


public class DBEventDeleteListener extends DBEventListener implements Serializable,Initializable,PostDeleteEventListener{


	private static final long serialVersionUID = -4638389810690052022L;

	public void initialize(Configuration configuration) {
	}

	public void onPostDelete(PostDeleteEvent arg0) {
		if(arg0.getEntity() instanceof Records){
				Records record = (Records)arg0.getEntity();
				Directory directory = null;
				try {
					if(indexConfiguration.isFsDirectory())
						directory = FSDirectory.open(new File(indexConfiguration.getReal_path()+indexConfiguration.getIndex_location()+"/"+record.getArchives().getIdArchive()+"_"+indexConfiguration.getIndex_name()));	
					else
						directory = new JdbcDirectory(dataSource,DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), record.getArchives().getIdArchive()+"_"+indexConfiguration.getIndex_name());
					indexConfiguration.getIndexManager(record.getArchives().getIdArchive()).deleteIndex(directory, record.getIdRecord().toString());
					if(!indexConfiguration.isFsDirectory())
						((JdbcDirectory)directory).deleteMarkDeleted();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						if(directory!=null)
							directory.close();
					} catch (IOException e) {
					}
				}
				if(record.getArchives().getUse_default_index()){
					try {
						if(indexConfiguration.isFsDirectory())
							directory = FSDirectory.open(new File(indexConfiguration.getReal_path()+indexConfiguration.getIndex_location()+"/"+record.getArchives().getIdArchive()+"_"+indexConfiguration.getGeneric_index_name()));	
						else
							directory = new JdbcDirectory(dataSource,DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), record.getArchives().getIdArchive()+"_"+indexConfiguration.getGeneric_index_name());
						indexConfiguration.getIndexManager(record.getArchives().getIdArchive()).deleteIndex(directory, record.getIdRecord().toString());
						if(!indexConfiguration.isFsDirectory())
							((JdbcDirectory)directory).deleteMarkDeleted();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (CorruptIndexException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						try {
							if(directory!=null)
								directory.close();
						} catch (IOException e) {
						}
					}
				}
				List<String> endPointList =  OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getAllowedEndPointList(record.getArchives().getIdArchive());
				if(endPointList!=null){
					for (int i = 0; i < endPointList.size(); i++) {				
					EndPointManager endPointManager =  OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(endPointList.get(i));
					if(endPointManager.isAutoPublishingArchive(record.getArchives().getIdArchive())){
						try {
							endPointManager.removeEndPointObject(record.getIdRecord(),"rimozione",endPointList.get(i));
						} catch (Exception e) {
							System.out.println("IMPOSSIBILE DEPUBBLICARE SU ENDPOINT IL RECORD "+record.getIdRecord());
						}
					}
					}
				}
			}else if(arg0.getEntity() instanceof Relations){
				Relations relations = (Relations)arg0.getEntity();
				if(relations.isAlredyProcessed()==false){
					if(relations.isAsynctask()){
						relations.setAlredyProcessed(true);
						try {
							System.out.println("DBEventDeleteListener.onPostDelete() relations: " + relations.getId().getRefIdRecord1() + " -> " + relations.getId().getRefIdRecord2() + " (tipo: " + relations.getId().getRefIdRelationType() + ")");
							relationsManager.removeXmlRelation(relations.getId().getRefIdRelationType(),relations.getId().getRefIdRecord1(),relations.getId().getRefIdRecord2());
						} catch (Exception e) {
							System.out.println("################################ERRORE RELAZIONE POST DELETE##############################");
						}
					}else{
						relations.setAlredyProcessed(true);
						System.out.println("DBEventDeleteListener.onPostDelete() RELAZIONE GESTITA IN MANIERA SINCRONA !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					}
				}
			}
	}
}
