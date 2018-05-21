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
