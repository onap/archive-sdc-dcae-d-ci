package com.onap.dcae.ci.ui.pages;

import java.util.UUID;

import org.openecomp.d2.ci.report.ExtentTestActions;
import org.openecomp.d2.ci.utilities.GeneralUIUtils;
import org.openqa.selenium.WebElement;
import com.aventstack.extentreports.Status;

public class DCAEGeneralPage {
	
	public static String addAssetName(String name)
	{
		WebElement nameTextbox = GeneralUIUtils.getWebElementByTestID("name");
		String assetName = name + UUID.randomUUID();
		nameTextbox.clear();
		nameTextbox.sendKeys(assetName);		
		GeneralUIUtils.ultimateWait();
		ExtentTestActions.log(Status.INFO,String.format("Add asset name: %s.", assetName));
		return assetName;
	} 
	
	public static void addAssetDescription(String description)
	{
		ExtentTestActions.log(Status.INFO,"Add asset description");
		WebElement descriptionTextbox = GeneralUIUtils.getWebElementByTestID("description");
		descriptionTextbox.clear();
		descriptionTextbox.sendKeys(description);
		GeneralUIUtils.ultimateWait();
	}
	
	
	public static void clickSaveAsset() throws Exception
	{
		ExtentTestActions.log(Status.INFO,"Click Save asset");
		GeneralUIUtils.waitForElementInVisibilityByTestId("Save-General");
		GeneralUIUtils.clickOnElementByTestId("Save-General");
		GeneralUIUtils.ultimateWait();
	}
	
}
