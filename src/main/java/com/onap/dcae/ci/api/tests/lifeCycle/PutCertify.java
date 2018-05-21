package com.onap.dcae.ci.api.tests.lifeCycle;

import com.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import com.aventstack.extentreports.Status;
import org.assertj.core.api.SoftAssertions;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.openecomp.d2.ci.report.ExtentTestActions;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import org.testng.annotations.Test;

public class PutCertify extends DcaeRestBaseTest {

	private ArrangeHelper arrange = new ArrangeHelper(client);

	/* Positive */

	@Test
	public void certifyVfcmt_success() throws Exception {
		// arrange
		Vfcmt vfcmt = arrange.getCheckedinVfcmt();
		// act
		ExtentTestActions.log(Status.INFO, "Calling certify vfcmt with lastUpdater as user (designer role)");
		RestResponse response = DcaeRestClient.certifyVfcmt(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
		// assert
		Vfcmt vfcmtAfterCheckin = gson.fromJson(response.getResponse(), Vfcmt.class);
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).isEqualTo(200);
			softly.assertThat(vfcmtAfterCheckin.getLifecycleState()).isEqualTo("CERTIFIED");
		});
	}


	/* Negative */

	@Test
	public void notReadyForCertification_statusCode500() throws Exception {
		// arrange
		Vfcmt vfcmt = arrange.getCheckedoutVfcmt();
		// act
		ExtentTestActions.log(Status.INFO, "Calling certify on a checked out vfcmt");
		RestResponse response = DcaeRestClient.certifyVfcmt(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
		// assert
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).isEqualTo(500);
		});
	}

}
