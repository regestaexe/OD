package com.openDams.index.analyzers;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.dom4j.DocumentException;

import com.openDams.bean.Records;
import com.openDams.bean.Relations;
import com.openDams.configuration.ConfigurationException;
import com.openDams.dao.OpenDamsServiceProvider;
import com.openDams.index.configuration.IndexConfiguration;
import com.openDams.index.configuration.OpenDamsIndexConfigurationProvider;
import com.openDams.index.factory.DocumentFactory;
import com.openDams.title.configuration.OpenDamsTitleManagerProvider;

public class IndexCleaner {

	public String cropAfterLastSharp(Integer idRecord, String string) {

		if (string != null && string.indexOf("#") > -1) {
			string = StringUtils.substringAfterLast(string, "#");
		}

		return string;
	}

	public String truncate(Integer idRecord, String string) {

		if (string != null && string.length() > 370) {

			int gap = string.indexOf(" ", 350);
			if (gap > 0) {
				string = string.substring(0, gap) + "...";

				if (StringUtils.countMatches(string, "&lt;em&gt;") != StringUtils.countMatches(string, "&lt;/em&gt;")) {
					string = string + "&lt;/em&gt;";
				}
				if (StringUtils.countMatches(string, "&lt;strong&gt;") != StringUtils.countMatches(string, "&lt;/strong&gt;")) {
					string = string + "&lt;/strong&gt;";
				}
				if (StringUtils.countMatches(string, "<em>") != StringUtils.countMatches(string, "</em>")) {
					string = string + "</em>";
				}
				if (StringUtils.countMatches(string, "<strong>") != StringUtils.countMatches(string, "</strong>")) {
					string = string + "</strong>";
				}
			}
		}

		return string;
	}

	public String removeDot(Integer idRecord, String string) {

		if (string != null && string.indexOf(".") > -1) {
			string = string.replaceAll("\\.", "");
			string = string.trim();
		}

		return string;
	}

	public String removeRdfFile(Integer idRecord, String string) {
		System.out.println("\n\n\n\n");
		System.out.println("IndexCleaner.removeRdfFile()");
		System.out.print(string + " --> ");
		if (string != null && string.indexOf(".rdf") > -1) {
			string = string.replaceAll("[a-zA-Z.]*/", "");
		}
		System.out.println(string);
		System.out.println("\n\n\n\n");
		return string;
	}

	public String toLowerCase(Integer idRecord, String string) {
		if (string != null) {
			string = string.toLowerCase().trim();
		}
		return string;
	}

	public String toUpperCase(Integer idRecord, String string) {
		if (string != null) {
			string = string.toUpperCase().trim();
		}
		return string;
	}

	public String cropBeforeLastComma(Integer idRecord, String string) {

		if (string != null && string.indexOf(",") > -1) {
			string = StringUtils.substringBeforeLast(string, ",");
			string = string.trim();
		}

		return string;
	}

	public String cropBeforeLastCommaAndUnescape(Integer idRecord, String string) {

		if (string != null && string.indexOf(",") > -1) {
			string = StringUtils.substringBeforeLast(string, ",");
			string = string.trim();
		}
		if (string != null) {
			string = StringEscapeUtils.unescapeXml(string);
		}

		return string;
	}

	public String cropAfterDash(Integer idRecord, String string) {

		if (string != null && string.indexOf("-") > -1) {
			string = StringUtils.substringAfter(string, "-");
			string = string.trim();
		}

		return string;
	}

	public String firstYear(Integer idRecord, String string) {
		if (string != null && string.length() > 4) {
			String stringt = string.replaceAll("([0-9][0-9][0-9][0-9]).*", "$1");
			if (stringt != null && stringt.length() == 4) {
				string = stringt;
			}
		}
		return string;
	}

	public String fill3zero(Integer idRecord, String string) {
		if (string != null) {
			string = StringUtils.leftPad(string, 3, "0");
		}
		return string;
	}

	public String cropAfterLastSlash(Integer idRecord, String string) {

		if (string != null && string.indexOf("/") > -1) {
			string = StringUtils.substringAfterLast(string, "/");
		}

		return string;
	}

	public String cropBeforeTrattino(Integer idRecord, String string) {

		if (string != null && string.indexOf("-") > -1) {
			string = StringUtils.substringAfterLast(string, "-");
		}

		return string;
	}

