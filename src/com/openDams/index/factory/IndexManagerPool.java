package com.openDams.index.factory;

import java.util.Hashtable;

public class IndexManagerPool  extends IndexManager{
    public String indexManagerClass = "";
	private Hashtable<Integer, IndexManager> indexManagerHashMap = null;
	
	public synchronized IndexManager getIndexManager(int idArchive) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		IndexManager result = null;
		if(indexManagerHashMap==null){
			indexManagerHashMap = new Hashtable<Integer, IndexManager>();
		}
		if(indexManagerHashMap.get(idArchive)==null){
			result = (IndexManager)(Class.forName(indexManagerClass).newInstance());
			result.setAnalyzerClass(analyzerClass);
			result.setDoWaitOperationsComplete(doWaitOperationsComplete);
			result.setMaxMergeDocs(maxMergeDocs);
			result.setMaxNumSegments(maxNumSegments);
			result.setOptimize(optimize);
			result.setRAMBufferSizeMB(RAMBufferSizeMB);
			result.setUseCompoundFile(useCompoundFile);
			indexManagerHashMap.put(idArchive, result);
		}else{
			result = indexManagerHashMap.get(idArchive);
		}
		return result;
	}

	public void setIndexManagerClass(String indexManagerClass) {
		this.indexManagerClass = indexManagerClass;
	}
}
