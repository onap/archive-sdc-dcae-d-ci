package com.onap.dcae.ci.utilities;

import com.onap.dcae.ci.entities.composition.services.ServiceReqDetailsUpdated;
import com.onap.dcae.ci.entities.composition.services.Vfi;
import com.onap.dcae.ci.entities.sdc.SdcComponent;
import com.onap.dcae.ci.entities.sdc.SdcComponentMetadata;
import com.onap.dcae.ci.ui.setup.DcaeConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.openecomp.d2.ci.api.BaseRestUtils;
import org.openecomp.d2.ci.api.ElementFactory;
import org.openecomp.d2.ci.api.ServiceRestUtils;
import org.openecomp.d2.ci.api.VfRestUtils;
import org.openecomp.d2.ci.datatypes.*;
import org.openecomp.d2.ci.datatypes.http.RestResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SdcInternalApiClient extends BaseRestUtils {
	
	private static Gson gson = (new GsonBuilder()).create();
	private static User defaultUser = DcaeRestClient.getDefaultUser();
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static ServiceReqDetails createService() throws Exception {
		ServiceReqDetails defaultServiceOld = ElementFactory.getDefaultService();
		ServiceReqDetailsUpdated serviceReqDetailsUpdated = new ServiceReqDetailsUpdated(defaultServiceOld);
		RestResponse response = ServiceRestUtils.createService(serviceReqDetailsUpdated, defaultUser);
		Report.logDebug("Response:", response);
		if (response.getStatusCode() != 201) {
			throw new Exception("Unable to create service.\nResponse: " + response.toString());
		}
		return gson.fromJson(response.getResponse(), ServiceReqDetails.class);
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static ResourceReqDetails createVf() throws Exception {
		ResourceReqDetails defaultVf = ElementFactory.getDefaultResource(ResourceTypeEnum.VF);
		RestResponse response = VfRestUtils.createResource(defaultVf, defaultUser);
		Report.logDebug("Response:", response);
		if (response.getStatusCode() != 201) {
			throw new Exception("Unable to create vf.\nResponse: " + response.toString());
		}
		return gson.fromJson(response.getResponse(), ResourceReqDetails.class);
	}
	
	/**
	 * 
	 * @param vf
	 * @return
	 * @throws Exception
	 */
	public static ResourceReqDetails checkinVf(ResourceReqDetails vf) throws Exception {
		RestResponse response = VfRestUtils.changeResourceState(vf, defaultUser, LifeCycleStatesEnum.CHECKIN);
		Report.logDebug("Response:", response);
		if (response.getStatusCode() != 200) {
			throw new Exception("Unable to checkin vf.\nResponse: " + response.toString());
		}
		return gson.fromJson(response.getResponse(), ResourceReqDetails.class);
	}
	
	/**
	 * 
	 * @param service
	 * @param vf
	 * @return
	 * @throws Exception
	 */
	public static Vfi createVfi(ServiceReqDetails service, ResourceReqDetails vf) throws Exception {
		RestResponse response = ServiceRestUtils.createComponentInstance(service, vf, defaultUser, ComponentTypeEnum.RESOURCE, true);
		Report.logDebug("Response:", response);
		if (response.getStatusCode() != 201) {
			throw new Exception("Unable to create vfi.\nResponse: " + response.toString());
		}
		try {
			JsonObject resBody = new JsonParser().parse(response.getResponse()).getAsJsonObject();
			String vfiName = resBody.get("name").getAsString();
			return new Vfi(vfiName, service);
		} catch (Exception err) {
			throw new Exception(String.format("Unable to parse vfi name\nResponse: %s\n", response), err);
		}
	}

	// DELETE - Clean up //

	public static RestResponse deleteAssetFromSdc(String context, String uniqueId) throws IOException {
		DcaeConfiguration configuration = ConfigurationReader.getConfiguration();
		String url = String.format("%s:%s/sdc2/rest/v1/catalog/%s/%s", configuration.getBeHost(), configuration.getBePort(), context, uniqueId);
		return sendDelete(url, ElementFactory.getDefaultUser(UserRoleEnum.ADMIN).getUserId());
	}

	public static RestResponse deleteMarkedResources() throws IOException {
		DcaeConfiguration configuration = ConfigurationReader.getConfiguration();
		String url = String.format("%s:%s/sdc2/rest/v1/inactiveComponents/resource", configuration.getBeHost(), configuration.getBePort());
		return sendDelete(url, ElementFactory.getDefaultUser(UserRoleEnum.ADMIN).getUserId());
	}

	public static RestResponse deleteMarkedServices() throws IOException {
		DcaeConfiguration configuration = ConfigurationReader.getConfiguration();
		String url = String.format("%s:%s/sdc2/rest/v1/inactiveComponents/service", configuration.getBeHost(), configuration.getBePort());
		return sendDelete(url, ElementFactory.getDefaultUser(UserRoleEnum.ADMIN).getUserId());
	}

	public static Map<String, List<SdcComponent>> getAssetsByUser(String userId) throws IOException {
		DcaeConfiguration configuration = ConfigurationReader.getConfiguration();
		String url = String.format("%s:%s/sdc2/rest/v1/followed", configuration.getBeHost(), configuration.getBePort());
		RestResponse restResponse = sendGet(url, userId);
		return 200 == restResponse.getStatusCode() ? gson.fromJson(restResponse.getResponse(), new TypeToken<Map<String, List<SdcComponent>>>(){}.getType()) : new HashMap<>();
	}


	public static SdcComponentMetadata getAssetMetadata(String context, String uniqueId, String userId) throws IOException {
		DcaeConfiguration configuration = ConfigurationReader.getConfiguration();
		String url = String.format("%s:%s/sdc2/rest/v1/catalog/%s/%s/filteredDataByParams?include=metadata", configuration.getBeHost(), configuration.getBePort(), context, uniqueId);
		RestResponse restResponse = sendGet(url, userId);
		return gson.fromJson(restResponse.getResponse(), SdcComponentMetadata.class);
	}

}
