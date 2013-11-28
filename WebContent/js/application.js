var globalScript = {};
function loadApplicationScripts(scripts) {
	for ( var a = 0; a < scripts.length; a++) {
		var aScript = scripts[a];
		var aPath = (scriptsMap.prefix ? scriptsMap.prefix : '') + 'js/application/' + aScript + '.js?b' + top.Application.today;
 
		if ((top.Application && top.Application.user && top.Application.user.company == 1000000000) || !top.globalScript[aPath]) {
			$.ajax({
				async : false,
				url : aPath,
				dataType : 'text',
				error : function() {
					alert('error loading ' + aScript);
				},
				success : function(data) {
					$('head').append('<script type="text/javascript">' + data + "</script>");
					top.globalScript[aPath] = data;
				}
			});
		} else {
			$('head').append('<script type="text/javascript">' + top.globalScript[aPath] + "</script>");
		}

	}
}
function loadScripts(scriptsMap) {
	for ( var a = 0; a < scriptsMap.scripts.length; a++) {
		var aScript = scriptsMap.scripts[a];
		var aPath = (scriptsMap.prefix ? scriptsMap.prefix : "") + "js/" + scriptsMap.base + '/' + aScript + '.js?b' + top.Application.today;
 
		if ((top.Application && top.Application.user && top.Application.user.company == 1000000000) || !top.globalScript[aPath]) {
			// alert(aScript)
			$.ajax({
				async : false,
				url : aPath,
				dataType : 'text',
				error : function() {
					alert('error loading ' + aScript);
				},
				success : function(data) {
					$('head').append('<script type="text/javascript">' + data + "</script>");
					top.globalScript[aPath] = data;
				}
			});
		} else {
			$('head').append('<script type="text/javascript">' + top.globalScript[aPath] + "</script>");
		}
  	}
}
function closeLoading() {
	try {
		loadBar = false;
		Ext.MessageBox.hide();
	} catch (e) {
	}
}
$.ajaxSetup({
	error : function(e, xhr, settings, exception) {
		if (e.status == 601) {
			top.location.reload();
		} else {
			closeLoading();
			/*
			 * Ext.MessageBox.show({ title : 'Status', msg : 'Si è verificato un
			 * errore.', buttons : Ext.MessageBox.OK, icon :
			 * Ext.MessageBox.WARNING })
			 */
			// alert('')
			top.Ext.ods.errorMsg('si è verificato un errore');
			// alert(09)
		}
	}
});
if (Ext != null) {
	Ext.MessageBox.buttonText.yes = 'sì';
	Ext.MessageBox.buttonText.cancel = 'annulla';
	Ext.MessageBox.buttonText.ok = 'conferma';
	Ext.ods = function() {
		var msgCt;
		var errormsgCt;
		function createErrorBox(s) {
			return [ '<div class="msg">', '<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>', '<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3 style="color:red">Attenzione</h3>', s, '</div></div></div>', '<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>', '</div>' ].join('');
		}
		function createBox(s) {
			return [ '<div class="msg">', '<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>', '<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3> </h3>', s, '</div></div></div>', '<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>', '</div>' ].join('');
		}
		return {
			msg : function(format) {
				closeLoading();
				if (!msgCt) {
					msgCt = Ext.DomHelper.insertFirst(document.body, {
						id : 'msg-div'
					}, true);
				}
				msgCt.alignTo(document, 't-t');
				var s = String.format.apply(String, Array.prototype.slice.call(arguments, 0));
				var m = Ext.DomHelper.append(msgCt, {
					html : createBox(s)
				}, true);
				m.slideIn('t').pause(1).ghost("t", {
					remove : true
				});
			},
			errorMsg : function(format) {
				closeLoading();
				if (!errormsgCt) {
					errormsgCt = Ext.DomHelper.insertFirst(document.body, {
						id : 'errormsg-div'
					}, true);
				}
				errormsgCt.alignTo(document, 't-t');
				var s = String.format.apply(String, Array.prototype.slice.call(arguments, 0));
				var m = Ext.DomHelper.append(errormsgCt, {
					html : createErrorBox(s)
				}, true);
				m.slideIn('t').pause(2).ghost("t", {
					remove : true
				});
			},
			init : function() {
				var lb = Ext.get('lib-bar');
				if (lb) {
					lb.show();
				}
			}
		};
	}();
}
String.prototype.trim = function() {
	return (this.replace(/^[\s\xA0]+/, "").replace(/[\s\xA0]+$/, ""));
};
String.prototype.startsWith = function(str) {
	return (this.match("^" + str) == str);
};
String.prototype.endsWith = function(str) {
	return (this.match(str + "$") == str);
};
Array.prototype.contains = function(obj) {
	var i = this.length;
	while (i--) {
		if (this[i] === obj) {
			return true;
		}
	}
	return false;
};
// Ext.WindowMgr.zseed = 300000;
