package org.onap.dcae.ci.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentXReporter;
import com.aventstack.extentreports.reporter.configuration.Protocol;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.onap.dcae.ci.config.Configuration;
import org.onap.dcae.ci.utilities.SetupReport;
import org.testng.ITestContext;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class ExtentManager {

	private static final String VERSIONS_INFO_FILE_NAME = "versions.info";
	private static ExtentReports extent;
	private static ExtentHtmlReporter htmlReporter;
	private static ExtentXReporter extentxReporter;
	private static final String icon = "$(document).ready(function() {" + "\n"
			+ "$('.brand-logo').html('').prepend(\"<span><img src='data:image/png;base64,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"
			+ "\n" + "})";
	private static String suiteName;

	public synchronized static ExtentReports setReporter(String filepath, String htmlFile, String dbIp, int dbPort, Boolean isAppend) {
		if (extent == null) {
			extentxReporter = new ExtentXReporter(dbIp, dbPort);
			extent = new ExtentReports();
			initAndSetExtentHtmlReporter(filepath, htmlFile, isAppend);

			if (extentxReporter.config().getReportObjectId() != null) {
				setExtentXReporter(isAppend);
			} else {
				extentxReporter.stop();
			}
		}
		return extent;
	}


	public synchronized static void setExtentXReporter(Boolean isAppend) {
		extentxReporter.setAppendExisting(isAppend);
		extent.attachReporter(extentxReporter);
	}

	public synchronized static void initAndSetExtentHtmlReporter(String filePath, String htmlFile, Boolean isAppend) {
		setHtmlReportConfiguration(filePath, htmlFile);
		htmlReporter.setAppendExisting(isAppend);
		extent.attachReporter(htmlReporter);
	}

	public synchronized static ExtentReports getReporter() {
		return extent;
	}

	public static void initReporter(Configuration config, ITestContext context) {
		setSuiteName(context);
		String envData = config.getUrl();
		String dbIp = config.getReportDBhost();
		int dbPort = config.getReportDBport();
		String filepath = config.getReportFolder();
		String htmlFile = config.getReportFileName();


		if (suiteName.equals(SetupReport.TESTNG_FAILED_XML_NAME)) {
			if (config.isUseBrowserMobProxy()) {
				setTrafficCaptue(config);
			}

			setReporter(filepath, htmlFile, dbIp, dbPort, true);
			suiteName = getKeyByValueFromPropertyFormatFile(filepath + VERSIONS_INFO_FILE_NAME, "suiteName");
		} else {
			deleteDirectory(config.getReportFolder());
			createDirectory(filepath);
			setReporter(filepath, htmlFile, dbIp, dbPort, false);
			createVersionsInfoFile(filepath, VERSIONS_INFO_FILE_NAME, envData, suiteName);
		}
		reporterDataDefinition(envData, suiteName);
	}

	private static void createVersionsInfoFile(String path, String file, String envData, String suiteName) {
		File myFoo = new File(path + file);
		FileOutputStream fooStream;
		try {
			fooStream = new FileOutputStream(myFoo, false);
			String versions = ("env=\"" + envData + "\"\n" + "suiteName=\"" + suiteName + "\"\n");
			byte[] myBytes = versions.getBytes();
			fooStream.write(myBytes);
			fooStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void reporterDataDefinition(String envData, String suiteNameFromVersionInfoFile) {
		extent.setSystemInfo("Host Name Address", getExecutionHostAddress());
		extent.setSystemInfo("ExecutedOn", envData);
		extent.setSystemInfo("SuiteName", suiteNameFromVersionInfoFile);
	}

	private static void setSuiteName(ITestContext context) {
		String suitePath = context.getSuite().getXmlSuite().getFileName();
		if (suitePath != null) {
			File file = new File(suitePath);
			suiteName = file.getName();
		}
	}

	public static String getSuiteName() {
		return suiteName;
	}

	public synchronized static void setHtmlReportConfiguration(String filePath, String htmlFile) {
		htmlReporter = new ExtentHtmlReporter(filePath + htmlFile);
		htmlReporter.config().setTheme(Theme.STANDARD);
		htmlReporter.config().setEncoding("UTF-8");
		htmlReporter.config().setProtocol(Protocol.HTTPS);
		htmlReporter.config().setDocumentTitle("Automation Report");
		htmlReporter.config().setChartVisibilityOnOpen(true);
		htmlReporter.config().setReportName("Automation Report");
		htmlReporter.config().setChartVisibilityOnOpen(false);
		htmlReporter.config().setJS(icon);
	}

	public static void closeReporter() {
		extent.flush();
	}

	public static void setTrafficCaptue(Configuration config) {
		config.setCaptureTraffic(true);
	}


	private static String getKeyByValueFromPropertyFormatFile(String fullPath, String key) {
		Properties prop = new Properties();
		InputStream input = null;
		String value = null;
		try {
			input = new FileInputStream(fullPath);
			prop.load(input);
			value = (prop.getProperty(key));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return value.replaceAll("\"","");
	}

	private static void deleteDirectory(String directoryPath) {
		File dir = new File(directoryPath);
		try {
			FileUtils.deleteDirectory(dir);
		} catch (IOException e) {
			System.out.println("Failed to delete " + dir);
		}
	}

	private static void createDirectory(String directoryPath) {
		File directory = new File(String.valueOf(directoryPath));
		if (! directory.exists()){
			directory.mkdir();
		}
	}

	private static String getExecutionHostAddress() {

		String computerName = null;
		try {
			computerName = InetAddress.getLocalHost().getHostAddress().replaceAll("\\.", "&middot;");
			System.out.println(computerName);
			if (computerName.indexOf(".") > -1)
				computerName = computerName.substring(0,
						computerName.indexOf(".")).toUpperCase();
		} catch (UnknownHostException e) {
			System.out.println("Uknown hostAddress");
		}
		return computerName != null ? computerName : "Uknown hostAddress";
	}
}
