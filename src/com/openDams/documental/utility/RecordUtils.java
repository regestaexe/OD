package com.openDams.schedoni.utility;

import java.util.List;

import com.openDams.bean.Records;
import com.openDams.dao.OpenDamsServiceProvider;
import com.regesta.framework.xml.XMLReader;

public class RecordUtils {

	public static XMLReader getReaderFromAbout(String id) {
		XMLReader reader = null;
		try {
			Records record = ((List<Records>) OpenDamsServiceProvider.getService().getListFromSQL(Records.class, "SELECT * FROM records where xml_id='" + id + "';")).get(0);
			reader = record.getXMLReader();
		} catch (Exception e) {
		}
		return reader;
	}

}
