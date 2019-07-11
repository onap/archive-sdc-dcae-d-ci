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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.utilities.DcaeUtil;
import org.onap.dcae.ci.report.Report;
import org.testng.annotations.Test;

import org.onap.sdc.dcae.composition.services.Service;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import com.aventstack.extentreports.Status;

public class GetCompositionControllerTests extends DcaeRestBaseTest {

	private static final String VFCMT_NAME = "teSt.__.monitoring---TempLATE.";
	
	@Test
	public void test_getComposition() throws Exception{
		Report.log(Status.INFO, "test_getComposition start");
		Vfcmt vfcmtWithArtifactUuid = createServiceAndVfiAndVfcmtAndAttach();
		String uuid = vfcmtWithArtifactUuid.getUuid();
		String lastUpdaterUserId = vfcmtWithArtifactUuid.getLastUpdaterUserId();
		Report.log(Status.INFO, "the created VFCMT uuid is:"+uuid+", going to save composition now");
		saveComposition(uuid, lastUpdaterUserId);
		Report.log(Status.INFO, "Composition saved");
        
        Report.log(Status.INFO, "Get Composition - execute");
		RestResponse response = DcaeRestClient.getComposition(uuid);
		JSONObject resData = (JSONObject) JSONValue.parse(response.getResponse());
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			softly.assertThat(resData.get("successResponse").toString()).isNotEmpty();
		});
	}
	
	@Test
	public void test_getCompositionNoArtifact() throws Exception{
		Report.log(Status.INFO, "test_getCompositionNoArtifact Get Composition - create vfcmt");
		
		Vfcmt newVfcmt = createNewVfcmt();
		String uuid = newVfcmt.getUuid();

		Report.log(Status.INFO, "Get Composition - execute");
		RestResponse response = DcaeRestClient.getComposition(uuid);
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(204);
		});
	}

	private void saveComposition(String uuid, String lastUpdaterUserId) throws Exception, IOException {
		//generate cdump
        String cdump = generateAndSaveCdump(uuid);
		// save composition.yml 
        DcaeRestClient.saveComposition(uuid, lastUpdaterUserId, cdump);
	}
	
	private Vfcmt createServiceAndVfiAndVfcmtAndAttach() throws Exception{
		Vfcmt newVfcmt = createNewVfcmt();

        // create a service in sdc
        Service newService = createServiceWithVFiAsSdcDesigner();

        String serviceUuid = newService.getUuid();
        String vfiName = newService.getResources().get(0).getResourceInstanceName();
        String vfcmtId = newVfcmt.getUuid();

        // attach VFCMT to vfi
        RestResponse res = DcaeRestClient.attachVfiRef(vfcmtId, serviceUuid, vfiName);
        
        return newVfcmt;
	}
	
	private Vfcmt createNewVfcmt() throws Exception {
		String randomSuffix = WordUtils.capitalize(RandomStringUtils.randomAlphanumeric(4).toLowerCase());
        Vfcmt newVfcmt = createVfcmtInstance(randomSuffix);
		return newVfcmt;
	}
	
	private Vfcmt createVfcmtInstance(String randomSuffix) throws Exception{
        String newName = VFCMT_NAME + randomSuffix;
        RestResponse res = DcaeRestClient.createVfcmt(newName,"description");
        assertThat(res.getStatusCode()).isEqualTo(200);
        return gson.fromJson(res.getResponse(), Vfcmt.class);
    }
	private String generateAndSaveCdump(String vfcmtId) throws Exception{
    	JsonArray snmpModelItemFromSdc = DcaeUtil.SdcElementsModelType.getSNMPModelItemFromSdc();
        JsonObject cdump = DcaeRestClient.generateCdumpInput(vfcmtId);
        cdump.add("nodes", snmpModelItemFromSdc);
        return cdump.toString();
    }
}
