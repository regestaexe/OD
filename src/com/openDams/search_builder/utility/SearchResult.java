package com.openDams.search_builder.utility;

public class SearchResult {
    private int idArchive;
    private String archiveLabel;
    private int totalHits = 0;
    private String query;
    private String order_by;
    private String sort_type;
    private String find_sons;
    private String file_search;
	public SearchResult() {
	}
	public SearchResult(int idArchive, String archiveLabel, int totalHits,String query,String order_by,String sort_type,String find_sons,String file_search) {
		super();
		this.idArchive = idArchive;
		this.archiveLabel = archiveLabel;
		this.totalHits = totalHits;
		this.query = query;
		this.order_by = order_by;
		this.sort_type = sort_type;
		this.find_sons = find_sons;
		this.file_search = file_search;
	}
	public int getIdArchive() {
		return idArchive;
	}
	public String getArchiveLabel() {
		return archiveLabel;
	}
	public int getTotalHits() {
		return totalHits;
	}
	public String getQuery() {
		return query;
	}
	public void setIdArchive(int idArchive) {
		this.idArchive = idArchive;
	}
	public void setArchiveLabel(String archiveLabel) {
		this.archiveLabel = archiveLabel;
	}
	public void setTotalHits(int totalHits) {
		this.totalHits = totalHits;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getOrder_by() {
		return order_by;
	}
	public void setOrder_by(String orderBy) {
		order_by = orderBy;
	}
	public String getSort_type() {
		return sort_type;
	}
	public void setSort_type(String sortType) {
		sort_type = sortType;
	}
	public String getFind_sons() {
		return find_sons;
	}
	public void setFind_sons(String findSons) {
		find_sons = findSons;
	}
	public String getFile_search() {
		return file_search;
	}
	public void setFile_search(String fileSearch) {
		file_search = fileSearch;
	}

}
