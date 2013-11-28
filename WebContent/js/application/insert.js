var myWin;
function addRecord(idArchivio, specificaDocumento, labelArchivio, reloadCmp, customCallback, type) {

	var originalArchive = 0;
	if (globalOpt.document && globalOpt.document.archive) {
		originalArchive = globalOpt.document.archive;
	}

	globalOpt.document.archiveLabel = labelArchivio;
	globalOpt.document.archive = idArchivio;
	globalOpt.document.reloadCmp = reloadCmp;
	if (myWin != null) {
		myWin.destroy();
		myWin = null;
	}
	myWin = new Ext.Window({
		height : 300,
		width : 400,
		fullscreen : true,
		resizable : true,
		autoScroll : true,
		id : 'addRecord',
		title : specificaDocumento,
		listeners : {
			'beforedestroy' : function(elemento) {
				$('#inj_insert').find('.simplemce, .verysimplemce').each(function() {
					getMCEremove($(this));
				});
			},
			'close' : function() {
				if (originalArchive) {
					globalOpt.document.archive = originalArchive;
				}
			},
			'destroy' : function() {
				if (originalArchive) {
					globalOpt.document.archive = originalArchive;
				}
			}
		},
		autoLoad : {
			url : 'ajax/insertDocumental.html?action=insertTemplate&idArchive=' + idArchivio + (type ? "&type=/" + type : ""),
			discardUrl : false,
			nocache : true,
			text : 'caricamento in corso...',
			timeout : 30,
			callback : customCallback ? customCallback : mainInsertCallback,
			scripts : true
		},
		bbar : [ {
			id : 'salva_record',
			disabled : true,
			// iconCls:'icon-ok',
			text : 'salva e continua',
			handler : function() {
				Ext.getCmp('salva_record').setDisabled(true);
				var alredyBounded = false;
				if ($("#ajaxInsertForm").data("events")) {
					$.each($("#ajaxInsertForm").data("events"), function(i, event) {
						$.each(event, function(j, h) {
							if ((h.handler + "").indexOf("ajaxSubmit") != -1) {
								alredyBounded = true;
							}
						});
					});
				}
				if (!alredyBounded) {
					$('#ajaxInsertForm').ajaxForm({
						dataType : 'html',
						beforeSubmit : function(arr, $form, options) {
							$('#loadingSpan').html('<img src="img/ajax-loader.gif" />');

						},
						success : function(responseText, statusText, xhr, form) {
							$('#loadingSpan').html('');
							responseText = $.parseJSON(responseText);
							if (responseText.result == 'ok') {
								top.Ext.ods.msg('documento creato con successo');
								top['Message']['Bus' + globalOpt.document.archive].fireEvent('savedoc');
								myWin.destroy();
								resetRecord();
								openRecord(responseText.idRecord, 'scheda', globalOpt.document.archive, globalOpt.document.archiveLabel, true);
								openRecord(responseText.idRecord, 'edit', globalOpt.document.archive, globalOpt.document.archiveLabel, true);
							}

						}
					});
				}
				$('#ajaxInsertForm').trigger('submit');
			}
		}, '|', {
			id : 'salva_record_chiudi',
			disabled : true,
			// iconCls:'icon-ok',
			text : 'salva e chiudi',
			handler : function() {

				Ext.getCmp('salva_record_chiudi').setDisabled(true);
				var alredyBounded = false;
				if ($("#ajaxInsertForm").data("events")) {
					$.each($("#ajaxInsertForm").data("events"), function(i, event) {
						$.each(event, function(j, h) {
							if ((h.handler + "").indexOf("ajaxSubmit") != -1) {
								alredyBounded = true;
							}
						});
					});
				}
				if (!alredyBounded) {
					$('#ajaxInsertForm').ajaxForm({
						dataType : 'html',
						beforeSubmit : function(arr, $form, options) {
							$('#loadingSpan').html('<img src="img/ajax-loader.gif" />');
						},
						success : function(responseText, statusText, xhr, form) {
							$('#loadingSpan').html('');
							responseText = $.parseJSON(responseText);
							if (responseText.result == 'ok') {
								top.Ext.ods.msg('documento creato con successo');
								top['Message']['Bus' + globalOpt.document.archive].fireEvent('savedoc');
								myWin.destroy();
								resetRecord();
								openRecord(responseText.idRecord);
							}

						}
					});
				}
				$('#ajaxInsertForm').trigger('submit');
			}
		}, '<span id="loadingSpan"></span>', '->', {
			id : 'annulla',
			disabled : false,
			text : 'annulla e chiudi',
			handler : function() {
				Ext.getCmp('addRecord').destroy();
			}
		} ]

	});
	myWin.show().maximize();
}
function callParser(testoPlain, dest) {
	var dataToSend = {
		blnDea : '0',
		docType : 'txt',
		testo : testoPlain
	};
	inputName = dest.attr("rel");
	dataDest = "";
	dest.find('span').html('<img src="img/ajax-loader.gif" />');

	$.ajax({
		type : 'POST',
		dataType : 'json',
		url : 'ajax/getRiferimentiNormativi.html',
		data : dataToSend,
		cache : true,
		success : function(data) {
			var valori = new Array();
			for (var i = 0; i < data.valori.length - 1; i++) {
				if (valori.length == 0) {
					valori[0] = data.valori[i];
					if (inputName.indexOf("[repeat]") == -1) {
						break;
					}
				} else {
					founded = false;
					for (var a = 0; a < valori.length; a++) {
						if (data.valori[i] == valori[a]) {
							founded = true;
							break;
						}
					}
					if (!founded) {
						valori[valori.length] = data.valori[i];
					}
				}
			}
			for (i = 0; i < valori.length; i++) {
				dataDest += '<input type="hidden" value="' + valori[i] + '" name="' + (inputName.replace(/\[repeat\]/gi, '[' + (i + 1) + ']')) + '">  <a onclick="doSimpleSearch(\'catena:' + valori[i].substring(valori[i].indexOf('?') + 1).replace(/\:/gi, '\:') + '\')" title="segui la catena normativa" href="#no">' + valori[i].substring(valori[i].indexOf('?') + 1) + '</a> [<a href="' + valori[i] + '" target="_blank">apri su nir</a>]<br />';
			}
			if (dataDest == '') {
				dataDest = '<em style="font-style:italic">nessun riferimento</em>';
			}
			dest.find('span').html(dataDest);
		}
	});
}

