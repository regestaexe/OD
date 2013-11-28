package com.openDams.admin.tasks;

import com.openDams.configuration.ConfigurationException;
import com.openDams.endpoint.managing.EndPointManager;
import com.openDams.endpoint.managing.OpenDamsEndPointManagerFactoryProvider;

public class SQLEndPointPublisher {
	public SQLEndPointPublisher() throws ConfigurationException {}
	public void publishArchive(int idArchive,String endPointManagerkey,String user) throws Exception{
		System.out.println("############################################### publishArchive");
		EndPointManager endPointManager =  OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(endPointManagerkey);
		endPointManager.publishArchive(idArchive,user,endPointManagerkey);
	}
	public void dePublishArchive(int idArchive,String endPointManagerkey,String user) throws Exception{
		System.out.println("############################################### dePublishArchive");
		EndPointManager endPointManager =  OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(endPointManagerkey);
		endPointManager.removeEndPointObjects(idArchive,user,endPointManagerkey);
	}
	public void rePublishRecords(int idArchive,String endPointManagerkey,String user) throws Exception{
		System.out.println("############################################### republishArchive");
		EndPointManager endPointManager =  OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(endPointManagerkey);
		endPointManager.rePublishRecords(idArchive, user,endPointManagerkey);
	}
	public void publishRecordsModified(int idArchive,String endPointManagerkey,String user) throws Exception{
		System.out.println("############################################### publishModified");
		EndPointManager endPointManager =  OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(endPointManagerkey);
		endPointManager.publishRecordsModified(idArchive, user,endPointManagerkey);
	}
	public void publishRecordsNotPublished(int idArchive,String endPointManagerkey,String user) throws Exception{
		System.out.println("############################################### publishNotPublished");
		EndPointManager endPointManager =  OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(endPointManagerkey);
		endPointManager.publishRecordsNotPublished(idArchive, user,endPointManagerkey);
	}
	public void publishErrorResults(int idArchive,String endPointManagerkey,String user) throws Exception{
		System.out.println("############################################### publishErrorResults");
		EndPointManager endPointManager =  OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(endPointManagerkey);
		endPointManager.publishErrorResults(idArchive, user,endPointManagerkey);
	}
}
