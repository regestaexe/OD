package com.openDams.documental.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.ArchiveIdentity;
import com.openDams.bean.Archives;
import com.openDams.bean.Records;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;
import com.regesta.framework.io.UTF8Encoder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class RecordController implements Controller, ApplicationContextAware {
	private OpenDamsService service;
	private TitleManager titleManager;
	private int page_size = 1;
	private ApplicationContext applicationContext;
	private Map<Integer, Integer> thArchiveRelations;

	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if (arg0.getParameter("action") != null) {
			mav = new ModelAndView();
			Records records = null;
			if (arg0.getParameter("idRecord") != null && mav != null) {
				records = (Records) service.getObject(Records.class, new Integer(arg0.getParameter("idRecord")));
				mav.addObject("records", records);
				mav.addObject("titleManager", titleManager);
			}

			mav.addObject("type", arg0.getParameter("type"));
			mav.addObject("idArchive", arg0.getParameter("idArchive"));
			if (arg0.getParameter("action").equalsIgnoreCase("schedaTemplate")) {
				String result = null;
				try {
					VelocityEngine velocityEngine = (VelocityEngine) applicationContext.getBean("velocityEngine");
					result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/velocity/document.vm", mav.getModel());
				} catch (VelocityException e) {
					e.printStackTrace();
				}
				mav.addObject("content", result);
				// mav.setViewName("documental/"+arg0.getParameter("idArchive")+"/document_configuration");
				mav.setViewName("documental/simpleResponse");
			} else if (arg0.getParameter("action").equalsIgnoreCase("documentResultsTemplate")) {
				String result = null;
				try {
					VelocityEngine velocityEngine = (VelocityEngine) applicationContext.getBean("velocityEngine");
					result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/velocity/documentResults.vm", mav.getModel());
				} catch (VelocityException e) {
					e.printStackTrace();
				}
				mav.addObject("content", result);
				// mav.setViewName("documental/"+arg0.getParameter("idArchive")+"/document_configuration");
				mav.setViewName("documental/simpleResponse");
			} else if (arg0.getParameter("action").equalsIgnoreCase("editTemplate")) {
				String result = null;
				try {
					VelocityEngine velocityEngine = (VelocityEngine) applicationContext.getBean("velocityEngine");
					result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/velocity/edit.vm", mav.getModel());
				} catch (VelocityException e) {
					e.printStackTrace();
				}
				mav.addObject("content", result);
				// mav.setViewName("documental/"+arg0.getParameter("idArchive")+"/document_configuration");
				mav.setViewName("documental/simpleResponse");
			} else if (arg0.getParameter("action").equalsIgnoreCase("updateTemplate")) {
				String result = null;
				try {
					VelocityEngine velocityEngine = (VelocityEngine) applicationContext.getBean("velocityEngine");
					result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/velocity/update.vm", mav.getModel());
				} catch (VelocityException e) {
					e.printStackTrace();
				}
				mav.addObject("content", result);
				// mav.setViewName("documental/"+arg0.getParameter("idArchive")+"/document_configuration");
				mav.setViewName("documental/simpleResponse");
			} else if (arg0.getParameter("action").equalsIgnoreCase("json_data")) {
				mav.addObject("result", fromRecordToJson(records.getXml()));
				mav.setViewName("documental/json/simpleJsonResponse");
			} else if (arg0.getParameter("action").equalsIgnoreCase("xml_tab")) {
				mav.setViewName("documental/panel/xml_tab");
			} else if (arg0.getParameter("action").equalsIgnoreCase("edit_xml_tab")) {
				mav.setViewName("documental/panel/edit_xml_tab");
			} else if (arg0.getParameter("action").equalsIgnoreCase("save_xml")) {
				// System.out.println("####################################################################################");
				// System.out.println(arg0.getParameter("xml"));
				String xml = URLDecoder.decode(arg0.getParameter("xml"), "UTF-8");
				// System.out.println("####################################################################################");
				// System.out.println(xml);
				// System.out.println("####################################################################################");
				xml = xml.replaceAll(" xmlns=\"\"", "");
				records.setXml(xml.getBytes("UTF-8"));
				records.setModifyDate(new Date());
				service.update(records);
				mav.setViewName("documental/panel/xml_tab");
			} else if (arg0.getParameter("action").equalsIgnoreCase("relations_tab")) {
				UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
				@SuppressWarnings("unchecked")
				List<Archives> archiveList = (List<Archives>) service.getListFromSQL(Archives.class, "SELECT * FROM archives  inner join archive_user_role on archives.id_archive=archive_user_role.ref_id_archive where archive_user_role.ref_id_user=" + user.getId() + " and archives.ref_id_archive_identity=" + ArchiveIdentity.THESAURUS + " order by archives.archive_order;");
				mav.addObject("archiveList", archiveList);
				mav.addObject("thArchiveRelations", thArchiveRelations);
				mav.setViewName("documental/panel/relations_tab");
			} else if (arg0.getParameter("action").endsWith("Template")) {
				String result = null;
				try {
					mav.addObject("action", arg0.getParameter("action"));
					VelocityEngine velocityEngine = (VelocityEngine) applicationContext.getBean("velocityEngine");
					result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/velocity/custom.vm", mav.getModel());
				} catch (VelocityException e) {
					e.printStackTrace();
				}
				mav.addObject("content", result);
				mav.setViewName("documental/simpleResponse");
			}

		}
		return mav;
	}

	public String fromRecordToJson(final byte[] bs) {
		String xml;
		try {
			xml = new String(bs, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("fromRecordToJson errore encoding non UTF-8");
			xml = new String(bs);
		}

		xml = xml.replaceAll(" xmlns=\"\"", "");
		xml = xml.replaceAll("<([^ >]*)([ ]*[^>]*)/>", "<$1$2></$1>");

		// System.out.println("********************" + xml);
		xml = xml.replaceAll("<([^:/]*):([^>]*)>", "<$1:$2 ods:dummy=\"value\">");
		//
 
		xml = xml.replaceAll("<br([^>]*)></br>", "<br />");

		xml = xml.replaceAll("</([^>]*)><", "</$1><");
		xml = xml.replaceAll("\\]\\] ods:dummy=\"value\">", "]]>");

		// xml = xml.replaceAll(">(\\s)*\"", ">$1&quot;");
		// xml = xml.replaceAll("\"(\\s)*</", "&quot;$1</");

		// System.out.println("-----------------------------" + xml);

		xml = UTF8Encoder.encode(xml);
		// workaround per conservare le virgolette nel testo interno tra i tag,
		// senza testo intorno
		// (sono ammessi anche spazi tra parentesi angolate e virgoletta)
		String jsonPrettyPrintString = "";
		try {
			JSONObject xmlJSONObj = XML.toJSONObject(xml);
			jsonPrettyPrintString = xmlJSONObj.getJSONObject("rdf:RDF").toString(4);
			jsonPrettyPrintString = toPreviousVersion(jsonPrettyPrintString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("aaaa"+jsonObject.toString());
		// System.out.println("-----------------------------");
		// System.out.println(result);
		// System.out.println("-----------------------------");
		return jsonPrettyPrintString;
	}

	private String toPreviousVersion(String jsonPrettyPrintString) {
		jsonPrettyPrintString = jsonPrettyPrintString.replaceAll("\"([a-zA-Z0-9:]+)\"\\s*:\\s+([^\\[\\{])", "\"@$1\" : $2");
		jsonPrettyPrintString = jsonPrettyPrintString.replaceAll("\"@content\"", "\"#text\"");
		return jsonPrettyPrintString;
	}

	public void setService(OpenDamsService service) {
		this.service = service;
	}

	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}

	public int getPage_size() {
		return page_size;
	}

	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		applicationContext = arg0;

	}

	public Map<Integer, Integer> getThArchiveRelations() {
		return thArchiveRelations;
	}

	public void setThArchiveRelations(Map<Integer, Integer> thArchiveRelations) {
		this.thArchiveRelations = thArchiveRelations;
	}

	public static void main(String[] args) {

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:isbd=\"http://iflastandards.info/ns/isbd/elements/\" xmlns:bibo=\"http://purl.org/ontology/bibo/\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:ods=\"http://intesasanpaolo.xdams.org/data/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:skos=\"http://www.w3.org/2008/05/skos#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:bio=\"http://purl.org/vocab/bio/0.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns=\"http://intesasanpaolo.xdams.org/data/\" xml:base=\"http://intesasanpaolo.xdams.org/data/\">  <bibo:Periodical xmlns=\"\" rdf:about=\"periodico.rdf/ISP1040000003\">    <dc:title>\"ciao\"</dc:title>    <dcterms:issued>222222</dcterms:issued>    <isbd:P1006>m</isbd:P1006>    <isbd:P1012>newseltter</isbd:P1012>    <ods:rif_file rdf:resource=\"file.rdf/ISP0170000004\"/>    <ods:rif_file rdf:resource=\"file.rdf/ISP0170000005\"/>    <skos:changeNote rdf:parseType=\"Resource\">      <dc:creator>admin</dc:creator>      <dc:date>20131011</dc:date>      <rdf:value>create</rdf:value>    </skos:changeNote>    <skos:changeNote rdf:parseType=\"Resource\">      <dc:creator>admin</dc:creator>      <dc:date>20131017</dc:date>      <rdf:value>modify</rdf:value>    </skos:changeNote>  </bibo:Periodical></rdf:RDF>";

		System.out.println((new RecordController()).fromRecordToJson(xml.getBytes()).replaceAll("\",\"", "\",\n\""));
		// System.out.println();
		System.out.println(xml.replaceAll(">\\s*<", ">\n<"));

	}

}