function callDataParser(testoPlain, dest) {
	var dataToSend = testoPlain;
	inputName = dest.attr("rel");
	dataDest = "";
	dest.find('span').html('<img src="img/ajax-loader.gif" />');

	dataToSend = dataToSend.replace(/  /gi, ' ');
	dataToSend = dataToSend.replace(/gennaio/gi, '01');
	dataToSend = dataToSend.replace(/febbraio/gi, '02');
	dataToSend = dataToSend.replace(/marzo/gi, '03');
	dataToSend = dataToSend.replace(/aprile/gi, '04');
	dataToSend = dataToSend.replace(/maggio/gi, '05');
	dataToSend = dataToSend.replace(/giugno/gi, '06');
	dataToSend = dataToSend.replace(/luglio/gi, '07');
	dataToSend = dataToSend.replace(/agosto/gi, '08');
	dataToSend = dataToSend.replace(/settembre/gi, '09');
	dataToSend = dataToSend.replace(/ottobre/gi, '10');
	dataToSend = dataToSend.replace(/novembre/gi, '11');
	dataToSend = dataToSend.replace(/dicembre/gi, '12');
	dataToSend = dataToSend.replace(/dic\./gi, '01');
	dataToSend = dataToSend.replace(/feb\./gi, '02');
	dataToSend = dataToSend.replace(/mar\./gi, '03');
	dataToSend = dataToSend.replace(/apr\./gi, '04');
	dataToSend = dataToSend.replace(/mag\./gi, '05');
	dataToSend = dataToSend.replace(/giu\./gi, '06');
	dataToSend = dataToSend.replace(/lug\./gi, '07');
	dataToSend = dataToSend.replace(/ago\./gi, '08');
	dataToSend = dataToSend.replace(/set\./gi, '09');
	dataToSend = dataToSend.replace(/ott\./gi, '10');
	dataToSend = dataToSend.replace(/nov\./gi, '11');
	dataToSend = dataToSend.replace(/dic\./gi, '12');
	dataToSend = dataToSend.replace(/[^\w](\d)[^\w]/gi, '/0$1/');

	var datePattern = /(\d{2})[^\w](\d{4})/;
	var re = new RegExp(datePattern);
	var m = re.exec(dataToSend);
	if (m == null) {
		dataDest = "";
	} else {
		dataDest = m[0][3] + m[0][4] + m[0][5] + m[0][6] + "-" + m[0][0] + m[0][1];
	}

	if (dataDest == '') {
		dataDest = '<em style="font-style:italic">non specificata</em> (verifica la correttezza della citazione bibliografica, contiene almeno una data nei formati "gennaio 2010", "1.2010" o "1/2010"?)';
	} else {
		dataDest = '<em style="font-style:italic"><input type="hidden" value="' + dataDest + '" name="' + (inputName) + '">  ' + dataDest + '</em>';
	}
	dest.find('span').html(dataDest);

}

function eneableDeleteButton() {
	var enable = false;
	$("*[name*='rif_unitaOrganizzativa'],input[name*='rif_unitaOrganizzativa']", $('#inj_edit, #inj_update')).each(function() {
		var dip = $(this).val();
		if ('unitaOrganizzativa.rdf/' + top.Application.user.dipartimento == dip) {
			enable = true;
		}
	});
	if (!globalOpt.editOn) {
		enable = false;
	}
	if (Ext.getCmp('deleteButton'))
		Ext.getCmp('deleteButton').setDisabled(!enable);
}

function mainEditCallback() {
	if (globalOpt.addImports) {
		globalOpt.addImports();
	}
	eneableDeleteButton();
	if ($('#inj_edit').length > 0) {
		commonEditigFunctions($('#inj_edit'));
		if (typeof customEditingFunctions != null && typeof customEditingFunctions != 'undefined') {
			customEditingFunctions($('#inj_edit'));
		}
	}
	if ($('#inj_update').length > 0) {
		commonEditigFunctions($('#inj_update'));
		if (typeof customEditingFunctions != null && typeof customEditingFunctions != 'undefined') {
			customEditingFunctions($('#inj_update'));
		}
	}

	// publish("applicationbus/Bus"+globalOpt.document.archive,escape('{"action":"edit","date":"'+Date.parse(new
	// Date())+'","user":"'+top.Application.user.id+'","doc":"'+globalOpt.document.id+'"}'));
	// publish("applicationbus/Bus"+globalOpt.document.archive,"Bus"+globalOpt.document.archive+"
	// "+(new Date()).getTime());
}

