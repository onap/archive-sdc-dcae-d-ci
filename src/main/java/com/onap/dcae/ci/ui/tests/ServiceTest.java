package com.onap.dcae.ci.ui.tests;

import java.util.UUID;

import com.onap.dcae.ci.ui.pages.DCAEHomePage;
import org.openecomp.d2.ci.datatypes.UserRoleEnum;
import org.openecomp.d2.ci.utilities.GeneralUIUtils;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.onap.dcae.ci.ui.setup.ConfigTest;

public class ServiceTest extends ConfigTest {

	@Override
	protected UserRoleEnum getUserRole() {
		return UserRoleEnum.DESIGNER;
	}
	
	@Test
	public void createServiceTest(){
		DCAEHomePage.clickOnDcaeTab();
		
		GeneralUIUtils.hoverOnAreaByTestId("AddButtonsArea");
		GeneralUIUtils.findByText("Add Service Assurance Template").click();
		GeneralUIUtils.ultimateWait();
		
		WebElement nameTextbox = GeneralUIUtils.getWebElementByTestID("name");
		nameTextbox.clear();
		nameTextbox.sendKeys("autoServicer" + UUID.randomUUID());
		
		WebElement descriptionTextbox = GeneralUIUtils.getWebElementByTestID("description");
		descriptionTextbox.clear();
		descriptionTextbox.sendKeys("new service");
		
		GeneralUIUtils.findByText("Create").click();
		GeneralUIUtils.ultimateWait();
	}



}
