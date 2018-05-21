package com.onap.dcae.ci.ui.rule_editor.components.actions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.onap.dcae.ci.ui.rule_editor.components.BaseComponenet;
import com.onap.dcae.ci.ui.utils.ByTest;
import com.onap.dcae.ci.ui.utils.Locator;
import com.onap.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class ActionComponent extends BaseComponenet {

	public ActionComponent(WebDriverWait timeout, WebElement element) {
		super(timeout, element);
	}
	
	public void delete() {
		Report.log(Status.INFO, "click on delete action");
		wait.until(Locator.from(root).findVisible(ByTest.id("deleteAction"), 0)).click();
	}
	
	public void setTarget(String target) {
		Report.log(Status.INFO, "Filling input target...");
		getTargetElement().clear();
		getTargetElement().sendKeys(target);
	}
	
	public void setTargetFromTree(){
		Report.log(Status.INFO, "Pick target from tree");
		wait.until(Locator.from(root).findVisible(ByTest.id("openTargetTree"), 0)).click();
		Report.log(Status.INFO, "Click on tree toggle");
		wait.until(Locator.from(root).findVisible(ByTest.cssSelector(".toggle-children-wrapper"),0)).click();
		Report.log(Status.INFO, "Click on first target node");
		wait.until(Locator.from(root).findVisible(ByTest.id("targetNode"), 1)).click();
	}
	
	public String getTarget() {
		Report.log(Status.INFO, "get input target...");
		return getTargetElement().getAttribute("value");
	}
	
	/* Private Methods */
	
	private WebElement getTargetElement() {
		return wait.until(Locator.from(root).findVisible(ByTest.id("inputTarget"), 0));
	}
}
