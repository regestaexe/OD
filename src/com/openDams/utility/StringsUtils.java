package com.openDams.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;

import com.regesta.framework.xml.XMLBuilder;
import com.regesta.framework.xml.exception.XMLException;

public class StringsUtils {
	public static String escapeJson(String value) {
		String s = value;
		s = StringEscapeUtils.escapeXml(s);
		s = s.replaceAll("\r", "");
		s = s.replaceAll("\n", " ");
		s = s.replaceAll("&gt;", ">");
		s = s.replaceAll("&lt;", "<");
		s = s.replaceAll("&apos;", "'");
		s = s.replaceAll("\\\\", "\\\\\\\\");
		s = s.replaceAll("&quot;", "\\\\\"");
		return "\"" + s + "\"";
	}

	public static String escapeJsonNoXML(String value) {
		String s = value;
		s = s.replaceAll("\"", "&quot;");
		s = s.replaceAll("\r", "");
		s = s.replaceAll("\n", " ");
		s = s.replaceAll("&gt;", ">");
		s = s.replaceAll("&lt;", "<");
		s = s.replaceAll("&apos;", "'");
		s = s.replaceAll("\\\\", "\\\\\\\\");
		s = s.replaceAll("&quot;", "\\\\\"");
		return "\"" + s + "\"";
	}

	public static String postForString(URL url, String data) throws IOException {
		String result = "";
		// System.out.println();
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();

		// Get the response
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result += line + "\n";
		}
		wr.close();
		rd.close();

