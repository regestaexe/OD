var rdfConf = generateRDFConf();

function doInjection(opts) {
	var delim = '_';
	var scheda = opts.target;
	var mappa = {};
	var emptyValue = opts.ifEmpty != null ? opts.ifEmpty : '<span class=if-empty-remove>@empty@</span>';

	for (key in globalOpt.document.jsonMap) {
		mappa = selectFromJson(key, scheda, mappa, delim, opts, globalOpt.document.jsonMap, emptyValue);
	}
	var schedaString = $.format(scheda.html().replace(/value=\{([^}]+)\}/g,'value="{$1}"'), mappa);
	var schedaJq = $(schedaString);

	$('.if-empty-default:contains("@empty@")', schedaJq).each(function() {
		$(this).removeClass("if-empty-default");
		$(this).html($(this).find("span:first").html());
	});
	$('.if-empty-default', schedaJq).each(function() {
		$(this).find("span:first").remove();
	});
	$('.if-empty-next-default', schedaJq).each(function() {
		if ($(this).children("*").length != 0) {
			$(this).next("span").remove();
		}
	});
	$('.if-empty-remove:contains("@empty@")', schedaJq).each(function() {
		$(this).removeClass("if-empty-remove");
		$(this).remove();
	});
	$('.if-empty-remove-sezione', schedaJq).each(function() {
		if ($.trim($(this).find('*[class!="head"]').text()) == '') {
			$(this).remove();
		}
	});
	// console.debug(schedaJq.html());
	scheda.html(schedaJq); 
}
function selectFromJson(key, scheda, mappa, delim, opts, localValueMap, emptyValue) {
	var caso = localValueMap[key];
	if (typeof (caso) == 'string') {
		// console.debug('\t\t' + key + ' typeof STRING');
		mappa[key] = getJpath(opts, localValueMap[key], emptyValue);
		// console.debug('\t\t' + key + ' aaaaaaaaaaaaa '+localValueMap[key]);
		// verifico che nel codice html siano ancora presenti marcatori con
		// pipeline da sostituire

		var regex = new RegExp("({)(" + key + delim + ".*?)(})", "g");
		// var regex = regexPat.compile("({)(" + key + delim + ".*?)(})", "g");
		// console.debug('\t\t' + regex);
		ambito = scheda.html();

		while (match = regex.exec(ambito)) {

			// rivedere qui
			var segnaposto = match[2];

			var metodi = segnaposto.substring(segnaposto.indexOf(delim) + 1).split(delim);
			var etichetta = mappa[segnaposto.substring(0, segnaposto.indexOf(delim))];
			// console.debug("etichetta " +etichetta)
			// console.debug("segnaposto " + segnaposto)
			var out = etichetta;
			for (m in metodi) {
				var metodo = metodi[m] + '';
				try {
					out = eval(metodo + '(out)');
				} catch (e) {
					// console.debug('WARNING: no ' + metodo + '() method
					// found.');
				}
			}
			mappa[segnaposto] = out;
			// mappa[segnaposto+"-xpath"] = etichetta;
		}
	} else {
		// console.debug('\t\t' + key + ' typeof OBJECT');
		// utilizziamo .{lex-container}, cioè la convenzione è
		// .nome-container, da gestire in jquery
		$(".container-" + key, scheda).each(function() {
			// TODO: sostituire con un unescape di qualche tipo
			var ambito = $(this).html().replace(/%7B/g, '{').replace(/%7D/g, '}');
			var dest = "";
			var sublocalValueMap = localValueMap[key];

			var nodes = [];
			var numNodes = 0;
			try {
				nodes = myJpath(opts, sublocalValueMap["container"] + "/*/" + sublocalValueMap["valori"]);
				numNodes = nodes.length;
				if (numNodes == 0) {
					nodes = myJpath(opts, sublocalValueMap["container"] + sublocalValueMap["valori"]);
					numNodes = nodes.length;
				}
			} catch (e) {
				nodes = [];
				numNodes = 0;
			}
			if (numNodes == 0) {
				dest = emptyValue;
			}
			for ( var i = 0; i < numNodes; i++) {
				subMappa = {};
				var itemName = numNodes == 1 ? sublocalValueMap["container"] + sublocalValueMap["valori"] : sublocalValueMap["container"] + "[" + i + "]" + sublocalValueMap["valori"];
				subMappa[key] = getJpath(opts, itemName, emptyValue);
				subMappa['position'] = (i + 1);
				subMappa['xpath'] = numNodes == 1 ? sublocalValueMap["container"] + sublocalValueMap["valori"] : sublocalValueMap["container"] + "[" + (i + 1) + "]" + sublocalValueMap["valori"];
				var regex = new RegExp("({)(" + key + delim + ".*?)(})", "g");
				while (match = regex.exec(ambito)) {
					// rivedere qui
					var segnaposto = match[2];
					var etichetta;
					// ricavo la lista dei metodi da utilizzare in pipeline
					etichetta = subMappa[key];
					var metodi = segnaposto.substring(segnaposto.indexOf(delim) + 1).split(delim);
					var out = etichetta;
					for (m in metodi) {
						var metodo = metodi[m] + '';
						try {
							out = eval(metodo + '(out)');
						} catch (e) {
							// console.debug('WARNING: no ' + metodo + '()
							// method found.');
						}
					}
					subMappa[segnaposto] = out;
				}
				temp = $.format(ambito, subMappa);
				if (temp)
					dest += temp;
			}
			$(this).html(dest);

		});
	}
	return mappa;
}

