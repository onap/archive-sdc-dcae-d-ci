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
