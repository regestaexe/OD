function myCustomOnChangeHandler(inst) {
	// alert(inst)
	inst.getBody().innerHTML = cleanMceText(inst.getBody().innerHTML);
	return html;
}
function myCustomCleanup(type, value) {

	switch (type) {
	case "get_from_editor":
		// alert("Value HTML string: " + value);
		value = cleanMceText(value);
		// alert("Value HTML string: " + value);
		break;
	}
	return value;
}

function getMCEText(inst) {
	try {
		return inst.tinymce().getContent();
	} catch (e) {
		return inst.val();
	}
}

function getMCEremove(inst) {
	// console.debug(inst)
	try {
		inst.tinymce().remove();
	} catch (e) {
	}
}
function getMCEtriggerSave(inst) {
	try {
		inst.tinymce().save();
	} catch (e) {
	}
}

function emptyMCEvalues(inst) {
	inst.tinymce().getBody().innerHTML = '';
}

function cleanMceText(text) {
	text = text.replace(/<(!--)([\s\S]*)(--)>/gi, "");
	text = text.replace(/font-size:[^;]*;/gi, "");
	text = text.replace(/font-family:[^;]*;/gi, "");
	text = text.replace(/ ods:dummy="value"/gi, "");
	text = text.replace(/“/gi, "\"");
	text = text.replace(/”/gi, "\"");

	text = text.replace(/<table ([^>]*)width="[0-9]*"([^>]*)>/gi, "<table $1width=\"100%\"$2>");

	text = text.replace(/<table ([^>]*)border="[1-9]*"([^>]*)>/gi, "<table border=\"0\"$2>");

	// text = text.replace(/<a [^>]*urn:nir:regione.:[^>]*>([^>]*)<\/a>/gi,
	// "$1");
	text = text.replace(/’/gi, "'");

	text = text.replace(/<a href="#_([^"]*)">([^a]*)<\/a>/gi, "<sup>$2</sup>");

	text = text.replace(/<b( [^>]*|)>/gi, "<strong$1>");
	text = text.replace(/<\/b>/gi, "</strong>");

	text = text.replace(/<i([^m>]*)>/gi, "<em$1>");
	text = text.replace(/<\/i>/gi, "</em>");

	// text = text.replace(/<div[^>]*>/gi, "<p$1>");
	// text = text.replace(/<\/div>/gi, "</p>");

	/*
	 * if (text.startsWith('<p>') && text.endsWith('</p>')) { text =
	 * text.substring(3, text.length - 4); }
	 */
	return text;
}

