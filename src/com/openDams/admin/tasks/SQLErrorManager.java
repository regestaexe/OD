package com.openDams.admin.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

public class SQLErrorManager {
	private DataSource dataSource = null;
	public SQLErrorManager(){}
	public void insertError(String id_record,int idArchive,String xmlID,String description) throws SQLException{
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			PreparedStatement ps = null;
			try {
			  ps = connection.prepareStatement("INSERT INTO batch_error_report values (?,?,?,?,?)");
			  ps.setInt(1,Integer.parseInt(id_record));
			  ps.setInt(2,idArchive);
			  ps.setString(3,xmlID);
			  ps.setString(4,description);
			  ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			  ps.executeUpdate();
			}catch(Exception e){
			}finally {
			  ps.close();
			}
			connection.close();
		} catch (SQLException e) {
		} finally{
			if(connection!= null && !connection.isClosed())
				connection.close();
		}
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
