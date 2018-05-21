package com.onap.dcae.ci.ui.pages;


import org.openecomp.d2.ci.report.ExtentTestActions;
import org.openecomp.d2.ci.utilities.GeneralUIUtils;

import com.aventstack.extentreports.Status;

public class DCAEServicesPage {
	
	public static void selectService(String serviceName)
	{
		GeneralUIUtils.ultimateWait();		
		ExtentTestActions.log(Status.INFO,String.format("Select Service: %s from services dropdown", serviceName));
		GeneralUIUtils.getSelectList(serviceName, "Service Name SelectList");
		GeneralUIUtils.ultimateWait();		
	}
	
	public static void selectVNFI(String VNFIName)
	{
		GeneralUIUtils.ultimateWait();		
		VNFIName = VNFIName + " 0";
		ExtentTestActions.log(Status.INFO,String.format("Select VNFI: %s from VNFIs dropdown", VNFIName));
		GeneralUIUtils.getSelectList(VNFIName, "VNFI Name SelectList");
		GeneralUIUtils.ultimateWait();		
	}
	
	
	public static void clickAttach(String VNFIName)
	{
		ExtentTestActions.log(Status.INFO,"Click attach button");
		GeneralUIUtils.clickOnElementByTestId("Attach Button");
		GeneralUIUtils.ultimateWait();
		VNFIName = VNFIName + " 0";
		GeneralUIUtils.waitForElementInVisibilityByTestId(VNFIName); // Element to wait for after object is attached.
		GeneralUIUtils.ultimateWait();
	}	
}
