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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.admin.tasks.JobType;
import com.openDams.security.UserDetails;

public class EndPointControllerWorkspace implements Controller{
	private JobLauncher jobLauncher;
	private Job endPointPublishJob;
	private Job endPointDePublishJob;
	private BasicDataSource dataSource;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		 ModelAndView mav = null;
		 Connection connection = null;
		 Statement statement = null;
		 ResultSet resultSet = null;
		 UserDetails user =  (UserDetails)((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
		 String userName = user.getUsername();
		 if(arg0.getParameter("action").equals("publishEndPoint")){ 
			 mav = new ModelAndView("admin/index_managment/indexStatusWorkspace");
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
					 jobParametersBuilder.addString("endPointManager", arg0.getParameter("endPointManager"));
					 jobParametersBuilder.addString("publishEndPoint","publishEndPoint");
					 jobParametersBuilder.addString("userName", userName);
					 jobParametersBuilder.addDate("date", new Date());
					 jobParametersBuilder.addLong("jobType",JobType.ENDPOINT_PUBLISHING);
					 JobExecution jobExecution = jobLauncher.run(endPointPublishJob, jobParametersBuilder.toJobParameters());
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
		 }else if(arg0.getParameter("action").equals("rePublishEndPoint")){ 
			 mav = new ModelAndView("admin/index_managment/indexStatusWorkspace");
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
					 jobParametersBuilder.addString("endPointManager", arg0.getParameter("endPointManager"));
					 jobParametersBuilder.addString("republish","republish");
					 jobParametersBuilder.addString("userName", userName);
					 jobParametersBuilder.addDate("date", new Date());
					 jobParametersBuilder.addLong("jobType",JobType.ENDPOINT_PUBLISHING);
					 JobExecution jobExecution = jobLauncher.run(endPointPublishJob, jobParametersBuilder.toJobParameters());
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
		 }else if(arg0.getParameter("action").equals("notPublished")){ 
			 mav = new ModelAndView("admin/index_managment/indexStatusWorkspace");
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
					 jobParametersBuilder.addString("endPointManager", arg0.getParameter("endPointManager"));
					 jobParametersBuilder.addString("notPublished","notPublished");
					 jobParametersBuilder.addString("userName", userName);
					 jobParametersBuilder.addDate("date", new Date());
					 jobParametersBuilder.addLong("jobType",JobType.ENDPOINT_PUBLISHING);
					 JobExecution jobExecution = jobLauncher.run(endPointPublishJob, jobParametersBuilder.toJobParameters());
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
		 }else if(arg0.getParameter("action").equals("modified")){ 
			 mav = new ModelAndView("admin/index_managment/indexStatusWorkspace");
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
					 jobParametersBuilder.addString("endPointManager", arg0.getParameter("endPointManager"));
					 jobParametersBuilder.addString("modified","modified");
					 jobParametersBuilder.addString("userName", userName);
					 jobParametersBuilder.addDate("date", new Date());
					 jobParametersBuilder.addLong("jobType",JobType.ENDPOINT_PUBLISHING);
					 JobExecution jobExecution = jobLauncher.run(endPointPublishJob, jobParametersBuilder.toJobParameters());
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
		 }else if(arg0.getParameter("action").equals("errors")){ 
			 mav = new ModelAndView("admin/index_managment/indexStatusWorkspace");
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
					 jobParametersBuilder.addString("endPointManager", arg0.getParameter("endPointManager"));
					 jobParametersBuilder.addString("errors","errors");
					 jobParametersBuilder.addString("userName", userName);
					 jobParametersBuilder.addDate("date", new Date());
					 jobParametersBuilder.addLong("jobType",JobType.ENDPOINT_PUBLISHING);
					 JobExecution jobExecution = jobLauncher.run(endPointPublishJob, jobParametersBuilder.toJobParameters());
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
		 }else if(arg0.getParameter("action").equals("archiveRemove")){
			 mav = new ModelAndView("admin/index_managment/indexStatusWorkspace");
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
					 jobParametersBuilder.addString("endPointManager", arg0.getParameter("endPointManager"));
					 jobParametersBuilder.addString("userName", userName);
					 jobParametersBuilder.addDate("date", new Date());
					 jobParametersBuilder.addLong("jobType",JobType.ENDPOINT_DEPUBLISHING);
					 JobExecution jobExecution = jobLauncher.run(endPointDePublishJob, jobParametersBuilder.toJobParameters());
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
			 mav = new ModelAndView("admin/index_managment/indexStatusWorkspace");
			 try {					
				 String idArchive = arg0.getParameter("idArchive");
				 connection = dataSource.getConnection();
				 statement = connection.createStatement();					 
				 resultSet = statement.executeQuery("SELECT STATUS FROM BATCH_JOB_EXECUTION where BATCH_JOB_EXECUTION.JOB_EXECUTION_ID=(select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where JOB_INSTANCE_ID = (select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where BATCH_JOB_PARAMS.KEY_NAME='idArchive' and BATCH_JOB_PARAMS.STRING_VAL='"+idArchive+"') and JOB_INSTANCE_ID=(select max(JOB_INSTANCE_ID) from BATCH_JOB_PARAMS where BATCH_JOB_PARAMS.KEY_NAME='jobType' and (BATCH_JOB_PARAMS.LONG_VAL="+JobType.ENDPOINT_PUBLISHING+" OR BATCH_JOB_PARAMS.LONG_VAL="+JobType.ENDPOINT_DEPUBLISHING+")));");	 
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
	public void setEndPointPublishJob(Job endPointPublishJob) {
		this.endPointPublishJob = endPointPublishJob;
	}
	public void setDataSource(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}
	public void setEndPointDePublishJob(Job endPointDePublishJob) {
		this.endPointDePublishJob = endPointDePublishJob;
	}
}
