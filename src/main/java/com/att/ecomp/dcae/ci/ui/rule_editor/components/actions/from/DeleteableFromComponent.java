package com.att.ecomp.dcae.ci.ui.rule_editor.components.actions.from;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.att.ecomp.dcae.ci.ui.utils.ByTest;
import com.att.ecomp.dcae.ci.ui.utils.Locator;
import com.att.ecomp.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class DeleteableFromComponent extends SimpleFromComponent {

	public DeleteableFromComponent(WebDriverWait timeout, WebElement element) {
		super(timeout, element);
	}

	public void clickDelete(WebDriver driver) {
		Report.log(Status.INFO, "Click on delete from...");
		Actions action  = new Actions(driver);
		action.moveToElement(root).perform();
		wait.until(Locator.from(root).findVisible(ByTest.id("btnDelete"), 0)).click();
	}
	
}
