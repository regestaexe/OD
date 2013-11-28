function changeNumeroElementi(gridId, numElementi) {
	var titolo = Ext.getCmp(gridId).title;
	if (titolo.indexOf("elementi") > 0) {
		titolo = titolo.substring(0, titolo.indexOf(":"));
	}
	Ext.getCmp(gridId).setTitle(titolo + ": " + numElementi + " elementi");
}
function generateDipartimentiFilter(gridId, titoloId) {
	var dipaMap = [];
	var xml = $(getDataFromRDF('rdf/unitaOrganizzativa.xml'));
	i = 0;
	xml.find(rdfConf['odsNamespaceEscape'] + "unitaOrganizzativa").each(function() {
		if ($(this).find(rdfConf['odsNamespaceEscape'] + "rif_unitaOrganizzativa").length == 0) {
			var resource = $(this).attr(rdfConf['rdfNamespace'] + "about");
			var testo = $(this).find(rdfConf['rdfsNamespaceEscape'] + "label").text();
			var company = $(this).attr("company");
			if ((top.Application.user.company == 1000000000) || (top.Application.user.company == company)) {
				dipaMap[i] = {
					text : testo,
					checked : false,
					group : 'filtro_' + gridId,
					checkHandler : function(item, checked) {
						if (checked) {
							delete (Ext.getCmp(gridId).getStore().baseParams.filtersUtente);
							delete (Ext.getCmp(gridId).getStore().baseParams.filtersfiltersToday);
							Ext.getCmp(gridId).getStore().baseParams.filtersDipartimento = resource.substring(resource.indexOf("/") + 1);
							Ext.getCmp(gridId).getStore().load({
								callback : function() {
									if (titoloId) {
										changeNumeroElementi(titoloId, this.reader.jsonData.totalCount);
									}
								},
								params : {
									"start" : 0,
									department : top.Application['user'].dipartimento
								/*
								 * , "limit" : 40
								 */
								}
							});
							Ext.getCmp('filter_button_' + gridId).setIconClass('filter');

						}
					}
				};
				i++;
			}
		}

	});
	return dipaMap;
}
function generateArchiveFiltersFilterList(gridId, titoloId) {
	var dipaMap = [];
	var xml = $(getDataFromRDF('rdf/archivi.xml'));
	i = 0;
	xml.find(rdfConf['odsNamespaceEscape'] + "archivio").each(function() {
		var resource = $(this).attr(rdfConf['rdfNamespace'] + "about");
		var testo = $(this).find(rdfConf['rdfsNamespaceEscape'] + "label").text();
		dipaMap[i] = {
			text : testo,
			checked : false,
			group : 'filtro_archive_' + gridId,
			checkHandler : function(item, checked) {
				if (checked) {
					Ext.getCmp(gridId).getStore().baseParams.id_archive = resource;
					Ext.getCmp(gridId).getStore().load({
						callback : function() {
							if (titoloId) {
								changeNumeroElementi(titoloId, this.reader.jsonData.totalCount);
							}
						},
						params : {
							department : top.Application['user'].dipartimento,

							"start" : 0
						/*
						 * , "limit" : 40
						 */
						}
					});
					// console.debug(item);
					Ext.getCmp('filter_button_archive_' + gridId).setText(item.text);
				}
			}
		};
		i++;
	});
	return dipaMap;
}

function generateArchiveFilters(gridId, titoloId) {
	return new Ext.Button({
		text : 'archivi',
		id : 'filter_button_archive_' + gridId,
		menu : new Ext.menu.Menu({
			id : 'menu_archive' + gridId,
			items : [ generateArchiveFiltersFilterList(gridId, titoloId) ]
		})
	});
}

