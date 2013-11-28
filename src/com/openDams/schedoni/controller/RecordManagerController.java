package com.openDams.schedoni.controller;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.RecordTypes;
import com.openDams.bean.Records;
import com.openDams.bean.Relations;
import com.openDams.bean.XmlId;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;
import com.openDams.utility.StringsUtils;
import com.regesta.framework.xml.XMLBuilder;

public class RecordManagerController implements Controller {
	private OpenDamsService service;
	private TitleManager titleManager;

	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse arg1) throws Exception {

		System.out.println("save ###########################################");
		System.out.println("save ########### idRecord " + request.getParameter("idRecord"));
		System.out.println("save ########### action " + request.getParameter("action"));
		System.out.println("save ########### xpath " + request.getParameter("xpath"));
		System.out.println("save ########### method " + request.getParameter("method"));
		System.out.println("save ########### idArchive " + request.getParameter("idArchive"));

		UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
		Date currentDate = new Date();

		System.out.println("save ########### user " + user.getUsername());
		System.out.println("save ########### date " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(currentDate));

		ModelAndView mav = null;

		Enumeration<String> names = request.getParameterNames();
		TreeSet<String> namesTree = new TreeSet<String>();
		Hashtable<String, String> mapSKOS = new Hashtable<String, String>();
		while (names.hasMoreElements()) {
			String string = (String) names.nextElement();
			namesTree.add(string.replaceAll("\\[([0-9])\\]", "[00$1]").replaceAll("\\[([0-9][0-9])\\]", "[0$1]").replaceAll("/\\*/", "/zzzZZzz/"));
			if (string.startsWith("xmlns")) {
				System.out.println("save ########### namespaces: " + string + " ||| " + request.getParameter(string));
				mapSKOS.put(string.replaceAll("xmlns:", ""), request.getParameter(string));
			}
		}

		XMLBuilder xmlBuilder = null;
		boolean preserveOriginalXML = false;
		boolean deleteEmptyNodes = false;
		if (request.getParameter("method") != null && request.getParameter("method").equals("preserve")) {
			preserveOriginalXML = true;
		}
		if (request.getParameter("emptyNodesAction") != null && request.getParameter("emptyNodesAction").equals("delete")) {
			deleteEmptyNodes = true;
		}

		if (preserveOriginalXML) {
			Records arecord = (Records) service.getObject(Records.class, new Integer(request.getParameter("idRecord")));
			String xml = (new String(arecord.getXml(), "UTF-8")).replaceAll(" xmlns=\"\"", "");
			xmlBuilder = new XMLBuilder(new ByteArrayInputStream(xml.getBytes()));
		} else {
			xmlBuilder = new XMLBuilder(request.getParameter("xml_root"), mapSKOS, request.getParameter("encoding"));
		}

		for (String string : namesTree) {
			string = string.replaceAll("/zzzZZzz/", "/*/");
			string = string.replaceAll("\\[[0]+([0-9]+)\\]", "[$1]");
			boolean cdata = false;
			if (string.endsWith("/text(cdata)")) {
				cdata = true;
			}
			if (string.startsWith("/" + request.getParameter("xml_root") + "/")) {
				if (string.indexOf("[*]") > 0) {
					System.out.println("save ########### campi multipli " + string + "\n\t\t\t" + request.getParameter(string));
					/* TODO: gestire istanze di istanza */
					String[] parameterValue = request.getParameterValues(string);
					String prefix = StringUtils.substringBefore(string, "[*]");
					String suffix = StringUtils.substringAfter(string, "[*]");
					for (int i = 0; i < parameterValue.length; i++) {
						if (!parameterValue[i].equals("")) {
							if (cdata) {
								if (preserveOriginalXML) {
									System.out.println("save ########### " + prefix + "[" + (1 + i) + "]" + StringUtils.substringBeforeLast(suffix, "/text(cdata)") + "/text()" + "\t " + parameterValue[i]);
									xmlBuilder.insertValueAt(prefix + "[" + (1 + i) + "]" + StringUtils.substringBeforeLast(suffix, "/text(cdata)") + "/text()", parameterValue[i], cdata);
								} else {
									System.out.println("save ########### " + prefix + "[" + (1 + i) + "]" + StringUtils.substringBeforeLast(suffix, "/text(cdata)") + "/text()" + "\t " + parameterValue[i]);
									xmlBuilder.insertNode(prefix + "[" + (1 + i) + "]" + StringUtils.substringBeforeLast(suffix, "/text(cdata)") + "/text()", parameterValue[i], cdata);
								}
							} else {
								if (preserveOriginalXML) {
									System.out.println("save ########### " + prefix + "[" + (1 + i) + "]" + suffix + "\t " + parameterValue[i]);
									xmlBuilder.insertValueAt(prefix + "[" + (1 + i) + "]" + suffix, parameterValue[i]);
								} else {
									System.out.println("save ########### " + prefix + "[" + (1 + i) + "]" + suffix + "\t " + parameterValue[i]);
									xmlBuilder.insertNode(prefix + "[" + (1 + i) + "]" + suffix, parameterValue[i]);
								}
							}
						} else if (deleteEmptyNodes) {
							xmlBuilder.deleteNode(prefix + "[" + (1 + i) + "]" + StringUtils.substringBeforeLast(suffix, "/"));
						}
					}
				} else {
					System.out.println("save ########### campo singolo " + string + "\n\t\t\t" + request.getParameter(string));
					if (!request.getParameter(string).equals("")) {
						if (cdata) {
							if (preserveOriginalXML) {
								xmlBuilder.insertValueAt(StringUtils.substringBeforeLast(string, "/text(cdata)") + "/text()", request.getParameter(string), cdata);
							} else {
								xmlBuilder.insertNode(StringUtils.substringBeforeLast(string, "/text(cdata)") + "/text()", request.getParameter(string), cdata);
							}

						} else {
							if (preserveOriginalXML) {
								xmlBuilder.insertValueAt(string, request.getParameter(string));
							} else {
								xmlBuilder.insertNode(string, request.getParameter(string));
							}
						}
					} else if (deleteEmptyNodes) {
						xmlBuilder.deleteNode(StringUtils.substringBeforeLast(string, "/"));
					}
				}
			}
		}
		Records record = null;
		if (request.getParameter("action") != null) {
			if (request.getParameter("action").equalsIgnoreCase("update")) {
				record = (Records) service.getObject(Records.class, new Integer(request.getParameter("idRecord")));
				record.setXml(xmlBuilder.getXML(request.getParameter("encoding"), false).getBytes("UTF-8"));
				record.setModifyDate(currentDate);

				service.update(record);
			} else if (request.getParameter("action").equalsIgnoreCase("updateXpath")) {

				record = (Records) service.getObject(Records.class, new Integer(request.getParameter("idRecord")));
				String xml = new String(record.getXml());
				String value = request.getParameter("value");
				String xpath = request.getParameter("xpath");
				if (request.getParameter("escapeString") != null && !request.getParameter("escapeString").equals("")) {
					xml = xml.replaceAll(request.getParameter("escapeString"), URLEncoder.encode(request.getParameter("escapeString"), "UTF-8"));
					xpath = xpath.replaceAll(request.getParameter("escapeString"), URLEncoder.encode(request.getParameter("escapeString"), "UTF-8"));
				}
				XMLBuilder thisBuilder = new XMLBuilder(new ByteArrayInputStream(xml.getBytes()));
				thisBuilder.insertValueAt(xpath, value);
				xml = thisBuilder.getXML("UTF-8");
				if (request.getParameter("escapeString") != null && !request.getParameter("escapeString").equals("")) {
					xml = xml.replaceAll(URLEncoder.encode(request.getParameter("escapeString"), "UTF-8"), request.getParameter("escapeString"));
				}
				record.setXml(xml.getBytes());
				System.out.println("save ########### updateXpath ------- " + record.getTitle());
				record.setModifyDate(currentDate);
				service.update(record);
			} else if (request.getParameter("action").equalsIgnoreCase("removeXpath")) {
				// request.getParameter("encoding"));
				record = (Records) service.getObject(Records.class, new Integer(request.getParameter("idRecord")));
				XMLBuilder thisBuilder = new XMLBuilder(new ByteArrayInputStream(record.getXml()));
				thisBuilder.deleteNode(request.getParameter("xpath"));
				record.setXml(thisBuilder.getXML("UTF-8").getBytes());
				// record.setTitle(titleManager.getTitle(record.getXMLReader(),
				// new Integer(request.getParameter("idArchive"))));
				System.out.println("save ########### removeXpath ------- " + record.getTitle());
				record.setModifyDate(currentDate);
				service.update(record);
			} else if (request.getParameter("action").equalsIgnoreCase("delete")) {
				// CANCELLAZIONE
				record = (Records) service.getObject(Records.class, new Integer(request.getParameter("idRecord")));

				/*
				 * DIEGO: modificato perché le relazioni non venivano eliminate
				 * record.getRelationsesForRefIdRecord1().clear();
				 * record.getRelationsesForRefIdRecord2().clear();
				 */
				record.setDeleted(true);
				record.setModifyDate(currentDate);
				// service.remove(record);
				service.update(record);

				/* DIEGO: modificato perché le relazioni non venivano eliminate */
				List<Relations> list = (List<Relations>) service.getListFromSQL(Relations.class, "select * from relations  where (ref_id_record_1=" + record.getIdRecord() + " or ref_id_record_2=" + record.getIdRecord() + ")");
				for (int i = 0; i < list.size(); i++) {
					Relations relations = list.get(i);
					System.out.println("save ########### REMOVING RELATION " + relations.getRecordsByRefIdRecord1().getIdRecord() + " --> " + relations.getRecordsByRefIdRecord2().getIdRecord() + " (tipo: " + relations.getRelationTypes().getIdRelationType() + ")");
					service.remove(relations);
				}

			} else if (request.getParameter("action").equalsIgnoreCase("insert")) {
				Archives archives = (Archives) service.getObject(Archives.class, new Integer(request.getParameter("idArchive")));
				record = new Records();
				record.setXml(xmlBuilder.getXML(request.getParameter("encoding")).getBytes("UTF-8"));
				record.setRecordTypes((RecordTypes) service.getObject(RecordTypes.class, new Integer(request.getParameter("idRecordType"))));
				record.setArchives(archives);
				// record.setTitle(titleManager.getTitle(record.getXMLReader(),
				// new Integer(request.getParameter("idArchive"))));
				record.setCreationDate(currentDate);
				record.setModifyDate(currentDate);
				record.setPosition(0);
				record.setDepth(0);
				XmlId xmlId = archives.getXmlId();
				xmlId.setIdXml(xmlId.getIdXml() + 1);
				service.add(record);
				service.update(xmlId);
			}
		}
		System.out.println("save ###########################################");
		mav = new ModelAndView("documental/record_manager_result");
		return mav;
	}

	public void setService(OpenDamsService service) {
		this.service = service;
	}

	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}

}
