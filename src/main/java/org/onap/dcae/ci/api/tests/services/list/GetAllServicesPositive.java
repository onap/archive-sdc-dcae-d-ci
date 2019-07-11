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

package org.onap.dcae.ci.api.tests.services.list;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.report.Report;
import org.onap.dcae.ci.utilities.StringUtils;

import org.onap.sdc.dcae.composition.restmodels.sdc.Service;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import com.aventstack.extentreports.Status;
import com.google.gson.reflect.TypeToken;

public class GetAllServicesPositive extends DcaeRestBaseTest {
	
	private RestResponse response;
	
	@BeforeClass
	public void executeApiCall() throws Exception {
		Report.log(Status.INFO, "@BeforeClass --> executeApiCall");
		Vfcmt component = client.createCheckedoutVfcmt();
		String uuid = component.getUuid();
		String userIdLastUpdaterUserId = component.getLastUpdaterUserId();
		try {
			// act
			Report.log(Status.INFO, "Get all services");
			response =  DcaeRestClient.getServices(uuid, userIdLastUpdaterUserId);
			Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		} catch (Exception err) {
			Report.log(Status.ERROR, err);
			err.printStackTrace();
		}
	}
	
	@Test
	public void test_responseStatusOk() throws IOException{
		// assert
		Report.log(Status.INFO, "Verifing response status is 200");
		assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
	}
	
	@Test 
	public void  test_responseIsValidStructure() throws IOException{
		// assert
		Report.log(Status.INFO, "Parsing response to a List of Services");
		Type listType = new TypeToken<List<Service>>(){}.getType();
		List<Service> servicesList = gson.fromJson(response.getResponse(), listType);
		Service service = servicesList.get(0);

		Report.log(Status.INFO, "validating parsed response structure");
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(service.getInvariantUUID()).isNotEmpty();
			softly.assertThat(service.getLastUpdaterUserId()).isNotEmpty();
			softly.assertThat(service.getName()).isNotEmpty();
			softly.assertThat(service.getUuid()).isNotEmpty();
			softly.assertThat(service.getLifecycleState()).isNotEmpty();
			softly.assertThat(service.getVersion()).isNotEmpty();
		});
	}


}
