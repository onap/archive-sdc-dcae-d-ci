package org.onap.dcae.ci.api.tests.lifeCycle;

import java.io.IOException;
import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;

import org.onap.dcae.ci.report.Report;
import org.onap.dcae.ci.utilities.StringUtils;
import org.testng.annotations.*;
import static org.assertj.core.api.Assertions.*;


import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;

import com.aventstack.extentreports.Status;

public class PutCheckin extends DcaeRestBaseTest {
	
	private ArrangeHelper arrange = new ArrangeHelper(client);
	
	/* Positive */
	
	@Test
	public void checkedoutVfcmt_success() throws Exception {
		// arrange
		Vfcmt vfcmt = arrange.getCheckedoutVfcmt();
		// act
		Report.log(Status.INFO, "Checkin the vfcmt with it's lastUpdater user");
		RestResponse response = checkinVfcmt(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
		// assert
		Vfcmt vfcmtAfterCheckin = gson.fromJson(response.getResponse(), Vfcmt.class);
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).isEqualTo(200);
			softly.assertThat(vfcmtAfterCheckin.getLifecycleState()).contains("CHECKIN");
		});
	}
	
	/* Negative */
	
	@Test
	public void alreadyCheckedinVfcmt_statusCode409() throws Exception {
		// arrange
		Vfcmt vfcmt = arrange.getCheckedinVfcmt();
		// act
		Report.log(Status.INFO, "Checkin the vfcmt <b>AGAIN</b> with it's lastUpdater user");
		RestResponse response = checkinVfcmt(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
		// assert
		assertThat(response.getStatusCode()).isEqualTo(409);
	}
	
	@Test
	public void invalidVfcmtUuid_statusCode400() throws Exception {
		// arrange
		String userId = DcaeRestClient.getDefaultUserId();
		// act
		Report.log(Status.INFO, "Checkin with an invalid-uuid as vfcmt-uuid");
		RestResponse response = checkinVfcmt("invalid-vfcmt-uuid", userId);
		// assert
		assertThat(response.getStatusCode()).isEqualTo(400);
	}
	
	@Test
	public void nonExistingUuid_statusCode409() throws Exception {
		// arrange
		String uuid = UUID.randomUUID().toString();
		String userId = DcaeRestClient.getDefaultUserId();
		// act
		Report.log(Status.INFO, "Checkin with a non-existing vfcmt-uuid");
		RestResponse response = checkinVfcmt(uuid, userId);
		// assert
		assertThat(response.getStatusCode()).isEqualTo(409);
	}
	
	@Test
	public void nonExistingUser_statusCode403() throws Exception {
		// arrange
		Vfcmt vfcmt = arrange.getCheckedoutVfcmt();
		// act
		Report.log(Status.INFO, "Checkin with a non-existing user");
		RestResponse response = checkinVfcmt(vfcmt.getUuid(), "anonymus");
		// assert
		assertThat(response.getStatusCode()).isEqualTo(403);
	}
	
	@Test
	public void notLastUser_statusCode403() throws Exception {
		// arrange
		String user1 = DcaeRestClient.getDefaultUserId();
		String user2 = DcaeRestClient.getDesigner2UserId();
		Vfcmt vfcmt = arrange.getCheckedoutVfcmt(user1);
		// act
		Report.log(Status.INFO, "Checkin the vfcmt with different user: " + user2);
		RestResponse response = checkinVfcmt(vfcmt.getUuid(), user2);
		// assert
		assertThat(response.getStatusCode()).isEqualTo(403);
	}
	
	@Test
	public void invalidAssetType_statusCode400() throws Exception {
		// arrange 
		String assetType = "kengero";
		String userId = DcaeRestClient.getDefaultUserId();
		Vfcmt vfcmt = arrange.getCheckedoutVfcmt();
		// act
		Report.log(Status.INFO, "Checkin the vfcmt with invalid asset-type");
		RestResponse response = checkinGeneral(assetType, userId, vfcmt);
		// assert
		assertThat(response.getStatusCode()).isEqualTo(400);
	}
	
	
	/* Private Methods */

	/**
	 * Performs checkin on a general vfcmt, use this in test action
	 * @param assetType
	 * @param userId
	 * @param vfcmt
	 * @return
	 * @throws IOException
	 */
	private RestResponse checkinGeneral(String assetType, String userId, Vfcmt vfcmt) throws IOException {
		RestResponse response = DcaeRestClient.checkinGeneral(assetType, vfcmt.getUuid(), userId);
		Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		return response;
	}
	
	/**
	 * Performs checkin on vfcmt, use this in test action
	 * @param vfcmtUuid
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	private RestResponse checkinVfcmt(String vfcmtUuid, String userId) throws IOException {
		RestResponse response = DcaeRestClient.checkinVfcmt(vfcmtUuid, userId);
		Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		return response;
	}	
	
	
}
