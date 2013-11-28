package com.openDams.index.factory;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.jdbc.JdbcDirectory;
import org.apache.lucene.util.Version;


public class JDBCIndexManager extends IndexManager{
	   /* private String analyzerClass = null;
	    private int maxNumSegments = 1;
	    private int maxMergeDocs = LogMergePolicy.DEFAULT_MAX_MERGE_DOCS;
	    private boolean optimize = true;
	    private boolean doWaitOperationsComplete = true;
	    private double RAMBufferSizeMB = IndexWriter.DEFAULT_RAM_BUFFER_SIZE_MB;*/
	 	public void createIndex(JdbcDirectory jdbcDir) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	    	try {
				System.out.println("Indice non esiste e lo creo "+jdbcDir.getTable().getName());
			} catch (Exception e) {
				System.out.println("Indice non esiste e lo creo ");
			}
	    	Analyzer analyzer = LuceneFactory.getAnalyzer(analyzerClass);
			jdbcDir.create();
			IndexWriter iwriter = new IndexWriter(jdbcDir, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
			if(RAMBufferSizeMB!=IndexWriter.DEFAULT_RAM_BUFFER_SIZE_MB)
				iwriter.setRAMBufferSizeMB(RAMBufferSizeMB);
			if(maxMergeDocs!=LogMergePolicy.DEFAULT_MAX_MERGE_DOCS)
				iwriter.setMaxMergeDocs(maxMergeDocs);
			iwriter.commit();
			iwriter.close(doWaitOperationsComplete);
			jdbcDir.clearLock(jdbcDir.getLockID());
			System.out.println("Indice creato");
	    }
	 	public void writeIndex(JdbcDirectory jdbcDir,Document doc) throws CorruptIndexException, LockObtainFailedException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	    	Analyzer analyzer = LuceneFactory.getAnalyzer(analyzerClass);
	    	IndexWriter iwriter = new IndexWriter(jdbcDir, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
	    	if(RAMBufferSizeMB!=IndexWriter.DEFAULT_RAM_BUFFER_SIZE_MB)
				iwriter.setRAMBufferSizeMB(RAMBufferSizeMB);
	    	if(maxMergeDocs!=LogMergePolicy.DEFAULT_MAX_MERGE_DOCS)
				iwriter.setMaxMergeDocs(maxMergeDocs);
			try {
				iwriter.addDocument(doc);
				if(optimize)
					iwriter.optimize(doWaitOperationsComplete);
				iwriter.commit();
				iwriter.close(doWaitOperationsComplete);
			} catch (OutOfMemoryError e) {
				iwriter.close(doWaitOperationsComplete);
				e.printStackTrace();
				jdbcDir.clearLock(jdbcDir.getLockID());
			}
	    }
	 	public void optimizeIndex(JdbcDirectory jdbcDir) throws CorruptIndexException, LockObtainFailedException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	    	Analyzer analyzer = LuceneFactory.getAnalyzer(analyzerClass);
	    	IndexWriter iwriter = new IndexWriter(jdbcDir, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
	    	if(RAMBufferSizeMB!=IndexWriter.DEFAULT_RAM_BUFFER_SIZE_MB)
				iwriter.setRAMBufferSizeMB(RAMBufferSizeMB);
	    	if(maxMergeDocs!=LogMergePolicy.DEFAULT_MAX_MERGE_DOCS)
				iwriter.setMaxMergeDocs(maxMergeDocs);
			try {
				iwriter.optimize(doWaitOperationsComplete);
				iwriter.close();
			} catch (OutOfMemoryError e) {
				iwriter.close();
				e.printStackTrace();
				jdbcDir.clearLock(jdbcDir.getLockID());
			}
	    }
	 	public void updateIndex(JdbcDirectory jdbcDir,Document doc,String id_record) throws ParseException, CorruptIndexException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	    	Analyzer analyzer = LuceneFactory.getAnalyzer(analyzerClass);
			QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,"id_record", analyzer);
			Query query = parser.parse(id_record);
			IndexWriter iwriter = new IndexWriter(jdbcDir, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
			if(RAMBufferSizeMB!=IndexWriter.DEFAULT_RAM_BUFFER_SIZE_MB)
				iwriter.setRAMBufferSizeMB(RAMBufferSizeMB);
			if(maxMergeDocs!=LogMergePolicy.DEFAULT_MAX_MERGE_DOCS)
				iwriter.setMaxMergeDocs(maxMergeDocs);
	        try {
				iwriter.deleteDocuments(query);
				iwriter.addDocument(doc);
				iwriter.expungeDeletes(false);
				if(optimize)
					iwriter.optimize(doWaitOperationsComplete);			
				iwriter.commit();
				iwriter.close(doWaitOperationsComplete);
	        } catch (OutOfMemoryError e) {
				iwriter.close(doWaitOperationsComplete);
				e.printStackTrace();
				jdbcDir.clearLock(jdbcDir.getLockID());
			}
	    }
	 	public void deleteIndex(JdbcDirectory jdbcDir,String id_record) throws ParseException, CorruptIndexException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	    	Analyzer analyzer = LuceneFactory.getAnalyzer(analyzerClass);
			QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,"id_record", analyzer);
			Query query = parser.parse(id_record);
			IndexWriter iwriter = new IndexWriter(jdbcDir, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
			if(RAMBufferSizeMB!=IndexWriter.DEFAULT_RAM_BUFFER_SIZE_MB)
				iwriter.setRAMBufferSizeMB(RAMBufferSizeMB);
			if(maxMergeDocs!=LogMergePolicy.DEFAULT_MAX_MERGE_DOCS)
				iwriter.setMaxMergeDocs(maxMergeDocs);
	        try {
				iwriter.deleteDocuments(query);
				iwriter.expungeDeletes(false);
				if(optimize)
					iwriter.optimize(doWaitOperationsComplete);
				iwriter.commit();
				iwriter.close(doWaitOperationsComplete);
	        } catch (OutOfMemoryError e) {
				iwriter.close(doWaitOperationsComplete);
				e.printStackTrace();
				jdbcDir.clearLock(jdbcDir.getLockID());
			}
	    }
		/*public String getAnalyzerClass() {
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
		}*/
}
