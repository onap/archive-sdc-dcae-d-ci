package org.onap.dcae.ci.api.tests.services.attachment;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.report.Report;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;

import org.testng.annotations.*;

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
