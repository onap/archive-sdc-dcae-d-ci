package com.onap.dcae.ci.ui.tests.verificator;

import org.openecomp.d2.ci.report.ExtentTestActions;
import org.openecomp.d2.ci.utilities.GeneralUIUtils;

import com.aventstack.extentreports.Status;

import static org.testng.Assert.assertTrue;

public class ServiceVerificator 
{
	public static void verifyBluePrintArtifactExist(String name)
	{
		ExtentTestActions.log(Status.INFO,"Verifying Blue Print artifact exist");
		String artifactName = "Foi" + "." + name.replaceAll("\\s+","").replaceAll("-", "") + ".event_proc_bp.yaml";
		GeneralUIUtils.waitForElementInVisibilityByTestId("artifactName-blueprint-foi");
		String actualBluePrintFileName = GeneralUIUtils.getWebElementByTestID("artifactName-blueprint-foi").getText();		
		assertTrue(artifactName.equalsIgnoreCase(actualBluePrintFileName));
		ExtentTestActions.log(Status.INFO, String.format("Actual Blue print file name is: %s.", actualBluePrintFileName));
		GeneralUIUtils.ultimateWait();
	}
}
