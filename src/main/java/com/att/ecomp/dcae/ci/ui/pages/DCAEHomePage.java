package com.att.ecomp.dcae.ci.ui.pages;


import org.openecomp.d2.ci.report.ExtentTestActions;
import org.openecomp.d2.ci.utilities.GeneralUIUtils;

import com.aventstack.extentreports.Status;


public class DCAEHomePage {
	
	
	
	public static void clickOnDcaeTab()
	{
		ExtentTestActions.log(Status.INFO,"Clicking on DCAE Tab");
		GeneralUIUtils.clickOnElementByTestId("main-menu-button-dcae");
		GeneralUIUtils.ultimateWait();
	}
	
	public static void clickOnCreateNewAsset()
	{
		ExtentTestActions.log(Status.INFO,"Clicking on Create new asset button");
		GeneralUIUtils.clickOnElementByTestId("AddButtonsArea");
		GeneralUIUtils.ultimateWait();
	}
}
