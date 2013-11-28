package com.openDams.documental.controller;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.utility.StringsUtils;

public class RiferimentiController implements Controller {
	private String resolverUrlNir;
	private String resolverUrlAC;
	private Properties tidyProps;

	public static void main(String[] args) throws ClientProtocolException, IOException {
		// testoPlain =
		// StringsUtils.clearMessyCode(StringsUtils.postForString(new URL(""),
		// ""));

		HttpClient httpclient = new DefaultHttpClient();

		HttpGet httppost = new HttpGet("http://www.regedfdsta.com");

		HttpResponse response = httpclient.execute(httppost);
		int httpResult = response.getStatusLine().getStatusCode();
		String testoPlain;
		System.out.println(httpResult);

		if (httpResult < 400) {
			testoPlain = "ok";
		} else {
			testoPlain = "error";
		}
		System.out.println(testoPlain);

	}

	public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = new ModelAndView("documental/simpleResponse");
		String testoPlain = req.getParameter("testo");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		HttpClient httpclient = new DefaultHttpClient();

		if (testoPlain != null) {
			testoPlain = testoPlain.replaceAll("\\/([0-9]+=)\\/([0-9]+=)\\/UE", "/$1/$2/CE");
		}

		// props.load(req.getSession().getServletContext().getResourceAsStream("../Tidy.properties"));

		if (req.getParameter("parser").equals("nir")) {

			try {
				testoPlain = testoPlain.replaceAll("/UE", "/CE");

				HttpPost httppost = new HttpPost(resolverUrlNir);
				nameValuePairs.add(new BasicNameValuePair("blnDea", req.getParameter("blnDea")));
				nameValuePairs.add(new BasicNameValuePair("docType", req.getParameter("docType")));
				nameValuePairs.add(new BasicNameValuePair("testo", testoPlain));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpclient.execute(httppost);
				String httpResult = EntityUtils.toString(response.getEntity());
				testoPlain = StringsUtils.clearMessyCode(httpResult);
				System.out.println(resolverUrlNir + " " + response.getStatusLine().getStatusCode());
				System.out.println(testoPlain);
				if (response.getStatusLine().getStatusCode() > 399) {
					testoPlain = "";
					throw new Exception(resolverUrlNir + " - si è verificato un errore");
				}

				testoPlain = testoPlain.replaceAll("/CE", "/UE");
				mav.addObject("errorMsg", "");
			} catch (IOException e) {
				mav.addObject("errorMsg", e.getMessage());
				return mav;
			}

		} else if (req.getParameter("parser").equals("ac")) {
			try {

				HttpPost httppost = new HttpPost(resolverUrlAC);
				nameValuePairs.add(new BasicNameValuePair("legislatura", 17 + ""));
				nameValuePairs.add(new BasicNameValuePair("tipoLink", req.getParameter("tipoLink")));
				nameValuePairs.add(new BasicNameValuePair("xpath", req.getParameter("xpath")));
				nameValuePairs.add(new BasicNameValuePair("attivita", testoPlain));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpclient.execute(httppost);
				String httpResult = EntityUtils.toString(response.getEntity());
				testoPlain = StringsUtils.clearMessyCode(httpResult);

		

				mav.addObject("errorMsg", "");

			} catch (IOException e) {
				mav.addObject("errorMsg", e.getMessage());
				return mav;
			}
		} else if (req.getParameter("parser").equals("justTest")) {
			try {

				HttpGet httppost = new HttpGet(req.getParameter("url"));

				HttpResponse response = httpclient.execute(httppost);
				int httpResult = response.getStatusLine().getStatusCode();
				System.out.println("----------------- " + httpResult);
				if (httpResult < 400) {
					testoPlain = "ok";
				} else {
					testoPlain = "error";
				}

				mav.addObject("errorMsg", "");
			} catch (Exception e) {
				testoPlain = "error";
			}
		} else if (req.getParameter("parser").equals("webUrl")) {

			HttpGet httppost = new HttpGet(req.getParameter("url"));

			HttpResponse response = httpclient.execute(httppost);
			testoPlain = EntityUtils.toString(response.getEntity());

	//			System.out.println(testoPlain);
			if (response.getStatusLine().getStatusCode() > 399) {
				testoPlain = "";
				mav.addObject("errorMsg", "error: " + response.getStatusLine().getStatusCode());
			} else {
				mav.addObject("errorMsg", "");
			}

		}
		// mav.addObject("urls", estraiUrl(testoPlain));
		testoPlain = StringsUtils.clearMessyCode(testoPlain);
		/*
		 * System.out.println(
		 * "----------------------------------------------------------");
		 * System.
		 * out.println("----------------------------------------------------------"
		 * ); System.out.println(testoPlain); System.out.println(
		 * "----------------------------------------------------------");
		 * System.
		 * out.println("----------------------------------------------------------"
		 * );
		 */
		mav.addObject("content", testoPlain);
		return mav;
	}

	public void setResolverUrlNir(String resolverUrlNir) {
		this.resolverUrlNir = resolverUrlNir;
	}

	public void setResolverUrlAC(String resolverUrlAC) {
		this.resolverUrlAC = resolverUrlAC;
	}

	public void setTidyProps(Properties tidyProps) {
		this.tidyProps = tidyProps;
	}

}
