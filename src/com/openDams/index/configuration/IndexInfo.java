package com.openDams.index.configuration;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;

import com.regesta.framework.io.FileSizeConverter;

public class IndexInfo {
    private String index_name;
    private long index_version;
    private boolean optimized;
    private int numDocs;
    private int numDocsArchive;
    private int numDocsBasket;
    private String lastModifyDate;
    private HashMap<String,String> files;
    private String xmlIndex;
    private String xmlTitle;
    public IndexInfo(String indexName,Directory directory,Analyzer analyzer) throws CorruptIndexException, IOException{
    	IndexWriter iwriter = null;
    	IndexReader indexReader = null;
    	try {
			iwriter = new IndexWriter(directory, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);			
			indexReader = iwriter.getReader();
			this.index_name = indexName;
			this.index_version = indexReader.getVersion();
			this.optimized = indexReader.isOptimized();
			this.numDocs = indexReader.numDocs();	
			Date date = new Date(IndexReader.lastModified(directory));
			DateFormat formatter  = new SimpleDateFormat("EEEE, dd MMMM yyyy, hh:mm:ss a");
			this.lastModifyDate = formatter.format(date);
			this.files = new HashMap<String, String>();
			String[] files = directory.listAll();
			for (int i = 0; i < files.length; i++) {
				if(files[i].startsWith("_"))
					this.files.put(files[i], Long.toString(Math.round(FileSizeConverter.convertSize(directory.fileLength(files[i]), FileSizeConverter.KB)))+" KB" );
			}
			indexReader.close();
			iwriter.close();
			iwriter = null;
			directory.clearLock(directory.getLockID());
		} catch (CorruptIndexException e) {
			if (iwriter != null) {
				iwriter.close();
				iwriter = null;
			}
			if (indexReader != null) {
				indexReader.close();
				indexReader = null;
			}
			throw e;
		} catch (LockObtainFailedException e) {
			if (iwriter != null) {
				iwriter.close();
				iwriter = null;
			}
			if (indexReader != null) {
				indexReader.close();
				indexReader = null;
			}
			throw e;
		} catch (IOException e) {
			if (iwriter != null) {
				iwriter.close();
				iwriter = null;
			}
			if (indexReader != null) {
				indexReader.close();
				indexReader = null;
			}
			throw e;
		}
    }
	public String getIndex_name() {
		return index_name;
	}
	public long getIndex_version() {
		return index_version;
	}
	public boolean isOptimized() {
		return optimized;
	}
	public int getNumDocs() {
		return numDocs;
	}
	public String getLastModifyDate() {
		return lastModifyDate;
	}
	public HashMap<String, String> getFiles() {
		return files;
	}
	public String getXmlIndex() {
		return xmlIndex;
	}
	public String getXmlTitle() {
		return xmlTitle;
	}
	public void setXmlIndex(String xmlIndex) {
		this.xmlIndex = xmlIndex;
	}
	public void setXmlTitle(String xmlTitle) {
		this.xmlTitle = xmlTitle;
	}
	public int getNumDocsArchive() {
		return numDocsArchive;
	}
	public void setNumDocsArchive(int numDocsArchive) {
		this.numDocsArchive = numDocsArchive;
	}
	public int getNumDocsBasket() {
		return numDocsBasket;
	}
	public void setNumDocsBasket(int numDocsBasket) {
		this.numDocsBasket = numDocsBasket;
	}
}
