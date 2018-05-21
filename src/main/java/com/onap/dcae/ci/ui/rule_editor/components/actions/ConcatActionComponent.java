package com.onap.dcae.ci.ui.rule_editor.components.actions;

import com.onap.dcae.ci.ui.rule_editor.components.actions.from.DeleteableFromComponent;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.onap.dcae.ci.ui.utils.ByTest;
import com.onap.dcae.ci.ui.utils.Locator;
import com.onap.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;


public class ConcatActionComponent extends ActionComponent {

	public ConcatActionComponent(WebDriverWait timeout, WebElement element) {
		super(timeout, element);
	}

	public DeleteableFromComponent clickAddInput(int index) {
		Report.log(Status.INFO, "Clicking on add input (another from)...");
		wait.until(Locator.from(root).findVisible(ByTest.id("btnAddInput"), 0)).click();
		return getFromComponent(index);
	}
	
	public DeleteableFromComponent getFromComponent(int index) {
		Report.log(Status.INFO, "Getting from component at index %d...", index);
		WebElement fromElement = wait.until(Locator.from(root).findVisible(ByTest.id("fromComponent"), index));
		return new DeleteableFromComponent(wait, fromElement);
	}
	
}
