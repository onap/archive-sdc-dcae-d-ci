package com.onap.dcae.ci.api.tests.vfcmt;

import com.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import com.onap.dcae.ci.utilities.StringUtils;
import com.aventstack.extentreports.Status;
import org.onap.sdc.dcae.composition.restmodels.CreateMcResponse;
import org.onap.sdc.dcae.composition.restmodels.CreateVFCMTRequest;
import org.onap.sdc.dcae.composition.restmodels.ImportVFCMTRequest;
import org.onap.sdc.dcae.composition.restmodels.MessageResponse;
import org.onap.sdc.dcae.composition.restmodels.sdc.Resource;
import org.onap.sdc.dcae.composition.restmodels.sdc.ResourceDetailed;
import org.onap.sdc.dcae.composition.services.Service;
import org.onap.sdc.dcae.composition.util.DcaeBeConstants;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import org.openecomp.d2.ci.api.ElementFactory;
import org.openecomp.d2.ci.datatypes.UserRoleEnum;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.openecomp.d2.ci.report.ExtentTestActions;
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
            ExtentTestActions.log(Status.INFO, "Creating vfcmt");
            RestResponse clientResponse = DcaeRestClient.getAllMonitoringTemplatesVfcmts();
            Resource[] resources = gson.fromJson(clientResponse.getResponse(),Resource[].class);
            if (resources.length > 0){
                res = resources[0];
            }
        } catch (Exception err) {
            ExtentTestActions.log(Status.ERROR, err);
        }
    }

    @Test
    public void testCreateMonitoringComponentSuccess() throws IOException {

        Service service;

        try {
            service = createServiceWithVFiAsSdcDesigner();
        } catch (Exception e) {
            ExtentTestActions.log(Status.ERROR, e);
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

        ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
    }

    @Test
    public void testCreateMonitoringComponentBadUser() throws IOException {

        String fakeUserId = "anonymous";
        CreateVFCMTRequest request = new CreateVFCMTRequest();
        // If you crashed here (below) it is because your environment has no Base Monitoring Templates
        request.setTemplateUuid(res.getUuid());
        DcaeRestClient.fillCreateMcRequestMandatoryFields(request);

        response = DcaeRestClient.createMc(gson.toJson(request),fakeUserId);

        ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
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
            ExtentTestActions.log(Status.ERROR, e);
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
        ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
    }

    @Test
    public void testImportMonitoringComponentCloneFalseUpdateFlowTypeTrueSuccess() throws Exception {

        Service service;

        try {
            service = createServiceWithVFiAsSdcDesigner();
        } catch (Exception e) {
            ExtentTestActions.log(Status.ERROR, e);
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
        assertThat(updatedMC.getLastUpdaterUserId()).isEqualTo(DcaeRestClient.getDefaultUser().getUserId());
        assertThat(updatedMC.getLifecycleState()).isEqualTo(DcaeBeConstants.LifecycleStateEnum.NOT_CERTIFIED_CHECKIN.name());
        // assert that the updated cdump includes 'flowType'

        response = DcaeRestClient.getComposition(vfcmt.getUuid());
        String cdump = gson.fromJson(response.getResponse(), MessageResponse.class).getSuccessResponse();
        assertThat(cdump).isEqualTo("{\"flowType\":\"flowType_xxx\",\"cid\":\"1234567\"}");
        ExtentTestActions.log(Status.DEBUG, "Response updated mock cdump: " + cdump);
    }

    @Test
    public void testImportMonitoringComponentBothFlagsTrueSuccess() throws Exception {

        Service service;

        try {
            service = createServiceWithVFiAsSdcDesigner();
        } catch (Exception e) {
            ExtentTestActions.log(Status.ERROR, e);
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

        ExtentTestActions.log(Status.DEBUG, "Response cloned Monitoring component: " + mcResponse);
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

        ExtentTestActions.log(Status.DEBUG, "Error response: " + response.getResponse());
        // assert vfcmt version was not increased
        response = DcaeRestClient.getResourceDetails(vfcmt.getUuid());
        vfcmt = gson.fromJson(response.getResponse(), Vfcmt.class);
        assertThat(vfcmt.getVersion()).isEqualTo("0.1");
    }


    private Vfcmt prepareMockVfcmtForImport() throws IOException {
        RestResponse createResponse = DcaeRestClient.createVfcmt(StringUtils.randomString("CI-", 20), "description", ElementFactory.getDefaultUser(UserRoleEnum.DESIGNER2).getUserId());
        Vfcmt vfcmt = gson.fromJson(createResponse.getResponse(), Vfcmt.class);
        // create a composition artifact
        DcaeRestClient.saveComposition(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId(), "{\"cid\":\"1234567\"}");
        return vfcmt;
    }

}
