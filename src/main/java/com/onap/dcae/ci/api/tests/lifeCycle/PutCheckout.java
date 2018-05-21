package com.onap.dcae.ci.api.tests.lifeCycle;

import java.io.IOException;
import java.util.UUID;

import com.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import com.onap.dcae.ci.utilities.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.openecomp.d2.ci.api.ElementFactory;
import org.openecomp.d2.ci.datatypes.UserRoleEnum;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.openecomp.d2.ci.report.ExtentTestActions;
import org.testng.annotations.*;
import static org.assertj.core.api.Assertions.*;

import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import com.aventstack.extentreports.Status;

public class PutCheckout extends DcaeRestBaseTest {
	
	private ArrangeHelper arrange = new ArrangeHelper(client);
	
	/* Positive */
	
	@Test
	public void checkedinVfcmt_success() throws Exception {
		// arrange
		Vfcmt vfcmt = arrange.getCheckedinVfcmt();
		// act
		ExtentTestActions.log(Status.INFO, "Checkout the vfcmt with it's lastUpdater user");
		RestResponse response = checkoutVfcmt(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
		// assert
		Vfcmt vfcmtAfterCheckout = gson.fromJson(response.getResponse(), Vfcmt.class);
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).isEqualTo(200);
			softly.assertThat(vfcmtAfterCheckout.getLifecycleState()).contains("CHECKOUT");
		});
	}
	
	@Test
	public void notLastUser_success() throws Exception {
		// arrange
		String user1 = ElementFactory.getDefaultUser(UserRoleEnum.DESIGNER).getUserId();
		String user2 = ElementFactory.getDefaultUser(UserRoleEnum.DESIGNER2).getUserId();
		Vfcmt vfcmt = arrange.getCheckedinVfcmt(user1);
		// act
		ExtentTestActions.log(Status.INFO, "Checkout the vfcmt with different user: " + user2);
		RestResponse response = checkoutVfcmt(vfcmt.getUuid(), user2);
		// assert
		Vfcmt vfcmtAfterCheckout = gson.fromJson(response.getResponse(), Vfcmt.class);
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).isEqualTo(200);
			softly.assertThat(vfcmtAfterCheckout.getLifecycleState()).contains("CHECKOUT");
		});
	}
	
	
	/* Negative */
	
	@Test
	public void alreadyCheckedoutVfcmt_statusCode403() throws Exception {
		// arrange
		Vfcmt vfcmt = arrange.getCheckedoutVfcmt();
		// act
		ExtentTestActions.log(Status.INFO, "Checkout the vfcmt with it's lastUpdater user");
		RestResponse response = checkoutVfcmt(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
		// assert
		assertThat(response.getStatusCode()).isEqualTo(403);
	}
	
	@Test
	public void invalidVfcmtUuid_statusCode400() throws Exception {
		// arrange
		String userId = DcaeRestClient.getDefaultUser().getUserId();
		// act
		ExtentTestActions.log(Status.INFO, "Checkout with an invalid-uuid as vfcmt-uuid");
		RestResponse response = checkoutVfcmt("invalid-vfcmt-uuid", userId);
		// assert
		assertThat(response.getStatusCode()).isEqualTo(400);
	}
	
	@Test
	public void nonExistingUuid_statusCode409() throws Exception {
		// arrange
		String uuid = UUID.randomUUID().toString();
		String userId = DcaeRestClient.getDefaultUser().getUserId();
		// act
		ExtentTestActions.log(Status.INFO, "Checkin with a non-existing vfcmt-uuid");
		RestResponse response = checkoutVfcmt(uuid, userId);
		// assert
		assertThat(response.getStatusCode()).isEqualTo(409);
	}

	@Test
	public void nonExistingUser_statusCode403() throws Exception {
		// arrange
		Vfcmt vfcmt = arrange.getCheckedinVfcmt();
		// act
		ExtentTestActions.log(Status.INFO, "Checkin with a non-existing user");
		RestResponse response = checkoutVfcmt(vfcmt.getUuid(), "anonymus");
		// assert
		assertThat(response.getStatusCode()).isEqualTo(403);
	}
	
	@Test
	public void invalidAssetType_statusCode400() throws Exception {
		// arrange 
		String assetType = "kengero";
		String userId = DcaeRestClient.getDefaultUser().getUserId();
		Vfcmt vfcmt = arrange.getCheckedoutVfcmt();
		// act
		ExtentTestActions.log(Status.INFO, "Checkout the vfcmt with invalid asset-type");
		RestResponse response = checkoutGeneral(assetType, userId, vfcmt);
		// assert
		assertThat(response.getStatusCode()).isEqualTo(400);
	}
	
	
	/* Private Methods */
	
	/**
	 * Performs checkout on a general vfcmt/service and logs the response
	 * @param assetType
	 * @param userId
	 * @param vfcmt
	 * @return
	 * @throws IOException
	 */
	private RestResponse checkoutGeneral(String assetType, String userId, Vfcmt vfcmt) throws IOException {
		RestResponse response = DcaeRestClient.checkinGeneral(assetType, vfcmt.getUuid(), userId);
		ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		return response;
	}
	
	/**
	 *  Performs checkout on vfcmt and logs the response
	 * @param vfcmtUuid
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	private RestResponse checkoutVfcmt(String vfcmtUuid, String userId) throws IOException {
		RestResponse response = DcaeRestClient.checkoutVfcmt(vfcmtUuid, userId);
		ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		return response;
	}
}