function initSimpleMceEditor(obj) {

	obj.each(function() {
		var addSimpleSave = $(this).hasClass("addSimpleSave") != -1;
		var removeFullscreen = $(this).hasClass("removeFullscreen") != -1;
		var insertimage = $(this).hasClass("addInsertimage") != -1;
		var soppressed = $(this).hasClass("addSoppressed") != -1;
		var editTables = $(this).hasClass("addTables") != -1;
		var selectStyles = $(this).hasClass("addStyleControl") != -1;

		// var insertimage = false;

		var btt1 = (removeFullscreen ? "" : "fullscreen,") + "removeformat,bold,italic,underline,|,pasteword,|,search,replace,|,bullist,numlist,hr,sub,sup|,undo,redo";
		var btt2 = (insertimage ? "image,insertimage,|,justifyleft,justifycenter,justifyright,justifyfull," : "") + "indent,outdent," + "|";
		var btt3 = (selectStyles ? ",styleselect,blockquote" : "") + (soppressed ? ",backcolor,strikethrough,|" : "") + (addSimpleSave ? ",save" : "") + ",|,link,unlink,|,charmap,|,code";
		var btt4 = (editTables ? ",visualaid,tablecontrols" : "");

		if (btt2 == "|" || btt2 == "") {
			btt2 = btt3;
			btt3 = btt4;
		}
		if (btt2 == "|" || btt2 == "") {
			btt2 = btt3;
			btt3 = "";
		}

		$(this).tinymce({
			// Location of TinyMCE script
			script_url : 'js/tiny_mce/tiny_mce.js',

			// General options.
			paste_use_dialog : false,
			paste_auto_cleanup_on_paste : true,
			disk_cache : true,
			fix_nesting : true,
			force_p_newlines : true,
			forced_root_block : 'p',
			cleanup_callback : "myCustomCleanup",

			force_br_newlines : false,
			convert_urls : false,
			entity_encoding : "numeric",
			//
			plugins : "table,style,paste,searchreplace," + (insertimage ? "imagemanager,advimage," : "") + "" + (removeFullscreen ? "" : "fullscreen,") + "inlinepopups,nonbreaking,xhtmlxtras,template,advlist" + (addSimpleSave ? ",save" : ""),
			width : "100%",

			height : '300px',
			doctype : '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">',
			// Theme options
			theme_advanced_buttons1 : btt1,
			theme_advanced_buttons2 : btt2,
			theme_advanced_buttons3 : btt3,
			theme_advanced_buttons4 : btt4,
			// table_styles :
			// "titolo1=theader1;titolo2=header2;titolo3=header3",

			fullscreen_settings : {
				theme_advanced_buttons1 : (removeFullscreen ? "" : "fullscreen,") + "removeformat,bold,italic,underline,|,pasteword,|,search,replace,|,bullist,numlist,hr,sub,sup," + (insertimage ? "image,insertimage,|,justifyleft,justifycenter,justifyright,justifyfull," : "") + "indent,outdent,|,undo,redo,|",
				theme_advanced_buttons2 : (selectStyles ? "styleselect,blockquote," : "") + (soppressed ? "backcolor,strikethrough,|," : "") +  (addSimpleSave ? "save," : "") + "|,link,unlink,|,charmap,|,code," + (editTables ? "visualaid,tablecontrols," : "") + "|",
				theme_advanced_buttons3 : "",
				theme_advanced_buttons4 : ""
			},
			theme_advanced_toolbar_location : "top",
			theme_advanced_toolbar_align : "left",
			valid_elements : "blockquote[class],img[*],a[href|class],strong[class],b[class],div[style|align|class],br,p[style|class],hr,em[class],sup[class],sub[class],ul[style|class],li[class],ol[style|class|start|type],span[class|style],dfn[id|class|style|data-title|title|name],tr[*],td[*],table[*],tbody[*],th[*]",
			// button_tile_map : true,
			theme : "advanced",

			style_formats : [ {
				title : 'blocco ridotto',
				block : 'div',
				attributes : {
					'class' : 'testo_ridotto'
				}
			}, {
				title : 'blocco grande',
				block : 'div',
				attributes : {
					'class' : 'testo_grande'
				}
			}, {
				title : 'blocco super ridotto',
				block : 'div',
				attributes : {
					'class' : 'testo_superRidotto'
				}
			}/*
				 * ,{ title : 'blocco medio', block : 'div', attributes : {
				 * 'class' : 'testo_medio' } }
				 */, {
				title : 'blu',
				inline : 'span',
				attributes : {
					'class' : 'blu'
				}
			}, {
				title : 'rosso',
				inline : 'span',
				attributes : {
					'class' : 'rosso'
				}
			}, {
				title : 'blocco ridotto blu',
				block : 'div',
				attributes : {
					'class' : 'testo_ridotto_blu'
				}
			} /*
				 * ,{ title : 'blocco medio blu', block : 'div', attributes : {
				 * 'class' : 'testo_medio_blu' } }
				 */],

			language : "it",
			convert_fonts_to_spans : true,
			cleanup : true,
			fix_nesting : true,
			save_enablewhendirty : true,
			save_onsavecallback : "simpleSave",
			setup : function(ed) {
			},
			verify_html : true,
			theme_advanced_resizing : false,
			content_css : "css/tinyMCE.css?" + top.Application.today

		});
	});
}
function simpleSave() {
	saveDocument('editForm', function() {
		top.Ext.ods.msg('documento salvato con successo');
	}, function() {
	});
	$('#editForm').trigger('submit');
}
function initVerySimpleMceEditor(obj) {
	obj.each(function() {
		var removeFullscreen = $(this).hasClass("removeFullscreen") != -1;
		$(this).tinymce({
			// Location of TinyMCE script
			script_url : 'js/tiny_mce/tiny_mce.js',

			// General options.
			paste_use_dialog : false,
			paste_auto_cleanup_on_paste : true,
			disk_cache : true,
			fix_nesting : true,
			force_p_newlines : true,
			forced_root_block : 'p',
			cleanup_callback : "myCustomCleanup",
			force_br_newlines : true,
			convert_urls : false,
			entity_encoding : "numeric",
			plugins : (removeFullscreen ? "" : "fullscreen,") + "inlinepopups",
			width : "100%",
			doctype : '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">',
			// Theme options
			theme_advanced_buttons1 : "removeformat,bold,italic,|," + (removeFullscreen ? "" : "fullscreen,") + "code" + ",|,undo,redo",
			theme_advanced_buttons2 : "",
			theme_advanced_buttons3 : "",
			theme_advanced_buttons4 : "",
			theme_advanced_toolbar_location : "top",
			theme_advanced_toolbar_align : "left",

			valid_elements : "a[href],strong[class],b[class],div[align],br,p,hr,em,sup,sub,ul[style|class],li[class],ol",
			// button_tile_map : true,
			theme : "advanced",
			language : "it",
			convert_fonts_to_spans : true,
			cleanup : true,
			fix_nesting : true,
			setup : function(ed) {
			},

			verify_html : true,
			theme_advanced_resizing : false,
			content_css : "css/tinyMCE.css?" + top.Application.today

		});
	});

}
var lexWindow;

function addNormToMCE(ed) {
}

function scegliNorme(ed, obj) {
}
var currentEd = null;
