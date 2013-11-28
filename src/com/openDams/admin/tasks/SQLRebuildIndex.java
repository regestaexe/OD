package com.openDams.admin.tasks;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.jdbc.JdbcDirectory;

import com.openDams.configuration.ConfigurationException;
import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.index.factory.DialectFactory;
import com.openDams.index.factory.DocumentFactory;
import com.regesta.framework.util.Watch;

public class SQLRebuildIndex {
	private DataSource dataSource = null;
	private HashMap<Integer, ArrayList<Object>> elements_map = null;
	private IndexConfiguration indexConfiguration = null;
	private Directory directory = null;
	private Directory directory2 = null;
	private boolean resetIndex = true;
	private boolean resetIndex2 = true;
	private boolean createIndex = true;
	private boolean createIndex2 = true;
	private boolean useDefaultIndex = false;
	private SQLErrorManager sqlErrorManager = null;

	public SQLRebuildIndex() throws ConfigurationException {
	}

	public void rebuildIndex(int idArchive) throws Exception {
		Connection connection = null;
		try {
			String archive_label = "";
			connection = dataSource.getConnection();
			setArchiveOffline(connection, idArchive);
			Statement st = connection.createStatement();
			String query = "SELECT label FROM archives where id_archive=" + idArchive + ";";
			ResultSet resultSet = st.executeQuery(query);
			while (resultSet.next()) {
				archive_label = resultSet.getString("label");
			}
			resultSet.close();
			st.close();
			st = connection.createStatement();
			query = "SELECT id_record FROM records where ref_id_archive=" + idArchive + " and (deleted<>1 OR deleted is NULL);";
			resultSet = st.executeQuery(query);
			int i = 0;
			int count = 0;
			while (resultSet.next()) {
				String idRecord = resultSet.getString("id_record");
				Statement stRecord = connection.createStatement();
				String queryRecord = "SELECT id_record,title,xml,xml_id FROM records where id_record=" + idRecord + ";";
				ResultSet resultSetRecord = stRecord.executeQuery(queryRecord);
				while (resultSetRecord.next()) {
					String id_record = resultSetRecord.getString("id_record");
					String title = resultSetRecord.getString("title");
					String xml = new String(resultSetRecord.getBytes(("xml")), "UTF-8");
					String xml_id = resultSetRecord.getString("xml_id");
					try {
						writeIndex(id_record, title, xml, idArchive, archive_label);
					} catch (Exception e) {
						sqlErrorManager.insertError(id_record, idArchive, xml_id, "rigenerazione indice");
					}
				}
				resultSetRecord.close();
				stRecord.close();
				if (i != 0 && i % 100 == 0) {
					count++;
					System.out.println("-------------------------------->Record analizzati ed indicizzati " + (100 * count) + " " + Watch.getTime());
				}
				i++;
			}
			resultSet.close();
			st.close();
			setArchiveOnline(connection, idArchive);
			connection.close();
			// optimize();
			optimize(idArchive);
			closeAll();

		} finally {
			if (connection != null && !connection.isClosed())
				connection.close();
			resetIndex = true;
			resetIndex2 = true;
		}
	}

	private void setArchiveOffline(Connection connection, int idArchive) {
		String update = "Update archives set offline=1 where id_archive=" + idArchive + ";";

		try {
			Statement updateStatement = connection.createStatement();
			updateStatement.executeUpdate(update);
		} catch (Exception e) {
		}
	}

	private void setArchiveOnline(Connection connection, int idArchive) {
		String update = "Update archives set offline=0 where id_archive=" + idArchive + ";";
		try {
			Statement updateStatement = connection.createStatement();
			updateStatement.executeUpdate(update);
		} catch (Exception e) {
		}
	}

