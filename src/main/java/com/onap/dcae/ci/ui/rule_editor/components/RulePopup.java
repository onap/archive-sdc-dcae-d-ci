package com.onap.dcae.ci.ui.rule_editor.components;

import java.util.Arrays;

import com.onap.dcae.ci.ui.rule_editor.components.actions.*;
import com.onap.dcae.ci.ui.rule_editor.components.condition.ConditionComponent;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.onap.dcae.ci.ui.rule_editor.components.actions.ActionComponent;
import com.onap.dcae.ci.ui.rule_editor.components.actions.ActionType;
import com.onap.dcae.ci.ui.rule_editor.components.actions.ConcatActionComponent;
import com.onap.dcae.ci.ui.rule_editor.components.actions.CopyActionComponent;
import com.onap.dcae.ci.ui.rule_editor.components.actions.MapActionComponent;
import com.onap.dcae.ci.ui.utils.ByTest;
import com.onap.dcae.ci.ui.utils.Locator;
import com.onap.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class RulePopup extends BaseComponenet {

	public RulePopup(WebDriverWait timeout, WebElement element) {
		super(timeout, element);
	}
	
	public String getDescription() {
		Report.log(Status.INFO, "get input description...");
		return wait.until(Locator.from(root).findVisible(ByTest.id("inputDescription"), 0)).getAttribute("value");
	}
	
	public void setDescription(String text) {
		Report.log(Status.INFO, "Filling input description...");
		wait.until(Locator.from(root).findVisible(ByTest.id("inputDescription"), 0)).sendKeys(text);
	}
	
	public ActionComponent getAction(ActionType actionType, int index) {
		WebElement actionElement = wait.until(Locator.from(root).findVisible(ByTest.id("action"), index));
		ActionComponent newActionComponent = createAction(actionType, wait, actionElement);
		return newActionComponent;
	}
	
	public CopyActionComponent addCopyAction(int index) {
		return (CopyActionComponent) addAction(ActionType.Copy, index);
	}
	
	public ConcatActionComponent addConcatAction(int index) {
		return (ConcatActionComponent) addAction(ActionType.Concat, index);
	}
	
	public MapActionComponent addMapAction(int index) {
		return (MapActionComponent) addAction(ActionType.Map, index);
	}
	
	public WebElement getErrorList(){
		return wait.until(Locator.from(root).findVisible(ByTest.id("errorList"), 0));
	}
	
	public void clickOnAddCondition(){
		wait.until(Locator.from(root).find(ByTest.id("isCondition"), 0)).click();
	}
	
	public void addSimpleCondition(){
		clickOnAddCondition();
		Report.log(Status.INFO, "Create and fill simple condition");
		ConditionComponent condition = new ConditionComponent(wait,root);
		condition.setLeftField(0, "A");
		condition.setRightField(0, "B");
		condition.selectOperator(0, "Contains");
	}
	
	public void addComplexCondition(){
		Report.log(Status.INFO, "Click on add condition checkbox");
		clickOnAddCondition();
		Report.log(Status.INFO, "Create and fill top condition");
		ConditionComponent condition1 = new ConditionComponent(wait,root);
		condition1.setLeftField(0, "A");
		condition1.setRightField(0, "B");
		condition1.selectOperator(0, "Contains");
		Report.log(Status.INFO, "Click on add group condition");
		wait.until(Locator.from(root).findVisible(ByTest.id("addConditionGroup"), 0)).click();
		Report.log(Status.INFO, "Create and fill first nested condition");
		ConditionComponent innerCondition1 = new ConditionComponent(wait,root);
		condition1.setLeftField(1, "C");
		condition1.setRightField(1, "D");
		condition1.selectOperator(1, "Contains");
		Report.log(Status.INFO, "Create and fill second nested condition");
		ConditionComponent innerCondition2 = new ConditionComponent(wait,root);
		condition1.setLeftField(2, "E");
		condition1.setRightField(2, "F");
		condition1.selectOperator(2, "Contains");
	}
	
	public void clickSave() {
		Report.log(Status.INFO, "Clicking Save (changes on rule-editor)...");
		wait.until(Locator.from(root).findVisible(ByTest.id("btnSave"), 0)).click();
		waitForLoaderFinish();
	}
	
	public void clickCancel() {
		Report.log(Status.INFO, "Clicking Cancel (changes on rule-editor)...");
		wait.until(Locator.from(root).findVisible(ByTest.id("btnCancel"), 0)).click();
	}
	
	
	/* Private Methods */
	
	private void waitForLoaderFinish() {
		Report.log(Status.INFO, "Wait for loader finish...");
		ByTest loaderTestId = ByTest.id("loader");
		WebElement loader = root.findElement(loaderTestId);
		wait.until(ExpectedConditions.invisibilityOfAllElements(Arrays.asList(loader))); // wait until loader disappears
	}
	
	private ActionComponent addAction(ActionType actionType, int index) {
		Report.log(Status.INFO, "Selecting action...");
		WebElement actionElement = wait.until(Locator.from(root).findVisible(ByTest.id("selectAction"), 0));
		Select actionSelect = new Select(actionElement);
		actionSelect.selectByVisibleText(actionType.toString());
		Report.log(Status.INFO, "Clicking Add Action...");
		wait.until(Locator.from(root).findVisible(ByTest.id("btnAddAction"), 0)).click();
		return getAction(actionType, index);
	}

	private static ActionComponent createAction(ActionType actionType, WebDriverWait wait, WebElement newActionElement) {
		switch (actionType) {
		case Concat:
			return new ConcatActionComponent(wait, newActionElement);
		case Copy:
			return new CopyActionComponent(wait, newActionElement);
		case Map:
			return new MapActionComponent(wait, newActionElement);
		default:
			throw new IllegalArgumentException(String.format("action type '%s' not supported", actionType));
		}
	}
	
	
	
}
