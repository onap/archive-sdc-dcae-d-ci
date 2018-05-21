package org.onap.dcae.ci.entities.sdc;


public class SdcComponentInstance {

	String componentUid;
	String description;
	String posX;
	String posY;
	String name;
	String uniqueId;

	public SdcComponentInstance(SdcResource vf) {
		this.componentUid = vf.getUniqueId();
		this.posX = "20";
		this.posY = "20";
		this.name = vf.getName();
		this.description = vf.getDescription();
	}
}
