package com.openDams.endpoint.managing;


import java.util.ArrayList;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;

public class NTriplesReader implements RDFWriter {

    private ArrayList<Statement> statements = null;
    public NTriplesReader(){}

    public RDFFormat getRDFFormat() {
        return RDFFormat.NTRIPLES;
    }

    public void startRDF() throws RDFHandlerException { 
    }

    public void endRDF() throws RDFHandlerException {
    }

    public void handleNamespace(String prefix, String name) {
        // N-Triples does not support namespace prefixes.
    }

    public void handleStatement(Statement st) throws RDFHandlerException {
        try {
        	if(statements==null)
        		statements = new ArrayList<Statement>();
        	statements.add(st);        	
        }
        catch (Exception e) {
            throw new RDFHandlerException(e);
        }
    }

    public void handleComment(String comment) throws RDFHandlerException {
    }

	public ArrayList<Statement> getStatements() {
		return statements;
	}
}