function saveDocument(formId, after, before) {
	$("#" + formId).ajaxForm({
		dataType : 'html',
		beforeSubmit : function(arr, $form, options) {
			// openLoading();
			if (before) {
				before();
			} else {
				top.Ext.ods.msg('salvataggio in corso');
			}
		},
		success : function(responseText, statusText, xhr, form) {
			resetRecord();
			top['Message']['Bus' + globalOpt.idArchivio].fireEvent('savedoc');
			if (after) {
				after();
			}
		}
	});
}
function doAddNode() {
	var cloned = $(this).parent().parent().parent().clone(false);
	cloned.find('.aggiungi').click(doAddNode);
	cloned.find('.rimuovi').click(doRemoveNode);
	$(this).parent().parent().parent().parent().append(cloned);
 

}
function doRemoveNode() {
	/*
	 * var nodo = $(this).parent().parent().parent().parent();
	 * if(nodo.find("select").length > 1){
	 */
	$(this).parent().parent().parent().remove();
	// }
}

function doSblocNode() {
 
	var sel = $(this).parent().parent().parent().find('select');
	var input = $('<input />');
	input.attr("name", sel.attr('name'));
	input.attr("class", sel.attr('class'));
	input.val(sel.val());
	sel.before(input);
	sel.detach();
	var sblocca = $(this);
	var blocca = $("<a href=\"#s\" class=\"blocca fll\"><span class=\"ui-icon ui-icon-circle-triangle-n fll\"></span>blocca</a>");
	sblocca.before(blocca);
	sblocca.detach();

	blocca.click(function() {
		var classe = $(this).parent().parent().parent().attr("class");
		sel.prependTo('.' + classe);
		blocca.before(sblocca);
		blocca.remove();

		input.remove();
	});

}

function addRemoveNode(obj) {
	$('.add-remove', obj).each(function() {
		var a = $("<span class=\"clone-control\"><br /><span class=\"ui-icon ui-icon-circle-plus fll\"></span><a href=\"#s\" class=\"aggiungi fll\">aggiungi</a><span class=\"fll\">&#160;&#160;&#160;|&#160;&#160;&#160;</span><span class=\"ui-icon ui-icon-circle-minus fll\"></span><a href=\"#s\" class=\"rimuovi fll\">rimuovi</a><br /><br /></span>");
		a.find('.aggiungi').click(doAddNode);
		a.find('.rimuovi').click(doRemoveNode);
		$(this).append(a);

	});

	$('.add-remove-unblock', obj).each(function() {
		var a = $("<span class=\"clone-control\"><br /><a href=\"#s\" class=\"aggiungi fll\"><span class=\"ui-icon ui-icon-circle-plus fll\"></span>aggiungi</a><span class=\"fll\">&#160;&#160;&#160;|&#160;&#160;&#160;</span><a href=\"#s\" class=\"rimuovi fll\"><span class=\"ui-icon ui-icon-circle-minus fll\"></span>rimuovi</a><span class=\"fll\">&#160;&#160;&#160;|&#160;&#160;&#160;</span><a href=\"#s\" class=\"sblocca fll\"><span class=\"ui-icon ui-icon-circle-triangle-s fll\"></span>sblocca</a><br /><br /></span>");
		a.find('.aggiungi').click(doAddNode);
		a.find('.rimuovi').click(doRemoveNode);
		a.find('.sblocca').click(doSblocNode);
		$(this).append(a);
	});

}

function highlightMatches(fieldJ, destColor) {
	if (!fieldJ.data('stopHighlight')) {
		setTimeout(function() {
			fieldJ.css("border-color", destColor);
		}, 200);
		setTimeout(function() {
			fieldJ.css("border-color", fieldJ.data('prevColor'));
			highlightMatches(fieldJ, destColor);
		}, 400);
	} else {
		fieldJ.css("border-color", fieldJ.data('prevColor'));
	}
}
function addMatches(obj) {
	obj.find('*[rel^=match]').each(function() {
		var fieldJ = $(this);
		var matchString = fieldJ.attr("rel").substring($(this).attr("rel").indexOf("/") + 1, $(this).attr("rel").lastIndexOf("/"));
		var modifier = fieldJ.attr("rel").substring($(this).attr("rel").lastIndexOf("/") + 1);
		var prevColor = fieldJ.css("border-color");
		fieldJ.data('prevColor', prevColor);
		fieldJ.keyup(function() {
			if (fieldJ.val() != '') {
				fieldJ.data('stopHighlight', true);
				if (!fieldJ.val().match(new RegExp(matchString, modifier))) {
					fieldJ.data('stopHighlight', false);
					var destColor = "red";
					highlightMatches(fieldJ, destColor);
				} else {
					fieldJ.data('stopHighlight', true);
					fieldJ.css("border-color", prevColor);
				}
			} else {
				fieldJ.data('stopHighlight', true);
				fieldJ.css("border-color", prevColor);
			}
		});
	});
}
function addSuggestions(obj) {
	obj.find('*[rel^=voc]').each(function() {
		var fieldJ = $(this);
		var field = fieldJ.attr("rel").substring($(this).attr("rel").indexOf(":") + 1);
		var suggTarget = $(this).parent().find(".vocTarget");
		var idArchivio = obj.find('input[name=idArchive]').attr("value");
		var id = 0;
		fieldJ.keydown(function() {
			if (id != 0)
				clearTimeout(id);
			id = setTimeout(function() {
				var value = fieldJ.attr("value");
				if (value.length > 2) {
					suggTarget.empty();
					suggTarget.append('<img src="img/ajax-loader.gif" />');
					var elenco = getVocArray(idArchivio, 10, field, null, value + "*");
					suggTarget.empty();
					for (var i = 0; i < elenco.length; i++) {
						var voceJ = $("<a href=\"#aa\">" + elenco[i] + "</a>");
						voceJ.click((function(index) {
							return function() {
								fieldJ.attr("value", unescape(elenco[index]));
								suggTarget.empty();
							};
						})(i));
						suggTarget.append(voceJ);
					}
					if (elenco.length == 0) {
						suggTarget.append("<a>nessun suggerimento</a>");
					}
				}
			}, 600);

		});

	});
}

