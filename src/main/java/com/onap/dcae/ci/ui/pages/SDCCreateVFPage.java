package com.onap.dcae.ci.ui.pages;

import java.util.UUID;

import org.openecomp.d2.ci.report.ExtentTestActions;
import org.openecomp.d2.ci.utilities.GeneralUIUtils;
import org.openqa.selenium.WebElement;

import com.aventstack.extentreports.Status;

public class SDCCreateVFPage 
{
	public static String addVFName()
	{
		GeneralUIUtils.waitForElementInVisibilityByTestId("name");
		WebElement vfNameTextbox = GeneralUIUtils.getWebElementByTestID("name");
		vfNameTextbox.clear();
		String vfName = "VF" + UUID.randomUUID();
		vfNameTextbox.sendKeys(vfName);
		ExtentTestActions.log(Status.INFO, String.format("Add VF Name: %s",vfName));
		GeneralUIUtils.ultimateWait();
		return vfName;
	}
	
	public static void addVFCategory(String category)
	{
		ExtentTestActions.log(Status.INFO, String.format("Add %s Category", category));
		GeneralUIUtils.waitForElementInVisibilityByTestId("selectGeneralCategory");
		GeneralUIUtils.getSelectList(category, "selectGeneralCategory");
		GeneralUIUtils.ultimateWait();
	}
	
	public static void addVFDescription()
	{
		ExtentTestActions.log(Status.INFO,"Add VF Desc");
		GeneralUIUtils.waitForElementInVisibilityByTestId("description");
		WebElement vfdescTextbox = GeneralUIUtils.getWebElementByTestID("description");
		vfdescTextbox.clear();
		String vfDesc = "Desc";
		vfdescTextbox.sendKeys(vfDesc);
		GeneralUIUtils.ultimateWait();
	}
	
	public static void addVFVendorName()
	{
		ExtentTestActions.log(Status.INFO,"Add VF Vendor Name");
		GeneralUIUtils.waitForElementInVisibilityByTestId("vendorName");
		WebElement vendorTextbox = GeneralUIUtils.getWebElementByTestID("vendorName");
		vendorTextbox.clear();
		String vfVendorName = "Vendor";
		vendorTextbox.sendKeys(vfVendorName);
		GeneralUIUtils.ultimateWait();
	}
	
	public static void addVFVendorRelease()
	{
		ExtentTestActions.log(Status.INFO,"Add VF Vendor Release");
		GeneralUIUtils.waitForElementInVisibilityByTestId("vendorRelease");
		WebElement vendorReleaseTextbox = GeneralUIUtils.getWebElementByTestID("vendorRelease");
		vendorReleaseTextbox.clear();
		String vfVendorRelease = "1";
		vendorReleaseTextbox.sendKeys(vfVendorRelease);
		GeneralUIUtils.ultimateWait();
	}
	
	public static String addAllVFMandtoryFields(String category)
	{
		String vfName = addVFName();
		addVFCategory(category);
		addVFDescription();
		addVFVendorName();
		addVFVendorRelease();
		
		return vfName;
	}
	
	public static void clickOnCreateVFButton()
	{
		ExtentTestActions.log(Status.INFO,"Click on Create VF Button");
		GeneralUIUtils.waitForElementInVisibilityByTestId("create/save");
		GeneralUIUtils.clickOnElementByTestId("create/save");	
		GeneralUIUtils.ultimateWait();
	}
	
	public static void clickOnCheckInVFButton()
	{
		ExtentTestActions.log(Status.INFO,"Click on Check In VF Button");
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
	
	public static void checkInVF()
	{
		clickOnCheckInVFButton();
		addCheckInMessage();
		clickOnCheckInOKFButton();		
	}
	
	
	
	

}
