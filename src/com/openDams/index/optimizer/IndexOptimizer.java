package com.openDams.index.optimizer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.jdbc.JdbcDirectory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.openDams.bean.Archives;
import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.index.factory.DialectFactory;
import com.openDams.services.OpenDamsService;


public class IndexOptimizer extends QuartzJobBean{
	private static final long serialVersionUID = 1L;
	private OpenDamsService service ;
	private DataSource	dataSource = null;
	private IndexConfiguration indexConfiguration = null;
	
	public IndexOptimizer() {
	}
	@SuppressWarnings("unchecked")
	public void optimizeIndexes(){
		List<Archives> list = (List<Archives>) service.getList(Archives.class);
		for (int i = 0; i < list.size(); i++) {
			Archives archives = list.get(i);		
			System.out.println(archives.getIdArchive()+"_"+indexConfiguration.getIndex_name());
			Directory directory = null;
			try{
				if(indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path()+indexConfiguration.getIndex_location()+"/"+archives.getIdArchive()+"_"+indexConfiguration.getIndex_name()));	
				else
					directory = new JdbcDirectory(dataSource,DialectFactory.getDialect(indexConfiguration.getjDBCDialect()),archives.getIdArchive()+"_"+indexConfiguration.getIndex_name());	
				indexConfiguration.getIndexManager(archives.getIdArchive()).optimizeIndex(directory);
				if(!indexConfiguration.isFsDirectory())
					((JdbcDirectory)directory).deleteMarkDeleted();
				System.out.println("Ottimizzazione indice "+archives.getIdArchive()+"_"+indexConfiguration.getIndex_name()+" avvenuta con successo");
			}catch (Exception e) {
                System.out.println("impossibile ottimizzare l'indice "+archives.getIdArchive()+"_"+indexConfiguration.getIndex_name());
			}finally{
				try {
					if(directory!=null)
						directory.close();
				} catch (IOException e) {
				}
			}
			try{
				if(indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path()+indexConfiguration.getIndex_location()+"/"+archives.getIdArchive()+"_"+indexConfiguration.getGeneric_index_name()));	
				else
					directory = new JdbcDirectory(dataSource,DialectFactory.getDialect(indexConfiguration.getjDBCDialect()),archives.getIdArchive()+"_"+indexConfiguration.getGeneric_index_name());	
				indexConfiguration.getIndexManager(archives.getIdArchive()).optimizeIndex(directory);
				if(!indexConfiguration.isFsDirectory())
					((JdbcDirectory)directory).deleteMarkDeleted();
				System.out.println("Ottimizzazione indice "+archives.getIdArchive()+"_"+indexConfiguration.getGeneric_index_name()+" avvenuta con successo");
			}catch (Exception e) {
                System.out.println("impossibile ottimizzare l'indice "+archives.getIdArchive()+"_"+indexConfiguration.getGeneric_index_name());
			}finally{
				try {
					if(directory!=null)
						directory.close();
				} catch (IOException e) {
				}
			}
		}
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("SONO PARTITO executeInternal!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
}
