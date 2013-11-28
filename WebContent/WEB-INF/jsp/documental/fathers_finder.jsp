<%@page import="com.openDams.bean.Records"%>
<%@page import="java.util.ArrayList"%>
<script>
var fathers = new Array;
var positions = new Array;
<%
if(request.getAttribute("fathers")!=null){
	ArrayList<Records> fathers = (ArrayList<Records>)request.getAttribute("fathers");
	ArrayList<Integer> positions = (ArrayList<Integer>)request.getAttribute("positions");
	int count = 0;
	for (int i = fathers.size()-1; i >= 0; i--) {%>
	    
	fathers[<%=count%>]='<%=fathers.get(i).getIdRecord().intValue()%>';	   
	positions[<%=count%>]='<%=positions.get(i).intValue()%>';	
	<%
	count++;
	}%>
	fathers[<%=fathers.size()%>]='<%=request.getParameter("idRecord")%>';	   
	positions[<%=fathers.size()%>]='1';	
	<%
}
%>
openFathers(fathers,positions,'<%=request.getParameter("idRecord")%>','<%=request.getParameter("idArchive")%>');
 </script>