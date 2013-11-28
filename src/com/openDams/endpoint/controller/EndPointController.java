package com.openDams.endpoint.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Records;
import com.openDams.dao.OpenDamsServiceProvider;
import com.openDams.endpoint.managing.EndPointManager;
import com.openDams.endpoint.managing.OpenDamsEndPointManagerFactoryProvider;
import com.openDams.security.UserDetails;

public class EndPointController implements Controller {
	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = new ModelAndView("endpoint/endpoint");
		UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
		String userName = user.getUsername();
		if (arg0.getParameter("action") != null && arg0.getParameter("action").equals("getEndPointList")) {
			mav = new ModelAndView("endpoint/endpointList");
			List<String> endPointList = (List<String>) OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getAllowedEndPointList(new Integer(arg0.getParameter("idArchive")));
			mav.addObject("endPointList", endPointList);
		} else if (arg0.getParameter("action") != null && arg0.getParameter("action").equals("getRelations")) {
			mav = new ModelAndView("endpoint/recordRelations");
			System.out.println("recordRelations idRecord = " + arg0.getParameter("idRecord"));
			int idRecord = Integer.parseInt(arg0.getParameter("idRecord"));
			List<Records> recordRelations = (List<Records>) OpenDamsServiceProvider.getService().getListFromSQL(Records.class, "select records.* from records inner join relations as relations1 on records.id_record = relations1.ref_id_record_1 inner join relations as relations2 on records.id_record = relations2.ref_id_record_2 where relations1.ref_id_record_2=" + idRecord + " or relations2.ref_id_record_1=" + idRecord + " group by id_record;");
			mav.addObject("recordRelations", recordRelations);
		} else if (arg0.getParameter("action") != null && arg0.getParameter("action").equals("singleRecord")) {
			EndPointManager endPointManager = OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(arg0.getParameter("endPointManager"));
			endPointManager.publishRecord(Integer.parseInt(arg0.getParameter("idRecord")), userName,arg0.getParameter("endPointManager"));
		} else if (arg0.getParameter("action") != null && arg0.getParameter("action").equals("singleRecordRemove")) {
			int idRecord = Integer.parseInt(arg0.getParameter("idRecord"));
			try {
				EndPointManager endPointManager = OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(arg0.getParameter("endPointManager"));
				endPointManager.removeEndPointObject(idRecord, userName,arg0.getParameter("endPointManager"));
				System.out.println("RECORD RIMOSSO CORRETTAMENTE");
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else if (arg0.getParameter("action") != null && arg0.getParameter("action").equals("archiveRemove")) {
			int idArchive = Integer.parseInt(arg0.getParameter("idArchive"));
			try {
				EndPointManager endPointManager = OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(arg0.getParameter("endPointManager"));
				endPointManager.removeEndPointObjects(idArchive, userName,arg0.getParameter("endPointManager"));
				System.out.println("ARCHIVIO RIMOSSO CORRETTAMENTE");
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else if (arg0.getParameter("action") != null && arg0.getParameter("action").equals("checkPublished")) {
			int idRecord = Integer.parseInt(arg0.getParameter("idRecord"));
			int idArchive = Integer.parseInt(arg0.getParameter("idArchive"));
			Records records = (Records) OpenDamsServiceProvider.getService().getObject(Records.class, idRecord);
			EndPointManager endPointManager = OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(arg0.getParameter("endPointManager"));
			try {
				mav.addObject("published", endPointManager.checkPublished(idRecord, idArchive, records.getXmlId()));
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else if (arg0.getParameter("action") != null && arg0.getParameter("action").equals("checkPublishedArchive")) {
			int idArchive = Integer.parseInt(arg0.getParameter("idArchive"));
			try {
				EndPointManager endPointManager = OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory().getEndPointMap().get(arg0.getParameter("endPointManager"));
				mav.addObject("published", endPointManager.checkPublishedArchive(idArchive));
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		return mav;
	}

}
