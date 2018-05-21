package com.onap.dcae.ci.ui.rule_editor.components;

import static org.testng.Assert.fail;

import java.util.Arrays;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.onap.dcae.ci.ui.rule_editor.TranslateResult;
import com.onap.dcae.ci.ui.utils.ByTest;
import com.onap.dcae.ci.ui.utils.Locator;
import com.onap.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HomePage extends BaseComponenet {

	public HomePage(WebDriverWait timeout, WebElement element) {
		super(timeout, element);
	}
	
	public void waitForLoaderFinish() {
		Report.log(Status.INFO, "Wait for loader finish...");
		ByTest loaderTestId = ByTest.id("loader");
		WebElement loader = wait.until(Locator.from(root).find(loaderTestId, 0)); // wait until loader appears
		wait.until(ExpectedConditions.invisibilityOfAllElements(Arrays.asList(loader))); // wait until loader disappears
	}
	
	public void load(String version, String eventType) {
		Report.log(Status.INFO, "Selecting version...");
		select(ByTest.id("selectVersion"), version);
		Report.log(Status.INFO, "Selecting event-type...");
		select(ByTest.id("selectEventType"), eventType);
	} 
	
	public RuleComponent getRule(int index) {
		WebElement ruleElement = wait.until(Locator.from(root).findVisible(ByTest.id("ruleElement"), index));
		return new RuleComponent(wait, ruleElement);
	}

	private WebElement getRulePopupElem() {
		return wait.until(Locator.from(root).findVisible(ByTest.id("popupRuleEditor"), 0));
	}
	
	public WebElement getRoot() {
		return root;
	}

	public RulePopup clickAddFirstRule() {
		Report.log(Status.INFO, "Clicking add-rule...");
		wait.until(Locator.from(root).findVisible(ByTest.id("btnAddFirstRule"), 0)).click();
		WebElement popupAddRule = getRulePopupElem();
		Report.log(Status.INFO, "Rule popup visible");
		return new RulePopup(wait, popupAddRule);
	}
	
	public RulePopup clickAddMoreRule() {
		Report.log(Status.INFO, "Clicking add-rule...");
		wait.until(Locator.from(root).findVisible(ByTest.id("addMoreRule"), 0)).click();
		WebElement popupAddRule = getRulePopupElem();
		Report.log(Status.INFO, "Rule popup visible");
		return new RulePopup(wait, popupAddRule);
	}
	
	public void clickOnRuleDeleteConfirmPopup (){
		Report.log(Status.INFO, "Click on delete on popup confirmation");
		WebElement deletePopup = wait.until(Locator.from(root).findVisible(ByTest.id("delete-popup"), 0));
		wait.until(Locator.from(root).findVisible(ByTest.id("btnDelete"), 0)).click();
		waitForLoaderFinish();
	}
	
	public void clickTranslate() {
		Report.log(Status.INFO, "Clicking Translate...");
		wait.until(Locator.from(root).findVisible(ByTest.id("btnTranslate"), 0)).click();
		waitForLoaderFinish();
	}
	
	public void clickCancel() {
		Report.log(Status.INFO, "Clicking Cancel...");
		wait.until(Locator.from(root).findVisible(ByTest.id("btnCancel"), 0)).click();
	}
	
	public String getTranslation(WebDriver driver) throws Exception {
		Report.log(Status.INFO, "Retriving translation result...");
		Object result = (Object) ((JavascriptExecutor) driver).executeScript("return window.translateResult;");
		ObjectMapper mapper = new ObjectMapper();
		TranslateResult parsedResult = mapper.convertValue(result, TranslateResult.class);
		switch(parsedResult.getStatus()) {
		case error:
			fail(String.format("translate error: %s", parsedResult.getData()));
			break;
		case ok:
			Report.logDebug("translation:", parsedResult.getData());
			return parsedResult.getData();
		}
		throw new Exception(String.format("invalid status argument. expected (ok/error) but got %s", parsedResult.getData()));
	}
	
	/* Private Methods */
	
	private void select(ByTest by, String option) {
		WebElement selectElement = wait.until(Locator.from(root).findVisible(by, 0));
		wait.until(Locator.from(selectElement).find(ByTest.id("option"), 0)); // wait for dynamic options
		Select versionSelect = new Select(selectElement);
		versionSelect.selectByVisibleText(option);
	}
	
}
