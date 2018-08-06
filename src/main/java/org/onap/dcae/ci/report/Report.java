package org.onap.dcae.ci.report;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.onap.dcae.ci.utilities.SetupReport;
import org.testng.ITestResult;

public class Report {

	private static String testName;
	private static Throwable throwable;
	private static int status;

	public static void log(Status status, String format, Object... args) {
		log(status, String.format(format, args));
	}
	
	public static void logDebug(String message, Object obj) {
		log(Status.DEBUG, "%s %s", message, wrapWithTextareaRO(obj.toString()));
	}
	
	public static void fatal(String message, Exception err) {
		log(Status.FATAL, "%s %s", message, wrapWithTextareaRO(err.toString()));
	}


	public static void log(Status logStatus, Markup mark){
		ExtentTest test = ExtentTestManager.getTest();
		test.log(logStatus, mark);
	}

	public static void log(Status logStatus, String message){
		ExtentTest test = ExtentTestManager.getTest();
		test.log(logStatus, message);
		System.out.println(message);
	}

	public static void log(Status logStatus, String message, String duration){
		log(logStatus, message + addDurationTag(duration));
	}

	public static void log(Status logStatus, Throwable throwable){
		ExtentTest test = ExtentTestManager.getTest();
		test.log(logStatus, throwable);
	}

	public static void addTag(Status logStatus, String message){
		Markup m = null;
		switch(logStatus){
		case PASS:
			m = MarkupHelper.createLabel(message, ExtentColor.GREEN);
			break;
		case FAIL:
			m = MarkupHelper.createLabel(message, ExtentColor.RED);
			break;
		case SKIP:
			m = MarkupHelper.createLabel(message, ExtentColor.BLUE);
			break;
		case FATAL:
			m = MarkupHelper.createLabel(message, ExtentColor.BROWN);
			break;
		default:
			break;
		}

		if (m != null){
			log(logStatus, m);
		}
	}


	private static String addDurationTag(String duration){
		return "<td width=\"80px\">" + duration + "</td>";
	}

	public static void report(ITestResult result) {

		testName = result.getName();
		throwable = result.getThrowable();
		status = result.getStatus();
		String suiteName = ExtentManager.getSuiteName();

		switch (status) {
		case ITestResult.SUCCESS:
			logSuccessAfterTest();
			break;

		case ITestResult.FAILURE:
			if (suiteName.equals(SetupReport.TESTNG_FAILED_XML_NAME)) {
				logFatalAfterTest();
			} else {
				logFailAfterTest();
			}
			break;

		case ITestResult.SKIP:
			logSkipAfterTest();
			break;

		default:
			break;
		}

	}
	
	/* Private members */
	
	private static String wrapWithTextareaRO(String str) {
		return String.format("<textarea readonly style=\"height:100px\">%s</textarea>", str);
	}

	private static void logSuccessAfterTest() {
		final Status logStatus = Status.PASS;
		addTag(logStatus, "Success");
	}

	private static void logFailAfterTest() {
		addTag(Status.FAIL, "Failure");
		try {
			log(Status.ERROR, "ERROR - The following exepction occured : ");
			log(Status.ERROR, throwable);
		} catch (Exception e) {
			log(Status.ERROR, "ERROR - The following exepction occured : " + e.getMessage());
		}
	}

	private static void logSkipAfterTest() {
		final Status logStatus = Status.SKIP;
		addTag(logStatus, "Skipped");
		try {
			log(logStatus, "SKIP - The following exepction occured : ");
			log(logStatus, throwable);
		} catch (Exception e) {
			log(logStatus, "SKIP - The following exepction occured : " + e.getMessage());
		}
	}

	private static void logFatalAfterTest() {
		final Status logStatus = Status.FATAL;
		addTag(logStatus, "Fatal");
		try {
			log(logStatus, "FATAL - The following exepction occured : ");
			log(logStatus, throwable);
		} catch (Exception e) {
			log(logStatus, "FATAL - The following exepction occured : " + e.getMessage());
		}
	}


}
