package com.onap.dcae.ci.ui.utils;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public abstract class NestedFinder implements ExpectedCondition<WebElement> {
	
	private WebElement parent;
	private By by;

	public NestedFinder(WebElement parent, By by) {
		this.parent = parent;
		this.by = by;
	}

	abstract protected WebElement predicate(List<WebElement> elements);
	
	@Override
	public WebElement apply(WebDriver input) {
		List<WebElement> elements = parent.findElements(by);
		return predicate(elements);
	}

}
