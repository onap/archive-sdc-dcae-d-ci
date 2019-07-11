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

package org.onap.dcae.ci.api.tests.ruleEditor;


import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;

import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.report.Report;
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
				.doesNotContain(SetUtils.emptySet())
				.doesNotContainNull();
			softly.assertThat(resBody).flatExtracting("eventTypes").as("eventTypes list (flatten)")
				.doesNotContainAnyElementsOf(EventTypesByVersionUI.DEFAULT_EVENTS)
				.doesNotContain(StringUtils.EMPTY)
				.doesNotContainNull();
		});
	}
	
}
