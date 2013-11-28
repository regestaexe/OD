package com.openDams.admin.tasks;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.dom4j.DocumentException;

import com.openDams.configuration.ConfigurationException;
import com.openDams.title.configuration.TitleManager;
import com.regesta.framework.util.DBSolver;
import com.regesta.framework.util.DateUtility;
import com.regesta.framework.util.Watch;
import com.regesta.framework.xml.XMLReader;

public class SQLRebuildTitles {
	private DataSource dataSource = null;
	private TitleManager titleManager;
	private SQLErrorManager sqlErrorManager = null;
	public SQLRebuildTitles() throws ConfigurationException {
	}

	public void rebuildTitles(int idArchive) throws FileNotFoundException, DocumentException, ConfigurationException, UnsupportedEncodingException, SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			setArchiveOffline(connection,idArchive);
			Statement st = connection.createStatement();
			String query = "SELECT id_record,xml,title,xml_id FROM records where ref_id_archive=" + idArchive + ";";
			//String query = "SELECT id_record,xml FROM records where id_record="+222668+";";
			ResultSet resultSet = st.executeQuery(query);
			int i = 0;
			int count = 0;
			connection.setAutoCommit(false);
			while (resultSet.next()) {
				String idRecord = resultSet.getString("id_record");
				String oldTitle = resultSet.getString("title");
				String xml = new String(resultSet.getBytes(("xml")), "UTF-8");
				String xml_id = resultSet.getString("xml_id");
				String title;
				try {
					XMLReader xmlReader = new XMLReader(xml);
					title = DBSolver.escapeSingleApex(titleManager.buildTitle(xmlReader, new Integer(idArchive),new Integer(idRecord)));
				} catch (Exception e2) {
					sqlErrorManager.insertError(idRecord, idArchive, xml_id, "rigenerazione titolo");
					title = DBSolver.escapeSingleApex(oldTitle);
				}
				String update = "Update records set title='" + title + "',modify_date='" + DateUtility.getMySQLSystemDate() + " " + DateUtility.getMySQLTime() + "' where id_record=" + idRecord + ";";
				Statement updateStatement = connection.createStatement();
				try {
					updateStatement.executeUpdate(update);
				} catch (Exception e) {
				}
				updateStatement.close();
				if (i != 0 && i % 100 == 0) {
					count++;
					System.out.println("-------------------------------->Record analizzati e rititolati " + (100 * count) + " " + Watch.getTime());
					//System.gc();
				}
				i++;
			}
			resultSet.close();
			st.close();
			setArchiveOnline(connection,idArchive);
			connection.commit();
			connection.close();

		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			if (connection != null && !connection.isClosed())
				connection.close();
		}
	}
    private void setArchiveOffline(Connection connection,int idArchive){
    	String update = "Update archives set offline=1 where id_archive=" + idArchive + ";";
		
		try {
			Statement updateStatement = connection.createStatement();
			updateStatement.executeUpdate(update);
		} catch (Exception e) {
		}
    }
    private void setArchiveOnline(Connection connection,int idArchive){
    	String update = "Update archives set offline=0 where id_archive=" + idArchive + ";";		
		try {
			Statement updateStatement = connection.createStatement();
			updateStatement.executeUpdate(update);
		} catch (Exception e) {
		}
    }
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	public void setSqlErrorManager(SQLErrorManager sqlErrorManager) {
		this.sqlErrorManager = sqlErrorManager;
	}
}
