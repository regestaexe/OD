package com.openDams.admin.controller;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.jdbc.JdbcDirectory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.admin.tasks.JobDetails;
import com.openDams.admin.tasks.JobType;
import com.openDams.bean.ArchiveIdentity;
import com.openDams.bean.Archives;
import com.openDams.endpoint.managing.OpenDamsEndPointManagerFactoryProvider;
import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.index.configuration.IndexInfo;
import com.openDams.index.factory.DialectFactory;
import com.openDams.index.factory.LuceneFactory;
import com.openDams.security.RoleTester;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;

public class IndexController implements Controller,ServletContextAware{
	private OpenDamsService service ;
	private IndexConfiguration indexConfiguration ;
	private String analyzerClass = null;
	private JobLauncher jobLauncher;
	private Job rebuildIndexJob;
	private BasicDataSource dataSource;
	private ServletContext servletContext;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if(arg0.getParameter("action")!=null){
			 Connection connection = null;
			 Statement statement = null;
			 ResultSet resultSet = null;
			 if(arg0.getParameter("action").equals("rebuild_index")){ 
				 mav = new ModelAndView("admin/index_managment/indexStatus");
				 try {					
					 String idArchive = arg0.getParameter("idArchive");
					 connection = dataSource.getConnection();
					 statement = connection.createStatement();
					 resultSet = statement.executeQuery("SELECT JOB_EXECUTION_ID FROM BATCH_JOB_EXECUTION where BATCH_JOB_EXECUTION.STATUS='STARTED' and BATCH_JOB_EXECUTION.JOB_EXECUTION_ID=(select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where BATCH_JOB_PARAMS.KEY_NAME='idArchive' and BATCH_JOB_PARAMS.STRING_VAL='"+idArchive+"');");					 					 
					 if(resultSet.next()){
						 mav.addObject("STATUS", "STARTED");
					 }else{
						 JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
						 jobParametersBuilder.addString("idArchive", idArchive);
						 jobParametersBuilder.addDate("date", new Date());
						 jobParametersBuilder.addLong("jobType",JobType.REBUILD_INDEX);
						 JobExecution jobExecution = jobLauncher.run(rebuildIndexJob, jobParametersBuilder.toJobParameters());
						 mav.addObject("STATUS", jobExecution.getStatus());
					 }
					 resultSet.close();
					 statement.close();
					 connection.close();
				} catch (Exception e) {
					 e.printStackTrace();
					 if(!connection.isClosed())
						 connection.close();
				}    
			 }else if(arg0.getParameter("action").equals("index_details")){
				 mav = new ModelAndView("admin/index_managment/indexDetails");
				 try {					
					 String idArchive = arg0.getParameter("idArchive");		
					 IndexInfo indexInfo = getIndexInfo(false, idArchive);
					
					 String path="";
					 if(!indexConfiguration.isUse_external_conf_location()){
						path+=servletContext.getRealPath("");
					 }
					 path+=indexConfiguration.getConfiguration_location();
					 indexInfo.setXmlIndex(readXmlConfiguration(path+"/"+idArchive+"/"+"index_configuration.xml"));
					 indexInfo.setXmlTitle(readXmlConfiguration(path+"/"+idArchive+"/"+"title_configuration.xml"));
					 indexInfo.setNumDocsArchive(service.getCountFromSQL("SELECT count(id_record) as tot FROM records where ref_id_archive="+idArchive+" and (deleted<>1 OR deleted is NULL);").intValue());
					 indexInfo.setNumDocsBasket(service.getCountFromSQL("SELECT count(id_record) as tot FROM records where ref_id_archive="+idArchive+" and deleted=1;").intValue());
					 mav.addObject("indexInfo", indexInfo);
					 connection = dataSource.getConnection();
					 statement = connection.createStatement();
					 //resultSet = statement.executeQuery("SELECT STATUS FROM BATCH_JOB_EXECUTION where BATCH_JOB_EXECUTION.JOB_EXECUTION_ID=(select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where JOB_INSTANCE_ID in (select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where BATCH_JOB_PARAMS.KEY_NAME='idArchive' and BATCH_JOB_PARAMS.STRING_VAL='"+idArchive+"'));");
					 resultSet = statement.executeQuery("SELECT STATUS FROM BATCH_JOB_EXECUTION where BATCH_JOB_EXECUTION.JOB_EXECUTION_ID=(select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where BATCH_JOB_PARAMS.KEY_NAME='idArchive' and BATCH_JOB_PARAMS.STRING_VAL='"+idArchive+"');");					
					 if(resultSet.next()){
						 String STATUS = resultSet.getString("STATUS");
						 if(STATUS!=null && STATUS.equals("STARTED"))
							 mav.addObject("STATUS", "STARTED");
						 else
							 mav.addObject("STATUS", "COMPLETED");
					 }else{
						 mav.addObject("STATUS", "COMPLETED");
					 }
					 resultSet.close();
					 statement.close();
					 connection.close();
					 List<String> endPointList =  OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getAllowedEndPointList(new Integer(idArchive));
					 mav.addObject("endPointList",endPointList );
				} catch (Exception e) {
					 e.printStackTrace();
					 if(!connection.isClosed())
						 connection.close();
				}   
				mav.addObject("archives", service.getObject(Archives.class, new Integer(arg0.getParameter("idArchive"))));
			 }else if(arg0.getParameter("action").equals("check_status")){
				 mav = new ModelAndView("admin/index_managment/indexStatus");
				 try {					
					 String idArchive = arg0.getParameter("idArchive");
					 connection = dataSource.getConnection();
					 statement = connection.createStatement();					 
					 resultSet = statement.executeQuery("SELECT STATUS FROM BATCH_JOB_EXECUTION where BATCH_JOB_EXECUTION.JOB_EXECUTION_ID=(select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where JOB_INSTANCE_ID = (select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where BATCH_JOB_PARAMS.KEY_NAME='idArchive' and BATCH_JOB_PARAMS.STRING_VAL='"+idArchive+"') and JOB_INSTANCE_ID=(select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where BATCH_JOB_PARAMS.KEY_NAME='jobType' and BATCH_JOB_PARAMS.LONG_VAL="+JobType.REBUILD_INDEX+"));");					 
					 if(resultSet.next()){
						 String STATUS = resultSet.getString("STATUS");
						 if(STATUS!=null && !STATUS.equals("null"))
							 mav.addObject("STATUS", resultSet.getString("STATUS"));
						 else
							 mav.addObject("STATUS", "COMPLETED");
					 }else{
						 mav.addObject("STATUS", "COMPLETED");
					 }
					 resultSet.close();
					 statement.close();
					 connection.close();
				} catch (Exception e) {
					 e.printStackTrace();
					 if(!connection.isClosed())
						 connection.close();
				}   
				 
			 }else if(arg0.getParameter("action").equals("history")){
				 try {					
					 String idArchive = arg0.getParameter("idArchive");
					 connection = dataSource.getConnection();
					 statement = connection.createStatement();					 
					 resultSet = statement.executeQuery("SELECT BATCH_JOB_EXECUTION.*,BATCH_JOB_PARAMS.LONG_VAL FROM BATCH_JOB_EXECUTION inner join BATCH_JOB_PARAMS on BATCH_JOB_EXECUTION.JOB_EXECUTION_ID=BATCH_JOB_PARAMS.JOB_INSTANCE_ID where BATCH_JOB_PARAMS.LONG_VAL="+JobType.REBUILD_INDEX+" and BATCH_JOB_PARAMS.KEY_NAME='jobType' and BATCH_JOB_EXECUTION.JOB_EXECUTION_ID in (SELECT JOB_INSTANCE_ID from BATCH_JOB_PARAMS where BATCH_JOB_PARAMS.KEY_NAME='idArchive' and BATCH_JOB_PARAMS.STRING_VAL='"+idArchive+"');");
					 ArrayList<JobDetails> rebuildIndexList = new ArrayList<JobDetails>();
					 while(resultSet.next()){
						 String JOB_EXECUTION_ID = resultSet.getString("JOB_EXECUTION_ID");
						 String VERSION = resultSet.getString("VERSION");
						 String JOB_INSTANCE_ID = resultSet.getString("JOB_INSTANCE_ID");
						 String CREATE_TIME = resultSet.getString("CREATE_TIME");
						 String START_TIME = resultSet.getString("START_TIME");
						 String END_TIME = resultSet.getString("END_TIME");
						 String STATUS = resultSet.getString("STATUS");
						 JobDetails jobDetails = new JobDetails();
						 jobDetails.setCREATE_TIME(CREATE_TIME);
						 jobDetails.setEND_TIME(END_TIME);
						 jobDetails.setJOB_EXECUTION_ID(JOB_EXECUTION_ID);
						 jobDetails.setJOB_INSTANCE_ID(JOB_INSTANCE_ID);
						 jobDetails.setSTART_TIME(START_TIME);
						 jobDetails.setSTATUS(STATUS);
						 jobDetails.setVERSION(VERSION);
						 rebuildIndexList.add(jobDetails);
					 }
					 resultSet.close();
					 statement.close();
					 statement = connection.createStatement();					 
					 resultSet = statement.executeQuery("SELECT BATCH_JOB_EXECUTION.*,BATCH_JOB_PARAMS.LONG_VAL FROM BATCH_JOB_EXECUTION inner join BATCH_JOB_PARAMS on BATCH_JOB_EXECUTION.JOB_EXECUTION_ID=BATCH_JOB_PARAMS.JOB_INSTANCE_ID where BATCH_JOB_PARAMS.LONG_VAL="+JobType.REBUILD_TITLE+" and BATCH_JOB_PARAMS.KEY_NAME='jobType' and BATCH_JOB_EXECUTION.JOB_EXECUTION_ID in (SELECT JOB_INSTANCE_ID from BATCH_JOB_PARAMS where BATCH_JOB_PARAMS.KEY_NAME='idArchive' and BATCH_JOB_PARAMS.STRING_VAL='"+idArchive+"');");
					 ArrayList<JobDetails> rebuildTitleList = new ArrayList<JobDetails>();
					 while(resultSet.next()){
						 String JOB_EXECUTION_ID = resultSet.getString("JOB_EXECUTION_ID");
						 String VERSION = resultSet.getString("VERSION");
						 String JOB_INSTANCE_ID = resultSet.getString("JOB_INSTANCE_ID");
						 String CREATE_TIME = resultSet.getString("CREATE_TIME");
						 String START_TIME = resultSet.getString("START_TIME");
						 String END_TIME = resultSet.getString("END_TIME");
						 String STATUS = resultSet.getString("STATUS");
						 JobDetails jobDetails = new JobDetails();
						 jobDetails.setCREATE_TIME(CREATE_TIME);
						 jobDetails.setEND_TIME(END_TIME);
						 jobDetails.setJOB_EXECUTION_ID(JOB_EXECUTION_ID);
						 jobDetails.setJOB_INSTANCE_ID(JOB_INSTANCE_ID);
						 jobDetails.setSTART_TIME(START_TIME);
						 jobDetails.setSTATUS(STATUS);
						 jobDetails.setVERSION(VERSION);
						 rebuildTitleList.add(jobDetails);
						 
					 }
					 resultSet.close();
					 statement.close();					 
					 connection.close();
					 mav = new ModelAndView("admin/index_managment/history");
					 mav.addObject("rebuildIndexList", rebuildIndexList);
					 mav.addObject("rebuildTitleList", rebuildTitleList);
				} catch (Exception e) {
					 e.printStackTrace();
					 if(!connection.isClosed())
						 connection.close();
				}   
			 }else if(arg0.getParameter("action").equals("save_xml")){
				 mav = new ModelAndView("admin/index_managment/save");
				 String idArchive = arg0.getParameter("idArchive");		
				 String path="";
				 if(!indexConfiguration.isUse_external_conf_location()){
					path+=servletContext.getRealPath("");
				 }
				 path+=indexConfiguration.getConfiguration_location();
				 if(arg0.getParameter("xml_type").equals("index")){
					 writeXmlConfiguration(path+"/"+idArchive+"/"+"index_configuration.xml",URLDecoder.decode(arg0.getParameter("xml"), "UTF-8"));
				 }else{
					 writeXmlConfiguration(path+"/"+idArchive+"/"+"index_configuration.xml",URLDecoder.decode(arg0.getParameter("xml"), "UTF-8"));
				 }
				 
			 }
		}else{
			mav = new ModelAndView("admin/index_managment/indexList");
			UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();	
			if(user.getIdCompany()==RoleTester.SYSTEM_ADMIN_GOD){
				mav.addObject("archiveIndexList", service.getListFromSQL(Archives.class,"SELECT * FROM archives where not ref_id_archive_identity="+ArchiveIdentity.APPLICATION+" order by id_archive;"));
			}else{		
				mav.addObject("archiveIndexList", service.getListFromSQL(Archives.class,"SELECT archives.* FROM archives inner join company_archives on archives.id_archive=company_archives.ref_id_archive where not archives.ref_id_archive_identity="+ArchiveIdentity.APPLICATION+" and company_archives.ref_id_company="+user.getIdCompany()+" order by archives.id_archive;"));				
			}	
			
		}
		return mav;	
	}
	private IndexInfo getIndexInfo(boolean generic,String archive) throws IllegalAccessException, IOException, ClassNotFoundException, InstantiationException{
		Directory directory = null;
		try{
			if (generic) {
				try {
					if (indexConfiguration.isFsDirectory())
						directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getGeneric_index_name()));
					else
						directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getGeneric_index_name());
					if(IndexWriter.isLocked(directory)){
						directory.getLockFactory().clearLock("write.lock");
						IndexWriter.unlock(directory);
					}
				} catch (Exception e) {
					throw new IllegalAccessException("Indice generico non configurato");
				}
			} else {
				if (indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + archive + "_" + indexConfiguration.getIndex_name()));
				else
					directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), archive + "_" + indexConfiguration.getIndex_name());
				if(IndexWriter.isLocked(directory)){
					directory.deleteFile("write.lock");
					IndexWriter.unlock(directory);
				}
			}
			return new IndexInfo(archive + "_" + indexConfiguration.getIndex_name(),directory,LuceneFactory.getAnalyzer(analyzerClass));
			
		} catch (CorruptIndexException e) {			
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IOException e) {			
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (ClassNotFoundException e) {			
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (InstantiationException e) {			
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		} catch (IllegalAccessException e) {			
			if (directory != null) {
				directory.close();
				directory = null;
			}
			throw e;
		}
	}
	private String readXmlConfiguration(String fileName) throws IOException{
		String xml = "";
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			File file = new File(fileName);
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			while (bufferedReader.ready()) {
				xml+=bufferedReader.readLine()+"\r\n";
			}
			bufferedReader.close();
			fileReader.close();
		} catch (IOException e) {
			throw e;
		}
		return xml;
	}
	private void writeXmlConfiguration(String fileName,String xml) throws IOException{
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			File file = new File(fileName);
			fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(xml);
			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			throw e;
		}
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public IndexConfiguration getIndexConfiguration() {
		return indexConfiguration;
	}
	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}
	public void setAnalyzerClass(String analyzerClass) {
		this.analyzerClass = analyzerClass;
	}
	public void setJobLauncher(JobLauncher jobLauncher) {
		this.jobLauncher = jobLauncher;
	}
	public void setRebuildIndexJob(Job rebuildIndexJob) {
		this.rebuildIndexJob = rebuildIndexJob;
	}
	public void setDataSource(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}
	public void setServletContext(ServletContext arg0) {
 		servletContext = arg0;
 	}
}
