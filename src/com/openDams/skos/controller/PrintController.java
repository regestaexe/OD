package com.openDams.skos.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Records;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;

public class PrintController implements Controller{
	private  OpenDamsService service ;
	private  TitleManager titleManager;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
			
		if(arg0.getParameter("action")!=null){
			if(arg0.getParameter("print_mode").equals("branch")){
				mav = new ModelAndView("skos/print/print_branch_concepts");
			}else if(arg0.getParameter("print_mode").equals("table")){
				mav = new ModelAndView("skos/print/print_table_concepts");
			}else if(arg0.getParameter("print_mode").equals("relations")){
				mav = new ModelAndView("skos/print/print_table_relations");
			}		
			if(arg0.getParameter("action").equalsIgnoreCase("getConcept")){
				getConcept(mav, arg0);
			}else if(arg0.getParameter("action").equalsIgnoreCase("getConcepts")){
				getConcepts(mav, arg0);
			}else if(arg0.getParameter("action").equalsIgnoreCase("getRelations")){
				Records records = (Records)service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));
				mav.addObject("records", records);
				getRelations(mav, arg0);
			}			
		}		
        mav.addObject("titleManager", titleManager);
		return mav;
	}
	@SuppressWarnings("unchecked")
	private void getConcept(ModelAndView mav,HttpServletRequest arg0){
		try{
			List<Records> list = (List<Records>) service.getListFromSQL(Records.class,"SELECT * FROM records where id_record="+arg0.getParameter("idRecord"));	     
			mav.addObject("recordsList", list);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getConcepts");
			e.printStackTrace();
		}	
	}
	@SuppressWarnings("unchecked")
	private void getConcepts(ModelAndView mav,HttpServletRequest arg0){
		try{
			List<Records> list = (List<Records>) service.getListFromSQL(Records.class,"SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=2 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=10 order by relation_order");	     
			mav.addObject("recordsList", list);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getConcepts");
			e.printStackTrace();
		}	
	}
	@SuppressWarnings({"unchecked" })
	private void getRelations(ModelAndView mav,HttpServletRequest arg0){
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
    public OpenDamsService getService() {
		return service;
	}
	
	public void setService(OpenDamsService service) {
			this.service = service;
	}
	public TitleManager getTitleManager() {
		return titleManager;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
}
