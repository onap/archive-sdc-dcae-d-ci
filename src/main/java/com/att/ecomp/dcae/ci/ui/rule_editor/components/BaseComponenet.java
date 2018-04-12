package com.att.ecomp.dcae.ci.ui.rule_editor.components;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaseComponenet {
	
	protected WebDriverWait wait;
	protected WebElement root;
	
	public BaseComponenet(WebDriverWait timeout, WebElement element) {
		this.wait = timeout;
		this.root = element;
	}
}
