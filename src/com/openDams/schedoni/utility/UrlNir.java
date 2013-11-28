package com.openDams.schedoni.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.openDams.utility.StringsUtils;

public class UrlNir {

	private static Properties props = new Properties();

	public static void setProperties(Properties properties) {
		props = properties;
	}

	public static String postForString(URL url, String data) throws IOException {
		String result = "";
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result += line + "\n";
		}
		wr.close();
		rd.close();

		result = result.replaceAll("<a [^>]*urn:nir:regione.:[^>]*>([^>]*)</a>", "$1");
		return result;
	}

	public static String[] estraiUrl(String input) {
		final Pattern p = Pattern.compile("<a(.*?)href=\"(.*?)\"(.*)?");
		Matcher m = p.matcher(input);
		Set<String> output = new HashSet<String>();
		while (m.find()) {
			// System.out.println("-->"+m.group(2));
			// output.add(m.group(2).replaceAll("&",
			// "&amp;").replaceAll("&amp;amp;", "&amp;"));
			output.add(m.group(2).replaceAll("&amp;", "&"));
			input = m.group(3);
			m = p.matcher(input);
		}
		if (output.size() > 0)
			return output.toArray(new String[output.size()]);
		else
			return null;
	}

 
	public static void main(String[] args) {
	}

}
