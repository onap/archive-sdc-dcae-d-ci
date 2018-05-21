package org.onap.dcae.ci.api.tests.services.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

import java.io.IOException;

import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.report.Report;
import org.onap.dcae.ci.utilities.StringUtils;
import org.testng.annotations.Test;

import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;

import com.aventstack.extentreports.Status;

public class GetAllServicesNagative extends DcaeRestBaseTest {

	@Test
	public void userNotLastUpdater_statusCode403() throws Exception{
		// arrange
		String user1 = DcaeRestClient.getDefaultUserId();
		String user2 = DcaeRestClient.getDesigner2UserId();
		Vfcmt vfcmt = null;
		try {
			vfcmt = client.createCheckedoutVfcmt(user1);
		} catch (Exception err) {
			fail("Unable to arrange test", err);
		}
		// act
		Report.log(Status.INFO, "GetServices for user that is not the vfcmt last-updater");
		RestResponse response = getServices(vfcmt.getUuid(), user2);
		// assert
		assertThat(response.getStatusCode()).as("response status").isEqualTo(403);
	}
	
	
	/* Private Methods */

	private RestResponse getServices(String uuid, String user2) throws IOException {
		RestResponse response =  DcaeRestClient.getServices(uuid, user2);
		Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		return response;
	}
}