function commonEditigFunctions(obj) {
	$('.autogrow', obj).autogrow({
		minHeight : 13,
		lineHeight : 13,
		expandTolerance : 1
	});

	addSuggestions(obj);
	addMatches(obj);
	addRemoveNode(obj);

	/*
	 * $('.rangedatepicker', obj).each(function() { $(this).daterangepicker({
	 * appendText : '<em>(aaaammgg)</em>', dateFormat : 'yymmdd', firstDay :
	 * 1, dayNamesMin : [ 'dom', 'lun', 'mar', 'mer', 'gio', 'ven', 'sab' ], });
	 * if ($(this).val() == '{now.yyyymmdd}') { var jsDate = new Date();
	 * $(this).val(jsDate.getFullYear() + ((jsDate.getUTCMonth() + 1) < 10 ? '0' :
	 * '') + (jsDate.getUTCMonth() + 1) + (jsDate.getUTCDate() < 10 ? '0' : '') +
	 * jsDate.getUTCDate()); } });
	 */

	$('.mydatepicker', obj).each(function() {
		$(this).datepicker({
			appendText : '<em>(aaaammgg)</em>',
			dateFormat : 'yymmdd',
			autoSize : true,
			firstDay : 1,
			beforeShow : function() {
				setTimeout(function() {
					$(".ui-datepicker").css("z-index", 999999);
				}, 10);
			},
			constrainInput : true,
			currentText : 'Now',
			showOtherMonths : true,
			selectOtherMonths : true,
			dayNamesMin : [ 'dom', 'lun', 'mar', 'mer', 'gio', 'ven', 'sab' ],
			nextText : 'avanti',
			prevText : 'indietro'
		});
		if ($(this).val() == '{now.yyyymmdd}') {
			var jsDate = new Date();
			$(this).val(jsDate.getFullYear() + ((jsDate.getUTCMonth() + 1) < 10 ? '0' : '') + (jsDate.getUTCMonth() + 1) + (jsDate.getUTCDate() < 10 ? '0' : '') + jsDate.getUTCDate());
		}
	});

	$('.mydatepickerNoDesc', obj).each(function() {
		$(this).datepicker({
			dateFormat : 'yymmdd',
			autoSize : true,
			firstDay : 1,
			beforeShow : function() {
				setTimeout(function() {
					$(".ui-datepicker").css("z-index", 999999);
				}, 10);
			},
			constrainInput : true,
			currentText : 'Now',
			showOtherMonths : true,
			selectOtherMonths : true,
			dayNamesMin : [ 'dom', 'lun', 'mar', 'mer', 'gio', 'ven', 'sab' ],
			nextText : 'avanti',
			prevText : 'indietro'
		});
		if ($(this).val() == '{now.yyyymmdd}') {
			var jsDate = new Date();
			$(this).val(jsDate.getFullYear() + ((jsDate.getUTCMonth() + 1) < 10 ? '0' : '') + (jsDate.getUTCMonth() + 1) + (jsDate.getUTCDate() < 10 ? '0' : '') + jsDate.getUTCDate());
		}
	});

	$('.mydatepickeryear', obj).each(function() {
		$(this).datepicker({
			appendText : '<em>(aaaa)</em>',
			dateFormat : 'yy',
			autoSize : true,
			changeYear : true,
			showButtonPanel : true,
			yearRange : '1930:+0',
			beforeShow : function() {
				setTimeout(function() {
					$(".ui-datepicker").css("z-index", 999999);
					$(".ui-datepicker-calendar").css("display", "none");
				}, 10);
			},
			onClose : function(dateText, inst) {
				var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
				$(this).datepicker('setDate', new Date(year, 1, 1));
			}
		});

		$(".mydatepickeryear").focus(function() {
			$(".ui-datepicker-calendar").hide();
		});
	});

	executeClassToFunction(obj);

	if ($('.simplemce', obj).length > 0) {
		loadScripts({
			base : 'application',
			scripts : [ 'mce' ]
		});
		initSimpleMceEditor($('.simplemce', obj));
	}
	if ($('.verysimplemce', obj).length > 0) {
		loadScripts({
			base : 'application',
			scripts : [ 'mce' ]
		});
		initVerySimpleMceEditor($('.verysimplemce', obj));
	}

}

function mainInsertCallback() {

	loadScripts({
		base : 'application/jsbusiness',
		scripts : [ $("input[name='type']").val() ]
	});

	eval($("input[name='type']").val() + "Logic()");

	commonEditigFunctions($('#inj_insert'));

	if (typeof customEditingFunctions != null && typeof customEditingFunctions != 'undefined') {
		customEditingFunctions($('#inj_insert'));
	}
}

function hideElements() {
	$('.hideOnLoad').hide();
}

function hideFields(obj) {
	obj.hide();
	obj.find('input, select').val('');
}
function showFields(obj) {
	obj.slideDown();
}

