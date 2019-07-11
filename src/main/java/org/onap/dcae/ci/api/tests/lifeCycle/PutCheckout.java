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

package org.onap.dcae.ci.api.tests.lifeCycle;

import java.io.IOException;
import java.util.UUID;


import org.assertj.core.api.SoftAssertions;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;

import org.onap.dcae.ci.report.Report;
import org.onap.dcae.ci.utilities.StringUtils;
import org.testng.annotations.*;
import static org.assertj.core.api.Assertions.*;

import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import com.aventstack.extentreports.Status;

public class PutCheckout extends DcaeRestBaseTest {
	
	private ArrangeHelper arrange = new ArrangeHelper(client);
	
	/* Positive */
	
	@Test
	public void checkedinVfcmt_success() throws Exception {
		// arrange
		Vfcmt vfcmt = arrange.getCheckedinVfcmt();
		// act
		Report.log(Status.INFO, "Checkout the vfcmt with it's lastUpdater user");
		RestResponse response = checkoutVfcmt(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
		// assert
		Vfcmt vfcmtAfterCheckout = gson.fromJson(response.getResponse(), Vfcmt.class);
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).isEqualTo(200);
			softly.assertThat(vfcmtAfterCheckout.getLifecycleState()).contains("CHECKOUT");
		});
	}
	
	@Test
	public void notLastUser_success() throws Exception {
		// arrange
		String user1 = DcaeRestClient.getDefaultUserId();
		String user2 = DcaeRestClient.getDesigner2UserId();
		Vfcmt vfcmt = arrange.getCheckedinVfcmt(user1);
		// act
		Report.log(Status.INFO, "Checkout the vfcmt with different user: " + user2);
		RestResponse response = checkoutVfcmt(vfcmt.getUuid(), user2);
		// assert
		Vfcmt vfcmtAfterCheckout = gson.fromJson(response.getResponse(), Vfcmt.class);
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).isEqualTo(200);
			softly.assertThat(vfcmtAfterCheckout.getLifecycleState()).contains("CHECKOUT");
		});
	}
	
	
	/* Negative */
	
	@Test
	public void alreadyCheckedoutVfcmt_statusCode403() throws Exception {
		// arrange
		Vfcmt vfcmt = arrange.getCheckedoutVfcmt();
		// act
		Report.log(Status.INFO, "Checkout the vfcmt with it's lastUpdater user");
		RestResponse response = checkoutVfcmt(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
		// assert
		assertThat(response.getStatusCode()).isEqualTo(403);
	}
	
	@Test
	public void invalidVfcmtUuid_statusCode400() throws Exception {
		// arrange
		String userId = DcaeRestClient.getDefaultUserId();
		// act
		Report.log(Status.INFO, "Checkout with an invalid-uuid as vfcmt-uuid");
		RestResponse response = checkoutVfcmt("invalid-vfcmt-uuid", userId);
		// assert
		assertThat(response.getStatusCode()).isEqualTo(400);
	}
	
	@Test
	public void nonExistingUuid_statusCode409() throws Exception {
		// arrange
		String uuid = UUID.randomUUID().toString();
		String userId = DcaeRestClient.getDefaultUserId();
		// act
		Report.log(Status.INFO, "Checkin with a non-existing vfcmt-uuid");
		RestResponse response = checkoutVfcmt(uuid, userId);
		// assert
		assertThat(response.getStatusCode()).isEqualTo(409);
	}

	@Test
	public void nonExistingUser_statusCode403() throws Exception {
		// arrange
		Vfcmt vfcmt = arrange.getCheckedinVfcmt();
		// act
		Report.log(Status.INFO, "Checkin with a non-existing user");
		RestResponse response = checkoutVfcmt(vfcmt.getUuid(), "anonymus");
		// assert
		assertThat(response.getStatusCode()).isEqualTo(403);
	}
	
	@Test
	public void invalidAssetType_statusCode400() throws Exception {
		// arrange 
		String assetType = "kengero";
		String userId = DcaeRestClient.getDefaultUserId();
		Vfcmt vfcmt = arrange.getCheckedoutVfcmt();
		// act
		Report.log(Status.INFO, "Checkout the vfcmt with invalid asset-type");
		RestResponse response = checkoutGeneral(assetType, userId, vfcmt);
		// assert
		assertThat(response.getStatusCode()).isEqualTo(400);
	}
	
	
	/* Private Methods */
	
	/**
	 * Performs checkout on a general vfcmt/service and logs the response
	 * @param assetType
	 * @param userId
	 * @param vfcmt
	 * @return
	 * @throws IOException
	 */
	private RestResponse checkoutGeneral(String assetType, String userId, Vfcmt vfcmt) throws IOException {
		RestResponse response = DcaeRestClient.checkinGeneral(assetType, vfcmt.getUuid(), userId);
		Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		return response;
	}
	
	/**
	 *  Performs checkout on vfcmt and logs the response
	 * @param vfcmtUuid
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	private RestResponse checkoutVfcmt(String vfcmtUuid, String userId) throws IOException {
		RestResponse response = DcaeRestClient.checkoutVfcmt(vfcmtUuid, userId);
		Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		return response;
	}
}
