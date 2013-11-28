package com.openDams.skos.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Records;
import com.openDams.services.OpenDamsService;

public class FathersFinderController implements Controller{
	private  OpenDamsService service ;
	private  ArrayList<Records> fathers = null;
	private  ArrayList<Integer> positions = null;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		Records records = (Records)service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));
		fathers = new ArrayList<Records>();
		positions = new ArrayList<Integer>();
		while(getAllFathers(new Integer(arg0.getParameter("idRecord"))));
		mav = new ModelAndView("skos/fathers_finder");
		mav.addObject("records", records);
		mav.addObject("fathers", fathers);
		mav.addObject("positions", positions);    
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	@SuppressWarnings("unchecked")
	private boolean getAllFathers(int id_record){
		try{
			Records records = ((List<Records>) service.getListFromSQL(Records.class,
					"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+id_record+" and (relations.ref_id_relation_type=2 or relations.ref_id_relation_type=10) union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+id_record+" and relations.ref_id_relation_type=1 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+id_record+" and relations.ref_id_relation_type=11")).get(0);
			fathers.add(records);
			//System.out.println(records.getTitle());
			try {
				List<Records> list = (List<Records>) service.getListFromSQL(Records.class,"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+records.getIdRecord()+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+records.getIdRecord()+" and relations.ref_id_relation_type=2 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+records.getIdRecord()+" and relations.ref_id_relation_type=10");
				//System.out.println("list size = "+list.size()+" pre id "+records.getIdRecord()+" con titolo = "+records.getTitle());
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i).getIdRecord()==id_record){
					    //System.out.println("LA POSIZIONE di "+id_record+" é "+i+" nel padre "+records.getIdRecord()+" con titolo = "+records.getTitle());
					    positions.add(i);				    
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			return getAllFathers(records.getIdRecord());	
		}catch (Exception e) {
			return false;
		}
	}
}
