package com.onap.dcae.ci.ui.rule_editor.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.onap.dcae.ci.ui.utils.ByTest;
import com.onap.dcae.ci.ui.utils.Locator;
import com.onap.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class RuleComponent extends BaseComponenet {

	public RuleComponent(WebDriverWait timeout, WebElement element) {
		super(timeout, element);
	}
	
	public String getDescription(){
		Report.log(Status.INFO, "Get rule description");
		return root.getText();
	}
	
	public RulePopup clickEdit(WebDriver driver, WebElement homePageRoot){
		Report.log(Status.INFO, "Click on edit...");
		Actions action  = new Actions(driver);
		action.moveToElement(root).perform();
		wait.until(Locator.from(root).findVisible(ByTest.id("editRule"), 0)).click();
		WebElement rulePopupElem = wait.until(Locator.from(homePageRoot).findVisible(ByTest.id("popupRuleEditor"), 0));
		return new RulePopup(wait, rulePopupElem);
	}
	
	public void clickDelete(WebDriver driver){
		Report.log(Status.INFO, "Click on delete...");
		Actions action  = new Actions(driver);
		action.moveToElement(root).perform();
		wait.until(Locator.from(root).findVisible(ByTest.id("deleteRule"), 0)).click();
	}

}
