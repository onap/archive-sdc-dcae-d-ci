package org.onap.dcae.ci.entities.sdc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SdcComponent {

	private String name;
	private String description;
	private List<String> tags;
	private String contactId;
	private String icon;
	private String uuid;

	private String lastUpdaterUserId;
	private String uniqueId;
	private String version;
	private String resourceType;
	private List<SdcCategory> categories;
	private Map<String, String> allVersions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public String getLastUpdaterUserId() {
		return lastUpdaterUserId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public List<SdcCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<SdcCategory> categories) {
		this.categories = categories;
	}

	public Map<String, String> getAllVersions() {
		return allVersions;
	}


}