function addRelationFromGrid(id_record, id_relations, textRelation, callback, relation_type) {
	relation_type = relation_type == null ? '13' : relation_type;
	$.ajax({// TODO: ATTENZIONE QUI VA relation_type=13
		url : "ajax/documental_relation_manager.html?idRecord=" + id_relations + "&id_record_relation=" + id_record + "&relation_type=" + relation_type + "&action=add",
		cache : false,
		async : false,
		success : function() {
			// $("#elementiAssociati").append('<li>'+selectedRecord.data.title+'</li>');
			$("#elementiAssociati").append("<li style=\"border-bottom:1px solid #ededed;padding:2px;border-top:0;\" id=\"li_relations_" + id_relations + "\"> " + textRelation + "</li>");
			if (callback) {
				callback();
			}
		}
	});
	return true;
}

function deleteRecord() {
	Ext.MessageBox.show({
		title : 'Elimina documento',
		msg : 'Attenzione, confermare la cancellazione del documento?',
		width : 300,
		buttonText : {
			yes : 'sì',
			cancel : 'annulla',
			ok : 'ok'
		},
		buttons : Ext.MessageBox.OKCANCEL,
		fn : function(btn, text) {
			if (btn == 'ok') {
				openLoading();
				callAjax({
					success : function(jsonData) {
						closeLoading();
						Ext.MessageBox.alert('Elimina documento', 'documento cancellato con successo');
						globalOpt.document = {};
						openRecord(null);
						top['Message']['Bus' + globalOpt.document.archive].fireEvent('savedoc');
						// reloadDocumenti();
					},
					url : 'ajax/saveDocumental.html',
					type : 'POST',
					data : {
						'idRecord' : globalOpt.document.id,
						'action' : 'delete'
					}
				});
			}
		},
		icon : Ext.MessageBox.QUESTION

	});

}
function deleteRelationFromXpath(opts) {
	Ext.MessageBox.show({
		title : 'Elimina relazione',
		msg : 'Attenzione, confermare la cancellazione della relazione?',
		width : 300,
		buttonText : {
			yes : 'sì',
			cancel : 'annulla',
			ok : 'ok'
		},
		buttons : Ext.MessageBox.OKCANCEL,
		fn : function(btn, text) {
			if (btn == 'ok') {
				$.ajax({
					url : "ajax/documental_relation_manager.html?idRecord=" + opts.idRecord + "&xpath=" + escape(opts.xpath) + "&action=deleteFromXpath",
					cache : false,
					success : function() {
						top.Ext.ods.msg('relazione eliminata con successo');
						top['Message']['Bus' + globalOpt.document.archive].fireEvent('savedoc');
						reloadRecord('scheda');
					}
				});
			}
		},
		icon : Ext.MessageBox.QUESTION
	});

}
function deleteRelation(opts) {
	Ext.MessageBox.show({
		title : 'Elimina relazione',
		msg : 'Attenzione, confermare la cancellazione della relazione?',
		width : 300,
		buttonText : {
			cancel : 'annulla',
			ok : 'conferma'
		},
		buttons : Ext.MessageBox.OKCANCEL,
		fn : function(btn, text) {
			if (btn == 'ok' || btn == 'conferma') {
				$.ajax({
					url : "ajax/documental_relation_manager.html?relation_type=" + opts.relationType + "&idRecord=" + opts.idRecord + "&id_record_relation=" + opts.idRecord_relations + "&action=delete",
					cache : false,
					success : function() {
						if (opts.inverse) {
							$.ajax({
								url : "ajax/documental_relation_manager.html?relation_type=" + opts.relationType + "&idRecord=" + opts.idRecord_relations + "&id_record_relation=" + opts.idRecord + "&action=delete",
								cache : false,
								success : function() {
									top.Ext.ods.msg('relazione eliminata con successo');
									top['Message']['Bus' + globalOpt.document.archive].fireEvent('savedoc');
									reloadRecord('scheda');
								}
							});
						} else {
							top.Ext.ods.msg('relazione eliminata con successo');
							top['Message']['Bus' + globalOpt.document.archive].fireEvent('savedoc');
							reloadRecord('scheda');
						}
					}
				});
			}
		},
		icon : Ext.MessageBox.QUESTION
	});
}

function deleteElement(opts) {
	Ext.MessageBox.show({
		title : 'Elimina relazione',
		msg : 'Attenzione, confermare la cancellazione della relazione l\'eliminazione del documento?',
		width : 300,
		buttonText : {
			cancel : 'annulla',
			ok : 'conferma'
		},
		buttons : Ext.MessageBox.OKCANCEL,
		fn : function(btn, text) {
			if (btn == 'ok' || btn == 'conferma') {

				if (btn == 'ok') {
					openLoading();
					callAjax({
						success : function(jsonData) {
							closeLoading();
							Ext.MessageBox.alert('Elimina documento', 'documento cancellato con successo');
							top['Message']['Bus' + globalOpt.document.archive].fireEvent('savedoc');
							reloadRecord('scheda');
							// reloadDocumenti();
						},
						url : 'ajax/saveDocumental.html',
						type : 'POST',
						data : {
							'idRecord' : opts.idRecord,
							'action' : 'delete'
						}
					});
				}
			}
		},
		icon : Ext.MessageBox.QUESTION
	});
}
function salvaOrdine(obj) {
	var pathOrder = "";
	obj.find('*[data-xpath]').each(function() {
		pathOrder += ";" + $(this).attr("data-xpath");
	});
	pathOrder = pathOrder.substring(1);
	$('.loadingSpan', obj).html('<img src="img/ajax-loader.gif" />');
	$.ajax({
		url : "ajax/insertDocumental.html",
		data : {
			'xpathArray' : pathOrder,
			'idRecord' : globalOpt.document.id,
			'action' : 'sortNodes'
		},
		type : 'POST',
		cache : false,
		success : function() {
			reloadRecord('scheda');
		}
	});
}

