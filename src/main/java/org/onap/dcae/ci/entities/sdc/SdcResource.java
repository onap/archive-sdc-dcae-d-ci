package org.onap.dcae.ci.entities.sdc;


public class SdcResource extends SdcComponent {

	private String vendorName;
	private String vendorRelease;


	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorRelease() {
		return vendorRelease;
	}

	public void setVendorRelease(String vendorRelease) {
		this.vendorRelease = vendorRelease;
	}

}
