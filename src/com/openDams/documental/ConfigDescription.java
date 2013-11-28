package com.openDams.documental;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.openDams.utility.dom.XMLException;
import com.openDams.utility.dom.XMLHelper;

public class ConfigDescription {

	private static Properties props;

	private Map<String, Map<String, String>> map;
	
	public ConfigDescription (Properties properties) throws XPathExpressionException, IOException, XMLException{
		props = properties;
		String[] types = props.getProperty("import.types").trim().split("\\s*,\\s*");
		System.out.println(">> TYPES: "+joinStringArray(", ", types));
		
		this.map = new HashMap<String,Map<String,String>>();
		for(String t:types){
			String template = props.getProperty("rdf."+t+".template");
			Map<String,String> placeholders = getPlaceHolders(new File(template));
			this.map.put(t, placeholders);
		}
		
	}
	
	private static String joinStringArray(final String separator, final String... list){
		if(list == null || list.length==0) return null;
		if(list.length==1) return list[0]; 
		final StringBuffer sb = new StringBuffer();
		for(int i=0; i<list.length; i++){
			sb.append(list[i]);
			if (i<list.length-1) sb.append(separator);
		}
		return sb.toString();
	}
	
	public static String getPath(final Node actualNode){
		List<String> list = new LinkedList<String>();
		if(actualNode==null) return null;
		int k=0;
		Node node = actualNode;
		
		if(node instanceof Attr){
			node = ((Attr) node).getOwnerElement();
		}
		
		while(node.getParentNode()!=null){
			list.add(node.getNodeName());
			node = node.getParentNode();
		}
		Collections.reverse(list);
		String path = "/"+joinStringArray("/",list.toArray(new String[list.size()]));
		return path;
	}
	
	public static Map<String, String> getPlaceHolders(File file) throws IOException, XMLException, XPathExpressionException{
		final XPath xpath = XPathFactory.newInstance().newXPath();
		final Document doc = XMLHelper.newDocument(file);
		
		String filter = "//*[contains(./text(),'{')][contains(./text(),'}')] | //@*[contains(., '{')][contains(., '}')]";
		
		final NodeList nodes = (NodeList) xpath.evaluate(filter, doc, XPathConstants.NODESET); 
		System.out.printf(">> placeholder per il file: %s (%d)\n",file,nodes.getLength());
		
		final Map<String,String> results = new HashMap<String,String>();
		for(int i=0; i<nodes.getLength();i++){
			Node node = nodes.item(i);
			String text = node.getTextContent().trim();
			results.put(text, getPath(node));	
		}
		return results;
	}
	
	public Map<String, Map<String, String>> getPlaceholdersMap() {
		return this.map;
	}
	
	public JSONObject getPlaceholdersJSON(){
		JSONObject json = new JSONObject(this.map);
		return json;
	}
	
	public void saveFile(String fileName) throws IOException{
		File file = new File(fileName).getCanonicalFile();
		if(!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		PrintStream ps = new PrintStream(file);
		ps.println(this.getPlaceholdersJSON());
		ps.flush();
		ps.close();
	}

	public static void main(String[] args) throws Exception {

		Properties props = new Properties();
			//props.load(new FileInputStream(new File("WebContent/WEB-INF/Importer.properties")));

		
			
		ConfigDescription desc = new ConfigDescription(props);
		
		System.out.println(desc.getPlaceholdersMap());
		System.out.println(desc.getPlaceholdersJSON());
		
		desc.saveFile("devel/config/config.js");
	}


}


/*
TODO: si protrebbero utilizare delle enums per controllare i placeholders utilizzabili

VEDI:

http://java.dzone.com/articles/enum-tricks-dynamic-enums
http://stackoverflow.com/questions/478403/can-i-add-and-remove-elements-of-enumeration-at-runtime-in-java
http://blog.xebia.com/2009/04/dynamic-enums-in-java/
http://snipplr.com/view/1655/typesafe-enum-pattern/
*/