var myWinValue;
function addAvalue(precValue, xPath, title, type, prefix) {

	if (myWinValue != null) {
		myWinValue.destroy();
		myWinValue = null;
	}
	myWinValue = new Ext.Window({
		height : 300,
		width : 550,
		resizable : true,
		autoScroll : true,
		id : 'addRecordValue',
		title : 'inserisci ' + title,
		listeners : {
			'beforedestroy' : function(elemento) {
				$('#inj_insertValue').find('.simplemce, .verysimplemce').each(function() {
					getMCEremove($(this));
				});
			},
			'afterrender' : function(elemento) {
				commonEditigFunctions($('#inj_insertValue'));
				$('#ajaxInsertValueForm').ajaxForm({
					dataType : 'html',
					beforeSubmit : function(arr, $form, options) {
						$('#loadingSpan').html('<img src="img/ajax-loader.gif" />');
					},
					success : function(responseText, statusText, xhr, form) {
						top.Ext.ods.msg('modifica effettuata con successo');
						$('#loadingSpan').html('');
						Ext.getCmp('addRecordValue').destroy();
						reloadRecord('scheda');
						top['Message']['Bus' + globalOpt.idArchivio].fireEvent('savedoc');
					}
				});
			}
		},
		// // <input type="hidden" name="' + prefix +
		// '/skos:changeNote[@rdf:parseType=\'Resource\'\'][*]/dc:creator/text()"
		// value="' + top.Application['user'].idUser + '">\
		// <input type="hidden" name="' + prefix +
		// '/skos:changeNote[@rdf:parseType=\'Resource\'][99]/dc:date/text()"
		// value="' + top.Application.today + '">\
		// <input type="hidden" name="' + prefix +
		// '/skos:changeNote[@rdf:parseType=\'Resource\'][99]/rdf:value/text()"
		// value="comment">\

		html : '<div id="inj_insertValue" style="padding:15px"><form name="ajaxInsertValueForm" id="ajaxInsertValueForm" method="post" action="ajax/saveDocumental.html">\
			<input type="hidden" name="idRecord" value="' + globalOpt.document.id + '" />\
			<input type="hidden" name="idArchive" value="' + globalOpt.document.archive + '" />\
			<input type="hidden" name="xml_root" value="' + globalOpt.document.jsonMap.configurazioni.root + '" />\
			<input type="hidden" name="method" value="preserve" />\
			<input type="hidden" name="encoding" value="' + globalOpt.document.jsonMap.configurazioni.encoding + '" />\
			<input type="hidden" name="action" value="update" />\
			<input type="hidden" name="' + prefix + '/ods:rif_unitaOrganizzativa/@rdf:resource" value="unitaOrganizzativa.rdf/'
				+ top.Application['user'].dipartimento + '" value="">\
			<input type="hidden" name="emptyNodesAction" value="delete" />\
			<label>' + title + '</label>\
			' + (type == 'rich' ? '<textarea class="edit_field simplemce addParser removeFullscreen" name="' + xPath + '">' + precValue + '</textarea>' : '<input class="edit_field" name="' + xPath + '" value="" />') + '\
			\
			\
			\
			</form></div>',
		bbar : [ {
			disabled : false,
			// iconCls:'icon-ok',
			text : 'salva e chiudi',
			handler : function() {
				// Ext.getCmp('ajaxInsertFormId');
				$('#ajaxInsertValueForm').trigger('submit');
			}
		}, '|', {
			disabled : false,
			// iconCls:'icon-ok',
			text : 'elimina e chiudi',
			handler : function() {
				// Ext.getCmp('ajaxInsertFormId');
				$('#inj_insertValue').find('.simplemce, .verysimplemce').each(function() {
					emptyMCEvalues($(this));
				});
				$('#ajaxInsertValueForm').trigger('submit');
			}
		}, '<span id="loadingSpan"></span>', '->', {
			disabled : false,
			text : 'annulla e chiudi',
			handler : function() {
				Ext.getCmp('addRecordValue').destroy();
			}
		} ]

	});
	myWinValue.show().maximize();
}

