package org.onap.dcae.ci.api.tests.configuration;

import com.aventstack.extentreports.Status;
import com.google.gson.JsonObject;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.report.Report;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationControllerApiTest extends DcaeRestBaseTest {

	@Test
	public void getFlowTypesAndConfiguredPhases() throws IOException {
		String entryPhase = "entryPhase";
		String publishPhase = "publishPhase";
		RestResponse response = DcaeRestClient.getConfiguredFlowTypes();
		Report.log(Status.INFO, "get flowTypes response=%s", response);
		assertThat(response.getStatusCode()).isEqualTo(200);
		String flowType = gson.fromJson(response.getResponse(),JsonObject.class).get("flowTypes").getAsJsonArray().get(0).getAsString();
		response = DcaeRestClient.getConfiguredPhasesByFlowType(flowType);
		Report.log(Status.INFO, "get phases by flowType %s response=%s", flowType, response);
		assertThat(response.getStatusCode()).isEqualTo(200);
		JsonObject phases = gson.fromJson(response.getResponse(), JsonObject.class);
		assertThat(phases.get(entryPhase)).isNotNull();
		assertThat(phases.get(publishPhase)).isNotNull();
		assertThat(phases.get(entryPhase).getAsString()).isNotEqualTo("");
		assertThat(phases.get(publishPhase).getAsString()).isNotEqualTo("");
		response = DcaeRestClient.getConfiguredPhasesByFlowType("noSuchFlowType");
		Report.log(Status.INFO, "get phases by none existing flowType response=%s", response);
		assertThat(response.getStatusCode()).isEqualTo(200);
		phases = gson.fromJson(response.getResponse(), JsonObject.class);
		assertThat(phases.get(entryPhase)).isNotNull();
		assertThat(phases.get(publishPhase)).isNotNull();
		assertThat(phases.get(entryPhase).getAsString()).isEqualTo("");
		assertThat(phases.get(publishPhase).getAsString()).isEqualTo("");
	}
}
