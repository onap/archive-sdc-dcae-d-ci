package org.onap.dcae.ci.utilities;

import org.apache.commons.io.IOUtils;
import org.onap.dcae.ci.entities.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequest {
	private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class.getName());

	public RestResponse httpSendGet(String url, Map<String, String> headers) throws IOException {

		RestResponse restResponse = new RestResponse();
		url = url.replaceAll("\\s", "%20");
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		// add request header
		if (headers != null) {
			for (Entry<String, String> header : headers.entrySet()) {
				String key = header.getKey();
				String value = header.getValue();
				con.setRequestProperty(key, value);
			}

		}

		int responseCode = con.getResponseCode();
		logger.debug("Send GET http request, url: {}",url);
		logger.debug("Response Code: {}",responseCode);

		StringBuffer response = new StringBuffer();
		String result;

		try {

			result = IOUtils.toString(con.getInputStream());
			response.append(result);

		} catch (Exception e) {
		}

		try {

			result = IOUtils.toString(con.getErrorStream());
			response.append(result);

		} catch (Exception e) {
		}

		logger.debug("Response body: {}" ,response);

		// print result

		restResponse.setStatusCode(responseCode);

		if (response != null) {
			restResponse.setResponse(response.toString());
		}

		restResponse.setStatusCode(responseCode);
		Map<String, List<String>> headerFields = con.getHeaderFields();
		restResponse.setHeaderFields(headerFields);
		String responseMessage = con.getResponseMessage();
		restResponse.setResponseMessage(responseMessage);

		con.disconnect();

		return restResponse;
	}


	public RestResponse httpSendByMethod(String url, String method, String body, Map<String, String> headers)
			throws IOException {

		RestResponse restResponse = new RestResponse();
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add request method
		con.setRequestMethod(method);

		// add request headers
		if (headers != null) {
			for (Entry<String, String> header : headers.entrySet()) {
				String key = header.getKey();
				String value = header.getValue();
				con.setRequestProperty(key, value);
			}

		}
		if (body != null && !body.isEmpty() && !method.equals("DELETE")) {
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(body);
			wr.flush();
			wr.close();
		}


		int responseCode = con.getResponseCode();
		logger.debug("Send {} http request, url: {}",method,url);
		logger.debug("Response Code: {}",responseCode);

		StringBuffer response = new StringBuffer();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			logger.debug("response body is null");
		}

		String result;
		try {

			result = IOUtils.toString(con.getErrorStream());
			response.append(result);

		} catch (Exception e2) {
			result = null;
		}
		logger.debug("Response body: {}",response);

		// print result

		restResponse.setStatusCode(responseCode);

		if (response != null) {
			restResponse.setResponse(response.toString());
		}
		Map<String, List<String>> headerFields = con.getHeaderFields();
		restResponse.setHeaderFields(headerFields);
		String responseMessage = con.getResponseMessage();
		restResponse.setResponseMessage(responseMessage);

		con.disconnect();
		return restResponse;

	}


	public RestResponse httpSendPost(String url, String body, Map<String, String> headers) throws IOException {
		return httpSendPost(url, body, headers, "POST");
	}


	public RestResponse httpSendPost(String url, String body, Map<String, String> headers, String methodType)
			throws IOException {

		RestResponse restResponse = new RestResponse();
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add request method
		con.setRequestMethod(methodType);

		// add request headers
		if (headers != null) {
			for (Entry<String, String> header : headers.entrySet()) {
				String key = header.getKey();
				String value = header.getValue();
				con.setRequestProperty(key, value);
			}
		}

		// Send post request
		if (body != null) {
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(body);
			wr.flush();
			wr.close();
		}

		int responseCode = con.getResponseCode();
		logger.debug("Send POST http request, url: {}",url);
		logger.debug("Response Code: {}",responseCode);

		StringBuffer response = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			logger.debug("response body is null");
		}

		String result;

		try {

			result = IOUtils.toString(con.getErrorStream());
			response.append(result);

		} catch (Exception e2) {
			result = null;
		}
		logger.debug("Response body: {}",response);

		// print result

		restResponse.setStatusCode(responseCode);

		if (response != null) {
			restResponse.setResponse(response.toString());
		}

		Map<String, List<String>> headerFields = con.getHeaderFields();
		restResponse.setHeaderFields(headerFields);
		String responseMessage = con.getResponseMessage();
		restResponse.setResponseMessage(responseMessage);

		con.disconnect();
		return restResponse;

	}

	public RestResponse httpSendPost(String url, String body) throws IOException {

		RestResponse restResponse = new RestResponse();
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// Send post request
		if (body != null) {
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(body);
			wr.flush();
			wr.close();
		}
		int responseCode = con.getResponseCode();
		StringBuilder response = new StringBuilder();

		try {
			response.append(IOUtils.toString(con.getInputStream(), "UTF-8"));
		} catch (Exception e) {
			logger.debug("response body is null");
		}

		String result;

		try {

			result = IOUtils.toString(con.getErrorStream());
			response.append(result);

		} catch (Exception e2) {
			result = null;
		}
		logger.debug("Response body: {}",response);

		// print result

		restResponse.setStatusCode(responseCode);

		if (response != null) {
			restResponse.setResponse(response.toString());
		}

		Map<String, List<String>> headerFields = con.getHeaderFields();
		restResponse.setHeaderFields(headerFields);
		String responseMessage = con.getResponseMessage();
		restResponse.setResponseMessage(responseMessage);

		con.disconnect();
		return restResponse;

	}


	public RestResponse httpSendDelete(String url, Map<String, String> headers) throws IOException {

		RestResponse restResponse = new RestResponse();
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		if (headers != null) {
			for (Entry<String, String> header : headers.entrySet()) {
				String key = header.getKey();
				String value = header.getValue();
				con.setRequestProperty(key, value);
			}

		}

		con.setDoOutput(true);
		con.setRequestMethod("DELETE");
		int responseCode = con.getResponseCode();
		logger.debug("Send DELETE http request, url: {}",url);
		logger.debug("Response Code: {}",responseCode);

		StringBuffer response = new StringBuffer();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			logger.debug("response body is null");
		}

		String result;

		try {

			result = IOUtils.toString(con.getErrorStream());
			response.append(result);

		} catch (Exception e2) {
			result = null;
		}
		logger.debug("Response body: {}",response);

		// print result

		restResponse.setStatusCode(responseCode);

		if (response != null) {
			restResponse.setResponse(response.toString());
		}

		restResponse.setStatusCode(con.getResponseCode());
		Map<String, List<String>> headerFields = con.getHeaderFields();
		restResponse.setHeaderFields(headerFields);
		String responseMessage = con.getResponseMessage();
		restResponse.setResponseMessage(responseMessage);

		con.disconnect();

		return restResponse;
	}

}
