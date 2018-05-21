package com.onap.dcae.ci.ui.pages;

import org.openecomp.d2.ci.report.ExtentTestActions;
import org.openecomp.d2.ci.utilities.GeneralUIUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.onap.dcae.ci.ui.tests.verificator.CompositionVerificator;
import com.onap.dcae.ci.ui.utils.ByTest;
import com.onap.dcae.ci.ui.utils.Locator;
import com.onap.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class DCAECompositionPage {
	
	public static WebElement expandList(String listname)
	{
		ExtentTestActions.log(Status.INFO,String.format("Expand %s list", listname));
		GeneralUIUtils.clickOnElementByTestId(listname);
		GeneralUIUtils.ultimateWait();	
		WebDriver driver = GeneralUIUtils.getDriver();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement categoryElement = driver.findElement(ByTest.id(listname)).findElement(By.xpath("./.."));
		return wait.until(Locator.from(categoryElement).findVisible(By.cssSelector(".draggable"), 0));
	}
	
	public static void addItemFromList(WebElement item)
	{
		ExtentTestActions.log(Status.INFO,String.format("Click on %s item", item.getText()));
		item.click();
		GeneralUIUtils.ultimateWait();
		
	}
	
	public static void clickSave()
	{
		ExtentTestActions.log(Status.INFO,"Click save button");
		GeneralUIUtils.clickOnElementByTestId("SaveButton");
		GeneralUIUtils.ultimateWait();
		GeneralUIUtils.waitForElementInVisibilityByTestId("saveMsg");
		String actualSaveRes = GeneralUIUtils.getWebElementByTestID("saveMsg").getText();
		CompositionVerificator.verifySaveSuccessfully(actualSaveRes);
		GeneralUIUtils.ultimateWait();
	}
	
	public static void clickSubmit()
	{
		ExtentTestActions.log(Status.INFO,"Click submit button");
		GeneralUIUtils.clickOnElementByTestId("SubmitButton");
		GeneralUIUtils.ultimateWait();
		GeneralUIUtils.waitForElementInVisibilityByTestId("submitMsg");
		String actualSubmitRes = GeneralUIUtils.getWebElementByTestID("submitMsg").getText();
		CompositionVerificator.verifySubmitSuccessfully(actualSubmitRes);
		GeneralUIUtils.ultimateWait();
	}
	
	public static void SelectFlowType(String flowType) {
		Report.log(Status.INFO, "Selecting flow type '%s'", flowType);
		WebDriver driver = GeneralUIUtils.getDriver();
		Select flowTypeSelect = new Select(driver.findElement(ByTest.id("flowTypeSelect")));
		flowTypeSelect.selectByVisibleText(flowType);
	}
}




