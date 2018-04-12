package com.att.ecomp.dcae.ci.ui.rule_editor.components;

import org.openecomp.d2.ci.datatypes.UserCredentials;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.att.ecomp.dcae.ci.utilities.Report;
import com.aventstack.extentreports.Status;

public class LoginPage extends BaseComponenet {
	
	public LoginPage(WebDriverWait timeout, WebElement element) {
		super(timeout, element);
	}

	public void login(String userId, String password) {
		Report.log(Status.INFO, "Filling input userid...");
		root.findElement(By.name("userid")).sendKeys(userId);
		Report.log(Status.INFO, "Filling input password...");
		root.findElement(By.name("password")).sendKeys(password);
		Report.log(Status.INFO, "Clicking submit...");
		root.findElement(By.name("btnSubmit")).click();
		Report.log(Status.INFO, "Waiting for login success page...");
		wait.until(ExpectedConditions.titleIs("AT&T - Log On Successful"));
		Report.log(Status.INFO, "Clicking OK...");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("successOK"))).click();
	}
	
	public void login(UserCredentials credentials) {
		login(credentials.getUserId(), credentials.getPassword());
	}
	
	public static boolean isCurrentPage(WebDriver driver) {
		return driver.getTitle().equals("AT&T Security Server: Login");
	}
}
