<%session.invalidate();%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Companies"%>
<%@include file="locale.jsp"%>
<html>
<head>
<title>openDams</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=9">
<style type="text/css">
 html, body {
    font:normal 12px verdana;
    color: #15428b;
    margin:0;
    padding:0;
    border:0 none;
    overflow:hidden;
    height:100%;
    text-align: -moz-center;
    background-color: #dfe8f6;
}
 *:first-child+html body{text-align: center;}
 * html body{text-align: center; }
 .edit_field{
	border:0;
	color: #15428b;
	width: 99%;
	padding:2px;
	border:1px dotted #7DAAEA;
}
</style>
<script type="text/javascript">
  BrowserDetect = {
		init: function () {
			this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
			this.version = this.searchVersion(navigator.userAgent)
				|| this.searchVersion(navigator.appVersion)
				|| "an unknown version";
			this.OS = this.searchString(this.dataOS) || "an unknown OS";
		},
		searchString: function (data) {
			for (var i=0;i<data.length;i++)	{
				var dataString = data[i].string;
				var dataProp = data[i].prop;
				this.versionSearchString = data[i].versionSearch || data[i].identity;
				if (dataString) {
					if (dataString.indexOf(data[i].subString) != -1)
						return data[i].identity;
				}
				else if (dataProp)
					return data[i].identity;
			}
		},
		searchVersion: function (dataString) {
			var index = dataString.indexOf(this.versionSearchString);
			if (index == -1) return;
			return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
		},
		dataBrowser: [
			{
				string: navigator.userAgent,
				subString: "Chrome",
				identity: "Chrome"
			},
			{ 	string: navigator.userAgent,
				subString: "OmniWeb",
				versionSearch: "OmniWeb/",
				identity: "OmniWeb"
			},
			{
				string: navigator.vendor,
				subString: "Apple",
				identity: "Safari",
				versionSearch: "Version"
			},
			{
				prop: window.opera,
				identity: "Opera"
			},
			{
				string: navigator.vendor,
				subString: "iCab",
				identity: "iCab"
			},
			{
				string: navigator.vendor,
				subString: "KDE",
				identity: "Konqueror"
			},
			{
				string: navigator.userAgent,
				subString: "Firefox",
				identity: "Firefox"
			},
			{
				string: navigator.vendor,
				subString: "Camino",
				identity: "Camino"
			},
			{		// for newer Netscapes (6+)
				string: navigator.userAgent,
				subString: "Netscape",
				identity: "Netscape"
			},
			{
				string: navigator.userAgent,
				subString: "MSIE",
				identity: "Explorer",
				versionSearch: "MSIE"
			},
			{
				string: navigator.userAgent,
				subString: "Gecko",
				identity: "Mozilla",
				versionSearch: "rv"
			},
			{ 		// for older Netscapes (4-)
				string: navigator.userAgent,
				subString: "Mozilla",
				identity: "Netscape",
				versionSearch: "Mozilla"
			}
		],
		dataOS : [
			{
				string: navigator.platform,
				subString: "Win",
				identity: "Windows"
			},
			{
				string: navigator.platform,
				subString: "Mac",
				identity: "Mac"
			},
			{
				   string: navigator.userAgent,
				   subString: "iPhone",
				   identity: "iPhone/iPod"
		    },
			{
				string: navigator.platform,
				subString: "Linux",
				identity: "Linux"
			}
		]

	};
	BrowserDetect.init();

	
</script>
</head>

<body >
	<div style="margin-top: 30px;ba">Login - <span id="brDetect"></span></div>
	
	<div style="border:1px solid; width: 320px;height:190px;background-color: #ffffff"> 
	<br /><br /><br /><br />
	Attenzione, si &egrave; verificato un errore<br /> &egrave; possibile effettuare un nuovo accesso dal <br />Portale intranet. 
	<br />
	[<a href="javascript:window.close()">chiudi</a>] 
	</div>
	<script type="text/javascript">
		document.getElementById('brDetect').innerHTML = BrowserDetect.browser+' '+BrowserDetect.version+' ('+BrowserDetect.OS+')';
		if(BrowserDetect.browser  != 'Chrome' && BrowserDetect.browser  != 'Firefox'){
		//	alert('Attenzione: accesso non consentito con il browser '+BrowserDetect.browser+"\n"+"Allo stato attuale è possibile utilizzare solo Mozilla Firefox o Google Chrome");
		//	document.getElementById('subBtt').disabled = true;
		//	document.getElementById('resetBtt').disabled = true;
		}
	</script>
</body>
</html>