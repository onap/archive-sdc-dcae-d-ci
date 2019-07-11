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

package org.onap.dcae.ci.api.tests.vfcmt;

import static org.assertj.core.api.Assertions.*;

import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.report.Report;
import org.onap.dcae.ci.utilities.StringUtils;
import org.testng.annotations.*;


import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;

import com.aventstack.extentreports.Status;

public class GetAllVfcmts extends DcaeRestBaseTest {

    private RestResponse response;

    @BeforeClass
    public void executeApiCall() {
        try {
            // act
            Report.log(Status.INFO, "Fetching all vfcmt resources");
            response = DcaeRestClient.getAllVfcmts();
			Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
        } catch (Exception err) {
			Report.log(Status.ERROR, err);
        }
    }

    /* Positive tests */

    @Test
    public void testResponseStatusOk() {
        // assert
		Report.log(Status.INFO, "Verifying response status is 200");
        assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
    }

    @Test
    public void testResponseBodyIsListOfVfcmtsOnly() {
        // assert
		Report.log(Status.INFO, "Parsing response to a list of VFCMTs");
        Vfcmt[] vfcmtList = gson.fromJson(response.getResponse(), Vfcmt[].class);
		Report.log(Status.INFO, "Verifying all items of the parsed response are VFCMTs");
        assertThat(vfcmtList)
            .as("response data")
            .extracting("resourceType")
            .containsOnly("VFCMT");
    }

    /* Negative tests */
}
