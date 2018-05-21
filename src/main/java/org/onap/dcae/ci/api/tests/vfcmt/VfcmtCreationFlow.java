package org.onap.dcae.ci.api.tests.vfcmt;

import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.report.Report;
import org.testng.annotations.*;
import static org.assertj.core.api.Assertions.*;

import com.aventstack.extentreports.Status;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;

public class VfcmtCreationFlow extends DcaeRestBaseTest {
	
	private Vfcmt[] vfcmtsBeforeAdd;
	private Vfcmt	newVfcmt;
	private Vfcmt[] vfcmtsAfterAdd;
	
	@BeforeClass
	public void executeFlow() throws Exception {
		Report.log(Status.INFO, "@BeforeClass --> executeFlow (getAllVfcmts)");
		try {
			// act
			vfcmtsBeforeAdd = client.getAllVfcmts();
			newVfcmt = client.createVfcmt();
			vfcmtsAfterAdd = client.getAllVfcmts();
		} catch (Exception err) {
			Report.log(Status.ERROR, err);
			err.printStackTrace();
		}
	}
	
	@Test
	public void test_numberOfVfcmtsIncresed() {
		Report.log(Status.INFO, String.format("asserting (after[%d] > before[%d])", vfcmtsAfterAdd.length, vfcmtsBeforeAdd.length));
		// assert
		assertThat(vfcmtsAfterAdd.length).isGreaterThan(vfcmtsBeforeAdd.length);
	}
}