function generateFilters(gridId, titoloId) {
	return new Ext.Button({
		text : 'filtra',
		iconCls : '',
		id : 'filter_button_' + gridId,
		menu : new Ext.menu.Menu({
			id : 'menu' + gridId,
			items : [ {
				text : 'tutti',
				checked : true,
				group : 'filtro_' + gridId,
				checkHandler : function(item, checked) {
					if (checked) {
						delete (Ext.getCmp(gridId).getStore().baseParams.filtersUtente);
						delete (Ext.getCmp(gridId).getStore().baseParams.filtersDipartimento);
						delete (Ext.getCmp(gridId).getStore().baseParams.filtersToday);
						Ext.getCmp(gridId).getStore().load({
							callback : function() {
								if (titoloId) {
									changeNumeroElementi(titoloId, this.reader.jsonData.totalCount);
								}
							},
							params : {
								department : top.Application['user'].dipartimento,

								"start" : 0
							/*
							 * , "limit" : 40
							 */
							}
						});
						Ext.getCmp('filter_button_' + gridId).setIconClass('');

					}
				}
			}, {
				text : 'i miei',
				checked : false,
				group : 'filtro_' + gridId,
				checkHandler : function(item, checked) {
					if (checked) {
						delete (Ext.getCmp(gridId).getStore().baseParams.filtersDipartimento);
						delete (Ext.getCmp(gridId).getStore().baseParams.filtersfiltersToday);
						Ext.getCmp(gridId).getStore().baseParams.filtersUtente = top.Application.user.id;
						Ext.getCmp(gridId).getStore().load({
							callback : function() {
								if (titoloId) {
									changeNumeroElementi(titoloId, this.reader.jsonData.totalCount);
								}
							},
							params : {
								department : top.Application['user'].dipartimento,

								"start" : 0
							/*
							 * , "limit" : 40
							 */
							}
						});
						Ext.getCmp('filter_button_' + gridId).setIconClass('filter');

					}
				}
			},/*
				 * { text : 'di oggi', checked : false, group : 'filtro_' +
				 * gridId, checkHandler : function(item, checked) { if (checked) {
				 * delete
				 * (Ext.getCmp(gridId).getStore().baseParams.filtersUtente);
				 * delete
				 * (Ext.getCmp(gridId).getStore().baseParams.filtersDipartimento);
				 * Ext.getCmp(gridId).getStore().baseParams.filtersToday =
				 * 'true'; Ext.getCmp(gridId).getStore().load({ params : {
				 * "start" : 0, "limit" : 40 } }); } } },
				 */
			{
				text : 'del mio dipartimento',
				checked : false,
				group : 'filtro_' + gridId,
				checkHandler : function(item, checked) {
					if (checked) {
						delete (Ext.getCmp(gridId).getStore().baseParams.filtersUtente);
						delete (Ext.getCmp(gridId).getStore().baseParams.filtersfiltersToday);
						Ext.getCmp(gridId).getStore().baseParams.filtersDipartimento = top.Application.user.dipartimento;
						Ext.getCmp(gridId).getStore().load({
							callback : function() {
								if (titoloId) {
									changeNumeroElementi(titoloId, this.reader.jsonData.totalCount);
								}
							},
							params : {
								department : top.Application['user'].dipartimento,

								"start" : 0
							/*
							 * , "limit" : 40
							 */
							}
						});
						Ext.getCmp('filter_button_' + gridId).setIconClass('filter');

					}
				}
			}, {
				text : 'di altri dipartimenti',
				checked : false,
				group : 'filtro_' + gridId,
				menu : new Ext.menu.Menu({
					items : generateDipartimentiFilter(gridId, titoloId)
				})
			} ]
		})
	});
}
var ds = new Ext.data.JsonStore({
	url : 'ajax/documental_adv_search.html',
	root : 'data',
	idProperty : 'id',
	totalProperty : 'totalCount',
	baseParams : {
		id_archive : globalOpt.idArchivio,
		department : top.Application['user'].dipartimento,
		action : 'search',
		limit : 40,
		sort : 'DATE'
	},
	fields : [ 'id', 'title', 'date', 'description', 'afunction', 'preview', 'myDip' ],
	listeners : {
		'load' : function() {
			highlightRecord();
		}
	}
});
var dsRegionWestRicerca = new Ext.data.JsonStore({
	url : 'ajax/documental_adv_search.html',
	root : 'data',
	idProperty : 'id',
	totalProperty : 'totalCount',
	baseParams : {
		id_archive : globalOpt.idArchivio,
		department : top.Application['user'].dipartimento,
		action : 'searchRegionWest',
		limit : 40
	},
	fields : [ 'id', 'title', 'date', 'description', 'afunction', 'preview', 'myDip' ],
	listeners : {
		'load' : function() {
			highlightRecord();
		}
	}
});
var storeConcetti = new Ext.data.JsonStore({
	url : 'ajax/documental_adv_search.html',
	root : 'data',
	idProperty : 'id',
	totalProperty : 'totalCount',
	baseParams : {
		id_archive : '1',
		inScheme : '',
		action : 'search',
		outputMode : 'skosAddRelation',
		limit : 40
	},
	fields : [ 'id', 'title', 'date', 'description', 'afunction', 'preview', 'myDip' ],
	listeners : {
		'beforeload' : function() {
			if (Ext.getCmp('checkboxMyDip').getValue()) {
				this.baseParams.inScheme = getInScheme();
			} else {
				this.baseParams.inScheme = '';
			}
		}

	}

});
function getInScheme() {
	var elencoDipartimenti = listDipartimenti();
	for (a = 0; a < elencoDipartimenti.length; a++) {
		value = elencoDipartimenti[a];
		if (value.code == top.Application['user'].dipartimento) {
			return value.inScheme;
		}
	}
	return '';
}
function getInSchemeId(id_archive) {
	var elencoDipartimenti = listDipartimenti();
	for (a = 0; a < elencoDipartimenti.length; a++) {
		value = elencoDipartimenti[a];
		if (value.code == top.Application['user'].dipartimento) {
			if (value.idArchive == id_archive) {
				return value.inSchemeId;
			}
		}
	}
	return '';
}
var storeDocumenti = new Ext.data.GroupingStore({
	url : 'ajax/documental_adv_search.html',
	reader : new Ext.data.JsonReader({
		totalProperty : 'totalCount',
		root : 'data',
		id_archive : globalOpt.idArchivio,
		id : 'id',
		fields : [ 'id', 'title', 'date', 'description', 'datenormal', 'preview', 'myDip' ]
	}),
	groupDir : 'DESC',
	sortInfo : {
		field : 'date',
		direction : 'DESC'
	},
	groupField : 'datenormal',
	baseParams : {
		id_archive : globalOpt.idArchivio,
		action : 'search',
		sort : 'DATE',
		query : '{a TO Z}',
		limit : 40
	},
	listeners : {
		'load' : function() {
			highlightRecord();
		}
	}

});
var magazinesFields = [ {
	name : 'rivista',
	mapping : 'rivista'
} ];

