package com.onap.dcae.ci.api.tests.lifeCycle;

import static org.assertj.core.api.Assertions.fail;

import com.onap.dcae.ci.utilities.DcaeEntityClient;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;

public class ArrangeHelper {
	
	private DcaeEntityClient client;
	
	public ArrangeHelper(DcaeEntityClient client) {
		this.client = client;
	}
	
	/**
	 * Tries to arrange checkedin vfcmt.
	 * If unable to do so, fails test with arrange error message
	 * @param userId - lastUpdater of the returned vfcmt
	 * @return checkedin vfcmt
	 */
	public Vfcmt getCheckedinVfcmt(String userId) {
		Vfcmt vfcmt = null;
		try {
			vfcmt = client.createCheckedinVfcmt(userId);
		} catch (Exception err) {
			fail("could not arrange test: ", err);
		}
		return vfcmt;
	}
	
	/**
	 * Tries to arrange checkedin vfcmt.
	 * If unable to do so, fails test with arrange error message
	 * @return checkedin vfcmt
	 */
	public Vfcmt getCheckedinVfcmt() {
		String userId = DcaeRestClient.getDefaultUser().getUserId();
		return getCheckedinVfcmt(userId);
	}
	
	/**
	 * Tries to arrange checkedout vfcmt.
	 * If unable to do so, fails test with arrange error message
	 * @param userId - lastUpdater of the returned vfcmt
	 * @return checkedout vfcmt
	 */
	public Vfcmt getCheckedoutVfcmt(String userId) {
		Vfcmt vfcmt = null;
		try {
			vfcmt = client.createCheckedoutVfcmt(userId);
		} catch (Exception err) {
			fail("could not arrange test: ", err);
		}
		return vfcmt;
	}
	
	/**
	 * Tries to arrange checkedout vfcmt.
	 * If unable to do so, fails test with arrange error message
	 * @return checkedout vfcmt
	 */
	public Vfcmt getCheckedoutVfcmt() {
		String userId = DcaeRestClient.getDefaultUser().getUserId();
		return getCheckedoutVfcmt(userId);
	}
}
