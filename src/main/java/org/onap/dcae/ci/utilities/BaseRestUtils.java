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

package org.onap.dcae.ci.utilities;

import org.onap.dcae.ci.config.Configuration;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.enums.HttpHeaderEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BaseRestUtils {

	protected static final String acceptHeaderData = "application/json";
	protected static final String contentTypeHeaderData = "application/json";

	BaseRestUtils() {
	}

	static String getApiUrl(String path) {
		Configuration configuration = SetupReport.getConfiguration();
		return String.format("%s:%s/sdc2/rest/v1/catalog/%s", configuration.getSdcBeHost(), configuration.getSdcBePort(), path);
	}

	private static Map<String, String> prepareHeadersMap(String userId) {
		return prepareHeadersMap(userId, acceptHeaderData);
	}

	private static Map<String, String> prepareHeadersMap(String userId, String accept) {
		Map<String, String> headersMap = new HashMap<>();

		headersMap.put(HttpHeaderEnum.CONTENT_TYPE.getValue(), contentTypeHeaderData);

		if (accept != null) {
			headersMap.put(HttpHeaderEnum.ACCEPT.getValue(), accept);
		}

		if (userId != null) {
			headersMap.put(HttpHeaderEnum.USER_ID.getValue(), userId);
		}

		return headersMap;
	}

	static RestResponse sendGet(String url, String userId) throws IOException {
		return sendGet(url, userId, (Map) null);
	}

	protected static RestResponse sendGet(String url, String userId, Map<String, String> additionalHeaders) throws IOException {
		Map<String, String> headersMap = prepareHeadersMap(userId);
		if (additionalHeaders != null) {
			headersMap.putAll(additionalHeaders);
		}

		HttpRequest http = new HttpRequest();
		return http.httpSendGet(url, headersMap);
	}

	static RestResponse sendPut(String url, String userBodyJson, String userId, String cont) throws IOException {
		Map<String, String> headersMap = prepareHeadersMap(userId, cont);
		HttpRequest http = new HttpRequest();
		return http.httpSendByMethod(url, "PUT", userBodyJson, headersMap);
	}

	static RestResponse sendPost(String url, String userBodyJson, String userId, String accept) throws IOException {
		return sendPost(url, userBodyJson, userId, accept, (Map) null);
	}

	private static RestResponse sendPost(String url, String userBodyJson, String userId, String accept, Map<String, String> additionalHeaders) throws IOException {
		Map<String, String> headersMap = prepareHeadersMap(userId, accept);
		if (additionalHeaders != null) {
			headersMap.putAll(additionalHeaders);
		}

		HttpRequest http = new HttpRequest();
		return http.httpSendPost(url, userBodyJson, headersMap);
	}

	static RestResponse sendDelete(String url, String userId) throws IOException {
		return sendDelete(url, userId, (Map) null);
	}

	private static RestResponse sendDelete(String url, String userId, Map<String, String> additionalHeaders) throws IOException {
		Map<String, String> headersMap = prepareHeadersMap(userId);
		if (additionalHeaders != null) {
			headersMap.putAll(additionalHeaders);
		}

		HttpRequest http = new HttpRequest();
		return http.httpSendDelete(url, headersMap);
	}

}