	public String cropAfterLastQuestionMark(Integer idRecord, String string) {

		if (string != null && string.indexOf("?") > -1) {
			string = StringUtils.substringAfterLast(string, "?");
		}

		return string;
	}

	public String cropAfterLastQuestionMarkAndBeforeSharp(Integer idRecord, String string) {

		if (string != null && string.indexOf("?") > -1) {
			string = StringUtils.substringAfterLast(string, "?");
		}

		if (string != null && string.indexOf("#") > -1) {
			string = StringUtils.substringBeforeLast(string, "#");
		}
		return string;
	}

	public String normalizeAndCleanDate(Integer idRecord, String string) {
		if (string.indexOf(",") > -1) {
			string = StringUtils.substringBefore(string, ",");
		}
		if (string.indexOf("(") > -1) {
			string = StringUtils.substringBefore(string, "(");
		}
		return normalizeDate(idRecord, string);
	}

	public String normalizeDate(Integer idRecord, String string) {
		if (string.length() < 8) {
			if (string != null) {
				if (StringUtils.countMatches(string, "-") == 1) {
					string = string + "-00";
				} else if (StringUtils.countMatches(string, "-") == 0) {
					if (string.length() == 4) {
						string = string + "-00-00";
					} else {
						string = StringUtils.rightPad(string, 8, "0");
					}
				}
				string = string.replaceAll("-", "");
			}
		}
		return string;
	}
 

	public String importMembershipTitle(Integer idRecord, String string) throws ConfigurationException {
		String result = "";

		@SuppressWarnings("unchecked")
		List<Relations> relationsUnita = (List<Relations>) OpenDamsServiceProvider.getService().getListFromSQL(Relations.class, "SELECT * FROM relations where ref_id_record_1=" + idRecord + " and ref_id_relation_type=25;");
		@SuppressWarnings("unchecked")
		List<Relations> relationsMember = (List<Relations>) OpenDamsServiceProvider.getService().getListFromSQL(Relations.class, "SELECT * FROM relations where ref_id_record_1=" + idRecord + " and ref_id_relation_type=29;");
		@SuppressWarnings("unchecked")
		List<Relations> relationsCarica = (List<Relations>) OpenDamsServiceProvider.getService().getListFromSQL(Relations.class, "SELECT * FROM relations where ref_id_record_1=" + idRecord + " and ref_id_relation_type=26;");

		// persona
		if (relationsMember.size() > 0) {
			Records recordsFatherMember = relationsMember.get(0).getRecordsByRefIdRecord2();
			HashMap<String, String[]> parsedTitle = OpenDamsTitleManagerProvider.getTitleManager().parseTitle(recordsFatherMember.getTitle(), recordsFatherMember.getArchives().getIdArchive());
			String title = OpenDamsTitleManagerProvider.getTitleManager().getFieldValues(parsedTitle.get("notation"));
			String fathersNameMember = getFathersString(recordsFatherMember.getIdRecord());
			if (!fathersNameMember.equals("")) {
				result += fathersNameMember + " " + title;
			} else {
				result += title;
			}
		}
		// carica
		if (relationsCarica.size() > 0) {
			Records recordsFatherCarica = relationsCarica.get(0).getRecordsByRefIdRecord2();
			HashMap<String, String[]> parsedTitle = OpenDamsTitleManagerProvider.getTitleManager().parseTitle(recordsFatherCarica.getTitle(), recordsFatherCarica.getArchives().getIdArchive());
			String title = OpenDamsTitleManagerProvider.getTitleManager().getFieldValues(parsedTitle.get("notation"));
			String fathersNameCarica = getFathersString(recordsFatherCarica.getIdRecord());
			if (!fathersNameCarica.equals("")) {
				result = "<br />" + fathersNameCarica + ", " + title;
			} else {
				result += "<br />" + title;
			}
		}
		// unita organizzativa
		if (relationsUnita.size() > 0) {
			Records recordsFatherUnita = relationsUnita.get(0).getRecordsByRefIdRecord2();
			HashMap<String, String[]> parsedTitle = OpenDamsTitleManagerProvider.getTitleManager().parseTitle(recordsFatherUnita.getTitle(), recordsFatherUnita.getArchives().getIdArchive());
			String title = OpenDamsTitleManagerProvider.getTitleManager().getFieldValues(parsedTitle.get("notation"));
			String fathersNameUnita = getFathersString(recordsFatherUnita.getIdRecord());
			if (!fathersNameUnita.equals("")) {
				result = fathersNameUnita + ", " + title;
			} else {
				result += "<br />" + title;
			}
		}
		try {
			SimpleDateFormat formatoIngresso = new SimpleDateFormat("yyyyMMdd");
			Date data = formatoIngresso.parse(string);
			SimpleDateFormat formatoUscita = new SimpleDateFormat("d MMMM yyyy", Locale.ITALY);
			string = formatoUscita.format(data);

		} catch (Exception e) {

		}
		// System.out.println("+++++++++++"+idRecord+"+++++++++++"+result+"**************"+string+"************");
		return result + "<br />dal " + string;
	}