function lockFields(where, test) {
	var myTest = true;
	if (test) {
		myTest = false;
		$(".container-gestione_utente").find('input').each(function() {
			if ($(this).attr("value").indexOf(test) != -1) {
				myTest = true;
				return;
			}
		});
	}
	if (globalOpt.editUnlock) {
		myTest = false;
	}
	if (myTest) {
		if (Ext.getCmp('deleteButton'))
			Ext.getCmp('deleteButton').setDisabled(true);
		$(where).find('input,select').each(function() {
			if ($(this).attr("class").indexOf('editUnlock') == -1) {
				$(this).attr("readonly", "readonly");
				$(this).css({
					background : 'lightgray'
				});
			}
		});
		$(where).find('textarea').each(function() {
			if ($(this).attr("class").indexOf('editUnlock') == -1) {
				if ($(this).attr("class").indexOf('mce') != -1) {
					$(this).attr("class", $(this).attr("class").replace(/[a-z]*mce/gi, ''));
				}
				$(this).attr("readonly", "readonly");
				$(this).css({
					background : 'lightgray'
				});
			}
		});

	}
}
function saveAnXML(idArchive, idRecord, val) {
	$.ajax({
		url : 'ajax/documentalRecord.html?',
		type : 'POST',
		dataType : 'text',
		data : {
			'idArchive' : idArchive,
			'action' : 'save_xml',
			'idRecord' : idRecord,
			'xml' : val
		},
		cache : false,
		success : function() {
			top.Ext.ods.msg('salvato con successo');
			loadScripts({
				base : "sh",
				scripts : [ 'shCore', 'shBrushXml' ]
			});
			highLightXML();
			resetRecord();
			top['Message']['Bus' + globalOpt.idArchivio].fireEvent('savedoc');
		},

		beforeSubmit : function(arr, $form, options) {
			top.Ext.ods.msg('salvataggio in corso');
		}

	});

}
function addRecordPostIt(type) {
	var postit_font = '';
	var idArchive = globalOpt.idArchivio;
	var idUser = top.Application['user'].idUser;
	var idRecord = globalOpt.document.id;
	var idDepartment = '';
	var noteTypes = '1';
	if (type == 'my') {
		postit_font = 'postit_y';
	} else {
		postit_font = 'postit_g';
		idDepartment = top.Application.user.dipartimento;
		if (Ext.util.Format.trim(top.Application.user.dipartimento) == '') {
			Ext.Msg.alert('Attenzione', 'Selezionare un dipartimento prima di scrivere un postit dipartimentale!');
			return false;
		}
	}
	if (idRecord == undefined || idRecord == null || idRecord == 'null') {
		Ext.Msg.alert('Attenzione', 'Selezionare un record prima di scrivere un postit!');
		return false;
	}
	var postit = new Ext.Panel({
		id : 'postit',
		/* layout: 'fit', */
		bodyCssClass : postit_font,
		height : 345,
		width : 319,
		html : '<textarea id="note_text" class="' + postit_font + '_textarea"></textarea>',
		bbar : new Ext.Toolbar({
			/* height:'20', */
			autoHeight : true,
			autoScroll : true,
			buttonAlign : 'center',
			items : [ {
				text : 'Salva',
				disabled : false,
				handler : function() {
					var note_text = $('#note_text').val();
					if (Ext.util.Format.trim(note_text) != '') {
						Ext.Ajax.request({
							url : 'notesManager.html',
							method : 'POST',
							nocache : true,
							params : {
								action : 'save_note',
								note_text : note_text,
								idUser : idUser,
								idArchive : idArchive,
								idRecord : idRecord,
								idDepartment : idDepartment,
								noteTypes : noteTypes
							},
							success : function(result, request) {
								var idNote = Ext.util.Format.trim(result.responseText);
								if (type == 'my') {
									$('#postit_container').append('<a id="href_note_' + idNote + '" href="#no" onclick="openPostit(\'' + idNote + '\',\'' + Ext.util.Format.htmlEncode(note_text) + '\',\'my\')"><img id="' + idNote + '" src="img/notes/postit_y.gif" border="0" title="' + Ext.util.Format.htmlEncode(note_text) + '" alt="' + Ext.util.Format.htmlEncode(note_text) + '"/></a>');
								} else {
									$('#postit_container').append('<a id="href_note_' + idNote + '" href="#no" onclick="openPostit(\'' + idNote + '\',\'' + Ext.util.Format.htmlEncode(note_text) + '\',\'dep\')"><img id="' + idNote + '" src="img/notes/postit_g.gif" border="0" title="' + Ext.util.Format.htmlEncode(note_text) + '" alt="' + Ext.util.Format.htmlEncode(note_text) + '"/></a>');
								}

							},
							failure : function(result, request) {
								Ext.Msg.alert('Attenzione', 'Si è verificato un errore!');
							}
						});
						Ext.getCmp('postit_win').destroy();
					} else {
						Ext.Msg.alert('Attenzione', 'Inserire il testo della nota!');
					}
				}
			}, '|', {
				text : 'chiudi',
				disabled : false,
				handler : function() {
					Ext.getCmp('postit_win').destroy();
				}
			} ]
		})
	});
	var postit_win = new Ext.Window({
		id : 'postit_win',
		closable : true,
		closeAction : 'close',
		layout : 'fit',
		resizable : false,
		plain : true,
		width : 337,
		height : 380,
		modal : true,
		baseCls : 'x-plain',
		items : [ postit ]

	});
	postit_win.show();
}
function writeArchiveNote(id_note, title) {
	var idArchive = globalOpt.idArchivio;
	var idUser = top.Application['user'].idUser;
	var idNote = '';
	var idDepartment = top.Application.user.dipartimento;
	var noteTypes = '5';
	var del_dis = true;
	var action = 'save_note';
	var noteTitle = '';
	if (id_note != null && id_note != undefined) {
		del_dis = false;
		action = 'modify_note';
		idNote = id_note;
		noteTitle = title;
	}
	var archiveNote = new Ext.Panel({
		id : 'archiveNote',
		layout : 'fit',
		items : [ {
			id : 'archiveNote_textarea',
			xtype : 'textarea',
			height : 30,
			grow : true,
			value : title,
			preventScrollbars : true,
			style : {
				width : '95%'
			}
		} ],
		bbar : new Ext.Toolbar({
			/* height:'20', */
			autoHeight : true,
			autoScroll : true,
			buttonAlign : 'center',
			items : [ {
				text : 'salva e chiudi',
				disabled : false,
				handler : function() {
					var note_text = '';
					var note_title = Ext.getCmp('archiveNote_textarea').getValue();
					if (Ext.util.Format.trim(note_title) != '') {
						Ext.Ajax.request({
							url : 'notesManager.html',
							method : 'POST',
							nocache : true,
							params : {
								action : action,
								idNote : idNote,
								note_text : note_text,
								note_title : note_title,
								idUser : idUser,
								idArchive : idArchive,
								idDepartment : idDepartment,
								noteTypes : noteTypes
							},
							success : function(result, request) {
								Ext.getCmp('archiveNotesGrid_' + globalOpt.idArchivio).getStore().reload();
							},
							failure : function(result, request) {
								Ext.Msg.alert('Attenzione', 'Si è verificato un errore!');
							}
						});
						Ext.getCmp('archive_note_win').destroy();
					} else {
						Ext.Msg.alert('Attenzione', 'Inserire il testo della nota!');
					}
				}
			}, '|', {
				text : 'elimina e chiudi',
				disabled : del_dis,
				handler : function() {
					Ext.Ajax.request({
						url : 'notesManager.html',
						method : 'POST',
						nocache : true,
						params : {
							action : 'delete_note',
							idNote : idNote
						},
						success : function(result, request) {
							Ext.getCmp('archiveNotesGrid_' + globalOpt.idArchivio).getStore().reload();
						},
						failure : function(result, request) {
							Ext.Msg.alert('Attenzione', 'Si è verificato un errore!');
						}
					});
					Ext.getCmp('archive_note_win').destroy();
				}
			}, '|', {
				text : 'annulla e chiudi',
				disabled : false,
				handler : function() {
					Ext.getCmp('archive_note_win').destroy();
				}
			} ]
		})
	});
	var archive_note_win = new Ext.Window({
		id : 'archive_note_win',
		closable : true,
		closeAction : 'close',
		layout : 'fit',
		resizable : false,
		plain : true,
		width : 400,
		height : 200,
		modal : true,
		items : [ archiveNote ]

	});
	archive_note_win.show();
}
function twitterCount(text) {
	text = text.replace(/http:\/\/[^ ]+/gi, 'aaaaaaaaaaaaaaaaaaaaaa');
	text = text.replace(/https:\/\/[^ ]+/gi, 'aaaaaaaaaaaaaaaaaaaaaaa');
	return text.length;
}

