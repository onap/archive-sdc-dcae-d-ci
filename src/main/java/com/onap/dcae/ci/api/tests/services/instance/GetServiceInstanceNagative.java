package com.onap.dcae.ci.api.tests.services.instance;

import com.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import com.aventstack.extentreports.Status;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.openecomp.d2.ci.report.ExtentTestActions;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class GetServiceInstanceNagative extends DcaeRestBaseTest {

	@Test
	public void test_invalidServiceID() throws IOException{
		ExtentTestActions.log(Status.INFO, "Send invalid service Instance");
		RestResponse response = DcaeRestClient.getServicesInstance("123456789");
		ExtentTestActions.log(Status.INFO, "Verifying response status is 404");
		assertThat(response.getStatusCode()).as("response status").isEqualTo(404);
	}
	
}
