package com.openDams.search.configuration;

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
	private SearchConfiguration searchConfiguration;
	private HashMap<Integer, ArrayList<Object>> elements_map = null;

	public ConfigurationReader() throws FileNotFoundException, DocumentException {
	}

	public HashMap<Integer, ArrayList<Object>> getElementsMap() throws ConfigurationException {
		if (elements_map != null && searchConfiguration.isUse_test_settings()==false) {
			return elements_map;
		}
		try {
			ConfigurationXMLReader configurationXMLReader = null;
			String path = "";
			if (!searchConfiguration.isUse_external_conf_location()) {
				path += servletContext.getRealPath("");
			}
			path += searchConfiguration.getConfiguration_location();
			File file = new File(path);
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					Integer integer = 0;
					try {
						integer = new Integer(files[i].getName());
					} catch (Exception de) {
						System.err.println("[openDams] - ConfigurationReader.getElementsMap() - skipping " + files[i].getName());
					}
					File fileConf = new File(files[i].getAbsolutePath() + "/" + searchConfiguration.getFile_name());
					configurationXMLReader = new ConfigurationXMLReader(new FileInputStream(fileConf), Element.class, true);
					if (elements_map == null)
						elements_map = new HashMap<Integer, ArrayList<Object>>();
					elements_map.put(integer, configurationXMLReader.getObjects());

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

	public ArrayList<Object> getFilteredElementsList(int idCompany, String archives) throws ConfigurationException {
		ArrayList<Object> elements_list = getElementsMap().get(idCompany);
		ArrayList<Object> filteredElements = null;
		for (int i = 0; i < elements_list.size(); i++) {
			boolean isIn = true;
			if (!((Element) elements_list.get(i)).getArchives().equalsIgnoreCase("all")) {
				String elementArchives = "#" + ((Element) elements_list.get(i)).getArchives().replaceAll(";", ";#") + ";";
				String[] requestArchives = archives.split(";");
				for (int j = 0; j < requestArchives.length; j++) {
					if (elementArchives.indexOf("#" + requestArchives[j] + ";") == -1) {
						isIn = false;
						break;
					}
				}
			}
			if (isIn) {
				if (filteredElements == null)
					filteredElements = new ArrayList<Object>();
				filteredElements.add(elements_list.get(i));
			}
		}
		return filteredElements;
	}

	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public SearchConfiguration getSearchConfiguration() {
		return searchConfiguration;
	}

	public void setSearchConfiguration(SearchConfiguration searchConfiguration) {
		this.searchConfiguration = searchConfiguration;
	}

}
