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


import com.aventstack.extentreports.Status;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.report.Report;
import org.onap.dcae.ci.utilities.StringUtils;
import org.onap.sdc.dcae.composition.restmodels.CreateMcResponse;
import org.onap.sdc.dcae.composition.restmodels.CreateVFCMTRequest;
import org.onap.sdc.dcae.composition.restmodels.sdc.Resource;
import org.onap.sdc.dcae.composition.services.Service;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetVfcmtsForMigration extends DcaeRestBaseTest {

    private static final String STATUS_CODE = "status code";
    private RestResponse response;
    private Resource res = null;

    @BeforeClass
    public void executeApiCall() {
        try {
            // act
			Report.log(Status.INFO, "Selecting vfcmt resources for migration");
            response = DcaeRestClient.getVfcmtsForMigration("services","1111111","1.3");
			Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));

			Report.log(Status.INFO, "getAllMonitoringTemplatesVfcmts");
            RestResponse clientResponse = DcaeRestClient.getAllMonitoringTemplatesVfcmts();
            Resource[] resources = gson.fromJson(clientResponse.getResponse(),Resource[].class);
            if (resources.length > 0){
                res = resources[0];
            }
        } catch (Exception err) {
			Report.log(Status.ERROR, err);
        }
    }

    // If tests are taking too long: consider removing this very tedious and long test
    @Test
    public void testCreateMonitoringComponentSuccess() throws IOException {
        CreateVFCMTRequest request = new CreateVFCMTRequest();

        request.setDescription("Test description");

        Service service;

        try {
            service = createServiceWithVFiAsSdcDesigner();
        } catch (Exception e) {
			Report.log(Status.ERROR, e);
            return;
        }

        // If you crashed here (below) it is because your environment has no Base Monitoring Templates
        request.setTemplateUuid(res.getUuid());
        if (service != null) {
            request.setServiceUuid(service.getUuid());
        }
        request.setVfiName(service.getResources().get(0).getResourceInstanceName());

        List<String> listOfVfcmtsConnectedToService = new ArrayList<>();
        for (int i = 0 ; i < 4 ; i++){
            request.setName(StringUtils.randomString("CI-", 20));
            DcaeRestClient.fillCreateMcRequestMandatoryFields(request);
            response = DcaeRestClient.createMc(gson.toJson(request));
            assertThat(response.getStatusCode()).as(STATUS_CODE).isEqualTo(200);
            CreateMcResponse mcResponse = gson.fromJson(response.getResponse(),CreateMcResponse.class);
            String key = mcResponse.getVfcmt().getUuid();
            String value = gson.toJson(mcResponse.getCdump());
            assertThat(value.contains(key)).isTrue();
			Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
            listOfVfcmtsConnectedToService.add(key);
        }

        response = DcaeRestClient.getVfcmtsForMigration("service",service.getUuid(),service.getVersion());
        assertThat(response.getStatusCode()).as(STATUS_CODE).isEqualTo(200);

        listOfVfcmtsConnectedToService.forEach( s -> assertThat(response.getResponse().contains(s)).isFalse());

        response = DcaeRestClient.getAllVfcmts();
        assertThat(response.getStatusCode()).as(STATUS_CODE).isEqualTo(200);

        listOfVfcmtsConnectedToService.forEach( s -> assertThat(response.getResponse().contains(s)).isTrue());

    }
}
