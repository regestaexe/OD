package com.openDams.index.factory;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;

public abstract class IndexManager {
	    public String analyzerClass = null;
	    public int maxNumSegments = 1;
	    public int maxMergeDocs = LogMergePolicy.DEFAULT_MAX_MERGE_DOCS;
	    public boolean optimize = true;
	    public boolean doWaitOperationsComplete = true;
	    public boolean useCompoundFile = false;
	    public double RAMBufferSizeMB = IndexWriter.DEFAULT_RAM_BUFFER_SIZE_MB;
	    public synchronized void createIndex(Directory directory) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{}
	 	public synchronized void writeIndex(Directory directory,Document doc) throws CorruptIndexException, LockObtainFailedException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{}
	 	public synchronized void optimizeIndex(Directory directory) throws CorruptIndexException, LockObtainFailedException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{}
	 	public synchronized void updateIndex(Directory directory,Document doc,String id_record) throws ParseException, CorruptIndexException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{}
	 	public synchronized void updateIndex(Directory directory,Document doc,String id_record,ArrayList<Object> elements_map) throws ParseException, CorruptIndexException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{}
	 	public synchronized void deleteIndex(Directory directory,String id_record) throws ParseException, CorruptIndexException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{}
		public String getAnalyzerClass() {
			return analyzerClass;
		}
		public void setAnalyzerClass(String analyzerClass) {
			this.analyzerClass = analyzerClass;
		}
		public int getMaxNumSegments() {
			return maxNumSegments;
		}
		public void setMaxNumSegments(int maxNumSegments) {
			this.maxNumSegments = maxNumSegments;
		}
		public boolean isOptimize() {
			return optimize;
		}
		public void setOptimize(boolean optimize) {
			this.optimize = optimize;
		}
		public boolean isDoWaitOperationsComplete() {
			return doWaitOperationsComplete;
		}
		public void setDoWaitOperationsComplete(boolean doWaitOperationsComplete) {
			this.doWaitOperationsComplete = doWaitOperationsComplete;
		}
		public double getRAMBufferSizeMB() {
			return RAMBufferSizeMB;
		}
		public void setRAMBufferSizeMB(double rAMBufferSizeMB) {
			RAMBufferSizeMB = rAMBufferSizeMB;
		}
		public void setMaxMergeDocs(int maxMergeDocs) {
			this.maxMergeDocs = maxMergeDocs;
		}
		public boolean isUseCompoundFile() {
			return useCompoundFile;
		}
		public void setUseCompoundFile(boolean useCompoundFile) {
			this.useCompoundFile = useCompoundFile;
		}
}
