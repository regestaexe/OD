package com.openDams.dao;

import com.openDams.services.OpenDamsService;

public class OpenDamsServiceProvider {
	private static OpenDamsService service ;

	public static OpenDamsService getService() {
		return service;
	}
	public void setService(OpenDamsService service) {
		OpenDamsServiceProvider.service = service;
	}
}
