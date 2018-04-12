package com.att.ecomp.dcae.ci.ui.pages;

import org.openecomp.d2.ci.report.ExtentTestActions;
import org.openecomp.d2.ci.setup.SetupCDTest;
import org.openecomp.d2.ci.utilities.GeneralUIUtils;
import org.openqa.selenium.WebElement;

import com.att.ecomp.dcae.ci.utilities.ConfigurationReader;
import com.aventstack.extentreports.Status;

public class SDCHomePage 
{
	
	public static void clickOnAddVf()
	{
		ExtentTestActions.log(Status.INFO,"Clicking on Add VF button");
		GeneralUIUtils.waitForElementInVisibilityByTestId("AddButtonsArea");
		GeneralUIUtils.hoverOnAreaByTestId("AddButtonsArea");		
		GeneralUIUtils.waitForElementInVisibilityByTestId("createResourceButton");
		GeneralUIUtils.clickOnElementByTestId("createResourceButton");
		GeneralUIUtils.ultimateWait();
	}
	
	public static void clickOnAddsService()
	{
		ExtentTestActions.log(Status.INFO,"Clicking on Add Service button");
		GeneralUIUtils.waitForElementInVisibilityByTestId("AddButtonsArea");
		GeneralUIUtils.hoverOnAreaByTestId("AddButtonsArea");		
		GeneralUIUtils.waitForElementInVisibilityByTestId("createServiceButton");
		GeneralUIUtils.clickOnElementByTestId("createServiceButton");
		GeneralUIUtils.ultimateWait();
	}
	
	public static void searchForElement(String name)
	{
		ExtentTestActions.log(Status.INFO,"Search for element in Homepage");
		GeneralUIUtils.waitForElementInVisibilityByTestId("main-menu-input-search");
		WebElement searchTextbox = GeneralUIUtils.getWebElementByTestID("main-menu-input-search");
		searchTextbox.clear();		
		searchTextbox.sendKeys(name);
		GeneralUIUtils.ultimateWait();		
	}
	
	public static void clickOnElement(String name)
	{
		ExtentTestActions.log(Status.INFO,"Clicking on Add VF button");
		GeneralUIUtils.waitForElementInVisibilityByTestId(name);
		GeneralUIUtils.clickOnElementByTestId(name);
		GeneralUIUtils.ultimateWait();	
	}

	public static void clickOnHomeTab() throws Exception {
		ExtentTestActions.log(Status.INFO,"Navigate to SDC Homepage");
		GeneralUIUtils.clickOnElementByCSS("div[class=triangle] span"); // Temp workaround... need to click by data-tests-id
		GeneralUIUtils.ultimateWait();	
		GeneralUIUtils.clickOnElementByTestId("sub-menu-button-home");		
		GeneralUIUtils.ultimateWait();	
	}

}
