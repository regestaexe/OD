<%@page contentType="text/html; charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.openDams.bean.Users"%>  
<%@page import="com.openDams.bean.Archives"%>
<%@page import="com.openDams.security.UserDetails"%>
<%@page import="org.springframework.security.core.context.SecurityContext"%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="java.util.Collection"%>
<%@page import="org.springframework.security.core.GrantedAuthority"%>
<%@page import="com.openDams.security.RoleTester"%>
<%@page import="com.openDams.utility.ArchiveColorFactory"%>
<%@page import="com.regesta.framework.util.JsSolver"%>
<%if(request.getAttribute("user")!=null){
	Users user = (Users)request.getAttribute("user");
	List<Object> archives = (List)request.getAttribute("archivesList");
	Object[] archivesList = archives.toArray();
	List<Object> thesaurus = (List)request.getAttribute("thesaurusList");
	Object[] thesaurusList = thesaurus.toArray();
	
%>	
<%
	UserDetails userApp = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
	Collection<GrantedAuthority> authorities = userApp.getAuthorities();
	Object[] authoritiesList = authorities.toArray();
	boolean god = false;
	int approle = user.getUserRoles().getRoles().getIdRole();
	for (int s = 0; s < authoritiesList.length; s++) {
		GrantedAuthority grantedAuthority = (GrantedAuthority) authoritiesList[s];
		if (RoleTester.testGod(grantedAuthority.getAuthority())) {
			god = true;
			break;
		}
	}
%>
<script type="text/javascript">
  Ext.onReady(function(){
	 var checkApplication = {
		    	width:250,
		    	height:55,
		        xtype: 'fieldset',
		        title: 'N.S.I.D.',
		        style: 'background-color:#deecfd;',
		        autoHeight: false,
		        layout: 'fit',
		        collapsed: false,
		        collapsible: true,
		        items: [
		       {
		            xtype: 'radiogroup',
		            items: [
				        <%
				        String ck_1="";
				        String ck_2="";
				        String ck_3="";
				        String ck_4="";
				        if(approle==1){
				        	ck_1=", checked: true";
				        }else if(approle==2){
				        	ck_2=", checked: true";
				        }else if(approle==3){
				        	ck_3=", checked: true";
				        }else if(approle==4){
				        	ck_4=", checked: true";
				        }
				        %>			
		                {boxLabel: 'admin',  name: 'role_nsid',inputValue:'1'<%=ck_1%>},
		                {boxLabel: 'utente', name: 'role_nsid',inputValue:'2'<%=ck_2%>},
		                {boxLabel: 'lettore',name: 'role_nsid',inputValue:'4'<%=ck_4%>}
		                <%if(god) {%>
		                ,{boxLabel: 'god',name: 'role_nsid',inputValue:'3'<%=ck_3%>}
		                <%}%>
		            ]
		        }]
	};
	<%
	for(int i=0;i<archivesList.length;i++){
		   Archives archive = (Archives)archivesList[i];
	%>  
    var checkGroup_<%=archive.getIdArchive()%> = {
    	width:250,
    	height:55,
        xtype: 'fieldset',
        title: '<%=JsSolver.escapeSingleApex(archive.getLabel())%>',
        style: 'background-color:<%=ArchiveColorFactory.getHTMLArchiveColor(archive.getIdArchive())%>;',
        autoHeight: false,
        layout: 'fit',
        collapsed: false,   // initially collapse the group
        collapsible: true,
        items: [
       {    
    	   <%
	        String ck_archive_1="";
	        String ck_archive_2="";
	        String ck_archive_3="";
	        String ck_archive_0="";
	        int archive_role = user.getArchiveRole(archive.getIdArchive());
	        if(archive_role==1){
	        	ck_archive_1=", checked: true";
	        }else if(archive_role==2){
	        	ck_archive_2=", checked: true";
	        }else if(archive_role==4){
	        	ck_archive_3=", checked: true";
	        }else if(archive_role==0){
	        	ck_archive_0=", checked: true";
	        }
	        %>		
            xtype: 'radiogroup',
            items: [
                {boxLabel: 'admin',  name: 'ref_id_archive_<%=archive.getIdArchive()%>',inputValue:'1'<%=ck_archive_1%>},
                {boxLabel: 'utente', name: 'ref_id_archive_<%=archive.getIdArchive()%>',inputValue:'2'<%=ck_archive_2%>},
                {boxLabel: 'lettore',name: 'ref_id_archive_<%=archive.getIdArchive()%>',inputValue:'4'<%=ck_archive_3%>},
                {boxLabel: 'n.d.',   name: 'ref_id_archive_<%=archive.getIdArchive()%>',inputValue:'0'<%=ck_archive_0%>}
            ]
        }]
    };
    <%}%>
    <%
	for(int i=0;i<thesaurusList.length;i++){
		   Archives archive = (Archives)thesaurusList[i];
	%>  
    var checkGroup_<%=archive.getIdArchive()%> = {
    	width:250,
    	height:55,
        xtype: 'fieldset',
        style: 'background-color:#eeeff1;',
        title: '<%=archive.getLabel()%>',
        autoHeight: false,
        layout: 'fit',
        collapsed: false,   // initially collapse the group
        collapsible: true,
        items: [
       {
    	   <%
	        String ck_archive_1="";
	        String ck_archive_2="";
	        String ck_archive_3="";
	        String ck_archive_0="";
	        int archive_role = user.getArchiveRole(archive.getIdArchive());
	        if(archive_role==1){
	        	ck_archive_1=", checked: true";
	        }else if(archive_role==2){
	        	ck_archive_2=", checked: true";
	        }else if(archive_role==4){
	        	ck_archive_3=", checked: true";
	        }else if(archive_role==0){
	        	ck_archive_0=", checked: true";
	        }
	        %>
            xtype: 'radiogroup',
            items: [
                {boxLabel: 'admin',  name: 'ref_id_archive_<%=archive.getIdArchive()%>',inputValue:'1'<%=ck_archive_1%>},
                {boxLabel: 'utente', name: 'ref_id_archive_<%=archive.getIdArchive()%>',inputValue:'2'<%=ck_archive_2%>},
                {boxLabel: 'lettore',name: 'ref_id_archive_<%=archive.getIdArchive()%>',inputValue:'4'<%=ck_archive_3%>},
                {boxLabel: 'n.d.',   name: 'ref_id_archive_<%=archive.getIdArchive()%>',inputValue:'0'<%=ck_archive_0%>}
            ]
        }]
    };
    <%}%>
     var table = new  Ext.FormPanel({
    	 title: 'Gestione ruolo e archivi per l\'utente : <b><%=user.getUsername()%></b>',
         id:'archive-table-panel',
         renderTo:'userDetails',
         layout:'table',
         width: '100%',
         height:300,
         autoScroll: true,
         layoutConfig: {columns:5},
         items: [
				checkApplication,
				<%
				for(int i=0;i<thesaurusList.length;i++){
					   Archives archive = (Archives)thesaurusList[i];
				%>  
				checkGroup_<%=archive.getIdArchive()%>,
				<%}%>
				<%
				for(int i=0;i<archivesList.length;i++){
					   Archives archive = (Archives)archivesList[i];
				%>  
				checkGroup_<%=archive.getIdArchive()%><%if(i<archivesList.length-1){%>,<%}%>
				<%}%>

         ]
     });
     Ext.getCmp('but_save').setDisabled(false);
     Ext.getCmp('but_reset').setDisabled(false);
});		
</script>
<%}else{%>
<script type="text/javascript">
Ext.onReady(function(){
     Ext.getCmp('but_save').setDisabled(true);
     Ext.getCmp('but_reset').setDisabled(true);
});		
</script>
<%}%>

