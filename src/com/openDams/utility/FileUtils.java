package com.openDams.utility;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class FileUtils {

	public static void replaceAll(File fileName, String inputEncoding, Map<String, String> replacements) throws Exception{
		InputStream in = new FileInputStream(fileName);
		FileOutputStream fos = new FileOutputStream(fileName);
	    replaceInFile(in, fos, inputEncoding, null);
	}
	
	public static String readFileAsString(InputStream input, OutputStream output, String inputEncoding) throws Exception{
		return replaceInFile(input, output, inputEncoding, null);
	}
	
	public static String replaceInFile(InputStream input, OutputStream output, String inputEncoding, Map<String, String> replacements) throws Exception{
		byte[] buffer = new byte[input.available()];
	    BufferedInputStream bis = null;
	    try {
	        bis = new BufferedInputStream(input);
	        Reader reader = (inputEncoding!=null)?new InputStreamReader(bis, inputEncoding) : new InputStreamReader(bis);
	        bis.read(buffer);
	    } finally {
	        if (bis != null) try { bis.close(); } catch (IOException ignored) { }
	    }

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    baos.write(buffer);
	    if(replacements!=null){
	    	String text = baos.toString(); 
		    for(Entry<String, String> replacement: replacements.entrySet()){
		    	text = text.replaceAll(replacement.getKey(), replacement.getValue());
		    }
		    baos.reset();
		    baos.write(text.getBytes());
	    }
	    baos.writeTo(output);
	    
	    return baos.toString();
	}
	
	
	public static void main(String args[]) throws Exception{
		String fileName = "/home/seralf/regesta/nsid/app/cache/TestImportRDF/articoliDottrina/TOC_20110803_1503.xml";
		InputStream in = new FileInputStream(fileName);
		String encoding = "windows-1252";
		Map<String, String> replacements = new HashMap<String, String>();
	    replacements.put("D&amp;L", "D&L");
	    replacements.put("D&L", "D&amp;L");
	    FileOutputStream fos = new FileOutputStream("PROVA.xml");
		String testo = replaceInFile(in, fos, encoding, replacements);
	}

}