var magazinesStore = new Ext.data.JsonStore({
	url : 'searchBuilderVoc.html',
	root : 'riviste',
	idProperty : 'rivista',
	fields : magazinesFields,
	baseParams : {
		id_archive : '2',
		department : top.Application['user'].dipartimento,
		magazines : 'dip',
		action : 'vocListDottrina',
		field : 'rivista'
	}
});
var archiveStoricFields = [ {
	name : 'type',
	mapping : 'type'
} ];

var archiveStoricStore = new Ext.data.JsonStore({
	url : 'searchBuilderVoc.html',
	root : 'types',
	idProperty : 'type',
	fields : archiveStoricFields,
	baseParams : {
		id_archive : '13',
		action : 'vocListStoric',
		field : 'type'
	}
});
/*
 * var storeDocumenti = new Ext.data.JsonStore({ remoteSort : true, fields : [
 * 'id', 'title', 'date', 'description' ], url : 'ajax/loadLastOccurences.html',
 * baseParams : { idArchive : globalOpt.idArchivio, action : 'json_data', limit :
 * 40 } });
 */

// var resultTpl = new Ext.XTemplate('<tpl for=".">', '<div
// class="link_document"><a href="#no" onclick="openRecord(\'{id}\',null,' +
// globalOpt.idArchivio + ');">{value}<br
// /><span>{descrizione}</span></a></div>', '</tpl>');
var expander = new Ext.ux.grid.RowExpander({
	width : 16,
	tpl : new Ext.Template('<div style="padding:2px;margin:1px;border-top:1px solid #d0d0d0;text-align:justify">{description}</div>')
});
var expanderEsito = new Ext.ux.grid.RowExpander({
	width : 16,
	tpl : new Ext.Template('<div style="padding:2px;margin:1px;border-top:1px solid #d0d0d0;text-align:justify">{description}</div>')
});
var expanderConcetti = new Ext.ux.grid.RowExpander({
	width : 16,
	tpl : new Ext.Template('<div style="padding:2px;margin:1px;border-top:1px solid #d0d0d0;text-align:justify">{description}</div>')
});
/*
 * var expanderRicerche = new Ext.ux.grid.RowExpander({ width : 16, tpl : new
 * Ext.Template('<div style="padding:2px;margin:1px;border-top:1px solid
 * #d0d0d0;text-align:justify">{description}</div>') });
 */
