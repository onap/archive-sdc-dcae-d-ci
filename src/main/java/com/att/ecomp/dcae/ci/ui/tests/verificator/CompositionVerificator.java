package com.att.ecomp.dcae.ci.ui.tests.verificator;

import static org.testng.Assert.assertTrue;

public class CompositionVerificator {
	
	public static void verifySaveSuccessfully(String actualSaveRes)
	{
		assertTrue(actualSaveRes.startsWith("Composition Created"));
	}

	public static void verifySubmitSuccessfully(String actualSubmitRes) {
		assertTrue(actualSubmitRes.equalsIgnoreCase("Blueprint Created"));
		
	}
}
