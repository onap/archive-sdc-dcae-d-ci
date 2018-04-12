package com.att.ecomp.dcae.ci.utilities;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.base.Ascii;

public class StringUtils {
	
	public static String randomString(String prefix, int length) {
		return prefix + RandomStringUtils.randomAlphanumeric(length - prefix.length());
	}
	
	public static String truncate(Object obj) {
		 return Ascii.truncate(obj.toString(), 160, "...");
	}
}