var gridEsitoRicerca = new Ext.grid.GridPanel({
	store : dsRegionWestRicerca,
	id : 'esitoRicercaId',
	trackMouseOver : true,
	disableSelection : true,
	loadMask : {
		msg : 'caricamento in corso...',
		enable : true
	},
	// grid columns
	border : false,
	// plugins : expanderEsito,
	collapsible : false,
	enableColumnResize : false,
	enableColumnMove : false,
	headerAsText : false,
	enableColumnHide : false,
	animCollapse : false,
	header : false,
	hideHeaders : true,
	iconCls : 'icon-grid',
	autoHeight : true,
	stateful : true,
	hideHeaders : true,
	autoScroll : false,
	enableHdMenu : false,
	autoSizeColumns : true,
	autoSizeGrid : true,
	autoExpandColumn : 'title',
	columns : [ {
		id : 'preview',
		header : '',
		width : 20,
		flex : 0,
		fixed : true,
		dataIndex : 'preview'
	}, {
		id : 'title',
		header : "Titolo",
		dataIndex : 'title'
	}, {
		id : 'myDip',
		header : '',
		width : 23,
		flex : 0,
		fixed : true,
		dataIndex : 'myDip'
	} ],
	viewConfig : {
		forceFit : true
	},
	listeners : {
		'afterrender' : function() {
			top.Message['Bus' + globalOpt.idArchivio].on('savedoc', function() {
				this.getStore().reload();
			}, this);
		}

	}

});

