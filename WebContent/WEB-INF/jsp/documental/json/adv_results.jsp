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
		HashMap<String, String> idToCheck = null;
		if (request.getAttribute("idToCheck") != null) {
			idToCheck = (HashMap<String, String>) request.getAttribute("idToCheck");
		}
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
					//HashMap<String, String[]> parsedTitle = titleManager.parseExtendedTitle(docsearch.get("title_record"),new Integer(request.getParameter("id_archive")),new Integer(docsearch.get("id_record")));
					String description = "";
					String title = "";
					String date = "";
					String dateNormal = "";
					String function = "";
					String classString = "";
					boolean mioDip = false;
					if (mode.equals("")  || mode.equals("schedoniGest")  || mode.equals("schedoni") || mode.equals("dottrina") || mode.equals("normativa") || mode.equals("normativa") || mode.equals("giurisprudenza")) {

						String[] titleArray = parsedTitle.get("notation");
						String[] dipartimentoArray = parsedTitle.get("dipartimento");

						if (dipartimentoArray != null && dipartimento != null) {
							for (String singleTitle : dipartimentoArray) {
								try {
									if (singleTitle.equals(dipartimento)) {
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
									titleBuffer.append(singleTitle.replaceAll("ç", "") + "");
								} catch (Exception e) {
								}
							}
						}
						titleArray = parsedTitle.get("title");
						if (titleArray != null) {
							for (String singleTitle : titleArray) {
								try {
									titleBuffer.append(singleTitle.replaceAll("ç", ""));
								} catch (Exception e) {
								}
							}
						}
						titleArray = parsedTitle.get("sottotitolo");
						if (titleArray != null) {
							for (String singleTitle : titleArray) {
								try {
									titleBuffer.append(singleTitle.replaceAll("ç", ""));
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
						classString = " linkToHiglight linkToHiglight_" + docsearch.get("id_record");
						//						function = "return openRecord('" + docsearch.get("id_record") + "');";
						try {
							date = parsedTitle.get("data")[0].trim();
							dateNormal = date;
							Date data = formatter.parse(date);
							date = formatterDests.format(data);
							 
						} catch (Exception e) {
						}
						title = title.replaceAll("<(/)?a[^>]*>", "");
						title = "<a href=\"#no\" class=\"link_document" + classString + "\" onclick=\"" + function + "\">" + title + "</a>";
					} else if (mode.startsWith("multisearch")) {

						String[] titleArray = parsedTitle.get("notation");
						String[] dipartimentoArray = parsedTitle.get("dipartimento");

						if (dipartimentoArray != null && dipartimento != null) {
							for (String singleTitle : dipartimentoArray) {
								try {
									if (singleTitle.equals(dipartimento)) {
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
									titleBuffer.append(singleTitle.replaceAll("ç", ""));
								} catch (Exception e) {
								}
							}
						}
						titleArray = parsedTitle.get("title");
						if (titleArray != null) {
							for (String singleTitle : titleArray) {
								try {
									titleBuffer.append(singleTitle.replaceAll("ç", ""));
								} catch (Exception e) {
								}
							}
						}
						titleArray = parsedTitle.get("sottotitolo");
						if (titleArray != null) {
							for (String singleTitle : titleArray) {
								try {
									titleBuffer.append(singleTitle.replaceAll("ç", ""));
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
						//function = "openRecord('" + docsearch.get("id_record") + "', null,'" + docsearch.get("id_archive") + "','" + JsSolver.escapeSingleApex(docsearch.get("archive_label")) + "');";

						//						function = "return openRecord('" + docsearch.get("id_record") + "');";
						try {
							date = parsedTitle.get("data")[0].trim();
							dateNormal = date;
							Date data = formatter.parse(date);
							date = formatterDests.format(data);
						} catch (Exception e) {
						}

						//	title = "<a href=\"#no\" class=\"link_document\" onclick=\"" + function + "\">" + title + "</a>";

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
						title = (parsedTitle.get("notation")[0] + " " + titleBuffer.toString());
						if (title.trim().equals("")) {
							title = "[senza titolo]";
						}
						String[] altLabelArray = parsedTitle.get("altLabel");
						StringBuffer altLabelBuffer = new StringBuffer();
						String altLabel = "";
						for (String singleTitle : altLabelArray) {
							try {
								altLabelBuffer.append(singleTitle.replaceAll("ç", ""));
							} catch (Exception e) {
							}
						}
						altLabel = " (<span style=\"font-style: italic;\">" + altLabelBuffer.toString() + "</span>)";
						if (!altLabelBuffer.toString().trim().equals("")) {
							title += altLabel;
						}
						try {
							String[] descriptionArray = parsedTitle.get("description");
							StringBuffer descriptionBuffer = new StringBuffer();
							String description2 = "";
							for (String singleTitle : descriptionArray) {
								try {
									descriptionBuffer.append(singleTitle.replaceAll("ç", ""));
								} catch (Exception e) {
								}
							}
							description2 = "<br />" + descriptionBuffer.toString();
							if (!descriptionBuffer.toString().trim().equals("")) {
								title += description2;
							}
						} catch (Exception e) {
						}
						try {
							String[] noteArray = parsedTitle.get("note");
							StringBuffer noteBuffer = new StringBuffer();
							String note = "";
							for (String singleTitle : noteArray) {
								try {
									noteBuffer.append(singleTitle.replaceAll("ç", ""));
								} catch (Exception e) {
								}
							}
							note = " " + noteBuffer.toString();
							if (!note.trim().equals("")) {
								description += note;
							}
						} catch (Exception e) {
						}
						function = "addRelationFromTree('" + docsearch.get("id_record") + "','','" + JsSolver.escapeSingleApex(title) + "','" + docsearch.get("id_archive") + "')";
						if (mode.equals("skosAddRelation")) {
							if (parsedTitle.get("deprecated") != null && !parsedTitle.get("deprecated")[0].trim().equals("")) {
								function = "messageDeprecated('" + parsedTitle.get("deprecated")[0].trim() + "','" + docsearch.get("id_archive") + "');";
								title = "<a href=\"#no\" class=\"link_document\" style=\"color:#808080;\" title=\"voce deprecata\" onclick=\"" + function + "\">" + title + "</a>";
							} else {
								title = "<a href=\"#no\" class=\"link_document\" onclick=\"" + function + "\">" + title + "</a>";
							}
						} else if (mode.equals("skosAddFilterToSearch")) {
							String titleToAdd = title.replaceAll("<br />", " ");
							if (titleToAdd.length() > 50) {
								titleToAdd = titleToAdd.substring(0, 50) + "...";
							}
							function = "addFilterToSearch('" + docsearch.get("id_record") + "',' " + JsSolver.escapeSingleApex(titleToAdd) + "  [" + docsearch.get("archive_label") + "]');";
							if (parsedTitle.get("deprecated") != null && !parsedTitle.get("deprecated")[0].trim().equals("")) {
								title = "<a href=\"#no\" class=\"link_document\" style=\"color:#808080;\" title=\"voce deprecata\" onclick=\"" + function + "\">" + title + "</a>";
							} else {
								title = "<a href=\"#no\" class=\"link_document\" onclick=\"" + function + "\">" + title + "</a>";
							}
						} else if (mode.equals("skosSimpleShow")) {

						}

					} else {

					}

					if (mode.equals("dottrina") || mode.equals("normativa") || mode.equals("giurisprudenza")) {
						String checkFunction = "";
						String param1 = "";
						String param2 = "";
						if (mode.equals("dottrina")) {
							param1 = docsearch.get("rivista");
							param2 = docsearch.get("numero");
							checkFunction = "markMagazine";
						} else if (mode.equals("normativa")) {
							param1 = date;
							checkFunction = "markNormativa";
						} else if (mode.equals("giurisprudenza")) {
							param1 = date;
							checkFunction = "markGiurisprudenza";
						}

						if (idToCheck != null) {
							if (idToCheck.get(parsedTitle.get("id")[0]) != null) {
								date = date + "<a style=\"float:right;margin-left:2px;\" onclick=\"return " + checkFunction + "(this,'" + JsSolver.escapeSingleApex(param1) + "','" + JsSolver.escapeSingleApex(param2) + "');\" href=\"#no\"><img title=\"Segna come terminata\" src=\"img/archive_img/checkbox_on.gif\"></a>";
							} else {
								date = date + "<a style=\"float:right;margin-left:2px;\" onclick=\"return " + checkFunction + "(this,'" + JsSolver.escapeSingleApex(param1) + "','" + JsSolver.escapeSingleApex(param2) + "');\" href=\"#no\"><img title=\"Segna come terminata\" src=\"img/archive_img/checkbox_off.gif\"></a>";
							}
						} else {
							date = date + "<a style=\"float:right;margin-left:2px;\" onclick=\"return " + checkFunction + "(this,'" + JsSolver.escapeSingleApex(param1) + "','" + JsSolver.escapeSingleApex(param2) + "');\" href=\"#no\"><img title=\"Segna come terminata\" src=\"img/archive_img/checkbox_off.gif\"></a>";
						}
						date = StringsUtils.escapeJson(date);
					} else {
						date = date.replaceAll(" ", "&nbsp;");
						date = "\"" + date + "\"";
					}

					//description = JsSolver.escapeDoubleApex(description);
					//title = JsSolver.escapeDoubleApex(title);

					//title = "<a href=\"#no\" class=\"link_document\" onclick=\"" + function + "\">" + title + "</a>";
					function = StringsUtils.escapeJson(function);
					title = title.replaceAll("&amp;apos;", "'");
					title = title.replaceAll("&apos;", "'");
					
					
					/*String test = titleManager.getFieldValues(parsedTitle.get("id2"));
					test+= " "+titleManager.getFieldValues(parsedTitle.get("mimetype"));
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+test);
					title+= " "+test;*/
					
					String preview = "\"<a class='previewTool' title='anteprima del documento' href='#s' onclick='return previewDoc(" + docsearch.get("id_record") + ",true," + docsearch.get("id_archive") + ",\\\"" + JsSolver.escapeSingleDoubleApex(docsearch.get("archive_label")) + "\\\")'></a>\"";
					String myDip = StringsUtils.escapeJson((mioDip ? "<div title='del mio dipartimento' class=\"myDip\"></div>" : ""));
					if (mode.equals("skosAddRelation")) {
						myDip = StringsUtils.escapeJson("<a onclick=\"fathersFinder(" + docsearch.get("id_record") + "," + docsearch.get("id_archive") + ");\" href=\"#no\"><img title=\"Visualizza nell'albero\" src=\"img/skos_tab/site_tree.gif\"></a>  <a onclick=\"return doSimpleSearch('griglia:" + parsedTitle.get("id")[0].trim() + "');\" href=\"#no\"><img title=\"Consulta\" src=\"img/skos_tab/prev_doc_related2.gif\"></a>");
					}

					if (mode == null || mode.equals("") || mode.equals("schedoniGest")) {

						description = description.replaceAll("<a[ ]*[^>]*>", "");
						description = description.replaceAll("</a>", "");
						description = description.replaceAll("<span[ ]*[^>]*>", "");
						description = description.replaceAll("</span>", "");

						description = description.replaceAll("<ul[ ]*[^>]*>", "");
						description = description.replaceAll("</ul>", "");

						description = description.replaceAll("<ol[ ]*[^>]*>", "");
						description = description.replaceAll("</ol>", "");
						description = description.replaceAll("<li[ ]*[^>]*>", "- ");
						description = description.replaceAll("</li>", "");

						description = description.replaceAll("<strong[ ]*[^>]*>", "");
						description = description.replaceAll("</strong>", "");

						description = description.replaceAll("<em[ ]*[^>]*>", "");
						description = description.replaceAll("</em>", "");

						description = description.replaceAll("<table[ ]*[^>]*>", "");
						description = description.replaceAll("</table>", "");

						description = description.replaceAll("<tr[ ]*[^>]*>", "-");
						description = description.replaceAll("</tr>", "");

						description = description.replaceAll("<td[ ]*[^>]*>", "|");
						description = description.replaceAll("</td>", "");

						description = description.replaceAll("<th[ ]*[^>]*>", "|");
						description = description.replaceAll("</th>", "");

						description = description.replaceAll("<tbody[ ]*[^>]*>", "");
						description = description.replaceAll("</tbody>", "");

						description = description.replaceAll("<p[ ]*[^>]*>", "");
						description = description.replaceAll("</p>", "");
 
						title = title.replaceAll("<span[ ]*[^>]*>", "");
						title = title.replaceAll("</span>", "");

						title = title.replaceAll("<ul[ ]*[^>]*>", "");
						title = title.replaceAll("</ul>", "");

						title = title.replaceAll("<p[ ]*[^>]*>", "");
						title = title.replaceAll("</p>", "");

						title = title.replaceAll("<ol[ ]*[^>]*>", "");
						title = title.replaceAll("</ol>", "");
						title = title.replaceAll("<li[ ]*[^>]*>", "- ");
						title = title.replaceAll("</li>", "");

						title = title.replaceAll("<strong[ ]*[^>]*>", "");
						title = title.replaceAll("</strong>", "");

						title = title.replaceAll("<em[ ]*[^>]*>", "");
						title = title.replaceAll("</em>", "");

						title = title.replaceAll("<table[ ]*[^>]*>", "");
						title = title.replaceAll("</table>", "");

						title = title.replaceAll("<tbody[ ]*[^>]*>", "");
						title = title.replaceAll("</tbody>", "");

						title = title.replaceAll("<tr[ ]*[^>]*>", "-");
						title = title.replaceAll("</tr>", "");

						title = title.replaceAll("<td[ ]*[^>]*>", "|");
						title = title.replaceAll("</td>", "");

						title = title.replaceAll("<th[ ]*[^>]*>", "|");
						title = title.replaceAll("</th>", "");

					}

					title = StringsUtils.escapeJson(title);
					description = StringsUtils.escapeJson(description.replaceAll("\t", " "));
%>
<%--    {"id": ,"description": "<%=description%>","title": "<%=title%>"<%if(request.getParameter("reopen_accordion")!=null && !request.getParameter("reopen_accordion").trim().equals("")){%>,"reopen_accordion": "<%=request.getParameter("reopen_accordion")%>"<%}%>}<%if(i!=hits.length-1 && count!=limit-1){%>,<%}%>        
	 --%>
	 
<%
if (i > 0) {
%>,<%
}
%>	 
{"id":"<%=docsearch.get("id_record")%>","title":<%=title%>,"date":<%=date%>,"description":<%=description%>,"datenormal":"<%=dateNormal%>","afunction":<%=function%>,"preview":<%=preview%>,"myDip":<%=myDip%>}<%

%>
<%
	count++;
				} catch (Exception a) {
					a.printStackTrace();
					if (i > 0) {
						%>,<%
						}
%><%--			    {"id": <%=docsearch.get("id_record")%>,"title": "<%=docsearch.get("title_record")%>"<%if(request.getParameter("reopen_accordion")!=null && !request.getParameter("reopen_accordion").trim().equals("")){%>,"reopen_accordion": "<%=request.getParameter("reopen_accordion")%>"<%}%>}<%if(i!=hits.length-1 && count!=limit-1){%>,<%}%>        
	 --%>
{"id":"<%=docsearch.get("id_record")%>","title":"<%=docsearch.get("title_record")%>","date":"","description":"","datenormal":""}<%
 
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