function myJpath(opts, path) {
	if (path.indexOf("[@") != -1 || path.indexOf("[child::") != -1) {

		return [ getJvalue(opts.jsonData, path, null) ];

	} else {
		return jpath(opts.jsonData, path);
	}
}

function getJpath(opts, map, emptyValue) {
	var val = null;
	try {
		val = myJpath(opts, map)[0];
	} catch (e) {
		val = null;
	}
	if (!val) {
		val = emptyValue;
	}
	try {
		// val = val.replace(/&quot;/g, "&quotaaaaa;");
		// DIEGO FIX
		if (opts.escapeDblQuote) {
			val = val.replace(/"/g, "&quot;");
		}
		// val = val.replace(/&quotaaaaa;/g, "\"");

		val = val.replace(/href=\"http/g, "target=\"_blank\" href=\"http");

		val = val.replace(/&gt;/g, ">");
		val = val.replace(/&lt;/g, "<");

	} catch (e) {
	}
	return val;
}
function afterLastSharp(arg) {
	arg = arg + '';
	return arg.substring(arg.lastIndexOf('#') + 1);
}
function readMore(arg) {
	arg = arg + '';
	if (arg.length > 150)
		arg = '<div style="height:50px;overflow:hidden;">' + arg + '</div><div style="text-align:right"><a onclick="$(this).parent().prev(\'div\').attr(\'style\',\'\');$(this).remove()" style="cursor:pointer">leggi tutto</a></div>';
	return arg;

}
function beforeDot(arg) {
	arg = arg + '';
	if (arg.lastIndexOf('.') > 0) {
		arg = arg.substring(0, arg.indexOf('.'));
	}
	return arg;
}
function beforeLastComma(arg) {
	arg = arg + '';
	if (arg.lastIndexOf(',') > 0) {
		arg = arg.substring(0, arg.lastIndexOf(','));
	}
	return arg;
}
function beforeLastSharp(arg) {
	arg = arg + '';
	if (arg.lastIndexOf('#') > 0) {
		arg = arg.substring(0, arg.lastIndexOf('#'));
	}
	return arg;
}
function afterLastQuestionmark(arg) {
	arg = arg + '';
	if (arg.lastIndexOf('?') > 0) {
		arg = arg.substring(arg.lastIndexOf('?') + 1);
	}
	return arg;
}
function afterLastSlash(arg) {
	arg = arg + '';
	return arg.substring(arg.lastIndexOf('/') + 1);
}

function uppercase(arg) {
	arg = arg + '';
	return arg.toUpperCase();
}

function luceneEscape(arg) {
	arg = arg + '';
	arg = arg.replace(/:/g, '\\:');
	return arg;
}
function jsEscape(arg) {
	arg = arg + '';
	arg = escape(arg);
	return arg;
}

function tweetEscape(arg) {
	arg = arg + '';
	arg = arg.replace(/<(\/)?[a-zA-Z]+[^>]*>/gi, '');
	arg = arg.replace('\"', '&quot;');
	arg = jsEscape(arg);
	return arg;
}
function removePtag(arg) {
	if (arg.indexOf("<p") != -1 && arg.indexOf("</p>") != -1) {
		arg = arg.replace(/<p[^>]*>/gi, '');
		arg = arg.replace(/<\/p>/gi, '');
	}
	if (arg.indexOf("&lt;p") != -1 && arg.indexOf("&lt;/p&gt;") != -1) {
		arg = arg.replace(/&lt;p[^&gt;]*&gt;/gi, '');
		arg = arg.replace(/&lt;\/p&gt;/gi, '');
	}
	return arg;
}
function extractRoman(arg) {
	arg = arg.substring(arg.lastIndexOf('(') + 1);
	arg = arg.substring(0, arg.indexOf(' '));
	return arg;
}

function toLink(arg) {
	if (arg.indexOf("http://") == 0) {
		arg = '<a href="' + arg + '" target="_blank">' + arg + '</a>';
	}
	return arg;
}

function textToLink(arg) {
	if (arg && (arg.indexOf("http://") == 0 || arg.indexOf("https://") == 0)) {
		arg = arg.replace(/(http[s]*:\/\/[^\s]+)/gi, '<a href="$1" target="_blank">$1</a>');
	}
	return arg;
}
function visibilita(arg) {
	if (arg == 'external') {
		arg = 'disponibile per la pubblicazione';
	} else if (arg == 'internal') {
		arg = 'non disponibile per la pubblicazione';
	}
	return arg;
}
function status(arg) {
	if (arg == 'edited') {
		arg = 'completato';
	} else if (arg == 'draft') {
		arg = 'da verificare';
	}
	return arg;
}

function clearAnnotazioni() {
	var myDest = lastDest;
	var annotazioni = [];
	myDest.children('ol').find('li').each(function() {
		annotazioni.push($.trim($(this).text()));
	});
	if (annotazioni.length > 0) {
		var dataToSend = {
			dip : top.Application['user'].dipartimento,
			annotazioni : annotazioni
		};
		connection = $.ajax({
			cache : true,
			type : 'POST',
			data : dataToSend,
			url : 'annotazioni.html',
			async : true,
			beforeSend : function() {
				myDest.children('ol').empty();
				myDest.children('ol').append("<li><img src=\"img/ajax-loader.gif\" /></li>");
			},
			success : function(data) {
				myDest.children('ol').html(data);
				if (myDest.children('ol').find('li:not(:has(span.myDip))').length > 0) {
					var mostraTutti = $('<div style="margin-top: 10px;"><span class="ui-icon	  ui-icon-circle-arrow-s fll"></span><a href="#a" >mostra le annotazioni	  degli altri Dipartimenti</a><span class="loadingSpan"></span></div>');
					myDest.append(mostraTutti);
					gestListActions(myDest, 'ol');

					myDest.children('ol').children('li').each(function() {
						var afunction = $(this).find('input[name=aFunction]').val();
						$(".ui-icon-pencil", $(this)).click(function() {
							eval(afunction.replace(/null/g, '\'edit\''));
						});
						$(".deferred-ui-icon-pencil", $(this)).attr("href", 'javascript:' + afunction.replace(/null/g, '\'edit\'') + " ");
						$(".ui-icon-document", $(this)).click(function() {
							eval(afunction.replace(/'edit'/g, 'true'));
						});
					});

					mostraTutti.find('a').toggle(function() {
						$(this).parent().parent().find('li:not(:has(span.myDip))').slideDown();
						$(this).text('nascondi le annotazioni degli altri Dipartimenti');
						$(this).parent().find('span').addClass('ui-icon-circle-arrow-n');
						$(this).parent().find('span').removeClass('ui-icon-circle-arrow-s');
					}, function() {
						$(this).parent().parent().find('li:not(:has(span.myDip))').slideUp();
						$(this).text('mostra le annotazioni degli altri Dipartimenti');
						$(this).parent().find('span').removeClass('ui-icon-circle-arrow-n');
						$(this).parent().find('span').addClass('ui-icon-circle-arrow-s');
					});
				}
			}
		});
	}
}

function clearCommenti() {
	lastDest.find('li').hide();
	if (lastDest.find('li').length > 0) {
		var mostraTutti = $('<div style="margin-top: 10px;"><span class="ui-icon ui-icon-circle-arrow-s fll"></span><a href="#a"  >mostra i commenti degli altri Dipartimenti</a><span class="loadingSpan"></span></div>');
		lastDest.append(mostraTutti);
		mostraTutti.find('a').toggle(function() {
			$(this).parent().parent().find('li').slideDown();
			$(this).text('nascondi i commenti degli altri Dipartimenti');
			$(this).parent().find('span').addClass('ui-icon-circle-arrow-n');
			$(this).parent().find('span').removeClass('ui-icon-circle-arrow-s');
		}, function() {
			$(this).parent().parent().find('li').slideUp();
			$(this).text('mostra i commenti degli altri Dipartimenti');
			$(this).parent().find('span').removeClass('ui-icon-circle-arrow-n');
			$(this).parent().find('span').addClass('ui-icon-circle-arrow-s');
		});
	}
}

function addStyle(arg0, arg1) {
	// console.debug(lastDest);
	lastDest.find('*').css(arg0, arg1);
}

function formatItalian(arg) {
	if (arg && arg != '') {
		arg = arg.replace(/,/gi, "");
	}
	if (arg != '' && arg.length == 8) {
		arg = parseInt(arg.substring(6, 8), 10) + " " + getMonthsNames()[parseInt(arg.substring(4, 6), 10) - 1] + " " + arg.substring(0, 4);
	} else if (arg != '' && arg.length == 17) {
		dest = parseInt(arg.substring(6, 8), 10) + " " + getMonthsNames()[parseInt(arg.substring(4, 6), 10) - 1] + " " + arg.substring(0, 4);
		dest += " - ";
		dest += parseInt(arg.substring(15, 17), 10) + " " + getMonthsNames()[parseInt(arg.substring(13, 15), 10) - 1] + " " + arg.substring(9, 13);
		arg = dest;
	} else if (arg != '' && arg.length == 6) {
		arg = getMonthsNames()[parseInt(arg.substring(4, 6), 10) - 1] + " " + arg.substring(0, 4);
	}
	return arg;
}

var lastDest = null;

function insertHtml(stringa, test, result, value) {
	if ((stringa.indexOf(test) != -1) == result) {
		return value;
	}
	return "";
}

function simpleSelect() {
	var input = lastDest.children('input');
	// console.debug(lastDest);
	// console.debug(arguments);
	var select = $('<select></select>');
	select.attr("class", input.attr("class"));
	select.attr("name", input.attr("name"));
	for ( var a = 0; a < arguments.length; a++) {

		var val = arguments[a].split(":").length == 1 ? [ arguments[a], arguments[a] ] : arguments[a].split(":");
		select.append("<option value='" + val[0] + "' " + (val[0] == input.val() ? "selected=\"selected\"" : "") + ">" + val[1] + "</option>");
	}
	lastDest.empty();
	lastDest.append(select);
}

function selectValue(valore) {
	lastDest.find('input').each(function() {
		if ($(this).attr("value") == valore) {
			$(this).attr("checked", "checked");
			return;
		}
	});
	lastDest.find('select').each(function() {
		$(this).attr("value", valore);
	});
}
function executeClassToFunction(obj) {
	obj.find("*[class^='function']").each(function() {
		

		var funzione = $(this).attr("class");
		try {
			lastDest = $(this);
			funzione = funzione.substring(funzione.indexOf("-") + 1);
			var dest = eval(funzione);
			$(this).html(dest);
		} catch (e) {
			console.info('error ' + funzione);
		}
	});
	obj.find("*[data-function]").each(function() {
		try {
			lastDest = $(this);
			var functionSon = $.parseJSON($(this).attr('data-function'));
			var functionName = functionSon.shift();
			window[functionName].apply($(this), functionSon);
		} catch (e) {
			console.info('error ' + $(this).attr("class"));
		}
	});
	obj.find("*[class^='deferred-function']").each(function() {
		var funzione = $(this).attr("class");
		try {
			
			lastDest = $(this);
			funzione = funzione.substring(funzione.indexOf("-", funzione.indexOf("-") + 1) + 1);
			var dest = eval(funzione);
			$(this).html(dest);
		} catch (e) {
			console.info('error ' + funzione);
		}
	});
}
function buildStoredSearch() {
	var stored_search_container = new Ext.Panel({
		id : 'stored_search_container',
		renderTo : 'buildStoredSearchTarget',
		border : true,
		autoScroll : true,
		defaults : {
			style : 'margin-top:10px;margin-left:5px;margin-right:5px;margin-bottom:5px;'
		}

	});
	stored_search_container.load({
		url : 'searchBuilder_adv_search.html?action=load_stored_search&idRecord=' + globalOpt.document.id,
		nocache : true,
		text : 'Ricerca in corso...',
		timeout : 30,
		scripts : true
	});
}

function editStoredSearch() {
	Ext.getCmp('buttonSaveDocumental').disable();
	Ext.getCmp('buttonSaveDocumentalRapid').disable();
	if (!Ext.getCmp('buttonUpdateDocumental')) {
		Ext.getCmp('edit_tab_tab_bbar').insert(0, {
			text : 'Elabora ricerca',
			id : 'buttonUpdateDocumental',
			handler : function() {
				var results = Ext.getCmp('stored_search_container').items;
				parent.Ext.getCmp("tabSearchBuilder").show();
				top['Application'].searchpanels = results;
				top['Message']['Bus7'].fireEvent('modifyStoredSearch');
			}
		});
	}
}

function afterLastDash(arg) {
	arg = arg + '';
	return arg.substring(arg.lastIndexOf('-') + 1);
}