var gridDocumenti = new Ext.grid.GridPanel({
	store : storeDocumenti,
	id : 'gridDocumentiId',
	trackMouseOver : true,
	disableSelection : true,
	loadMask : {
		msg : 'caricamento in corso...',
		enable : true
	},
	listeners : {
		'afterrender' : function() {
			var myStore = this.getStore();
			myStore.load({
				callback : function() {
					Ext.getCmp('btt_documenti_precedenti').setDisabled(false);
					changeNumeroElementi('documents_accordion', this.reader.jsonData.totalCount);
				},
				params : {
					"start" : 0,
					department : top.Application['user'].dipartimento,
					"limit" : 40
				}
			});
			top.Message['Bus' + globalOpt.idArchivio].on('savedoc', function() {
				this.getStore().reload({
					callback : function() {
						changeNumeroElementi('documents_accordion', this.reader.jsonData.totalCount);
					}
				});
			}, this);
		}
	},

	// grid columns
	border : false,
	// plugins : expander,
	collapsible : false,
	headerAsText : false,
	enableColumnResize : false,
	enableColumnMove : false,
	enableColumnHide : false,
	animCollapse : false,
	hideHeaders : true,
	stripeRows : true,
	header : false,
	iconCls : 'icon-grid',
	autoHeight : true,
	stateful : true,
	autoScroll : false,
	enableHdMenu : false,
	autoSizeColumns : true,
	autoSizeGrid : true,
	autoExpandColumn : 'title',
	columns : [ {
		id : 'preview',
		header : '',
		width : 20,
		flex : 0,
		fixed : true,
		dataIndex : 'preview'
	}, {
		id : 'title',
		flex : 1,
		header : "Titolo",
		dataIndex : 'title'
	}, {
		id : 'myDip',
		header : '',
		width : 23,
		flex : 0,
		fixed : true,
		dataIndex : 'myDip'
	}, {
		id : 'laData',
		dataIndex : 'date',
		hidden : true,
		hideable : false
	}, {
		id : 'datenormal',
		dataIndex : 'datenormal',
		hidden : true,
		hideable : false
	} ],
	view : new Ext.grid.GroupingView({
		forceFit : true,
		groupTextTpl : '{[ values.rs[0].data["date"] ]} ({[values.rs.length]} {[values.rs.length > 1 ? "elementi" : "elemento"]})'
	}),
	viewConfig : {
		forceFit : true
	}
});
var gridConcetti = new Ext.grid.GridPanel({
	store : storeConcetti,

	trackMouseOver : true,
	disableSelection : true,
	loadMask : {
		msg : 'caricamento in corso...',
		enable : true
	},
	// grid columns
	border : false,
	enableColumnResize : false,
	enableColumnMove : false,
	headerAsText : false,
	hideHeaders : true,
	enableColumnHide : false,
	header : false,
	enableHdMenu : false,
	plugins : expanderConcetti,
	collapsible : false,
	animCollapse : false,
	iconCls : 'icon-grid',
	autoHeight : true,
	stateful : true,
	autoScroll : false,
	autoSizeColumns : true,
	autoSizeGrid : true,
	autoExpandColumn : 'title',

	columns : [ expanderConcetti, {
		id : 'title',
		header : "Titolo",
		dataIndex : 'title'
	}, {
		id : 'myDip',
		header : '',
		width : 23,
		flex : 0,
		fixed : true,
		dataIndex : 'myDip'
	} ],
	viewConfig : {
		forceFit : true
	}
});
var toolbarDocumenti = new Ext.Toolbar({
	items : [ '->', new Ext.Button({
		text : 'documenti più recenti',
		id : 'btt_documenti_recenti',
		disabled : true,
		handler : function(btn) {
			Ext.getCmp('btt_documenti_recenti').setDisabled(true);
			Ext.getCmp('btt_documenti_precedenti').setDisabled(true);
			startfrom = (storeDocumenti.lastOptions.params.start ? storeDocumenti.lastOptions.params.start : 0) - 40;
			storeDocumenti.load({
				callback : function() {
					if (startfrom > 0) {
						Ext.getCmp('btt_documenti_recenti').setDisabled(false);
					}
					Ext.getCmp('btt_documenti_precedenti').setDisabled(false);
				},
				params : {
					department : top.Application['user'].dipartimento,
					start : startfrom
				}
			});
			// $.ajax({success:function(data){$('.documents').html(data);},cache:false,url:'ajax/loadLastOccurences.html?idArchive='+globalOpt.idArchivio+'&limit=40'});
		}
	}), new Ext.Button({
		text : 'documenti precedenti',
		id : 'btt_documenti_precedenti',
		disabled : true,
		handler : function(btn) {
			Ext.getCmp('btt_documenti_recenti').setDisabled(true);
			Ext.getCmp('btt_documenti_precedenti').setDisabled(true);
			startfrom = (storeDocumenti.lastOptions.params.start ? storeDocumenti.lastOptions.params.start : 0) + 40;
			storeDocumenti.load({
				callback : function() {
					if (startfrom > 0) {
						Ext.getCmp('btt_documenti_recenti').setDisabled(false);
					}
					Ext.getCmp('btt_documenti_precedenti').setDisabled(false);
				},
				params : {
					department : top.Application['user'].dipartimento,
					start : startfrom
				}
			});
			// $.ajax({success:function(data){$('.documents').html(data);},cache:false,url:'ajax/loadLastOccurences.html?idArchive='+globalOpt.idArchivio+'&limit=40'});
		}
	}) ]
});
function getDataLegislature(inverse, limit) {
	var data = [];
	var elems = $(getDataFromRDF('rdf/legislatura.xml')).find(rdfConf['odsNamespaceEscape'] + "legislatura");
	var nodes = jQuery.makeArray(elems);
	var end = Math.min(limit, nodes.length);
	if (inverse)
		nodes.reverse();
	for (var i = 0; i < end; i++) {
		data[i] = [ afterLastSlash($(nodes[i]).attr(rdfConf['rdfNamespace'] + 'about')), $(nodes[i]).find(rdfConf['rdfsNamespaceEscape'] + 'label').text() ];
	}
	return data;
}
function getDataDipartimenti() {
	var data = [];
	var count = 0;
	$(getDataFromRDF('rdf/unitaOrganizzativa.xml')).find(rdfConf['odsNamespaceEscape'] + "unitaOrganizzativa").each(function() {
		if ($(this).attr(rdfConf['rdfNamespace'] + 'about').indexOf("/tor") == -1) {
			var company = $(this).attr("company");
			if ((top.Application.user.company == 1000000000) || (top.Application.user.company == company)) {
				data[count] = [ afterLastSlash($(this).attr(rdfConf['rdfNamespace'] + 'about')), $(this).find(rdfConf['rdfsNamespaceEscape'] + 'label').text() ];
				count++;
			}
		}
	});
	return data;
}
function getDataLegislatureForSearch(inverse, limit) {
	var data = [];
	var elems = $(getDataFromRDF('rdf/legislatura.xml')).find(rdfConf['odsNamespaceEscape'] + "legislatura");
	var nodes = jQuery.makeArray(elems);
	var end;
	if (limit == "all") {
		end = nodes.length; 
	} else {
		end = Math.min(limit, nodes.length);
	}
	if (inverse)
		nodes.reverse();
	data[0] = [ "", "Tutte le Legislature" ];
	for (var i = 0; i < end; i++) {
		data[i + 1] = [ afterLastSlash($(nodes[i]).attr(rdfConf['rdfNamespace'] + 'about')), $(nodes[i]).find(rdfConf['rdfsNamespaceEscape'] + 'label').text() ];
	}
	return data;
}


