package com.openDams.schedoni.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.Records;
import com.openDams.bean.RelationTypes;
import com.openDams.bean.Relations;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;

public class LastOccurrencesController implements Controller {
	private OpenDamsService service;
	private TitleManager titleManager;
	private String dbName;

	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;

		mav = new ModelAndView();
		if (arg0.getParameter("action") != null && arg0.getParameter("action").equals("json_data")) {
			mav.setViewName("documental/json/loadLastOccurences");
		} else {
			mav.setViewName("documental/loadLastOccurences");
		}
		mav.addObject("titleManager", titleManager);

		if (arg0.getParameter("idArchive") != null) {
			String frase = "SELECT * FROM " + dbName + ".records where ref_id_archive = " + arg0.getParameter("idArchive") + " order by creation_date desc";
			if (arg0.getParameter("query") != null) {
				frase = "SELECT * FROM " + dbName + ".records where ref_id_archive = " + arg0.getParameter("idArchive") + " AND (" + generateFrase(1, arg0.getParameter("query")) + ") order by modify_date";
			}
			/* TODO: gestire filtri a cascata */
			if (arg0.getParameter("filtersUtente") != null) {
				frase = "SELECT * FROM " + dbName + ".records where ref_id_archive = " + arg0.getParameter("idArchive") + " AND title like '%]" + arg0.getParameter("filtersUtente") + "[user]%' order by creation_date desc";
			}
			if (arg0.getParameter("filtersToday") != null) {
				/* TODO: sandro perdonami per questa like! */
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				frase = "SELECT * FROM " + dbName + ".records where ref_id_archive = " + arg0.getParameter("idArchive") + " AND creation_date like '" + (format.format(new Date())) + "%' order by creation_date desc";
			}
			if (arg0.getParameter("filtersDipartimento") != null) {
				frase = "SELECT * FROM " + dbName + ".records where ref_id_archive = " + arg0.getParameter("idArchive") + " AND title like '%]" + arg0.getParameter("filtersDipartimento") + "[dipart]%' order by creation_date desc";
			}
			// System.out.println("LastOccurrencesController.handleRequest() "+dbName+" "+frase);
			int startFrom = 0;

			if (arg0.getParameter("start") != null) {
				startFrom = Integer.parseInt(arg0.getParameter("start"));
			}

			// System.out.println("frase "+frase);
			Archives archives = (Archives) service.getObject(Archives.class, new Integer(arg0.getParameter("idArchive")));
			mav.addObject("archives", archives);
			int limit = Integer.parseInt(arg0.getParameter("limit"));
			List<Records> list = (List<Records>) service.getPagedListFromSQL(Records.class, frase, startFrom, limit);

			frase = frase.replaceAll("SELECT \\* ", "SELECT COUNT(records.id_record) ");
			System.out.println("frasefrasefrasefrase " + frase);
			mav.addObject("totalCount", service.getCountFromSQL(frase));
			mav.addObject("recordsList", list);
		} else if (arg0.getParameter("idRecord") != null) {

			RelationTypes tipoRelazione = (RelationTypes) service.getObject(RelationTypes.class, Integer.parseInt(arg0.getParameter("idRelation")));
			Records record = (Records) service.getObject(Records.class, Integer.parseInt(arg0.getParameter("idRecord")));

			Set<Relations> relations2 = record.getRelationsesForRefIdRecord2();

			HashSet<Records> recordInseriti = new HashSet<Records>();
			if (arg0.getParameter("allRelation") != null && arg0.getParameter("allRelation").equals("true")) {
				Set<Relations> relations = record.getRelationsesForRefIdRecord1();
				for (Relations relazione : relations) {
					Records record2 = relazione.getRecordsByRefIdRecord2();
					if (!recordInseriti.contains(record2) && !record.equals(record2) && relazione.getRelationTypes().equals(tipoRelazione)) {
						recordInseriti.add(record2);
					}
				}

			}
			for (Relations relazione : relations2) {
				Records record2 = relazione.getRecordsByRefIdRecord1();
				if (!recordInseriti.contains(record2) && !record.equals(record2) && relazione.getRelationTypes().equals(tipoRelazione)) {
					recordInseriti.add(record2);
				}
			}
			List<Records> list = new ArrayList<Records>();
			for (Iterator<Records> iterator = recordInseriti.iterator(); iterator.hasNext();) {
				list.add((Records) iterator.next());
			}
			mav.addObject("recordsList", list);

		}

		return mav;
	}

	private String generateFrase(int idArchive, String parameter) {
		List<Records> relazioni = (List<Records>) service.getListFromSQL(Records.class, "SELECT * FROM " + dbName + ".records where title like '" + StringUtils.substringAfter(parameter, "griglia:") + "%'");
		// System.out.println("cercando: "+"SELECT * FROM "+dbName+".records where title like '"+StringUtils.substringAfter(parameter,
		// "griglia:")+"%'");
		StringBuffer frase = new StringBuffer();
		if (relazioni.size() == 1) {
			Records padre = relazioni.get(0);
			try {
				frase.append(" title like '%§" + StringUtils.substringBefore(padre.getTitle(), "[id]").replaceAll("\\ç", "") + "%'");
			} catch (Exception e) {
			}

			frase.append(generaFraseDaRecord(padre, 15, 0));
		}
		return frase.toString();
	}

	private String generaFraseDaRecord(Records padre, int max, int depth) {

		List<Records> relazionis = (List<Records>) service.getListFromSQL(Records.class, "SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2=" + padre.getIdRecord() + " and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1=" + padre.getIdRecord() + " and relations.ref_id_relation_type=2 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1=" + padre.getIdRecord() + " and relations.ref_id_relation_type=10");

		StringBuffer frase = new StringBuffer();
		// System.err.println("record "+padre.getIdRecord());
		if (max > depth) {
			for (Records ilRecord : relazionis) {
				try {
					frase.append(" OR title like '%§" + StringUtils.substringBefore(ilRecord.getTitle(), "[id]").replaceAll("\\ç", "") + "%'");
				} catch (Exception e) {
				}
				frase.append(generaFraseDaRecord(ilRecord, max, depth + 1));
			}
		} else {

			// System.out.println("massima profondità!");
		}
		// System.out.println("temp: "+frase);
		return frase.toString();
	}

	public void setService(OpenDamsService service) {
		this.service = service;
	}

	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

}