	public String importUnitaIndex(Integer idRecord, String string) throws ConfigurationException {
		String result = "";

		@SuppressWarnings("unchecked")
		List<Relations> relationsUnita = (List<Relations>) OpenDamsServiceProvider.getService().getListFromSQL(Relations.class, "SELECT * FROM relations where ref_id_record_1=" + idRecord + " and ref_id_relation_type=25;");

		// unita
		if (relationsUnita.size() > 0) {
			Records recordsFatherUnita = relationsUnita.get(0).getRecordsByRefIdRecord2();
			HashMap<String, String[]> parsedTitle = OpenDamsTitleManagerProvider.getTitleManager().parseTitle(recordsFatherUnita.getTitle(), recordsFatherUnita.getArchives().getIdArchive());
			String title = OpenDamsTitleManagerProvider.getTitleManager().getFieldValues(parsedTitle.get("notation"));
			String fathersNameUnita = getFathersString(recordsFatherUnita.getIdRecord());
			if (!fathersNameUnita.equals("")) {
				result += fathersNameUnita + " " + title;
			} else {
				result += title;
			}
		}

		// System.out.println("+++++++++++"+idRecord+"+++++++++++"+result+"**************"+string+"************");
		return result;
	}

	public String importPersonaIndex(Integer idRecord, String string) throws ConfigurationException {
		String result = "";

		@SuppressWarnings("unchecked")
		List<Relations> relationsMember = (List<Relations>) OpenDamsServiceProvider.getService().getListFromSQL(Relations.class, "SELECT * FROM relations where ref_id_record_1=" + idRecord + " and ref_id_relation_type=29;");

		// persona
		if (relationsMember.size() > 0) {
			Records recordsFatherMember = relationsMember.get(0).getRecordsByRefIdRecord2();
			HashMap<String, String[]> parsedTitle = OpenDamsTitleManagerProvider.getTitleManager().parseTitle(recordsFatherMember.getTitle(), recordsFatherMember.getArchives().getIdArchive());
			String title = OpenDamsTitleManagerProvider.getTitleManager().getFieldValues(parsedTitle.get("notation"));
			String fathersNameMember = getFathersString(recordsFatherMember.getIdRecord());
			if (!fathersNameMember.equals("")) {
				result += fathersNameMember + " " + title;
			} else {
				result += title;
			}
		}

		// System.out.println("+++++++++++"+idRecord+"+++++++++++"+result+"**************"+string+"************");
		return result;
	}

	public String importCaricaIndex(Integer idRecord, String string) throws ConfigurationException {
		String result = "";

		@SuppressWarnings("unchecked")
		List<Relations> relationsCarica = (List<Relations>) OpenDamsServiceProvider.getService().getListFromSQL(Relations.class, "SELECT * FROM relations where ref_id_record_1=" + idRecord + " and ref_id_relation_type=26;");

		// persona
		if (relationsCarica.size() > 0) {
			Records recordsFatherCarica = relationsCarica.get(0).getRecordsByRefIdRecord2();
			HashMap<String, String[]> parsedTitle = OpenDamsTitleManagerProvider.getTitleManager().parseTitle(recordsFatherCarica.getTitle(), recordsFatherCarica.getArchives().getIdArchive());
			String title = OpenDamsTitleManagerProvider.getTitleManager().getFieldValues(parsedTitle.get("notation"));
			String fathersNameCarica = getFathersString(recordsFatherCarica.getIdRecord());
			if (!fathersNameCarica.equals("")) {
				result += fathersNameCarica + " " + title;
			} else {
				result += title;
			}
		}

		// System.out.println("+++++++++++"+idRecord+"+++++++++++"+result+"**************"+string+"************");
		return result;
	}