function getDataDipartimentiForSearchBPR() {
	var data = [];
	data[0] = [ "", "tutti" ];
	data[1] = [ "bpr", "BPR" ];
	data[2] = [ "none", "NON BPR" ];
	return data;
}


function getDataDipartimentiForSearch() {
	var data = [];
	var count = 0;
	data[count] = [ "", "Tutti i Dipartimenti" ];
	count++;
	$(getDataFromRDF('rdf/unitaOrganizzativa.xml')).find(rdfConf['odsNamespaceEscape'] + "unitaOrganizzativa").each(function() {
		if ($(this).attr(rdfConf['rdfNamespace'] + 'about').indexOf("/tor") == -1) {
			if ((top.Application.user.company == 1000000000) || ($(this).attr("company") == top.Application.user.company)) {
				data[count] = [ afterLastSlash($(this).attr(rdfConf['rdfNamespace'] + 'about')), $(this).find(rdfConf['rdfsNamespaceEscape'] + 'label').text() ];
				count++;
			}
		}
	});
	return data;
}

var countCheckTipoNorma = 0;
function getCheckTipoNorma(name) {
	var currentCountCheckTipoNorma = countCheckTipoNorma;
	var data = [];
	var count = 0;
	$(getDataFromRDF('rdf/ambitoTerritoriale.xml')).find(rdfConf['odsNamespaceEscape'] + "ambitoTerritoriale").each(function() {
		data[count] = [ {
			boxLabel : $(this).find(rdfConf['rdfsNamespaceEscape'] + 'label').text(),
			name : name + '_' + count,
			inputValue : afterLastSlash($(this).attr(rdfConf['rdfNamespace'] + 'about')),
			listeners : {
				'check' : function(ck, checked) {
					if (checked) {
						Ext.getCmp(name + '_empty_' + currentCountCheckTipoNorma).setValue(true);
					}
				}
			}
		} ];
		count++;
	});
	data[count] = [ {
		boxLabel : 'Non specificato',
		id : name + '_empty_' + currentCountCheckTipoNorma,
		name : name + '_' + count,
		inputValue : '""'
	} ];
	countCheckTipoNorma++;
	return data;
}
function getPeriodoStorico(name) {
	var data = [];
	var count = 0;
	$(getDataFromRDF('rdf/periodoStorico.xml')).find(rdfConf['odsNamespaceEscape'] + "periodoStorico").each(function() {
		data[count] = [ {
			boxLabel : $(this).find(rdfConf['rdfsNamespaceEscape'] + 'label').text(),
			name : name + '_' + count,
			inputValue : afterLastSlash($(this).attr(rdfConf['rdfNamespace'] + 'about'))
		} ];
		count++;
	});
	return data;
}

