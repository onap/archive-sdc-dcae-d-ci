package org.onap.dcae.ci.entities.sdc;

import java.util.Arrays;
import java.util.List;

public class SdcCategory {

	private String name;
	private List<SdcCategory> subcategories;


	public String getName() {
		return name;
	}

	public List<SdcCategory> getSubcategories() {
		return subcategories;
	}

	public SdcCategory(String categoryName) {
		this.name = categoryName;
	}

	public SdcCategory(String categoryName, String subCategoryName){
		this(categoryName);
		this.subcategories = Arrays.asList(new SdcCategory(subCategoryName));
	}
}
