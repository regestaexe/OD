package com.openDams.basket.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.ArchiveType;
import com.openDams.bean.Archives;
import com.openDams.bean.Records;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;

public class BasketController implements Controller{
	private OpenDamsService service;
	private  int page_size = 1;
	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = new ModelAndView("basket/archiveList");
		if(arg0.getParameter("action")!=null){
			if(arg0.getParameter("action").equals("archiveList")){
				mav = new ModelAndView("basket/archiveList");
				UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
				List<Archives> allArchiveList = (List<Archives>)service.getListFromSQL(Archives.class,"SELECT * FROM archives  inner join archive_user_role on archives.id_archive=archive_user_role.ref_id_archive where archive_user_role.ref_id_user="+user.getId()+" and ref_id_archive_type<"+ArchiveType.MESSAGE+" order by archives.archive_order;");
				List<Archives> archiveList = new ArrayList<Archives>();
				for (int i = 0; i < allArchiveList.size(); i++) {
					Archives archives = allArchiveList.get(i);
					int count_deleted = service.getCountFromSQL("SELECT count(id_record) FROM records where deleted = 1 and ref_id_archive = "+archives.getIdArchive()+";").intValue();
					if(count_deleted>0){
						archives.setCountDeleted(count_deleted);
						archiveList.add(archives);
					}
				}
				mav.addObject("archiveList",archiveList);
			}else if(arg0.getParameter("action").equals("deletedRecordsList")){
				mav = new ModelAndView("basket/deletedRecordsList");
				int limit = page_size;
				if(arg0.getParameter("limit")!=null){
					limit = Integer.parseInt(arg0.getParameter("limit"));
				}
				int start = 0;
				if(arg0.getParameter("start")!=null){
					start = Integer.parseInt(arg0.getParameter("start"));
				}
				mav.addObject("totDeletedRecords",service.getCountFromSQL("SELECT count(id_record) FROM records where deleted = 1 and ref_id_archive = "+arg0.getParameter("idArchive")+";").intValue());
				mav.addObject("deletedRecordsList",service.getPagedListFromSQL(Records.class, "SELECT * FROM records where deleted = 1 and ref_id_archive = "+arg0.getParameter("idArchive"),start, limit));
			}else if(arg0.getParameter("action").equals("delete")){
				mav = new ModelAndView("basket/basketResult");
				System.out.println(arg0.getParameter("action")+" "+arg0.getParameter("selectedRecords"));
				String[] idRecords = arg0.getParameter("selectedRecords").split(";");
				System.out.println(arg0.getParameter("action")+" "+arg0.getParameter("selectedRecords"));
				for (int i = 0; i < idRecords.length; i++) {
					int idRecord = Integer.parseInt(idRecords[i]);
					Records records = (Records)service.getObject(Records.class, idRecord);
					service.remove(records);
				}
			}else if(arg0.getParameter("action").equals("restore")){
				mav = new ModelAndView("basket/basketResult");
				String[] idRecords = arg0.getParameter("selectedRecords").split(";");
				System.out.println(arg0.getParameter("action")+" "+arg0.getParameter("selectedRecords"));
				for (int i = 0; i < idRecords.length; i++) {
					int idRecord = Integer.parseInt(idRecords[i]);
					Records records = (Records)service.getObject(Records.class, idRecord);
					records.setDeleted(false);
					service.update(records);
				}
			}
		}		
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}
}
