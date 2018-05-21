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

	public BaseRestUtils() {
	}

	protected static String getApiUrl(String path) {
		Configuration configuration = SetupReport.getConfiguration();
		return String.format("%s:%s/sdc2/rest/v1/catalog/%s", configuration.getBeHost(), configuration.getBePort(), path);
	}

	protected static Map<String, String> prepareHeadersMap(String userId) {
		return prepareHeadersMap(userId, acceptHeaderData);
	}

	protected static Map<String, String> prepareHeadersMap(String userId, String accept) {
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

	protected static RestResponse sendGet(String url, String userId) throws IOException {
		return sendGet(url, userId, (Map) null);
	}

	protected static RestResponse sendGet(String url, String userId, Map<String, String> additionalHeaders) throws IOException {
		Map<String, String> headersMap = prepareHeadersMap(userId);
		if (additionalHeaders != null) {
			headersMap.putAll(additionalHeaders);
		}

		HttpRequest http = new HttpRequest();
		RestResponse getResourceResponse = http.httpSendGet(url, headersMap);
		return getResourceResponse;
	}

	protected static RestResponse sendPut(String url, String userBodyJson, String userId, String cont) throws IOException {
		Map<String, String> headersMap = prepareHeadersMap(userId, cont);
		HttpRequest http = new HttpRequest();
		RestResponse updateResourceResponse = http.httpSendByMethod(url, "PUT", userBodyJson, headersMap);
		return updateResourceResponse;
	}

	public static RestResponse sendPost(String url, String userBodyJson, String userId, String accept) throws IOException {
		return sendPost(url, userBodyJson, userId, accept, (Map) null);
	}

	protected static RestResponse sendPost(String url, String userBodyJson, String userId, String accept, Map<String, String> additionalHeaders) throws IOException {
		Map<String, String> headersMap = prepareHeadersMap(userId, accept);
		if (additionalHeaders != null) {
			headersMap.putAll(additionalHeaders);
		}

		HttpRequest http = new HttpRequest();
		RestResponse postResourceResponse = http.httpSendPost(url, userBodyJson, headersMap);
		return postResourceResponse;
	}

	protected static RestResponse sendDelete(String url, String userId) throws IOException {
		return sendDelete(url, userId, (Map) null);
	}

	protected static RestResponse sendDelete(String url, String userId, Map<String, String> additionalHeaders) throws IOException {
		Map<String, String> headersMap = prepareHeadersMap(userId);
		if (additionalHeaders != null) {
			headersMap.putAll(additionalHeaders);
		}

		HttpRequest http = new HttpRequest();
		RestResponse deleteResourceResponse = http.httpSendDelete(url, headersMap);
		return deleteResourceResponse;
	}

}
