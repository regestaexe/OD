package com.openDams.utility.tidy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Properties;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import com.openDams.utility.dom.XMLHelper;
import com.openDams.utility.pdf.PDFHelper;

/**
 * Facility class per jTidy
 * 
 * @author seralf
 * 
 */
public class XHTMLTidy {

	/**
	 * metodo per la conversione del charset di una String
	 * 
	 * @param input
	 * @param charset
	 * @throws XHTMLTidyException
	 */
	private static String decode(final InputStream input, final String charset) throws XHTMLTidyException {
		try {
			final CharsetDecoder charsetDecoder = Charset.forName(charset).newDecoder();
			charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE);
			charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPORT);
			final Reader reader = new InputStreamReader(input, charsetDecoder);
			final CharBuffer buffer = CharBuffer.allocate(input.available());
			reader.read(buffer);
			buffer.compact();
			final String text = new String(buffer.array());
			buffer.clear();
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XHTMLTidyException(e);
		}
	}

	/**
	 * metodo che restituisce il contenuto HTML di un certo file, in base
	 * all'encoding
	 * 
	 * @param fileName
	 * @param encoding
	 * @throws XHTMLTidyException
	 */
	public static String getHTMLFileContent(final File file, final String encoding) throws XHTMLTidyException {
		try {
			final FileInputStream fis = new FileInputStream(file);
			final InputStreamReader isr = new InputStreamReader(fis, encoding);
			final CharBuffer buf = CharBuffer.allocate(fis.available());
			isr.read(buf);
			final String testHTML = new String(buf.array());
			isr.close();
			fis.close();
			return testHTML;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XHTMLTidyException(e);
		}
	}

	// NOTA: da qui non viene generato un warning sul carattere 0x0
	@Deprecated
	public static String loadHTML(File f, String encoding) throws XHTMLTidyException {
		try {
			final FileInputStream fis = new FileInputStream(f);
			final BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = null;
			final StringBuilder stringBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}
			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XHTMLTidyException(e);
		}
	}

	/**
	 * 
	 * @param in
	 *            - InputStream di lettura, che contiene l'html da ripulire
	 * @param out
	 *            - OutputStream dove viene generato il risultato in xhtml
	 * @param props
	 *            - Properties associate a jTidy
	 * @param encoding
	 *            - encoding da utilizzare nei vari passaggi di conversione
	 * @throws XHTMLTidyException
	 */
	public static Document toXhtml(InputStream in, OutputStream out, Properties props, String encodingIn, String encodingOut) throws XHTMLTidyException {
		// se non specifico encoding, uso UTF-8
		try {
			if (encodingOut == null || encodingOut.equals(""))
				encodingOut = "UTF-8";
			final InputStreamReader isr = new InputStreamReader(in, encodingIn);
			final OutputStreamWriter osw = new OutputStreamWriter(out, encodingOut);
			final Tidy tidy = new Tidy();
			tidy.setConfigurationFromProps(props);
			final Document doc = tidy.parseDOM(isr, osw);
			isr.close();
			osw.close();
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XHTMLTidyException(e);
		}
	}

	/**
	 * @param input
	 *            - InputStream dei byte da formattare con jTidy
	 * @param props
	 *            - properties per jTidy
	 * @param encoding
	 *            - encoding dell'input
	 * @return - DOM dell'xhtml generato dall'input
	 */
	public static Document htmlToDocument(InputStream input, Properties props, String encodingIn, String encodingOut) throws XHTMLTidyException {
		try {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final Document doc = XHTMLTidy.toXhtml(input, bos, props, encodingIn, encodingOut);
		//	System.out.println("XHTMLTidy.htmlToXHtml() ****************************************************************************");
		//	System.out.println(XMLHelper.write(doc, System.out, "UTF-8"));
		//	System.out.println("XHTMLTidy.htmlToXHtml() ****************************************************************************");
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XHTMLTidyException(e);
		}
	}

	/**
	 * @param text
	 *            - testo da formattare con jTidy
	 * @param props
	 *            - properties per jTidy
	 * @param encoding
	 *            - encoding dell'input
	 * @return - testo dell'xhtml generato dall'input
	 * @throws XHTMLTidyException
	 */
	public static String toXhtml(final String text, Properties props, String encodingIn, String encodingOut) throws XHTMLTidyException {
		try {
			final ByteArrayInputStream bis = new ByteArrayInputStream(text.getBytes(encodingIn));
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// NOTA: in questo caso non serve tenere traccia del Document
			// generato
			Document doc = toXhtml(bis, bos, props, encodingIn, encodingOut);
			final String outText = new String(bos.toByteArray());
			bis.close();
			bos.close();
			return outText;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XHTMLTidyException(e);
		}
	}
	public static Document toXhtmlDoc(final String text, Properties props, String encodingIn, String encodingOut) throws XHTMLTidyException {
		try {
			final ByteArrayInputStream bis = new ByteArrayInputStream(text.getBytes(encodingIn));
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// NOTA: in questo caso non serve tenere traccia del Document
			// generato
			Document doc = toXhtml(bis, bos, props, encodingIn, encodingOut);
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XHTMLTidyException(e);
		}
	}
	public static Document toTidyDocument(final Document doc, final Properties properties, String encodingIn, String encodingOut) throws XHTMLTidyException {
		try {
			final DOMSource source = new DOMSource(doc);
			final StringWriter xmlAsWriter = new StringWriter();
			TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(xmlAsWriter));
			final ByteArrayInputStream in = new ByteArrayInputStream(xmlAsWriter.toString().getBytes(encodingIn));
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final Document docOut = XHTMLTidy.toXhtml(in, baos, properties, encodingIn, encodingOut);
			return docOut;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XHTMLTidyException(e);
		}
	}

	public static Document htmlToXHtml(final File htmlFile, final File xhtmlFile, final Properties properties, String encodingIn, String encodingOut) throws XHTMLTidyException {
		try {
			final FileInputStream fis = new FileInputStream(htmlFile);
			final FileOutputStream fos = new FileOutputStream(xhtmlFile);
			final Tidy tidy = new Tidy();
			tidy.setConfigurationFromProps(properties);
			final Document doc = XHTMLTidy.toXhtml(fis, fos, properties, encodingIn, encodingOut);

			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XHTMLTidyException(e);
		}
	}

	/**
	 * metodo per rimuovere caratteri unicode problematici per XML
	 * 
	 * @param in
	 *            - Stringa iniziale, contenente anche caratteri non validi per
	 *            XML
	 * @return - String restituita, dove abbiamo rimossi i caratteri non validi
	 */
	public static String stripNonValidXMLCharacters(String in) {
		final StringBuffer out = new StringBuffer();
		char current;
		if (in == null || ("".equals(in)))
			return "";
		for (int i = 0; i < in.length(); i++) {
			current = in.charAt(i);
			if (current == 0x0)
				System.err.println("WARNING: FOUND 0x0 char on index " + i);
			// eliminiamo in particolare 0x0
			if ((current == 0x9) || (current == 0xA) || (current == 0xD) || ((current >= 0x20) && (current <= 0xD7FF)) || ((current >= 0xE000) && (current <= 0xFFFD)) || ((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		return out.toString();
	}


	/*
	 * main di TEST
	 */
	public static void main(String[] args) { }

}
