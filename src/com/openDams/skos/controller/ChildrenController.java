package com.openDams.skos.controller;


import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.services.OpenDamsService;

public class ChildrenController implements Controller{
	private  OpenDamsService service ;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;		
        mav = new ModelAndView("skos/tot_children");
        if(arg0.getParameter("").equalsIgnoreCase("children")){
        	mav.addObject("count", getCountChildren(arg0));
        }else{
        	mav.addObject("count", getCountRelated(arg0));
        }
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}

	private BigInteger getCountChildren(HttpServletRequest arg0){
		try{
			return service.getCountFromSQL("SELECT count(records.id_record) FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=1 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=2 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=10");
		}catch (Exception e) {
			return new BigInteger("0");
		}
		
	}
	private BigInteger getCountRelated(HttpServletRequest arg0){
		try{
			return service.getCountFromSQL("SELECT count(records.id_record) FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=3 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=2 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+arg0.getParameter("idRecord")+" and relations.ref_id_relation_type=3");
		}catch (Exception e) {
			return new BigInteger("0");
		}
		
	}
}
