package com.openDams.db.event;

import java.io.Serializable;

import org.hibernate.cfg.Configuration;
import org.hibernate.event.Initializable;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;

import com.openDams.bean.Relations;
import com.openDams.configuration.ConfigurationException;

public class DBEventPreInsertListener extends DBEventListener implements Serializable, Initializable, PreInsertEventListener {

	private static final long serialVersionUID = 8628132333422887069L;

	public void initialize(Configuration configuration) {
		try {
			elements_map = indexConfiguration.getConfigurationIndexReader().getElementsMap();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public boolean onPreInsert(PreInsertEvent arg0) {
		if (arg0.getEntity() instanceof Relations) {
			Relations relations = (Relations) arg0.getEntity();
			try {
				System.out.println("DBEventInsertListener.onPostInsert() relations: " + relations.getId().getRefIdRecord1() + " -> " + relations.getId().getRefIdRecord2() + " (tipo: " + relations.getId().getRefIdRelationType() + ")");
				relationsManager.insertXmlRelation(relations.getId().getRefIdRelationType(), relations.getId().getRefIdRecord1(), relations.getId().getRefIdRecord2());
			} catch (Exception e) {
				System.out.println("################################ERRORE RELAZIONE POST INSERT##############################");
				//e.printStackTrace();
			}
		}
		return true;
	}
}