function getTipologia() {
	var data = [];
	data[0] = [ {
		boxLabel : 'Parlamento e istituzioni',
		name : 'type',		
		inputValue : 'Parlamento e istituzioni',
		height:40
	} ];
	data[1] = [ {
		boxLabel : 'Politica, cultura e società',
		name : 'type',
		inputValue : 'Politica, cultura e società',
		height:40
	} ];
	data[2] = [ {
		boxLabel : 'Politica estera ed eventi internazionali',
		name : 'type',
		inputValue : 'Politica estera ed eventi internazionali',
		height:40
	} ];
	return data;
}

function getTHArchives() {
	var data = [];
	data[0] = [ {
		boxLabel : 'Thesaurus',
		name : 'griglia',
		inputValue : '""'
	} ];
 ;
	return data;
} 

function messageDeprecated(id_deprecate, id_archive) {
	if (Ext.util.Format.trim(id_deprecate) != '' && Ext.util.Format.trim(id_deprecate) != 'norecord') {
		Ext.Ajax.request({
			url : 'ajax/deprecate.html?action=deprecate_to&id_deprecate=' + id_deprecate + '&idArchive=' + id_archive,
			nocache : true,
			success : function(result) {
				var jsonData = Ext.util.JSON.decode(result.responseText);
				Ext.MessageBox.buttonText.yes = "Sí";
				Ext.MessageBox.buttonText.cancel = "No";
				Ext.MessageBox.confirm('Attenzione', 'Il termine selezionato è stato deprecato in favore di  <b>"' + jsonData.title + '"</b>, aggiungere la relazione con il nuovo termine proposto?', function(btn) {
					if (btn == "yes") {
						rel = '12';
						if (jsonData.id_archive != 1) {
							rel = '14';
						}
						addRelationFromTree(jsonData.id_record, rel, jsonData.title, id_archive);
					}
				});
			},
			failure : function() {
			}
		});
	} else {
		Ext.Msg.alert('Attenzione', 'Impossibile aggiungere una relazione ad un termine deprecato che non indica in favore di chi è stato deprecato!');
	}
}

