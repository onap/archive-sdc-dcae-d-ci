package com.onap.dcae.ci.ui.pages;

import java.util.UUID;

import org.openecomp.d2.ci.report.ExtentTestActions;
import org.openecomp.d2.ci.utilities.GeneralUIUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import com.aventstack.extentreports.Status;

public class SDCCreateServicePage 
{
	public static String addServiceName()
	{
		GeneralUIUtils.waitForElementInVisibilityByTestId("name");
		WebElement serviceNameTextbox = GeneralUIUtils.getWebElementByTestID("name");
		serviceNameTextbox.clear();
		String serviceName = "Service" + UUID.randomUUID();
		ExtentTestActions.log(Status.INFO, String.format("Add Service Name: %s", serviceName));
		serviceNameTextbox.sendKeys(serviceName);
		GeneralUIUtils.ultimateWait();
		return serviceName;
	}
	
	public static void addServiceCategory(String category)
	{
		ExtentTestActions.log(Status.INFO, String.format("Add %s Category", category));
		GeneralUIUtils.waitForElementInVisibilityByTestId("selectGeneralCategory");
		GeneralUIUtils.getSelectList(category, "selectGeneralCategory");
		GeneralUIUtils.ultimateWait();
	}
	
	public static void addServiceDescription()
	{
		ExtentTestActions.log(Status.INFO,"Add Service Desc");
		GeneralUIUtils.waitForElementInVisibilityByTestId("description");
		WebElement serviceDescTextbox = GeneralUIUtils.getWebElementByTestID("description");
		serviceDescTextbox.clear();
		String serviceDesc = "Desc";
		serviceDescTextbox.sendKeys(serviceDesc);
		GeneralUIUtils.ultimateWait();
	}
	
	public static void addServiceDescriptionProjectCode()
	{
		ExtentTestActions.log(Status.INFO,"Add Service Project Code");
		GeneralUIUtils.waitForElementInVisibilityByTestId("projectCode");
		WebElement projectCodeTextbox = GeneralUIUtils.getWebElementByTestID("projectCode");
		projectCodeTextbox.clear();
		String projectCode = "12345";
		projectCodeTextbox.sendKeys(projectCode);
		GeneralUIUtils.ultimateWait();
	}	
		
	public static String addAllServiceMandtoryFields(String category)
	{
		String serviceName = addServiceName();
		addServiceCategory(category);
		addServiceDescription();
		addServiceDescriptionProjectCode();
		
		return serviceName;
	}
	
	public static void clickOnCreateServiceButton()
	{
		ExtentTestActions.log(Status.INFO,"Click on Create Service Button");
		GeneralUIUtils.waitForElementInVisibilityByTestId("create/save");
		GeneralUIUtils.clickOnElementByTestId("create/save");	
		GeneralUIUtils.ultimateWait();
	}
	
	public static void clickOnCheckInServiceButton()
	{
		ExtentTestActions.log(Status.INFO,"Click on Check In Service Button");
		GeneralUIUtils.waitForElementInVisibilityByTestId("check_in");
		GeneralUIUtils.clickOnElementByTestId("check_in");	
		GeneralUIUtils.ultimateWait();		
	}
	
	public static void addCheckInMessage()
	{
		ExtentTestActions.log(Status.INFO,"Add check in message in pop window");
		GeneralUIUtils.waitForElementInVisibilityByTestId("checkindialog");
		WebElement checkInDialogTextbox = GeneralUIUtils.getWebElementByTestID("checkindialog");
		checkInDialogTextbox.clear();
		String vfCheckInDialog = "Text";
		checkInDialogTextbox.sendKeys(vfCheckInDialog);
		GeneralUIUtils.ultimateWait();		
	}
	
	public static void clickOnCheckInOKFButton()
	{
		ExtentTestActions.log(Status.INFO,"Click OK in check in pop up window");
	    GeneralUIUtils.waitForElementInVisibilityByTestId("OK");
		GeneralUIUtils.clickOnElementByTestId("OK");	
		GeneralUIUtils.ultimateWait();
	}
	
	public static void checkInService()
	{
		clickOnCheckInServiceButton();
		addCheckInMessage();
		clickOnCheckInOKFButton();		
	}
	
	public static void clickOnServiceCompsitionTab()
	{
		ExtentTestActions.log(Status.INFO,"Click on Service composition tab");
	    GeneralUIUtils.waitForElementInVisibilityByTestId("CompositionLeftSideMenu");
		GeneralUIUtils.clickOnElementByTestId("CompositionLeftSideMenu");	
		GeneralUIUtils.ultimateWait();
	}
	
	
	public static void searchAssetOnCompsitionTab(String assetName)
	{
		ExtentTestActions.log(Status.INFO,String.format("Search for %s in Service composition", assetName));
		GeneralUIUtils.waitForElementInVisibilityByTestId("searchAsset");		
		WebElement compositionSearchTextbox = GeneralUIUtils.getWebElementByTestID("searchAsset");
		compositionSearchTextbox.clear();		
		compositionSearchTextbox.sendKeys(assetName);		
		GeneralUIUtils.ultimateWait();
	}
	
	public static void dragAndDropAssetToCanvas(String assetName)
	{
		ExtentTestActions.log(Status.INFO,String.format("Drag and drop %s to Canvas", assetName));
		GeneralUIUtils.waitForElementInVisibilityByTestId("searchAsset");		
		WebElement vfElementFrom = GeneralUIUtils.getWebElementByTestID("leftbar-section-content-item-" + assetName);		
		WebElement canvasElementTo = GeneralUIUtils.getWebElementByTestID("canvas");		
		GeneralUIUtils.ultimateWait();		
		WebDriver driver = GeneralUIUtils.getDriver();				 
		Actions builder = new Actions(driver);		 
		Action dragAndDrop = builder.clickAndHold(vfElementFrom).moveToElement(canvasElementTo).release(canvasElementTo).build();
		dragAndDrop.perform();		
		GeneralUIUtils.ultimateWait();
	}
	
	public static void addAssetToCanvas(String assetName)	
	{
		searchAssetOnCompsitionTab(assetName);
		dragAndDropAssetToCanvas(assetName);
	}
	
	public static void clickOnElementInCanavs()
	{
		ExtentTestActions.log(Status.INFO,"Click on VF in Canavs");
		GeneralUIUtils.waitForElementInVisibilityByTestId("canvas");
		WebElement canvasElement = GeneralUIUtils.getWebElementByTestID("canvas");		
		GeneralUIUtils.ultimateWait();		
		WebDriver driver = GeneralUIUtils.getDriver();				 
		Actions actions = new Actions(driver);
		actions.moveToElement(canvasElement, 1316, 661);
		actions.clickAndHold();
		actions.moveToElement(canvasElement, 1316, 661);
		actions.release();
		actions.perform();
		GeneralUIUtils.ultimateWait();
	}
	
	public static void clickOnArtifactTab()
	{
		ExtentTestActions.log(Status.INFO,"Click on Artifcat");
		GeneralUIUtils.getWebElementByTestID("deployment-artifact-tab");		
		GeneralUIUtils.clickOnElementByTestId("deployment-artifact-tab");		
	}
	
	
	

}
