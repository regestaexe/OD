package com.openDams.note.controller;


import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.util.Version;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Archives;
import com.openDams.bean.Departments;
import com.openDams.bean.NoteType;
import com.openDams.bean.NoteTypes;
import com.openDams.bean.Notes;
import com.openDams.bean.Records;
import com.openDams.bean.Users;
import com.openDams.index.factory.LuceneFactory;
import com.openDams.index.searchers.QueryBuilder;
import com.openDams.index.searchers.Searcher;
import com.openDams.services.OpenDamsService;
import com.openDams.title.configuration.TitleManager;

public class NotesController implements Controller{
	private OpenDamsService service ;
	private QueryBuilder queryBuilder;
	private Searcher searcher = null;
	private TitleManager titleManager;
	private  int page_size = 1;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if(arg0.getParameter("action")!=null){
			if(arg0.getParameter("action").equals("archiveNoteList")){
				mav = new ModelAndView("notes/archiveNoteList");		
				if(!arg0.getParameter("idDepartment").trim().equals("")){
					String department = arg0.getParameter("idDepartment");
					List<Departments> departmentsList = (List<Departments>)service.getListFromSQL(Departments.class, "SELECT * FROM departments where acronym = '"+department+"';");
					Departments departments = null;		
					if(departmentsList!=null && departmentsList.size()>0){
						departments = departmentsList.get(0);	
					}
					if(departments!=null){
						mav.addObject("noteList", service.getListFromSQL(Notes.class, "SELECT * FROM notes where ref_id_department="+departments.getIdDepartment()+" AND ref_id_archive="+arg0.getParameter("idArchive")+" AND (ref_id_note_type="+NoteType.APPLICATION_NOTE+" OR ref_id_note_type="+NoteType.USER_APPLICATION_NOTE+") ORDER BY date DESC LIMIT "+page_size+";"));
					}
				}
				//service.getListFromSQL(Notes.class,"SELECT * FROM notes where ref_id_user="+arg0.getParameter("idUser")+" AND ref_id_archive="+arg0.getParameter("idArchive")+" AND ref_id_note_type="+NoteType.ARCHIVE_NOTE+" order by date desc;"));
			}else if(arg0.getParameter("action").equals("save_note")){
				String note_text = arg0.getParameter("note_text");
				int idArchive = Integer.parseInt(arg0.getParameter("idArchive"));
				int idUser = Integer.parseInt(arg0.getParameter("idUser"));				
				int noteTypes = Integer.parseInt(arg0.getParameter("noteTypes"));
				Notes notes = new Notes();
				notes.setDate(new Date());
				notes.setArchives((Archives)service.getObject(Archives.class, idArchive));
				notes.setUsers((Users)service.getObject(Users.class, idUser));
				notes.setNoteText(note_text);
				if(arg0.getParameter("idRecord")!=null){
					int idRecord = Integer.parseInt(arg0.getParameter("idRecord"));
					notes.setRecords((Records)service.getObject(Records.class, idRecord));
				}
				if(arg0.getParameter("note_title")!=null){
					notes.setNoteTitle(arg0.getParameter("note_title"));
				}
				if(arg0.getParameter("idDepartment")!=null && !arg0.getParameter("idDepartment").trim().equals("")){
					String department = arg0.getParameter("idDepartment");
					notes.setDepartments((Departments)service.getListFromSQL(Departments.class, "SELECT * FROM departments where acronym = '"+department+"';").get(0));
				}
				notes.setNoteTypes((NoteTypes)service.getObject(NoteTypes.class, noteTypes));				
				service.add(notes);
				mav = new ModelAndView("notes/addNoteResult");
				mav.addObject("notes",notes);
			}else if(arg0.getParameter("action").equals("modify_note")){				
				String note_text = arg0.getParameter("note_text");
				int idNote = Integer.parseInt(arg0.getParameter("idNote"));
				int idUser = Integer.parseInt(arg0.getParameter("idUser"));
				Notes notes = (Notes)service.getObject(Notes.class, idNote);
				notes.setDate(new Date());
				notes.setUsers((Users)service.getObject(Users.class, idUser));
				notes.setNoteText(note_text);
				if(arg0.getParameter("note_title")!=null){
					notes.setNoteTitle(arg0.getParameter("note_title"));
				}
				service.update(notes);
				mav = new ModelAndView("notes/addNoteResult");
				mav.addObject("notes",notes);
			}else if(arg0.getParameter("action").equals("delete_note")){
				int idNote = Integer.parseInt(arg0.getParameter("idNote"));
				Notes notes = (Notes)service.getObject(Notes.class, idNote);
				service.remove(notes);
				mav = new ModelAndView("notes/addNoteResult");
			}else if(arg0.getParameter("action").equals("recordNoteList")){
				mav = new ModelAndView("notes/recordNoteList");	
				String departementFilter = "";
				if(!arg0.getParameter("idDepartment").trim().equals("")){
					String department = arg0.getParameter("idDepartment");
					Departments departments = (Departments)service.getListFromSQL(Departments.class, "SELECT * FROM departments where acronym = '"+department+"';").get(0);
					departementFilter=" or ref_id_department="+departments.getIdDepartment();
				}
				System.out.println("SELECT * FROM notes where (ref_id_user="+arg0.getParameter("idUser")+" "+departementFilter+") AND ref_id_archive="+arg0.getParameter("idArchive")+" AND ref_id_note_type="+NoteType.RECORD_NOTE+" AND ref_id_record="+arg0.getParameter("idRecord")+" order by date;");
				mav.addObject("noteList", service.getListFromSQL(Notes.class,"SELECT * FROM notes where (ref_id_user="+arg0.getParameter("idUser")+" "+departementFilter+") AND ref_id_archive="+arg0.getParameter("idArchive")+" AND ref_id_note_type="+NoteType.RECORD_NOTE+" AND ref_id_record="+arg0.getParameter("idRecord")+" order by date;"));
			}else if(arg0.getParameter("action") != null && arg0.getParameter("action").equals("checkDottrina")){
				String id_archive = arg0.getParameter("id_archive");
				String rivista = arg0.getParameter("rivista");
				String numero = arg0.getParameter("numero");
				String toSearch = "+rivista:"+rivista.trim();
				if(!numero.trim().equals("")){
					toSearch+="~+numero:"+numero.trim();
				}
				System.out.println(">>>>>>>>>>checkDottrina>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>rivista<"+toSearch);
				try {
					String[] multipleQuerys = toSearch.split("~");
					int idArchive = Integer.parseInt(id_archive);
					BooleanQuery booleanQuery = queryBuilder.buildQuery(idArchive, multipleQuerys);
					Document[] hits = searcher.singleSearch(booleanQuery, null, 0, id_archive, false);					
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>hits.length<"+hits.length);
					String note_text = "";
					for (int i = 0; i < hits.length; i++) {
						HashMap<String, String[]> parsedTitle = titleManager.parseTitle(hits[i].get("title_record"), idArchive);
						note_text+="~"+parsedTitle.get("id")[0]+"~";						
					}		
					
					Notes notes = new Notes();
					notes.setDate(new Date());
					notes.setArchives((Archives)service.getObject(Archives.class, idArchive));
					notes.setNoteText(note_text);
					notes.setNoteTitle(rivista+" "+numero);
					if(!arg0.getParameter("idDepartment").trim().equals("")){
						String department = arg0.getParameter("idDepartment");
						notes.setDepartments((Departments)service.getListFromSQL(Departments.class, "SELECT * FROM departments where acronym = '"+department+"';").get(0));
					}
					notes.setNoteTypes((NoteTypes)service.getObject(NoteTypes.class, NoteType.APPLICATION_NOTE));					
					service.add(notes);
					mav = new ModelAndView("notes/checkDottrinaresult");				
				} catch (Exception e) {	
					e.printStackTrace();
					throw e;
				}
				
			}else if(arg0.getParameter("action") != null && arg0.getParameter("action").equals("deCheckDottrina")){
				String id_archive = arg0.getParameter("id_archive");
				String rivista = arg0.getParameter("rivista");
				String numero = arg0.getParameter("numero");
				String toSearch = "+rivista:"+rivista.trim();
				if(!numero.trim().equals("")){
					toSearch+="~+numero:"+numero.trim();
				}
				System.out.println(">>>>>>>>>>decheckDottrina>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>rivista<"+toSearch);;
				try {
					String[] multipleQuerys = toSearch.split("~");
					int idArchive = Integer.parseInt(id_archive);
					BooleanQuery booleanQuery = queryBuilder.buildQuery(idArchive, multipleQuerys);
					Document[] hits = searcher.singleSearch(booleanQuery, null, 0, id_archive, false);
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>hits.length<"+hits.length);					
					HashMap<String, String[]> parsedTitle = titleManager.parseTitle(hits[0].get("title_record"), idArchive);
					String note_text =parsedTitle.get("id")[0];				
					String department = arg0.getParameter("idDepartment");
					Departments departments = (Departments)service.getListFromSQL(Departments.class, "SELECT * FROM departments where acronym = '"+department+"';").get(0);					
					Notes notes = (Notes)service.getListFromSQL(Notes.class, "SELECT * FROM notes where ref_id_department="+departments.getIdDepartment()+" AND ref_id_note_type="+NoteType.APPLICATION_NOTE+" AND note_text LIKE '%"+note_text+"%';").get(0);					
					service.remove(notes);
					mav = new ModelAndView("notes/checkDottrinaresult");				
				} catch (Exception e) {	
					e.printStackTrace();
					throw e;
				}
				
			}else if(arg0.getParameter("action") != null && (arg0.getParameter("action").equals("checkNormativa") || arg0.getParameter("action").equals("checkGiurisprudenza"))){
				String id_archive = arg0.getParameter("id_archive");
				String serie = arg0.getParameter("serie");
				System.out.println(">>>>>>>>>>checkNormativa-checkGiurisprudenza>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>id_archive<"+id_archive);
				System.out.println(">>>>>>>>>>checkNormativa-checkGiurisprudenza>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>serie<"+serie);
				String toSearch = "+serie:"+serie;
				try {
					String[] multipleQuerys = toSearch.split("~");
					int idArchive = Integer.parseInt(id_archive);
					BooleanQuery booleanQuery = queryBuilder.buildQuery(idArchive, multipleQuerys);
					Document[] hits = searcher.singleSearch(booleanQuery, null, 0, id_archive, false);
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>1>>>>>>>>>>>>>>>>>>>>>>"+booleanQuery.toString());
					if(hits.length==0){
						booleanQuery = new BooleanQuery();
						QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "serie", new KeywordAnalyzer());
						parser.setDefaultOperator(Operator.AND);
						booleanQuery.add(parser.parse( "\""+StringUtils.substringAfter(toSearch, ":")+"\""), BooleanClause.Occur.MUST);
						hits = searcher.singleSearch(booleanQuery, null, 0, id_archive, false);						
						System.out.println(">>>>>>>>>>>>>>>>>>>2>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+booleanQuery.toString());
					}
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>hits.length<"+hits.length);
					String note_text = "";
					for (int i = 0; i < hits.length; i++) {
						HashMap<String, String[]> parsedTitle = titleManager.parseTitle(hits[i].get("title_record"), idArchive);
						note_text+="~"+parsedTitle.get("id")[0]+"~";						
					}		
					
					Notes notes = new Notes();
					notes.setDate(new Date());
					notes.setArchives((Archives)service.getObject(Archives.class, idArchive));
					notes.setNoteText(note_text);
					notes.setNoteTitle(serie);
					if(!arg0.getParameter("idDepartment").trim().equals("")){
						String department = arg0.getParameter("idDepartment");
						notes.setDepartments((Departments)service.getListFromSQL(Departments.class, "SELECT * FROM departments where acronym = '"+department+"';").get(0));
					}
					notes.setNoteTypes((NoteTypes)service.getObject(NoteTypes.class, NoteType.APPLICATION_NOTE));					
					service.add(notes);
					mav = new ModelAndView("notes/checkDottrinaresult");				
				} catch (Exception e) {	
					e.printStackTrace();
					throw e;
				}
				
			}else if(arg0.getParameter("action") != null && (arg0.getParameter("action").equals("deCheckNormativa") || arg0.getParameter("action").equals("deCheckGiurisprudenza"))){
				String id_archive = arg0.getParameter("id_archive");
				String serie = arg0.getParameter("serie");
				System.out.println(">>>>>>>>>>deCheckNormativa-deCheckGiurisprudenza>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>id_archive<"+id_archive);
				System.out.println(">>>>>>>>>>deCheckNormativa-deCheckGiurisprudenza>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>serie<"+serie);
				String toSearch = "+serie:"+serie;
				try {
					String[] multipleQuerys = toSearch.split("~");
					int idArchive = Integer.parseInt(id_archive);
					BooleanQuery booleanQuery = queryBuilder.buildQuery(idArchive, multipleQuerys);
					Document[] hits = searcher.singleSearch(booleanQuery, null, 0, id_archive, false);
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>hits.length<"+hits.length);
					if(hits.length==0){
						booleanQuery = new BooleanQuery();
						QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "serie", new KeywordAnalyzer());
						parser.setDefaultOperator(Operator.AND);
						booleanQuery.add(parser.parse( "\""+StringUtils.substringAfter(toSearch, ":")+"\""), BooleanClause.Occur.MUST);
						hits = searcher.singleSearch(booleanQuery, null, 0, id_archive, false);						
						System.out.println(">>>>>>>>>>>>>>>>>>>2>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+booleanQuery.toString());
					}
					HashMap<String, String[]> parsedTitle = titleManager.parseTitle(hits[0].get("title_record"), idArchive);
					String note_text =parsedTitle.get("id")[0];				
					String department = arg0.getParameter("idDepartment");
					Departments departments = (Departments)service.getListFromSQL(Departments.class, "SELECT * FROM departments where acronym = '"+department+"';").get(0);					
					Notes notes = (Notes)service.getListFromSQL(Notes.class, "SELECT * FROM notes where ref_id_department="+departments.getIdDepartment()+" AND ref_id_note_type="+NoteType.APPLICATION_NOTE+" AND note_text LIKE '%"+note_text+"%';").get(0);					
					service.remove(notes);
					mav = new ModelAndView("notes/checkDottrinaresult");				
				} catch (Exception e) {	
					e.printStackTrace();
					throw e;
				}
				
			}
		}
		return mav;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
	public void setQueryBuilder(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}
	public void setTitleManager(TitleManager titleManager) {
		this.titleManager = titleManager;
	}
	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}
}
