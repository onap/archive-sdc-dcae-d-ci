package com.att.ecomp.dcae.ci.ui.setup;

import java.io.File;
import java.util.Map;

import org.openecomp.d2.ci.datatypes.Configuration;
import org.openecomp.d2.ci.datatypes.UserCredentials;
import org.openecomp.d2.ci.datatypes.UserRoleEnum;
import org.openecomp.d2.ci.setup.SetupCDTest;
import org.openecomp.d2.ci.utilities.FileHandling;
import org.openecomp.d2.ci.utilities.GeneralUIUtils;
import org.openecomp.d2.ci.utilities.LoginUtils;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;

import com.att.ecomp.dcae.ci.utilities.ConfigurationReader;
import com.att.ecomp.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public abstract class ConfigTest extends SetupCDTest {

	protected abstract UserRoleEnum getUserRole();
	
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
			
			UserCredentials userCredentials = new UserCredentials(userId, password, firstname, lastname, null);
			Report.log(Status.INFO, "Using user Credentials="+userCredentials);
			return userCredentials;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void loginToLocalSimulator(UserCredentials user) {
		LoginUtils.loginToLocalWebsealSimulator(user);
	}
	
	@BeforeMethod
	public void beforeTest(){
		try{
			if(getEnvConfiguration().getUrl().contains("localhost"))
			{
				GeneralUIUtils.ultimateWait();
			}
			else
			{
				WebElement close = GeneralUIUtils.getWebElementByClassName("sdc-welcome-close");
				close.click();
				GeneralUIUtils.ultimateWait();
			}
		}
		catch(Exception e){
			Report.logDebug("Exception has occured in beforeTest - unable to proceed to test ", e);
			e.printStackTrace();
		}
	}


}
