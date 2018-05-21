package org.onap.dcae.ci.api.tests.services.instance;


import com.aventstack.extentreports.Status;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;

import org.onap.dcae.ci.report.Report;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class GetServiceInstanceNagative extends DcaeRestBaseTest {

	@Test
	public void test_invalidServiceID() throws IOException{
		Report.log(Status.INFO, "Send invalid service Instance");
		RestResponse response = DcaeRestClient.getServicesInstance("123456789");
		Report.log(Status.INFO, "Verifying response status is 404");
		assertThat(response.getStatusCode()).as("response status").isEqualTo(404);
	}
}
