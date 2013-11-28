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

<body onload="document.f.j_username.focus();">
	<div style="margin-top: 30px;ba">Login - <span id="brDetect"></span></div>
	
	<div style="border:1px solid; width: 320px;height:190px;background-color: #ffffff">
			 <c:if test="${not empty param.login_error}">
		      <div style="width: 100%;padding:10px;color:red;">
		        ${loginMessage}<br/><br/>
		        Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
		      </div> 
		    </c:if>
		    <form name="f" action="<c:url value='j_spring_security_check'/>" method="POST">
			    <div style="width: 100%;padding:10px;"><input type='hidden'  name='locale' value="it_it">
				    <div style="float:left;width: 100%;padding:5px;">
				    	<div style="float:left;width:80px;text-align:left;">${loginUser}:</div>
				    	<div style="float:left;width: 250px;text-align:left;">
				    
				    	  <input type='text' class="edit_field" name='j_username' value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>'/>
				    	</div>
				    </div>
				    <div style="float:left;width: 100%;padding:5px;">
				    	<div style="float:left;width:80px;text-align:left;">${loginPassword}:</div>
				    	<div style="float:left;width: 250px;text-align:left;">
				    		<input type='password' class="edit_field" name='j_password' value="">
				    	</div>
			    	</div>
			    	<div style="float:left;width: 100%;padding:5px;">
				    	<div style="float:left;width:80px;text-align:left;">area:</div>
				    	<div style="float:left;width: 250px;text-align:left;">
					    	<select name="j_company" class="edit_field">
					    	<%List<Companies> companyList = (List<Companies>)request.getAttribute("companyList");%>
								<!-- option></option-->
								<%for(int i=0;i<companyList.size();i++){
								%>
									<option  value="<%=companyList.get(i).getIdCompany()%>"><%=companyList.get(i).getAcronym()%> - <%=companyList.get(i).getCompanyName()%></option>
								<%}%>
							</select>
				    	</div>
			    	</div>
				    <div style="float:left;width: 100%;text-align:left;padding:5px;">
				    	<input type="checkbox" name="_spring_security_remember_me">${loginRemember}
			   		</div>
				    <div style="float:right;padding:5px;">
				    	<input id="subBtt" name="submit" type="submit" value="Invia">
				    	<input id="resetBtt" name="reset" type="reset">
			    	</div>
			    </div>
		    </form>
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
