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

package org.onap.dcae.ci.api.tests.services.attachment;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.SoftAssertions;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.entities.composition.services.Vfi;
import org.onap.dcae.ci.entities.sdc.SdcResource;
import org.onap.dcae.ci.entities.sdc.SdcService;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.report.Report;
import org.onap.dcae.ci.utilities.SdcInternalApiClient;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import org.testng.annotations.*;

import java.util.UUID;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.aventstack.extentreports.Status;

public class PostAttachment extends DcaeRestBaseTest {

	/* Positive */
	
	@Test
	public void vfcmtWithVfi_responseStatusOk() throws Exception {
		// arrange
		Vfcmt vfcmt;
		Vfi vfi;
		try {
			vfcmt = client.createCheckedinVfcmt();
			vfi = arrangeVfi();
		} catch (Exception err) {
			throw new Exception("Could not arrange test", err);
		}
		// act
		Report.log(Status.INFO, "Attaching vfcmt [%s] to vfi [%s]", vfcmt.getUuid(), vfi.getName());
		RestResponse response = DcaeRestClient.attachVfiRef(vfcmt.getUuid(), vfi.getContainer().getUuid(), vfi.getName());
		Report.logDebug("Response:", response);
		// assert
		JSONObject resData = (JSONObject) JSONValue.parse(response.getResponse());
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			softly.assertThat(resData.get("successResponse"))
			 	.as("successResponse")
			 	.isEqualTo("Artifact updated");
		});
	}
	
	
	@Test
	public void vfcmtAlreadyAttached_responseStatusOk() throws Exception {
		// arrange
		Vfcmt vfcmt;
		Vfi vfi;
		try {
			vfcmt = client.createCheckedinVfcmt();
			Report.log(Status.INFO, "Arranging first attachment");
			arrangeAttachmentToNewVfi(vfcmt);
			vfi = arrangeVfi();
		} catch (Exception err) {
			throw new Exception("Could not arrange test", err);
		}
		// act
		Report.log(Status.INFO, "Updating attachment of vfcmt [%s] to a new vfi [%s]", vfcmt.getUuid(), vfi.getName());
		RestResponse response = DcaeRestClient.attachVfiRef(vfcmt.getUuid(), vfi.getContainer().getUuid(), vfi.getName());
		Report.logDebug("Response:", response);
		// assert
		JSONObject resData = (JSONObject) JSONValue.parse(response.getResponse());
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			softly.assertThat(resData.get("successResponse"))
			 	.as("successResponse")
			 	.isEqualTo("Artifact updated");
		});
	}

	
	/* Negative */
	
	
	@Test
	public void vfWithVfi_statusCode400() throws Exception {
		// arrange
		SdcResource vf;
		Vfi vfi;
		try {
			Report.log(Status.INFO, "Create vf");
			vf = SdcInternalApiClient.createVf();
			vfi = arrangeVfi();
		} catch (Exception err) {
			throw new Exception("Could not arrange test", err);
		}
		// act
		Report.log(Status.INFO, "Attaching vf [%s] to vfi [%s]", vf.getUuid(), vfi.getName());
		RestResponse response = DcaeRestClient.attachVfiRef(vf.getUuid(), vfi.getContainer().getUuid(), vfi.getName());
		Report.logDebug("Response:", response);
		// assert
		assertThat(response.getStatusCode()).as("status code").isEqualTo(400);
	}
	
	
	@Test
	public void vfcmtWithFakeServiceAndFakeVfi_statusCode404() throws Exception {
		// arrange
		Vfcmt vfcmt;
		String fakeServiceUuid = UUID.randomUUID().toString();
		String fakeVfiName = "fakeVfi";
		try {
			vfcmt = client.createCheckedinVfcmt();
			fakeServiceUuid = UUID.randomUUID().toString();
			fakeVfiName = "fakeVfi";
		} catch (Exception err) {
			throw new Exception("Could not arrange test", err);
		}
		// act
		Report.log(Status.INFO, "Attaching real vfcmt [%s] to fake service [%s] (random uuid) and fake vfi [%s]", vfcmt.getUuid(), fakeServiceUuid, fakeVfiName);
		RestResponse response = DcaeRestClient.attachVfiRef(vfcmt.getUuid(), fakeServiceUuid, fakeVfiName);
		Report.logDebug("Response:", response);
		// assert
		assertThat(response.getStatusCode()).as("status code").isEqualTo(404);
	}
	
	
	@Test
	public void vfcmtWithFakeVfi_statusCode404() throws Exception {
		// arrange
		Vfcmt vfcmt;
		Vfi vfi;
		try {
			vfcmt = client.createCheckedinVfcmt();
			Report.log(Status.INFO, "Create service");
			SdcService service = SdcInternalApiClient.createService();
			vfi = new Vfi("fakeVfi", service);
		} catch (Exception err) {
			throw new Exception("Could not arrange test", err);
		}
		// act
		Report.log(Status.INFO, "Attaching vfcmt [%s] to real service [%s] and fake vfi [%s]", vfcmt.getUuid(), vfi.getContainer().getUuid(), vfi.getName());
		RestResponse response = DcaeRestClient.attachVfiRef(vfcmt.getUuid(), vfi.getContainer().getUuid(), vfi.getName());
		Report.logDebug("Response:", response);
		// assert
		assertThat(response.getStatusCode()).as("status code").isEqualTo(404);
	}
	
	
	/* Private Methods */
	
	private Vfi arrangeVfi() throws Exception {
		Report.log(Status.INFO, "Create service");
		SdcService service = SdcInternalApiClient.createService();
		Report.log(Status.INFO, "Create vf");
		SdcResource vf = SdcInternalApiClient.createVf();
		Report.log(Status.INFO, "Checkin vf");
		vf = SdcInternalApiClient.checkinVf(vf);
		Report.log(Status.INFO, "Create vfi from (service [%s] + vf [%s])", service.getUuid(), vf.getUuid());
		return SdcInternalApiClient.createVfi(service, vf);
	}

	
	private void arrangeAttachmentToNewVfi(Vfcmt vfcmt) throws Exception {
		Vfi vfi = arrangeVfi();
		Report.log(Status.INFO, "Attaching vfcmt [%s] to vfi [%s]", vfcmt.getUuid(), vfi.getName());
		RestResponse response = DcaeRestClient.attachVfiRef(vfcmt.getUuid(), vfi.getContainer().getUuid(), vfi.getName());
		Report.logDebug("Response:", response);
		if (response.getStatusCode() != 200) {
			throw new Exception("Failed to attach vfcmt to vfi\nResponse: " + response.toString());
		}
	}
	
}
