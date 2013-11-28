package com.openDams.index.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.dom4j.DocumentException;

import com.openDams.bean.Archives;
import com.openDams.bean.Records;
import com.openDams.bean.Relations;
import com.openDams.dao.OpenDamsServiceProvider;
import com.openDams.index.configuration.Element;
import com.regesta.framework.xml.XMLReader;

public class DocumentFactory {
	public final static String ID_RECORD = "id_record";
	public final static String TITLE_RECORD = "title_record";
	public final static String ID_ARCHIVE = "id_archive";
	public final static String ARCHIVE_LABEL = "archive_label";
	public DocumentFactory() {
	}
	public synchronized static Document buildDocument(Records record,Archives archives,ArrayList<Object> elements) throws DocumentException{
		try {
			//System.out.println("#######################################################################1 DocumentFactory.buildDocument()");
			Document doc = new Document();
			doc.add(new Field(ID_RECORD, record.getIdRecord().toString(), LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			doc.add(new Field(TITLE_RECORD, record.getTitle(), LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			doc.add(new Field(ID_ARCHIVE, Integer.toString(archives.getIdArchive()), LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			doc.add(new Field(ARCHIVE_LABEL, archives.getLabel(), LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			XMLReader xmlReader = record.getXMLReader();
			HashMap<Integer, Integer> alredyDone= new HashMap<Integer, Integer>();
			ArrayList<Element> externalElements  = new ArrayList<Element>();
			for(int i=0;i<elements.size();i++){
				Element element=(Element)elements.get(i);
				if(element.getType()==null || (!element.getType().equalsIgnoreCase("external") && !element.getType().equalsIgnoreCase("boostRecord") && !element.getType().equalsIgnoreCase("relation") && !element.getType().equalsIgnoreCase("rebuilder"))){
				String[] search_alias = element.getSearch_alias().split(",");
				@SuppressWarnings("unused")
				String sortValue = "";
				ArrayList<String> values =  xmlReader.getNodesValues(element.getText());
				for (int k = 0; k < values.size(); k++) {
					//String value = values.get(k);
					String value = StringEscapeUtils.unescapeXml(values.get(k).trim());
					sortValue+=" "+value;
					for (int j = 0; j < search_alias.length; j++) {					
						if(value!=null && !value.equals("")){
							try { 
								if(element.getClassUtil()!=null){
									Class<?> c = Class.forName(element.getClassUtil());
									Object obj = c.newInstance();
									Method method =  c.getMethod(element.getMethod(), Integer.class ,String.class );
									value = (String) method.invoke(obj,record.getIdRecord(),value);
								}
							} catch (ClassNotFoundException e) {
								throw new DocumentException(e.getMessage());
							} catch (SecurityException e) {
								throw new DocumentException(e.getMessage());
							} catch (NoSuchMethodException e) {
								throw new DocumentException(e.getMessage());
							} catch (IllegalArgumentException e) {
								throw new DocumentException(e.getMessage());
							} catch (IllegalAccessException e) {
								throw new DocumentException(e.getMessage());
							} catch (InvocationTargetException e) {
								throw new DocumentException(e.getMessage());
							} catch (InstantiationException e) {
								throw new DocumentException(e.getMessage());
							}
							if(j==0){
								if(element.getKey_style().equalsIgnoreCase("multi")){
									doc.add(new Field(search_alias[j].trim()+"_one", value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType("one")));
									doc.add(new Field(search_alias[j].trim(), value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType("double")));
								}else{
									doc.add(new Field(search_alias[j].trim(), value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType(element.getKey_style())));
								}
							}else{
								doc.add(new Field(search_alias[j].trim(), value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType("double")));
							}
						}
					}
				}
				if( (values==null || values.size()==0) && element.getIf_empty_default()!=null){
					for (int j = 0; j < search_alias.length; j++) {
						doc.add(new Field(search_alias[j].trim(), element.getIf_empty_default(), LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType(element.getKey_style())));
					}
				}
			  }else if(element.getType()!=null && element.getType().equalsIgnoreCase("boostRecord")){
				  if(xmlReader.getNodeValue(element.getText())!=null && !xmlReader.getNodeValue(element.getText()).equals("")){
					  doc.setBoost(Float.parseFloat(element.getBoost()));					  
				  }  
			  }else if(element.getType()!=null && element.getType().equalsIgnoreCase("relation")){
				  alredyDone.put(record.getIdRecord(), record.getIdRecord());
				  importFromRelatedDocuments(element, record.getIdRecord(), doc, alredyDone);
			  }else if(element.getType()!=null && element.getType().equalsIgnoreCase("rebuilder")){
					try {
						if(element.getClassUtil()!=null){
							Class<?> c = Class.forName(element.getClassUtil());
							Object obj = c.newInstance();
							Method method =  c.getMethod(element.getMethod(),Integer.class,Integer.class,Integer.class );
							method.invoke(obj,record.getIdRecord(),new Integer(element.getId_archive()),new Integer(element.getId_relation()));
						}
					} catch (ClassNotFoundException e) {
						throw new DocumentException(e.getMessage());
					} catch (SecurityException e) {
						throw new DocumentException(e.getMessage());
					} catch (NoSuchMethodException e) {
						throw new DocumentException(e.getMessage());
					} catch (IllegalArgumentException e) {
						throw new DocumentException(e.getMessage());
					} catch (IllegalAccessException e) {
						throw new DocumentException(e.getMessage());
					} catch (InvocationTargetException e) {
						throw new DocumentException(e.getMessage());
					} catch (InstantiationException e) {
						throw new DocumentException(e.getMessage());
					}
					  		
				}else{
				  externalElements.add(element);
			  }
			}
			if(record.getExternalcontentsMap()!=null){
				HashMap<String, ArrayList<String>> externalcontentsMap = record.getExternalcontentsMap();
				Set<String> keys = externalcontentsMap.keySet();
				Iterator<String> iterator = keys.iterator();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					ArrayList<String> values = externalcontentsMap.get(key);
					Element element = null;
					for (int i = 0; i < externalElements.size(); i++) {
						if(externalElements.get(i).getSearch_alias().equalsIgnoreCase(key)){
							element = externalElements.get(i);
							break;
						}
					}
					if(element!=null)
					for (int i = 0; i < values.size(); i++) {
						doc.add(new Field(key, values.get(i), LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType(element.getKey_style())));
					}
				}
			}
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			Document doc = new Document();
			doc.add(new Field(ID_RECORD, record.getIdRecord().toString(), LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			doc.add(new Field(TITLE_RECORD, record.getTitle(), LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			doc.add(new Field(ID_ARCHIVE, Integer.toString(archives.getIdArchive()), LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			doc.add(new Field(ARCHIVE_LABEL, archives.getLabel(), LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			return getErrorDocument(elements,doc);
			//throw new DocumentException(e.getMessage());
		}
	}
	public synchronized static Document buildDocument(String id_record,String id_archive,String archive_label,String title,String xml, ArrayList<Object> elements) throws DocumentException{
		try {
			//System.out.println("#######################################################################2 DocumentFactory.buildDocument()");
			Document doc = new Document();
			doc.add(new Field(ID_RECORD, id_record, LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			doc.add(new Field(TITLE_RECORD,title, LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			doc.add(new Field(ID_ARCHIVE, id_archive, LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			doc.add(new Field(ARCHIVE_LABEL, archive_label, LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			XMLReader xmlReader = new XMLReader(xml);
			HashMap<Integer, Integer> alredyDone= new HashMap<Integer, Integer>();
			for(int i=0;i<elements.size();i++){
				Element element=(Element)elements.get(i);
				if(element.getType()==null || (!element.getType().equalsIgnoreCase("external") && !element.getType().equalsIgnoreCase("boostRecord") && !element.getType().equalsIgnoreCase("relation") && !element.getType().equalsIgnoreCase("rebuilder"))){
						String[] search_alias = element.getSearch_alias().split(",");
						//String value = xmlReader.getNodeValue(element.getText());
						@SuppressWarnings("unused")
						String sortValue = "";
						ArrayList<String> values =  xmlReader.getNodesValues(element.getText());
						for (int k = 0; k < values.size(); k++) {
							//String value = values.get(k);
							String value=StringEscapeUtils.unescapeXml(values.get(k).trim());
							sortValue+=" "+value;
							for (int j = 0; j < search_alias.length; j++) {				
								if(value!=null && !value.equals("")){
									try {
										if(element.getClassUtil()!=null){
											Class<?> c = Class.forName(element.getClassUtil());
											Object obj = c.newInstance();
											Method method =  c.getMethod(element.getMethod(),Integer.class,String.class );
											//System.out.println(method.invoke(obj, value));
											value = (String) method.invoke(obj,new Integer(id_record),value);
										}
									} catch (ClassNotFoundException e) {
										throw new DocumentException(e.getMessage());
									} catch (SecurityException e) {
										throw new DocumentException(e.getMessage());
									} catch (NoSuchMethodException e) {
										throw new DocumentException(e.getMessage());
									} catch (IllegalArgumentException e) {
										throw new DocumentException(e.getMessage());
									} catch (IllegalAccessException e) {
										throw new DocumentException(e.getMessage());
									} catch (InvocationTargetException e) {
										throw new DocumentException(e.getMessage());
									} catch (InstantiationException e) {
										throw new DocumentException(e.getMessage());
									}
									if(j==0){
										if(element.getKey_style().equalsIgnoreCase("multi")){
											doc.add(new Field(search_alias[j].trim()+"_one", value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType("one")));
											doc.add(new Field(search_alias[j].trim(), value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType("double")));
										}else{
											doc.add(new Field(search_alias[j].trim(), value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType(element.getKey_style())));
										}
									}else{
										doc.add(new Field(search_alias[j].trim(), value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType("double")));
									}
									//doc.add(new Field(search_alias[j].trim(), value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType(element.getKey_style())));
								}
							}
						}
						if( (values==null || values.size()==0) && element.getIf_empty_default()!=null){
							for (int j = 0; j < search_alias.length; j++) {
								doc.add(new Field(search_alias[j].trim(), element.getIf_empty_default(), LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType(element.getKey_style())));
							}
						}
				}else if(element.getType()!=null && element.getType().equalsIgnoreCase("boostRecord")){
					  if(xmlReader.getNodeValue(element.getText())!=null && !xmlReader.getNodeValue(element.getText()).equals("")){
						  doc.setBoost(new Float(element.getBoost()));
					  } 
				}else if(element.getType()!=null && element.getType().equalsIgnoreCase("relation")){
					alredyDone.put(new Integer(id_record), new Integer(id_record));
					importFromRelatedDocuments(element, new Integer(id_record), doc, alredyDone);
					  		
				}else if(element.getType()!=null && element.getType().equalsIgnoreCase("rebuilder")){
					try {
						if(element.getClassUtil()!=null){
							Class<?> c = Class.forName(element.getClassUtil());
							Object obj = c.newInstance();
							Method method =  c.getMethod(element.getMethod(),Integer.class,Integer.class,Integer.class );
							method.invoke(obj,new Integer(id_record),new Integer(element.getId_archive()),new Integer(element.getId_relation()));
						}
					} catch (ClassNotFoundException e) {
						throw new DocumentException(e.getMessage());
					} catch (SecurityException e) {
						throw new DocumentException(e.getMessage());
					} catch (NoSuchMethodException e) {
						throw new DocumentException(e.getMessage());
					} catch (IllegalArgumentException e) {
						throw new DocumentException(e.getMessage());
					} catch (IllegalAccessException e) {
						throw new DocumentException(e.getMessage());
					} catch (InvocationTargetException e) {
						throw new DocumentException(e.getMessage());
					} catch (InstantiationException e) {
						throw new DocumentException(e.getMessage());
					}
					  		
				}
			}
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			Document doc = new Document();
			doc.add(new Field(ID_RECORD, id_record, LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			doc.add(new Field(TITLE_RECORD,title, LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			doc.add(new Field(ID_ARCHIVE, id_archive, LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			doc.add(new Field(ARCHIVE_LABEL, archive_label, LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
			return getErrorDocument(elements,doc);
			//throw new DocumentException(e.getMessage());
		}
	}
	
	private static void importFromRelatedDocuments(Element element,Integer id_record,Document doc,HashMap<Integer, Integer> alredyDone) throws DocumentException{
  		System.out.println("VADO A PRENDERE I VALORI DAI RECORD COLLEGATI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		@SuppressWarnings("unchecked")
		List<Relations> relationsList = (List<Relations>)OpenDamsServiceProvider.getService().getListFromSQL(Relations.class,"SELECT * FROM relations r where (ref_id_record_1="+id_record+" or ref_id_record_2="+id_record+") and ref_id_relation_type="+element.getId_relation()+";");  
		for (int z = 0; z < relationsList.size(); z++) {
			Relations relations = relationsList.get(z);
			Records relatedRecords = null;
			if(relations.getRecordsByRefIdRecord1().getIdRecord()!= id_record)
				relatedRecords = relations.getRecordsByRefIdRecord1();
			else
				relatedRecords = relations.getRecordsByRefIdRecord2();
			if(relatedRecords.getArchives().getIdArchive().intValue() == new Integer(element.getId_archive()).intValue() && alredyDone.get(relatedRecords.getIdRecord())==null){
			    String[] toGetValuesXpaths =  element.getText().split(",");
			    String[] search_alias = element.getSearch_alias().split(",");
			    for (int y = 0; y < toGetValuesXpaths.length; y++) {
					String xpath = toGetValuesXpaths[y];
					ArrayList<String> values =  relatedRecords.getXMLReader().getNodesValues(xpath);
					for (int k = 0; k < values.size(); k++) {
						String value=StringEscapeUtils.unescapeXml(values.get(k).trim());
						for (int j = 0; j < search_alias.length; j++) {				
							if(value!=null && !value.equals("")){
								try {
									if(element.getClassUtil()!=null){
										Class<?> c = Class.forName(element.getClassUtil());
										Object obj = c.newInstance();
										Method method =  c.getMethod(element.getMethod(),Integer.class,String.class );
										value = (String) method.invoke(obj,id_record,value);
									}
								} catch (ClassNotFoundException e) {
									throw new DocumentException(e.getMessage());
								} catch (SecurityException e) {
									throw new DocumentException(e.getMessage());
								} catch (NoSuchMethodException e) {
									throw new DocumentException(e.getMessage());
								} catch (IllegalArgumentException e) {
									throw new DocumentException(e.getMessage());
								} catch (IllegalAccessException e) {
									throw new DocumentException(e.getMessage());
								} catch (InvocationTargetException e) {
									throw new DocumentException(e.getMessage());
								} catch (InstantiationException e) {
									throw new DocumentException(e.getMessage());
								}
								if(j==0){
									if(element.getKey_style().equalsIgnoreCase("multi")){
										doc.add(new Field(search_alias[j].trim()+"_one", value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType("one")));
										doc.add(new Field(search_alias[j].trim(), value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType("double")));
									}else{
										doc.add(new Field(search_alias[j].trim(), value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType(element.getKey_style())));
									}
								}else{
									doc.add(new Field(search_alias[j].trim(), value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType("double")));
								}
							}
						}
					}
				}
			    alredyDone.put(relatedRecords.getIdRecord(), relatedRecords.getIdRecord());
			}
		}
	
}
	
	public synchronized static Document buildGenericdDocument(Records record,Archives archives) throws DocumentException{
		Document doc = new Document();
		doc.add(new Field(ID_RECORD, record.getIdRecord().toString(), LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
		doc.add(new Field(TITLE_RECORD, record.getTitle(), LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
		doc.add(new Field(ID_ARCHIVE, Integer.toString(archives.getIdArchive()), LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
		doc.add(new Field(ARCHIVE_LABEL, archives.getLabel(), LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
		HashMap<String, ArrayList<String>> xpatsMap = record.getXpatsMap();
		Set<String> set = xpatsMap.keySet();
		for (String string : set) {	
		    ArrayList<String> strings = xpatsMap.get(string);
		    for (int i = 0; i < strings.size(); i++) {
		    	doc.add(new Field(string, strings.get(i), LuceneFactory.getStore("no"), LuceneFactory.getIndexType("ANALYZED")));
			}
		}
		return doc;
	}
	public synchronized static Document buildGenericdDocument(String id_record,String id_archive,String archive_label,String title,String xml) throws DocumentException{
		Document doc = new Document();
		doc.add(new Field(ID_RECORD, id_record, LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
		doc.add(new Field(TITLE_RECORD, title, LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
		doc.add(new Field(ID_ARCHIVE, id_archive, LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
		doc.add(new Field(ARCHIVE_LABEL, archive_label, LuceneFactory.getStore("yes"), LuceneFactory.getIndexType("NOT_ANALYZED")));
		XMLReader xmlReader = new XMLReader(xml);
		HashMap<String, ArrayList<String>> xpatsMap = new HashMap<String, ArrayList<String>>();
		xmlReader.analyzeNodes(xpatsMap);		
		Set<String> set = xpatsMap.keySet();
		for (String string : set) {	
		    ArrayList<String> strings = xpatsMap.get(string);
		    for (int i = 0; i < strings.size(); i++) {
		    	doc.add(new Field(string, strings.get(i), LuceneFactory.getStore("no"), LuceneFactory.getIndexType("ANALYZED")));
			}
		}
		return doc;
	}
	public static boolean checkMultiFieldValue(HashMap<Integer, ArrayList<Object>> elements_map,String fieldName,String[] requestArchives){
		boolean result = true;
		for (int x = 0; x < requestArchives.length; x++) {
			ArrayList<Object> elements = elements_map.get(new Integer(requestArchives[x]));
			for(int i=0;i<elements.size();i++){
				Element element=(Element)elements.get(i);
				if(element.getType()==null || !element.getType().equalsIgnoreCase("external")){						
					String[] searchAliases = element.getSearch_alias().split(",");
					for (int k = 0; k < searchAliases.length; k++) {
						if(searchAliases[k].equalsIgnoreCase(fieldName)){
							if(element.getKey_style().equalsIgnoreCase("multi")){
								result = true;
							}else{
								return false;
							}
						}
					}						
				}
			}
		}
		return result;
	}
	private static Document getErrorDocument(ArrayList<Object> elements,Document doc){
		for(int i=0;i<elements.size();i++){
			Element element=(Element)elements.get(i);
			if(element.getType()==null || (!element.getType().equalsIgnoreCase("external") && !element.getType().equalsIgnoreCase("boostRecord"))){
				String[] search_alias = element.getSearch_alias().split(",");
				@SuppressWarnings("unused")
				String sortValue = "";
				String value= "n/a";
				sortValue+=" "+value;
				for (int j = 0; j < search_alias.length; j++) {				
					doc.add(new Field(search_alias[j].trim(), value, LuceneFactory.getStore(element.getLucene_store_type()), LuceneFactory.getIndexType("one")));
				}			
			}
		}
		return doc;
	}
}
