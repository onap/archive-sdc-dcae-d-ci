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

package org.onap.dcae.ci.report;

import java.util.HashMap;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {

	private static HashMap<Long, ExtentTest> extentTestMap = new HashMap<>();
	private static ExtentReports extent = ExtentManager.getReporter();

	public static synchronized ExtentTest getTest() {
		ExtentTest extentTest = extentTestMap.get(Thread.currentThread().getId());
		if (extentTest == null){
			extentTest = startTest("Automated setup/cleanup");
		}
		return extentTest;
	}

	private ExtentTestManager(){}

	public static synchronized void endTest() {
		extent.flush();
	}

	public static synchronized ExtentTest startTest(String testName) {
		return startTest(testName, "");
	}

	private static synchronized ExtentTest startTest(String testName, String desc) {
		ExtentTest test = extent.createTest(testName, desc);
		extentTestMap.put(Thread.currentThread().getId(), test);
		return test;
	}

	public static synchronized <T> void assignCategory(Class<T> clazz){
		String[] parts = clazz.getName().split("\\.");
		String lastOne1 = parts[parts.length-1];
		String lastOne2 = parts[parts.length-2];
		getTest().assignCategory(lastOne2 + "-" + lastOne1);
	}
}

