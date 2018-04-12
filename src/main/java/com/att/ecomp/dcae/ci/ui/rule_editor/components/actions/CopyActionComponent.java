package com.att.ecomp.dcae.ci.ui.rule_editor.components.actions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.att.ecomp.dcae.ci.ui.rule_editor.components.actions.from.RegexFromComponent;
import com.att.ecomp.dcae.ci.ui.utils.ByTest;
import com.att.ecomp.dcae.ci.ui.utils.Locator;
import com.att.ecomp.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class CopyActionComponent extends ActionComponent {

	public CopyActionComponent(WebDriverWait timeout, WebElement element) {
		super(timeout, element);
	}
	
	public RegexFromComponent getFromComponent() {
		Report.log(Status.INFO, "Getting fromComponent...");
		WebElement fromElement = wait.until(Locator.from(root).findVisible(ByTest.id("fromComponent"), 0));
		return new RegexFromComponent(wait, fromElement);
	}

}
