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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.parser.ParseException;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.utilities.DcaeUtil;
import org.onap.dcae.ci.report.Report;
import org.onap.sdc.dcae.composition.services.Artifact;
import org.onap.sdc.dcae.composition.services.Resource;
import org.onap.sdc.dcae.composition.services.Service;
import org.onap.sdc.dcae.composition.util.DcaeBeConstants;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CompositionControllerApiTests extends DcaeRestBaseTest {

    private static final String EVENT_PROC_BP_YAML = "event_proc_bp.yaml";
    private static final String CREATE_BLUEPRINT_DESCRIPTION = "creating new artifact blueprint on the service vfi";
    private static final String UPDATE_BLUEPRINT_DESCRIPTION = "updating artifact blueprint on the service vfi";
    private static final String VFCMT_NAME = "teSt.__.monitoring---TempLATE.";
    private static final String NORMALIZED_VFCMT_NAME = "TestMonitoringTemplate";

    @Test
	public void createMultipleBlueprintsTest() throws Exception {
    	Report.log(Status.INFO, "createMultipleBlueprintsTest start");
        String randomSuffix = WordUtils.capitalize(RandomStringUtils.randomAlphanumeric(4).toLowerCase());
        Vfcmt newVfcmt = createVfcmt(randomSuffix);

        // create a service in sdc
        Service newService = createServiceWithVFiAsSdcDesigner();

        String serviceUuid = newService.getUuid();
        String vfiName = newService.getResources().get(0).getResourceInstanceName();
        String vfcmtId = newVfcmt.getUuid();

        // attach VFCMT to vfi
        Report.log(Status.INFO, "Trying to attach the service+vfi to the vfcmt. serviceUuid="+serviceUuid+", vfiName="+vfiName+", vfcmtId="+vfcmtId);
        RestResponse res = DcaeRestClient.attachVfiRef(vfcmtId, serviceUuid, vfiName);
        assertThat(res.getStatusCode()).isEqualTo(200);

        //generate and save composition.yml(cdump)
        generateAndSaveCdump(vfcmtId);

        String bpType1 = "bpType t1";
        String bpType2 = "bpType T2";
        String normalizedVfcmtName = NORMALIZED_VFCMT_NAME + randomSuffix;
        List<ImmutablePair<String, String>> expectedBpDetails = Arrays.asList(buildBlueprintDetails(bpType1, normalizedVfcmtName), buildBlueprintDetails(bpType2, normalizedVfcmtName));

        // submit - generate and save type1 blueprint artifact
        Report.log(Status.INFO, "Going to Submit the composition with vfcmtId="+vfcmtId+", serviceUuid="+serviceUuid+", vfiName="+vfiName+", TYPE1="+bpType1);
        res = DcaeRestClient.submitComposition(vfcmtId, serviceUuid, vfiName, bpType1);
        assertBlueprintSuccessResponse(res);

        // 1806 US 374593 assert vfcmt is certified
        res = DcaeRestClient.getResourceDetails(vfcmtId);
        newVfcmt = gson.fromJson(res.getResponse(), Vfcmt.class);
        Report.log(Status.INFO, "Checking lifecycle state of vfcmt after first blueprint submission. State = {}", newVfcmt.getLifecycleState());
        assertThat(newVfcmt.getLifecycleState()).isEqualTo(DcaeBeConstants.LifecycleStateEnum.CERTIFIED.name());
        Report.log(Status.INFO, "Checking version of vfcmt after first blueprint submission. Version = {}", newVfcmt.getVersion());
        assertThat(newVfcmt.getVersion()).isEqualTo("1.0");

        res = DcaeRestClient.checkoutVfcmt(vfcmtId, newVfcmt.getLastUpdaterUserId());
        newVfcmt = gson.fromJson(res.getResponse(), Vfcmt.class);
        vfcmtId = newVfcmt.getUuid();
        // submit - generate and save type2 blueprint artifact
        Report.log(Status.INFO, "Going to Submit the composition with vfcmtId="+vfcmtId+", serviceUuid="+serviceUuid+", vfiName="+vfiName+", TYPE2="+bpType2);
        res = DcaeRestClient.submitComposition(vfcmtId,  serviceUuid, vfiName, bpType2);
        assertBlueprintSuccessResponse(res);
        res = DcaeRestClient.getResourceDetails(vfcmtId);
        newVfcmt = gson.fromJson(res.getResponse(), Vfcmt.class);
        Report.log(Status.INFO, "Checking lifecycle state of vfcmt after checkout and second blueprint submission. State = {}", newVfcmt.getLifecycleState());
        assertThat(newVfcmt.getLifecycleState()).isEqualTo(DcaeBeConstants.LifecycleStateEnum.CERTIFIED.name());
        Report.log(Status.INFO, "Checking version of vfcmt after checkout and second blueprint submission. Version = {}", newVfcmt.getVersion());
        assertThat(newVfcmt.getVersion()).isEqualTo("2.0");
        assertBlueprintsExistOnVfiAfterUpload(expectedBpDetails, serviceUuid, vfiName);
    }


    @Test
    public void updateBlueprintTest() throws Exception {
    	Report.log(Status.INFO, "updateBlueprintTest start");
        String randomSuffix = WordUtils.capitalize(RandomStringUtils.randomAlphanumeric(4).toLowerCase());
        Vfcmt newVfcmt = createVfcmt(randomSuffix);

        // create a service in sdc
        Service newService = createServiceWithVFiAsSdcDesigner();

        String serviceUuid = newService.getUuid();
        String vfiName = newService.getResources().get(0).getResourceInstanceName();
        String vfcmtId = newVfcmt.getUuid();

        // attach VFCMT to vfi
        RestResponse res = DcaeRestClient.attachVfiRef(vfcmtId, serviceUuid, vfiName);
        assertThat(res.getStatusCode()).isEqualTo(200);

        generateAndSaveCdump(vfcmtId);

        String normalizedVfcmtName = NORMALIZED_VFCMT_NAME + randomSuffix;
        String bpType1 = "bpType 1";
        ImmutablePair<String, String> expectedBpDetails = buildBlueprintDetails(bpType1, normalizedVfcmtName);

        // submit - generate and save type1 blueprint artifact
        Report.log(Status.INFO, "Going to Submit the composition with vfcmtId="+vfcmtId+", serviceUuid="+serviceUuid+", vfiName="+vfiName+", bpType1="+bpType1);
        res = DcaeRestClient.submitComposition(vfcmtId, serviceUuid, vfiName, bpType1);
        assertBlueprintSuccessResponse(res);
        // assert initial version exists
        assertBlueprintExistsOnVfi(expectedBpDetails, serviceUuid, vfiName, CREATE_BLUEPRINT_DESCRIPTION);

        // submit - regenerate and save type1 blueprint artifact
        Report.log(Status.INFO, "Going to Submit the composition with vfcmtId="+vfcmtId+", serviceUuid="+serviceUuid+", vfiName="+vfiName+", bpType1="+bpType1);
        res = DcaeRestClient.submitComposition(vfcmtId, serviceUuid, vfiName, bpType1);
        assertBlueprintSuccessResponse(res);
        // assert blueprint updated
        assertBlueprintExistsOnVfi(expectedBpDetails, serviceUuid, vfiName, UPDATE_BLUEPRINT_DESCRIPTION);

    }


    private void assertBlueprintSuccessResponse(RestResponse response){
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(DcaeUtil.getValueFromJsonResponse(response.getResponse(), "successResponse")).isNotNull();
    }

    private void assertBlueprintsExistOnVfiAfterUpload(List<ImmutablePair<String, String>> expectedBpDetails, String serviceId, String vfiName) throws IOException{
       List<Artifact> vfiArtifact = fetchVfiArtifacts(serviceId, vfiName);
       List<ImmutablePair<String, String>> vfiArtifactDetails = vfiArtifact.stream()
                                                        .map(a -> new ImmutablePair<>(a.getArtifactLabel(), a.getArtifactName()))
                                                        .collect(Collectors.toList());
       for(ImmutablePair<String,String> bp : expectedBpDetails){
           assertThat(vfiArtifactDetails).contains(bp);
       }
    }

    private void assertBlueprintExistsOnVfi(ImmutablePair<String, String> expectedBpDetails, String serviceId, String vfiName, String description) throws IOException{
        List<Artifact> vfiArtifact = fetchVfiArtifacts(serviceId, vfiName);
        Predicate<Artifact> artifactPredicate = p -> expectedBpDetails.getLeft().equals(p.getArtifactLabel()) && expectedBpDetails.getRight().equals(p.getArtifactName()) && description.equals(p.getArtifactDescription());
        Artifact artifact = vfiArtifact.stream()
                .filter(artifactPredicate)
                .findAny()
                .orElse(null);
        assertThat(artifact).isNotNull();
    }


    private List<Artifact> fetchVfiArtifacts(String serviceId, String vfiName) throws IOException {
        Report.log(Status.INFO, "fetchVfiArtifacts start");
        RestResponse serviceRes = DcaeRestClient.getServicesInstance(serviceId);
        Report.log(Status.INFO, "fetchVfiArtifacts response=%s", serviceRes);
        Service service = gson.fromJson(serviceRes.getResponse(), Service.class);
        Resource vfi = service.getResources().stream()
                .filter(p -> p.getResourceInstanceName().equals(vfiName))
                .findAny()
                .orElse(null);
        assertThat(vfi).isNotNull();
        return vfi.getArtifacts();
    }

    private ImmutablePair<String, String> buildBlueprintDetails(String bpType, String normalizedVfcmtName) {
        return new ImmutablePair<>("blueprint"+(bpType.replaceAll(" ", "")+normalizedVfcmtName).toLowerCase(),bpType.replaceAll(" ", "-")+"."+normalizedVfcmtName+"."+EVENT_PROC_BP_YAML);
    }


    private Vfcmt createVfcmt(String randomSuffix) throws IOException {
        String newName = VFCMT_NAME + randomSuffix;
        Report.log(Status.DEBUG, "createVfcmt start. VFCMT Name="+newName);
        RestResponse res = DcaeRestClient.createVfcmt(newName,"description");
        Report.log(Status.DEBUG, "Create VFCMT response=%s", res);
        assertThat(res.getStatusCode()).isEqualTo(200);
        return gson.fromJson(res.getResponse(), Vfcmt.class);
    }

    private void generateAndSaveCdump(String vfcmtId) throws ParseException, IOException {
    	Report.log(Status.DEBUG, "generateAndSaveCdump start");
        RestResponse res = DcaeUtil.SdcElementsModelType.getItemModelFromSdc(0);
        assertThat(res.getStatusCode()).isEqualTo(200);
        JsonObject cdump = DcaeRestClient.generateCdumpInput(vfcmtId);
        Report.log(Status.DEBUG, "Generated cdump="+cdump);
        cdump.add("nodes", DcaeUtil.parseNodesFromDcaeModelAndAssignUiNid(res.getResponse()));
        Report.log(Status.DEBUG, "save composition - generate and save composition.yml artifact");
        res = DcaeRestClient.saveComposition(vfcmtId, DcaeRestClient.getDefaultUserId(), cdump.toString());
        Report.log(Status.DEBUG, "Save composition result="+res);
        assertThat(res.getStatusCode()).isEqualTo(200);
    }


}
