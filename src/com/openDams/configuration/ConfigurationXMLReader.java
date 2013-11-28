/*
 * Creato il 28-apr-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package com.openDams.configuration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



/**
 * @author sandro
 *
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ConfigurationXMLReader {
    private Document document = null;
    private ArrayList<Object> objects = null;
    private Class<?> elementObject = null; 
	private HashMap<String,String> hashMap = null;
	public ConfigurationXMLReader(File xml,Class<?> obj) throws ParserConfigurationException, SAXException, IOException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		elementObject = obj;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		document = builder.parse( xml);
        read(document.getFirstChild(),null,null,null);
	}
	public ConfigurationXMLReader(String xml,Class<?> obj) throws ParserConfigurationException, SAXException, IOException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		elementObject = obj;
		ByteArrayInputStream input= new ByteArrayInputStream(xml.getBytes());
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		document = builder.parse( input );
		read(document.getFirstChild(),null,null,null);
	}
	public ConfigurationXMLReader(InputStream input,Class<?> obj,boolean close) throws ParserConfigurationException, SAXException, IOException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		elementObject = obj;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		document = builder.parse( input );
		read(document.getFirstChild(),null,null,null);
		if(close)
			input.close();
	}
	public ConfigurationXMLReader(byte[]xml,Class<?> obj) throws ParserConfigurationException, SAXException, IOException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		elementObject = obj;
		ByteArrayInputStream input= new ByteArrayInputStream(xml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		document = builder.parse( input );
		read(document.getFirstChild(),null,null,null);
	}
    private void read(Node node,Class<?> cParam,Object objectParam,String deepFather) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
		NodeList nodeList = node.getChildNodes();
		int gap=0;
		if(nodeList!=null && nodeList.getLength()>0){
			if(hashMap==null)
			  	hashMap = new HashMap<String,String>();
			for(int i=0;i<nodeList.getLength();i++){
				Class<?> constructorParamDef[] = {};
				Object constructorParam[] = {};
				Class<?> c = null;
				Object object = null;							
				if(nodeList.item(i).getNodeType()!=Node.TEXT_NODE && nodeList.item(i).getNodeType()!=Node.COMMENT_NODE && nodeList.item(i).getNodeType()!=Node.CDATA_SECTION_NODE){				
					c = Class.forName(elementObject.getPackage().toString().replaceAll("/",".").replaceAll("package ","")+"."+nodeList.item(i).getNodeName().replaceFirst(nodeList.item(i).getNodeName().substring(0,1),nodeList.item(i).getNodeName().substring(0,1).toUpperCase()));
					Constructor<?> theConstructor = c.getConstructor(constructorParamDef);
					object = theConstructor.newInstance(constructorParam);		
					NamedNodeMap namedNodeMap = nodeList.item(i).getAttributes();
			        if(namedNodeMap!=null && namedNodeMap.getLength()>0){  
						for (int j = 0; j < namedNodeMap.getLength(); j++) {
							Class<?>[] classes = {String.class};
							String[] values = {namedNodeMap.item(j).getNodeValue().trim()};
							Method method=c.getDeclaredMethod("set"+namedNodeMap.item(j).getNodeName().replaceFirst(namedNodeMap.item(j).getNodeName().substring(0,1),namedNodeMap.item(j).getNodeName().substring(0,1).toUpperCase()),classes);
							method.invoke(object,(Object[])values);
						}
					}
					String deep=Integer.toString(i-gap);
					if(deepFather!=null)
						deep=deepFather+"_"+deep;  	
					read(nodeList.item(i),c,object,deep);
					if(cParam!=null){
						Class<?>[] classes = {Object.class};
						Object[] values = {object};
						Method method=cParam.getDeclaredMethod("add"+nodeList.item(i).getNodeName().replaceFirst(nodeList.item(i).getNodeName().substring(0,1),nodeList.item(i).getNodeName().substring(0,1).toUpperCase()),classes);
						method.invoke(objectParam,values);
					}else{
				    	if(objects==null)
				    	    objects = new ArrayList<Object>();
						objects.add((Element)object);
				    }
				}else if(nodeList.item(i).getNodeType()!=Node.COMMENT_NODE){
					if(!nodeList.item(i).getNodeValue().trim().equals("")){
						Class<?>[] classes = {String.class};
						String[] values = {nodeList.item(i).getNodeValue().trim()};
						Method method = cParam.getDeclaredMethod("set"+nodeList.item(i).getNodeName().replaceFirst(nodeList.item(i).getNodeName().substring(1,2),nodeList.item(i).getNodeName().substring(1,2).toUpperCase()).replaceAll("#","").replaceAll("-","_"),classes);
						method.invoke(objectParam,(Object[])values);
						hashMap.put(nodeList.item(i).getNodeValue().trim(),deepFather);
					}else{
						gap++;
					}
				}
			}
		}		
    }
	public ArrayList<Object> getObjects() {
		return objects;
	}
	public Element getElement(String xpath)throws NullPointerException{
		Element result = null;
		try {
			String deep=(String)hashMap.get(xpath);
			String[] levels = deep.split("_");
			for(int i=0;i<levels.length;i++){
				   result=getObject(result,Integer.parseInt(levels[i]));
			}
		} catch (Exception e) {
              throw new NullPointerException("Nessuna Corrispondenza per l'elemento \""+xpath+"\" cercato!");
		}
		return result;
	}
	private Element getObject(Element element,int index){
		if(element==null)
		   return (Element)objects.get(index);
		else   
		   return ((Element)element.getElemets().get(index));
	}
}
