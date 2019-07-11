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

package org.onap.dcae.ci.entities.sdc;

import org.apache.commons.lang3.RandomStringUtils;

import org.onap.dcae.ci.utilities.DcaeRestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SdcComponentFactory {

	private SdcComponentFactory(){}

	public static SdcService getDefaultService() {
		SdcService service = new SdcService();
		service.setCategories(Arrays.asList(new SdcCategory("Mobility")));
		setDefaultComponentFields(service, "CiService");
		return service;
	}

	public static SdcResource getDefaultResource() {
		SdcResource resource = new SdcResource();
		resource.setVendorName("vendorName");
		resource.setVendorRelease("1.1");
		resource.setResourceType("VF");
		resource.setCategories(Arrays.asList(new SdcCategory("Generic", "Infrastructure")));
		setDefaultComponentFields(resource, "CiResource");
		return resource;
	}

	private static void setDefaultComponentFields(SdcComponent component, String name) {
		component.setName(name.concat(RandomStringUtils.randomAlphanumeric(4)));
		List<String> tags = new ArrayList<>();
		tags.add(component.getName());
		component.setTags(tags);
		component.setDescription("Ci component description");
		component.setIcon("defaultIcon");
		component.setContactId(DcaeRestClient.getDefaultUserId());
	}
}