	public String importAttoTitle(Integer idRecord, String string) throws ConfigurationException {
		String result = "";
		String fathersNameAtto = "";
		String title = "";
		@SuppressWarnings("unchecked")
		List<Relations> relationsAtto = (List<Relations>) OpenDamsServiceProvider.getService().getListFromSQL(Relations.class, "SELECT * FROM relations where ref_id_record_1=" + idRecord + " and ref_id_relation_type=23;");
		if (relationsAtto.size() > 0) {
			for (int y = 0; y < relationsAtto.size(); y++) {
				Records recordsFatherAtto = relationsAtto.get(y).getRecordsByRefIdRecord2();
				HashMap<String, String[]> parsedTitle = OpenDamsTitleManagerProvider.getTitleManager().parseTitle(recordsFatherAtto.getTitle(), recordsFatherAtto.getArchives().getIdArchive());
				title = OpenDamsTitleManagerProvider.getTitleManager().getFieldValues(parsedTitle.get("notation"));
				fathersNameAtto += getFathersString(recordsFatherAtto.getIdRecord());
				if (!fathersNameAtto.equals("")) {
					result += " - " + fathersNameAtto + " " + title.replaceAll("A\\.[CG]\\.[ ]+\\|", "").replaceAll("D\\.L\\.[ ]*$", "");
				} else {
					result += " - " + title.replaceAll("A\\.[CG]\\.[ ]+\\|", "").replaceAll("D\\.L\\.[ ]*$", "");
				}
			}
		}

		if (!result.equals(""))
			return string + "<br /><em>" + result.substring(2) + "</em>";
		else
			return string;
	}

