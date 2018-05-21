package org.onap.dcae.ci.api.tests.lifeCycle;


import com.aventstack.extentreports.Status;
import org.assertj.core.api.SoftAssertions;

import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.report.Report;
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
		Report.log(Status.INFO, "Calling certify vfcmt with lastUpdater as user (designer role)");
		RestResponse response = DcaeRestClient.certifyVfcmt(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
		// assert
		Vfcmt vfcmtAfterCheckin = gson.fromJson(response.getResponse(), Vfcmt.class);
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).isEqualTo(200);
			softly.assertThat(vfcmtAfterCheckin.getLifecycleState()).isEqualTo("CERTIFIED");
		});
	}
}
