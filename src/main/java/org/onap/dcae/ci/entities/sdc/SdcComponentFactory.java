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
