package com.openDams.admin.tasks;

public class JobDetails {
	
	private String JOB_EXECUTION_ID = "";
	private String VERSION = "";
	private String JOB_INSTANCE_ID = "";
	private String CREATE_TIME = "";
	private String START_TIME = "";
	private String END_TIME = "";
	private String STATUS = "";
	 
	public JobDetails(){}

	public String getJOB_EXECUTION_ID() {
		return JOB_EXECUTION_ID;
	}

	public String getVERSION() {
		return VERSION;
	}

	public String getJOB_INSTANCE_ID() {
		return JOB_INSTANCE_ID;
	}

	public String getCREATE_TIME() {
		return CREATE_TIME;
	}

	public String getSTART_TIME() {
		return START_TIME;
	}

	public String getEND_TIME() {
		return END_TIME;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setJOB_EXECUTION_ID(String jOBEXECUTIONID) {
		JOB_EXECUTION_ID = jOBEXECUTIONID;
	}

	public void setVERSION(String vERSION) {
		VERSION = vERSION;
	}

	public void setJOB_INSTANCE_ID(String jOBINSTANCEID) {
		JOB_INSTANCE_ID = jOBINSTANCEID;
	}

	public void setCREATE_TIME(String cREATETIME) {
		CREATE_TIME = cREATETIME;
	}

	public void setSTART_TIME(String sTARTTIME) {
		START_TIME = sTARTTIME;
	}

	public void setEND_TIME(String eNDTIME) {
		END_TIME = eNDTIME;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
}
