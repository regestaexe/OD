package com.openDams.title.configuration;


public class OpenDamsTitleManagerProvider {
	private static TitleManager titleManager;

	public static TitleManager getTitleManager() {
		return titleManager;
	}
	public void setTitleManager(TitleManager titleManager) {
		OpenDamsTitleManagerProvider.titleManager = titleManager;
	}
}
