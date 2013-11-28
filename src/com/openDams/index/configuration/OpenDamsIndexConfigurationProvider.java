package com.openDams.index.configuration;


public class OpenDamsIndexConfigurationProvider {
	private static IndexConfiguration indexConfiguration = null;

	public static IndexConfiguration getIndexConfiguration() {
		return indexConfiguration;
	}
	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		OpenDamsIndexConfigurationProvider.indexConfiguration = indexConfiguration;
	}
}
