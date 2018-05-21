package com.onap.dcae.ci.ui.rule_editor;

import java.io.File;
import java.util.Map;

import com.onap.dcae.ci.ui.rule_editor.components.LoginPage;
import org.openecomp.d2.ci.datatypes.Configuration;
import org.openecomp.d2.ci.datatypes.UserCredentials;
import org.openecomp.d2.ci.datatypes.UserRoleEnum;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.openecomp.d2.ci.setup.DriverFactory;
import org.openecomp.d2.ci.utilities.FileHandling;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.testng.annotations.BeforeMethod;

import com.onap.dcae.ci.utilities.ConfigurationReader;
import com.onap.dcae.ci.utilities.DcaeEntityClient;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import com.onap.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class BaseTest extends DriverFactory {
	
	private String baseUrl = ConfigurationReader.getConfiguration().getRuleEditorUrl();
	private final long defaultTimeout = 90;
	protected WebDriverWait defaultTimeoutWait;
	
	@BeforeMethod
	protected void setupTest(){
		try {
			/**
			 * this is surrounded by a try block because of a bug in TestNg that causes 'afterMethod' to not be called 
			 * when an exception is thrown  in the 'beforeMethod' - this prevents the ExtentReport from producing a report
			 */
			
			Report.log(Status.INFO, "Setting up...");
			LinkedMultiValueMap<String, String> params = arrangeRequiredParams();
			String url = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParams(params).build().toUriString();
			Report.log(Status.INFO, "Deleting cookies...");
			WebDriver driver = getDriver();
			driver.manage().deleteAllCookies();
			Report.log(Status.INFO, "Navigating to URL: %s", url);
			driver.navigate().to(url); 
			Report.log(Status.INFO, "Checking if diverted to login page...");
			defaultTimeoutWait = new WebDriverWait(driver, defaultTimeout);
			if (LoginPage.isCurrentPage(driver)) {
				Report.log(Status.INFO, "Preforming login...");
				WebElement root = driver.findElement(By.cssSelector("body"));
				LoginPage loginPage = new LoginPage(defaultTimeoutWait, root);
				loginPage.login(getUserCredentials());
				Report.log(Status.INFO, "Refreshing...");
				driver.navigate().refresh(); // refresh fixes missing cookies after login
				Report.log(Status.INFO, "Logged-in successfully.");
			} else {
				Report.log(Status.INFO, "Not in login page.");
			}
			Report.log(Status.INFO, "Setup done.");
		} catch (Exception err) {
			Report.fatal("Error during setup", err);
		}
	}

	protected LinkedMultiValueMap<String, String> arrangeRequiredParams() throws Exception {
		String userId = getUserCredentials().getUserId();
		DcaeEntityClient dcaeEntityClient = new DcaeEntityClient();
		Report.log(Status.INFO, "Creating vfcmt...");
		Vfcmt vfcmt = dcaeEntityClient.createVfcmt(userId);
//		Report.log(Status.INFO, "Generating cdump of snmp (simulated drag of snmp component)...");
		String cdump = getFakeCdump();
		Report.log(Status.INFO, "Saving composition...");
		RestResponse saveResponse = DcaeRestClient.saveComposition(vfcmt.getUuid(), userId, cdump);
		if (saveResponse.getStatusCode() != 200) {
			throw new Exception("Save composition failed.\nDetails: " + saveResponse.toString());
		}
		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("vfcmtUuid", vfcmt.getUuid());
		params.add("nodeName", "map");
		params.add("nodeId", "map");
		params.add("fieldName", "fake");
		params.add("userId", userId);
		params.add("flowType", "FOI");
		return params;
	}
	
	protected LinkedMultiValueMap<String, String> arrangeFakeParams() {
		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("vfcmtUuid", "aece8152-a97f-4ec4-9a42-941d8e586f97");
		params.add("nodeName", "map");
		params.add("nodeId", "map");
		params.add("fieldName", "fake");
		params.add("userId", "fakeo");
		params.add("flowType", "FOI");
		return params;
	}
	
	private static String getFakeCdump() {
		return "{\"nid\":\"map\"}";
	}
	
	protected UserRoleEnum getUserRole()
	{
		return UserRoleEnum.DESIGNER;
	}

	@Override
	protected Configuration getEnvConfiguration() {
		return ConfigurationReader.getConfiguration();
	}

	@Override
	protected UserCredentials getUserCredentials() {
		try {
			String userRole = getUserRole().name().toLowerCase();
			String credentialsFile = System.getProperty("credentials.file");
			if (credentialsFile == null)
				 credentialsFile = ConfigurationReader.confPath() + "credentials.yaml";
			File file = new File(credentialsFile);
			if (!file.exists()){
				throw new Exception("Please provide a credentails file");
			}
			Map<?, ?> credentialsParsedFile = FileHandling.parseYamlFile(credentialsFile); 
			Map<String, String> credentialsMap = (Map<String, String>) credentialsParsedFile.get(userRole);
			String userId = (String) credentialsMap.get("username");
			String password = (String) credentialsMap.get("password");
			String firstname = (String) credentialsMap.get("firstname");
			String lastname = (String) credentialsMap.get("lastname");
			
			return new UserCredentials(userId, password, firstname, lastname, null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
