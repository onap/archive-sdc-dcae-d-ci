package com.onap.dcae.ci.ui.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class Locator {
	
	private WebElement element;

	private Locator(WebElement element) {
		this.element = element;
	}
	
	public static Locator from(WebElement element) {
		return new Locator(element);
	}
	
	public ExpectedCondition<WebElement> find(By by, int index) {
		return new NestedFinder(this.element, by) {
			@Override
			protected WebElement predicate(List<WebElement> elements) {
				return (elements.size() > index) ? elements.get(index) : null;
			}
			
			@Override
			public String toString() {
				return "element located by " + by;
			}
		};
	}
	
	public ExpectedCondition<WebElement> findVisible(By by, int index) {
		return new NestedFinder(this.element, by) {
			@Override
			protected WebElement predicate(List<WebElement> elements) {
				return visibleElementsOrNull(elements, index);
			}
			
			@Override
			public String toString() {
				return "visibility of element located by " + by;
			}
		};
	}
	
	public ExpectedCondition<List<WebElement>> findNoVisible(By by) {
		return new NestedFilter(this.element, by) {
			@Override
			protected List<WebElement> predicate(List<WebElement> elements) {
				WebElement visible = visibleElementsOrNull(elements, 0);
				return (visible != null) ? null : new ArrayList<WebElement>();
			}
			
			@Override
			public String toString() {
				return "no visible elements located by " + by;
			}
		};
	}
	
	private static WebElement visibleElementsOrNull(List<WebElement> elements, int index) {
		List<WebElement> visibles = elements.stream()
				.filter(elem -> elem.isDisplayed())
				.collect(Collectors.toList());
		return (visibles.size() > index) ? visibles.get(index) : null;
	}
	
	
}
