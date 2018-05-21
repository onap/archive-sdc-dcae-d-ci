package org.onap.dcae.ci.enums;

public enum HttpHeaderEnum {
	Content_MD5("Content-MD5"),
	USER_ID("USER_ID"),
	CONTENT_TYPE("Content-Type"),
	ACCEPT("Accept"),
	X_ECOMP_REQUEST_ID_HEADER("X-ECOMP-RequestID"),
	CACHE_CONTROL("Cache-Control"),
	X_ECOMP_INSTANCE_ID("X-ECOMP-InstanceID"),
	AUTHORIZATION("Authorization"),
	CONTENT_LENGTH("Content-Length"),
	X_ECOMP_SERVICE_ID_HEADER("X-ECOMP-ServiceID");


	String value;

	private HttpHeaderEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
