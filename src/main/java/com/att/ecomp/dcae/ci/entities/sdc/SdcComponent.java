package com.att.ecomp.dcae.ci.entities.sdc;

import java.util.List;
import java.util.Map;

public class SdcComponent {

	private String lastUpdaterUserId;
	private String uniqueId;
	private String version;
	private String resourceType;
	private List<SdcCategory> categories;
	private Map<String, String> allVersions;

	public String getLastUpdaterUserId() {
		return lastUpdaterUserId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public String getVersion() {
		return version;
	}

	public String getResourceType() {
		return resourceType;
	}

	public List<SdcCategory> getCategories() {
		return categories;
	}

	public Map<String, String> getAllVersions() {
		return allVersions;
	}


}
