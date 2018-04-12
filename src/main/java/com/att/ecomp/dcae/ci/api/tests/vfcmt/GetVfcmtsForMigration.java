package com.att.ecomp.dcae.ci.api.tests.vfcmt;

import com.att.ecomp.dcae.ci.api.tests.DcaeRestBaseTest;
import com.att.ecomp.dcae.ci.utilities.DcaeRestClient;
import com.att.ecomp.dcae.ci.utilities.StringUtils;
import com.aventstack.extentreports.Status;
import org.onap.sdc.dcae.composition.restmodels.CreateMcResponse;
import org.onap.sdc.dcae.composition.restmodels.CreateVFCMTRequest;
import org.onap.sdc.dcae.composition.restmodels.sdc.Resource;
import org.onap.sdc.dcae.composition.services.Service;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.openecomp.d2.ci.report.ExtentTestActions;
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
            ExtentTestActions.log(Status.INFO, "Selecting vfcmt resources for migration");
            response = DcaeRestClient.getVfcmtsForMigration("services","1111111","1.3");
            ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));

            ExtentTestActions.log(Status.INFO, "getAllMonitoringTemplatesVfcmts");
            RestResponse clientResponse = DcaeRestClient.getAllMonitoringTemplatesVfcmts();
            Resource[] resources = gson.fromJson(clientResponse.getResponse(),Resource[].class);
            if (resources.length > 0){
                res = resources[0];
            }
        } catch (Exception err) {
            ExtentTestActions.log(Status.ERROR, err);
            err.printStackTrace();
        }
    }

    @Test
    public void testResponseStatus500ServiceNotFound() {
        // assert
        ExtentTestActions.log(Status.INFO, "Verifing response status is 500");
        assertThat(response.getStatusCode()).as("response status").isEqualTo(500);
        assertThat(response.getResponse().contains("resource was not found"));
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
            e.printStackTrace();
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
            addMandatoryInputFieldsToRequestObject(request);
            response = DcaeRestClient.createMc(gson.toJson(request));
            assertThat(response.getStatusCode()).as(STATUS_CODE).isEqualTo(200);
            CreateMcResponse mcResponse = gson.fromJson(response.getResponse(),CreateMcResponse.class);
            String key = mcResponse.getVfcmt().getUuid();
            String value = gson.toJson(mcResponse.getCdump());
            assertThat(value.contains(key)).isTrue();
            ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
            listOfVfcmtsConnectedToService.add(key);
        }

        response = DcaeRestClient.getVfcmtsForMigration("services",service.getUuid(),service.getVersion());
        assertThat(response.getStatusCode()).as(STATUS_CODE).isEqualTo(200);

        listOfVfcmtsConnectedToService.forEach( s -> assertThat(response.getResponse().contains(s)).isFalse());

        response = DcaeRestClient.getAllVfcmts();
        assertThat(response.getStatusCode()).as(STATUS_CODE).isEqualTo(200);

        listOfVfcmtsConnectedToService.forEach( s -> assertThat(response.getResponse().contains(s)).isTrue());

    }
}
