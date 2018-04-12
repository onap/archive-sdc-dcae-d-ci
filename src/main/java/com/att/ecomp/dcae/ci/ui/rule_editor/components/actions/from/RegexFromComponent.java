package com.att.ecomp.dcae.ci.ui.rule_editor.components.actions.from;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.att.ecomp.dcae.ci.ui.utils.ByTest;
import com.att.ecomp.dcae.ci.ui.utils.Locator;
import com.att.ecomp.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class RegexFromComponent extends SimpleFromComponent {

	public RegexFromComponent(WebDriverWait timeout, WebElement element) {
		super(timeout, element);
	}
	
	public void clickRegex() {
		Report.log(Status.INFO, "Clicking regex button...");
		wait.until(Locator.from(root).findVisible(ByTest.id("btnFromRegex"), 0)).click();
	}

	public String getRegex() {
		Report.log(Status.INFO, "Getting regex from...");
		return getRegexElement().getAttribute("value");
	}
	
	public void setRegex(String value) {
		Report.log(Status.INFO, "Setting regex from...");
		getRegexElement().clear();
		getRegexElement().sendKeys(value);
	}
	
	/* Private Methods */
	
	private WebElement getRegexElement() {
		return wait.until(Locator.from(root).findVisible(ByTest.id("inputFromRegex"), 0));
	}
}
