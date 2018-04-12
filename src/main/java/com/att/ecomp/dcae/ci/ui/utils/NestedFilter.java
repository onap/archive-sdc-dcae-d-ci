package com.att.ecomp.dcae.ci.ui.utils;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public abstract class NestedFilter implements ExpectedCondition<List<WebElement>> {
	
	private WebElement parent;
	private By by;

	public NestedFilter(WebElement parent, By by) {
		this.parent = parent;
		this.by = by;
	}

	abstract protected List<WebElement> predicate(List<WebElement> elements);
	
	@Override
	public List<WebElement> apply(WebDriver input) {
		List<WebElement> elements = parent.findElements(by);
		return predicate(elements);
	}

}