	public String sonsDescriptions(Integer idRecord, String string) throws ConfigurationException {
		System.out.println("+++++++++++" + idRecord + " PRIMA +++++++++++ " + string + " ************");
		Records record = (Records) OpenDamsServiceProvider.getService().getObject(Records.class, idRecord);
		Set<Relations> relations = record.getRelationsesForRefIdRecord1();
		if (relations.size() > 0) {
			for (Relations relazione : relations) {
				if (relazione.getRelationTypes().getIdRelationType() == 13) {
					Records recordsSon = relazione.getRecordsByRefIdRecord2();
					try {
						string += " " + recordsSon.getXMLReader().getNodesValues("/rdf:RDF/*/dc:description/text()");
					} catch (DocumentException e) {
						e.printStackTrace();
					}
					Set<Relations> relations2 = recordsSon.getRelationsesForRefIdRecord1();
					if (relations.size() > 0) {
						for (Relations relazione2 : relations2) {
							if (relazione2.getRelationTypes().getIdRelationType() == 13) {
								Records recordsSons2 = relazione2.getRecordsByRefIdRecord2();
								try {
									string += " " + recordsSons2.getXMLReader().getNodesValues("/rdf:RDF/*/dc:description/text()");
								} catch (DocumentException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
		System.out.println("+++++++++++" + idRecord + " DOPO +++++++++++ " + string + " ************");
		return string;
	} 
	
	public String touchFather(Integer idRecord, String string) throws ConfigurationException {
		Records record = (Records) OpenDamsServiceProvider.getService().getObject(Records.class, idRecord);

		Set<Relations> relations = record.getRelationsesForRefIdRecord2();
		if (relations.size() > 0) {
			for (Relations relazione : relations) {
				System.out.println("relazione.getRelationTypes().getIdRelationType() " + relazione.getRelationTypes().getIdRelationType());
				if (relazione.getRelationTypes().getIdRelationType() == 13) {
					Records recordsFather = relazione.getRecordsByRefIdRecord1();
					recordsFather.setModifyDate(new Date());
					OpenDamsServiceProvider.getService().update(recordsFather);

					System.out.println("+++++++++++" + recordsFather.getIdRecord() + "+++++++++++ TOUCHED! ************");
				}

			}

		}
		return string;
	}
    
	public void rebuildFatherIndex(Integer id_record, Integer id_archive,Integer id_relation) throws ConfigurationException, SQLException, DocumentException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException {
		@SuppressWarnings("unchecked")
		List<Relations> relationsList = (List<Relations>)OpenDamsServiceProvider.getService().getListFromSQL(Relations.class,"SELECT * FROM relations r where (ref_id_record_1="+id_record+" or ref_id_record_2="+id_record+") and ref_id_relation_type="+id_relation+";");  
		for (int j = 0; j < relationsList.size(); j++) {
			Relations relations = relationsList.get(j);
			Records relatedRecords = null;
			if(relations.getRecordsByRefIdRecord1().getIdRecord()!=id_record)
				relatedRecords = relations.getRecordsByRefIdRecord1();
			else
				relatedRecords = relations.getRecordsByRefIdRecord2();
			if(relatedRecords.getArchives().getIdArchive().intValue() == id_archive){
				IndexConfiguration indexConfiguration = OpenDamsIndexConfigurationProvider.getIndexConfiguration();
				Directory directory = FSDirectory.open(new File(indexConfiguration.getReal_path() + indexConfiguration.getIndex_location() + "/" + relatedRecords.getArchives().getIdArchive() + "_" + indexConfiguration.getIndex_name()));
				HashMap<Integer, ArrayList<Object>> elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();
				Document doc = DocumentFactory.buildDocument(relatedRecords, relatedRecords.getArchives(), elements_map.get(relatedRecords.getArchives().getIdArchive()));
				indexConfiguration.getIndexManager(relatedRecords.getArchives().getIdArchive()).updateIndex(directory, doc, relatedRecords.getIdRecord().toString(), elements_map.get(relatedRecords.getArchives().getIdArchive()));
			}
		}
		
	}
	
	
	public String importAutoreTitle(Integer idRecord, String string) throws ConfigurationException {
		String result = "";
		@SuppressWarnings("unchecked")
		List<Relations> relationsAutore = (List<Relations>) OpenDamsServiceProvider.getService().getListFromSQL(Relations.class, "SELECT * FROM relations where ref_id_record_1=" + idRecord + " and ref_id_relation_type=24;");
		// persone
		if (relationsAutore.size() > 0) {
			Records recordsFatherAutore = relationsAutore.get(0).getRecordsByRefIdRecord2();
			HashMap<String, String[]> parsedTitle = OpenDamsTitleManagerProvider.getTitleManager().parseTitle(recordsFatherAutore.getTitle(), recordsFatherAutore.getArchives().getIdArchive());
			String title = OpenDamsTitleManagerProvider.getTitleManager().getFieldValues(parsedTitle.get("notation"));
			String fathersNameAutore = getFathersString(recordsFatherAutore.getIdRecord());
			if (!fathersNameAutore.equals("")) {
				result += fathersNameAutore + " " + title;
			} else {
				result += title;
			}
		}
		// System.out.println("+++++++++++"+idRecord+"+++++++++++"+result+"************");
		if (!result.equals(""))
			return "<em>" + result + "</em><br /> " + string;
		else
			return string;
	}

	@SuppressWarnings("unchecked")
	private String getFathersString(Integer idRecord) throws ConfigurationException {
		String result = "";
		List<Relations> relations = (List<Relations>) OpenDamsServiceProvider.getService().getListFromSQL(Relations.class, "SELECT * FROM relations where ref_id_record_2=" + idRecord + " and ref_id_relation_type=13 order by relation_order;");
		if (relations.size() > 0) {
			Records recordsFather = relations.get(0).getRecordsByRefIdRecord1();
			HashMap<String, String[]> parsedTitle = OpenDamsTitleManagerProvider.getTitleManager().parseTitle(recordsFather.getTitle(), recordsFather.getArchives().getIdArchive());
			String title = OpenDamsTitleManagerProvider.getTitleManager().getFieldValues(parsedTitle.get("notation"));
			String fathersName = getFathersString(recordsFather.getIdRecord());
			if (!fathersName.equals("")) {
				result += fathersName + "<br />" + title;
			} else {
				result += title;
			}
		}
		return result;
	}

  

	public static void main(String[] args) {
		System.out.println(new IndexCleaner().cropBeforeLastCommaAndUnescape(1, "Gazzetta Ufficiale - 1&#170; Serie Speciale - Corte Costituzionale n. 44 del 19-10-2011"));
		System.out.println(new IndexCleaner().normalizeDate(1, "201202"));
		// System.out.println(new IndexCleaner().normalizePlace(1,
		// "http://linkedgeodata.org/triplify/node424310982"));
	}
}