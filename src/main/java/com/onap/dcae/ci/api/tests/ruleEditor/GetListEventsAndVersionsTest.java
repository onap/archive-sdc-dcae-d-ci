package com.onap.dcae.ci.api.tests.ruleEditor;

import com.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import com.onap.dcae.ci.utilities.Report;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.onap.sdc.dcae.composition.restmodels.ruleeditor.EventTypesByVersionUI;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

public class GetListEventsAndVersionsTest extends DcaeRestBaseTest {
	
	/* Positive */
	
	@Test
	public void test_successResponse() throws Exception {
		// act
		Report.log(Status.INFO, "test_successResponse: Requesting VES event types");
		RestResponse response = DcaeRestClient.getVesEventTypes();
		Report.logDebug("Response", response);
		// assert
		EventTypesByVersionUI[] resBody = gson.fromJson(response.getResponse(), EventTypesByVersionUI[].class);
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			
			softly.assertThat(resBody).as("response body").isNotEmpty();
			
			softly.assertThat(resBody).extracting("version").as("version")
				.doesNotContain(StringUtils.EMPTY)
				.doesNotContainNull();
			
			softly.assertThat(resBody).extracting("eventTypes").as("eventTypes list")
				.doesNotContain(SetUtils.EMPTY_SET)
				.doesNotContainNull();
			softly.assertThat(resBody).flatExtracting("eventTypes").as("eventTypes list (flatten)")
				.doesNotContainAnyElementsOf(EventTypesByVersionUI.DEFAULT_EVENTS)
				.doesNotContain(StringUtils.EMPTY)
				.doesNotContainNull();
		});
	}
	
}
