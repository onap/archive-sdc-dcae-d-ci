package com.onap.dcae.ci.ui.tests;

import com.onap.dcae.ci.ui.tests.verificator.ServiceVerificator;
import org.openecomp.d2.ci.datatypes.UserRoleEnum;
import org.openecomp.d2.ci.report.ExtentTestActions;
import org.openecomp.d2.ci.utilities.GeneralUIUtils;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import com.onap.dcae.ci.ui.pages.DCAECompositionPage;
import com.onap.dcae.ci.ui.pages.DCAEGeneralPage;
import com.onap.dcae.ci.ui.pages.DCAEHomePage;
import com.onap.dcae.ci.ui.pages.DCAELeftPanel;
import com.onap.dcae.ci.ui.pages.DCAEServicesPage;
import com.onap.dcae.ci.ui.pages.SDCCreateServicePage;
import com.onap.dcae.ci.ui.pages.SDCCreateVFPage;
import com.onap.dcae.ci.ui.pages.SDCHomePage;
import com.onap.dcae.ci.ui.setup.ConfigTest;
import com.aventstack.extentreports.Status;

public class DCAESanity extends ConfigTest{

	@Override
	protected UserRoleEnum getUserRole() {
		// TODO Auto-generated method stub
		return UserRoleEnum.DESIGNER;
	}
	
	@Test (description = "DCAE Main scenario", groups={"Sanity"})
	public void sanityTestSDCSection() throws Exception 
	{
		String vfName;
		String serviceName;
		
        // ------------------ SDC-Side: Create VF --------------------------
		
		System.out.println("Start Test");
		
		SDCHomePage.clickOnAddVf();
		vfName = SDCCreateVFPage.addAllVFMandtoryFields("Utility"); //Creates VF, need to send only the desired category name
		SDCCreateVFPage.clickOnCreateVFButton();
		SDCCreateVFPage.checkInVF();

        // ------------------ SDC-Side: Create Service ---------------------
		SDCHomePage.clickOnAddsService();
		serviceName = SDCCreateServicePage.addAllServiceMandtoryFields("Network L1-3");
		SDCCreateServicePage.clickOnCreateServiceButton();
		SDCCreateServicePage.clickOnServiceCompsitionTab();
		SDCCreateServicePage.addAssetToCanvas(vfName);		
		SDCCreateServicePage.checkInService();
		
        // ------------------ DCAE-Side: Create Asset -------------------

		DCAEHomePage.clickOnDcaeTab();
		DCAEHomePage.clickOnCreateNewAsset();
		
		String assetName = DCAEGeneralPage.addAssetName("Asset Name");
		DCAEGeneralPage.addAssetDescription("Asset Description");
		DCAEGeneralPage.clickSaveAsset();		
				
		DCAELeftPanel.navigateToServices();
		
		DCAEServicesPage.selectService(serviceName);
		DCAEServicesPage.selectVNFI(vfName);
		DCAEServicesPage.clickAttach(vfName);

		DCAELeftPanel.navigateToComposition();		
		
		WebElement item = DCAECompositionPage.expandList("Microservice");
        DCAECompositionPage.addItemFromList(item);
		DCAECompositionPage.SelectFlowType("Syslog");
        DCAECompositionPage.clickSave();
        DCAECompositionPage.clickSubmit(); 
        
      //------------------ Verify Blue Print 
		
        SDCHomePage.clickOnHomeTab();
  		SDCHomePage.searchForElement(serviceName);
  		SDCHomePage.clickOnElement(serviceName);
  		try
  		{
  			SDCCreateServicePage.clickOnServiceCompsitionTab();  			
  		}
  		catch(Exception e)
  		{
  			GeneralUIUtils.getDriver().navigate().refresh();
  			SDCCreateServicePage.clickOnServiceCompsitionTab();
  			ExtentTestActions.log(Status.WARNING,"SDC UI known Bug - Blank page with text: {{getStatus()}}, the browser was refreshed to bypass this bug.");
  		}
  		
  		SDCCreateServicePage.clickOnElementInCanavs();
  		SDCCreateServicePage.clickOnArtifactTab();
  		ServiceVerificator.verifyBluePrintArtifactExist(assetName);
  		
		ExtentTestActions.log(Status.INFO, "Test Ended.");

		System.out.println("End Test");
	}

}
