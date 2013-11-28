package com.openDams.skos.controller;


import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.Records;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;

public class RecordController implements Controller{
	private  OpenDamsService service ;
	private  TitleManager titleManager ;
	private int page_size = 1;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if(arg0.getParameter("action")!=null){
			Records records = (Records)service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));
			if(arg0.getParameter("action").equalsIgnoreCase("xml_tab")){				
				mav = new ModelAndView("skos/xml_tab");			
			}if(arg0.getParameter("action").equalsIgnoreCase("edit_xml_tab")){				
				mav = new ModelAndView("skos/edit_xml_tab");			
			}else if (arg0.getParameter("action").equalsIgnoreCase("save_xml")) {
				String xml = arg0.getParameter("xml");			
				records.setXml(xml.getBytes("UTF-8"));
				records.setModifyDate(new Date());
				service.update(records);
				mav = new ModelAndView("skos/xml_tab");
			}else if(arg0.getParameter("action").equalsIgnoreCase("short_tab")){
				mav = new ModelAndView("skos/short_tab");
			}else if(arg0.getParameter("action").equalsIgnoreCase("full_tab")){
				mav = new ModelAndView("skos/full_tab");			
			}else if(arg0.getParameter("action").equalsIgnoreCase("edit_tab")){
				mav = new ModelAndView("skos/edit_tab");			
			}else if(arg0.getParameter("action").equalsIgnoreCase("skos_tab")){
				mav = new ModelAndView("skos/skos_tab");
				mav.addObject("titleManager", titleManager);
			}else if(arg0.getParameter("action").equalsIgnoreCase("relations_tab")){
				mav = new ModelAndView("skos/relations_tab");
				mav.addObject("titleManager", titleManager);
				getThesaurusArchives(mav);
			}else if(arg0.getParameter("action").equalsIgnoreCase("record_relations")){
				mav = new ModelAndView("skos/relations");
				getRelations(mav, arg0);
				mav.addObject("titleManager", titleManager);
				mav.addObject("page_size", page_size);
			}else if(arg0.getParameter("action").equalsIgnoreCase("record_relations_sort")){
				mav = new ModelAndView("skos/relations_sort");
				getRelationsToSort(mav, arg0);
				mav.addObject("titleManager", titleManager);
			}else if(arg0.getParameter("action").equalsIgnoreCase("visual_tab")){
				mav = new ModelAndView("skos/visual_tab");
				getAllRelations(mav, arg0);
				mav.addObject("titleManager", titleManager);
				mav.addObject("page_size", page_size);
			}
			mav.addObject("records", records);
	    }
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	@SuppressWarnings("unchecked")
	private void getRelations(ModelAndView mav,HttpServletRequest arg0){
		int idRelationType = new Integer(arg0.getParameter("idRelationType"));
		int start = 0;
		if(arg0.getParameter("current_page")!=null){
			 int current_page = new Integer(arg0.getParameter("current_page"));
			 start = (current_page-1)*page_size;
		}
		List<Records> list = null;
		switch (idRelationType) {
		case 2:{
			try{				 
			     list = (List<Records>) service.getPagedListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=2 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=10 order by relation_order",
						start, page_size);				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 10:{
			try{				 
			     list = (List<Records>) service.getPagedListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=2 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=10 order by relation_order",
						start, page_size);				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 1:{
			try{				 
			     list = (List<Records>) service.getPagedListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=2 or relations.ref_id_relation_type=10) union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=1 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=11 order by relation_order",
						start, page_size);				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 11:{
			try{				 
			     list = (List<Records>) service.getPagedListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=2 or relations.ref_id_relation_type=10) union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=1 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=11 order by relation_order",
						start, page_size);				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 3:{
			try{				 
			     list = (List<Records>) service.getPagedListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=3 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=3 order by relation_order",
						start, page_size);				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 4:{
			try{				 
			     list = (List<Records>) service.getPagedListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=4 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=4 order by relation_order",
						start, page_size);				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 5:{
			try{				 
			     list = (List<Records>) service.getPagedListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=5 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=5 order by relation_order",
						start, page_size);				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 6:{
			try{				 
			     list = (List<Records>) service.getPagedListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=6 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=6 order by relation_order",
						start, page_size);				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 7:{
			try{				 
			     list = (List<Records>) service.getPagedListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=7 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=7 order by relation_order",
						start, page_size);				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 8:{
			try{				 
			     list = (List<Records>) service.getPagedListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=8 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=8 order by relation_order",
						start, page_size);				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
				e.printStackTrace();
			}
			break;
		}
		case 9:{
			try{				 
			     list = (List<Records>) service.getPagedListFromSQL(Records.class,
			    		 //"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=9 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=9",
						//"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=9",
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=9 order by relation_order",
						start, page_size);				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		default:
			break;
		}
		 mav.addObject("recordsList", list);
		
	}
	@SuppressWarnings("unchecked")
	private void getRelationsToSort(ModelAndView mav,HttpServletRequest arg0){
		int idRelationType = new Integer(arg0.getParameter("idRelationType"));
		List<Records> list = null;
		switch (idRelationType) {
		case 2:{
			try{				 
			     list = (List<Records>) service.getListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=2 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=10 order by relation_order");				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 10:{
			try{				 
			     list = (List<Records>) service.getListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=2 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=10 order by relation_order");				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 1:{
			try{				 
			     list = (List<Records>) service.getListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=2 or relations.ref_id_relation_type=10) union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=1 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=11 order by relation_order");				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 11:{
			try{				 
			     list = (List<Records>) service.getListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=2 or relations.ref_id_relation_type=10) union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=1 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=11 order by relation_order");				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 3:{
			try{				 
			     list = (List<Records>) service.getListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=3 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=3 order by relation_order");				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 4:{
			try{				 
			     list = (List<Records>) service.getListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=4 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=4 order by relation_order");				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 5:{
			try{				 
			     list = (List<Records>) service.getListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=5 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=5 order by relation_order");				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 6:{
			try{				 
			     list = (List<Records>) service.getListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=6 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=6 order by relation_order");				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 7:{
			try{				 
			     list = (List<Records>) service.getListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=7 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=7 order by relation_order");				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		case 8:{
			try{				 
			     list = (List<Records>) service.getListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=8 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=8 order by relation_order");				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
				e.printStackTrace();
			}
			break;
		}
		case 9:{
			try{				 
			     list = (List<Records>) service.getListFromSQL(Records.class,
						"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=9 order by relation_order");				
			}catch (Exception e) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione "+idRelationType);
			}
			break;
		}
		default:
			break;
		}
		 mav.addObject("recordsList", list);
		
	}
	@SuppressWarnings("unchecked")
	private void getAllRelations(ModelAndView mav,HttpServletRequest arg0){
		List<Records> recordsNT = null;
		List<Records> recordsBT = null;
		List<Records> recordsRT = null;
		List<Records> recordsNM = null;
		List<Records> recordsBM = null;
		List<Records> recordsRM = null;
		List<Records> recordsCM = null;
		List<Records> recordsIS = null;		
		try{	
			int start = 0;
			if(arg0.getParameter("current_page")!=null){
				 int current_page = new Integer(arg0.getParameter("current_page"));
				 start = (current_page-1)*page_size;
			}
			recordsNT = (List<Records>) service.getPagedListFromSQL(Records.class,
					"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=2 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=10 order by relation_order",
					start, page_size);	
			mav.addObject("recordsNT", recordsNT);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 1");
		}
		try{
			recordsBT = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=2 or relations.ref_id_relation_type=10) union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=1 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=11 order by relation_order");	
			mav.addObject("recordsBT", recordsBT);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 2");
		}		
		try{				 
			recordsRT = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=3 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=3 order by relation_order");		
			mav.addObject("recordsRT", recordsRT);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 3");
		}
		try{				 
			recordsNM = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=5 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=5 order by relation_order");	
			mav.addObject("recordsNM", recordsNM);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 5");
		}		
		try{				 
			recordsBM = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=6 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=6 order by relation_order");	
			mav.addObject("recordsBM", recordsBM);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 6");
		}		
		try{				 
			recordsRM = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=7 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=7 order by relation_order");	
			mav.addObject("recordsRM", recordsRM);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 7");
		}		
		try{				 
			recordsCM = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=8 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=8 order by relation_order");	
			mav.addObject("recordsCM", recordsCM);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 8");
		}		
		try{				 
			recordsIS = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=9 order by relation_order");
			mav.addObject("recordsIS", recordsIS);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 9");
		}	
	}
	private void getThesaurusArchives(ModelAndView mav){
		mav.addObject("archives", service.getListFromSQL(Archives.class, "SELECT * FROM archives where ref_id_archive_type=3 order by label;"));
	}
	public int getPage_size() {
		return page_size;
	}
	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}
}
