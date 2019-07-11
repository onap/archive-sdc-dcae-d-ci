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

import com.aventstack.extentreports.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.onap.dcae.ci.config.Configuration;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.entities.composition.services.Vfi;
import org.onap.dcae.ci.entities.sdc.*;
import org.onap.dcae.ci.report.Report;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SdcInternalApiClient extends BaseRestUtils {
	
	private static Gson gson = (new GsonBuilder()).create();
	private static String defaultUserId = DcaeRestClient.getDefaultUserId();
	private static String adminUserId = DcaeRestClient.getDefaultAdminId();
	private static Configuration configuration = ConfigurationReader.getConfiguration();
	

	public static SdcService createService() throws IOException {
		String url = getApiUrl("services");
		SdcService service = SdcComponentFactory.getDefaultService();
		Report.log(Status.INFO, "Going to create service with name " + service.getName());
		String serviceBodyJson = gson.toJson(service);
		RestResponse res = sendPost(url, serviceBodyJson, defaultUserId, acceptHeaderData);
		Report.logDebug("Response:", res);
		assertThat(res.getStatusCode()).isEqualTo(201);
		return gson.fromJson(res.getResponse(), SdcService.class);
	}


	public static SdcResource createVf() throws IOException {
		String url = getApiUrl("resources");
		SdcResource defaultVf = SdcComponentFactory.getDefaultResource();
		String resourceBodyJson = gson.toJson(defaultVf);
		RestResponse res = sendPost(url, resourceBodyJson, defaultUserId, acceptHeaderData);
		Report.logDebug("Response:", res);
		assertThat(res.getStatusCode()).isEqualTo(201);
		return gson.fromJson(res.getResponse(), SdcResource.class);
	}
	

	public static SdcResource checkinVf(SdcResource vf) throws IOException {
		RestResponse response = changeResourceLifeCycleState(vf.getUniqueId(), "checkin");
		Report.logDebug("Response:", response);
		assertThat(response.getStatusCode()).isEqualTo(200);
		return gson.fromJson(response.getResponse(), SdcResource.class);
	}
	

	public static Vfi createVfi(SdcService service, SdcResource vf) throws IOException {
		String url = getApiUrl(String.format("services/%s/resourceInstance", service.getUniqueId()));
		SdcComponentInstance vfi = new SdcComponentInstance(vf);
		String requestBodyJson = gson.toJson(vfi);
		RestResponse createResourceInstance = sendPost(url, requestBodyJson, defaultUserId, acceptHeaderData);
		Report.logDebug("Response:", createResourceInstance);
		assertThat(createResourceInstance.getStatusCode()).isEqualTo(201);
		return new Vfi(DcaeUtil.getValueFromJsonResponse(createResourceInstance.getResponse(), "name"), service);
	}

	public static RestResponse changeResourceLifeCycleState(String assetUniqueId, String lifeCycleOperation) throws IOException  {
		String url = getApiUrl(String.format("resources/%s/lifecycleState/%s", assetUniqueId, lifeCycleOperation));
		return sendPost(url, "{\"userRemarks\":\"Ci lifecycle operation\"}", defaultUserId, acceptHeaderData);
	}

	// DELETE - Clean up //

	public static RestResponse deleteAssetFromSdc(String context, String uniqueId) throws IOException {
		String url = getApiUrl(String.format("%s/%s", context, uniqueId));
		return sendDelete(url, adminUserId);
	}

	public static RestResponse deleteMarkedResources() throws IOException {

		String url = String.format("%s:%s/sdc2/rest/v1/inactiveComponents/resource", configuration.getSdcBeHost(), configuration.getSdcBePort());
		return sendDelete(url, adminUserId);
	}

	public static RestResponse deleteMarkedServices() throws IOException {
		String url = String.format("%s:%s/sdc2/rest/v1/inactiveComponents/service", configuration.getSdcBeHost(), configuration.getSdcBePort());
		return sendDelete(url, adminUserId);
	}

	public static Map<String, List<SdcComponent>> getAssetsByUser(String userId) throws IOException {
		String url = String.format("%s:%s/sdc2/rest/v1/followed", configuration.getSdcBeHost(), configuration.getSdcBePort());
		RestResponse restResponse = sendGet(url, userId);
		return 200 == restResponse.getStatusCode() ? gson.fromJson(restResponse.getResponse(), new TypeToken<Map<String, List<SdcComponent>>>(){}.getType()) : new HashMap<>();
	}

	public static SdcComponentMetadata getAssetMetadata(String context, String uniqueId, String userId) throws IOException {
		String url = getApiUrl(String.format("%s/%s/filteredDataByParams?include=metadata", context, uniqueId));
		RestResponse restResponse = sendGet(url, userId);
		return gson.fromJson(restResponse.getResponse(), SdcComponentMetadata.class);
	}

}
