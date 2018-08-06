package org.onap.dcae.ci.api.tests.composition;


import com.aventstack.extentreports.Status;
import com.google.gson.reflect.TypeToken;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.report.Report;
import org.onap.sdc.dcae.composition.restmodels.CreateMcResponse;
import org.onap.sdc.dcae.composition.restmodels.CreateVFCMTRequest;
import org.onap.sdc.dcae.composition.restmodels.MonitoringComponent;
import org.onap.sdc.dcae.composition.restmodels.VfcmtData;
import org.onap.sdc.dcae.composition.restmodels.sdc.Resource;
import org.onap.sdc.dcae.composition.restmodels.sdc.ResourceDetailed;
import org.onap.sdc.dcae.composition.services.Service;
import org.onap.sdc.dcae.composition.util.DcaeBeConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SaveAndSubmitCompositionFullFlowTest extends DcaeRestBaseTest {

    private Resource baseTemplate = null;


    @BeforeClass
    public void setup() {
        try {
			Report.log(Status.INFO, "running before class - find a base template");
            Resource[] resources = gson.fromJson(DcaeRestClient.getAllMonitoringTemplatesVfcmts().getResponse(), Resource[].class);
            if (resources.length > 0){
                baseTemplate = resources[0];
            }
        } catch (Exception e) {
			Report.log(Status.ERROR, e);
        }
    }


    @Test
    public void saveAndSubmitCompositionSuccessTest() throws IOException {

        CreateVFCMTRequest request = new CreateVFCMTRequest();
        // If you crashed here (below) it is because your environment has no Base Monitoring Templates
        request.setTemplateUuid(baseTemplate.getUuid());
        Service service = createServiceWithVFiAsSdcDesigner();
        request.setVfiName(service.getResources().get(0).getResourceInstanceName());
        request.setServiceUuid(service.getUuid());
        DcaeRestClient.fillCreateMcRequestMandatoryFields(request);

        RestResponse response = DcaeRestClient.createMc(gson.toJson(request));
        assertThat(response.getStatusCode())
                .as("status code")
                .isEqualTo(200);

        CreateMcResponse mcResponse = gson.fromJson(response.getResponse(), CreateMcResponse.class);
		Report.log(Status.INFO, "Vfcmt created successfully. About to update composition");
        String initialUuid =  mcResponse.getVfcmt().getUuid();
        response = DcaeRestClient.saveComposition(request.getServiceUuid(), request.getVfiName(), initialUuid, gson.toJson(mcResponse.getCdump()));
        assertThat(response.getStatusCode())
                .as("status code")
                .isEqualTo(200);

        ResourceDetailed mc = gson.fromJson(response.getResponse(), ResourceDetailed.class);
        // the save action should check the mc out then in - promoting the version
        assertThat(mc.getLifecycleState()).isEqualTo(DcaeBeConstants.LifecycleStateEnum.NOT_CERTIFIED_CHECKIN.name());
        assertThat(mc.getVersion()).isEqualTo("0.2");

		Report.log(Status.INFO, "About to submit the composition");
        response = DcaeRestClient.submitComposition(request.getServiceUuid(), request.getVfiName(), mc.getUuid());
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		// the submit action should certify the mc.
		Report.log(Status.INFO, "Save new composition version after submit");
		response = DcaeRestClient.saveComposition(request.getServiceUuid(), request.getVfiName(), initialUuid, gson.toJson(mcResponse.getCdump()));
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		// the save action should promote the mc version to 1.1 and create a new reference to it - both references should be kept at this point
		mc = gson.fromJson(response.getResponse(), ResourceDetailed.class);
		assertThat(mc.getLifecycleState()).isEqualTo(DcaeBeConstants.LifecycleStateEnum.NOT_CERTIFIED_CHECKIN.name());
		assertThat(mc.getVersion()).isEqualTo("1.1");

		response = DcaeRestClient.getServiceExternalReferences(service.getUuid(), service.getVersion());
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		Report.log(Status.INFO, "Verify service vfi has references to both mc versions");
		Type typeToken = new TypeToken<Map<String, List<MonitoringComponent>>>(){}.getType();
		Map<String, List<MonitoringComponent>> monitoringComponents = gson.fromJson(response.getResponse(), typeToken);
		assertThat(monitoringComponents.get("monitoringComponents").size()).isEqualTo(2);
		Report.log(Status.INFO, "About to re-submit the composition");
		response = DcaeRestClient.submitComposition(request.getServiceUuid(), request.getVfiName(), mc.getUuid());
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		// a successful submission of the new version should result in the deletion of the previous reference
		response = DcaeRestClient.getServiceExternalReferences(service.getUuid(), service.getVersion());
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		monitoringComponents = gson.fromJson(response.getResponse(), typeToken);
		List<MonitoringComponent> mcList = monitoringComponents.get("monitoringComponents");
		assertThat(mcList.size()).isEqualTo(1);
		assertThat(mcList.get(0).getUuid()).isEqualTo(mc.getUuid());

    }


	@Test
	public void certifiedMcCheckoutAndBindToServiceSuccessTest() throws IOException {

		CreateVFCMTRequest request = new CreateVFCMTRequest();
		// If you crashed here (below) it is because your environment has no Base Monitoring Templates
		request.setTemplateUuid(baseTemplate.getUuid());
		Service service = createServiceWithVFiAsSdcDesigner();
		request.setVfiName(service.getResources().get(0).getResourceInstanceName());
		request.setServiceUuid(service.getUuid());
		DcaeRestClient.fillCreateMcRequestMandatoryFields(request);

		RestResponse response = DcaeRestClient.createMc(gson.toJson(request));
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);

		CreateMcResponse mcResponse = gson.fromJson(response.getResponse(), CreateMcResponse.class);
		String initialUuid =  mcResponse.getVfcmt().getUuid();

		response = DcaeRestClient.getLatestMcUuid(request.getContextType(), request.getServiceUuid(), request.getVfiName(), initialUuid);
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		VfcmtData getLatestUuidRes = gson.fromJson(response.getResponse(), VfcmtData.class);
		assertThat(initialUuid).isEqualTo(getLatestUuidRes.getUuid());

		response = DcaeRestClient.getServiceExternalReferences(service.getUuid(), service.getVersion());
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		Report.log(Status.INFO, "Verify service vfi only references initial mc version");
		Type typeToken = new TypeToken<Map<String, List<MonitoringComponent>>>(){}.getType();
		Map<String, List<MonitoringComponent>> monitoringComponents = gson.fromJson(response.getResponse(), typeToken);
		assertThat(monitoringComponents.get("monitoringComponents").size()).isEqualTo(1);
		Report.log(Status.INFO, "About to submit the composition");
		response = DcaeRestClient.submitComposition(request.getServiceUuid(), request.getVfiName(), initialUuid);
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);

		response = DcaeRestClient.getLatestMcUuid(request.getContextType(), request.getServiceUuid(), request.getVfiName(), initialUuid);
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		getLatestUuidRes = gson.fromJson(response.getResponse(), VfcmtData.class);
		Report.log(Status.INFO, "Verify latest Mc uuid is not the initial one");
		assertThat(initialUuid).isNotEqualTo(getLatestUuidRes.getUuid());
		response = DcaeRestClient.getServiceExternalReferences(service.getUuid(), service.getVersion());
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		Report.log(Status.INFO, "Verify service vfi has references to both mc versions");

		monitoringComponents = gson.fromJson(response.getResponse(), typeToken);
		assertThat(monitoringComponents.get("monitoringComponents").size()).isEqualTo(2);

	}

}
