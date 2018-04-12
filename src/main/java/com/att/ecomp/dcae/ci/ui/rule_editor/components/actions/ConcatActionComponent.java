package com.att.ecomp.dcae.ci.ui.rule_editor.components.actions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.att.ecomp.dcae.ci.ui.rule_editor.components.actions.from.DeleteableFromComponent;
import com.att.ecomp.dcae.ci.ui.utils.ByTest;
import com.att.ecomp.dcae.ci.ui.utils.Locator;
import com.att.ecomp.dcae.ci.utilities.Report;
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
