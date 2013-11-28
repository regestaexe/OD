package com.openDams.search_builder.utility;

public class StoredSearchContainer {
    public final static int TYPE_PANEL = 1;
    public final static int TYPE_TITLE = 2;
    public final static int TYPE_TEXT = 3;
    public final static int TYPE_INDEX = 4;
    private int type;
    private String text;
    private int idRecord;
    private int idArchive;
    private String panelTitle;
	public StoredSearchContainer() {
	}
	public StoredSearchContainer(int type, String text, int idRecord, int idArchive,String panelTitle) {
		super();
		this.type = type;
		this.text = text;
		this.idRecord = idRecord;
		this.idArchive = idArchive;
		this.panelTitle=panelTitle;
	}
	public int getType() {
		return type;
	}
	public String getText() {
		return text;
	}
	public int getIdRecord() {
		return idRecord;
	}
	public int getIdArchive() {
		return idArchive;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setIdRecord(int idRecord) {
		this.idRecord = idRecord;
	}
	public void setIdArchive(int idArchive) {
		this.idArchive = idArchive;
	}
	public String getPanelTitle() {
		return panelTitle;
	}
	public void setPanelTitle(String panelTitle) {
		this.panelTitle = panelTitle;
	}

}
