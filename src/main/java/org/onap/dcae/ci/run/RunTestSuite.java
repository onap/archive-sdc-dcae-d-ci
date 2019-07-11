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

package org.onap.dcae.ci.run;

import org.testng.TestNG;

import java.util.ArrayList;
import java.util.List;

public class RunTestSuite {

	private RunTestSuite(){}

	public static void main(String[] args) {
		TestNG testng = new TestNG();
		List<String> suites = new ArrayList<>();

		String testSuite = System.getProperty("testSuite");
		if (testSuite == null){
			System.out.println("No test suite file was found, please provide test suite.");
			System.exit(1);
		}
		suites.add(testSuite);
		testng.setTestSuites(suites);
		testng.setUseDefaultListeners(true);
		testng.setOutputDirectory("target/");
		testng.run();
	}

}
