package com.openDams.endpoint.managing;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.turtle.TurtleUtil;

public class RDFReader implements RDFWriter {
    //protected IndentingWriter writer;

    protected Map<String, String> namespaceTable;

    protected boolean writingStarted;

    protected boolean statementClosed;
    
    protected String rdfSubject;

    protected RDFObject rdfObject;

    
    public RDFReader() {
    	 namespaceTable = new LinkedHashMap<String, String>();
         writingStarted = false;
         statementClosed = true;
    }

    public RDFReader(Writer writer) {
        namespaceTable = new LinkedHashMap<String, String>();
        writingStarted = false;
        statementClosed = true;
    }

    public RDFFormat getRDFFormat() {
        return RDFFormat.TURTLE;
    }

    public void startRDF()throws RDFHandlerException{
        if (writingStarted) {
            throw new RuntimeException("Document writing has already started");
        }

        writingStarted = true;
    }

    public void endRDF()throws RDFHandlerException{
        if (!writingStarted) {
            throw new RuntimeException("Document writing has not yet started");
        }

        try {
            closePreviousStatement();
        }
        catch (IOException e) {
            throw new RDFHandlerException(e);
        }
        finally {
            writingStarted = false;
        }
    }

    public void handleNamespace(String prefix, String name)throws RDFHandlerException{
        try {
            if (!namespaceTable.containsKey(name)) {
                boolean isLegalPrefix = prefix.length() == 0 || TurtleUtil.isLegalPrefix(prefix);

                if (!isLegalPrefix || namespaceTable.containsValue(prefix)) {
                    if (prefix.length() == 0 || !isLegalPrefix) {
                        prefix = "ns";
                    }

                    int number = 1;

                    while (namespaceTable.containsValue(prefix + number)) {
                        number++;
                    }

                    prefix += number;
                }                
                namespaceTable.put(name, prefix);

                if (writingStarted) {
                    closePreviousStatement();
                    //writeNamespace(prefix, name);
                }
            }
        }
        catch (IOException e) {
            throw new RDFHandlerException(e);
        }
    }

    public void handleStatement(Statement st)throws RDFHandlerException{
        if (!writingStarted) {
            throw new RuntimeException("Document writing has not yet been started");
        }
        Resource subj = st.getSubject();
        URI pred = st.getPredicate();
        Value obj = st.getObject();        
        try {
        	 if(rdfSubject==null){
        		 rdfSubject = getResource(subj);
        	 }
        	 if(isTypePredicate(pred) || rdfObject==null){
        		 rdfObject = new RDFObject();
        		 rdfObject.setNamespaceTable(namespaceTable);
        		 rdfObject.setSubject(rdfSubject);
        		 rdfObject.setPredicate(getPredicate(pred));
        		 rdfObject.setObject(getValue(obj));
        		 rdfObject.setObjectType(RDFObject.OBJECT_TYPE_NODE);
        	 }else{
        		 if(isBNode(subj)){
        			 if(rdfObject.findBlankNode(getResource(subj))!=null){
        				 if(!getValue(obj).trim().equals("")){
        					 RDFObject blankNode = rdfObject.findBlankNode(getResource(subj));   					 
    						 blankNode.addToValues(new Tuple(getPredicate(pred), getValue(obj), getValueType(obj)));
    						 rdfObject.addToBlankNodes(getResource(subj), blankNode);
    					 }
    				 }else{
    					 if(!getValue(obj).trim().equals("")){
	    					 RDFObject blankNode = new RDFObject();
	    					 rdfObject.setObjectType(RDFObject.OBJECT_TYPE_BLANK_NODE);
	            			 blankNode.setSubject(getResource(subj));
	            			 blankNode.addToValues(new Tuple(getPredicate(pred), getValue(obj), getValueType(obj)));
	            			 rdfObject.addToBlankNodes(getResource(subj), blankNode);
    					 }
    				 }
        			 
        		 }else{
        			 if(rdfSubject.equals(getResource(subj))){
        				 if(!getValue(obj).trim().equals("")){
        					 rdfObject.addToValues(new Tuple(getPredicate(pred), getValue(obj), getValueType(obj)));
        				 }
        			 }else{
        				 if(rdfObject.findBlankNode(getResource(subj))!=null){
        					 if(!getValue(obj).trim().equals("")){
	        					 RDFObject blankNode = rdfObject.findBlankNode(getResource(subj));
	        					 blankNode.addToValues(new Tuple(getPredicate(pred), getValue(obj), getValueType(obj)));
	        					 rdfObject.addToBlankNodes(getResource(subj), blankNode);
        					 }
        				 }
        			 }	 
        		 }
        	 }
        }
        catch (IOException e) {
            throw new RDFHandlerException(e);
        }
    }

