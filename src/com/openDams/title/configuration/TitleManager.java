package com.openDams.title.configuration;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;

import com.openDams.bean.Records;
import com.openDams.bean.Relations;
import com.openDams.configuration.ConfigurationException;
import com.openDams.dao.OpenDamsServiceProvider;
import com.regesta.framework.xml.XMLReader;

public class TitleManager {
	ConfigurationReader configurationTitleReader = null;

	public TitleManager() {
	}

	/*
	 * public String getTitle(XMLReader xmlReader,Integer id_archive) throws
	 * ConfigurationException{ ArrayList<Object> titles =
	 * configurationTitleReader.getTitlesMap().get(id_archive); String
	 * resultTitle = ""; for(int i=0;i<titles.size();i++){ Title
	 * title=(Title)titles.get(i); if(!title.getText().trim().equals("")){
	 * String[] xpaths=title.getText().split(","); for (int j = 0; j <
	 * xpaths.length; j++) { String
	 * value=xmlReader.getNodeValue(xpaths[j],title.
	 * getMultiple_node_separator()).trim(); if(title.getClassUtil()!=null){ try
	 * { Class<?> c = Class.forName(title.getClassUtil()); Object obj =
	 * c.newInstance(); Method method = c.getMethod(title.getMethod(),
	 * String.class ); // //System.out.println(method.invoke(obj, value)); value
	 * = (String) method.invoke(obj,value); ////System.out.println(value);
	 * }catch (Exception e) { System.err.println("errore!!! "+e.getMessage()); }
	 * } resultTitle+=value; if(j<xpaths.length-1 && !value.equals(""))
	 * resultTitle+=title.getMultiple_path_separator(); }
	 * resultTitle+=title.getSeparator(); } } return resultTitle; }
	 */
	public String buildTitle(XMLReader xmlReader, Integer idArchive, Integer idRecord) throws ConfigurationException, SQLException {
		ArrayList<Object> titles = configurationTitleReader.getTitlesMap().get(idArchive);
		String resultTitle = "";
		for (int i = 0; i < titles.size(); i++) {
			String partialTitle = "";
			Title title = (Title) titles.get(i);
			if (title.getText()!=null && !title.getText().trim().equals("") && (title.getType()==null || !title.getType().equalsIgnoreCase("relation"))) {
				String[] xpaths = title.getText().split(",");
				for (int j = 0; j < xpaths.length; j++) {
					if (xpaths[j].trim().startsWith("{") && xpaths[j].trim().endsWith("}")) {
						String value = xpaths[j].trim();
						partialTitle += value.substring(1, value.length() - 1);
					} else {
						ArrayList<String> values = xmlReader.getNodesValues(xpaths[j]);
						for (int k = 0; k < values.size(); k++) {
							/*
							 * FIXED: per sandro... ho tolto String
							 * value=StringEscapeUtils
							 * .unescapeXml(values.get(k)); perché eliminava gli
							 * spazi prima dei caratteri "escapati"
							 */
							String value = StringEscapeUtils.unescapeXml(values.get(k));
							/*
							 * DIEGO: ho spostato questa parte di codice per
							 * applicare le specifiche classi di trasformazione
							 * e tutte le parti e non solo all'insieme finale
							 */
							if (title.getClassUtil() != null && !title.getMethod().startsWith("deferred-")) {
								try {
									Class<?> c = Class.forName(title.getClassUtil());
									Object obj = c.newInstance();
									Method method = c.getMethod(title.getMethod(), Integer.class, String.class);
									value = (String) method.invoke(obj, idRecord, value);
								} catch (Exception e) {
									System.err.println("errore!!! " + e.getMessage());
								}
							}

							partialTitle += value;
							if (values.size() > 1 && k != values.size() - 1)
								partialTitle += title.getMultiple_node_separator();
						}
						if (j < xpaths.length - 1 && values.size() > 0)
							partialTitle += title.getMultiple_path_separator();
					}
				}

			
				if (title.getClassUtil() != null && title.getMethod().startsWith("deferred-")) {
					try {
						Class<?> c = Class.forName(title.getClassUtil());
						Object obj = c.newInstance();
						Method method = c.getMethod(title.getMethod().replaceAll("^deferred-", ""), Integer.class, String.class);
						partialTitle = (String) method.invoke(obj, idRecord, partialTitle);
					} catch (Exception e) {
						System.err.println("errore!!! " + e.getMessage());
					}
				}	 
				resultTitle += partialTitle;

				if (title.getFind() != null && title.getReplace() != null)
					resultTitle = resultTitle.replaceAll(StringEscapeUtils.unescapeXml(title.getFind()), title.getReplace());

				resultTitle += title.getSeparator();
			}
		}
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();

		decoder.onMalformedInput(CodingErrorAction.IGNORE);
		
		decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
		CharsetEncoder encoder = charset.newEncoder();
		encoder.onMalformedInput(CodingErrorAction.IGNORE);
		encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
		try {
			ByteBuffer byteBuffer = encoder.encode(CharBuffer.wrap(resultTitle));
			CharBuffer charBuffer = decoder.decode(byteBuffer);
			resultTitle = charBuffer.toString();
		} catch (CharacterCodingException e1) {
			e1.printStackTrace();
		}
		return resultTitle;
	}

