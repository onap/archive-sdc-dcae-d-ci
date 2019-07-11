/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 - 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

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
