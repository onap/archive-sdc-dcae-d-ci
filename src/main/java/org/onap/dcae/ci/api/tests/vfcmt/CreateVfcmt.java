package org.onap.dcae.ci.api.tests.vfcmt;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.assertj.core.api.SoftAssertions;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.utilities.DcaeTestConstants;
import org.onap.dcae.ci.report.Report;
import org.onap.dcae.ci.utilities.StringUtils;
import org.testng.annotations.*;

import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import com.aventstack.extentreports.Status;

public class CreateVfcmt extends DcaeRestBaseTest {
	
	private Vfcmt input;
	private RestResponse response;
	
	@BeforeClass
	public void executeApiCall() {
		// arrange
		input = new Vfcmt();
		input.setName(StringUtils.randomString("CI-", 20));
		input.setDescription(StringUtils.randomString("", 10));
		try {
			// act
			Report.log(Status.INFO, "Creating vfcmt");
			response = DcaeRestClient.createVfcmt(input.getName(), input.getDescription());
			Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		} catch (Exception err) {
			Report.log(Status.ERROR, err);
		}
	}
	
	/* Positive tests */
	
	@Test
	public void test_responseStatusOk() throws IOException {
		// assert
		Report.log(Status.INFO, "Verifing response status is 200");
		assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
	}
	
	@Test
	public void test_responseIsValidStructure() throws IOException {
		// assert
		Report.log(Status.INFO, "Parsing response to a VFCMT");
		Vfcmt createdVfcmt = gson.fromJson(response.getResponse(), Vfcmt.class);
		Report.log(Status.INFO, "validating parsed response structure");
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(createdVfcmt.getCategory()).isEqualTo("Template");
			softly.assertThat(createdVfcmt.getInvariantUUID()).isNotEmpty();
			softly.assertThat(createdVfcmt.getUuid()).isNotEmpty();
			softly.assertThat(createdVfcmt.getLastUpdaterUserId()).isEqualTo(DcaeRestClient.getDefaultUserId());
			softly.assertThat(createdVfcmt.getLifecycleState()).isEqualTo(DcaeTestConstants.Sdc.State.NOT_CERTIFIED_CHECKOUT);
			softly.assertThat(createdVfcmt.getName()).isEqualTo(input.getName());
			softly.assertThat(createdVfcmt.getDescription()).isEqualTo(input.getDescription());
			softly.assertThat(createdVfcmt.getResourceType()).isEqualTo("VFCMT");
			softly.assertThat(createdVfcmt.getSubCategory()).isEqualTo("Monitoring Template");
			softly.assertThat(createdVfcmt.getVersion()).isEqualTo("0.1");
		});
	}
	
	/* Negative tests */
	

	
	@Test
	public void testWithNonExistingUser_status403() throws IOException {
		// arrange
		String fakeUserId = "anonymous";
		String name = StringUtils.randomString("CI-", 20);
		String description = StringUtils.randomString("", 10);
		// act
		Report.log(Status.INFO, "Creating vfcmt with fake user");
		RestResponse res = DcaeRestClient.createVfcmt(name, description, fakeUserId);
		Report.log(Status.DEBUG, "response: " + StringUtils.truncate(res));
		// assert
		assertThat(res.getStatusCode())
			.as("status code")
			.isEqualTo(403);
	}
	
	@Test
	public void testCreateTwoVfcmtsWithSameName_status409() throws IOException {
		// arrange
		String name = StringUtils.randomString("CI-", 20);
		Report.log(Status.INFO, "Creating first vfcmt with name '" + name + "'");
		RestResponse response1 = DcaeRestClient.createVfcmt(name, StringUtils.randomString("", 10));
		Report.log(Status.DEBUG, "First Response: " + StringUtils.truncate(response1));
		if (response1.getStatusCode() != 200) {
			fail("unable to arrange test - could not create the first vfcmt");
		}
		// act
		Report.log(Status.INFO, "Creating second vfcmt with same name '" + name + "'");
		RestResponse response2 = DcaeRestClient.createVfcmt(name, StringUtils.randomString("", 10));
		Report.log(Status.DEBUG, "Second response: " + StringUtils.truncate(response2));
		// assert
		assertThat(response2.getStatusCode())
			.as("second status code")
			.isEqualTo(409);
	}
	
	@Test
	public void testWithNonDesignerRole_status403() throws IOException {
		// arrange
		String testerId = DcaeRestClient.getDefaultTesterId();
		String name = StringUtils.randomString("CI-", 20);
		String description = StringUtils.randomString("", 10);
		// act
		Report.log(Status.INFO, "Creating vfcmt with role tester");
		RestResponse response = DcaeRestClient.createVfcmt(name, description, testerId);
		Report.log(Status.DEBUG, "response: " + StringUtils.truncate(response));
		// assert
		assertThat(response.getStatusCode())
			.as("status code")
			.isEqualTo(403);
	}

	
}