	public HashMap<String, String[]> parseTitle(String titleString, Integer id_archive) throws ConfigurationException {
		ArrayList<Object> titles = configurationTitleReader.getTitlesMap().get(id_archive);
		int start = 0;
		HashMap<String, String[]> resultMap = new HashMap<String, String[]>();
		String[] composed_title = new String[1];
		composed_title[0] = "";
		for (int i = 0; i < titles.size(); i++) {
			try {
				Title title = (Title) titles.get(i);
				if(title.getType()==null || !title.getType().equalsIgnoreCase("relation")){
					String nome = title.getName();
					String separator = title.getSeparator();
					// System.out.println("TitleManager.parseTitle()33333 "+separator);
					String value = titleString.substring(start, titleString.indexOf(separator));
					// System.out.println("TitleManager.parseTitle()4444");
					start = titleString.indexOf(separator) + separator.length();
					// System.out.println("TitleManager.parseTitle()55555");
					String[] strings = value.split(title.getMultiple_node_separator());
					// System.out.println("TitleManager.parseTitle()6666");
					for (int j = 0, tot = strings.length; j < tot; j++) {
						String string = strings[j].replaceAll(title.getMultiple_path_separator(), " ");
						if (title.getHtml_container() != null && !title.getHtml_container().trim().equals("")) {
							string = "<" + title.getHtml_container() + " style=\"" + title.getStyle() + "\">" + string + "</" + title.getHtml_container() + ">";
							composed_title[0] += string;
						} else {
							composed_title[0] += string + " ";
						}
					}
					for (int j = 0, tot = strings.length; j < tot; j++) {
						String string = strings[j].replaceAll(title.getMultiple_path_separator(), " ");
						strings[j] = string;
					}
					resultMap.put(nome, strings);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		composed_title[0] = composed_title[0].trim();
		resultMap.put("composed_title", composed_title);
		return resultMap;
	}

	public HashMap<String, String[]> parseExtendedTitle(String titleString, Integer id_archive,Integer id_record) throws ConfigurationException {
		HashMap<String, String[]> resultMap = parseTitle(titleString, id_archive);
		HashMap<Integer, Integer> alredyDone= new HashMap<Integer, Integer>();
		alredyDone.put(id_record, id_record);
		scanRelatedTitle(titleString, id_archive,id_record ,resultMap,alredyDone);
		return resultMap;
	}
	@SuppressWarnings("unchecked")
	private void scanRelatedTitle(String titleString, Integer id_archive,Integer id_record,HashMap<String, String[]> resultMap,Map<Integer, Integer> alredyDone) throws ConfigurationException {
		ArrayList<Object> titles = configurationTitleReader.getTitlesMap().get(id_archive);
		try {
			for (int i = 0; i < titles.size(); i++) {			
					Title title = (Title) titles.get(i);
					if(title.getType()!=null && title.getType().equalsIgnoreCase("relation")){
						List<Relations> relationsList = (List<Relations>)OpenDamsServiceProvider.getService().getListFromSQL(Relations.class,"SELECT * FROM relations r where (ref_id_record_1="+id_record+" or ref_id_record_2="+id_record+") and ref_id_relation_type="+title.getId_relation()+";");  
						for (int j = 0; j < relationsList.size(); j++) {
							Relations relations = relationsList.get(j);
							Records relatedRecords = null;
							if(relations.getRecordsByRefIdRecord1().getIdRecord()!=id_record)
								relatedRecords = relations.getRecordsByRefIdRecord1();
							else
								relatedRecords = relations.getRecordsByRefIdRecord2();
							if(relatedRecords.getArchives().getIdArchive().intValue() == new Integer(title.getId_archive()).intValue() && alredyDone.get(relatedRecords.getIdRecord())==null){
								HashMap<String, String[]> parsedTitle = parseTitle(relatedRecords.getTitle(), relatedRecords.getArchives().getIdArchive());
							    String[] toGetTitleValues =  title.getImport_values().split(",");
							    for (int k = 0; k < toGetTitleValues.length; k++) {
									String toGet = StringUtils.substringBefore(toGetTitleValues[k], "[");
									String toName = StringUtils.substringBetween(toGetTitleValues[k], "[", "]");
									String[] titleArray = parsedTitle.get(toGet);
									if(resultMap.get(toName)==null){
										resultMap.put(toName, titleArray);
									}else{
										resultMap.put(toName, fillTitleArray(resultMap.get(toName),titleArray));
									}
								}
							    scanRelatedTitle(relatedRecords.getTitle(), relatedRecords.getArchives().getIdArchive(),relatedRecords.getIdRecord(),resultMap,alredyDone);
							    alredyDone.put(relatedRecords.getIdRecord(), relatedRecords.getIdRecord());
							}
						}
					}
			}
		} catch (HibernateException e) {
			System.out.println("#################Error in parseExtendedTitle:"+e.getMessage()+", reults taken from parseTitle method()###############");
		} catch (NumberFormatException e) {
			System.out.println("#################Error in parseExtendedTitle:"+e.getMessage()+", reults taken from parseTitle method()###############");
		}
	}
	
	public String[] fillTitleArray(String[] toFill,String[] values){
		String[] result =  new String[toFill.length+values.length];
		for (int i = 0; i < toFill.length; i++) {
			result[i]=toFill[i];
		}
		for (int i = 0; i < values.length; i++) {
			result[toFill.length+i]=values[i];
		}
		return result;
	}
	
	public String getFieldValues(String[] values) {
		String result = "";
		if (values != null)
			for (int s = 0; s < values.length; s++) {
				result += values[s];
			}
		return result.trim();
	}

	public void setConfigurationTitleReader(ConfigurationReader configurationTitleReader) {
		this.configurationTitleReader = configurationTitleReader;
	}
}
