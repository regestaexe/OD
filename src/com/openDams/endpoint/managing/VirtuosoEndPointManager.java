package com.openDams.endpoint.managing;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.sesame2.driver.VirtuosoRepository;

import com.openDams.utility.rdf.QueryRDF;
import com.regesta.framework.util.Watch;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class VirtuosoEndPointManager extends EndPointManager {
	private String VIRTUOSO_INSTANCE;
	private int VIRTUOSO_PORT;
	private String VIRTUOSO_USERNAME;
	private String VIRTUOSO_PASSWORD;
	protected Map<Integer, List<String>> endPointArchiveDomains;
	protected Map<Integer, URI[]> endPointArchiveDomainsUris;

	public VirtuosoEndPointManager() {
	}

	@Override
	protected void manageRecord(EndpointRecordContainerList endpointRecordContainerList, boolean singleRecord, String action, boolean clear, int ref_id_endpoint_action) {
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<manageRecord>>>>>>>>>>>>>>>>>>>>>>>> endpointRecordContainerList " + endpointRecordContainerList.size());
		int count = 0;
		int countFailed = 0;
		RepositoryConnection repositoryConnection = null;
		Map<String, VirtGraph> endpointsMap = new HashMap<String, VirtGraph>();
		try {
			int idArchive = 0;
			Repository repository = new VirtuosoRepository("jdbc:virtuoso://" + VIRTUOSO_INSTANCE + ":" + VIRTUOSO_PORT, VIRTUOSO_USERNAME, VIRTUOSO_PASSWORD);
			URI[] graphs = null;
			URI[] addDeletedToGraphs = null;
			repositoryConnection = repository.getConnection();

			int refIdEndoPoint = findEndPoint(endpointRecordContainerList.endPointManagerkey);
			String user = endpointRecordContainerList.user;
			for (int s = 0; s < endpointRecordContainerList.size(); s++) {
				EndpointRecordContainer endpointRecordContainer = endpointRecordContainerList.get(s);
				idArchive = endpointRecordContainer.idArchive;
				int idRecord = endpointRecordContainer.idRecord;
				byte[] rdf = endpointRecordContainer.rdf;
				byte[] odsRdf = endpointRecordContainer.odsRdf;
				String rdfAbout = endpointRecordContainer.rdfAbout;
				System.out.println(action + " --- " + rdfAbout);
				try {
					if (singleRecord) {
						graphs = getArchiveGraphs(repository, idArchive);
						addDeletedToGraphs = new URI[graphs.length - 1];
						String toRemove = defaultDomain + rdfAbout;
						System.out.println("singleRecord " + toRemove);
						for (int i = 0; i < graphs.length; i++) {
							removeRecord(toRemove, null, null, (Resource) graphs[i], repositoryConnection);
							if (i > 0) {
								addDeletedToGraphs[i - 1] = graphs[i];
							}
						}
					} else if (s == 0) {
						graphs = getArchiveGraphs(repository, idArchive);
						addDeletedToGraphs = new URI[graphs.length - 1];
						for (int i = 0; i < graphs.length; i++) {
							if (i > 0) {
								// con.clear(graphs[i]);
								addDeletedToGraphs[i - 1] = graphs[i];
							}
						}
					}
					if (action.equalsIgnoreCase("rimozione") && !singleRecord) {
						System.out.println("in rimozione " + defaultDomain + rdfAbout);
						removeRecord(defaultDomain + rdfAbout, null, null, graphs[0], repositoryConnection);
						if (!clear) {
							addRecords(new ByteArrayInputStream(odsRdf), addDeletedToGraphs, endpointsMap);
						}
					} else if (action.equalsIgnoreCase("rimozione") && singleRecord) {
						for (URI graph : addDeletedToGraphs) {
							removeRecord(defaultDomain + rdfAbout, null, null, graph, repositoryConnection);
						}
						addRecords(new ByteArrayInputStream(odsRdf), addDeletedToGraphs, endpointsMap);

					} else {
						for (URI graph : addDeletedToGraphs) {
							removeRecord(defaultDomain + rdfAbout, null, null, graph, repositoryConnection);
						}
						addRecord(new ByteArrayInputStream(rdf), graphs[0], endpointsMap);
						addRecords(new ByteArrayInputStream(odsRdf), addDeletedToGraphs, endpointsMap);
						/*
						 * repositoryConnection.add(new
						 * ByteArrayInputStream(rdf), "", RDFFormat.RDFXML,
						 * graphs[0]); repositoryConnection.add(new
						 * ByteArrayInputStream(odsRdf), "", RDFFormat.RDFXML,
						 * addDeletedToGraphs);
						 */
					}
					performOperation(idRecord, refIdEndoPoint, action, true, idArchive, user, ref_id_endpoint_action);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("ERRORE GENERAZIONE RECORD IN END POINT");
					System.err.println("IDRECORD = " + idRecord);
					System.err.println("URI = " + defaultDomain + rdfAbout);
					System.err.println(new String(rdf, "UTF-8"));
					performOperation(idRecord, refIdEndoPoint, action, false, idArchive, user, ref_id_endpoint_action);
					countFailed++;
				}
				if (s != 0 && s % 100 == 0) {
					count++;
					System.out.println("-------------------------------->");
					System.out.println("-------------------------------->");
					System.out.println("-------------------------------->Record inseriti in " + action + " su Endpoint " + (100 * count) + " " + Watch.getTime());
					System.out.println("-------------------------------->");
					System.out.println("-------------------------------->");
					System.out.println("-------------------------------->");
				}
			}
			System.out.println("RECORD NON PUBBLICATI CORRETTAMENTE = " + countFailed);
			
		} catch (Exception e) {
			try {
				repositoryConnection.rollback();
			} catch (RepositoryException e1) {
				e1.printStackTrace();
			}
		} finally {
			for (String chiave : endpointsMap.keySet()) {
				if (endpointsMap.get(chiave) != null && !endpointsMap.get(chiave).isClosed()) {
					System.out.println("------- closing " + chiave);
					endpointsMap.get(chiave).close();
				}
			}
			if (repositoryConnection != null)
				try {
					repositoryConnection.commit();
					repositoryConnection.getRepository().shutDown();
					repositoryConnection.close();
				} catch (RepositoryException e) {
					// e.printStackTrace();
				}
		}

	}

	private void addRecords(ByteArrayInputStream byteArrayInputStream, URI[] addDeletedToGraphs, Map<String, VirtGraph> endpointsMap) {
		for (URI uri : addDeletedToGraphs) {
			addRecord(byteArrayInputStream, uri, endpointsMap);
		}
	}

	private void addRecord(ByteArrayInputStream byteArrayInputStream, URI uri, Map<String, VirtGraph> endpointsMap) {

		if (!endpointsMap.containsKey(VIRTUOSO_INSTANCE + "+" + uri.stringValue())) {
			System.out.println("------- connecting to  " + VIRTUOSO_INSTANCE + " for graph " + uri.stringValue());
			endpointsMap.put(VIRTUOSO_INSTANCE + "+" + uri.stringValue(), new VirtGraph(uri.stringValue(), "jdbc:virtuoso://" + VIRTUOSO_INSTANCE + ":" + VIRTUOSO_PORT, VIRTUOSO_USERNAME, VIRTUOSO_PASSWORD));
			System.out.println("------- connected!");
		}
		VirtGraph virtGraph = endpointsMap.get(VIRTUOSO_INSTANCE + "+" + uri.stringValue());
		Model m = ModelFactory.createDefaultModel();
		m.read(byteArrayInputStream, "");

		Map<String, Node> sub = new HashMap<String, Node>();

		for (StmtIterator i = m.listStatements(); i.hasNext();) {
			Statement s = (Statement) i.next();
			// System.out.println(s.getSubject() + " -- " + s.getPredicate() +
			// " -- " + s.getObject());
			Triple a = s.asTriple();
			if (a.getObject().isBlank()) {
				String obs = a.getObject().getBlankNodeLabel();
				if (!sub.containsKey(obs)) {
					sub.put(obs, Node.createURI("nodeID://b" + System.nanoTime()));
				}
			}
			if (a.getSubject().isBlank()) {
				String obs = a.getSubject().getBlankNodeLabel();
				if (!sub.containsKey(obs)) {
					sub.put(obs, Node.createURI("nodeID://b" + System.nanoTime()));
				}

			}
		}

		for (StmtIterator i = m.listStatements(); i.hasNext();) {
			Statement s = (Statement) i.next();
			// System.out.println(s.getSubject() + " -- " + s.getPredicate() +
			// " -- " + s.getObject());
			Triple a = s.asTriple();
			if (a.getObject().isBlank()) {
				String obs = a.getObject().getBlankNodeLabel();
				// System.out.println("OBJ-----" + obs);
				a = Triple.create(a.getSubject(), a.getPredicate(), sub.get(obs));
				// System.out.println("||||||\\ " + a.getSubject() + " -- " +
				// a.getPredicate() + " -- " + a.getObject());
			}
			if (a.getSubject().isBlank()) {
				String obs = a.getSubject().getBlankNodeLabel();
				// System.out.println("SUBJ-----" + obs);
				a = Triple.create(sub.get(obs), a.getPredicate(), a.getObject());
				// System.out.println("||||||\\ " + a.getSubject() + " -- " +
				// a.getPredicate() + " -- " + a.getObject());
			}
			virtGraph.add(a);
		}

	}

	private void removeRecord(String about, URI object, Value object2, Resource graph, RepositoryConnection repositoryConnection) throws RepositoryException {
		System.out.println("removing single record");
		URI rdfIdentifier = repositoryConnection.getValueFactory().createURI(about);

		StringBuilder query = new StringBuilder();
		query.append("SELECT ?bn FROM <" + graph.stringValue() + "> WHERE {");
		query.append("{<" + about + "> ?p ?o");
		query.append(". FILTER(isBlank(?o))");
		query.append(". ?o ?c ?s");
		query.append(". FILTER(isBlank(?s))");
		query.append(". ?s ?d ?bn");
		query.append(". FILTER(isBlank(?bn))}");
		query.append("UNION{");
		query.append("<" + about + "> ?p ?o");
		query.append(". FILTER(isBlank(?o))");
		query.append(". ?o ?c ?bn");
		query.append(". FILTER(isBlank(?bn))}");
		query.append("UNION{");
		query.append(" <" + about + "> ?p ?bn");
		query.append(". FILTER(isBlank(?bn))");
		query.append("}}");

		List<HashMap<String, Value>> riga = QueryRDF.doTupleQuery(repositoryConnection, query.toString());

		for (int ia = 0; ia < riga.size(); ia++) {
			HashMap<String, Value> colonne = riga.get(ia);
			System.out.println(ia + "\t" + QueryRDF.getValue("bn", colonne));
			BNode bnRdfIdentifier = repositoryConnection.getValueFactory().createBNode(QueryRDF.getValue("bn", colonne));
			repositoryConnection.remove(bnRdfIdentifier, null, null, graph);
		}

		repositoryConnection.remove(rdfIdentifier, null, null, graph);

	}

	@Override
	public boolean checkPublished(int id_record, int idArchive, String rdfAbout) throws SQLException, RepositoryException, MalformedQueryException, QueryEvaluationException {
		RepositoryConnection con = null;
		try {
			Repository repository = new VirtuosoRepository("jdbc:virtuoso://" + VIRTUOSO_INSTANCE + ":" + VIRTUOSO_PORT, VIRTUOSO_USERNAME, VIRTUOSO_PASSWORD);
			con = repository.getConnection();
			String query = "ASK FROM <" + getArchiveGraphs(repository, idArchive)[1] + "> WHERE {<" + defaultDomain + rdfAbout + "> ?predicato ?oggetto . FILTER(?predicato != <http://lod.xdams.org/ontologies/ods/deleted> AND ?predicato !=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>)}";
			return QueryRDF.doBooleanQuery(con, query);
		} catch (RepositoryException e) {
			throw e;
		} catch (MalformedQueryException e) {
			throw e;
		} catch (QueryEvaluationException e) {
			throw e;
		} finally {
			if (con != null)
				try {
					con.getRepository().shutDown();
					con.close();
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public boolean checkPublishedArchive(int idArchive) throws SQLException, RepositoryException, MalformedQueryException, QueryEvaluationException {
		RepositoryConnection con = null;
		try {
			Repository repository = new VirtuosoRepository("jdbc:virtuoso://" + VIRTUOSO_INSTANCE + ":" + VIRTUOSO_PORT, VIRTUOSO_USERNAME, VIRTUOSO_PASSWORD);
			con = repository.getConnection();
			String query = "ASK FROM <" + getArchiveGraphs(repository, idArchive)[1] + "> WHERE {?soggetto ?predicato ?oggetto . FILTER(?predicato != <http://lod.xdams.org/ontologies/ods/deleted> AND ?predicato !=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>)}";
			return QueryRDF.doBooleanQuery(con, query);
		} catch (RepositoryException e) {
			throw e;
		} catch (MalformedQueryException e) {
			throw e;
		} catch (QueryEvaluationException e) {
			throw e;
		} finally {
			if (con != null)
				try {
					con.getRepository().shutDown();
					con.close();
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
		}
	}

	public List<HashMap<String, Value>> executeQuery(String query) {
		RepositoryConnection con = null;
		try {
			Repository repository = new VirtuosoRepository("jdbc:virtuoso://" + VIRTUOSO_INSTANCE + ":" + VIRTUOSO_PORT, VIRTUOSO_USERNAME, VIRTUOSO_PASSWORD);
			con = repository.getConnection();
			return QueryRDF.doTupleQuery(con, query.toString());
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (con != null)
				try {
					con.getRepository().shutDown();
					con.close();
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
		}
		return new ArrayList<HashMap<String, Value>>();

	}

	public URI[] getArchiveGraphs(Repository repository, int idArchive) {
		URI[] uris = null;
		if (repository == null) {
			repository = new VirtuosoRepository("jdbc:virtuoso://" + VIRTUOSO_INSTANCE + ":" + VIRTUOSO_PORT, VIRTUOSO_USERNAME, VIRTUOSO_PASSWORD);
		}

		if (endPointArchiveDomainsUris != null && endPointArchiveDomainsUris.get(idArchive) != null) {
			uris = endPointArchiveDomainsUris.get(idArchive);
		} else {
			List<String> archiveDomainsList = endPointArchiveDomains.get(idArchive);
			if (archiveDomainsList != null) {
				uris = new URI[archiveDomainsList.size() + 1];
				uris[0] = repository.getValueFactory().createURI(defaultDomain);
				for (int i = 0; i < archiveDomainsList.size(); i++) {
					uris[(i + 1)] = repository.getValueFactory().createURI(archiveDomainsList.get(i));
				}
			} else {
				uris = new URI[2];
				uris[0] = repository.getValueFactory().createURI(defaultDomain);
				uris[1] = repository.getValueFactory().createURI(defaultDomain + idArchive);
			}
			if (endPointArchiveDomainsUris == null) {
				endPointArchiveDomainsUris = new HashMap<Integer, URI[]>();
			}
			endPointArchiveDomainsUris.put(idArchive, uris);
		}
		return uris;
	}

	public void setVIRTUOSO_INSTANCE(String vIRTUOSOINSTANCE) {
		VIRTUOSO_INSTANCE = vIRTUOSOINSTANCE;
	}

	public void setVIRTUOSO_PORT(int vIRTUOSOPORT) {
		VIRTUOSO_PORT = vIRTUOSOPORT;
	}

	public void setVIRTUOSO_USERNAME(String vIRTUOSOUSERNAME) {
		VIRTUOSO_USERNAME = vIRTUOSOUSERNAME;
	}

	public void setVIRTUOSO_PASSWORD(String vIRTUOSOPASSWORD) {
		VIRTUOSO_PASSWORD = vIRTUOSOPASSWORD;
	}

	public void setEndPointArchiveDomains(Map<Integer, List<String>> endPointArchiveDomains) {
		this.endPointArchiveDomains = endPointArchiveDomains;
	}

	@Override
	protected void clearEndpoint(int idArchive) throws RepositoryException {
		RepositoryConnection repositoryConnection = null;
		Repository repository = new VirtuosoRepository("jdbc:virtuoso://" + VIRTUOSO_INSTANCE + ":" + VIRTUOSO_PORT, VIRTUOSO_USERNAME, VIRTUOSO_PASSWORD);
		try {
			repositoryConnection = repository.getConnection();
			URI[] graphs = null;
			graphs = getArchiveGraphs(repository, idArchive);
			for (int i = 0; i < graphs.length; i++) {
				if (i > 0) {
					repositoryConnection.clear(graphs[i]);
				}
			}
		} catch (RepositoryException e) {
			if (repositoryConnection != null)
				try {
					repositoryConnection.rollback();
				} catch (RepositoryException e1) {
					e1.printStackTrace();
				}
		} finally {
			if (repositoryConnection != null)
				try {
					repositoryConnection.commit();
					repositoryConnection.getRepository().shutDown();
					repositoryConnection.close();
				} catch (RepositoryException e) {
				}
		}
	}

}
