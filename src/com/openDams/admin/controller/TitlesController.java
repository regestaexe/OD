package com.openDams.admin.controller;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.admin.tasks.JobType;

public class TitlesController implements Controller{
	private JobLauncher jobLauncher;
	private Job rebuildTitlesJob;
	private BasicDataSource dataSource;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		 ModelAndView mav = null;
		 Connection connection = null;
		 Statement statement = null;
		 ResultSet resultSet = null;
		 if(arg0.getParameter("action").equals("rebuild_title")){ 
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
					 jobParametersBuilder.addLong("jobType",JobType.REBUILD_TITLE);
					 JobExecution jobExecution = jobLauncher.run(rebuildTitlesJob, jobParametersBuilder.toJobParameters());
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
		 }else if(arg0.getParameter("action").equals("check_status")){
			 mav = new ModelAndView("admin/index_managment/indexStatus");
			 try {					
				 String idArchive = arg0.getParameter("idArchive");
				 connection = dataSource.getConnection();
				 statement = connection.createStatement();					 
				 resultSet = statement.executeQuery("SELECT STATUS FROM BATCH_JOB_EXECUTION where BATCH_JOB_EXECUTION.JOB_EXECUTION_ID=(select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where JOB_INSTANCE_ID = (select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where BATCH_JOB_PARAMS.KEY_NAME='idArchive' and BATCH_JOB_PARAMS.STRING_VAL='"+idArchive+"') and JOB_INSTANCE_ID=(select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where BATCH_JOB_PARAMS.KEY_NAME='jobType' and BATCH_JOB_PARAMS.LONG_VAL="+JobType.REBUILD_TITLE+"));");	 
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
			 
		 }			
		return mav;	
	}
	public void setJobLauncher(JobLauncher jobLauncher) {
		this.jobLauncher = jobLauncher;
	}
	public void setRebuildTitlesJob(Job rebuildTitlesJob) {
		this.rebuildTitlesJob = rebuildTitlesJob;
	}
	public void setDataSource(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}
}
