package com.openDams.bean;

// Generated 18-dic-2009 11.35.39 by Hibernate Tools 3.2.4.GA

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.DocumentException;

import com.regesta.framework.xml.XMLReader;

/**
 * Records generated by hbm2java
 */
public class Records extends Record implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer idRecord;
	private RecordTypes recordTypes;
	private Archives archives;
	private String title = "";
	private byte[] xml;
	private Date creationDate;
	private Date modifyDate;
	private String phisicalPath;
	private Integer position;
	private Integer depth;
	private String xmlId;
	private Boolean deleted;
	private Boolean alredyProcessed = false;

	private String alreadyPublished="";
	private Integer totChildren = 0;
	private Set<RecordsVersion> recordsVersions = new HashSet<RecordsVersion>(0);
	private Set<Relations> relationsesForRefIdRecord1 = new HashSet<Relations>(0);
	private Set<Relations> relationsesForRefIdRecord2 = new HashSet<Relations>(0);
	private Set<Notes> noteses = new HashSet<Notes>(0);
	private HashMap<String, ArrayList<String>> xpatsMap = null;
	private XMLReader xmlReader = null;
	private HashMap<String, ArrayList<String>> externalcontentsMap = null;

	public Records() {
		super();
	}

	public Records(RecordTypes recordTypes, Archives archives) {
		this.recordTypes = recordTypes;
		this.archives = archives;
	}

	public Records(int idRecord, RecordTypes recordTypes, Archives archives, String title, byte[] xml, Date creationDate, Date modifyDate, String phisicalPath, int position, int depth, Boolean deleted, Set<RecordsVersion> recordsVersions, Set<Relations> relationsesForRefIdRecord1, Set<Relations> relationsesForRefIdRecord2) {
		this.idRecord = idRecord;
		this.recordTypes = recordTypes;
		this.archives = archives;
		this.title = title;
		this.xml = xml;
		this.creationDate = creationDate;
		this.modifyDate = modifyDate;
		this.phisicalPath = phisicalPath;
		this.position = position;
		this.depth = depth;
		this.deleted = deleted;
		this.recordsVersions = recordsVersions;
		this.relationsesForRefIdRecord1 = relationsesForRefIdRecord1;
		this.relationsesForRefIdRecord2 = relationsesForRefIdRecord2;
	}

	public Integer getIdRecord() {
		return this.idRecord;
	}

	public void setIdRecord(Integer idRecord) {
		this.idRecord = idRecord;
	}

	public RecordTypes getRecordTypes() {
		return this.recordTypes;
	}

	public void setRecordTypes(RecordTypes recordTypes) {
		this.recordTypes = recordTypes;
	}

	public Archives getArchives() {
		return this.archives;
	}

	public void setArchives(Archives archives) {
		this.archives = archives;
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

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModifyDate() {
		return this.modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getPhisicalPath() {
		return this.phisicalPath;
	}

	public void setPhisicalPath(String phisicalPath) {
		this.phisicalPath = phisicalPath;
	}

	public Set<Relations> getRelationsesForRefIdRecord1() {
		return this.relationsesForRefIdRecord1;
	}

	public void setRelationsesForRefIdRecord1(Set<Relations> relationsesForRefIdRecord1) {
		this.relationsesForRefIdRecord1 = relationsesForRefIdRecord1;
	}

	public Set<Relations> getRelationsesForRefIdRecord2() {
		return this.relationsesForRefIdRecord2;
	}

	public void setRelationsesForRefIdRecord2(Set<Relations> relationsesForRefIdRecord2) {
		this.relationsesForRefIdRecord2 = relationsesForRefIdRecord2;
	}

	public XMLReader getXMLReader() throws DocumentException {
		if (xmlReader == null) {
			String xml_string = null;
			try {
				xml_string = new String(xml, "UTF-8");
			} catch (Exception e) {
				System.err.println("failed to build UTF-8 XML,try to build ISO-8859-1 XML");
				try {
					xml_string = new String(xml, "ISO-8859-1");
				} catch (Exception e1) {
					System.err.println("failed to build ISO-8859-1 XML,try to build with bytes encoding");
					xml_string = new String(xml);

				}
			}
			// DIEGO: PERCHE' LO FACCIAMO?
			// xml_string = StringEscapeUtils.unescapeXml(xml_string);
			// xml_string = StringEscapeUtils.escapeXml(xml_string);
			// xml_string = xml_string.replaceAll("&lt;",
			// "<").replaceAll("&gt;", ">").replaceAll("&quot;",
			// "\"").replaceAll("&apos;", "'");

			xmlReader = new XMLReader(xml_string);
		}
		return xmlReader;
	}

	public void closeXMLReader() {
		if (xmlReader != null) {
			xmlReader = null;
		}
	}

	public HashMap<String, ArrayList<String>> getXpatsMap() throws DocumentException {
		if (xmlReader == null) {
			xmlReader = new XMLReader(xml);
		}
		xpatsMap = new HashMap<String, ArrayList<String>>();
		xmlReader.analyzeNodes(xpatsMap);
		return xpatsMap;
	}

	public boolean xpathFilter(String xPath) throws DocumentException {
		if (xmlReader == null) {
			xmlReader = new XMLReader(xml);
		}
		if (xmlReader.getNodeValue(xPath) != null && !xmlReader.getNodeValue(xPath).equals("")) {
			return true;
		}
		return false;
	}

	public Integer getPosition() {
		return this.position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public String getXmlId() {
		return this.xmlId;
	}

	public void setXmlId(String xmlId) {
		this.xmlId = xmlId;
	}

	public Boolean getDeleted() {
		return this.deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String isAlreadyPublished() {
		return alreadyPublished;
	}

	public void setAlreadyPublished(String alreadyPublished) {
		this.alreadyPublished += "@" + alreadyPublished + "@";
	}

	public Set<RecordsVersion> getRecordsVersions() {
		return recordsVersions;
	}

	public void setRecordsVersions(Set<RecordsVersion> recordsVersions) {
		this.recordsVersions = recordsVersions;
	}

	public HashMap<String, ArrayList<String>> getExternalcontentsMap() {
		return externalcontentsMap;
	}

	public void setExternalcontentsMap(HashMap<String, ArrayList<String>> externalcontentsMap) {
		this.externalcontentsMap = externalcontentsMap;
	}

	public Integer getTotChildren() {
		return totChildren;
	}

	public void setTotChildren(Integer totChildren) {
		this.totChildren = totChildren;
	}

	public Set<Notes> getNoteses() {
		return this.noteses;
	}

	public void setNoteses(Set<Notes> noteses) {
		this.noteses = noteses;
	}

 

	public Boolean getAlredyProcessed() {
		return alredyProcessed;
	}

	public void setAlredyProcessed(Boolean alredyProcessed) {
		this.alredyProcessed = alredyProcessed;
	}

}
