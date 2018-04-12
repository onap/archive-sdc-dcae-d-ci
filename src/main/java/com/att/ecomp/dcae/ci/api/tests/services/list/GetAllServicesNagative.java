package com.att.ecomp.dcae.ci.api.tests.services.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

import java.io.IOException;

import org.openecomp.d2.ci.api.ElementFactory;
import org.openecomp.d2.ci.datatypes.UserRoleEnum;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.openecomp.d2.ci.report.ExtentTestActions;
import org.testng.annotations.Test;

import com.att.ecomp.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import com.att.ecomp.dcae.ci.utilities.DcaeRestClient;
import com.att.ecomp.dcae.ci.utilities.StringUtils;
import com.aventstack.extentreports.Status;

public class GetAllServicesNagative extends DcaeRestBaseTest {

	@Test
	public void userNotLastUpdater_statusCode403() throws Exception{
		// arrange
		String user1 = ElementFactory.getDefaultUser(UserRoleEnum.DESIGNER).getUserId();
		String user2 = ElementFactory.getDefaultUser(UserRoleEnum.DESIGNER2).getUserId();
		Vfcmt vfcmt = null;
		try {
			vfcmt = client.createCheckedoutVfcmt(user1);
		} catch (Exception err) {
			fail("Unable to arrange test", err);
		}
		// act
		ExtentTestActions.log(Status.INFO, "GetServices for user that is not the vfcmt last-updater");
		RestResponse response = getServices(vfcmt.getUuid(), user2);
		// assert
		assertThat(response.getStatusCode()).as("response status").isEqualTo(403);
	}
	
	
	/* Private Methods */

	private RestResponse getServices(String uuid, String user2) throws IOException {
		RestResponse response =  DcaeRestClient.getServices(uuid, user2);
		ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		return response;
	}
}
