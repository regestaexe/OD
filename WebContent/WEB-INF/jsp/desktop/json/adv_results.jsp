<%@page import="com.openDams.utility.StringsUtils"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat formatterDests = new SimpleDateFormat("d MMM. yyyy", Locale.ITALY);
	response.setContentType("application/json");
%><%@page import="org.apache.commons.lang.StringEscapeUtils"%><%@page import="org.apache.commons.lang.StringUtils"%><%@page import="com.regesta.framework.util.StringUtility"%><%@page contentType="text/html; charset=UTF-8"%><%@page pageEncoding="UTF-8"%><%@page import="com.openDams.bean.Records"%><%@page import="java.util.List"%><%@page import="org.apache.lucene.document.Document"%><%@page import="com.openDams.title.configuration.TitleManager"%><%@page import="java.util.HashMap"%>
<%
	String dipartimento = request.getParameter("department");
	String mode = request.getParameter("outputMode");
	if (mode == null) {
		mode = "";
	}
	if (request.getAttribute("results") != null) {
		Document[] hits = (Document[]) request.getAttribute("results");
		TitleManager titleManager = (TitleManager) request.getAttribute("titleManager");
		int limit = (Integer) request.getAttribute("limit");
%>{ "success": true, "totalCount":<%=request.getAttribute("totResults") != null ? request.getAttribute("totResults") : hits.length%>, "data": [<%
	int count = 0;
		for (int i = 0; i < hits.length; i++) {
			if (hits[i] != null) {
				Document docsearch = hits[i];
				try {
					HashMap<String, String[]> parsedTitle = titleManager.parseTitle(docsearch.get("title_record"), new Integer(request.getParameter("id_archive")));
					String description = "";
					String title = "";
					String date = "";
					String dateNormal = "";
					String function = "";
					boolean mioDip = false;
					if (mode.equals("") || mode.equals("schedoni")) {

						String[] titleArray = parsedTitle.get("notation");
						String[] dipartimentoArray = parsedTitle.get("dipartimento");

						if (dipartimentoArray != null && dipartimento!=null) {
							for (String singleTitle : dipartimentoArray) {
								try {
									 if(singleTitle.equals(dipartimento)){
										 mioDip = true;
										 break;
									 }
								} catch (Exception e) {
								}
							}
						}
						
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
						String[] descriptionArray = parsedTitle.get("descrizione");
						StringBuffer descriptionBuffer = new StringBuffer();
						if (descriptionArray != null) {
							for (String singleTitle : descriptionArray) {
								try {
									descriptionBuffer.append(singleTitle.replaceAll("ç", ""));
								} catch (Exception e) {
								}
							}
						}
						description = (descriptionBuffer.toString());

						//description = description.replaceAll("&gt;", ">");
						//description = description.replaceAll("&lt;", "<");
						title = (titleBuffer.toString());

						if (title.trim().equals("")) {
							title = "[senza titolo]";
						}
						function = "openRecord('" + docsearch.get("id_record") + "', null,'" + docsearch.get("id_archive") + "','" + JsSolver.escapeSingleApex(docsearch.get("archive_label")) + "');";

						//						function = "return openRecord('" + docsearch.get("id_record") + "');";
						try {
							date = parsedTitle.get("data")[0].trim();
							dateNormal = date;
							Date data = formatter.parse(date);
							date = formatterDests.format(data);
						} catch (Exception e) {
						}

						title = "<a href=\"#no\" class=\"link_document\" onclick=\"" + function + "\">" + title + "</a>";

					} else if (mode.startsWith("skos")) {

						String[] titleArray = parsedTitle.get("label");
						StringBuffer titleBuffer = new StringBuffer();
						for (String singleTitle : titleArray) {
							try {
								titleBuffer.append(singleTitle.replaceAll("ç", ""));
							} catch (Exception e) {
							}
						}
						try {
							date = parsedTitle.get("data")[0];
							dateNormal = date;
							Date data = formatter.parse(date);
							date = formatterDests.format(data);
						} catch (Exception e) {
						}
						String[] descriptionArray = parsedTitle.get("altLabel");
						StringBuffer descriptionBuffer = new StringBuffer();
						for (String singleTitle : descriptionArray) {
							try {
								descriptionBuffer.append(singleTitle.replaceAll("ç", ""));
							} catch (Exception e) {
							}
						}
						description = descriptionBuffer.toString();
						title = (parsedTitle.get("notation")[0] + " " + titleBuffer.toString());
						if (title.trim().equals("")) {
							title = "[senza titolo]";
						}
						function = "addRelationFromTree('" + docsearch.get("id_record") + "','12','" + title + "')";
						if (mode.equals("skosAddRelation")) {
							title = "<a href=\"#no\" class=\"link_document\" onclick=\"" + function + "\">" + title + "</a>";
						} else if (mode.equals("skosSimpleShow")) {

						}

					} else {

					}

					date = date.replaceAll(" ", "&nbsp;");

					//description = JsSolver.escapeDoubleApex(description);
					//title = JsSolver.escapeDoubleApex(title);

					//title = "<a href=\"#no\" class=\"link_document\" onclick=\"" + function + "\">" + title + "</a>";
					function = StringsUtils.escapeJson(function);
					title = title.replaceAll("&amp;apos;", "'");
					title = title.replaceAll("&apos;", "'");

					//title = (mioDip ? "<div class=\"myDip\">"+title+"</div>" : title) ;
					String preview = "\"<a class='previewTool' title='anteprima del documento' href='#s' onclick='return previewDoc(" + docsearch.get("id_record") + "," + docsearch.get("id_archive") + ")'></a>\"";
					String myDip = StringsUtils.escapeJson((mioDip ? "<div title='del mio dipartimento' class=\"myDip\"></div>" : ""));
					
					description ="";
					
					title = StringsUtils.escapeJson(title);
					description = StringsUtils.escapeJson(description);
%>
<%--    {"id": ,"description": "<%=description%>","title": "<%=title%>"<%if(request.getParameter("reopen_accordion")!=null && !request.getParameter("reopen_accordion").trim().equals("")){%>,"reopen_accordion": "<%=request.getParameter("reopen_accordion")%>"<%}%>}<%if(i!=hits.length-1 && count!=limit-1){%>,<%}%>        
	 --%>
{"id":"<%=docsearch.get("id_record")%>","title":<%=title%>,"date":"<%=date%>","description":<%=description%>,"datenormal":"<%=dateNormal%>","afunction":<%=function%>,"preview":<%=preview %>,"myDip":<%=myDip %>}<%
	if (i != hits.length - 1 && count != limit - 1) {
%>,<%
	}
%>
<%
	count++;
				} catch (Exception a) {
					a.printStackTrace();
%><%--			    {"id": <%=docsearch.get("id_record")%>,"title": "<%=docsearch.get("title_record")%>"<%if(request.getParameter("reopen_accordion")!=null && !request.getParameter("reopen_accordion").trim().equals("")){%>,"reopen_accordion": "<%=request.getParameter("reopen_accordion")%>"<%}%>}<%if(i!=hits.length-1 && count!=limit-1){%>,<%}%>        
	 --%>
{"id":"<%=docsearch.get("id_record")%>","title":"<%=docsearch.get("title_record")%>","date":"","description":"","datenormal":""}<%
	if (i != hits.length - 1 && count != limit - 1) {
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
