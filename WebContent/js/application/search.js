function lastVocKey(campo, sort, limit, query) {
	var aDest = lastDest;
	callAjax({
		success : function(jsonData) {
			try {
				jsonData = $.parseJSON(jsonData);
				if (jsonData[0][campo]) {
					aDest.html(("<span style='font-style:italic'><span class='ui-icon ui-icon-info' style='float:left'></span> prossimo numero: " + 
							(parseInt(jsonData[0][campo].replace(/[^0-9].*$/,''),10)+1) + "</span>"));
				} else {
					aDest.html("<span style='font-style:italic'><span class='ui-icon ui-icon-info' style='float:left'></span> prossimo numero: 1</span>");
				}

			} catch (e) {
				aDest.html("<span style='font-style:italic'><span class='ui-icon ui-icon-info' style='float:left'></span> prossimo numero: errore</span>");
			}
		},
		loadingTarget : aDest.children('span'),
		url : 'ajax/documental_search_utils.html',
		type : 'POST',
		data : {
			'resultFormat' : [ campo ],
			'sort' : sort,
			'limit' : limit,
			'query' : query,
			'id_archive' : globalOpt.document.archive
		}
	});
}

function getVocArray(idArchivio, limit, field, sort, skipTo, filter) {
	var elenco = [];
	if (!skipTo)
		skipTo = "";
	if (!filter)
		filter = "";

	if (!top.utilCache[idArchivio + 'limit' + limit + 'field' + field + 'sort' + sort + 'skipTo' + encodeURI(skipTo) + 'filter' + encodeURI(filter)]) {
		$.ajax({
			async : false,
			url : 'ajax/documental_search_utils.html?start=0&id_archive=' + idArchivio + '&action=vocList&limit=' + limit + '&field=' + field + '&sort=' + sort + '&skipTo=' + encodeURI(skipTo) + '&filter=' + encodeURI(filter),
			dataType : 'json',
			success : function(data) {
				elenco = data;
				top.utilCache[idArchivio + 'limit' + limit + 'field' + field + 'sort' + sort + 'skipTo' + encodeURI(skipTo) + 'filter' + encodeURI(filter)] = data;
			}
		});
	} else {
		elenco = top.utilCache[idArchivio + 'limit' + limit + 'field' + field + 'sort' + sort + 'skipTo' + encodeURI(skipTo) + 'filter' + encodeURI(filter)];
	}
	return elenco;
}
function doAjaxSearch(query, anIdArchivio, aMode) {

	var idArchivio = anIdArchivio ? anIdArchivio : '1';
	var mode = aMode ? aMode : 'skosSimpleShow';

	$.ajax({
		async : false,
		url : 'ajax/documental_adv_search.html?start=0&outputMode=' + mode + '&id_archive=' + idArchivio + '&action=search&limit=1&query=' + query + '&department=' + top.Application['user'].dipartimento,
		success : function(data) {
			if (data.data[0])
				query = data.data[0].title;
		},
		error : function() {
			query += " - errore - codice non trovato";
		}
	});
	return query;
}
function doKeywordsAjaxSearch(query, anIdArchivio, aMode, notShowDescription) {

	var idArchivio = anIdArchivio ? anIdArchivio : '1';
	var mode = aMode ? aMode : 'skosSimpleShow';

	$.ajax({
		async : false,
		url : 'ajax/documental_adv_search.html?start=0&outputMode=' + mode + '&id_archive=' + idArchivio + '&action=search&limit=1&keywords=' + query + '&department=' + top.Application['user'].dipartimento,
		dataType : 'text',
		success : function(adata) {
			adata = adata.replace(/\t/g, ' ');
			var data = $.parseJSON(adata);
			if (data.data[0]) {
				query = data.data[0].title;
				id = data.data[0].id;
				preview = data.data[0].preview;
				if (notShowDescription && query.indexOf("<br") != -1) {
					query = query.substring(0, query.indexOf("<br"));
				} else {
					if (query.indexOf("<br") > -1) {
						// query = query.substring(0, query.indexOf("<br"));
						query = query.replace(/<br[^>]*>/, '<div class="littleDescription">') + "</div>";
					}
				}
				query = '<input name="idRecord" type="hidden" value="' + id + '"/>' + query;
				if (preview) {
					preview = preview.replace(/.*onclick=('|")return ([^>]*)('|").*/gi, "$2");
					preview = preview.replace(/"/g, "'");
					query = '<input name="preview" type="hidden" value="' + preview + '"/>' + query;
				}
			}
		},
		error : function() {
			query += " - errore - codice non trovato";
		}
	});
	return query;
}

function doKeywordsAjaxSearchWithDescription(query, anIdArchivio, aMode, notShowDescription) {
	var idArchivio = anIdArchivio ? anIdArchivio : '1';
	var mode = aMode ? aMode : 'skosSimpleShow';
	var obj = this;
	$.ajax({
		async : false,
		url : 'ajax/documental_adv_search.html?start=0&outputMode=' + mode + '&id_archive=' + idArchivio + '&action=search&limit=1&keywords=' + query + '&department=' + top.Application['user'].dipartimento,
		dataType : 'text',
		success : function(adata) {
			adata = adata.replace(/\t/g, ' ');
			var data = $.parseJSON(adata);
			if (data.data[0]) {
				if (obj.parent().find("*[class^='value-']").length > 0) {
					obj.parent().find("*[class^='value-']").each(function() {
						var className = $(this).attr("class");
						var mapName = className.substring(className.indexOf("-") + 1);
						var valore = $(data.data[0][mapName]);
						if (mapName == 'title') {
							valore = valore.html();
							valore = valore.replace(/<br \/>/g, '\n');
							valore = $("<p>" + valore + "</p>").text();
							valore = valore.replace(/\n/g, '<br \/>');
						}
						$(this).append(valore);
					});
					query = "";
				} else {
					valore = $(data.data[0].title).html();
					valore = valore.replace(/<br \/>/g, '\n');
					valore = valore.replace(/<br>/g, '\n');
					valore = valore.replace(/<[a-z]*>/, '');
					valore = valore.replace(/<\/[a-z]*>/, '');
					valore = $("<p>" + valore + "</p>").text();
					valore = valore.replace(/\n/g, '<br />');
					if (data.data[0].myDip != '') {
						query = "<span class=\"myDip\" style=\"padding-left:20px;background-position: left center;\">" + valore + "</span>";
					} else {
						query = valore;
					}
					if (!notShowDescription && data.data[0].description) {
						var anchor = $("<a style=\"margin-right:5px;width:15px\" class=\"ui-icon ui-icon-plusthick fll\" title=\"visualizza descrizione\" href=\"#nn\"\"></a>").toggle(function() {
							$(this).attr("class", "ui-icon ui-icon-minusthick fll");
							$(this).parent().find('div.descrDiv').slideDown();
						}, function() {
							$(this).attr("class", "ui-icon ui-icon-plusthick fll");
							$(this).parent().find('div.descrDiv').slideUp();
						});
						obj.parent().prepend(anchor);
						var descrNode = $(" <div style=\"display:none;padding:10px\" class=\"descrDiv\">" + textToLink(data.data[0].description) + "</div>");
						descrNode.find("a").attr("target", "_blank");
						obj.parent().append(descrNode);
					}
				}

				if (data.data[0].afunction) {
					var afunction = data.data[0].afunction;
					$(".ui-icon-pencil", obj.parent()).click(function() {
						eval(afunction.replace(/null/g, '\'edit\''));
					});
					$(".deferred-ui-icon-pencil", obj.parent()).attr("href", 'javascript:' + afunction.replace(/null/g, '\'edit\'') + " ");
					$(".ui-icon-document", obj.parent()).click(function() {
						eval(afunction.replace(/'edit'/g, 'true'));
					});
				}

				var id = data.data[0].id;
				if (id) {
					query = '<input name="idRecord" type="hidden" value="' + id + '"/>' + query;
				}
			}
		},
		error : function(e) {
			query += " - errore - codice non trovato " + e;
		}
	});
	obj.html(query);
}

function getSingleDataFromRDF(url, nodo, valore) {
	if ((navigator.userAgent + "").indexOf("Chrome") != -1) {
		nodo = nodo.replace(/^.*:/g, "");
	} else {
		nodo = nodo.replace(/:/g, "\\:");
	}

	$(getDataFromRDF(url)).find(nodo).each(function() {
		if ($(this).attr('rdf:about') == valore) {
			valore = $(this).find(rdfConf['rdfsNamespaceEscape'] + 'label').text();
		}
	});
	return valore;
}

var fromTreeMultiSearch = false;
var fromTreeMultiSearchIdRecord = '';
var fromTreeMultiSearchTitle = '';
function doMultiSearch(idRecord, title) {
	if (Ext.getCmp("multiSearchPanel") != undefined) {
		Ext.getCmp("multiSearchTab").show();
		fromTreeMultiSearchIdRecord = idRecord;
		fromTreeMultiSearchTitle = title;
		hierarchicalSearch(idRecord, title);
	} else {
		fromTreeMultiSearch = true;
		fromTreeMultiSearchIdRecord = idRecord;
		fromTreeMultiSearchTitle = title;
		Ext.getCmp("multiSearchTab").show();
	}

	//
	/*
	 * var conta = $(".applications").length; openLoading();
	 * $(".applications").each(function() { conta--; var idArchive =
	 * $(this).attr("id").substring($(this).attr("id").indexOf("_") + 1); var
	 * limit = 10; $(this).load(path + "&idArchive=" + idArchive + "&limit=" +
	 * limit, function() { if (conta == 0) { closeLoading(); } }); });
	 */
}

/* TODO: da verificare!!! */
function doSimpleSearch(query, idArchivio) {
	if (idArchivio != null) {
		var iframeComp = Ext.getCmp("iframe-" + id);
		var tabComp = Ext.getCmp("iframe-tab-" + id);

		if (iframeComp != null && tabComp != null) {
			iframeComp.getDom().doSimpleSearch(query);
			// alert('2');
		}
	} else {
		if (query.indexOf("#") > 0) {
			query = query.substring(0, query.indexOf("#"));
		}

		Ext.getCmp('results_accordion').expand();
		Ext.getCmp('query_field').setValue('k:' + query);
		Ext.getCmp('esitoRicercaId').getStore().load({
			params : {
				query : 'k:' + query,
				department : top.Application['user'].dipartimento,
				start : 0
			}
		});
		return true;
	}
}
var sameAsWin = null;
function sameAs(query, documentId) {
	if (myWin != null) {
		sameAsWin.destroy();
		myWin = null;
	}
	sameAsWin = new Ext.Window({
		height : 400,
		width : 600,
		id : 'sameaswin',
		resizable : true,
		html : '<div id="sameAsWindow" class="scheda"></div>',
		autoScroll : true,
		id : 'showRecord',
		title : 'owl:sameAs',
		bbar : [ '->', {
			disabled : false,
			text : 'chiudi',
			handler : function() {
				Ext.getCmp('showRecord').destroy();
			}
		} ]
	});
	sameAsWin.show();

	var jSameAsWin = $('#sameAsWindow');
	jSameAsWin.append("<div><label>stringa di ricerca</label><input type=\"text\" class=\"edit_field\" name=\"queryString\"></div>");
	$('input[name=queryString]', jSameAsWin).val(unescape(query));
	var connection=null;
	jSameAsWin.append('<div id="controls" style="padding:15px;"></div>');

	/* DBPEDIA */
	var onDbPedia = $('<span><a href="#nn">cerca su dbpedia</a> &#160;&#160;&#160;</span>');
	onDbPedia.click(function() {
		$('#results', jSameAsWin).empty();
		if (connection) {
			connection.abort();
		}
		connection = $.ajax({
			url : 'http://lookup.dbpedia.org/api/search.asmx/PrefixSearch?QueryClass=&MaxHits=20&QueryString=' + $('input[name=queryString]', jSameAsWin).val(),
			async : true,
			beforeSend : function() {
				$('#results', jSameAsWin).empty();
				$('#results', jSameAsWin).append("<img src=\"img/ajax-loader.gif\" />");
			},
			success : function(data) {
				var xml = $(data);
				var result = [];
				xml.find('Result').each(function() {
					result.push({
						uri : $(this).children('URI').text(),
						label : $(this).children('Label').text(),
						description : $(this).children('Description').text()
					});
				});
				$('#results', jSameAsWin).empty();
				$.each(result, function(key) {
					$('#results', jSameAsWin).append("<li style=\"padding-bottom:10px\"><a href=\"" + result[key].uri + "\" target=\"_blank\">" + result[key].uri + "</a><br /><strong>" + result[key].label + "</strong><br /><em>" + result[key].description + "</em></li>");
				});
				if ($('#results', jSameAsWin).children('li').length == 0) {
					$('#results', jSameAsWin).append("<li><strong>nessun elemento trovato</strong></li>");
				}

			},
			error : function() {
				$('#results', jSameAsWin).empty();
				$('#results', jSameAsWin).append("<li><strong>attenzione si &egrave; qualche problema di connessione con dbpedia</strong></li>");
			}
		});
		return false;
	});
	jSameAsWin.find('#controls').append(onDbPedia);
	/* FINE DBPEDIA */

	/* VIAF */
	var onViaf = $('<a href="#nn">cerca su viaf</a>');
	onViaf.click(function() {
		$('#results', jSameAsWin).empty();
		if (connection) {
			connection.abort();
		}
		var dataToSend = {
			url : 'http://viaf.org/viaf/search?query=local.mainHeadingEl+all+' + escape($('input[name=queryString]', jSameAsWin).val()) + '%20and%20local.sources+any%20lc&sortKeys=holdingscount&maximumRecords=100&httpAccept=text/xml',
			parser : 'webUrl'
		};
		connection = $.ajax({
			cache : true,
			data : dataToSend,
			url : 'ajax/getRiferimentiNormativi.html',
			async : true,
			beforeSend : function() {
				$('#results', jSameAsWin).empty();
				$('#results', jSameAsWin).append("<img src=\"img/ajax-loader.gif\" />");
			},
			success : function(data) {
				data = data.replace(/ns[0-9]+:/gi, '');
				var xml = $(data);
				// console.debug(xml)
				var result = [];
				xml.find('record').each(function() {
					var localMap = {};
					var description = [];
					localMap.uri = $(this).find('document').attr("about");
					localMap.type = $(this).find('nametype').text();
					var labels = "";

					$(this).find('mainheadings').each(function() {
						$(this).find('data').each(function() {
							labels += $(this).find('text').text() + " <span style=\"font-weight:normal;font-size:9px\">(";
							if ($(this).find('s').length == 1) {
								labels += $(this).find('s').text();
							} else {
								$(this).find('s').each(function() {
									labels += $(this).text() + " - ";
								});
							}
							labels += ")</span><br />";
						});
					});
					localMap.label = labels.replace(/ - \)/g, ")");

					$(this).find('titles').each(function() {
						$(this).find('data').each(function() {
							var aDescr = ($(this).find('text').text()).replace(/</gi, "&lt;").replace(/>/gi, "&gt;");
							//Rimosso contains per incompatibilità browser
							//if (!description.contains(aDescr + "<br />")) {
							if (description.indexOf(aDescr + "<br />")==-1) {
								description.push(aDescr + "<br />");
							}
						});
					});
					var descr = "";
					for ( var int = 0; int < description.length; int++) {
						if (description.length > 5 && int == 5) {
							descr += "<span class=\"readMore\" style='display:none'>";
						}
						descr += description[int];
					}
					if (description.length > 5) {
						descr += "</span>";
					}
					localMap.description = descr;

					result.push(localMap);
				});
				$('#results', jSameAsWin).empty();
				$.each(result, function(key) {
					var li = $("<li style=\"padding-bottom:10px\"><span class=\"ui-icon ui-icon-circle-check  \" style=\"float:right\" ></span><a href=\"" + result[key].uri + "\" target=\"_blank\">" + result[key].uri + "</a> - " + result[key].type + "<br /><strong>" + result[key].label + "</strong><em>" + result[key].description + "</em></li>");
					li.find('span.readMore').each(function() {
						var a = $('<a href="#dfff">espandi</a>');
						a.toggle(function() {
							$(this).text(" ");
							li.find('span.readMore').slideDown('fast', function() {
								a.text("riduci");
							});
						}, function() {
							$(this).text(" ");
							li.find('span.readMore').slideUp('fast', function() {
								a.text("espandi");
							});
						});
						li.append(a);
					});
					$('#results', jSameAsWin).append(li);
				});
				if ($('#results', jSameAsWin).children('li').length == 0) {
					$('#results', jSameAsWin).append("<li><strong>nessun elemento trovato</strong></li>");
				}
			},
			error : function() {
				$('#results', jSameAsWin).empty();
				$('#results', jSameAsWin).append("<li><strong>attenzione si &egrave; qualche problema di connessione con viaf</strong></li>");
			}
		});

		var dataToSub = {
			"idRecord" : documentId,
			"idArchive" : globalOpt.document.archive,
			"xml_root" : globalOpt.document.jsonMap.configurazioni.root,
			"method" : "preserve",
			"encoding" : globalOpt.document.jsonMap.configurazioni.encoding,
			"action" : "update",
			"emptyNodesAction" : "delete"
		};
		$('.ui-icon-circle-check', jSameAsWin).click(function() {/*
																	 * var ele =
																	 * $(this);
																	 * $.ajax({
																	 * cache :
																	 * true,
																	 * data :
																	 * dataToSub,
																	 * url :
																	 * 'ajax/saveDocumental.html',
																	 * async :
																	 * true,
																	 * beforeSend :
																	 * function() {
																	 * $('#results',
																	 * jSameAsWin).empty();
																	 * ele.before("<img
																	 * src=\"img/ajax-loader.gif\"
																	 * />"); },
																	 * success :
																	 * function(data) {
																	 *  }, error :
																	 * function() {
																	 *  } });
																	 * 
																	 */
		});

		return false;
	});
	jSameAsWin.find('#controls').append(onViaf);
	/* FINE VIAF */

	jSameAsWin.append("<ul id=\"results\"></ul>");

}
