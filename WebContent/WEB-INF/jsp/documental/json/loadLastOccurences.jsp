<%@page import="com.openDams.bean.ArchiveType"%>
<%@page import="com.openDams.bean.ArchiveTypes"%>
<%@page import="com.openDams.utility.StringsUtils"%>
<%@page import="net.sf.json.util.JSONStringer"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%><%@page import="java.util.Locale"%><%@page import="com.openDams.title.configuration.TitleManager"%><%@page import="java.util.HashMap"%><%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.regesta.framework.util.JsSolver"%><%@page import="com.openDams.bean.Records"%><%@page import="java.util.List"%><%@page import="com.openDams.bean.Archives"%>
<%
	String dipartimento = request.getParameter("department");
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat formatterDests = new SimpleDateFormat("d MMM. yyyy", Locale.ITALY);
	response.setContentType("application/json");
%>{"data":[
<%
	boolean render = false;
	if (request.getParameter("renderTo") == null) {
		render = true;
	}

	TitleManager titleManager = (TitleManager) request.getAttribute("titleManager");
	Archives archives = (Archives) request.getAttribute("archives");
	int idArchive = 0;
	int archiveType = 0;
	boolean noArchive = true;
	String label = "";
	if (archives != null) {
		noArchive = false;
		idArchive = archives.getIdArchive();
		archiveType = archives.getArchiveTypes().getIdArchiveType();
		label = StringEscapeUtils.escapeJavaScript(archives.getLabel());
	}

	List<Records> recordsList = (List<Records>) request.getAttribute("recordsList");
	if (recordsList != null && recordsList.size() > 0) {
		for (int i = 0; i < recordsList.size(); i++) {
			Records records = recordsList.get(i);
			if (noArchive) {
				Archives a = records.getArchives();
				idArchive = a.getIdArchive();
				label = StringEscapeUtils.escapeJavaScript(records.getArchives().getLabel());
				archiveType = a.getArchiveTypes().getIdArchiveType();
			}
			//System.out.println("   " +records.getTitle());

			HashMap<String, String[]> parsedTitle = titleManager.parseTitle(records.getTitle(), idArchive);
			String description = "";
			String title = "";
			String date = "";
			String dateNormal = "";
			String function = "";
			String[] dipartimentoArray = parsedTitle.get("dipartimento");

			boolean mioDip = false;
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

			String[] descriptionArray = parsedTitle.get("descrizione");
			StringBuffer descriptionBuffer = new StringBuffer();
			if (descriptionArray != null) {
				for (String singleTitle : descriptionArray) {
					try {
						descriptionBuffer.append(singleTitle.replaceAll("ç", " "));
					} catch (Exception e) {
					}
				}
			}
			try {
				date = parsedTitle.get("data")[0].trim();
				dateNormal = date;
				Date data = formatter.parse(date);
				date = formatterDests.format(data);
			} catch (Exception ess) {
				//date = "data errata";
				//dateNormal = date;
			}

			description = descriptionBuffer.toString();
			if(archiveType == ArchiveType.TWITTER){
				function = "return openRecord('" + records.getIdRecord() + "','"+idArchive+"',false,'" + JsSolver.escapeSingleApex(label) + "');";	
			}else{
				function = "return openRecord('" + records.getIdRecord() + "',true,'" + idArchive + "','" + JsSolver.escapeSingleApex(label) + "');";
			}
			
			title = titleBuffer.toString();
			if (title.trim().equals("")) {
				title = "[senza titolo]";
			}
			title = title.replaceAll("<(/)?a[^>]*>","");
			title = "<a href=\"#no\" class=\"link_document\" onclick=\"" + function + "\">" + title + "</a>";
			title = title.replaceAll("&amp;apos;", "'");
			title = title.replaceAll("&apos;", "'");
			//			title = (mioDip ? "<div class=\"myDip\">" + title + "</div>" : title);
			title = StringsUtils.escapeJson(title);
			description = "";
			description = StringsUtils.escapeJson(description);

			//date = date.replaceAll(" ", "&nbsp;");

			HashMap<String, String> singleResult = new HashMap<String, String>();

			singleResult.put("id", "\"" + records.getIdRecord() + "\"");
			singleResult.put("title", title);
			singleResult.put("date", "\"" + date + "\"");
			singleResult.put("description", description);
			singleResult.put("datenormal", "\"" + dateNormal + "\"");
			singleResult.put("preview", "\"<a class='previewTool' title='anteprima del documento' href='#s' onclick='return previewDoc(" + records.getIdRecord() + ",true," + idArchive + ",'" + JsSolver.escapeSingleApex(label) + "')'></a>\"");
			singleResult.put("myDip", StringsUtils.escapeJson((mioDip ? "<div class=\"myDip\" title='del mio dipartimento'></div>" : "")));
			singleResult.put("archiveLabel", JsSolver.escapeSingleApex(label));

			String[] resultFormat = request.getParameterValues("resultFormat[]");

			if (resultFormat != null && resultFormat.length > 0) {
				
				if (i > 0) {
					%>,
					<%
						}
				
%>{<%
	for (int a = 0; a < resultFormat.length; a++) {

					if (singleResult.get(resultFormat[a]) != null) {
%>"<%=resultFormat[a]%>":<%=singleResult.get(resultFormat[a])%>
<%
	} else {
						String[] aa = parsedTitle.get(resultFormat[a]);
						StringBuffer genericBuffer = new StringBuffer();
						if (aa != null && aa.length > 0) {
							for (String singleTitle : aa) {
								try {
									genericBuffer.append(singleTitle.replaceAll("ç", " "));
								} catch (Exception e) {
								}
							}
						}
%>"<%=resultFormat[a]%>":<%=StringsUtils.escapeJson(genericBuffer.toString())%>
<%
	}
					if (a < resultFormat.length - 1) {
%>,<%
	}
				}
%>,"id":<%=singleResult.get("id")%>,"archiveLabel":"<%=JsSolver.escapeSingleApex(label)%>"<%
	
%>}<%
	} else { /*json di default*/
%>{"id":<%=singleResult.get("id")%>,"title":<%=singleResult.get("title")%>,"date":<%=singleResult.get("date")%>,"description":<%=singleResult.get("description")%>,"datenormal":<%=singleResult.get("datenormal")%>,"preview":<%=singleResult.get("preview")%>,"myDip":<%=singleResult.get("myDip")%>,"archiveLabel":"<%=JsSolver.escapeSingleApex(label)%>"}<%
	}


			records.closeXMLReader();
		}
	}
%>],"totalCount":<%=request.getAttribute("totalCount")%>}
