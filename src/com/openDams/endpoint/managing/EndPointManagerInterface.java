package com.openDams.endpoint.managing;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

public interface EndPointManagerInterface {
	
	public void publishRecord(int idRecord, String user,String endPointManagerkey) throws Exception;

	public void rePublishRecords(int idArchive, String user,String endPointManagerkey) throws Exception;

	public void publishRecordsModified(int idArchive, String user,String endPointManagerkey) throws Exception;

	public void publishRecordsNotPublished(int idArchive, String user,String endPointManagerkey) throws Exception;

	public void publishErrorResults(int idArchive, String user,String endPointManagerkey) throws Exception;
	
	public void publishArchive(int idArchive, String user,String endPointManagerkey) throws Exception;

	public boolean checkPublished(int id_record, int id_archive, String rdfAbout) throws SQLException, RepositoryException, MalformedQueryException, QueryEvaluationException;

	public boolean checkPublishedArchive(int idArchive) throws SQLException, RepositoryException, MalformedQueryException, QueryEvaluationException;

	public void removeEndPointObject(int idRecord, String user,String endPointManagerkey);

	public void removeEndPointObjects(int idArchive, String user,String endPointManagerkey) throws Exception;

	public void setExcludeXpathListArchiveMap(Map<Object, List<String>> excludeXpathListArchiveMap);

	public Map<Object, List<String>> getExcludeXpathListArchiveMap();

	public void setOpenDamsdataSource(DataSource openDamsdataSource);

	public void setAutoArchivePublishing(Map<Integer, Boolean> autoArchivePublishing);

	public void setAllowedArchivePublishing(Map<Integer, Boolean> allowedArchivePublishing);

	public boolean isAutoPublishingArchive(int idArchive);
}