		return result;
	}

	public static String[] estraiUrl(String input) {
		Pattern p = Pattern.compile("<a(.*?)href=\"(.*?)\"(.*)?");
		Matcher m = p.matcher(input);

		List<String> output = new ArrayList<String>();
		while (m.find()) {
			output.add(m.group(2).replaceAll("&", "&amp;"));
			input = m.group(3);
			m = p.matcher(input);
		}
		if (output.size() > 0)
			return output.toArray(new String[output.size()]);
		else
			return null;
	}

	public static String[] estraiAllUrl(String input) {
		Pattern p = Pattern.compile("(http\\://[:/?#\\[\\]@%!$&'()*+,;=a-zA-Z0-9._\\-~]+)");
		Matcher m = p.matcher(input);

		List<String> output = new ArrayList<String>();
		while (m.find()) {
			output.add(m.group(1));

		}
		if (output.size() > 0)
			return output.toArray(new String[output.size()]);
		else
			return null;
	}

 

	 
	static public String clearMessyCode(String text) {
		// System.out.println(text);
		StringBuffer stringBuffer = new StringBuffer();

		try {
			Pattern regex = Pattern.compile("(href=\"[^\"]*)<a .*?>(.*?)</a>(.*?>)(.*?</a>)");
			Matcher regexMatcher = regex.matcher(text);
			while (regexMatcher.find()) {
				regexMatcher.appendReplacement(stringBuffer, "$1$2$3$4");
			}
			regexMatcher.appendTail(stringBuffer);
		} catch (PatternSyntaxException ex) {
		}
		text = stringBuffer.toString();
		stringBuffer = new StringBuffer();

		try {
			Pattern regex = Pattern.compile("(<a [^<]*?>[^<]*?)(<a [^<]*?>)([^<]*?)(</a>)([^<]*?</a>)");
			Matcher regexMatcher = regex.matcher(text);
			while (regexMatcher.find()) {
				regexMatcher.appendReplacement(stringBuffer, "$1$3$5");
			}
			regexMatcher.appendTail(stringBuffer);
		} catch (PatternSyntaxException ex) {
		}
		text = stringBuffer.toString();

		return text;
	}

	public static String[] splitStringEvery(String s, int interval) {
		int arrayLength = (int) Math.ceil(((s.length() / (double) interval)));
		String[] result = new String[arrayLength];
		int j = 0;
		int lastIndex = result.length - 1;
		for (int i = 0; i < lastIndex; i++) {
			result[i] = s.substring(j, j + interval);
			j += interval;
		}
		result[lastIndex] = s.substring(j);
		return result;
	}

	public static String wordWrap(String input, int width, Locale locale) {
		// protect ourselves
		if (input == null) {
			return "";
		} else if (width < 5) {
			return input;
		} else if (width >= input.length()) {
			return input;
		}

		StringBuilder buf = new StringBuilder(input);
		boolean endOfLine = false;
		int lineStart = 0;

		for (int i = 0; i < buf.length(); i++) {
			if (buf.charAt(i) == '\n') {
				lineStart = i + 1;
				endOfLine = true;
			}
			if (buf.charAt(i) == '\u200e') {
				lineStart = i + 1;
			}
			// handle splitting at width character
			if (i > lineStart + width - 1) {
				if (!endOfLine) {
					int limit = i - lineStart - 1;
					BreakIterator breaks = BreakIterator.getLineInstance(locale);
					breaks.setText(buf.substring(lineStart, i));
					int end = breaks.last();

					// if the last character in the search string isn't a space,
					// we can't split on it (looks bad). Search for a previous
					// break character
					if (end == limit + 1) {
						if (!Character.isWhitespace(buf.charAt(lineStart + end))) {
							end = breaks.preceding(end - 1);
						}
					}

					// if the last character is a space, replace it with a \n
					if (end != BreakIterator.DONE && end == limit + 1) {
						buf.replace(lineStart + end, lineStart + end + 1, "\n");
						lineStart = lineStart + end;
					}
					// otherwise, just insert a \n
					else if (end != BreakIterator.DONE && end != 0) {
						buf.insert(lineStart + end, '\n');
						lineStart = lineStart + end + 1;
					} else {
						buf.insert(i, '\n');
						lineStart = i + 1;
					}
				} else {
					buf.insert(i, '\n');
					lineStart = i + 1;
					endOfLine = false;
				}
			}
		}

		return buf.toString();
	}

	public static String html2text(String html) {
		return Jsoup.parse(html).text();
	}

	public static ArrayList<String> getHtmlTags(String html) {
		int beginIndex = 0;
		ArrayList<String> tags = new ArrayList<String>();
		while (beginIndex != -1) {
			beginIndex = html.indexOf("<", 0);
			int endIndex = html.indexOf(">", beginIndex + 1);
			String htmlTag = "";
			try {
				if (beginIndex != -1) {
					htmlTag = html.substring(beginIndex, endIndex + 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!htmlTag.trim().equals("")) {
				tags.add(htmlTag);
			}
			html = html.substring(endIndex + 1, html.length());
		}
		return tags;
	}

	public static String wordWrapHtml(String html, int width, Locale locale) {
		String result = html;
		int beginIndex = 0;
		ArrayList<String> tags = new ArrayList<String>();
		while (beginIndex != -1) {
			beginIndex = html.indexOf("<", 0);
			int endIndex = html.indexOf(">", beginIndex + 1);
			String htmlTag = "";
			try {
				if (beginIndex != -1) {
					htmlTag = html.substring(beginIndex, endIndex + 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (!htmlTag.trim().equals("")) {
				tags.add(htmlTag);
				if (result.startsWith(htmlTag)) {
					result = "\u200e" + org.apache.commons.lang.StringUtils.substringAfter(result, htmlTag);
				} else {
					result = org.apache.commons.lang.StringUtils.substringBefore(result, htmlTag) + "\u200e" + org.apache.commons.lang.StringUtils.substringAfter(result, htmlTag);
				}
				html = html.substring(endIndex + 1, html.length());
			}
		}
		result = wordWrap(result, width, locale);
		for (int i = 0; i < tags.size(); i++) {
			result = org.apache.commons.lang.StringUtils.replaceOnce(result, "\u200e", tags.get(i));
		}
		return result;
	} 
	public static void main(String[] args) { }
}
