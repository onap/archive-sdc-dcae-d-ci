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
	private String dcaeBePort;
	private String sdcBeHost;
	private int sdcBePort;
	private String url;
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
	private String toscaLabUrl;

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
		this.captureTraffic = false;
		this.useBrowserMobProxy = false;
		this.reportFolder = "." + File.separator + "ExtentReport" + File.separator;
		this.reportFileName = "UI_Extent_Report.html";
		this.screenshotFolder = this.reportFolder + "screenshots" + File.separator;
		this.harFilesFolder = this.reportFolder + "har_files" + File.separator;
		this.url = url;
		this.numOfAttemptsToRefresh = 2;
	}

	public String getSdcBeHost() {
		return this.sdcBeHost;
	}

	public void setSdcBeHost(String sdcBeHost) {
		this.sdcBeHost = sdcBeHost;
	}

	public int getSdcBePort() {
		return this.sdcBePort;
	}

	public void setSdcBePort(int sdcBePort) {
		this.sdcBePort = sdcBePort;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getToscaLabUrl() {
		return toscaLabUrl;
	}

	public void setToscaLabUrl(String toscaLabUrl) {
		this.toscaLabUrl = toscaLabUrl;
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
