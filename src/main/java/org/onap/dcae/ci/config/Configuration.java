package org.onap.dcae.ci.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;

public class Configuration {
	private String apiPath;
	private String dcaeBeHost;
	private String beHost;
	private String feHost;
	private int bePort;
	private int fePort;
	private String dcaeBePort;
	private String url;
	private String remoteTestingMachineIP;
	private int remoteTestingMachinePort;
	private boolean remoteTesting;
	private String browser;
	private String systemUnderDebug;
	private String reportDBhost;
	private int reportDBport;
	private boolean captureTraffic;
	private boolean useBrowserMobProxy;
	private String stopOnClassFailure;
	private String reportFileName;
	private String reportFolder;
	private int numOfAttemptsToRefresh;
	private boolean rerun;
	private String windowsDownloadDirectory;
	private String screenshotFolder;
	private String harFilesFolder;
	private String ruleEditorUrl;

	public Configuration() {
		this.basicInit((String) null);
	}

	public Configuration(String url) {
		this.basicInit(url);
	}

	public static synchronized Configuration loadConfigFile(File configFile) {
		return loadConfigFile(configFile, Configuration.class);
	}

	private void basicInit(String url) {
		this.remoteTesting = false;
		this.captureTraffic = false;
		this.useBrowserMobProxy = false;
		this.reportFolder = "." + File.separator + "ExtentReport" + File.separator;
		this.reportFileName = "UI_Extent_Report.html";
		this.screenshotFolder = this.reportFolder + "screenshots" + File.separator;
		this.harFilesFolder = this.reportFolder + "har_files" + File.separator;
		this.browser = "firefox";
		this.url = url;
		this.numOfAttemptsToRefresh = 2;
	}

	public String getBeHost() {
		return this.beHost;
	}

	public void setBeHost(String beHost) {
		this.beHost = beHost;
	}

	public String getFeHost() {
		return this.feHost;
	}

	public void setFeHost(String feHost) {
		this.feHost = feHost;
	}

	public int getBePort() {
		return this.bePort;
	}

	public void setBePort(int bePort) {
		this.bePort = bePort;
	}

	public int getFePort() {
		return this.fePort;
	}

	public void setFePort(int fePort) {
		this.fePort = fePort;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRemoteTestingMachineIP() {
		return this.remoteTestingMachineIP;
	}

	public void setRemoteTestingMachineIP(String remoteTestingMachineIP) {
		this.remoteTestingMachineIP = remoteTestingMachineIP;
	}

	public int getRemoteTestingMachinePort() {
		return this.remoteTestingMachinePort;
	}

	public void setRemoteTestingMachinePort(int remoteTestingMachinePort) {
		this.remoteTestingMachinePort = remoteTestingMachinePort;
	}

	public boolean isRemoteTesting() {
		return this.remoteTesting;
	}

	public void setRemoteTesting(boolean remoteTesting) {
		this.remoteTesting = remoteTesting;
	}

	public String getBrowser() {
		return this.browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getSystemUnderDebug() {
		return this.systemUnderDebug;
	}

	public void setSystemUnderDebug(String systemUnderDebug) {
		this.systemUnderDebug = systemUnderDebug;
	}

	public String getReportDBhost() {
		return this.reportDBhost;
	}

	public void setReportDBhost(String reportDBhost) {
		this.reportDBhost = reportDBhost;
	}

	public int getReportDBport() {
		return this.reportDBport;
	}

	public void setReportDBport(int reportDBport) {
		this.reportDBport = reportDBport;
	}

	public boolean isCaptureTraffic() {
		return this.captureTraffic;
	}

	public void setCaptureTraffic(boolean captureTraffic) {
		this.captureTraffic = captureTraffic;
	}

	public boolean isUseBrowserMobProxy() {
		return this.useBrowserMobProxy;
	}

	public void setUseBrowserMobProxy(boolean useBrowserMobProxy) {
		this.useBrowserMobProxy = useBrowserMobProxy;
	}

	public String getStopOnClassFailure() {
		return this.stopOnClassFailure;
	}

	public void setStopOnClassFailure(String stopOnClassFailure) {
		this.stopOnClassFailure = stopOnClassFailure;
	}

	public String getReportFileName() {
		return this.reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}

	public String getReportFolder() {
		return this.reportFolder;
	}

	public void setReportFolder(String reportFolder) {
		this.reportFolder = reportFolder;
	}

	public int getNumOfAttemptsToRefresh() {
		return this.numOfAttemptsToRefresh;
	}

	public void setNumOfAttemptsToRefresh(int numOfAttemptsToRefresh) {
		this.numOfAttemptsToRefresh = numOfAttemptsToRefresh;
	}

	public boolean isRerun() {
		return this.rerun;
	}

	public void setRerun(boolean rerun) {
		this.rerun = rerun;
	}

	public String getWindowsDownloadDirectory() {
		return this.windowsDownloadDirectory;
	}

	public void setWindowsDownloadDirectory(String windowsDownloadDirectory) {
		this.windowsDownloadDirectory = windowsDownloadDirectory;
	}

	public String getScreenshotFolder() {
		return this.screenshotFolder;
	}

	public void setScreenshotFolder(String screenshotFolder) {
		this.screenshotFolder = screenshotFolder;
	}

	public String getHarFilesFolder() {
		return this.harFilesFolder;
	}

	public void setHarFilesFolder(String harFilesFolder) {
		this.harFilesFolder = harFilesFolder;
	}

	public String getDcaeBeHost() {
		return dcaeBeHost;
	}

	public void setDcaeBeHost(String dcaeBeHost) {
		this.dcaeBeHost = dcaeBeHost;
	}

	public String getDcaeBePort() {
		return dcaeBePort;
	}

	public void setDcaeBePort(String dcaeBePort) {
		this.dcaeBePort = dcaeBePort;
	}

	public String getApiPath() {
		return apiPath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	public String getRuleEditorUrl() {
		return ruleEditorUrl;
	}

	public void setRuleEditorUrl(String ruleEditorUrl) {
		this.ruleEditorUrl = ruleEditorUrl;
	}


	public static synchronized <T> T loadConfigFile(File configFile, Class<T> clazz) {
		InputStream in = null;
		T config = null;

		try {
			String absolutePath = configFile.getAbsolutePath();
			in = Files.newInputStream(Paths.get(absolutePath, new String[0]), new OpenOption[0]);
			Yaml yaml = new Yaml();
			config = yaml.loadAs(in, clazz);
		} catch (IOException var14) {
			var14.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException var13) {
					var13.printStackTrace();
				}
			}

		}

		return config;
	}

}
