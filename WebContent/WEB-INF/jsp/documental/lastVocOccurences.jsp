<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="com.openDams.index.searchers.VocTerm"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.openDams.index.searchers.Vocabulary"%>
<%if(request.getAttribute("vocabulary")!=null){
	Vocabulary vocabulary = (Vocabulary)request.getAttribute("vocabulary");
	ArrayList<VocTerm> vocTerms = vocabulary.getTerms();
	if(vocTerms!=null && vocTerms.size()>0){
		for(int i=0;i<vocTerms.size();i++){
			VocTerm vocTerm = vocTerms.get(i);%>
			[<%=vocTerm.getFrequence()%>]<%=vocTerm.getTerm()%><br />
		<%}
	}
}%>