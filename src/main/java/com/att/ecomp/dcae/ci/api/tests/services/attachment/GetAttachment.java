package com.att.ecomp.dcae.ci.api.tests.services.attachment;

import com.att.ecomp.dcae.ci.api.tests.DcaeRestBaseTest;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.testng.annotations.*;

import com.att.ecomp.dcae.ci.utilities.DcaeRestClient;
import com.att.ecomp.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class GetAttachment extends DcaeRestBaseTest {
	
	/* Positive */
	
	@Test
	public void testNewVfcmt_noAttachedService() throws Exception {
		Report.log(Status.INFO, "testNewVfcmt_noAttachedService start");
		// arrange
		Vfcmt vfcmt = client.createVfcmt();
		// act
		RestResponse response = DcaeRestClient.getAttachedService(vfcmt.getUuid());
		// assert
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
			JSONObject resData = (JSONObject) JSONValue.parse(response.getResponse());
			softly.assertThat(resData.get("successResponse"))
				.as("successResponse")
				.isEqualTo("No Artifacts");
		});
	}
	
	/* Negative */
	
	@Test
	public void testNonUuidVfcmt_responseStatus404() throws Exception {
		// act
		RestResponse response = DcaeRestClient.getAttachedService("I am not a uuid");
		// assert
		assertThat(response.getStatusCode()).as("status code").isEqualTo(404);
	}
	
	@Test
	public void testNonExistingVfcmt_responseStatus404() throws Exception {
		// arrange
		String fakeUuid = UUID.randomUUID().toString();
		// act
		RestResponse response = DcaeRestClient.getAttachedService(fakeUuid);
		// assert
		assertThat(response.getStatusCode()).as("status code").isEqualTo(404);
	}
}
