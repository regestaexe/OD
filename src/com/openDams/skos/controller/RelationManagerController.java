package com.openDams.skos.controller;


import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Relations;
import com.openDams.bean.RelationsId;
import com.openDams.services.OpenDamsService;

public class RelationManagerController implements Controller{
	private  OpenDamsService service ;
	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if(arg0.getParameter("action")!=null){
			//int id_relation_type = new Integer(arg0.getParameter("relation_type"));
			if(arg0.getParameter("action").trim().equalsIgnoreCase("add")){
				Relations relations = new Relations();
				RelationsId relationsId= new RelationsId(new Integer(arg0.getParameter("idRecord")),new Integer(arg0.getParameter("id_record_relation")),new Integer(arg0.getParameter("relation_type")));
				relations.setId(relationsId);
				relations.setRelationOrder(0);
				service.add(relations);								
			}else if(arg0.getParameter("action").trim().equalsIgnoreCase("add_note")){
				try {
					RelationsId relationsId= new RelationsId(new Integer(arg0.getParameter("idRecord")),new Integer(arg0.getParameter("id_record_relation")),new Integer(arg0.getParameter("relation_type")));
					Relations relations =  (Relations)service.getObject(Relations.class,relationsId);
					String note = null;
					if(arg0.getParameter("note")!=null && !arg0.getParameter("note").equals("null")){
						note = URLDecoder.decode(arg0.getParameter("note"), "UTF-8");
					}
					relations.setNote(note);
					service.update(relations);
				} catch (Exception e) {
					RelationsId relationsId= new RelationsId(new Integer(arg0.getParameter("id_record_relation")),new Integer(arg0.getParameter("idRecord")),new Integer(arg0.getParameter("relation_type")));
					Relations relations =  (Relations)service.getObject(Relations.class,relationsId);
					String note = null;
					if(arg0.getParameter("note")!=null && !arg0.getParameter("note").equals("null")){
						note = URLDecoder.decode(arg0.getParameter("note"), "UTF-8");
					}
					relations.setNote(note);
					service.update(relations);
				}	
			}else if(arg0.getParameter("action").trim().equalsIgnoreCase("reorder_relations")){
				if(arg0.getParameter("relationsToSort")!=null){
				String[] relationsList = arg0.getParameter("relationsToSort").split(";");				
				for (int i = 0; i < relationsList.length; i++) {
					String idRecord1=StringUtils.substringBefore(relationsList[i], "_");
					String idRecord2=StringUtils.substringBetween(relationsList[i], "_");
					String order=StringUtils.substringAfterLast(relationsList[i], "_");
					String relation_type = arg0.getParameter("relation_type");
					String relation_type_alternative = arg0.getParameter("relation_type");
					String relation_type_alternative2 = arg0.getParameter("relation_type");
					if(relation_type.equals("1")){
						relation_type_alternative = "11";
						relation_type_alternative2 = "2";
					}else if(relation_type.equals("2")){
						relation_type_alternative= "10";
						relation_type_alternative2 = "1";
					}
					System.out.println("relazioni>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+idRecord1+"-"+idRecord2+"-"+arg0.getParameter("relation_type"));
					try {
						RelationsId relationsId= new RelationsId(new Integer(idRecord1),new Integer(idRecord2),new Integer(relation_type));
						Relations relations =  (Relations)service.getObject(Relations.class,relationsId);
						relations.setRelationOrder(new Integer(order));
						service.update(relations);
					} catch (Exception e) {
						try {
							RelationsId relationsId= new RelationsId(new Integer(idRecord2),new Integer(idRecord1),new Integer(relation_type));
							Relations relations =  (Relations)service.getObject(Relations.class,relationsId);
							relations.setRelationOrder(new Integer(order));
							service.update(relations);
						} catch (Exception e1) {
							try {
								RelationsId relationsId= new RelationsId(new Integer(idRecord1),new Integer(idRecord2),new Integer(relation_type_alternative));
								Relations relations =  (Relations)service.getObject(Relations.class,relationsId);
								relations.setRelationOrder(new Integer(order));
								service.update(relations);
							} catch (Exception e2) {
								try {
									RelationsId relationsId= new RelationsId(new Integer(idRecord2),new Integer(idRecord1),new Integer(relation_type_alternative));
									Relations relations =  (Relations)service.getObject(Relations.class,relationsId);
									relations.setRelationOrder(new Integer(order));
									service.update(relations);
								} catch (Exception e3) {
									try {
										RelationsId relationsId= new RelationsId(new Integer(idRecord1),new Integer(idRecord2),new Integer(relation_type_alternative2));
										Relations relations =  (Relations)service.getObject(Relations.class,relationsId);
										relations.setRelationOrder(new Integer(order));
										service.update(relations);
									} catch (Exception e4) {
										RelationsId relationsId= new RelationsId(new Integer(idRecord2),new Integer(idRecord1),new Integer(relation_type_alternative2));
										Relations relations =  (Relations)service.getObject(Relations.class,relationsId);
										relations.setRelationOrder(new Integer(order));
										service.update(relations);
									}
									
								}
							}
						}
					}
				}
				}
			}else{
				//System.out.println("id_record_relation="+arg0.getParameter("id_record_relation"));
				//System.out.println("idRecord"+arg0.getParameter("idRecord"));
				//System.out.println("relation_type="+arg0.getParameter("relation_type"));
				try {					
					//List<Relations> relationsList =(List<Relations>) service.getListFromSQL(Relations.class,"SELECT * FROM relations where (ref_id_record_1="+arg0.getParameter("idRecord")+" and ref_id_record_2="+arg0.getParameter("id_record_relation")+" and (ref_id_relation_type=1 or ref_id_relation_type=2 or ref_id_relation_type=10 or ref_id_relation_type=11)) or (ref_id_record_1="+arg0.getParameter("id_record_relation")+" and ref_id_record_2="+arg0.getParameter("idRecord")+" and (ref_id_relation_type=1 or ref_id_relation_type=2 or ref_id_relation_type=10 or ref_id_relation_type=11));");
					List<Relations> relationsList = null;
					if(arg0.getParameter("relation_type").equals("1") || arg0.getParameter("relation_type").equals("2") || arg0.getParameter("relation_type").equals("10") || arg0.getParameter("relation_type").equals("11")){
						relationsList =(List<Relations>) service.getListFromSQL(Relations.class,"SELECT * FROM relations where (ref_id_record_1="+arg0.getParameter("idRecord")+" and ref_id_record_2="+arg0.getParameter("id_record_relation")+" and (ref_id_relation_type=1 or ref_id_relation_type=2 or ref_id_relation_type=10 or ref_id_relation_type=11)) or (ref_id_record_1="+arg0.getParameter("id_record_relation")+" and ref_id_record_2="+arg0.getParameter("idRecord")+" and (ref_id_relation_type=1 or ref_id_relation_type=2 or ref_id_relation_type=10 or ref_id_relation_type=11));");
					}else{
						relationsList =(List<Relations>) service.getListFromSQL(Relations.class,"SELECT * FROM relations where (ref_id_record_1="+arg0.getParameter("idRecord")+" and ref_id_record_2="+arg0.getParameter("id_record_relation")+" and ref_id_relation_type="+arg0.getParameter("relation_type")+") or (ref_id_record_1="+arg0.getParameter("id_record_relation")+" and ref_id_record_2="+arg0.getParameter("idRecord")+" and ref_id_relation_type="+arg0.getParameter("relation_type")+");");
					}
					for (int i = 0; i < relationsList.size(); i++) {
						service.remove(relationsList.get(i));
					}			
				} catch (Exception e) {
					throw e;
				}				
			}
		}
		mav = new ModelAndView("skos/relations_manager_result");
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	
}
