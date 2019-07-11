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
import org.onap.sdc.dcae.composition.restmodels.ImportVFCMTRequest;
import org.onap.sdc.dcae.composition.restmodels.MessageResponse;
import org.onap.sdc.dcae.composition.restmodels.sdc.Resource;
import org.onap.sdc.dcae.composition.restmodels.sdc.ResourceDetailed;
import org.onap.sdc.dcae.composition.services.Service;
import org.onap.sdc.dcae.composition.util.DcaeBeConstants;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateMonitoringComponent extends DcaeRestBaseTest {

    private RestResponse response;
    private Resource res = null;


    @BeforeClass
    public void executeApiCall() {
        // arrange
        try {
            // act
            Report.log(Status.INFO, "Creating vfcmt");
            RestResponse clientResponse = DcaeRestClient.getAllMonitoringTemplatesVfcmts();
            Resource[] resources = gson.fromJson(clientResponse.getResponse(),Resource[].class);
            if (resources.length > 0){
                res = resources[0];
            }
        } catch (Exception err) {
			Report.log(Status.ERROR, err);
        }
    }

    @Test
    public void testCreateMonitoringComponentSuccess() throws IOException {

        Service service;

        try {
            service = createServiceWithVFiAsSdcDesigner();
        } catch (Exception e) {
			Report.log(Status.ERROR, e);
            return;
        }

        CreateVFCMTRequest request = new CreateVFCMTRequest();
        // If you crashed here (below) it is because your environment has no Base Monitoring Templates
        request.setTemplateUuid(res.getUuid());
        request.setVfiName(service.getResources().get(0).getResourceInstanceName());
        request.setServiceUuid(service.getUuid());
        DcaeRestClient.fillCreateMcRequestMandatoryFields(request);

        response = DcaeRestClient.createMc(gson.toJson(request));

        assertThat(response.getStatusCode())
                .as("status code")
                .isEqualTo(200);

        CreateMcResponse mcResponse = gson.fromJson(response.getResponse(),CreateMcResponse.class);

        String vfUuid = mcResponse.getVfcmt().getUuid();
        String vfCdump = gson.toJson(mcResponse.getCdump());
        assertThat(vfCdump.contains(vfUuid)).isTrue();

        RestResponse r = DcaeRestClient.getAttachedService(vfUuid);
        assertThat(r.getResponse().contains(service.getUuid())).isTrue();

		Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
    }

    @Test
    public void testCreateMonitoringComponentBadUser() throws IOException {

        String fakeUserId = "anonymous";
        CreateVFCMTRequest request = new CreateVFCMTRequest();
        // If you crashed here (below) it is because your environment has no Base Monitoring Templates
        request.setTemplateUuid(res.getUuid());
        DcaeRestClient.fillCreateMcRequestMandatoryFields(request);

        response = DcaeRestClient.createMc(gson.toJson(request),fakeUserId);

		Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
        // assert
        assertThat(response.getStatusCode())
                .as("status code")
                .isEqualTo(403);
    }

    @Test
    public void testImportMonitoringComponentBothFlagsFalseSuccess() throws Exception {

        Service service;

        try {
            service = createServiceWithVFiAsSdcDesigner();
        } catch (Exception e) {
			Report.log(Status.ERROR, e);
            return;
        }

        String vfiName = service.getResources().get(0).getResourceInstanceName();
        Vfcmt vfcmt = prepareMockVfcmtForImport();
        ImportVFCMTRequest request = new ImportVFCMTRequest();
        request.setTemplateUuid(vfcmt.getUuid());
        request.setVfiName(vfiName);
        request.setServiceUuid(service.getUuid());
        DcaeRestClient.fillCreateMcRequestMandatoryFields(request);

        response = DcaeRestClient.importMc(gson.toJson(request));

        assertThat(response.getStatusCode())
                .as("status code")
                .isEqualTo(200);

        // the service vfi should now have an external reference record to the vfcmt
        response = DcaeRestClient.getServiceExternalReferences(service.getUuid(), service.getVersion());
        assertThat(response.getStatusCode())
                .as("status code")
                .isEqualTo(200);
        Map<String, List<Map>> refs = gson.fromJson(response.getResponse(), Map.class);
        assertThat(vfcmt.getUuid().equals(refs.get("monitoringComponents").get(0).get("uuid"))).isTrue();
		Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
    }

    @Test
    public void testImportMonitoringComponentCloneFalseUpdateFlowTypeTrueSuccess() throws Exception {

        Service service;

        try {
            service = createServiceWithVFiAsSdcDesigner();
        } catch (Exception e) {
			Report.log(Status.ERROR, e);
            return;
        }

        String vfiName = service.getResources().get(0).getResourceInstanceName();
        Vfcmt vfcmt = prepareMockVfcmtForImport();
        ImportVFCMTRequest request = new ImportVFCMTRequest();
        request.setUpdateFlowType(true);
        request.setTemplateUuid(vfcmt.getUuid());
        request.setVfiName(vfiName);
        request.setServiceUuid(service.getUuid());
        DcaeRestClient.fillCreateMcRequestMandatoryFields(request);

        response = DcaeRestClient.importMc(gson.toJson(request));

        assertThat(response.getStatusCode())
                .as("status code")
                .isEqualTo(200);

        // TODO update to one GET call after editMC is implemented
        // assert that the vfcmt was updated as expected (version, owner, lifecycle)
        response = DcaeRestClient.getResourceDetails(vfcmt.getUuid());
        ResourceDetailed updatedMC = gson.fromJson(response.getResponse(), ResourceDetailed.class);
        assertThat(updatedMC.getVersion()).isEqualTo("0.2");
        assertThat(updatedMC.getLastUpdaterUserId()).isEqualTo(DcaeRestClient.getDefaultUserId());
        assertThat(updatedMC.getLifecycleState()).isEqualTo(DcaeBeConstants.LifecycleStateEnum.NOT_CERTIFIED_CHECKIN.name());
        // assert that the updated cdump includes 'flowType'

        response = DcaeRestClient.getComposition(vfcmt.getUuid());
        String cdump = gson.fromJson(response.getResponse(), MessageResponse.class).getSuccessResponse();
        assertThat(cdump).isEqualTo("{\"flowType\":\"flowType_xxx\",\"cid\":\"1234567\"}");
		Report.log(Status.DEBUG, "Response updated mock cdump: " + cdump);
    }

    @Test
    public void testImportMonitoringComponentBothFlagsTrueSuccess() throws Exception {

        Service service;

        try {
            service = createServiceWithVFiAsSdcDesigner();
        } catch (Exception e) {
			Report.log(Status.ERROR, e);
            return;
        }

        String vfiName = service.getResources().get(0).getResourceInstanceName();
        Vfcmt vfcmt = prepareMockVfcmtForImport();
        ImportVFCMTRequest request = new ImportVFCMTRequest();
        request.setUpdateFlowType(true);
        request.setCloneVFCMT(true);
        request.setTemplateUuid(vfcmt.getUuid());
        request.setVfiName(vfiName);
        request.setServiceUuid(service.getUuid());
        DcaeRestClient.fillCreateMcRequestMandatoryFields(request);

        response = DcaeRestClient.importMc(gson.toJson(request));

        assertThat(response.getStatusCode())
                .as("status code")
                .isEqualTo(200);

        // assert that the vfcmt was cloned, the flowType updated and the reference artifact created
        CreateMcResponse mcResponse = gson.fromJson(response.getResponse(),CreateMcResponse.class);

        assertThat(mcResponse.getVfcmt().getFlowType()).isEqualTo("flowType_xxx");
        // check the reference artifact
        response = DcaeRestClient.getAttachedService(mcResponse.getVfcmt().getUuid());
        String ref = gson.fromJson(response.getResponse(), MessageResponse.class).getSuccessResponse();
        assertThat(ref).isEqualTo(service.getUuid()+"/resources/"+vfiName);

		Report.log(Status.DEBUG, "Response cloned Monitoring component: " + mcResponse);
    }

    @Test
    public void testImportMonitoringComponentServiceNotFoundFailure() throws Exception {

        Vfcmt vfcmt = prepareMockVfcmtForImport();
        ImportVFCMTRequest request = new ImportVFCMTRequest();
        request.setTemplateUuid(vfcmt.getUuid());
        DcaeRestClient.fillCreateMcRequestMandatoryFields(request);
        response = DcaeRestClient.importMc(gson.toJson(request));

        assertThat(response.getStatusCode())
                .as("status code")
                .isEqualTo(404);

		Report.log(Status.DEBUG, "Error response: " + response.getResponse());
        // assert vfcmt version was not increased
        response = DcaeRestClient.getResourceDetails(vfcmt.getUuid());
        vfcmt = gson.fromJson(response.getResponse(), Vfcmt.class);
        assertThat(vfcmt.getVersion()).isEqualTo("0.1");
    }


    private Vfcmt prepareMockVfcmtForImport() throws IOException {
        RestResponse createResponse = DcaeRestClient.createVfcmt(StringUtils.randomString("CI-", 20), "description", DcaeRestClient.getDesigner2UserId());
        Vfcmt vfcmt = gson.fromJson(createResponse.getResponse(), Vfcmt.class);
        // create a composition artifact
        DcaeRestClient.saveComposition(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId(), "{\"cid\":\"1234567\"}");
        return vfcmt;
    }

}