	private void writeIndex(String id_record, String title, String xml, int idArchive, String archive_label) throws Exception {
		Document doc = null;
		try {
			elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();
			if (elements_map.get(idArchive) == null)
				elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();
			try {
				doc = DocumentFactory.buildDocument(id_record, Integer.toString(idArchive), archive_label, title, xml, elements_map.get(idArchive));
			} catch (Exception e1) {
				throw new Exception("buildDocument:error");
			}
			if (directory == null) {
				if (indexConfiguration.isFsDirectory())
					directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + idArchive + "_" + indexConfiguration.getIndex_name()));
				else
					directory = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), idArchive + "_" + indexConfiguration.getIndex_name());
				if (createIndex && !indexConfiguration.isFsDirectory()) {
					((JdbcDirectory) directory).create();
				}
			}
			// Analyzer analyzer = new IT_Analyzer();
			/*
			 * if(iwriter==null){ iwriter = new IndexWriter(directory, analyzer,
			 * true, IndexWriter.MaxFieldLength.UNLIMITED);
			 * iwriter.setUseCompoundFile(true); }
			 */
			try {
				if (resetIndex) {
					indexConfiguration.getIndexManager(idArchive).createIndex(directory);
					resetIndex = false;
				}
				// iwriter.addDocument(doc);
				indexConfiguration.getIndexManager(idArchive).writeIndex(directory, doc);
			} catch (OutOfMemoryError e) {
				throw e;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (!e.getMessage().equals("buildDocument:error")) {
				if (directory != null) {
					if (!indexConfiguration.isFsDirectory())
						((JdbcDirectory) directory).deleteMarkDeleted();
					directory.clearLock(directory.getLockID());
					directory.close();
					directory = null;
				}
				/*
				 * if(iwriter!=null){ iwriter.close(false); iwriter = null; }
				 */
			}
			throw e;
		} catch (Error e) {
			e.printStackTrace();
			if (directory != null) {
				if (!indexConfiguration.isFsDirectory())
					((JdbcDirectory) directory).deleteMarkDeleted();
				directory.clearLock(directory.getLockID());
				directory.close();
				directory = null;
			}
			/*
			 * if(iwriter!=null){ iwriter.close(false); iwriter = null; }
			 */
			throw e;
		}
		if (useDefaultIndex) {
			try {
				doc = DocumentFactory.buildGenericdDocument(id_record, Integer.toString(idArchive), archive_label, title, xml);
				if (directory2 == null) {
					if (indexConfiguration.isFsDirectory())
						directory2 = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + idArchive + "_" + indexConfiguration.getGeneric_index_name()));
					else
						directory2 = new JdbcDirectory(dataSource, DialectFactory.getDialect(indexConfiguration.getjDBCDialect()), idArchive + "_" + indexConfiguration.getGeneric_index_name());
					if (createIndex2 && !indexConfiguration.isFsDirectory()) {
						((JdbcDirectory) directory2).create();
					}
				}
				/*
				 * if(iwriter2==null){ Analyzer analyzer = new IT_Analyzer();
				 * if(createIndex2){ iwriter2 = new IndexWriter(directory2,
				 * analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
				 * createIndex2=false; }else{ iwriter2 = new
				 * IndexWriter(directory2, analyzer, false,
				 * IndexWriter.MaxFieldLength.UNLIMITED); } }
				 */
				try {
					if (resetIndex2) {
						indexConfiguration.getIndexManager(idArchive).createIndex(directory2);
						resetIndex2 = false;
					}
					indexConfiguration.getIndexManager(idArchive).writeIndex(directory2, doc);
				} catch (OutOfMemoryError e) {
					throw e;
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (directory2 != null) {
					if (!indexConfiguration.isFsDirectory())
						((JdbcDirectory) directory2).deleteMarkDeleted();
					directory2.clearLock(directory2.getLockID());
					directory2.close();
					directory2 = null;
				}
				/*
				 * if(iwriter2!=null){ iwriter2.close(false); iwriter2 = null; }
				 */
				throw e;
			} catch (Error e) {
				e.printStackTrace();
				if (directory2 != null) {
					if (!indexConfiguration.isFsDirectory())
						((JdbcDirectory) directory2).deleteMarkDeleted();
					directory2.clearLock(directory2.getLockID());
					directory2.close();
					directory2 = null;
				}
				/*
				 * if(iwriter2!=null){ iwriter2.close(false); iwriter2 = null; }
				 */
				throw e;
			}
		}
	}

	private void closeAll() {
		try {
			// iwriter.commit();
			// iwriter.close(false);
			// iwriter = null;
			if (directory != null) {
				if (!indexConfiguration.isFsDirectory())
					((JdbcDirectory) directory).deleteMarkDeleted();
				directory.close();
				directory = null;
			}
			// if(iwriter2!=null){
			// iwriter2.commit();
			// iwriter2.close(false);
			// iwriter2 = null;
			if (directory2 != null) {
				if (!indexConfiguration.isFsDirectory())
					((JdbcDirectory) directory2).deleteMarkDeleted();
				directory2.close();
				directory2 = null;
			}
			// }
		} catch (IOException e) {
		}
	}

	/*
	 * private void optimize(){ try { if(iwriter!=null) iwriter.optimize(true);
	 * if(iwriter2!=null) iwriter2.optimize(true); } catch (Exception e) { } }
	 */
	private void optimize(int idArchive) {
		try {
			if (directory != null)
				indexConfiguration.getIndexManager(idArchive).optimizeIndex(directory);
			if (directory2 != null)
				indexConfiguration.getIndexManager(idArchive).optimizeIndex(directory2);
		} catch (Exception e) {
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public IndexConfiguration getIndexConfiguration() {
		return indexConfiguration;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}

	public void setSqlErrorManager(SQLErrorManager sqlErrorManager) {
		this.sqlErrorManager = sqlErrorManager;
	}
}
