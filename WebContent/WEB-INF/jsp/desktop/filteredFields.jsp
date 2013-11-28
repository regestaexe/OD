<%@page import="com.openDams.search.configuration.Element"%>
<%@page import="java.util.ArrayList"%> 

<%@page import="com.regesta.framework.util.StringUtility"%>
<%@page import="com.regesta.framework.util.StringUtility1_4"%><script type="text/javascript">
<%
if(request.getAttribute("searchElements")!=null){%>
	    	        <%
	    	        ArrayList<Object>  searchElements= (ArrayList<Object>)request.getAttribute("searchElements");
					
					for (int i = 0; i < searchElements.size(); i++) {
						Element element = (Element) searchElements.get(i);
						if(!element.getName().equals("order")){
							if(element.getPages().equalsIgnoreCase("all") || element.getPages().indexOf("simple_search")!=-1){
						%>  
						    <%if(element.getName().indexOf("_set")==-1){%>
							    var filed<%=element.getName()%> = <%=element.getCdata_section()%>;
								Ext.getCmp('commonSearchFields').add(filed<%=element.getName()%>);
						    <%}else{	
							    String cDataString = StringUtility1_4.internalTrim( element.getCdata_section().replaceAll("\\s*\\{","{").replaceAll("\\}\\s*","}").replaceAll("\\},\\{","}~{") ) ;
							    String[] cDatas = cDataString.split("~");
							    for(int x=0;x<cDatas.length;x++){%>
							    	var filed<%=element.getName()%>_<%=x%> = <%=cDatas[x]%>;
									Ext.getCmp('commonSearchFields').add(filed<%=element.getName()%>_<%=x%>);
							    <%}%>						    	
						    <%}%>
						<%  }
						}
					}
					%>
					Ext.getCmp('commonSearchFields').doLayout();
	     
<%}%>
</script>

