package com.onap.dcae.ci.ui.rule_editor;

import com.fasterxml.jackson.annotation.*;

public class TranslateResult {
	
	public enum Status {
		ok,
		error
	}
	
	private Status status;
	private String data;
	
	@JsonCreator
	public TranslateResult(
			@JsonProperty("status") Status status,
			@JsonProperty("data") String data) {
		
		this.status = status;
		this.data = data;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public String getData() {
		return data;
	}
	
}
