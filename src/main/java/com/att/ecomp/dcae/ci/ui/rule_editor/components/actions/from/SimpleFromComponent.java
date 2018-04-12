package com.att.ecomp.dcae.ci.ui.rule_editor.components.actions.from;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.att.ecomp.dcae.ci.ui.rule_editor.components.BaseComponenet;
import com.att.ecomp.dcae.ci.ui.utils.ByTest;
import com.att.ecomp.dcae.ci.ui.utils.Locator;
import com.att.ecomp.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class SimpleFromComponent extends BaseComponenet {

	public SimpleFromComponent(WebDriverWait timeout, WebElement element) {
		super(timeout, element);
	}
	
	public String getValue() {
		Report.log(Status.INFO, "Getting input from...");
		return getFromValueElement().getAttribute("value");
	}
	
	public void setValue(String value) {
		Report.log(Status.INFO, "Setting input from...");
		getFromValueElement().clear();
		getFromValueElement().sendKeys(value);
	}
	
	/* Private Methods */

	private WebElement getFromValueElement() {
		return wait.until(Locator.from(root).findVisible(ByTest.id("valueInput"), 0));
	}
}