function generateGridConcetti(idArchiveGrid) {

	if (!globalOpt.skosArchives)
		globalOpt.skosArchives = {};

	if (!globalOpt.skosArchives['expanderConcetti_' + idArchiveGrid]) {
		globalOpt.skosArchives['expanderConcetti_' + idArchiveGrid] = new Ext.ux.grid.RowExpander({
			width : 16,
			tpl : new Ext.Template('<div style="padding:2px;margin:1px;border-top:1px solid #d0d0d0;text-align:justify">{description}</div>')
		});
	}

	if (!globalOpt.skosArchives['storeConcetti_' + idArchiveGrid]) {
		globalOpt.skosArchives['storeConcetti_' + idArchiveGrid] = new Ext.data.JsonStore({
			url : 'ajax/documental_adv_search.html',
			root : 'data',
			idProperty : 'id',
			totalProperty : 'totalCount',
			baseParams : {
				id_archive : idArchiveGrid,
				inScheme : '',
				action : 'search',
				outputMode : 'skosAddRelation',
				limit : 40
			},
			fields : [ 'id', 'title', 'date', 'description', 'afunction', 'preview', 'myDip' ],
			listeners : {
				'beforeload' : function() {
					if (Ext.getCmp('checkboxMyDip_' + idArchiveGrid).getValue()) {
						if (idArchiveGrid == 1)
							this.baseParams.inScheme = getInScheme();
					} else {
						this.baseParams.inScheme = '';
					}
				}
			}
		});
	}

	if (!globalOpt.skosArchives['gridConcetti_' + idArchiveGrid]) {
		globalOpt.skosArchives['gridConcetti_' + idArchiveGrid] = new Ext.grid.GridPanel({
			store : globalOpt.skosArchives['storeConcetti_' + idArchiveGrid],

			trackMouseOver : true,
			disableSelection : true,
			loadMask : {
				msg : 'caricamento in corso...',
				enable : true
			},
			// grid columns
			border : false,
			enableColumnResize : false,
			enableColumnMove : false,
			headerAsText : false,
			hideHeaders : true,
			enableColumnHide : false,
			header : false,
			enableHdMenu : false,
			plugins : globalOpt.skosArchives['expanderConcetti_' + idArchiveGrid],
			collapsible : false,
			animCollapse : false,
			iconCls : 'icon-grid',
			autoHeight : true,
			stateful : true,
			autoScroll : false,
			autoSizeColumns : true,
			autoSizeGrid : true,
			autoExpandColumn : 'title',

			columns : [ globalOpt.skosArchives['expanderConcetti_' + idArchiveGrid], {
				id : 'title',
				header : "Titolo",
				dataIndex : 'title'
			}, {
				id : 'myDip',
				header : '',
				width : 35,
				flex : 0,
				fixed : true,
				dataIndex : 'myDip'
			} ],
			viewConfig : {
				forceFit : true
			}
		});
	}
}

var archiveNotesGridStore = new Ext.data.GroupingStore({
	url : 'notesManager.html',
	reader : new Ext.data.JsonReader({
		root : 'notes',
		totalProperty : 'totalCount',
		id : 'id',
		fields : [ 'id', 'title', 'date', 'img' ]
	}),
	baseParams : {
		idArchive : globalOpt.idArchivio,
		action : 'archiveNoteList',
		idDepartment : top.Application['user'].dipartimento,
		idUser : top.Application['user'].idUser
	}
});

var archiveNotesGrid = new Ext.grid.GridPanel({
	id : 'archiveNotesGrid_' + globalOpt.idArchivio,
	store : archiveNotesGridStore,
	height : 100,
	listeners : {
		'afterrender' : function() {
			this.getStore().load();
		}
	},
	trackMouseOver : false,

	border : true,
	collapsible : false,
	headerAsText : false,
	enableColumnResize : false,
	enableColumnMove : false,
	enableColumnHide : false,
	animCollapse : false,
	hideHeaders : true,
	header : false,
	autoHeight : true,
	autoWidth : true,
	stateful : true,
	autoScroll : false,
	enableHdMenu : false,
	autoExpandColumn : 'title',
	columns : [ {
		id : 'title',
		header : 'Titolo',
		flex : 1,
		dataIndex : 'title'
	}, {
		id : 'date',
		header : 'Date',
		width : 70,
		flex : 0,
		fixed : true,
		dataIndex : 'date'
	}, {
		id : 'img',
		header : '',
		width : 27,
		flex : 0,
		fixed : true,
		dataIndex : 'img'
	} ],
	viewConfig : {
		forceFit : true
	}
});
var task = {
	run : function() {
		if (top.testActiveTab(globalOpt.idArchivio)) {
			if (archiveNotesGridStore) {
				archiveNotesGridStore.reload();
			}
		}
	},
	interval : 10000
// 1 second
};
var runner = new Ext.util.TaskRunner();
runner.start(task);
