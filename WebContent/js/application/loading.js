/*jQuery.fn.highlight = function (str, className) {
    var regex = new RegExp(str, "gi");
    return this.each(function () {
        this.innerHTML = this.innerHTML.replace(regex, function(matched) {return "<span class=\"" + className + "\">" + matched + "</span>";});
    });
};*/

jQuery.extend({
	highlight : function(node, re, nodeName, className) {
		if (node.nodeType === 3) {
			var match = node.data.match(re);
			if (match) {
				var highlight = document.createElement(nodeName || 'span');
				highlight.className = className || 'highlight_class';
				var wordNode = node.splitText(match.index);
				wordNode.splitText(match[0].length);
				var wordClone = wordNode.cloneNode(true);
				highlight.appendChild(wordClone);
				wordNode.parentNode.replaceChild(highlight, wordNode);
				return 1; // skip added node in parent
			}
		} else if ((node.nodeType === 1 && node.childNodes) && // only element
		// nodes that
		// have children
		!/(script|style)/i.test(node.tagName) && // ignore script and style
		// nodes
		!(node.tagName === nodeName.toUpperCase() && node.className === className)) { // skip
			// if
			// already
			// highlighted
			for ( var i = 0; i < node.childNodes.length; i++) {
				i += jQuery.highlight(node.childNodes[i], re, nodeName, className);
			}
		}
		return 0;
	}
});
jQuery.fn.highlight = function(words, options) {
	var settings = {
		className : 'highlight_class',
		element : 'span',
		caseSensitive : false,
		wordsOnly : false
	};
	jQuery.extend(settings, options);

	if (words.constructor === String) {
		words = [ words ];
	}
	words = jQuery.grep(words, function(word, i) {
		return word != '';
	});
	words = jQuery.map(words, function(word, i) {
		return word.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&");
	});
	if (words.length == 0) {
		return this;
	}

	var flag = settings.caseSensitive ? "" : "i";
	var pattern = "(" + words.join("|") + ")";
	if (settings.wordsOnly) {
		pattern = "\\b" + pattern + "\\b";
	}
	var re = new RegExp(pattern, flag);

	return this.each(function() {
		jQuery.highlight(this, re, settings.element, settings.className);
	});
};
function highlightHTMLText(text, str, className) {
	var regex = new RegExp(str, "gi");
	return text.replace(regex, function(matched) {
		return "<span class=\"" + className + "\">" + matched + "</span>";
	});
}
function loadDocument(opts) {
	if (globalOpt.document != null && globalOpt.document.id != null && globalOpt.document.id != 'null') {
		options = opts ? opts : {};
		var what = options.what ? options.what : 'scheda';
		if (!globalOpt.document[what]) {
			globalOpt.document[what] = {};
		}

		idArchivio = options.idArchivio ? options.idArchivio : globalOpt.idArchivio;
		if (!top['documentCache']) {
			top['documentCache'] = {};
		}
		if (!top['documentCache'][idArchivio] || !top['documentCache'][idArchivio][what]) {
			top['documentCache'][idArchivio] = {};
			top['documentCache'][idArchivio][what] = {};
		}

		/* produco il json del record */
		if (!globalOpt.document.jsonData) {
			$.ajax({
				success : function(json_data) {
					globalOpt.document.jsonData = json_data;
				},
				cache : false,
				async : false,
				url : 'ajax/documentalRecord.html?idArchive=' + idArchivio + '&action=json_data&idRecord=' + globalOpt.document.id
			});
		}
		var appendAnnotazione = "";
		 

		/* carico il template associato alla vista (scheda|edit|etc.) */

		if (!top['documentCache'][idArchivio][what].template) {
			$.ajax({
				url : 'ajax/documentalRecord.html?idArchive=' + idArchivio + appendAnnotazione + '&action=' + what + "Template",
				success : function(htmlTemplate) {
					if (Ext.isIE) {
						try {
							htmlTemplate = htmlTemplate.replace(/<!\[CDATA\[/gi, '');
							htmlTemplate = htmlTemplate.replace(/\]\]>/gi, '');
						} catch (e) {
						}
					}
					top['documentCache'][idArchivio][what].template = htmlTemplate;
				},
				async : false,
				cache : true
			});
		}
		/* estraggo dal template i tag script */
		if (!top['documentCache'][idArchivio][what].scriptTemplate) {
			htmlTemplateScript = "";
			regex = /<script\ .*?>((?:.|[\n|\r])*?)<\/script>/ig;
			while (match = regex.exec(top['documentCache'][idArchivio][what].template)) {
				htmlTemplateScript += match[1] + "\n";
			}
			try {
				top['documentCache'][idArchivio][what].template = (top['documentCache'][idArchivio][what].template).replace(regex, '');
			} catch (e) {
			}
			top['documentCache'][idArchivio][what].scriptTemplate = htmlTemplateScript;
		}

		/* eseguo la logica di scrittura */
		var schedaJq = $(top['documentCache'][idArchivio][what].template);
		eval(top['documentCache'][idArchivio][what].scriptTemplate);
		if (what == 'edit' || what == 'update') {
			doInjection({
				target : schedaJq,
				jsonData : globalOpt.document.jsonData,
				ifEmpty : '',
				escapeDblQuote : true
			});
		} else {
			doInjection({
				target : schedaJq,
				jsonData : globalOpt.document.jsonData
			});
		}
		var schedaJqString = schedaJq.html();
		if (what == 'edit' || what == 'update') {
			schedaJqString = schedaJqString.replace(/<!--\[CDATA\[/g, '');
			schedaJqString = schedaJqString.replace(/\]\]-->/g, '');
			// alert(schedaJqString)
			$('#inj_' + what).html(schedaJqString);
			schedaJq = $(schedaJqString);
		} else {
			if(typeof toDea != null && typeof toDea != "undefined")
				$('#inj_' + what).html(toDea(schedaJqString));
			else
				$('#inj_' + what).html(schedaJqString);
		}

		if (what != 'edit' && what != 'update') {
			mainLoadCallback($('#inj_' + what));
			executeClassToFunction($('#inj_' + what));
		} else {
			// executeClassToFunction($('#inj_' + what));
		}
		globalOpt.document[what].output = schedaJq.html();
		if (what == 'scheda') {
			$('#postit_container').load('notesManager.html?idArchive=' + idArchivio + appendAnnotazione + '&idUser=' + top.Application['user'].idUser + '&idRecord=' + globalOpt.document.id + '&idDepartment=' + top.Application['user'].dipartimento + '&action=recordNoteList');
			$("#myDipPanel").html(myDipartimentoLabel() + "&#160;");
 
			if (Ext.getCmp('documental_tab_panel'))
				Ext.getCmp('documental_tab_panel').fireEvent('tabLoaded');

		}

		if (options.callback) {
			options.callback();
		}
	} else {
		$('#inj_scheda').html('<div class="scheda"><div class="sezione"><div class="head">&lt;--</div><h2>selezionare un documento</h2></div></div>');
	}
 
}
function resetRecord() {
	globalOpt.document.jsonData = null;
	globalOpt.document['scheda'] = {};
	globalOpt.document['update'] = {};
	globalOpt.document['edit'] = {};
}
function openRecord(id_record, showTab, idArchivio, descrArchivio, addToHistory) {
	if (!idArchivio)
		idArchivio = globalOpt.idArchivio;
	  {
		if ((Ext.getCmp('documental_tab_panel')  ) && idArchivio == globalOpt.idArchivio) {
			$('#inj_scheda').html('<div class="loading-indicator">caricamento in corso...</div>');
			if (!globalOpt.document['scheda']) {
				globalOpt.document['scheda'] = {};
			}
			globalOpt.document['scheda'].output = null;
			globalOpt.document.id = id_record;
			globalOpt.document.archive = idArchivio;
			globalOpt.document.jsonData = null;

			loadDocument();
			try {
				if (showTab && showTab == 'edit') {
					if (Ext.getCmp('documental_tab_panel'))
						Ext.getCmp('documental_tab_panel').setActiveTab(Ext.getCmp('edit_tab_tab'));
				} else if (showTab && showTab == 'relations') {
					if (Ext.getCmp('documental_tab_panel'))
						Ext.getCmp('documental_tab_panel').setActiveTab(Ext.getCmp('relations_tab_tab'));
				} else if (showTab || showTab == null || showTab == undefined) {
					if (Ext.getCmp('documental_tab_panel'))
						Ext.getCmp('documental_tab_panel').setActiveTab(Ext.getCmp('schedaBreveContainer'));
				}
			} catch (e) {
			}

			if (addToHistory != false && globalOpt.document.id != null && globalOpt.document.id != 'null' && globalOpt.document.id != undefined) {
				if (globalOpt.archiveHistory) {
					if (globalOpt.archiveHistory[globalOpt.archiveHistory.length - 1] != id_record) {
						globalOpt.archiveHistory.push(id_record);
						globalOpt.archiveHistory.index = globalOpt.archiveHistory.length - 1;
					}
					managebackForwardButtons();
				}
			}
			// checkPublished(Ext.getCmp('singleRecordPublishButton'));
			highlightRecord(id_record);
		} else {
			var img = $('<img style="display:none" width="12" height="12" src="img/archive_img/archive_' + idArchivio + '.gif" border="0"/>');
			var hasImg = true;
			img.error(function() {
				hasImg = false;
			});
			$('body').append(img);
			parent.addTab((hasImg ? '<img width="12" height="12" src="img/archive_img/archive_' + idArchivio + '.gif" border="0"/>' : '') + '&nbsp;' + descrArchivio, idArchivio, 'documentalWorkspace.html?idArchive=' + idArchivio + '&idRecord=' + id_record + (showTab && showTab == 'edit' ? '&openEdit=true' : ''));
		}

	}

}
function reloadRecord(tab, showTab) {
	globalOpt.document.jsonData = null;
	globalOpt.document[tab] = {};
	openRecord(globalOpt.document.id, showTab);
	return true;
}
function backForward(action, tab, showTab) {
	globalOpt.document.jsonData = null;
	globalOpt.document[tab] = {};
	if (globalOpt.archiveHistory.length > 1) {
		if (action == 'back') {
			if (globalOpt.archiveHistory.index > 0) {
				globalOpt.archiveHistory.index = globalOpt.archiveHistory.index - 1;
				openRecord(globalOpt.archiveHistory[globalOpt.archiveHistory.index], showTab, globalOpt.idArchivio, '', false);
			}
		} else {
			if (globalOpt.archiveHistory.index < globalOpt.archiveHistory.length - 1) {
				globalOpt.archiveHistory.index = globalOpt.archiveHistory.index + 1;
				openRecord(globalOpt.archiveHistory[globalOpt.archiveHistory.index], showTab, globalOpt.idArchivio, '', false);
			}
		}
	}
	managebackForwardButtons();
	return true;
}
function deleteHistory() {
	globalOpt.archiveHistory.index = 0;
	globalOpt.archiveHistory = [];
	globalOpt.archiveHistory.push(globalOpt.document.id);
	managebackForwardButtons();
}
function managebackForwardButtons() {
	if (globalOpt.archiveHistory.length > 1) {
		Ext.getCmp('history_delete_button_' + globalOpt.idArchivio).setDisabled(false);
		if (globalOpt.archiveHistory.index == 0) {
			Ext.getCmp('history_back_button_' + globalOpt.idArchivio).setDisabled(true);
			Ext.getCmp('history_forward_button_' + globalOpt.idArchivio).setDisabled(false);
		} else if (globalOpt.archiveHistory.index == globalOpt.archiveHistory.length - 1) {
			Ext.getCmp('history_forward_button_' + globalOpt.idArchivio).setDisabled(true);
			Ext.getCmp('history_back_button_' + globalOpt.idArchivio).setDisabled(false);
		} else {
			Ext.getCmp('history_back_button_' + globalOpt.idArchivio).setDisabled(false);
			Ext.getCmp('history_forward_button_' + globalOpt.idArchivio).setDisabled(false);
		}
	} else {
		Ext.getCmp('history_back_button_' + globalOpt.idArchivio).setDisabled(true);
		Ext.getCmp('history_forward_button_' + globalOpt.idArchivio).setDisabled(true);
		Ext.getCmp('history_delete_button_' + globalOpt.idArchivio).setDisabled(true);
	}
	return true;
}
function sendRecord(idArchive, archiveLabel) {

	if (!idArchive) {
		idArchive = globalOpt.document.archive;
	}

	if (!archiveLabel) {
		archiveLabel = globalOpt.descrArchivio;
	}

	if (globalOpt.document.id != null && globalOpt.document.id != 'null' && globalOpt.document.id != undefined) {
		var messages = [ [ "Documento di interesse", "Questo documento di " + archiveLabel + " potrebbe essere di tuo interesse", "Il seguente documento, nell'archivio " + archiveLabel + ", credo ti possa interessare!<br /><a href=\"#no\" onclick=\"return openRecord('" + globalOpt.document.id + "',true,'" + idArchive + "','" + archiveLabel + "');\">visualizza record</a>" ], [ "Documento pubblicabile", "Documento dell'archivio " + archiveLabel + " è pubblicabile", "Il seguente documento, nell'archivio " + archiveLabel + ", è stato completato ed è pubblicabile!<br /><a href=\"#no\" onclick=\"return openRecord('" + globalOpt.document.id + "',true,'" + idArchive + "','" + archiveLabel + "');\">visualizza record</a>" ] ];
		var messagesStore = new Ext.data.ArrayStore({
			fields : [ 'label', 'object', 'text' ],
			data : messages
		});
		var usersDsAll = new Ext.data.JsonStore({
			url : 'messagesManager.html',
			root : 'users',
			id : 'usersDsAll',
			idProperty : 'idUser',
			totalProperty : 'totalCount',
			baseParams : {
				action : 'getUsersList',
				type : 'all_dep',
				messageMode : 'normal'
			},
			fields : [ 'idUser', 'name', 'class' ]
		});
		var send_record_fields = {
			xtype : 'fieldset',
			id : 'save_search_fields',
			bodyStyle : 'padding:5px;',
			border : false,
			margins : '5 5 5 5',
			anchor : '95%',
			items : [ {
				xtype : 'combo',
				fieldLabel : 'Destinatario',
				name : 'send_to',
				anchor : '95%',
				multiSelect : true,
				editable : false,
				displayField : 'name',
				valueField : 'idUser',
				store : usersDsAll,
				blankText : 'seleziona un destinatario...',
				id : 'send_to',
				selectOnFocus : true,
				triggerAction : 'all',
				itemSelector : 'div',
				mode : 'remote',
				tpl : new Ext.XTemplate('<tpl for="."><div class="{class}">{name}</div></tpl>')
			}, {
				xtype : 'combo',
				name : 'message_type',
				fieldLabel : 'Tipo segnalazione',
				id : 'message_type',
				anchor : '95%',
				editable : false,
				triggerAction : 'all',
				displayField : 'label',
				typeAhead : false,
				mode : 'local',
				store : messagesStore,
				listeners : {
					'select' : function(combo, record, index) {
						Ext.getCmp('message_object').setValue(record.data.object);
						Ext.getCmp('message_text').setValue(record.data.text);
					}
				}
			}, {
				xtype : 'radiogroup',
				id : 'messageMode',
				fieldLabel : 'Tipo messaggio',
				items : [ {
					boxLabel : 'Normale',
					name : 'messageMode',
					inputValue : 'normal',
					checked : true
				}, {
					boxLabel : 'Lettura obbligata',
					name : 'messageMode',
					inputValue : 'modal'
				} ]
			}, {
				xtype : 'textfield',
				fieldLabel : 'Oggetto',
				name : 'message_object',
				id : 'message_object',
				readOnly : true,
				anchor : '95%'
			}, {
				xtype : 'textarea',
				fieldLabel : 'Testo',
				name : 'message_text',
				readOnly : true,
				hidden : true,
				hideLabel : true,
				id : 'message_text',
				anchor : '95%'
			}, {
				id : 'add_message_text',
				fieldLabel : 'Testo aggiuntivo',
				xtype : 'textarea',
				height : 50,
				grow : true,
				value : '',
				preventScrollbars : true,
				anchor : '95%'
			} ],
			buttons : [ {
				text : 'Invia',
				disabled : false,
				handler : function(btn) {
					var messageMode = '';
					var ckFields = Ext.getCmp('messageMode').items;
					for ( var y = 0; y < ckFields.length; y++) {
						if (ckFields.get(y).getValue()) {
							messageMode += ckFields.get(y).inputValue;
						}
					}
					messageMode = Ext.util.Format.trim('' + messageMode + '');
					var sendTo = Ext.util.Format.trim('' + Ext.getCmp('send_to').getValue() + '');
					var message_object = Ext.util.Format.trim('' + Ext.getCmp('message_object').getValue() + '');
					var message_text = Ext.util.Format.trim('' + Ext.getCmp('message_text').getValue() + '');
					var add_message_text = Ext.util.Format.trim('' + Ext.getCmp('add_message_text').getValue() + '');
					if (messageMode != '' && sendTo != '' && message_object != '' && message_text != '') {
						if (add_message_text != '') {
							message_text += '<br />' + add_message_text;
						}
						Ext.Ajax.request({
							url : 'messagesManager.html',
							method : 'POST',
							nocache : true,
							params : {
								action : 'sendMessage',
								messageMode : messageMode,
								sendTo : sendTo,
								message_object : message_object,
								message_text : message_text
							},
							success : function(result, request) {
								Ext.MessageBox.alert('Stato invio Messaggio:', 'Messaggio inviato correttamente', function() {
									send_record_win.destroy();
								});
							},
							failure : function(result, request) {
								Ext.Msg.alert('Stato invio Messaggio:', 'Si è verificato un errore imprevisto durante l\'invio del messaggio!');
							}
						});

					} else {
						Ext.Msg.alert('Attenzione', 'Compilare tutti i campi prima di inviare il messaggio!');
					}
				}
			}, {
				text : 'Svuota',
				handler : function() {
					Ext.getCmp('other_depComboBox').setValue('');
					Ext.getCmp('my_depComboBox').setValue('');
					Ext.getCmp('message_object').setValue('');
					Ext.getCmp('message_text').setValue('');
					if (messageMode == 'system') {
						Ext.getCmp('startdt').setValue('');
						Ext.getCmp('starttime').setValue('');
						Ext.getCmp('enddt').setValue('');
						Ext.getCmp('endtime').setValue('');
					}
				}
			}, {
				text : 'Chiudi',
				disabled : false,
				handler : function(btn) {
					send_record_win.destroy();
				}
			} ]
		};

		var send_record_win = new Ext.Window({
			title : 'Segnala documento',
			id : 'sendRecordWin',
			closable : true,
			width : 600,
			height : 350,
			modal : true,
			plain : true,
			layout : 'fit',
			items : [ send_record_fields ]
		});
		send_record_win.show(this);
	} else {
		Ext.Msg.alert('Attenzione', 'Selezionare un documento!');
	}

	return true;
}
function cratePost() {
	if (globalOpt.document.id != null && globalOpt.document.id != 'null' && globalOpt.document.id != undefined) {

		Ext.MessageBox.show({
			title : 'Crea post da documento corrente',
			msg : 'Attenzione, confermare la creazione del post?',
			width : 300,
			buttonText : {
				yes : 'sì',
				cancel : 'annulla',
				ok : 'ok'
			},
			buttons : Ext.MessageBox.OKCANCEL,
			fn : function(btn, text) {
				if (btn == 'ok') {
					Ext.Ajax.request({
						url : 'ajax/createPost.html',
						method : 'POST',
						nocache : true,
						params : {
							idRecord : globalOpt.document.id,
							idArchive : globalOpt.idArchivio,
							department : top.Application['user'].dipartimento
						},
						success : function(result, request) {
							Ext.Msg.alert('Creazione Post', result.responseText);
							reloadRecord('scheda');
						},
						failure : function(result, request) {
							Ext.Msg.alert('Attenzione', 'Si è verificato un errore imprevisto durante la creazione del Post!');
						}
					});
				}
			},
			icon : Ext.MessageBox.QUESTION

		});

	} else {
		Ext.Msg.alert('Attenzione', 'Selezionare un documento!');
	}
}
function publishRecord(btn, jbtn) {
	if (globalOpt.document.id != null && globalOpt.document.id != 'null' && globalOpt.document.id != undefined) {
		if (jbtn ? jbtn.text().indexOf('pubblica') != -1 : btn.text.indexOf('pubblica') != -1) {

			Ext.MessageBox.show({
				title : 'Pubblica documento su <strong>' + (jbtn ? jbtn.attr("rel") : btn.id) + '</strong>',
				msg : 'Attenzione, confermare la pubblicazione del documento su <strong>' + (jbtn ? jbtn.attr("rel") : btn.id) + '</strong>?',
				width : 300,
				buttonText : {
					yes : 'sì',
					cancel : 'annulla',
					ok : 'ok'
				},
				buttons : Ext.MessageBox.OKCANCEL,
				fn : function(btncofirm, text) {
					if (btncofirm == 'ok') {
						Ext.Ajax.request({
							url : 'endPointManager.html',
							method : 'POST',
							nocache : true,
							params : {
								action : 'singleRecord',
								idRecord : globalOpt.document.id,
								idArchive : globalOpt.idArchivio,
								endPointManager : (jbtn ? jbtn.attr("rel") : btn.id)
							},
							success : function(result, request) {
								Ext.MessageBox.alert('Stato pubblicazione:', 'Documento pubblicato con successo!', function() {
									if (jbtn) {
										// jbtn.parent().children('a').html('pubblica
										// su <strong>' + jbtn.attr("rel") +
										// '</strong>');
										// jbtn.parent().children('span').attr('class',
										// 'not_published');
									} else {
										btn.setText('rimuovi da <strong>' + (jbtn ? jbtn.attr("rel") : btn.id) + '</strong>');
										btn.setIconClass('publisched');
									}
									reloadRecord('scheda');
								});
							},
							failure : function(result, request) {
								Ext.Msg.alert('Stato pubblicazione', 'Si è verificato un errore imprevisto durante la pubblicazione!');
							}
						});
					}
				},
				icon : Ext.MessageBox.QUESTION

			});

		} else {

			Ext.MessageBox.show({
				title : 'Rimuovi documento da <strong>' + (jbtn ? jbtn.attr("rel") : btn.id) + '</strong>',
				msg : 'Attenzione, confermare la rimozione del documento da <strong>' + (jbtn ? jbtn.attr("rel") : btn.id) + '</strong>?',
				width : 300,
				buttonText : {
					yes : 'sì',
					cancel : 'annulla',
					ok : 'ok'
				},
				buttons : Ext.MessageBox.OKCANCEL,
				fn : function(btnconfirm, text) {
					if (btnconfirm == 'ok') {
						Ext.Ajax.request({
							url : 'endPointManager.html',
							method : 'POST',
							nocache : true,
							params : {
								action : 'singleRecordRemove',
								idRecord : globalOpt.document.id,
								idArchive : globalOpt.idArchivio,
								endPointManager : (jbtn ? jbtn.attr("rel") : btn.id)
							},
							success : function(result, request) {
								Ext.MessageBox.alert('Stato pubblicazione:', 'Documento rimosso con successo!', function() {
									if (jbtn) {
										// jbtn.parent().children('a').html('pubblica
										// su <strong>' + jbtn.attr("rel") +
										// '</strong>');
										// jbtn.parent().children('span').attr('class',
										// 'not_published');
									} else {
										btn.setText('pubblica su <strong>' + btn.id + '</strong>');
										btn.setIconClass('not_publisched');
									}
									reloadRecord('scheda');
								});
							},
							failure : function(result, request) {
								Ext.Msg.alert('Stato pubblicazione', 'Si è verificato un errore imprevisto durante la rimozione della pubblicazione!');
							}
						});
					}
				},
				icon : Ext.MessageBox.QUESTION

			});

		}
	} else {
		Ext.Msg.alert('Attenzione', 'Selezionare un documento!');
	}
}

