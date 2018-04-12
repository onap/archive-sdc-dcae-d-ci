package com.att.ecomp.dcae.ci.ui.rule_editor.components.condition;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.att.ecomp.dcae.ci.ui.rule_editor.components.BaseComponenet;
import com.att.ecomp.dcae.ci.ui.utils.ByTest;
import com.att.ecomp.dcae.ci.ui.utils.Locator;

public class ConditionComponent extends BaseComponenet{

	public ConditionComponent(WebDriverWait timeout, WebElement element) {
		super(timeout, element);
	}
	
	public void setLeftField(int index, String value){
		WebElement left = wait.until(Locator.from(root).findVisible(ByTest.id("left"), index));
		left.clear();
		left.sendKeys(value);
	}
	
	public void setRightField(int index, String value){
		WebElement right = wait.until(Locator.from(root).findVisible(ByTest.id("right"), index));
		right.clear();
		right.sendKeys(value);
	}
	
	public void selectOperator(int index, String value){
		WebElement operatorElement = wait.until(Locator.from(root).findVisible(ByTest.id("selectOperator"), index));
		Select operatorSelect = new Select(operatorElement);
		operatorSelect.selectByVisibleText(value);
	}
}
