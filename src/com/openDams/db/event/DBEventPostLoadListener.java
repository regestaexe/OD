package com.openDams.db.event;

import java.io.Serializable;

import org.hibernate.cfg.Configuration;
import org.hibernate.event.Initializable;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;


public class DBEventPostLoadListener extends DBEventListener implements Serializable,Initializable,PostLoadEventListener{


	private static final long serialVersionUID = -4638389810690052022L;

	public void initialize(Configuration configuration) {
	}

	public void onPostLoad(PostLoadEvent arg0) {
		System.out.println("DBEventPostLoadListener.onPostLoad()");
		/*if(arg0.getEntity() instanceof Records){
			Records records = (Records)arg0.getEntity();
			try {
				
			} catch (Exception e) {						
				e.printStackTrace();
			}
		}*/
	}

	
}
