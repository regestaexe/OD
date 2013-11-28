package com.openDams.utility.dom;


import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NSContext implements NamespaceContext {

	private final Map<String, Set<String>> prefixesByURI = new HashMap<String, Set<String>>();
    private final Map<String, String> urisByPrefix = new HashMap<String, String>();
    
    /**
     * metodo costruttore generico senza parametri: di default aggiungiamo i due namespace
     * predefiniti: xml e xmlns
     */
    public NSContext() {
        addNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        addNamespace(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    }
    
    /**
     * costruttore basato su Document: costruisce un contesto per i namespace 
     * contenuti nel DOM in esame.
     * @param doc - Document da cui estrarre i namespace da aggiungere al contesto
     */
    public NSContext(final Document doc) {
    	this();
    	// ricavo i namespace definiti nella root dell'xml Document
    	final Element root = doc.getDocumentElement();
    	addNamespaces(root);
    }
    
    /**
     * aggiunta dei namespace presenti nell'Element XML
     * @param element - Element (Elemento XML) in esame
     */
    public synchronized void addNamespaces(final Element element){
    	final NamedNodeMap attributes = element.getAttributes();
    	for(int i=0; attributes!=null && i<attributes.getLength();i++){
    		final Node node = (Node) attributes.item(i);
    		if(node instanceof Attr){
    			String pref = ((Attr)node).getNodeName();
    			if(pref.contains(":")) pref = pref.substring(pref.lastIndexOf(":")+1);
    			addNamespace(pref, node.getTextContent());
    		}
//    		TODO: qui è da sviluppare se volessimo estrarre namespace da elementi non root
//    		else if(node instanceof Element){
//    			Element el = (Element) node;
//    			addNamespaces(el);
//    		}
    	}
    }
    
    public String toString(){
    	return urisByPrefix.toString();
    }

    /**
     * metodo per l'aggiunta di una coppia prefisso/uri relativa ad un namespace
     * @param prefix		- prefisso del namespace
     * @param namespaceURI	- URI del namespace
     */
    public synchronized void addNamespace(final String prefix, final String namespaceURI) {
    	urisByPrefix.put(prefix, namespaceURI);
        if (prefixesByURI.containsKey(namespaceURI)) {
        	prefixesByURI.get(namespaceURI).add(prefix);
        } else {
        	final Set<String> set = new HashSet<String>(2);
        	set.add(prefix);
            prefixesByURI.put(namespaceURI, set);
        }
    }
    
    /**
     * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
     */
    public String getNamespaceURI(final String prefix) {
        if (prefix == null)
            throw new IllegalArgumentException("prefix cannot be null");
        if (urisByPrefix.containsKey(prefix))
            return (String) urisByPrefix.get(prefix);
        else
            return XMLConstants.NULL_NS_URI;
    }
    
    /**
     * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
     */
    public String getPrefix(final String namespaceURI) {
        return (String) getPrefixes(namespaceURI).next(  );
    }

    /**
     * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public Iterator<String> getPrefixes(final String namespaceURI) {
        if (namespaceURI == null)
            throw new IllegalArgumentException("namespaceURI cannot be null");
        if (prefixesByURI.containsKey(namespaceURI)) {
            return prefixesByURI.get(namespaceURI).iterator();
        } else {
        	return (Iterator<String>)Collections.EMPTY_SET.iterator();
        }
    }
    
    /*
     * main di TEST
     */
    public static void main(String[] args) throws Exception{ }
    
}