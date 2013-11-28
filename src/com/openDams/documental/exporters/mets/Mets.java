package com.openDams.schedoni.exporters.mets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.openDams.utility.dom.XMLException;
import com.openDams.utility.dom.XMLHelper;


/*
 * TODO rifattorizzare ed aggiungere i segnaposto: caricare da properties i valori predefiniti e poi iniettare quelli che servono a runtime
 */
public class Mets {

	private Document doc;
		
	private String contentFileName;
	private String contentMime;
	private String contentMD5;
	private String contentSize;
	private String contentUri;
	private String contentObjectIdentifierType;
	private String contentObjectIdentifierValue;
	
	private String newsTitle;
	private String newsCreator;
	private String newsDate;
			
	private String metsDate;
	private String metsCreator;
	private String copyright;

	// da dove?
	private String fidID;
	private String amdID;
	private String dmdID;
	private String rmdID;
	private String groupID;
	private String seq;

	public Mets(String templateFileName) throws IOException, XMLException{
		this.doc = XMLHelper.newDocument(new File(templateFileName));
	}
			
	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public void setContentFileName(String contentFileName) {
		this.contentFileName = contentFileName;
	}

	public void setContentMime(String contentMime) {
		this.contentMime = contentMime;
	}

	public void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}

	public void setContentSize(String contentSize) {
		this.contentSize = contentSize;
	}

	public void setContentUri(String contentUri) {
		this.contentUri = contentUri;
	}

	public void setContentObjectIdentifierType(String contentObjectIdentifierType) {
		this.contentObjectIdentifierType = contentObjectIdentifierType;
	}

	public void setContentObjectIdentifierValue(String contentObjectIdentifierValue) {
		this.contentObjectIdentifierValue = contentObjectIdentifierValue;
	}

	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}

	public void setNewsCreator(String newsCreator) {
		this.newsCreator = newsCreator;
	}

	public void setNewsDate(String newsDate) {
		this.newsDate = newsDate;
	}

	public void setMetsDate(String metsDate) {
		this.metsDate = metsDate;
	}

	public void setMetsCreator(String metsCreator) {
		this.metsCreator = metsCreator;
	}

	public void setFidID(String fidID) {
		this.fidID = fidID;
	}

	public void setAmdID(String amdID) {
		this.amdID = amdID;
	}

	public void setDmdID(String dmdID) {
		this.dmdID = dmdID;
	}

	public void setRmdID(String rmdID) {
		this.rmdID = rmdID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}
	
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	
	public void setContentUri(final URI uri) {
		this.contentUri = uri.toString();
	}

	public void process() throws XMLException, MetsException{
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			this.metsDate = sdf.format(new Date());
												
			final Map<String, String> nodeValues = new HashMap<String, String>();
					
			nodeValues.put("/mets:mets/mets:metsHdr/@CREATEDATE", this.metsDate);
			nodeValues.put("/mets:mets/mets:metsHdr/mets:agent/mets:name", this.metsCreator);
			
			// mets:dmdSec
			nodeValues.put("/mets:mets/mets:dmdSec/@ID", this.dmdID);
			nodeValues.put("/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:title", this.newsTitle);
			nodeValues.put("/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:creator", this.newsCreator);
			nodeValues.put("/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:date", this.newsDate);
			
			nodeValues.put("/mets:mets/mets:amdSec/mets:techMD/mets:mdWrap/mets:xmlData/premis:object/premis:originalName", this.contentFileName);
			nodeValues.put("/mets:mets/mets:amdSec/mets:techMD/mets:mdWrap/mets:xmlData/premis:object/premis:objectCharacteristics/premis:size", this.contentSize);
			nodeValues.put("/mets:mets/mets:amdSec/mets:techMD/mets:mdWrap/mets:xmlData/premis:object/premis:storage/premis:contentLocation/premis:contentLocationValue", this.contentUri);
						
			nodeValues.put("/mets:mets/mets:amdSec/mets:techMD/@ID", this.amdID);
			nodeValues.put("/mets:mets/mets:amdSec/mets:rightsMD/@ID", this.rmdID);
			nodeValues.put("/mets:mets/mets:fileSec/mets:fileGrp/mets:file/@ID", this.fidID);
			nodeValues.put("/mets:mets/mets:fileSec/mets:fileGrp/mets:file/@GROUPID", this.groupID);
			
			nodeValues.put("/mets:mets/mets:fileSec/mets:fileGrp/mets:file/@MIMETYPE", this.contentMime);
			nodeValues.put("/mets:mets/mets:fileSec/mets:fileGrp/mets:file/@SEQ", this.seq);
			nodeValues.put("/mets:mets/mets:fileSec/mets:fileGrp/mets:file/mets:FLocat/@xlink:href", this.contentUri);
						
			nodeValues.put("/mets:mets/mets:amdSec/mets:techMD/mets:mdWrap/mets:xmlData/premis:object/premis:objectIdentifier/premis:objectIdentifierType", this.contentObjectIdentifierType);
			nodeValues.put("/mets:mets/mets:amdSec/mets:techMD/mets:mdWrap/mets:xmlData/premis:object/premis:objectIdentifier/premis:objectIdentifierValue", this.contentObjectIdentifierValue);
			
			nodeValues.put("/mets:mets/mets:structMap/mets:div/@ADMID", this.rmdID);
			nodeValues.put("/mets:mets/mets:structMap/mets:div/@DMDID", this.dmdID);
			nodeValues.put("/mets:mets/mets:structMap/mets:div/mets:fptr/@FILEID", this.fidID);
			
			nodeValues.put("/mets:mets/mets:amdSec/mets:techMD/mets:mdWrap/mets:xmlData/premis:object/premis:originalName", this.contentFileName);
			nodeValues.put("/mets:mets/mets:amdSec/mets:techMD/mets:mdWrap/mets:xmlData/premis:object/premis:storage/premis:contentLocation/premis:contentLocationValue", this.contentUri);
			
			nodeValues.put("/mets:mets/mets:amdSec/mets:rightsMD/mets:mdWrap/mets:xmlData/rights:RightsDeclarationMD/rights:Context/rights:Constraints/rights:ConstraintDescription", this.copyright);
			
			// MD5
			nodeValues.put("/mets:mets/mets:amdSec/mets:techMD/mets:mdWrap/mets:xmlData/premis:object/premis:objectCharacteristics/premis:fixity/premis:messageDigest", this.contentMD5);
			nodeValues.put("/mets:mets/mets:amdSec/mets:techMD/mets:mdWrap/mets:xmlData/premis:object/premis:objectCharacteristics/premis:format/premis:formatDesignation/premis:formatName", this.contentMime);
			nodeValues.put("/mets:mets/mets:amdSec/mets:techMD/mets:mdWrap/mets:xmlData/premis:object/premis:objectCharacteristics/premis:size", this.contentSize);
			
			XMLHelper.setElementsValues(doc, nodeValues);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new MetsException(e);
		}
	}

	
	public void setContent(InputStream inputStream, String contentType) throws MetsException{
		try {
			this.contentSize = String.valueOf(inputStream.available());
			this.contentMD5 = getMD5(inputStream);
			this.contentMime = contentType;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MetsException(e);
		}		
	}
	
	public String toXMLString() throws XMLException, MetsException{
		this.process();
		return XMLHelper.toStringValue(doc);
	}
	
	/*
	 * generazione dell'md5 dal contentuo binario di un inputStream
	 */
	public static String getMD5(InputStream inputStream) throws MetsException{
		try {
			final byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			final MessageDigest m = MessageDigest.getInstance("MD5");
			final String md5 = new BigInteger(1, m.digest(data)).toString(16);
			return md5;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MetsException(e);
		}
	}
	
	public static void main(String[] args) throws IOException, XMLException, MetsException, URISyntaxException { }

}
