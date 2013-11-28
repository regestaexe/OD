package com.openDams.documental.controller;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.RecordTypes;
import com.openDams.bean.Records;
import com.openDams.bean.XmlId;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;
import com.regesta.framework.util.StringUtility;
import com.regesta.framework.xml.XMLBuilder;
import com.regesta.framework.xml.XMLReader;

public class InsertController implements Controller, ApplicationContextAware {
	private OpenDamsService service;
	private ApplicationContext applicationContext;
	private TitleManager titleManager;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = new ModelAndView();

		mav.addObject("type", request.getParameter("type"));

		if (request.getParameter("action").equalsIgnoreCase("insertTemplate")) {
			String result = null;
			try {
				mav.addObject("idArchive", request.getParameter("idArchive"));
				VelocityEngine velocityEngine = (VelocityEngine) applicationContext.getBean("velocityEngine");
				result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/velocity/insert.vm", mav.getModel());
			} catch (VelocityException e) {
				e.printStackTrace();
			}
			mav.addObject("content", result);
			mav.setViewName("documental/simpleResponse");
		} else if (request.getParameter("action").equalsIgnoreCase("sortNodes")) {
			String result = null;
			try {
				Records record = (Records) service.getObject(Records.class, new Integer(request.getParameter("idRecord")));
				XMLReader reader = record.getXMLReader();
				XMLBuilder writer = new XMLBuilder(new ByteArrayInputStream(record.getXml()));

				String[] xpathArray = request.getParameter("xpathArray").split(";");
 
				String prefix = StringUtils.substringBefore(xpathArray[0], "[");
				String suffix = StringUtils.substringAfter(xpathArray[0], "]");

				for (int i = 0; i < xpathArray.length; i++) {
					writer.insertValueAt(prefix + "[" + (i + 1) + "]" + suffix, reader.getNodeValue(xpathArray[i]));
				}
				record.setXml(writer.getXML("UTF-8").getBytes());
				record.closeXMLReader();
				service.update(record);
				mav.addObject("result", "{\"result\":\"ok\",\"idRecord\":\"" + record.getIdRecord() + "\"}");

			} catch (VelocityException e) {
				e.printStackTrace();
			}
			mav.addObject("content", result);
			mav.setViewName("documental/simpleResponse");
		} else if (request.getParameter("action").equalsIgnoreCase("doInsert")) {

			System.out.println("**** inizio inserimento");

			Enumeration<String> names = request.getParameterNames();
			Hashtable<String, String> mapSKOS = new Hashtable<String, String>();
			while (names.hasMoreElements()) {
				String string = (String) names.nextElement();
				if (string.startsWith("xmlns")) {
					mapSKOS.put(string.replaceAll("xmlns:", ""), request.getParameter(string));
				}
			}

			XMLBuilder xmlBuilder = new XMLBuilder(request.getParameter("xml_root"), mapSKOS, request.getParameter("encoding"));
			names = request.getParameterNames();

			Archives archives = (Archives) service.getObject(Archives.class, new Integer(request.getParameter("idArchive")));
			XmlId xmlId = archives.getXmlId();
			xmlId.setIdXml(xmlId.getIdXml() + 1);
			System.out.println("**** codice id generato: " + (xmlId.getIdXml() + 1));
			String id =archives.getArchiveXmlPrefix() + StringUtility.fillZero(Integer.toString(xmlId.getIdXml()), 7);
			String xmlAbout = request.getParameter("rdfName") + "/" + id;

			while (names.hasMoreElements()) {
				String string = (String) names.nextElement();
				boolean cdata = false;
				if (string.endsWith("/text(cdata)")) {
					cdata = true;
				}
				if (string.startsWith("/" + request.getParameter("xml_root") + "/")) {
					System.out.println("inserendo: " + string + " ||| " + request.getParameter(string));
					if (string.indexOf("[*]") > 0) {
						System.out.println("campi multipli");
						/* TODO: gestire istanze di istanza */
						String[] parameterValue = request.getParameterValues(string);
						String prefix = StringUtils.substringBefore(string, "[*]");
						String suffix = StringUtils.substringAfter(string, "[*]");
						for (int i = 0; i < parameterValue.length; i++) {
							if (!parameterValue[i].equals("")) {
								if (cdata) {
									xmlBuilder.insertNode(prefix + "[" + (1 + i) + "]" + StringUtils.substringBeforeLast(suffix, "/text(cdata)") + "/text()", parameterValue[i], cdata);
								} else {
									xmlBuilder.insertNode(prefix + "[" + (1 + i) + "]" + suffix, parameterValue[i]);
								}
							}
						}
					} else {
						System.out.println("campo singolo");
						if (!request.getParameter(string).equals("")) {
							String valore =  request.getParameter(string).equals(".") ? xmlAbout : request.getParameter(string);
							if(request.getParameter(string).contains("${id}")){
								valore = valore.replaceAll("\\$\\{id\\}",id);
							}
						 
							if (cdata) {
								xmlBuilder.insertNode(StringUtils.substringBeforeLast(string, "/text(cdata)") + "/text()", valore, cdata);
							} else {
								xmlBuilder.insertNode(string, valore);
							}

						}
					}
				}
			}

			Records record = new Records();
			record.setRecordTypes(new RecordTypes(new Integer(request.getParameter("idRecordType"))));
			record.setArchives(archives);
			record.setPosition(0);
			record.setDepth(0);
			record.setXmlId(xmlAbout);
			// System.out.println("**** inizio xml");
			System.out.println(new String(xmlBuilder.getXML(request.getParameter("encoding"), true).getBytes("UTF-8")));
			// System.out.println("**** fine xml");

			record.setXml(xmlBuilder.getXML(request.getParameter("encoding"), true).getBytes("UTF-8"));
			System.out.println("**** inizio titoli");

			//record.setTitle(titleManager.getTitle(record.getXMLReader(), new Integer(request.getParameter("idArchive"))));
			record.setCreationDate(new Date());
			System.out.println("**** titolo del documento: " + record.getTitle());
			record.setModifyDate(new Date());
			service.add(record);
			System.out.println("**** fine inserimento");
			mav.addObject("result", "{\"result\":\"ok\",\"idRecord\":\"" + record.getIdRecord() + "\"}");
			mav.setViewName("documental/json/simpleJsonResponse");
		}
		return mav;
	}

	public void setService(OpenDamsService service) {
		this.service = service;
	}

	@Override
	public void setApplicationContext(ApplicationContext request) throws BeansException {
		this.applicationContext = request;

	}

	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
}
