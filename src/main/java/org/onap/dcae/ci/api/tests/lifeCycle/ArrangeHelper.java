/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 - 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.dcae.ci.api.tests.lifeCycle;

import static org.assertj.core.api.Assertions.fail;

import org.onap.dcae.ci.utilities.DcaeEntityClient;
import org.onap.dcae.ci.utilities.DcaeRestClient;
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
		String userId = DcaeRestClient.getDefaultUserId();
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
		String userId = DcaeRestClient.getDefaultUserId();
		return getCheckedoutVfcmt(userId);
	}
}
