package com.att.ecomp.dcae.ci.ui.tests;

import org.openecomp.d2.ci.datatypes.Configuration;
import org.openecomp.d2.ci.datatypes.UserCredentials;
import org.openecomp.d2.ci.datatypes.UserRoleEnum;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import com.att.ecomp.dcae.ci.ui.pages.DCAECompositionPage;
import com.att.ecomp.dcae.ci.ui.pages.DCAEGeneralPage;
import com.att.ecomp.dcae.ci.ui.pages.DCAEHomePage;
import com.att.ecomp.dcae.ci.ui.pages.DCAELeftPanel;
import com.att.ecomp.dcae.ci.ui.pages.DCAEServicesPage;
import com.att.ecomp.dcae.ci.ui.pages.SDCCreateServicePage;
import com.att.ecomp.dcae.ci.ui.pages.SDCCreateVFPage;
import com.att.ecomp.dcae.ci.ui.pages.SDCHomePage;
import com.att.ecomp.dcae.ci.ui.setup.ConfigTest;

public class SanityTest extends ConfigTest{
	
		
	public Configuration configuration = ConfigTest.getConfiguration();
	

	@Override
	protected UserRoleEnum getUserRole() {
		return UserRoleEnum.DESIGNER;
	}
	
	
	// Only when running locally
	@Override
	public void navigateAndLogin(UserCredentials userCredentials) throws Exception {

		navigateToUrl(configuration.getUrl());
	}
		
	
	@Test (description = "DCAE Main scenario", groups={"Sanity"})
	public void sanityTestDCAESection() throws InterruptedException{

		//HomePage.clickOnDcaeTab(); // not needed on local
		
		
		
		
		
		
		
		////------TO DELETE
		String vfName;
		String serviceName;
		
        // ------------------ SDC-Side: Create VF --------------------------
		
		System.out.println("Start Test");
		
		SDCCreateServicePage.clickOnElementInCanavs();
		
		SDCHomePage.clickOnAddVf();
		vfName = SDCCreateVFPage.addAllVFMandtoryFields("Microservice"); //Creates VF, need to send only the desired category name
		SDCCreateVFPage.clickOnCreateVFButton();
		SDCCreateVFPage.checkInVF();

        // ------------------ SDC-Side: Create Service ---------------------
		SDCHomePage.clickOnAddsService();
		serviceName = SDCCreateServicePage.addAllServiceMandtoryFields("Network L1-3");
		SDCCreateServicePage.clickOnCreateServiceButton();
		SDCCreateServicePage.clickOnServiceCompsitionTab();
		SDCCreateServicePage.addAssetToCanvas(vfName);		
		SDCCreateServicePage.checkInService();

		
		////------TO DELETE


		
		
		
		
		
		
		
		
		System.out.println("Start Test");
		
		DCAEHomePage.clickOnCreateNewAsset();
		
		DCAEGeneralPage.addAssetName("Asset Name");
		DCAEGeneralPage.addAssetDescription("Asset Description");
//		DCAEGeneralPage.clickSaveAsset();		
				
		DCAELeftPanel.navigateToServices();
		
		DCAEServicesPage.selectService("Servicedbb463a1-50bb-4aef-b84c-8c2bb9a8f866");
		DCAEServicesPage.selectVNFI("VF30ca7676-35c5-4c3c-9cc8-a06c23b17872");
		DCAEServicesPage.clickAttach("VF30ca7676-35c5-4c3c-9cc8-a06c23b17872");

		DCAELeftPanel.navigateToComposition();	
		
		WebElement item = DCAECompositionPage.expandList("Microservice");
        DCAECompositionPage.addItemFromList(item);
        DCAECompositionPage.clickSave();
        DCAECompositionPage.clickSubmit();
		
		System.out.println("End Test");
	}
	
}
