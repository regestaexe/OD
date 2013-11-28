package com.openDams.admin.controller;


import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ArchiveStatusController implements Controller{
	private DataSource	dataSource = null;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		 ModelAndView mav = null;
		 Connection connection = null;
		 mav = new ModelAndView("admin/archiveStatus/archiveStatus");
		 if(arg0.getParameter("action").equals("online")){ 			
			 try {					
				 String idArchive = arg0.getParameter("idArchive");
				 connection = dataSource.getConnection();
				 setArchiveOnline(connection,idArchive);
				 connection.close();
			} catch (Exception e) {
				 e.printStackTrace();
				 if(!connection.isClosed())
					 connection.close();
			}    
		 }else if(arg0.getParameter("action").equals("offline")){
			 try {					
				 String idArchive = arg0.getParameter("idArchive");
				 connection = dataSource.getConnection();
				 setArchiveOffline(connection,idArchive);
				 connection.close();
			} catch (Exception e) {
				 e.printStackTrace();
				 if(!connection.isClosed())
					 connection.close();
			}   
		 }			
		return mav;	
	}
	private void setArchiveOffline(Connection connection,String idArchive){
    	String update = "Update archives set offline=1 where id_archive=" + idArchive + ";";
		
		try {
			Statement updateStatement = connection.createStatement();
			updateStatement.executeUpdate(update);
		} catch (Exception e) {
		}
    }
    private void setArchiveOnline(Connection connection,String idArchive){
    	String update = "Update archives set offline=0 where id_archive=" + idArchive + ";";		
		try {
			Statement updateStatement = connection.createStatement();
			updateStatement.executeUpdate(update);
		} catch (Exception e) {
		}
    }
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
