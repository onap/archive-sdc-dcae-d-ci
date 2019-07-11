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

package org.onap.dcae.ci.api.tests.composition;


import com.aventstack.extentreports.Status;
import com.google.gson.JsonObject;
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
		assertThat(monitoringComponents.get("monitoringComponents").size()).isEqualTo(1);
		MonitoringComponent monitoringComponent = monitoringComponents.get("monitoringComponents").get(0);
		assertThat(monitoringComponent.getSubmittedUuid()).isEqualTo(initialUuid);
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
		String initialUuid = mcResponse.getVfcmt().getUuid();

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
		MonitoringComponent monitoringComponent = monitoringComponents.get("monitoringComponents").get(0);
		assertThat(monitoringComponent.getSubmittedUuid()).isNull();
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
		assertThat(monitoringComponents.get("monitoringComponents").size()).isEqualTo(1);
		monitoringComponent = monitoringComponents.get("monitoringComponents").get(0);
		assertThat(monitoringComponent.getSubmittedUuid()).isEqualTo(initialUuid);

	}

	@Test
	public void revertOverwriteRevertedAndDeleteMcTest() throws IOException {

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
		Report.log(Status.INFO, "Vfcmt created successfully. About to submit composition");
		String initialUuid =  mcResponse.getVfcmt().getUuid();

		Report.log(Status.INFO, "About to submit the composition");
		response = DcaeRestClient.submitComposition(request.getServiceUuid(), request.getVfiName(), initialUuid);
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		// the submit action should certify the mc.
		Report.log(Status.INFO, "Save new composition version after submit");
		String nid = "12313";
		response = DcaeRestClient.saveComposition(request.getServiceUuid(), request.getVfiName(), initialUuid, String.format("{\"nid\":\"%s\"}", nid));
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		// the save action should promote the mc version to 1.1 and a new reference should be created - both references should be kept at this point
		ResourceDetailed mc = gson.fromJson(response.getResponse(), ResourceDetailed.class);
		assertThat(mc.getLifecycleState()).isEqualTo(DcaeBeConstants.LifecycleStateEnum.NOT_CERTIFIED_CHECKIN.name());
		assertThat(mc.getVersion()).isEqualTo("1.1");
		response = DcaeRestClient.saveRule(mc.getUuid(), "dcaeComp", nid, "configParam",  "{version:4.1,eventType:syslogFields,description:newRule,actions:[{from:{state:closed,value:fromField,regex:\"\"},target:event.commonEventHeader.target,id:id,actionType:copy}]}");
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		response = DcaeRestClient.getServiceExternalReferences(service.getUuid(), service.getVersion());
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		Report.log(Status.INFO, "Verify service vfi has references to both mc versions");
		Type typeToken = new TypeToken<Map<String, List<MonitoringComponent>>>(){}.getType();
		Map<String, List<MonitoringComponent>> monitoringComponents = gson.fromJson(response.getResponse(), typeToken);
		assertThat(monitoringComponents.get("monitoringComponents").size()).isEqualTo(1);
		MonitoringComponent monitoringComponent = monitoringComponents.get("monitoringComponents").get(0);
		String revertableUuid = monitoringComponent.getUuid();
		assertThat(monitoringComponent.getSubmittedUuid()).isEqualTo(initialUuid);
		Report.log(Status.INFO, "About to revert to the submitted composition");
		response = DcaeRestClient.revertToSubmittedComposition(request.getServiceUuid(), request.getVfiName(), monitoringComponent.getUuid(), initialUuid);
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		// a successful revert of the new version should result in the submitted MC referencing it
		response = DcaeRestClient.getServiceExternalReferences(service.getUuid(), service.getVersion());
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		monitoringComponents = gson.fromJson(response.getResponse(), typeToken);
		List<MonitoringComponent> mcList = monitoringComponents.get("monitoringComponents");
		assertThat(mcList.size()).isEqualTo(1);
		monitoringComponent = monitoringComponents.get("monitoringComponents").get(0);
		assertThat(monitoringComponent.getUuid()).isEqualTo(initialUuid.concat("/").concat(revertableUuid));
		// before saving the composition the reverted MC has 3 artifacts - ref, cdump and rule.
		response = DcaeRestClient.getResourceDetails(revertableUuid);
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		mc = gson.fromJson(response.getResponse(), ResourceDetailed.class);
		assertThat(mc.getArtifacts().size()).isEqualTo(3);
		// after saving the composition the reverted MC should be overwritten by the initial version and only contain 2 artifacts - ref and cdump (no rule).
		response = DcaeRestClient.saveComposition(service.getUuid(), request.getVfiName(), monitoringComponent.getUuid(), gson.toJson(mcResponse.getCdump()));
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		response = DcaeRestClient.getResourceDetails(revertableUuid);
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		mc = gson.fromJson(response.getResponse(), ResourceDetailed.class);
		assertThat(mc.getArtifacts().size()).isEqualTo(2);
		// a save rule request should now fail with the provided nid as the composition has been reset to the initial one
		response = DcaeRestClient.saveRule(mc.getUuid(), "dcaeComp", nid, "configParam",  "{version:4.1,eventType:syslogFields,description:newRule,actions:[{from:{state:closed,value:fromField,regex:\"\"},target:event.commonEventHeader.target,id:id,actionType:copy}]}");
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(400);
		assertThat(response.getResponse()).isEqualTo("{\"requestError\":{\"serviceException\":{\"messageId\":\"SVC6114\",\"text\":\"DCAE component %1 not found in composition\",\"variables\":[\"dcaeComp\"],\"formattedErrorMessage\":\"DCAE component dcaeComp not found in composition\"}},\"notes\":\"\"}");
		response = DcaeRestClient.getServiceExternalReferences(service.getUuid(), service.getVersion());
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		monitoringComponents = gson.fromJson(response.getResponse(), typeToken);
		mcList = monitoringComponents.get("monitoringComponents");
		assertThat(mcList.size()).isEqualTo(1);
		monitoringComponent = monitoringComponents.get("monitoringComponents").get(0);
		assertThat(monitoringComponent.getUuid()).isEqualTo(revertableUuid);
		// revert again and then delete. table should be empty
		Report.log(Status.INFO, "About to revert to the submitted composition");
		response = DcaeRestClient.revertToSubmittedComposition(request.getServiceUuid(), request.getVfiName(), revertableUuid, initialUuid);
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		monitoringComponent = gson.fromJson(response.getResponse(), MonitoringComponent.class);
		Report.log(Status.INFO, "About to delete the submitted MC");
		response = DcaeRestClient.deleteMcReferenceAndBP(monitoringComponent.getName(), request.getServiceUuid(), request.getVfiName(), monitoringComponent.getUuid());
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		response = DcaeRestClient.getServiceExternalReferences(service.getUuid(), service.getVersion());
		assertThat(response.getStatusCode())
				.as("status code")
				.isEqualTo(200);
		monitoringComponents = gson.fromJson(response.getResponse(), typeToken);
		mcList = monitoringComponents.get("monitoringComponents");
		assertThat(mcList.size()).isEqualTo(0);
	}
}