    public void handleComment(String comment)throws RDFHandlerException{
        try {
            closePreviousStatement();

            if (comment.indexOf('\r') != -1 || comment.indexOf('\n') != -1) {
                StringTokenizer st = new StringTokenizer(comment, "\r\n");
                while (st.hasMoreTokens()) {
                    writeCommentLine(st.nextToken());
                }
            }
            else {
                writeCommentLine(comment);
            }
        }
        catch (IOException e) {
            throw new RDFHandlerException(e);
        }
    }

    protected void writeCommentLine(String line)throws IOException{}

    protected void writeNamespace(String prefix, String name)throws IOException{}

    protected void writePredicate(URI predicate)throws IOException{}
    protected boolean isTypePredicate(URI predicate)throws IOException{
        if (predicate.equals(RDF.TYPE)) {
        	return true;
        }
        else {
        	return false;
        }
    }
    protected String getPredicate(URI predicate)throws IOException{
    	String result = "";
        if (predicate.equals(RDF.TYPE)) {
        	result+="a";
        }
        else {
        	result+=getURI(predicate);
        }
        return result;
    }
    protected void writeValue(Value val)throws IOException{
        if (val instanceof Resource) {
            writeResource((Resource)val);
        }
        else {
            writeLiteral((Literal)val);
        }
    }
    protected String getValue(Value val)throws IOException{
    	String result = "";
        if (val instanceof Resource) {
        	result+=getResource((Resource)val);
        }
        else {
        	result+=getLiteral((Literal)val);
        }
        return result;
    }
    protected int getValueType(Value val)throws IOException{
    	int result = RDFObject.TYPE_LITERAL;
        if (val instanceof Resource) {
        	if ((Resource)val instanceof URI) {
        		result = RDFObject.TYPE_URI;
            } else {
            	result = RDFObject.TYPE_BLANK_NODE;
            }
        }
        return result;
    }
    protected void writeResource(Resource res)throws IOException{
        if (res instanceof URI) {
            writeURI((URI)res);
        } else {
            writeBNode((BNode)res);
        }
    }
    protected String getResource(Resource res)throws IOException{
    	String result = "";
        if (res instanceof URI) {
        	result+=getURI((URI)res);
        } else {
        	result+=getBNode((BNode)res);
        }
        return result;
    }
    protected boolean isBNode(Resource res)throws IOException{
    	boolean result;
        if (res instanceof URI) {
        	result = false;
        } else {
        	result = true;
        }
        return result;
    }
    protected void writeURI(URI uri)throws IOException{}
    protected String getURI(URI uri)throws IOException{
        String uriString = uri.toString();
        String result="";
        // Try to find a prefix for the URI's namespace
        String prefix = null;

        int splitIdx = TurtleUtil.findURISplitIndex(uriString);
        if (splitIdx > 0) {
            String namespace = uriString.substring(0, splitIdx);
            prefix = namespaceTable.get(namespace);
        }

        if (prefix != null) {
        	result+=prefix;
        	if(!prefix.trim().equals(""))
        		result+=":";
        	result+=uriString.substring(splitIdx);
        }
        else {
            // Write full URI
        	//result+="<";
        	result+=TurtleUtil.encodeURIString(uriString);
        	//result+=">";
        }
        return result;
    }

    protected void writeBNode(BNode bNode)throws IOException{}
    protected String getBNode(BNode bNode)throws IOException{
    	String result = "";
    	result+="_:";
    	result+=bNode.getID();
        return result;
    }
    protected void writeLiteral(Literal lit)throws IOException{}
    protected String getLiteral(Literal lit)throws IOException{
        String label = lit.getLabel();
        String result = "";
        /*if (label.indexOf('\n') > 0 || label.indexOf('\r') > 0 || label.indexOf('\t') > 0) {
            // Write label as long string
        	//result+="\"\"\"";
        	result+=TurtleUtil.encodeLongString(label);
        	//result+="\"\"\"";
        }else {
            // Write label as normal string
        	//result+="\"";
        	result+=TurtleUtil.encodeString(label);
        	//result+="\"";
        }*/
        result = label;
        if (lit.getDatatype() != null) {
        	result+="^^";
        	result+=getURI(lit.getDatatype());
        }
        else if (lit.getLanguage() != null) {
        	result+="@";
        	result+=lit.getLanguage();
        }
        return result;
    }
    protected void closePreviousStatement()throws IOException{
        if (!statementClosed) {
            statementClosed = true;
        }
    }

	public RDFObject getRdfObject() {
		return rdfObject;
	}
}