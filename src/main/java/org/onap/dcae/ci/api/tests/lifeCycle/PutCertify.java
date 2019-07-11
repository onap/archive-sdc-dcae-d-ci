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

package org.onap.dcae.ci.api.tests.lifeCycle;


import com.aventstack.extentreports.Status;
import org.assertj.core.api.SoftAssertions;

import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.report.Report;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import org.testng.annotations.Test;

public class PutCertify extends DcaeRestBaseTest {

	private ArrangeHelper arrange = new ArrangeHelper(client);

	/* Positive */

	@Test
	public void certifyVfcmt_success() throws Exception {
		// arrange
		Vfcmt vfcmt = arrange.getCheckedinVfcmt();
		// act
		Report.log(Status.INFO, "Calling certify vfcmt with lastUpdater as user (designer role)");
		RestResponse response = DcaeRestClient.certifyVfcmt(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
		// assert
		Vfcmt vfcmtAfterCheckin = gson.fromJson(response.getResponse(), Vfcmt.class);
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).isEqualTo(200);
			softly.assertThat(vfcmtAfterCheckin.getLifecycleState()).isEqualTo("CERTIFIED");
		});
	}
}
