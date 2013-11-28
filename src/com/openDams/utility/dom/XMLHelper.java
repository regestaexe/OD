package com.openDams.utility.dom;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;

/*
 * TODO: estendere con un file di Properties
 */
public class XMLHelper {
	
	private final static XPath xpath = XPathFactory.newInstance().newXPath();
		
	private final static class DefaultNamespaceContextResolver implements NamespaceContext{
		private static PrefixResolver resolver = null;
		public DefaultNamespaceContextResolver(final Document doc) {
			resolver = new PrefixResolverDefault(doc.getDocumentElement());
		}
		@Override
		public String getNamespaceURI(final String prefix) {
			return resolver.getNamespaceForPrefix(prefix);
		}
		@Override
		public String getPrefix(final String uri) {
			return null;
		}
		@Override
		public Iterator<String> getPrefixes(final String val) {
			return null;
		}
	};
	
	// trasformazione identica xslt per modificare l'encoding
	private static final String xsltTemplateString = "<?xml version=\"1.0\" encoding=\"%s\"?>" +
			"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">" +
			"<xsl:output encoding=\"%s\" indent=\"yes\" />" +
			"<xsl:template match=\"/\">" +
			"<xsl:copy-of select=\".\"/>" +
			"</xsl:template>" +
			"</xsl:stylesheet>";
	
		
	/*
	 * questo metodo produce un nuovo XML DOM Document partendo da un InputStream
	 */
	private static Document parse(final InputStream in, boolean useNamespaces) throws IOException, XMLException{
		try{
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document doc = builder.parse(in);
			//	factory.setAttribute(name, value)
			//	factory.setSchema(Schema)
			factory.setCoalescing(true);
			factory.setExpandEntityReferences(true);
			factory.setIgnoringComments(false);
			if(useNamespaces) factory.setNamespaceAware(true); // WORKAROUND per xalan!
			factory.setXIncludeAware(true);
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			// aggiungo alcune feature per impedire il tentativo di caricamento del DTD da remoto, al fine di validazione
			factory.setFeature("http://xml.org/sax/features/namespaces", false);
			factory.setFeature("http://xml.org/sax/features/validation", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			in.close();
			return doc;
		}catch(Exception e){
			e.printStackTrace();
			throw new XMLException(e);
		}
	}
			
	/*
	 * costruisco un nuovo DOM vuoto
	 */
	public static Document newDocument() throws XMLException{
		try{
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();			
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document doc = builder.newDocument();
			return doc;
		}catch(Exception e){
			e.printStackTrace();
			throw new XMLException(e);
		}
	}
	
	public static Document newDocument(final File file) throws IOException, XMLException{
		return newDocument(file, false);
	}
	
	/*
	 * costruisco un DOM partendo da un File
	 */
	public static Document newDocument(final File file, boolean useNamespaces) throws IOException, XMLException {
		final InputStream in = new FileInputStream(file);
		return XMLHelper.parse(in, useNamespaces);
	}
	
	/*
	 * costruisco un DOM partendo da una URL
	 * NOTA: attenzione qui perchè se la URL non contiene un XHTML valido verrà generato un errore
	 */
	public static Document newDocument(final URL url, boolean useNamespaces) throws IOException, XMLException {
		final InputStream in = url.openConnection().getInputStream();
		final Document document = XMLHelper.parse(in, useNamespaces);
		in.close();
		return document;
	}
	
	/*
	 * metodo generico che rimanda ai due specifici per File o URIs...
	 */
	public static Document newDocument(final URI uri, boolean useNamespaces) throws IOException, XMLException {
		String scheme = uri.getScheme();
		if(scheme==null || scheme.equals("file") || scheme.equals("")){
			return newDocument(new File(uri), useNamespaces);
		}else{
			return newDocument(uri.toURL(), useNamespaces);
		}
	}
	
	
	/*
	 * costruisco un DOM partendo da un frammeno di testo, passato come String
	 */
	public static Document newDocument(final String xmlContent, boolean useNamespaces) throws IOException, SAXException, ParserConfigurationException, XMLException {
		final ByteArrayInputStream in = new ByteArrayInputStream(xmlContent.getBytes());
		return XMLHelper.parse(in, useNamespaces);
	}
	
	/*
	 * serializzazione del DOM (da Document)
	 */
	public static OutputStream write(final Document doc, final OutputStream out, String encoding) throws XMLException {
		if (doc==null) throw new XMLException("Document is null");
		doc.normalizeDocument();
		// TODO: rivedere e aggiungere strip dei nodi testo vuoti 
		// (ad esempio righe vuote derivanti da rimozione nodi)
		return write(doc.getDocumentElement(), out, encoding);
	}
	
	/*
	 * serializzazione del DOM (da Element)
	 */
	public static OutputStream write(Node node, final OutputStream out, String encoding) throws XMLException {
		if (node==null) throw new XMLException("node is null");
		try {
			node.normalize();
			// di default utilizzo UTF-8, se non viene esplicitamente specificato un encoding differente
			if(encoding==null || encoding.equals("")) encoding = "UTF-8";
			
			final ByteArrayInputStream xsltStream = new ByteArrayInputStream(
					String.format(xsltTemplateString, encoding, encoding).getBytes()
				);
			
			Source source = new StreamSource(xsltStream);
						
			final TransformerFactory factory = TransformerFactory.newInstance();
			//factory.setFeature(name, value)
			//factory.setURIResolver(resolver)
			final Transformer transformer = factory.newTransformer(source);
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
				transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
								
				// DOCTYPE_PUBLIC, DOCTYPE_SYSTEM, CDATA_SECTION_ELEMENTS
			final StreamResult result = new StreamResult(out);
						
			transformer.transform(new DOMSource(node), result);
			return out;
		}catch(Exception e){
			e.printStackTrace();
			throw new XMLException(e);
		}
	}

	/*
	 * metodo di comodo per reperire il contenuto testuale di un nodo
	 */
	public static String evaluateAsString(final Document doc, String xpathQuery) throws XPathException{
		try {
			xpath.setNamespaceContext(new DefaultNamespaceContextResolver(doc));
			String result = xpath.evaluate(xpathQuery, doc);
			return result;
		}catch(Exception e){
			e.printStackTrace();
			throw new XPathException(e);
		}
	}
	
	public static String evaluateContentAsString(final Document doc, final String xpathQuery, String separator) throws XPathExpressionException{
		final String[] content = evaluateContentAsStringArray(doc, xpathQuery, separator, null);
		if (content==null || content.length == 0) return null;
		else return content[0];
	}
	
	/*
	 * metodo per reperire l'intero contenuto testuale (compresi nodi testo nei discendenti) di una serie di nodi
	 */
	public static String[] evaluateContentAsStringArray(final Document doc, final String xpathQuery, String separator, String filter) throws XPathExpressionException{
		
		xpath.setNamespaceContext(new DefaultNamespaceContextResolver(doc));
		
		// aggiunta dei criteri per filtrare i risultati delle query xpath
		String query = (filter!=null && !filter.equalsIgnoreCase("")) ? String.format("%s[%s]", xpathQuery, filter) : xpathQuery;
		
		final NodeList elements = (NodeList) xpath.evaluate(query, doc, XPathConstants.NODESET);
		final ArrayList<String> list = new ArrayList<String>();
		for(int i=0; i<elements.getLength();i++){
			Node node = elements.item(i);
			
			if(node==null) continue;
			StringBuffer sb = new StringBuffer();
			NodeList testi = (NodeList) xpath.evaluate(".//*|.//following-sibling::text()", node, XPathConstants.NODESET);
			
			boolean hasSeparator = false; 
			for(int j=0; j<testi.getLength();j++){
				Node item = testi.item(j);

				String testo;
				if(item.getNodeType()==Node.TEXT_NODE){
					Text text = (Text) item;
					testo = text.getNodeValue();
				}else{
					 testo = xpath.evaluate("normalize-space(./text())", item).trim();
				}
				if((!testo.trim().equals("")) && (testo!=null)){
					if(hasSeparator) sb.append(separator);
					sb.append(testo);
					hasSeparator = true;
				}else{
					continue;
				}
			}
			sb.trimToSize();
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * NOTA: utilizzare il metodo evaluateAsString
	 * @throws XPathException 
	 */
	@Deprecated
	public static String getTextValue(final Document doc, final String xpathElement) throws XPathException{
		try {
			xpath.setNamespaceContext(new DefaultNamespaceContextResolver(doc));
			final XPathExpression expr = xpath.compile(xpathElement);
			final String text = (String) expr.evaluate(doc, XPathConstants.STRING);
			return text;
		}catch(Exception e){
			e.printStackTrace();
			throw new XPathException(e);
		}
	}
	
	// ricavo un nodo specifico in base ad XPath
	public static Element getElement(final Document doc, final String xpathElement) throws XMLException{
		try {
			xpath.setNamespaceContext(new DefaultNamespaceContextResolver(doc));
			final XPathExpression expr = xpath.compile(xpathElement);
			final Node element = (Node) expr.evaluate(doc, XPathConstants.NODE);
			return (Element)element;
		}catch(Exception e){
			e.printStackTrace();
			throw new XMLException(e);
		}
	}
		
	
	// modifico il contenuto di un singolo nodo
	public static void setElementValue(final Document doc, final String xpathElement, final String value) throws XMLException{
		try {
			xpath.setNamespaceContext(new DefaultNamespaceContextResolver(doc));
			final XPathExpression expr = xpath.compile(xpathElement);
			final Node element = (Node) expr.evaluate(doc, XPathConstants.NODE);
			if (element==null){
				throw new XMLException("NO Element found for: "+xpathElement);
			}
			element.setTextContent(value); // TODO: riscrivo l'intero contenuto del nodo XML, da rivedere
		}catch(Exception e){
			e.printStackTrace();
			throw new XMLException(e);
		}
	}
	
	/*
	 * con questo metodo posso scrivere i valori di una serie di elementi in base ad una mappa che ha entry del tipo: <xpath-elemento, valore>
	 */
	public static void setElementsValues(final Document doc, final Map<String, String> valueMap) throws XMLException {
		for(final Entry<String, String> entry : valueMap.entrySet()){
			XMLHelper.setElementValue(doc, entry.getKey(), entry.getValue());
		}
	}
	
	// ottengo una rappresentazione String di un Element
	public static String toStringValue(final Element element) throws XMLException{
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			write(element, baos, null);
			return baos.toString();
		}catch(Exception e){
			e.printStackTrace();
			throw new XMLException(e);
		}
	}
	
	// ottengo una rappresentazione String di un Document
	public static String toStringValue(final Document doc) throws XMLException{
		return toStringValue(doc.getDocumentElement());
	}
		
	// sostituisco dei segnaposto...
	// TODO: rifattorizzare il nome in qualcosa tipo "replaceAll"?
	public static String injectValues(final Document doc, final Map<String, String> values) throws XMLException{
		String result = XMLHelper.toStringValue(doc);
		for(final Entry<String,String> entry:values.entrySet()){
			final String placeholder = new String("\\{"+entry.getKey()+"\\}");
			result = result.replaceAll(placeholder, entry.getValue());
		}
		return result;
	}
		
	public static void remove(final Document doc, final String xpathSelector) throws XMLException {
		try {
			final XPathExpression expr = xpath.compile(xpathSelector);
			final Node element = (Node) expr.evaluate(doc, XPathConstants.NODE);
			if (element==null) return;
			final Node parent = element.getParentNode();
			parent.removeChild(element);
		}catch(Exception e){
			e.printStackTrace();
			throw new XMLException(e);
		}
	}
	
	/*
	 * Questo metodo è utile per generare un Set contenente i path (univoci) ai vari nodi.
	 * In pratica fornisce una descrizione naif della struttura ad albero dell'xml, utile per costruire le query xpath
	 */
	private static Set<String> getTree(Node doc){
		final Set<String> set = new TreeSet<String>();
		if(doc==null) return set;
		
		String path = new String();
		if(doc.getPrefix()!=null && doc.getLocalName()!=null){
			path = new String(doc.getPrefix() + ":" + doc.getLocalName());
			set.add(path);
		}
		
		final NamedNodeMap attributes = doc.getAttributes();
		for(int i=0; attributes!=null && i<attributes.getLength();i++){
			final Node attribute = attributes.item(i);
			String name = attribute.getLocalName();
			if(attribute.getPrefix()!=null) name=attribute.getPrefix()+":"+name; 
			set.add(path+"/@"+name);
		}
		
		final NodeList nodes = doc.getChildNodes();
		for(int i=0; i< nodes.getLength() ; i++){
			final Node node = nodes.item(i);
			if(node instanceof Element){
				final Set<String> children = getTree(node);
				for(final String child:children){
					set.add(path+"/"+child);
				}
			}
		}
		return set;
	}
	
	/*
	 * main di test:
	 * posso creare Document da String, da File, da InputStream
	 */
	public static void main(String[] args) {
		try {
			
			// TEST generazione DOM da String contenente l'xml
			Document doc1 = XMLHelper.newDocument("<root xmlns:testNS=\"http://testNS\"><uno>vediamo {cosa}</uno><testNS:tre>prova con namespace</testNS:tre><due>prova {prova}<due-uno>primo elemento interno all'elemento due</due-uno></due></root>", true);
						
			XMLHelper.write(doc1, System.out, null);
			
			// TEST sostituzioni....
			final Map<String,String> mappa = new HashMap<String, String>();
				mappa.put("cosa", "sostituto_cosa");
				mappa.put("prova", "sostituto_prova");
			
			final String result = XMLHelper.injectValues(doc1, mappa);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
