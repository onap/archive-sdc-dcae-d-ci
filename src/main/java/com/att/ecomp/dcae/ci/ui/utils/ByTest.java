package com.att.ecomp.dcae.ci.ui.utils;

import org.openqa.selenium.By.ByCssSelector;

public class ByTest extends ByCssSelector {

	private static final long serialVersionUID = 7435597710732625685L; // auto generated - does nothing
	
	protected ByTest(String cssSelector) {
		super(cssSelector);
	}

	public static ByTest id(String id) {
		return new ByTest(String.format("[data-tests-id=\"%s\"]", id));
	}
	
}
