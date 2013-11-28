<%@page import="com.openDams.utility.StringsUtils"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.regesta.framework.util.StringUtility"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.lucene.document.Document"%>
<%@page import="com.openDams.title.configuration.TitleManager"%>
<%@page import="java.util.HashMap"%>
<%
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat formatterDests = new SimpleDateFormat("d MMM. yyyy", Locale.ITALY);
	response.setContentType("application/json");
%>
<%
	String dipartimento = request.getParameter("department");
	String mode = request.getParameter("outputMode");
	if (mode == null) {
		mode = "";
	}
	if (request.getAttribute("results") != null) {
		HashMap<String,String> idToCheck = null;
		if(request.getAttribute("idToCheck")!=null){
			idToCheck = (HashMap<String,String>)request.getAttribute("idToCheck");
		}
		Document[] hits = (Document[]) request.getAttribute("results");
		TitleManager titleManager = (TitleManager) request.getAttribute("titleManager");
		int page_size = (Integer) request.getAttribute("page_size");
%>{ "success": true, "totalCount":<%=request.getAttribute("totResults") != null ? request.getAttribute("totResults") : hits.length%>, "data": [<%
	int count = 0;
		for (int i = 0; i < hits.length; i++) {
			if (hits[i] != null) {
				Document docsearch = hits[i];
				try {
					HashMap<String, String[]> parsedTitle = titleManager.parseTitle(docsearch.get("title_record"), new Integer(request.getParameter("idArchive")));
					String description = "";
					String title = "";
					

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
					titleArray = parsedTitle.get("sottotitolo");
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
					
					String keyId = "";
					try{
						keyId = parsedTitle.get(request.getParameter("keyId"))[0];
					}catch(Exception e){
						keyId = "";
					}					
					String function = "setLookUpValue('"+request.getParameter("idRef")+"','"+JsSolver.escapeSingleApex(title)+"','" + keyId + "');";
					title = "<a href=\"#no\" class=\"link_document\" onclick=\"" + function + "\">" + title + "</a>";
					title = StringsUtils.escapeJson(title);
					title = title.replaceAll("&amp;apos;", "'");
					title = title.replaceAll("&apos;", "'");
					String preview = "\"<a class='previewTool' title='anteprima del documento' href='#s' onclick='return previewDoc(" + docsearch.get("id_record") + "," + docsearch.get("id_archive") + ")'></a>\"";
					
%>
{"id":"<%=docsearch.get("id_record")%>","title":<%=title%>,"preview":<%=preview %>}<%
	if (i != hits.length - 1 && count != page_size - 1) {
%>,<%
	}
%>
<%
	count++;
				} catch (Exception a) {
					a.printStackTrace();
%>
{"id":"<%=docsearch.get("id_record")%>","title":"<%=docsearch.get("title_record")%>","date":"","description":"","datenormal":""}<%
	if (i != hits.length - 1 && count != page_size - 1) {
%>,<%
	}
%>
<%
	}
			}
		}
%>
] }
<%
	} else {
%>
{ "success": false }
<%
	}
%>
