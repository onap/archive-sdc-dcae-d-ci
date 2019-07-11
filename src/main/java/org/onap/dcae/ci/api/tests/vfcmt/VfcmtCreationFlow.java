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
