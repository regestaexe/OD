package com.openDams.skos.controller;


import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.util.Version;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.RecordTypes;
import com.openDams.bean.Records;
import com.openDams.index.searchers.Searcher;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;
import com.regesta.framework.xml.XMLBuilder;

public class DeprecateController implements Controller{
	private  OpenDamsService service ;
	private  TitleManager titleManager ;
	private  int page_size = 1;
	private Searcher searcher = null;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		
		ModelAndView mav = null;
        if(arg0.getParameter("action")==null){			
			mav = new ModelAndView("skos/deprecate/skos");
			getSkosConcepts(mav,arg0);
			Records record  = (Records)service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));
			mav.addObject("oldRecords", record);
			mav.addObject("page_size", page_size);
		    mav.addObject("titleManager", titleManager);
		}else if(arg0.getParameter("action").equalsIgnoreCase("get_children")){
			mav = new ModelAndView("skos/deprecate/skos_concepts");
			getConcepts(mav, arg0);
			mav.addObject("page_size", page_size);
		    mav.addObject("titleManager", titleManager);
		}else if(arg0.getParameter("action").equalsIgnoreCase("delete")){
			mav = new ModelAndView("skos/deprecate/deprecate");
			System.out.println("idRecord="+arg0.getParameter("idRecord"));
			System.out.println("idArchive="+arg0.getParameter("idArchive"));
			System.out.println("encoding="+arg0.getParameter("encoding"));
			Records record  = (Records)service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));		
			XMLBuilder xmlBuilder = new XMLBuilder(new ByteArrayInputStream(record.getXml()));
			xmlBuilder.deleteNode("/rdf:RDF/*/owl:deprecated");
			xmlBuilder.deleteNode("/rdf:RDF/*/dcterms:isReplacedBy");
			String xmlString = xmlBuilder.getXML(arg0.getParameter("encoding"));
			xmlString = xmlString.replace("xmlns=\"\"", "");	
			System.out.println(xmlString);
			record.setXml(xmlString.getBytes(arg0.getParameter("encoding")));
			record.setModifyDate(new Date());
			service.update(record);
		}else if(arg0.getParameter("action").equalsIgnoreCase("deprecate_to")){
			Analyzer analyzer = new KeywordAnalyzer();
			QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "id", analyzer);
			BooleanQuery query = new BooleanQuery();
			query.add(parser.parse("id:"+arg0.getParameter("id_deprecate")), BooleanClause.Occur.MUST);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<"+query.toString());
			Document doc = null;
			try {
				doc = searcher.singleSearch(query,null,1,arg0.getParameter("idArchive"),false)[0];
			} catch (ArrayIndexOutOfBoundsException e) {
				doc = searcher.singleSearch(query,null,1,arg0.getParameter("idArchive").toLowerCase(),false)[0];
			}
			mav = new ModelAndView("skos/deprecate/confirm");
			mav.addObject("titleManager", titleManager);
			mav.addObject("doc_to", doc);
		}else{
			mav = new ModelAndView("skos/deprecate/deprecate");
			System.out.println("idRecord="+arg0.getParameter("idRecord"));
			System.out.println("idArchive="+arg0.getParameter("idArchive"));
			System.out.println("encoding="+arg0.getParameter("encoding"));
			Records record  = (Records)service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));		
			XMLBuilder xmlBuilder = new XMLBuilder(new ByteArrayInputStream(record.getXml()));
			xmlBuilder.insertValueAt("/rdf:RDF/*/owl:deprecated/text()","true");
			xmlBuilder.insertValueAt("/rdf:RDF/*/dcterms:isReplacedBy/@rdf:resource",arg0.getParameter("idRecordDeprecate"));
			String xmlString = xmlBuilder.getXML(arg0.getParameter("encoding"));
			xmlString = xmlString.replace("xmlns=\"\"", "");	
			System.out.println(xmlBuilder.getXML(arg0.getParameter("encoding")));
			record.setXml(xmlString.getBytes(arg0.getParameter("encoding")));
			record.setModifyDate(new Date());
			service.update(record);
		}
       
		return mav;
	}
	@SuppressWarnings("unchecked")
	private void getSkosConcepts(ModelAndView mav,HttpServletRequest arg0){
		try {
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Records.class);
			detachedCriteria.add(Restrictions.eq("archives", (Archives)service.getObject(Archives.class, new Integer(arg0.getParameter("idArchive")))));
			detachedCriteria.add(Restrictions.eq("recordTypes", (RecordTypes)service.getObject(RecordTypes.class, Records.ARCHIVE_RECORD)));	    
			List<Records> list = (List<Records>) service.getPagedList(detachedCriteria,0,1);		
			mav.addObject("recordsList", list);
		} catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getSkosConcepts");
		}	
	}
	@SuppressWarnings("unchecked")
	private void getConcepts(ModelAndView mav,HttpServletRequest arg0){
		try{
		 int start = 0;
		 if(arg0.getParameter("current_page")!=null){
			 int current_page = new Integer(arg0.getParameter("current_page"));
			 start = (current_page-1)*page_size;
		 }
	     List<Records> list = (List<Records>) service.getPagedListFromSQL(Records.class,
					"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=2 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=10",
					start, page_size);
		 mav.addObject("recordsList", list);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getConcepts");
			e.printStackTrace();
		}
		
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
}
