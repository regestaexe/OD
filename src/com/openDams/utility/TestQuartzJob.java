package com.openDams.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 * classe per i TEST su quartz
 */
public class TestQuartzJob {

	private TimerLog log;
	private String logFile;
	
	public TestQuartzJob() {}
	
	public void setLogFile(String logFile){
		this.logFile = logFile;
	}
	
	public TestQuartzJob(String logFile) throws IOException {
		log = new TimerLog();
		this.logFile = logFile;
		log("AVVIO"+log.toString());
	}
	
	public void log(String propertyValue) throws IOException{
		FileWriter fos = new FileWriter(new File(logFile), true);
		fos.write(log.toString().toCharArray());
		fos.write(propertyValue.toCharArray());
		fos.close();
	}
	
	public static void main(String[] args) throws IOException{ }
	
}
