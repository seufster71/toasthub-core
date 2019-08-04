package org.toasthub.core.general.model;

public class SearchCriteria {

	protected String searchValue;
	protected String searchColumn;
	
	public SearchCriteria() {}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public String getSearchColumn() {
		return searchColumn;
	}

	public void setSearchColumn(String searchColumn) {
		this.searchColumn = searchColumn;
	};
	
	
}
