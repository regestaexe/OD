package com.openDams.skos.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Records;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;

public class VisualTabController implements Controller{
	private  OpenDamsService service ;
	private  TitleManager titleManager ;
	private int page_size = 1;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;		
		Records records = (Records)service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));
		mav = new ModelAndView("skos/visual_tab");
		getAllRelations(mav, arg0);
		mav.addObject("titleManager", titleManager);
		mav.addObject("records", records);
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
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
			recordsNT = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=2 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=10");	
			mav.addObject("recordsNT", recordsNT);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 1");
		}
		try{		
			int start = 0;
			if(arg0.getParameter("current_page")!=null){
				 int current_page = new Integer(arg0.getParameter("current_page"));
				 start = (current_page-1)*page_size;
			}
			recordsBT = (List<Records>) service.getPagedListFromSQL(Records.class,
					"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=2 or relations.ref_id_relation_type=10) union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=1 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=11",
					start, page_size);	
			mav.addObject("recordsBT", recordsBT);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 2");
		}		
		try{				 
			recordsRT = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=3 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=3");		
			mav.addObject("recordsRT", recordsRT);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 3");
		}
		try{				 
			recordsNM = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=5 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=5");	
			mav.addObject("recordsNM", recordsNM);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 5");
		}		
		try{				 
			recordsBM = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=6 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=6");	
			mav.addObject("recordsBM", recordsBM);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 6");
		}		
		try{				 
			recordsRM = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=7 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=7");	
			mav.addObject("recordsRM", recordsRM);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 7");
		}		
		try{				 
			recordsCM = (List<Records>) service.getListFromSQL(Records.class,
					"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=8 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=8");	
			mav.addObject("recordsCM", recordsCM);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 8");
		}		
		try{				 
			recordsIS = (List<Records>) service.getListFromSQL(Records.class,
					  "SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=9");
					//"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=9 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=9");
			mav.addObject("recordsIS", recordsIS);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getRelations idRelazione 9");
		}	
	}
	public int getPage_size() {
		return page_size;
	}
	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}
}
