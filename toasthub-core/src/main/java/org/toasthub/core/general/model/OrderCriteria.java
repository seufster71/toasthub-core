package org.toasthub.core.general.model;

public class OrderCriteria {

	protected String orderColumn;
	protected String orderDir;
	
	public OrderCriteria() {}

	public String getOrderColumn() {
		return orderColumn;
	}

	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}

	public String getOrderDir() {
		return orderDir;
	}

	public void setOrderDir(String orderDir) {
		this.orderDir = orderDir;
	};

}