function openTweetWindow(text, idRecord, callback) {
	var dip = top.Application['user'].dipartimento;
	var form = new Ext.FormPanel({
		frame : true,
		layout : 'fit',
		id : 'form',
		border : false,
		// bodyStyle : 'padding:5px',
		items : [ {
			name : 'depAcronym',
			xtype : 'field',
			hidden : true,
			value : dip
		}, {
			name : 'action',
			xtype : 'field',
			hidden : true,
			value : 'newTweet'
		}, {
			name : 'idRecord',
			xtype : 'field',
			hidden : true,
			value : idRecord
		}, {
			name : 'idUser',
			xtype : 'field',
			hidden : true,
			value : top.Application['user'].idUser
		},

		{
			name : 'tweet',
			xtype : 'textarea',
			id : 'textarea',
			grow : false,
			allowBlank : false,
			fullscreen : true,
			enableKeyEvents : true,
			width : 272,
			height : 119,
			// anchor : '100%',
			value : text,
			listeners : {
				'keyup' : function(textarea) {
					var textSize = 140 - twitterCount(text);
					a = this.getValue();
					a = a.replace(/http:\/\/[^ ]+/gi, 'aaaaaaaaaaaaaaaaaaaaaa');
					a = a.replace(/https:\/\/[^ ]+/gi, 'aaaaaaaaaaaaaaaaaaaaaaa');
					Ext.getCmp('countWords').setText(textSize - (a.length - twitterCount(text)));

				}
			}
		} ],

		bbar : new Ext.Toolbar({
			autoHeight : true,
			autoScroll : true,
			buttonAlign : 'center',
			items : [ {
				xtype : 'tbtext',
				text : 140 - twitterCount(text),
				id : 'countWords'
			}, {
				text : 'Tweet',
				handler : function() {
					$('.testSpan').empty();
					var size = $('#countWords').text();
					var form = Ext.getCmp('form').getForm();
					var link = Ext.getCmp('textarea').getValue().replace(/.*(http[s]*:\/\/[^ ]*).*/g, '$1');
					openLoading("Verifica link in corso...");
					if(link==Ext.getCmp('textarea').getValue()){
						link="";
					}
					testUrl(link, $('.testSpan'), function() {
					closeLoading();
					if($('.testSpan span').attr('class').indexOf("alert")==-1){
						if (size < 141 && size > 0) {
							if (confirm('Attenzione:\nConfermare l\'invio del tweet')) {
								
								form.submit({
									waitMsg : 'Invio in corso... ',
									url : 'twitter.html',
									success : function(form, action) {
										twitter_win.destroy();
										Ext.Msg.alert('Attenzione:', 'Operazione eseguita con successo!');
										if (callback) {
											callback();
										}
									},
									failure : function(form, action) {
										Ext.Msg.alert('Attenzione:', action.result.msg);
										twitter_win.destroy();
									}
								});
								
							}
						} else
							{Ext.ods.errorMsg('Attenzione: Numero di caratteri non consentito!');
							}
					}else{
						Ext.Msg.alert('Attenzione:', 'Il link "'+link+'" non  è valido!');
						
					}
					});

				}
			}, ' | ', '<div class="testSpan"></div>', ' | ', {
				text : 'Annulla',
				handler : function() {
					twitter_win.destroy();
				}
			} ]
		})
	});

	var twitter_win = new Ext.Window({
		title : 'Invio nuovo Tweet',
		id : 'tweetWindow_window',
		closable : true,
		closeAction : 'close',
		layout : 'fit',
		resizable : false,
		plain : true,
		width : 300,
		height : 200,
		modal : true,
		items : [ form ]

	});
	if (!dip)
		Ext.ods.errorMsg('Selezionare un dipartimento');
	else
		twitter_win.show();
}
