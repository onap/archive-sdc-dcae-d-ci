package com.onap.dcae.ci.ui.rule_editor.components.actions;

import com.onap.dcae.ci.ui.rule_editor.components.actions.from.SimpleFromComponent;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.onap.dcae.ci.ui.utils.ByTest;
import com.onap.dcae.ci.ui.utils.Locator;
import com.onap.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class MapActionComponent extends ActionComponent {

	public MapActionComponent(WebDriverWait timeout, WebElement element) {
		super(timeout, element);
	}
	
	public SimpleFromComponent getFromComponent() {
		Report.log(Status.INFO, "getting simple from component");
		WebElement fromElement = wait.until(Locator.from(root).findVisible(ByTest.id("fromComponent"), 0));
		return new SimpleFromComponent(wait, fromElement);
	}
	
	public void openDefaultOption(){
		Report.log(Status.INFO, "open default opention by click on checkbox");
		wait.until(Locator.from(root).find(ByTest.id("defaultCheckbox"), 0)).click();
	}
	
	public void setDefaultData(String value){
		Report.log(Status.INFO, "set default field with a value");
		wait.until(Locator.from(root).findVisible(ByTest.id("defaultInput"), 0)).sendKeys(value);
	}
	
	public void setKeyData(String value){
		Report.log(Status.INFO, "set map key with a value");
		wait.until(Locator.from(root).findVisible(ByTest.id("key"), 0)).sendKeys(value);
	}
	
	public void setValueData(String value){
		Report.log(Status.INFO, "set map value with a value");
		wait.until(Locator.from(root).findVisible(ByTest.id("value"), 0)).sendKeys(value);
	}
}
