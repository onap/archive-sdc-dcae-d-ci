package com.onap.dcae.ci.api.tests.services.instance;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.assertj.core.api.SoftAssertions;
import org.onap.sdc.dcae.composition.restmodels.sdc.ServiceDetailed;
import org.onap.sdc.dcae.composition.services.Resource;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.openecomp.d2.ci.report.ExtentTestActions;
import org.onap.sdc.dcae.composition.restmodels.DcaeMinimizedService;
import org.springframework.util.CollectionUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import com.onap.dcae.ci.utilities.StringUtils;
import com.aventstack.extentreports.Status;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class GetServiceInstancePositive extends DcaeRestBaseTest {
	
	private RestResponse response;
	
	@BeforeClass
	public void executeApiCall() throws Exception {
		// arrange
		DcaeMinimizedService service = arrangeService();
		try {
			// act
			ExtentTestActions.log(Status.INFO, "Get all VFIs for service [" + service.getUuid() + "]");
			response = DcaeRestClient.getServicesInstance(service.getUuid());
			ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		} catch (Exception err) {
			ExtentTestActions.log(Status.FAIL, "Unable to execute api call: " + err.toString());
			err.printStackTrace();
		}
	}

	private DcaeMinimizedService arrangeService() throws Exception {
		DcaeMinimizedService service = null;
		try {
			Predicate<DcaeMinimizedService> hasVfi = p -> !CollectionUtils.isEmpty(getService(p.getUuid()).getResources());
			Vfcmt vfcmt = client.createCheckedoutVfcmt();
			ExtentTestActions.log(Status.INFO, "Created vfcmt [" + vfcmt.getUuid() + "]");
			ExtentTestActions.log(Status.INFO, "Get all services for vfcmt [" + vfcmt.getUuid() + "]");
			RestResponse responseServices =  DcaeRestClient.getServices(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
			ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(responseServices));
			DcaeMinimizedService[] servicesList = gson.fromJson(responseServices.getResponse(), DcaeMinimizedService[].class);
			// TODO: create a service instead of picking a random one
			// find a service with a vfi
			service = Arrays.stream(servicesList).filter(hasVfi).findAny().orElse(null);
		} catch (Exception err) {
			ExtentTestActions.log(Status.ERROR, "Could not arrange test: " + err.toString());
		}
		return service;
	}

	private ServiceDetailed getService(String serviceId) {
		ServiceDetailed service = null;
		try {
			service = gson.fromJson(DcaeRestClient.getServicesInstance(serviceId).getResponse(), ServiceDetailed.class);
		} catch (Exception e) {
			ExtentTestActions.log(Status.ERROR, "Could not arrange test: " + e.toString());
		}
		return service;
	}
	
	@Test
	public void test_responseStatusOk() throws IOException{
		// assert
		ExtentTestActions.log(Status.INFO, "Verifing response status is 200");
		assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
	}
	
	@Test 
	public void test_atLeastOneOrMoreResources() throws IOException{
		// assert
		ExtentTestActions.log(Status.INFO, "Parsing response to a one service instance");
		List<Resource> resourceList = getResourceListFromJsonResponse();
		ExtentTestActions.log(Status.INFO, "validating parsed response structure");
		assertThat(resourceList).size().isGreaterThanOrEqualTo(1); // TODO: create a VFI for the service instead of picking a random one
	}
	
	@Test 
	public void  test_responseIsValidStructure() throws IOException{
		// assert
		ExtentTestActions.log(Status.INFO, "Parsing response to a one service instance");
		
		List<Resource> resourceList = getResourceListFromJsonResponse();
		
		ExtentTestActions.log(Status.INFO, "validating parsed response structure");
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(resourceList.get(0).getResourceInstanceName()).isNotEmpty();
			softly.assertThat(resourceList.get(0).getResourceInvariantUUID()).isNotEmpty();
		});
	}
	
	
	/*** private method ***/
	private List<Resource> getResourceListFromJsonResponse() {
		JsonParser jsonParser = new JsonParser();
		JsonObject responseJson = (JsonObject)jsonParser.parse(response.getResponse());
		JsonArray resources = responseJson.getAsJsonArray("resources");
		Type listType = new TypeToken<List<Resource>>(){}.getType();
		List<Resource> resourceList = gson.fromJson(resources, listType);
		return resourceList;
	}

}
