package com.openDams.skos.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.RecordTypes;
import com.openDams.bean.Records;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;

public class SKOSController implements Controller{
	private  OpenDamsService service ;
	private  TitleManager titleManager;
	private  int page_size = 1;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
        if(arg0.getParameter("action")==null || !arg0.getParameter("action").equalsIgnoreCase("get_children")){			
			mav = new ModelAndView("skos/skos");
			getSkosConcepts(mav,arg0);
		}else{
			mav = new ModelAndView("skos/skos_concepts");
			getConcepts(mav, arg0);
		}
        mav.addObject("page_size", page_size);
        mav.addObject("titleManager", titleManager);
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
	     /*List<Records> list = (List<Records>) service.getPagedListFromSQL(Records.class,
					"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=2 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=10",
					start, page_size);*/
	     
	     List<Records> list = (List<Records>) service.getPagedListFromSQL(Records.class,
	    		 "SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=2 union SELECT records.*,relations.relation_order FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=10 order by relation_order",
					start, page_size);
	     
		 mav.addObject("recordsList", list);
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in getConcepts");
			e.printStackTrace();
		}
		
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
	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}
}
