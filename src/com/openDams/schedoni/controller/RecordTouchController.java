package com.openDams.schedoni.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Records;
import com.openDams.endpoint.managing.EndPointManager;
import com.openDams.endpoint.managing.EndPointManagerFactory;
import com.openDams.endpoint.managing.OpenDamsEndPointManagerFactoryProvider;
import com.openDams.index.searchers.Searcher;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;

public class RecordTouchController implements Controller {
	private OpenDamsService service;
	private TitleManager titleManager;
	private String resolverUrlNir;
	private String resolverUrlAC;
	private Searcher searcher;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse arg1) throws Exception {

		System.out.println("RecordTouchController.handleRequest() ");
		System.out.println("*********************************************");

		System.out.println("*********** idRecord:" + request.getParameter("idRecord"));
		System.out.println("*********** publish:" + request.getParameter("publish"));

		System.out.println("*********************************************");
		ModelAndView mav = new ModelAndView();

		if (request.getParameter("publish") != null && request.getParameter("publish").equals("true")) {
			publishRecord(request, arg1, mav);
		} else {
			insertRecord(request, arg1, mav);
		}

		mav.setViewName("documental/json/simpleJsonResponseSessionInvalidate");
		try {
			HttpSession theSession = request.getSession(false);
			if (theSession != null) {
				System.out.println("sessione " + theSession + " eliminata");
				theSession.invalidate();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return mav;
	}

	private synchronized void publishRecord(HttpServletRequest request, HttpServletResponse arg1, ModelAndView mav) {
		try {
			int idRecord = Integer.parseInt(request.getParameter("idRecord"));
			Records record = (Records) service.getObject(Records.class, new Integer(idRecord));
			int idArchive = record.getArchives().getIdArchive();
			EndPointManagerFactory factory = OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory();
			List<String> endPointList = (List<String>) factory.getAllowedEndPointList(idArchive);
			for (int i = 0; i < endPointList.size(); i++) {
				EndPointManager endPointManager = factory.getEndPointMap().get(endPointList.get(i));
				if (request.getParameter("onlyRemove").equals("true")) {
					endPointManager.removeEndPointObject(idRecord, "remove", endPointList.get(i));
				} else {
					endPointManager.removeEndPointObject(idRecord, "remove", endPointList.get(i));
					endPointManager.publishRecord(idRecord, "importer", endPointList.get(i));
				}
			}
			if (endPointList.size() == 0) {
				mav.addObject("result", "{\"result\":\"ok\",\"message\":\"record not published endPointList empty id=" + idRecord + "\"}");
			} else {
				mav.addObject("result", "{\"result\":\"ok\",\"message\":\"record published id=" + idRecord + "\"}");
			}

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("result", "{\"result\":\"error\",\"message\":\"error publishing" + e.getMessage() + "\"}");
		}

	}

	private synchronized void insertRecord(HttpServletRequest request, HttpServletResponse arg1, ModelAndView mav) {

		String idRecord = request.getParameter("idRecord");
		try {
			Records record = (Records) service.getObject(Records.class, new Integer(idRecord));
			record.setModifyDate(new Date());
			service.update(record);
			mav.addObject("result", "{\"result\":\"ok\",\"message\":\"record touched id=" + record.getIdRecord() + "\"}");

		} catch (Exception wewewe) {
			wewewe.printStackTrace();
			mav.addObject("result", "{\"result\":\"error\",\"message\":\"error touching: " + idRecord + " | " + wewewe.getMessage() + "\"}");
		}

	}

	public void setService(OpenDamsService service) {
		this.service = service;
	}

	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}

	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}

	public Searcher getSearcher() {
		return searcher;
	}

	public void setResolverUrlNir(String resolverUrlNir) {
		this.resolverUrlNir = resolverUrlNir;
	}

	public void setResolverUrlAC(String resolverUrlAC) {
		this.resolverUrlAC = resolverUrlAC;
	}

}
