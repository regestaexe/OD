package com.openDams.documental.controller;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Records;
import com.openDams.bean.Relations;
import com.openDams.bean.RelationsId;
import com.openDams.services.OpenDamsService;
import com.regesta.framework.xml.XMLBuilder;

public class RelationManagerController implements Controller {
	private OpenDamsService service;
	private Map<String, String> relazioni;

	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		mav = new ModelAndView("documental/simpleResponse");

		if (arg0.getParameter("action") != null) {
			System.out.println("RelationManagerController.handleRequest() action:" + arg0.getParameter("action"));
			if (arg0.getParameter("action").trim().equalsIgnoreCase("add")) {
				Relations relations = new Relations();
				RelationsId relationsId = new RelationsId(new Integer(arg0.getParameter("idRecord")), new Integer(arg0.getParameter("id_record_relation")), new Integer(arg0.getParameter("relation_type")));
				List<Relations> relazionis = (List<Relations>) service.getListFromSQL(Relations.class, "SELECT * FROM relations WHERE ref_id_record_1 = " + arg0.getParameter("idRecord") + " AND ref_id_record_2=" + arg0.getParameter("id_record_relation") + " AND ref_id_relation_type=" + arg0.getParameter("relation_type"));
				if (relazionis.size() == 0) {
					relations.setId(relationsId);
					service.add(relations);
					/*Records toModify = (Records) service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));
					service.update(toModify);*/
					mav.addObject("content", "");
				} else {
					mav.addObject("content", "ko");
				}

			} else if (arg0.getParameter("action").trim().equalsIgnoreCase("deleteFromXpath")) {
				Records record = (Records) service.getObject(Records.class, Integer.parseInt(arg0.getParameter("idRecord")));
				XMLBuilder recordBuilder = new XMLBuilder(new ByteArrayInputStream(record.getXml()));
				String xPath = URLDecoder.decode(arg0.getParameter("xpath"), "UTF-8");

				DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Records.class);
				detachedCriteria.add(Restrictions.like("title", recordBuilder.getNodeValue(xPath), MatchMode.START));
				List<Records> list = (List<Records>) service.getPagedList(detachedCriteria, 0, 1);
				//System.out.println("trovati record per xpath " + xPath + " " + list.size());
				//System.out.println("record: " + record.getIdRecord());
				/*
				 * TODO: GESTIRE RELAZIONI MULTIPLE TRA RECORD IN DIFFERENTI
				 * XPATH (relation type)
				 */
				for (Records records : list) {
					//System.out.println("verifica records collegati: " + records.getIdRecord());
					Set<Relations> relazioni1 = records.getRelationsesForRefIdRecord1();
					for (Relations relations : relazioni1) {
						//System.out.println("relations.getRecordsByRefIdRecord2(): " + relations.getRecordsByRefIdRecord2().getIdRecord());
						if (relations.getRecordsByRefIdRecord2().equals(record)) {
							System.out.println("eliminato!");
							relazioni1.remove(relations);
							//service.remove(relations);
						}
					}
					Set<Relations> relazioni2 = records.getRelationsesForRefIdRecord2();
					for (Relations relations : relazioni2) {
						//System.out.println("relations.getRecordsByRefIdRecord1(): " + relations.getRecordsByRefIdRecord1().getIdRecord());
						if (relations.getRecordsByRefIdRecord1().equals(record)) {
							System.out.println("eliminato!");
							relazioni2.remove(relations);
							//service.remove(relations);
						}
					}
				}
				service.update(record);
				/*recordBuilder.deleteNode(StringUtils.substringBeforeLast(xPath, "/"));
				System.out.println("dopo cancellazione " + recordBuilder.getXML("UTF-8"));
				record.setXml(recordBuilder.getXML("UTF-8").getBytes());
				service.update(record);*/
				// url : "ajax/documental_relation_manager.html?idRecord=" +
				// opts.idRecord + "&xpath=" + escape(opts.xpath) +
				// "&action=deleteFromXpath",
				mav.addObject("content", "");
			} else if (arg0.getParameter("action").trim().equalsIgnoreCase("delete")) {

				try {
					System.out.println("Cancello la relazione " + arg0.getParameter("idRecord") + "," + arg0.getParameter("id_record_relation") + "," + arg0.getParameter("relation_type"));
					/*RelationsId relationsId = new RelationsId(new Integer(arg0.getParameter("idRecord")), new Integer(arg0.getParameter("id_record_relation")), new Integer(arg0.getParameter("relation_type")));
					System.out.println("trovata? " + relationsId);
					Relations relations = (Relations) service.getObject(Relations.class, relationsId);
					*/
					Relations relations = (Relations) service.getListFromSQL(Relations.class, "SELECT * FROM relations where ref_id_record_1="+arg0.getParameter("idRecord")+" and ref_id_record_2="+arg0.getParameter("id_record_relation")+" and ref_id_relation_type="+arg0.getParameter("relation_type")+";").get(0);
					System.out.println("relations " + relations);
					System.out.println("relations.getId() " + relations.getId());
					System.out.println("relations.getRecordsByRefIdRecord1() " + relations.getRecordsByRefIdRecord1());
					System.out.println("relations.getRecordsByRefIdRecord2() " + relations.getRecordsByRefIdRecord2());
					service.remove(relations);
				} catch (Exception e) {
					System.out.println("Cancello la relazione secondo tentativo! l'inversa " + arg0.getParameter("id_record_relation") + "," + arg0.getParameter("idRecord") + "," + arg0.getParameter("relation_type"));
					/*RelationsId relationsId = new RelationsId(new Integer(arg0.getParameter("id_record_relation")), new Integer(arg0.getParameter("idRecord")), new Integer(arg0.getParameter("relation_type")));
					System.out.println("trovata? " + relationsId);
					Relations relations = (Relations) service.getObject(Relations.class, relationsId);*/
					Relations relations = (Relations) service.getListFromSQL(Relations.class, "SELECT * FROM relations where ref_id_record_1="+arg0.getParameter("id_record_relation")+" and ref_id_record_2="+arg0.getParameter("idRecord")+" and ref_id_relation_type="+arg0.getParameter("relation_type")+";").get(0);
					System.out.println("relations " + relations);
					System.out.println("relations.getId() " + relations.getId());
					System.out.println("relations.getRecordsByRefIdRecord1() " + relations.getRecordsByRefIdRecord1());
					System.out.println("relations.getRecordsByRefIdRecord2() " + relations.getRecordsByRefIdRecord2());
					service.remove(relations);
				}

				/*
				Records toRel = (Records) service.getObject(Records.class, new Integer(arg0.getParameter("id_record_relation")));
				Records toModify = (Records) service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));
				String confPrefix = arg0.getParameter("relation_type") + "-from:" + toRel.getArchives().getIdArchive() + "-to:" + toModify.getArchives().getIdArchive();
				String valRelazione = toRel.getXMLReader().getNodeValue("/rdf:RDF/*[name()!='owl:Ontology']/@rdf:about");
				if(valRelazione==null || valRelazione.equals("")){
					valRelazione = toRel.getXMLReader().getNodeValue("/rdf:RDF/*[name()!='owl:Ontology']/@rdf:ID");
				}
				toRel.closeXMLReader();
				XMLBuilder xmlToModify = new XMLBuilder(new ByteArrayInputStream(toModify.getXml()));
				xmlToModify.deleteNode(relazioni.get("removerel:" + confPrefix).replaceAll("\\{value\\}", valRelazione));
				String xmlString = xmlToModify.getXML("UTF-8");
				xmlString = xmlString.replace("xmlns=\"\"", "");
				toModify.setXml(xmlString.getBytes("UTF-8"));
				service.update(toModify);*/
				mav.addObject("content", "");
			}
		}

		return mav;
	}

	public void setService(OpenDamsService service) {
		this.service = service;
	}

	public void setRelazioni(Map<String, String> relazioni) {
		this.relazioni = relazioni;
	}

}
