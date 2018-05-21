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

	public static synchronized void endTest() {
		extent.flush();
	}

	public static synchronized ExtentTest startTest(String testName) {
		return startTest(testName, "");
	}

	public static synchronized ExtentTest startTest(String testName, String desc) {
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

