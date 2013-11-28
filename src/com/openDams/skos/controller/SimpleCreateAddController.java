package com.openDams.skos.controller;


import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.util.Version;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.RecordType;
import com.openDams.bean.RecordTypes;
import com.openDams.bean.Records;
import com.openDams.bean.RelationType;
import com.openDams.bean.Relations;
import com.openDams.bean.RelationsId;
import com.openDams.bean.XmlId;
import com.openDams.index.factory.IndexManager;
import com.openDams.index.factory.LuceneFactory;
import com.openDams.index.searchers.Searcher;
import com.openDams.relations.managing.RelationsManager;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;
import com.regesta.framework.util.DateUtility;
import com.regesta.framework.util.StringUtility;

public class SimpleCreateAddController implements Controller{
	private  OpenDamsService service ;
	private  Searcher searcher = null;
	private  IndexManager indexManager = null;
	private  TitleManager titleManager ;
	private  RelationsManager relationsManager = null;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;		
		String action = arg0.getParameter("action");
		if(action!=null){
			if(action.equals("createAdd")){
				
				Enumeration<String> enumeration =arg0.getParameterNames();
				while (enumeration.hasMoreElements()) {
					String string = (String) enumeration.nextElement();
					System.out.println(string+"="+arg0.getParameter(string));
				}
				
				int idRecordFather = 0;
				try {
					idRecordFather = Integer.parseInt(arg0.getParameter("voce"));
				} catch (NumberFormatException e) {
				}
				Archives archives = (Archives)service.getObject(Archives.class, new Integer(arg0.getParameter("idArchive")));
				@SuppressWarnings("unchecked")
				int idRecordArchive = ((List<Records>)service.getListFromSQL(Records.class,"SELECT * FROM records where ref_id_archive="+arg0.getParameter("idArchive")+" and ref_id_record_type="+RecordType.ARCHIVE+";")).get(0).getIdRecord();
				if(idRecordFather!=0){
					System.out.println("creo la sottovece");
					Records recordFather = (Records)service.getObject(Records.class, idRecordFather);
					String labelFather = recordFather.getXMLReader().getNodeValue("/rdf:RDF/skos:Concept/rdfs:label/text()");
					XmlId xmlId = archives.getXmlId();
					xmlId.setIdXml(xmlId.getIdXml()+1);
					String xmlIdRecord = archives.getArchiveClass()+"/"+archives.getArchiveXmlPrefix()+StringUtility.fillZero(Integer.toString(xmlId.getIdXml()),7);
					String xml = getRDF(xmlIdRecord, labelFather+" - "+arg0.getParameter("suddivisione").trim());
					Records record = addRecord(archives, xmlId, xml, xmlIdRecord);
					int idRecord = record.getIdRecord();
					byte[] xmlByte = record.getXml();
					xmlByte = addRelation(idRecordArchive, idRecord, RelationType.IS,xmlByte);
					xmlByte = addRelation(idRecordFather, idRecord, RelationType.BT,xmlByte);
					xmlByte = addRelation(idRecord,new Integer(arg0.getParameter("idRecord")), 25,xmlByte);
					record.setXml(xmlByte);
					service.update(record);
				}else if(arg0.getParameter("suddivisione").trim().equals("")){
					System.out.println("creo la voce");
					XmlId xmlId = archives.getXmlId();
					xmlId.setIdXml(xmlId.getIdXml()+1);
					String xmlIdRecord = archives.getArchiveClass()+"/"+archives.getArchiveXmlPrefix()+StringUtility.fillZero(Integer.toString(xmlId.getIdXml()),7);
					String xml = getRDF(xmlIdRecord, arg0.getParameter("voce"));
					Records record = addRecord(archives, xmlId, xml, xmlIdRecord);
					int idRecord = record.getIdRecord();
					byte[] xmlByte = record.getXml();
					xmlByte = addRelation(idRecordArchive, idRecord, RelationType.IS,xmlByte);
					xmlByte = addRelation(idRecordArchive, idRecord, RelationType.BT,xmlByte);
					xmlByte = addRelation(idRecord,new Integer(arg0.getParameter("idRecord")), 25,xmlByte);
					record.setXml(xmlByte);
					service.update(record);
				}else if(!arg0.getParameter("voce").trim().equals("") && !arg0.getParameter("suddivisione").trim().equals("")){
					System.out.println("creo la voce e la sottovoce");
					XmlId xmlId = archives.getXmlId();
					xmlId.setIdXml(xmlId.getIdXml()+1);
					String xmlIdRecord = archives.getArchiveClass()+"/"+archives.getArchiveXmlPrefix()+StringUtility.fillZero(Integer.toString(xmlId.getIdXml()),7);
					String xml = getRDF(xmlIdRecord, arg0.getParameter("voce"));
					Records record = addRecord(archives, xmlId, xml, xmlIdRecord);
					int idRecord = record.getIdRecord();
					byte[] xmlByte = record.getXml();
					xmlByte = addRelation(idRecordArchive, idRecord, RelationType.IS,xmlByte);
					xmlByte = addRelation(idRecordArchive, idRecord, RelationType.BT,xmlByte);
					record.setXml(xmlByte);
					service.update(record);
					idRecordFather = idRecord; 
					xmlId = archives.getXmlId();
					xmlId.setIdXml(xmlId.getIdXml()+1);
					xmlIdRecord = archives.getArchiveClass()+"/"+archives.getArchiveXmlPrefix()+StringUtility.fillZero(Integer.toString(xmlId.getIdXml()),7);
					xml = getRDF(xmlIdRecord, arg0.getParameter("voce")+" - "+arg0.getParameter("suddivisione").trim());
					record = addRecord(archives, xmlId, xml, xmlIdRecord);
					idRecord = record.getIdRecord();
					xmlByte = record.getXml();
					xmlByte = addRelation(idRecordArchive, idRecord, RelationType.IS,xmlByte);
					xmlByte = addRelation(idRecordFather, idRecord, RelationType.BT,xmlByte);
					xmlByte = addRelation(idRecord,new Integer(arg0.getParameter("idRecord")), 25,xmlByte);
					record.setXml(xmlByte);
					service.update(record);
				}
				mav = new ModelAndView("skos/simpleCreateAdd");
			}else if(action.equals("getList")){
				Analyzer analyzer = LuceneFactory.getAnalyzer(indexManager.getAnalyzerClass());
		        BooleanQuery query = new BooleanQuery();
		        String toSearch="";
		        QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,"contents", analyzer);		 	
				Document[] hits = null;
				int totalCount = 0;
				int limit = 0;
		        if(arg0.getParameter("query")!=null){
		        	toSearch=arg0.getParameter("query")+"*";
		        	System.out.println(">>>>>>>>>>>>>>>>>>>>>toSearch<<<<<<<<<<<<<<<<<"+toSearch);
		        	query.add(parser.parse(toSearch), BooleanClause.Occur.MUST);  
		        	System.out.println(">>>>>>>>>>>>>>>>>>>>>start<<<<<<<<<<<<<<<<<"+arg0.getParameter("start"));
		        	System.out.println(">>>>>>>>>>>>>>>>>>>>>limit<<<<<<<<<<<<<<<<<"+arg0.getParameter("limit"));
		        	limit = new Integer(arg0.getParameter("limit"));
		        	hits = searcher.singleSearchPaged(query,null,limit, new Integer(arg0.getParameter("start")), arg0.getParameter("idArchive"), false);
		        	System.out.println(">>>>>>>>>>>>>>>>>>>>>totalCount<<<<<<<<<<<<<<<<<"+totalCount);
		        }
				System.out.println(">>>>>>>>>>>>>>>>>>>>>hits<<<<<<<<<<<<<<<<<"+hits.length);
				mav = new ModelAndView("skos/skos_concepts_json");
				mav.addObject("results", hits);
				mav.addObject("titleManager", titleManager);
				mav.addObject("limit", limit);
			}
			
		}
		
		return mav;
	}
	
	public Records addRecord(Archives archives,XmlId xmlId,String xml,String xmlIdRecord){
		Records record = new Records();
		record.setXml(xml.getBytes());
		record.setXmlId(xmlIdRecord);
		record.setRecordTypes((RecordTypes)service.getObject(RecordTypes.class,RecordType.RECORD));				
		record.setArchives(archives);
		record.setCreationDate(new Date());
		record.setModifyDate(new Date());
		record.setPosition(0);
		record.setDepth(0);				
		service.add(record);
		service.update(xmlId);
		return record;
	}
	
	public byte[] addRelation(int idFather,int idRecord,int relationType,byte[] xml) throws Exception{
		Relations relations = new Relations();
		RelationsId relationsId= new RelationsId(idRecord,idFather,relationType);
		relations.setId(relationsId);
		relations.setAsynctask(false);
		service.add(relations);
		return relationsManager.removeRelationFromXml(relations.getId().getRefIdRelationType(), relations.getId().getRefIdRecord1(), relations.getId().getRefIdRecord2(),xml);
	}
	public String getRDF(String xmlIdRecord,String label){
		UserDetails user =  (UserDetails)((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
		String  xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; 
				xml+="<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:nfo=\"http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#\" xmlns:isbd=\"http://iflastandards.info/ns/isbd/elements/\" xmlns:bibo=\"http://purl.org/ontology/bibo/\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:skos=\"http://www.w3.org/2008/05/skos#\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:bio=\"http://purl.org/vocab/bio/0.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"   xmlns:ods=\"http://lod.xdams.org/ontologies/ods/\" xmlns:time=\"http://www.w3.org/2006/time#\" xmlns:org=\"http://www.w3.org/ns/org#\"     >";       
				xml+="  <skos:Concept rdf:about=\""+xmlIdRecord+"\">"; 
				xml+="    <skos:prefLabel>"+label+"</skos:prefLabel>"; 
				xml+="    <rdfs:label>"+label+"</rdfs:label>";
				xml+="				<skos:changeNote rdf:parseType=\"Resource\">";
				xml+="					<rdf:value>create</rdf:value>";
				xml+="					<dc:creator rdf:parseType=\"Literal\">"+user.getName()+" "+user.getLastname()+"</dc:creator>";
				xml+="					<dc:date>"+DateUtility.getMySQLSystemDate().replaceAll("-", "")+"</dc:date>";
				xml+="				</skos:changeNote>";
				xml+="  </skos:Concept>"; 
				xml+="</rdf:RDF>"; 
		return xml;		
	}
	
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public void setRelationsManager(RelationsManager relationsManager) {
		this.relationsManager = relationsManager;
	}
	
}
