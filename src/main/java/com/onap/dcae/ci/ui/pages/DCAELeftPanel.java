package com.onap.dcae.ci.ui.pages;



import org.openecomp.d2.ci.report.ExtentTestActions;
import org.openecomp.d2.ci.utilities.GeneralUIUtils;

import com.aventstack.extentreports.Status;

public class DCAELeftPanel {
	
	public static void navigateToServices()
	{
		ExtentTestActions.log(Status.INFO,"Navigate to Services internal tab");
		GeneralUIUtils.clickOnElementByTestId("dcae-menu-item-Services");
		GeneralUIUtils.ultimateWait();
	}
	
	public static void navigateToComposition()
	{
		ExtentTestActions.log(Status.INFO,"Navigate to Composition internal tab");
		GeneralUIUtils.clickOnElementByTestId("dcae-menu-item-Composition");
		GeneralUIUtils.ultimateWait();	
	}
	
	

}
