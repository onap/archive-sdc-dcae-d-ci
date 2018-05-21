package com.onap.dcae.ci.utilities;

import org.openecomp.d2.ci.report.ExtentTestActions;

import com.aventstack.extentreports.Status;

public class Report {
	
	public static void log(Status status, String format, Object... args) {
		ExtentTestActions.log(status, String.format(format, args));
	}
	
	public static void logDebug(String message, Object obj) {
		log(Status.DEBUG, "%s %s", message, wrapWithTextareaRO(obj.toString()));
	}
	
	public static void fatal(String message, Exception err) {
		log(Status.FATAL, "%s %s", message, wrapWithTextareaRO(err.toString()));
	}
	
	/* Private members */
	
	private static String wrapWithTextareaRO(String str) {
		return String.format("<textarea readonly style=\"height:100px\">%s</textarea>", str);
	}
}
