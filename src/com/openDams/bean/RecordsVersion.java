package com.openDams.bean;

import java.util.Date;



// Generated 1-giu-2010 18.15.48 by Hibernate Tools 3.2.4.GA

public class RecordsVersion implements java.io.Serializable {

	private static final long serialVersionUID = 7296454235896868816L;
	private Integer idVersion;
	private int version;
	private Records records;
	private String title;
	private byte[] xml;
	private Date versionDate;

	public RecordsVersion() {
	}

	public RecordsVersion(Records records, String title, byte[] xml,
			Date versionDate) {
		this.records = records;
		this.title = title;
		this.xml = xml;
		this.versionDate = versionDate;
	}

	public Integer getIdVersion() {
		return this.idVersion;
	}

	public void setIdVersion(Integer idVersion) {
		this.idVersion = idVersion;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Records getRecords() {
		return this.records;
	}

	public void setRecords(Records records) {
		this.records = records;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public byte[] getXml() {
		return this.xml;
	}

	public void setXml(byte[] xml) {
		this.xml = xml;
	}

	public Date getVersionDate() {
		return this.versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

}