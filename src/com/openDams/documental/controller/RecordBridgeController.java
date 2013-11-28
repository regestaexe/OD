package com.openDams.schedoni.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.ImportHistory;
import com.openDams.bean.RecordTypes;
import com.openDams.bean.Records;
import com.openDams.bean.XmlId;
import com.openDams.endpoint.managing.EndPointManager;
import com.openDams.endpoint.managing.EndPointManagerFactory;
import com.openDams.endpoint.managing.OpenDamsEndPointManagerFactoryProvider;
import com.openDams.index.searchers.Searcher;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;
import com.openDams.utility.StringsUtils;
import com.regesta.framework.util.StringUtility;
import com.regesta.framework.xml.XMLBuilder;
import com.regesta.framework.xml.XMLReader;

public class RecordBridgeController implements Controller {
	private OpenDamsService service;
	private TitleManager titleManager;
	private String resolverUrlNir;
	private String resolverUrlAC;
	private Searcher searcher;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse arg1) throws Exception {

		System.out.println("RecordBridgeController.handleRequest() ");
		System.out.println("*********************************************");
		System.out.println("*********** encoding:" + request.getParameter("encoding"));
		System.out.println("*********** idRecordType:" + request.getParameter("idRecordType"));
		System.out.println("*********** uri:" + request.getParameter("uri"));
		System.out.println("*********** rdf_name:" + request.getParameter("rdf_name"));
		System.out.println("*********** disableParse:" + request.getParameter("disableParse"));
		System.out.println("*********** xpath_id:" + request.getParameter("xpath_id"));

		System.out.println("*********** publish:" + request.getParameter("publish"));
		System.out.println("*********** idArchive:" + request.getParameter("idArchive"));
		System.out.println("*********** idRecord:" + request.getParameter("idRecord"));

		System.out.println("*********************************************");
		ModelAndView mav = new ModelAndView();

		if (request.getParameter("publish") != null && request.getParameter("publish").equals("true")) {
			publishRecord(request, arg1, mav);
		} else {
			insertRecord(request, arg1, mav);
		}

		mav.setViewName("documental/json/simpleJsonResponseSessionInvalidate");
		HttpSession theSession = request.getSession(false);
		if (theSession != null) {
			System.out.println("sessione " + theSession + " eliminata");
			theSession.invalidate();
		}
		return mav;
	}

	private synchronized void publishRecord(HttpServletRequest request, HttpServletResponse arg1, ModelAndView mav) {
		try {
			int idArchive = Integer.parseInt(request.getParameter("idArchive"));
			int idRecord = Integer.parseInt(request.getParameter("idRecord"));

			EndPointManagerFactory factory = OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory();
			List<String> endPointList = (List<String>) factory.getAllowedEndPointList(idArchive);
			for (int i = 0; i < endPointList.size(); i++) {
				EndPointManager endPointManager = factory.getEndPointMap().get(endPointList.get(i));
				endPointManager.publishRecord(idRecord, "importer", endPointList.get(i));
			}
			mav.addObject("result", "{\"result\":\"ok\",\"message\":\"record published id=" + idRecord + "\"}");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("result", "{\"result\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
		}

	}

	private synchronized void insertRecord(HttpServletRequest request, HttpServletResponse arg1, ModelAndView mav) {
		boolean disableParse = false;
		if (request.getParameter("disableParse") != null && request.getParameter("disableParse").equals("true")) {
			disableParse = true;
		}
		boolean publish = false;
		if (request.getParameter("publish") != null && request.getParameter("publish").equals("true")) {
			publish = true;
		}
		String rdf = request.getParameter("rdf");
		if (rdf.startsWith("URLencoded:")) {
			rdf = StringUtils.substringAfter(rdf, "URLencoded:");
			try {
				rdf = java.net.URLDecoder.decode(rdf, request.getParameter("encoding"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		rdf = rdf.replaceAll("’", "'");

		rdf = rdf.replaceAll("&#160;", " ");
		rdf = rdf.replaceAll("&apos;", "'");
		rdf = rdf.replaceAll("&#8217;", "'");
		rdf = rdf.replaceAll("&#8211;", "-");
		rdf = rdf.replaceAll("—", "-");
		rdf = rdf.replaceAll("&#186;", "°");

		try {
			XMLBuilder xmlBuilder = new XMLBuilder(rdf, request.getParameter("encoding"));
			XMLReader xmlReader = new XMLReader(rdf);
 

			try {
				Integer idArchivio = new Integer(request.getParameter("idArchive"));
				Archives archives = (Archives) service.getObject(Archives.class, idArchivio);
				XmlId xmlId = archives.getXmlId();
				Records record = new Records();
				boolean insertMode = false;
				try {
					String about = xmlReader.getTrimmedNodeValue(request.getParameter("xpath_id"));
					if (about.equals(".")) {
						// INSERT MODE
						xmlId.setIdXml(xmlId.getIdXml() + 1);
						String valore = request.getParameter("rdf_name") + "/" + archives.getArchiveXmlPrefix() + StringUtility.fillZero(Integer.toString(xmlId.getIdXml()), 7);
						xmlBuilder.insertValueAt(request.getParameter("xpath_id"), valore);
						insertMode = true;
						record.setXmlId(valore);
						service.update(xmlId);
					} else {
						List<Records> list = (List<Records>) service.getListFromSQL(Records.class, "SELECT *  FROM records where xml_id='" + about + "';");
						for (Records records : list) {
							record = records;
						}
						if (record.getXmlId() == null || record.getXmlId().equals("")) {
							insertMode = true;
						}
						record.setXmlId(about);
						System.out.println("RecordBridgeController.handleRequest() UPDATE MODE " + record + " xml_id: " + about + " insertMode: " + insertMode);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				if (!insertMode && record.getXmlId() != null && !record.getXmlId().equals("") && record.getXml() != null && record.getXml().length > 0) {
					System.out.println("RecordBridgeController.handleRequest() recovering old information"); 
					
					System.out.println("RecordBridgeController.handleRequest() end recovering old information");

				}

				String descrizione = xmlReader.getNodeValue("/rdf:RDF/*/dc:description");
				String titolo = xmlReader.getNodeValue("/rdf:RDF/*/dc:title");
				String note = xmlReader.getNodeValue("/rdf:RDF/*/dc:note");
				if (!disableParse) {
					if (!descrizione.equals("")) {
						System.out.println("descrizione " + descrizione);
						descrizione = StringsUtils.addRiferimentiNormativi(descrizione, "nir", resolverUrlNir);
						descrizione = StringsUtils.addRiferimentiNormativi(descrizione, "ac", resolverUrlAC);
						System.out.println("descrizione " + descrizione);
						if (!descrizione.equals(xmlReader.getNodeValue("/rdf:RDF/*/dc:description/text()"))) {
							xmlBuilder.deleteNode("/rdf:RDF/*/dc:description");
							xmlBuilder.insertValueAt("/rdf:RDF/*/dc:description/text()", StringEscapeUtils.unescapeXml(descrizione), true);
							System.out.println("---------- " + StringEscapeUtils.unescapeXml(descrizione));
						}

					}
					if (!titolo.equals("")) {
						System.out.println("titolo " + titolo);
						titolo = StringsUtils.addRiferimentiNormativi(titolo, "nir", resolverUrlNir);
						titolo = StringsUtils.addRiferimentiNormativi(titolo, "ac", resolverUrlAC);
						System.out.println("titolo " + titolo);
						if (!titolo.equals(xmlReader.getNodeValue("/rdf:RDF/*/dc:title/text()"))) {
							xmlBuilder.deleteNode("/rdf:RDF/*/dc:title");
							xmlBuilder.insertValueAt("/rdf:RDF/*/dc:title/text()", StringEscapeUtils.unescapeXml(titolo), true);
							System.out.println("---------- " + StringEscapeUtils.unescapeXml(titolo));
						}

					}
					if (!note.equals("")) {
						System.out.println("note " + note);
						note = StringsUtils.addRiferimentiNormativi(note, "nir", resolverUrlNir);
						note = StringsUtils.addRiferimentiNormativi(note, "ac", resolverUrlAC);
						System.out.println("note " + note);
						if (!note.equals(xmlReader.getNodeValue("/rdf:RDF/*/dc:note/text()"))) {
							xmlBuilder.deleteNode("/rdf:RDF/*/dc:note");
							xmlBuilder.insertValueAt("/rdf:RDF/*/dc:note/text()", StringEscapeUtils.unescapeXml(note), true);
						}

					}
				}
				String xmlFromDB = xmlBuilder.getXML(request.getParameter("encoding"));

				// xmlFromDB = xmlFromDB.replaceAll("a!\\[CDATA\\[",
				// "<![CDATA[").replaceAll("\\]\\]a", "]]>");
				xmlFromDB = xmlFromDB.replaceAll("&amp;amp;", "&");
				xmlFromDB = xmlFromDB.replaceAll("&amp;#", "&#");
				xmlFromDB = xmlFromDB.replaceAll("&amp;quot", "&quot");
				xmlFromDB = xmlFromDB.replaceAll("&amp;apos;", "'");

				xmlBuilder = new XMLBuilder(xmlFromDB, request.getParameter("encoding"));
				xmlFromDB = xmlBuilder.getXML(request.getParameter("encoding"));

				record.setXml(xmlFromDB.getBytes("UTF-8"));
				record.setRecordTypes((RecordTypes) service.getObject(RecordTypes.class, new Integer(request.getParameter("idRecordType"))));
				record.setArchives(archives);
				// record.setTitle(titleManager.getTitle(record.getXMLReader(),
				// new Integer(request.getParameter("idArchive"))));
				record.setCreationDate(new Date());
				record.setModifyDate(new Date());
				record.setPosition(0);
				record.setDepth(0);
				if (insertMode) {
					service.add(record);
				} else {
					service.update(record);
				}

				if (publish) {
					EndPointManagerFactory factory = OpenDamsEndPointManagerFactoryProvider.getEndPointManagerFactory();
					List<String> endPointList = (List<String>) factory.getAllowedEndPointList(idArchivio);
					for (int i = 0; i < endPointList.size(); i++) {
						EndPointManager endPointManager = factory.getEndPointMap().get(endPointList.get(i));
						endPointManager.publishRecord(record.getIdRecord(), "importer", endPointList.get(i));
					}
				}
				mav.addObject("result", "{\"result\":\"ok\",\"message\":\"record inserted id=" + record.getIdRecord() + "\"}");
				try {
					createHistoryErrorForURI(request.getParameter("uri"), "record inserted id=" + record.getIdRecord(), true);
				} catch (Exception wewewe) {
					wewewe.printStackTrace();

					mav.addObject("result", "{\"result\":\"error\",\"message\":\"error creating history: " + wewewe.getMessage() + "\"}");
				}

			} catch (Exception aaaae) {
				aaaae.printStackTrace();
				mav.addObject("result", "{\"result\":\"error\",\"message\":\"" + aaaae.getMessage() + "\"}");
				createHistoryErrorForURI(request.getParameter("uri"), "application_error=" + aaaae.getMessage(), false);
			}
		} catch (Exception essss) {
			essss.printStackTrace();
			mav.addObject("result", "{\"result\":\"error\",\"message\":\"xml parsing failed=" + essss.getMessage() + "\"}");
			createHistoryErrorForURI(request.getParameter("uri"), "application_error=xml parsing failed: " + essss.getMessage(), false);
		}

	}

	private void createHistoryErrorForURI(String uri, String message, boolean status) {
		ImportHistory importHistory = new ImportHistory();
		// importHistory.setDate(new Date());
		// importHistory.setType(type);
		importHistory.setApplicationStatus(status);
		importHistory.setMessage(message);
		importHistory.setUri(uri.toString());
		if (service != null) {
			// service.update(importHistory);
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
