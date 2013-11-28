package com.openDams.admin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.RecordTypes;
import com.openDams.bean.Records;
import com.openDams.bean.XmlId;
import com.openDams.configuration.ConfigurationException;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;
import com.regesta.framework.util.Watch;
import com.regesta.framework.xml.XMLReader;


public class ImportController implements Controller,ServletContextAware{
	private  ServletContext servletContext;
	private  OpenDamsService service ;
	private  TitleManager titleManager ;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if(arg0.getParameter("action")==null || !arg0.getParameter("action").equalsIgnoreCase("import")){
			mav = new ModelAndView("import");
			mav.addObject("archiveList", service.getList(Archives.class));
		}else{
			String start_time="";
		    start_time="Operazione iniziata il "+Watch.getDate()+" alle "+Watch.getTime();
		    System.out.println(start_time);
			String file=servletContext.getRealPath("");
			file+="/WEB-INF/import/temi.rdf";
			XMLReader xmlReader = new XMLReader(new InputStreamReader(new FileInputStream(new File(file)),"UTF-8"));
			int tot_concept = xmlReader.getNodeCount("/rdf:RDF/skos:Concept");
			int idArchive = new Integer(arg0.getParameter("id_archive"));
			String idRadice = arg0.getParameter("idRadice");
			System.out.println("numero skos:ConceptScheme radice con id "+idRadice+" = "+xmlReader.getNodeCount("/rdf:RDF/skos:ConceptScheme[@rdf:ID='"+idRadice+"']"));
			System.out.println("numero skos:ConceptScheme non radice= "+xmlReader.getNodeCount("/rdf:RDF/skos:ConceptScheme[@rdf:ID!='"+idRadice+"']"));
			System.out.println("numero skost:Concept = "+tot_concept);
			Hashtable<String, Integer> records_map = new Hashtable<String, Integer>();
			Hashtable<String, ArrayList<Integer>> fathers_map = new Hashtable<String,  ArrayList<Integer>>();
			Hashtable<String, String> related_map = new Hashtable<String, String>();
			String xml = "";
			String key = "";
			String key_father = "";
			String key_related = "";
			int id_record;
			try {
				   xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
			       xml += "<rdf:RDF xmlns=\"http://regesta.com/ontologie/luce#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:skos=\"http://www.w3.org/2008/05/skos#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xml:base=\"http://regesta.com/ontologie/luce\">\r\n";
			       xml += "	"+xmlReader.getNodeAsXML("/rdf:RDF/skos:ConceptScheme[@rdf:ID='"+idRadice+"']").replaceAll("&lt;","<").replaceAll("&gt;",">");
			       xml += "\r\n</rdf:RDF>";
			       XMLReader xmlReader2 = new XMLReader(xml.getBytes("UTF-8"));
				   key = xmlReader2.getNodeValue("/rdf:RDF/skos:ConceptScheme/@rdf:ID");
			       key_father = xmlReader2.getNodeValue("/rdf:RDF/skos:ConceptScheme/skos:broader/@rdf:resource").replaceAll("#","");
			       key_related = xmlReader2.getNodeValue("/rdf:RDF/skos:ConceptScheme/skos:related/@rdf:resource").replaceAll("#","");
			       id_record = addRecord(xml,idArchive,Records.ARCHIVE_RECORD);
			       records_map.put(key, id_record);
			       if(key_father!=null && !key_father.equals("")){
			           if(fathers_map.get(key_father)==null){
			        	   ArrayList<Integer> arrayList = new ArrayList<Integer>();
			        	   arrayList.add(id_record);
			        	   fathers_map.put(key_father, arrayList);
			           }else{
			        	   ArrayList<Integer> arrayList = fathers_map.get(key_father);
			        	   arrayList.add(id_record);
			        	   fathers_map.put(key_father, arrayList); 
			           }
			       }
			       if(key_related!=null && !key_related.equals(""))
			    	   related_map.put(key, key_related);
			} catch (Exception e) {
				System.out.println("-------------------------------->ERRORRE IMPORT skos:ConceptScheme archive");
				e.printStackTrace();
			}
			
			try {
				   xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
				   xml += "<rdf:RDF xmlns=\"http://regesta.com/ontologie/luce#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:skos=\"http://www.w3.org/2008/05/skos#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xml:base=\"http://regesta.com/ontologie/luce\">\r\n";
				   xml += "	"+xmlReader.getNodeAsXML("/rdf:RDF/skos:ConceptScheme[@rdf:ID!='"+idRadice+"']").replaceAll("&lt;","<").replaceAll("&gt;",">");
				   xml += "\r\n</rdf:RDF>";
				   XMLReader xmlReader3 = new XMLReader(xml.getBytes("UTF-8"));
				   key = xmlReader3.getNodeValue("/rdf:RDF/skos:ConceptScheme/@rdf:ID");
				   key_father = xmlReader3.getNodeValue("/rdf:RDF/skos:ConceptScheme/skos:broader/@rdf:resource").replaceAll("#","");
				   key_related = xmlReader3.getNodeValue("/rdf:RDF/skos:ConceptScheme/skos:related/@rdf:resource").replaceAll("#","");
				   id_record = addRecord(xml,idArchive,Records.SCHEME_CONCEPT_RECORD);
				   records_map.put(key, id_record);
				   if(key_father!=null && !key_father.equals("")){
				       if(fathers_map.get(key_father)==null){
				    	   ArrayList<Integer> arrayList = new ArrayList<Integer>();
				    	   arrayList.add(id_record);
				    	   fathers_map.put(key_father, arrayList);
				       }else{
				    	   ArrayList<Integer> arrayList = fathers_map.get(key_father);
				    	   arrayList.add(id_record);
				    	   fathers_map.put(key_father, arrayList); 
				       }
				   }
				   if(key_related!=null && !key_related.equals(""))
					   related_map.put(key, key_related);
			} catch (Exception e) {
				System.out.println("-------------------------------->ERRORRE IMPORT skos:ConceptScheme");
				System.out.println("-------------------------------->"+xml);
				e.printStackTrace();
			}
				int count = 0;       
				//for(int i=0;i<tot_concept;i++){
					for(int i=0;i<1000;i++){
			     try {
					   xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
				       xml += "<rdf:RDF xmlns=\"http://regesta.com/ontologie/luce#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:skos=\"http://www.w3.org/2008/05/skos#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xml:base=\"http://regesta.com/ontologie/luce\">\r\n";
				       xml += "	"+xmlReader.getNodeAsXML("/rdf:RDF/skos:Concept["+(i+1)+"]").replaceAll("&lt;","<").replaceAll("&gt;",">");
				       xml += "\r\n</rdf:RDF>";
				       XMLReader xmlReader4 = new XMLReader(xml.getBytes("UTF-8"));
				       key = xmlReader4.getNodeValue("/rdf:RDF/skos:Concept["+(i+1)+"]/@rdf:ID");
				       key_father = xmlReader4.getNodeValue("/rdf:RDF/skos:Concept["+(i+1)+"]/skos:broader/@rdf:resource").replaceAll("#","");
				       key_related = xmlReader4.getNodeValue("/rdf:RDF/skos:Concept["+(i+1)+"]/skos:related/@rdf:resource").replaceAll("#","");
				       id_record = addRecord(xml,idArchive,Records.CONCEPT_RECORD);
				       records_map.put(key, id_record);
				       if(key_father!=null && !key_father.equals("")){
				           if(fathers_map.get(key_father)==null){
				        	   ArrayList<Integer> arrayList = new ArrayList<Integer>();
				        	   arrayList.add(id_record);
				        	   fathers_map.put(key_father, arrayList);
				           }else{
				        	   ArrayList<Integer> arrayList = fathers_map.get(key_father);
				        	   arrayList.add(id_record);
				        	   fathers_map.put(key_father, arrayList); 
				           }
				       }
				       if(key_related!=null && !key_related.equals(""))
				    	   related_map.put(key, key_related);
				       if(i!=0 && i%100==0){
				    	   count++;
				    	   System.out.println("-------------------------------->inseriti e indicizzati "+(100*count));
				       }	
				} catch (Exception e) {
					System.out.println("-------------------------------->ERRORRE IMPORT CONCEPT");
					System.out.println("-------------------------------->"+xml);
					e.printStackTrace();
					break;
				}
				}
			
		    try {
				Enumeration<String> enumeration= fathers_map.keys();
				while (enumeration.hasMoreElements()) {
					String father = (String) enumeration.nextElement();
					ArrayList<Integer> arrayList = fathers_map.get(father);
					for (int i = 0; i < arrayList.size(); i++) {
						    try {
								int id = arrayList.get(i);
								int id_father = records_map.get(father);
								System.out.println("------------------------------------> ID: "+id);
								System.out.println("------------------------------------> ID_FATHER: "+id_father);
								/*Relations relations = new Relations();
								relations.setRecordsByRefIdRecord1((Records)service.getObject(Records.class, new Integer(id)));
								relations.setRecordsByRefIdRecord2((Records)service.getObject(Records.class, new Integer(id_father)));
								relations.setRelationTypes((RelationTypes)service.getObject(RelationTypes.class, new Integer(1)));
								service.add(relations);*/
							} catch (Exception e) {
								System.out.println("errore!!! relazione broader");
							}
					}
					
				}
				enumeration= related_map.keys();
				while (enumeration.hasMoreElements()) {
					try {
						String record1 = (String) enumeration.nextElement();
						String record2 = related_map.get(record1);			
						int id_record1 = records_map.get(record1);
						int id_record2 = records_map.get(record2);
						System.out.println("------------------------------------> ID_RECORD1: "+id_record1);
						System.out.println("------------------------------------> ID_RECORD2: "+id_record2);
						/*Relations relations = new Relations();
						relations.setRecordsByRefIdRecord1((Records)service.getObject(Records.class, new Integer(id_record1)));
						relations.setRecordsByRefIdRecord2((Records)service.getObject(Records.class, new Integer(id_record2)));
						relations.setRelationTypes((RelationTypes)service.getObject(RelationTypes.class, new Integer(3)));	
						service.add(relations);*/
					} catch (Exception e) {
						System.out.println("errore!!! relazione related");
					}
				}
			} catch (Exception e) {
				System.out.println("-------------------------------->ERRORRE NELLA GENERAZIONE DELLE RELAZIONI");
				System.out.println("-------------------------------->"+xml);
				e.printStackTrace();
			}
			mav = new ModelAndView("import");
			mav.addObject("archiveList", service.getList(Archives.class));
			mav.addObject("impoprtOK", "OK");
			System.out.println(start_time);
			System.out.println("e terminata il "+Watch.getDate()+" alle "+Watch.getTime());
		}
		return mav;
	}
	private int addRecord(String xml,int idArchive,int recordTypes) throws ConfigurationException, DocumentException, UnsupportedEncodingException{
		Records record = new Records();
		record.setXml(xml.getBytes("UTF-8"));
		record.setRecordTypes((RecordTypes)service.getObject(RecordTypes.class, new Integer(recordTypes)));
		Archives archives = (Archives)service.getObject(Archives.class, new Integer(idArchive));
		record.setArchives(archives);		
		//record.setTitle(titleManager.getTitle(record.getXMLReader(),new Integer(idArchive)));
		record.setCreationDate(new Date());
		record.setModifyDate(new Date());
		record.setPosition(0);
		record.setDepth(0);
		XmlId xmlId = archives.getXmlId();
		xmlId.setIdXml(xmlId.getIdXml()+1);
		service.add(record);
		service.update(xmlId);
		return record.getIdRecord();	
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	@Override
	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;		
	}
}
