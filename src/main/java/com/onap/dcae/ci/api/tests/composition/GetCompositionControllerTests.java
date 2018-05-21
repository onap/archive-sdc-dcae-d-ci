package com.onap.dcae.ci.api.tests.composition;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import com.onap.dcae.ci.utilities.DcaeUtil;
import com.onap.dcae.ci.utilities.Report;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.openecomp.d2.ci.report.ExtentTestActions;
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
        
        ExtentTestActions.log(Status.INFO, "Get Composition - execute");
		RestResponse response = DcaeRestClient.getComposition(uuid);
		JSONObject resData = (JSONObject) JSONValue.parse(response.getResponse());
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			softly.assertThat(resData.get("successResponse").toString()).isNotEmpty();
		});
	}
	
	@Test
	public void test_getCompositionNoArtifact() throws Exception{
		ExtentTestActions.log(Status.INFO, "test_getCompositionNoArtifact Get Composition - create vfcmt");
		
		Vfcmt newVfcmt = createNewVfcmt();
		String uuid = newVfcmt.getUuid();
        
        ExtentTestActions.log(Status.INFO, "Get Composition - execute");
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
