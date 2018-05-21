package org.onap.dcae.ci.api.tests.blueprint;

import com.aventstack.extentreports.Status;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.assertj.core.api.SoftAssertions;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.utilities.DcaeUtil;
import org.onap.dcae.ci.report.Report;
import org.onap.dcae.ci.utilities.StringUtils;
import org.onap.sdc.dcae.composition.restmodels.sdc.Resource;
import org.onap.sdc.dcae.composition.services.Service;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;

import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class SaveCompositionPositve extends DcaeRestBaseTest {

	private RestResponse response;
    private static final String VFCMT_NAME = "teSt.__.monitoring---TempLATE.";
	
	private Vfcmt createVFCMTAndServiceWithVFIAndAttach() throws Exception{
		Report.log(Status.INFO, "createVFCMTAndServiceWithVFIAndAttach start");
		Vfcmt newVfcmt = createNewVfcmt();

        // create a service in sdc
        Service newService = createServiceWithVFiAsSdcDesigner();

        String serviceUuid = newService.getUuid();
        String vfiName = newService.getResources().get(0).getResourceInstanceName();
        String vfcmtId = newVfcmt.getUuid();

        // attach VFCMT to vfi and service (creation of svc_reference file in the BE)
        Report.log(Status.INFO, "Going to attach the created service and VFi to the VFCMT");
        RestResponse res = DcaeRestClient.attachVfiRef(vfcmtId, serviceUuid, vfiName);
        Report.log(Status.INFO, "attachment result="+res);
        
        return newVfcmt;
	}
	
	private Vfcmt createNewVfcmtWithoutArtifact() throws Exception{
		Report.log(Status.INFO, "createNewVfcmtWithoutArtifact");
		Vfcmt newVfcmt = createNewVfcmt();
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
	


	@Test
	public void test_responseStatusOkAndCreateMode() throws Exception{
		// assert
		Report.log(Status.INFO, "Save Composition init");
		Vfcmt vfcmtWithoutArtifactUuid = createNewVfcmtWithoutArtifact();
		String uuid = vfcmtWithoutArtifactUuid.getUuid();
		String lastUpdaterUserId = vfcmtWithoutArtifactUuid.getLastUpdaterUserId();
		response =  DcaeRestClient.saveComposition(uuid, lastUpdaterUserId);
		Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		Report.log(Status.INFO, "Verifing response status is 200");
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			softly.assertThat((gson.fromJson(response.getResponse(), Resource.class)).getVersion())
					.as("version")
					.isEqualTo("0.1");
		});
	}
	
	@Test
	public void test_updateMode() throws Exception {
		// assert
		Report.log(Status.INFO, "Save Composition init");
		Vfcmt vfcmtWithArtifactUuid = createVFCMTAndServiceWithVFIAndAttach();
		String uuid = vfcmtWithArtifactUuid.getUuid();
		String lastUpdaterUserId = vfcmtWithArtifactUuid.getLastUpdaterUserId();
		Report.log(Status.INFO, "New VFCMT uuid="+uuid+", lastUpdaterUserId="+lastUpdaterUserId);
		//generate cdump
        String cdump = DcaeUtil.generateCdumpFromSnmpModels(uuid);
		// save composition.yml 
		Report.log(Status.INFO, "Going to Save the composition now");
        DcaeRestClient.saveComposition(uuid, lastUpdaterUserId, cdump);
        // update
		response =  DcaeRestClient.saveComposition(uuid, lastUpdaterUserId, cdump);
		Report.log(Status.DEBUG, "Save composition Response: " + StringUtils.truncate(response));
		Report.log(Status.INFO, "Verifying response status is 200");
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			softly.assertThat((gson.fromJson(response.getResponse(), Resource.class)).getVersion())
					.as("version")
					.isEqualTo("0.3");
		});
	}
	
	
}