function endpointPublisher(id) {

	var obj = $(id);
	var scheda = obj.parents('#scheda');
	if (globalOpt.endpoints) {
		if (globalOpt.document.id != null && globalOpt.document.id != 'null' && globalOpt.document.id != undefined) {
			$.each(globalOpt.endpoints, function(key, value) {
				Ext.Ajax.request({
					url : 'endPointManager.html',
					method : 'POST',
					nocache : true,
					params : {
						action : 'checkPublished',
						idRecord : globalOpt.document.id,
						idArchive : globalOpt.idArchivio,
						endPointManager : value.id
					},
					success : function(result, request) {
						var line = $('<li><span></span><a href="#s"></a></li>');
						var published = Ext.util.Format.trim(result.responseText);
						if (published == 'true' || published == true) {
							line.children('span').addClass('published');
							line.children('a').html('rimuovi da <strong>' + value.id + '</strong>');
							$('#sezioneAdmin', scheda).css("background-color", "#80FF80");
							$('#sezioneAdmin', scheda).css("border-color", "#80FF80");
							$('.mustPublished', obj).show();
						} else {
							line.children('span').addClass('not_published');
							line.children('a').html('pubblica su <strong>' + value.id + '</strong>');
						}
						line.children('a').attr("rel", value.id);
						line.children('a').click(function() {
							publishRecord(null, $(this));
						});
						obj.append(line);
					},
					failure : function(result, request) {
						item.setDisabled(true);
					}
				});
			});

		}
	}
}
function endpointPublisherBPR(id, status) {
	if (status != "draft") {
		var obj = $(id);
		var scheda = obj.parents('#scheda');
		if (globalOpt.endpoints) {
			if (globalOpt.document.id != null && globalOpt.document.id != 'null' && globalOpt.document.id != undefined) {
				$.each(globalOpt.endpoints, function(key, value) {
					Ext.Ajax.request({
						url : 'endPointManager.html',
						method : 'POST',
						nocache : true,
						params : {
							action : 'checkPublished',
							idRecord : globalOpt.document.id,
							idArchive : globalOpt.idArchivio,
							endPointManager : value.id
						},
						success : function(result, request) {
							var line = $('<li><span></span><a href="#s"></a></li>');
							var published = Ext.util.Format.trim(result.responseText);
							if (published == 'true' || published == true) {
								line.children('span').addClass('published');
								line.children('a').html('rimuovi da <strong>' + value.id + '</strong>');
								$('#sezioneAdmin', scheda).css("background-color", "#80FF80");
								$('#sezioneAdmin', scheda).css("border-color", "#80FF80");
								$('.mustPublished', obj).show();
							} else {
								line.children('span').addClass('not_published');
								line.children('a').html('pubblica su <strong>' + value.id + '</strong>');
							}
							line.children('a').attr("rel", value.id);
							line.children('a').click(function() {
								publishRecord(null, $(this));
							});
							obj.append(line);
						},
						failure : function(result, request) {
							item.setDisabled(true);
						}
					});
				});

			}
		}
	}
}
//
// function checkPublished(btn) {
// if (btn != null && btn != 'undefined' && btn != undefined) {
// btn.setDisabled(false);
// if (globalOpt.document.id != null && globalOpt.document.id != 'null' &&
// globalOpt.document.id != undefined) {
// btn.menu.items.each(function(item) {
// Ext.Ajax.request({
// url : 'endPointManager.html',
// method : 'POST',
// nocache : true,
// params : {
// action : 'checkPublished',
// idRecord : globalOpt.document.id,
// idArchive : globalOpt.idArchivio,
// endPointManager : item.id
// },
// success : function(result, request) {
// var published = Ext.util.Format.trim(result.responseText);
// if (published == 'true' || published == true) {
// item.setIconClass('publisched');
// item.setText('rimuovi da <strong>' + item.id + '</strong>');
// item.setDisabled(false);
// } else {
// item.setIconClass('not_publisched');
// item.setText('pubblica su <strong>' + item.id + '</strong>');
// item.setDisabled(false);
// }
// },
// failure : function(result, request) {
// item.setDisabled(true);
// }
// });
// });
// }
// }
// }
function highLightXML() {
	dp.SyntaxHighlighter.HighlightAll('codeXML');
} 
function highlightRecord(id_record) {
	if (!id_record) {
		id_record = globalOpt.document.id;
	}
	$(".linkToHiglightHighlighted").removeClass("linkToHiglightHighlighted");
	$(".linkToHiglight_" + id_record).parent("div").addClass("linkToHiglightHighlighted");
}
function setExternalLink(obj) {
	obj.find('a').each(function() {
		if ($(this).attr("href") && $(this).attr("href").indexOf("http") == 0) {
			$(this).attr("target", "_blank");
		}
	});
	obj.find('img').each(function() {
		// alert($(this).attr("src"))
		if ($(this).attr("src") && $(this).attr("src").indexOf("\"") == 0) {
			$(this).attr("src", $(this).attr("src").replace(/"/gi, ""));
		}
	});
}
function mainLoadCallback(obj) {
	$(".relazioni", obj).each(function() {
		gestRelazioni($(this));
	});
	gestRelated(obj);
	setExternalLink(obj);
	gestCommenti(obj);
	gestListActions(obj);
}

function gestListActions(obj, anode) {
	var node = anode ? anode : "ul";

	if (globalOpt.editOn) {
		obj.find(node + ".sortnodes").each(function() {
			var ele = $(this);
			ele.find('li').prepend('<font class="ui-icon ui-icon-arrowthick-2-n-s"></font>');
			ele.sortable({
				forcePlaceholderSize : true,
				opacity : 0.6,
				items : 'li',
				stop : function(event, ui) {
					ele = ui.item;
					var ul = ele.parent();
					if (ul.find("a#salvaOrdineAnchor").length == 0) {
						var toAppend = $('<div style="margin-top:10px"><span class="ui-icon ui-icon-circle-check fll"></span><a id="salvaOrdineAnchor" href="#a">salva nuovo ordine</a><span class="loadingSpan"></span></div>');
						toAppend.find("a").click(function() {
							salvaOrdine(ul);
						});
						ul.append(toAppend);
					}
				},
				placeholder : 'ui-state-highlight'
			});
		});
		obj.find(node + ".doTrash").find('li').each(function() {
			var ele = $(this);
			var anchor = $('<a href="#nn" class="ui-icon ui-icon-trash flr" title="elimina relazione" style="width:15px;"></a>');
			anchor.click(function() {
				deleteRelationFromXpath({
					'dest' : anchor,
					'xpath' : $(this).parent().attr("data-xpath"),
					'idRecord' : globalOpt.document.id
				});
			});
			ele.prepend(anchor);
		});
		obj.find(node + ".doErase").find('li').each(function() {
			var ele = $(this);
			var anchor = $('<a href="#nn" class="ui-icon ui-icon-trash flr" title="elimina documento" style="width:15px;"></a>');
			anchor.click(function() {
				deleteElement({
					'dest' : anchor,
					'xpath' : $(this).parent().attr("data-xpath"),
					'idRecord' : $(this).parent().find('input[name=idRecord]').attr("value")
				});
			});
			ele.prepend(anchor);
		});
		obj.find(node + ".doTrashRel").find('li').each(function() {
			var ele = $(this);
			var anchor = $('<a href="#nn" class="ui-icon ui-icon-trash flr" title="elimina relazione" style="width:15px;"></a>');
			anchor.click(function() {
				deleteRelation({
					'dest' : anchor,
					'relationType' : $(this).parent().attr("data-relationType"),
					'idRecord_relations' : $(this).parent().find('input[name=idRecord]').attr("value"),
					'idRecord' : globalOpt.document.id
				});
			});
			ele.prepend(anchor);
		});
		obj.find(node + ".doModify").find('li').each(function() {
			var ele = $(this);
			var anchor = $('<a href="#nn" class="ui-icon ui-icon-pencil flr" title="modifica il documento" style="width:15px;"></a>');
			ele.prepend(anchor);
		});
	}
	obj.find(node + ".doScheda").find('li').each(function() {
		var ele = $(this);
		var anchor = $('<a href="#nn" class="ui-icon ui-icon-document flr" title="scheda del documento" style="width:15px;"></a>');
		anchor.click(function() {
			eval($(this).parent().find('input[name=preview]').val());
		});
		ele.prepend(anchor);
	});
}
function gestRelazioni(obj) {
	var relationType = obj.find('input').val();
	var inverse = obj.find('input[name=inverse]').val();
	var doTrash = true;
	if (obj.hasClass("doNotTrash")) {
		doTrash = false;
	}

	callAjax({
		success : function(jsonData) {
			jsonData = $.parseJSON(jsonData);
			jsonData = jsonData['data'];
			if (jsonData.length == 0) {
				$(".elementiAssociati", obj).parent('div').hide();
			} else {
				// $("#elementiAssociati").parent('div').show();
			}
			for ( var i = 0; i < jsonData.length; i++) {
				var aData = jsonData[i];
				aData.title = aData.title.replace(/openRecord/gi, 'previewDoc');
				var ele = $("<li style=\"border-bottom:1px solid #ededed;padding:2px;border-top:0; \"  id=\"li_relations_" + aData.id + "\">" + "<span class=\"ui-icon ui-icon-document fll\"></span>" + aData.archiveLabel + " - " + aData.title + "</li>");
				var idRecord_relations = aData.id;
				if (globalOpt.editOn && doTrash) {
					var anchor = $('<a href="#nn" class="ui-icon ui-icon-trash flr" onclick="return deleteRelation({\'dest\' : null,\'relationType\' : ' + relationType + ',\'idRecord_relations\' : ' + idRecord_relations + ',\'idRecord\' : ' + globalOpt.document.id + ',\'inverse\' : ' + inverse + '});" title="elimina relazione" style="width:15px;">' + relationType + '-' + aData.id + '-' + globalOpt.document.id + '</a>');
					ele.prepend(anchor);
				}
				$(".elementiAssociati", obj).append(ele);
			}
		},
		loadingTarget : $(".elementiAssociatiLoad", obj),
		url : 'ajax/loadLastOccurences.html',
		type : 'POST',
		async : true,
		data : {
			'resultFormat' : [ 'id', 'title', 'date', 'description' ],
			'limit' : '999',
			'idRelation' : $('input:first', obj).attr("value"),
			'allRelation' : $('input[name=\'allRelation\']', obj).attr("value"),
			'action' : 'json_data',
			'department' : top.Application['user'].dipartimento,
			'idRecord' : $('input[name=\'thisRecordId\']', obj).length ? $('input[name=\'thisRecordId\']', obj).attr("value") : globalOpt.document.id
		}
	});
}
function gestRelated(obj) {
	if (globalOpt.editOn) {
		$("#aggiungiAnnotazione", obj).click(function() {
			loadScripts({
				base : 'application/jsbusiness',
				scripts : [ "annotazione" ]
			});
			addRecord(globalOpt.document.archive, "annotazione", "annotazioni", null, annotazioneInsertCallback, "annotazione");
		});

	}
	$("#aggiungiInfoAtto", obj).click(function() {
		loadScripts({
			base : 'application/jsbusiness',
			scripts : [ "infoAtto" ]
		});
		addRecord($("#attoIdArchive", obj).text(), "infoAtto", "infoAtto", null, infoAttoInsertCallback, null, true);
		globalOpt.document.lastAtto = null;

	});
}

function gestCommenti(obj) {
	if (globalOpt.editOn) {
		$("#aggiungiCommento", obj).click(function() {
			addAvalue($("#commenti", obj).html(), globalOpt.document.jsonMap.configurazioni['fixedvaluesprefix'] + '/ods:note[@rdf:datatype=\'http://lod.xdams.org/ods/unitaOrganizzativa.rdf/' + top.Application['user'].dipartimento + '\']/text(cdata)', 'commento', 'rich', globalOpt.document.jsonMap.configurazioni['fixedvaluesprefix']);
		});
	}
	$("#commenti", obj).each(function() {
		var commento = getJvalue(globalOpt.document.jsonData, globalOpt.document.jsonMap.configurazioni.fixedvaluesprefix.replace(/\/rdf:RDF\//, '') + '£ods:note[@rdf:datatype=\'http://lod.xdams.org/ods/unitaOrganizzativa.rdf/' + top.Application['user'].dipartimento + '\']£#text', '£');
		$(this).html(commento);
	});
	$("#altricommenti", obj).each(function() {
		var elencoDipartimenti = listDipartimenti();
		for ( var a = 0; a < elencoDipartimenti.length; a++) {
			value = elencoDipartimenti[a];
			if (value.code != top.Application['user'].dipartimento) {
				var commento = getJvalue(globalOpt.document.jsonData, globalOpt.document.jsonMap.configurazioni.fixedvaluesprefix.replace(/\/rdf:RDF\//, '') + '£ods:note[@rdf:datatype=\'http://lod.xdams.org/ods/unitaOrganizzativa.rdf/' + value.code + '\']£#text', '£');
				if (commento) {
					$(this).append("<li style=\"padding:10px\"><b>" + value.label + "</b> " + commento + "</li>");
				}
			}
		}
	});

}

var previewWin;
function previewDoc(id, idArchivio, alternativeIdArchive, archiveLabel) {

	var originalArchive = 0;
	if (globalOpt.document && globalOpt.document.archive) {
		originalArchive = globalOpt.document.archive;
	}
	if (typeof idArchivio == 'boolean') {
		idArchivio = alternativeIdArchive;
	}

	if (previewWin != null) {
		previewWin.destroy();
		previewWin = null;
	}

	previewWin = new Ext.Window({
		height : 300,
		width : 400,
		resizable : true,
		autoScroll : true,
		id : 'previewRecord',
		title : 'anteprima del documento',
		listeners : {
			'afterrender' : function(elemento) {
				var div_dest = 'inj_previewRecord';
				if (id != null && id != 'null') {
					var what = 'documentResults_' + idArchivio;
					if (!globalOpt.document[what]) {
						globalOpt.document[what] = {};
					}

					if (!top['documentCache']) {
						top['documentCache'] = {};
					}
					if (!top['documentCache'][idArchivio] || !top['documentCache'][idArchivio][what]) {
						top['documentCache'][idArchivio] = {};
						top['documentCache'][idArchivio][what] = {};
					}
					
					
					
					/*
					 * carico il template associato alla vista
					 * (scheda|edit|etc.)
					 */
					if (!top['documentCache'][idArchivio][what].template) {
						$.ajax({
							url : 'ajax/documentalRecord.html?idArchive=' + idArchivio + '&action=' + what.substring(0, what.lastIndexOf("_")) + "Template",
							success : function(htmlTemplate) {
								if (Ext.isIE) {
									htmlTemplate = htmlTemplate.replace(/<!\[CDATA\[/gi, '');
									htmlTemplate = htmlTemplate.replace(/\]\]>/gi, '');
								}
								top['documentCache'][idArchivio][what].template = htmlTemplate;
							},
							async : false,
							cache : true
						});
					}
					// alert('2');
					/* estraggo dal template i tag script */
					if (!top['documentCache'][idArchivio][what].scriptTemplate) {
						htmlTemplateScript = "";
						regex = /<script\ .*?>((?:.|[\n|\r])*?)<\/script>/ig;
						while (match = regex.exec(top['documentCache'][idArchivio][what].template)) {
							htmlTemplateScript += match[1] + "\n";
						}
						top['documentCache'][idArchivio][what].template = (top['documentCache'][idArchivio][what].template).replace(regex, '');
						top['documentCache'][idArchivio][what].scriptTemplate = htmlTemplateScript;
					}
					/* produco il json del record */
					// alert('3');
					$.ajax({
						success : function(json_data) {
							globalOpt.document.jsonData = json_data;
						},
						cache : false,
						async : false,
						url : 'ajax/documentalRecord.html?idArchive=' + idArchivio + '&action=json_data&idRecord=' + id
					});
					// alert('4');
					var schedaJq = $(top['documentCache'][idArchivio][what].template);
					eval(top['documentCache'][idArchivio][what].scriptTemplate);
					doInjection({
						target : schedaJq,
						jsonData : globalOpt.document.jsonData
					});
					schedaJq.find("input[name='thisRecordId']").attr("value", id);
					if(typeof toDea != null && typeof toDea != "undefined")
						$('#' + div_dest).html($(toDea(schedaJq.html())));
					else
						$('#' + div_dest).html(schedaJq.html());
					mainLoadCallback($('#' + div_dest));
					executeClassToFunction($('#' + div_dest));
					// alert('5');
					globalOpt.document[what].output = schedaJq.html();

					/*top['documentCache'][idArchivio][what].template = null;
					globalOpt.document[what].scriptTemplate = null;
					*/
					globalOpt.document.jsonData = null;
				}
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
		html : '<div id="inj_previewRecord"></div>',
		bbar : [ alternativeIdArchive ? {
			id : 'apri_record',
			// iconCls:'icon-ok',
			text : 'apri la scheda',
			handler : function() {
				openRecord(id, idArchivio, alternativeIdArchive, archiveLabel);
				previewWin.destroy();
			}
		} : '', '<span id="loadingSpan"></span>', '->', {
			id : 'annulla',
			disabled : false,
			text : 'chiudi',
			handler : function() {
				previewWin.destroy();
			}
		} ]

	});
	previewWin.show();

}

function loadDocumentForSearchResults(id, idArchivio, div_dest, panel_id, words) {
	var originalId = globalOpt.document.id;

	if (id != null && id != 'null') {
		var what = 'documentResults_' + idArchivio;

		if (!globalOpt.document[what]) {
			globalOpt.document[what] = {};
		}
		if (!top['documentCache']) {
			top['documentCache'] = {};
		}
		if (!top['documentCache'][idArchivio] || !top['documentCache'][idArchivio][what]) {
			top['documentCache'][idArchivio] = {};
			top['documentCache'][idArchivio][what] = {};
		}
		// alert('1');
		/* carico il template associato alla vista (scheda|edit|etc.) */
		if (!top['documentCache'][idArchivio][what].template) {
			$.ajax({
				url : 'ajax/documentalRecord.html?idArchive=' + idArchivio + '&action=' + what.substring(0, what.lastIndexOf("_")) + "Template",
				success : function(htmlTemplate) {
					if (Ext.isIE) {
						htmlTemplate = htmlTemplate.replace(/<!\[CDATA\[/gi, '');
						htmlTemplate = htmlTemplate.replace(/\]\]>/gi, '');
					}
					top['documentCache'][idArchivio][what].template = htmlTemplate;
				},
				async : false,
				cache : true
			});
		}
		// alert('2');
		/* estraggo dal template i tag script */
		if (!top['documentCache'][idArchivio][what].scriptTemplate) {
			htmlTemplateScript = "";
			regex = /<script\ .*?>((?:.|[\n|\r])*?)<\/script>/ig;
			while (match = regex.exec(top['documentCache'][idArchivio][what].template)) {
				htmlTemplateScript += match[1] + "\n";
			}
			top['documentCache'][idArchivio][what].template = (top['documentCache'][idArchivio][what].template).replace(regex, '');
			top['documentCache'][idArchivio][what].scriptTemplate = htmlTemplateScript;
		}
		/* produco il json del record */
		// alert('3');
		$.ajax({
			success : function(json_data) {
				globalOpt.document.jsonData = json_data;
			},
			cache : false,
			async : false,
			url : 'ajax/documentalRecord.html?idArchive=' + idArchivio + '&action=json_data&idRecord=' + id
		});
		// alert('4');
		var schedaJq = $(top['documentCache'][idArchivio][what].template);
		schedaJq.find("input[name='thisRecordId']").attr("value", id);
		eval(top['documentCache'][idArchivio][what].scriptTemplate);
		doInjection({
			target : schedaJq,
			jsonData : globalOpt.document.jsonData
		});
		if(typeof toDea != null && typeof toDea != "undefined")
			$('#' + div_dest).html($(toDea(schedaJq.html())));
		else
			$('#' + div_dest).html(schedaJq.html());
			 
		mainLoadCallback($('#' + div_dest));
		executeClassToFunction($('#' + div_dest));
		// alert('5');
		if (words != null && words != undefined && words != 'null' && words != '') {
			var wordsArray = words.split("~");
			for ( var x = 0; x < wordsArray.length; x++) {
				var singleWord = wordsArray[x];
				singleWord = singleWord.replace(/"/g, '');
				var hasAccent = findAccent(singleWord);
				if (singleWord.indexOf('*') != -1) {
					singleWord = singleWord.replace('*', '');
					$('#' + div_dest).highlight(singleWord, {
						'wordsOnly' : false
					});
					$('#span_' + div_dest).highlight(singleWord, {
						'wordsOnly' : false
					});
					if (hasAccent) {
						$('#' + div_dest).highlight(removeDiacritics(singleWord), {
							'wordsOnly' : false
						});
						$('#span_' + div_dest).highlight(removeDiacritics(singleWord), {
							'wordsOnly' : false
						});
					}
				} else {
					if (!isNaN(singleWord)) {
						$('#' + div_dest).highlight(removeDiacritics(singleWord), {
							'wordsOnly' : true
						});
					} else {
						$('#' + div_dest).highlight(singleWord);
						$('#span_' + div_dest).highlight(singleWord);
						if (hasAccent) {
							$('#' + div_dest).highlight(removeDiacritics(singleWord));
							$('#span_' + div_dest).highlight(removeDiacritics(singleWord));
						}
					}
				}

				// Ext.getCmp(panel_id).setTitle(highlightHTMLText(Ext.getCmp(panel_id).title,wordsArray[x],"highlight_class"));
			}
		}
		globalOpt.document[what].output = schedaJq.html();
	} else {
		$('#inj_scheda').html('<div class="scheda"><div class="sezione"><div class="head">&lt;--</div><h2>selezionare un documento</h2></div></div>');
	}
	globalOpt.document = {};
	globalOpt.document.id = originalId;
}

function openPostit(id, text, type) {
	var postit_font = '';
	var idArchive = globalOpt.idArchivio;
	var idUser = top.Application['user'].idUser;
	var idRecord = globalOpt.document.id;
	var idNote = id;
	var idDepartment = '';
	var noteTypes = '1';
	if (type == 'my') {
		postit_font = 'postit_y';
	} else {
		postit_font = 'postit_g';
		idDepartment = top.Application.user.dipartimento;
	}
	var postit = new Ext.Panel({
		id : 'postit',
		bodyCssClass : postit_font,
		height : 345,
		width : 319,
		html : '<textarea id="note_text" class="' + postit_font + '_textarea">' + text + '</textarea>',
		bbar : new Ext.Toolbar({
			/* height:'20', */
			autoHeight : true,
			autoScroll : true,
			buttonAlign : 'center',
			items : [ {
				text : 'salva e chiudi',
				disabled : false,
				handler : function() {
					var note_text = $('#note_text').val();
					if (Ext.util.Format.trim(note_text) != '') {
						Ext.Ajax.request({
							url : 'notesManager.html',
							method : 'POST',
							nocache : true,
							params : {
								action : 'modify_note',
								idNote : idNote,
								note_text : note_text,
								idUser : idUser,
								idArchive : idArchive,
								idRecord : idRecord,
								idDepartment : idDepartment,
								noteTypes : noteTypes
							},
							success : function(result, request) {
								var idNote = Ext.util.Format.trim(result.responseText);
								$('#href_note_' + idNote).remove();
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
				text : 'elimina e chiudi',
				disabled : false,
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
							$('#href_note_' + idNote).remove();

						},
						failure : function(result, request) {
							Ext.Msg.alert('Attenzione', 'Si è verificato un errore!');
						}
					});
					Ext.getCmp('postit_win').destroy();
				}
			}, '|', {
				text : 'annulla e chiudi',
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
//
// function openPublishWindow() {
// var singlePublishing = new Ext.Panel({
// title : 'Pubblicazione singola',
// layout : 'fit',
// id : 'singlePublishing',
// bodyStyle : 'padding:5px;',
// items : [ {
// xtype : 'fieldset',
// title : 'End Point disponibili',
// autoHeight : true,
// id : 'singlePublishingFieldset',
// defaultType : 'checkbox',
// items : []
// } ],
// bbar : new Ext.Toolbar({
// id : 'singlePublishing_edit_bbar',
// items : [ '->', {
// text : 'Pubblica',
// id : 'singlePublishing_edit_publish_button',
// handler : function() {
//
// }
// }, '|', {
// text : 'Rimuovi pubblicazione',
// id : 'singlePublishing_edit_depublish_button',
// handler : function() {
//
// }
// } ]
// })
// });
// var endpointRelationsStore = new Ext.data.GroupingStore({
// url : 'endPointManager.html',
// reader : new Ext.data.JsonReader({
// root : 'records',
// totalProperty : 'totalCount',
// id : 'id',
// // fields : [ 'id','archive','title','publish']
// fields : [
// // the 'name' below matches the tag name to read, except 'availDate'
// // which is mapped to the tag 'availability'
// {
// name : 'id',
// type : 'string'
// }, {
// name : 'archive',
// type : 'string'
// }, {
// name : 'title',
// type : 'string'
// }, {
// name : 'publish',
// type : 'bool'
// } ]
// }),
// groupDir : 'ASC',
// sortInfo : {
// field : 'id',
// direction : 'ASC'
// },
// groupField : 'archive',
// baseParams : {
// idArchive : globalOpt.idArchivio,
// action : 'getRelations',
// idRecord : globalOpt.document.id
// }
// });
// var checkColumn = new Ext.grid.CheckColumn({
// header : 'Pubblica',
// dataIndex : 'publish',
// width : 20
// });
// var endpointRelationsGrid = new Ext.grid.EditorGridPanel({
// id : 'endpointRelationsGrid_' + globalOpt.idArchivio,
// store : endpointRelationsStore,
// width : 550,
// height : 300,
// autoScroll : true,
// collapsible : false,
// enableColumnResize : false,
// enableColumnMove : false,
// enableColumnHide : false,
// animCollapse : false,
// stripeRows : true,
// iconCls : 'icon-grid',
// autoHeight : true,
// enableHdMenu : false,
// autoSizeColumns : true,
// autoSizeGrid : true,
// plugins : checkColumn,
// listeners : {
// 'afterrender' : function() {
// this.getStore().load();
// }
// },
// autoExpandColumn : 'title',
// columns : [ {
// id : 'title',
// header : 'Risorse collegate',
// flex : 1,
// dataIndex : 'title',
// editable : false
// }, checkColumn, {
// id : 'archive',
// dataIndex : 'archive',
// hidden : true,
// hideable : false
// }, {
// id : 'id',
// dataIndex : 'id',
// hidden : true,
// hideable : false
// } ],
// view : new Ext.grid.GroupingView({
// forceFit : true,
// groupTextTpl : '{[ values.rs[0].data["archive"] ]} ({[values.rs.length]}
// {[values.rs.length > 1 ? "elementi" : "elemento"]})'
// }),
// viewConfig : {
// forceFit : true
// },
// loadMask : {
// msg : 'caricamento in corso...',
// enable : true
// }
// });
// var multiPublishing = new Ext.Panel({
// title : 'Pubblicazione multipla',
// layout : 'fit',
// id : 'multiPublishing',
// bodyStyle : 'padding:5px;',
// items : [ {
// xtype : 'fieldset',
// title : 'End Point disponibili',
// autoHeight : true,
// id : 'multiPublishingFieldset',
// defaultType : 'checkbox',
// items : []
// } ],
// bbar : new Ext.Toolbar({
// id : 'multiPublishing_edit_bbar',
// items : [ '->', {
// text : 'Pubblica tutti i selezionati',
// id : 'multiPublishing_edit_publish_button',
// handler : function() {
//
// }
// }, '|', {
// text : 'Rimuovi dalla pubblicazione tutti i selezionati',
// id : 'multiPublishing_edit_depublish_button',
// handler : function() {
//
// }
// } ]
// })
// });
// var publishTabPanel = new Ext.TabPanel({
// id : 'publishTabPanel',
// closable : false,
// autoScroll : true,
// margins : '0 0 0 0',
// activeTab : 0,
// items : [ singlePublishing, multiPublishing ],
// listeners : {
// 'beforerender' : function() {
// Ext.Ajax.request({
// url : 'endPointManager.html',
// method : 'POST',
// nocache : true,
// params : {
// action : 'getEndPointList',
// idArchive : globalOpt.idArchivio
// },
// success : function(result, request) {
// var jsonData = Ext.util.JSON.decode(result.responseText);
// var singlePublishingFieldset = Ext.getCmp('singlePublishingFieldset');
// var multiPublishingFieldset = Ext.getCmp('multiPublishingFieldset');
// for ( var i = 0; i < jsonData.endpoints.length; i++) {
// var endpoint = jsonData.endpoints[i].endpoint;
// singlePublishingFieldset.add({
// checked : true,
// boxLabel : 'pubblica su <strong>' + endpoint + '</strong> <img
// src="img/archive_img/notpublished.gif"/>',
// labelSeparator : '',
// fieldLabel : '',
// width : 300,
// endpoint : endpoint,
// name : 'endpoint',
// disabled : true,
// listeners : {
// 'beforerender' : function() {
// checkPublishedCk(this);
// }
// }
// });
// multiPublishingFieldset.add({
// checked : true,
// boxLabel : 'pubblica su <strong>' + endpoint + '</strong> <img
// src="img/archive_img/notpublished.gif"/>',
// labelSeparator : '',
// fieldLabel : '',
// width : 300,
// endpoint : endpoint,
// name : 'endpoint',
// disabled : true,
// listeners : {
// 'beforerender' : function() {
// checkPublishedCk(this);
// }
// }
// });
// }
// singlePublishingFieldset.doLayout();
// multiPublishingFieldset.add(endpointRelationsGrid);
// multiPublishingFieldset.doLayout();
// },
// failure : function(result, request) {
// Ext.Msg.alert('Attenzione!', 'Nessun EndPoint disponibile!');
// }
// });
// }
// }
// });
// var publishWindow = new Ext.Window({
// title : 'Console di pubblicazione su EndPoint',
// id : 'publishWindow',
// closable : true,
// width : 600,
// height : 500,
// modal : true,
// plain : true,
// layout : 'fit',
// items : [ publishTabPanel ]
// });
// publishWindow.show();
// }
