package com.openDams.search_builder.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.ArchiveIdentity;
import com.openDams.bean.ArchiveType;
import com.openDams.bean.Archives;
import com.openDams.bean.RecordTypes;
import com.openDams.bean.Records;
import com.openDams.bean.Relations;
import com.openDams.bean.RelationsId;
import com.openDams.bean.SearchSequentials;
import com.openDams.bean.XmlId;
import com.openDams.index.searchers.QueryBuilder;
import com.openDams.index.searchers.Searcher;
import com.openDams.search_builder.utility.ArchiveUtils;
import com.openDams.search_builder.utility.SearchResult;
import com.openDams.search_builder.utility.StoredSearchContainer;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;
import com.regesta.framework.util.DateUtility;
import com.regesta.framework.util.StringUtility;
import com.regesta.framework.xml.XMLReader;

public class AdvSearchController implements Controller {
	private Searcher searcher = null;
	private OpenDamsService service ;
	private TitleManager titleManager;
	private QueryBuilder queryBuilder;
	private int page_size = 15;
	private int idArchive = 0;
	private String excludeList;
	private String fileArchiveList;
	private Map<Integer, String> gridIndexFieldsName;
	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;		
		if (arg0.getParameter("action").equalsIgnoreCase("query_all")) {
			boolean executeFileSearch = false;
			HashMap<Integer, String>  recordsToAddFromFileSearch = null; 
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>find_sons = "+arg0.getParameter("find_sons"));
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>id_filters = "+arg0.getParameter("id_filters"));
			mav = new ModelAndView("search_builder/allResults");
			TermRangeQuery termRangeQuery = null;
			String toSearch = "";
			String date_range_query = "";
			toSearch = URLDecoder.decode(arg0.getParameter("query"),"UTF-8");
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>toSearch="+toSearch);
			if(arg0.getParameter("find_sons")!=null && arg0.getParameter("find_sons").equals("true") && arg0.getParameter("id_filters")!=null){
				ArrayList<String> querys = new ArrayList<String>();
				String[] id_filters = arg0.getParameter("id_filters").split(";");
				for (int i = 0; i < id_filters.length; i++) {
					int idRecord = Integer.parseInt(id_filters[i]);
					querys.add(findXMLId(idRecord));
					findSons(idRecord,querys);
				}
				if(querys.size()>0){
					toSearch+="+(";
					for (int i = 0; i < querys.size(); i++) {
						toSearch+=querys.get(i)+"$";
					}
					toSearch+=")~";
				}
			}else if(arg0.getParameter("id_filters")!=null && !arg0.getParameter("id_filters").trim().equals("")){
				String[] id_filters = arg0.getParameter("id_filters").split(";");
				toSearch+="+(";
				for (int i = 0; i < id_filters.length; i++) {
					int idRecord = Integer.parseInt(id_filters[i]);
					toSearch+=findXMLId(idRecord)+"$";
				}
				toSearch+=")~";
			}
			if(arg0.getParameter("file_search")!=null && arg0.getParameter("file_search").equals("yes")){
				//System.out.println("##############################################################cerco nei file degli archivi"+fileArchiveList+" e cerco "+toSearch);
				executeFileSearch = true;
				recordsToAddFromFileSearch = executeFileSearch(toSearch);			
			} 
			if(arg0.getParameter("date_range_query")!=null && !arg0.getParameter("date_range_query").trim().equals("")){
				date_range_query = arg0.getParameter("date_range_query").trim();
				String startD = "";
				String endD = "";
				String field = "";
				if(date_range_query.indexOf(" ")!=-1){
					String[] dates = date_range_query.split(" ");
					for (int i = 0; i < dates.length; i++) {
						if(dates[i].indexOf("_start")!=-1){
							field = StringUtils.substringBefore(dates[i], "_");
							startD=DateUtility.UserToXwDateTranslate(StringUtils.substringAfterLast(dates[i],":"));
						}else{
							endD=DateUtility.UserToXwDateTranslate(StringUtils.substringAfterLast(dates[i],":"));
						}	
					}
					termRangeQuery = new TermRangeQuery(field,startD,endD,true,true);
				}else{
					field = StringUtils.substringBefore(date_range_query, "_");
					startD=DateUtility.UserToXwDateTranslate(StringUtils.substringAfterLast(date_range_query,":"));
					toSearch+=" +"+field+":"+startD;
				}	
			}
			UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();		
			List<Archives> archiveList = (List<Archives>)service.getListFromSQL(Archives.class,"SELECT * FROM archives  inner join archive_user_role on archives.id_archive=archive_user_role.ref_id_archive where archive_user_role.ref_id_user="+user.getId()+" and not archives.ref_id_archive_identity="+ArchiveIdentity.COLLECTION+" "+ArchiveUtils.getExcludeSLQFields(excludeList)+" order by archives.archive_order;");
			ArrayList<SearchResult> searchResultsList = new ArrayList<SearchResult>();
			if(termRangeQuery!=null){
				toSearch+="+"+termRangeQuery.toString()+"~";
			}
			
			for(int i=0;i<archiveList.size();i++){
				    Archives archives = archiveList.get(i);
				    if (archives.getArchiveTypes().getIdArchiveType()!=ArchiveType.THESAURUS) {
						int totalHits = 0;
						BooleanQuery booleanQuery = null;						
						String[] multipleQuerys = toSearch.split("~");
						try {							
							booleanQuery = queryBuilder.buildQuery(archives.getIdArchive(), multipleQuerys);
							if(executeFileSearch && recordsToAddFromFileSearch.get(archives.getIdArchive())!=null){
								BooleanQuery booleanQueryAndFile = new BooleanQuery();
								booleanQueryAndFile.add(booleanQuery, BooleanClause.Occur.SHOULD);
								booleanQueryAndFile.add(queryBuilder.getQuery(archives.getIdArchive(),"("+recordsToAddFromFileSearch.get(archives.getIdArchive())+")"),BooleanClause.Occur.SHOULD);
								booleanQuery = booleanQueryAndFile;
							}							
							System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<query action=query_all>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+booleanQuery.toString());
							totalHits = searcher.getTotalHitsNumber(booleanQuery,Integer.toString(archives.getIdArchive()),false);
						} catch (Exception e) {
						}
						//System.out.println("toSplitQuery "+toSplitQuery);
						searchResultsList.add(new SearchResult(archives.getIdArchive(), archives.getLabel(),totalHits, toSearch,arg0.getParameter("order_by"),arg0.getParameter("sort_type"),arg0.getParameter("find_sons"),arg0.getParameter("file_search")));
					}
			}
			mav.addObject("searchResultsList", searchResultsList);
		}else if(arg0.getParameter("action").equalsIgnoreCase("search")){
			Document[] hits = null;
			String order_by = null;
			String sort_type = null;
			String toSearch = "";
			boolean executeFileSearch = false;
			HashMap<Integer, String>  recordsToAddFromFileSearch = null; 
			toSearch = URLDecoder.decode(arg0.getParameter("query"),"UTF-8");			
			if(arg0.getParameter("find_sons")!=null && arg0.getParameter("find_sons").equals("true") && arg0.getParameter("id_filters")!=null){
				ArrayList<String> querys = new ArrayList<String>();
				String[] id_filters = arg0.getParameter("id_filters").split(";");
				for (int i = 0; i < id_filters.length; i++) {
					int idRecord = Integer.parseInt(id_filters[i]);
					querys.add(findXMLId(idRecord));
					findSons(idRecord,querys);
				}
				if(querys.size()>0){
					toSearch+="+(";
					for (int i = 0; i < querys.size(); i++) {
						toSearch+=querys.get(i)+"$";
					}
					toSearch+=")~";
				}
			}else if(arg0.getParameter("id_filters")!=null && !arg0.getParameter("id_filters").trim().equals("")){
				String[] id_filters = arg0.getParameter("id_filters").split(";");
				toSearch+="+(";
				for (int i = 0; i < id_filters.length; i++) {
					int idRecord = Integer.parseInt(id_filters[i]);
					toSearch+=findXMLId(idRecord)+"$";
				}
				toSearch+=")~";
			}			
			TermRangeQuery termRangeQuery = null;
			if(arg0.getParameter("order_by")!=null && !arg0.getParameter("order_by").trim().equals("") && !arg0.getParameter("order_by").trim().equals("relevance")){
				order_by = arg0.getParameter("order_by");
			}
			if(arg0.getParameter("sort_type")!=null && !arg0.getParameter("sort_type").trim().equals("")){
				sort_type = arg0.getParameter("sort_type");
			}
			if(arg0.getParameter("date_range_query")!=null && !arg0.getParameter("date_range_query").trim().equals("")){
				String date_range_query = arg0.getParameter("date_range_query").trim();
				String startD = "";
				String endD = "";
				String field = "";
				if(date_range_query.indexOf(" ")!=-1){
					String[] dates = date_range_query.split(" ");
					for (int i = 0; i < dates.length; i++) {
						if(dates[i].indexOf("_start")!=-1){
							field = StringUtils.substringBefore(dates[i], "_");
							startD=DateUtility.UserToXwDateTranslate(StringUtils.substringAfterLast(dates[i],":"));
						}else{
							endD=DateUtility.UserToXwDateTranslate(StringUtils.substringAfterLast(dates[i],":"));
						}	
					}
					termRangeQuery = new TermRangeQuery(field,startD,endD,true,true);
				}else{
					field = StringUtils.substringBefore(date_range_query, "_");
					startD=DateUtility.UserToXwDateTranslate(StringUtils.substringAfterLast(date_range_query,":"));
					toSearch+=" +"+field+":"+startD;
				}	
			}
			if(arg0.getParameter("file_search")!=null && arg0.getParameter("file_search").equals("yes")){
				//System.out.println("##############################################################cerco nei file degli archivi"+fileArchiveList+" e cerco "+toSearch);
				executeFileSearch = true;
				recordsToAddFromFileSearch = executeFileSearch(toSearch);			
			} 
			BooleanQuery booleanQuery = null;
			if(termRangeQuery!=null){
				toSearch+="+"+termRangeQuery.toString()+"~";
			}
			String[] multipleQuerys = toSearch.split("~");
			if (toSearch != null || termRangeQuery!=null){
				int idArchive = Integer.parseInt(arg0.getParameter("idArchive"));
				booleanQuery = queryBuilder.buildQuery(idArchive, multipleQuerys);
				if(executeFileSearch && recordsToAddFromFileSearch.get(idArchive)!=null){
					BooleanQuery booleanQueryAndFile = new BooleanQuery();
					booleanQueryAndFile.add(booleanQuery, BooleanClause.Occur.SHOULD);
					booleanQueryAndFile.add(queryBuilder.getQuery(idArchive,"("+recordsToAddFromFileSearch.get(idArchive)+")"),BooleanClause.Occur.SHOULD);
					booleanQuery = booleanQueryAndFile;
				}	
			}
			mav = new ModelAndView("search_builder/singleArchiveResults");
			int start = 0;
			if(arg0.getParameter("start")!=null){
				start = Integer.parseInt(arg0.getParameter("start"));
			}
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<query action=search>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+booleanQuery.toString());
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<order_by>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+order_by);
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<sort_type>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+sort_type);
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<page_size>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+page_size);
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+start);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>find_sons = "+arg0.getParameter("find_sons"));
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>id_filters = "+arg0.getParameter("id_filters"));
			hits = searcher.singleSearchPaged(booleanQuery, order_by,sort_type, page_size, start, arg0.getParameter("idArchive"), false);
			
			mav.addObject("results",hits);
			mav.addObject("page_size",page_size);
			mav.addObject("titleManager",titleManager);			
			mav.addObject("wordsToHighlight",queryBuilder.getWordsToHilight(multipleQuerys));

		}else if(arg0.getParameter("action").equalsIgnoreCase("getRecord")){
			mav = new ModelAndView("search_builder/singleRecordResult");
			mav.addObject("result",service.getObject(Records.class,new Integer(arg0.getParameter("idRecord"))));
			mav.addObject("titleManager",titleManager);
		}else if(arg0.getParameter("action").equalsIgnoreCase("save_search")){
			UserDetails user =  (UserDetails)((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
			Archives archives = (Archives)service.getObject(Archives.class,idArchive);
			XmlId xmlId = archives.getXmlId();
			xmlId.setIdXml(xmlId.getIdXml()+1);
			System.out.println("has_index "+arg0.getParameter("has_index"));
			String[] to_saves = arg0.getParameter("to_save").split("~|~|~");
			String xml_id = "ricerca.rdf/"+archives.getArchiveXmlPrefix()+StringUtility.fillZero(Integer.toString(xmlId.getIdXml()),7);
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
				   xml+="<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:skos=\"http://www.w3.org/2008/05/skos#\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:bio=\"http://purl.org/vocab/bio/0.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xmlns:ods=\"http://lod.xdams.org/ontologies/ods/\"  >";
				   xml+="<ods:ricerca rdf:about=\""+xml_id+"\">\r\n";
				   xml+="<skos:changeNote rdf:parseType=\"Resource\">\r\n";
				   xml+="<rdf:value>create</rdf:value>\r\n";
				   xml+="<dc:creator rdf:parseType=\"Literal\">"+user.getName()+" "+user.getLastname()+"</dc:creator>\r\n";
				   xml+="<dc:date>"+DateUtility.getSystemDate()+"</dc:date>\r\n";
				   xml+="</skos:changeNote>\r\n";
				   xml+="<dc:identifier>"+arg0.getParameter("search_code")+"</dc:identifier>\r\n";
				   xml+="<dc:title>"+arg0.getParameter("search_title")+"</dc:title>\r\n";
				   xml+="<dc:description>"+arg0.getParameter("save_description")+"</dc:description>\r\n";
				   xml+="<dc:date>"+DateUtility.UserToXwDateTranslate(arg0.getParameter("save_date"))+"</dc:date>\r\n";
				   xml+="<ods:rif_leg rdf:resource=\"legislatura.rdf/"+arg0.getParameter("search_leg")+"\"/>\r\n";
				   xml+="<ods:rif_unitaOrganizzativa rdf:resource=\"unitaOrganizzativa.rdf/"+arg0.getParameter("search_dep")+"\"/>\r\n";
				   xml+="<rdf:Seq rdf:parseType=\"Resource\">\r\n";
			int count_sec = 0;
			ArrayList<Integer> idForRelations = null;
			for (int i = 0; i < to_saves.length; i++) {
				if(to_saves[i].startsWith("panel_archive_")){
					count_sec++;
					xml+="<rdf:_"+count_sec+">{"+StringUtils.substringAfter(to_saves[i], "_record_")+"}</rdf:_"+count_sec+">\r\n";
					if(idForRelations==null)
						idForRelations = new ArrayList<Integer>();
					idForRelations.add(Integer.parseInt(StringUtils.substringAfter(to_saves[i], "_record_")));
				}else if(to_saves[i].startsWith("text_")){
					count_sec++;
					xml+="<rdf:_"+count_sec+"><![CDATA["+to_saves[i]+"]]></rdf:_"+count_sec+">\r\n";
				}else if(to_saves[i].startsWith("title_")){
					count_sec++;
					xml+="<rdf:_"+count_sec+"><![CDATA["+to_saves[i]+"]]></rdf:_"+count_sec+">\r\n";
				}
			}
			if(arg0.getParameter("has_index")!=null && arg0.getParameter("has_index").equals("true")){
				 xml+="<ods:index rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</ods:index>\r\n";
			}else{
				 xml+="<ods:index rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</ods:index>\r\n";
			}		
			xml+="</rdf:Seq>\r\n";
			xml+="<dcterms:audience>"+arg0.getParameter("search_audience")+"</dcterms:audience>\r\n";
			xml+="</ods:ricerca>\r\n";
			xml+="</rdf:RDF>";
			//System.out.println(xml);
			Records records = new Records();
			records.setXml(xml.getBytes());			
			records.setRecordTypes((RecordTypes)service.getObject(RecordTypes.class,RecordTypes.RECORD));				
			records.setArchives(archives);		
			//records.setTitle(titleManager.getTitle(records.getXMLReader(),idArchive));
			records.setCreationDate(new Date());
			records.setModifyDate(new Date());
			records.setPosition(0);
			records.setDepth(0);
			records.setXmlId(xml_id);
			ArrayList<String> txtList = new ArrayList<String>();
		    txtList.add(URLDecoder.decode(arg0.getParameter("search_contents"),"UTF-8").replaceAll("\\<.*?\\>",""));
		    HashMap<String, ArrayList<String>> externalcontentsMap = new HashMap<String, ArrayList<String>>();
		    externalcontentsMap.put("contents", txtList); 
		    records.setExternalcontentsMap(externalcontentsMap);
			service.add(records);
			service.update(xmlId);
			if(idForRelations!=null){
				for (int i = 0; i < idForRelations.size(); i++) {
					Relations relations = new Relations();
					RelationsId relationsId = new RelationsId(records.getIdRecord(),idForRelations.get(i),18);
					List<Relations> rels = (List<Relations>) service.getListFromSQL(Relations.class, "SELECT * FROM relations WHERE ref_id_record_1 = " + records.getIdRecord() + " AND ref_id_record_2=" + idForRelations.get(i) + " AND ref_id_relation_type=18");
					if (rels.size() == 0) {
						relations.setId(relationsId);
						service.add(relations);
					}else{
						service.remove(rels.get(0));
						relations.setId(relationsId);
						service.add(relations);
					}
				}
			}
			mav = new ModelAndView("search_builder/save_result");
			mav.addObject("records",records);
		}else if(arg0.getParameter("action").equalsIgnoreCase("modify_search")){
			UserDetails user =  (UserDetails)((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
			//Archives archives = (Archives)service.getObject(Archives.class,idArchive);
			Records records = (Records)service.getObject(Records.class,Integer.parseInt(arg0.getParameter("idRecord")));
			String changeNotes = records.getXMLReader().getNodeListAsXML("/rdf:RDF/ods:ricerca/skos:changeNote").replaceAll("&lt;","<").replaceAll("&gt;",">");
			String[] to_saves = arg0.getParameter("to_save").split("~|~|~");
			String xml_id = records.getXMLReader().getNodeValue("/rdf:RDF/ods:ricerca/@rdf:about");
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
				   xml+="<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\"   xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:skos=\"http://www.w3.org/2008/05/skos#\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:bio=\"http://purl.org/vocab/bio/0.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n";
				   xml+="<ods:ricerca rdf:about=\""+xml_id+"\">\r\n";
				   xml+=changeNotes;
				   xml+="<skos:changeNote rdf:parseType=\"Resource\">\r\n";
				   xml+="<rdf:value>modify</rdf:value>\r\n";
				   xml+="<dc:creator rdf:parseType=\"Literal\">"+user.getName()+" "+user.getLastname()+"</dc:creator>\r\n";
				   xml+="<dc:date>"+DateUtility.getSystemDate()+"</dc:date>\r\n";
				   xml+="</skos:changeNote>\r\n";
				   xml+="<dc:identifier>"+arg0.getParameter("search_code")+"</dc:identifier>\r\n";
				   xml+="<dc:title>"+arg0.getParameter("search_title")+"</dc:title>\r\n";
				   xml+="<dc:description>"+arg0.getParameter("save_description")+"</dc:description>\r\n";
				   xml+="<dc:date>"+DateUtility.UserToXwDateTranslate(arg0.getParameter("save_date"))+"</dc:date>\r\n";
				   xml+="<ods:rif_leg rdf:resource=\"legislatura.rdf/"+arg0.getParameter("search_leg")+"\"/>\r\n";
				   xml+="<ods:rif_unitaOrganizzativa rdf:resource=\"unitaOrganizzativa.rdf/"+arg0.getParameter("search_dep")+"\"/>\r\n";
				   xml+="<rdf:Seq rdf:parseType=\"Resource\">\r\n";
			int count_sec = 0;
			ArrayList<Integer> idForRelations = null;
			for (int i = 0; i < to_saves.length; i++) {
				if(to_saves[i].startsWith("panel_archive_")){
					count_sec++;					
					xml+="<rdf:_"+count_sec+">{"+StringUtils.substringAfter(to_saves[i], "_record_")+"}</rdf:_"+count_sec+">\r\n";
					if(idForRelations==null)
						idForRelations = new ArrayList<Integer>();
					idForRelations.add(Integer.parseInt(StringUtils.substringAfter(to_saves[i], "_record_")));
				}else if(to_saves[i].startsWith("text_")){
					count_sec++;					
					xml+="<rdf:_"+count_sec+"><![CDATA["+to_saves[i]+"]]></rdf:_"+count_sec+">\r\n";
				}else if(to_saves[i].startsWith("title_")){
					count_sec++;					
					xml+="<rdf:_"+count_sec+"><![CDATA["+to_saves[i]+"]]></rdf:_"+count_sec+">\r\n";
				}
			}
			if(arg0.getParameter("has_index")!=null && arg0.getParameter("has_index").equals("true")){
				 xml+="<ods:index rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</ods:index>\r\n";
			}else{
				 xml+="<ods:index rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</ods:index>\r\n";
			}		
			xml+="</rdf:Seq>\r\n";
			xml+="<dcterms:audience>"+arg0.getParameter("search_audience")+"</dcterms:audience>\r\n";
			xml+="</ods:ricerca>\r\n";
			xml+="</rdf:RDF>";
			System.out.println(xml);
			
			records.setXml(xml.getBytes());				
			//records.setTitle(titleManager.getTitle(records.getXMLReader(),idArchive));
			records.setModifyDate(new Date());
			//records.setXmlId(xml_id);
			ArrayList<String> txtList = new ArrayList<String>();
		    txtList.add(URLDecoder.decode(arg0.getParameter("search_contents"),"UTF-8").replaceAll("\\<.*?\\>",""));
		    HashMap<String, ArrayList<String>> externalcontentsMap = new HashMap<String, ArrayList<String>>();
		    externalcontentsMap.put("contents", txtList); 
		    records.setExternalcontentsMap(externalcontentsMap);
			service.update(records);
			if(idForRelations!=null){
				for (int i = 0; i < idForRelations.size(); i++) {
					Relations relations = new Relations();
					RelationsId relationsId = new RelationsId(records.getIdRecord(),idForRelations.get(i),18);
					List<Relations> rels = (List<Relations>) service.getListFromSQL(Relations.class, "SELECT * FROM relations WHERE ref_id_record_1 = " + records.getIdRecord() + " AND ref_id_record_2=" + idForRelations.get(i) + " AND ref_id_relation_type=18");
					if (rels.size() == 0) {
						relations.setId(relationsId);
						service.add(relations);
					}else{
						service.remove(rels.get(0));
						relations.setId(relationsId);
						service.add(relations);
					}
				}
			}
			mav = new ModelAndView("search_builder/save_result");
			mav.addObject("records",records);
		}else if(arg0.getParameter("action").equalsIgnoreCase("get_code")){
			mav = new ModelAndView("search_builder/get_code");
			try {
				List<SearchSequentials> list = (List<SearchSequentials>) service.getListFromSQL(SearchSequentials.class,"SELECT search_sequentials.* FROM search_sequentials inner join departments on search_sequentials.ref_id_department=departments.id_department where departments.acronym='"+arg0.getParameter("department_acronym")+"';");				
				SearchSequentials searchSequentials = (SearchSequentials)list.get(0);
				if(arg0.getParameter("last_code")==null){
					searchSequentials.setSequentialNumber(searchSequentials.getSequentialNumber()+1);
					service.update(searchSequentials); 
				}
				mav.addObject("searchSequentials",searchSequentials);
			} catch (Exception e) {
			}
		}else if(arg0.getParameter("action").equalsIgnoreCase("check_code")){
			Query query = queryBuilder.getQuery(idArchive, "identifier:"+arg0.getParameter("search_code"));
			int totalHits = searcher.getTotalHitsNumber(query,Integer.toString(idArchive),false);
			mav = new ModelAndView("search_builder/json/check_code");
			mav.addObject("totalHits",totalHits);
		}else if(arg0.getParameter("action").equalsIgnoreCase("load_stored_search")){
			mav = new ModelAndView("search_builder/load_stored_search");
			Records records = (Records)service.getObject(Records.class,Integer.parseInt(arg0.getParameter("idRecord")));
			XMLReader xmlReader = records.getXMLReader();
			int count_seq = xmlReader.getNodeCount("/rdf:RDF/ods:ricerca/rdf:Seq/*");
			ArrayList<StoredSearchContainer> storedSearchContainers = new ArrayList<StoredSearchContainer>();
			boolean hasTitle = false;
			boolean hasIndex = false;
			String index = xmlReader.getNodeValue("/rdf:RDF/ods:ricerca/rdf:Seq/ods:index/text()").trim();
			String indexText = null;
			if(index.equalsIgnoreCase("true")){
				hasIndex = true;
				indexText="<div><ul>";	
			}
			int countText = 0;
			for(int i=0;i<count_seq-1;i++){
				String text = xmlReader.getNodeValue("/rdf:RDF/ods:ricerca/rdf:Seq/rdf:_"+(i+1)+"/text()");
				if(text.startsWith("{") && text.endsWith("}")){
					int id = Integer.parseInt(StringUtils.substringBetween(text,"{","}"));
					Records records2 = (Records)service.getObject(Records.class,id);
					String title = getSearchResultTitle(records2.getTitle(), records2.getArchives().getIdArchive());
					if(hasIndex)
						indexText+="<li> - <!--start index voice-->"+title+"<!--end index voice--></li>";
					StoredSearchContainer storedSearchContainer = new StoredSearchContainer(StoredSearchContainer.TYPE_PANEL, null, id, records2.getArchives().getIdArchive().intValue(),title);
					storedSearchContainers.add(storedSearchContainer);
				}else if(text.startsWith("text_")){
					countText++;
					StoredSearchContainer storedSearchContainer = new StoredSearchContainer(StoredSearchContainer.TYPE_TEXT, StringUtils.substringAfter(text, "text_"), 0, 0, "Sezione testuale "+countText);
					storedSearchContainers.add(storedSearchContainer);
				}else if(text.startsWith("title_")){
					hasTitle=true;
					StoredSearchContainer storedSearchContainer = new StoredSearchContainer(StoredSearchContainer.TYPE_TITLE, StringUtils.substringAfter(text, "title_"), 0, 0, "Titolo documento");
					storedSearchContainers.add(storedSearchContainer);
				}
			}
			if(hasIndex){
				if(hasTitle){
					StoredSearchContainer storedSearchContainer = new StoredSearchContainer(StoredSearchContainer.TYPE_INDEX, indexText , 0, 0, "Indice");
					storedSearchContainers.add(1,storedSearchContainer);
				}else{
					StoredSearchContainer storedSearchContainer = new StoredSearchContainer(StoredSearchContainer.TYPE_INDEX, indexText, 0, 0, "Indice");
					storedSearchContainers.add(0,storedSearchContainer);
				}
				indexText+="</ul></div>";
			}
			mav.addObject("storedSearchContainers",storedSearchContainers);
		}else if(arg0.getParameter("action").equalsIgnoreCase("get_modify_values")){
			mav = new ModelAndView("search_builder/json/modify_values");
			mav.addObject("records",service.getObject(Records.class,new Integer(arg0.getParameter("idRecord"))));
		}
		return mav;
	}
	@SuppressWarnings("unchecked")
	private void findSons(int idRecord,ArrayList<String> querys){
    	List<Records> list = (List<Records>) service.getListFromSQL(Records.class,"SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_1 where relations.ref_id_record_2="+idRecord+" and (relations.ref_id_relation_type=1 or relations.ref_id_relation_type=11) union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+idRecord+" and relations.ref_id_relation_type=2 union SELECT * FROM records inner join relations on  records.id_record = relations.ref_id_record_2 where relations.ref_id_record_1="+idRecord+" and relations.ref_id_relation_type=10");
        if(list!=null && list.size()>0){
        	for (int i = 0; i < list.size(); i++) {
				Records records = list.get(i);
				String xmlId = StringUtils.substringBefore(records.getTitle(),"[").replaceAll("ç","");
				if(xmlId.indexOf("/")!=-1)
					xmlId=StringUtils.substringAfterLast(xmlId,"/");
				    querys.add(gridIndexFieldsName.get(records.getArchives().getIdArchive())+":"+xmlId);
					findSons(records.getIdRecord(),querys);
			}
        }
    }
	private String findXMLId(int idRecord){
		String result = "";
		Records records =  (Records)service.getObject(Records.class,new Integer(idRecord));
		String xmlId = StringUtils.substringBefore(records.getTitle(),"[").replaceAll("ç","");
		if(xmlId.indexOf("/")!=-1)
			xmlId=StringUtils.substringAfterLast(xmlId,"/");
		    result=gridIndexFieldsName.get(records.getArchives().getIdArchive())+":"+xmlId;		
		return result;
    }
	@SuppressWarnings("unchecked")
	private HashMap<Integer, String> executeFileSearch(String toSearchInput){
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		if(toSearchInput.indexOf("contents:")!=-1){			
			try {
				String toSearch = StringUtils.substringBetween(toSearchInput,"contents:", "$");
				if(!toSearch.trim().equals("")){					
					String[] fileArchivesId = fileArchiveList.split(",");
					for (int i = 0; i < fileArchivesId.length; i++) {							
								int idArchive = Integer.parseInt(fileArchivesId[i]);
								BooleanQuery booleanQuery = null;
								try {			
									String[] multipleQuerys = new String[1];
									multipleQuerys[0] = "contents:" + toSearch;
									booleanQuery = queryBuilder.buildQuery(idArchive, multipleQuerys);
									//System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< query file search >>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+booleanQuery.toString());									
									Document[] hits = searcher.singleSearch(booleanQuery, null, 0, fileArchivesId[i], false);
									for (int j = 0; j < hits.length; j++) {
										Document docsearch = hits[j];
										String id_record = docsearch.get("id_record");
										List<Relations> list = (List<Relations>) service.getListFromSQL(Relations.class,"SELECT * FROM relations where ref_id_relation_type=22 and ref_id_record_2="+id_record+";");
										for (int k = 0; k < list.size(); k++) {
											Relations relations = list.get(k);
											Records records = relations.getRecordsByRefIdRecord1();
											int idNormalRecord = records.getIdRecord();
											int idArchiveNormalRecord = records.getArchives().getIdArchive();
											String toPuth = "";
											if(result.get(idArchiveNormalRecord)!=null){
												toPuth = result.get(idArchiveNormalRecord)+"id_record:"+idNormalRecord+"$";
											}else{
												toPuth = "id_record:"+idNormalRecord+"$";
											}
											result.put(idArchiveNormalRecord,toPuth);
										}
									}
								} catch (Exception e) {
								}
					}
				}
			} catch (Exception e) {

			}
		}		
		return result;
    }
	/*private String getXMLId(int idRecord){
		Records records =  (Records)service.getObject(Records.class,new Integer(idRecord));
		String xmlId = StringUtils.substringBefore(records.getTitle(),"[").replaceAll("ç","");
		if(xmlId.indexOf("/")!=-1)
			xmlId=StringUtils.substringAfterLast(xmlId,"/");
		return xmlId;
    }*/	

	private String getSearchResultTitle(String titleRecord,int idArchive){
		String title = "";
		try {
			HashMap<String, String[]> parsedTitle = titleManager.parseTitle(titleRecord,idArchive);
				String[] titleArray = parsedTitle.get("notation");				
				StringBuffer titleBuffer = new StringBuffer();
				if (titleArray != null) {
					for (String singleTitle : titleArray) {
						try {
							titleBuffer.append(singleTitle.replaceAll("ç", " "));
						} catch (Exception e) {
						}
					}
				}
				titleArray = parsedTitle.get("title");
				if (titleArray != null) {
					for (String singleTitle : titleArray) {
						try {									
							titleBuffer.append(singleTitle.replaceAll("ç", " "));
						} catch (Exception e) {
						}
					}
				}
				title = (titleBuffer.toString());
				if (title.trim().equals("")) {
					title = "[senza titolo]";
				}
				title = title.replaceAll("&amp;apos;", "'");
				title = title.replaceAll("&apos;", "'");
				
		} catch (Exception a) {
			a.printStackTrace();
		}
		return title;
	}
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
	/*public void setPage_size(int pageSize) {
		page_size = pageSize;
	}*/
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}
	public void setQueryBuilder(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}
	public void setIdArchive(int idArchive) {
		this.idArchive = idArchive;
	}
	public void setExcludeList(String excludeList) {
		this.excludeList = excludeList;
	}
	public Map<Integer, String> getGridIndexFieldsName() {
		return gridIndexFieldsName;
	}
	public void setGridIndexFieldsName(Map<Integer, String> gridIndexFieldsName) {
		this.gridIndexFieldsName = gridIndexFieldsName;
	}
	public void setFileArchiveList(String fileArchiveList) {
		this.fileArchiveList = fileArchiveList;
	}

}
