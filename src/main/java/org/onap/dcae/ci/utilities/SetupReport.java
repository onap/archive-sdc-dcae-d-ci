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

package org.onap.dcae.ci.utilities;


import org.onap.dcae.ci.config.Configuration;
import org.onap.dcae.ci.report.ExtentManager;
import org.onap.dcae.ci.report.ExtentTestManager;
import org.onap.dcae.ci.report.Report;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

public abstract class SetupReport {
	public static final String TESTNG_FAILED_XML_NAME = "testng-failed.xml";
	private static Method myMethod;
	private static Configuration configuration;

	public SetupReport() {
		try {
			configuration = this.getEnvConfiguration();
		} catch (Exception var2) {
			var2.printStackTrace();
		}

	}

	protected abstract Configuration getEnvConfiguration();

	@BeforeSuite(
			alwaysRun = true
	)
	public void beforeSuiteStarts(ITestContext context) {
		this.initReport(context);
	}

	@BeforeMethod(
			alwaysRun = true
	)
	public void beforeTestStarts(Method method, Object[] testArgs) {
		myMethod = method;
		this.initTestReporting(testArgs);
	}

	@AfterMethod(
			alwaysRun = true
	)
	public void afterTestEnds(ITestResult result) {
		this.reportTestResult(result);
		this.closeTestReporting();
		this.closeReport();
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	private void initReport(ITestContext context) {
		try {
			ExtentManager.initReporter(this.getEnvConfiguration(), context);
		} catch (Exception var3) {
			var3.printStackTrace();
		}

	}

	private void initTestReporting(Object[] testArgs) {
		if(this.isDataProviderEmpty(testArgs)) {
			this.startTest();
		} else {
			this.startTest(this.getDataProviderValues(testArgs));
		}

	}

	private boolean isDataProviderEmpty(Object[] testArgs) {
		return testArgs.length == 0;
	}

	private String getDataProviderValues(Object[] testArgs) {
		StringBuilder sb = new StringBuilder();
		Object[] var3 = testArgs;
		int var4 = testArgs.length;

		for(int var5 = 0; var5 < var4; ++var5) {
			Object arg = var3[var5];
			sb.append(arg);
			sb.append(" ");
		}

		return sb.toString().trim();
	}

	private void reportTestResult(ITestResult result) {
		Report.report(result);
	}

	private void closeTestReporting() {
		ExtentTestManager.endTest();
	}

	private void closeReport() {
		ExtentManager.closeReporter();
	}

	private void startTest(String fromDataProvider) {
		String suiteName = ExtentManager.getSuiteName();
		String methodName = this.getFullMethodName(fromDataProvider);
		if(suiteName != null) {
			if(suiteName.equals("testng-failed.xml")) {
				ExtentTestManager.startTest("<html><font color=\"red\">ReRun - </font></html>" + methodName);
			} else {
				ExtentTestManager.startTest(methodName);
			}

			ExtentTestManager.assignCategory(this.getClass());
		}

	}

	private void startTest() {
		this.startTest("");
	}

	private String getFullMethodName(String fromDataProvider) {
		return fromDataProvider.equals("")?myMethod.getName():myMethod.getName() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + fromDataProvider;
	}
}

