<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<html>
<head>
<title>openDams</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
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
</head>
<%@include file="WEB-INF/jsp/locale.jsp"%>
<body onload="document.f.j_username.focus();">
	<div style="margin-top: 30px;ba">Login</div>
	<div style="border:1px solid; width: 320px;height:190px;background-color: #ffffff">
			 <c:if test="${not empty param.login_error}">
		      <div style="width: 100%;padding:10px;color:red;">
		         ${loginMessage}<br/><br/>
		        Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
		      </div>
		    </c:if>
		    <form name="f" action="<c:url value='j_spring_security_check'/>" method="POST">
			    <div style="width: 100%;padding:10px;">
				    <div style="float:left;width: 100%;padding:5px;">
				    	<div style="float:left;width:80px;text-align:left;">${loginUser}:</div>
				    	<div style="float:left;width: 200px;text-align:left;">
				    	  <input type='text' class="edit_field" name='j_username' value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>sandro'/>
				    	</div>
				    </div>
				    <div style="float:left;width: 100%;padding:5px;">
				    	<div style="float:left;width:80px;text-align:left;">${loginPassword}:</div>
				    	<div style="float:left;width: 200px;text-align:left;">
				    		<input type='password' class="edit_field" name='j_password' value="sandro">
				    	</div>
			    	</div>
				    <div style="float:left;width: 100%;text-align:left;padding:5px;">
				    	<input type="checkbox" name="_spring_security_remember_me">${loginRemember}
			   		</div>
				    <div style="float:right;padding:5px;">
				    	<input name="submit" type="submit" value="Invia">
				    	<input name="reset" type="reset">
			    	</div>
			    </div>
		    </form>
	</div>
</body>
</html>
