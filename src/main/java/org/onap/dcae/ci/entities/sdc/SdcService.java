package org.onap.dcae.ci.entities.sdc;

import org.onap.dcae.ci.entities.sdc.SdcComponent;


public class SdcService extends SdcComponent {

	private String projectCode = "12345";
    private String serviceRole =  "";// empty string is valid
	private String serviceType = "";// empty string is valid
	private String instantiationType = "Macro";


    public void setServiceRole(String serviceRole){
        this.serviceRole = serviceRole;
    }

    public String getServiceRole(){
        return serviceRole;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceType(){
        return serviceType;
    }

    public void setInstantiationType(String instantiationType) {
        this.instantiationType = instantiationType;
    }

    public String getInstantiationType(){
        return instantiationType;
    }
}
