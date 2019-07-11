/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 - 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.dcae.ci.utilities;

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
