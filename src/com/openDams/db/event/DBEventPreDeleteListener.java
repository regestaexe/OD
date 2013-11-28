package com.openDams.db.event;

import java.io.Serializable;

import org.hibernate.cfg.Configuration;
import org.hibernate.event.Initializable;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;

import com.openDams.bean.Relations;


public class DBEventPreDeleteListener extends DBEventListener implements Serializable,Initializable,PreDeleteEventListener{


	private static final long serialVersionUID = -4638389810690052022L;

	public void initialize(Configuration configuration) {
	}

	public boolean onPreDelete(PreDeleteEvent arg0) {
		if(arg0.getEntity() instanceof Relations){
				Relations relations = (Relations)arg0.getEntity();
				try {
					relationsManager.removeXmlRelation(relations.getId().getRefIdRelationType(),relations.getId().getRefIdRecord1(),relations.getId().getRefIdRecord2());
				} catch (Exception e) {						
					e.printStackTrace();
				}
		}
		return true;
	}
}
