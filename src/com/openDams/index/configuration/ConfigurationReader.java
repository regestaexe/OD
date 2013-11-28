package com.openDams.index.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentException;
import org.springframework.web.context.ServletContextAware;
import org.xml.sax.SAXException;

import com.openDams.configuration.ConfigurationException;
import com.openDams.configuration.ConfigurationXMLReader;

public class ConfigurationReader implements ServletContextAware {
	private ServletContext servletContext;
	private HashMap<Integer, ArrayList<Object>> elements_map = null;
	private IndexConfiguration indexConfiguration;

	public ConfigurationReader() throws FileNotFoundException, DocumentException {
	}

	public HashMap<Integer, ArrayList<Object>> getElementsMap() throws ConfigurationException {
		if (elements_map != null && indexConfiguration.isUse_test_settings()==false) {
			return elements_map;
		}
		try {
			ConfigurationXMLReader configurationXMLReader = null;
			String path = "";
			if (!indexConfiguration.isUse_external_conf_location()) {
				path += servletContext.getRealPath("");
			}
			path += indexConfiguration.getConfiguration_location();
			File file = new File(path);
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					File[] filesConf = files[i].listFiles();
					for (int j = 0; j < filesConf.length; j++) {
						if (filesConf[j].getName().startsWith("index")) {
							Integer integer = new Integer(files[i].getName());
							configurationXMLReader = new ConfigurationXMLReader(new FileInputStream(filesConf[j].getAbsolutePath()), Element.class, true);
							if (elements_map == null)
								elements_map = new HashMap<Integer, ArrayList<Object>>();
							elements_map.put(integer, configurationXMLReader.getObjects());
						}
					}
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return elements_map;
	}

	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public IndexConfiguration getIndexConfiguration() {
		return indexConfiguration;
	}

	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}
}
