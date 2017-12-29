package org.toasthub.core.general.model;

import java.util.List;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonView;

public class ResponseStatus {

	private List<StatusMessage> info;
	private List<StatusMessage> warn;
	private List<StatusMessage> error;
	
	public ResponseStatus() {}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	public List<StatusMessage> getInfo() {
		return info;
	}
	public void setInfo(List<StatusMessage> info) {
		this.info = info;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	public List<StatusMessage> getWarn() {
		return warn;
	}
	public void setWarn(List<StatusMessage> warn) {
		this.warn = warn;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	public List<StatusMessage> getError() {
		return error;
	}
	public void setError(List<StatusMessage> error) {
		this.error = error;
	}
	
	
}
