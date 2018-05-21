package org.onap.dcae.ci.entities;

import java.util.List;
import java.util.Map;

public class RestResponse {
	Integer statusCode;
	String response;
	Map<String, List<String>> headerFields;
	String responseMessage;

	public RestResponse() {
	}

	public RestResponse(Integer errorCode, String response, Map<String, List<String>> headerFields, String responseMessage) {
		this.statusCode = errorCode;
		this.response = response;
		this.headerFields = headerFields;
		this.responseMessage = responseMessage;
	}

	public Integer getStatusCode() {
		return this.statusCode;
	}

	public void setStatusCode(Integer errorCode) {
		this.statusCode = errorCode;
	}

	public String getResponse() {
		return this.response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Map<String, List<String>> getHeaderFields() {
		return this.headerFields;
	}

	public void setHeaderFields(Map<String, List<String>> headerFields) {
		this.headerFields = headerFields;
	}

	public String getResponseMessage() {
		return this.responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String toString() {
		return "RestResponse [errorCode=" + this.statusCode + ", response=" + this.response + ", headerFields=" + this.headerFields + ", responseMessage=" + this.responseMessage + "]";
	}
}
