package com.openDams.utility;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

/**
 * facility class per gestire gruppi di File e Url complessivamente come URI:
 * abbiamo metodi per gestire il contenuto dell'URI o per salvarlo su file locale.
 * 
 * @author seralf
 */
public class URIHelper {
	
	/**
	 * ottengo un array di URI da un array contenente File o URL
	 * @param objs - array contenente oggetti di tipo File, URL o URI
	 */
	public static URI[] getURIs(final Object[] objs) throws URISyntaxException{
		final List<URI> uris = new LinkedList<URI>();
		for(final Object obj:objs){
			if(obj instanceof URI){
				uris.add((URI)obj);
			}else if(obj instanceof URL){
				uris.add(((URL)obj).toURI());
			}else if(obj instanceof File){
				uris.add(((File)obj).toURI());		
			}else{
				continue;
			}
		}
		return uris.toArray(new URI[uris.size()]);
	}
	
	
	/**
	 * metodo per ricavare un InputStream con differente encoding.
	 * TODO: testing
	 * @param uri - uri di cui vogliamo ricavare l'InputStream
	 * @param encoding - Encoding desiderato
	 */
	public static InputStream getInputStream(final URI uri, String encoding) throws MalformedURLException, IOException{
		final InputStream input = URIHelper.getInputStream(uri);
		// decode/conversion of encoding
		final CharsetDecoder charsetDecoder = Charset.forName(encoding).newDecoder();
			charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE);
			charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		final Reader reader = new InputStreamReader(input, charsetDecoder);
		
		final CharBuffer buffer = CharBuffer.allocate(input.available());
		reader.read(buffer);
		buffer.compact();
		
		//
		
		final String text = new String(buffer.array());
		final ByteArrayInputStream bais = new ByteArrayInputStream(text.getBytes(encoding));
		buffer.clear();
		return bais;
	}

	/**
	 * salvataggio su File della URI
	 * @param uri		- uri da salvare
	 * @param outFile	- File dove salvare il contenuto della URI in esame
	 */
	public static void toFile(final URI uri, final File outFile) throws MalformedURLException, IOException{
		// TODO: rivedere la gestione dell'encoding!
		// se non esiste il path di destinazione pe il File, lo creo
		final File dir = outFile.getAbsoluteFile().getParentFile();
		if(!dir.exists()) dir.mkdirs();			
		int bufSize = 8 * 1024;
		final InputStream input = getInputStream(uri);
		final BufferedInputStream in = new java.io.BufferedInputStream(input);
		final FileOutputStream fos = new FileOutputStream(outFile.getAbsoluteFile());
		final BufferedOutputStream bout = new BufferedOutputStream(fos, bufSize);
		byte[] buf = new byte[bufSize];
		int read = 0;
		while ((read = in.read(buf, 0, bufSize)) >= 0) {
			bout.write(buf, 0, read);
		}
		bout.flush();
		bout.close();
		in.close();
	}
	
	/**
	 * Ricavo l'InputStream relativo ad una certa URI, con il metodo appropriato
	 * nei casi in cui la URI sia un File o una URL
	 * @param uri
	 */
	public static InputStream getInputStream(final URI uri) throws MalformedURLException, IOException{
		final int bufSize = 8 * 1024;
		// gestione InputStream nei vari casi
		if(uri.getScheme().equalsIgnoreCase("file")){
			return new BufferedInputStream(new FileInputStream(new File(uri)), bufSize);
		}else if(uri.getScheme().equalsIgnoreCase("http")){
			return new BufferedInputStream(uri.toURL().openStream(), bufSize);
		}
		return null;
	}
	
	/**
	 * Ricavo il contenuto relativo alla URI come String
	 * @param uri
	 */
	public static String getContent(final URI uri) throws IOException{
		final InputStream input = getInputStream(uri);
		final int bufSize = 8 * 1024;
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		// salviamo direttamente i byte così come sono
		int read = -1;
		byte[] buf = new byte[bufSize];
		while ((read = input.read(buf, 0, bufSize)) >= 0) {
			output.write(buf, 0, read);
		}
		output.flush();
		// rilasciamo le risorse
		input.close();
		return new String(output.toByteArray());
	}
	
	public static URI createURI(String text) throws URIException, URISyntaxException {
		final URI uri = new URI(text);
		return uri.normalize();
	}
	
	/*
	 * main di TEST
	 */
	public static void main(String[] args) throws URISyntaxException, IOException { }

}
