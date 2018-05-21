package com.onap.dcae.ci.ui.rule_editor.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.onap.dcae.ci.ui.rule_editor.components.HomePage;
import com.onap.dcae.ci.ui.rule_editor.components.RuleComponent;
import com.onap.dcae.ci.ui.rule_editor.components.RulePopup;
import com.onap.dcae.ci.ui.rule_editor.components.actions.ActionType;
import com.onap.dcae.ci.ui.rule_editor.components.actions.ConcatActionComponent;
import com.onap.dcae.ci.ui.rule_editor.components.actions.CopyActionComponent;
import com.onap.dcae.ci.ui.rule_editor.components.actions.MapActionComponent;
import com.onap.dcae.ci.ui.rule_editor.components.actions.from.RegexFromComponent;
import org.openecomp.d2.ci.report.ExtentTestActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.onap.dcae.ci.ui.rule_editor.BaseTest;
import com.onap.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class SanityTest extends BaseTest {
	
	@Test
	public void testTranslateOfSingleCopyAction() throws Exception {
		WebDriver driver = getDriver();
		ExtentTestActions.log(Status.INFO, "test start");
		List<WebElement> findElements = driver.findElements(By.cssSelector("[data-tests-id*=\"map\" i]"));
		
		
		WebElement appElement;
		HomePage homePage = initHomePage(driver);
		homePage.load("5.3", "syslog");
		setRule1(homePage.clickAddFirstRule());
		setRule2(homePage.clickAddMoreRule(), driver);
		RulePopup editRule1 = homePage.getRule(0).clickEdit(driver, homePage.getRoot());
		validateRule1(editRule1);
		editRule1 = homePage.getRule(0).clickEdit(driver, homePage.getRoot());
		editRule1(editRule1);
		RuleComponent firstRuleinList = verifyRuleListDescription(homePage);
		deleteRuleFromRuleList(driver, homePage, firstRuleinList);
        translateRuleList(driver, homePage);
        refreshAndVerifyGetRules(driver);
	}

	private void refreshAndVerifyGetRules(WebDriver driver) {
		Report.log(Status.INFO, "Refresh and verify getting rule list");
		HomePage homePage;
		RuleComponent firstRuleinList;
		driver.navigate().refresh();
        homePage = initHomePage(driver);
        firstRuleinList = homePage.getRule(0);
        assertThat(firstRuleinList.getDescription()).contains("Yanir Manor");
	}

	private void translateRuleList(WebDriver driver, HomePage homePage) throws Exception {
		Report.log(Status.INFO, "Translate rule list");
		homePage.clickTranslate();
        String translation = homePage.getTranslation(driver);
        assertThat(translation).isNotEmpty();
	}

	private void deleteRuleFromRuleList(WebDriver driver, HomePage homePage, RuleComponent firstRuleinList) {
		Report.log(Status.INFO, "Delete the first rule from list");
		firstRuleinList.clickDelete(driver);
        homePage.clickOnRuleDeleteConfirmPopup();
	}

	private RuleComponent verifyRuleListDescription(HomePage homePage) {
		Report.log(Status.INFO, "Verify Rule List Description");
		RuleComponent firstRuleinList = homePage.getRule(0);
		assertThat(firstRuleinList.getDescription()).contains("Oren Levi");
		return firstRuleinList;
	}

	private HomePage initHomePage(WebDriver driver) {
		WebElement appElement = driver.findElement(By.cssSelector("body")); 
		HomePage homePage = new HomePage(defaultTimeoutWait, appElement);
		homePage.waitForLoaderFinish();
		return homePage;
	}

	private void setRule1(RulePopup rulePopup) {
		Report.log(Status.INFO, "Set first rule");
		rulePopup.setDescription("Oren Levi");
		CopyActionComponent copyAction = rulePopup.addCopyAction(0);
		rulePopup.clickSave();
		WebElement errorList = rulePopup.getErrorList();
		assertThat(errorList.getText()).isNotEmpty();
		rulePopup.addSimpleCondition();
		RegexFromComponent fromComponent = copyAction.getFromComponent();
		fromComponent.setValue("Argentina");
		fromComponent.clickRegex();
		fromComponent.setRegex("*");
		copyAction.setTarget("Mexico");
		rulePopup.clickSave();
	}
	
	private void validateRule1(RulePopup rulePopup) {
		Report.log(Status.INFO, "Validate first rule");
		assertThat(rulePopup.getDescription()).isEqualTo("Oren Levi");
		CopyActionComponent copyAction = (CopyActionComponent) rulePopup.getAction(ActionType.Copy, 0);
		RegexFromComponent fromComponent = copyAction.getFromComponent();
		assertThat(fromComponent.getValue()).isEqualTo("Argentina");
		assertThat(fromComponent.getRegex()).isEqualTo("*");
		assertThat(copyAction.getTarget()).isEqualTo("Mexico");
		rulePopup.clickCancel();
	}
	
	private void editRule1(RulePopup rulePopup) {	
		Report.log(Status.INFO, "Edit first rule");
		CopyActionComponent copyAction = (CopyActionComponent) rulePopup.getAction(ActionType.Copy, 0);
		RegexFromComponent fromComponent = copyAction.getFromComponent();
		fromComponent.setValue("Brazil");
		fromComponent.setRegex("**");
		copyAction.setTarget("Canada");
		rulePopup.clickSave();
	}
	
	private void setRule2(RulePopup rulePopup, WebDriver driver) {
		Report.log(Status.INFO, "Set second rule");
		rulePopup.setDescription("Yanir Manor");
		
		Report.log(Status.INFO, "Add complex condition");
		rulePopup.addComplexCondition();
		
		Report.log(Status.INFO, "Add concat action");
		ConcatActionComponent concatAction = rulePopup.addConcatAction(0);
		concatAction.getFromComponent(0).setValue("Oren");
		concatAction.getFromComponent(1).setValue("Levi");
		concatAction.clickAddInput(2).setValue("yanir");
		concatAction.getFromComponent(1).clickDelete(driver);
		concatAction.setTargetFromTree();
		
		Report.log(Status.INFO, "Add map action");
		MapActionComponent mapAction = rulePopup.addMapAction(1);
		mapAction.getFromComponent().setValue("Mapfrom");
		mapAction.setTarget("MapTarget");
		mapAction.openDefaultOption();
		mapAction.setDefaultData("default data");
		mapAction.setKeyData("A");
		mapAction.setValueData("B");
		
		rulePopup.clickSave();
	}

}
