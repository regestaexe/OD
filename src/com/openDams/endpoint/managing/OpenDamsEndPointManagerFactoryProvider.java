package com.openDams.endpoint.managing;


public class OpenDamsEndPointManagerFactoryProvider {
	private static EndPointManagerFactory endPointManagerFactory;

	public static EndPointManagerFactory getEndPointManagerFactory() {
		return endPointManagerFactory;
	}
	public void setEndPointManagerFactory(EndPointManagerFactory endPointManagerFactory) {
		OpenDamsEndPointManagerFactoryProvider.endPointManagerFactory = endPointManagerFactory;
	}
}
