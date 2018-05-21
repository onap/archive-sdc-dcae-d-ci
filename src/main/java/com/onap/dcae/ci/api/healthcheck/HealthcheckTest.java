package com.onap.dcae.ci.api.healthcheck;

import com.aventstack.extentreports.Status;
import com.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import com.onap.dcae.ci.utilities.Report;
import org.assertj.core.api.SoftAssertions;
import org.onap.sdc.dcae.composition.restmodels.health.ComponentsInfo;
import org.onap.sdc.dcae.composition.restmodels.health.HealthResponse;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HealthcheckTest extends DcaeRestBaseTest {
	
	@Test
	public void getHealthcheck_statusOK() throws IOException {
		Report.log(Status.INFO, "Starting Health Check test");
		RestResponse response = DcaeRestClient.getHealthcheck();
		Report.log(Status.INFO, "Health Check response=%s", response);
		assertThat(response.getStatusCode().intValue())
			.as("response status")
			.isEqualTo(200);
	}
	
	@Test
	public void getHealthcheck_validDataStructure() throws IOException {
		Report.log(Status.INFO, "Starting getHealthcheck_validDataStructure");
		RestResponse response = DcaeRestClient.getHealthcheck();
		Report.log(Status.INFO, "Response=%s", response);
		HealthResponse hcData = gson.fromJson(response.getResponse(), HealthResponse.class);
		
		SoftAssertions.assertSoftly(softly -> {
			assertHealthStructure(softly, hcData, "DCAE Designer");
			List<ComponentsInfo> componentsInfo = hcData.getComponentsInfo();
			softly.assertThat(componentsInfo).extracting("healthCheckComponent").contains("BE", "TOSCA_LAB");
			assertHealthComponentStructure(softly, hcData.getComponentsInfo().get(0));
			assertHealthComponentStructure(softly, hcData.getComponentsInfo().get(1));
		});
	}

	private void assertHealthStructure(SoftAssertions softly, HealthResponse hcData, String name) {
		softly.assertThat(hcData.getHealthCheckComponent()).isEqualTo(name);
		softly.assertThat(hcData.getHealthCheckStatus()).isEqualTo("UP");
		softly.assertThat(hcData.getSdcVersion()).isNotEmpty();
		softly.assertThat(hcData.getDescription()).isNotEmpty();
	}
	
	private void assertHealthComponentStructure(SoftAssertions softly, ComponentsInfo hcData) {
		softly.assertThat(hcData.getHealthCheckStatus()).isEqualTo("UP");
		softly.assertThat(hcData.getVersion()).isNotEmpty();
		softly.assertThat(hcData.getDescription()).isNotEmpty();
	}
}