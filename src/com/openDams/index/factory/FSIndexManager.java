package com.openDams.index.factory;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import com.openDams.index.configuration.Element;


public class FSIndexManager extends IndexManager{
	 	public synchronized void createIndex(Directory directory) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	    	Analyzer analyzer = LuceneFactory.getAnalyzer(analyzerClass);
			IndexWriter iwriter = new IndexWriter(directory, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
			if(useCompoundFile)
				iwriter.setUseCompoundFile(true);
			if(RAMBufferSizeMB!=IndexWriter.DEFAULT_RAM_BUFFER_SIZE_MB)
				iwriter.setRAMBufferSizeMB(RAMBufferSizeMB);
			if(maxMergeDocs!=LogMergePolicy.DEFAULT_MAX_MERGE_DOCS)
				iwriter.setMaxMergeDocs(maxMergeDocs);
			iwriter.commit();
			iwriter.close(doWaitOperationsComplete);
			System.out.println("Indice creato");
	    }
	 	public synchronized void writeIndex(Directory directory,Document doc) throws CorruptIndexException, LockObtainFailedException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	    	Analyzer analyzer = LuceneFactory.getAnalyzer(analyzerClass);
	    	IndexWriter iwriter = new IndexWriter(directory, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
	    	if(useCompoundFile)
				iwriter.setUseCompoundFile(true);
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
				directory.clearLock(directory.getLockID());
			}
	    }
	 	public synchronized void optimizeIndex(Directory directory) throws CorruptIndexException, LockObtainFailedException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	    	Analyzer analyzer = LuceneFactory.getAnalyzer(analyzerClass);
	    	IndexWriter iwriter = new IndexWriter(directory, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
	    	if(useCompoundFile)
				iwriter.setUseCompoundFile(true);
	    	if(RAMBufferSizeMB!=IndexWriter.DEFAULT_RAM_BUFFER_SIZE_MB)
				iwriter.setRAMBufferSizeMB(RAMBufferSizeMB);
	    	if(maxMergeDocs!=LogMergePolicy.DEFAULT_MAX_MERGE_DOCS)
				iwriter.setMaxMergeDocs(maxMergeDocs);
			try {
				iwriter.optimize(true);
				iwriter.close();
			} catch (OutOfMemoryError e) {
				iwriter.close();
				e.printStackTrace();
				directory.clearLock(directory.getLockID());
			}
	    }
	 	public synchronized void updateIndex(Directory directory,Document doc,String id_record) throws ParseException, CorruptIndexException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	    	Analyzer analyzer = LuceneFactory.getAnalyzer(analyzerClass);
			QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,"id_record", analyzer);
			Query query = parser.parse(id_record);
			IndexWriter iwriter = new IndexWriter(directory, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
			if(useCompoundFile)
				iwriter.setUseCompoundFile(true);
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
				directory.clearLock(directory.getLockID());
			}
	    }
	 	public synchronized void updateIndex(Directory directory,Document doc,String id_record,ArrayList<Object> elements) throws ParseException, CorruptIndexException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	    	Analyzer analyzer = LuceneFactory.getAnalyzer(analyzerClass);
			QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,"id_record", analyzer);
			Query query = parser.parse(id_record);
			IndexWriter iwriter = new IndexWriter(directory, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
			if(useCompoundFile)
				iwriter.setUseCompoundFile(true);
			ArrayList<String> permanentFields = null;
			for(int i=0;i<elements.size();i++){
				Element element=(Element)elements.get(i);
				if(element.getType()!=null && element.getType().equalsIgnoreCase("external")){
					if(permanentFields==null)
						permanentFields = new ArrayList<String>(); 
					permanentFields.add(element.getSearch_alias());
				}
			}
			if(permanentFields!=null){
				IndexSearcher searcher = new IndexSearcher(directory, true);
				ScoreDoc[] hits = null;
				TopDocs topDocs = searcher.search(query, null, 1);
				hits = topDocs.scoreDocs;
				int docId = hits[0].doc;
				Document oldDoc = searcher.doc(docId);
				for (int i = 0; i < permanentFields.size(); i++){
					if(oldDoc.getField(permanentFields.get(i))!=null)
						doc.add(oldDoc.getField(permanentFields.get(i)));
				}	
			}	
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
				directory.clearLock(directory.getLockID());
			}
	    }
	 	public synchronized void deleteIndex(Directory directory,String id_record) throws ParseException, CorruptIndexException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	    	Analyzer analyzer = LuceneFactory.getAnalyzer(analyzerClass);
			QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,"id_record", analyzer);
			Query query = parser.parse(id_record);
			IndexWriter iwriter = new IndexWriter(directory, analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
			if(useCompoundFile)
				iwriter.setUseCompoundFile(true);
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
				directory.clearLock(directory.getLockID());
			}
	    }
}
