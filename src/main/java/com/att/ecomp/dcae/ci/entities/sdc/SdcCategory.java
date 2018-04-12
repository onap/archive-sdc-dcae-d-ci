package com.att.ecomp.dcae.ci.entities.sdc;

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


}
