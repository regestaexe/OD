package com.openDams.skos.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Records;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;

public class UnlinkedController implements Controller{
	private  OpenDamsService service ;
	private  TitleManager titleManager ;
	private int page_size = 1;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
        mav = new ModelAndView("skos/unlinked");
		getUnlinked(mav, arg0);
		mav.addObject("titleManager", titleManager);
		mav.addObject("page_size", page_size);
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	@SuppressWarnings("unchecked")
	private void getUnlinked(ModelAndView mav,HttpServletRequest arg0){
		List<Records> unlinkedList = null;	
		try{	
			int start = 0;
			if(arg0.getParameter("current_page")!=null){
				 int current_page = new Integer(arg0.getParameter("current_page"));
				 start = (current_page-1)*page_size;
			}
			unlinkedList = (List<Records>) service.getPagedListFromSQL(Records.class,
					//"SELECT * FROM records inner join sort_fields on records.id_record=sort_fields.ref_id_record  where id_record not in (select ref_id_record_1 from relations) and id_record not in (select ref_id_record_2 from relations) and records.ref_id_archive=1 and sort_fields.sort_name='label' order by sort_fields.sort_value",
					"SELECT * FROM records WHERE ref_id_archive = 1 and NOT EXISTS (SELECT ref_id_record_1 FROM relations WHERE ref_id_record_1 = id_record and (ref_id_relation_type=1 or ref_id_relation_type=2 or ref_id_relation_type=10 or ref_id_relation_type=11)) and NOT EXISTS (SELECT ref_id_record_2 FROM relations WHERE ref_id_record_2 = id_record and (ref_id_relation_type=1 or ref_id_relation_type=2 or ref_id_relation_type=10 or ref_id_relation_type=11))",
					start, page_size);	
			mav.addObject("unlinkedList", unlinkedList);
			mav.addObject("unlinked", service.getCountFromSQL("SELECT count(id_record) FROM records WHERE ref_id_archive = 1 and NOT EXISTS (SELECT ref_id_record_1 FROM relations WHERE ref_id_record_1 = id_record and (ref_id_relation_type=1 or ref_id_relation_type=2 or ref_id_relation_type=10 or ref_id_relation_type=11)) and NOT EXISTS (SELECT ref_id_record_2 FROM relations WHERE ref_id_record_2 = id_record and (ref_id_relation_type=1 or ref_id_relation_type=2 or ref_id_relation_type=10 or ref_id_relation_type=11))"));
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>errore in unlinkedList");
		}
	}
	public int getPage_size() {
		return page_size;
	}
	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}
}
